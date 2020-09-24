package cdi.lite.extension.phases.discovery;

public interface AppArchiveBuilder {
    void add(String clazz);

    void addWithSubclasses(String clazz);
}
