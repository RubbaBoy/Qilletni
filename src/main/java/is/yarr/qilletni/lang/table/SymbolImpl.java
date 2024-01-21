package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.api.lang.table.Symbol;
import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import org.antlr.v4.runtime.ParserRuleContext;

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
