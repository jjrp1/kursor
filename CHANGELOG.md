# 📝 Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere al [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2024-12-19

### 🎉 Lanzamiento Inicial
Primera versión estable de Kursor - Plataforma de Formación Interactiva Modular.

### ✨ Añadido
- **StrategyManager**: Implementación completa del gestor de estrategias modulares
  - Carga dinámica de estrategias desde archivos JAR
  - Descubrimiento automático usando ServiceLoader
  - Cache inteligente de estrategias cargadas
  - Manejo robusto de errores y logging detallado
  - Métodos para crear, verificar y gestionar estrategias
  - Estadísticas de carga y recarga dinámica

- **EstrategiaStateManager actualizado**: Integración completa con StrategyManager
  - Persistencia de estado usando StrategyManager para crear estrategias
  - Serialización/deserialización JSON mejorada
  - Extracción de estado específico por tipo de estrategia
  - Restauración de estado con validación
  - Métodos para verificar y eliminar estado guardado

- **Pruebas unitarias completas**:
  - `StrategyManagerTest`: Pruebas exhaustivas del gestor de estrategias
  - `EstrategiaStateManagerTest`: Pruebas de integración con StrategyManager
  - Cobertura de casos de éxito, error y edge cases
  - Mocks para simular dependencias externas

- **Configuración de build mejorada**:
  - Plugin Maven para copiar JARs de estrategias a `/strategies/`
  - Dependencias actualizadas para soporte modular
  - Configuración de shade plugin para JAR ejecutable

- **Aplicación principal actualizada**:
  - Integración completa con StrategyManager
  - Detección automática de directorio de estrategias
  - Interfaz de usuario mejorada con información de estrategias
  - Manejo robusto de errores de inicialización
  - Logging detallado de operaciones

### 🔧 Configuración
- **GitHub Actions** para CI/CD automático
- **GitHub Pages** para documentación web
- **Sistema de releases** automático
- **Dependabot** para actualizaciones de dependencias
- **Plantillas de issues** para bugs y features
- **Guía de contribución** completa

### 📚 Documentación
- README profesional con badges y guías
- Documentación técnica de arquitectura
- Guías de usuario y FAQ
- Bitácora de GitHub completa

### 🛠️ Tecnologías
- **Java 17** con características modernas
- **JavaFX 17.0.2** para interfaz gráfica
- **Maven 3.8+** para gestión de dependencias
- **SLF4J + Logback** para logging
- **SnakeYAML** para procesamiento de archivos YAML
- **JUnit 5** para testing

### 🏗️ Arquitectura
- **Patrón Factory** para creación de preguntas
- **Patrón Builder** para construcción de cursos
- **Patrón Strategy** para estrategias de aprendizaje
- **Plugin Pattern** para módulos extensibles
- **ServiceLoader** para carga dinámica de módulos

### 📦 Distribución
- **JAR ejecutable** principal
- **Paquete portable** con JRE incluido
- **Módulos independientes** para cada tipo de pregunta
- **Documentación completa** incluida

---

## [1.0.0] - 2025-06-19

### 🎉 Lanzamiento Inicial
Primera versión estable de Kursor - Plataforma de Formación Interactiva Modular.

### ✨ Añadido
- **Arquitectura modular completa** con 4 tipos de preguntas
  - 🃏 Módulo de Flashcards
  - ☑️ Módulo de Opción Múltiple  
  - 🔤 Módulo de Completar Huecos
  - ✅ Módulo de Verdadero/Falso
- **Interfaz de usuario JavaFX** moderna y responsive
- **Sistema de estrategias de aprendizaje**:
  - Estrategia Secuencial
  - Estrategia Aleatoria
  - Estrategia de Repetición Espaciada
- **Gestión de sesiones** con persistencia de progreso
- **Sistema de logging** completo con SLF4J
- **Documentación técnica** exhaustiva
- **Cursos de ejemplo** incluidos

### 🔧 Configuración
- **GitHub Actions** para CI/CD automático
- **GitHub Pages** para documentación web
- **Sistema de releases** automático
- **Dependabot** para actualizaciones de dependencias
- **Plantillas de issues** para bugs y features
- **Guía de contribución** completa

### 📚 Documentación
- README profesional con badges y guías
- Documentación técnica de arquitectura
- Guías de usuario y FAQ
- Bitácora de GitHub completa

### 🛠️ Tecnologías
- **Java 17** con características modernas
- **JavaFX 17.0.2** para interfaz gráfica
- **Maven 3.8+** para gestión de dependencias
- **SLF4J + Logback** para logging
- **SnakeYAML** para procesamiento de archivos YAML
- **JUnit 5** para testing

### 🏗️ Arquitectura
- **Patrón Factory** para creación de preguntas
- **Patrón Builder** para construcción de cursos
- **Patrón Strategy** para estrategias de aprendizaje
- **Plugin Pattern** para módulos extensibles
- **ServiceLoader** para carga dinámica de módulos

### 📦 Distribución
- **JAR ejecutable** principal
- **Paquete portable** con JRE incluido
- **Módulos independientes** para cada tipo de pregunta
- **Documentación completa** incluida

---

## [Unreleased]

### 🚀 Próximas funcionalidades
- Soporte para múltiples idiomas
- Integración con sistemas LMS
- Analytics avanzados de aprendizaje
- API REST para integraciones
- Nuevos tipos de preguntas
- Sistema de usuarios y autenticación

### 🔧 Mejoras planificadas
- Optimización de rendimiento
- Mejoras en la interfaz de usuario
- Tests de integración adicionales
- Documentación de API
- Configuración de Codecov

### Added
- **Modularización de Estrategias COMPLETADA**: Implementación completa de módulos independientes para estrategias de aprendizaje
  - `kursor-secuencial-strategy`: Estrategia secuencial como módulo independiente
  - `kursor-aleatoria-strategy`: Estrategia aleatoria como módulo independiente
  - `kursor-repeticion-espaciada-strategy`: Estrategia de repetición espaciada como módulo independiente
  - `kursor-repetir-incorrectas-strategy`: Estrategia de repetir incorrectas como módulo independiente
- **Interfaz EstrategiaModule**: Definida en el core para contratos de módulos de estrategias
- **Estructura de Distribución**: Carpeta `/strategies/` creada en `kursor-portable/`
- **Configuración Maven**: pom.xml completados para todos los módulos de estrategias
- **Registro de Servicios**: META-INF/services configurado para carga dinámica

### Changed
- **README.md**: Actualizado con información sobre arquitectura modular de estrategias implementada
- **Documentación de Persistencia**: Integración con nueva arquitectura modular de estrategias
- **Estructura del Proyecto**: Módulos de estrategias independientes implementados

### Technical
- **Patrón de Nomenclatura**: Implementado patrón `kursor-(nombre)-strategy` para módulos de estrategias
- **Interfaz EstrategiaModule**: Implementada en el core con métodos de creación y metadatos
- **Carga Dinámica**: Preparado para ServiceLoader con registros META-INF
- **Compatibilidad**: Mantenida compatibilidad hacia atrás con API existente
- **Dependencias**: Configuradas dependencias del core con scope `provided`

### Files Created
- `kursor-secuencial-strategy/src/main/java/com/kursor/strategy/secuencial/SecuencialStrategy.java`
- `kursor-secuencial-strategy/src/main/java/com/kursor/strategy/secuencial/SecuencialStrategyModule.java`
- `kursor-aleatoria-strategy/src/main/java/com/kursor/strategy/aleatoria/AleatoriaStrategy.java`
- `kursor-aleatoria-strategy/src/main/java/com/kursor/strategy/aleatoria/AleatoriaStrategyModule.java`
- `kursor-repeticion-espaciada-strategy/src/main/java/com/kursor/strategy/repeticionespaciada/RepeticionEspaciadaStrategy.java`
- `kursor-repeticion-espaciada-strategy/src/main/java/com/kursor/strategy/repeticionespaciada/RepeticionEspaciadaStrategyModule.java`
- `kursor-repetir-incorrectas-strategy/src/main/java/com/kursor/strategy/repetirincorrectas/RepetirIncorrectasStrategy.java`
- `kursor-repetir-incorrectas-strategy/src/main/java/com/kursor/strategy/repetirincorrectas/RepetirIncorrectasStrategyModule.java`
- `kursor-core/src/main/java/com/kursor/strategy/EstrategiaModule.java`

## 📋 Convenciones de Versionado

### Formato: MAJOR.MINOR.PATCH

- **MAJOR**: Cambios incompatibles con versiones anteriores
- **MINOR**: Nuevas funcionalidades compatibles hacia atrás
- **PATCH**: Correcciones de bugs compatibles hacia atrás

### Tipos de Cambios

- **✨ Añadido**: Nuevas funcionalidades
- **🔧 Configuración**: Cambios en configuración
- **🐛 Corregido**: Correcciones de bugs
- **📚 Documentación**: Cambios en documentación
- **🚀 Lanzamiento**: Información de releases
- **♻️ Refactorizado**: Refactorización de código
- **⚡ Rendimiento**: Mejoras de rendimiento
- **🔒 Seguridad**: Mejoras de seguridad
- **🧪 Tests**: Añadir o corregir tests

---

**Mantenedor**: Juan José Ruiz Pérez <jjrp1@um.es>  
**Repositorio**: https://github.com/jjrp1/kursor 
