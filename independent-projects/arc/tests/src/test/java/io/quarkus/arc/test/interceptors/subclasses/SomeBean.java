package io.quarkus.arc.test.interceptors.subclasses;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@MyBinding
public class SomeBean {

    public String foo() {
        return "";
    }

    public String bar() {
        return "";
    }

}
