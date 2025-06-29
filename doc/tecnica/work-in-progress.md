# Work in Progress: Refactoring de Validación y Navegación de Preguntas

## **Problema Identificado**

### **Descripción del Error:**
Cuando la verificación de una pregunta resulta negativa, se muestra un texto "ERROR" pero no se recupera la navegación.  
El usuario queda "atascado" sin poder continuar con el curso.

### **Síntomas Específicos:**
1. Respuesta incorrecta → Se muestra "Incorrecto" pero no permite volver a la dinámica del curso
2. No se avanza a la siguiente pregunta automáticamente
3. No se actualiza la barra de progreso
4. Los botones de la UI son genéricos, no específicos del tipo de pregunta
5. La validación está centralizada en `CursoInterfaceView`, no en los módulos correspondientes

## **Análisis del Problema Actual**

### **Lo que está mal ahora:**

#### **1. Centralización Excesiva:**
- `CursoInterfaceView` maneja toda la lógica de validación y UI
- Los módulos solo crean la vista de la pregunta, no la interacción completa

#### **2. Botones Genéricos:**
- Los botones "Verificar" y "Siguiente" son genéricos
- No son específicos del tipo de pregunta
- No permiten flujos de interacción personalizados

#### **3. Validación Centralizada:**
- La validación se hace en la vista, no en el módulo correspondiente
- Los módulos no tienen control sobre su propia lógica de validación
- Difícil implementar validaciones específicas por tipo de pregunta

#### **4. Progreso No Actualizado:**
- La barra de progreso no se actualiza correctamente
- No hay sincronización entre la validación y el progreso

#### **5. Falta de Flexibilidad:**
- Cada tipo de pregunta puede necesitar diferentes flujos de interacción
- Difícil agregar nuevos tipos de pregunta con comportamientos únicos

## **Planteamiento Propuesto - REVISADO**

### **Principio Fundamental:**
**Los módulos deben tener acceso a toda (o casi toda) la UI para poder controlar completamente su experiencia de usuario.**

### **Estructura de UI Propuesta:**

#### **UI General (CursoInterfaceView):**
```
┌─────────────────────────────────────────────────────────┐
│ CABECERA (Accesible por módulos)                        │
│ - Información del curso                                 │
│ - Bloque actual                                         │
│ - Pregunta en curso                                     │
│ - Indicador de progreso (actualizable por módulos)      │
├─────────────────────────────────────────────────────────┤
│ CONTENIDO CENTRAL (Controlado por módulos)              │
│ - Enunciado de la pregunta                              │
│ - Forma de resolver la pregunta                         │
│ - Elementos interactivos específicos del tipo           │
├─────────────────────────────────────────────────────────┤
│ PIE - BOTONERA (Accesible por módulos)                  │
│ - Botones específicos del módulo (izquierda)            │
│ - Botón "Terminar" (derecha, siempre disponible)        │
└─────────────────────────────────────────────────────────┘
```

### **Responsabilidades Redefinidas:**

#### **CursoInterfaceController → Responsable de:**
- **Coordinación general:** Orquestar todo el flujo de ejecución del curso
- **Gestión de estrategias:** Crear y configurar estrategias de aprendizaje
- **Inicialización:** Preparar sesión y cargar curso completo
- **Navegación entre preguntas:** Controlar el flujo de preguntas según estrategia
- **Gestión de sesión:** Coordinar con CursoSessionManager para persistencia
- **Integración de componentes:** Conectar CursoInterfaceView con módulos y estrategias

#### **CursoInterfaceView → Responsable de:**
- **Estructura general:** Proporcionar contenedores para cabecera, contenido y pie
- **Gestión de sesión:** Guardado automático de progreso
- **Coordinación:** Comunicación con módulos y estrategias
- **Navegación:** Control de flujo entre preguntas
- **Botón "Terminar":** Siempre disponible en el pie

#### **Módulo de Pregunta → Responsable de:**
- **Cabecera:** Actualizar progreso, barra de progreso y otros indicadores
- **Contenido:** Mostrar enunciado y forma de resolver la pregunta
- **Pie:** Insertar sus propios botones (antes del botón "Terminar")
- **Validación:** Lógica de validación específica del tipo
- **Resultados:** Mostrar resultados específicos del tipo
- **Eventos:** Notificar al contenedor sobre acciones del usuario

## **Sistema de Guardado Automático**

### **Estados de Sesión:**
```java
public enum EstadoSesion {
    EN_CURSO,        // Sesión activa
    GUARDADA,        // Progreso guardado exitosamente
    DESCARTADA,      // Sesión cancelada por el usuario
    COMPLETADA       // Curso terminado exitosamente
}
```

### **Estados de Pregunta Individual (PreguntaSesion):**
```java
public enum EstadoPregunta {
    SIN_CONTESTAR,  // Pregunta no ha sido respondida
    CORRECTA,       // Respuesta correcta
    INCORRECTA      // Respuesta incorrecta
}
```

### **Guardado Automático (AUTOSAVE):**
- **Al presentar pregunta:** Estado "SIN_CONTESTAR" - Guardar posición actual
- **Al contestar pregunta:** Estado "CONTESTADA" - Guardar respuesta y resultado
- **Después de guardar:** Estado "GUARDADA" en sesión general
- **Recuperación:** En caso de corte de luz o cierre accidental
- **Frecuencia:** Inmediato en ambos momentos (presentar y contestar)

### **Botón "Terminar":**
- **Ubicación:** Pie de la ventana (siempre visible)
- **Comportamiento:** 
  - Preguntar si guardar progreso
  - Marcar sesión como "DESCARTADA" si no guardar
  - Cerrar ventana de curso
- **Evento de cierre:** Capturar y redirigir al mismo comportamiento que "Terminar"

## **Flujo Propuesto - REVISADO**

```
1. CursoInterfaceController inicia curso y crea estrategia
2. CursoInterfaceController inicializa CursoSessionManager
3. CursoInterfaceController crea CursoInterfaceView con estructura general
4. CursoInterfaceView carga módulo correspondiente
5. Módulo configura toda la UI (cabecera + contenido + pie)
6. AUTOSAVE: Guardar posición actual (PreguntaSesion: SIN_CONTESTAR)
7. Usuario interactúa con contenido del módulo
8. Módulo valida respuesta según su lógica específica
9. Módulo muestra resultado específico del tipo
10. AUTOSAVE: Guardar respuesta y resultado (PreguntaSesion: CONTESTADA)
11. Módulo notifica a CursoInterfaceView para:
    - Actualizar progreso en cabecera
    - Confirmar guardado (EstadoSesion: GUARDADA)
    - Solicitar siguiente pregunta
12. CursoInterfaceView notifica a CursoInterfaceController
13. CursoInterfaceController obtiene siguiente pregunta de la estrategia
14. CursoInterfaceView prepara siguiente pregunta (vuelve al paso 4)
```

## **Arquitectura Propuesta - REVISADA**

### **1. Interfaz de Eventos:**
```java
public interface PreguntaEventListener {
    void onRespuestaValidada(boolean esCorrecta);
    void onProgresoActualizado(int preguntasCompletadas, int totalPreguntas);
    void onSolicitarSiguientePregunta();
    void onSolicitarTerminarCurso();
}
```

### **2. Método Actualizado en PreguntaModule:**
```java
void configureQuestionUI(Pregunta pregunta, 
                        Node cabecera, 
                        Node contenido, 
                        Node pie, 
                        PreguntaEventListener listener);
```

### **3. Gestión de Sesión:**
```java
public interface SesionManager {
    void guardarProgresoAutomatico();
    void marcarSesionComoDescartada();
    void confirmarGuardado();
    EstadoSesion getEstadoActual();
}
```

### **4. Ejemplo de Implementación:**
```java
// En CursoInterfaceView
private void cargarPregunta(Pregunta pregunta) {
    // Cargar módulo
    PreguntaModule modulo = moduleManager.findModuleByQuestionType(pregunta.getTipo());
    
    // Configurar toda la UI con el módulo
    modulo.configureQuestionUI(pregunta, 
                              headerSection, 
                              contentSection, 
                              footerSection, 
                              this);
}

// En MultipleChoiceModule
public void configureQuestionUI(Pregunta pregunta, 
                               Node cabecera, 
                               Node contenido, 
                               Node pie, 
                               PreguntaEventListener listener) {
    
    // Configurar cabecera - actualizar progreso
    VBox headerBox = (VBox) cabecera;
    ProgressBar progressBar = new ProgressBar(preguntasCompletadas / totalPreguntas);
    headerBox.getChildren().set(3, progressBar); // Índice del indicador de progreso
    
    // Configurar contenido - mostrar pregunta
    VBox contentBox = (VBox) contenido;
    contentBox.getChildren().clear();
    
    Label enunciado = new Label(pregunta.getEnunciado());
    contentBox.getChildren().add(enunciado);
    
    ToggleGroup group = new ToggleGroup();
    for (String opcion : opciones) {
        RadioButton rb = new RadioButton(opcion);
        rb.setToggleGroup(group);
        contentBox.getChildren().add(rb);
    }
    
    // Configurar pie - botones específicos
    HBox footerBox = (HBox) pie;
    footerBox.getChildren().clear();
    
    Button btnVerificar = new Button("Verificar Respuesta");
    btnVerificar.setOnAction(e -> {
        String respuesta = obtenerRespuestaSeleccionada(group);
        boolean esCorrecta = validarRespuesta(pregunta, respuesta);
        
        // Mostrar resultado específico
        mostrarResultadoMultipleChoice(esCorrecta, respuestaCorrecta);
        
        // Notificar al contenedor
        listener.onRespuestaValidada(esCorrecta);
        listener.onProgresoActualizado(preguntasCompletadas + 1, totalPreguntas);
    });
    
    // Insertar botones del módulo antes del botón "Terminar"
    footerBox.getChildren().addAll(btnVerificar, new Separator(), btnTerminar);
}
```

## **Implicaciones del Cambio - REVISADAS**

### **✅ Ventajas:**

#### **1. Consistencia Visual:**
- UI uniforme para todos los tipos de pregunta
- Experiencia de usuario coherente
- Fácil mantenimiento de estilos

#### **2. Flexibilidad Controlada:**
- Módulos manejan solo su lógica específica
- UI general se mantiene estable
- Fácil agregar nuevos tipos sin afectar estructura general

#### **3. Gestión de Estado Centralizada:**
- Progreso y sesión manejados en un lugar
- Guardado automático confiable
- Recuperación de sesiones interrumpidas

#### **4. Separación Clara de Responsabilidades:**
- CursoInterfaceView: UI general y coordinación
- Módulos: Lógica específica de validación y contenido
- SesionManager: Gestión de estado y persistencia

### **⚠️ Desafíos:**

#### **1. Coordinación:**
- Definir claramente qué va en cada sección
- Manejar eventos entre módulos y contenedor
- Sincronizar estado entre componentes

#### **2. Gestión de Sesión:**
- Implementar guardado automático eficiente
- Manejar estados de sesión complejos
- Recuperación de sesiones interrumpidas

#### **3. Consistencia de Interacción:**
- Mantener flujos de interacción predecibles
- Balancear flexibilidad y consistencia

## **Preguntas para Confirmar el Enfoque - REVISADAS**

### **1. Control de UI:**
- ✅ **Confirmado:** UI general con partes comunes
- ✅ **Confirmado:** Módulos solo manejan contenido central
- ❓ ¿Los módulos pueden sugerir botones adicionales en el pie?

### **2. Gestión de Sesión:**
- ✅ **Confirmado:** Guardado automático después de cada respuesta
- ✅ **Confirmado:** Estados de sesión definidos
- ❓ ¿Frecuencia de guardado automático? (inmediato vs. con delay)

### **3. Navegación:**
- ✅ **Confirmado:** Botón "Terminar" siempre disponible
- ✅ **Confirmado:** Captura de evento de cierre de ventana
- ❓ ¿Comportamiento específico para diferentes tipos de pregunta?

### **4. Progreso:**
- ✅ **Confirmado:** Actualización automática en cabecera
- ✅ **Confirmado:** Sincronización con guardado
- ❓ ¿Información adicional en cabecera? (tiempo, puntuación, etc.)

## **Próximos Pasos Sugeridos - REVISADOS**

### **Fase 1: Diseño de Interfaces**
1. Definir interfaz `PreguntaEventListener` revisada
2. Actualizar interfaz `PreguntaModule` con `createQuestionContent`
3. Diseñar sistema de gestión de sesión
4. Definir estados de sesión y flujos

### **Fase 2: Refactoring de CursoInterfaceView**
1. Implementar estructura de UI general (cabecera + contenido + pie)
2. Implementar sistema de eventos revisado
3. Implementar gestión de sesión automática
4. Implementar captura de evento de cierre

### **Fase 3: Actualización de Módulos**
1. Actualizar un módulo de ejemplo (ej: MultipleChoice)
2. Probar el concepto con UI general
3. Iterar y refinar

### **Fase 4: Migración Completa**
1. Actualizar todos los módulos existentes
2. Implementar sistema de guardado automático
3. Pruebas exhaustivas de recuperación de sesión
4. Documentación final

## **Consideraciones Técnicas - REVISADAS**

### **Patrón de Diseño:**
- **Observer Pattern** para comunicación de eventos
- **Template Method** para estructura de UI general
- **Strategy Pattern** para diferentes tipos de validación
- **State Pattern** para gestión de estados de sesión

### **Gestión de Estado:**
- Estado de sesión centralizado en CursoInterfaceView
- Estado específico de pregunta en cada módulo
- Sincronización a través de eventos
- Persistencia automática con recuperación

### **Testing:**
- Tests unitarios para cada módulo
- Tests de integración para flujo completo
- Tests de recuperación de sesión
- Tests de gestión de estados

---

**Estado:** En análisis y diseño - REVISADO  
**Fecha:** 28 de Junio, 2025  
**Autor:** Juan José Ruiz Pérez  
**Versión:** 2.0 