package edu.isistan.spellchecker.corrector.impl;

import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

import java.io.*;

/**
 * Corrector basado en un archivo.
 * 
 */
public class FileCorrector extends Corrector {
	private Map<String, Set<String>> corrections;

	/** Clase especial que se utiliza al tener 
	 * algún error de formato en el archivo de entrada.
	 */
	public static class FormatException extends Exception {
		public FormatException(String msg) {
			super(msg);
		}
	}


	/**
	 * Constructor del FileReader
	 *
	 * Utilice un BufferedReader para leer el archivo de definición
	 *
	 * <p> 
	 * Cada línea del archivo del diccionario tiene el siguiente formato:
	 * misspelled_word,corrected_version
	 *
	 * <p>
	 *Ejemplo:<br>
	 * <pre>
	 * aligatur,alligator<br>
	 * baloon,balloon<br>
	 * inspite,in spite<br>
	 * who'ev,who've<br>
	 * ther,their<br>
	 * ther,there<br>
	 * </pre>
	 * <p>
	 * Estas líneas no son case-insensitive, por lo que todas deberáan generar el mismo efecto:<br>
	 * <pre>
	 * baloon,balloon<br>
	 * Baloon,balloon<br>
	 * Baloon,Balloon<br>
	 * BALOON,balloon<br>
	 * bAlOon,BALLOON<br>
	 * </pre>
	 * <p>
	 * Debe ignorar todos los espacios vacios alrededor de las palabras, por lo
	 * que estas entradas son todas equivalentes:<br>
	 * <pre>
	 * inspite,in spite<br>
	 *    inspite,in spite<br>
	 * inspite   ,in spite<br>
	 *  inspite ,   in spite  <br>
	 * </pre>
	 * Los espacios son permitidos dentro de las sugerencias. 
	 *
	 * <p>
	 * Debería arrojar <code>FileCorrector.FormatException</code> si se encuentra algún
	 * error de formato:<br>
	 * <pre>
	 * ,correct<br>
	 * wrong,<br>
	 * wrong correct<br>
	 * wrong,correct,<br>
	 * </pre>
	 * <p>
	 *
	 * @param r Secuencia de caracteres 
	 * @throws IOException error leyendo el archivo
	 * @throws FileCorrector.FormatException error de formato
	 * @throws IllegalArgumentException reader es null
	 */
	public FileCorrector(Reader r) throws IOException, FormatException {
		if (r == null) {
			throw new IllegalArgumentException("Reader cannot be null");
		}
		
		this.corrections = new HashMap<>();
		BufferedReader br = new BufferedReader(r);
		String line;
		boolean isFirstLine = true; // Flag para detectar el inicio
		
		while ((line = br.readLine()) != null) {

			if (isFirstLine) {
				if (line.startsWith("\uFEFF")) {
					line = line.replace("\uFEFF", "");
				}
				isFirstLine = false;
			}

			line = line.trim();
			if (line.isEmpty()) {
				continue; // Ignorar líneas vacías
			}
			
			// Buscar la coma
			int commaIndex = line.indexOf(',');
			if (commaIndex == -1) {
				throw new FormatException("Line missing comma: " + line);
			}
			
			String wrong = line.substring(0, commaIndex).trim();
			String correct = line.substring(commaIndex + 1).trim();
			
			// Validar formato
			if (wrong.isEmpty()) {
				throw new FormatException("Empty misspelled word before comma");
			}
			if (correct.isEmpty()) {
				throw new FormatException("Empty correction after comma");
			}
			
			// Verificar que no haya más comas después de la primera
			if (line.indexOf(',', commaIndex + 1) != -1) {
				throw new FormatException("Multiple commas in line: " + line);
			}
			
			// Convertir a minúsculas para la clave (case-insensitive)
			String wrongKey = wrong.toLowerCase();
			
			// Agregar corrección
			if (!corrections.containsKey(wrongKey)) {
				corrections.put(wrongKey, new TreeSet<String>());
			}
			corrections.get(wrongKey).add(correct);
		}
	}

	/** Construye el Filereader.
	 *
	 * @param filename 
	 * @throws IOException 
	 * @throws FileCorrector.FormatException 
	 * @throws FileNotFoundException 
	 */
	public static FileCorrector make(String filename) throws IOException, FormatException {
		Reader r = new FileReader(filename);
		FileCorrector fc;
		try {
			fc = new FileCorrector(r);
		} finally {
			if (r != null) { r.close(); }
		}
		return fc;
	}

	/**
	 * Retorna una lista de correcciones para una palabra dada.
	 * Si la palabra mal escrita no está en el diccionario el set es vacio.
	 * <p>
	 * Ver superclase.
	 *
	 * @param wrong 
	 * @return retorna un conjunto (potencialmente vacío) de sugerencias.
	 * @throws IllegalArgumentException si la entrada no es una palabra válida
	 */
	public Set<String> getCorrections(String wrong) {
		if (!TokenScanner.isWord(wrong)) {
			throw new IllegalArgumentException("Input is not a valid word");
		}
		
		String wrongKey = wrong.toLowerCase();
		Set<String> result = corrections.get(wrongKey);

		return (result == null || result.isEmpty()) ? new TreeSet<>() : matchCase(wrong, result);
	}

}
