package dev.qilletni.impl.lang.stack;

import dev.qilletni.api.lang.stack.QilletniStackTrace;
import dev.qilletni.api.lang.stack.QilletniStackTraceElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

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
    public String displayStackTrace() {
        return "Stack trace:\n%s".formatted(stackTraceElements.reversed().stream().map(QilletniStackTraceElement::displayString).collect(Collectors.joining("\n")));
    }

    @Override
    @SuppressWarnings("unchecked")
    public QilletniStackTrace cloneStackTrace() {
        return new QilletniStackTraceImpl((Stack<QilletniStackTraceElement>) stackTraceElements.clone());
    }

    @Override
    public String toString() {
        return "QilletniStackTraceImpl{" +
                "stackTraceElements=" + stackTraceElements +
                '}';
    }
}
