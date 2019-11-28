package react4j.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

@SuppressWarnings( "SameParameterValue" )
final class ProcessorUtil
{
  private static final Pattern GETTER_PATTERN = Pattern.compile( "^get([A-Z].*)$" );
  private static final Pattern ISSER_PATTERN = Pattern.compile( "^is([A-Z].*)$" );

  private ProcessorUtil()
  {
  }

  @SuppressWarnings( "unchecked" )
  static boolean isWarningSuppressed( @Nonnull final Element element,
                                      @Nonnull final String warning,
                                      @Nullable final String alternativeSuppressWarnings )
  {
    if ( null != alternativeSuppressWarnings )
    {
      final AnnotationMirror suppress = AnnotationsUtil.findAnnotationByType( element, alternativeSuppressWarnings );
      if ( null != suppress )
      {
        final AnnotationValue value = AnnotationsUtil.findAnnotationValueNoDefaults( suppress, "value" );
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
  static List<TypeElement> getInterfaces( @Nonnull final TypeElement element )
  {
    final List<TypeElement> superTypes = new ArrayList<>();
    enumerateInterfaces( element, superTypes );
    return superTypes;
  }

  private static void enumerateInterfaces( @Nonnull final TypeElement element,
                                           @Nonnull final List<TypeElement> superTypes )
  {
    final TypeMirror superclass = element.getSuperclass();
    if ( TypeKind.NONE != superclass.getKind() )
    {
      final TypeElement superclassElement = (TypeElement) ( (DeclaredType) superclass ).asElement();
      enumerateInterfaces( superclassElement, superTypes );
    }
    for ( final TypeMirror interfaceType : element.getInterfaces() )
    {
      final TypeElement interfaceElement = (TypeElement) ( (DeclaredType) interfaceType ).asElement();
      superTypes.add( interfaceElement );
      enumerateInterfaces( interfaceElement, superTypes );
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
  static String getPropertyAccessorName( @Nonnull final ExecutableElement method,
                                         @Nonnull final String specifiedName,
                                         @Nonnull final String sentinelName )
    throws ProcessorException
  {
    String name = deriveName( method, GETTER_PATTERN, specifiedName, sentinelName );
    if ( null != name )
    {
      return name;
    }
    else if ( method.getReturnType().getKind() == TypeKind.BOOLEAN )
    {
      name = deriveName( method, ISSER_PATTERN, specifiedName, sentinelName );
      if ( null != name )
      {
        return name;
      }
    }
    return method.getSimpleName().toString();
  }

  private static boolean isAbstractInterfaceMethod( final @Nonnull ExecutableElement method )
  {
    return method.getModifiers().contains( Modifier.ABSTRACT ) &&
           ElementKind.INTERFACE == method.getEnclosingElement().getKind();
  }

  private static boolean isSubsignature( @Nonnull final Types typeUtils,
                                         @Nonnull final TypeElement typeElement,
                                         @Nonnull final ExecutableType methodType,
                                         @Nonnull final ExecutableElement candidate )
  {
    final ExecutableType candidateType =
      (ExecutableType) typeUtils.asMemberOf( (DeclaredType) typeElement.asType(), candidate );
    final boolean isEqual = methodType.equals( candidateType );
    final boolean isSubsignature = typeUtils.isSubsignature( methodType, candidateType );
    return isSubsignature || isEqual;
  }

  @Nonnull
  static List<ExecutableElement> getConstructors( @Nonnull final TypeElement element )
  {
    return element.getEnclosedElements().stream().
      filter( m -> m.getKind() == ElementKind.CONSTRUCTOR ).
      map( m -> (ExecutableElement) m ).
      collect( Collectors.toList() );
  }

  @Nullable
  static String deriveName( @Nonnull final Element method,
                            @Nonnull final Pattern pattern,
                            @Nonnull final String name,
                            @Nonnull final String sentinelName )
    throws ProcessorException
  {
    if ( sentinelName.equals( name ) )
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

  static boolean doesMethodOverrideInterfaceMethod( @Nonnull final Types typeUtils,
                                                    @Nonnull final TypeElement typeElement,
                                                    @Nonnull final ExecutableElement method )
  {
    return getInterfaces( typeElement ).stream()
      .flatMap( i -> i.getEnclosedElements().stream() )
      .filter( e1 -> e1 instanceof ExecutableElement )
      .map( e1 -> (ExecutableElement) e1 )
      .collect(
        Collectors.toList() ).stream()
      .anyMatch( e -> isSubsignature( typeUtils,
                                      typeElement,
                                      (ExecutableType) typeUtils.asMemberOf( (DeclaredType) typeElement.asType(), e ),
                                      method ) );
  }
}
