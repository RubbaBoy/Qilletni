package is.yarr.qilletni;

import is.yarr.qilletni.lang.types.EntityType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EntityTest {

    private QilletniProgramTester programTester;

    @BeforeEach
    void setup() {
        programTester = new QilletniProgramTester(List.of(), List.of());
    }
    
    @Test
    void testEntityCreationWithoutProperties() {
        var ranProgram = programTester.runProgram("""
                entity Foo {
                    Foo()
                }
                """);
        var runner = ranProgram.runner();

        var symbols = ranProgram.symbolTable().currentScope().getAllSymbols();
        assertEquals(0, symbols.size());

        var entityDefinitionManager = runner.getEntityDefinitionManager();
        var definition = entityDefinitionManager.lookup("Foo");
        assertEquals("Foo", definition.getTypeName());
        assertEquals(0, definition.getUninitializedParams().size());
    }
    
    @Test
    void testEntityCreationWithProperties() {
        var ranProgram = programTester.runProgram("""
                entity Foo {
                    int i
                    string s
                    
                    Foo(i, s)
                }
                """);
        var runner = ranProgram.runner();

        var symbols = ranProgram.symbolTable().currentScope().getAllSymbols();
        assertEquals(0, symbols.size());

        var entityDefinitionManager = runner.getEntityDefinitionManager();
        var entityDefinition = entityDefinitionManager.lookup("Foo");
        assertEquals("Foo", entityDefinition.getTypeName());
        
        var params = entityDefinition.getUninitializedParams();
        assertEquals(2, params.size());

        var iParam = params.get("i");
        assertFalse(iParam.isEntity());
        assertEquals(QilletniTypeClass.INT, iParam.getNativeTypeClass());

        var sParam = params.get("s");
        assertFalse(sParam.isEntity());
        assertEquals(QilletniTypeClass.STRING, sParam.getNativeTypeClass());
    }

    @Test
    void testEntityInstantiationWithoutProperties() {
        var ranProgram = programTester.runProgram("""
                entity Foo {
                    Foo()
                }
                
                Foo foo = new Foo()
                """);
        var runner = ranProgram.runner();

        var symbols = ranProgram.symbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var entityDefinitionManager = runner.getEntityDefinitionManager();
        var entityDefinition = entityDefinitionManager.lookup("Foo");

        var entitySymbol = symbols.get("foo");
        assertEquals(entityDefinition, entitySymbol.getType().getEntityDefinition());
        var entity = (EntityType) entitySymbol.getValue();
        
        assertEquals(0, entity.getEntityScope().getAllSymbols().size());
    }

    @Test
    void testEntityInstantiationWithProperties() {
        var ranProgram = programTester.runProgram("""
                entity Foo {
                    int i
                    string s
                    
                    Foo(i, s)
                }
                
                Foo foo = new Foo(123, "bar")
                """);
        var runner = ranProgram.runner();

        var symbols = ranProgram.symbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var entityDefinitionManager = runner.getEntityDefinitionManager();
        var entityDefinition = entityDefinitionManager.lookup("Foo");

        var entitySymbol = symbols.get("foo");
        assertEquals(entityDefinition, entitySymbol.getType().getEntityDefinition());
        var entity = (EntityType) entitySymbol.getValue();
        
        var entitySymbols = entity.getEntityScope().getAllSymbols();
        assertEquals(2, entitySymbols.size());

        var iSymbol = entitySymbols.get("i");
        assertEquals(QilletniTypeClass.INT, iSymbol.getType());
        assertEquals(123, ((IntType) iSymbol.getValue()).getValue());

        var sSymbol = entitySymbols.get("s");
        assertEquals(QilletniTypeClass.STRING, sSymbol.getType());
        assertEquals("bar", ((StringType) sSymbol.getValue()).getValue());
    }

    @Test
    void testEntityInstantiationWithPropertiesAndRearrangedConstructor() {
        var ranProgram = programTester.runProgram("""
                entity Foo {
                    int i
                    string s
                    
                    Foo(s, i)
                }
                
                Foo foo = new Foo("bar", 123)
                """);
        var runner = ranProgram.runner();

        var symbols = ranProgram.symbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var entityDefinitionManager = runner.getEntityDefinitionManager();
        var entityDefinition = entityDefinitionManager.lookup("Foo");

        var entitySymbol = symbols.get("foo");
        assertEquals(entityDefinition, entitySymbol.getType().getEntityDefinition());
        var entity = (EntityType) entitySymbol.getValue();
        
        var entitySymbols = entity.getEntityScope().getAllSymbols();
        assertEquals(2, entitySymbols.size());

        var iSymbol = entitySymbols.get("i");
        assertEquals(QilletniTypeClass.INT, iSymbol.getType());
        assertEquals(123, ((IntType) iSymbol.getValue()).getValue());

        var sSymbol = entitySymbols.get("s");
        assertEquals(QilletniTypeClass.STRING, sSymbol.getType());
        assertEquals("bar", ((StringType) sSymbol.getValue()).getValue());
    }

    @Test
    void testEntityInstantiationWithPredefinedProperties() {
        var ranProgram = programTester.runProgram("""
                entity Foo {
                    int i = 123
                    string s
                    
                    Foo(s)
                }
                
                Foo foo = new Foo("bar")
                """);
        var runner = ranProgram.runner();

        var symbols = ranProgram.symbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var entityDefinitionManager = runner.getEntityDefinitionManager();
        var entityDefinition = entityDefinitionManager.lookup("Foo");

        var entitySymbol = symbols.get("foo");
        assertEquals(entityDefinition, entitySymbol.getType().getEntityDefinition());
        var entity = (EntityType) entitySymbol.getValue();

        var entitySymbols = entity.getEntityScope().getAllSymbols();
        assertEquals(2, entitySymbols.size());

        var iSymbol = entitySymbols.get("i");
        assertEquals(QilletniTypeClass.INT, iSymbol.getType());
        assertEquals(123, ((IntType) iSymbol.getValue()).getValue());

        var sSymbol = entitySymbols.get("s");
        assertEquals(QilletniTypeClass.STRING, sSymbol.getType());
        assertEquals("bar", ((StringType) sSymbol.getValue()).getValue());
    }

    @Test
    void testEntityInstantiationWithOnlyPredefinedProperties() {
        var ranProgram = programTester.runProgram("""
                entity Foo {
                    int i = 123
                    
                    Foo()
                }
                
                Foo foo = new Foo()
                """);
        var runner = ranProgram.runner();

        var symbols = ranProgram.symbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var entityDefinitionManager = runner.getEntityDefinitionManager();
        var entityDefinition = entityDefinitionManager.lookup("Foo");

        var entitySymbol = symbols.get("foo");
        assertEquals(entityDefinition, entitySymbol.getType().getEntityDefinition());
        var entity = (EntityType) entitySymbol.getValue();

        var entitySymbols = entity.getEntityScope().getAllSymbols();
        assertEquals(1, entitySymbols.size());

        var iSymbol = entitySymbols.get("i");
        assertEquals(QilletniTypeClass.INT, iSymbol.getType());
        assertEquals(123, ((IntType) iSymbol.getValue()).getValue());
    }
    
}
