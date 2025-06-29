# Roadmap: Mecánica de la Aplicación Kursor

## 🎯 **Flujo Principal de Realización de un Curso**

La aplicación sigue un flujo bien estructurado para la realización de cursos:

### **1. Inicio desde la Interfaz Principal**
- El usuario selecciona un curso desde la lista principal (`MainView`)
- Elige un bloque específico del curso mediante un diálogo de selección
- El sistema inicia el proceso de ejecución

### **2. Selección de Estrategia**
- Se muestra un modal informativo (`EstrategiaSelectionModal`) donde el usuario puede ver las estrategias disponibles
- **Nota importante**: Actualmente el sistema ignora la selección del usuario y usa la estrategia "Secuencial" por defecto
- Se crea un `CursoInterfaceController` para coordinar todo el proceso

### **3. Inicialización de la Sesión**
- Se crea un `CursoSessionManager` que gestiona el estado de la sesión
- Se inicializa la estrategia de aprendizaje seleccionada
- Se prepara la vista de ejecución del curso (`CursoInterfaceView`)

## 🔄 **Arquitectura Modular de Preguntas**

La aplicación utiliza un sistema modular muy inteligente:

### **Módulos de Preguntas**
- **Multiple Choice**: Preguntas de opción múltiple
- **True/False**: Preguntas de verdadero/falso  
- **Flashcards**: Tarjetas de memoria
- **Fill Blanks**: Completar espacios en blanco

### **Carga Dinámica de Módulos**
- Cada pregunta tiene un `tipo` asociado
- El `ModuleManager` encuentra automáticamente el módulo correspondiente
- El módulo se encarga de:
  - Configurar la interfaz de usuario
  - Validar las respuestas del usuario
  - Manejar la lógica específica del tipo de pregunta

## 📊 **Gestión de Sesiones y Persistencia**

### **Estado Actual de Persistencia**
**⚠️ IMPORTANTE**: La persistencia en base de datos **NO está completamente implementada**:

- **`CursoSessionManager`**: Actualmente solo almacena en memoria (Map y List)
- **Entidades JPA**: Existen (`Sesion`, `PreguntaSesion`, `EstadoEstrategia`) pero no se usan
- **Repositorios**: Están implementados pero no conectados al flujo principal

### **Lo que SÍ funciona en memoria**:
```java
// En CursoSessionManager
private final Map<String, Boolean> respuestas;  // preguntaId -> esCorrecta
private final List<String> preguntasRespondidas;
private int bloqueActual, preguntaActual;
```

### **Lo que está preparado para BD pero no se usa**:
- Entidad `Sesion` con todos los campos necesarios
- Entidad `PreguntaSesion` para respuestas individuales
- Entidad `EstadoEstrategia` para persistir el estado del iterador
- Repositorios JPA completos

## 🎮 **Flujo de Ejecución de Preguntas**

### **1. Carga de Pregunta**
```java
preguntaActual = estrategia.primeraPregunta(); // o siguientePregunta()
moduloActual = moduleManager.findModuleByQuestionType(pregunta.getTipo());
moduloActual.configureCompleteUI(pregunta, header, content, footer, listener);
```

### **2. Respuesta del Usuario**
- El módulo maneja la validación específica
- Notifica el resultado mediante `PreguntaEventListener`
- La vista actualiza el progreso y estadísticas

### **3. Transición**
- Se verifica si hay más preguntas con `estrategia.hayMasPreguntas()`
- Se carga la siguiente pregunta automáticamente
- Se actualiza la barra de progreso

## 🧠 **Estrategias de Aprendizaje**

La aplicación soporta múltiples estrategias:
- **Secuencial**: Preguntas en orden
- **Aleatoria**: Preguntas en orden aleatorio
- **Repetición Espaciada**: Basada en algoritmos de memoria
- **Repetir Incorrectas**: Enfocada en errores

---

# 🚧 **DEFICIENCIAS CRÍTICAS IDENTIFICADAS**

## **1. Desconexión Total entre `CursoSessionManager` y `CursoInterfaceView`**
- **Problema**: `CursoInterfaceView` NO recibe el `sessionManager` en su constructor
- **Evidencia**: 
  ```java
  // CursoInterfaceView constructor solo recibe: (Window owner, CursoDTO curso, EstrategiaAprendizaje estrategia)
  // NO recibe CursoSessionManager
  ```
- **Impacto**: Las líneas comentadas en `onRespuestaValidada()` nunca funcionarán:
  ```java
  // TODO: Guardar progreso automáticamente
  // sessionManager.guardarProgreso(preguntaActual, esCorrecta);
  ```

## **2. Falta de Integración con JPA**
- **Problema**: `CursoSessionManager` tiene métodos `cargarProgreso()` y `guardarProgreso()` que son placeholders
- **Evidencia**: 
  ```java
  private void guardarProgreso() {
      // TODO: Implementar guardado en base de datos con JPA
      // Por ahora, solo se registra en el log
  }
  ```

## **3. Ausencia de Gestión de Sesiones en BD**
- **Problema**: No se crean registros de `Sesion` al iniciar un curso
- **Evidencia**: `CursoInterfaceController.inicializarSessionManager()` solo crea el objeto en memoria

## **4. Falta de Recuperación de Sesiones**
- **Problema**: No hay mecanismo para detectar sesiones incompletas al iniciar la aplicación
- **Evidencia**: `MainController` no busca sesiones `EN_CURSO` al inicializar

## **5. Inconsistencia en el Manejo de Bloques**
- **Problema**: El roadmap menciona selección de bloque, pero `CursoSessionManager` no maneja `bloqueId`
- **Evidencia**: `CursoSessionManager` solo tiene `cursoId` y `estrategiaSeleccionada`

## **6. Falta de Persistencia de Estado de Estrategia**
- **Problema**: No se guarda el estado del iterador de la estrategia
- **Evidencia**: `EstadoEstrategia` existe pero no se usa en el flujo principal

---

# 📋 **PLAN DE MEJORA E IMPLEMENTACIÓN POR FASES**

## **FASE 1: Conexión Básica y Persistencia Inicial** 
**Duración estimada: 2-3 semanas**

### **Ciclo 1.1: Detección y Análisis**
- [ ] Auditar todas las desconexiones entre componentes
- [ ] Identificar puntos de integración faltantes
- [ ] Documentar flujo de datos actual vs. deseado

### **Ciclo 1.2: Corrección de Arquitectura**
- [ ] Modificar `CursoInterfaceView` para recibir `CursoSessionManager`
- [ ] Actualizar `CursoInterfaceController` para pasar el sessionManager
- [ ] Implementar inyección de dependencias básica

### **Ciclo 1.3: Implementación de Persistencia Básica**
- [ ] Conectar `CursoSessionManager` con `SesionRepository`
- [ ] Implementar creación de sesión al iniciar curso
- [ ] Implementar guardado de respuestas en `PreguntaSesion`

### **Ciclo 1.4: Pruebas y Validación**
- [ ] Tests unitarios para flujo de persistencia
- [ ] Tests de integración para sesiones completas
- [ ] Validación de datos en BD después de sesiones

## **FASE 2: Recuperación y Gestión de Estado**
**Duración estimada: 2-3 semanas**

### **Ciclo 2.1: Detección de Sesiones Incompletas**
- [ ] Implementar búsqueda de sesiones `EN_CURSO` al iniciar app
- [ ] Crear interfaz para mostrar sesiones recuperables
- [ ] Implementar lógica de decisión (nueva vs. reanudar)

### **Ciclo 2.2: Corrección de Estados**
- [ ] Implementar gestión de estados de sesión (EN_CURSO, COMPLETADA, PAUSADA)
- [ ] Actualizar estados automáticamente según progreso
- [ ] Manejar transiciones de estado

### **Ciclo 2.3: Implementación de Recuperación**
- [ ] Cargar estado de estrategia desde `EstadoEstrategia`
- [ ] Restaurar posición exacta en el curso
- [ ] Recuperar respuestas previas

### **Ciclo 2.4: Pruebas de Recuperación**
- [ ] Simular cierres abruptos de aplicación
- [ ] Validar recuperación correcta de sesiones
- [ ] Tests de concurrencia y estados inconsistentes

## **FASE 3: Gestión Avanzada de Bloques y Estrategias**
**Duración estimada: 2-3 semanas**

### **Ciclo 3.1: Análisis de Gestión de Bloques**
- [ ] Revisar modelo de dominio vs. implementación actual
- [ ] Identificar inconsistencias en manejo de bloques
- [ ] Diseñar solución unificada

### **Ciclo 3.2: Corrección de Modelo de Bloques**
- [ ] Actualizar `CursoSessionManager` para manejar `bloqueId`
- [ ] Modificar entidad `Sesion` si es necesario
- [ ] Implementar lógica de selección de bloque

### **Ciclo 3.3: Implementación de Estado de Estrategias**
- [ ] Conectar `EstadoEstrategia` con el flujo principal
- [ ] Implementar serialización/deserialización de estado
- [ ] Integrar con `EstrategiaAprendizaje`

### **Ciclo 3.4: Validación Completa**
- [ ] Tests de estrategias con persistencia
- [ ] Validación de recuperación con diferentes estrategias
- [ ] Performance tests con sesiones largas

## **FASE 4: Optimización y Robustez**
**Duración estimada: 1-2 semanas**

### **Ciclo 4.1: Detección de Problemas de Performance**
- [ ] Profiling de operaciones de BD
- [ ] Identificar cuellos de botella
- [ ] Analizar uso de memoria

### **Ciclo 4.2: Corrección de Performance**
- [ ] Implementar cache de sesiones activas
- [ ] Optimizar consultas de BD
- [ ] Implementar batch operations para respuestas

### **Ciclo 4.3: Implementación de Robustez**
- [ ] Manejo de errores de BD
- [ ] Rollback automático en caso de fallos
- [ ] Logging detallado para debugging

### **Ciclo 4.4: Validación Final**
- [ ] Stress tests con múltiples sesiones
- [ ] Tests de recuperación ante fallos
- [ ] Validación de integridad de datos

---

# 🔄 **METODOLOGÍA DE CICLOS**

## **Estructura de Cada Ciclo:**
1. **Detección** (20% del tiempo)
   - Análisis del problema
   - Identificación de causas raíz
   - Documentación de requerimientos

2. **Corrección** (30% del tiempo)
   - Diseño de la solución
   - Refactoring de código existente
   - Preparación de infraestructura

3. **Implementación** (35% del tiempo)
   - Desarrollo de nueva funcionalidad
   - Integración con componentes existentes
   - Configuración de BD y JPA

4. **Pruebas y Validación** (15% del tiempo)
   - Tests unitarios
   - Tests de integración
   - Validación manual
   - Documentación de cambios

## **Criterios de Éxito por Ciclo:**
- ✅ Código compila sin errores
- ✅ Tests unitarios pasan (cobertura >80%)
- ✅ Tests de integración pasan
- ✅ Funcionalidad validada manualmente
- ✅ Documentación actualizada

---

# 📊 **MÉTRICAS DE SEGUIMIENTO**

## **Métricas Técnicas:**
- Cobertura de tests por fase
- Número de bugs detectados vs. corregidos
- Tiempo de respuesta de operaciones de BD
- Uso de memoria en sesiones largas

## **Métricas de Progreso:**
- Tareas completadas vs. planificadas
- Desviación de tiempo estimado
- Calidad del código (sonar, linting)
- Satisfacción del equipo

---

# 🎯 **BENEFICIOS ESPERADOS**

## **Inmediatos (Fase 1):**
- Persistencia real de progreso del usuario
- No más pérdida de datos al cerrar aplicación
- Base sólida para funcionalidades avanzadas

## **Medio Plazo (Fases 2-3):**
- Recuperación automática de sesiones interrumpidas
- Gestión completa de estados de aprendizaje
- Soporte robusto para múltiples estrategias

## **Largo Plazo (Fase 4):**
- Sistema escalable para múltiples usuarios
- Performance optimizada para cursos grandes
- Robustez ante fallos del sistema

---

# 🔗 **ARCHIVOS CLAVE DEL SISTEMA**

## **Controladores**
- `MainController.java`: Controlador principal de la aplicación
- `CursoInterfaceController.java`: Controlador de ejecución de cursos
- `CursoSessionManager.java`: Gestor de sesiones (actualmente en memoria)

## **Vistas**
- `MainView.java`: Interfaz principal
- `CursoInterfaceView.java`: Vista de ejecución de cursos
- `EstrategiaSelectionModal.java`: Modal de selección de estrategia

## **Persistencia**
- `Sesion.java`: Entidad JPA de sesiones
- `PreguntaSesion.java`: Entidad JPA de respuestas
- `EstadoEstrategia.java`: Entidad JPA del estado de estrategias
- `SesionRepository.java`: Repositorio de sesiones

## **Dominio**
- `EstrategiaAprendizaje.java`: Interfaz de estrategias
- `PreguntaModule.java`: Interfaz de módulos de preguntas
- `ModuleManager.java`: Gestor de módulos

---

# 📝 **NOTAS DE IMPLEMENTACIÓN**

## **Prioridades de Desarrollo:**
1. **ALTA**: Fase 1 - Conexión básica y persistencia
2. **ALTA**: Fase 2 - Recuperación de sesiones
3. **MEDIA**: Fase 3 - Gestión avanzada de bloques
4. **BAJA**: Fase 4 - Optimización y robustez

## **Riesgos Identificados:**
- Cambios en la arquitectura pueden afectar módulos existentes
- Migración de datos si hay sesiones existentes
- Compatibilidad con estrategias de aprendizaje existentes

## **Dependencias Técnicas:**
- JPA/Hibernate configurado correctamente
- Base de datos SQLite accesible
- Tests unitarios funcionando
- Logging configurado para debugging

---

*Documento actualizado para guiar el desarrollo y completar la implementación de la persistencia en Kursor. Última actualización: [Fecha actual]* 