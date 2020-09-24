package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;

public interface FieldInfo extends DeclarationInfo {
    String name();

    Type type();

    boolean isStatic();

    boolean isFinal();

    int modifiers();

    // ---

    @Override
    default Kind kind() {
        return Kind.FIELD;
    }

    @Override
    default FieldInfo asField() {
        return this;
    }
}
