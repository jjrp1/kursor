# Nueva Arquitectura MVC Mejorada - Kursor

## 📋 Resumen Ejecutivo

Este documento describe la implementación de una nueva arquitectura MVC mejorada para la aplicación Kursor, que reemplaza la arquitectura anterior con una estructura más limpia, mantenible y escalable.

## 🎯 Objetivos

- **Separación clara de responsabilidades** entre capas
- **Mejor testabilidad** mediante inyección de dependencias
- **Escalabilidad** para futuras funcionalidades
- **Mantenibilidad** mejorada con patrones establecidos
- **Compatibilidad** con el código existente

## 🏗️ Arquitectura General

### Estructura de Capas

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Views     │  │Controllers  │  │ ViewModels  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Use Cases   │  │  Services   │  │   DTOs      │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │Repositories │  │   External  │  │   Config    │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### Principios de Diseño

1. **Clean Architecture**: Separación clara entre capas
2. **Dependency Inversion**: Las capas internas no dependen de las externas
3. **Single Responsibility**: Cada clase tiene una responsabilidad específica
4. **Open/Closed**: Abierto para extensión, cerrado para modificación

## 📁 Estructura de Paquetes

```
com.kursor.ui/
├── presentation/           # Capa de presentación
│   ├── main/              # Vista principal
│   │   ├── MainController.java
│   │   ├── MainViewModel.java
│   │   └── MainView.java
│   ├── session/           # Vista de sesión
│   ├── course/            # Vista de curso
│   ├── dialogs/           # Diálogos
│   └── KursorApplication.java
├── application/           # Capa de aplicación
│   ├── usecases/         # Casos de uso
│   │   └── LoadCoursesUseCase.java
│   └── services/         # Servicios de aplicación
│       └── CourseService.java
└── infrastructure/       # Capa de infraestructura
    ├── persistence/      # Repositorios
    │   ├── CourseRepository.java
    │   └── CourseRepositoryImpl.java
    └── external/         # Servicios externos
```

## 🔧 Componentes Principales

### 1. Capa de Presentación

#### MainController
- **Responsabilidad**: Coordinar interacciones entre vista y modelo
- **Características**:
  - Manejo de eventos de UI
  - Configuración de binding
  - Coordinación de operaciones

```java
public class MainController implements PropertyChangeListener {
    private final MainViewModel viewModel;
    private final MainView view;
    private final LoadCoursesUseCase loadCoursesUseCase;
    
    public void onCourseSelected(CursoDTO curso) {
        viewModel.selectCourse(curso);
    }
}
```

#### MainViewModel
- **Responsabilidad**: Estado y lógica de presentación
- **Características**:
  - Binding automático con PropertyChangeSupport
  - Operaciones asíncronas
  - Manejo de estados de carga y error

```java
public class MainViewModel {
    private final PropertyChangeSupport propertyChangeSupport;
    private List<CursoDTO> cursos;
    private boolean loading;
    private String error;
    
    public void loadCoursesAsync() {
        setLoading(true);
        CompletableFuture.supplyAsync(() -> loadCoursesUseCase.execute())
            .thenAccept(this::setCursos)
            .exceptionally(this::handleError);
    }
}
```

#### MainView
- **Responsabilidad**: Renderizar la interfaz de usuario
- **Características**:
  - Interfaz moderna y responsiva
  - Delegación de eventos al controlador
  - Actualización automática basada en ViewModel

### 2. Capa de Aplicación

#### LoadCoursesUseCase
- **Responsabilidad**: Lógica de negocio para cargar cursos
- **Características**:
  - Validación de datos
  - Manejo de errores
  - Logging detallado

```java
public class LoadCoursesUseCase {
    private final CourseService courseService;
    
    public List<CursoDTO> execute() throws UseCaseException {
        validatePreconditions();
        List<CursoDTO> cursos = courseService.loadCourses();
        validateResults(cursos);
        return cursos;
    }
}
```

#### CourseService
- **Responsabilidad**: Orquestar operaciones de negocio
- **Características**:
  - Validación de entrada/salida
  - Manejo de transacciones
  - API limpia para casos de uso

### 3. Capa de Infraestructura

#### CourseRepository
- **Responsabilidad**: Interfaz para acceso a datos
- **Características**:
  - Contrato independiente de implementación
  - Operaciones CRUD
  - Búsqueda y filtrado

```java
public interface CourseRepository {
    List<CursoDTO> findAll() throws RepositoryException;
    Optional<CursoDTO> findById(String id) throws RepositoryException;
    CursoDTO save(CursoDTO curso) throws RepositoryException;
}
```

#### CourseRepositoryImpl
- **Responsabilidad**: Implementación del repositorio
- **Características**:
  - Adaptador para CursoManager existente
  - Logging detallado
  - Manejo de errores

## 🔄 Flujo de Datos

### 1. Carga de Cursos

```
User Action → MainView → MainController → MainViewModel → LoadCoursesUseCase → CourseService → CourseRepository → CursoManager
```

### 2. Selección de Curso

```
User Click → MainView → MainController → MainViewModel → UI Update
```

### 3. Binding Automático

```
ViewModel Property Change → PropertyChangeSupport → MainController → MainView Update
```

## 🎨 Patrones Implementados

### 1. MVC (Model-View-Controller)
- **Model**: ViewModel + Casos de uso
- **View**: MainView
- **Controller**: MainController

### 2. MVVM (Model-View-ViewModel)
- **Model**: Casos de uso y servicios
- **View**: MainView
- **ViewModel**: MainViewModel con binding automático

### 3. Repository Pattern
- Abstracción del acceso a datos
- Independencia de la fuente de datos
- Facilita testing

### 4. Use Case Pattern
- Encapsula lógica de negocio específica
- Validación y manejo de errores
- Reutilización de lógica

## 🧪 Testing

### Estrategia de Testing

1. **Unit Tests**: Para casos de uso y servicios
2. **Integration Tests**: Para repositorios
3. **UI Tests**: Para vistas (futuro)

### Ejemplo de Test

```java
@Test
public void testLoadCoursesUseCase() {
    // Arrange
    CourseService mockService = mock(CourseService.class);
    when(mockService.loadCourses()).thenReturn(Arrays.asList(curso1, curso2));
    LoadCoursesUseCase useCase = new LoadCoursesUseCase(mockService);
    
    // Act
    List<CursoDTO> result = useCase.execute();
    
    // Assert
    assertEquals(2, result.size());
    verify(mockService).loadCourses();
}
```

## 🚀 Migración

### Fase 1: Implementación Paralela ✅
- Crear nueva estructura de paquetes
- Implementar componentes básicos
- Mantener compatibilidad con código existente

### Fase 2: Integración Gradual 🔄
- Migrar funcionalidades una por una
- Probar exhaustivamente cada migración
- Mantener ambas versiones funcionando

### Fase 3: Eliminación de Código Legacy 📋
- Remover código antiguo
- Optimizar y refactorizar
- Documentar lecciones aprendidas

## 📊 Beneficios

### Antes vs Después

| Aspecto | Arquitectura Anterior | Nueva Arquitectura |
|---------|----------------------|-------------------|
| **Separación de Responsabilidades** | Mezclada | Clara y definida |
| **Testabilidad** | Difícil | Fácil con mocks |
| **Mantenibilidad** | Compleja | Simple y modular |
| **Escalabilidad** | Limitada | Alta |
| **Logging** | Básico | Detallado y estructurado |
| **Manejo de Errores** | Inconsistente | Robusto y centralizado |

### Métricas de Mejora

- **Cobertura de Tests**: 0% → 85% (objetivo)
- **Tiempo de Desarrollo**: -30% (estimado)
- **Bugs en Producción**: -50% (estimado)
- **Tiempo de Onboarding**: -40% (estimado)

## 🔧 Configuración y Uso

### Compilación

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
java -cp target/classes com.kursor.ui.presentation.KursorApplication
```

### Script de Prueba

```powershell
# Verificar nueva arquitectura
.\scripts\test-nueva-arquitectura.ps1 -Compile -Run

# Solo compilar
.\scripts\test-nueva-arquitectura.ps1 -Compile

# Ver ayuda
.\scripts\test-nueva-arquitectura.ps1 -Help
```

## 📝 Logging

### Configuración

La nueva arquitectura utiliza SLF4J + Logback para logging estructurado:

```xml
<!-- logback.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### Niveles de Log

- **ERROR**: Errores críticos que impiden funcionamiento
- **WARN**: Situaciones anómalas pero manejables
- **INFO**: Información importante del flujo de la aplicación
- **DEBUG**: Información detallada para desarrollo

## 🔮 Roadmap

### Corto Plazo (1-2 meses)
- [ ] Migrar todas las vistas principales
- [ ] Implementar casos de uso restantes
- [ ] Completar cobertura de tests
- [ ] Documentar API interna

### Mediano Plazo (3-6 meses)
- [ ] Implementar inyección de dependencias
- [ ] Agregar validación de entrada
- [ ] Optimizar rendimiento
- [ ] Implementar cache

### Largo Plazo (6+ meses)
- [ ] Migrar a framework moderno (JavaFX/Spring)
- [ ] Implementar arquitectura de microservicios
- [ ] Agregar análisis de métricas
- [ ] Implementar CI/CD

## 👥 Contribución

### Guías de Desarrollo

1. **Nuevas Funcionalidades**: Implementar siguiendo la nueva arquitectura
2. **Bug Fixes**: Mantener compatibilidad con ambas versiones
3. **Refactoring**: Migrar gradualmente código existente
4. **Documentación**: Actualizar este documento con cambios

### Code Review Checklist

- [ ] Sigue la estructura de paquetes definida
- [ ] Implementa logging apropiado
- [ ] Incluye tests unitarios
- [ ] Maneja errores correctamente
- [ ] Documenta cambios en JavaDoc

## 📚 Referencias

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVVM Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel)
- [Repository Pattern](https://martinfowler.com/eaaCatalog/repository.html)
- [Use Case Pattern](https://martinfowler.com/bliki/UseCase.html)

---

**Autor**: Juan José Ruiz Pérez <jjrp1@um.es>  
**Versión**: 1.0.0  
**Fecha**: 2025-06-29  
**Estado**: Implementación en Progreso 