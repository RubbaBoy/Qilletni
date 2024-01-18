package is.yarr.qilletni;

import is.yarr.qilletni.lang.runner.ImportPathState;
import is.yarr.qilletni.lang.runner.QilletniProgramRunner;
import is.yarr.qilletni.lang.table.SymbolTable;
import org.antlr.v4.runtime.CharStreams;

import java.util.List;
import java.util.stream.Collectors;

public class QilletniProgramTester {
    
    private final String importStatement;
    private final List<Class<?>> nativeFunctionClasses;

    public QilletniProgramTester(List<String> importedFiles, List<Class<?>> nativeFunctionClasses) {
        importStatement = importedFiles.stream().map(file -> String.format("import \"%s\"", file)).collect(Collectors.joining("\n"));
        this.nativeFunctionClasses = nativeFunctionClasses;
    }
    
    public RanProgram runProgram(String program) {
        var runner = new QilletniProgramRunner(null);
        runner.getNativeFunctionHandler().registerClasses(nativeFunctionClasses.toArray(Class[]::new));
        var symbolTable = runner.runProgram(CharStreams.fromString(importStatement + "\n" + program), ImportPathState.VIRTUAL_STATE);
        
        return new RanProgram(runner, symbolTable);
    }
    
    record RanProgram(QilletniProgramRunner runner, SymbolTable symbolTable) {}
    
}
