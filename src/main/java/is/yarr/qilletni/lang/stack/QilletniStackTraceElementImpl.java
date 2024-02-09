package is.yarr.qilletni.lang.stack;

import is.yarr.qilletni.api.lang.stack.QilletniStackTraceElement;

public class QilletniStackTraceElementImpl implements QilletniStackTraceElement {
    
    private final String library;
    private final String fileName;
    private final String methodName;
    private final int line;
    private final int column;

    public QilletniStackTraceElementImpl(String library, String fileName, String methodName, int line, int column) {
        this.library = library;
        this.fileName = fileName;
        this.methodName = methodName;
        this.line = line;
        this.column = column;
    }

    @Override
    public String getLibrary() {
        return library;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public String displayString() {
        return String.format("\tat [%s] %s %s:%d%s", library, methodName + "(..)", fileName, line, column != -1 ? ":" + column : "");
    }
}
