#!/bin/bash

# Script para ejecutar los benchmarks de performance del Dictionary

echo "Compilando el proyecto..."
mvn clean package -DskipTests

echo ""
echo "Ejecutando benchmarks..."
echo ""

# Ejecutar benchmarks
java -jar target/benchmarks.jar DictionaryBenchmark
echo ""
echo "Benchmarks completados!"
