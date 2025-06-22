# Estructura del Proyecto Kursor

## ✅ Estado Actual: Estructura Modular Completa

La estructura actual del proyecto presenta una organización clara y modular:

### 📁 **Estructura Actual (Implementada):**

```
kursor/
├── kursor-core/                           # Módulo principal
│   ├── src/main/java/com/kursor/
│   │   ├── ui/                            # Interfaz de usuario JavaFX
│   │   ├── domain/                        # Modelo de dominio
│   │   ├── persistence/                   # Capa de persistencia JPA
│   │   ├── service/                       # Servicios de negocio
│   │   ├── util/                          # Utilidades y gestores
│   │   ├── yaml/dto/                      # Objetos de transferencia
│   │   ├── modules/                       # Interfaz de módulos de preguntas
│   │   ├── strategy/                      # Interfaz de módulos de estrategias
│   │   ├── factory/                       # Patrón Factory
│   │   └── builder/                       # Patrón Builder
│   ├── src/test/java/com/kursor/          # Tests unitarios
│   ├── src/main/resources/                # Recursos y configuración
│   ├── src/test/resources/                # Recursos de test
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
│   ├── tecnica/                           # Documentación técnica
│   └── anotaciones/                       # Anotaciones del proyecto
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
├── modules/                               # Módulos adicionales (opcional)
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

## ✅ **Ventajas de la Estructura Actual:**

### 🎯 **Organización Clara:**
1. **Separación de responsabilidades**: Cada módulo tiene su propósito específico
2. **Módulos independientes**: Fácil mantenimiento y extensión
3. **Configuración centralizada**: POM principal gestiona todos los módulos
4. **Documentación organizada**: Separación clara entre usuario y técnica
5. **Distribución simplificada**: Versión portable lista para usar

### 🔧 **Facilidad de Desarrollo:**
1. **Maven multi-módulo**: Gestión eficiente de dependencias
2. **ServiceLoader**: Carga dinámica automática de módulos
3. **Tests organizados**: Cada módulo tiene sus propios tests
4. **Logging centralizado**: Sistema de logs unificado
5. **Scripts de utilidad**: Automatización de tareas comunes

### 📦 **Distribución Eficiente:**
1. **Versión portable**: Incluye JRE y todas las dependencias
2. **Módulos incluidos**: Todos los JARs necesarios
3. **Configuración automática**: No requiere instalación
4. **Multiplataforma**: Scripts para Windows, Linux y Mac
5. **Documentación incluida**: Guías de usuario completas

## 🚀 **Uso de la Estructura**

### **Desarrollo:**
```bash
# Compilar todo el proyecto
mvn clean install

# Ejecutar aplicación
mvn javafx:run -pl kursor-core

# Ejecutar tests
mvn test
```

### **Distribución:**
```bash
# Crear versión portable
.\create-portable-zip.ps1

# Ejecutar versión portable
cd kursor-portable
.\run.bat
```

### **Documentación:**
- **Usuario**: `doc/usuario/` - Guías de uso
- **Técnica**: `doc/tecnica/` - Documentación de desarrollo
- **Web**: `docs/` - GitHub Pages

## 🎉 **Conclusión**

La estructura actual del proyecto Kursor es robusta, bien organizada y facilita tanto el desarrollo como la distribución. La arquitectura modular permite una extensibilidad sin límites mientras mantiene la simplicidad de uso para los usuarios finales.