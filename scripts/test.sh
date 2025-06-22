#!/bin/bash
# Script de configuración para entorno de testing
# Uso: ./scripts/test.sh

echo "🧪 Iniciando Kursor en modo testing..."

# Configuración de testing
export KURSOR_LOG_STACKTRACE=true
export KURSOR_LOG_LEVEL=WARNING
export KURSOR_DEV_MODE=false
export KURSOR_LOG_FILE=test.log

echo "📋 Configuración aplicada:"
echo "   - Stacktrace: $KURSOR_LOG_STACKTRACE"
echo "   - Nivel de log: $KURSOR_LOG_LEVEL"
echo "   - Modo desarrollo: $KURSOR_DEV_MODE"
echo "   - Archivo de log: $KURSOR_LOG_FILE"
echo ""

# Ejecutar aplicación
echo "▶️  Ejecutando aplicación..."
java -jar kursor-ui/target/kursor-ui-1.0.0.jar 