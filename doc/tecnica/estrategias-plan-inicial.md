---
title: Plan Inicial de Estrategias de Aprendizaje
subtitle: Documentación técnica del sistema de estrategias
description: Análisis y propuestas para el sistema de estrategias de aprendizaje en Kursor
keywords: estrategias, aprendizaje, módulos, iteradores
status: desarrollo
created: 2025-01-27
modified: 2025-01-27
author: "Juanjo Ruiz"
---

# Plan Inicial de Estrategias de Aprendizaje

## 1. Concepto Fundamental: Estrategias como Iteradores

### 1.1 Definición Conceptual

Las **estrategias de aprendizaje** son **iteradores especializados** que:

1. **Reciben** una colección de preguntas de un bloque específico
2. **Reordenan** las preguntas en memoria según su algoritmo específico
3. **Facilitan** elementos uno tras otro bajo demanda (iteración)
4. **Mantienen** estado interno del progreso de iteración
5. **Reaccionan** a las respuestas del usuario para ajustar su comportamiento

### 1.2 Características de las Estrategias

- **Trabajo a nivel de bloque**: Cada estrategia maneja preguntas del mismo tipo/modelo
- **Reordenación en memoria**: Cada estrategia reorganiza las preguntas según su lógica
- **Iteración bajo demanda**: No precarga todas las preguntas, las facilita una a una
- **Estado interno**: Mantiene información del progreso actual y pregunta actual
- **Persistencia**: Puede guardar/restaurar su estado de iteración
- **Reactividad**: Puede ajustar su comportamiento basado en las respuestas del usuario

## 2. Interfaz EstrategiaAprendizaje

### 2.1 Definición de la Interfaz

```java
/**
 * Interfaz que define una estrategia de aprendizaje como iterador especializado.
 * 
 * <p>Una estrategia recibe una colección de preguntas de un bloque específico,
 * las reordena según su algoritmo específico, y las facilita una a una bajo demanda,
 * manteniendo estado interno del progreso de iteración y reaccionando a las
 * respuestas del usuario.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Iterador especializado:</strong> Facilita preguntas una a una</li>
 *   <li><strong>Trabajo por bloques:</strong> Maneja preguntas del mismo tipo/modelo</li>
 *   <li><strong>Reordenación en memoria:</strong> Reorganiza preguntas según su lógica</li>
 *   <li><strong>Estado interno:</strong> Mantiene progreso y pregunta actual</li>
 *   <li><strong>Reactividad:</strong> Ajusta comportamiento según respuestas</li>
 *   <li><strong>Persistencia:</strong> Puede guardar/restaurar estado</li>
 *   <li><strong>Extensible:</strong> Permite nuevas estrategias</li>
 * </ul>
 * 
 * <p>Flujo típico de uso:</p>
 * <ol>
 *   <li>Obtener primera pregunta con {@link #primeraPregunta()}</li>
 *   <li>Mientras {@link #hayMasPreguntas()} sea true:</li>
 *   <li>Mostrar pregunta al usuario</li>
 *   <li>Obtener respuesta del usuario</li>
 *   <li>Registrar respuesta con {@link #registrarRespuesta(Respuesta)}</li>
 *   <li>Obtener siguiente pregunta con {@link #siguientePregunta()}</li>
 * </ol>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EstrategiaAprendizaje extends Serializable {
    
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * 
     * @return El nombre de la estrategia de aprendizaje
     */
    String getNombre();
    
    /**
     * Obtiene la primera pregunta según la estrategia de aprendizaje.
     * 
     * <p>Este método retorna la primera pregunta que debe presentarse
     * al usuario según el algoritmo de la estrategia. Inicializa el
     * estado interno de la estrategia.</p>
     * 
     * @return La primera pregunta a mostrar, o null si no hay preguntas
     */
    Pregunta primeraPregunta();
    
    /**
     * Registra la respuesta del usuario para la pregunta actual.
     * 
     * <p>Este método permite que la estrategia reaccione a la respuesta
     * del usuario y ajuste su comportamiento futuro. Por ejemplo, una
     * estrategia de repetición de incorrectas podría agregar la pregunta
     * a una cola de repetición si la respuesta es incorrecta.</p>
     * 
     * <p>La estrategia conoce internamente cuál es la pregunta actual,
     * por lo que no es necesario especificarla como parámetro.</p>
     * 
     * @param respuesta La respuesta del usuario para la pregunta actual
     */
    void registrarRespuesta(Respuesta respuesta);
    
    /**
     * Verifica si hay más preguntas disponibles para iterar.
     * 
     * <p>Este método determina si la estrategia puede proporcionar
     * más preguntas según su algoritmo y estado actual.</p>
     * 
     * @return true si hay más preguntas disponibles, false en caso contrario
     */
    boolean hayMasPreguntas();
    
    /**
     * Obtiene la siguiente pregunta según la estrategia de aprendizaje.
     * 
     * <p>Este método retorna la siguiente pregunta según el algoritmo
     * de la estrategia y avanza el estado interno de iteración.</p>
     * 
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Si no hay más preguntas: retorna null</li>
     *   <li>En caso contrario: retorna la siguiente pregunta y avanza el estado</li>
     *   <li>Debe ser thread-safe si se usa en entornos concurrentes</li>
     * </ul>
     * 
     * @return La siguiente pregunta a mostrar, o null si no hay más preguntas
     */
    Pregunta siguientePregunta();
    
    /**
     * Guarda el estado actual de la estrategia.
     * 
     * <p>Este método persiste el estado interno de la estrategia para
     * permitir su restauración posterior. El estado incluye información
     * como el progreso de iteración, pregunta actual, preguntas ya vistas, etc.</p>
     */
    void guardarEstado();
    
    /**
     * Restaura el estado previamente guardado de la estrategia.
     * 
     * <p>Este método recupera el estado guardado y restaura la estrategia
     * a ese punto, permitiendo continuar la iteración desde donde se dejó.</p>
     */
    void restaurarEstado();
    
    /**
     * Obtiene el progreso actual de la estrategia.
     * 
     * <p>Retorna un valor entre 0.0 y 1.0 que representa el progreso
     * de la iteración según la estrategia específica.</p>
     * 
     * @return El progreso como valor entre 0.0 (inicio) y 1.0 (completado)
     */
    double getProgreso();
    
    /**
     * Reinicia la estrategia al inicio de la iteración.
     * 
     * <p>Este método resetea el estado interno de la estrategia,
     * permitiendo comenzar una nueva iteración desde el principio.</p>
     */
    void reiniciar();
}
```

### 2.2 Flujo de Uso Típico

```java
// 1. Crear estrategia con preguntas del bloque
EstrategiaAprendizaje estrategia = new EstrategiaRepeticionIncorrectas(preguntasBloque);

// 2. Obtener primera pregunta
Pregunta pregunta = estrategia.primeraPregunta();

// 3. Bucle principal
while (estrategia.hayMasPreguntas()) {
    // Mostrar pregunta al usuario
    // Obtener respuesta del usuario
    Respuesta respuesta = /* respuesta del usuario */;
    
    // Registrar respuesta (permite que la estrategia reaccione)
    estrategia.registrarRespuesta(respuesta);
    
    // Obtener siguiente pregunta
    pregunta = estrategia.siguientePregunta();
}

// 4. Mostrar resultados del bloque
```

## 3. Estrategias Implementadas

### 3.1 Estrategia Secuencial

**Descripción:** Presenta las preguntas en el orden original del bloque.

**Características:**
- **Reordenación**: Ninguna (mantiene orden original)
- **Estado**: Índice actual y pregunta actual
- **Reactividad**: No reacciona a respuestas
- **Complejidad**: Muy baja

### 3.2 Estrategia Aleatoria

**Descripción:** Presenta las preguntas en orden aleatorio.

**Características:**
- **Reordenación**: Aleatoria, sin repetición
- **Estado**: Lista de preguntas disponibles y pregunta actual
- **Reactividad**: No reacciona a respuestas
- **Complejidad**: Baja

### 3.3 Estrategia de Repetición Espaciada

**Descripción:** Repite preguntas con intervalos crecientes para optimizar la retención.

**Características:**
- **Reordenación**: Con repetición según intervalos
- **Estado**: Índice actual, intervalo configurable, preguntas procesadas
- **Reactividad**: Ajusta intervalos según respuestas
- **Complejidad**: Media

### 3.4 Estrategia de Repetir Incorrectas

**Descripción:** Repite automáticamente las preguntas que respondiste incorrectamente.

**Características:**
- **Reordenación**: Dos fases (originales + incorrectas)
- **Estado**: Índice actual, cola de incorrectas, fase actual
- **Reactividad**: Registra incorrectas para repetición
- **Complejidad**: Media

## 4. Estrategia Repetición de Incorrectas - IMPLEMENTADA

### 4.1 Concepto

Una estrategia que mantiene dos fases:
1. **Fase principal**: Preguntas del bloque en orden original
2. **Fase de repetición**: Preguntas falladas que se repiten al final

### 4.2 Implementación

La estrategia ya está implementada en el módulo `kursor-repetir-incorrectas-strategy` con las siguientes características:

- **Módulo independiente**: Carga dinámica mediante ServiceLoader
- **Dos fases claras**: Originales + repetición de incorrectas
- **Reactiva**: Registra incorrectas automáticamente
- **Persistencia**: Estado serializable para continuar sesiones
- **Progreso inteligente**: Calcula progreso considerando ambas fases

### 4.3 Ventajas de esta Estrategia

1. **✅ No modifica código existente**: Usa la interfaz actual
2. **✅ Trabaja a nivel de bloque**: Solo maneja preguntas del mismo tipo/modelo
3. **✅ Lógica simple**: Dos fases claras (originales + incorrectas)
4. **✅ Valor educativo real**: Refuerza lo que el usuario no sabe
5. **✅ Implementación sencilla**: Solo dos colas y un método de registro
6. **✅ Reactiva**: Ajusta su comportamiento según las respuestas
7. **✅ Modular**: Implementada como módulo independiente

## 5. Estado Actual de Implementación

### ✅ Completado
- **Interfaz EstrategiaAprendizaje**: Implementada y estable
- **Estrategia Secuencial**: Implementada como módulo independiente
- **Estrategia Aleatoria**: Implementada como módulo independiente
- **Estrategia de Repetición Espaciada**: Implementada como módulo independiente
- **Estrategia de Repetir Incorrectas**: Implementada como módulo independiente
- **Sistema de módulos**: Carga dinámica mediante ServiceLoader
- **Testing**: Pruebas unitarias completas para todas las estrategias

### 🔧 Mejoras Futuras
- **Optimización de rendimiento**: Mejoras en algoritmos de estrategias
- **Nuevas estrategias**: Implementación de estrategias más avanzadas
- **Configuración avanzada**: Parámetros configurables por estrategia
- **Analytics**: Métricas detalladas de rendimiento por estrategia

## 6. Consideraciones Técnicas

### 6.1 Clase Respuesta

```java
public class Respuesta {
    private boolean esCorrecta;
    private String textoRespuesta;
    private long tiempoRespuesta;
    private int intentos;
    
    // Constructores, getters, setters...
    
    public boolean esCorrecta() {
        return esCorrecta;
    }
}
```

### 6.2 Compatibilidad
- Mantener compatibilidad con estrategias existentes
- No romper la interfaz actual
- Migración gradual

### 6.3 Testing
- Tests unitarios para cada estrategia
- Tests de integración para el flujo completo
- Tests de persistencia de estado

## 7. Conclusiones

1. **Las estrategias son iteradores especializados** que trabajan a nivel de bloque ✅ **IMPLEMENTADO**
2. **La interfaz mejorada** incluye `primeraPregunta()`, `registrarRespuesta()` sin parámetro pregunta, y control de flujo ✅ **IMPLEMENTADO**
3. **Las 4 estrategias están implementadas** como módulos independientes con carga dinámica ✅ **IMPLEMENTADO**
4. **El sistema modular** permite extensibilidad sin modificar el código principal ✅ **IMPLEMENTADO**

**Estado Actual**: Todas las estrategias están implementadas y funcionando correctamente. El sistema es modular, extensible y bien documentado.

**Próximos Pasos**: 
- Optimización de rendimiento
- Nuevas estrategias avanzadas
- Mejoras en la configuración
- Analytics detallados 