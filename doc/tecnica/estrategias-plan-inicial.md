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

```java
public class EstrategiaSecuencial implements EstrategiaAprendizaje {
    private List<Pregunta> preguntas;
    private int indiceActual = 0;
    private Pregunta preguntaActual;
    
    public EstrategiaSecuencial(List<Pregunta> preguntas) {
        this.preguntas = new ArrayList<>(preguntas);
        this.indiceActual = 0;
    }
    
    @Override
    public Pregunta primeraPregunta() {
        if (preguntas.isEmpty()) {
            return null;
        }
        preguntaActual = preguntas.get(0);
        return preguntaActual;
    }
    
    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        // La estrategia secuencial no reacciona a las respuestas
        // Solo mantiene el progreso
    }
    
    @Override
    public boolean hayMasPreguntas() {
        return indiceActual < preguntas.size();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (!hayMasPreguntas()) {
            return null;
        }
        indiceActual++;
        if (indiceActual < preguntas.size()) {
            preguntaActual = preguntas.get(indiceActual);
            return preguntaActual;
        }
        return null;
    }
    
    @Override
    public double getProgreso() {
        if (preguntas.isEmpty()) return 0.0;
        return (double) indiceActual / preguntas.size();
    }
    
    @Override
    public void reiniciar() {
        indiceActual = 0;
        preguntaActual = null;
    }
}
```

**Características:**
- **Reordenación**: Ninguna (mantiene orden original)
- **Estado**: Índice actual y pregunta actual
- **Reactividad**: No reacciona a respuestas
- **Complejidad**: Muy baja

### 3.2 Estrategia Aleatoria

```java
public class EstrategiaAleatoria implements EstrategiaAprendizaje {
    private List<Pregunta> preguntas;
    private List<Pregunta> preguntasDisponibles;
    private Pregunta preguntaActual;
    private Random random = new Random();
    
    public EstrategiaAleatoria(List<Pregunta> preguntas) {
        this.preguntas = new ArrayList<>(preguntas);
        this.preguntasDisponibles = new ArrayList<>(preguntas);
    }
    
    @Override
    public Pregunta primeraPregunta() {
        if (preguntasDisponibles.isEmpty()) {
            return null;
        }
        int indiceAleatorio = random.nextInt(preguntasDisponibles.size());
        preguntaActual = preguntasDisponibles.get(indiceAleatorio);
        return preguntaActual;
    }
    
    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        // La estrategia aleatoria no reacciona a las respuestas
        // Solo mantiene el progreso
    }
    
    @Override
    public boolean hayMasPreguntas() {
        return !preguntasDisponibles.isEmpty();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (!hayMasPreguntas()) {
            return null;
        }
        
        // Remover la pregunta actual de las disponibles
        preguntasDisponibles.remove(preguntaActual);
        
        if (preguntasDisponibles.isEmpty()) {
            preguntaActual = null;
            return null;
        }
        
        int indiceAleatorio = random.nextInt(preguntasDisponibles.size());
        preguntaActual = preguntasDisponibles.get(indiceAleatorio);
        return preguntaActual;
    }
    
    @Override
    public double getProgreso() {
        if (preguntas.isEmpty()) return 0.0;
        return (double) (preguntas.size() - preguntasDisponibles.size()) / preguntas.size();
    }
    
    @Override
    public void reiniciar() {
        preguntasDisponibles = new ArrayList<>(preguntas);
        preguntaActual = null;
    }
}
```

**Características:**
- **Reordenación**: Aleatoria, sin repetición
- **Estado**: Lista de preguntas disponibles y pregunta actual
- **Reactividad**: No reacciona a respuestas
- **Complejidad**: Baja

## 4. Propuesta: Estrategia Repetición de Incorrectas

### 4.1 Concepto

Una estrategia que mantiene dos fases:
1. **Fase principal**: Preguntas del bloque en orden original
2. **Fase de repetición**: Preguntas falladas que se repiten al final

### 4.2 Implementación

```java
public class EstrategiaRepeticionIncorrectas implements EstrategiaAprendizaje {
    private List<Pregunta> preguntasBloque;           // Preguntas originales del bloque
    private Queue<Pregunta> colaIncorrectas;          // Preguntas falladas para repetir
    private int indiceActual = 0;                     // Índice en preguntas originales
    private boolean enFaseRepeticion = false;         // Si estamos repitiendo incorrectas
    private Set<Long> preguntasIncorrectas;           // IDs de preguntas incorrectas
    private Pregunta preguntaActual;                  // Pregunta actual
    
    public EstrategiaRepeticionIncorrectas(List<Pregunta> preguntasBloque) {
        this.preguntasBloque = new ArrayList<>(preguntasBloque);
        this.colaIncorrectas = new LinkedList<>();
        this.preguntasIncorrectas = new HashSet<>();
    }
    
    @Override
    public Pregunta primeraPregunta() {
        if (preguntasBloque.isEmpty()) {
            return null;
        }
        preguntaActual = preguntasBloque.get(0);
        return preguntaActual;
    }
    
    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        if (!respuesta.esCorrecta() && !preguntasIncorrectas.contains(preguntaActual.getId())) {
            // Es incorrecta y no la hemos marcado antes
            preguntasIncorrectas.add(preguntaActual.getId());
            colaIncorrectas.offer(preguntaActual);
        }
    }
    
    @Override
    public boolean hayMasPreguntas() {
        // Hay siguiente si: estamos en preguntas originales O hay incorrectas para repetir
        return indiceActual < preguntasBloque.size() || !colaIncorrectas.isEmpty();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (!hayMasPreguntas()) {
            return null;
        }
        
        // Fase 1: Preguntas originales del bloque
        if (indiceActual < preguntasBloque.size()) {
            indiceActual++;
            if (indiceActual < preguntasBloque.size()) {
                preguntaActual = preguntasBloque.get(indiceActual);
                return preguntaActual;
            } else {
                // Terminamos fase principal, comenzamos repetición
                enFaseRepeticion = true;
                if (!colaIncorrectas.isEmpty()) {
                    preguntaActual = colaIncorrectas.poll();
                    return preguntaActual;
                }
                return null;
            }
        }
        
        // Fase 2: Repetición de incorrectas
        if (!colaIncorrectas.isEmpty()) {
            preguntaActual = colaIncorrectas.poll();
            return preguntaActual;
        }
        
        return null;
    }
    
    @Override
    public double getProgreso() {
        int totalPreguntas = preguntasBloque.size() + colaIncorrectas.size();
        if (totalPreguntas == 0) return 0.0;
        
        int preguntasVistas = indiceActual;
        return (double) preguntasVistas / totalPreguntas;
    }
    
    @Override
    public void reiniciar() {
        indiceActual = 0;
        colaIncorrectas.clear();
        preguntasIncorrectas.clear();
        enFaseRepeticion = false;
        preguntaActual = null;
    }
    
    /**
     * Obtiene el número de preguntas incorrectas registradas.
     */
    public int getNumeroIncorrectas() {
        return colaIncorrectas.size();
    }
    
    /**
     * Verifica si estamos en la fase de repetición.
     */
    public boolean estaEnFaseRepeticion() {
        return enFaseRepeticion;
    }
}
```

### 4.3 Ventajas de esta Estrategia

1. **✅ No modifica código existente**: Usa la interfaz actual
2. **✅ Trabaja a nivel de bloque**: Solo maneja preguntas del mismo tipo/modelo
3. **✅ Lógica simple**: Dos fases claras (originales + incorrectas)
4. **✅ Valor educativo real**: Refuerza lo que el usuario no sabe
5. **✅ Implementación sencilla**: Solo dos colas y un método de registro
6. **✅ Reactiva**: Ajusta su comportamiento según las respuestas

## 5. Plan de Implementación

### 5.1 Fase 1: Actualizar Interfaz (1 semana)
- [ ] Actualizar `EstrategiaAprendizaje` con nuevos métodos
- [ ] Refactorizar estrategias existentes (Secuencial, Aleatoria)
- [ ] Testing de compatibilidad

### 5.2 Fase 2: Implementar Estrategia Repetición de Incorrectas (1 semana)
- [ ] Implementar `EstrategiaRepeticionIncorrectas`
- [ ] Testing exhaustivo
- [ ] Documentación

### 5.3 Fase 3: Módulos de Carga Dinámica (2-3 semanas)
- [ ] Diseñar sistema de módulos
- [ ] Refactorizar a módulos
- [ ] Testing y optimización

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

1. **Las estrategias son iteradores especializados** que trabajan a nivel de bloque
2. **La interfaz mejorada** incluye `primeraPregunta()`, `registrarRespuesta()` sin parámetro pregunta, y control de flujo
3. **La Estrategia Repetición de Incorrectas** es la propuesta más adecuada: sencilla, reactiva, y con valor educativo real
4. **Los módulos de carga dinámica** son una excelente idea para el futuro

**Recomendación**: Empezar actualizando la interfaz y implementando la Estrategia Repetición de Incorrectas como prueba de concepto. 