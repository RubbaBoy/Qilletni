package is.yarr.qilletni.lang.math;

import is.yarr.qilletni.api.lang.types.QilletniType;

public record MixedExpression<L extends QilletniType, R extends QilletniType>(L left, R right) {
}
