package is.yarr.qilletni.lang.stack;

import is.yarr.qilletni.api.lang.stack.QilletniStackTrace;
import is.yarr.qilletni.api.lang.stack.QilletniStackTraceElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class QilletniStackTraceImpl implements QilletniStackTrace {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniStackTraceImpl.class);
    
    private final Stack<QilletniStackTraceElement> stackTraceElements;

    public QilletniStackTraceImpl() {
        this(new Stack<>());
    }

    public QilletniStackTraceImpl(Stack<QilletniStackTraceElement> stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    @Override
    public void pushStackTraceElement(QilletniStackTraceElement stackTraceElement) {
        stackTraceElements.push(stackTraceElement);
    }

    @Override
    public void popStackTraceElement() {
        stackTraceElements.pop();
    }

    @Override
    public List<QilletniStackTraceElement> getStackTrace() {
        return Collections.unmodifiableList(stackTraceElements);
    }

    @Override
    public void printStackTrace() {
        LOGGER.debug("TODO: print stack trace");
    }

    @Override
    @SuppressWarnings("unchecked")
    public QilletniStackTrace cloneStackTrace() {
        return new QilletniStackTraceImpl((Stack<QilletniStackTraceElement>) stackTraceElements.clone());
    }
}
