package cdi.lite.extension;

import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.declarations.FieldInfo;
import cdi.lite.extension.model.declarations.MethodInfo;
import cdi.lite.extension.model.types.Type;
import cdi.lite.extension.phases.enhancement.*;

import java.lang.annotation.Annotation;

public interface AppArchive {
    // Graeme comments start
    // -------------
    // Enable access to the factory for creating types from strings.
    // -------------
    // Graeme comments end
    Types types();

    // Graeme comments start
    // -------------
    // Note that in the case of annotation processors the model is event driven
    // rather than pull driven hence I altered this interface to add DeclarationInfoProcessor where you register hooks that can be triggered.
    //
    // This is different from the pull model where you ask for all the classes.
    // In addition in the case of processing source code you don't have access
    // to the whole world, only the classes being processed by this annotation round
    // so the queries would only return results from those.
    // -------------
    // Graeme comments end
    ClassQuery classes();

    MethodQuery constructors(); // no static initializers

    MethodQuery methods(); // no constructors nor static initializers

    FieldQuery fields();

    /**
     * The {@code exactly}, {@code subtypeOf} and {@code supertypeOf} methods are additive.
     * When called multiple times, they form a union of requested classes (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.classes()
     *     .exactly(Foo.class)
     *     .subtypeOf(Bar.class)
     *     .find()
     * }</pre>
     * returns the {@code Foo} class and all subtypes of the {@code Bar} class.
     * <p>
     * The {@code annotatedWith} methods are additive.
     * When called multiple times, they form a union of requested annotations (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.classes()
     *     .annotatedWith(Foo.class)
     *     .annotatedWith(Bar.class)
     *     .find()
     * }</pre>
     * returns all classes annotated either with {@code @Foo} or with {@code @Bar} (or both).
     */
    interface ClassQuery extends DeclarationInfoProcessor<ClassQuery, ClassInfo> {
        ClassQuery exactly(String clazz);

        ClassQuery exactly(ClassInfo clazz);

        ClassQuery exactly(Type type);

        ClassQuery subtypeOf(String clazz);

        ClassQuery subtypeOf(ClassInfo clazz);

        ClassQuery subtypeOf(Type type);

        ClassQuery supertypeOf(String clazz);

        ClassQuery supertypeOf(ClassInfo clazz);

        ClassQuery supertypeOf(Type type);

        ClassQuery annotatedWith(Class<? extends Annotation> annotationType);

        ClassQuery annotatedWith(String annotationName);

        ClassQuery annotatedWith(ClassInfo annotationType);

        ClassQuery annotatedWith(Type annotationType);
    }

    /**
     * The {@code declaredOn} method is additive.
     * When called multiple times, it forms a union of requested classes (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.methods()
     *     .declaredOn(appArchive.classes().exactly(Foo.class))
     *     .declaredOn(appArchive.classes().subtypeOf(Bar.class))
     *     .find()
     * }</pre>
     * returns all methods declared on the {@code Foo} class and on all subtypes of the {@code Bar} class.
     * Note that this example can be rewritten as
     * <pre>{@code
     * appArchive.methods()
     *     .declaredOn(appArchive.classes().exactly(Foo.class).subtypeOf(Bar.class))
     *     .find()
     * }</pre>
     * which is probably easier to understand.
     * <p>
     * The {@code withReturnType} methods are additive.
     * When called multiple times, they form a union of requested return types (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.methods()
     *     .withReturnType(Foo.class)
     *     .withReturnType(Bar.class)
     *     .find()
     * }</pre>
     * returns all methods that return either {@code Foo} or {@code Bar}.
     * <p>
     * The {@code annotatedWith} methods are additive.
     * When called multiple times, they form a union of requested annotations (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.methods()
     *     .annotatedWith(Foo.class)
     *     .annotatedWith(Bar.class)
     *     .find()
     * }</pre>
     * returns all methods annotated either with {@code @Foo} or with {@code @Bar} (or both).
     */
    interface MethodQuery extends DeclarationInfoProcessor<MethodQuery, MethodInfo> {
        MethodQuery declaredOn(ClassQuery classes);

        /**
         * Equivalent to {@code withReturnType(types.of(type))}, where {@code types} is {@link Types}.
         */
        MethodQuery withReturnType(String type); // TODO remove for stringly-typed API?

        MethodQuery withReturnType(Type type);

        // TODO parameters?

        MethodQuery annotatedWith(Class<? extends Annotation> annotationType);

        MethodQuery annotatedWith(ClassInfo annotationType);

        MethodQuery annotatedWith(Type annotationType);
    }

    /**
     * The {@code declaredOn} method is additive.
     * When called multiple times, it forms a union of requested classes (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.fields()
     *     .declaredOn(appArchive.classes().exactly(Foo.class))
     *     .declaredOn(appArchive.classes().subtypeOf(Bar.class))
     *     .find()
     * }</pre>
     * returns all fields declared on the {@code Foo} class and on all subtypes of the {@code Bar} class.
     * Note that this example can be rewritten as
     * <pre>{@code
     * appArchive.fields()
     *     .declaredOn(appArchive.classes().exactly(Foo.class).subtypeOf(Bar.class))
     *     .find()
     * }</pre>
     * which is probably easier to understand.
     * <p>
     * The {@code ofType} methods are additive.
     * When called multiple times, they form a union of requested field types (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.fields()
     *     .ofType(Foo.class)
     *     .ofType(Bar.class)
     *     .find()
     * }</pre>
     * returns all fields that are of type either {@code Foo} or {@code Bar}.
     * <p>
     * The {@code annotatedWith} methods are additive.
     * When called multiple times, they form a union of requested annotations (not an intersection).
     * For example,
     * <pre>{@code
     * appArchive.fields()
     *     .annotatedWith(Foo.class)
     *     .annotatedWith(Bar.class)
     *     .find()
     * }</pre>
     * returns all fields annotated either with {@code @Foo} or with {@code @Bar} (or both).
     */
    interface FieldQuery extends DeclarationInfoProcessor<FieldQuery, FieldInfo> {
        FieldQuery declaredOn(ClassQuery classes);

        /**
         * Equivalent to {@code ofType(types.of(type))}, where {@code types} is {@link Types}.
         */
        FieldQuery ofType(String type); // TODO remove for stringly-typed API?

        FieldQuery ofType(Type type);

        FieldQuery annotatedWith(Class<? extends Annotation> annotationType);

        FieldQuery annotatedWith(ClassInfo annotationType);

        FieldQuery annotatedWith(Type annotationType);
    }
}
