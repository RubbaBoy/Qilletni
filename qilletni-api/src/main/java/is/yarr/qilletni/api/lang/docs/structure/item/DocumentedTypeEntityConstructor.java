package is.yarr.qilletni.api.lang.docs.structure.item;

import java.util.List;

public record DocumentedTypeEntityConstructor(String libraryName, String importPath, String name, List<String> params) implements DocumentedType {
}
