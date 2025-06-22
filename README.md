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
├── kursor-core/                 # Módulo principal
│   ├── src/main/java/com/kursor/
│   │   ├── ui/                  # Interfaz de usuario
│   │   ├── domain/              # Modelo de dominio
│   │   ├── util/                # Utilidades y gestores
│   │   ├── yaml/dto/            # Objetos de transferencia
│   │   ├── service/             # Servicios
│   │   ├── modules/             # Interfaz de módulos
│   │   ├── factory/             # Patrón Factory
│   │   └── builder/             # Patrón Builder
│   └── src/test/                # Tests unitarios
├── kursor-flashcard-module/     # Módulo de flashcards
├── kursor-multiplechoice-module/ # Módulo de opción múltiple
├── kursor-fillblanks-module/    # Módulo de completar huecos
├── kursor-truefalse-module/     # Módulo verdadero/falso
├── kursor-secuencial-strategy/  # Estrategia secuencial
├── kursor-aleatoria-strategy/   # Estrategia aleatoria
├── kursor-repeticion-espaciada-strategy/ # Estrategia repetición espaciada
├── kursor-repetir-incorrectas-strategy/ # Estrategia repetir incorrectas
├── cursos/                      # Cursos de ejemplo
├── doc/                         # Documentación
├── docs/                        # GitHub Pages
└── scripts/                     # Scripts de utilidad
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

### 🔧 Documentación Técnica (Markdown)
- [Arquitectura Modular](doc/tecnica/arquitectura-modular-dominio.md)
- [Estructura del Proyecto](doc/tecnica/estructura-proyecto.md)
- [Configuración de Logging](doc/tecnica/logging-configuracion.md)
- [Plan de Despliegue](doc/tecnica/deployment-plan.md)

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

### Documentación de Usuario
- [Guía de Inicio Rápido](doc/usuario/guia-inicio-rapido.md)
- [FAQ](doc/usuario/faq.md)

### Documentación Web
- [Sitio Web](docs/index.html)
- [Arquitectura](docs/arquitectura.html)

## Estado del Proyecto

### ✅ Completado
- [x] Arquitectura modular de preguntas
- [x] Estrategias de aprendizaje básicas
- [x] Sistema de persistencia JPA + SQLite
- [x] Pruebas unitarias y de integración
- [x] Documentación técnica básica

### 🔄 En Desarrollo
- [ ] Modularización de estrategias
- [ ] StrategyManager para carga dinámica
- [ ] Integración completa de persistencia
- [ ] Documentación de usuario

### 📋 Pendiente
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