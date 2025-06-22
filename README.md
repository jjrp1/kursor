# 🎓 Kursor - Plataforma de Formación Interactiva Modular

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-green.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI/CD](https://github.com/jjrp1/kursor/workflows/CI%2FCD%20Pipeline/badge.svg)](https://github.com/jjrp1/kursor/actions)
[![Release](https://img.shields.io/github/v/release/jjrp1/kursor)](https://github.com/jjrp1/kursor/releases)
[![GitHub Pages](https://img.shields.io/badge/GitHub%20Pages-Enabled-brightgreen.svg)](https://jjrp1.github.io/kursor)

## 📋 Descripción

**Kursor** es una plataforma de formación interactiva modular desarrollada en Java que permite crear y gestionar cursos educativos con diferentes tipos de preguntas. La aplicación está diseñada con una arquitectura modular que facilita la extensión con nuevos tipos de preguntas sin modificar el código principal.

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
├── cursos/                      # Cursos de ejemplo
├── doc/                         # Documentación
└── scripts/                     # Scripts de utilidad
```

## 📖 Documentación

### 📚 Guías de Usuario
- [Guía de Inicio Rápido](doc/usuario/guia-inicio-rapido.md)
- [FAQ](doc/usuario/faq.md)
- [README de Usuario](doc/usuario/README.md)

### 🔧 Documentación Técnica
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
- **Estrategias de aprendizaje**: 3
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

## 🙏 Agradecimientos

- **JavaFX Team** por el framework de interfaz gráfica
- **Apache Maven** por el sistema de build
- **SLF4J** por el sistema de logging
- **SnakeYAML** por el procesamiento de archivos YAML

## 📞 Soporte

- **Issues**: [GitHub Issues](https://github.com/jjrp1/kursor/issues)
- **Documentación**: [Wiki del Proyecto](https://github.com/jjrp1/kursor/wiki)
- **Email**: jjrp1@um.es

---

⭐ **Si te gusta este proyecto, ¡dale una estrella en GitHub!** 