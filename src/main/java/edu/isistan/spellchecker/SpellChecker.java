package edu.isistan.spellchecker;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * El SpellChecker usa un Dictionary, un Corrector, and I/O para chequear
 * de forma interactiva un stream de texto. Despues escribe la salida corregida
 * en un stream de salida. Los streams pueden ser archivos, sockets, o cualquier
 * otro stream de Java.
 * <p>
 * Nota:
 * <ul>
 * <li> La implementación provista provee métodos utiles para implementar el SpellChecker.
 * <li> Toda la salida al usuario deben enviarse a System.out (salida estandar)
 * </ul>
 * <p>
 * El SpellChecker es usado por el SpellCkecherRunner. Ver:
 * @see SpellCheckerRunner
 */
public class SpellChecker {
	private Corrector corr;
	private Dictionary dict;

	/**
	 * Constructor del SpellChecker
	 * 
	 * @param c un Corrector
	 * @param d un Dictionary
	 */
	public SpellChecker(Corrector c, Dictionary d) {
		corr = c;
		dict = d;
	}

	/**
	 * Returna un entero desde el Scanner provisto. El entero estará en el rango [min, max].
	 * Si no se ingresa un entero o este está fuera de rango, repreguntará.
	 *
	 * @param min
	 * @param max
	 * @param sc
	 */
	private int getNextInt(int min, int max, Scanner sc) {
		while (true) {
			try {
				String input = sc.next().replace("\uFEFF", "").trim();
				int choice = Integer.parseInt(input);
				if (choice >= min && choice <= max) {
					return choice;
				}
			} catch (NumberFormatException ex) {
				System.out.println(ex.getMessage());
				// Was not a number. Ignore and prompt again.
			}
			System.out.println("Entrada invalida. Pruebe de nuevo!");
		}
	}

	/**
	 * Retorna el siguiente String del Scanner.
	 *
	 * @param sc
	 */
	private String getNextString(Scanner sc) {
		return sc.next();
	}



	/**
	 * checkDocument interactivamente chequea un archivo de texto..  
	 * Internamente, debe usar un TokenScanner para parsear el documento.  
	 * Tokens de tipo palabra que no se encuentran en el diccionario deben ser corregidos
	 * ; otros tokens deben insertarse tal cual en en documento de salida.
	 * <p>
	 *
	 * @param in stream donde se encuentra el documento de entrada.
	 * @param input entrada interactiva del usuario. Por ejemplo, entrada estandar System.in
	 * @param out stream donde se escribe el documento de salida.
	 * @throws IOException si se produce algún error leyendo el documento.
	 */
	public void checkDocument(Reader in, InputStream input, Writer out) throws IOException {
		Scanner sc = new Scanner(input);
		TokenScanner tokenScanner = new TokenScanner(in);
		PrintWriter pw = new PrintWriter(out);
		
		while (tokenScanner.hasNext()) {
			String token = tokenScanner.next();
			
			if (TokenScanner.isWord(token)) {
				// Es una palabra
				if (dict.isWord(token)) {
					// Palabra correcta, escribirla tal cual
					pw.print(token);
				} else {
					// Palabra incorrecta, obtener sugerencias
					Set<String> suggestions = corr.getCorrections(token);

					System.out.println("Palabra no encontrada: " + token);
					System.out.println("0. Ignorar y continuar");
					System.out.println("1. Reemplazar con otra palabra manualmente");

					List<String> suggestionList = new LinkedList<String>(suggestions);

					for (int i = 0; i < suggestionList.size(); i++) {
						System.out.println((i + 2) + ". Reemplazar con " + suggestionList.get(i));
					}

					System.out.println();

					int choice = getNextInt(0, suggestionList.size() + 1, sc);
					if (choice == 0) {
						// Ignorar - escribir palabra original
						pw.print(token);
					} else if (choice == 1) {
						// Reemplazar manualmente
						System.out.println("Ingrese la palabra de reemplazo:");
						String replacement = getNextString(sc);
						System.out.println(replacement + '\n');
						pw.print(replacement);
					} else if (choice > 1 && choice <= suggestionList.size() + 1){
						// Usuario eligió una sugerencia
						pw.print(suggestionList.get(choice - 2));
					}
				}
			} else {
				// No es una palabra, escribir tal cual
				pw.print(token);
			}
		}
		pw.flush();
	}
}
