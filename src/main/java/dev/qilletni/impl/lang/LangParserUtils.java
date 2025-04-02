package dev.qilletni.impl.lang;

import dev.qilletni.impl.lang.exceptions.InternalLanguageException;
import org.antlr.v4.runtime.ParserRuleContext;

public class LangParserUtils {
    
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new InternalLanguageException("%s  NOTE: This should never occur, please report this as a bug.".formatted(message));
        }
        
        return obj;
    }
    
    public static <T> T requireNonNull(T obj, ParserRuleContext ctx, String message) {
        if (obj == null) {
            throw new InternalLanguageException(ctx, "%s  NOTE: This should never occur, please report this as a bug.".formatted(message));
        }
        
        return obj;
    }
    
}
