package com.kursor.strategy.secuencial;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Estrategia de aprendizaje secuencial: presenta las preguntas en orden.
 * 
 * <p>Esta estrategia presenta las preguntas en el orden exacto en que están
 * definidas en el curso, sin modificar el orden ni adaptarse al rendimiento
 * del usuario.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Orden fijo:</strong> Las preguntas se presentan siempre en el mismo orden</li>
 *   <li><strong>Simplicidad:</strong> Comportamiento predecible y fácil de entender</li>
 *   <li><strong>Progreso lineal:</strong> Avance secuencial sin adaptación</li>
 *   <li><strong>Ideal para principiantes:</strong> No requiere conocimiento previo del material</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * SecuencialStrategy estrategia = new SecuencialStrategy(listaPreguntas);
 * Pregunta primera = estrategia.primeraPregunta();
 * while (estrategia.hayMasPreguntas()) {
 *     Respuesta respuesta = ...;
 *     estrategia.registrarRespuesta(respuesta);
 *     Pregunta siguiente = estrategia.siguientePregunta();
 * }
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class SecuencialStrategy implements EstrategiaAprendizaje {
    
    private static final Logger logger = LoggerFactory.getLogger(SecuencialStrategy.class);
    
    /** Lista de preguntas en orden secuencial */
    private final List<Pregunta> preguntas;
    
    /** Índice actual en la lista de preguntas */
    private int indiceActual;

    /**
     * Constructor de la estrategia secuencial.
     * 
     * <p>Inicializa la estrategia con la lista de preguntas proporcionada,
     * manteniendo el orden original de las preguntas.</p>
     * 
     * @param preguntas Lista de preguntas en orden secuencial
     * @throws IllegalArgumentException Si la lista es nula o vacía
     */
    public SecuencialStrategy(List<Pregunta> preguntas) {
        logger.debug("[SecuencialStrategy] Inicializando con {} preguntas", 
                    preguntas != null ? preguntas.size() : 0);
        
        if (preguntas == null || preguntas.isEmpty()) {
            logger.error("[SecuencialStrategy] La lista de preguntas no puede ser nula ni vacía");
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        
        this.preguntas = preguntas;
        this.indiceActual = 0;
        logger.debug("[SecuencialStrategy] Estrategia inicializada correctamente");
    }

    @Override
    public String getNombre() {
        logger.debug("[SecuencialStrategy] getNombre llamado");
        return "Secuencial";
    }

    @Override
    public Pregunta primeraPregunta() {
        logger.debug("[SecuencialStrategy] primeraPregunta llamado. Reiniciando índice a 0");
        indiceActual = 0;
        Pregunta p = preguntas.get(indiceActual);
        logger.debug("[SecuencialStrategy] Primera pregunta: {}", p.getId());
        return p;
    }

    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        logger.debug("[SecuencialStrategy] registrarRespuesta llamado. Respuesta: {}", respuesta);
        // No modifica el orden ni el estado, solo avanza
        // Se puede extender para registrar estadísticas
        logger.debug("[SecuencialStrategy] Respuesta registrada (no afecta el orden secuencial)");
    }

    @Override
    public boolean hayMasPreguntas() {
        boolean hayMas = indiceActual < preguntas.size() - 1;
        logger.debug("[SecuencialStrategy] hayMasPreguntas llamado. Hay más: {} (índice: {}, total: {})", 
                    hayMas, indiceActual, preguntas.size());
        return hayMas;
    }

    @Override
    public Pregunta siguientePregunta() {
        logger.debug("[SecuencialStrategy] siguientePregunta llamado. Índice actual: {}", indiceActual);
        
        if (hayMasPreguntas()) {
            indiceActual++;
            Pregunta p = preguntas.get(indiceActual);
            logger.debug("[SecuencialStrategy] Siguiente pregunta: {} (índice: {})", p.getId(), indiceActual);
            return p;
        }
        
        logger.debug("[SecuencialStrategy] No hay más preguntas");
        return null;
    }

    @Override
    public double getProgreso() {
        double progreso = (indiceActual + 1.0) / preguntas.size();
        logger.debug("[SecuencialStrategy] getProgreso llamado. Progreso: {} ({}/{})", 
                    progreso, indiceActual + 1, preguntas.size());
        return progreso;
    }

    @Override
    public String serializarEstado() {
        String estado = String.valueOf(indiceActual);
        logger.debug("[SecuencialStrategy] serializarEstado llamado. Estado: {}", estado);
        return estado;
    }

    @Override
    public void deserializarEstado(String estado) {
        logger.debug("[SecuencialStrategy] deserializarEstado llamado. Estado recibido: {}", estado);
        
        try {
            this.indiceActual = Integer.parseInt(estado);
            logger.debug("[SecuencialStrategy] Estado deserializado correctamente. Nuevo índice: {}", indiceActual);
        } catch (NumberFormatException e) {
            logger.error("[SecuencialStrategy] Error al deserializar estado: {}. Reiniciando índice a 0", estado);
            this.indiceActual = 0;
        }
    }

    // Métodos auxiliares para pruebas y persistencia
    
    /**
     * Obtiene el índice actual en la lista de preguntas.
     * 
     * @return Índice actual
     */
    public int getIndiceActual() {
        logger.debug("[SecuencialStrategy] getIndiceActual llamado: {}", indiceActual);
        return indiceActual;
    }
    
    /**
     * Obtiene el total de preguntas en la estrategia.
     * 
     * @return Total de preguntas
     */
    public int getTotalPreguntas() {
        logger.debug("[SecuencialStrategy] getTotalPreguntas llamado: {}", preguntas.size());
        return preguntas.size();
    }
} 