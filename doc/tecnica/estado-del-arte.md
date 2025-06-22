# Estado del Arte - Proyecto Kursor

## Información General
- **Proyecto**: Kursor - Plataforma de Formación Interactiva
- **Versión**: 1.0.0
- **Fecha de última actualización**: 19 de junio de 2025
- **Estado**: GUI funcional, compilación exitosa, algunos módulos de preguntas con problemas

## Arquitectura del Proyecto

### Estructura de Módulos Maven
```
ultima-version/
├── kursor-core/           # Módulo principal con dominio y servicios
├── kursor-ui/            # Interfaz gráfica JavaFX
├── kursor-fillblanks-module/    # Módulo de preguntas completar huecos
├── kursor-flashcard-module/     # Módulo de flashcards
├── kursor-multiplechoice-module/ # Módulo de preguntas múltiple choice
├── kursor-truefalse-module/     # Módulo de preguntas verdadero/falso
└── cursos/               # Archivos YAML de cursos
```

### Componentes Principales

#### Core (kursor-core)
- **Dominio**: `Curso`, `Bloque`, `Pregunta`, `Flashcard`, `Sesion`
- **Estrategias**: `EstrategiaAprendizaje`, `EstrategiaSecuencial`, `EstrategiaAleatoria`, `EstrategiaRepeticionEspaciada`
- **Servicios**: `CursoPreviewService`
- **Utilidades**: `CursoManager`, `ModuleManager`, `Logger`
- **DTOs**: `CursoPreviewDTO`

#### UI (kursor-ui)
- **Aplicación principal**: `KursorApplication` (JavaFX)
- **Vistas**: Dashboard, Cursos, Módulos, Estadísticas
- **Navegación**: Sistema de pestañas con botones de navegación

#### Módulos de Preguntas
- Sistema de plugins para diferentes tipos de preguntas
- Carga dinámica mediante `ModuleManager`
- Cada módulo implementa `PreguntaModule`

## Estado Actual

### ✅ Problemas Resueltos

1. **Errores de Compilación**
   - Implementados métodos abstractos faltantes en clases de dominio
   - Corregidos constructores y métodos en `Pregunta`, `PreguntaTrueFalse`, `Flashcard`, etc.
   - Ajustada lógica de creación de preguntas para usar módulos específicos

2. **Estructura de Proyecto**
   - Movidos `ModuleManager` y `CursoManager` de `kursor-ui` a `kursor-core/utils`
   - Eliminado `CursoFactory` (no se usaba)
   - Centralizadas utilidades en el módulo core

3. **Imports y Dependencias**
   - Corregidos imports en `KursorApplication.java`
   - Actualizadas referencias para apuntar a `com.kursor.core.utils`
   - Eliminados imports innecesarios en módulos de preguntas

4. **Interfaz Gráfica**
   - Corregido error de navegación (`IndexOutOfBoundsException`)
   - Implementados métodos faltantes: `crearVistaDashboard()`, `configurarNavegacion()`
   - Reordenada inicialización en método `start()` para evitar errores de acceso a componentes

5. **DTOs y Modelos**
   - Corregido constructor de `CursoPreviewDTO` (3 parámetros en lugar de 4)
   - Implementados getters/setters en `Flashcard`
   - Ajustada lógica de parseo de preguntas

### ⚠️ Problemas Conocidos

1. **Módulos de Preguntas**
   - Error: "No se encontró módulo para el tipo de pregunta: test"
   - Error: "No se encontró módulo para el tipo de pregunta: null"
   - Los módulos no se cargan correctamente desde el directorio `modules/`

2. **Carga de Cursos**
   - Algunos cursos tienen tipos de preguntas no soportados
   - Errores al cargar cursos completos debido a módulos faltantes

3. **Directorio de Módulos**
   - Warning: "El directorio de módulos no existe: modules"
   - Los JARs de módulos están en `target/classes/modules/` pero no en `modules/`

### 🟢 Funcionalidades Operativas

1. **Compilación**: ✅ Todos los módulos compilan correctamente
2. **Ejecución**: ✅ La aplicación JavaFX inicia sin errores críticos
3. **GUI**: ✅ Interfaz gráfica funcional con navegación
4. **Carga de Cursos**: ✅ Se cargan 3 cursos desde archivos YAML
5. **Navegación**: ✅ Dashboard, Cursos, Módulos, Estadísticas

## Cursos Disponibles

1. **curso_ingles** (curso_ingles.yaml)
2. **curso_ingles_b2** (curso_ingles_b2.yaml)  
3. **flashcards_ingles** (flashcards_ingles.yml)

## Comandos de Desarrollo

### Compilación
```bash
mvn clean compile
```

### Ejecución
```bash
mvn javafx:run -pl kursor-ui
```

### Compilación de Módulos Específicos
```bash
mvn clean compile -pl kursor-core
mvn clean compile -pl kursor-fillblanks-module
mvn clean compile -pl kursor-flashcard-module
mvn clean compile -pl kursor-multiplechoice-module
mvn clean compile -pl kursor-truefalse-module
```

## Próximos Pasos Prioritarios

### 1. Arreglar Carga de Módulos de Preguntas
- **Problema**: Los módulos no se cargan correctamente
- **Solución**: Verificar configuración de `ModuleManager` y rutas de carga
- **Archivos a revisar**: 
  - `ModuleManager.java`
  - `PreguntaModule` implementations
  - Configuración de servicios en `META-INF/services/`

### 2. Crear Directorio de Módulos
- **Problema**: Warning sobre directorio `modules/` inexistente
- **Solución**: Crear directorio y copiar JARs de módulos
- **Comando**: `mkdir modules && copy target\classes\modules\*.jar modules\`

### 3. Revisar Tipos de Preguntas en Cursos
- **Problema**: Cursos usan tipos "test" y "null" no soportados
- **Solución**: Mapear tipos de preguntas a módulos existentes
- **Archivos a revisar**: Archivos YAML de cursos

### 4. Implementar Funcionalidad de Preguntas
- **Estado**: Módulos compilan pero no se cargan dinámicamente
- **Próximo paso**: Hacer que los módulos funcionen en tiempo de ejecución

### 5. Mejorar Manejo de Errores
- **Estado**: Errores se muestran en consola pero no en GUI
- **Próximo paso**: Implementar diálogos de error en la interfaz

## Archivos Críticos para el Desarrollo

### Core
- `kursor-core/src/main/java/com/kursor/core/util/ModuleManager.java`
- `kursor-core/src/main/java/com/kursor/core/util/CursoManager.java`
- `kursor-core/src/main/java/com/kursor/core/service/CursoPreviewService.java`

### UI
- `kursor-ui/src/main/java/com/kursor/ui/KursorApplication.java`

### Módulos
- `kursor-fillblanks-module/src/main/java/com/kursor/fillblanks/FillBlanksModule.java`
- `kursor-flashcard-module/src/main/java/com/kursor/flashcard/FlashcardModule.java`
- `kursor-multiplechoice-module/src/main/java/com/kursor/multiplechoice/MultipleChoiceModule.java`
- `kursor-truefalse-module/src/main/java/com/kursor/truefalse/TrueFalseModule.java`

### Configuración
- `kursor-*-module/src/main/resources/META-INF/services/com.kursor.core.PreguntaModule`

## Notas Técnicas

### JavaFX
- Versión: 17.0.2
- Plugin Maven: javafx-maven-plugin:0.0.8
- Configuración en `pom.xml` de kursor-ui

### Maven
- Versión Java: 17
- Estructura multi-módulo
- Dependencias entre módulos configuradas

### Logging
- Sistema de logging propio en `Logger.java`
- Logs se escriben en `kursor-ui/log/kursor.log`

## Estado de la GUI

### Funcionalidades Implementadas
- ✅ Barra de navegación con 4 secciones
- ✅ Dashboard con acciones rápidas y cursos recientes
- ✅ Vista de cursos con lista y detalles
- ✅ Vista de módulos (placeholder)
- ✅ Vista de estadísticas (placeholder)
- ✅ Navegación entre vistas
- ✅ Carga de cursos desde archivos YAML

### Funcionalidades Pendientes
- ❌ Funcionalidad real de módulos de preguntas
- ❌ Ejecución de preguntas
- ❌ Sistema de progreso y estadísticas
- ❌ Gestión de sesiones de aprendizaje
- ❌ Sistema de flashcards funcional

## Comandos Útiles para Debugging

### Ver Logs
```bash
tail -f kursor-ui/log/kursor.log
```

### Verificar Módulos Cargados
```bash
ls -la modules/
ls -la kursor-ui/target/classes/modules/
```

### Limpiar y Recompilar Todo
```bash
mvn clean compile -DskipTests
```

## Contacto y Referencias

- **Documentación técnica**: `doc/tecnica/`
- **Patrones de diseño**: `doc/patrones-diseno-curso.md`
- **Modelo de dominio**: `doc/pds-modelo-de-dominio.md`
- **Casos de uso**: `doc/pds-casos-de-uso.md`

---

**Nota**: Este documento debe actualizarse cada vez que se realicen cambios significativos en el proyecto para mantener un registro preciso del estado actual. 