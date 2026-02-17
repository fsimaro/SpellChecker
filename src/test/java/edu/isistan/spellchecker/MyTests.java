package edu.isistan.spellchecker;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

import edu.isistan.spellchecker.tokenizer.TokenScanner;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;

/** Cree sus propios tests. */
public class MyTests {

	// ========== Tests para TokenScanner ==========
	
	@Test
	public void testTokenScannerEmptyInput() throws IOException {
		Reader in = new StringReader("");
		TokenScanner ts = new TokenScanner(in);
		assertFalse("Empty input should have no tokens", ts.hasNext());
		try {
			ts.next();
			fail("Should throw NoSuchElementException");
		} catch (java.util.NoSuchElementException e) {
			// Expected
		}
		in.close();
	}

	@Test
	public void testTokenScannerSingleWord() throws IOException {
		Reader in = new StringReader("hello");
		TokenScanner ts = new TokenScanner(in);
		assertTrue("Should have a token", ts.hasNext());
		assertEquals("hello", ts.next());
		assertFalse("Should have no more tokens", ts.hasNext());
		in.close();
	}

	@Test
	public void testTokenScannerSingleNonWord() throws IOException {
		Reader in = new StringReader("!");
		TokenScanner ts = new TokenScanner(in);
		assertTrue("Should have a token", ts.hasNext());
		assertEquals("!", ts.next());
		assertFalse("Should have no more tokens", ts.hasNext());
		in.close();
	}

	@Test
	public void testTokenScannerMixedEndsWithWord() throws IOException {
		Reader in = new StringReader("Hello, world");
		TokenScanner ts = new TokenScanner(in);
		assertEquals("Hello", ts.next());
		assertEquals(", ", ts.next());
		assertEquals("world", ts.next());
		assertFalse("Should have no more tokens", ts.hasNext());
		in.close();
	}

	@Test
	public void testTokenScannerMixedEndsWithNonWord() throws IOException {
		Reader in = new StringReader("Hello, world!");
		TokenScanner ts = new TokenScanner(in);
		assertEquals("Hello", ts.next());
		assertEquals(", ", ts.next());
		assertEquals("world", ts.next());
		assertEquals("!", ts.next());
		assertFalse("Should have no more tokens", ts.hasNext());
		in.close();
	}

	// ========== Tests para Dictionary ==========

	@Test
	public void testDictionaryWordExists() throws IOException {
		// Crear un diccionario simple
		Reader in = new StringReader("apple banana cherry");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		assertTrue("'apple' should be in dictionary", dict.isWord("apple"));
		assertTrue("'banana' should be in dictionary", dict.isWord("banana"));
		in.close();
	}

	@Test
	public void testDictionaryWordExistsWithTrie() throws IOException {
		// Crear un diccionario simple
		Reader in = new StringReader("apple banana cherry");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		assertTrue("'apple' should be in dictionary", dict.isWord("apple"));
		assertTrue("'banana' should be in dictionary", dict.isWord("banana"));
		in.close();
	}

	@Test
	public void testDictionaryWordNotExists() throws IOException {
		Reader in = new StringReader("apple banana cherry");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		assertFalse("'orange' should not be in dictionary", dict.isWord("orange"));
		assertFalse("'grape' should not be in dictionary", dict.isWord("grape"));
		in.close();
	}

	@Test
	public void testDictionaryWordNotExistsWithTrie() throws IOException {
		Reader in = new StringReader("apple banana cherry");
		Dictionary dict = new Dictionary(new TokenScanner(in), true);
		assertFalse("'orange' should not be in dictionary", dict.isWord("orange"));
		assertFalse("'grape' should not be in dictionary", dict.isWord("grape"));
		in.close();
	}

	@Test
	public void testDictionaryNumWords() throws IOException {
		Reader in = new StringReader("apple banana cherry");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		assertEquals("Should have 3 words", 3, dict.getNumWords());
		in.close();
	}

	@Test
	public void testDictionaryNumWordsWithTrie() throws IOException {
		Reader in = new StringReader("apple banana cherry");
		Dictionary dict = new Dictionary(new TokenScanner(in), true);
		assertEquals("Should have 3 words", 3, dict.getNumWords());
		in.close();
	}

	@Test
	public void testDictionaryEmptyString() throws IOException {
		Reader in = new StringReader("apple banana");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		assertFalse("Empty string should not be a word", dict.isWord(""));
		in.close();
	}

	@Test
	public void testDictionaryEmptyStringWithTrie() throws IOException {
		Reader in = new StringReader("apple banana");
		Dictionary dict = new Dictionary(new TokenScanner(in), true);
		assertFalse("Empty string should not be a word", dict.isWord(""));
		in.close();
	}

	@Test
	public void testDictionaryCaseInsensitive() throws IOException {
		Reader in = new StringReader("Apple");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		assertTrue("'apple' should be found (case insensitive)", dict.isWord("apple"));
		assertTrue("'APPLE' should be found (case insensitive)", dict.isWord("APPLE"));
		assertTrue("'ApPlE' should be found (case insensitive)", dict.isWord("ApPlE"));
		in.close();
	}

	@Test
	public void testDictionaryCaseInsensitiveWithTrie() throws IOException {
		Reader in = new StringReader("Apple");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		assertTrue("'apple' should be found (case insensitive)", dict.isWord("apple"));
		assertTrue("'APPLE' should be found (case insensitive)", dict.isWord("APPLE"));
		assertTrue("'ApPlE' should be found (case insensitive)", dict.isWord("ApPlE"));
		in.close();
	}

	// ========== Tests para FileCorrector ==========

	@Test
	public void testFileCorrectorWithExtraSpaces() throws IOException, FileCorrector.FormatException {
		// Crear un archivo temporal con espacios extras
		File tempFile = File.createTempFile("testCorrections", ".txt");
		tempFile.deleteOnExit();
		
		PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		pw.println("   wrong,correct   ");
		pw.println("  misspelled ,  correction  ");
		pw.println("test,result");
		pw.close();
		
		FileCorrector fc = FileCorrector.make(tempFile.getAbsolutePath());
		Set<String> corrections = fc.getCorrections("wrong");
		assertTrue("Should contain 'correct'", corrections.contains("correct"));
		
		corrections = fc.getCorrections("misspelled");
		assertTrue("Should contain 'correction'", corrections.contains("correction"));
	}

	@Test
	public void testFileCorrectorNoCorrections() throws IOException, FileCorrector.FormatException {
		File tempFile = File.createTempFile("testCorrections", ".txt");
		tempFile.deleteOnExit();
		
		PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		pw.println("wrong,correct");
		pw.close();
		
		FileCorrector fc = FileCorrector.make(tempFile.getAbsolutePath());
		Set<String> corrections = fc.getCorrections("nonexistent");
		assertTrue("Should return empty set", corrections.isEmpty());
	}

	@Test
	public void testFileCorrectorMultipleCorrections() throws IOException, FileCorrector.FormatException {
		File tempFile = File.createTempFile("testCorrections", ".txt");
		tempFile.deleteOnExit();
		
		PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		pw.println("wrong,correction1");
		pw.println("wrong,correction2");
		pw.println("wrong,correction3");
		pw.close();
		
		FileCorrector fc = FileCorrector.make(tempFile.getAbsolutePath());
		Set<String> corrections = fc.getCorrections("wrong");
		assertEquals("Should have 3 corrections", 3, corrections.size());
		assertTrue("Should contain correction1", corrections.contains("correction1"));
		assertTrue("Should contain correction2", corrections.contains("correction2"));
		assertTrue("Should contain correction3", corrections.contains("correction3"));
	}

	@Test
	public void testFileCorrectorCaseVariations() throws IOException, FileCorrector.FormatException {
		File tempFile = File.createTempFile("testCorrections", ".txt");
		tempFile.deleteOnExit();
		
		PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		pw.println("palabra,correction");
		pw.close();
		
		FileCorrector fc = FileCorrector.make(tempFile.getAbsolutePath());
		
		// Todas estas variaciones deberían encontrar la misma corrección
		Set<String> corrections1 = fc.getCorrections("PaLaBra");
		Set<String> corrections2 = fc.getCorrections("palABRa");
		Set<String> corrections3 = fc.getCorrections("palabra");
		Set<String> corrections4 = fc.getCorrections("PALABRA");
		
		assertTrue("PaLaBar should find correction", corrections1.contains("Correction"));
		assertTrue("palABAR should find correction", corrections2.contains("correction"));
		assertTrue("palabra should find correction", corrections3.contains("correction"));
		assertTrue("PALABAR should find correction", corrections4.contains("Correction"));
	}

	// ========== Tests para SwapCorrector ==========

	@Test
	public void testSwapCorrectorNullDictionary() {
		try {
			new SwapCorrector(null);
			fail("Should throw IllegalArgumentException for null dictionary");
		} catch (IllegalArgumentException e) {
			// Expected
		}
	}

	@Test
	public void testSwapCorrectorWordInDictionary() throws IOException {
		Reader in = new StringReader("correct");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		SwapCorrector swap = new SwapCorrector(dict);
		
		// "correct" está en el diccionario, no debería sugerir nada
		Set<String> corrections = swap.getCorrections("correct");
		assertTrue("Word in dictionary should return empty corrections", corrections.isEmpty());
		in.close();
	}

	@Test
	public void testSwapCorrectorCaseVariations() throws IOException {
		Reader in = new StringReader("correct");
		Dictionary dict = new Dictionary(new TokenScanner(in));
		SwapCorrector swap = new SwapCorrector(dict);
		
		// "ocorrect" con swap debería dar "correct"
		Set<String> corrections1 = swap.getCorrections("corerct");
		assertTrue("Should find 'correct'", corrections1.contains("correct"));

		// Probar con distintas capitalizaciones
		Set<String> corrections2 = swap.getCorrections("Corerct");
		assertTrue("Should find 'Correct'", corrections2.contains("Correct"));

		in.close();
	}
}
