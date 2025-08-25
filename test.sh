#!/bin/bash

# ==================== SCRIPT VALIDACIÓN COBERTURA ====================
# Script para validar cobertura mínima del 90% en CI/CD

set -e

echo "🧪 Ejecutando tests unitarios..."
mvn clean test

echo "📊 Generando reporte de cobertura..."
mvn jacoco:report

echo "✅ Validando cobertura mínima (90%)..."
mvn jacoco:check

# Extraer métricas del reporte XML
COVERAGE_FILE="target/site/jacoco/jacoco.xml"

if [[ -f "$COVERAGE_FILE" ]]; then
    # Extraer cobertura de líneas
    LINE_COVERAGE=$(xmllint --xpath "string(//counter[@type='LINE']/@covered)" $COVERAGE_FILE)
    LINE_MISSED=$(xmllint --xpath "string(//counter[@type='LINE']/@missed)" $COVERAGE_FILE)
    LINE_TOTAL=$((LINE_COVERAGE + LINE_MISSED))
    LINE_PERCENTAGE=$(echo "scale=2; $LINE_COVERAGE * 100 / $LINE_TOTAL" | bc)

    # Extraer cobertura de ramas
    BRANCH_COVERAGE=$(xmllint --xpath "string(//counter[@type='BRANCH']/@covered)" $COVERAGE_FILE)
    BRANCH_MISSED=$(xmllint --xpath "string(//counter[@type='BRANCH']/@missed)" $COVERAGE_FILE)
    BRANCH_TOTAL=$((BRANCH_COVERAGE + BRANCH_MISSED))
    BRANCH_PERCENTAGE=$(echo "scale=2; $BRANCH_COVERAGE * 100 / $BRANCH_TOTAL" | bc)

    # Extraer cobertura de métodos
    METHOD_COVERAGE=$(xmllint --xpath "string(//counter[@type='METHOD']/@covered)" $COVERAGE_FILE)
    METHOD_MISSED=$(xmllint --xpath "string(//counter[@type='METHOD']/@missed)" $COVERAGE_FILE)
    METHOD_TOTAL=$((METHOD_COVERAGE + METHOD_MISSED))
    METHOD_PERCENTAGE=$(echo "scale=2; $METHOD_COVERAGE * 100 / $METHOD_TOTAL" | bc)

    echo "📈 REPORTE DE COBERTURA:"
    echo "├── Líneas: ${LINE_PERCENTAGE}% (${LINE_COVERAGE}/${LINE_TOTAL})"
    echo "├── Ramas: ${BRANCH_PERCENTAGE}% (${BRANCH_COVERAGE}/${BRANCH_TOTAL})"
    echo "└── Métodos: ${METHOD_PERCENTAGE}% (${METHOD_COVERAGE}/${METHOD_TOTAL})"

    # Validar que todas las métricas superen el 90%
    if (( $(echo "$LINE_PERCENTAGE >= 90" | bc -l) )); then
        echo "✅ Cobertura de líneas: ${LINE_PERCENTAGE}% >= 90%"
    else
        echo "❌ Cobertura de líneas: ${LINE_PERCENTAGE}% < 90%"
        exit 1
    fi

    if (( $(echo "$BRANCH_PERCENTAGE >= 90" | bc -l) )); then
        echo "✅ Cobertura de ramas: ${BRANCH_PERCENTAGE}% >= 90%"
    else
        echo "❌ Cobertura de ramas: ${BRANCH_PERCENTAGE}% < 90%"
        exit 1
    fi

    if (( $(echo "$METHOD_PERCENTAGE >= 90" | bc -l) )); then
        echo "✅ Cobertura de métodos: ${METHOD_PERCENTAGE}% >= 90%"
    else
        echo "❌ Cobertura de métodos: ${METHOD_PERCENTAGE}% < 90%"
        exit 1
    fi

    echo ""
    echo "🎉 ¡COBERTURA EXITOSA! Todas las métricas superan el 90%"
    echo "📄 Reporte HTML disponible en: target/site/jacoco/index.html"

else
    echo "❌ No se encontró el archivo de reporte de cobertura"
    exit 1
fi

# Comandos útiles para desarrollo local:
echo ""
echo "🔧 COMANDOS ÚTILES:"
echo "├── Ver reporte: open target/site/jacoco/index.html"
echo "├── Solo tests: mvn test"
echo "├── Solo cobertura: mvn jacoco:report"
echo "└── Validar cobertura: mvn jacoco:check"