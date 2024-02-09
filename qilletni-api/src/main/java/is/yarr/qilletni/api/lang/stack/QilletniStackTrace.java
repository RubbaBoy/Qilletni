package is.yarr.qilletni.api.lang.stack;

import java.util.List;

public interface QilletniStackTrace {
    
    void pushStackTraceElement(QilletniStackTraceElement stackTraceElement);
    
    void popStackTraceElement();
    
    List<QilletniStackTraceElement> getStackTrace();
    
    String displayStackTrace();

    QilletniStackTrace cloneStackTrace();
    
}
