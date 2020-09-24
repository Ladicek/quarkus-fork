package cdi.lite.extension.model.declarations;

import cdi.lite.extension.model.types.Type;
import cdi.lite.extension.model.types.TypeVariable;
import java.util.Collection;
import java.util.List;

public interface ClassInfo extends DeclarationInfo {
    String name();

    String simpleName();

    PackageInfo packageInfo();

    List<TypeVariable> typeParameters();

    Type superClass();

    ClassInfo superClassDeclaration();

    List<Type> superInterfaces();

    List<ClassInfo> superInterfacesDeclarations();

    boolean isPlainClass();

    boolean isInterface();

    boolean isEnum();

    boolean isAnnotation();

    boolean isAbstract();

    boolean isFinal();

    int modifiers();

    Collection<MethodInfo> constructors(); // no static initializers

    Collection<MethodInfo> methods(); // no constructors nor static initializers

    Collection<FieldInfo> fields();

    // ---

    @Override
    default Kind kind() {
        return Kind.CLASS;
    }

    @Override
    default ClassInfo asClass() {
        return this;
    }
}
