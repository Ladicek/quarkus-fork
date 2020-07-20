package cdi.lite.extension.model.declarations;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface DeclarationInfo {
    boolean hasAnnotation(Class<? extends Annotation> annotationType);

    AnnotationInfo annotation(Class<? extends Annotation> annotationType);

    Collection<AnnotationInfo> repeatableAnnotation(Class<? extends Annotation> annotationType);

    Collection<AnnotationInfo> annotations();

    // ---

    default boolean isPackage() {
        return false;
    }

    default boolean isType() {
        return false;
    }

    default boolean isMethod() {
        return false;
    }

    default boolean isParameter() {
        return false;
    }

    default boolean isField() {
        return false;
    }

    default PackageInfo asPackage() {
        throw new IllegalArgumentException("Not a package");
    }

    default ClassInfo<?> asType() {
        throw new IllegalArgumentException("Not a type");
    }

    default MethodInfo<?> asMethod() {
        throw new IllegalArgumentException("Not a method");
    }

    default ParameterInfo<?> asParameter() {
        throw new IllegalArgumentException("Not a parameter");
    }

    default FieldInfo<?> asField() {
        throw new IllegalArgumentException("Not a field");
    }
}
