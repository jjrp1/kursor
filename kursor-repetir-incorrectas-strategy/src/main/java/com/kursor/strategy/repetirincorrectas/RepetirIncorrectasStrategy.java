package com.kursor.strategy.repetirincorrectas;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 * Estrategia de aprendizaje que repite las preguntas incorrectas al final.
 * 
 * <p>Esta estrategia funciona en dos fases:</p>
 * <ol>
 *   <li><strong>Fase Original:</strong> Presenta todas las preguntas en orden secuencial</li>
 *   <li><strong>Fase de Repetición:</strong> Repite solo las preguntas que fueron respondidas incorrectamente</li>
 * </ol>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Doble fase:</strong> Primero todas las preguntas, luego solo las incorrectas</li>
 *   <li><strong>Enfoque en errores:</strong> Prioriza el aprendizaje de conceptos difíciles</li>
 *   <li><strong>Repetición inmediata:</strong> Las incorrectas se repiten inmediatamente después</li>
 *   <li><strong>Eficiencia:</strong> No repite preguntas ya dominadas</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * RepetirIncorrectasStrategy estrategia = new RepetirIncorrectasStrategy(listaPreguntas);
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
public class RepetirIncorrectasStrategy implements EstrategiaAprendizaje {
    
    private static final Logger logger = LoggerFactory.getLogger(RepetirIncorrectasStrategy.class);
    
    /** Lista original de preguntas del bloque */
    private final List<Pregunta> preguntasBloque;
    
    /** Cola de preguntas incorrectas para repetir */
    private final Queue<Pregunta> colaIncorrectas;
    
    /** Conjunto de IDs de preguntas marcadas como incorrectas */
    private final Set<String> preguntasIncorrectas;
    
    /** Índice actual en la lista de preguntas originales */
    private int indiceActual;
    
    /** Indica si estamos en la fase de repetición de incorrectas */
    private boolean enFaseRepeticion;
    
    /** Pregunta actualmente siendo presentada */
    private Pregunta preguntaActual;

    /**
     * Constructor de la estrategia de repetir incorrectas.
     * 
     * <p>Inicializa la estrategia con la lista de preguntas proporcionada,
     * preparando las estructuras de datos para el seguimiento de respuestas
     * incorrectas.</p>
     * 
     * @param preguntasBloque Lista de preguntas para la estrategia
     * @throws IllegalArgumentException Si la lista es nula o vacía
     */
    public RepetirIncorrectasStrategy(List<Pregunta> preguntasBloque) {
        logger.debug("[RepetirIncorrectasStrategy] Inicializando con {} preguntas", 
                    preguntasBloque != null ? preguntasBloque.size() : 0);
        
        if (preguntasBloque == null || preguntasBloque.isEmpty()) {
            logger.error("[RepetirIncorrectasStrategy] La lista de preguntas no puede ser nula ni vacía");
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        
        this.preguntasBloque = new ArrayList<>(preguntasBloque);
        this.colaIncorrectas = new LinkedList<>();
        this.preguntasIncorrectas = new HashSet<>();
        this.indiceActual = 0;
        this.enFaseRepeticion = false;
        
        logger.debug("[RepetirIncorrectasStrategy] Estrategia inicializada correctamente");
    }

    @Override
    public String getNombre() {
        logger.debug("[RepetirIncorrectasStrategy] getNombre llamado");
        return "Repetir Incorrectas";
    }

    @Override
    public Pregunta primeraPregunta() {
        logger.debug("[RepetirIncorrectasStrategy] primeraPregunta llamado. Reiniciando estrategia");
        
        indiceActual = 0;
        enFaseRepeticion = false;
        preguntaActual = preguntasBloque.get(indiceActual);
        
        logger.debug("[RepetirIncorrectasStrategy] Primera pregunta: {} (fase original)", preguntaActual.getId());
        return preguntaActual;
    }

    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        logger.debug("[RepetirIncorrectasStrategy] registrarRespuesta llamado. Respuesta: {}, Pregunta: {}", 
                    respuesta, preguntaActual != null ? preguntaActual.getId() : "null");
        
        if (preguntaActual == null) {
            logger.warn("[RepetirIncorrectasStrategy] No hay pregunta actual para registrar respuesta");
            return;
        }
        
        if (!respuesta.esCorrecta() && !preguntasIncorrectas.contains(preguntaActual.getId())) {
            // Es incorrecta y no la hemos marcado antes
            logger.debug("[RepetirIncorrectasStrategy] Pregunta {} marcada como incorrecta y agregada a la cola", 
                        preguntaActual.getId());
            preguntasIncorrectas.add(preguntaActual.getId());
            colaIncorrectas.offer(preguntaActual);
        } else if (respuesta.esCorrecta()) {
            logger.debug("[RepetirIncorrectasStrategy] Pregunta {} respondida correctamente", preguntaActual.getId());
        } else {
            logger.debug("[RepetirIncorrectasStrategy] Pregunta {} ya estaba marcada como incorrecta", preguntaActual.getId());
        }
    }

    @Override
    public boolean hayMasPreguntas() {
        // Hay siguiente si: estamos en preguntas originales O hay incorrectas para repetir
        boolean hayMas = indiceActual < preguntasBloque.size() - 1 || !colaIncorrectas.isEmpty();
        
        logger.debug("[RepetirIncorrectasStrategy] hayMasPreguntas llamado. Hay más: {} (índice: {}, incorrectas: {}, enFaseRepeticion: {})", 
                    hayMas, indiceActual, colaIncorrectas.size(), enFaseRepeticion);
        
        return hayMas;
    }

    @Override
    public Pregunta siguientePregunta() {
        logger.debug("[RepetirIncorrectasStrategy] siguientePregunta llamado. EnFaseRepeticion: {}, Índice: {}, ColaIncorrectas: {}", 
                    enFaseRepeticion, indiceActual, colaIncorrectas.size());
        
        if (!hayMasPreguntas()) {
            logger.debug("[RepetirIncorrectasStrategy] No hay más preguntas");
            return null;
        }

        if (!enFaseRepeticion) {
            // Estamos en la fase original
            indiceActual++;
            if (indiceActual < preguntasBloque.size()) {
                preguntaActual = preguntasBloque.get(indiceActual);
                logger.debug("[RepetirIncorrectasStrategy] Siguiente pregunta original: {} (índice: {})", 
                            preguntaActual.getId(), indiceActual);
                return preguntaActual;
            } else {
                // Terminamos la fase original, pasamos a repetición
                logger.debug("[RepetirIncorrectasStrategy] Fase original completada. Pasando a fase de repetición con {} incorrectas", 
                            colaIncorrectas.size());
                enFaseRepeticion = true;
                return siguientePregunta(); // Llamada recursiva para obtener la primera incorrecta
            }
        } else {
            // Estamos en la fase de repetición
            if (!colaIncorrectas.isEmpty()) {
                preguntaActual = colaIncorrectas.poll();
                logger.debug("[RepetirIncorrectasStrategy] Siguiente pregunta incorrecta: {} (restantes: {})", 
                            preguntaActual.getId(), colaIncorrectas.size());
                return preguntaActual;
            }
        }
        
        logger.debug("[RepetirIncorrectasStrategy] No hay más preguntas disponibles");
        return null;
    }

    @Override
    public double getProgreso() {
        int totalPreguntas = preguntasBloque.size() + colaIncorrectas.size();
        if (totalPreguntas == 0) {
            logger.debug("[RepetirIncorrectasStrategy] getProgreso: no hay preguntas");
            return 0.0;
        }
        
        int preguntasProcesadas = enFaseRepeticion ? 
            preguntasBloque.size() + (preguntasBloque.size() - colaIncorrectas.size()) : 
            indiceActual + 1;
        
        double progreso = (double) preguntasProcesadas / totalPreguntas;
        logger.debug("[RepetirIncorrectasStrategy] getProgreso: {} ({}/{})", progreso, preguntasProcesadas, totalPreguntas);
        return progreso;
    }

    @Override
    public String serializarEstado() {
        String estado = String.valueOf(indiceActual) + "," + enFaseRepeticion;
        logger.debug("[RepetirIncorrectasStrategy] serializarEstado: {}", estado);
        return estado;
    }

    @Override
    public void deserializarEstado(String estado) {
        logger.debug("[RepetirIncorrectasStrategy] deserializarEstado llamado. Estado: {}", estado);
        
        try {
            String[] partes = estado.split(",");
            this.indiceActual = Integer.parseInt(partes[0]);
            this.enFaseRepeticion = Boolean.parseBoolean(partes[1]);
            logger.debug("[RepetirIncorrectasStrategy] Estado deserializado: índice={}, enFaseRepeticion={}", 
                        indiceActual, enFaseRepeticion);
        } catch (Exception e) {
            logger.error("[RepetirIncorrectasStrategy] Error al deserializar estado: {}. Usando valores por defecto", e.getMessage());
            this.indiceActual = 0;
            this.enFaseRepeticion = false;
        }
    }

    // Métodos auxiliares para pruebas y persistencia
    
    /**
     * Obtiene el índice actual en la lista de preguntas originales.
     * 
     * @return Índice actual
     */
    public int getIndiceActual() {
        logger.debug("[RepetirIncorrectasStrategy] getIndiceActual llamado: {}", indiceActual);
        return indiceActual;
    }
    
    /**
     * Verifica si la estrategia está en la fase de repetición.
     * 
     * @return true si está en fase de repetición, false si está en fase original
     */
    public boolean estaEnFaseRepeticion() {
        logger.debug("[RepetirIncorrectasStrategy] estaEnFaseRepeticion llamado: {}", enFaseRepeticion);
        return enFaseRepeticion;
    }
    
    /**
     * Obtiene la cantidad de preguntas incorrectas en la cola de repetición.
     * 
     * @return Número de preguntas incorrectas pendientes
     */
    public int getCantidadIncorrectas() {
        logger.debug("[RepetirIncorrectasStrategy] getCantidadIncorrectas llamado: {}", colaIncorrectas.size());
        return colaIncorrectas.size();
    }
    
    /**
     * Obtiene la cantidad de preguntas originales en el bloque.
     * 
     * @return Número de preguntas originales
     */
    public int getCantidadOriginales() {
        logger.debug("[RepetirIncorrectasStrategy] getCantidadOriginales llamado: {}", preguntasBloque.size());
        return preguntasBloque.size();
    }
} 