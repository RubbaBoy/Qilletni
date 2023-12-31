package is.yarr.qilletni;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionTest {
    
    private QilletniProgramTester programTester;
    
    private static final AtomicBoolean invoked = new AtomicBoolean(false);
    
    @BeforeEach
    void setup() {
        programTester = new QilletniProgramTester(List.of("test/functiontest.ql"), List.of(NativeFunctions.class));
    }

    /**
     * Verifies the actual test functions are working.
     */
    @Test
    void verifyTestFunctions() {
        programTester.runProgram("""
                invokeFunction()
                """);
        
        assertTrue(invoked.get());
    }
    
    public static class NativeFunctions {
        
        public static void invokeFunction() {
            invoked.set(true);
        }
    }
    
}
