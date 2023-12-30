package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.EntityType;
import is.yarr.qilletni.lang.types.FunctionType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.WeightsType;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.Arrays;
import java.util.Objects;

public class Symbol<T extends QilletniType> {
    
    private final String name;
    private final int paramCount;
    private final QilletniTypeClass<T> type;
    private T value;

    public Symbol(String name, QilletniTypeClass<T> type, T value) {
        this.name = name;
        this.paramCount = -1;
        this.type = type;
        this.value = value;
    }

    private Symbol(String name, int paramCount, QilletniTypeClass<T> type, T value) {
        this.name = name;
        this.paramCount = paramCount;
        this.type = type;
        this.value = value;
    }
    
    public static <T extends QilletniType> Symbol<?> createGenericSymbol(String name, QilletniTypeClass<?> type, T value) {
        return new Symbol<>(name, (QilletniTypeClass<T>) type, value);
    }
    
    public static Symbol<FunctionType> createFunctionSymbol(String name, int paramCount, FunctionType value) {
        return new Symbol<>(name, paramCount, QilletniTypeClass.FUNCTION, value);
    }

    public String getName() {
        return name;
    }

    public int getParamCount() {
        return paramCount;
    }

    public QilletniTypeClass<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

//    public enum SymbolType {
//        INT(QilletniLexer.INT_TYPE, Integer.class, IntType.class),
//        BOOLEAN(QilletniLexer.BOOLEAN_TYPE, Boolean.class, BooleanType.class),
//        STRING(QilletniLexer.STRING_TYPE, String.class, StringType.class),
//        COLLECTION(QilletniLexer.COLLECTION_TYPE, CollectionType.class, CollectionType.class),
//        SONG(QilletniLexer.SONG_TYPE, SongType.class, SongType.class),
//        WEIGHTS(QilletniLexer.WEIGHTS_KEYWORD, WeightsType.class, WeightsType.class),
//        FUNCTION(-1, FunctionType.class, FunctionType.class),
//        ENTITY(QilletniLexer.ID, EntityType.class, EntityType.class);
//        
//        private final int tokenType;
//        private final Class<?> internalType;
//        private final Class<?> qilletniType;
//
//        SymbolType(int tokenType, Class<?> internalType, Class<?> qilletniType) {
//            this.tokenType = tokenType;
//            this.internalType = internalType;
//            this.qilletniType = qilletniType;
//        }
//
//        public int getTokenType() {
//            return tokenType;
//        }
//
//        public Class<?> getInternalType() {
//            return internalType;
//        }
//
//        public Class<?> getQilletniType() {
//            return qilletniType;
//        }
//
//        public boolean validateSameType(Object object) {
//            return object.getClass().equals(internalType);
//        }
//        
//        public static SymbolType fromTokenType(int tokenType) {
//            return Arrays.stream(values())
//                    .filter(symbol -> symbol.tokenType == tokenType)
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Unknown token!"));
//        }
//        
//        public static SymbolType fromInternalType(Class<?> internalType) {
//            return Arrays.stream(values())
//                    .filter(symbol -> symbol.internalType.equals(internalType))
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Unknown internal type: " + internalType.getName()));
//        }
//        
//        public static <T extends QilletniType> SymbolType fromQilletniType(Class<T> qilletniType) {
//            return Arrays.stream(values())
//                    .filter(symbol -> symbol.qilletniType.equals(qilletniType))
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Unknown qilletni type: " + qilletniType.getName()));
//        }
//    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Symbol<?> symbol = (Symbol<?>) object;
        return paramCount == symbol.paramCount && Objects.equals(name, symbol.name) && type == symbol.type && Objects.equals(value, symbol.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, paramCount, type, value);
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", paramCount=" + paramCount +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
