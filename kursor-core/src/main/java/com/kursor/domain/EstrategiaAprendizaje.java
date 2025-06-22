package com.kursor.domain;

import java.io.Serializable;
import com.kursor.domain.Pregunta;

/**
 * Interfaz que define una estrategia de aprendizaje para seleccionar la siguiente pregunta.
 * 
 * <p>Esta interfaz establece el contrato para implementar diferentes estrategias
 * de aprendizaje que determinan el orden y la frecuencia con que se presentan
 * las preguntas al usuario. Cada estrategia puede implementar algoritmos específicos
 * para optimizar el proceso de aprendizaje.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Selección inteligente:</strong> Determina la siguiente pregunta a mostrar</li>
 *   <li><strong>Gestión de estado:</strong> Permite guardar y restaurar el estado de la estrategia</li>
 *   <li><strong>Serializable:</strong> Permite persistir el estado de la estrategia</li>
 *   <li><strong>Extensible:</strong> Permite implementar nuevas estrategias</li>
 * </ul>
 * 
 * <p>Estrategias implementadas:</p>
 * <ul>
 *   <li><strong>Secuencial:</strong> Preguntas en orden secuencial</li>
 *   <li><strong>Aleatoria:</strong> Preguntas en orden aleatorio</li>
 *   <li><strong>Repetición espaciada:</strong> Basada en el algoritmo de repetición espaciada</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * EstrategiaAprendizaje estrategia = new EstrategiaSecuencial(preguntas);
 * estrategia.guardarEstado();
 * Pregunta siguiente = estrategia.siguientePregunta();
 * estrategia.restaurarEstado();
 * }</pre>
 * 
 * <p>Flujo típico de una estrategia:</p>
 * <ol>
 *   <li>Inicializar la estrategia con las preguntas disponibles</li>
 *   <li>Guardar el estado inicial</li>
 *   <li>Obtener la siguiente pregunta según el algoritmo</li>
 *   <li>Actualizar el estado basado en la respuesta del usuario</li>
 *   <li>Repetir hasta completar el aprendizaje</li>
 * </ol>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaSecuencial
 * @see EstrategiaAleatoria
 * @see EstrategiaRepeticionEspaciada
 * @see Pregunta
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
     * Guarda el estado actual de la estrategia.
     * 
     * <p>Este método debe persistir el estado interno de la estrategia para
     * permitir su restauración posterior. El estado puede incluir información
     * como el progreso actual, preguntas ya vistas, puntuaciones, etc.</p>
     * 
     * <p>Implementaciones típicas:</p>
     * <ul>
     *   <li>Guardar en archivo local</li>
     *   <li>Persistir en base de datos</li>
     *   <li>Almacenar en memoria con backup</li>
     * </ul>
     */
    void guardarEstado();
    
    /**
     * Restaura el estado previamente guardado de la estrategia.
     * 
     * <p>Este método debe recuperar el estado guardado por {@link #guardarEstado()}
     * y restaurar la estrategia a ese punto. Esto permite continuar el aprendizaje
     * desde donde se dejó.</p>
     * 
     * <p>Consideraciones:</p>
     * <ul>
     *   <li>Debe manejar casos donde no existe estado previo</li>
     *   <li>Debe validar la integridad del estado restaurado</li>
     *   <li>Debe ser compatible con versiones anteriores</li>
     * </ul>
     */
    void restaurarEstado();
    
    /**
     * Obtiene la siguiente pregunta según la estrategia de aprendizaje.
     * 
     * <p>Este es el método principal de la estrategia que determina cuál es la
     * siguiente pregunta que debe presentarse al usuario. La implementación
     * debe considerar el algoritmo específico de la estrategia y el estado
     * actual del aprendizaje.</p>
     * 
     * <p>Consideraciones de implementación:</p>
     * <ul>
     *   <li>Debe retornar null cuando no hay más preguntas disponibles</li>
     *   <li>Debe considerar el historial de respuestas del usuario</li>
     *   <li>Debe optimizar para el aprendizaje efectivo</li>
     *   <li>Debe ser thread-safe si se usa en entornos concurrentes</li>
     * </ul>
     * 
     * @return La siguiente pregunta a mostrar, o null si no hay más preguntas
     */
    Pregunta siguientePregunta();
} 