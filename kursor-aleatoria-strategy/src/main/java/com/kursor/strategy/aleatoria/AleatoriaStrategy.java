package com.kursor.strategy.aleatoria;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Estrategia de aprendizaje aleatoria: presenta las preguntas en orden aleatorio.
 * <p>
 * Esta estrategia baraja las preguntas y las presenta en un orden diferente en cada sesión.
 * Es útil para repaso general y para evitar la memorización del orden de las preguntas.
 * </p>
 * <p>
 * Ejemplo de uso:
 * <pre>{@code
 * AleatoriaStrategy estrategia = new AleatoriaStrategy(listaPreguntas);
 * Pregunta primera = estrategia.primeraPregunta();
 * while (estrategia.hayMasPreguntas()) {
 *     Respuesta respuesta = ...;
 *     estrategia.registrarRespuesta(respuesta);
 *     Pregunta siguiente = estrategia.siguientePregunta();
 * }
 * }</pre>
 * </p>
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class AleatoriaStrategy implements EstrategiaAprendizaje {
    private static final Logger logger = LoggerFactory.getLogger(AleatoriaStrategy.class);
    private final List<Pregunta> preguntas;
    private final List<Pregunta> preguntasAleatorias;
    private int indiceActual;

    /**
     * Constructor de la estrategia aleatoria.
     * @param preguntas Lista de preguntas a barajar
     * @throws IllegalArgumentException si la lista es nula o vacía
     */
    public AleatoriaStrategy(List<Pregunta> preguntas) {
        logger.debug("[AleatoriaStrategy] Inicializando con {} preguntas", preguntas != null ? preguntas.size() : 0);
        if (preguntas == null || preguntas.isEmpty()) {
            logger.error("[AleatoriaStrategy] La lista de preguntas no puede ser nula ni vacía");
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        this.preguntas = preguntas;
        this.preguntasAleatorias = new ArrayList<>(preguntas);
        Collections.shuffle(preguntasAleatorias);
        this.indiceActual = 0;
        logger.debug("[AleatoriaStrategy] Preguntas barajadas: {}", preguntasAleatorias);
    }

    @Override
    public String getNombre() {
        logger.debug("[AleatoriaStrategy] getNombre llamado");
        return "Aleatoria";
    }

    @Override
    public Pregunta primeraPregunta() {
        logger.debug("[AleatoriaStrategy] primeraPregunta llamado. Reiniciando índice a 0");
        indiceActual = 0;
        Pregunta p = preguntasAleatorias.get(indiceActual);
        logger.debug("[AleatoriaStrategy] Primera pregunta: {}", p.getId());
        return p;
    }

    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        logger.debug("[AleatoriaStrategy] registrarRespuesta llamado. Respuesta: {}", respuesta);
        // No modifica el orden aleatorio, solo avanza
        // Se puede extender para registrar estadísticas
    }

    @Override
    public boolean hayMasPreguntas() {
        boolean hayMas = indiceActual < preguntasAleatorias.size() - 1;
        logger.debug("[AleatoriaStrategy] hayMasPreguntas llamado. Indice actual: {}, Hay más: {}", indiceActual, hayMas);
        return hayMas;
    }

    @Override
    public Pregunta siguientePregunta() {
        logger.debug("[AleatoriaStrategy] siguientePregunta llamado. Indice actual antes: {}", indiceActual);
        if (hayMasPreguntas()) {
            indiceActual++;
            Pregunta p = preguntasAleatorias.get(indiceActual);
            logger.debug("[AleatoriaStrategy] Siguiente pregunta: {}", p.getId());
            return p;
        }
        logger.debug("[AleatoriaStrategy] No hay más preguntas");
        return null;
    }

    @Override
    public double getProgreso() {
        double progreso = (indiceActual + 1.0) / preguntasAleatorias.size();
        logger.debug("[AleatoriaStrategy] getProgreso llamado. Progreso: {}", progreso);
        return progreso;
    }

    @Override
    public String serializarEstado() {
        logger.debug("[AleatoriaStrategy] serializarEstado llamado. Indice actual: {}", indiceActual);
        return String.valueOf(indiceActual);
    }

    @Override
    public void deserializarEstado(String estado) {
        logger.debug("[AleatoriaStrategy] deserializarEstado llamado. Estado recibido: {}", estado);
        try {
            this.indiceActual = Integer.parseInt(estado);
            logger.debug("[AleatoriaStrategy] Estado deserializado correctamente. Nuevo índice: {}", indiceActual);
        } catch (NumberFormatException e) {
            logger.error("[AleatoriaStrategy] Error al deserializar estado: {}. Reiniciando índice a 0", estado);
            this.indiceActual = 0;
        }
    }

    // Métodos auxiliares para pruebas y persistencia
    public int getIndiceActual() {
        logger.debug("[AleatoriaStrategy] getIndiceActual llamado: {}", indiceActual);
        return indiceActual;
    }
    public int getTotalPreguntas() {
        logger.debug("[AleatoriaStrategy] getTotalPreguntas llamado: {}", preguntasAleatorias.size());
        return preguntasAleatorias.size();
    }
    public List<Pregunta> getPreguntasAleatorias() {
        logger.debug("[AleatoriaStrategy] getPreguntasAleatorias llamado");
        return new ArrayList<>(preguntasAleatorias);
    }
} 