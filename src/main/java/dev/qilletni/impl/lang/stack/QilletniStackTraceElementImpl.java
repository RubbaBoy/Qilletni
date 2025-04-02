package dev.qilletni.impl.lang.stack;

import dev.qilletni.api.lang.stack.QilletniStackTraceElement;

public class QilletniStackTraceElementImpl implements QilletniStackTraceElement {
    
    private final String library;
    private final String fileName;
    private final String methodName;
    private final int line;
    private final int column;
    
    private final boolean isBackgroundTask;

    public QilletniStackTraceElementImpl(String library, String fileName, String methodName, int line, int column) {
        this.library = library;
        this.fileName = fileName;
        this.methodName = methodName;
        this.line = line;
        this.column = column;

        this.isBackgroundTask = false;
    }
    
    private QilletniStackTraceElementImpl() {
        this.library = "";
        this.fileName = "";
        this.methodName = "";
        this.line = -1;
        this.column = -1;
        
        this.isBackgroundTask = true;
    }

    /**
     * Creates a background task stack trace element.
     *
     * @return The background task stack trace element
     */
    public static QilletniStackTraceElementImpl createBackgroundTask() {
        return new QilletniStackTraceElementImpl();
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
    public boolean isBackgroundTask() {
        return isBackgroundTask;
    }

    @Override
    public String displayString() {
        if (isBackgroundTask) {
            return "\tat [internal] Background Task";
        } else {
            return String.format("\tat [%s] %s %s:%d%s", library, methodName + "(..)", fileName, line, column != -1 ? ":" + column : "");
        }
    }
}
