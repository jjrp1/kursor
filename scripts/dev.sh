#!/bin/bash
# Script de configuración para entorno de desarrollo
# Uso: ./scripts/dev.sh

echo "🚀 Iniciando Kursor en modo desarrollo..."

# Configuración de desarrollo
export KURSOR_LOG_STACKTRACE=true
export KURSOR_LOG_LEVEL=DEBUG
export KURSOR_DEV_MODE=true
export KURSOR_LOG_FILE=dev.log

echo "📋 Configuración aplicada:"
echo "   - Stacktrace: $KURSOR_LOG_STACKTRACE"
echo "   - Nivel de log: $KURSOR_LOG_LEVEL"
echo "   - Modo desarrollo: $KURSOR_DEV_MODE"
echo "   - Archivo de log: $KURSOR_LOG_FILE"
echo ""

# Ejecutar aplicación
echo "▶️  Ejecutando aplicación..."
java -jar kursor-ui/target/kursor-ui-1.0.0.jar 