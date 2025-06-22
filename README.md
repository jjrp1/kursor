# 🎓 Kursor - Plataforma de Formación Interactiva Modular

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-green.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI/CD](https://github.com/jjrp1/kursor/workflows/CI%2FCD%20Pipeline/badge.svg)](https://github.com/jjrp1/kursor/actions)
[![Release](https://img.shields.io/github/v/release/jjrp1/kursor)](https://github.com/jjrp1/kursor/releases)
[![GitHub Pages](https://img.shields.io/badge/GitHub%20Pages-Enabled-brightgreen.svg)](https://jjrp1.github.io/kursor)
[![Codecov](https://codecov.io/gh/jjrp1/kursor/branch/master/graph/badge.svg)](https://codecov.io/gh/jjrp1/kursor)
[![Coverage](https://img.shields.io/codecov/c/github/jjrp1/kursor/master.svg)](https://codecov.io/gh/jjrp1/kursor)
[![Quality Gate](https://img.shields.io/badge/Quality%20Gate-Passed-brightgreen.svg)](https://github.com/jjrp1/kursor/actions)

## 📋 Descripción

**Kursor** es una plataforma de formación interactiva modular desarrollada en Java que permite crear y gestionar cursos educativos con diferentes tipos de preguntas. La aplicación está diseñada con una arquitectura modular que facilita la extensión con nuevos tipos de preguntas sin modificar el código principal.

## 📑 Índice de Contenido

### 🚀 **Inicio Rápido**
- [📋 Descripción](#-descripción)
- [🌐 Documentación Web](#-documentación-web)
- [✨ Características Principales](#-características-principales)
- [🚀 Instalación y Uso](#-instalación-y-uso)

### 🏗️ **Arquitectura y Estructura**
- [📁 Estructura del Proyecto](#-estructura-del-proyecto)
- [🧠 Estrategias de Aprendizaje](#-estrategias-de-aprendizaje)
- [📝 Tipos de Preguntas](#-tipos-de-preguntas)
- [💾 Persistencia Robusta](#-persistencia-robusta)
- [🏗️ Arquitectura Modular](#arquitectura-modular)

### 🛠️ **Desarrollo**
- [Instalación y Uso](#instalación-y-uso)
- [Desarrollo](#desarrollo)
- [Agregar un Nuevo Tipo de Pregunta](#agregar-un-nuevo-tipo-de-pregunta)
- [Agregar una Nueva Estrategia](#agregar-una-nueva-estrategia)

### 📚 **Documentación**
- [Documentación](#documentación)
- [Documentación Técnica](#documentación-técnica)
- [Documentación de Usuario](#documentación-de-usuario)
- [Documentación Web](#documentación-web)

### 📊 **Estado y Contribución**
- [Estado del Proyecto](#estado-del-proyecto)
- [Contribución](#contribución)
- [Guías de Desarrollo](#guías-de-desarrollo)
- [Reporte de Bugs](#reporte-de-bugs)

### 📄 **Información del Proyecto**
- [📄 Licencia](#-licencia)
- [👨‍💻 Autor](#-autor)
- [🙏 Agradecimientos](#-agradecimientos)
- [📞 Soporte](#-soporte)

---

## 🌐 Documentación Web

**📖 [Visita las GitHub Pages](https://jjrp1.github.io/kursor)** para documentación completa, guías interactivas y ejemplos.

### 📚 Páginas Disponibles
- **[🏠 Página Principal](https://jjrp1.github.io/kursor/)** - Visión general del proyecto
- **[🚀 Guía de Inicio Rápido](https://jjrp1.github.io/kursor/guia-inicio-rapido.html)** - Tutorial paso a paso
- **[❓ FAQ](https://jjrp1.github.io/kursor/faq.html)** - Preguntas frecuentes
- **[🏗️ Arquitectura](https://jjrp1.github.io/kursor/arquitectura.html)** - Documentación técnica

## ✨ Características Principales

### 🏗️ Arquitectura Modular
- **Core independiente**: El sistema principal no depende de implementaciones específicas
- **Módulos extensibles**: Nuevos tipos de preguntas se pueden agregar como módulos independientes
- **Carga dinámica**: Los módulos se cargan automáticamente mediante ServiceLoader
- **Patrones de diseño**: Factory, Builder, Strategy y Plugin patterns

### 🔒 Modelo de Usuario
- **Aplicación monousuario**: Diseñada para un solo usuario por simplicidad
- **Campo `usuarioId`**: Se mantiene para futuras expansiones a multiusuario
- **Sin autenticación**: No requiere login ni gestión de sesiones de usuario
- **Extensibilidad**: Arquitectura preparada para futuras expansiones

### 📚 Tipos de Preguntas Soportados
- **🃏 Flashcards**: Tarjetas de memoria con pregunta y respuesta
- **☑️ Opción Múltiple**: Preguntas con múltiples opciones y explicaciones
- **🔤 Completar Huecos**: Texto con espacios para rellenar
- **✅ Verdadero/Falso**: Preguntas de tipo booleano

### 🎯 Estrategias de Aprendizaje
- **Secuencial**: Preguntas en orden fijo
- **Aleatoria**: Preguntas en orden aleatorio
- **Repetición Espaciada**: Algoritmos de memoria optimizados
- **Repetir Incorrectas**: Repite automáticamente las preguntas falladas

### 🎨 Interfaz de Usuario
- **JavaFX moderno**: Interfaz gráfica intuitiva y responsive
- **Diálogos modales**: Interacciones específicas para cada funcionalidad
- **Gestión de sesiones**: Control del progreso de aprendizaje
- **Estadísticas**: Métricas de rendimiento y progreso

## 🚀 Instalación y Uso

### Requisitos Previos
- **Java 17** o superior
- **Maven 3.8+**
- **Git**

### Instalación Rápida

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/jjrp1/kursor.git
   cd kursor
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**
   ```bash
   java -jar kursor-core/target/kursor-core-1.0.0.jar
   ```

### Versión Portable

Para usuarios que no tienen Java instalado, se proporciona una versión portable:

1. Descargar `kursor-portable-v1.0.0.zip`
2. Extraer el archivo
3. Ejecutar `run.bat` (Windows) o `run.sh` (Linux/Mac)

## 📁 Estructura del Proyecto

```
kursor/
├── kursor-core/                           # Módulo principal
│   ├── src/main/java/com/kursor/
│   │   ├── ui/                            # Interfaz de usuario JavaFX
│   │   │   ├── KursorApplication.java     # Aplicación principal
│   │   │   ├── CursoDialog.java           # Diálogo de gestión de cursos
│   │   │   ├── CursoSessionManager.java   # Gestión de sesiones
│   │   │   ├── InspeccionarCurso.java     # Inspector de cursos
│   │   │   ├── EstadisticasDialog.java    # Diálogo de estadísticas
│   │   │   ├── AboutDialog.java           # Diálogo de información
│   │   │   ├── MenuBarExample.java        # Barra de menú
│   │   │   └── PreguntaResponseExtractor.java # Extractor de respuestas
│   │   ├── domain/                        # Modelo de dominio
│   │   │   ├── Curso.java                 # Entidad Curso
│   │   │   ├── Bloque.java                # Entidad Bloque
│   │   │   ├── Pregunta.java              # Entidad Pregunta
│   │   │   ├── Sesion.java                # Entidad Sesión
│   │   │   ├── Respuesta.java             # Entidad Respuesta
│   │   │   ├── PreguntaSesion.java        # Entidad PreguntaSesion
│   │   │   └── EstrategiaAprendizaje.java # Interfaz de estrategias
│   │   ├── persistence/                   # Capa de persistencia
│   │   │   ├── entity/                    # Entidades JPA
│   │   │   │   ├── Sesion.java            # Entidad de sesión
│   │   │   │   ├── EstadisticasUsuario.java # Estadísticas de usuario
│   │   │   │   ├── EstadoEstrategia.java  # Estado de estrategias
│   │   │   │   ├── EstadoSesion.java      # Estado de sesiones
│   │   │   │   └── RespuestaPregunta.java # Respuestas de preguntas
│   │   │   ├── repository/                # Repositorios JPA
│   │   │   │   ├── SesionRepository.java  # Repositorio de sesiones
│   │   │   │   ├── EstadisticasUsuarioRepository.java # Estadísticas
│   │   │   │   ├── EstadoEstrategiaRepository.java # Estados de estrategias
│   │   │   │   └── RespuestaPreguntaRepository.java # Respuestas
│   │   │   ├── config/                    # Configuración JPA
│   │   │   │   └── PersistenceConfig.java # Configuración de persistencia
│   │   │   ├── manager/                   # Gestores de persistencia
│   │   │   └── dialect/                   # Dialectos de base de datos
│   │   ├── service/                       # Servicios de negocio
│   │   │   └── CursoPreviewService.java   # Servicio de vista previa
│   │   ├── util/                          # Utilidades y gestores
│   │   │   ├── ModuleManager.java         # Gestor de módulos
│   │   │   └── CursoManager.java          # Gestor de cursos
│   │   ├── yaml/dto/                      # Objetos de transferencia
│   │   │   └── CursoPreviewDTO.java       # DTO para vista previa
│   │   ├── modules/                       # Interfaz de módulos
│   │   │   └── PreguntaModule.java        # Interfaz de módulos de preguntas
│   │   ├── strategy/                      # Interfaz de estrategias
│   │   │   └── EstrategiaModule.java      # Interfaz de módulos de estrategias
│   │   ├── factory/                       # Patrón Factory
│   │   │   └── PreguntaFactory.java       # Factory de preguntas
│   │   └── builder/                       # Patrón Builder
│   │       └── CursoBuilder.java          # Builder de cursos
│   ├── src/test/java/com/kursor/          # Tests unitarios
│   │   ├── domain/                        # Tests de dominio
│   │   │   ├── CursoTest.java             # Tests de Curso
│   │   │   ├── BloqueTest.java            # Tests de Bloque
│   │   │   ├── PreguntaTest.java          # Tests de Pregunta
│   │   │   └── SesionTest.java            # Tests de Sesión
│   │   ├── service/                       # Tests de servicios
│   │   │   └── CursoPreviewServiceTest.java # Tests de vista previa
│   │   ├── persistence/                   # Tests de persistencia
│   │   │   ├── repository/                # Tests de repositorios
│   │   │   │   └── PersistenceTest.java   # Tests de persistencia
│   │   │   └── manager/                   # Tests de gestores
│   │   │       └── EstrategiaStateManagerTest.java # Tests de estado
│   │   ├── strategy/                      # Tests de estrategias
│   │   │   └── StrategyManagerTest.java   # Tests de gestor de estrategias
│   │   ├── util/                          # Tests de utilidades
│   │   │   ├── ModuleManagerTest.java     # Tests de gestor de módulos
│   │   │   └── CursoManagerTest.java      # Tests de gestor de cursos
│   │   └── builder/                       # Tests de builders
│   │       └── CursoBuilderTest.java      # Tests de builder de cursos
│   ├── src/main/resources/                # Recursos
│   │   ├── logback.xml                    # Configuración de logging
│   │   └── META-INF/                      # Metadatos
│   │       └── persistence.xml            # Configuración JPA
│   ├── src/test/resources/                # Recursos de test
│   │   └── logback-test.xml               # Configuración de logging para tests
│   ├── log/                               # Logs de la aplicación
│   ├── pom.xml                            # Configuración Maven del core
│   └── target/                            # Archivos compilados
├── kursor-flashcard-module/               # Módulo de flashcards
│   ├── src/main/java/com/kursor/flashcard/
│   │   ├── FlashcardModule.java           # Módulo de flashcards
│   │   └── domain/
│   │       └── Flashcard.java             # Entidad Flashcard
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.modules.PreguntaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── kursor-multiplechoice-module/          # Módulo de opción múltiple
│   ├── src/main/java/com/kursor/multiplechoice/
│   │   ├── MultipleChoiceModule.java      # Módulo de opción múltiple
│   │   └── domain/
│   │       └── PreguntaTest.java          # Entidad PreguntaTest
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.modules.PreguntaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── kursor-fillblanks-module/              # Módulo de completar huecos
│   ├── src/main/java/com/kursor/fillblanks/
│   │   ├── FillBlanksModule.java          # Módulo de completar huecos
│   │   └── domain/
│   │       └── PreguntaCompletarHuecos.java # Entidad de completar huecos
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.modules.PreguntaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── kursor-truefalse-module/               # Módulo verdadero/falso
│   ├── src/main/java/com/kursor/truefalse/
│   │   ├── TrueFalseModule.java           # Módulo verdadero/falso
│   │   └── domain/
│   │       └── PreguntaTrueFalse.java     # Entidad verdadero/falso
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.modules.PreguntaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── kursor-secuencial-strategy/            # Estrategia secuencial
│   ├── src/main/java/com/kursor/strategy/secuencial/
│   │   ├── SecuencialStrategy.java        # Implementación de estrategia
│   │   └── SecuencialStrategyModule.java  # Módulo de estrategia
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.strategy.EstrategiaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── kursor-aleatoria-strategy/             # Estrategia aleatoria
│   ├── src/main/java/com/kursor/strategy/aleatoria/
│   │   ├── AleatoriaStrategy.java         # Implementación de estrategia
│   │   └── AleatoriaStrategyModule.java   # Módulo de estrategia
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.strategy.EstrategiaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── kursor-repeticion-espaciada-strategy/  # Estrategia repetición espaciada
│   ├── src/main/java/com/kursor/strategy/repeticionespaciada/
│   │   ├── RepeticionEspaciadaStrategy.java # Implementación de estrategia
│   │   └── RepeticionEspaciadaStrategyModule.java # Módulo de estrategia
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.strategy.EstrategiaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── kursor-repetir-incorrectas-strategy/   # Estrategia repetir incorrectas
│   ├── src/main/java/com/kursor/strategy/repetirincorrectas/
│   │   ├── RepetirIncorrectasStrategy.java # Implementación de estrategia
│   │   └── RepetirIncorrectasStrategyModule.java # Módulo de estrategia
│   ├── src/main/resources/META-INF/services/
│   │   └── com.kursor.strategy.EstrategiaModule # Registro del módulo
│   └── pom.xml                            # Configuración Maven
├── cursos/                                # Cursos de ejemplo
│   ├── curso_ingles/                      # Curso de inglés básico
│   │   └── curso_ingles.yaml              # Archivo de curso
│   ├── curso_ingles_b2/                   # Curso de inglés B2
│   │   └── curso_ingles_b2.yaml           # Archivo de curso
│   └── flashcards_ingles/                 # Flashcards de inglés
│       └── flashcards_ingles.yml          # Archivo de flashcards
├── doc/                                   # Documentación
│   ├── usuario/                           # Documentación de usuario
│   │   ├── estrategias.md                 # Guía de estrategias
│   │   ├── faq.md                         # Preguntas frecuentes
│   │   ├── guia-inicio-rapido.md          # Guía de inicio rápido
│   │   └── README.md                      # README de usuario
│   ├── tecnica/                           # Documentación técnica
│   │   ├── README.md                      # README técnico
│   │   ├── estrategias-plan-inicial.md    # Plan de estrategias
│   │   ├── arquitectura-modular-dominio.md # Arquitectura modular
│   │   ├── estructura-proyecto.md         # Estructura del proyecto
│   │   ├── logging-configuracion.md       # Configuración de logging
│   │   ├── deployment-plan.md             # Plan de despliegue
│   │   ├── pruebas-plan-inicial.md        # Plan de pruebas
│   │   ├── persistencia-plan-inicial.md   # Plan de persistencia
│   │   ├── estado-proyecto.md             # Estado del proyecto
│   │   ├── github-bitacora.md             # Bitácora de GitHub
│   │   ├── gui-curso.md                   # GUI de cursos
│   │   ├── logging.md                     # Sistema de logging
│   │   ├── logging-testing.md             # Testing de logging
│   │   ├── implementacion-arquitectura-modular.md # Implementación modular
│   │   ├── estado-del-arte.md             # Estado del arte
│   │   ├── cambio-brutal.md               # Cambios importantes
│   │   ├── estado-proyecto-mini.md        # Estado mini del proyecto
│   │   ├── justifica-dto.md               # Justificación de DTOs
│   │   └── estrategias-modularizacion.md  # Estrategias de modularización
│   └── anotaciones/                       # Anotaciones del proyecto
│       ├── estado-del-arte-viernes.md     # Estado del arte (viernes)
│       └── prompts.md                     # Prompts utilizados
├── docs/                                  # GitHub Pages
│   ├── index.html                         # Página principal
│   ├── estrategias.html                   # Página de estrategias
│   ├── arquitectura.html                  # Página de arquitectura
│   ├── faq.html                           # Página de FAQ
│   ├── guia-inicio-rapido.html            # Guía de inicio rápido
│   ├── resultados-pruebas.html            # Resultados de pruebas
│   ├── _config.yml                        # Configuración de GitHub Pages
│   ├── sitemap.xml                        # Sitemap
│   └── robots.txt                         # Robots.txt
├── scripts/                               # Scripts de utilidad
│   ├── dev.ps1                            # Script de desarrollo (PowerShell)
│   ├── dev.sh                             # Script de desarrollo (Linux/Mac)
│   ├── prod.sh                            # Script de producción
│   ├── test.sh                            # Script de pruebas
│   ├── debug.sh                           # Script de debug
│   └── README.md                          # README de scripts
├── kursor-portable/                       # Versión portable
│   ├── kursor.jar                         # JAR ejecutable
│   ├── jre/                               # JRE portable
│   ├── lib/                               # Bibliotecas JavaFX
│   ├── config/                            # Configuración
│   ├── cursos/                            # Cursos incluidos
│   ├── strategies/                        # Estrategias incluidas
│   ├── modules/                           # Módulos incluidos
│   ├── run.bat                            # Script de ejecución (Windows)
│   ├── run.sh                             # Script de ejecución (Linux/Mac)
│   └── run.ps1                            # Script de ejecución (PowerShell)
├── modules/                               # Módulos adicionales
├── log/                                   # Logs del proyecto
├── build/                                 # Archivos de build
├── dist/                                  # Distribución
├── release/                               # Releases
├── .github/                               # Configuración de GitHub
│   ├── workflows/                         # Workflows de CI/CD
│   ├── ISSUE_TEMPLATE/                    # Plantillas de issues
│   └── CONTRIBUTING.md                    # Guía de contribución
├── .mvn/                                  # Configuración de Maven
├── .vscode/                               # Configuración de VS Code
├── pom.xml                                # POM principal (multi-módulo)
├── README.md                              # README principal
├── CHANGELOG.md                           # Registro de cambios
├── RELEASE_NOTES.md                       # Notas de release
├── LICENSE                                # Licencia MIT
├── .gitignore                             # Archivos ignorados por Git
├── codecov.yml                            # Configuración de Codecov
├── deployment-config.json                 # Configuración de despliegue
├── create-portable-zip.ps1                # Script para crear ZIP portable
└── jre-temp.zip                           # JRE temporal (178MB)
```

## 📖 Documentación

### 🌐 Documentación Web (Recomendada)
- **[🏠 Página Principal](https://jjrp1.github.io/kursor/)** - Visión general y características
- **[🚀 Guía de Inicio Rápido](https://jjrp1.github.io/kursor/guia-inicio-rapido.html)** - Tutorial interactivo
- **[❓ FAQ](https://jjrp1.github.io/kursor/faq.html)** - Preguntas frecuentes y solución de problemas
- **[🏗️ Arquitectura](https://jjrp1.github.io/kursor/arquitectura.html)** - Documentación técnica detallada

### 📚 Guías de Usuario (Markdown)
- [Guía de Inicio Rápido](doc/usuario/guia-inicio-rapido.md)
- [FAQ](doc/usuario/faq.md)
- [README de Usuario](doc/usuario/README.md)

### 🔧 Documentación Técnica
- **[📋 Estado del Arte](doc/tecnica/estado-del-arte.md)** - Documento unificado con estado actual, arquitectura y cumplimiento
- [Arquitectura Modular](doc/tecnica/arquitectura-modular-dominio.md)
- [Plan de Persistencia](doc/tecnica/persistencia-plan-inicial.md)
- [Modularización de Estrategias](doc/tecnica/estrategias-modularizacion.md)
- [Plan de Pruebas](doc/tecnica/pruebas-plan-inicial.md)

### Documentación de Usuario
- [Guía de Inicio Rápido](doc/usuario/guia-inicio-rapido.md)
- [FAQ](doc/usuario/faq.md)

### Documentación Web
- [Sitio Web](docs/index.html)
- [Arquitectura](docs/arquitectura.html)

## 🛠️ Desarrollo

### Configuración del Entorno

1. **IDE recomendado**: IntelliJ IDEA, Eclipse o VS Code
2. **Plugins útiles**:
   - Java Extension Pack (VS Code)
   - Maven Integration
   - JavaFX Support

### Compilación y Tests

```bash
# Compilar todo el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Generar JAR ejecutable
mvn package

# Instalar en repositorio local
mvn install
```

### Crear un Nuevo Módulo

1. Crear nuevo módulo Maven siguiendo la estructura:
   ```
   kursor-[tipo]-module/
   ├── src/main/java/com/kursor/[tipo]/
   │   ├── [Tipo]Module.java
   │   └── domain/
   │       └── [Tipo]Pregunta.java
   └── pom.xml
   ```

2. Implementar la interfaz `PreguntaModule`
3. Registrar el módulo en `META-INF/services/`
4. Agregar dependencia en el pom.xml principal

## 📊 Estadísticas del Proyecto

- **Líneas de código**: ~6,000
- **Módulos implementados**: 4
- **Tipos de preguntas**: 4
- **Estrategias de aprendizaje**: 4
- **Tests unitarios**: 5

## 🤝 Contribuir

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👨‍💻 Autor

**Juan José Ruiz Pérez** <jjrp1@um.es>

- **GitHub**: [@jjrp1](https://github.com/jjrp1)
- **Proyecto**: [Kursor](https://github.com/jjrp1/kursor)
- **Documentación**: [GitHub Pages](https://jjrp1.github.io/kursor)

## 🙏 Agradecimientos

- **JavaFX Team** por el framework de interfaz gráfica
- **Apache Maven** por el sistema de build
- **SLF4J** por el sistema de logging
- **SnakeYAML** por el procesamiento de archivos YAML

## 📞 Soporte

- **🌐 Documentación Web**: [GitHub Pages](https://jjrp1.github.io/kursor)
- **Issues**: [GitHub Issues](https://github.com/jjrp1/kursor/issues)
- **Discusiones**: [GitHub Discussions](https://github.com/jjrp1/kursor/discussions)
- **Email**: jjrp1@um.es

---

⭐ **Si te gusta este proyecto, ¡dale una estrella en GitHub!** 

## 🧠 Estrategias de Aprendizaje
- **Secuencial**: Presenta preguntas en orden secuencial
- **Aleatoria**: Presenta preguntas en orden aleatorio
- **Repetición Espaciada**: Repite preguntas con intervalos crecientes
- **Repetir Incorrectas**: Repite preguntas falladas al final de la sesión

## 📝 Tipos de Preguntas
- **Opción Múltiple**: Preguntas con múltiples opciones de respuesta
- **Verdadero/Falso**: Preguntas de verdadero o falso
- **Flashcards**: Tarjetas de memoria
- **Completar Huecos**: Preguntas de completar espacios en blanco

## 💾 Persistencia Robusta
- **Sesiones**: Guardado y restauración de sesiones de aprendizaje
- **Estado de Estrategias**: Persistencia del estado interno de cada estrategia
- **Estadísticas**: Seguimiento del progreso del usuario
- **Base de Datos**: SQLite con JPA para datos persistentes

## Arquitectura Modular

### Módulos de Preguntas
```
kursor-multiplechoice-module/
kursor-truefalse-module/
kursor-flashcard-module/
kursor-fillblanks-module/
```

### Módulos de Estrategias
```
kursor-secuencial-strategy/
kursor-aleatoria-strategy/
kursor-repeticion-espaciada-strategy/
kursor-repetir-incorrectas-strategy/
```

### Distribución Final
```
kursor-portable/
├── strategies/          # Estrategias de aprendizaje
├── modules/            # Tipos de preguntas
├── kursor-core.jar     # Núcleo del sistema
├── kursor.db          # Base de datos SQLite
└── [configuración]
```

## Instalación y Uso

### Requisitos
- Java 17 o superior
- Maven 3.6 o superior

### Compilación
```bash
# Compilar todos los módulos
mvn clean install

# Compilar solo el core
cd kursor-core
mvn clean install

# Compilar módulos de preguntas
cd kursor-multiplechoice-module
mvn clean install
```

### Ejecución
```bash
# Ejecutar desde el directorio raíz
java -jar kursor-core/target/kursor-core-1.0.0.jar

# O usar el script de desarrollo
./scripts/dev.sh
```

## Desarrollo

### Estructura del Proyecto
```
inflexion/
├── kursor-core/                    # Núcleo del sistema
├── kursor-*-module/               # Módulos de preguntas
├── kursor-*-strategy/             # Módulos de estrategias
├── doc/                           # Documentación técnica
├── docs/                          # Documentación web
├── scripts/                       # Scripts de desarrollo
└── cursos/                        # Cursos de ejemplo
```

### Agregar un Nuevo Tipo de Pregunta
1. Crear nuevo módulo siguiendo el patrón `kursor-[nombre]-module`
2. Implementar la interfaz `PreguntaModule`
3. Crear la clase de pregunta específica
4. Registrar el módulo en `META-INF/services/`

### Agregar una Nueva Estrategia
1. Crear nuevo módulo siguiendo el patrón `kursor-[nombre]-strategy`
2. Implementar la interfaz `EstrategiaModule`
3. Crear la clase de estrategia específica
4. Registrar el módulo en `META-INF/services/`

## Documentación

### Documentación Técnica
- [Arquitectura Modular](doc/tecnica/arquitectura-modular-dominio.md)
- [Plan de Persistencia](doc/tecnica/persistencia-plan-inicial.md)
- [Modularización de Estrategias](doc/tecnica/estrategias-modularizacion.md)
- [Plan de Pruebas](doc/tecnica/pruebas-plan-inicial.md)

### Documentación de Usuario
- [Guía de Inicio Rápido](doc/usuario/guia-inicio-rapido.md)
- [FAQ](doc/usuario/faq.md)

### Documentación Web
- [Sitio Web](docs/index.html)
- [Arquitectura](docs/arquitectura.html)

## Estado del Proyecto

### 📋 **Documento Principal de Estado**
**📖 [Estado del Arte Completo](doc/tecnica/estado-del-arte.md)** - Documento unificado con el estado actual del proyecto, cumplimiento del enunciado original, arquitectura, pruebas y próximos pasos.

### ✅ **Completado (100%)**
- [x] **Arquitectura modular completa** - 8 módulos (4 tipos de preguntas + 4 estrategias)
- [x] **Sistema de persistencia JPA** - SQLite con entidades completas
- [x] **Carga dinámica de módulos** - ServiceLoader implementado
- [x] **Interfaz JavaFX** - Interfaz moderna y funcional
- [x] **Pruebas unitarias** - 96 pruebas en modelo de dominio
- [x] **Documentación completa** - Técnica, usuario y web
- [x] **Cumplimiento enunciado original** - 100% de requisitos implementados

### 🎯 **Características Implementadas**
- **4 tipos de preguntas**: Flashcards, Opción Múltiple, Completar Huecos, Verdadero/Falso
- **4 estrategias de aprendizaje**: Secuencial, Aleatoria, Repetición Espaciada, **Repetir Incorrectas** (característica adicional)
- **Sistema de persistencia**: JPA con SQLite, sesiones, estadísticas, estados de estrategias
- **Carga dinámica**: ServiceLoader para módulos de preguntas y estrategias
- **Interfaz de usuario**: JavaFX con diálogos modales y gestión de sesiones

### 📊 **Métricas de Éxito**
- **Módulos implementados**: 8/8 ✅
- **Pruebas unitarias**: 96/96 ✅
- **Cumplimiento enunciado**: 100% ✅
- **Documentación**: Completa ✅
- **Arquitectura**: Modular y extensible ✅

### 🚀 **Próximos Pasos (Opcionales)**
- [ ] Completar FASE 2 de pruebas (Utilidades y Factory)
- [ ] Implementar FASE 3 de pruebas (Servicios y DTOs)
- [ ] Optimizar cobertura de código
- [ ] Nuevas estrategias de aprendizaje
- [ ] Interfaz web opcional
- [ ] Sistema de analytics avanzado

**🎉 El proyecto está COMPLETADO y cumple todos los requisitos del enunciado original.**

## 📋 Pendiente
- [ ] Interfaz gráfica de usuario
- [ ] Sistema de estadísticas avanzado
- [ ] Soporte para múltiples idiomas
- [ ] API REST para integración externa

## Contribución

### Guías de Desarrollo
1. Seguir las convenciones de código establecidas
2. Agregar pruebas unitarias para nuevas funcionalidades
3. Actualizar la documentación correspondiente
4. Usar commits descriptivos

### Reporte de Bugs
- Usar el sistema de issues de GitHub
- Incluir información del entorno y pasos para reproducir
- Adjuntar logs de error cuando sea posible

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## Autores

- **Juan José Ruiz Pérez** <jjrp1@um.es>
- **Equipo de Desarrollo Kursor**

## Changelog

Ver [CHANGELOG.md](CHANGELOG.md) para un historial detallado de cambios.

## Contacto

- **Email**: jjrp1@um.es
- **Proyecto**: [GitHub Repository](https://github.com/jjrp1/kursor)
- **Documentación**: [GitHub Pages](https://jjrp1.github.io/kursor) 