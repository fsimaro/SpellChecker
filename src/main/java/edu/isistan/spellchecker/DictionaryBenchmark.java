package edu.isistan.spellchecker;

import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks de performance para comparar HashSet vs Trie en Dictionary.
 *
 * Este benchmark compara el rendimiento de dos implementaciones del Dictionary:
 * - HashSet: Implementación basada en java.util.HashSet (O(1) promedio)
 * - Trie: Implementación basada en árbol de prefijos (O(m) donde m es la longitud de la palabra)
 *
 * Los benchmarks evalúan:
 * 1. Búsqueda de palabras que están en el diccionario
 * 2. Búsqueda de palabras que NO están en el diccionario
 * 3. Obtención del número de palabras en el diccionario
 *
 * Se ejecutan con tres tamaños de diccionario: small, medium, large
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class DictionaryBenchmark {

    @Param({"small", "medium", "large"})
    public String dictionarySize;

    private Dictionary hashSetDict;
    private Dictionary trieDict;
    private List<String> wordsInDictionary;
    private List<String> wordsNotInDictionary;
    private Random random;

    @Setup
    public void setup() throws IOException {
        random = new Random(42); // Semilla fija para reproducibilidad

        // Determinar archivo de diccionario según tamaño
        String dictFile;
        switch (dictionarySize) {
            case "small":
                dictFile = "smallDictionary.txt";
                break;
            case "medium":
                dictFile = "mediumDictionary.txt";
                break;
            case "large":
                dictFile = "dictionary.txt";
                break;
            default:
                dictFile = "smallDictionary.txt";
        }

        // Verificar que el archivo existe
        File file = new File(dictFile);
        if (!file.exists()) {
            throw new FileNotFoundException("Dictionary file not found: " + dictFile);
        }

        // Crear diccionarios
        Reader reader1 = new FileReader(dictFile);
        hashSetDict = new Dictionary(new TokenScanner(reader1), false);
        reader1.close();

        Reader reader2 = new FileReader(dictFile);
        trieDict = new Dictionary(new TokenScanner(reader2), true);
        reader2.close();

        // Preparar listas de palabras para testing
        wordsInDictionary = new ArrayList<>();
        wordsNotInDictionary = new ArrayList<>();

        // Leer palabras del diccionario
        Reader reader3 = new FileReader(dictFile);
        TokenScanner ts = new TokenScanner(reader3);
        while (ts.hasNext()) {
            String token = ts.next();
            if (edu.isistan.spellchecker.tokenizer.TokenScanner.isWord(token)) {
                String lowerWord = token.toLowerCase();
                if (!wordsInDictionary.contains(lowerWord)) {
                    wordsInDictionary.add(lowerWord);
                }
            }
        }
        reader3.close();

        // Generar palabras que NO están en el diccionario
        int numNonExistent = Math.min(1000, wordsInDictionary.size() * 2);
        generateNonExistentWords(wordsInDictionary, wordsNotInDictionary, numNonExistent);
    }

    /**
     * Genera palabras que no están en el diccionario
     */
    private void generateNonExistentWords(List<String> existent, List<String> nonExistent, int count) {
        int generated = 0;
        while (generated < count) {
            // Generar palabra aleatoria
            int length = 3 + random.nextInt(8); // Longitud entre 3 y 10
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append((char) ('a' + random.nextInt(26)));
            }
            String word = sb.toString();

            // Verificar que no esté en el diccionario y agregar
            if (!hashSetDict.isWord(word) && !nonExistent.contains(word)) {
                nonExistent.add(word);
                generated++;
            }
        }
    }

    /**
     * Benchmark: Búsqueda de palabra existente usando HashSet
     */
    @Benchmark
    public boolean hashSetSearchWordExists() {
        if (wordsInDictionary.isEmpty()) {
            return false;
        }
        String word = wordsInDictionary.get(random.nextInt(wordsInDictionary.size()));
        return hashSetDict.isWord(word);
    }

    /**
     * Benchmark: Búsqueda de palabra inexistente usando HashSet
     */
    @Benchmark
    public boolean hashSetSearchWordNotExists() {
        if (wordsNotInDictionary.isEmpty()) {
            return false;
        }
        String word = wordsNotInDictionary.get(random.nextInt(wordsNotInDictionary.size()));
        return hashSetDict.isWord(word);
    }

    /**
     * Benchmark: Búsqueda de palabra existente usando Trie
     */
    @Benchmark
    public boolean trieSearchWordExists() {
        if (wordsInDictionary.isEmpty()) {
            return false;
        }
        String word = wordsInDictionary.get(random.nextInt(wordsInDictionary.size()));
        return trieDict.isWord(word);
    }

    /**
     * Benchmark: Búsqueda de palabra inexistente usando Trie
     */
    @Benchmark
    public boolean trieSearchWordNotExists() {
        if (wordsNotInDictionary.isEmpty()) {
            return false;
        }
        String word = wordsNotInDictionary.get(random.nextInt(wordsNotInDictionary.size()));
        return trieDict.isWord(word);
    }

    /**
     * Benchmark: Obtener número de palabras usando HashSet
     */
    @Benchmark
    public int hashSetGetNumWords() {
        return hashSetDict.getNumWords();
    }

    /**
     * Benchmark: Obtener número de palabras usando Trie
     */
    @Benchmark
    public int trieGetNumWords() {
        return trieDict.getNumWords();
    }

    /**
     * Método main para ejecutar los benchmarks desde el IDE.
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DictionaryBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

