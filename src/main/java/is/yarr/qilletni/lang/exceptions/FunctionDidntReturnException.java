package is.yarr.qilletni.lang.exceptions;

public class FunctionDidntReturnException extends RuntimeException {
    public FunctionDidntReturnException() {
    }

    public FunctionDidntReturnException(String message) {
        super(message);
    }
}
