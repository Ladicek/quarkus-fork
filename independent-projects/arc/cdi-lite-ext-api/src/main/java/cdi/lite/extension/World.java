package cdi.lite.extension;

import cdi.lite.extension.model.declarations.ClassInfo;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Stream;

public interface World {
    ClassesQuery classes();

    MethodQuery methods();

    FieldQuery fields();

    AnnotationQuery annotations();

    interface ClassesQuery {
        ClassesQuery subtypeOf(Class<?> clazz);
        ClassesQuery subtypeOf(ClassInfo<?> clazz);

        ClassesQuery supertypeOf(Class<?> clazz);
        ClassesQuery supertypeOf(ClassInfo<?> clazz);

        ClassesQuery annotatedWith(Class<? extends Annotation> annotationType);
        ClassesQuery annotatedWith(ClassInfo<?> annotationType);

        Collection<ClassInfo<?>> find();

        default Stream<ClassInfo<?>> stream() {
            return find().stream();
        };
    }

    interface MethodQuery {

    }

    interface FieldQuery {

    }

    interface AnnotationQuery {

    }
}
