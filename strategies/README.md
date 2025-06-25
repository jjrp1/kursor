# Directorio de Estrategias

Este directorio contiene los archivos JAR de las estrategias de aprendizaje que se cargan dinámicamente en la aplicación Kursor.

## Propósito

Las estrategias de aprendizaje definen cómo se presentan las preguntas al usuario durante una sesión de estudio. Cada estrategia implementa un algoritmo específico para optimizar el aprendizaje.

## Estrategias Disponibles

- **Secuencial**: Presenta las preguntas en orden secuencial
- **Aleatoria**: Presenta las preguntas en orden aleatorio
- **Repetición Espaciada**: Optimizada para retención a largo plazo
- **Repetir Incorrectas**: Enfocada en preguntas falladas anteriormente

## Carga Dinámica

Las estrategias se cargan automáticamente al iniciar la aplicación usando el patrón ServiceLoader de Java. Cada archivo JAR debe contener:

1. Una implementación de `EstrategiaModule`
2. El archivo de servicios `META-INF/services/com.kursor.strategy.EstrategiaModule`

## Estructura de un JAR de Estrategia

```
mi-estrategia.jar
├── META-INF/
│   └── services/
│       └── com.kursor.strategy.EstrategiaModule
└── com/
    └── kursor/
        └── strategy/
            └── miestrategia/
                ├── MiEstrategiaModule.java
                └── MiEstrategia.java
```

## Instalación

Para agregar una nueva estrategia:

1. Compilar el módulo de estrategia
2. Copiar el archivo JAR resultante a este directorio
3. Reiniciar la aplicación

La aplicación detectará automáticamente la nueva estrategia y la hará disponible para su uso. 