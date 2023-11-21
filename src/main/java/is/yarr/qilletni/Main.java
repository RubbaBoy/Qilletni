package is.yarr.qilletni;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.table.Scope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws IOException {
        LOGGER.info("Hello world!");
        
        new Main().main(args[0]);
    }
    
    private void main(String programFile) throws IOException {
        var qilletniProgramRunner = new QilletniProgramRunner();
        
        qilletniProgramRunner.runProgram(Paths.get("input", programFile));

        LOGGER.debug("Symbol table at the end:");

        qilletniProgramRunner.getSymbolTable().getAllScopes()
                .stream()
                .map(Scope::toString)
                .forEach(LOGGER::debug);
    }
}