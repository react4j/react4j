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
@SupportedAnnotationTypes( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "react4j.defer.unresolved", "react4j.defer.errors", "react4j.debug" } )
public final class React4jProcessor
  extends AbstractStandardProcessor
{
  private static final String SENTINEL_NAME = "<default>";
  private static final Pattern DEFAULT_GETTER_PATTERN = Pattern.compile( "^get([A-Z].*)Default$" );
  private static final Pattern VALIDATE_PROP_PATTERN = Pattern.compile( "^validate([A-Z].*)$" );
  private static final Pattern LAST_PROP_PATTERN = Pattern.compile( "^last([A-Z].*)$" );
  private static final Pattern PREV_PROP_PATTERN = Pattern.compile( "^prev([A-Z].*)$" );
  private static final Pattern PROP_PATTERN = Pattern.compile( "^([a-z].*)$" );
  @Nonnull
  private final DeferredElementSet _deferredTypes = new DeferredElementSet();

  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    processTypeElements( annotations,
                         env,
                         Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                         _deferredTypes,
                         this::process );
    errorIfProcessingOverAndInvalidTypesDetected( env );
    return true;
  }

  @Override
  @Nonnull
  protected final String getIssueTrackerURL()
  {
    return "https://github.com/react4j/react4j/issues";
  }

  @Nonnull
  @Override
  protected final String getOptionPrefix()
  {
    return "react4j";
  }

  private void process( @Nonnull final TypeElement element )
    throws IOException, ProcessorException
  {
    final ComponentDescriptor descriptor = parse( element );
    final String packageName = descriptor.getPackageName();
    emitTypeSpec( packageName, ComponentGenerator.buildType( processingEnv, descriptor ) );
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
      .anyMatch( e -> AnnotationsUtil.hasAnnotationOfType( e, Constants.POST_CONSTRUCT_ANNOTATION_CLASSNAME ) );
  }

  @Nonnull
  private ComponentDescriptor parse( @Nonnull final TypeElement typeElement )
  {
    final String name = deriveComponentName( typeElement );
    final ComponentType type = extractComponentType( typeElement );
    final boolean hasPostConstruct = hasPostConstruct( typeElement );
    final boolean shouldSetDefaultPriority = shouldSetDefaultPriority( typeElement );

    MemberChecks.mustNotBeFinal( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME, typeElement );
    MemberChecks.mustBeAbstract( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME, typeElement );
    if ( ElementKind.CLASS != typeElement.getKind() )
    {
      throw new ProcessorException( "@ReactComponent target must be a class", typeElement );
    }
    else if ( ElementsUtil.isNonStaticNestedClass( typeElement ) )
    {
      throw new ProcessorException( "@ReactComponent target must not be a non-static nested class", typeElement );
    }
    final List<ExecutableElement> constructors = ElementsUtil.getConstructors( typeElement );
    if ( 1 != constructors.size() || !isConstructorValid( constructors.get( 0 ) ) )
    {
      throw new ProcessorException( MemberChecks.must( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                       "have a single, package-access constructor or the default constructor" ),
                                    typeElement );
    }
    final ExecutableElement constructor = constructors.get( 0 );

    final boolean inject = deriveInject( typeElement, constructor );
    final boolean sting = deriveSting( typeElement, constructor );

    final List<? extends VariableElement> parameters = constructor.getParameters();
    if ( inject )
    {
      if ( parameters.isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
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
        throw new ProcessorException( MemberChecks.mustNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                            "have specified inject=DISABLED and have a constructor parameter annotated with the " +
                                                            Constants.JSR_330_NAMED_CLASSNAME + " annotation" ),
                                      constructor );
      }
    }

    if ( sting )
    {
      if ( parameters.isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
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
        throw new ProcessorException( MemberChecks.mustNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                            "have specified sting=DISABLED and have a constructor parameter annotated with the " +
                                                            Constants.STING_NAMED_CLASSNAME + " annotation" ),
                                      constructor );
      }
    }

    final ComponentDescriptor descriptor =
      new ComponentDescriptor( name,
                               typeElement,
                               constructor,
                               type,
                               inject,
                               sting,
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
                                                  Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME ) )
        {
          final String message =
            MemberChecks.shouldNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                    "declare a public method. " +
                                    MemberChecks.suppressedBy( Constants.WARNING_PUBLIC_METHOD,
                                                               Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME ) );
          processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, method );
        }
        if ( method.getModifiers().contains( Modifier.FINAL ) &&
             ElementsUtil.isWarningNotSuppressed( method,
                                                  Constants.WARNING_FINAL_METHOD,
                                                  Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME ) )
        {
          final String message =
            MemberChecks.shouldNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                    "declare a final method. " +
                                    MemberChecks.suppressedBy( Constants.WARNING_FINAL_METHOD,
                                                               Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME ) );
          processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, method );
        }
        if ( method.getModifiers().contains( Modifier.PROTECTED ) &&
             ElementsUtil.isWarningNotSuppressed( method,
                                                  Constants.WARNING_PROTECTED_METHOD,
                                                  Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME ) &&
             !isMethodAProtectedOverride( typeElement, method ) )
        {
          final String message =
            MemberChecks.shouldNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                    "declare a protected method. " +
                                    MemberChecks.suppressedBy( Constants.WARNING_PROTECTED_METHOD,
                                                               Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME ) );
          processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, method );
        }
      }
    }

    determineComponentCapabilities( descriptor, typeElement );
    determineProps( descriptor );
    determinePropValidatesMethods( descriptor );
    determineOnPropChangeMethods( descriptor );
    determineDefaultPropsMethods( descriptor );
    determineDefaultPropsFields( descriptor );
    determinePreUpdateMethod( typeElement, descriptor );
    determinePostMountOrUpdateMethod( typeElement, descriptor );
    determinePostUpdateMethod( typeElement, descriptor );
    determinePostMountMethod( typeElement, descriptor );
    determineOnErrorMethod( typeElement, descriptor );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      if ( !isPropRequired( prop ) )
      {
        prop.markAsOptional();
      }
      else
      {
        if ( prop.isContextProp() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.PROP_ANNOTATION_CLASSNAME,
                                                              "specify require=ENABLE parameter when the for source=CONTEXT parameter is specified" ),
                                        prop.getMethod() );
        }
      }
    }

    /*
     * Sorting must occur after @PropDefault has been processed to ensure the sorting
     * correctly sorts optional props after required props.
     */
    descriptor.sortProps();

    verifyPropsNotAnnotatedWithArezAnnotations( descriptor );
    verifyPropsNotCollectionOfArezComponents( descriptor );

    return descriptor;
  }

  private boolean isMethodAProtectedOverride( @Nonnull final TypeElement typeElement,
                                              @Nonnull final ExecutableElement method )
  {
    final ExecutableElement overriddenMethod = ProcessorUtil.getOverriddenMethod( processingEnv, typeElement, method );
    return null != overriddenMethod && overriddenMethod.getModifiers().contains( Modifier.PROTECTED );
  }

  private boolean deriveInject( @Nonnull final TypeElement typeElement, final @Nonnull ExecutableElement constructor )
  {
    final String inject =
      AnnotationsUtil.getEnumAnnotationParameter( typeElement,
                                                  Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
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
                                                  Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
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

  private void verifyPropsNotCollectionOfArezComponents( @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final ExecutableElement method = prop.getMethod();
      final TypeMirror returnType = method.getReturnType();
      if ( TypeKind.DECLARED == returnType.getKind() )
      {
        final DeclaredType declaredType = (DeclaredType) returnType;
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if ( isCollection( declaredType ) )
        {
          if ( 1 == typeArguments.size() && isArezComponent( typeArguments.get( 0 ) ) )
          {
            throw new ProcessorException( "@Prop target is a collection that contains Arez components. " +
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
            throw new ProcessorException( "@Prop target is a collection that contains Arez components. " +
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
          throw new ProcessorException( "@Prop target is an array that contains Arez components. " +
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
             .anyMatch( a -> a.getAnnotationType().toString().equals( Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME ) );
  }

  private void verifyPropsNotAnnotatedWithArezAnnotations( @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final ExecutableElement method = prop.getMethod();
      for ( final AnnotationMirror mirror : method.getAnnotationMirrors() )
      {
        final String classname = mirror.getAnnotationType().toString();
        if ( classname.startsWith( "arez.annotations." ) )
        {
          throw new ProcessorException( "@Prop target must not be annotated with any arez annotations but " +
                                        "is annotated by '" + classname + "'.", method );
        }
      }
    }
  }

  private void determineOnPropChangeMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    final ArrayList<OnPropChangeDescriptor> onPropChangeDescriptors = new ArrayList<>();
    for ( final ExecutableElement method : methods )
    {
      final VariableElement phase = (VariableElement)
        AnnotationsUtil.getAnnotationValue( method, Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME, "phase" ).getValue();
      final boolean preUpdate = phase.getSimpleName().toString().equals( "PRE" );

      final List<? extends VariableElement> parameters = method.getParameters();
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      final List<? extends TypeMirror> parameterTypes = methodType.getParameterTypes();

      MemberChecks.mustBeSubclassCallable( descriptor.getElement(),
                                           Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                           Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME,
                                           method );
      MemberChecks.mustNotThrowAnyExceptions( Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME, method );
      MemberChecks.mustNotReturnAnyValue( Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME, method );

      final int parameterCount = parameters.size();
      if ( 0 == parameterCount )
      {
        throw new ProcessorException( "@OnPropChange target must have at least 1 parameter.", method );
      }
      final List<PropDescriptor> propDescriptors = new ArrayList<>( parameterCount );
      for ( int i = 0; i < parameterCount; i++ )
      {
        final VariableElement parameter = parameters.get( i );
        final String name = deriveOnPropChangeName( parameter );
        final PropDescriptor prop = descriptor.findPropNamed( name );
        if ( null == prop )
        {
          throw new ProcessorException( "@OnPropChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' and the parameter is associated with a " +
                                        "@Prop named '" + name + "' but there is no corresponding @Prop " +
                                        "annotated method.", parameter );
        }
        final Types typeUtils = processingEnv.getTypeUtils();
        if ( !typeUtils.isAssignable( parameterTypes.get( i ), prop.getMethodType().getReturnType() ) )
        {
          throw new ProcessorException( "@OnPropChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' and the parameter type is not " +
                                        "assignable to the return type of the associated @Prop annotated method.",
                                        method );
        }
        final boolean mismatchedNullability =
          (
            AnnotationsUtil.hasNonnullAnnotation( parameter ) &&
            AnnotationsUtil.hasNullableAnnotation( prop.getMethod() )
          ) ||
          (
            AnnotationsUtil.hasNullableAnnotation( parameter ) &&
            AnnotationsUtil.hasNonnullAnnotation( prop.getMethod() ) );

        if ( mismatchedNullability )
        {
          throw new ProcessorException( "@OnPropChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' that has a nullability annotation " +
                                        "incompatible with the associated @Prop method named " +
                                        method.getSimpleName(), method );
        }
        if ( prop.isImmutable() )
        {
          throw new ProcessorException( "@OnPropChange target has a parameter named '" +
                                        parameter.getSimpleName() + "' that is associated with a @Prop " +
                                        "annotated method and the prop is specified as immutable.", method );
        }
        propDescriptors.add( prop );
      }
      onPropChangeDescriptors.add( new OnPropChangeDescriptor( method, propDescriptors, preUpdate ) );
    }
    descriptor.setOnPropChangeDescriptors( onPropChangeDescriptors );
  }

  @Nonnull
  private String deriveOnPropChangeName( @Nonnull final VariableElement parameter )
  {
    final AnnotationValue value =
      AnnotationsUtil.findAnnotationValue( parameter, Constants.PROP_REF_ANNOTATION_CLASSNAME, "value" );

    if ( null != value )
    {
      return (String) value.getValue();
    }
    else
    {
      final String parameterName = parameter.getSimpleName().toString();
      if ( LAST_PROP_PATTERN.matcher( parameterName ).matches() ||
           PREV_PROP_PATTERN.matcher( parameterName ).matches() )
      {
        return Character.toLowerCase( parameterName.charAt( 4 ) ) + parameterName.substring( 5 );
      }
      else if ( PROP_PATTERN.matcher( parameterName ).matches() )
      {
        return parameterName;
      }
      else
      {
        throw new ProcessorException( "@OnPropChange target has a parameter named '" + parameterName +
                                      "' is not explicitly associated with a prop using @PropRef nor does it " +
                                      "follow required naming conventions 'prev[MyProp]', 'last[MyProp]' or " +
                                      "'[myProp]'.", parameter );
      }
    }
  }

  private void determinePropValidatesMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : methods )
    {
      final String name = derivePropValidateName( method );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ProcessorException( "@PropValidate target for prop named '" + name + "' has no corresponding " +
                                      "@Prop annotated method.", method );
      }
      if ( 1 != method.getParameters().size() )
      {
        throw new ProcessorException( "@PropValidate target must have exactly 1 parameter", method );
      }
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getParameterTypes().get( 0 ),
                                                       prop.getMethodType().getReturnType() ) )
      {
        throw new ProcessorException( "@PropValidate target has a parameter type that is not assignable to the " +
                                      "return type of the associated @Prop annotated method.", method );
      }
      MemberChecks.mustBeSubclassCallable( descriptor.getElement(),
                                           Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                           Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME,
                                           method );
      MemberChecks.mustNotThrowAnyExceptions( Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME, method );
      MemberChecks.mustNotReturnAnyValue( Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME, method );

      final VariableElement param = method.getParameters().get( 0 );
      final boolean mismatchedNullability =
        (
          AnnotationsUtil.hasNonnullAnnotation( param ) &&
          AnnotationsUtil.hasNullableAnnotation( prop.getMethod() )
        ) ||
        (
          AnnotationsUtil.hasNullableAnnotation( param ) &&
          AnnotationsUtil.hasNonnullAnnotation( prop.getMethod() ) );

      if ( mismatchedNullability )
      {
        throw new ProcessorException( "@PropValidate target has a parameter that has a nullability annotation " +
                                      "incompatible with the associated @Prop method named " +
                                      prop.getMethod().getSimpleName(), method );
      }
      prop.setValidateMethod( method );
    }
  }

  @Nonnull
  private String derivePropValidateName( @Nonnull final Element element )
    throws ProcessorException
  {
    final String name =
      (String) AnnotationsUtil.getAnnotationValue( element, Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME, "name" )
        .getValue();

    if ( isSentinelName( name ) )
    {
      final String deriveName = ProcessorUtil.deriveName( element, VALIDATE_PROP_PATTERN, name, SENTINEL_NAME );
      if ( null == deriveName )
      {
        throw new ProcessorException( "@PropValidate target has not specified name nor is it named according " +
                                      "to the convention 'validate[Name]Prop'.", element );
      }
      return deriveName;
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ProcessorException( "@PropValidate target specified an invalid name '" + name + "'. The " +
                                      "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ProcessorException( "@PropValidate target specified an invalid name '" + name + "'. The " +
                                      "name must not be a java keyword.", element );
      }
      return name;
    }
  }

  private void determineDefaultPropsMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> defaultPropsMethods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : defaultPropsMethods )
    {
      final String name = derivePropDefaultName( method );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ProcessorException( "@PropDefault target for prop named '" + name + "' has no corresponding " +
                                      "@Prop annotated method.", method );
      }
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getReturnType(),
                                                       prop.getMethodType().getReturnType() ) )
      {
        throw new ProcessorException( "@PropDefault target has a return type that is not assignable to the " +
                                      "return type of the associated @Prop annotated method.", method );
      }
      MemberChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                     Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                     method );
      MemberChecks.mustNotHaveAnyParameters( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );
      MemberChecks.mustNotThrowAnyExceptions( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );
      MemberChecks.mustReturnAValue( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );

      prop.setDefaultMethod( method );
    }
  }

  private void determineDefaultPropsFields( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<VariableElement> defaultPropsFields =
      ElementsUtil.getFields( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final VariableElement field : defaultPropsFields )
    {
      final String name = derivePropDefaultName( field );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ProcessorException( "@PropDefault target for prop named '" + name + "' has no corresponding " +
                                      "@Prop annotated method.", field );
      }
      if ( !processingEnv.getTypeUtils().isAssignable( field.asType(), prop.getMethodType().getReturnType() ) )
      {
        throw new ProcessorException( "@PropDefault target has a type that is not assignable to the " +
                                      "return type of the associated @Prop annotated method.", field );
      }
      MemberChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                     Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                     field );
      MemberChecks.mustBeFinal( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, field );
      prop.setDefaultField( field );
    }
  }

  @Nonnull
  private String derivePropDefaultName( @Nonnull final Element element )
    throws ProcessorException
  {
    final String name =
      (String) AnnotationsUtil.getAnnotationValue( element, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, "name" )
        .getValue();

    if ( isSentinelName( name ) )
    {
      if ( element instanceof ExecutableElement )
      {
        final String deriveName = ProcessorUtil.deriveName( element, DEFAULT_GETTER_PATTERN, name, SENTINEL_NAME );
        if ( null == deriveName )
        {
          throw new ProcessorException( "@PropDefault target has not specified name nor is it named according " +
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
          throw new ProcessorException( "@PropDefault target has not specified name nor is it named according " +
                                        "to the convention 'DEFAULT_[NAME]'.", element );
        }
      }
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ProcessorException( "@PropDefault target specified an invalid name '" + name + "'. The " +
                                      "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ProcessorException( "@PropDefault target specified an invalid name '" + name + "'. The " +
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

  private void determineProps( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> props =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.PROP_ANNOTATION_CLASSNAME ) )
        .map( m -> createPropDescriptor( descriptor, m ) )
        .collect( Collectors.toList() );

    final PropDescriptor childrenProp =
      props.stream().filter( p -> p.getName().equals( "children" ) ).findAny().orElse( null );
    final PropDescriptor childProp =
      props.stream().filter( p -> p.getName().equals( "child" ) ).findAny().orElse( null );
    if ( null != childrenProp && null != childProp )
    {
      throw new ProcessorException( "Multiple candidate children @Prop annotated methods: " +
                                    childrenProp.getMethod().getSimpleName() + " and " +
                                    childProp.getMethod().getSimpleName(),
                                    childrenProp.getMethod() );
    }

    descriptor.setProps( props );
  }

  private boolean isPropRequired( @Nonnull final PropDescriptor prop )
  {
    final String requiredValue = prop.getRequiredValue();
    if ( "ENABLE".equals( requiredValue ) )
    {
      return true;
    }
    else if ( "DISABLE".equals( requiredValue ) )
    {
      return false;
    }
    else if ( prop.isContextProp() )
    {
      return false;
    }
    else
    {
      return !prop.hasDefaultMethod() &&
             !prop.hasDefaultField() &&
             !AnnotationsUtil.hasNullableAnnotation( prop.getMethod() );
    }
  }

  @Nonnull
  private PropDescriptor createPropDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                               @Nonnull final ExecutableElement method )
  {
    final String name = derivePropName( method );
    final ExecutableType methodType = resolveMethodType( descriptor, method );

    verifyNoDuplicateAnnotations( method );
    MemberChecks.mustBeAbstract( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MemberChecks.mustNotHaveAnyParameters( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MemberChecks.mustReturnAValue( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MemberChecks.mustNotThrowAnyExceptions( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MemberChecks.mustNotBePackageAccessInDifferentPackage( descriptor.getElement(),
                                                           Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                           Constants.PROP_ANNOTATION_CLASSNAME,
                                                           method );
    final TypeMirror returnType = method.getReturnType();
    if ( "build".equals( name ) )
    {
      throw new ProcessorException( "@Prop named 'build' is invalid as it conflicts with the method named " +
                                    "build() that is used in the generated Builder classes",
                                    method );
    }
    else if ( "child".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode".equals( returnType.toString() ) ) )
    {
      throw new ProcessorException( "@Prop named 'child' should be of type react4j.ReactNode", method );
    }
    else if ( "children".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode[]".equals( returnType.toString() ) ) )
    {
      throw new ProcessorException( "@Prop named 'children' should be of type react4j.ReactNode[]", method );
    }

    if ( returnType instanceof TypeVariable )
    {
      final TypeVariable typeVariable = (TypeVariable) returnType;
      final String typeVariableName = typeVariable.asElement().getSimpleName().toString();
      List<? extends TypeParameterElement> typeParameters = method.getTypeParameters();
      if ( typeParameters.stream().anyMatch( p -> p.getSimpleName().toString().equals( typeVariableName ) ) )
      {
        throw new ProcessorException( "@Prop named '" + name + "' is has a type variable as a return type " +
                                      "that is declared on the method.", method );
      }
    }
    final String qualifier = (String) AnnotationsUtil
      .getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "qualifier" ).getValue();
    final boolean contextProp = isContextProp( method );
    final Element propType = processingEnv.getTypeUtils().asElement( returnType );
    final boolean immutable = isPropImmutable( method );
    final boolean shouldUpdateOnChange = shouldUpdateOnChange( method, immutable );
    final boolean observable = isPropObservable( descriptor, method, shouldUpdateOnChange, immutable );
    final boolean disposable = null != propType && isPropDisposable( method, propType );
    final TypeName typeName = TypeName.get( returnType );
    if ( typeName.isBoxedPrimitive() && AnnotationsUtil.hasNonnullAnnotation( method ) )
    {
      throw new ProcessorException( "@Prop named '" + name + "' is a boxed primitive annotated with a " +
                                    "@Nonnull annotation. The return type should be the primitive type.",
                                    method );
    }
    final ImmutablePropKeyStrategy strategy = immutable ? getImmutablePropKeyStrategy( typeName, propType ) : null;
    if ( immutable && null == strategy )
    {
      throw new ProcessorException( "@Prop named '" + name + "' has specified the 'immutable' parameter as " +
                                    "true but the annotation processor can not extract a key part from the " +
                                    "type. This is because the type is not recognized as conforming to the " +
                                    "rules as documented in the javadocs for the immutable parameter of " +
                                    "the @Prop annotation.",
                                    method );
    }
    if ( !"".equals( qualifier ) && !contextProp )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.PROP_ANNOTATION_CLASSNAME,
                                                          "specify qualifier parameter unless source=CONTEXT is also specified" ),
                                    method );
    }
    final String requiredValue =
      ( (VariableElement) AnnotationsUtil.getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "require" )
        .getValue() )
        .getSimpleName().toString();

    final PropDescriptor propDescriptor =
      new PropDescriptor( descriptor,
                          name,
                          qualifier,
                          method,
                          methodType,
                          contextProp,
                          shouldUpdateOnChange,
                          observable,
                          disposable,
                          strategy,
                          requiredValue );
    if ( propDescriptor.mayNeedMutablePropAccessedInPostConstructInvariant() )
    {
      if ( ElementsUtil.isWarningSuppressed( method,
                                             Constants.WARNING_MUTABLE_PROP_ACCESSED_IN_POST_CONSTRUCT,
                                             Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME ) )
      {
        propDescriptor.suppressMutablePropAccessedInPostConstruct();
      }
    }
    return propDescriptor;
  }

  @Nullable
  private ImmutablePropKeyStrategy getImmutablePropKeyStrategy( @Nonnull final TypeName typeName,
                                                                @Nullable final Element element )
  {
    if ( typeName.toString().equals( "java.lang.String" ) )
    {
      return ImmutablePropKeyStrategy.IS_STRING;
    }
    else if ( typeName.isBoxedPrimitive() || typeName.isPrimitive() )
    {
      return ImmutablePropKeyStrategy.TO_STRING;
    }
    else if ( null != element )
    {
      if ( ( ElementKind.CLASS == element.getKind() || ElementKind.INTERFACE == element.getKind() ) &&
           isAssignableToKeyed( element ) )
      {
        return ImmutablePropKeyStrategy.KEYED;
      }
      else if ( ElementKind.CLASS == element.getKind() &&
                AnnotationsUtil.hasAnnotationOfType( element, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME ) &&
                isIdRequired( (TypeElement) element ) )
      {
        return ImmutablePropKeyStrategy.AREZ_IDENTIFIABLE;
      }
      else if ( ( ElementKind.CLASS == element.getKind() || ElementKind.INTERFACE == element.getKind() ) &&
                AnnotationsUtil.hasAnnotationOfType( element, Constants.ACT_AS_COMPONENT_ANNOTATION_CLASSNAME ) )
      {
        return ImmutablePropKeyStrategy.AREZ_IDENTIFIABLE;
      }
      else if ( ElementKind.ENUM == element.getKind() )
      {
        return ImmutablePropKeyStrategy.ENUM;
      }
    }
    return null;
  }

  private boolean isAssignableToKeyed( @Nonnull final Element element )
  {
    final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement( Constants.KEYED_CLASSNAME );
    return processingEnv.getTypeUtils().isAssignable( element.asType(), typeElement.asType() );
  }

  /**
   * The logic from this method has been cloned from Arez.
   * One day we should consider improving Arez so that this is not required somehow?
   */
  private boolean isIdRequired( @Nonnull final TypeElement element )
  {
    final VariableElement requireIdParameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( element, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME, "requireId" )
        .getValue();
    switch ( requireIdParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return getMethods( element )
          .stream()
          .anyMatch( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.COMPONENT_ID_ANNOTATION_CLASSNAME ) ||
                          AnnotationsUtil.hasAnnotationOfType( m, Constants.COMPONENT_ID_REF_ANNOTATION_CLASSNAME ) );
    }
  }

  @Nonnull
  private String derivePropName( @Nonnull final ExecutableElement method )
    throws ProcessorException
  {
    final String specifiedName =
      (String) AnnotationsUtil.getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "name" ).getValue();

    final String name = ProcessorUtil.getPropertyAccessorName( method, specifiedName, SENTINEL_NAME );
    if ( !SourceVersion.isIdentifier( name ) )
    {
      throw new ProcessorException( "@Prop target specified an invalid name '" + specifiedName + "'. The " +
                                    "name must be a valid java identifier.", method );
    }
    else if ( SourceVersion.isKeyword( name ) )
    {
      throw new ProcessorException( "@Prop target specified an invalid name '" + specifiedName + "'. The " +
                                    "name must not be a java keyword.", method );
    }
    else
    {
      return name;
    }
  }

  private void determineOnErrorMethod( @Nonnull final TypeElement typeElement,
                                       @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.ON_ERROR_ANNOTATION_CLASSNAME ) )
      {
        MemberChecks.mustNotBeAbstract( Constants.ON_ERROR_ANNOTATION_CLASSNAME, method );
        MemberChecks.mustBeSubclassCallable( typeElement,
                                             Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                             Constants.ON_ERROR_ANNOTATION_CLASSNAME,
                                             method );
        MemberChecks.mustNotReturnAnyValue( Constants.ON_ERROR_ANNOTATION_CLASSNAME, method );
        MemberChecks.mustNotThrowAnyExceptions( Constants.ON_ERROR_ANNOTATION_CLASSNAME, method );

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

  private void determinePostMountMethod( @Nonnull final TypeElement typeElement,
                                         @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.POST_MOUNT_ANNOTATION_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                          Constants.POST_MOUNT_ANNOTATION_CLASSNAME,
                                          method );
        descriptor.setPostMount( method );
      }
    }
  }

  private void determinePostMountOrUpdateMethod( @Nonnull final TypeElement typeElement,
                                                 @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.POST_MOUNT_OR_UPDATE_ANNOTATION_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                          Constants.POST_MOUNT_OR_UPDATE_ANNOTATION_CLASSNAME,
                                          method );
        descriptor.setPostRender( method );
      }
    }
  }

  private void determinePostUpdateMethod( @Nonnull final TypeElement typeElement,
                                          @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.POST_UPDATE_ANNOTATION_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                          Constants.POST_UPDATE_ANNOTATION_CLASSNAME,
                                          method );
        descriptor.setPostUpdate( method );
      }
    }
  }

  private void determinePreUpdateMethod( @Nonnull final TypeElement typeElement,
                                         @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.PRE_UPDATE_ANNOTATION_CLASSNAME ) )
      {
        MemberChecks.mustBeLifecycleHook( typeElement,
                                          Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                          Constants.PRE_UPDATE_ANNOTATION_CLASSNAME,
                                          method );
        descriptor.setPreUpdate( method );
      }
    }
  }

  private ExecutableType resolveMethodType( @Nonnull final ComponentDescriptor descriptor,
                                            @Nonnull final ExecutableElement method )
  {
    return (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );
  }

  @Nonnull
  private String deriveComponentName( @Nonnull final TypeElement typeElement )
  {
    final String name =
      (String) AnnotationsUtil.getAnnotationValue( typeElement, Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME, "name" )
        .getValue();

    if ( isSentinelName( name ) )
    {
      return typeElement.getSimpleName().toString();
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ProcessorException( "@ReactComponent target specified an invalid name '" + name + "'. The " +
                                      "name must be a valid java identifier.", typeElement );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ProcessorException( "@ReactComponent target specified an invalid name '" + name + "'. The " +
                                      "name must not be a java keyword.", typeElement );
      }
      return name;
    }
  }

  private void determineComponentCapabilities( @Nonnull final ComponentDescriptor descriptor,
                                               @Nonnull final TypeElement typeElement )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
    final TypeMirror rawComponentType = processingEnv.getTypeUtils().erasure( componentType.asType() );

    final TypeMirror declaredType = descriptor.getDeclaredType();

    final boolean isComponent = processingEnv.getTypeUtils().isSubtype( declaredType, rawComponentType );

    if ( !isComponent )
    {
      throw new ProcessorException( MemberChecks.must( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                       "be a subclass of react4j.Component" ),
                                    typeElement );
    }
    else
    {
      if ( AnnotationsUtil.hasAnnotationOfType( typeElement, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                            "be annotated with the " +
                                                            MemberChecks.toSimpleName( Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME ) +
                                                            " as React4j will add the annotation." ),
                                      typeElement );
      }
    }

    if ( descriptor.needsInjection() && !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME ) +
                                    " target has enabled injection integration but the class " +
                                    "has type arguments which is incompatible with injection integration.",
                                    typeElement );
    }

    final boolean hasArezElements =
      descriptor.trackRender() ||
      getMethods( typeElement ).stream().anyMatch( e -> e.getAnnotationMirrors()
        .stream()
        .map( a -> a.getAnnotationType().toString() )
        .anyMatch( n -> n.startsWith( "arez.annotations." ) &&
                        !(
                          // Ignore these annotations as they do not create disposable elements
                          n.endsWith( "PostConstruct" ) ||
                          n.endsWith( "ContextRef" ) ||
                          n.endsWith( "ComponentTypeNameRef" ) ||
                          n.endsWith( "ComponentNameRef" ) ||
                          n.endsWith( "ComponentIdRef" ) ||
                          n.endsWith( "ComponentId" ) ||
                          n.endsWith( "Action" )
                        )
        )
      ) ||
      ElementsUtil.getFields( typeElement )
        .stream()
        .anyMatch( e -> e.getAnnotationMirrors()
          .stream()
          .map( a -> a.getAnnotationType().toString() )
          .anyMatch( n -> n.equals( Constants.CASCADE_DISPOSE_ANNOTATION_CLASSNAME ) ||
                          n.equals( Constants.COMPONENT_DEPENDENCY_ANNOTATION_CLASSNAME ) )
        );

    descriptor.setHasArezElements( hasArezElements );
  }

  @Nonnull
  private ComponentType extractComponentType( @Nonnull final TypeElement typeElement )
  {
    final VariableElement declaredTypeEnum = (VariableElement)
      AnnotationsUtil
        .getAnnotationValue( typeElement, Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME, "type" )
        .getValue();
    return ComponentType.valueOf( declaredTypeEnum.getSimpleName().toString() );
  }

  private boolean shouldUpdateOnChange( @Nonnull final ExecutableElement method,
                                        final boolean immutable )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "shouldUpdateOnChange" )
        .getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        if ( immutable )
        {
          throw new ProcessorException( "@Prop target has specified both immutable=true and " +
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

  private boolean isPropObservable( @Nonnull final ComponentDescriptor descriptor,
                                    @Nonnull final ExecutableElement method,
                                    final boolean shouldUpdateOnChange,
                                    final boolean immutable )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "observable" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        if ( immutable )
        {
          throw new ProcessorException( "@Prop target has specified both immutable=true and " +
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
      .anyMatch( m -> AnnotationsUtil.hasAnnotationOfType( m, Constants.MEMOIZE_ANNOTATION_CLASSNAME ) ||
                      ( AnnotationsUtil.hasAnnotationOfType( m, Constants.OBSERVE_ANNOTATION_CLASSNAME ) &&
                        ( !m.getParameters().isEmpty() || !m.getSimpleName().toString().equals( "trackRender" ) ) ) );
  }

  private boolean isPropImmutable( @Nonnull final ExecutableElement method )
  {
    return (Boolean) AnnotationsUtil.getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "immutable" )
      .getValue();
  }

  private boolean isPropDisposable( @Nonnull final ExecutableElement method, @Nonnull final Element propType )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "disposable" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return
          (
            ElementKind.CLASS == propType.getKind() &&
            AnnotationsUtil.hasAnnotationOfType( propType, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME )
          ) ||
          (
            ( ElementKind.CLASS == propType.getKind() || ElementKind.INTERFACE == propType.getKind() ) &&
            AnnotationsUtil.hasAnnotationOfType( propType, Constants.ACT_AS_COMPONENT_ANNOTATION_CLASSNAME )
          );
    }
  }

  private boolean isContextProp( @Nonnull final ExecutableElement method )
  {
    final VariableElement parameter = (VariableElement)
      AnnotationsUtil.getAnnotationValue( method, Constants.PROP_ANNOTATION_CLASSNAME, "source" ).getValue();
    return "CONTEXT".equals( parameter.getSimpleName().toString() );
  }

  private boolean shouldSetDefaultPriority( @Nonnull final TypeElement typeElement )
  {
    final List<ExecutableElement> methods = getMethods( typeElement );
    return methods
      .stream()
      .filter( method -> !method.getModifiers().contains( Modifier.PRIVATE ) )
      .anyMatch( method -> AnnotationsUtil.hasAnnotationOfType( method, Constants.MEMOIZE_ANNOTATION_CLASSNAME ) ||
                           AnnotationsUtil.hasAnnotationOfType( method, Constants.OBSERVE_ANNOTATION_CLASSNAME ) );
  }

  private void verifyNoDuplicateAnnotations( @Nonnull final ExecutableElement method )
    throws ProcessorException
  {
    final List<String> annotations =
      Arrays.asList( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                     Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME,
                     Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME,
                     Constants.PROP_ANNOTATION_CLASSNAME );
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
}
