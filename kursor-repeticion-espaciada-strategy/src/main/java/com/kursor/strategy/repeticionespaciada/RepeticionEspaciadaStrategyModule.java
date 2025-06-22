package com.kursor.strategy.repeticionespaciada;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo de estrategia para la estrategia de repetición espaciada.
 */
public class RepeticionEspaciadaStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Repetición Espaciada";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new RepeticionEspaciadaStrategy(preguntas);
    }

    @Override
    public String getDescripcion() {
        return "Repite preguntas con intervalos crecientes para mejorar la retención.";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
} 