package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.types.PrimitiveType;
import org.jboss.jandex.DotName;

class TypesReflection {
    static org.jboss.jandex.Type jandexType(Class<?> clazz) {
        if (clazz.isArray()) {
            int dimensions = 1;
            Class<?> componentType = clazz.getComponentType();
            while (componentType.isArray()) {
                dimensions++;
                componentType = componentType.getComponentType();
            }
            return org.jboss.jandex.ArrayType.create(jandexType(componentType), dimensions);
        }

        if (clazz.isPrimitive()) {
            if (clazz == Void.TYPE) {
                return org.jboss.jandex.Type.create(DotName.createSimple("void"), org.jboss.jandex.Type.Kind.VOID);
            } else if (clazz == Boolean.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.BOOLEAN.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else if (clazz == Byte.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.BYTE.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else if (clazz == Short.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.SHORT.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else if (clazz == Integer.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.INT.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else if (clazz == Long.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.LONG.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else if (clazz == Float.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.FLOAT.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else if (clazz == Double.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.DOUBLE.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else if (clazz == Character.TYPE) {
                return org.jboss.jandex.Type.create(
                        DotName.createSimple(PrimitiveType.PrimitiveKind.CHAR.name().toLowerCase()),
                        org.jboss.jandex.Type.Kind.PRIMITIVE);
            } else {
                throw new IllegalArgumentException("Unknown primitive type " + clazz);
            }
        }

        return org.jboss.jandex.Type.create(DotName.createSimple(clazz.getName()), org.jboss.jandex.Type.Kind.CLASS);
    }

    static org.jboss.jandex.Type jandexType(String clazz) {
        if (clazz.endsWith("[]")) {
            int dimensions = 1;
            String componentType = clazz.substring(0, clazz.length() - 2);
            while (componentType.endsWith("[]")) {
                dimensions++;
                componentType = componentType.substring(0, clazz.length() - 2);
            }
            return org.jboss.jandex.ArrayType.create(jandexType(componentType), dimensions);
        }

        if ("void".equals(clazz)) {
            return org.jboss.jandex.Type.create(DotName.createSimple("void"), org.jboss.jandex.Type.Kind.VOID);
        } else if ("boolean".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.BOOLEAN.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else if ("byte".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.BYTE.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else if ("short".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.SHORT.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else if ("int".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.INT.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else if ("long".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.LONG.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else if ("float".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.FLOAT.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else if ("double".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.DOUBLE.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else if ("char".equals(clazz)) {
            return org.jboss.jandex.Type.create(
                    DotName.createSimple(PrimitiveType.PrimitiveKind.CHAR.name().toLowerCase()),
                    org.jboss.jandex.Type.Kind.PRIMITIVE);
        } else {
            return org.jboss.jandex.Type.create(DotName.createSimple(clazz), org.jboss.jandex.Type.Kind.CLASS);
        }
    }
}
