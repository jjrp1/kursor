# Estrategias de Aprendizaje en Kursor

## Introducción

Kursor ofrece cuatro estrategias de aprendizaje diferentes, cada una diseñada para optimizar el proceso de aprendizaje según tus necesidades y preferencias. Todas las estrategias están implementadas como módulos independientes que se cargan dinámicamente.

## Estrategias Disponibles

### 1. Estrategia Secuencial

**Descripción:** Presenta las preguntas en el orden exacto en que aparecen en el curso.

**Características:**
- ✅ Orden predecible y estructurado
- ✅ Progreso lineal y fácil de seguir
- ✅ Ideal para cursos con secuencia lógica
- ✅ Fácil de pausar y reanudar

**Cuándo usar:**
- Cursos con progresión lógica (ej: matemáticas, programación)
- Cuando prefieres un orden estructurado
- Para revisar material en secuencia específica
- Aprendizaje inicial de conceptos nuevos

**Ejemplo de uso:**
```
Pregunta 1 → Pregunta 2 → Pregunta 3 → ... → Pregunta N
```

### 2. Estrategia Aleatoria

**Descripción:** Presenta las preguntas en orden completamente aleatorio.

**Características:**
- ✅ Evita el sesgo de orden
- ✅ Mejora la retención a largo plazo
- ✅ Ideal para repaso y práctica
- ✅ Desafía la memoria de manera impredecible

**Cuándo usar:**
- Repaso de material ya estudiado
- Preparación para exámenes
- Cuando quieres evitar la memorización por orden
- Práctica de conceptos ya aprendidos

**Ejemplo de uso:**
```
Pregunta 7 → Pregunta 2 → Pregunta 15 → Pregunta 3 → ...
```

### 3. Estrategia de Repetición Espaciada

**Descripción:** Repite preguntas con intervalos crecientes para optimizar la retención.

**Características:**
- ✅ Basada en la curva del olvido
- ✅ Optimiza el momento de repaso
- ✅ Mejora la retención a largo plazo
- ✅ Intervalo configurable (por defecto: cada 3 preguntas)

**Cuándo usar:**
- Memorización de conceptos clave
- Vocabulario y definiciones
- Preparación para exámenes importantes
- Cuando quieres maximizar la retención

**Ejemplo de uso:**
```
Pregunta 1 → Pregunta 2 → Pregunta 3 → [Repite Pregunta 1] → Pregunta 4 → Pregunta 5 → Pregunta 6 → [Repite Pregunta 2] → ...
```

### 4. Estrategia de Repetir Incorrectas

**Descripción:** Repite automáticamente las preguntas que respondiste incorrectamente.

**Características:**
- ✅ Dos fases: original + repetición de incorrectas
- ✅ Enfoque en áreas de dificultad
- ✅ Mejora progresiva en conceptos débiles
- ✅ Sesiones más largas pero más efectivas

**Cuándo usar:**
- Identificar y mejorar áreas débiles
- Preparación intensiva para exámenes
- Cuando tienes tiempo para sesiones más largas
- Aprendizaje de conceptos difíciles

**Ejemplo de uso:**
```
Fase 1: Pregunta 1 → Pregunta 2 → Pregunta 3 → ... → Pregunta N
Fase 2: [Repite preguntas incorrectas] → Pregunta 2 → Pregunta 7 → ...
```

## Cómo Seleccionar una Estrategia

### Para Principiantes
**Recomendación:** Estrategia Secuencial
- Proporciona estructura y orden
- Fácil de seguir y entender
- Ideal para aprender conceptos nuevos

### Para Repaso
**Recomendación:** Estrategia Aleatoria
- Evita la memorización por orden
- Mejora la retención real
- Ideal para preparar exámenes

### Para Memorización
**Recomendación:** Estrategia de Repetición Espaciada
- Optimiza el momento de repaso
- Basada en principios científicos
- Mejora la retención a largo plazo

### Para Mejorar Áreas Débiles
**Recomendación:** Estrategia de Repetir Incorrectas
- Enfoque en conceptos difíciles
- Mejora progresiva
- Sesiones más intensivas

## Configuración de Estrategias

### Cambiar Estrategia
1. Abre la configuración de la sesión
2. Selecciona "Estrategia de Aprendizaje"
3. Elige la estrategia deseada
4. Guarda la configuración

### Configuración Avanzada
- **Intervalo de Repetición**: Configurable en Repetición Espaciada
- **Persistencia**: El estado se guarda automáticamente
- **Progreso**: Se mantiene entre sesiones

## Persistencia y Estado

### Guardado Automático
- El estado de cada estrategia se guarda automáticamente
- Puedes pausar y reanudar en cualquier momento
- El progreso se mantiene entre sesiones

### Restauración de Estado
- Al reanudar una sesión, la estrategia continúa exactamente donde la dejaste
- No se pierde progreso al cerrar la aplicación
- Compatible con múltiples sesiones simultáneas

## Consejos de Uso

### Maximizar la Efectividad
1. **Consistencia**: Usa la misma estrategia para un curso completo
2. **Tiempo**: Dedica tiempo suficiente para cada sesión
3. **Reflexión**: Analiza tu progreso regularmente
4. **Adaptación**: Cambia de estrategia si no funciona para ti

### Combinación de Estrategias
- **Aprendizaje inicial**: Secuencial
- **Repaso general**: Aleatoria
- **Memorización**: Repetición Espaciada
- **Mejora específica**: Repetir Incorrectas

## Preguntas Frecuentes

### ¿Puedo cambiar de estrategia en medio de un curso?
Sí, pero se recomienda mantener la misma estrategia para consistencia.

### ¿Se pierde el progreso al cambiar de estrategia?
No, el progreso se mantiene, pero la presentación de preguntas cambiará.

### ¿Cuál es la mejor estrategia para exámenes?
Depende de tu estilo de aprendizaje, pero Repetición Espaciada suele ser muy efectiva.

### ¿Puedo usar múltiples estrategias simultáneamente?
No, cada sesión usa una estrategia específica, pero puedes tener múltiples sesiones.

### ¿Cómo sé qué estrategia funciona mejor para mí?
Experimenta con diferentes estrategias y observa cuál te da mejores resultados.

## Soporte Técnico

### Problemas Comunes
- **Estrategia no carga**: Verifica que el módulo esté instalado
- **Progreso perdido**: Verifica la configuración de persistencia
- **Rendimiento lento**: Considera usar estrategias más simples

### Contacto
Para soporte técnico o preguntas sobre estrategias:
- **Email**: jjrp1@um.es
- **Documentación**: Ver documentación técnica
- **Issues**: Usar el sistema de issues del proyecto

---

**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Fecha:** 2025-06-15  
**Versión:** 1.0.0  
**Estado:** Documentación de Usuario 