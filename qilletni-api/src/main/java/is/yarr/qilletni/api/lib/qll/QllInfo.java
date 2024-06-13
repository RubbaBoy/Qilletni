package is.yarr.qilletni.api.lib.qll;

import java.util.List;

/**
 * A record of a JSON file in a .qll that holds metadata about it.
 * 
 * @param name
 * @param version
 * @param author
 * @param dependencies
 * @param libraryClass
 * @param providerClass
 */
public record QllInfo(String name, Version version, String author, List<QilletniInfoData.Dependency> dependencies, String libraryClass, String providerClass) {
    public QllInfo(QilletniInfoData qilletniInfoData, String libraryClass, String providerClass) {
        this(qilletniInfoData.name(), qilletniInfoData.version(), qilletniInfoData.author(), qilletniInfoData.dependencies(), libraryClass, providerClass);
    }
}
