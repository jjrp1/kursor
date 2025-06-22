#!/bin/bash
# Script de configuración para entorno de producción
# Uso: ./scripts/prod.sh

echo "🏭 Iniciando Kursor en modo producción..."

# Configuración de producción
export KURSOR_LOG_STACKTRACE=false
export KURSOR_LOG_LEVEL=INFO
export KURSOR_DEV_MODE=false
export KURSOR_LOG_FILE=production.log

echo "📋 Configuración aplicada:"
echo "   - Stacktrace: $KURSOR_LOG_STACKTRACE"
echo "   - Nivel de log: $KURSOR_LOG_LEVEL"
echo "   - Modo desarrollo: $KURSOR_DEV_MODE"
echo "   - Archivo de log: $KURSOR_LOG_FILE"
echo ""

# Ejecutar aplicación
echo "▶️  Ejecutando aplicación..."
java -jar kursor-ui/target/kursor-ui-1.0.0.jar 