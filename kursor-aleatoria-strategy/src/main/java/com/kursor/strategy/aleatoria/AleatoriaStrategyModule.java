package com.kursor.strategy.aleatoria;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo de estrategia para la estrategia aleatoria.
 */
public class AleatoriaStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Aleatoria";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new AleatoriaStrategy(preguntas);
    }

    @Override
    public String getDescripcion() {
        return "Presenta las preguntas en orden aleatorio.";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
} 