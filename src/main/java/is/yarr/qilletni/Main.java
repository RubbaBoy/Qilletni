package is.yarr.qilletni;

import is.yarr.qilletni.lang.runner.QilletniProgramRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws IOException {
        new Main().main(args[0]);
    }
    
    private void main(String programFile) throws IOException {
        var qilletniProgramRunner = new QilletniProgramRunner();

        qilletniProgramRunner.runProgram(Paths.get("input", programFile));
    }
}
