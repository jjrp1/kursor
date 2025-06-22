package com.kursor.strategy.secuencial;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo de estrategia para la estrategia secuencial.
 */
public class SecuencialStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Secuencial";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new SecuencialStrategy(preguntas);
    }

    @Override
    public String getDescripcion() {
        return "Presenta las preguntas en orden secuencial.";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
} 