package react4j.processor;

import com.google.auto.common.AnnotationMirrors;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@SuppressWarnings( { "SameParameterValue", "WeakerAccess", "unused", "RedundantSuppression" } )
final class AnnotationsUtil
{
  private AnnotationsUtil()
  {
  }

  @SuppressWarnings( "unchecked" )
  @Nonnull
  static List<TypeMirror> getTypeMirrorsAnnotationParameter( @Nonnull final AnnotatedConstruct annotated,
                                                             @Nonnull final String annotationClassName,
                                                             @Nonnull final String parameterName )
  {
    final AnnotationValue annotationValue =
      getAnnotationValue( annotated, annotationClassName, parameterName );
    return ( (List<AnnotationValue>) annotationValue.getValue() )
      .stream()
      .map( v -> (TypeMirror) v.getValue() ).collect( Collectors.toList() );
  }

  @Nonnull
  static List<TypeElement> getTypeElementsAnnotationParameter( @Nonnull final ProcessingEnvironment processingEnv,
                                                               @Nonnull final AnnotatedConstruct annotated,
                                                               @Nonnull final String annotationClassName,
                                                               @Nonnull final String parameterName )
  {
    return AnnotationsUtil.getTypeMirrorsAnnotationParameter( annotated,
                                                              annotationClassName,
                                                              parameterName ).stream().
      map( typeMirror -> (TypeElement) processingEnv.getTypeUtils().asElement( typeMirror ) ).
      collect( Collectors.toList() );
  }

  @Nonnull
  static String getEnumAnnotationParameter( @Nonnull final AnnotatedConstruct annotated,
                                            @Nonnull final String annotationClassname,
                                            @Nonnull final String parameterName )
  {
    final VariableElement parameter = (VariableElement)
      getAnnotationValue( annotated, annotationClassname, parameterName ).getValue();
    return parameter.getSimpleName().toString();
  }

  @Nonnull
  static AnnotationValue getAnnotationValue( @Nonnull final AnnotatedConstruct annotated,
                                             @Nonnull final String annotationClassName,
                                             @Nonnull final String parameterName )
  {
    final AnnotationValue value = findAnnotationValue( annotated, annotationClassName, parameterName );
    assert null != value;
    return value;
  }

  @Nullable
  static AnnotationValue findAnnotationValue( @Nonnull final AnnotatedConstruct annotated,
                                              @Nonnull final String annotationClassName,
                                              @Nonnull final String parameterName )
  {
    final AnnotationMirror mirror = findAnnotationByType( annotated, annotationClassName );
    return null == mirror ? null : findAnnotationValue( mirror, parameterName );
  }

  @Nullable
  private static AnnotationValue findAnnotationValue( @Nonnull final AnnotationMirror annotation,
                                                      @Nonnull final String parameterName )
  {
    final ImmutableMap<ExecutableElement, AnnotationValue> values =
      AnnotationMirrors.getAnnotationValuesWithDefaults( annotation );
    final ExecutableElement annotationKey = values.keySet().stream().
      filter( k -> parameterName.equals( k.getSimpleName().toString() ) ).findFirst().orElse( null );
    return values.get( annotationKey );
  }

  @Nullable
  static AnnotationValue findAnnotationValueNoDefaults( @Nonnull final AnnotationMirror annotation,
                                                        @Nonnull final String parameterName )
  {
    final Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
    final ExecutableElement annotationKey = values.keySet().stream().
      filter( k -> parameterName.equals( k.getSimpleName().toString() ) ).findFirst().orElse( null );
    return values.get( annotationKey );
  }

  @SuppressWarnings( "unchecked" )
  @Nonnull
  static <T> T getAnnotationValue( @Nonnull final AnnotationMirror annotation, @Nonnull final String parameterName )
  {
    final AnnotationValue value = findAnnotationValue( annotation, parameterName );
    assert null != value;
    return (T) value.getValue();
  }

  @Nonnull
  static AnnotationMirror getAnnotationByType( @Nonnull final AnnotatedConstruct annotated,
                                               @Nonnull final String annotationClassName )
  {
    AnnotationMirror mirror = findAnnotationByType( annotated, annotationClassName );
    assert null != mirror;
    return mirror;
  }

  @Nullable
  static AnnotationMirror findAnnotationByType( @Nonnull final AnnotatedConstruct annotated,
                                                @Nonnull final String annotationClassName )
  {
    return
      annotated.getAnnotationMirrors().stream()
        .filter( a -> a.getAnnotationType().toString().equals( annotationClassName ) )
        .findFirst()
        .orElse( null );
  }

  static boolean hasAnnotationOfType( @Nonnull final AnnotatedConstruct annotated,
                                      @Nonnull final String annotationClassName )
  {
    return null != findAnnotationByType( annotated, annotationClassName );
  }

  @Nonnull
  static String extractName( @Nonnull final ExecutableElement method,
                             @Nonnull final Function<ExecutableElement, String> defaultExtractor,
                             @Nonnull final String annotationClassname,
                             @Nonnull final String parameterName,
                             @Nonnull final String sentinelValue )
  {
    final String declaredName =
      (String) getAnnotationValue( method, annotationClassname, parameterName ).getValue();
    if ( sentinelValue.equals( declaredName ) )
    {
      final String defaultValue = defaultExtractor.apply( method );
      if ( null == defaultValue )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) + " target did not specify " +
                                      "the parameter " + parameterName + " and the default value could not be derived",
                                      method );
      }
      return defaultValue;
    }
    else
    {
      if ( !SourceVersion.isIdentifier( declaredName ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) + " target specified an " +
                                      "invalid value '" + declaredName + "' for the parameter " + parameterName + ". " +
                                      "The value must be a valid java identifier", method );
      }
      else if ( SourceVersion.isKeyword( declaredName ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) + " target specified an " +
                                      "invalid value '" + declaredName + "' for the parameter " + parameterName + ". " +
                                      "The value must not be a java keyword", method );
      }
      return declaredName;
    }
  }
}
