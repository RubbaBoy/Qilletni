package is.yarr.qilletni;

import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.ListType;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.TypelessListType;
import is.yarr.qilletni.lang.types.WeightsType;
import is.yarr.qilletni.lang.types.collection.CollectionDefinition;
import is.yarr.qilletni.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.types.weights.WeightUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

public class TypeTest {

    private QilletniProgramTester programTester;
    
    @BeforeEach
    void setup() {
        programTester = new QilletniProgramTester(List.of("test/typetest.ql"), List.of(TypeTestFunctions.class));
    }
    
    @Test
    void testBoolean() {
        var ranProgram = programTester.runProgram("""
                boolean t = true
                boolean f = false
                """);

        var symbols = ranProgram.symbolTable().currentScope();
        
        var t = symbols.lookup("t");
        assertEquals(QilletniTypeClass.BOOLEAN, t.getType());
        assertTrue(((BooleanType) t.getValue()).getValue());
        
        var f = symbols.lookup("f");
        assertEquals(QilletniTypeClass.BOOLEAN, f.getType());
        assertFalse(((BooleanType) f.getValue()).getValue());
    }
    
    @Test
    void testInt() {
        var ranProgram = programTester.runProgram("""
                int i = 42
                """);

        var symbols = ranProgram.symbolTable().currentScope();
        
        var i = symbols.lookup("i");
        assertEquals(QilletniTypeClass.INT, i.getType());
        assertEquals(42, ((IntType) i.getValue()).getValue());
    }
    
    @Test
    void testString() {
        var ranProgram = programTester.runProgram("""
                string s = "foo"
                """);

        var symbols = ranProgram.symbolTable().currentScope();
        
        var s = symbols.lookup("s");
        assertEquals(QilletniTypeClass.STRING, s.getType());
        assertEquals("foo", ((StringType) s.getValue()).getValue());
    }

    @Test
    void testSongWithNameAndArtist() {
        var ranProgram = programTester.runProgram("""
                song s = "God Knows" by "Knocked Loose"
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var songSymbol = symbols.lookup("s");
        assertEquals(QilletniTypeClass.SONG, songSymbol.getType());
        var song = (SongType) songSymbol.getValue();
        
        assertEquals("God Knows", song.getSuppliedTitle());
        assertEquals("Knocked Loose", song.getSuppliedArtist());
        assertNull(song.getSuppliedUrl());
    }

    @Test
    void testSongWithUrl() {
        var ranProgram = programTester.runProgram("""
                song s = "https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe"
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var songSymbol = symbols.lookup("s");
        assertEquals(QilletniTypeClass.SONG, songSymbol.getType());
        var song = (SongType) songSymbol.getValue();
        
        assertNull(song.getSuppliedTitle());
        assertNull(song.getSuppliedArtist());
        assertEquals("https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe", song.getSuppliedUrl());
    }
    
    @Test
    void testWeights() {
        var ranProgram = programTester.runProgram("""
                weights w =
                    | 3x "God Knows" by "Knocked Loose"
                    | 2x "https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe"
                    | 10% "US" by "Apex Alpha"
                """);

        var symbols = ranProgram.symbolTable().currentScope();
        
        var weightsSymbol = symbols.lookup("w");
        assertEquals(QilletniTypeClass.WEIGHTS, weightsSymbol.getType());
        var weights = (WeightsType) weightsSymbol.getValue();
        
        var weightEntries = weights.getWeightEntries();
        assertEquals(3, weightEntries.size());

        var firstWeight = weightEntries.get(0);
        assertEquals(3, firstWeight.getWeightAmount());
        assertEquals(WeightUnit.MULTIPLIER, firstWeight.getWeightUnit());
        assertEquals("God Knows", firstWeight.getSong().getSuppliedTitle());
        assertEquals("Knocked Loose", firstWeight.getSong().getSuppliedArtist());

        var secondWeight = weightEntries.get(1);
        assertEquals(2, secondWeight.getWeightAmount());
        assertEquals(WeightUnit.MULTIPLIER, secondWeight.getWeightUnit());
        assertEquals("https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe", secondWeight.getSong().getSuppliedUrl());

        var thirdWeight = weightEntries.get(2);
        assertEquals(10, thirdWeight.getWeightAmount());
        assertEquals(WeightUnit.PERCENT, thirdWeight.getWeightUnit());
        assertEquals("US", thirdWeight.getSong().getSuppliedTitle());
        assertEquals("Apex Alpha", thirdWeight.getSong().getSuppliedArtist());
    }

    @Test
    void testCollection() {
        var ranProgram = programTester.runProgram("""
                collection c = "My Playlist #59" collection by "rubbaboy"
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var collectionSymbol = symbols.lookup("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getSuppliedName());
        assertEquals("rubbaboy", collection.getSuppliedCreator());
        assertEquals(CollectionOrder.SEQUENTIAL, collection.getOrder());
        assertNull(collection.getWeights());
    }

    @Test
    void testCollectionWithOrder() {
        var ranProgram = programTester.runProgram("""
                collection c = "My Playlist #59" collection by "rubbaboy" order[shuffle]
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var collectionSymbol = symbols.lookup("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();

        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getSuppliedName());
        assertEquals("rubbaboy", collection.getSuppliedCreator());
        assertEquals(CollectionOrder.SHUFFLE, collection.getOrder());
        assertNull(collection.getWeights());
    }

    @Test
    void testCollectionWithWeights() {
        var ranProgram = programTester.runProgram("""
                weights w = emptyWeights()
                
                collection c = "My Playlist #59" collection by "rubbaboy" weights[w]
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var weights = (WeightsType) symbols.lookup("w").getValue();

        var collectionSymbol = symbols.lookup("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getSuppliedName());
        assertEquals("rubbaboy", collection.getSuppliedCreator());
        assertEquals(CollectionOrder.SEQUENTIAL, collection.getOrder());
        assertEquals(weights, collection.getWeights());
    }

    @Test
    void testCollectionWithWeightsFunction() {
        var ranProgram = programTester.runProgram("""
                collection c = "My Playlist #59" collection by "rubbaboy" weights[emptyWeights()]
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var collectionSymbol = symbols.lookup("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getSuppliedName());
        assertEquals("rubbaboy", collection.getSuppliedCreator());
        assertEquals(CollectionOrder.SEQUENTIAL, collection.getOrder());
        assertEquals(0, collection.getWeights().getWeightEntries().size());
    }

    @Test
    void testCollectionWithWeightsAndOrder() {
        var ranProgram = programTester.runProgram("""
                weights w = emptyWeights()
                
                collection c = "My Playlist #59" collection by "rubbaboy" order[shuffle] weights[w]
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var weights = (WeightsType) symbols.lookup("w").getValue();

        var collectionSymbol = symbols.lookup("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getSuppliedName());
        assertEquals("rubbaboy", collection.getSuppliedCreator());
        assertEquals(CollectionOrder.SHUFFLE, collection.getOrder());
        assertEquals(weights, collection.getWeights());
    }

    @Test
    void testList() {
        var ranProgram = programTester.runProgram("""
                int[] i = [1, 2, 3]
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var listSymbol = symbols.lookup("i");
        assertEquals(QilletniTypeClass.LIST, listSymbol.getType());
        
        var list = (ListType) listSymbol.getValue();
        assertEquals(QilletniTypeClass.INT, list.getSubType());
        
        var items = list.getItems().toArray(QilletniType[]::new);
        assertEquals(3, items.length);
        assertArrayEquals(new QilletniType[] {new IntType(1), new IntType(2), new IntType(3)}, items);
    }

    @Test
    void testEmptyList() {
        var ranProgram = programTester.runProgram("""
                int[] i = []
                """);

        var symbols = ranProgram.symbolTable().currentScope();

        var listSymbol = symbols.lookup("i");
        assertEquals(QilletniTypeClass.LIST, listSymbol.getType());
        
        var list = (ListType) listSymbol.getValue();
        assertEquals(QilletniTypeClass.INT, list.getSubType());
        
        assertFalse(list instanceof TypelessListType);
        assertEquals(Collections.emptyList(), list.getItems());
    }
    
    public static class TypeTestFunctions {
        public static WeightsType emptyWeights() {
            return new WeightsType(Collections.emptyList());
        }
    }
    
}
