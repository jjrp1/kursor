---
title: Plan Inicial de Estrategias de Aprendizaje
subtitle: Documentación técnica del sistema de estrategias
description: Análisis y propuestas para el sistema de estrategias de aprendizaje en Kursor
keywords: estrategias, aprendizaje, módulos, repetición espaciada
status: desarrollo
created: 2025-01-27
modified: 2025-01-27
author: "Juanjo Ruiz"
---

# Plan Inicial de Estrategias de Aprendizaje

## 1. Estado Actual del Sistema

### 1.1 Estrategias Implementadas

Actualmente el sistema cuenta con tres estrategias básicas implementadas:

#### **Estrategia Secuencial**
- **Propósito**: Recorrer preguntas en orden fijo
- **Complejidad**: Baja
- **Casos de uso**: Contenido que debe aprenderse en orden específico, principiantes
- **Estado**: Implementada y funcional

#### **Estrategia Aleatoria**
- **Propósito**: Selección completamente aleatoria de preguntas
- **Complejidad**: Baja
- **Casos de uso**: Repaso general, evitar memorización por posición
- **Estado**: Implementada y funcional

#### **Estrategia Repetición Espaciada**
- **Propósito**: Repetición cíclica con intervalos configurables
- **Complejidad**: Media
- **Casos de uso**: Memorización, retención a largo plazo
- **Estado**: Implementada y funcional

### 1.2 Arquitectura Actual

```java
public interface EstrategiaAprendizaje extends Serializable {
    String getNombre();
    void guardarEstado();
    void restaurarEstado();
    Pregunta siguientePregunta();
}
```

**Ventajas de la arquitectura actual:**
- Patrón Strategy bien implementado
- Extensibilidad mediante interfaz
- Persistencia de estado
- Logging detallado

## 2. Propuesta: Módulos de Carga Dinámica

### 2.1 Motivación

Las estrategias secuencial y aleatoria son efectivamente muy sencillas, pero la propuesta de implementarlas como módulos de carga dinámica ofrece varias ventajas:

#### **Beneficios:**
- **Extensibilidad sin recompilación**: Nuevas estrategias sin modificar el core
- **Distribución independiente**: Estrategias como paquetes separados
- **Testing aislado**: Cada estrategia puede probarse independientemente
- **Mantenimiento simplificado**: Cambios en estrategias no afectan el core
- **Reutilización**: Estrategias pueden usarse en otros proyectos

### 2.2 Diseño Propuesto

#### **Estructura de Módulos:**
```
kursor-estrategias/
├── kursor-estrategia-secuencial/
├── kursor-estrategia-aleatoria/
├── kursor-estrategia-repeticion-espaciada/
└── kursor-estrategia-nueva/
```

#### **Interfaz del Módulo:**
```java
public interface EstrategiaModule {
    String getModuleId();
    String getModuleName();
    String getModuleVersion();
    EstrategiaAprendizaje createEstrategia(List<Pregunta> preguntas, Map<String, Object> config);
    List<String> getRequiredParameters();
    String getDescription();
}
```

#### **Sistema de Carga:**
```java
public class EstrategiaModuleLoader {
    public List<EstrategiaModule> loadModules(String modulesPath);
    public EstrategiaAprendizaje createEstrategia(String moduleId, List<Pregunta> preguntas, Map<String, Object> config);
}
```

### 2.3 Implementación Gradual

**Fase 1**: Refactorizar estrategias existentes como módulos
**Fase 2**: Implementar sistema de carga dinámica
**Fase 3**: Crear nuevas estrategias como módulos

## 3. Análisis: Estrategia Repetición Espaciada

### 3.1 Implementación Actual

La implementación actual es una versión simplificada que:
- Usa intervalos fijos (por defecto cada 3 preguntas)
- No considera el rendimiento del usuario
- No implementa algoritmos científicos de repetición espaciada

### 3.2 Propuesta de Mejora

#### **Algoritmo SM-2 Simplificado**

Propongo implementar una versión simplificada del algoritmo SuperMemo SM-2:

```java
public class EstrategiaRepeticionEspaciadaAvanzada implements EstrategiaAprendizaje {
    private Map<Long, PreguntaEstado> estadosPreguntas;
    
    private static class PreguntaEstado {
        int repeticiones = 0;
        int intervalo = 1;
        double factorFacilidad = 2.5;
        Date proximaRepeticion;
    }
    
    @Override
    public Pregunta siguientePregunta() {
        // 1. Buscar preguntas listas para repetición
        // 2. Si no hay, seleccionar nueva pregunta
        // 3. Retornar la pregunta más prioritaria
    }
    
    public void registrarRespuesta(Pregunta pregunta, int calidad) {
        // Aplicar algoritmo SM-2 simplificado
        // Actualizar intervalo y factor de facilidad
    }
}
```

#### **Parámetros del Algoritmo:**
- **Calidad de respuesta**: 0-5 (muy mal a perfecto)
- **Factor de facilidad**: Se ajusta según el rendimiento
- **Intervalo**: Se calcula dinámicamente
- **Repeticiones**: Contador de veces vista la pregunta

### 3.3 Ventajas de la Propuesta

- **Base científica**: Basado en algoritmos probados
- **Adaptativo**: Se ajusta al rendimiento del usuario
- **Eficiente**: Optimiza el tiempo de estudio
- **Configurable**: Parámetros ajustables

## 4. Propuesta: Cuarta Estrategia "Sencilla"

### 4.1 Estrategia "Por Dificultad"

#### **Concepto:**
Una estrategia que prioriza preguntas según su nivel de dificultad, comenzando por las más fáciles y progresando hacia las más difíciles.

#### **Implementación:**
```java
public class EstrategiaPorDificultad implements EstrategiaAprendizaje {
    private List<Pregunta> preguntasOrdenadas;
    private int indiceActual = 0;
    
    public EstrategiaPorDificultad(List<Pregunta> preguntas) {
        // Ordenar preguntas por dificultad (fácil -> difícil)
        this.preguntasOrdenadas = preguntas.stream()
            .sorted(Comparator.comparing(Pregunta::getDificultad))
            .collect(Collectors.toList());
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (indiceActual >= preguntasOrdenadas.size()) {
            return null;
        }
        return preguntasOrdenadas.get(indiceActual++);
    }
}
```

#### **Ventajas:**
- **Implementación trivial**: Solo requiere ordenar y recorrer
- **No modifica código existente**: Usa la interfaz actual
- **Lógica intuitiva**: Fácil de entender para usuarios
- **Progresión natural**: Construye confianza gradualmente

#### **Consideraciones:**
- Requiere que las preguntas tengan un campo `dificultad`
- Si no existe, puede usar un valor por defecto
- Compatible con la arquitectura actual sin cambios

### 4.2 Alternativa: Estrategia "Por Categorías"

#### **Concepto:**
Agrupa preguntas por categorías y las presenta en bloques, completando una categoría antes de pasar a la siguiente.

#### **Implementación:**
```java
public class EstrategiaPorCategorias implements EstrategiaAprendizaje {
    private Map<String, List<Pregunta>> categorias;
    private Iterator<String> iteradorCategorias;
    private Iterator<Pregunta> iteradorPreguntas;
    private String categoriaActual;
    
    public EstrategiaPorCategorias(List<Pregunta> preguntas) {
        // Agrupar preguntas por categoría
        this.categorias = preguntas.stream()
            .collect(Collectors.groupingBy(Pregunta::getCategoria));
        this.iteradorCategorias = categorias.keySet().iterator();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (iteradorPreguntas == null || !iteradorPreguntas.hasNext()) {
            if (!iteradorCategorias.hasNext()) {
                return null; // No hay más categorías
            }
            categoriaActual = iteradorCategorias.next();
            iteradorPreguntas = categorias.get(categoriaActual).iterator();
        }
        return iteradorPreguntas.next();
    }
}
```

## 5. Plan de Implementación

### 5.1 Fase 1: Refactorización (Semana 1-2)
- [ ] Crear estructura de módulos
- [ ] Refactorizar estrategias existentes
- [ ] Implementar sistema de carga dinámica

### 5.2 Fase 2: Mejoras (Semana 3-4)
- [ ] Implementar repetición espaciada avanzada
- [ ] Agregar cuarta estrategia
- [ ] Testing y documentación

### 5.3 Fase 3: Optimización (Semana 5-6)
- [ ] Optimizar rendimiento
- [ ] Mejorar logging
- [ ] Documentación final

## 6. Consideraciones Técnicas

### 6.1 Compatibilidad
- Mantener compatibilidad con estrategias existentes
- No romper la interfaz actual
- Migración gradual

### 6.2 Testing
- Tests unitarios para cada estrategia
- Tests de integración para módulos
- Tests de rendimiento

### 6.3 Documentación
- Documentación técnica de cada estrategia
- Guías de usuario
- Ejemplos de configuración

## 7. Conclusiones

La propuesta de módulos de carga dinámica es técnicamente sólida y ofrece grandes ventajas de mantenibilidad y extensibilidad. La cuarta estrategia "Por Dificultad" es simple de implementar y no requiere cambios en el código existente.

La mejora de la repetición espaciada con algoritmos científicos sería un gran valor añadido, aunque requiere más desarrollo.

**Recomendación**: Implementar primero la estrategia "Por Dificultad" como prueba de concepto, luego proceder con la refactorización a módulos. 