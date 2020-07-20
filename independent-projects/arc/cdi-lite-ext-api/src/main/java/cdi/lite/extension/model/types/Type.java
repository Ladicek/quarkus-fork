package cdi.lite.extension.model.types;

import cdi.lite.extension.model.declarations.AnnotationInfo;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface Type { // TODO name
    enum Kind {
        /** E.g. when method returns {@code void}. */
        VOID,
        /** E.g. when method returns {@code int}. */
        PRIMITIVE,
        /** E.g. when method returns {@code String}. */
        CLASS,
        /** E.g. when method returns {@code int[]} or {@code String[]}. */
        ARRAY,
        /** E.g. when method returns {@code List<String>}. */
        PARAMETERIZED_TYPE,
        /** E.g. when method returns {@code T} and {@code T} is a type parameter of the declaring class. */
        TYPE_VARIABLE,
        /**
         * E.g. when method returns {@code List<? extends Number>}. On the first level, we have a {@code PARAMETERIZED_TYPE},
         * but on the second level, the first (and only) type argument is a {@code WILDCARD_TYPE}.
         */
        WILDCARD_TYPE,
    }

    Kind kind();

    // TODO for some kinds (class, parameterized type), provide a way to get to the declaration

    // annotations on type parameters and type uses

    boolean hasAnnotation(Class<? extends Annotation> annotationType);

    AnnotationInfo annotation(Class<? extends Annotation> annotationType);

    Collection<AnnotationInfo> repeatableAnnotation(Class<? extends Annotation> annotationType);

    Collection<AnnotationInfo> annotations();
}
