package cdi.lite.extension.model.declarations;

// Graeme comments start
// -------------
// Roughly equivalent to Micronaut's JavaPackageElement:
// https://docs.micronaut.io/latest/api/io/micronaut/annotation/processing/visitor/JavaPackageElement.html
//
// -------------
// Graeme comments end
// TODO is this useful? perhaps `ClassInfo.packageName` returning a `String` would be enough?
public interface PackageInfo extends DeclarationInfo, NamedInfo {
    // ---

    @Override
    default Kind kind() {
        return Kind.PACKAGE;
    }

    @Override
    default PackageInfo asPackage() {
        return this;
    }
}
