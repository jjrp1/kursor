# Análisis de la Estructura Actual del Proyecto Kursor

## Resumen Ejecutivo

El proyecto Kursor presenta una **estructura híbrida y confusa** que mezcla diferentes patrones arquitectónicos sin una separación clara de responsabilidades. Se han identificado múltiples inconsistencias que violan los principios de Clean Architecture y MVC.

## Estructura Actual del Proyecto

### 1. Organización de Módulos
```
kursor/
├── kursor-core/                    # Módulo principal
├── kursor-studio/                  # Módulo de administración
├── kursor-*-module/               # Módulos de tipos de preguntas
├── kursor-*-strategy/             # Módulos de estrategias
└── cursos/                        # Datos de cursos
```

### 2. Estructura Interna de kursor-core
```
kursor-core/src/main/java/com/kursor/
├── ui/                           # ❌ MEZCLA DE CAPAS
│   ├── presentation/             # ✅ Nueva arquitectura
│   ├── application/              # ✅ Nueva arquitectura
│   ├── infrastructure/           # ✅ Nueva arquitectura
│   ├── main/                     # ❌ Arquitectura antigua
│   ├── session/                  # ❌ Arquitectura antigua
│   └── [archivos sueltos]        # ❌ Sin organización
├── domain/                       # ✅ Entidades de dominio
├── service/                      # ❌ Servicios mezclados
├── persistence/                  # ✅ Capa de persistencia
├── strategy/                     # ✅ Estrategias
├── factory/                      # ✅ Factories
├── builder/                      # ✅ Builders
├── modules/                      # ✅ Módulos
├── util/                         # ❌ Utilidades mezcladas
└── yaml/                         # ❌ DTOs mezclados
```

## Problemas Identificados

### 1. **Duplicación de Clases**
- **MainController**: Existe en `ui/main/` y `ui/presentation/main/`
- **MainView**: Existe en `ui/main/` y `ui/presentation/main/`
- **MainViewModel**: Existe en `ui/main/` y `ui/presentation/main/`
- **KursorApplication**: Existe en `ui/` y `ui/presentation/`

### 2. **Violación del Principio de Responsabilidad Única**

#### En `ui/` (Capa de Presentación):
- `CursoInterfaceController.java` - Controlador
- `CursoInterfaceView.java` - Vista
- `SessionController.java` - Controlador
- `SessionTableView.java` - Vista
- `MainController.java` - Controlador
- `MainView.java` - Vista

#### En `service/` (Capa de Aplicación):
- `CursoPreviewService.java` - Servicio de aplicación

#### En `persistence/` (Capa de Infraestructura):
- `repository/` - Repositorios
- `entity/` - Entidades JPA
- `config/` - Configuración

### 3. **Inconsistencias Arquitectónicas**

#### ❌ Mezcla de Frameworks
- **JavaFX**: `ui/presentation/KursorApplication.java` (Swing)
- **Swing**: `ui/KursorApplication.java` (JavaFX)
- **Ambos**: Clases que usan tanto JavaFX como Swing

#### ❌ Acoplamiento Excesivo
- Los controladores acceden directamente a entidades de dominio
- Las vistas conocen detalles de implementación de servicios
- Los servicios están acoplados a frameworks específicos

#### ❌ Falta de Separación de Capas
- Lógica de negocio mezclada con lógica de presentación
- Acceso directo a base de datos desde controladores
- Configuración dispersa en múltiples lugares

### 4. **Problemas de Nomenclatura**

#### Inconsistencias en Nombres:
- `CursoInterfaceController` vs `MainController`
- `SessionController` vs `CursoInterfaceController`
- `CursoPreviewService` vs `CourseService`
- `SesionRepository` vs `CourseRepository`

#### Mezcla de Idiomas:
- Clases en español: `Curso`, `Sesion`, `Pregunta`
- Clases en inglés: `Course`, `Session`, `Question`
- Métodos mezclados: `cargarCursos()` vs `loadCourses()`

## Comparación: MVC vs Clean Architecture

### MVC Tradicional (Actual)
```
┌─────────────────┐
│     View        │  ← Interfaz de usuario
├─────────────────┤
│   Controller    │  ← Lógica de control
├─────────────────┤
│     Model       │  ← Datos y lógica de negocio
└─────────────────┘
```

### Clean Architecture (Propuesta)
```
┌─────────────────────────────────────────────────┐
│              Presentation Layer                 │
│  (Controllers, Views, ViewModels)              │
├─────────────────────────────────────────────────┤
│              Application Layer                  │
│  (Use Cases, Services, DTOs)                   │
├─────────────────────────────────────────────────┤
│              Domain Layer                       │
│  (Entities, Value Objects, Domain Services)    │
├─────────────────────────────────────────────────┤
│            Infrastructure Layer                 │
│  (Repositories, External Services, Config)     │
└─────────────────────────────────────────────────┘
```

## Incompatibilidades Identificadas

### 1. **MVC y Clean Architecture NO son incompatibles**
- MVC es un patrón de presentación
- Clean Architecture es un patrón de organización de capas
- Se pueden usar juntos: MVC en la capa de presentación

### 2. **Problema Real: Mezcla de Patrones**
- Se está intentando implementar ambos sin planificación
- Falta de separación clara entre capas
- Dependencias circulares entre componentes

## Recomendaciones

### 1. **Migración Gradual**
```
Fase 1: Consolidar estructura actual
Fase 2: Separar capas claramente
Fase 3: Implementar Clean Architecture
Fase 4: Optimizar y refactorizar
```

### 2. **Estructura Propuesta**
```
kursor-core/
├── presentation/           # MVC + JavaFX
│   ├── controllers/
│   ├── views/
│   └── viewmodels/
├── application/            # Casos de uso
│   ├── usecases/
│   ├── services/
│   └── dto/
├── domain/                 # Entidades y lógica de negocio
│   ├── entities/
│   ├── valueobjects/
│   └── services/
└── infrastructure/         # Implementaciones técnicas
    ├── persistence/
    ├── external/
    └── config/
```

### 3. **Acciones Inmediatas**
1. **Eliminar duplicados**: Mantener solo una versión de cada clase
2. **Estandarizar nomenclatura**: Usar inglés consistentemente
3. **Separar responsabilidades**: Mover clases a sus capas correspondientes
4. **Eliminar dependencias circulares**: Implementar inversión de dependencias

## Conclusión

El proyecto tiene una **base sólida** pero necesita una **reorganización arquitectónica urgente**. La mezcla actual de patrones está causando:

- **Mantenibilidad reducida**
- **Testabilidad limitada**
- **Acoplamiento excesivo**
- **Confusión en el desarrollo**

La solución no es elegir entre MVC y Clean Architecture, sino **implementar ambos correctamente**:
- **Clean Architecture** para la organización general
- **MVC** para la capa de presentación
- **Separación clara** de responsabilidades
- **Inversión de dependencias** para reducir acoplamiento 