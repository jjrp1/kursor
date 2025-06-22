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

1. **Reciben** una colección de preguntas
2. **Reordenan** las preguntas en memoria según su algoritmo específico
3. **Facilitan** elementos uno tras otro bajo demanda (iteración)
4. **Mantienen** estado interno del progreso de iteración

### 1.2 Analogía con Iteradores Java

```java
// Iterador estándar de Java
Iterator<Pregunta> iterador = preguntas.iterator();
while (iterador.hasNext()) {
    Pregunta pregunta = iterador.next();
    // Procesar pregunta
}

// Nuestra estrategia (iterador especializado)
EstrategiaAprendizaje estrategia = new EstrategiaSecuencial(preguntas);
while (estrategia.tieneSiguiente()) {
    Pregunta pregunta = estrategia.siguientePregunta();
    // Procesar pregunta
}
```

### 1.3 Características de las Estrategias

- **Reordenación en memoria**: Cada estrategia reorganiza las preguntas según su lógica
- **Iteración bajo demanda**: No precarga todas las preguntas, las facilita una a una
- **Estado interno**: Mantiene información del progreso actual
- **Persistencia**: Puede guardar/restaurar su estado de iteración

## 2. Interfaz EstrategiaAprendizaje

### 2.1 Definición de la Interfaz

```java
/**
 * Interfaz que define una estrategia de aprendizaje como iterador especializado.
 * 
 * <p>Una estrategia recibe una colección de preguntas, las reordena según su algoritmo
 * específico, y las facilita una a una bajo demanda, manteniendo estado interno
 * del progreso de iteración.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Iterador especializado:</strong> Facilita preguntas una a una</li>
 *   <li><strong>Reordenación en memoria:</strong> Reorganiza preguntas según su lógica</li>
 *   <li><strong>Estado interno:</strong> Mantiene progreso de iteración</li>
 *   <li><strong>Persistencia:</strong> Puede guardar/restaurar estado</li>
 *   <li><strong>Extensible:</strong> Permite nuevas estrategias</li>
 * </ul>
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
     * Verifica si hay más preguntas disponibles para iterar.
     * 
     * <p>Este método determina si la estrategia puede proporcionar
     * más preguntas según su algoritmo y estado actual.</p>
     * 
     * @return true si hay más preguntas disponibles, false en caso contrario
     */
    boolean tieneSiguiente();
    
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
     * como el progreso de iteración, preguntas ya vistas, etc.</p>
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

### 2.2 Cambios Principales en la Interfaz

#### **Nuevos Métodos:**
- `tieneSiguiente()`: Verifica si hay más elementos (patrón iterador estándar)
- `getProgreso()`: Informa el progreso de la iteración
- `reiniciar()`: Permite reiniciar la iteración

#### **Método Modificado:**
- `siguientePregunta()`: Ahora es más claro que avanza el estado interno

## 3. Análisis de Estrategias Existentes

### 3.1 Estrategia Secuencial

```java
public class EstrategiaSecuencial implements EstrategiaAprendizaje {
    private List<Pregunta> preguntas;
    private int indiceActual = 0;
    
    public EstrategiaSecuencial(List<Pregunta> preguntas) {
        this.preguntas = new ArrayList<>(preguntas); // Copia defensiva
        this.indiceActual = 0;
    }
    
    @Override
    public boolean tieneSiguiente() {
        return indiceActual < preguntas.size();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (!tieneSiguiente()) {
            return null;
        }
        return preguntas.get(indiceActual++);
    }
    
    @Override
    public double getProgreso() {
        if (preguntas.isEmpty()) return 0.0;
        return (double) indiceActual / preguntas.size();
    }
    
    @Override
    public void reiniciar() {
        indiceActual = 0;
    }
}
```

**Características:**
- **Reordenación**: Ninguna (mantiene orden original)
- **Estado**: Solo índice actual
- **Complejidad**: Muy baja

### 3.2 Estrategia Aleatoria

```java
public class EstrategiaAleatoria implements EstrategiaAprendizaje {
    private List<Pregunta> preguntas;
    private List<Pregunta> preguntasDisponibles;
    private Random random = new Random();
    
    public EstrategiaAleatoria(List<Pregunta> preguntas) {
        this.preguntas = new ArrayList<>(preguntas);
        this.preguntasDisponibles = new ArrayList<>(preguntas);
    }
    
    @Override
    public boolean tieneSiguiente() {
        return !preguntasDisponibles.isEmpty();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (!tieneSiguiente()) {
            return null;
        }
        
        int indiceAleatorio = random.nextInt(preguntasDisponibles.size());
        return preguntasDisponibles.remove(indiceAleatorio);
    }
    
    @Override
    public double getProgreso() {
        if (preguntas.isEmpty()) return 0.0;
        return (double) (preguntas.size() - preguntasDisponibles.size()) / preguntas.size();
    }
    
    @Override
    public void reiniciar() {
        preguntasDisponibles = new ArrayList<>(preguntas);
    }
}
```

**Características:**
- **Reordenación**: Aleatoria, sin repetición
- **Estado**: Lista de preguntas disponibles
- **Complejidad**: Baja

## 4. Revisión de Propuestas Anteriores

### 4.1 Problemas Identificados

#### **Repetición Espaciada Avanzada:**
- ❌ **Problema**: Requiere parámetro "calidad" que no existe en el sistema actual
- ❌ **Problema**: Necesita modificar la interfaz para registrar respuestas
- ❌ **Problema**: Requiere persistencia compleja de estado por pregunta

#### **Estrategia Por Dificultad:**
- ❌ **Problema**: Requiere atributo "dificultad" en las preguntas
- ❌ **Problema**: Necesita modificar el modelo de datos existente

### 4.2 Propuestas Corregidas

#### **Estrategia "Por Categorías" (Sencilla)**
```java
public class EstrategiaPorCategorias implements EstrategiaAprendizaje {
    private Map<String, List<Pregunta>> categorias;
    private Iterator<String> iteradorCategorias;
    private Iterator<Pregunta> iteradorPreguntas;
    private String categoriaActual;
    
    public EstrategiaPorCategorias(List<Pregunta> preguntas) {
        // Asumimos que las preguntas tienen un método getCategoria()
        // Si no existe, usamos un valor por defecto
        this.categorias = preguntas.stream()
            .collect(Collectors.groupingBy(p -> 
                p.getCategoria() != null ? p.getCategoria() : "General"));
        this.iteradorCategorias = categorias.keySet().iterator();
    }
    
    @Override
    public boolean tieneSiguiente() {
        return (iteradorPreguntas != null && iteradorPreguntas.hasNext()) || 
               iteradorCategorias.hasNext();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (iteradorPreguntas == null || !iteradorPreguntas.hasNext()) {
            if (!iteradorCategorias.hasNext()) {
                return null;
            }
            categoriaActual = iteradorCategorias.next();
            iteradorPreguntas = categorias.get(categoriaActual).iterator();
        }
        return iteradorPreguntas.next();
    }
    
    @Override
    public double getProgreso() {
        // Implementación compleja que requiere contar progreso
        // Simplificada para este ejemplo
        return 0.5; // Placeholder
    }
    
    @Override
    public void reiniciar() {
        iteradorCategorias = categorias.keySet().iterator();
        iteradorPreguntas = null;
    }
}
```

#### **Estrategia "Repetición Espaciada Mejorada" (Sin Modificar Código)**
```java
public class EstrategiaRepeticionEspaciadaMejorada implements EstrategiaAprendizaje {
    private List<Pregunta> preguntas;
    private List<Pregunta> preguntasDisponibles;
    private int intervalo;
    private int contador = 0;
    
    public EstrategiaRepeticionEspaciadaMejorada(List<Pregunta> preguntas, int intervalo) {
        this.preguntas = new ArrayList<>(preguntas);
        this.preguntasDisponibles = new ArrayList<>(preguntas);
        this.intervalo = intervalo;
    }
    
    @Override
    public boolean tieneSiguiente() {
        return !preguntasDisponibles.isEmpty();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (!tieneSiguiente()) {
            return null;
        }
        
        // Seleccionar pregunta según patrón de repetición espaciada
        int indice = contador % preguntasDisponibles.size();
        Pregunta pregunta = preguntasDisponibles.get(indice);
        
        // Cada 'intervalo' preguntas, mover la pregunta al final para repetición
        if (contador % intervalo == 0) {
            preguntasDisponibles.remove(indice);
            preguntasDisponibles.add(pregunta);
        }
        
        contador++;
        return pregunta;
    }
    
    @Override
    public double getProgreso() {
        if (preguntas.isEmpty()) return 0.0;
        return (double) contador / (preguntas.size() * 2); // Estimación
    }
    
    @Override
    public void reiniciar() {
        preguntasDisponibles = new ArrayList<>(preguntas);
        contador = 0;
    }
}
```

## 5. Propuesta Final: Estrategia "Por Categorías"

### 5.1 Justificación

La **Estrategia Por Categorías** es la más adecuada porque:

1. **No requiere modificar código existente**: Solo necesita que las preguntas tengan categoría (con valor por defecto)
2. **Implementación sencilla**: Solo agrupar y iterar
3. **Valor educativo real**: Los usuarios prefieren estudiar por temas
4. **Lógica intuitiva**: Fácil de entender y predecir

### 5.2 Implementación Simplificada

```java
public class EstrategiaPorCategorias implements EstrategiaAprendizaje {
    private List<List<Pregunta>> bloquesCategoria;
    private int categoriaActual = 0;
    private int preguntaActual = 0;
    
    public EstrategiaPorCategorias(List<Pregunta> preguntas) {
        // Agrupar por categoría, con valor por defecto "General"
        Map<String, List<Pregunta>> categorias = preguntas.stream()
            .collect(Collectors.groupingBy(p -> 
                p.getCategoria() != null ? p.getCategoria() : "General"));
        
        this.bloquesCategoria = new ArrayList<>(categorias.values());
    }
    
    @Override
    public boolean tieneSiguiente() {
        return categoriaActual < bloquesCategoria.size();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (!tieneSiguiente()) {
            return null;
        }
        
        List<Pregunta> categoria = bloquesCategoria.get(categoriaActual);
        Pregunta pregunta = categoria.get(preguntaActual);
        
        preguntaActual++;
        if (preguntaActual >= categoria.size()) {
            categoriaActual++;
            preguntaActual = 0;
        }
        
        return pregunta;
    }
    
    @Override
    public double getProgreso() {
        if (bloquesCategoria.isEmpty()) return 0.0;
        
        int totalPreguntas = bloquesCategoria.stream()
            .mapToInt(List::size).sum();
        int preguntasVistas = 0;
        
        for (int i = 0; i < categoriaActual; i++) {
            preguntasVistas += bloquesCategoria.get(i).size();
        }
        preguntasVistas += preguntaActual;
        
        return (double) preguntasVistas / totalPreguntas;
    }
    
    @Override
    public void reiniciar() {
        categoriaActual = 0;
        preguntaActual = 0;
    }
}
```

## 6. Plan de Implementación

### 6.1 Fase 1: Actualizar Interfaz (1 semana)
- [ ] Actualizar `EstrategiaAprendizaje` con nuevos métodos
- [ ] Refactorizar estrategias existentes
- [ ] Testing de compatibilidad

### 6.2 Fase 2: Implementar Estrategia Por Categorías (1 semana)
- [ ] Implementar `EstrategiaPorCategorias`
- [ ] Testing exhaustivo
- [ ] Documentación

### 6.3 Fase 3: Módulos de Carga Dinámica (2-3 semanas)
- [ ] Diseñar sistema de módulos
- [ ] Refactorizar a módulos
- [ ] Testing y optimización

## 7. Conclusiones

1. **Las estrategias son iteradores especializados** que reordenan y facilitan elementos bajo demanda
2. **La interfaz necesita métodos adicionales** para seguir el patrón iterador estándar
3. **La Estrategia Por Categorías** es la más adecuada: sencilla, sin modificar código existente, y con valor educativo real
4. **Los módulos de carga dinámica** son una excelente idea para el futuro

**Recomendación**: Empezar actualizando la interfaz y implementando la Estrategia Por Categorías como prueba de concepto. 