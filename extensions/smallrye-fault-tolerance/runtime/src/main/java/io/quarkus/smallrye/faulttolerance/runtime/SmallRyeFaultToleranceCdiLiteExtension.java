package io.quarkus.smallrye.faulttolerance.runtime;

import java.util.OptionalInt;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.inject.Scope;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import cdi.lite.extension.BuildCompatibleExtension;
import cdi.lite.extension.phases.Discovery;
import cdi.lite.extension.phases.Enhancement;
import cdi.lite.extension.phases.discovery.AppArchiveBuilder;
import cdi.lite.extension.phases.enhancement.Annotations;
import cdi.lite.extension.phases.enhancement.ClassConfig;
import cdi.lite.extension.phases.enhancement.ExactType;
import cdi.lite.extension.phases.enhancement.SubtypesOf;
import io.smallrye.faulttolerance.FaultToleranceBinding;
import io.smallrye.faulttolerance.FaultToleranceInterceptor;

public class SmallRyeFaultToleranceCdiLiteExtension implements BuildCompatibleExtension {
    @Discovery
    public void classes(AppArchiveBuilder app) {
        // MP FT doesn't contain beans.xml
        app.add("org.eclipse.microprofile.faulttolerance.Asynchronous");
        app.add("org.eclipse.microprofile.faulttolerance.Bulkhead");
        app.add("org.eclipse.microprofile.faulttolerance.CircuitBreaker");
        app.add("org.eclipse.microprofile.faulttolerance.Fallback");
        app.add("org.eclipse.microprofile.faulttolerance.Retry");
        app.add("org.eclipse.microprofile.faulttolerance.Timeout");

        // SRye FT doesn't contain beans.xml
        app.add("io.smallrye.faulttolerance.FaultToleranceInterceptor");
        app.add("io.smallrye.faulttolerance.ExecutorProvider");
        app.add("io.smallrye.faulttolerance.internal.StrategyCache");
        app.add("io.quarkus.smallrye.faulttolerance.runtime.QuarkusFaultToleranceOperationProvider");
        app.add("io.quarkus.smallrye.faulttolerance.runtime.QuarkusFallbackHandlerProvider");
        app.add("io.smallrye.faulttolerance.metrics.MetricsCollectorFactory");

        app.addSubtypesOf("org.eclipse.microprofile.faulttolerance.FallbackHandler");
    }

    @Enhancement
    @ExactType(type = Asynchronous.class)
    @ExactType(type = Bulkhead.class)
    @ExactType(type = CircuitBreaker.class)
    @ExactType(type = Fallback.class)
    @ExactType(type = Retry.class)
    @ExactType(type = Timeout.class)
    public void interceptorBinding(ClassConfig clazz) {
        clazz.addAnnotation(FaultToleranceBinding.class);
    }

    @Enhancement
    @ExactType(type = FaultToleranceInterceptor.class)
    public void interceptorPriority(ClassConfig clazz, Annotations ann) {
        Config config = ConfigProvider.getConfig();
        OptionalInt priority = config.getValue("mp.fault.tolerance.interceptor.priority", OptionalInt.class);
        if (priority.isPresent()) {
            clazz.removeAnnotation(it -> it.name().equals(Priority.class.getName()));
            clazz.addAnnotation(Priority.class, ann.attribute("value", priority.getAsInt()));
        }
    }

    @Enhancement
    @SubtypesOf(type = FallbackHandler.class) // added to bean archive in @Discovery, now make them beans
    public void fallbackHandlers(ClassConfig clazz) {
        boolean hasScope = clazz.annotations()
                .stream()
                .anyMatch(ann -> ann.declaration().hasAnnotation(Scope.class) || ann.declaration().hasAnnotation(NormalScope.class));
        if (!hasScope) {
            clazz.addAnnotation(Dependent.class);
        }
    }

    // alternative
/*
    @Enhancement
    public void process(AppArchiveConfig app) {
        app.classes()
                .exactly(Asynchronous.class)
                .exactly(Bulkhead.class)
                .exactly(CircuitBreaker.class)
                .exactly(Fallback.class)
                .exactly(Retry.class)
                .exactly(Timeout.class)
                .configure(it -> it.addAnnotation(new FaultToleranceBinding.Literal()));

        Config config = ConfigProvider.getConfig();
        OptionalInt priority = config.getValue("mp.fault.tolerance.interceptor.priority", OptionalInt.class);
        if (priority.isPresent()) {
            app.classes()
                    .exactly(FaultToleranceInterceptor.class)
                    .configure(it -> {
                        it.removeAnnotation(ann -> ann.name().equals(Priority.class.getName()));
                        it.addAnnotation(new FaultToleranceExtension.PriorityLiteral(priority.getAsInt()));
                    });
        }
    }
*/
}
