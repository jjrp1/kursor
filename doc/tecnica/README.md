# 📚 Documentación Técnica - Kursor

## 🎯 Estado Actual del Proyecto

### ✅ Funcionalidades Implementadas

#### 🏗️ Arquitectura Modular
- **Sistema modular completo** con 4 tipos de preguntas
- **4 estrategias de aprendizaje** implementadas como módulos independientes
- **Carga dinámica** mediante ServiceLoader
- **Patrones de diseño** implementados (Factory, Builder, Strategy, Plugin)

#### 📚 Tipos de Preguntas
- ✅ **Flashcards**: Tarjetas de memoria con pregunta y respuesta
- ✅ **Opción Múltiple**: Preguntas con múltiples opciones y explicaciones
- ✅ **Completar Huecos**: Texto con espacios para rellenar
- ✅ **Verdadero/Falso**: Preguntas de tipo booleano

#### 🎯 Estrategias de Aprendizaje
- ✅ **Secuencial**: Preguntas en orden fijo
- ✅ **Aleatoria**: Preguntas en orden aleatorio
- ✅ **Repetición Espaciada**: Algoritmos optimizados de memoria
- ✅ **Repetir Incorrectas**: Repite automáticamente las preguntas falladas

#### 🎨 Interfaz de Usuario
- ✅ **JavaFX moderno** con diseño responsive
- ✅ **Diálogos modales** para interacciones específicas
- ✅ **Gestión de sesiones** con persistencia de progreso
- ✅ **Estadísticas detalladas** de rendimiento

### 📊 Métricas del Proyecto
- **Líneas de código**: ~6,000
- **Módulos implementados**: 4 tipos de preguntas + 4 estrategias
- **Tests unitarios**: Completos para todas las funcionalidades
- **Documentación**: Guías de usuario y técnica completas

### 🔧 Tecnologías Utilizadas
- **Java 17** con características modernas
- **JavaFX 17.0.2** para interfaz gráfica
- **Maven 3.8+** para gestión de dependencias
- **SLF4J + Logback** para logging
- **SnakeYAML** para procesamiento de archivos YAML
- **JUnit 5** para testing

### 🚀 Próximas Mejoras
- Optimización de rendimiento
- Nuevas estrategias avanzadas
- Configuración avanzada por estrategia
- Analytics detallados de aprendizaje
- Soporte para múltiples idiomas
- Integración con sistemas LMS

## Autor
- **Nombre**: Juan José Ruiz Pérez
- **Email**: jjrp1@um.es

## Documentos Técnicos Disponibles

### 📋 [Estado del Proyecto](estado-proyecto.md)
**Documento principal de referencia** - Contiene el estado actual del proyecto, problemas resueltos, configuración técnica y próximos pasos. **Leer primero** para entender el contexto completo.

### 🏗️ [Arquitectura Modular del Dominio](arquitectura-modular-dominio.md)
**Documento de arquitectura** - Análisis exhaustivo de la estrategia de diseño para tipos de preguntas, evaluación de opciones arquitectónicas, principios SOLID aplicados y decisión de implementar arquitectura modular. **Leer antes de implementar cambios en el dominio**.

### ✅ [Implementación Arquitectura Modular](implementacion-arquitectura-modular.md)
**Documento de implementación** - Resumen detallado de la implementación exitosa de la arquitectura modular del dominio, incluyendo cambios realizados, beneficios obtenidos y validación de la implementación. **Leer para entender el estado actual**.

### 🎯 [Propuesta GUI Curso](gui-curso.md)
Plan detallado para implementar la interfaz gráfica de cursos con ventana modal, gestión de sesiones y navegación entre preguntas.

### 🧪 [Plan de Pruebas](pruebas-plan-inicial.md)
Estrategia de pruebas, cobertura de código y casos de prueba para el proyecto.

### 🔍 [Testing del Sistema de Logging](logging-testing.md)
Documentación completa del sistema de testing para el mecanismo de logging, incluyendo pruebas unitarias, integración, cobertura JaCoCo y ubicaciones de resultados.

## Estructura del Proyecto

### Arquitectura del Sistema
- **Multi-módulo Maven** con arquitectura de plugins
- **Módulos principales**: 
  - `kursor-core`: Lógica de negocio y dominio
  - `kursor-ui`: Interfaz gráfica JavaFX
  - Módulos de preguntas: `fillblanks`, `flashcard`, `multiplechoice`, `truefalse`
- **Versión actual**: 1.0.0
- **Directorio raíz**: `C:\pds\ultima-version`

### Patrones de Diseño Utilizados
- **Singleton**: ModuleManager, CursoManager, Logger
- **Factory**: CursoFactory para creación de cursos
- **Builder**: CursoBuilder para construcción de objetos complejos
- **Plugin Architecture**: Carga dinámica de módulos de preguntas

## Estado Actual del Desarrollo

### ✅ Funcionalidades Implementadas
- **Carga dinámica de módulos** desde carpeta `modules/`
- **Gestión de cursos** desde archivos YAML en carpeta `cursos/`
- **Interfaz principal** con lista de cursos y módulos
- **Ventana modal** para detalles de cursos con conteo de preguntas
- **Botones de acción**: Inspeccionar, Comenzar, Reanudar, Flashcards, Estadísticas
- **Sistema de logging** personalizado
- **Documentación Javadoc** completa

### 🔧 Problemas Resueltos Recientemente
- **Rutas de archivos**: Corregidas para funcionar desde Maven
- **Carga de preguntas**: Implementada en CursoPreviewService
- **Conteo de preguntas**: Ahora muestra valores correctos
- **Iconos de botones**: Configurados con fuente Segoe UI Emoji

### 🚧 Próximos Pasos (Plan de 6 Fases)
1. **Fase 1**: CursoSessionManager y entidades JPA
2. **Fase 2**: CursoInterfaceController básico
3. **Fase 3**: Ventana modal con 3 secciones
4. **Fase 4**: Extender QuestionModule
5. **Fase 5**: Navegación y validación
6. **Fase 6**: Integración final

## Configuración del Entorno

### Comandos Importantes
```bash
# Ejecutar aplicación
mvn javafx:run -pl kursor-ui

# Compilar todo el proyecto
mvn clean install

# Crear ZIP portable
.\create-portable-zip.ps1
```

### Dependencias Principales
- **Java**: 17
- **JavaFX**: 17.0.2
- **Maven**: 3.x
- **Jackson**: Para procesamiento YAML
- **SnakeYAML**: Para parsing YAML

## Convenciones de Código

### Estructura de Paquetes
```
com.kursor.core/
├── domain/          # Entidades de dominio
├── service/         # Servicios de negocio
├── factory/         # Factories
├── builder/         # Builders
└── util/           # Utilidades

com.kursor.ui/
├── KursorApplication.java    # Aplicación principal
├── CursoManager.java         # Gestión de cursos
└── ModuleManager.java        # Gestión de módulos
```

### Documentación
- **Javadoc completo** en todas las clases públicas
- **Comentarios en español** para consistencia
- **Logging detallado** para debugging

## Limitaciones Conocidas

### Técnicas
- **Emojis**: JavaFX no soporta emojis a color (solo monocromos)
- **Persistencia**: No implementada aún (pendiente JPA/Hibernate)
- **Validación**: Básica en módulos (pendiente implementación completa)

### Funcionales
- **Sesiones múltiples**: No soportadas aún
- **Estadísticas**: No implementadas
- **Progreso**: No persistido

## Guía de Contribución

### Para Nuevas Funcionalidades
1. Revisar [Estado del Proyecto](estado-proyecto.md)
2. Consultar [Propuesta GUI Curso](gui-curso.md) si es relevante
3. Seguir convenciones de código establecidas
4. Actualizar documentación técnica
5. Probar con `mvn javafx:run -pl kursor-ui`

### Para Reportar Problemas
- Verificar logs en `kursor.log`
- Revisar configuración de rutas
- Confirmar que módulos están en carpeta `modules/`
- Verificar que cursos están en carpeta `cursos/`

---

**Última actualización**: 2025-06-18
**Estado**: En desarrollo activo
**Próximo hito**: Implementación de interfaz de cursos (Fase 1) 