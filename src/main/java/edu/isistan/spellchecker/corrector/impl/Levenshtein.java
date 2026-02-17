package edu.isistan.spellchecker.corrector.impl;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 *
 * Un corrector inteligente que utiliza "edit distance" para generar correcciones.
 * 
 * La distancia de Levenshtein es el nmero minimo de ediciones que se deber
 * realizar a un string para igualarlo a otro. Por edicin se entiende:
 * <ul>
 * <li> insertar una letra
 * <li> borrar una letra
 * <li> cambiar una letra
 * </ul>
 *
 * Una "letra" es un caracter a-z (no contar los apostrofes).
 * Intercambiar letras (thsi -> this) <it>no</it> cuenta como una edicin.
 * <p>
 * Este corrector sugiere palabras que esten a edit distance uno.
 */
public class Levenshtein extends Corrector {
	private Dictionary dict;
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * Construye un Levenshtein Corrector usando un Dictionary.
	 * Debe arrojar <code>IllegalArgumentException</code> si el diccionario es null.
	 *
	 * @param dict
	 */
	public Levenshtein(Dictionary dict) {
		if (dict == null) {
			throw new IllegalArgumentException("Dictionary cannot be null");
		}
		this.dict = dict;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a erase distance uno
	 */
	public Set<String> getDeletions(String s) {
		Set<String> deletions = new HashSet<String>();
		for (int i = 0; i < s.length(); i++) {
			// Solo considerar letras (a-z), no apostrofes
			char c = s.charAt(i);
			if (c >= 'a' && c <= 'z') {
				String deletion = s.substring(0, i) + s.substring(i + 1);
				if (dict.isWord(deletion)) {
					deletions.add(deletion);
				}
			}
		}
		return deletions;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a substitution distance uno
	 */
	public Set<String> getSubstitutions(String s) {
		Set<String> substitutions = new HashSet<String>();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// Solo sustituir letras (a-z), no apostrofes
			if (c >= 'a' && c <= 'z') {
				for (char replacement : ALPHABET.toCharArray()) {
					if (replacement != c) {
						String substitution = s.substring(0, i) + replacement + s.substring(i + 1);
						if (dict.isWord(substitution)) {
							substitutions.add(substitution);
						}
					}
				}
			}
		}
		return substitutions;
	}


	/**
	 * @param s palabra
	 * @return todas las palabras a insert distance uno
	 */
	public Set<String> getInsertions(String s) {
		Set<String> insertions = new HashSet<String>();
		// Insertar en cada posición (incluyendo al inicio y al final)
		for (int i = 0; i <= s.length(); i++) {
			for (char letter : ALPHABET.toCharArray()) {
				String insertion = s.substring(0, i) + letter + s.substring(i);
				if (dict.isWord(insertion)) {
					insertions.add(insertion);
				}
			}
		}
		return insertions;
	}

	public Set<String> getCorrections(String wrong) {
		if (wrong == null || !TokenScanner.isWord(wrong)) {
			throw new IllegalArgumentException("Input is not a valid word");
		}
		
		// Convertir a minúsculas para procesamiento
		String lowerWrong = wrong.toLowerCase();
		
		Set<String> allCorrections = new HashSet<String>();
		
		// Obtener todas las correcciones posibles
		allCorrections.addAll(getDeletions(lowerWrong));
		allCorrections.addAll(getSubstitutions(lowerWrong));
		allCorrections.addAll(getInsertions(lowerWrong));
		
		// Aplicar matchCase para mantener la capitalización correcta
		return matchCase(wrong, allCorrections);
	}
}
