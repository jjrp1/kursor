#!/bin/bash

# Script para generar datos JSON de métricas de testing
# Extrae información de reportes Jacoco y Surefire

set -e

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
    echo "Extracting Surefire metrics..." >&2
    
    for report in $(find test-reports -name "TEST-*.xml" 2>/dev/null || true); do
        if [ -f "$report" ]; then
            tests=$(grep -o 'tests="[0-9]*"' "$report" | sed 's/tests="//g' | sed 's/"//g' || echo "0")
            failures=$(grep -o 'failures="[0-9]*"' "$report" | sed 's/failures="//g' | sed 's/"//g' || echo "0")
            errors=$(grep -o 'errors="[0-9]*"' "$report" | sed 's/errors="//g' | sed 's/"//g' || echo "0")
            skips=$(grep -o 'skipped="[0-9]*"' "$report" | sed 's/skipped="//g' | sed 's/"//g' || echo "0")
            time=$(grep -o 'time="[0-9.]*"' "$report" | sed 's/time="//g' | sed 's/"//g' || echo "0")
            
            total_tests=$((total_tests + tests))
            failed_tests=$((failed_tests + failures + errors))
            skipped_tests=$((skipped_tests + skips))
            execution_time=$(echo "$execution_time + $time" | bc -l 2>/dev/null || echo "$execution_time")
        fi
    done
    
    passed_tests=$((total_tests - failed_tests - skipped_tests))
}

# Función para extraer métricas de cobertura Jacoco
extract_jacoco_metrics() {
    echo "Extracting Jacoco metrics..." >&2
    
    for jacoco in $(find test-reports -name "jacoco.xml" 2>/dev/null || true); do
        if [ -f "$jacoco" ]; then
            # Extraer cobertura de líneas usando grep y sed más básico
            covered=$(grep -o 'INSTRUCTION.*covered="[0-9]*"' "$jacoco" | head -1 | sed 's/.*covered="//g' | sed 's/".*//g' || echo "0")
            missed=$(grep -o 'INSTRUCTION.*missed="[0-9]*"' "$jacoco" | head -1 | sed 's/.*missed="//g' | sed 's/".*//g' || echo "0")
            
            if [ "$covered" != "0" ] || [ "$missed" != "0" ]; then
                total_lines=$((covered + missed))
                if [ "$total_lines" -gt 0 ]; then
                    coverage=$((covered * 100 / total_lines))
                    total_coverage=$((total_coverage + coverage))
                    modules_count=$((modules_count + 1))
                fi
            fi
        fi
    done
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
commit_message=$(git log -1 --pretty=%B 2>/dev/null | head -1 || echo "No commit info")

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
    for jacoco in $(find test-reports -name "jacoco.xml" 2>/dev/null || true); do
        if [ -f "$jacoco" ]; then
            module_path=$(dirname "$jacoco" | sed 's|test-reports/||g' | sed 's|/target/site/jacoco||g')
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
  "status": "$([ "$failed_tests" -eq 0 ] && echo "success" || echo "failure")"
}
EOF 