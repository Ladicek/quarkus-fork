package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.AnnotationTarget;
import cdi.lite.extension.model.types.Type;

// Graeme comments start
// -------------
// What I don't understand regarding the design on this interface
// is why the need for the Kind and various isXXX() methods
// plus methods that throw IllegalStateException.
//
// Surely it is better to use inheritance and instanceof checks plus casts.
//
// The API here is roughly equivalent to https://github.com/micronaut-projects/micronaut-core/blob/2.0.x/inject/src/main/java/io/micronaut/inject/ast/ClassElement.java#L31
// -------------
// Graeme comments end
public interface DeclarationInfo extends AnnotationTarget {
    @Override
    default boolean isDeclaration() {
        return true;
    }

    @Override
    default boolean isType() {
        return false;
    }

    @Override
    default DeclarationInfo asDeclaration() {
        return this;
    }

    @Override
    default Type asType() {
        throw new IllegalStateException("Not a type");
    }

    enum Kind {
        /** Packages can be annotated in {@code package-info.java}. */
        PACKAGE,
        CLASS,
        METHOD,
        PARAMETER,
        FIELD,
    }

    Kind kind();

    default boolean isPackage() {
        return kind() == Kind.PACKAGE;
    }

    default boolean isClass() {
        return kind() == Kind.CLASS;
    }

    default boolean isMethod() {
        return kind() == Kind.METHOD;
    }

    default boolean isParameter() {
        return kind() == Kind.PARAMETER;
    }

    default boolean isField() {
        return kind() == Kind.FIELD;
    }

    default PackageInfo asPackage() {
        throw new IllegalStateException("Not a package");
    }

    default ClassInfo asClass() {
        throw new IllegalStateException("Not a class");
    }

    default MethodInfo asMethod() {
        throw new IllegalStateException("Not a method");
    }

    default ParameterInfo asParameter() {
        throw new IllegalStateException("Not a parameter");
    }

    default FieldInfo asField() {
        throw new IllegalStateException("Not a field");
    }
}
