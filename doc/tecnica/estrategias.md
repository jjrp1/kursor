---
title: Sistema de Estrategias de Aprendizaje
subtitle: Documentación técnica completa del sistema de estrategias
description: Análisis, diseño, implementación y modularización del sistema de estrategias de aprendizaje en Kursor
keywords: estrategias, aprendizaje, módulos, iteradores, modularización, ServiceLoader
status: implementado
created: 2025-05-01
modified: 2025-06-20
author: "Juanjo Ruiz"
---

# Sistema de Estrategias de Aprendizaje

## Resumen Ejecutivo

El sistema de estrategias de aprendizaje en Kursor está completamente implementado y modularizado. Las estrategias funcionan como **iteradores especializados** que reciben preguntas de un bloque específico, las reordenan según su algoritmo, y las facilitan una a una bajo demanda, manteniendo estado interno y reaccionando a las respuestas del usuario.

**Estado Actual**: ✅ **COMPLETAMENTE IMPLEMENTADO**
- 4 estrategias implementadas como módulos independientes
- Sistema de carga dinámica mediante ServiceLoader
- Arquitectura modular consistente
- Documentación completa y pruebas unitarias

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

## 2. Arquitectura Modular Implementada

### 2.1 Estructura de Módulos ✅ COMPLETADO

```
kursor-secuencial-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/secuencial/
│   ├── SecuencialStrategy.java
│   └── SecuencialStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-aleatoria-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/aleatoria/
│   ├── AleatoriaStrategy.java
│   └── AleatoriaStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-repeticion-espaciada-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/repeticionespaciada/
│   ├── RepeticionEspaciadaStrategy.java
│   └── RepeticionEspaciadaStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-repetir-incorrectas-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/repetirincorrectas/
│   ├── RepetirIncorrectasStrategy.java
│   └── RepetirIncorrectasStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule
```

### 2.2 Distribución Final ✅ IMPLEMENTADO

```
kursor-portable/
├── strategies/          # Estrategias de aprendizaje
│   ├── kursor-secuencial-strategy.jar
│   ├── kursor-aleatoria-strategy.jar
│   ├── kursor-repeticion-espaciada-strategy.jar
│   └── kursor-repetir-incorrectas-strategy.jar
├── modules/            # Tipos de preguntas
├── kursor-core.jar     # Núcleo del sistema
├── kursor.db          # Base de datos SQLite
└── [configuración]
```

## 3. Interfaces del Sistema

### 3.1 Interfaz EstrategiaAprendizaje

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

### 3.2 Interfaz EstrategiaModule

```java
package com.kursor.strategy;

import com.kursor.domain.EstrategiaAprendizaje;
import java.util.List;

public interface EstrategiaModule {
    /**
     * Obtiene el nombre de la estrategia
     */
    String getNombre();
    
    /**
     * Crea una nueva instancia de la estrategia
     */
    EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas);
    
    /**
     * Obtiene la descripción de la estrategia
     */
    String getDescripcion();
    
    /**
     * Obtiene la versión del módulo
     */
    String getVersion();
}
```

### 3.3 Flujo de Uso Típico

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

## 4. Estrategias Implementadas

### 4.1 Estrategia Secuencial

**Descripción:** Presenta las preguntas en el orden original del bloque.

**Características:**
- **Reordenación**: Ninguna (mantiene orden original)
- **Estado**: Índice actual y pregunta actual
- **Reactividad**: No reacciona a respuestas
- **Complejidad**: Muy baja

**Ubicación:** `kursor-secuencial-strategy/src/main/java/com/kursor/strategy/secuencial/SecuencialStrategy.java`

**Métodos auxiliares:**
- `getIndiceActual()`: Obtiene el índice actual
- `getTotalPreguntas()`: Obtiene el total de preguntas

### 4.2 Estrategia Aleatoria

**Descripción:** Presenta las preguntas en orden completamente aleatorio.

**Características:**
- **Reordenación**: Aleatoria, sin repetición
- **Estado**: Lista de preguntas disponibles y pregunta actual
- **Reactividad**: No reacciona a respuestas
- **Complejidad**: Baja

**Ubicación:** `kursor-aleatoria-strategy/src/main/java/com/kursor/strategy/aleatoria/AleatoriaStrategy.java`

**Métodos auxiliares:**
- `getPreguntasProcesadas()`: Obtiene cantidad de preguntas procesadas
- `getTotalPreguntas()`: Obtiene el total de preguntas

### 4.3 Estrategia de Repetición Espaciada

**Descripción:** Implementa el algoritmo **SuperMemo 2** para optimizar la retención a largo plazo mediante intervalos crecientes adaptativos.

**Características:**
- **Algoritmo**: SuperMemo 2 completo con factor de facilidad (EF)
- **Reordenación**: Programación inteligente basada en prioridad y tiempo
- **Estado**: Factor de facilidad, intervalos, repeticiones, calidad de respuestas
- **Reactividad**: Ajusta intervalos y factores según calidad de respuestas (0-5)
- **Complejidad**: Alta (algoritmo científico)

**Ubicación:** `kursor-repeticion-espaciada-strategy/src/main/java/com/kursor/strategy/repeticionespaciada/RepeticionEspaciadaStrategy.java`

**Algoritmo SuperMemo 2:**
- **Factor de Facilidad (EF)**: Se ajusta según la calidad de las respuestas
- **Intervalos Crecientes**: Las preguntas fáciles se repiten con mayor espaciado
- **Repetición Adaptativa**: Las preguntas difíciles se repiten más frecuentemente
- **Calidad de Respuesta**: 0-5 donde 5 es "perfecto" y 0 es "completamente olvidado"

**Fórmulas del Algoritmo:**
```java
// Actualización del factor de facilidad (EF)
double newEF = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
EF = Math.max(1.3, newEF)

// Cálculo de intervalos
if (repeticiones == 1) intervalo = 1 día
else if (repeticiones == 2) intervalo = 6 días
else intervalo = Math.round(intervalo * EF)
```

**Métodos auxiliares:**
- `getPreguntasProcesadas()`: Obtiene cantidad de preguntas procesadas en la sesión
- `getTotalPreguntasSesion()`: Obtiene el total de preguntas en la sesión actual
- `getTotalPreguntas()`: Obtiene el total de preguntas en la estrategia
- `getPreguntasProgramadas()`: Obtiene el número de preguntas programadas para repetición
- `getEstadoPregunta(String)`: Obtiene el estado de una pregunta específica
- `getFactorFacilidadPromedio()`: Obtiene el factor de facilidad promedio de todas las preguntas

**Clases Internas:**
- `PreguntaProgramada`: Representa una pregunta programada para repetición
- `EstadoPregunta`: Mantiene el estado de repetición espaciada de una pregunta

**Características Avanzadas:**
- **Cola de Prioridad**: Las preguntas se programan según urgencia y tiempo
- **Serialización Completa**: Estado persistente entre sesiones
- **Calidad Adaptativa**: Convierte respuestas a calidad 0-5 basada en tiempo y corrección
- **Priorización Inteligente**: Preguntas incorrectas tienen mayor prioridad

### 4.4 Estrategia de Repetir Incorrectas

**Descripción:** Repite automáticamente las preguntas que respondiste incorrectamente.

**Características:**
- **Reordenación**: Dos fases (originales + incorrectas)
- **Estado**: Índice actual, cola de incorrectas, fase actual
- **Reactividad**: Registra incorrectas para repetición
- **Complejidad**: Media

**Ubicación:** `kursor-repetir-incorrectas-strategy/src/main/java/com/kursor/strategy/repetirincorrectas/RepetirIncorrectasStrategy.java`

**Métodos auxiliares:**
- `getIndiceActual()`: Obtiene el índice actual
- `estaEnFaseRepeticion()`: Verifica si está en fase de repetición
- `getCantidadIncorrectas()`: Obtiene cantidad de preguntas incorrectas
- `getCantidadOriginales()`: Obtiene cantidad de preguntas originales

## 5. Configuración Maven ✅ IMPLEMENTADA

### 5.1 pom.xml de cada módulo de estrategia

Cada módulo incluye:
- **Dependencias del core**: Referencia a `kursor-core` con scope `provided`
- **Jackson**: Para serialización JSON del estado
- **Logging**: SLF4J para logging
- **Testing**: JUnit 5 para pruebas
- **Configuración Maven**: Compilador Java 17, plugins de JAR y recursos

### 5.2 Ejemplo de configuración:

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.kursor</groupId>
    <artifactId>kursor-secuencial-strategy</artifactId>
    <version>1.0.0</version>
    
    <dependencies>
        <dependency>
            <groupId>com.kursor</groupId>
            <artifactId>kursor-core</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Implementation-Title>Secuencial Strategy</Implementation-Title>
                            <Implementation-Version>1.0.0</Implementation-Version>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 6. Estado de Implementación

### ✅ Completado
- **Interfaz EstrategiaAprendizaje**: Implementada y estable
- **Estrategia Secuencial**: ✅ **Completamente implementada** como módulo independiente
- **Estrategia Aleatoria**: ✅ **Completamente implementada** como módulo independiente
- **Estrategia de Repetición Espaciada**: ✅ **Completamente implementada** con algoritmo SuperMemo 2
- **Estrategia de Repetir Incorrectas**: ✅ **Completamente implementada** como módulo independiente
- **Sistema de módulos**: Carga dinámica mediante ServiceLoader
- **Testing**: Pruebas unitarias completas para todas las estrategias
- **Configuración Maven**: Todos los módulos configurados correctamente
- **Documentación**: Completa y actualizada

### 🎯 Resumen de Implementaciones

| Estrategia | Estado | Algoritmo | Complejidad |
|------------|--------|-----------|-------------|
| **Secuencial** | ✅ Completa | Orden original | Muy baja |
| **Aleatoria** | ✅ Completa | Barajado único | Baja |
| **Repetición Espaciada** | ✅ Completa | SuperMemo 2 | Alta |
| **Repetir Incorrectas** | ✅ Completa | Dos fases | Media |

### 🔧 Mejoras Futuras
- **Optimización de rendimiento**: Mejoras en algoritmos de estrategias
- **Nuevas estrategias**: Implementación de estrategias más avanzadas
- **Configuración avanzada**: Parámetros configurables por estrategia
- **Analytics**: Métricas detalladas de rendimiento por estrategia

## 7. Ventajas de la Arquitectura Modular

### Técnicas
- **Separación de responsabilidades**: Cada estrategia es independiente ✅
- **Carga dinámica**: Estrategias se cargan solo cuando se necesitan ✅
- **Escalabilidad**: Fácil agregar nuevas estrategias ✅
- **Testing**: Pruebas unitarias independientes por estrategia ✅

### Arquitectónicas
- **Consistencia**: Mismo patrón que módulos de preguntas ✅
- **Flexibilidad**: Estrategias pueden tener dependencias específicas ✅
- **Mantenibilidad**: Código más organizado y fácil de mantener ✅
- **Reutilización**: Estrategias pueden usarse en otros proyectos ✅

### Operativas
- **Distribución**: JARs independientes en /strategies/ ✅
- **Actualización**: Estrategias se pueden actualizar independientemente ✅
- **Configuración**: Cada estrategia puede tener su propia configuración ✅

## 8. Consideraciones Técnicas

### 8.1 Clase Respuesta

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

### 8.2 Compatibilidad
- Mantener compatibilidad con estrategias existentes
- No romper la interfaz actual
- Migración gradual

### 8.3 Testing
- Tests unitarios para cada estrategia
- Tests de integración para el flujo completo
- Tests de persistencia de estado

### 8.4 Persistencia
- El `EstrategiaStateManager` debe ser compatible con la nueva estructura
- Los datos JSON de estado deben mantener compatibilidad
- Las entidades JPA no cambian

### 8.5 API Pública
- La interfaz `EstrategiaAprendizaje` se mantiene igual
- Los métodos públicos no cambian
- Compatibilidad hacia atrás garantizada

## 9. Conclusiones

1. **Las estrategias son iteradores especializados** que trabajan a nivel de bloque ✅ **IMPLEMENTADO**
2. **La interfaz mejorada** incluye `primeraPregunta()`, `registrarRespuesta()` sin parámetro pregunta, y control de flujo ✅ **IMPLEMENTADO**
3. **Las 4 estrategias están completamente implementadas** como módulos independientes con carga dinámica ✅ **IMPLEMENTADO**
4. **El sistema modular** permite extensibilidad sin modificar el código principal ✅ **IMPLEMENTADO**
5. **La estrategia de repetición espaciada** implementa el algoritmo científico SuperMemo 2 completo ✅ **IMPLEMENTADO**

**Estado Actual**: ✅ **TODAS LAS ESTRATEGIAS COMPLETAMENTE IMPLEMENTADAS**

- **Secuencial**: Presenta preguntas en orden original
- **Aleatoria**: Baraja la lista una vez y presenta en orden aleatorio
- **Repetición Espaciada**: Algoritmo SuperMemo 2 completo con factor de facilidad adaptativo
- **Repetir Incorrectas**: Dos fases bien implementadas (originales + incorrectas)

El sistema es modular, extensible, bien documentado y todas las estrategias funcionan correctamente.

**Próximos Pasos**: 
- Optimización de rendimiento
- Nuevas estrategias avanzadas
- Mejoras en la configuración
- Analytics detallados

---

**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Fecha:** 2025-06-20  
**Versión:** 2.0.0  
**Estado:** ✅ **Implementación Completada - Todas las estrategias funcionales** 