package is.yarr.qilletni.api.lang.stack;

public interface QilletniStackTraceElement {
    
    String getLibrary();
    
    String getFileName();

    String getMethodName();

    int getLine();
    
    int getColumn();
    
    String displayString();
    
}
