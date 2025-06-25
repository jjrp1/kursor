package com.kursor.strategy.repeticionespaciada;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo para la estrategia de repetición espaciada.
 * 
 * <p>Esta estrategia optimiza la retención a largo plazo mediante intervalos
 * crecientes entre repeticiones de preguntas.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class RepeticionEspaciadaStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Repetición Espaciada";
    }

    @Override
    public String getDescripcion() {
        return "Optimizada para retención a largo plazo";
    }

    @Override
    public String getIcon() {
        return "📅";
    }

    @Override
    public String getColorTema() {
        return "#9b59b6";
    }

    @Override
    public String getInformacionUso() {
        return "Ideal para memorización efectiva y retención a largo plazo. " +
               "Perfecta cuando necesitas recordar información por períodos " +
               "extendidos o cuando estás preparando exámenes importantes.";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new RepeticionEspaciadaStrategy(preguntas);
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
} 