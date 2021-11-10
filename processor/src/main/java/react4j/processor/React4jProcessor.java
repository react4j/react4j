package react4j.processor;

import com.squareup.javapoet.TypeName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.DeferredElementSet;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

/**
 * Annotation processor that analyzes React annotated source and generates models from the annotations.
 */
@SuppressWarnings( "Duplicates" )
@SupportedAnnotationTypes( Constants.VIEW_CLASSNAME )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "react4j.defer.unresolved", "react4j.defer.errors", "react4j.debug" } )
public final class React4jProcessor
  extends AbstractStandardProcessor
{
  private static final String SENTINEL_NAME = "<default>";
  private static final Pattern DEFAULT_GETTER_PATTERN = Pattern.compile( "^get([A-Z].*)Default$" );
  private static final Pattern VALIDATE_INPUT_PATTERN = Pattern.compile( "^validate([A-Z].*)$" );
  private static final Pattern LAST_INPUT_PATTERN = Pattern.compile( "^last([A-Z].*)$" );
  private static final Pattern PREV_INPUT_PATTERN = Pattern.compile( "^prev([A-Z].*)$" );
  private static final Pattern INPUT_PATTERN = Pattern.compile( "^([a-z].*)$" );
  private static final Pattern GETTER_PATTERN = Pattern.compile( "^get([A-Z].*)$" );
  private static final Pattern ISSER_PATTERN = Pattern.compile( "^is([A-Z].*)$" );
  @Nonnull
  private final DeferredElementSet _deferredTypes = new DeferredElementSet();

  @Override
  public boolean process( @Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env )
  {
    debugAnnotationProcessingRootElements( env );
    collectRootTypeNames( env );
    processTypeElements( annotations,
                         env,
                         Constants.VIEW_CLASSNAME,
                         _deferredTypes,
                         this::process );
    errorIfProcessingOverAndInvalidTypesDetected( env );
    clearRootTypeNamesIfProcessingOver( env );
    return true;
  }

  @Override
  @Nonnull
  protected String getIssueTrackerURL()
  {
    return "https://github.com/react4j/react4j/issues";
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "react4j";
  }

  private void process( @Nonnull final TypeElement element )
    throws IOException, ProcessorException
  {
    final ViewDescriptor descriptor = parse( element );
    final String packageName = descriptor.getPackageName();
    emitTypeSpec( packageName, ViewGenerator.buildType( processingEnv, descriptor ) );
    emitTypeSpec( packageName, BuilderGenerator.buildType( processingEnv, descriptor ) );
    if ( descriptor.needsInjection() )
    {
      emitTypeSpec( packageName, FactoryGenerator.buildType( processingEnv, descriptor ) );
    }
  }

  /**
   * Return true if there is any method annotated with @PostConstruct.
   */
  private boolean hasPostConstruct( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( e -> AnnotationsUtil.hasAnnotationOfType( e, Constants.POST_CONSTRUCT_CLASSNAME ) );
  }

  @Nonnull
  private ViewDescriptor parse( @Nonnull final TypeElement typeElement )
  {
    final String name = deriveViewName( typeElement );
    final ViewType type = extractViewType( typeElement );
    final boolean hasPostConstruct = hasPostConstruct( typeElement );
    final boolean shouldSetDefaultPriority = shouldSetDefaultPriority( typeElement );

    MemberChecks.mustNotBeFinal( Constants.VIEW_CLASSNAME, typeElement );
    MemberChecks.mustBeAbstract( Constants.VIEW_CLASSNAME, typeElement );
    if ( ElementKind.CLASS != typeElement.getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.VIEW_CLASSNAME, "be a class" ),
                                    typeElement );
    }
    else if ( ElementsUtil.isNonStaticNestedClass( typeElement ) )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.VIEW_CLASSNAME ) +
                                    " target must not be a non-static nested class",
                                    typeElement );
    }
    final List<ExecutableElement> constructors = ElementsUtil.getConstructors( typeElement );
    if ( 1 != constructors.size() || !isConstructorValid( constructors.get( 0 ) ) )
    {
      throw new ProcessorException( MemberChecks.must( Constants.VIEW_CLASSNAME,
                                                       "have a single, package-access constructor or the default constructor" ),
                                    typeElement );
    }
    final ExecutableElement constructor = constructors.get( 0 );

    final boolean inject = deriveInject( typeElement, constructor );
    final boolean sting = deriveSting( typeElement, constructor );
    final boolean notSyntheticConstructor = ElementsUtil.isNotSynthetic( constructor );

    final List<? extends VariableElement> parameters = constructor.getParameters();
    if ( inject )
    {
      if ( parameters.isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.VIEW_CLASSNAME,
                                                            "have specified inject=ENABLED if the constructor has no parameters" ),
                                      typeElement );
      }
    }
    else
    {
      final boolean hasNamedAnnotation =
        parameters.stream()
          .anyMatch( p -> AnnotationsUtil.hasAnnotationOfType( p, Constants.JSR_330_NAMED_CLASSNAME ) );
      if ( hasNamedAnnotation )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.VIEW_CLASSNAME,
                                                            "have specified inject=DISABLED and have a constructor parameter annotated with the " +
                                                            Constants.JSR_330_NAMED_CLASSNAME + " annotation" ),
                                      constructor );
      }
    }

    if ( sting )
    {
      if ( parameters.isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.VIEW_CLASSNAME,
                                                            "have specified sting=ENABLED if the constructor has no parameters" ),
                                      typeElement );
      }
    }
    else
    {
      final boolean hasNamedAnnotation =
        parameters.stream().anyMatch( p -> AnnotationsUtil.hasAnnotationOfType( p, Constants.STING_NAMED_CLASSNAME ) );
      if ( hasNamedAnnotation )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.VIEW_CLASSNAME,
                                                            "have specified sting=DISABLED and have a constructor parameter annotated with the " +
                                                            Constants.STING_NAMED_CLASSNAME + " annotation" ),
                                      constructor );
      }
    }

    final ViewDescriptor descriptor =
      new ViewDescriptor( name,
                          typeElement,
                          constructor,
                          type,
                          inject,
                          sting,
                          notSyntheticConstructor,
                          hasPostConstruct,
                          shouldSetDefaultPriority );

    for ( final Element element : descriptor.getElement().getEnclosedElements() )
    {
      if ( ElementKind.METHOD == element.getKind() )
      {
        final ExecutableElement method = (ExecutableElement) element;
        if ( method.getModifiers().contains( Modifier.PUBLIC ) &&
             MemberChecks.doesMethodNotOverrideInterfaceMethod( processingEnv, typeElement, method ) &&
             ElementsUtil.isWarningNotSuppressed( method,
                                                  Constants.WARNING_PUBLIC_METHOD,
                                                  Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ) )
        {
          final String message =
            MemberChecks.shouldNot( Constants.VIEW_CLASSNAME,
                                    "declare a public method. " +
                                    MemberChecks.suppressedBy( Constants.WARNING_PUBLIC_METHOD,
                                                               Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ) );
          processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, method );
        }
        if ( method.getModifiers().contains( Modifier.FINAL ) &&
             ElementsUtil.isWarningNotSuppressed( method,
                                                  Constants.WARNING_FINAL_METHOD,
                                                  Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ) )
        {
          final String message =
            MemberChecks.shouldNot( Constants.VIEW_CLASSNAME,
                                    "declare a final method. " +
                                    MemberChecks.suppressedBy( Constants.WARNING_FINAL_METHOD,
                                                               Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ) );
          processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, method );
        }
        if ( method.getModifiers().contains( Modifier.PROTECTED ) &&
             ElementsUtil.isWarningNotSuppressed( method,
                                                  Constants.WARNING_PROTECTED_METHOD,
                                                  Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ) &&
             !isMethodAProtectedOverride( typeElement, method ) )
        {
          final String message =
            MemberChecks.shouldNot( Constants.VIEW_CLASSNAME,
                                    "declare a protected method. " +
                                    MemberChecks.suppressedBy( Constants.WARNING_PROTECTED_METHOD,
                                                               Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ) );
          processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, method );
        }
      }
    }

    determineViewCapabilities( descriptor, typeElement );
    determineInputs( descriptor );
    determineInputValidatesMethods( descriptor );
    determineOnInputChangeMethods( descriptor );
    determineDefaultInputsMethods( descriptor );
    determineDefaultInputsFields( descriptor );
    determinePreUpdateMethod( typeElement, descriptor );
    determinePostMountOrUpdateMethod( typeElement, descriptor );
    determinePostUpdateMethod( typeElement, descriptor );
    determinePostMountMethod( typeElement, descriptor );
    determineOnErrorMethod( typeElement, descriptor );
    determineScheduleRenderMethods( typeElement, descriptor );
    determinePublishMethods( typeElement, descriptor );
    determineRenderMethod( typeElement, descriptor );

    for ( final InputDescriptor input : descriptor.getInputs() )
    {
      if ( !isInputRequired( input ) )
      {
        input.markAsOptional();
      }
      else
      {
        if ( input.isContextSource() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INPUT_CLASSNAME,
                                                              "specify require=ENABLE parameter when the for source=CONTEXT parameter is specified" ),
                                        input.getMethod() );
        }
      }
    }

    /*
     * Sorting must occur after @InputDefault has been processed to ensure the sorting
     * correctly sorts optional inputs after required inputs.
     */
    descriptor.sortInputs();

    verifyInputsNotAnnotatedWithArezAnnotations( descriptor );
    verifyInputsNotCollectionOfArezComponents( descriptor );

    return descriptor;
  }

  private boolean isMethodAProtectedOverride( @Nonnull final TypeElement typeElement,
                                              @Nonnull final ExecutableElement method )
  {
    final ExecutableElement overriddenMethod = ElementsUtil.getOverriddenMethod( processingEnv, typeElement, method );
    return null != overriddenMethod && overriddenMethod.getModifiers().contains( Modifier.PROTECTED );
  }

  private boolean deriveInject( @Nonnull final TypeElement typeElement, final @Nonnull ExecutableElement constructor )
  {
    final String inject =
      AnnotationsUtil.getEnumAnnotationParameter( typeElement,
                                                  Constants.VIEW_CLASSNAME,
                                                  "inject" );
    if ( "ENABLE".equals( inject ) )
    {
      return true;
    }
    else if ( "DISABLE".equals( inject ) )
    {
      return false;
    }
    else
    {
      return !constructor.getParameters().isEmpty() &&
             null != processingEnv.getElementUtils().getTypeElement( Constants.JSR_330_INJECT_CLASSNAME );
    }
  }

  private boolean deriveSting( @Nonnull final TypeElement typeElement, final @Nonnull ExecutableElement constructor )
  {
    final String inject =
      AnnotationsUtil.getEnumAnnotationParameter( typeElement,
                                                  Constants.VIEW_CLASSNAME,
                                                  "sting" );
    if ( "ENABLE".equals( inject ) )
    {
      return true;
    }
    else if ( "DISABLE".equals( inject ) )
    {
      return false;
    }
    else
    {
      return !constructor.getParameters().isEmpty() &&
             null != processingEnv.getElementUtils().getTypeElement( Constants.STING_INJECTABLE_CLASSNAME );
    }
  }

  private boolean isConstructorValid( @Nonnull final ExecutableElement ctor )
  {
    if ( ElementsUtil.isSynthetic( ctor ) )
    {
      return true;
    }
    else
    {
      final Set<Modifier> modifiers = ctor.getModifiers();
      return
        !modifiers.contains( Modifier.PRIVATE ) &&
        !modifiers.contains( Modifier.PUBLIC ) &&
        !modifiers.contains( Modifier.PROTECTED );
    }
  }

  private void verifyInputsNotCollectionOfArezComponents( @Nonnull final ViewDescriptor descriptor )
  {
    for ( final InputDescriptor input : descriptor.getInputs() )
    {
      final ExecutableElement method = input.getMethod();
      final TypeMirror returnType = method.getReturnType();
      if ( TypeKind.DECLARED == returnType.getKind() )
      {
        final DeclaredType declaredType = (DeclaredType) returnType;
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if ( isCollection( declaredType ) )
        {
          if ( 1 == typeArguments.size() && isArezComponent( typeArguments.get( 0 ) ) )
          {
            throw new ProcessorException( "@Input target is a collection that contains Arez components. " +
                                          "This is not a safe pattern when the arez components can be disposed.",
                                          method );
          }
        }
        else if ( isMap( declaredType ) )
        {
          if ( 2 == typeArguments.size() &&
               ( isArezComponent( typeArguments.get( 0 ) ) ||
                 isArezComponent( typeArguments.get( 1 ) ) ) )
          {
            throw new ProcessorException( "@Input target is a collection that contains Arez components. " +
                                          "This is not a safe pattern when the arez components can be disposed.",
                                          method );
          }
        }
      }
      else if ( TypeKind.ARRAY == returnType.getKind() )
      {
        final ArrayType arrayType = (ArrayType) returnType;
        if ( isArezComponent( arrayType.getComponentType() ) )
        {
          throw new ProcessorException( "@Input target is an array that contains Arez components. " +
                                        "This is not a safe pattern when the arez components can be disposed.",
                                        method );
        }
      }
    }
  }

  private boolean isCollection( @Nonnull final DeclaredType declaredType )
  {
    final TypeElement returnType = (TypeElement) processingEnv.getTypeUtils().asElement( declaredType );
    final String classname = returnType.getQualifiedName().toString();
    /*
     * For the time being lets just list out a bunch of collections. We can ge more specific when/if
     * it is ever required
     */
    return Collection.class.getName().equals( classname ) ||
           Set.class.getName().equals( classname ) ||
           List.class.getName().equals( classname ) ||
           HashSet.class.getName().equals( classname ) ||
           ArrayList.class.getName().equals( classname );
  }

  private boolean isMap( @Nonnull final DeclaredType declaredType )
  {
    final TypeElement returnType = (TypeElement) processingEnv.getTypeUtils().asElement( declaredType );
    final String classname = returnType.getQualifiedName().toString();
    /*
     * For the time being lets just list out a bunch of collections. We can ge more specific when/if
     * it is ever required
     */
    return Map.class.getName().equals( classname ) || HashMap.class.getName().equals( classname );
  }

  private boolean isArezComponent( @Nonnull final TypeMirror typeMirror )
  {
    return typeMirror instanceof DeclaredType &&
           processingEnv.getTypeUtils()
             .asElement( typeMirror )
             .getAnnotationMirrors()
             .stream()
             .anyMatch( a -> a.getAnnotationType().toString().equals( Constants.AREZ_COMPONENT_CLASSNAME ) );
  }

  private void verifyInputsNotAnnotatedWithArezAnnotations( @Nonnull final ViewDescriptor descriptor )
  {
    for ( final InputDescriptor input : descriptor.getInputs() )
    {
      final ExecutableElement method = input.getMethod();
      for ( final AnnotationMirror mirror : method.getAnnotationMirrors() )
      {
        final String classname = mirror.getAnnotationType().toString();
        if ( classname.startsWith( "arez.annotations." ) )
        {
          throw new ProcessorException( "@Input target must not be annotated with any arez annotations but " +
                                        "is annotated by '" + classname + "'.", method );
        }
      }
    }
  }

  private void determineOnInputChangeMethods( @Nonnull final ViewDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.ON_INPUT_CHANGE_CLASSNAME ) )
        .collect( Collectors.toList() );

    final ArrayList<OnInputChangeDescriptor> onInputChangeDescriptors = new ArrayList<>();
    for ( final ExecutableElement method : methods )
    {
      final VariableElement phase = (VariableElement)
        AnnotationsUtil.getAnnotationValue( method, Constants.ON_INPUT_CHANGE_CLASSNAME, "phase" ).getValue();
      final boolean preUpdate = phase.getSimpleName().toString().equals( "PRE" );

      final List<? extends VariableElement> parameters = method.getParameters();
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      final List<? extends TypeMirror> parameterTypes = methodType.getParameterTypes();

      MemberChecks.mustBeSubclassCallable( descriptor.getElement(),
                                           Constants.VIEW_CLASSNAME,
                                           Constants.ON_INPUT_CHANGE_CLASSNAME,
                                           method );
      MemberChecks.mustNotThrowAnyExceptions( Constants.ON_INPUT_CHANGE_CLASSNAME, method );
      MemberChecks.mustNotReturnAnyValue( Constants.ON_INPUT_CHANGE_CLASSNAME, method );

      final int parameterCount = parameters.size();
      if ( 0 == parameterCount )
      {
        throw new ProcessorException( "@OnInputChange target must have at least 1 parameter.", method );
      }
      final List<InputDescriptor> inputDescriptors = new ArrayList<>( parameterCount );
      for ( int i = 0; i < parameterCount; i++ )
      {
        final VariableElement parameter = parameters.get( i );
        final String name = deriveOnInputChangeName( parameter );
        final InputDescriptor input = descriptor.findInputNamed( name );
        if ( null == input )
        {
          throw new ProcessorException( "@OnInputChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' and the parameter is associated with a " +
                                        "@Input named '" + name + "' but there is no corresponding @Input " +
                                        "annotated method.", parameter );
        }
        final Types typeUtils = processingEnv.getTypeUtils();
        if ( !typeUtils.isAssignable( parameterTypes.get( i ), input.getMethodType().getReturnType() ) )
        {
          throw new ProcessorException( "@OnInputChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' and the parameter type is not " +
                                        "assignable to the return type of the associated @Input annotated method.",
                                        method );
        }
        final boolean mismatchedNullability =
          (
            AnnotationsUtil.hasNonnullAnnotation( parameter ) &&
            AnnotationsUtil.hasNullableAnnotation( input.getMethod() )
          ) ||
          (
            AnnotationsUtil.hasNullableAnnotation( parameter ) &&
            input.isNonNull() );

        if ( mismatchedNullability )
        {
          throw new ProcessorException( "@OnInputChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' that has a nullability annotation " +
                                        "incompatible with the associated @Input method named " +
                                        method.getSimpleName(), method );
        }
        if ( input.isImmutable() )
        {
          throw new ProcessorException( "@OnInputChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' that is associated with a @Input " +
                                        "annotated method and the input is specified as immutable.", method );
        }
        inputDescriptors.add( input );
      }
      onInputChangeDescriptors.add( new OnInputChangeDescriptor( method, inputDescriptors, preUpdate ) );
    }
    descriptor.setOnInputChangeDescriptors( onInputChangeDescriptors );
  }

  @Nonnull
  private String deriveOnInputChangeName( @Nonnull final VariableElement parameter )
  {
    final AnnotationValue value =
      AnnotationsUtil.findAnnotationValue( parameter, Constants.INPUT_REF_CLASSNAME, "value" );

    if ( null != value )
    {
      return (String) value.getValue();
    }
    else
    {
      final String parameterName = parameter.getSimpleName().toString();
      if ( LAST_INPUT_PATTERN.matcher( parameterName ).matches() ||
           PREV_INPUT_PATTERN.matcher( parameterName ).matches() )
      {
        return Character.toLowerCase( parameterName.charAt( 4 ) ) + parameterName.substring( 5 );
      }
      else if ( INPUT_PATTERN.matcher( parameterName ).matches() )
      {
        return parameterName;
      }
      else
      {
        throw new ProcessorException( "@OnInputChange target has a parameter named '" + parameterName +
                                      "' is not explicitly associated with a input using @InputRef nor does it " +
                                      "follow required naming conventions 'prev[MyInput]', 'last[MyInput]' or " +
                                      "'[myInput]'.", parameter );
      }
    }
  }

  private void determineInputValidatesMethods( @Nonnull final ViewDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.INPUT_VALIDATE_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : methods )
    {
      final String name = deriveInputValidateName( method );
      final InputDescriptor input = descriptor.findInputNamed( name );
      if ( null == input )
      {
        throw new ProcessorException( "@InputValidate target for input named '" + name + "' has no corresponding " +
                                      "@Input annotated method.", method );
      }
      if ( 1 != method.getParameters().size() )
      {
        throw new ProcessorException( "@InputValidate target must have exactly 1 parameter", method );
      }
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getParameterTypes().get( 0 ),
                                                       input.getMethodType().getReturnType() ) )
      {
        throw new ProcessorException( "@InputValidate target has a parameter type that is not assignable to the " +
                                      "return type of the associated @Input annotated method.", method );
      }
      MemberChecks.mustBeSubclassCallable( descriptor.getElement(),
                                           Constants.VIEW_CLASSNAME,
                                           Constants.INPUT_VALIDATE_CLASSNAME,
                                           method );
      MemberChecks.mustNotThrowAnyExceptions( Constants.INPUT_VALIDATE_CLASSNAME, method );
      MemberChecks.mustNotReturnAnyValue( Constants.INPUT_VALIDATE_CLASSNAME, method );

      final VariableElement param = method.getParameters().get( 0 );
      final boolean mismatchedNullability =
        (
          AnnotationsUtil.hasNonnullAnnotation( param ) &&
          AnnotationsUtil.hasNullableAnnotation( input.getMethod() )
        ) ||
        (
          AnnotationsUtil.hasNullableAnnotation( param ) &&
          input.isNonNull() );

      if ( mismatchedNullability )
      {
        throw new ProcessorException( "@InputValidate target has a parameter that has a nullability annotation " +
                                      "incompatible with the associated @Input method named " +
                                      input.getMethod().getSimpleName(), method );
      }
      input.setValidateMethod( method );
    }
  }

  @Nonnull
  private String deriveInputValidateName( @Nonnull final Element element )
    throws ProcessorException
  {
    final String name =
      (String) AnnotationsUtil.getAnnotationValue( element, Constants.INPUT_VALIDATE_CLASSNAME, "name" )
        .getValue();

    if ( isSentinelName( name ) )
    {
      final String deriveName = deriveName( element, VALIDATE_INPUT_PATTERN, name );
      if ( null == deriveName )
      {
        throw new ProcessorException( "@InputValidate target has not specified name nor is it named according " +
                                      "to the convention 'validate[Name]Input'.", element );
      }
      return deriveName;
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ProcessorException( "@InputValidate target specified an invalid name '" + name + "'. The " +
                                      "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ProcessorException( "@InputValidate target specified an invalid name '" + name + "'. The " +
                                      "name must not be a java keyword.", element );
      }
      return name;
    }
  }

  private void determineDefaultInputsMethods( @Nonnull final ViewDescriptor descriptor )
  {
    final List<ExecutableElement> defaultInputsMethods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.INPUT_DEFAULT_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : defaultInputsMethods )
    {
      final String name = deriveInputDefaultName( method );
      final InputDescriptor input = descriptor.findInputNamed( name );
      if ( null == input )
      {
        throw new ProcessorException( "@InputDefault target for input named '" + name + "' has no corresponding " +
                                      "@Input annotated method.", method );
      }
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getReturnType(),
                                                       input.getMethodType().getReturnType() ) )
      {
        throw new ProcessorException( "@InputDefault target has a return type that is not assignable to the " +
                                      "return type of the associated @Input annotated method.", method );
      }
      MemberChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.VIEW_CLASSNAME,
                                                     Constants.INPUT_DEFAULT_CLASSNAME,
                                                     method );
      MemberChecks.mustNotHaveAnyParameters( Constants.INPUT_DEFAULT_CLASSNAME, method );
      MemberChecks.mustNotThrowAnyExceptions( Constants.INPUT_DEFAULT_CLASSNAME, method );
      MemberChecks.mustReturnAValue( Constants.INPUT_DEFAULT_CLASSNAME, method );

      input.setDefaultMethod( method );
    }
  }

  private void determineDefaultInputsFields( @Nonnull final ViewDescriptor descriptor )
  {
    final List<VariableElement> defaultInputsFields =
      ElementsUtil.getFields( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.INPUT_DEFAULT_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final VariableElement field : defaultInputsFields )
    {
      final String name = deriveInputDefaultName( field );
      final InputDescriptor input = descriptor.findInputNamed( name );
      if ( null == input )
      {
        throw new ProcessorException( "@InputDefault target for input named '" + name + "' has no corresponding " +
                                      "@Input annotated method.", field );
      }
      if ( !processingEnv.getTypeUtils().isAssignable( field.asType(), input.getMethodType().getReturnType() ) )
      {
        throw new ProcessorException( "@InputDefault target has a type that is not assignable to the " +
                                      "return type of the associated @Input annotated method.", field );
      }
      MemberChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.VIEW_CLASSNAME,
                                                     Constants.INPUT_DEFAULT_CLASSNAME,
                                                     field );
      MemberChecks.mustBeFinal( Constants.INPUT_DEFAULT_CLASSNAME, field );
      input.setDefaultField( field );
    }
  }

  @Nonnull
  private String deriveInputDefaultName( @Nonnull final Element element )
    throws ProcessorException
  {
    final String name =
      (String) AnnotationsUtil.getAnnotationValue( element, Constants.INPUT_DEFAULT_CLASSNAME, "name" )
        .getValue();

    if ( isSentinelName( name ) )
    {
      if ( element instanceof ExecutableElement )
      {
        final String deriveName = deriveName( element, DEFAULT_GETTER_PATTERN, name );
        if ( null == deriveName )
        {
          throw new ProcessorException( "@InputDefault target has not specified name nor is it named according " +
                                        "to the convention 'get[Name]Default'.", element );
        }
        return deriveName;
      }
      else
      {
        final String fieldName = element.getSimpleName().toString();
        boolean matched = true;
        final int lengthPrefix = "DEFAULT_".length();
        final int length = fieldName.length();
        if ( fieldName.startsWith( "DEFAULT_" ) && length > lengthPrefix )
        {
          for ( int i = lengthPrefix; i < length; i++ )
          {
            final char ch = fieldName.charAt( i );
            if ( Character.isLowerCase( ch ) ||
                 (
                   ( i != lengthPrefix || !Character.isJavaIdentifierStart( ch ) ) &&
                   ( i == lengthPrefix || !Character.isJavaIdentifierPart( ch ) )
                 ) )
            {
              matched = false;
              break;
            }
          }
        }
        else
        {
          matched = false;
        }
        if ( matched )
        {
          return uppercaseConstantToPascalCase( fieldName.substring( lengthPrefix ) );
        }
        else
        {
          throw new ProcessorException( "@InputDefault target has not specified name nor is it named according " +
                                        "to the convention 'DEFAULT_[NAME]'.", element );
        }
      }
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ProcessorException( "@InputDefault target specified an invalid name '" + name + "'. The " +
                                      "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ProcessorException( "@InputDefault target specified an invalid name '" + name + "'. The " +
                                      "name must not be a java keyword.", element );
      }
      return name;
    }
  }

  @Nonnull
  private String uppercaseConstantToPascalCase( @Nonnull final String candidate )
  {
    final String s = candidate.toLowerCase();
    final StringBuilder sb = new StringBuilder();
    boolean uppercase = false;
    for ( int i = 0; i < s.length(); i++ )
    {
      final char ch = s.charAt( i );
      if ( '_' == ch )
      {
        uppercase = true;
      }
      else if ( uppercase )
      {
        sb.append( Character.toUpperCase( ch ) );
        uppercase = false;
      }
      else
      {
        sb.append( ch );
      }
    }
    return sb.toString();
  }

  private void determineInputs( @Nonnull final ViewDescriptor descriptor )
  {
    final List<InputDescriptor> inputs =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.INPUT_CLASSNAME ) )
        .map( m -> createInputDescriptor( descriptor, m ) )
        .collect( Collectors.toList() );

    final InputDescriptor childrenInput =
      inputs.stream().filter( p -> p.getName().equals( "children" ) ).findAny().orElse( null );
    final InputDescriptor childInput =
      inputs.stream().filter( p -> p.getName().equals( "child" ) ).findAny().orElse( null );
    if ( null != childrenInput && null != childInput )
    {
      throw new ProcessorException( "Multiple candidate children @Input annotated methods: " +
                                    childrenInput.getMethod().getSimpleName() + " and " +
                                    childInput.getMethod().getSimpleName(),
                                    childrenInput.getMethod() );
    }

    descriptor.setInputs( inputs );
  }

  private boolean isInputRequired( @Nonnull final InputDescriptor input )
  {
    final String requiredValue = input.getRequiredValue();
    if ( "ENABLE".equals( requiredValue ) )
    {
      return true;
    }
    else if ( "DISABLE".equals( requiredValue ) )
    {
      return false;
    }
    else if ( input.isContextSource() )
    {
      return false;
    }
    else
    {
      return !input.hasDefaultMethod() &&
             !input.hasDefaultField() &&
             !AnnotationsUtil.hasNullableAnnotation( input.getMethod() );
    }
  }

  @Nonnull
  private InputDescriptor createInputDescriptor( @Nonnull final ViewDescriptor descriptor,
                                                 @Nonnull final ExecutableElement method )
  {
    final String name = deriveInputName( method );
    final ExecutableType methodType = resolveMethodType( descriptor, method );

    verifyNoDuplicateAnnotations( method );
    MemberChecks.mustBeAbstract( Constants.INPUT_CLASSNAME, method );
    MemberChecks.mustNotHaveAnyParameters( Constants.INPUT_CLASSNAME, method );
    MemberChecks.mustReturnAValue( Constants.INPUT_CLASSNAME, method );
    MemberChecks.mustNotThrowAnyExceptions( Constants.INPUT_CLASSNAME, method );
    MemberChecks.mustNotBePackageAccessInDifferentPackage( descriptor.getElement(),
                                                           Constants.VIEW_CLASSNAME,
                                                           Constants.INPUT_CLASSNAME,
                                                           method );
    final TypeMirror returnType = method.getReturnType();
    if ( "build".equals( name ) )
    {
      throw new ProcessorException( "@Input named 'build' is invalid as it conflicts with the method named " +
                                    "build() that is used in the generated Builder classes",
                                    method );
    }
    else if ( "child".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode".equals( returnType.toString() ) ) )
    {
      throw new ProcessorException( "@Input named 'child' should be of type react4j.ReactNode", method );
    }
    else if ( "children".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode[]".equals( returnType.toString() ) ) )
    {
      throw new ProcessorException( "@Input named 'children' should be of type react4j.ReactNode[]", method );
    }

    if ( returnType instanceof TypeVariable )
    {
      final TypeVariable typeVariable = (TypeVariable) returnType;
      final String typeVariableName = typeVariable.asElement().getSimpleName().toString();
      List<? extends TypeParameterElement> typeParameters = method.getTypeParameters();
      if ( typeParameters.stream().anyMatch( p -> p.getSimpleName().toString().equals( typeVariableName ) ) )
      {
        throw new ProcessorException( "@Input named '" + name + "' is has a type variable as a return type " +
                                      "that is declared on the method.", method );
      }
    }
    final String qualifier = (String) AnnotationsUtil
      .getAnnotationValue( method, Constants.INPUT_CLASSNAME, "qualifier" ).getValue();
    final boolean contextInput = isContextInput( method );
    final Element inputType = processingEnv.getTypeUtils().asElement( returnType );
    final boolean immutable = isInputImmutable( method );
    final boolean shouldUpdateOnChange = shouldUpdateOnChange( method, immutable );
    final boolean observable = isInputObservable( descriptor, method, shouldUpdateOnChange, immutable );
    final boolean disposable = null != inputType && isInputDisposable( method, inputType );
    final TypeName typeName = TypeName.get( returnType );
    if ( typeName.isBoxedPrimitive() && AnnotationsUtil.hasNonnullAnnotation( method ) )
    {
      throw new ProcessorException( "@Input named '" + name + "' is a boxed primitive annotated with a " +
                                    "@Nonnull annotation. The return type should be the primitive type.",
                                    method );
    }
    final ImmutableInputKeyStrategy strategy = immutable ? getImmutableInputKeyStrategy( typeName, inputType ) : null;
    if ( !"".equals( qualifier ) && !contextInput )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INPUT_CLASSNAME,
                                                          "specify qualifier parameter unless source=CONTEXT is also specified" ),
                                    method );
    }
    final String requiredValue =
      ( (VariableElement) AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "require" )
        .getValue() )
        .getSimpleName().toString();

    final boolean dependency = isInputDependency( method, immutable, disposable );

    final InputDescriptor inputDescriptor =
      new InputDescriptor( descriptor,
                           name,
                           qualifier,
                           method,
                           methodType,
                           inputType,
                           contextInput,
                           shouldUpdateOnChange,
                           observable,
                           disposable,
                           dependency,
                           strategy,
                           requiredValue );
    if ( inputDescriptor.mayNeedMutableInputAccessedInPostConstructInvariant() )
    {
      if ( ElementsUtil.isWarningSuppressed( method,
                                             Constants.WARNING_MUTABLE_INPUT_ACCESSED_IN_POST_CONSTRUCT,
                                             Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ) )
      {
        inputDescriptor.suppressMutableInputAccessedInPostConstruct();
      }
    }
    return inputDescriptor;
  }

  @Nonnull
  private ImmutableInputKeyStrategy getImmutableInputKeyStrategy( @Nonnull final TypeName typeName,
                                                                  @Nullable final Element element )
  {
    if ( typeName.toString().equals( "java.lang.String" ) )
    {
      return ImmutableInputKeyStrategy.IS_STRING;
    }
    else if ( typeName.isBoxedPrimitive() || typeName.isPrimitive() )
    {
      return ImmutableInputKeyStrategy.TO_STRING;
    }
    else if ( null != element )
    {
      if ( ( ElementKind.CLASS == element.getKind() || ElementKind.INTERFACE == element.getKind() ) &&
           isAssignableToKeyed( element ) )
      {
        return ImmutableInputKeyStrategy.KEYED;
      }
      else if ( ( ElementKind.CLASS == element.getKind() || ElementKind.INTERFACE == element.getKind() ) &&
                (
                  isAssignableToIdentifiable( element ) ||
                  AnnotationsUtil.hasAnnotationOfType( element, Constants.ACT_AS_COMPONENT_CLASSNAME ) ||
                  ( AnnotationsUtil.hasAnnotationOfType( element, Constants.AREZ_COMPONENT_CLASSNAME ) &&
                    isIdRequired( (TypeElement) element ) )
                ) )
      {
        return ImmutableInputKeyStrategy.AREZ_IDENTIFIABLE;
      }
      else if ( ElementKind.ENUM == element.getKind() )
      {
        return ImmutableInputKeyStrategy.ENUM;
      }
    }
    return ImmutableInputKeyStrategy.DYNAMIC;
  }

  private boolean isAssignableToKeyed( @Nonnull final Element element )
  {
    final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement( Constants.KEYED_CLASSNAME );
    return processingEnv.getTypeUtils().isAssignable( element.asType(), typeElement.asType() );
  }

  private boolean isAssignableToIdentifiable( @Nonnull final Element element )
  {
    final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement( Constants.IDENTIFIABLE_CLASSNAME );
    final TypeMirror identifiableErasure = processingEnv.getTypeUtils().erasure( typeElement.asType() );
    return processingEnv.getTypeUtils().isAssignable( element.asType(), identifiableErasure );
  }

  /**
   * The logic from this method has been cloned from Arez.
   * One day we should consider improving Arez so that this is not required somehow?
   */
  private boolean isIdRequired( @Nonnull final TypeElement element )
  {
    final VariableElement requireIdParameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( element, Constants.AREZ_COMPONENT_CLASSNAME, "requireId" )
        .getValue();
    return !"DISABLE".equals( requireIdParameter.getSimpleName().toString() );
  }

  @Nonnull
  private String deriveInputName( @Nonnull final ExecutableElement method )
    throws ProcessorException
  {
    final String specifiedName =
      (String) AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "name" ).getValue();

    final String name = getPropertyAccessorName( method, specifiedName );
    if ( !SourceVersion.isIdentifier( name ) )
    {
      throw new ProcessorException( "@Input target specified an invalid name '" + specifiedName + "'. The " +
                                    "name must be a valid java identifier.", method );
    }
    else if ( SourceVersion.isKeyword( name ) )
    {
      throw new ProcessorException( "@Input target specified an invalid name '" + specifiedName + "'. The " +
                                    "name must not be a java keyword.", method );
    }
    else
    {
      return name;
    }
  }

  private void determineOnErrorMethod( @Nonnull final TypeElement typeElement,
                                       @Nonnull final ViewDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.ON_ERROR_CLASSNAME ) )
      {
        MemberChecks.mustNotBeAbstract( Constants.ON_ERROR_CLASSNAME, method );
        MemberChecks.mustBeSubclassCallable( typeElement,
                                             Constants.VIEW_CLASSNAME,
                                             Constants.ON_ERROR_CLASSNAME,
                                             method );
        MemberChecks.mustNotReturnAnyValue( Constants.ON_ERROR_CLASSNAME, method );
        MemberChecks.mustNotThrowAnyExceptions( Constants.ON_ERROR_CLASSNAME, method );

        boolean infoFound = false;
        boolean errorFound = false;
        for ( final VariableElement parameter : method.getParameters() )
        {
          final TypeName typeName = TypeName.get( parameter.asType() );
          if ( typeName.toString().equals( Constants.ERROR_INFO_CLASSNAME ) )
          {
            if ( infoFound )
            {
              throw new ProcessorException( "@OnError target has multiple parameters of type " +
                                            Constants.ERROR_INFO_CLASSNAME,
                                            method );
            }
            infoFound = true;
          }
          else if ( typeName.toString().equals( Constants.JS_ERROR_CLASSNAME ) )
          {
            if ( errorFound )
            {
              throw new ProcessorException( "@OnError target has multiple parameters of type " +
                                            Constants.JS_ERROR_CLASSNAME,
                                            method );
            }
            errorFound = true;
          }
          else
          {
            throw new ProcessorException( "@OnError target has parameter of invalid type named " +
                                          parameter.getSimpleName().toString(),
                                          parameter );
          }
        }
        descriptor.setOnError( method );
      }
    }
  }

  private void determineScheduleRenderMethods( @Nonnull final TypeElement typeElement,
                                               @Nonnull final ViewDescriptor descriptor )
  {
    final List<ScheduleRenderDescriptor> scheduleRenderDescriptors = new ArrayList<>();
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      final AnnotationMirror annotation =
        AnnotationsUtil.findAnnotationByType( method, Constants.SCHEDULE_RENDER_CLASSNAME );
      if ( null != annotation )
      {
        MemberChecks.mustBeAbstract( Constants.SCHEDULE_RENDER_CLASSNAME, method );
        MemberChecks.mustBeSubclassCallable( typeElement,
                                             Constants.VIEW_CLASSNAME,
                                             Constants.SCHEDULE_RENDER_CLASSNAME,
                                             method );
        MemberChecks.mustNotReturnAnyValue( Constants.SCHEDULE_RENDER_CLASSNAME, method );
        MemberChecks.mustNotThrowAnyExceptions( Constants.SCHEDULE_RENDER_CLASSNAME, method );

        final ViewType viewType = descriptor.getType();
        if ( ViewType.STATEFUL != viewType )
        {
          final String message =
            MemberChecks.mustNot( Constants.SCHEDULE_RENDER_CLASSNAME,
                                  "be enclosed in a type if it is annotated by @View(type=" + viewType +
                                  "). The type must be STATEFUL" );
          throw new ProcessorException( message, method );
        }

        final boolean skipShouldViewUpdate =
          AnnotationsUtil.getAnnotationValueValue( annotation, "skipShouldViewUpdate" );

        scheduleRenderDescriptors.add( new ScheduleRenderDescriptor( method, skipShouldViewUpdate ) );
      }
    }
    descriptor.setScheduleRenderDescriptors( scheduleRenderDescriptors );
  }

  private void determinePublishMethods( @Nonnull final TypeElement typeElement,
                                        @Nonnull final ViewDescriptor descriptor )
  {
    final List<PublishDescriptor> descriptors = new ArrayList<>();
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      final AnnotationMirror annotation = AnnotationsUtil.findAnnotationByType( method, Constants.PUBLISH_CLASSNAME );
      if ( null != annotation )
      {
        MemberChecks.mustBeSubclassCallable( typeElement,
                                             Constants.VIEW_CLASSNAME,
                                             Constants.PUBLISH_CLASSNAME,
                                             method );
        MemberChecks.mustNotHaveAnyParameters( Constants.PUBLISH_CLASSNAME, method );
        MemberChecks.mustNotHaveAnyTypeParameters( Constants.PUBLISH_CLASSNAME, method );
        MemberChecks.mustReturnAValue( Constants.PUBLISH_CLASSNAME, method );
        MemberChecks.mustNotThrowAnyExceptions( Constants.PUBLISH_CLASSNAME, method );

        final String qualifier = AnnotationsUtil.getAnnotationValueValue( annotation, "qualifier" );
        final ExecutableType methodType = resolveMethodType( descriptor, method );

        if ( TypeKind.TYPEVAR == methodType.getReturnType().getKind() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.PUBLISH_CLASSNAME, "return a type variable" ),
                                        method );
        }

        descriptors.add( new PublishDescriptor( qualifier, method, methodType ) );
      }
    }
    descriptor.setPublishDescriptors( descriptors );
  }

  private void determineRenderMethod( @Nonnull final TypeElement typeElement,
                                      @Nonnull final ViewDescriptor descriptor )
  {
    boolean foundRender = false;
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      final AnnotationMirror annotation =
        AnnotationsUtil.findAnnotationByType( method, Constants.RENDER_CLASSNAME );
      if ( null != annotation )
      {
        MemberChecks.mustNotBeAbstract( Constants.RENDER_CLASSNAME, method );
        MemberChecks.mustBeSubclassCallable( typeElement,
                                             Constants.VIEW_CLASSNAME,
                                             Constants.RENDER_CLASSNAME,
                                             method );
        MemberChecks.mustNotHaveAnyParameters( Constants.RENDER_CLASSNAME, method );
        MemberChecks.mustReturnAnInstanceOf( processingEnv,
                                             method,
                                             Constants.RENDER_CLASSNAME,
                                             Constants.VNODE_CLASSNAME );
        MemberChecks.mustNotThrowAnyExceptions( Constants.RENDER_CLASSNAME, method );
        MemberChecks.mustNotHaveAnyTypeParameters( Constants.RENDER_CLASSNAME, method );

        descriptor.setRender( method );
        foundRender = true;
      }
    }
    final boolean requireRender = descriptor.requireRender();
    if ( requireRender && !foundRender )
    {
      throw new ProcessorException( MemberChecks.must( Constants.VIEW_CLASSNAME,
                                                       "contain a method annotated with the " +
                                                       MemberChecks.toSimpleName( Constants.RENDER_CLASSNAME ) +
                                                       " annotation or must specify type=NO_RENDER" ),
                                    typeElement );
    }
    else if ( !requireRender )
    {
      if ( foundRender )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.VIEW_CLASSNAME,
                                                            "contain a method annotated with the " +
                                                            MemberChecks.toSimpleName( Constants.RENDER_CLASSNAME ) +
                                                            " annotation or must not specify type=NO_RENDER" ),
                                      typeElement );
      }
      else if ( !descriptor.hasConstructor() &&
                !descriptor.hasPostConstruct() &&
                null == descriptor.getPostMount() &&
                null == descriptor.getPostRender() &&
                null == descriptor.getPreUpdate() &&
                null == descriptor.getPostUpdate() &&
                !descriptor.hasPreUpdateOnInputChange() &&
                !descriptor.hasPostUpdateOnInputChange() )
      {
        throw new ProcessorException( MemberChecks.must( Constants.VIEW_CLASSNAME,
                                                         "contain lifecycle methods if the the @View(type=NO_RENDER) parameter is specified" ),
                                      typeElement );
      }
    }
  }

  private void determinePostMountMethod( @Nonnull final TypeElement typeElement,
                                         @Nonnull final ViewDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.POST_MOUNT_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.VIEW_CLASSNAME,
                                          Constants.POST_MOUNT_CLASSNAME,
                                          method );
        descriptor.setPostMount( method );
      }
    }
  }

  private void determinePostMountOrUpdateMethod( @Nonnull final TypeElement typeElement,
                                                 @Nonnull final ViewDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.POST_MOUNT_OR_UPDATE_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.VIEW_CLASSNAME,
                                          Constants.POST_MOUNT_OR_UPDATE_CLASSNAME,
                                          method );
        descriptor.setPostRender( method );
      }
    }
  }

  private void determinePostUpdateMethod( @Nonnull final TypeElement typeElement,
                                          @Nonnull final ViewDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.POST_UPDATE_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.VIEW_CLASSNAME,
                                          Constants.POST_UPDATE_CLASSNAME,
                                          method );
        descriptor.setPostUpdate( method );
      }
    }
  }

  private void determinePreUpdateMethod( @Nonnull final TypeElement typeElement,
                                         @Nonnull final ViewDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.PRE_UPDATE_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.VIEW_CLASSNAME,
                                          Constants.PRE_UPDATE_CLASSNAME,
                                          method );
        descriptor.setPreUpdate( method );
      }
    }
  }

  private ExecutableType resolveMethodType( @Nonnull final ViewDescriptor descriptor,
                                            @Nonnull final ExecutableElement method )
  {
    return (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );
  }

  @Nonnull
  private String deriveViewName( @Nonnull final TypeElement typeElement )
  {
    final String name =
      (String) AnnotationsUtil.getAnnotationValue( typeElement, Constants.VIEW_CLASSNAME, "name" )
        .getValue();

    if ( isSentinelName( name ) )
    {
      return typeElement.getSimpleName().toString();
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.VIEW_CLASSNAME ) +
                                      " target specified an invalid name '" + name + "'. The " +
                                      "name must be a valid java identifier.", typeElement );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.VIEW_CLASSNAME ) +
                                      " target specified an invalid name '" + name + "'. The " +
                                      "name must not be a java keyword.", typeElement );
      }
      return name;
    }
  }

  private void determineViewCapabilities( @Nonnull final ViewDescriptor descriptor,
                                          @Nonnull final TypeElement typeElement )
  {
    if ( AnnotationsUtil.hasAnnotationOfType( typeElement, Constants.AREZ_COMPONENT_CLASSNAME ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.VIEW_CLASSNAME,
                                                          "be annotated with the " +
                                                          MemberChecks.toSimpleName( Constants.AREZ_COMPONENT_CLASSNAME ) +
                                                          " as React4j will add the annotation." ),
                                    typeElement );
    }

    if ( descriptor.needsInjection() && !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.VIEW_CLASSNAME ) +
                                    " target has enabled injection integration but the class " +
                                    "has type arguments which is incompatible with injection integration.",
                                    typeElement );
    }
  }

  @Nonnull
  private ViewType extractViewType( @Nonnull final TypeElement typeElement )
  {
    final VariableElement declaredTypeEnum = (VariableElement)
      AnnotationsUtil
        .getAnnotationValue( typeElement, Constants.VIEW_CLASSNAME, "type" )
        .getValue();
    return ViewType.valueOf( declaredTypeEnum.getSimpleName().toString() );
  }

  private boolean shouldUpdateOnChange( @Nonnull final ExecutableElement method,
                                        final boolean immutable )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "shouldUpdateOnChange" )
        .getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        if ( immutable )
        {
          throw new ProcessorException( "@Input target has specified both immutable=true and " +
                                        "shouldUpdateOnChange=ENABLE which is an invalid combination.",
                                        method );
        }
        return true;
      case "DISABLE":
        return false;
      default:
        return !immutable;
    }
  }

  private boolean isInputObservable( @Nonnull final ViewDescriptor descriptor,
                                     @Nonnull final ExecutableElement method,
                                     final boolean shouldUpdateOnChange,
                                     final boolean immutable )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "observable" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        if ( immutable )
        {
          throw new ProcessorException( "@Input target has specified both immutable=true and " +
                                        "observable=ENABLE which is an invalid combination.",
                                        method );
        }
        return true;
      case "DISABLE":
        return false;
      default:
        return shouldUpdateOnChange && hasAnyArezObserverMethods( descriptor.getElement() );
    }
  }

  private boolean hasAnyArezObserverMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.MEMOIZE_CLASSNAME ) ||
                      ( AnnotationsUtil.hasAnnotationOfType( m, Constants.OBSERVE_CLASSNAME ) &&
                        ( !m.getParameters().isEmpty() || !m.getSimpleName().toString().equals( "trackRender" ) ) ) );
  }

  private boolean isInputImmutable( @Nonnull final ExecutableElement method )
  {
    return (Boolean) AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "immutable" )
      .getValue();
  }

  private boolean isInputDependency( @Nonnull final ExecutableElement method,
                                     final boolean immutable,
                                     final boolean disposable )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "dependency" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        if ( !immutable )
        {
          throw new ProcessorException( "@Input target must be immutable if dependency=ENABLE is specified",
                                        method );
        }
        else if ( !disposable )
        {
          throw new ProcessorException( "@Input target must be disposable if dependency=ENABLE is specified",
                                        method );
        }
        return true;
      case "DISABLE":
        return false;
      default:
        return immutable && disposable;
    }
  }

  private boolean isInputDisposable( @Nonnull final ExecutableElement method, @Nonnull final Element inputType )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "disposable" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return
          (
            ElementKind.CLASS == inputType.getKind() &&
            AnnotationsUtil.hasAnnotationOfType( inputType, Constants.AREZ_COMPONENT_CLASSNAME )
          ) ||
          (
            ( ElementKind.CLASS == inputType.getKind() || ElementKind.INTERFACE == inputType.getKind() ) &&
            AnnotationsUtil.hasAnnotationOfType( inputType, Constants.ACT_AS_COMPONENT_CLASSNAME )
          );
    }
  }

  private boolean isContextInput( @Nonnull final ExecutableElement method )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.INPUT_CLASSNAME, "source" ).getValue();
    return "CONTEXT".equals( parameter.getSimpleName().toString() );
  }

  private boolean shouldSetDefaultPriority( @Nonnull final TypeElement typeElement )
  {
    final List<ExecutableElement> methods = getMethods( typeElement );
    return methods
      .stream()
      .filter( method -> !method.getModifiers().contains( Modifier.PRIVATE ) )
      .anyMatch( method -> AnnotationsUtil.hasAnnotationOfType( method, Constants.MEMOIZE_CLASSNAME ) ||
                           AnnotationsUtil.hasAnnotationOfType( method, Constants.OBSERVE_CLASSNAME ) );
  }

  private void verifyNoDuplicateAnnotations( @Nonnull final ExecutableElement method )
    throws ProcessorException
  {
    final List<String> annotations =
      Arrays.asList( Constants.INPUT_DEFAULT_CLASSNAME,
                     Constants.INPUT_VALIDATE_CLASSNAME,
                     Constants.ON_INPUT_CHANGE_CLASSNAME,
                     Constants.INPUT_CLASSNAME );
    MemberChecks.verifyNoOverlappingAnnotations( method, annotations, Collections.emptyMap() );
  }

  @Nonnull
  private List<ExecutableElement> getMethods( @Nonnull final TypeElement typeElement )
  {
    return ElementsUtil.getMethods( typeElement, processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
  }

  private boolean isSentinelName( @Nonnull final String name )
  {
    return SENTINEL_NAME.equals( name );
  }

  @Nonnull
  private String getPropertyAccessorName( @Nonnull final ExecutableElement method,
                                          @Nonnull final String specifiedName )
    throws ProcessorException
  {
    String name = deriveName( method, GETTER_PATTERN, specifiedName );
    if ( null != name )
    {
      return name;
    }
    else if ( method.getReturnType().getKind() == TypeKind.BOOLEAN )
    {
      name = deriveName( method, ISSER_PATTERN, specifiedName );
      if ( null != name )
      {
        return name;
      }
    }
    return method.getSimpleName().toString();
  }

  @Nullable
  private String deriveName( @Nonnull final Element method, @Nonnull final Pattern pattern, @Nonnull final String name )
    throws ProcessorException
  {
    if ( isSentinelName( name ) )
    {
      final String methodName = method.getSimpleName().toString();
      final Matcher matcher = pattern.matcher( methodName );
      if ( matcher.find() )
      {
        final String candidate = matcher.group( 1 );
        return Character.toLowerCase( candidate.charAt( 0 ) ) + candidate.substring( 1 );
      }
      else
      {
        return null;
      }
    }
    else
    {
      return name;
    }
  }
}
