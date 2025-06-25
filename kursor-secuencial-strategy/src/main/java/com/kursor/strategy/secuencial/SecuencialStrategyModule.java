package com.kursor.strategy.secuencial;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo para la estrategia secuencial.
 * 
 * <p>Esta estrategia presenta las preguntas en orden secuencial, ideal para
 * aprendizaje estructurado y progresivo.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class SecuencialStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Secuencial";
    }

    @Override
    public String getDescripcion() {
        return "Preguntas en orden secuencial";
    }

    @Override
    public String getIcon() {
        return "📊";
    }

    @Override
    public String getColorTema() {
        return "#3498db";
    }

    @Override
    public String getInformacionUso() {
        return "Ideal para aprendizaje estructurado y progresivo. " +
               "Perfecta cuando quieres seguir un orden lógico de conceptos " +
               "o cuando estás aprendiendo material nuevo por primera vez.";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new SecuencialStrategy(preguntas);
    }

    @Override
    public String getVersion() {
        return "2.0.0";
    }
} 