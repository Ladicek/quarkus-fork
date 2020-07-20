package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;

/**
 * @param <T> type of whomever declares the inspected field
 */
public interface FieldInfo<T> extends DeclarationInfo {
    String name();

    Type type();

    // TODO modifiers

    // ---

    @Override
    default boolean isField() {
        return true;
    }

    @Override
    default FieldInfo<?> asField() {
        return this;
    }
}
