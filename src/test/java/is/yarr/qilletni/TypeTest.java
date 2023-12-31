package is.yarr.qilletni;

import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.StringType;
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
        var runner = programTester.runProgram("""
                boolean t = true
                boolean f = false
                """);
        
        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(2, symbols.size());
        
        var t = symbols.get("t");
        assertEquals(QilletniTypeClass.BOOLEAN, t.getType());
        assertTrue(((BooleanType) t.getValue()).getValue());
        
        var f = symbols.get("f");
        assertEquals(QilletniTypeClass.BOOLEAN, f.getType());
        assertFalse(((BooleanType) f.getValue()).getValue());
    }
    
    @Test
    void testInt() {
        var runner = programTester.runProgram("""
                int i = 42
                """);
        
        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());
        
        var i = symbols.get("i");
        assertEquals(QilletniTypeClass.INT, i.getType());
        assertEquals(42, ((IntType) i.getValue()).getValue());
    }
    
    @Test
    void testString() {
        var runner = programTester.runProgram("""
                string s = "foo"
                """);
        
        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());
        
        var s = symbols.get("s");
        assertEquals(QilletniTypeClass.STRING, s.getType());
        assertEquals("foo", ((StringType) s.getValue()).getValue());
    }

    @Test
    void testSongWithNameAndArtist() {
        var runner = programTester.runProgram("""
                song s = "God Knows" by "Knocked Loose"
                """);

        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var songSymbol = symbols.get("s");
        assertEquals(QilletniTypeClass.SONG, songSymbol.getType());
        var song = (SongType) songSymbol.getValue();
        
        assertEquals("God Knows", song.getTitle());
        assertEquals("Knocked Loose", song.getArtist());
        assertNull(song.getUrl());
    }

    @Test
    void testSongWithUrl() {
        var runner = programTester.runProgram("""
                song s = "https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe"
                """);

        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var songSymbol = symbols.get("s");
        assertEquals(QilletniTypeClass.SONG, songSymbol.getType());
        var song = (SongType) songSymbol.getValue();
        
        assertNull(song.getTitle());
        assertNull(song.getArtist());
        assertEquals("https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe", song.getUrl());
    }
    
    @Test
    void testWeights() {
        var runner = programTester.runProgram("""
                weights w =
                    | 3x "God Knows" by "Knocked Loose"
                    | 2x "https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe"
                    | 10% "US" by "Apex Alpha"
                """);
        
        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());
        
        var weightsSymbol = symbols.get("w");
        assertEquals(QilletniTypeClass.WEIGHTS, weightsSymbol.getType());
        var weights = (WeightsType) weightsSymbol.getValue();
        
        var weightEntries = weights.getWeightEntries();
        assertEquals(3, weightEntries.size());

        var firstWeight = weightEntries.get(0);
        assertEquals(3, firstWeight.getWeightAmount());
        assertEquals(WeightUnit.MULTIPLIER, firstWeight.getWeightUnit());
        assertEquals("God Knows", firstWeight.getSong().getTitle());
        assertEquals("Knocked Loose", firstWeight.getSong().getArtist());

        var secondWeight = weightEntries.get(1);
        assertEquals(2, secondWeight.getWeightAmount());
        assertEquals(WeightUnit.MULTIPLIER, secondWeight.getWeightUnit());
        assertEquals("https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe", secondWeight.getSong().getUrl());

        var thirdWeight = weightEntries.get(2);
        assertEquals(10, thirdWeight.getWeightAmount());
        assertEquals(WeightUnit.PERCENT, thirdWeight.getWeightUnit());
        assertEquals("US", thirdWeight.getSong().getTitle());
        assertEquals("Apex Alpha", thirdWeight.getSong().getArtist());
    }

    @Test
    void testCollection() {
        var runner = programTester.runProgram("""
                collection c = "My Playlist #59" created by "rubbaboy"
                """);

        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var collectionSymbol = symbols.get("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getName());
        assertEquals("rubbaboy", collection.getCreator());
        assertEquals(CollectionOrder.SEQUENTIAL, collection.getOrder());
        assertNull(collection.getWeights());
    }

    @Test
    void testCollectionWithOrder() {
        var runner = programTester.runProgram("""
                collection c = "My Playlist #59" created by "rubbaboy" order[shuffle]
                """);

        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var collectionSymbol = symbols.get("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();

        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getName());
        assertEquals("rubbaboy", collection.getCreator());
        assertEquals(CollectionOrder.SHUFFLE, collection.getOrder());
        assertNull(collection.getWeights());
    }

    @Test
    void testCollectionWithWeights() {
        var runner = programTester.runProgram("""
                weights w = emptyWeights()
                
                collection c = "My Playlist #59" created by "rubbaboy" weights[w]
                """);

        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(2, symbols.size());

        var weights = (WeightsType) symbols.get("w").getValue();

        var collectionSymbol = symbols.get("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getName());
        assertEquals("rubbaboy", collection.getCreator());
        assertEquals(CollectionOrder.SEQUENTIAL, collection.getOrder());
        assertEquals(weights, collection.getWeights());
    }

    @Test
    void testCollectionWithWeightsFunction() {
        var runner = programTester.runProgram("""
                collection c = "My Playlist #59" created by "rubbaboy" weights[emptyWeights()]
                """);

        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(1, symbols.size());

        var collectionSymbol = symbols.get("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getName());
        assertEquals("rubbaboy", collection.getCreator());
        assertEquals(CollectionOrder.SEQUENTIAL, collection.getOrder());
        assertEquals(0, collection.getWeights().getWeightEntries().size());
    }

    @Test
    void testCollectionWithWeightsAndOrder() {
        var runner = programTester.runProgram("""
                weights w = emptyWeights()
                
                collection c = "My Playlist #59" created by "rubbaboy" order[shuffle] weights[w]
                """);

        var symbols = runner.getSymbolTable().currentScope().getAllSymbols();
        assertEquals(2, symbols.size());

        var weights = (WeightsType) symbols.get("w").getValue();

        var collectionSymbol = symbols.get("c");
        assertEquals(QilletniTypeClass.COLLECTION, collectionSymbol.getType());
        var collection = (CollectionType) collectionSymbol.getValue();
        
        assertEquals(CollectionDefinition.NAME_CREATOR, collection.getCollectionDefinition());
        assertEquals("My Playlist #59", collection.getName());
        assertEquals("rubbaboy", collection.getCreator());
        assertEquals(CollectionOrder.SHUFFLE, collection.getOrder());
        assertEquals(weights, collection.getWeights());
    }
    
    public static class TypeTestFunctions {
        public static WeightsType emptyWeights() {
            return new WeightsType(Collections.emptyList());
        }
    }
    
}
