#!/bin/bash

# Script para generar datos JSON de métricas de testing
# Extrae información de reportes Jacoco y Surefire

set -e

echo "=== Iniciando generación de métricas de testing ===" >&2

# Inicializar contadores
total_tests=0
passed_tests=0
failed_tests=0
skipped_tests=0
total_coverage=0
modules_count=0
execution_time=0

# Función para extraer métricas de Surefire
extract_surefire_metrics() {
    echo "Extrayendo métricas de Surefire..." >&2
    
    # Buscar reportes de Surefire en múltiples ubicaciones
    surefire_dirs=(
        "test-reports"
        "**/target/surefire-reports"
        "**/target/site/surefire-reports"
    )
    
    for dir in "${surefire_dirs[@]}"; do
        if [ -d "$dir" ]; then
            echo "Buscando en directorio: $dir" >&2
            for report in $(find "$dir" -name "TEST-*.xml" 2>/dev/null || true); do
                if [ -f "$report" ]; then
                    echo "Procesando reporte: $report" >&2
                    
                    # Extraer métricas usando grep y sed
                    tests=$(grep -o 'tests="[0-9]*"' "$report" | head -1 | sed 's/tests="//g' | sed 's/"//g' || echo "0")
                    failures=$(grep -o 'failures="[0-9]*"' "$report" | head -1 | sed 's/failures="//g' | sed 's/"//g' || echo "0")
                    errors=$(grep -o 'errors="[0-9]*"' "$report" | head -1 | sed 's/errors="//g' | sed 's/"//g' || echo "0")
                    skips=$(grep -o 'skipped="[0-9]*"' "$report" | head -1 | sed 's/skipped="//g' | sed 's/"//g' || echo "0")
                    time=$(grep -o 'time="[0-9.]*"' "$report" | head -1 | sed 's/time="//g' | sed 's/"//g' || echo "0")
                    
                    echo "  Tests: $tests, Failures: $failures, Errors: $errors, Skips: $skips, Time: $time" >&2
                    
                    total_tests=$((total_tests + tests))
                    failed_tests=$((failed_tests + failures + errors))
                    skipped_tests=$((skipped_tests + skips))
                    
                    # Sumar tiempo de ejecución (usar awk si está disponible, sino bc)
                    if command -v awk >/dev/null 2>&1; then
                        execution_time=$(echo "$execution_time $time" | awk '{print $1 + $2}')
                    elif command -v bc >/dev/null 2>&1; then
                        execution_time=$(echo "$execution_time + $time" | bc -l 2>/dev/null || echo "$execution_time")
                    else
                        execution_time=$(echo "$execution_time + $time" | sed 's/^0*//' | sed 's/^\./0./' || echo "$execution_time")
                    fi
                fi
            done
        fi
    done
    
    passed_tests=$((total_tests - failed_tests - skipped_tests))
    echo "Métricas Surefire - Total: $total_tests, Pasados: $passed_tests, Fallidos: $failed_tests, Omitidos: $skipped_tests" >&2
}

# Función para extraer métricas de cobertura Jacoco
extract_jacoco_metrics() {
    echo "Extrayendo métricas de Jacoco..." >&2
    
    # Buscar reportes de Jacoco en múltiples ubicaciones
    jacoco_dirs=(
        "test-reports"
        "**/target/site/jacoco"
        "**/target/jacoco"
    )
    
    for dir in "${jacoco_dirs[@]}"; do
        if [ -d "$dir" ]; then
            echo "Buscando en directorio: $dir" >&2
            for jacoco in $(find "$dir" -name "jacoco.xml" 2>/dev/null || true); do
                if [ -f "$jacoco" ]; then
                    echo "Procesando reporte Jacoco: $jacoco" >&2
                    
                    # Extraer cobertura de líneas usando grep y sed
                    covered=$(grep -o 'INSTRUCTION.*covered="[0-9]*"' "$jacoco" | head -1 | sed 's/.*covered="//g' | sed 's/".*//g' || echo "0")
                    missed=$(grep -o 'INSTRUCTION.*missed="[0-9]*"' "$jacoco" | head -1 | sed 's/.*missed="//g' | sed 's/".*//g' || echo "0")
                    
                    echo "  Cubiertas: $covered, Perdidas: $missed" >&2
                    
                    if [ "$covered" != "0" ] || [ "$missed" != "0" ]; then
                        total_lines=$((covered + missed))
                        if [ "$total_lines" -gt 0 ]; then
                            coverage=$((covered * 100 / total_lines))
                            total_coverage=$((total_coverage + coverage))
                            modules_count=$((modules_count + 1))
                            echo "  Cobertura calculada: $coverage%" >&2
                        fi
                    fi
                fi
            done
        fi
    done
    
    echo "Métricas Jacoco - Módulos: $modules_count, Cobertura total: $total_coverage" >&2
}

# Extraer métricas
extract_surefire_metrics
extract_jacoco_metrics

# Calcular promedios
if [ "$modules_count" -gt 0 ]; then
    avg_coverage=$((total_coverage / modules_count))
else
    avg_coverage=0
fi

# Calcular tasa de éxito
if [ "$total_tests" -gt 0 ]; then
    success_rate=$((passed_tests * 100 / total_tests))
else
    success_rate=0
fi

# Obtener timestamp
timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

# Obtener información del commit
commit_hash=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
commit_message=$(git log -1 --pretty=%B 2>/dev/null | head -1 | tr -d '"' || echo "No commit info")

# Determinar estado del build
if [ "$failed_tests" -eq 0 ] && [ "$total_tests" -gt 0 ]; then
    build_status="success"
else
    build_status="failure"
fi

echo "Generando JSON final..." >&2

# Generar JSON
cat <<EOF
{
  "timestamp": "$timestamp",
  "commit": {
    "hash": "$commit_hash",
    "message": "$commit_message"
  },
  "tests": {
    "total": $total_tests,
    "passed": $passed_tests,
    "failed": $failed_tests,
    "skipped": $skipped_tests,
    "success_rate": $success_rate,
    "execution_time": "$execution_time"
  },
  "coverage": {
    "average": $avg_coverage,
    "modules_count": $modules_count
  },
  "modules": [
$(
    # Buscar reportes de Jacoco para generar datos de módulos
    for jacoco in $(find . -name "jacoco.xml" 2>/dev/null || true); do
        if [ -f "$jacoco" ]; then
            # Extraer nombre del módulo del path
            module_path=$(dirname "$jacoco" | sed 's|^\./||g' | sed 's|/target/site/jacoco||g' | sed 's|/target/jacoco||g')
            module_name=$(basename "$module_path")
            
            covered=$(grep -o 'INSTRUCTION.*covered="[0-9]*"' "$jacoco" | head -1 | sed 's/.*covered="//g' | sed 's/".*//g' || echo "0")
            missed=$(grep -o 'INSTRUCTION.*missed="[0-9]*"' "$jacoco" | head -1 | sed 's/.*missed="//g' | sed 's/".*//g' || echo "0")
            
            if [ "$covered" != "0" ] || [ "$missed" != "0" ]; then
                total_lines=$((covered + missed))
                if [ "$total_lines" -gt 0 ]; then
                    coverage=$((covered * 100 / total_lines))
                    echo "    {"
                    echo "      \"name\": \"$module_name\","
                    echo "      \"coverage\": $coverage,"
                    echo "      \"lines_covered\": $covered,"
                    echo "      \"lines_total\": $total_lines"
                    echo "    },"
                fi
            fi
        fi
    done | sed '$ s/,$//'
)
  ],
  "status": "$build_status"
}
EOF

echo "=== Generación de métricas completada ===" >&2 