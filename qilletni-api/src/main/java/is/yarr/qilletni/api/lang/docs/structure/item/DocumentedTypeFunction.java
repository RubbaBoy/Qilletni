package is.yarr.qilletni.api.lang.docs.structure.item;

import java.util.List;
import java.util.Optional;

public record DocumentedTypeFunction(String libraryName, String importPath, String name, List<String> params, boolean isNative, boolean isStatic, Optional<String> onOptional) implements DocumentedType {
}
