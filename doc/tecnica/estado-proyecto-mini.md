# Estado del Proyecto

Para una futura interacción, estos son los aspectos más importantes que deberías recordarme:

## **Aspectos Críticos del Proyecto Kursor**

### **1. Estructura del Proyecto**
- **Multi-módulo Maven** con arquitectura de plugins
- **Módulos principales**: `kursor-core`, `kursor-ui`, y módulos de preguntas (`fillblanks`, `flashcard`, `multiplechoice`, `truefalse`)
- **Versión actual**: 1.0.0 (eliminamos el sufijo -SNAPSHOT)
- **Directorio raíz**: `C:\pds\ultima-version`

### **2. Problemas Resueltos Recientemente**
- **Rutas de cursos/módulos**: Corregidas para funcionar desde Maven (buscar en directorio padre cuando se ejecuta desde `kursor-ui`)
- **Carga de preguntas**: Implementada en `CursoPreviewService.java` - antes mostraba 0 preguntas porque no se cargaban
- **Conteo de preguntas**: Ahora funciona correctamente en la ventana modal de detalles

### **3. Estado Actual de la UI**
- **Botones implementados**: Inspeccionar, Comenzar, Reanudar, Flashcards, Estadísticas
- **Iconos**: Usando "Segoe UI Emoji" con tamaño 32 (monocromos en JavaFX)
- **Ventana modal**: Implementada para detalles de cursos
- **Documentación Javadoc**: Completa en `ModuleManager`, `CursoManager`, `KursorApplication`

### **4. Documentación Técnica**
- **Propuesta GUI Curso**: `doc/tecnica/gui-curso.md` - plan completo para implementar interfaz de cursos
- **Arquitectura definida**: CursoInterfaceController → CursoSessionManager → Módulos
- **Plan de 6 fases** para implementación

### **5. Configuración Técnica**
- **JavaFX**: Configurado con Maven plugin
- **Logging**: Implementado con Logger personalizado
- **Carga dinámica**: Módulos se cargan desde carpeta `modules/` usando ServiceLoader
- **Cursos**: Se cargan desde carpeta `cursos/` en formato YAML

### **6. Próximos Pasos Planificados**
- **Fase 1**: Implementar CursoSessionManager y entidades JPA
- **Fase 2**: Crear CursoInterfaceController
- **Fase 3**: Ventana modal con 3 secciones (cabecera, contenido, pie)
- **Fase 4**: Extender QuestionModule con nuevos métodos
- **Fase 5**: Navegación y validación
- **Fase 6**: Integración con botón "Comenzar"

### **7. Comandos Importantes**
```bash
# Ejecutar aplicación
mvn javafx:run -pl kursor-ui

# Compilar todo
mvn clean install

# Crear ZIP portable
.\create-portable-zip.ps1
```

### **8. Archivos Críticos Modificados**
- `kursor-ui/src/main/java/com/kursor/ui/KursorApplication.java` - UI principal con botones
- `kursor-core/src/main/java/com/kursor/core/service/CursoPreviewService.java` - Carga de preguntas
- `kursor-ui/src/main/java/com/kursor/ui/CursoManager.java` - Gestión de cursos
- `kursor-ui/src/main/java/com/kursor/ui/ModuleManager.java` - Gestión de módulos

### **9. Limitaciones Conocidas**
- **Emojis**: JavaFX no soporta emojis a color, solo monocromos
- **Persistencia**: No implementada aún (pendiente JPA/Hibernate)
- **Validación**: No implementada en módulos (solo estructura básica)

### **10. Contexto del Usuario**
- **Sistema**: Windows 10
- **Shell**: PowerShell
- **Preferencias**: Documentación en español, comandos Windows-compatibles
- **Enfoque**: Desarrollo incremental con documentación técnica detallada

---

**Recomendación**: En una futura interacción, simplemente menciona "continuar con el proyecto Kursor" y refiere a este documento técnico `doc/tecnica/gui-curso.md` para el contexto completo.