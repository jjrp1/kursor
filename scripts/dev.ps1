# Configuración de desarrollo
$env:KURSOR_LOG_STACKTRACE = "true"
$env:KURSOR_LOG_LEVEL = "DEBUG"
$env:KURSOR_DEV_MODE = "true"
$env:KURSOR_LOG_FILE = "dev.log"

java -jar kursor-ui/target/kursor-ui-1.0.0.jar 