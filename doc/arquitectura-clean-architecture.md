# Arquitectura Clean Architecture - Kursor

## 📋 Resumen Ejecutivo

Este documento describe la implementación de **Clean Architecture** en Kursor, reemplazando la estructura anterior con una organización clara de capas que sigue los principios de arquitectura limpia.

## 🎯 Objetivos

- **Separación clara de responsabilidades** entre capas
- **Independencia de frameworks** y tecnologías externas
- **Testabilidad mejorada** mediante inversión de dependencias
- **Escalabilidad** para futuras funcionalidades
- **Mantenibilidad** mejorada con patrones establecidos

## 🏗️ Arquitectura General

### Estructura de Capas (Clean Architecture)

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
│                     DOMAIN LAYER                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Entities   │  │Value Objects│  │   Services  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                INFRASTRUCTURE LAYER                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │Repositories │  │   External  │  │   Config    │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### Principios de Clean Architecture

1. **Independencia de Frameworks**: El dominio no depende de frameworks externos
2. **Testabilidad**: Las reglas de negocio pueden ser probadas sin UI, base de datos, etc.
3. **Independencia de UI**: La interfaz puede cambiar fácilmente sin cambiar el resto del sistema
4. **Independencia de Base de Datos**: Las reglas de negocio no están acopladas a la base de datos
5. **Independencia de Agentes Externos**: Las reglas de negocio no conocen nada del mundo exterior

## 📁 Estructura de Paquetes

### Estructura Propuesta (Clean Architecture)

```
com.kursor/
├── presentation/           # ✅ Capa de presentación (UI)
│   ├── controllers/       # Controladores MVC
│   │   ├── MainController.java
│   │   └── SessionController.java
│   ├── views/            # Vistas JavaFX
│   │   ├── MainView.java
│   │   └── SessionTableView.java
│   ├── viewmodels/       # ViewModels para binding
│   │   ├── MainViewModel.java
│   │   └── SessionViewModel.java
│   └── dialogs/          # Diálogos de la aplicación
│       ├── AboutDialog.java
│       └── EstadisticasDialog.java
├── application/           # ✅ Capa de aplicación
│   ├── usecases/         # Casos de uso
│   │   └── LoadCoursesUseCase.java
│   ├── services/         # Servicios de aplicación
│   │   └── CourseService.java
│   └── dto/              # Objetos de transferencia
│       └── CursoDTO.java
├── domain/               # ✅ Capa de dominio
│   ├── entities/         # Entidades de dominio
│   │   ├── Curso.java
│   │   ├── Bloque.java
│   │   └── Pregunta.java
│   ├── valueobjects/     # Objetos de valor
│   │   └── EstadoPregunta.java
│   └── services/         # Servicios de dominio
│       └── CursoPreviewService.java
├── infrastructure/       # ✅ Capa de infraestructura
│   ├── persistence/      # Repositorios y JPA
│   │   ├── CourseRepository.java
│   │   ├── CourseRepositoryImpl.java
│   │   └── config/
│   ├── external/         # Servicios externos
│   └── config/           # Configuración
└── shared/               # ✅ Utilidades compartidas
    ├── util/             # Utilidades generales
    │   ├── CursoManager.java
    │   └── ModuleManager.java
    └── exceptions/       # Excepciones personalizadas
```

### Estructura Actual (Problemas Identificados)

```
com.kursor/
├── ui/                   # ❌ MEZCLA DE CAPAS
│   ├── application/      # ❌ NO debería estar aquí
│   ├── infrastructure/   # ❌ NO debería estar aquí
│   ├── main/            # ✅ Correcto
│   └── session/         # ✅ Correcto
├── domain/              # ✅ Correcto
├── service/             # ❌ Mezclado
├── persistence/         # ✅ Correcto
└── ...
```

## 🔧 Componentes Principales

### 1. Capa de Presentación (`presentation/`)

#### Responsabilidades:
- **Renderizar la interfaz de usuario**
- **Manejar eventos de usuario**
- **Coordinar con la capa de aplicación**
- **No contiene lógica de negocio**

#### Patrones Implementados:
- **MVC (Model-View-Controller)**
- **MVVM (Model-View-ViewModel)**
- **Observer Pattern** para binding

### 2. Capa de Aplicación (`application/`)

#### Responsabilidades:
- **Orquestar casos de uso**
- **Validar datos de entrada**
- **Manejar transacciones**
- **Coordinar entre capas**

#### Patrones Implementados:
- **Use Case Pattern**
- **Service Layer Pattern**
- **DTO Pattern**

### 3. Capa de Dominio (`domain/`)

#### Responsabilidades:
- **Contener las reglas de negocio**
- **Definir entidades y objetos de valor**
- **Ser independiente de frameworks externos**
- **Ser el núcleo de la aplicación**

#### Patrones Implementados:
- **Entity Pattern**
- **Value Object Pattern**
- **Domain Service Pattern**

### 4. Capa de Infraestructura (`infrastructure/`)

#### Responsabilidades:
- **Implementar interfaces definidas en capas internas**
- **Manejar detalles técnicos (BD, APIs, etc.)**
- **Proporcionar adaptadores para servicios externos**
- **Configurar frameworks y librerías**

#### Patrones Implementados:
- **Repository Pattern**
- **Adapter Pattern**
- **Factory Pattern**

## 🔄 Flujo de Datos

### 1. Carga de Cursos (Ejemplo Completo)

```
User Action → MainView → MainController → LoadCoursesUseCase → CourseService → CourseRepository → CursoManager → Database
```

### 2. Selección de Curso

```
User Click → MainView → MainController → MainViewModel → UI Update
```

### 3. Binding Automático

```
ViewModel Property Change → PropertyChangeSupport → MainController → MainView Update
```

## 🎨 Patrones de Diseño Implementados

### 1. Clean Architecture
- **Separación clara de capas**
- **Inversión de dependencias**
- **Independencia de frameworks**

### 2. MVC + MVVM
- **MVC** para la organización general de la UI
- **MVVM** para el binding de datos
- **ViewModels** como intermediarios

### 3. Repository Pattern
- **Interfaces** en la capa de aplicación
- **Implementaciones** en la capa de infraestructura
- **Abstracción** del acceso a datos

### 4. Use Case Pattern
- **Casos de uso** específicos y bien definidos
- **Validación** de entrada y salida
- **Manejo de errores** centralizado

## 🔧 Configuración y Dependencias

### Dependencias por Capa

#### Presentation Layer
- **JavaFX** - Framework de UI
- **SLF4J** - Logging

#### Application Layer
- **Ninguna dependencia externa**
- Solo depende del dominio

#### Domain Layer
- **Ninguna dependencia externa**
- Es el núcleo independiente

#### Infrastructure Layer
- **JPA/Hibernate** - Persistencia
- **SQLite** - Base de datos
- **YAML** - Configuración

## 🚀 Beneficios de la Nueva Arquitectura

### 1. **Mantenibilidad**
- Código organizado y fácil de entender
- Separación clara de responsabilidades
- Fácil localización de problemas

### 2. **Testabilidad**
- Cada capa puede ser probada independientemente
- Mocks fáciles de crear
- Tests unitarios más simples

### 3. **Escalabilidad**
- Fácil agregar nuevas funcionalidades
- Módulos independientes
- Bajo acoplamiento

### 4. **Flexibilidad**
- Cambiar frameworks sin afectar el dominio
- Múltiples interfaces de usuario
- Diferentes fuentes de datos

## 📋 Plan de Migración

### Fase 1: Eliminación de Duplicados ✅
- [x] Eliminar clases duplicadas
- [x] Consolidar versiones funcionales
- [x] Limpiar directorios vacíos

### Fase 2: Reorganización de Estructura 🔄
- [ ] Mover `ui/application/` → `application/`
- [ ] Mover `ui/infrastructure/` → `infrastructure/`
- [ ] Mover `ui/main/` → `presentation/controllers/`
- [ ] Mover `ui/session/` → `presentation/controllers/`
- [ ] Reorganizar archivos sueltos

### Fase 3: Estandarización de Nomenclatura
- [ ] Usar inglés consistentemente
- [ ] Estandarizar nombres de métodos
- [ ] Unificar convenciones

### Fase 4: Implementación de Inversión de Dependencias
- [ ] Crear interfaces para repositorios
- [ ] Implementar inyección de dependencias
- [ ] Eliminar acoplamientos directos

## 🔍 Métricas de Calidad

### Antes de la Reorganización
- **Acoplamiento**: Alto (dependencias circulares)
- **Cohesión**: Baja (responsabilidades mezcladas)
- **Testabilidad**: Media (dependencias externas)
- **Mantenibilidad**: Baja (código disperso)

### Después de la Reorganización
- **Acoplamiento**: Bajo (dependencias unidireccionales)
- **Cohesión**: Alta (responsabilidades claras)
- **Testabilidad**: Alta (capa por capa)
- **Mantenibilidad**: Alta (estructura clara)

## 📚 Referencias

- **Clean Architecture** - Robert C. Martin
- **Domain-Driven Design** - Eric Evans
- **Patterns of Enterprise Application Architecture** - Martin Fowler
- **JavaFX Best Practices** - Oracle Documentation

---

**Autor**: Juan José Ruiz Pérez  
**Versión**: 1.0.0  
**Fecha**: 2025-01-27  
**Estado**: En desarrollo
