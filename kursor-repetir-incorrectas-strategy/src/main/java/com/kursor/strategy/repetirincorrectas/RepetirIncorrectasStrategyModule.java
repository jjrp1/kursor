package com.kursor.strategy.repetirincorrectas;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo de estrategia para la estrategia de repetir incorrectas.
 */
public class RepetirIncorrectasStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Repetir Incorrectas";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new RepetirIncorrectasStrategy(preguntas);
    }

    @Override
    public String getDescripcion() {
        return "Repite las preguntas incorrectas al final de la sesión para reforzar el aprendizaje.";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
} 