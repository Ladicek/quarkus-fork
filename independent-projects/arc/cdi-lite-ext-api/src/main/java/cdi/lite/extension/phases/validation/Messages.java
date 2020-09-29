package cdi.lite.extension.phases.validation;

import cdi.lite.extension.beans.BeanInfo;
import cdi.lite.extension.beans.ObserverInfo;
import cdi.lite.extension.model.AnnotationTarget;

/**
 * Interface for reporting messages.
 */
// Graeme comments start
// -------------
// Renamed this from Errors to Messages and added the possibility to access it from
// various phases. In the scope of annotation processors it is important you pass the
// originating element such that you can report errors to javac and have the compiler
// point to the field / method / constructor / class that caused the error.

// In addition you may want to emit other kinds of messages like information and
// deprecation warnings.
// -------------
// Graeme comments end
public interface Messages {
    /**
     * Add a warning that is not related to any particular element, or that information is not known.
     */
    void info(String message);

    /**
     * Add a warning which is related to given {@link AnnotationTarget} (which is most likely
     * a {@link cdi.lite.extension.model.declarations.DeclarationInfo DeclarationInfo}).
     */
    void info(String message, AnnotationTarget relatedTo);

    /**
     * Add a warning which is related to given {@link BeanInfo}.
     */
    void info(String message, BeanInfo relatedTo);

    /**
     * Add a warning which is related to given {@link ObserverInfo}.
     */
    void info(String message, ObserverInfo relatedTo);
    /**
     * Add a warning that is not related to any particular element, or that information is not known.
     */
    void warn(String message);

    /**
     * Add a warning which is related to given {@link AnnotationTarget} (which is most likely
     * a {@link cdi.lite.extension.model.declarations.DeclarationInfo DeclarationInfo}).
     */
    void warn(String message, AnnotationTarget relatedTo);

    /**
     * Add a warning which is related to given {@link BeanInfo}.
     */
    void warn(String message, BeanInfo relatedTo);

    /**
     * Add a warning which is related to given {@link ObserverInfo}.
     */
    void warn(String message, ObserverInfo relatedTo);


    /**
     * Add a generic error that is not related to any particular element, or that information is not known.
     */
    void error(String message);

    /**
     * Add an error which is related to given {@link AnnotationTarget} (which is most likely
     * a {@link cdi.lite.extension.model.declarations.DeclarationInfo DeclarationInfo}).
     */
    void error(String message, AnnotationTarget relatedTo);

    /**
     * Add an error which is related to given {@link BeanInfo}.
     */
    void error(String message, BeanInfo relatedTo);

    /**
     * Add an error which is related to given {@link ObserverInfo}.
     */
    void error(String message, ObserverInfo relatedTo);

    /**
     * Add a generic error that is represented by an exception.
     */
    void error(Exception exception);
}
