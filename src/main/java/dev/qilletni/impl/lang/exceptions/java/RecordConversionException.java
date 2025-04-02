package dev.qilletni.impl.lang.exceptions.java;

import dev.qilletni.impl.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class RecordConversionException extends QilletniContextException {

    public RecordConversionException() {
        super();
    }

    public RecordConversionException(String message) {
        super(message);
    }

    public RecordConversionException(ParserRuleContext ctx) {
        super(ctx);
    }

    public RecordConversionException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

//    public RecordConversionException(String message, Throwable cause) {
//        super(cause);
//        setStackTrace(cause.getStackTrace());
//    }

    public RecordConversionException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}
