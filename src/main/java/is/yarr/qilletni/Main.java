package is.yarr.qilletni;

import is.yarr.qilletni.lang.runner.QilletniProgramRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    
    public static void main(String[] args) throws IOException {
        new Main().main(args[0]);
    }
    
    private void main(String programFile) throws IOException {
//        var dynamicProvider = ServiceManager.createDynamicProvider();
//        var qilletniProgramRunner = new QilletniProgramRunner(dynamicProvider);
//        qilletniProgramRunner.importInitialFiles();
//        qilletniProgramRunner.runProgram(Paths.get("input", programFile));
    }
}
