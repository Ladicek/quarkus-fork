package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.ClassConfig;
import cdi.lite.extension.LiteExtension;
import org.jboss.jandex.DotName;

import java.util.Collection;

class DotNames {
    static final DotName LITE_EXTENSION = DotName.createSimple(LiteExtension.class.getName());
    static final DotName COLLECTION = DotName.createSimple(Collection.class.getName());

    static final DotName CLASS_CONFIG = DotName.createSimple(ClassConfig.class.getName());
}
