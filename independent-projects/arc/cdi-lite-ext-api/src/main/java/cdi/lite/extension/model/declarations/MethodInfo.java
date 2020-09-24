package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;
import cdi.lite.extension.model.types.TypeVariable;
import java.util.List;
import java.util.Optional;

public interface MethodInfo extends DeclarationInfo {
    String name();

    List<ParameterInfo> parameters();

    Type returnType();

    // TODO return Optional<Type> and only return non-empty if receiver parameter is declared,
    //  or return Type and always return a receiver type, even if not declared (and hence not annotated)?
    Optional<Type> receiverType();

    List<Type> throwsTypes();

    List<TypeVariable> typeParameters();

    boolean isStatic();

    boolean isAbstract();

    boolean isFinal();

    int modifiers();

    // ---

    @Override
    default Kind kind() {
        return Kind.METHOD;
    }

    @Override
    default MethodInfo asMethod() {
        return this;
    }
}
