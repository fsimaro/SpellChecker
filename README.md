# SpellChecker - Correcto Ortográfico

Sistema de corrección ortográfica implementado en Java que permite verificar y corregir errores de ortografía en documentos de texto.

## Características

- **Tokenización inteligente**: Separa texto en tokens (palabras y no-palabras) preservando espacios y puntuación
- **Múltiples estructuras de diccionario**: Implementación con HashSet y Trie para comparación de rendimiento
- **Tres tipos de correctores**:
  - **FileCorrector**: Correcciones basadas en diccionario de errores comunes
  - **SwapCorrector**: Detecta errores de intercambio de letras adyacentes
  - **Levenshtein**: Correcciones basadas en distancia de edición (inserción, eliminación, sustitución)
- **Interfaz interactiva**: Permite al usuario elegir entre sugerencias o ingresar correcciones manuales
- **Case-insensitive**: Búsquedas independientes de mayúsculas/minúsculas

## Estructura del Proyecto

```
SpellChecker/
├── src/
│   ├── main/java/edu/isistan/spellchecker/
│   │   ├── tokenizer/
│   │   │   └── TokenScanner.java          # Tokenizador de texto
│   │   ├── corrector/
│   │   │   ├── Dictionary.java             # Diccionario (HashSet/Trie)
│   │   │   ├── Corrector.java              # Clase abstracta base
│   │   │   └── impl/
│   │   │       ├── FileCorrector.java      # Corrector basado en archivo
│   │   │       ├── SwapCorrector.java     # Corrector de intercambio
│   │   │       └── Levenshtein.java        # Corrector de distancia de edición
│   │   ├── SpellChecker.java               # Orquestador principal
│   │   └── SpellCheckerRunner.java         # Punto de entrada CLI
│   └── test/java/
│       └── ...                             # Tests unitarios y benchmarks
├── pom.xml                                 # Configuración Maven
└── README.md                               # Este archivo
```

## Requisitos

- Java 8 o superior
- Maven 3.6 o superior

## Compilación

```bash
mvn clean compile
```

## Ejecución

### Desde línea de comandos

```bash
java -cp target/classes edu.isistan.spellchecker.SpellCheckerRunner <archivo_entrada> <archivo_salida> <diccionario> <corrector>
```

**Parámetros**:
- `archivo_entrada`: Archivo de texto a verificar
- `archivo_salida`: Archivo donde se guardará el texto corregido
- `diccionario`: Archivo con palabras válidas (una por línea)
- `corrector`: Tipo de corrector:
  - `SWAP` - SwapCorrector
  - `LEV` - Levenshtein
  - `<archivo>` - FileCorrector (ruta a archivo de correcciones)

**Ejemplo**:
```bash
java -cp target/classes edu.isistan.spellchecker.SpellCheckerRunner \
  input.txt output.txt dictionary.txt SWAP
```

### Ejecutar tests

```bash
mvn test
```

### Ejecutar benchmarks de performance

```bash
./run-benchmarks.sh
```

O manualmente:
```bash
mvn clean package -DskipTests
java -jar target/benchmarks.jar DictionaryBenchmark
```

## Formato de Archivos

### Diccionario

Archivo de texto con una palabra por línea:
```
apple
banana
cherry
it's
don't
```

### Archivo de Correcciones (FileCorrector)

Formato CSV con palabra incorrecta y corrección separadas por coma:
```
baloon,balloon
aligatur,alligator
ther,their
ther,there
```

### Documento de Entrada

Cualquier archivo de texto. El sistema preserva espacios, puntuación y saltos de línea.

## Uso del Programa

1. El programa lee el documento token por token
2. Para cada palabra encontrada:
   - Verifica si está en el diccionario
   - Si no está, muestra sugerencias del corrector seleccionado
   - El usuario puede:
     - Elegir una sugerencia (número)
     - Ignorar el error (0)
     - Ingresar corrección manual (1)
3. El documento corregido se guarda en el archivo de salida

## Tests

El proyecto incluye tests unitarios completos para todas las clases:

- `TokenScannerTest`: Verifica tokenización correcta
- `DictionaryTest`: Prueba búsquedas y construcción
- `FileCorrectorTest`: Valida formato y correcciones
- `SwapCorrectorTest`: Verifica detección de intercambios
- `LevenshteinTest`: Prueba distancia de edición
- `SpellCheckerTest`: Tests de integración
- `MyTests`: Tests adicionales requeridos

## Benchmarks

El proyecto incluye benchmarks de performance usando JMH (Java Microbenchmark Harness) para comparar:

- **HashSet vs Trie**: Rendimiento de búsqueda en diferentes tamaños de diccionario
- **Palabras existentes vs inexistentes**: Comportamiento en ambos casos
- **Diferentes tamaños**: Small, Medium, Large

Ver `BENCHMARK_README.md` para más detalles.

## Documentación Adicional

- `BENCHMARK_README.md`: Guía de uso de los benchmarks

## Ejemplos de Uso

### Ejemplo 1: Corrector de Intercambio

```bash
java -cp target/classes edu.isistan.spellchecker.SpellCheckerRunner \
  documento.txt corregido.txt dictionary.txt SWAP
```

Detecta errores como "teh" → "the" (intercambio de letras adyacentes).

### Ejemplo 2: Corrector Levenshtein

```bash
java -cp target/classes edu.isistan.spellchecker.SpellCheckerRunner \
  documento.txt corregido.txt dictionary.txt LEV
```

Sugiere correcciones basadas en inserción, eliminación o sustitución de una letra.

### Ejemplo 3: Corrector desde Archivo

```bash
java -cp target/classes edu.isistan.spellchecker.SpellCheckerRunner \
  documento.txt corregido.txt dictionary.txt misspellings.txt
```

Usa un diccionario predefinido de errores comunes y sus correcciones.

## Licencia

Trabajo final para la materia Taller de Programación JAVA
