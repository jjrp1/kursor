package com.kursor.strategy.repetirincorrectas;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 * Estrategia de aprendizaje que repite las preguntas incorrectas al final.
 */
public class RepetirIncorrectasStrategy implements EstrategiaAprendizaje {
    private final List<Pregunta> preguntasBloque;
    private final Queue<Pregunta> colaIncorrectas;
    private final Set<String> preguntasIncorrectas;
    private int indiceActual;
    private boolean enFaseRepeticion;
    private Pregunta preguntaActual;

    public RepetirIncorrectasStrategy(List<Pregunta> preguntasBloque) {
        if (preguntasBloque == null || preguntasBloque.isEmpty()) {
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        this.preguntasBloque = new ArrayList<>(preguntasBloque);
        this.colaIncorrectas = new LinkedList<>();
        this.preguntasIncorrectas = new HashSet<>();
        this.indiceActual = 0;
        this.enFaseRepeticion = false;
    }

    @Override
    public String getNombre() {
        return "Repetir Incorrectas";
    }

    @Override
    public Pregunta primeraPregunta() {
        indiceActual = 0;
        enFaseRepeticion = false;
        preguntaActual = preguntasBloque.get(indiceActual);
        return preguntaActual;
    }

    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        if (!respuesta.esCorrecta() && !preguntasIncorrectas.contains(preguntaActual.getId())) {
            // Es incorrecta y no la hemos marcado antes
            preguntasIncorrectas.add(preguntaActual.getId());
            colaIncorrectas.offer(preguntaActual);
        }
    }

    @Override
    public boolean hayMasPreguntas() {
        // Hay siguiente si: estamos en preguntas originales O hay incorrectas para repetir
        return indiceActual < preguntasBloque.size() - 1 || !colaIncorrectas.isEmpty();
    }

    @Override
    public Pregunta siguientePregunta() {
        if (!hayMasPreguntas()) {
            return null;
        }

        if (!enFaseRepeticion) {
            // Estamos en la fase original
            indiceActual++;
            if (indiceActual < preguntasBloque.size()) {
                preguntaActual = preguntasBloque.get(indiceActual);
                return preguntaActual;
            } else {
                // Terminamos la fase original, pasamos a repetición
                enFaseRepeticion = true;
                return siguientePregunta(); // Llamada recursiva para obtener la primera incorrecta
            }
        } else {
            // Estamos en la fase de repetición
            if (!colaIncorrectas.isEmpty()) {
                preguntaActual = colaIncorrectas.poll();
                return preguntaActual;
            }
        }
        return null;
    }

    @Override
    public double getProgreso() {
        int totalPreguntas = preguntasBloque.size() + colaIncorrectas.size();
        if (totalPreguntas == 0) return 0.0;
        
        int preguntasProcesadas = enFaseRepeticion ? 
            preguntasBloque.size() + (preguntasBloque.size() - colaIncorrectas.size()) : 
            indiceActual + 1;
        
        return (double) preguntasProcesadas / totalPreguntas;
    }

    @Override
    public String serializarEstado() {
        return String.valueOf(indiceActual) + "," + enFaseRepeticion;
    }

    @Override
    public void deserializarEstado(String estado) {
        try {
            String[] partes = estado.split(",");
            this.indiceActual = Integer.parseInt(partes[0]);
            this.enFaseRepeticion = Boolean.parseBoolean(partes[1]);
        } catch (Exception e) {
            this.indiceActual = 0;
            this.enFaseRepeticion = false;
        }
    }

    // Métodos auxiliares para pruebas y persistencia
    public int getIndiceActual() {
        return indiceActual;
    }
    public boolean estaEnFaseRepeticion() {
        return enFaseRepeticion;
    }
    public int getCantidadIncorrectas() {
        return colaIncorrectas.size();
    }
    public int getCantidadOriginales() {
        return preguntasBloque.size();
    }
} 