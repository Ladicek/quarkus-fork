package io.quarkus.smallrye.faulttolerance.runtime;

import java.util.OptionalInt;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;

import cdi.lite.extension.BuildCompatibleExtension;
import cdi.lite.extension.phases.enhancement.ExactType;
import io.smallrye.faulttolerance.FaultToleranceExtension;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import cdi.lite.extension.phases.Enhancement;
import cdi.lite.extension.phases.enhancement.Annotations;
import cdi.lite.extension.phases.enhancement.AppArchiveConfig;
import cdi.lite.extension.phases.enhancement.ClassEntrypoint;
import io.smallrye.faulttolerance.FaultToleranceBinding;
import io.smallrye.faulttolerance.FaultToleranceInterceptor;

public class SmallRyeFaultToleranceCdiLiteExtension implements BuildCompatibleExtension {
    @Enhancement
    public void process(
            @ExactType(type = Asynchronous.class, annotatedWith = InterceptorBinding.class) ClassEntrypoint asynchronousClass,
            @ExactType(type = Bulkhead.class, annotatedWith = InterceptorBinding.class) ClassEntrypoint bulkheadClass,
            @ExactType(type = CircuitBreaker.class, annotatedWith = InterceptorBinding.class) ClassEntrypoint circuitBreakerClass,
            @ExactType(type = Fallback.class, annotatedWith = InterceptorBinding.class) ClassEntrypoint fallbackClass,
            @ExactType(type = Retry.class, annotatedWith = InterceptorBinding.class) ClassEntrypoint retryClass,
            @ExactType(type = Timeout.class, annotatedWith = InterceptorBinding.class) ClassEntrypoint timeoutClass,
            @ExactType(type = FaultToleranceInterceptor.class, annotatedWith = Interceptor.class) ClassEntrypoint ftInterceptorClass,
            Annotations ann,
            AppArchiveConfig app) {

        asynchronousClass.addAnnotation(FaultToleranceBinding.class);
        bulkheadClass.addAnnotation(FaultToleranceBinding.class);
        circuitBreakerClass.addAnnotation(FaultToleranceBinding.class);
        fallbackClass.addAnnotation(FaultToleranceBinding.class);
        retryClass.addAnnotation(FaultToleranceBinding.class);
        timeoutClass.addAnnotation(FaultToleranceBinding.class);

        // alternative:
//        app.classes()
//                .exactly(Asynchronous.class)
//                .exactly(Bulkhead.class)
//                .exactly(CircuitBreaker.class)
//                .exactly(Fallback.class)
//                .exactly(Retry.class)
//                .exactly(Timeout.class)
//                .configure(it -> it.addAnnotation(new FaultToleranceBinding.Literal()));

        // in the original Quarkus extension, this runs later (the build step consumes BeanArchiveIndexBuildItem)
        // do we need to find a way how to express that in CDI Lite Extensions?
        Config config = ConfigProvider.getConfig();
        OptionalInt priority = config.getValue("mp.fault.tolerance.interceptor.priority", OptionalInt.class);
        if (priority.isPresent()) {
            ftInterceptorClass.removeAnnotation(it -> it.name().equals(Priority.class.getName()));
            ftInterceptorClass.addAnnotation(Priority.class, ann.attribute("value", priority.getAsInt()));
            // alternative:
//            app.classes()
//                    .exactly(FaultToleranceInterceptor.class)
//                    .configure(it -> {
//                        it.removeAnnotation(ann -> ann.name().equals(Priority.class.getName()));
//                        it.addAnnotation(new FaultToleranceExtension.PriorityLiteral(priority.getAsInt()));
//                    });
        }
    }
}
