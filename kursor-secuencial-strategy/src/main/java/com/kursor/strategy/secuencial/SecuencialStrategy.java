package com.kursor.strategy.secuencial;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import java.util.List;

/**
 * Estrategia de aprendizaje secuencial: presenta las preguntas en orden.
 */
public class SecuencialStrategy implements EstrategiaAprendizaje {
    private final List<Pregunta> preguntas;
    private int indiceActual;

    public SecuencialStrategy(List<Pregunta> preguntas) {
        if (preguntas == null || preguntas.isEmpty()) {
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        this.preguntas = preguntas;
        this.indiceActual = 0;
    }

    @Override
    public String getNombre() {
        return "Secuencial";
    }

    @Override
    public Pregunta primeraPregunta() {
        indiceActual = 0;
        return preguntas.get(indiceActual);
    }

    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        // No modifica el orden ni el estado, solo avanza
        // Se puede extender para registrar estadísticas
    }

    @Override
    public boolean hayMasPreguntas() {
        return indiceActual < preguntas.size() - 1;
    }

    @Override
    public Pregunta siguientePregunta() {
        if (hayMasPreguntas()) {
            indiceActual++;
            return preguntas.get(indiceActual);
        }
        return null;
    }

    @Override
    public double getProgreso() {
        return (indiceActual + 1.0) / preguntas.size();
    }

    @Override
    public String serializarEstado() {
        return String.valueOf(indiceActual);
    }

    @Override
    public void deserializarEstado(String estado) {
        try {
            this.indiceActual = Integer.parseInt(estado);
        } catch (NumberFormatException e) {
            this.indiceActual = 0;
        }
    }

    // Métodos auxiliares para pruebas y persistencia
    public int getIndiceActual() {
        return indiceActual;
    }
    public int getTotalPreguntas() {
        return preguntas.size();
    }
} 