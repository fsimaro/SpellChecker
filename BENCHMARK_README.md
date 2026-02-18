# Benchmarks de Performance - Dictionary

Este documento describe cómo ejecutar los benchmarks de performance para comparar las implementaciones de `Dictionary` usando `HashSet` vs `Trie`.

## Descripción

Los benchmarks comparan el rendimiento de dos estructuras de datos:
- **HashSet**: Implementación basada en `java.util.HashSet`
- **Trie**: Implementación basada en un árbol de prefijos (Trie)

## Métricas Evaluadas

Los benchmarks miden:
1. **Búsqueda de palabras existentes**: Tiempo para buscar palabras que están en el diccionario
2. **Búsqueda de palabras inexistentes**: Tiempo para buscar palabras que NO están en el diccionario
3. **Obtener número de palabras**: Tiempo para obtener el tamaño del diccionario

## Tamaños de Diccionario

Los benchmarks se ejecutan con tres tamaños diferentes:
- **small**: Diccionario pequeño (~35 palabras) - `smallDictionary.txt`
- **medium**: Diccionario mediano (~1000 palabras) - `mediumDictionary.txt`
- **large**: Diccionario grande (~60,000 palabras) - `dictionary.txt`

## Ejecución

### Opción 1: Usando el script proporcionado

```bash
./run-benchmarks.sh
```

### Opción 2: Usando Maven directamente

```bash
# Compilar el proyecto inluyendo tests en JAR
mvn clean test-compile package -DskipTests
# Ejecutar los benchmarks
java -jar target/benchmarks.jar DictionaryBenchmark
```

### Opción 3: Ejecutar desde el IDE

Ejecutar la clase `DictionaryBenchmark` directamente desde el IDE. El método `main` ejecutará todos los benchmarks configurados.

## Configuración de los Benchmarks

Los benchmarks están configurados con:
- **Warmup**: 3 iteraciones de 1 segundo cada una
- **Measurement**: 5 iteraciones de 1 segundo cada una
- **Fork**: 1 (una sola ejecución)
- **Modo**: Tiempo promedio en nanosegundos

Para modificar estos parámetros, editar las anotaciones en `DictionaryBenchmark.java`.

## Interpretación de Resultados

Los resultados muestran:
- **Tiempo promedio**: Tiempo promedio de ejecución en nanosegundos
- **Score**: Puntuación del benchmark (menor es mejor)
- **Error**: Margen de error estadístico

### Ejemplo de salida:

```
Benchmark                                    (dictionarySize)  Mode  Cnt      Score      Error  Units
DictionaryBenchmark.hashSetSearchWordExists            small  avgt    5   1234.567  ±  45.678  ns/op
DictionaryBenchmark.trieSearchWordExists               small  avgt    5   2345.678  ±  67.890  ns/op
DictionaryBenchmark.hashSetSearchWordNotExists         small  avgt    5   1234.567  ±  45.678  ns/op
DictionaryBenchmark.trieSearchWordNotExists            small  avgt    5   2345.678  ±  67.890  ns/op
```

## Análisis Esperado

### HashSet
- **Ventajas**: 
  - Búsqueda O(1) promedio
  - Menor overhead de memoria para palabras cortas
  - Implementación simple y eficiente
  
- **Desventajas**:
  - No aprovecha prefijos comunes
  - Mayor uso de memoria para diccionarios grandes

### Trie
- **Ventajas**:
  - Eficiente para búsquedas por prefijo
  - Comparte memoria para prefijos comunes
  - Puede ser más eficiente en memoria para palabras con prefijos compartidos
  
- **Desventajas**:
  - Overhead de memoria por nodo
  - Búsqueda O(m) donde m es la longitud de la palabra
  - Más complejo de implementar

## Notas

- Las palabras de prueba se generan aleatoriamente pero de forma determinística
- Los resultados pueden variar según el hardware y la carga del sistema
- Se recomienda ejecutar los benchmarks en un sistema sin carga para obtener resultados más consistentes
