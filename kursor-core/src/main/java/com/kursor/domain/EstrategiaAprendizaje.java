package com.kursor.domain;

import java.io.Serializable;

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
 * <p>Estrategias implementadas (en módulos separados):</p>
 * <ul>
 *   <li><strong>Secuencial:</strong> Preguntas en orden secuencial</li>
 *   <li><strong>Aleatoria:</strong> Preguntas en orden aleatorio</li>
 *   <li><strong>Repetición espaciada:</strong> Basada en el algoritmo de repetición espaciada</li>
 *   <li><strong>Repetir incorrectas:</strong> Repite preguntas falladas al final</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * // Las estrategias se crean a través de módulos específicos
 * EstrategiaAprendizaje estrategia = strategyManager.crearEstrategia("Secuencial", preguntas);
 * Pregunta primera = estrategia.primeraPregunta();
 * 
 * while (estrategia.hayMasPreguntas()) {
 *     // Mostrar pregunta al usuario
 *     Respuesta respuesta = obtenerRespuestaUsuario(); // obtener respuesta del usuario
 *     estrategia.registrarRespuesta(respuesta);
 *     Pregunta siguiente = estrategia.siguientePregunta();
 * }
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see Pregunta
 * @see Respuesta
 * @see Sesion
 */
public interface EstrategiaAprendizaje extends Serializable {
    
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * 
     * <p>Este método debe retornar un nombre único y descriptivo que identifique
     * la estrategia de aprendizaje. El nombre se utiliza para mostrar la estrategia
     * al usuario y para propósitos de logging y debugging.</p>
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
     * <p>Este es el método principal de la estrategia que determina cuál es la
     * siguiente pregunta que debe presentarse al usuario. La implementación
     * debe considerar el algoritmo específico de la estrategia y el estado
     * actual del aprendizaje.</p>
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
     * Obtiene el progreso actual de la estrategia.
     * 
     * <p>Retorna un valor entre 0.0 y 1.0 que representa el progreso
     * de la iteración según la estrategia específica.</p>
     * 
     * @return El progreso como valor entre 0.0 (inicio) y 1.0 (completado)
     */
    double getProgreso();
    
    /**
     * Serializa el estado actual de la estrategia.
     * 
     * <p>Este método debe convertir el estado interno de la estrategia
     * a una representación String que pueda ser almacenada y restaurada
     * posteriormente.</p>
     * 
     * @return String que representa el estado actual de la estrategia
     */
    String serializarEstado();
    
    /**
     * Deserializa y restaura el estado de la estrategia.
     * 
     * <p>Este método debe recuperar el estado serializado por {@link #serializarEstado()}
     * y restaurar la estrategia a ese punto, permitiendo continuar el aprendizaje
     * desde donde se dejó.</p>
     * 
     * @param estado String que representa el estado a restaurar
     */
    void deserializarEstado(String estado);
} 