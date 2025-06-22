# GUI Curso - Propuesta de Implementación
## Interfaz Gráfica de Cursos para Kursor

- Versión: 2.0
- Fecha: 2025-01-27
- Autor: Juan José Ruiz Pérez <jjrp1@um.es>

---

## 1. Arquitectura General

```
KursorApplication (UI Principal)
    ↓
EstrategiaSelectionModal (Selección de estrategia)
    ↓
CursoInterfaceController (Nuevo controlador)
    ↓
CursoInterfaceModal (Modal inteligente híbrido)
    ↓
CursoSessionManager (Gestión de sesiones con estrategia)
    ↓
ModuleManager (Carga de módulos)
    ↓
QuestionModule (Plugins específicos)
```

## 2. Componentes Principales

### A. EstrategiaSelectionModal
- **Responsabilidad**: Permitir al usuario seleccionar la estrategia de aprendizaje
- **Funciones**:
  - Mostrar las 4 estrategias disponibles
  - Proporcionar descripción de cada estrategia
  - Validar selección antes de proceder
  - Integrar con CursoInterfaceController

### B. CursoInterfaceController
- **Responsabilidad**: Control principal de la interfaz de curso
- **Funciones**:
  - Gestionar el modal híbrido con las 3 secciones
  - Coordinar navegación entre bloques/preguntas
  - Delegar construcción de UI a módulos específicos
  - Gestionar validación y progreso
  - Integrar con la interfaz principal
  - Aplicar la estrategia de aprendizaje seleccionada

### C. CursoInterfaceModal (Modal Inteligente)
- **Responsabilidad**: Ventana modal híbrida para ejecución de cursos
- **Características**:
  - Modal solo para la ventana padre (no bloquea toda la aplicación)
  - Redimensionable con límites razonables (600x400 mínimo)
  - Centrado en pantalla
  - Mantiene contexto visual de la aplicación principal
  - Estructura de 3 secciones bien definidas
  - Muestra la estrategia seleccionada en la cabecera

### D. CursoSessionManager 
- **Responsabilidad**: Persistencia y gestión de sesiones con estrategia
- **Funciones**:
  - Crear/recuperar sesiones de usuario con estrategia específica
  - Guardar progreso (bloque actual, pregunta actual)
  - Persistir respuestas y resultados
  - Calcular estadísticas de progreso
  - Aplicar lógica de la estrategia seleccionada

### E. Ventana Modal (CursoInterfaceModal)
- **Estructura**:
  ```
  ┌─────────────────────────────────┐
  │ CABECERA (Tamaño fijo)          │
  │ - Título curso + estrategia     │
  │ - Bloque actual + progreso      │
  │ - Barra progreso + contador     │
  ├─────────────────────────────────┤
  │ CONTENIDO (Tamaño flexible)     │
  │ - UI específica del módulo      │
  │ - Botones de interacción        │
  ├─────────────────────────────────┤
  │ PIE (Tamaño fijo)               │
  │ - Botones: Verificar/Siguiente  │
  │ - Botón: Terminar               │
  └─────────────────────────────────┘
  ```

## 3. Flujo de Navegación

```
1. Usuario hace clic en "Comenzar" en la interfaz principal
   ↓
2. Se muestra EstrategiaSelectionModal con las 4 estrategias:
   - Secuencial: Preguntas en orden secuencial
   - Aleatoria: Preguntas en orden aleatorio  
   - Repetición Espaciada: Optimizada para retención
   - Repetir Incorrectas: Enfocada en errores previos
   ↓
3. Usuario selecciona estrategia y hace clic en "Comenzar"
   ↓
4. CursoInterfaceController se inicializa con la estrategia seleccionada
   ↓
5. CursoSessionManager recupera/crea sesión con la estrategia
   ↓
6. Se muestra CursoInterfaceModal con:
   - Cabecera: info curso + estrategia seleccionada + progreso
   - Contenido: UI del módulo específico
   - Pie: botones de navegación
   ↓
7. Usuario interactúa con la pregunta (según estrategia seleccionada)
   ↓
8. Según tipo de pregunta:
   - Flashcard: Botón "Siguiente" (sin validación)
   - Otros: Botón "Verificar" → Validación → "Siguiente"
   ↓
9. Se guarda progreso y se avanza según la estrategia
   ↓
10. Al terminar: Modal se cierra y se actualiza la interfaz principal
    ↓
11. Usuario ve estadísticas actualizadas en la lista de cursos
```

## 4. Estrategias de Aprendizaje Disponibles

### A. Secuencial
- **Descripción**: Preguntas en orden secuencial
- **Comportamiento**: Recorre los bloques y preguntas en orden
- **Uso**: Ideal para aprendizaje estructurado y progresivo

### B. Aleatoria
- **Descripción**: Preguntas en orden aleatorio
- **Comportamiento**: Selecciona preguntas de forma aleatoria
- **Uso**: Ideal para repaso general y evitar memorización de orden

### C. Repetición Espaciada
- **Descripción**: Optimizada para retención a largo plazo
- **Comportamiento**: Basada en algoritmos de repetición espaciada
- **Uso**: Ideal para memorización efectiva y retención

### D. Repetir Incorrectas
- **Descripción**: Enfocada en preguntas falladas anteriormente
- **Comportamiento**: Prioriza preguntas con respuestas incorrectas
- **Uso**: Ideal para mejorar áreas débiles y corregir errores

## 5. Interfaz QuestionModule Extendida

```java
public interface QuestionModule {
    // Métodos existentes...
    
    // Nuevos métodos para la interfaz de curso
    Node createQuestionUI(Map<String, Object> questionData);
    boolean validateAnswer(String userAnswer);
    String getQuestionType();
    boolean requiresValidation(); // false para flashcards
}
```

## 6. Gestión de Sesiones (JPA)

### Entidades (usando las existentes):
```java
// Usar las entidades existentes en kursor-core:
// - Sesion.java (ya implementada)
// - RespuestaPregunta.java (ya implementada)
// - EstadoSesion.java (ya implementada)
// - EstadoEstrategia.java (ya implementada)
```

## 7. Estructura del Modal de Selección de Estrategia

### EstrategiaSelectionModal:
```java
public class EstrategiaSelectionModal extends Stage {
    public EstrategiaSelectionModal(Window owner, CursoDTO curso) {
        super();
        
        // Configuración modal
        this.initOwner(owner);
        this.initModality(Modality.WINDOW_MODAL);
        this.setResizable(false);
        this.setWidth(500);
        this.setHeight(400);
        
        // Centrar en la pantalla
        this.centerOnScreen();
        
        // Crear contenido
        this.setScene(crearEscena(curso));
    }
}
```

### Estructura del Modal:
```
┌─────────────────────────────────┐
│ 🎯 Seleccionar Estrategia       │
│ Curso: [Nombre del Curso]       │
├─────────────────────────────────┤
│                                 │
│ ○ Secuencial                   │
│   Preguntas en orden secuencial │
│                                 │
│ ○ Aleatoria                    │
│   Preguntas en orden aleatorio  │
│                                 │
│ ○ Repetición Espaciada         │
│   Optimizada para retención     │
│                                 │
│ ○ Repetir Incorrectas          │
│   Enfocada en errores previos   │
│                                 │
├─────────────────────────────────┤
│ [Cancelar]    [Comenzar]       │
└─────────────────────────────────┘
```

## 8. Estructura del Modal Híbrido

### Configuración del Modal:
```java
public class CursoInterfaceModal extends Stage {
    public CursoInterfaceModal(Window owner, EstrategiaAprendizaje estrategia) {
        super();
        
        // Configuración híbrida
        this.initOwner(owner);
        this.initModality(Modality.WINDOW_MODAL); // Modal solo para la ventana padre
        this.setResizable(true);
        this.setMinWidth(600);
        this.setMinHeight(400);
        
        // Centrar en la pantalla
        this.centerOnScreen();
        
        // Mantener siempre visible
        this.setAlwaysOnTop(false);
        
        // Guardar estrategia
        this.estrategia = estrategia;
    }
}
```

### Cabecera:
- Título del curso (ej: "Inglés básico")
- Estrategia seleccionada (ej: "Estrategia: Repetición Espaciada")
- Bloque actual (ej: "Bloque 1 de 4")
- Barra de progreso (pregunta actual / total preguntas del bloque)
- Contador numérico centrado

### Contenido:
- Área flexible que se adapta al contenido del módulo
- Mínimo: 300px altura
- Máximo: 600px altura
- Scroll automático si es necesario

### Pie:
- Botón "Verificar" (para preguntas que requieren validación)
- Botón "Siguiente" (para flashcards o después de verificar)
- Botón "Terminar" (cierra sesión y vuelve a la lista)

## 9. Integración con Interfaz Principal

### Modificaciones en KursorApplication:
```java
// Añadir botón "Comenzar" en la lista de cursos
// Integrar con EstrategiaSelectionModal
// Integrar con CursoInterfaceController
// Actualizar estadísticas después de completar curso
```

### Flujo de Integración:
1. **Selección de Curso**: Usuario selecciona curso en la lista principal
2. **Botón Comenzar**: Aparece botón "Comenzar" para el curso seleccionado
3. **Modal de Estrategia**: Al hacer clic, se abre EstrategiaSelectionModal
4. **Selección de Estrategia**: Usuario selecciona estrategia y confirma
5. **Apertura Modal de Curso**: Se abre CursoInterfaceModal con la estrategia
6. **Ejecución**: Usuario completa el curso con la estrategia seleccionada
7. **Cierre y Actualización**: Modal se cierra y se actualizan estadísticas en la lista

## 10. Consideraciones Técnicas

### Responsabilidades de los Módulos:
- **Validación YAML**: Cada módulo valida su estructura específica
- **Construcción UI**: Cada módulo crea su interfaz específica
- **Validación respuestas**: Cada módulo implementa su lógica de validación
- **Interacción**: Cada módulo maneja eventos de su UI

### Persistencia:
- **JPA/Hibernate** para entidades de sesión (ya implementado)
- **Guardado automático** en cada interacción
- **Recuperación de sesión** al reiniciar
- **Persistencia de estrategia** seleccionada

### Manejo de Errores:
- Validación de datos YAML antes de mostrar
- Manejo de errores de módulos
- Recuperación de sesiones corruptas
- Validación de estrategia seleccionada

## 11. Ventajas del Enfoque Híbrido

### Experiencia de Usuario:
- **Contexto Visual**: La lista de cursos permanece visible/actualizada
- **Navegación Fluida**: Fácil regreso al punto de partida
- **Foco Claro**: Usuario se concentra en la tarea actual
- **Menos Confusión**: No hay múltiples ventanas abiertas
- **Personalización**: Usuario puede elegir su estrategia preferida

### Aspectos Técnicos:
- **Menos Acoplamiento**: Modal independiente pero conectado
- **Más Flexible**: Redimensionable según necesidades
- **Fácil Testing**: Componente aislado pero integrado
- **Escalable**: Fácil añadir nuevas funcionalidades
- **Estrategias Modulares**: Fácil añadir nuevas estrategias

## 12. Plan de Implementación

### Fase 1: EstrategiaSelectionModal
- Crear modal de selección de estrategia
- Implementar radio buttons para cada estrategia
- Añadir descripciones y validación
- Integrar con CursoInterfaceController

### Fase 2: CursoInterfaceModal básico
- Crear CursoInterfaceModal con estructura de 3 secciones
- Configurar modal híbrido (redimensionable, centrado)
- Implementar layout básico
- Mostrar estrategia seleccionada en cabecera

### Fase 3: CursoInterfaceController
- Crear controlador principal
- Implementar lógica de navegación básica
- Integrar con ModuleManager existente
- Aplicar lógica de estrategia seleccionada

### Fase 4: Extender QuestionModule
- Añadir nuevos métodos a la interfaz
- Actualizar módulos existentes
- Implementar validación específica por tipo

### Fase 5: Integración con CursoSessionManager
- Conectar con persistencia existente
- Implementar guardado automático
- Manejar recuperación de sesiones
- Integrar estrategia en la persistencia

### Fase 6: Integración con Interfaz Principal
- Añadir botón "Comenzar" en KursorApplication
- Conectar apertura del modal de estrategia
- Implementar actualización de estadísticas

### Fase 7: Navegación y validación
- Implementar lógica de verificación
- Manejar diferentes tipos de preguntas
- Integrar con persistencia
- Aplicar lógica de estrategia en la navegación

### Fase 8: Pruebas y depuración
- Testing completo del flujo
- Depuración de errores
- Optimización de rendimiento
- Validación de todas las estrategias

## 13. Archivos a Crear/Modificar

### Nuevos archivos:
- `kursor-core/src/main/java/com/kursor/ui/EstrategiaSelectionModal.java`
- `kursor-core/src/main/java/com/kursor/ui/CursoInterfaceController.java`
- `kursor-core/src/main/java/com/kursor/ui/CursoInterfaceModal.java`

### Archivos a modificar:
- `kursor-core/src/main/java/com/kursor/modules/PreguntaModule.java` (extender interfaz)
- `kursor-core/src/main/java/com/kursor/ui/KursorApplication.java` (integrar botón Comenzar)
- `kursor-core/src/main/java/com/kursor/ui/CursoSessionManager.java` (integrar estrategia)
- Todos los módulos existentes (añadir nuevos métodos)

## 14. Dependencias

### Ya incluidas en el proyecto:
- JavaFX (para la interfaz)
- JPA/Hibernate (para persistencia)
- SQLite (base de datos)

## 15. Notas de Implementación

- El modal debe ser responsive y adaptarse a diferentes tamaños de pantalla
- Los módulos deben ser thread-safe para evitar problemas de concurrencia
- La persistencia ya está configurada y funcionando
- Se debe implementar logging detallado para debugging
- La interfaz debe ser accesible (teclado, lectores de pantalla)
- El modal debe mantener el contexto visual de la aplicación principal
- La estrategia seleccionada debe ser persistida y recuperable
- Cada estrategia debe tener su propia lógica de navegación

---

**Estado**: Propuesta actualizada con selección de estrategia
**Próximo paso**: Implementación de EstrategiaSelectionModal
**Responsable**: Equipo de desarrollo Kursor 