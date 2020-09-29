package cdi.lite.extension.model;

import cdi.lite.extension.model.declarations.DeclarationInfo;
import cdi.lite.extension.model.types.Type;
import java.lang.annotation.Annotation;
import java.util.Collection;

// Graeme comments start
// -------------
// This API seems a much more limited version of Micronaut's AnnotationMetadata and AnnotationSource APIs:
//
// https://docs.micronaut.io/latest/api/io/micronaut/core/annotation/AnnotationMetadata.html
// https://docs.micronaut.io/latest/api/io/micronaut/core/annotation/AnnotationSource.html
//
// Seems like in a more realistic scenario we would need to make this more complete as it
// appears quite complex do things like retrieve a String[] of values, get nested annotations, get an array of enums, get all the values of a repeated annotations etc.
//
// -------------
// Graeme comments end
/**
 * Annotation target is anything that can be annotated.
 * That is:
 *
 * <ul>
 * <li>a <i>declaration</i>, such as a class, method, field, etc.</li>
 * <li>a <i>type parameter</i>, occuring in class declarations and method declarations</li>
 * <li>a <i>type use</i>, such as a type of method parameter, a type of field, a type argument, etc.</li>
 * </ul>
 */
public interface AnnotationTarget {
    boolean isDeclaration();

    boolean isType();

    DeclarationInfo asDeclaration();

    Type asType();

    boolean hasAnnotation(Class<? extends Annotation> annotationType);

    // TODO what if missing?
    AnnotationInfo annotation(Class<? extends Annotation> annotationType);

    Collection<AnnotationInfo> repeatableAnnotation(Class<? extends Annotation> annotationType);

    Collection<AnnotationInfo> annotations();
}
