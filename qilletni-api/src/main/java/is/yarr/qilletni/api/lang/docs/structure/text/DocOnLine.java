package is.yarr.qilletni.api.lang.docs.structure.text;

import is.yarr.qilletni.api.lang.docs.structure.DocFieldType;
import org.jetbrains.annotations.Nullable;

public record DocOnLine(@Nullable DocFieldType docFieldType, @Nullable DocDescription description) {
}
