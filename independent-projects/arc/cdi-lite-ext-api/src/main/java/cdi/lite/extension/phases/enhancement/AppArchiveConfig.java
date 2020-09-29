package cdi.lite.extension.phases.enhancement;

import cdi.lite.extension.AppArchive;
import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.types.Type;
import java.lang.annotation.Annotation;
import java.util.Collection;

public interface AppArchiveConfig extends AppArchive {
    @Override
    ClassConfigQuery classes();

    @Override
    MethodConfigQuery constructors(); // no static initializers

    @Override
    MethodConfigQuery methods(); // no constructors nor static initializers

    @Override
    FieldConfigQuery fields();

    interface ClassConfigQuery extends ClassQuery, AnnotationConfigurable<ClassQuery, ClassConfig>{
        @Override
        ClassConfigQuery exactly(String clazz);

        @Override
        ClassConfigQuery exactly(ClassInfo clazz);

        @Override
        ClassConfigQuery subtypeOf(String clazz);

        @Override
        ClassConfigQuery subtypeOf(ClassInfo clazz);

        @Override
        ClassConfigQuery supertypeOf(String clazz);

        @Override
        ClassConfigQuery supertypeOf(ClassInfo clazz);

        @Override
        ClassConfigQuery annotatedWith(Class<? extends Annotation> annotationType);

        @Override
        ClassConfigQuery annotatedWith(ClassInfo annotationType);
    }

    interface MethodConfigQuery extends MethodQuery, AnnotationConfigurable<MethodQuery, MethodConfig> {
        @Override
        MethodConfigQuery declaredOn(ClassQuery classes);

        @Override
        MethodConfigQuery withReturnType(String type); // TODO remove for stringly-typed API?

        @Override
        MethodConfigQuery withReturnType(Type type);

        @Override
        MethodConfigQuery annotatedWith(Class<? extends Annotation> annotationType);

        @Override
        MethodConfigQuery annotatedWith(ClassInfo annotationType);
    }

    interface FieldConfigQuery extends FieldQuery, AnnotationConfigurable<FieldQuery, FieldConfig> {
        @Override
        FieldConfigQuery declaredOn(ClassQuery classes);

        @Override
        FieldConfigQuery ofType(String type); // TODO remove for stringly-typed API?

        @Override
        FieldConfigQuery ofType(Type type);

        @Override
        FieldConfigQuery annotatedWith(Class<? extends Annotation> annotationType);

        @Override
        FieldConfigQuery annotatedWith(ClassInfo annotationType);
    }
}
