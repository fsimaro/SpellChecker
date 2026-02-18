package edu.isistan.spellchecker.corrector;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * El diccionario maneja todas las palabras conocidas.
 * El diccionario es case insensitive 
 * 
 * Una palabra "válida" es una secuencia de letras (determinado por Character.isLetter)
 * o apostrofes.
 */
public class Dictionary {
	private Set<String> words;
	private TrieNode trieRoot;
	private boolean useTrie;
	//Variable para contabilizar palabras únicas
	private int size = 0;

	/**
	 * Construye un diccionario usando un TokenScanner
	 * <p>
	 * Una palabra válida es una secuencia de letras (ver Character.isLetter) o apostrofes.
	 * Toda palabra no válida se debe ignorar
	 *
	 * <p>
	 *
	 * @param ts 
	 * @throws IOException Error leyendo el archivo
	 * @throws IllegalArgumentException el TokenScanner es null
	 */
	public Dictionary(TokenScanner ts) throws IOException {
		this(ts, false);
	}

	/**
	 * Construye un diccionario usando un TokenScanner con opción de usar Trie
	 * 
	 * @param ts TokenScanner
	 * @param useTrie si true, usa Trie; si false, usa HashSet
	 * @throws IOException Error leyendo el archivo
	 * @throws IllegalArgumentException el TokenScanner es null
	 */
	public Dictionary(TokenScanner ts, boolean useTrie) throws IOException {
		if (ts == null) {
			throw new IllegalArgumentException("TokenScanner cannot be null");
		}
		this.useTrie = useTrie;
		
		if (useTrie) {
			this.trieRoot = new TrieNode();
		} else {
			this.words = new HashSet<>();
		}
		
		while (ts.hasNext()) {
			String token = ts.next();
			if (TokenScanner.isWord(token)) {
				/* Se aplica String.toLowerCase() dado que es CaseInsensitive */
				String lowerWord = token.toLowerCase();
				if (useTrie) {
					if (trieRoot.insert(lowerWord))
						size++;
				} else {
					if (words.add(lowerWord))
						size++;
				}
			}
		}
	}

	/**
	 * Construye un diccionario usando un archivo.
	 *
	 *
	 * @param filename 
	 * @throws FileNotFoundException si el archivo no existe
	 * @throws IOException Error leyendo el archivo
	 */
	public static Dictionary make(String filename) throws IOException {
		Reader r = new FileReader(filename);
		Dictionary d = new Dictionary(new TokenScanner(r));
		r.close();
		return d;
	}

	/**
	 * Retorna el número de palabras correctas en el diccionario.
	 * Recuerde que como es case insensitive si Dogs y doGs están en el
	 * diccionario, cuentan como una sola palabra.
	 * 
	 * @return número de palabras únicas
	 */
	public int getNumWords() {
		return this.size;
	}

	/**
	 * Testea si una palabra es parte del diccionario. Si la palabra no está en
	 * el diccionario debe retornar false. null debe retornar falso.
	 * Si en el diccionario está la palabra Dog y se pregunta por la palabra dog
	 * debe retornar true, ya que es case insensitive.
	 *
	 *Llamar a este método no debe reabrir el archivo de palabras.
	 *
	 * @param word verifica si la palabra está en el diccionario.
	 * Asuma que todos los espacios en blanco antes y despues de la palabra fueron removidos.
	 * @return si la palabra está en el diccionario.
	 */
	public boolean isWord(String word) {
		if (word == null) {
			return false;
		}
		String lowerWord = word.toLowerCase();
		if (useTrie) {
			return trieRoot.search(lowerWord);
		} else {
			return words.contains(lowerWord);
		}
	}

	/**
	 * Clase interna para implementar un Trie (árbol de prefijos)
	 */
	private static class TrieNode {
		private Map<Character, TrieNode> children;
		private boolean isEndOfWord;

		public TrieNode() {
			this.children = new HashMap<>();
			this.isEndOfWord = false;
		}

		public boolean insert(String word) {
			TrieNode current = this;
			for (char c : word.toCharArray()) {
				// Solo se crea el nodo si realmente existe el carácter
				current.children.putIfAbsent(c, new TrieNode());
				current = current.children.get(c);
			}
			if (current.isEndOfWord) {
				return false; // La palabra ya existía
			}
			current.isEndOfWord = true;
			return true;
		}

		public boolean search(String word) {
			TrieNode current = this;
			for (char c : word.toCharArray()) {
				current = current.children.get(c);
				if (current == null) return false;
			}
			return current.isEndOfWord;
		}
	}
}
