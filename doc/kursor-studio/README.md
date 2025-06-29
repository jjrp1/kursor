# Documentación Kursor Studio

Esta carpeta contiene toda la documentación relacionada con **Kursor Studio**, la aplicación JavaFX independiente para inspeccionar y validar el sistema de persistencia de Kursor.

## 📋 Contenido de la Documentación

### Documentos Principales

- **[kursor-studio-design.md](kursor-studio-design.md)** - Documento de diseño técnico completo
  - Arquitectura y módulos principales
  - Casos de uso y flujos de trabajo
  - Mockups de interfaz de usuario
  - Plan de implementación por fases
  - Consideraciones técnicas y criterios de éxito

- **[logging-strategy.md](logging-strategy.md)** - Estrategia de logging especializada
  - Sistema de logging idéntico a Kursor principal
  - Configuración SLF4J + Logback detallada
  - Comandos de ejecución y verificación
  - Patrones de implementación en código
  - Testing y scripts de validación

### Próximos Documentos (Planificados)

- **user-guide.md** - Guía de usuario final
- **installation-guide.md** - Instrucciones de instalación y configuración
- **api-documentation.md** - Documentación de servicios internos
- **testing-strategy.md** - Estrategia de pruebas y validación

## 🎯 Objetivo de Kursor Studio

**Kursor Studio** es una herramienta de administración y validación diseñada para:

- ✅ **Inspeccionar** el sistema de persistencia de Kursor
- ✅ **Validar** que los datos se registran correctamente
- ✅ **Verificar** la integridad de la base de datos
- ✅ **Monitorear** el estado del sistema

## 🏗️ Arquitectura

Kursor Studio está diseñado como una **aplicación JavaFX completamente independiente** que reutiliza componentes clave del sistema de persistencia de Kursor principal mediante **dependencia Maven**, sin modificar el código existente de kursor-core.

### Arquitectura de Dependencias
```
kursor-core ──→ kursor-studio
(NO modificar)   (aplicación nueva)
```

**Propagación unidireccional**: Los cambios en `kursor-core` se propagan automáticamente a `kursor-studio`, pero no al revés.

## 🚀 Estado del Proyecto

- **Estado actual**: ✅ Documentación completa - Iniciando implementación
- **Próximo paso**: Ejecutar Fase 1 - Fundación
- **Implementación**: 🔄 EN PROGRESO - Creando módulo Maven

## 📞 Contacto y Feedback

***Autor***: *Juan José Ruiz Pérez* <jjrp1@um.es>