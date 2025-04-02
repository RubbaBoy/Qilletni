package dev.qilletni.impl.lang.math;

import dev.qilletni.api.lang.types.QilletniType;

public record MixedExpression<L extends QilletniType, R extends QilletniType>(L left, R right) {
}
