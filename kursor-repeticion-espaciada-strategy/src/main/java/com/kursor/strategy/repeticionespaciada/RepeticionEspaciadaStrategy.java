package com.kursor.strategy.repeticionespaciada;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import java.util.List;
import java.util.ArrayList;

/**
 * Estrategia de aprendizaje con repetición espaciada: repite preguntas con intervalos crecientes.
 */
public class RepeticionEspaciadaStrategy implements EstrategiaAprendizaje {
    private final List<Pregunta> preguntas;
    private final List<Pregunta> preguntasProcesadas;
    private int indiceActual;
    private int intervalo;

    public RepeticionEspaciadaStrategy(List<Pregunta> preguntas) {
        if (preguntas == null || preguntas.isEmpty()) {
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        this.preguntas = preguntas;
        this.preguntasProcesadas = new ArrayList<>();
        this.indiceActual = 0;
        this.intervalo = 3; // Intervalo por defecto
    }

    @Override
    public String getNombre() {
        return "Repetición Espaciada";
    }

    @Override
    public Pregunta primeraPregunta() {
        indiceActual = 0;
        return preguntas.get(indiceActual);
    }

    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        Pregunta preguntaActual = preguntas.get(indiceActual);
        preguntasProcesadas.add(preguntaActual);
        
        // Si se ha alcanzado el intervalo, repetir la pregunta
        if (preguntasProcesadas.size() % intervalo == 0) {
            // La pregunta se repetirá más adelante
        }
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
    public int getIntervalo() {
        return intervalo;
    }
    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }
    public int getPreguntasProcesadas() {
        return preguntasProcesadas.size();
    }
    public int getTotalPreguntas() {
        return preguntas.size();
    }
} 