package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;
import cdi.lite.extension.model.types.TypeVariable;

import java.util.List;
import java.util.Optional;

/**
 * @param <T> type of whomever declares the inspected method or constructor
 */
public interface MethodInfo<T> extends DeclarationInfo {
    String name();

    List<ParameterInfo<T>> parameters();

    Type returnType();

    Optional<Type> receiverType();

    List<Type> throwsTypes();

    List<TypeVariable> typeParameters();

    // TODO modifiers

    // ---

    @Override
    default boolean isMethod() {
        return true;
    }

    @Override
    default MethodInfo<?> asMethod() {
        return this;
    }
}
