package is.yarr.qilletni.table;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.types.CollectionType;
import is.yarr.qilletni.types.FunctionType;
import is.yarr.qilletni.types.QilletniType;
import is.yarr.qilletni.types.SongType;
import is.yarr.qilletni.types.WeightsType;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Symbol<T extends QilletniType> {
    
    private final String name;
    private final int paramCount;
    private final SymbolType type;
    private T value;

    public Symbol(String name, SymbolType type, T value) {
        this.name = name;
        this.paramCount = -1;
        this.type = type;
        this.value = value;
    }

    public Symbol(String name, int paramCount, T value) {
        this.name = name;
        this.paramCount = paramCount;
        this.type = SymbolType.FUNCTION;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getParamCount() {
        return paramCount;
    }

    public SymbolType getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public enum SymbolType {
        INT(QilletniLexer.INT_TYPE, Integer.class),
        STRING(QilletniLexer.STRING_TYPE, String.class),
        COLLECTION(QilletniLexer.COLLECTION_TYPE, CollectionType.class),
        SONG(QilletniLexer.SONG_TYPE, SongType.class),
        WEIGHTS(QilletniLexer.WEIGHTS_KEYWORD, WeightsType.class),
        FUNCTION(-1, FunctionType.class);
        
        private final int tokenType;
        private final Class<?> internalType;

        SymbolType(int tokenType, Class<?> internalType) {
            this.tokenType = tokenType;
            this.internalType = internalType;
        }

        public int getTokenType() {
            return tokenType;
        }

        public Class<?> getInternalType() {
            return internalType;
        }

        public boolean validateSameType(Object object) {
            return object.getClass().equals(internalType);
        }
        
        public static SymbolType fromTokenType(int tokenType) {
            return Arrays.stream(values())
                    .filter(symbol -> symbol.tokenType == tokenType)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Unknown token!"));
        }
    }
    
}
