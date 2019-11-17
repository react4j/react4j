package react4j.processor;

import com.google.auto.common.AnnotationMirrors;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
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
  static AnnotationValue getAnnotationValue( @Nonnull final AnnotatedConstruct annotated,
                                             @Nonnull final String annotationClassName,
                                             @Nonnull final String parameterName )
  {
    final AnnotationValue value = findAnnotationValue( annotated, annotationClassName, parameterName );
    assert null != value;
    return value;
  }

  @Nullable
  private static AnnotationValue findAnnotationValue( @Nonnull final AnnotatedConstruct annotated,
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
    return annotated.getAnnotationMirrors().stream().
      filter( a -> a.getAnnotationType().toString().equals( annotationClassName ) ).findFirst().orElse( null );
  }

  static boolean hasAnnotationOfType( @Nonnull final AnnotatedConstruct annotated,
                                      @Nonnull final String annotationClassName )
  {
    return null != findAnnotationByType( annotated, annotationClassName );
  }
}
