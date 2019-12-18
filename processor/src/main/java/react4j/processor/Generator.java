package react4j.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.ExecutableElement;

final class Generator
{
  private Generator()
  {
  }

  @Nonnull
  static final List<String> ANNOTATION_WHITELIST =
    Arrays.asList( Constants.NONNULL_ANNOTATION_CLASSNAME,
                   Constants.NULLABLE_ANNOTATION_CLASSNAME,
                   SuppressWarnings.class.getName(),
                   Deprecated.class.getName() );

  static void copyWhitelistedAnnotations( @Nonnull final AnnotatedConstruct element,
                                          @Nonnull final ParameterSpec.Builder builder )
  {
    GeneratorUtil.copyWhitelistedAnnotations( element, builder, ANNOTATION_WHITELIST );
  }

  static void copyWhitelistedAnnotations( @Nonnull final AnnotatedConstruct element,
                                          @Nonnull final MethodSpec.Builder builder )
  {
    GeneratorUtil.copyWhitelistedAnnotations( element, builder, ANNOTATION_WHITELIST );
  }

  static boolean isNonnull( @Nonnull final ExecutableElement method )
  {
    return AnnotationsUtil.hasAnnotationOfType( method, Constants.NONNULL_ANNOTATION_CLASSNAME );
  }
}
