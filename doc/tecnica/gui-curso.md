# GUI Curso - Propuesta de Implementación
## Interfaz Gráfica de Cursos para Kursor

### Versión: 1.0
### Fecha: 2025-06-18
### Autor: Juan José Ruiz Pérez <jjrp1@um.es>

---

## 1. Arquitectura General

```
KursorApplication (UI Principal)
    ↓
CursoInterfaceController (Nuevo controlador)
    ↓
CursoSessionManager (Gestión de sesiones)
    ↓
ModuleManager (Carga de módulos)
    ↓
QuestionModule (Plugins específicos)
```

## 2. Componentes Principales

### A. CursoInterfaceController
- **Responsabilidad**: Control principal de la interfaz de curso
- **Funciones**:
  - Gestionar la ventana modal con las 3 secciones
  - Coordinar navegación entre bloques/preguntas
  - Delegar construcción de UI a módulos específicos
  - Gestionar validación y progreso

### B. CursoSessionManager 
- **Responsabilidad**: Persistencia y gestión de sesiones
- **Funciones**:
  - Crear/recuperar sesiones de usuario
  - Guardar progreso (bloque actual, pregunta actual)
  - Persistir respuestas y resultados
  - Calcular estadísticas de progreso

### C. Ventana Modal (CursoInterfaceWindow)
- **Estructura**:
  ```
  ┌─────────────────────────────────┐
  │ CABECERA (Tamaño fijo)          │
  │ - Título curso + bloque actual  │
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
1. Usuario hace clic en "Comenzar"
   ↓
2. CursoInterfaceController se inicializa
   ↓
3. CursoSessionManager recupera/crea sesión
   ↓
4. Se muestra ventana modal con:
   - Cabecera: info curso + progreso
   - Contenido: UI del módulo específico
   - Pie: botones de navegación
   ↓
5. Usuario interactúa con la pregunta
   ↓
6. Según tipo de pregunta:
   - Flashcard: Botón "Siguiente" (sin validación)
   - Otros: Botón "Verificar" → Validación → "Siguiente"
   ↓
7. Se guarda progreso y se avanza
   ↓
8. Repetir hasta terminar o usuario cancela
```

## 4. Interfaz QuestionModule Extendida

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

## 5. Gestión de Sesiones (JPA)

### Entidades:
```java
@Entity
public class Sesion {
    @Id private Long id;
    private String cursoId;
    private String userId;
    private int bloqueActual;
    private int preguntaActual;
    private Date fechaInicio;
    private Date fechaUltimaActividad;
    @OneToMany private List<RespuestaUsuario> respuestas;
}

@Entity  
public class RespuestaUsuario {
    @Id private Long id;
    private String preguntaId;
    private String respuesta;
    private boolean correcta;
    private Date timestamp;
}
```

## 6. Estructura de la Ventana Modal

### Cabecera:
- Título del curso (ej: "Inglés básico")
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

## 7. Consideraciones Técnicas

### Responsabilidades de los Módulos:
- **Validación YAML**: Cada módulo valida su estructura específica
- **Construcción UI**: Cada módulo crea su interfaz específica
- **Validación respuestas**: Cada módulo implementa su lógica de validación
- **Interacción**: Cada módulo maneja eventos de su UI

### Persistencia:
- **JPA/Hibernate** para entidades de sesión
- **Guardado automático** en cada interacción
- **Recuperación de sesión** al reiniciar

### Manejo de Errores:
- Validación de datos YAML antes de mostrar
- Manejo de errores de módulos
- Recuperación de sesiones corruptas

## 8. Preguntas y Decisiones Pendientes

1. **¿Cómo manejar sesiones múltiples?** (mismo usuario, diferentes cursos)
2. **¿Qué hacer si un módulo falla al cargar?**
3. **¿Cómo manejar la persistencia si no hay base de datos configurada?**
4. **¿Qué información mostrar en la barra de progreso?** (solo bloque actual o progreso total del curso)

## 9. Plan de Implementación

### Fase 1: CursoSessionManager y entidades JPA
- Crear entidades Sesion y RespuestaUsuario
- Implementar CursoSessionManager básico
- Configurar JPA/Hibernate

### Fase 2: CursoInterfaceController básico
- Crear controlador principal
- Implementar lógica de navegación básica
- Integrar con ModuleManager

### Fase 3: Ventana modal con estructura básica
- Crear CursoInterfaceWindow
- Implementar las 3 secciones (cabecera, contenido, pie)
- Configurar tamaños y layout

### Fase 4: Extender QuestionModule
- Añadir nuevos métodos a la interfaz
- Actualizar módulos existentes
- Implementar validación específica por tipo

### Fase 5: Navegación y validación
- Implementar lógica de verificación
- Manejar diferentes tipos de preguntas
- Integrar con persistencia

### Fase 6: Integración final
- Conectar con botón "Comenzar"
- Pruebas y depuración
- Documentación final

## 10. Archivos a Crear/Modificar

### Nuevos archivos:
- `kursor-core/src/main/java/com/kursor/core/domain/Sesion.java`
- `kursor-core/src/main/java/com/kursor/core/domain/RespuestaUsuario.java`
- `kursor-core/src/main/java/com/kursor/core/service/CursoSessionManager.java`
- `kursor-ui/src/main/java/com/kursor/ui/CursoInterfaceController.java`
- `kursor-ui/src/main/java/com/kursor/ui/CursoInterfaceWindow.java`

### Archivos a modificar:
- `kursor-core/src/main/java/com/kursor/core/QuestionModule.java`
- `kursor-ui/src/main/java/com/kursor/ui/KursorApplication.java`
- Todos los módulos existentes (añadir nuevos métodos)

## 11. Dependencias Adicionales

### JPA/Hibernate:
```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.2.0.Final</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.214</version>
</dependency>
```

## 12. Notas de Implementación

- La ventana modal debe ser responsive y adaptarse a diferentes tamaños de pantalla
- Los módulos deben ser thread-safe para evitar problemas de concurrencia
- La persistencia debe ser configurable (base de datos o archivo local)
- Se debe implementar logging detallado para debugging
- La interfaz debe ser accesible (teclado, lectores de pantalla)

---

**Estado**: Propuesta inicial
**Próximo paso**: Revisión y aprobación de la propuesta
**Responsable**: Equipo de desarrollo Kursor 