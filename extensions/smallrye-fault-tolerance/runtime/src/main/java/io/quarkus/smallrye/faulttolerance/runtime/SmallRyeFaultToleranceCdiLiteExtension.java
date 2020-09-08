package io.quarkus.smallrye.faulttolerance.runtime;

import java.util.OptionalInt;

import javax.annotation.Priority;

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
import cdi.lite.extension.phases.enhancement.ClassConfig;
import io.smallrye.faulttolerance.FaultToleranceBinding;
import io.smallrye.faulttolerance.FaultToleranceInterceptor;

public class SmallRyeFaultToleranceCdiLiteExtension {
    @Enhancement
    public void process(ClassConfig<Asynchronous> asynchronousClass,
            ClassConfig<Bulkhead> bulkheadClass,
            ClassConfig<CircuitBreaker> circuitBreakerClass,
            ClassConfig<Fallback> fallbackClass,
            ClassConfig<Retry> retryClass,
            ClassConfig<Timeout> timeoutClass,
            ClassConfig<FaultToleranceInterceptor> ftInterceptorClass,
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
//                .configure()
//                .forEach(it -> it.addAnnotation(new FaultToleranceBinding.Literal()));

        // in the original Quarkus extension, this runs later (the build step consumes BeanArchiveIndexBuildItem)
        // do we need to find a way how to express that in CDI Lite Extensions?
        Config config = ConfigProvider.getConfig();
        OptionalInt priority = config.getValue("mp.fault.tolerance.interceptor.priority", OptionalInt.class);
        if (priority.isPresent()) {
            ftInterceptorClass.removeAnnotation(it -> it.name().equals(Priority.class.getName()));
            ftInterceptorClass.addAnnotation(Priority.class, ann.attribute("value", priority.getAsInt()));
            // alternative:
//            ftInterceptorClass.addAnnotation(new FaultToleranceExtension.PriorityLiteral(priority.getAsInt()));
        }
    }
}
