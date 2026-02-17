package edu.isistan.spellchecker.corrector.impl;

import java.util.Set;
import java.util.TreeSet;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * Este corrector sugiere correciones cuando dos letras adyacentes han sido cambiadas.
 * <p>
 * Un error comn es cambiar las letras de orden, e.g.
 * "with" -> "wiht". Este corrector intenta dectectar palabras con exactamente un swap.
 * <p>
 * Por ejemplo, si la palabra mal escrita es "haet", se debe sugerir
 * tanto "heat" como "hate".
 * <p>
 * Solo cambio de letras contiguas se considera como swap.
 */
public class SwapCorrector extends Corrector {
	private Dictionary dict;

	/**
	 * Construcye el SwapCorrector usando un Dictionary.
	 *
	 * @param dict 
	 * @throws IllegalArgumentException si el diccionario provisto es null
	 */
	public SwapCorrector(Dictionary dict) {
		if (dict == null) {
			throw new IllegalArgumentException("Dictionary cannot be null");
		}
		this.dict = dict;
	}

	/**
	 * 
	 * Este corrector sugiere correciones cuando dos letras adyacentes han sido cambiadas.
	 * <p>
	 * Un error comn es cambiar las letras de orden, e.g.
	 * "with" -> "wiht". Este corrector intenta dectectar palabras con exactamente un swap.
	 * <p>
	 * Por ejemplo, si la palabra mal escrita es "haet", se debe sugerir
	 * tanto "heat" como "hate".
	 * <p>
	 * Solo cambio de letras contiguas se considera como swap.
	 * <p>
	 * Ver superclase.
	 *
	 * @param wrong 
	 * @return retorna un conjunto (potencialmente vaco) de sugerencias.
	 * @throws IllegalArgumentException si la entrada no es una palabra vlida 
	 */
	public Set<String> getCorrections(String wrong) {
		if (!TokenScanner.isWord(wrong)) {
			throw new IllegalArgumentException("Input is not a valid word");
		}
		
		Set<String> suggestions = new TreeSet<String>();
		
		// Generar todas las posibles palabras con un swap de letras adyacentes
		for (int i = 0; i < wrong.length() - 1; i++) {
			// Crear palabra con swap en posición i e i+1
			String swapped = swapAdjacent(wrong, i);
			
			// Verificar si la palabra está en el diccionario
			if (dict.isWord(swapped) && !swapped.equals(wrong)) {
				suggestions.add(swapped);
			}
		}
		
		// Aplicar matchCase para mantener la capitalización correcta
		return matchCase(wrong, suggestions);
	}
	
	/**
	 * Intercambia dos caracteres adyacentes en una palabra
	 * @param word palabra original
	 * @param index índice del primer carácter a intercambiar
	 * @return palabra con los caracteres intercambiados
	 */
	private String swapAdjacent(String word, int index) {
		if (index < 0 || index >= word.length() - 1) {
			return word;
		}
		char[] chars = word.toCharArray();
		char temp = chars[index];
		chars[index] = chars[index + 1];
		chars[index + 1] = temp;
		return new String(chars);
	}
}
