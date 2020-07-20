package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;

import java.util.Collection;

/**
 * @param <T> the inspected class
 */
public interface ClassInfo<T> extends DeclarationInfo {
    String name();
    String simpleName();
    PackageInfo _package();

    ClassInfo<?> superClass();
    Collection<ClassInfo<?>> superInterfaces();

    Type superClassType(); // TODO
    Collection<Type> superInterfacesTypes(); // TODO

    boolean isClass();
    boolean isInterface();
    boolean isEnum();
    boolean isAnnotation();

    // TODO modifiers

    Collection<MethodInfo<?>> constructors(); // TODO static initializers?
    Collection<MethodInfo<?>> methods(); // static and instance methods, but not constructors; TODO fold them into this?
    Collection<FieldInfo<?>> fields();

    // ---

    @Override
    default boolean isType() {
        return true;
    }

    @Override
    default ClassInfo<?> asType() {
        return this;
    }
}
