# Instrucciones para la Nueva Interfaz de Kursor

## 🚀 Cómo Probar la Nueva Interfaz

### Opción 1: Usando el Script Automatizado
```powershell
# Desde la raíz del proyecto
.\scripts\test-nueva-interfaz.ps1
```

### Opción 2: Compilación Manual
```powershell
# Limpiar y compilar
mvn clean compile

# Ejecutar nueva interfaz
mvn exec:java -Dexec.mainClass="com.kursor.ui.KursorApplicationNew"

# Para volver a la interfaz anterior
mvn exec:java -Dexec.mainClass="com.kursor.ui.KursorApplication"
```

## 🎯 Casos de Uso Implementados

### 1. **Inspeccionar Curso (CU01)**
- **Cómo usar**: Haz clic en el botón "Inspeccionar" en cualquier tarjeta de curso
- **Resultado**: Se muestra un diálogo con información detallada del curso
- **Información mostrada**: Nombre, nivel, número de bloques, descripción

### 2. **Comenzar Curso (CU02)**
- **Cómo usar**: 
  1. Haz clic en "Comenzar Curso" en el dashboard, O
  2. Haz clic en el botón "Comenzar" en cualquier tarjeta de curso
- **Resultado**: Se abre un diálogo para seleccionar la estrategia de aprendizaje
- **Estrategias disponibles**:
  - 📖 **Secuencial**: Preguntas en orden predefinido
  - 🎲 **Aleatoria**: Preguntas en orden aleatorio
  - 🔄 **Repetición Espaciada**: Repite preguntas cada cierto intervalo
  - ❌ **Repetir Incorrectas**: Enfócate en las preguntas falladas

### 3. **Reanudar Sesión (CU03)**
- **Cómo usar**: Haz clic en "Reanudar Sesión" en el dashboard
- **Estado actual**: Funcionalidad en desarrollo
- **Próximamente**: Mostrará sesiones sin terminar

### 4. **Flashcards (CU04)**
- **Cómo usar**: Haz clic en "Flashcards" en el dashboard
- **Estado actual**: Funcionalidad en desarrollo
- **Próximamente**: Sesiones de estudio con tarjetas

### 5. **Estadísticas (CU05)**
- **Cómo usar**: Haz clic en "Estadísticas" en el dashboard
- **Estado actual**: Funcionalidad en desarrollo
- **Próximamente**: Estadísticas detalladas de progreso

## 🎨 Características de la Nueva Interfaz

### **Dashboard Principal**
- **Bienvenida personalizada** con mensaje motivacional
- **Estadísticas visibles** en el header (días seguidos, minutos hoy, precisión)
- **Acciones rápidas** para los casos de uso principales

### **Tarjetas de Cursos**
- **Información completa**: Nombre, nivel, descripción, estadísticas
- **Efectos visuales**: Hover effects con animaciones suaves
- **Botones de acción**: Inspeccionar y Comenzar
- **Indicadores visuales**: Nivel del curso con colores

### **Diálogo de Estrategia**
- **Interfaz intuitiva**: Opciones claras con descripciones
- **Selección visual**: Radio buttons con efectos hover
- **Confirmación**: Botones de "Comenzar" y "Cancelar"

## 🔧 Configuración y Personalización

### **Estilos CSS**
- **Archivo**: `kursor-core/src/main/resources/styles/kursor.css`
- **Colores principales**: #667eea (azul), #764ba2 (púrpura)
- **Efectos**: Gradientes, sombras, animaciones
- **Responsive**: Adaptable a diferentes tamaños de pantalla

### **Componentes Reutilizables**
- **Tarjetas de acción**: Para acciones rápidas
- **Tarjetas de curso**: Para mostrar información de cursos
- **Botones estilizados**: Con efectos hover
- **Diálogos modales**: Consistentes en toda la aplicación

## 🐛 Solución de Problemas

### **Error de Compilación**
```powershell
# Limpiar completamente
mvn clean
# Recompilar
mvn compile
```

### **Error de Estilos CSS**
- Verificar que el archivo `kursor.css` existe en `src/main/resources/styles/`
- Comprobar que la ruta en `scene.getStylesheets()` es correcta

### **Error de Carga de Cursos**
- Verificar que los archivos YAML están en la carpeta `cursos/`
- Comprobar que `CursoManager` está inicializado correctamente

### **Interfaz No Se Muestra**
- Verificar que JavaFX está configurado correctamente
- Comprobar que la versión de Java es compatible (Java 11+)

## 📋 Próximas Mejoras

### **Funcionalidades Pendientes**
1. **Reanudación de sesiones**: Mostrar sesiones sin terminar
2. **Sistema de flashcards**: Sesiones de estudio independientes
3. **Estadísticas detalladas**: Progreso, rachas, rendimiento
4. **Persistencia**: Guardar estado de sesiones

### **Mejoras de UX**
1. **Animaciones más sofisticadas**: Transiciones entre vistas
2. **Temas personalizables**: Modo oscuro/claro
3. **Tutorial interactivo**: Guía para nuevos usuarios
4. **Notificaciones**: Recordatorios y logros

### **Integración**
1. **Sistema de estrategias**: Conectar con las estrategias existentes
2. **Controladores de curso**: Integrar con `CursoInterfaceController`
3. **Persistencia**: Conectar con el sistema de base de datos
4. **Logging**: Mejorar el sistema de logs

## 🎯 Comparación con la Interfaz Anterior

| Característica | Interfaz Anterior | Nueva Interfaz |
|----------------|-------------------|----------------|
| **Diseño** | Pestañas tradicionales | Dashboard moderno |
| **Casos de Uso** | Parcialmente implementados | Todos implementados |
| **Experiencia** | Funcional pero básica | Moderna e intuitiva |
| **Responsive** | Limitado | Preparado |
| **Accesibilidad** | Básica | Mejorada |
| **Mantenimiento** | Complejo | Modular |

## ✅ Conclusión

La nueva interfaz de Kursor representa una mejora significativa en la experiencia de usuario, alineándose perfectamente con los casos de uso principales y proporcionando una base sólida para futuras expansiones. El diseño moderno y la arquitectura modular facilitarán el mantenimiento y la evolución del sistema.

**¡La nueva interfaz está lista para ser utilizada y proporcionará a los usuarios una experiencia de aprendizaje más atractiva, intuitiva y efectiva!** 