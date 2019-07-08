package react4j.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

final class ProcessorUtil
{
  private static final Pattern GETTER_PATTERN = Pattern.compile( "^get([A-Z].*)$" );
  static final Pattern DEFAULT_GETTER_PATTERN = Pattern.compile( "^get([A-Z].*)Default$" );
  static final Pattern VALIDATE_PROP_PATTERN = Pattern.compile( "^validate([A-Z].*)$" );
  static final Pattern LAST_PROP_PATTERN = Pattern.compile( "^last([A-Z].*)$" );
  static final Pattern PREV_PROP_PATTERN = Pattern.compile( "^prev([A-Z].*)$" );
  static final Pattern PROP_PATTERN = Pattern.compile( "^([a-z].*)$" );
  static final Pattern PRIORITY_OVERRIDE_PATTERN = Pattern.compile( "^(.*)Priority$" );
  private static final Pattern ISSER_PATTERN = Pattern.compile( "^is([A-Z].*)$" );
  private static final String SENTINEL_NAME = "<default>";

  private ProcessorUtil()
  {
  }

  @Nonnull
  static List<TypeElement> getSuperTypes( @Nonnull final TypeElement element )
  {
    final List<TypeElement> superTypes = new ArrayList<>();
    enumerateSuperTypes( element, superTypes );
    return superTypes;
  }

  private static void enumerateSuperTypes( @Nonnull final TypeElement element,
                                           @Nonnull final List<TypeElement> superTypes )
  {
    final TypeMirror superclass = element.getSuperclass();
    if ( TypeKind.NONE != superclass.getKind() )
    {
      final TypeElement superclassElement = (TypeElement) ( (DeclaredType) superclass ).asElement();
      superTypes.add( superclassElement );
      enumerateSuperTypes( superclassElement, superTypes );
    }
    for ( final TypeMirror interfaceType : element.getInterfaces() )
    {
      final TypeElement interfaceElement = (TypeElement) ( (DeclaredType) interfaceType ).asElement();
      enumerateSuperTypes( interfaceElement, superTypes );
    }
  }

  @Nonnull
  static List<TypeVariableName> getTypeArgumentsAsNames( @Nonnull final DeclaredType declaredType )
  {
    return declaredType.getTypeArguments()
      .stream()
      .map( argument -> TypeVariableName.get( (TypeVariable) argument ) )
      .collect( Collectors.toList() );
  }

  @Nonnull
  static List<ExecutableElement> getMethods( @Nonnull final TypeElement element,
                                             @Nonnull final Types typeUtils )
  {
    final Map<String, ExecutableElement> methodMap = new LinkedHashMap<>();
    enumerateMethods( element, typeUtils, element, methodMap );
    return new ArrayList<>( methodMap.values() );
  }

  private static void enumerateMethods( @Nonnull final TypeElement scope,
                                        @Nonnull final Types typeUtils,
                                        @Nonnull final TypeElement element,
                                        @Nonnull final Map<String, ExecutableElement> methods )
  {
    final TypeMirror superclass = element.getSuperclass();
    if ( TypeKind.NONE != superclass.getKind() )
    {
      enumerateMethods( scope, typeUtils, (TypeElement) ( (DeclaredType) superclass ).asElement(), methods );
    }
    for ( final TypeMirror interfaceType : element.getInterfaces() )
    {
      final TypeElement interfaceElement = (TypeElement) ( (DeclaredType) interfaceType ).asElement();
      enumerateMethods( scope, typeUtils, interfaceElement, methods );
    }
    for ( final Element member : element.getEnclosedElements() )
    {
      if ( member.getKind() == ElementKind.METHOD )
      {
        final ExecutableType methodType =
          (ExecutableType) typeUtils.asMemberOf( (DeclaredType) scope.asType(), member );
        methods.put( member.getSimpleName() + methodType.toString(), (ExecutableElement) member );
      }
    }
  }

  @Nonnull
  static List<VariableElement> getFieldElements( @Nonnull final TypeElement element )
  {
    final Map<String, VariableElement> methodMap = new LinkedHashMap<>();
    enumerateFieldElements( element, methodMap );
    return new ArrayList<>( methodMap.values() );
  }

  private static void enumerateFieldElements( @Nonnull final TypeElement element,
                                              @Nonnull final Map<String, VariableElement> fields )
  {
    final TypeMirror superclass = element.getSuperclass();
    if ( TypeKind.NONE != superclass.getKind() )
    {
      enumerateFieldElements( (TypeElement) ( (DeclaredType) superclass ).asElement(), fields );
    }
    for ( final Element member : element.getEnclosedElements() )
    {
      if ( member.getKind() == ElementKind.FIELD )
      {
        fields.put( member.getSimpleName().toString(), (VariableElement) member );
      }
    }
  }

  static void copyAccessModifiers( @Nonnull final TypeElement element, @Nonnull final TypeSpec.Builder builder )
  {
    if ( element.getModifiers().contains( Modifier.PUBLIC ) )
    {
      builder.addModifiers( Modifier.PUBLIC );
    }
  }

  static void copyAccessModifiers( @Nonnull final ExecutableElement element, @Nonnull final MethodSpec.Builder builder )
  {
    if ( element.getModifiers().contains( Modifier.PUBLIC ) )
    {
      builder.addModifiers( Modifier.PUBLIC );
    }
    else if ( element.getModifiers().contains( Modifier.PROTECTED ) )
    {
      builder.addModifiers( Modifier.PROTECTED );
    }
  }

  static void copyWhitelistedAnnotations( @Nonnull final AnnotatedConstruct element,
                                          @Nonnull final MethodSpec.Builder builder )
  {
    for ( final AnnotationMirror annotation : element.getAnnotationMirrors() )
    {
      if ( shouldCopyAnnotation( annotation.getAnnotationType().toString() ) )
      {
        builder.addAnnotation( AnnotationSpec.get( annotation ) );
      }
    }
  }

  static void copyWhitelistedAnnotations( @Nonnull final AnnotatedConstruct element,
                                          @Nonnull final ParameterSpec.Builder builder )
  {
    for ( final AnnotationMirror annotation : element.getAnnotationMirrors() )
    {
      if ( shouldCopyAnnotation( annotation.getAnnotationType().toString() ) )
      {
        builder.addAnnotation( AnnotationSpec.get( annotation ) );
      }
    }
  }

  private static boolean shouldCopyAnnotation( @Nonnull final String classname )
  {
    return Constants.NONNULL_ANNOTATION_CLASSNAME.equals( classname ) ||
           Constants.NULLABLE_ANNOTATION_CLASSNAME.equals( classname ) ||
           Constants.DEPRECATED_ANNOTATION_CLASSNAME.equals( classname );
  }

  static void copyTypeParameters( @Nonnull final ExecutableType action, @Nonnull final MethodSpec.Builder builder )
  {
    for ( final TypeVariable typeParameter : action.getTypeVariables() )
    {
      builder.addTypeVariable( TypeVariableName.get( typeParameter ) );
    }
  }

  static void copyTypeParameters( @Nonnull final TypeElement element, @Nonnull final MethodSpec.Builder builder )
  {
    for ( final TypeParameterElement typeParameter : element.getTypeParameters() )
    {
      builder.addTypeVariable( TypeVariableName.get( typeParameter ) );
    }
  }

  static void copyTypeParameters( @Nonnull final TypeElement element, @Nonnull final TypeSpec.Builder builder )
  {
    for ( final TypeParameterElement typeParameter : element.getTypeParameters() )
    {
      builder.addTypeVariable( TypeVariableName.get( typeParameter ) );
    }
  }

  @Nonnull
  static String getPropertyAccessorName( @Nonnull final ExecutableElement method, @Nonnull final String specifiedName )
    throws ReactProcessorException
  {
    String name = ProcessorUtil.deriveName( method, GETTER_PATTERN, specifiedName );
    if ( null != name )
    {
      return name;
    }
    else if ( method.getReturnType().getKind() == TypeKind.BOOLEAN )
    {
      name = ProcessorUtil.deriveName( method, ISSER_PATTERN, specifiedName );
      if ( null != name )
      {
        return name;
      }
    }
    return method.getSimpleName().toString();
  }

  @Nullable
  static String deriveName( @Nonnull final Element method,
                            @Nonnull final Pattern pattern,
                            @Nonnull final String name )
    throws ReactProcessorException
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

  static boolean isSentinelName( @Nonnull final String name )
  {
    return SENTINEL_NAME.equals( name );
  }

  @SuppressWarnings( "SameParameterValue" )
  @Nonnull
  static AnnotationValue getAnnotationValue( @Nonnull final Elements elements,
                                             @Nonnull final Element typeElement,
                                             @Nonnull final String annotationClassName,
                                             @Nonnull final String parameterName )
  {
    final AnnotationValue value = findAnnotationValue( elements, typeElement, annotationClassName, parameterName );
    assert null != value;
    return value;
  }

  @Nullable
  static AnnotationValue findAnnotationValue( @Nonnull final Elements elements,
                                              @Nonnull final Element typeElement,
                                              @Nonnull final String annotationClassName,
                                              @Nonnull final String parameterName )
  {
    final AnnotationMirror mirror = findAnnotationByType( typeElement, annotationClassName );
    if ( null == mirror )
    {
      return null;
    }
    else
    {
      final Map<? extends ExecutableElement, ? extends AnnotationValue> values =
        elements.getElementValuesWithDefaults( mirror );
      final ExecutableElement annotationKey = values.keySet().stream().
        filter( k -> parameterName.equals( k.getSimpleName().toString() ) ).findFirst().orElse( null );
      return values.get( annotationKey );
    }
  }

  @Nullable
  static AnnotationMirror findAnnotationByType( @Nonnull final Element typeElement,
                                                @Nonnull final String annotationClassName )
  {
    return typeElement.getAnnotationMirrors().stream().
      filter( a -> a.getAnnotationType().toString().equals( annotationClassName ) ).findFirst().orElse( null );
  }

  static boolean hasAnnotationOfType( @Nonnull final Element typeElement, @Nonnull final String annotationClassName )
  {
    return null != findAnnotationByType( typeElement, annotationClassName );
  }

  @Nonnull
  static String toSimpleName( @Nonnull final String annotationName )
  {
    return annotationName.replaceAll( ".*\\.", "" );
  }

  static boolean isNonnull( @Nonnull final ExecutableElement method )
  {
    return hasAnnotationOfType( method, Constants.NONNULL_ANNOTATION_CLASSNAME );
  }
}
