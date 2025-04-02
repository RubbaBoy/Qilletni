package dev.qilletni.impl.lang.table;

import dev.qilletni.api.lang.table.Symbol;
import dev.qilletni.api.lang.types.FunctionType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;

public class SymbolImpl<T extends QilletniType> implements Symbol<T> {
    
    private final String name;
    private final QilletniTypeClass<T> type;
    private T value;

    public SymbolImpl(String name, QilletniTypeClass<T> type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
    
    public static <T extends QilletniType> Symbol<?> createGenericSymbol(String name, QilletniTypeClass<?> type, T value) {
        return new SymbolImpl<>(name, (QilletniTypeClass<T>) type, value);
    }
    
    public static Symbol<FunctionType> createFunctionSymbol(String name, FunctionType value) {
        return new SymbolImpl<>(name, QilletniTypeClass.FUNCTION, value);
    }

    /**
     * Creates a symbol to be used as a placeholder for a real symbol that's in a {@link dev.qilletni.api.lang.table.Scope}.
     * 
     * @param value The value of the symbol
     * @return The synthetic symbol
     * @param <T> The type of the symbol
     */
    public static <T extends QilletniType> Symbol<T> createSyntheticSymbol(T value) {
        return new SymbolImpl<>("synthetic-%d".formatted(value.hashCode()), (QilletniTypeClass<T>) value.getTypeClass(), value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public QilletniTypeClass<T> getType() {
        return type;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Symbol<?> symbol = (Symbol<?>) object;
        return Objects.equals(name, symbol.getName()) && type == symbol.getType() && Objects.equals(value, symbol.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
