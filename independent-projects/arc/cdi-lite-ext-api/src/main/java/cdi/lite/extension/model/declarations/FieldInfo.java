package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;

// Graeme comments start
// -------------
// Roughly equivalent to Micronaut's FieldElement:
// https://docs.micronaut.io/latest/api/io/micronaut/inject/ast/FieldElement.html
//
// -------------
// Graeme comments end
public interface FieldInfo extends DeclarationInfo, NamedInfo {

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
