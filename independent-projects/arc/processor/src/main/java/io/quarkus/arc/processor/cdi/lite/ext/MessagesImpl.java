package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.Messages;
import cdi.lite.extension.beans.BeanInfo;
import cdi.lite.extension.beans.ObserverInfo;
import cdi.lite.extension.model.AnnotationTarget;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.spi.DeploymentException;

class MessagesImpl implements Messages {
    final List<Throwable> errors = new ArrayList<>();

    // TODO proper logging, if we decide to do that
    // TODO proper handling of `relatedTo.toString`

    @Override
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void info(String message, AnnotationTarget relatedTo) {
        System.out.println("[INFO] " + message + " at " + relatedTo);
    }

    @Override
    public void info(String message, BeanInfo<?> relatedTo) {
        System.out.println("[INFO] " + message + " at " + relatedTo);
    }

    @Override
    public void info(String message, ObserverInfo<?> relatedTo) {
        System.out.println("[INFO] " + message + " at " + relatedTo);
    }

    @Override
    public void warn(String message) {
        System.out.println("[WARN] " + message);
    }

    @Override
    public void warn(String message, AnnotationTarget relatedTo) {
        System.out.println("[WARN] " + message + " at " + relatedTo);
    }

    @Override
    public void warn(String message, BeanInfo<?> relatedTo) {
        System.out.println("[WARN] " + message + " at " + relatedTo);
    }

    @Override
    public void warn(String message, ObserverInfo<?> relatedTo) {
        System.out.println("[WARN] " + message + " at " + relatedTo);
    }

    @Override
    public void error(String message) {
        System.out.println("[ERROR] " + message);
        errors.add(new DeploymentException(message));
    }

    @Override
    public void error(String message, AnnotationTarget relatedTo) {
        System.out.println("[ERROR] " + message + " at " + relatedTo);
        errors.add(new DeploymentException(message + " at " + relatedTo));
    }

    @Override
    public void error(String message, BeanInfo<?> relatedTo) {
        System.out.println("[ERROR] " + message + " at " + relatedTo);
        errors.add(new DeploymentException(message + " at " + relatedTo));
    }

    @Override
    public void error(String message, ObserverInfo<?> relatedTo) {
        System.out.println("[ERROR] " + message + " at " + relatedTo);
        errors.add(new DeploymentException(message + " at " + relatedTo));
    }

    @Override
    public void error(Exception exception) {
        System.out.println("[ERROR] " + exception.getMessage());
        errors.add(exception);
    }
}
