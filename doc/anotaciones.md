# Anotaciones y Observaciones del Proyecto

## 1. Incoherencias Identificadas

### 1.1 Modelo de Dominio vs Implementación
- El diagrama de clases muestra `id` como `Long` pero la implementación usa `String`
- Algunos métodos mencionados en el modelo no están implementados:
  - `guardarSesion()`
  - `restaurarSesion()`
- La clase `Sesion` tiene campos adicionales no documentados en el modelo

### 1.2 Arquitectura Microkernel
- La documentación no refleja claramente la arquitectura de plugins
- Falta documentación sobre la carga dinámica de módulos
- No está claro el contrato entre el core y los módulos

### 1.3 Persistencia
- Se menciona JPA en el README pero no veo la implementación
- Falta documentación sobre el modelo de persistencia

## 2. Propuesta de Documentación Técnica

### 2.1 Arquitectura del Sistema
- Visión General
- Patrón Microkernel
- Sistema de Plugins
- Carga Dinámica de Módulos
- Persistencia de Datos

### 2.2 Modelo de Dominio
- Entidades Principales
- Relaciones
- Ciclo de Vida de Objetos
- Validaciones

### 2.3 API del Core
- Interfaces Públicas
- Contratos de Módulos
- Eventos y Callbacks
- Extensiones

### 2.4 Módulos
- Estructura de Módulos
- Contrato de Implementación
- Ciclo de Vida
- Recursos y Aislamiento

### 2.5 Persistencia
- Modelo de Datos
- Estrategias de Persistencia
- Serialización
- Caché

## 3. Mejoras Propuestas

### 3.1 Core
- Implementar serialización/deserialización para estados
- Mejorar el manejo de errores
- Añadir validaciones de datos

### 3.2 Módulos
- Estandarizar la interfaz de módulos
- Mejorar el aislamiento
- Implementar versionado

### 3.3 UI
- Documentar el patrón de diseño UI
- Especificar la comunicación UI-Core
- Definir eventos de UI

## 4. Plan de Documentación

### 4.1 Fase 1 - Documentación Base
- Actualizar modelo de dominio
- Documentar arquitectura
- Especificar APIs

### 4.2 Fase 2 - Documentación Técnica
- Detallar implementaciones
- Documentar patrones
- Especificar extensiones

### 4.3 Fase 3 - Documentación de Desarrollo
- Guías de desarrollo
- Convenciones
- Pruebas

## 5. Notas Adicionales

- Este documento servirá como guía para el desarrollo de la documentación técnica
- Las incoherencias identificadas deberán resolverse antes de la entrega final
- Las mejoras propuestas deberán evaluarse según su prioridad y tiempo disponible
- El plan de documentación puede ajustarse según las necesidades del proyecto

## 6. Estado Actual

- Proyecto en fase de desarrollo
- Arquitectura base implementada
- Módulos funcionando
- Pendiente de documentación técnica completa
- Pendiente de implementación de pruebas

## 7. Comandos Útiles

### 7.1 Listado de Archivos Markdown
* **Ficheros modificados (de más reciente a más antiguo)**:
  ```powershell
  Get-ChildItem . -filter *.md -Recurse | Select Name, FullName, LastWriteTime | Sort-Object LastWriteTime -Descending|Out-GridView
  ```

### 7.2 Tabla de Archivos Markdown (Última actualización: 18/06/2025)

| Archivo | Ubicación | Contenido Sugerido | Última Modificación |
|---------|-----------|-------------------|---------------------|
| justifica-dto.md | doc/tecnica/ | Justificación de los DTOs utilizados | 18/06/2025 14:35:22 |
| patrones-diseno-curso.md | doc/ | Patrones de diseño aplicados al curso | 18/06/2025 14:33:56 |
| anotaciones.md | doc/ | Notas y anotaciones del proyecto | 18/06/2025 13:57:57 |
| README.md | doc/tecnica/ | Documentación técnica del proyecto | 18/06/2025 13:45:00 |
| pruebas-plan-inicial.md | doc/tecnica/ | Plan inicial de pruebas | 18/06/2025 13:44:25 |
| README.md | doc/usuario/ | Documentación para usuarios | 18/06/2025 13:08:21 |
| README.md | Raíz del proyecto | Documentación general del proyecto | 18/06/2025 13:01:54 |
| pds-dto-yaml.md | doc/ | Especificaciones de los DTOs y formato YAML | 17/06/2025 22:31:49 |
| pds-enunciado-original.md | doc/ | Enunciado original del proyecto | 16/06/2025 20:19:51 |
| pds-casos-de-uso.md | doc/ | Documentación de los casos de uso | 16/06/2025 20:19:51 |
| pds-modelo-de-dominio.md | doc/ | Modelo de dominio del sistema | 16/06/2025 20:19:51 | 