package react4j.processor;

import com.google.auto.common.AnnotationMirrors;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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

  static boolean isWarningSuppressed( @Nonnull final Element element, @Nonnull final String warning )
  {
    return isWarningSuppressed( element, warning, Constants.SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME );
  }

  @SuppressWarnings( "unchecked" )
  static boolean isWarningSuppressed( @Nonnull final Element element,
                                      @Nonnull final String warning,
                                      @Nullable final String alternativeSuppressWarnings )
  {
    if ( null != alternativeSuppressWarnings )
    {
      final AnnotationMirror suppress = findAnnotationByType( element, alternativeSuppressWarnings );
      if ( null != suppress )
      {
        final AnnotationValue value = findAnnotationValueNoDefaults( suppress, "value" );
        if ( null != value )
        {
          final List<AnnotationValue> warnings = (List<AnnotationValue>) value.getValue();
          for ( final AnnotationValue suppression : warnings )
          {
            if ( warning.equals( suppression.getValue() ) )
            {
              return true;
            }
          }
        }
      }
    }

    final SuppressWarnings annotation = element.getAnnotation( SuppressWarnings.class );
    if ( null != annotation )
    {
      for ( final String suppression : annotation.value() )
      {
        if ( warning.equals( suppression ) )
        {
          return true;
        }
      }
    }
    final Element enclosingElement = element.getEnclosingElement();
    return null != enclosingElement && isWarningSuppressed( enclosingElement, warning, alternativeSuppressWarnings );
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

  @Nonnull
  static String getPropertyAccessorName( @Nonnull final ExecutableElement method, @Nonnull final String specifiedName )
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
  static String deriveName( @Nonnull final Element method,
                            @Nonnull final Pattern pattern,
                            @Nonnull final String name )
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

  static boolean isSentinelName( @Nonnull final String name )
  {
    return SENTINEL_NAME.equals( name );
  }

  @SuppressWarnings( "SameParameterValue" )
  @Nonnull
  static AnnotationValue getAnnotationValue( @Nonnull final Element typeElement,
                                             @Nonnull final String annotationClassName,
                                             @Nonnull final String parameterName )
  {
    final AnnotationValue value = findAnnotationValue( typeElement, annotationClassName, parameterName );
    assert null != value;
    return value;
  }

  @Nullable
  static AnnotationValue findAnnotationValue( @Nonnull final Element typeElement,
                                              @Nonnull final String annotationClassName,
                                              @Nonnull final String parameterName )
  {
    final AnnotationMirror annotation = findAnnotationByType( typeElement, annotationClassName );
    if ( null == annotation )
    {
      return null;
    }
    else
    {
      final ImmutableMap<ExecutableElement, AnnotationValue> values =
        AnnotationMirrors.getAnnotationValuesWithDefaults( annotation );
      final ExecutableElement annotationKey = values.keySet().stream().
        filter( k -> parameterName.equals( k.getSimpleName().toString() ) ).findFirst().orElse( null );
      return values.get( annotationKey );
    }
  }

  @SuppressWarnings( "SameParameterValue" )
  @Nullable
  private static AnnotationValue findAnnotationValueNoDefaults( @Nonnull final AnnotationMirror annotation,
                                                                @Nonnull final String parameterName )
  {
    final Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
    final ExecutableElement annotationKey = values.keySet().stream().
      filter( k -> parameterName.equals( k.getSimpleName().toString() ) ).findFirst().orElse( null );
    return values.get( annotationKey );
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

  static boolean isNonnull( @Nonnull final ExecutableElement method )
  {
    return hasAnnotationOfType( method, Constants.NONNULL_ANNOTATION_CLASSNAME );
  }
}
