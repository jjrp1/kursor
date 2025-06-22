# Estado del Proyecto Kursor

**Fecha**: 18 de Junio de 2025  
**Versión**: 1.0.0  
**Estado**: En desarrollo activo  
**Autor**: Juan José Ruiz Pérez (jjrp1@um.es)

## 📊 Resumen Ejecutivo

El proyecto **Kursor** es una plataforma de formación interactiva desarrollada en Java con arquitectura multi-módulo Maven. Actualmente se encuentra en una fase de desarrollo funcional con la interfaz principal operativa y la carga dinámica de módulos implementada.

### ✅ **Estado Actual**
- **Interfaz principal**: Completamente funcional
- **Carga de módulos**: Implementada y operativa
- **Gestión de cursos**: Funcional desde archivos YAML
- **Ventana modal**: Implementada con conteo de preguntas
- **Sistema de logging**: Operativo

### 🎯 **Próximo Objetivo**
Implementación de la interfaz gráfica de cursos siguiendo el plan de 6 fases detallado en [Propuesta GUI Curso](gui-curso.md).

## 🏗️ Arquitectura del Sistema

### Estructura Multi-módulo
```
kursor-core/           # Lógica de negocio y dominio
kursor-ui/             # Interfaz gráfica JavaFX
kursor-fillblanks-module/      # Módulo de preguntas
kursor-flashcard-module/       # Módulo de flashcards
kursor-multiplechoice-module/  # Módulo de opción múltiple
kursor-truefalse-module/       # Módulo de verdadero/falso
```

### Patrones de Diseño Implementados
- **Singleton**: ModuleManager, CursoManager, Logger
- **Factory**: CursoFactory para creación de cursos
- **Builder**: CursoBuilder para construcción de objetos complejos
- **Plugin Architecture**: Carga dinámica de módulos

### Tecnologías Utilizadas
- **Java**: 17
- **JavaFX**: 17.0.2
- **Maven**: 3.x
- **Jackson**: Procesamiento YAML
- **SnakeYAML**: Parsing YAML

## 🔧 Funcionalidades Implementadas

### 1. **Gestión de Módulos**
- ✅ Carga dinámica desde carpeta `modules/`
- ✅ Detección automática de módulos disponibles
- ✅ Interfaz para listar módulos cargados
- ✅ Sistema de logging para debugging

### 2. **Gestión de Cursos**
- ✅ Carga desde archivos YAML en carpeta `cursos/`
- ✅ Parsing de configuración de cursos
- ✅ Listado de cursos disponibles
- ✅ Conteo de preguntas por tipo

### 3. **Interfaz de Usuario**
- ✅ Ventana principal con lista de cursos
- ✅ Ventana modal para detalles de cursos
- ✅ Botones de acción con iconos emoji
- ✅ Sistema de navegación básico

### 4. **Sistema de Preguntas**
- ✅ Arquitectura modular para tipos de pregunta
- ✅ Interfaces QuestionModule implementadas
- ✅ Carga de preguntas desde YAML
- ✅ Conteo correcto de preguntas por módulo

## 🚧 Problemas Resueltos Recientemente

### 1. **Rutas de Archivos**
**Problema**: Las rutas no funcionaban al ejecutar desde Maven  
**Solución**: Configuración de rutas relativas al directorio de trabajo  
**Archivos modificados**: 
- `KursorApplication.java`
- `CursoManager.java`
- `ModuleManager.java`

### 2. **Carga de Preguntas**
**Problema**: El conteo de preguntas siempre era cero  
**Solución**: Implementación de `CursoPreviewService` con carga real de preguntas  
**Archivos modificados**:
- `CursoPreviewService.java`
- `CursoManager.java`

### 3. **Iconos de Botones**
**Problema**: Los emojis no se mostraban correctamente  
**Solución**: Configuración de fuente "Segoe UI Emoji" para Windows  
**Archivos modificados**:
- `KursorApplication.java`

### 4. **Errores de Compilación**
**Problema**: Errores en la UI y carga de módulos  
**Solución**: Corrección de imports y manejo de excepciones  
**Archivos modificados**:
- Múltiples archivos de UI y core

## 📁 Estructura de Archivos

### Directorios Principales
```
C:\pds\ultima-version\
├── cursos/                    # Archivos YAML de cursos
│   ├── curso_ingles/
│   ├── curso_ingles_b2/
│   └── flashcards_ingles/
├── modules/                   # Módulos JAR compilados
│   ├── kursor-fillblanks-module-1.0.0.jar
│   ├── kursor-flashcard-module-1.0.0.jar
│   ├── kursor-multiplechoice-module-1.0.0.jar
│   └── kursor-truefalse-module-1.0.0.jar
├── kursor-core/              # Módulo principal
├── kursor-ui/                # Interfaz gráfica
└── doc/                      # Documentación
```

### Archivos de Configuración
- `pom.xml`: Configuración Maven principal
- `create-portable-zip.ps1`: Script para crear ZIP portable
- `kursor.log`: Archivo de logs de la aplicación

## 🎯 Plan de Desarrollo (6 Fases)

### **Fase 1: CursoSessionManager y Entidades JPA**
- [ ] Crear `CursoSessionManager` para gestión de sesiones
- [ ] Implementar entidades JPA para persistencia
- [ ] Configurar base de datos H2 embebida
- [ ] Crear repositorios para acceso a datos

### **Fase 2: CursoInterfaceController Básico**
- [ ] Implementar `CursoInterfaceController` básico
- [ ] Crear estructura de ventana modal
- [ ] Implementar navegación entre secciones
- [ ] Configurar eventos de botones

### **Fase 3: Ventana Modal con 3 Secciones**
- [ ] Implementar cabecera con información del curso
- [ ] Crear sección de contenido principal
- [ ] Implementar pie con controles de navegación
- [ ] Añadir validación de entrada

### **Fase 4: Extender QuestionModule**
- [ ] Añadir métodos de validación a `QuestionModule`
- [ ] Implementar renderizado de preguntas
- [ ] Crear interfaces de respuesta
- [ ] Añadir feedback visual

### **Fase 5: Navegación y Validación**
- [ ] Implementar navegación entre preguntas
- [ ] Añadir validación de respuestas
- [ ] Crear sistema de puntuación
- [ ] Implementar progreso de sesión

### **Fase 6: Integración Final**
- [ ] Integrar todas las funcionalidades
- [ ] Realizar pruebas de integración
- [ ] Optimizar rendimiento
- [ ] Documentar API final

## 🔍 Limitaciones Conocidas

### Técnicas
- **Emojis**: JavaFX no soporta emojis a color (solo monocromos)
- **Persistencia**: No implementada aún (pendiente JPA/Hibernate)
- **Validación**: Básica en módulos (pendiente implementación completa)
- **Rendimiento**: No optimizado para grandes volúmenes de datos

### Funcionales
- **Sesiones múltiples**: No soportadas aún
- **Estadísticas**: No implementadas
- **Progreso**: No persistido
- **Exportación**: No implementada
- **Configuración avanzada**: Limitada

## 🛠️ Comandos de Desarrollo

### Ejecución
```bash
# Ejecutar aplicación
mvn javafx:run -pl kursor-ui

# Compilar todo el proyecto
mvn clean install

# Crear ZIP portable
.\create-portable-zip.ps1
```

### Debugging
```bash
# Ver logs en tiempo real
Get-Content kursor.log -Wait

# Limpiar logs
Remove-Item kursor.log
```

## 📊 Métricas del Proyecto

### Código
- **Líneas de código**: ~2,500 (estimado)
- **Clases**: ~25
- **Módulos**: 6
- **Cobertura de pruebas**: Pendiente

### Funcionalidades
- **Tipos de pregunta**: 4 implementados
- **Cursos disponibles**: 3
- **Módulos cargables**: 4
- **Interfaces de usuario**: 2 (principal + modal)

## 🚀 Próximos Pasos Inmediatos

### Semana 1
1. **Implementar Fase 1**: CursoSessionManager y entidades JPA
2. **Configurar base de datos**: H2 embebida
3. **Crear repositorios**: Para acceso a datos de sesiones

### Semana 2
1. **Implementar Fase 2**: CursoInterfaceController básico
2. **Crear estructura modal**: Con 3 secciones
3. **Implementar navegación**: Entre secciones

### Semana 3
1. **Implementar Fase 3**: Ventana modal completa
2. **Añadir validación**: De entrada de usuario
3. **Crear feedback visual**: Para acciones del usuario

## 📚 Documentación Relacionada

- **[Propuesta GUI Curso](gui-curso.md)**: Plan detallado de implementación
- **[Plan de Pruebas](pruebas-plan-inicial.md)**: Estrategia de testing
- **[README Principal](../README.md)**: Documentación general del proyecto

## 🔗 Enlaces Útiles

- **Repositorio**: `C:\pds\ultima-version`
- **Logs**: `kursor.log`
- **Documentación**: `doc/tecnica/`
- **Cursos**: `cursos/`
- **Módulos**: `modules/`

---

**Nota**: Este documento se actualiza regularmente con el progreso del desarrollo. Para la información más reciente, consultar los commits del repositorio y los logs de la aplicación. 