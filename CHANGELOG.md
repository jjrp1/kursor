# 📝 Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere al [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

---

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