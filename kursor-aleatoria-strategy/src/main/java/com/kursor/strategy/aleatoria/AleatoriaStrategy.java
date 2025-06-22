package com.kursor.strategy.aleatoria;

import com.cursor.domain.EstrategiaAprendizaje;
import com.cursor.domain.Pregunta;
import com.cursor.domain.Respuesta;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Estrategia de aprendizaje aleatoria: presenta las preguntas en orden aleatorio.
 */
public class AleatoriaStrategy implements EstrategiaAprendizaje {
    private final List<Pregunta> preguntas;
    private final List<Pregunta> preguntasAleatorias;
    private int indiceActual;

    public AleatoriaStrategy(List<Pregunta> preguntas) {
        if (preguntas == null || preguntas.isEmpty()) {
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        this.preguntas = preguntas;
        this.preguntasAleatorias = new ArrayList<>(preguntas);
        Collections.shuffle(preguntasAleatorias);
        this.indiceActual = 0;
    }

    @Override
    public String getNombre() {
        return "Aleatoria";
    }

    @Override
    public Pregunta primeraPregunta() {
        indiceActual = 0;
        return preguntasAleatorias.get(indiceActual);
    }

    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        // No modifica el orden aleatorio, solo avanza
        // Se puede extender para registrar estadísticas
    }

    @Override
    public boolean hayMasPreguntas() {
        return indiceActual < preguntasAleatorias.size() - 1;
    }

    @Override
    public Pregunta siguientePregunta() {
        if (hayMasPreguntas()) {
            indiceActual++;
            return preguntasAleatorias.get(indiceActual);
        }
        return null;
    }

    @Override
    public double getProgreso() {
        return (indiceActual + 1.0) / preguntasAleatorias.size();
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
        return preguntasAleatorias.size();
    }
    public List<Pregunta> getPreguntasAleatorias() {
        return new ArrayList<>(preguntasAleatorias);
    }
} 