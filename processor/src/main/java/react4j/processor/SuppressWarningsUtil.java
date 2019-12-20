package react4j.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

@SuppressWarnings( "unused" )
final class SuppressWarningsUtil
{
  private SuppressWarningsUtil()
  {
  }

  @Nonnull
  static AnnotationSpec suppressWarningsAnnotation( @Nonnull final String... warnings )
  {
    return Objects.requireNonNull( maybeSuppressWarningsAnnotation( warnings ) );
  }

  @Nullable
  static AnnotationSpec maybeSuppressWarningsAnnotation( @Nonnull final String... warnings )
  {
    final List<String> actualWarnings =
      Arrays.stream( warnings ).filter( Objects::nonNull ).sorted().collect( Collectors.toList() );
    if ( actualWarnings.isEmpty() )
    {
      return null;
    }
    else
    {
      final AnnotationSpec.Builder builder = AnnotationSpec.builder( SuppressWarnings.class );
      actualWarnings.forEach( w -> builder.addMember( "value", "$S", w ) );
      return builder.build();
    }
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final TypeSpec.Builder method,
                                             @Nonnull final TypeMirror type )
  {
    addSuppressWarningsIfRequired( processingEnv, method, Collections.singleton( type ) );
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final TypeSpec.Builder method,
                                             @Nonnull final Collection<TypeMirror> types )
  {
    addSuppressWarningsIfRequired( processingEnv, method, Collections.emptyList(), types );
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final TypeSpec.Builder method,
                                             @Nonnull final Collection<String> additionalSuppressions,
                                             @Nonnull final Collection<TypeMirror> types )
  {
    final AnnotationSpec suppress =
      maybeSuppressWarningsAnnotation( processingEnv, additionalSuppressions, types );
    if ( null != suppress )
    {
      method.addAnnotation( suppress );
    }
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final MethodSpec.Builder method,
                                             @Nonnull final TypeMirror type )
  {
    addSuppressWarningsIfRequired( processingEnv, method, Collections.singleton( type ) );
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final MethodSpec.Builder method,
                                             @Nonnull final Collection<TypeMirror> types )
  {
    addSuppressWarningsIfRequired( processingEnv, method, Collections.emptyList(), types );
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final MethodSpec.Builder method,
                                             @Nonnull final Collection<String> additionalSuppressions,
                                             @Nonnull final Collection<TypeMirror> types )
  {
    final AnnotationSpec suppress =
      maybeSuppressWarningsAnnotation( processingEnv, additionalSuppressions, types );
    if ( null != suppress )
    {
      method.addAnnotation( suppress );
    }
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final FieldSpec.Builder method,
                                             @Nonnull final TypeMirror type )
  {
    addSuppressWarningsIfRequired( processingEnv, method, Collections.singleton( type ) );
  }

  static void addSuppressWarningsIfRequired( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final FieldSpec.Builder method,
                                             @Nonnull final Collection<TypeMirror> types )
  {
    final AnnotationSpec suppress = maybeSuppressWarningsAnnotation( processingEnv, types );
    if ( null != suppress )
    {
      method.addAnnotation( suppress );
    }
  }

  /**
   * Generate a suppress warnings annotation if any of the types passed in are either deprecated or rawtypes.
   *
   * @param types the types to analyze to determine if suppressions are required.
   * @return a suppress annotation if required.
   */
  @Nullable
  static AnnotationSpec maybeSuppressWarningsAnnotation( @Nonnull final ProcessingEnvironment processingEnv,
                                                         @Nonnull final Collection<TypeMirror> types )
  {
    return maybeSuppressWarningsAnnotation( processingEnv, Collections.emptyList(), types );
  }

  /**
   * Generate a suppress warnings annotation if any of the types passed in are either deprecated or rawtypes.
   * The additionalSuppressions parameter will also be added to list of suppressions.
   *
   * @param additionalSuppressions the suppressions that must be added to suppression annotation.
   * @param types                  the types to analyze to determine if suppressions are required.
   * @return a suppress annotation if required.
   */
  @Nullable
  static AnnotationSpec maybeSuppressWarningsAnnotation( @Nonnull final ProcessingEnvironment processingEnv,
                                                         @Nonnull final Collection<String> additionalSuppressions,
                                                         @Nonnull final Collection<TypeMirror> types )
  {
    final boolean hasRawTypes = types.stream().anyMatch( t -> hasRawTypes( processingEnv, t ) );
    final boolean hasDeprecatedTypes =
      types.stream().anyMatch( t -> hasDeprecatedTypes( processingEnv, t ) );

    if ( hasRawTypes || hasDeprecatedTypes || !additionalSuppressions.isEmpty() )
    {
      final ArrayList<String> suppressions = new ArrayList<>( additionalSuppressions );
      if ( hasRawTypes )
      {
        suppressions.add( "rawtypes" );
      }
      if ( hasDeprecatedTypes )
      {
        suppressions.add( "deprecation" );
      }
      return suppressWarningsAnnotation( suppressions.stream().sorted().distinct().toArray( String[]::new ) );
    }
    else
    {
      return null;
    }
  }

  private static boolean hasDeprecatedTypes( @Nonnull final ProcessingEnvironment processingEnv,
                                             @Nonnull final TypeMirror type )
  {
    if ( type instanceof TypeVariable )
    {
      final TypeVariable typeVariable = (TypeVariable) type;
      return hasDeprecatedTypes( processingEnv, typeVariable.getLowerBound() ) ||
             hasDeprecatedTypes( processingEnv, typeVariable.getUpperBound() );
    }
    else if ( type instanceof ArrayType )
    {
      return hasDeprecatedTypes( processingEnv, ( (ArrayType) type ).getComponentType() );
    }
    else if ( type instanceof DeclaredType )
    {
      final TypeElement element = (TypeElement) processingEnv.getTypeUtils().asElement( type );
      if ( element.getAnnotationMirrors()
        .stream()
        .anyMatch( a -> a.getAnnotationType().toString().equals( Deprecated.class.getName() ) ) )
      {
        return true;
      }
      else
      {
        final DeclaredType declaredType = (DeclaredType) type;
        return declaredType
          .getTypeArguments()
          .stream()
          .anyMatch( t -> hasDeprecatedTypes( processingEnv, t ) );
      }
    }
    else if ( type instanceof ExecutableType )
    {
      final ExecutableType executableType = (ExecutableType) type;
      return AnnotationsUtil.hasAnnotationOfType( executableType, Deprecated.class.getName() ) ||
             hasDeprecatedTypes( processingEnv, executableType.getReturnType() ) ||
             executableType.getTypeVariables()
               .stream()
               .anyMatch( t -> hasDeprecatedTypes( processingEnv, t ) ) ||
             executableType.getThrownTypes()
               .stream()
               .anyMatch( t -> hasDeprecatedTypes( processingEnv, t ) ) ||
             executableType.getParameterTypes()
               .stream()
               .anyMatch( t -> hasDeprecatedTypes( processingEnv, t ) );
    }
    else
    {
      return false;
    }
  }

  private static boolean hasRawTypes( @Nonnull final ProcessingEnvironment processingEnv,
                                      @Nonnull final TypeMirror type )
  {
    if ( type instanceof TypeVariable )
    {
      final TypeVariable typeVariable = (TypeVariable) type;
      return hasRawTypes( processingEnv, typeVariable.getLowerBound() ) ||
             hasRawTypes( processingEnv, typeVariable.getUpperBound() );
    }
    else if ( type instanceof ArrayType )
    {
      return hasRawTypes( processingEnv, ( (ArrayType) type ).getComponentType() );
    }
    else if ( type instanceof DeclaredType )
    {
      final DeclaredType declaredType = (DeclaredType) type;
      final int typeArgumentCount = declaredType.getTypeArguments().size();
      final TypeElement typeElement = (TypeElement) processingEnv.getTypeUtils().asElement( type );
      if( typeArgumentCount != typeElement.getTypeParameters().size() )
      {
        return true;
      }
      else
      {
        return declaredType
          .getTypeArguments()
          .stream()
          .anyMatch( t -> hasRawTypes( processingEnv, t ) );
      }
    }
    else if ( type instanceof ExecutableType )
    {
      final ExecutableType executableType = (ExecutableType) type;
      return hasRawTypes( processingEnv, executableType.getReturnType() ) ||
             executableType.getTypeVariables()
               .stream()
               .anyMatch( t -> hasRawTypes( processingEnv, t ) ) ||
             executableType.getThrownTypes()
               .stream()
               .anyMatch( t -> hasRawTypes( processingEnv, t ) ) ||
             executableType.getParameterTypes()
               .stream()
               .anyMatch( t -> hasRawTypes( processingEnv, t ) );
    }
    else
    {
      return false;
    }
  }
}
