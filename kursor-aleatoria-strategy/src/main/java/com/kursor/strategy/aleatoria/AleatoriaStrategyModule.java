package com.kursor.strategy.aleatoria;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo de estrategia para la estrategia aleatoria.
 * 
 * <p>Esta estrategia presenta las preguntas en orden aleatorio, ideal para
 * repaso general y evitar memorización de orden.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class AleatoriaStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Aleatoria";
    }

    @Override
    public String getDescripcion() {
        return "Preguntas en orden aleatorio";
    }

    @Override
    public String getIcon() {
        return "🎲";
    }

    @Override
    public String getColorTema() {
        return "#e74c3c";
    }

    @Override
    public String getInformacionUso() {
        return "Ideal para repaso general y evitar memorización de orden. " +
               "Perfecta cuando ya conoces el material y quieres repasar " +
               "de manera más dinámica o cuando quieres evaluar tu conocimiento real.";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new AleatoriaStrategy(preguntas);
    }

    @Override
    public String getVersion() {
        return "2.0.0";
    }
} 