#!/bin/bash
# Script de configuración para debugging específico
# Uso: ./scripts/debug.sh

echo "🐛 Iniciando Kursor en modo debugging..."

# Configuración de debugging
export KURSOR_LOG_STACKTRACE=true
export KURSOR_LOG_LEVEL=DEBUG
export KURSOR_DEV_MODE=true
export KURSOR_LOG_FILE=debug.log

echo "📋 Configuración aplicada:"
echo "   - Stacktrace: $KURSOR_LOG_STACKTRACE"
echo "   - Nivel de log: $KURSOR_LOG_LEVEL"
echo "   - Modo desarrollo: $KURSOR_DEV_MODE"
echo "   - Archivo de log: $KURSOR_LOG_FILE"
echo ""

# Ejecutar aplicación con propiedades del sistema (máxima prioridad)
echo "▶️  Ejecutando aplicación con configuración de debugging..."
java -Dkursor.log.stacktrace=true \
     -Dkursor.log.level=DEBUG \
     -Dkursor.dev.mode=true \
     -Dkursor.log.file=debug.log \
     -jar kursor-ui/target/kursor-ui-1.0.0.jar 