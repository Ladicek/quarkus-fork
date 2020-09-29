package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;

// Graeme comments start
// -------------
// Roughly equivalent to Micronaut's ParameterElement:
// https://docs.micronaut.io/latest/api/io/micronaut/inject/ast/ParameterElement.html
//
// -------------
// Graeme comments end
public interface ParameterInfo extends DeclarationInfo, NamedInfo {
    Type type();

    // ---

    @Override
    default Kind kind() {
        return Kind.PARAMETER;
    }

    @Override
    default ParameterInfo asParameter() {
        return this;
    }
}
