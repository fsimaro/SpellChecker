package edu.isistan.spellchecker.tokenizer;

import java.util.Iterator;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Dado un archivo provee un método para recorrerlo.
 */
public class TokenScanner implements Iterator<String> {
  private java.io.Reader reader;
  private int nextChar;
  private boolean hasNextChar;
  private boolean hasNextToken;

  /**
   * Crea un TokenScanner.
   * <p>
   * Como un iterador, el TokenScanner solo debe leer lo justo y
   * necesario para implementar los métodos next() y hasNext().
   * No se debe leer toda la entrada de una.
   * <p>
   *
   * @param in fuente de entrada
   * @throws IOException si hay algún error leyendo.
   * @throws IllegalArgumentException si el Reader provisto es null
   */
  public TokenScanner(java.io.Reader in) throws IOException {
    if (in == null) {
      throw new IllegalArgumentException("Reader cannot be null");
    }
    this.reader = in;
    this.nextChar = reader.read();
    this.hasNextChar = (nextChar != -1);
    this.hasNextToken = hasNextChar;
  }

  /**
   * Determina si un caracter es una caracter válido para una palabra.
   * <p>
   * Un caracter válido es una letra (
   * Character.isLetter) o una apostrofe '\''.
   *
   * @param c 
   * @return true si es un caracter
   */
  public static boolean isWordCharacter(int c) {
    return c != -1 && (Character.isLetter(c) || c == '\'');
  }


   /**
   * Determina si un string es una palabra válida.
   * Null no es una palabra válida.
   * Un string que todos sus caracteres son válidos es una
   * palabra. Por lo tanto, el string vacío NO es una palabra válida.
   * @param s 
   * @return true si el string es una palabra.
   */
  public static boolean isWord(String s) {
    if (s == null || s.isEmpty()) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      int c = s.charAt(i);
      if (!isWordCharacter(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determina si hay otro token en el reader.
   */
  public boolean hasNext() {
    return hasNextToken;
  }

  /**
   * Retorna el siguiente token.
   *
   * @throws NoSuchElementException cuando se alcanzó el final de stream
   */
  public String next() {
    if (!hasNextToken)
      throw new NoSuchElementException("No more tokens available");

    StringBuilder token = new StringBuilder();
    
    if (isWordCharacter(nextChar)) {
      // Read word token
      while (hasNextChar && isWordCharacter(nextChar)) {
        token.append((char) nextChar);
        nextChar = this.read();
        hasNextChar = (nextChar != -1);
      }
    } else {
      // Read non-word token
      while (hasNextChar && !isWordCharacter(nextChar)) {
        token.append((char) nextChar);
        nextChar = this.read();
        hasNextChar = (nextChar != -1);
      }
    }
    
    hasNextToken = hasNextChar;
    return token.toString();
  }

  private int read() {
    try {
      return reader.read();
    } catch (IOException e) {
      throw new RuntimeException("Error leyendo el archivo", e.getCause());
    }
  }

}
