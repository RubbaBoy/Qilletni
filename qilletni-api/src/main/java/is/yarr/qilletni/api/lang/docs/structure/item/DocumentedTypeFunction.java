package is.yarr.qilletni.api.lang.docs.structure.item;

import java.util.List;
import java.util.Optional;

public record DocumentedTypeFunction(String libraryName, String importPath, String name, List<String> params, boolean isNative, Optional<String> onOptional) implements DocumentedType {
}
