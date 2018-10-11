package react4j.processor;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import static javax.tools.Diagnostic.Kind.*;

/**
 * Annotation processor that analyzes React annotated source and generates models from the annotations.
 */
@SuppressWarnings( "Duplicates" )
@AutoService( Processor.class )
@SupportedAnnotationTypes( { "react4j.annotations.*" } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public final class ReactProcessor
  extends AbstractProcessor
{
  /**
   * Cache of lifecycle names to methods as defined by Component.
   */
  private final HashMap<String, ExecutableElement> _componentLifecycleMethods = new HashMap<>();
  /**
   * Cache of render method method as defined by Component.
   */
  private ExecutableElement _componentRenderMethod;
  /**
   * Elements that were unresolved and have been deferred to a later compilation cycle.
   */
  @Nonnull
  private HashSet<TypeElement> _deferred = new HashSet<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    // Clear method caches to avoid potential inter-run problems
    _componentLifecycleMethods.clear();
    _componentRenderMethod = null;

    final TypeElement annotation =
      processingEnv.getElementUtils().getTypeElement( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME );
    final Set<? extends Element> elements = env.getElementsAnnotatedWith( annotation );
    final Collection<Element> elementsToProcess = getElementsToProcess( elements );
    processElements( elementsToProcess );
    if ( env.getRootElements().isEmpty() && !_deferred.isEmpty() )
    {
      _deferred.forEach( this::processingErrorMessage );
      _deferred.clear();
    }
    return true;
  }

  private void processingErrorMessage( @Nonnull final TypeElement target )
  {
    processingEnv
      .getMessager()
      .printMessage( ERROR,
                     "ReactProcessor unable to process " + target.getQualifiedName() +
                     " because not all of its dependencies could be resolved. Check for " +
                     "compilation errors or a circular dependency with generated code.",
                     target );
  }

  @Nonnull
  private Collection<Element> getElementsToProcess( @Nonnull final Set<? extends Element> elements )
  {
    final List<TypeElement> deferred = _deferred
      .stream()
      .map( e -> processingEnv.getElementUtils().getTypeElement( e.getQualifiedName() ) )
      .collect( Collectors.toList() );
    _deferred = new HashSet<>();

    final ArrayList<Element> elementsToProcess = new ArrayList<>();
    collectElementsToProcess( elements, elementsToProcess );
    collectElementsToProcess( deferred, elementsToProcess );
    return elementsToProcess;
  }

  private void collectElementsToProcess( @Nonnull final Collection<? extends Element> elements,
                                         @Nonnull final ArrayList<Element> elementsToProcess )
  {
    for ( final Element element : elements )
    {
      if ( SuperficialValidation.validateElement( element ) )
      {
        elementsToProcess.add( element );
      }
      else
      {
        _deferred.add( (TypeElement) element );
      }
    }
  }

  private void processElements( @Nonnull final Collection<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      try
      {
        process( (TypeElement) element );
      }
      catch ( final IOException ioe )
      {
        processingEnv.getMessager().printMessage( ERROR, ioe.getMessage(), element );
      }
      catch ( final ReactProcessorException e )
      {
        processingEnv.getMessager().printMessage( ERROR, e.getMessage(), e.getElement() );
      }
      catch ( final Throwable e )
      {
        final StringWriter sw = new StringWriter();
        e.printStackTrace( new PrintWriter( sw ) );
        sw.flush();

        final String message =
          "Unexpected error will running the " + getClass().getName() + " processor. This has " +
          "resulted in a failure to process the code and has left the compiler in an invalid " +
          "state. Please report the failure to the developers so that it can be fixed.\n" +
          " Report the error at: https://github.com/react4j/react4j/issues\n" +
          "\n\n" +
          sw.toString();
        processingEnv.getMessager().printMessage( ERROR, message, element );
      }
    }
  }

  private void process( @Nonnull final TypeElement element )
    throws IOException, ReactProcessorException
  {
    final ComponentDescriptor descriptor = parse( element );
    if ( descriptor.needsHelper() )
    {
      emitTypeSpec( descriptor.getPackageName(), Generator.buildComponentHelper( descriptor ) );
    }
    emitTypeSpec( descriptor.getPackageName(), Generator.buildEnhancedComponent( descriptor ) );
    emitTypeSpec( descriptor.getPackageName(), Generator.buildComponentBuilder( descriptor ) );
    if ( descriptor.needsDaggerIntegration() )
    {
      emitTypeSpec( descriptor.getPackageName(), Generator.buildDaggerFactory( descriptor ) );
    }
  }

  private void emitTypeSpec( @Nonnull final String packageName, @Nonnull final TypeSpec typeSpec )
    throws IOException
  {
    JavaFile.builder( packageName, typeSpec ).
      skipJavaLangImports( true ).
      build().
      writeTo( processingEnv.getFiler() );
  }

  @Nonnull
  private ComponentDescriptor parse( @Nonnull final TypeElement typeElement )
  {
    final String name = deriveComponentName( typeElement );
    final PackageElement packageElement = processingEnv.getElementUtils().getPackageOf( typeElement );
    final ComponentDescriptor descriptor =
      new ComponentDescriptor( processingEnv.getElementUtils(),
                               processingEnv.getSourceVersion(),
                               name,
                               packageElement,
                               typeElement );

    determineComponentType( descriptor, typeElement );
    determineRenderMethod( typeElement, descriptor );
    determineCallbacks( descriptor );
    determineProps( descriptor );
    determinePropValidatesMethods( descriptor );
    determineDefaultPropsMethods( descriptor );
    determineDefaultPropsFields( descriptor );
    determineLifecycleMethods( typeElement, descriptor );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      if ( !isPropRequired( prop ) )
      {
        prop.markAsOptional();
      }
    }

    /*
     * Sorting must occur after @PropDefault has been processed to ensure the sorting
     * correctly sorts optional props after required props.
     */
    descriptor.sortProps();

    verifyNoUnexpectedAbstractMethod( descriptor );
    verifyPropsNotAnnotatedWithArezAnnotations( descriptor );
    verifyPropsNotCollectionOfArezComponents( descriptor );

    return descriptor;
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
            throw new ReactProcessorException( "@Prop target is a collection that contains Arez components. " +
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
            throw new ReactProcessorException( "@Prop target is a collection that contains Arez components. " +
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
          throw new ReactProcessorException( "@Prop target is an array that contains Arez components. " +
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
        if ( isArezAnnotation( classname ) )
        {
          throw new ReactProcessorException( "@Prop target must not be annotated with any arez annotations but " +
                                             "is annotated by '" + classname + "'.", method );
        }
      }
    }
  }

  private void verifyNoUnexpectedAbstractMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    if ( !descriptor.isArezComponent() )
    {
      final ExecutableElement abstractMethod =
        getMethods( descriptor.getElement() )
          .stream()
          .filter( m -> m.getModifiers().contains( Modifier.ABSTRACT ) )
          // @Props methods are expected to be abstract
          .filter( m -> descriptor.getProps().stream().noneMatch( p -> p.getMethod() == m ) )
          .findAny()
          .orElse( null );
      if ( null != abstractMethod )
      {
        throw new ReactProcessorException( "@ReactComponent target has an unexpected abstract method",
                                           abstractMethod );
      }
    }
  }

  private void determinePropValidatesMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : methods )
    {
      final String name = derivePropValidateName( method );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ReactProcessorException( "@PropValidate target for prop named '" + name + "' has no corresponding " +
                                           "@Prop annotated method.", method );
      }
      if ( 1 != method.getParameters().size() )
      {
        throw new ReactProcessorException( "@PropValidate target must have exactly 1 parameter", method );
      }
      final ExecutableType methodType =
        (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getParameterTypes().get( 0 ),
                                                       prop.getMethodType().getReturnType() ) )
      {
        throw new ReactProcessorException( "@PropValidate target has a parameter type that is not assignable to the " +
                                           "return type of the associated @Prop annotated method.", method );
      }
      prop.setValidateMethod( method );
    }
  }

  @Nonnull
  private String derivePropValidateName( @Nonnull final Element element )
    throws ReactProcessorException
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 element,
                                                 Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      final String deriveName = ProcessorUtil.deriveName( element, ProcessorUtil.VALIDATE_PROP_PATTERN, name );
      if ( null == deriveName )
      {
        throw new ReactProcessorException( "@PropValidate target has not specified name nor is it named according " +
                                           "to the convention 'validate[Name]Prop'.", element );
      }
      return deriveName;
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@PropValidate target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@PropValidate target specified an invalid name '" + name + "'. The " +
                                           "name must not be a java keyword.", element );
      }
      return name;
    }
  }

  private void determineDefaultPropsMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> defaultPropsMethods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : defaultPropsMethods )
    {
      final String name = derivePropDefaultName( method );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ReactProcessorException( "@PropDefault target for prop named '" + name + "' has no corresponding " +
                                           "@Prop annotated method.", method );
      }
      final ExecutableType methodType = (ExecutableType) processingEnv.getTypeUtils()
        .asMemberOf( descriptor.getDeclaredType(), method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getReturnType(),
                                                       prop.getMethodType().getReturnType() ) )
      {
        throw new ReactProcessorException( "@PropDefault target has a return type that is not assignable to the " +
                                           "return type of the associated @Prop annotated method.", method );
      }
      MethodChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                     method );
      prop.setDefaultMethod( method );
    }
  }

  private void determineDefaultPropsFields( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<VariableElement> defaultPropsFields =
      ProcessorUtil.getFieldElements( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final VariableElement field : defaultPropsFields )
    {
      final String name = derivePropDefaultName( field );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ReactProcessorException( "@PropDefault target for prop named '" + name + "' has no corresponding " +
                                           "@Prop annotated method.", field );
      }
      if ( !processingEnv.getTypeUtils().isAssignable( field.asType(), prop.getMethodType().getReturnType() ) )
      {
        throw new ReactProcessorException( "@PropDefault target has a type that is not assignable to the " +
                                           "return type of the associated @Prop annotated method.", field );
      }
      MethodChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                     field );
      prop.setDefaultField( field );
    }
  }

  @Nonnull
  private String derivePropDefaultName( @Nonnull final Element element )
    throws ReactProcessorException
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 element,
                                                 Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      if ( element instanceof ExecutableElement )
      {
        final String deriveName = ProcessorUtil.deriveName( element, ProcessorUtil.DEFAULT_GETTER_PATTERN, name );
        if ( null == deriveName )
        {
          throw new ReactProcessorException( "@PropDefault target has not specified name nor is it named according " +
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
          throw new ReactProcessorException( "@PropDefault target has not specified name nor is it named according " +
                                             "to the convention 'DEFAULT_[NAME]'.", element );
        }
      }
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@PropDefault target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@PropDefault target specified an invalid name '" + name + "'. The " +
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

  private void determineCallbacks( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<CallbackDescriptor> callbacks =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.CALLBACK_ANNOTATION_CLASSNAME ) )
        .map( m -> createCallbackDescriptor( descriptor, m ) )
        .collect( Collectors.toList() );
    for ( final CallbackDescriptor callback : callbacks )
    {
      final ExecutableElement method = callback.getMethod();
      final TypeElement callbackType = getCallbackType( method );
      if ( ElementKind.INTERFACE != callbackType.getKind() )
      {
        throw new ReactProcessorException( "The @Callback specified an invalid type that is not an interface.",
                                           callback.getMethod() );
      }
      final CallbackDescriptor matched = callbacks.stream()
        .filter( h -> h != callback && h.getName().equals( callback.getName() ) )
        .findAny().orElse( null );
      if ( null != matched )
      {
        throw new ReactProcessorException( "The @Callback has the same name as the callback defined by " +
                                           matched.getMethod() + ".", callback.getMethod() );
      }
      final CallbackDescriptor matched2 = callbacks.stream()
        .filter( h -> h != callback &&
                      h.getMethod().getSimpleName().equals( callback.getMethod().getSimpleName() ) )
        .findAny().orElse( null );
      if ( null != matched2 )
      {
        throw new ReactProcessorException( "The @Callback has the same method name as the callback defined " +
                                           "by " + matched2.getMethod() + ".", callback.getMethod() );
      }
      final ExecutableType methodType = callback.getMethodType();
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getReturnType(),
                                                       callback.getCallbackMethod().getReturnType() ) )
      {
        throw new ReactProcessorException( "@Callback target has a return type that is not assignable to the return " +
                                           "type defined in the callback type " + callbackType.getQualifiedName() + ".",
                                           callback.getMethod() );
      }

      final List<? extends TypeMirror> parameters = methodType.getParameterTypes();
      if ( !parameters.isEmpty() )
      {
        // Our annotated callback method has parameters so they should exactly align
        // in count and type with the parameters in the callback method
        final ExecutableElement target = callback.getCallbackMethod();
        final List<? extends VariableElement> targetParameters = target.getParameters();
        if ( targetParameters.size() != parameters.size() )
        {
          throw new ReactProcessorException( "The @Callback target has " + parameters.size() + " parameters " +
                                             "but the type parameter specified a callback with method type " +
                                             callback.getCallbackType().getQualifiedName() + " that has a " +
                                             "callback method with " + targetParameters.size() + " parameters. The " +
                                             "@Callback target should have zero parameters or match the number " +
                                             "of parameter in the target method " + target.getSimpleName() + ".",
                                             callback.getMethod() );
        }
        for ( int i = 0; i < parameters.size(); i++ )
        {
          final TypeMirror parameterType = parameters.get( i );
          final VariableElement element = targetParameters.get( i );
          final TypeMirror targetParameterType = element.asType();
          final TypeMirror targetErased = processingEnv.getTypeUtils().erasure( targetParameterType );
          final TypeMirror parameterErased = processingEnv.getTypeUtils().erasure( parameterType );
          if ( !processingEnv.getTypeUtils().isAssignable( targetErased, parameterErased ) )
          {
            throw new ReactProcessorException( "The @Callback target parameter named " +
                                               callback.getMethod().getParameters().get( i ).getSimpleName() +
                                               " of type " + parameterType + " is not assignable from target type " +
                                               targetParameterType + " of parameter " + element.getSimpleName() +
                                               " in method " + callback.getCallbackType().getQualifiedName() +
                                               "." + target.getSimpleName() + ".",
                                               callback.getMethod() );
          }
        }
      }
    }

    descriptor.setCallbacks( callbacks );
  }

  @Nonnull
  private CallbackDescriptor createCallbackDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                                       @Nonnull final ExecutableElement method )
  {
    verifyNoDuplicateAnnotations( method );
    final String name = deriveCallbackName( method );
    final TypeElement callbackType = getCallbackType( method );
    final ExecutableType methodType =
      (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );
    final List<ExecutableElement> callbackMethods =
      getMethods( callbackType ).stream().
        filter( m11 -> m11.getModifiers().contains( Modifier.ABSTRACT ) ).
        collect( Collectors.toList() );
    if ( callbackMethods.isEmpty() )
    {
      throw new ReactProcessorException( "Method annotated with @Callback specified type " +
                                         callbackType.getQualifiedName() + " that has no abstract method and " +
                                         "thus is not a functional interface", method );
    }
    else if ( callbackMethods.size() > 1 )
    {
      throw new ReactProcessorException( "Method annotated with @Callback specified type " +
                                         callbackType.getQualifiedName() + " that has more than 1 abstract " +
                                         "method and thus is not a functional interface", method );
    }

    final boolean initCallbackContext = shouldInitCallbackContext( descriptor, method );

    if ( initCallbackContext &&
         descriptor.isArezComponent() &&
         null != ProcessorUtil.findAnnotationByType( method, Constants.ACTION_ANNOTATION_CLASSNAME ) )
    {
      final String message =
        "@Callback target is also annotated with @arez.annotations.Action but the @Callback parameter " +
        "'initCallbackContext' is not set to Feature.DISABLE which would stop react4j from also " +
        "annotating the method with @Action. Please remove @Action or change the 'initCallbackContext' to " +
        "Feature.DISABLE.";
      throw new ReactProcessorException( message, method );
    }
    MethodChecks.mustNotBeAbstract( Constants.CALLBACK_ANNOTATION_CLASSNAME, method );
    final TypeElement element = (TypeElement) method.getEnclosingElement();
    if ( ElementKind.CLASS == element.getKind() && method.getModifiers().contains( Modifier.PUBLIC ) )
    {
      throw new ReactProcessorException( "@Callback target must not be public unless it is a " +
                                         "default method on an interface", method );
    }
    MethodChecks.mustBeSubclassCallable( descriptor.getElement(), Constants.CALLBACK_ANNOTATION_CLASSNAME, method );
    return new CallbackDescriptor( name,
                                   method,
                                   methodType,
                                   callbackType,
                                   callbackMethods.get( 0 ),
                                   initCallbackContext );
  }

  private boolean shouldInitCallbackContext( @Nonnull final ComponentDescriptor descriptor,
                                             @Nonnull final ExecutableElement method )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.CALLBACK_ANNOTATION_CLASSNAME,
                                        "initCallbackContext" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return descriptor.isArezComponent();
    }
  }

  @Nonnull
  private TypeElement getCallbackType( @Nonnull final ExecutableElement method )
  {
    final DeclaredType typeMirror =
      ProcessorUtil.getTypeMirrorAnnotationParameter( processingEnv.getElementUtils(),
                                                      method,
                                                      Constants.CALLBACK_ANNOTATION_CLASSNAME,
                                                      "value" );
    assert null != typeMirror;
    return (TypeElement) processingEnv.getTypeUtils().asElement( typeMirror );
  }

  @Nonnull
  private String deriveCallbackName( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 method,
                                                 Constants.CALLBACK_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      return method.getSimpleName().toString();
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@Callback target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", method );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@Callback target specified an invalid name '" + name + "'. The " +
                                           "name must not be a java keyword.", method );
      }
      return name;
    }
  }

  private void determineProps( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> props =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_ANNOTATION_CLASSNAME ) )
        .map( m -> createPropDescriptor( descriptor, m ) )
        .collect( Collectors.toList() );

    final PropDescriptor childrenProp =
      props.stream().filter( p -> p.getName().equals( "children" ) ).findAny().orElse( null );
    final PropDescriptor childProp =
      props.stream().filter( p -> p.getName().equals( "child" ) ).findAny().orElse( null );
    if ( null != childrenProp && null != childProp )
    {
      throw new ReactProcessorException( "Multiple candidate children @Prop annotated methods: " +
                                         childrenProp.getMethod().getSimpleName() + " and " +
                                         childProp.getMethod().getSimpleName(),
                                         childrenProp.getMethod() );
    }

    descriptor.setProps( props );
  }

  private boolean isPropRequired( @Nonnull final PropDescriptor prop )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        prop.getMethod(),
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "require" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return !prop.hasDefaultMethod() &&
               !prop.hasDefaultField() &&
               null == ProcessorUtil.findAnnotationByType( prop.getMethod(), Constants.NULLABLE_ANNOTATION_CLASSNAME );
    }
  }

  @Nonnull
  private PropDescriptor createPropDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                               @Nonnull final ExecutableElement method )
  {
    final String name = derivePropName( method );
    final ExecutableType methodType =
      (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );

    verifyNoDuplicateAnnotations( method );
    MethodChecks.mustBeAbstract( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePublic( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotHaveAnyParameters( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustReturnAValue( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePackageAccessInDifferentPackage( descriptor.getElement(),
                                                           Constants.PROP_ANNOTATION_CLASSNAME,
                                                           method );
    final TypeMirror returnType = method.getReturnType();
    if ( "key".equals( name ) )
    {
      throw new ReactProcessorException( "@Prop named 'key' is invalid as the name references value used in the " +
                                         "reconciliation process. This value can be accessed via Component.getKey()",
                                         method );
    }
    else if ( "build".equals( name ) )
    {
      throw new ReactProcessorException( "@Prop named 'build' is invalid as it conflicts with the method named " +
                                         "build() that is used in the generated Builder classes",
                                         method );
    }
    else if ( "child".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode".equals( returnType.toString() ) ) )
    {
      throw new ReactProcessorException( "@Prop named 'child' should be of type react4j.ReactNode", method );
    }
    else if ( "children".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode[]".equals( returnType.toString() ) ) )
    {
      throw new ReactProcessorException( "@Prop named 'children' should be of type react4j.ReactNode[]", method );
    }

    if ( returnType instanceof TypeVariable )
    {
      final TypeVariable typeVariable = (TypeVariable) returnType;
      final String typeVariableName = typeVariable.asElement().getSimpleName().toString();
      List<? extends TypeParameterElement> typeParameters = method.getTypeParameters();
      if ( typeParameters.stream().anyMatch( p -> p.getSimpleName().toString().equals( typeVariableName ) ) )
      {
        throw new ReactProcessorException( "@Prop named '" + name + "' is has a type variable as a return type " +
                                           "that is declared on the method.", method );
      }
    }

    final Element propType = processingEnv.getTypeUtils().asElement( returnType );
    final boolean shouldUpdateOnChange = shouldUpdateOnChange( method );
    final boolean observable = isPropObservable( descriptor, method, shouldUpdateOnChange );
    final boolean disposable = null != propType && isPropDisposable( descriptor, method, propType );
    if ( observable && !descriptor.isArezComponent() )
    {
      throw new ReactProcessorException( "@Prop named '" + name + "' is marked as observable but the host component " +
                                         "is not a subclass of react4j.arez.ReactArezComponent", method );
    }
    if ( disposable && !descriptor.isArezComponent() )
    {
      throw new ReactProcessorException( "@Prop named '" + name + "' is marked as disposable but the host component " +
                                         "is not a subclass of react4j.arez.ReactArezComponent", method );
    }
    if ( TypeName.get( returnType ).isBoxedPrimitive() &&
         null != ProcessorUtil.findAnnotationByType( method, Constants.NONNULL_ANNOTATION_CLASSNAME ) )
    {
      throw new ReactProcessorException( "@Prop named '" + name + "' is a boxed primitive annotated with a " +
                                         "@Nonnull annotation. The return type should be the primitive type.",
                                         method );
    }
    return new PropDescriptor( descriptor, name, method, methodType, shouldUpdateOnChange, observable, disposable );
  }

  @Nonnull
  private String derivePropName( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String specifiedName =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 method,
                                                 Constants.PROP_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    final String name = ProcessorUtil.getPropertyAccessorName( method, specifiedName );
    if ( !SourceVersion.isIdentifier( name ) )
    {
      throw new ReactProcessorException( "@Prop target specified an invalid name '" + specifiedName + "'. The " +
                                         "name must be a valid java identifier.", method );
    }
    else if ( SourceVersion.isKeyword( name ) )
    {
      throw new ReactProcessorException( "@Prop target specified an invalid name '" + specifiedName + "'. The " +
                                         "name must not be a java keyword.", method );
    }
    else
    {
      return name;
    }
  }

  private void determineLifecycleMethods( @Nonnull final TypeElement typeElement,
                                          @Nonnull final ComponentDescriptor descriptor )
  {
    final boolean arezComponent = descriptor.isArezComponent();
    final boolean forceShouldComponentUpdate =
      !arezComponent && descriptor.shouldGeneratePropValidator();
    /*
     * Get the list of lifecycle methods that have been overridden by typeElement
     * a parent class, or by a default method method implemented by typeElement or
     * a parent class. Also include Component.shouldComponentUpdate() if forceShouldComponentUpdate is true.
     */
    final Collection<ExecutableElement> componentLifecycleMethods = getComponentLifecycleMethods().values();
    final Elements elementUtils = processingEnv.getElementUtils();
    final Types typeUtils = processingEnv.getTypeUtils();
    final List<MethodDescriptor> lifecycleMethods =
      // Get all methods on type parent classes, and default methods from interfaces
      getMethods( typeElement ).stream()
        // Only keep methods that override the lifecycle methods
        .filter( m -> (
                        forceShouldComponentUpdate &&
                        Constants.SHOULD_COMPONENT_UPDATE.equals( m.getSimpleName().toString() )
                      ) ||
                      (
                        Constants.COMPONENT_DID_MOUNT.equals( m.getSimpleName().toString() ) ||
                        Constants.COMPONENT_DID_UPDATE.equals( m.getSimpleName().toString() )
                      ) ||
                      componentLifecycleMethods.stream().anyMatch( l -> elementUtils.overrides( m, l, typeElement ) ) )
        .map( m -> new MethodDescriptor( m, (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), m ) ) )
        .collect( Collectors.toList() );

    for ( final MethodDescriptor method : lifecycleMethods )
    {
      for ( final AnnotationMirror mirror : method.getMethod().getAnnotationMirrors() )
      {
        final String classname = mirror.getAnnotationType().toString();
        if ( isArezAnnotation( classname ) && !classname.equals( Constants.ACTION_ANNOTATION_CLASSNAME ) )
        {
          throw new ReactProcessorException( "@ReactComponent target has a lifecycle method '" +
                                             method.getMethod().getSimpleName() + "' with an invalid arez " +
                                             "annotation '" + classname + "'. It is invalid for any arez annotation " +
                                             "other than " + Constants.ACTION_ANNOTATION_CLASSNAME + " to be on a " +
                                             "lifecycle method.", method.getMethod() );
        }
      }
    }

    descriptor.setLifecycleMethods( lifecycleMethods );
  }

  private void determineRenderMethod( @Nonnull final TypeElement typeElement,
                                      @Nonnull final ComponentDescriptor descriptor )
  {
    /*
     * Get the render method that has been overridden by the typeElement, a parent class, or by a default
     * method method implemented by the typeElement or a parent class.
     */
    final ExecutableElement renderMethod = getComponentRenderMethod();
    final Elements elementUtils = processingEnv.getElementUtils();
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeElement componentType = elementUtils.getTypeElement( Constants.COMPONENT_CLASSNAME );
    final MethodDescriptor overriddenRenderMethod =
      // Get all methods on type parent classes, and default methods from interfaces
      getMethods( typeElement ).stream()
        // Only keep method if they override the render method
        .filter( m -> elementUtils.overrides( m, renderMethod, typeElement ) )
        //Remove those that come from the base classes
        .filter( m -> m.getEnclosingElement() != componentType )
        .map( m -> new MethodDescriptor( m, (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), m ) ) )
        .findAny().
        orElse( null );

    if ( null == overriddenRenderMethod )
    {
      throw new ReactProcessorException( "The react component does not override the render method.", typeElement );
    }
  }

  @Nonnull
  private String deriveComponentName( @Nonnull final TypeElement typeElement )
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 typeElement,
                                                 Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      return typeElement.getSimpleName().toString();
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@ReactComponent target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", typeElement );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@ReactComponent target specified an invalid name '" + name + "'. The " +
                                           "name must not be a java keyword.", typeElement );
      }
      return name;
    }
  }

  /**
   * Return the set of lifecycle methods as defined on the base component class.
   * Used to compare against those on type class to see if they override.
   */
  @Nonnull
  private HashMap<String, ExecutableElement> getComponentLifecycleMethods()
  {
    if ( _componentLifecycleMethods.isEmpty() )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
      for ( final ExecutableElement method : getMethods( componentType ) )
      {
        final String methodName = method.getSimpleName().toString();
        if ( Constants.LIFECYCLE_METHODS.contains( methodName ) )
        {
          _componentLifecycleMethods.put( methodName, method );
        }
      }

    }
    return _componentLifecycleMethods;
  }

  /**
   * Return the set of render methods as defined on the base component class.
   * Used to compare against those on type class to see if they override.
   */
  @Nonnull
  private ExecutableElement getComponentRenderMethod()
  {
    if ( null == _componentRenderMethod )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
      for ( final ExecutableElement method : getMethods( componentType ) )
      {
        final String methodName = method.getSimpleName().toString();
        if ( "render".equals( methodName ) )
        {
          _componentRenderMethod = method;
          break;
        }
      }
    }
    return _componentRenderMethod;
  }

  private void determineComponentType( @Nonnull final ComponentDescriptor descriptor,
                                       @Nonnull final TypeElement typeElement )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
    final TypeMirror rawComponentType = processingEnv.getTypeUtils().erasure( componentType.asType() );

    /*
     * Arez need not be on the classpath in which case this will return a null value to arezComponentType.
     * Our code should just gracefully handle this and perform none of the arez specific checks or generation.
     */
    @Nullable
    final TypeElement arezComponentType =
      processingEnv.getElementUtils().getTypeElement( "react4j.arez.ReactArezComponent" );
    @Nullable
    final TypeMirror rawArezComponentType =
      null == arezComponentType ? null : processingEnv.getTypeUtils().erasure( arezComponentType.asType() );

    final TypeMirror type = descriptor.getDeclaredType();

    final boolean isComponent = processingEnv.getTypeUtils().isSubtype( type, rawComponentType );
    final boolean isArezComponent =
      null != rawArezComponentType && processingEnv.getTypeUtils().isSubtype( type, rawArezComponentType );

    if ( !isComponent )
    {
      throw new ReactProcessorException( "@ReactComponent target must be a subclass of react4j.Component",
                                         typeElement );
    }
    else if ( isArezComponent )
    {
      final AnnotationMirror arezAnnotation = typeElement.getAnnotationMirrors().stream().
        filter( m -> m.getAnnotationType().toString().equals( "arez.annotations.ArezComponent" ) ).
        findAny().orElse( null );
      if ( null != arezAnnotation )
      {
        throw new ReactProcessorException( "@ReactComponent target extends react4j.arez.ReactArezComponent and should " +
                                           "not be annotated with arez.annotations.ArezComponent as " +
                                           "React4j will add annotation", typeElement );
      }
    }

    final boolean needsInjection = isInjectionRequired( typeElement );
    final boolean isDaggerPresent = needsInjection && isDaggerRequired( typeElement );

    if ( isDaggerPresent && !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      throw new ReactProcessorException( "@ReactComponent target has enabled dagger injection and the class " +
                                         "has type argument but type arguments on a component are not compatible " +
                                         "with dagger injected components", typeElement );
    }

    descriptor.setNeedsInjection( needsInjection );
    descriptor.setNeedsDaggerIntegration( isDaggerPresent );
    descriptor.setArezComponent( isArezComponent );

    if ( isArezComponent )
    {
      ensureComputedMatchesExpectations( typeElement );
      ensureMemoizeMatchesExpectations( typeElement );
      final boolean runArezScheduler =
        hasAnyArezScheduledObserverMethods( typeElement ) ||
        hasAnyKeepAliveComputedMethods( typeElement ) ||
        hasAnyDependencyMethods( typeElement );
      descriptor.setRunArezScheduler( runArezScheduler );

      descriptor.setComputedMethods( getComputedMethods( typeElement ) );
      descriptor.setMemoizeMethods( getMemoizeMethods( typeElement ) );
    }
    else
    {
      for ( final ExecutableElement method : ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ) )
      {
        for ( final AnnotationMirror mirror : method.getAnnotationMirrors() )
        {
          final String classname = mirror.getAnnotationType().toString();
          if ( isArezAnnotation( classname ) )
          {
            throw new ReactProcessorException( "@ReactComponent target has a method '" + method.getSimpleName() +
                                               "' with an arez annotation '" + classname + "' but is not an " +
                                               "arez component.", method );
          }
        }
      }
      for ( final VariableElement element : ProcessorUtil.getFieldElements( typeElement ) )
      {
        for ( final AnnotationMirror mirror : element.getAnnotationMirrors() )
        {
          final String classname = mirror.getAnnotationType().toString();
          if ( isArezAnnotation( classname ) )
          {
            throw new ReactProcessorException( "@ReactComponent target has a field '" + element.getSimpleName() +
                                               "' with an arez annotation '" + classname + "' but is not an " +
                                               "arez component.", element );
          }
        }
      }
    }
  }

  private void ensureComputedMatchesExpectations( @Nonnull final TypeElement typeElement )
  {
    final TypeElement computedElement =
      processingEnv.getElementUtils().getTypeElement( Constants.COMPUTED_ANNOTATION_CLASSNAME );
    final Set<String> parameters = computedElement.getEnclosedElements()
      .stream()
      .map( e -> e.getSimpleName().toString() )
      .collect( Collectors.toSet() );
    if ( !( parameters.contains( "name" ) &&
            parameters.contains( "priority" ) &&
            parameters.contains( "keepAlive" ) &&
            parameters.contains( "observeLowerPriorityDependencies" ) &&
            parameters.contains( "depType" ) &&
            parameters.contains( "requireEnvironment" ) &&
            6 == parameters.size() ) )
    {
      throw new ReactProcessorException( "The @" + Constants.COMPUTED_ANNOTATION_CLASSNAME + " annotation was " +
                                         "expected to have the parameters name, priority, keepAlive, " +
                                         "depType, requireEnvironment and observeLowerPriorityDependencies but has " +
                                         parameters + ". The react4j annotation processor needs to be updated to " +
                                         "handle the change in parameters.", typeElement );

    }
  }

  private void ensureMemoizeMatchesExpectations( @Nonnull final TypeElement typeElement )
  {
    final TypeElement computedElement =
      processingEnv.getElementUtils().getTypeElement( Constants.MEMOIZE_ANNOTATION_CLASSNAME );
    final Set<String> parameters = computedElement.getEnclosedElements()
      .stream()
      .map( e -> e.getSimpleName().toString() )
      .collect( Collectors.toSet() );
    if ( !( parameters.contains( "name" ) &&
            parameters.contains( "priority" ) &&
            parameters.contains( "observeLowerPriorityDependencies" ) &&
            parameters.contains( "requireEnvironment" ) &&
            4 == parameters.size() ) )
    {
      throw new ReactProcessorException( "The @" + Constants.MEMOIZE_ANNOTATION_CLASSNAME + " annotation was " +
                                         "expected to have the parameters name, priority, requireEnvironment and " +
                                         "observeLowerPriorityDependencies but has " + parameters + ". The " +
                                         "react4j annotation processor needs to be updated to handle " +
                                         "the change in parameters.", typeElement );

    }
  }

  private boolean shouldUpdateOnChange( @Nonnull final ExecutableElement method )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "shouldUpdateOnChange" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return true;
    }
  }

  private boolean isPropObservable( @Nonnull final ComponentDescriptor descriptor,
                                    @Nonnull final ExecutableElement method,
                                    final boolean shouldUpdateOnChange )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "observable" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return descriptor.isArezComponent() &&
               shouldUpdateOnChange &&
               hasAnyArezObserverMethods( descriptor.getElement() );
    }
  }

  private boolean hasAnyArezObserverMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.COMPUTED_ANNOTATION_CLASSNAME ) ||
                      null != ProcessorUtil.findAnnotationByType( m, Constants.MEMOIZE_ANNOTATION_CLASSNAME ) ||
                      ( null != ProcessorUtil.findAnnotationByType( m, Constants.OBSERVE_ANNOTATION_CLASSNAME ) &&
                        ( !m.getParameters().isEmpty() || !m.getSimpleName().toString().equals( "trackRender" ) ) ) );
  }

  private boolean isPropDisposable( @Nonnull final ComponentDescriptor descriptor,
                                    @Nonnull final ExecutableElement method,
                                    @Nonnull final Element propType )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "disposable" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return descriptor.isArezComponent() &&
               ElementKind.CLASS == propType.getKind() &&
               null != ProcessorUtil.findAnnotationByType( propType, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME );
    }
  }

  private boolean isInjectionRequired( @Nonnull final TypeElement typeElement )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        typeElement,
                                        Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                        "inject" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return ProcessorUtil.getFieldElements( typeElement ).stream().anyMatch( this::hasInjectAnnotation ) ||
               getMethods( typeElement ).
                 stream().anyMatch( this::hasInjectAnnotation );
    }
  }

  private boolean isDaggerRequired( @Nonnull final TypeElement typeElement )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        typeElement,
                                        Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                        "dagger" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return null != processingEnv.getElementUtils().getTypeElement( Constants.DAGGER_MODULE_CLASSNAME );
    }
  }

  private boolean hasAnyArezScheduledObserverMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> {
        final AnnotationValue annotationValue =
          ProcessorUtil.findAnnotationValue( processingEnv.getElementUtils(),
                                             m,
                                             Constants.OBSERVE_ANNOTATION_CLASSNAME,
                                             "executor" );
        return null != annotationValue &&
               ( (VariableElement) annotationValue.getValue() ).getSimpleName().toString().equals( "AREZ" );
      } );
  }

  private boolean hasAnyDependencyMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.DEPENDENCY_ANNOTATION_CLASSNAME ) );
  }

  private boolean hasAnyKeepAliveComputedMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> {
        final AnnotationValue annotationValue =
          ProcessorUtil.findAnnotationValue( processingEnv.getElementUtils(),
                                             m,
                                             Constants.COMPUTED_ANNOTATION_CLASSNAME,
                                             "keepAlive" );
        return null != annotationValue && (boolean) annotationValue.getValue();
      } );
  }

  private boolean hasInjectAnnotation( final Element method )
  {
    return null != ProcessorUtil.findAnnotationByType( method, Constants.INJECT_ANNOTATION_CLASSNAME );
  }

  /**
   * Return computed that have not had the priority parameter explicitly set.
   */
  @Nonnull
  private List<MethodDescriptor> getComputedMethods( @Nonnull final TypeElement typeElement )
  {
    final DeclaredType type = (DeclaredType) typeElement.asType();
    return getMethods( typeElement )
      .stream()
      .filter( method -> !method.getModifiers().contains( Modifier.PRIVATE ) )
      .filter( method -> {
        final AnnotationMirror mirror =
          ProcessorUtil.findAnnotationByType( method, Constants.COMPUTED_ANNOTATION_CLASSNAME );
        return null != mirror &&
               mirror.getElementValues().keySet().stream()
                 .noneMatch( v -> "priority".equals( v.getSimpleName().toString() ) );
      } )
      .map( m -> new MethodDescriptor( m, (ExecutableType) processingEnv.getTypeUtils().asMemberOf( type, m ) ) )
      .collect( Collectors.toList() );
  }

  /**
   * Return @Memoize that have not had the priority parameter explicitly set.
   */
  @Nonnull
  private List<MethodDescriptor> getMemoizeMethods( @Nonnull final TypeElement typeElement )
  {
    final DeclaredType type = (DeclaredType) typeElement.asType();
    return getMethods( typeElement )
      .stream()
      .filter( method -> !method.getModifiers().contains( Modifier.PRIVATE ) )
      .filter( method -> {
        final AnnotationMirror mirror =
          ProcessorUtil.findAnnotationByType( method, Constants.MEMOIZE_ANNOTATION_CLASSNAME );
        return null != mirror &&
               mirror.getElementValues().keySet().stream()
                 .noneMatch( v -> "priority".equals( v.getSimpleName().toString() ) );
      } )
      .map( m -> new MethodDescriptor( m, (ExecutableType) processingEnv.getTypeUtils().asMemberOf( type, m ) ) )
      .collect( Collectors.toList() );
  }

  private void verifyNoDuplicateAnnotations( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String[] annotationTypes =
      new String[]{ Constants.CALLBACK_ANNOTATION_CLASSNAME,
                    Constants.PROP_ANNOTATION_CLASSNAME };
    for ( int i = 0; i < annotationTypes.length; i++ )
    {
      final String type1 = annotationTypes[ i ];
      final Object annotation1 = ProcessorUtil.findAnnotationByType( method, type1 );
      if ( null != annotation1 )
      {
        for ( int j = i + 1; j < annotationTypes.length; j++ )
        {
          final String type2 = annotationTypes[ j ];
          final Object annotation2 = ProcessorUtil.findAnnotationByType( method, type2 );
          if ( null != annotation2 )
          {
            final String message =
              "Method can not be annotated with both @" + ProcessorUtil.toSimpleName( type1 ) +
              " and @" + ProcessorUtil.toSimpleName( type2 );
            throw new ReactProcessorException( message, method );
          }
        }
      }
    }
  }

  @Nonnull
  private List<ExecutableElement> getMethods( @Nonnull final TypeElement typeElement )
  {
    return ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() );
  }

  private boolean isArezAnnotation( @Nonnull final String classname )
  {
    return classname.startsWith( "arez.annotations." );
  }
}
