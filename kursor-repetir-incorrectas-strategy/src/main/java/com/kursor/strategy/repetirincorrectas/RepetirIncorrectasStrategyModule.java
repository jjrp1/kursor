package com.kursor.strategy.repetirincorrectas;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Módulo de estrategia para la estrategia de repetir incorrectas.
 * 
 * <p>Esta estrategia se enfoca en preguntas falladas anteriormente para
 * mejorar áreas débiles y corregir errores.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class RepetirIncorrectasStrategyModule implements EstrategiaModule {
    @Override
    public String getNombre() {
        return "Repetir Incorrectas";
    }

    @Override
    public String getDescripcion() {
        return "Enfocada en preguntas falladas anteriormente";
    }

    @Override
    public String getIcon() {
        return "🎯";
    }

    @Override
    public String getColorTema() {
        return "#f39c12";
    }

    @Override
    public String getInformacionUso() {
        return "Ideal para mejorar áreas débiles y corregir errores. " +
               "Perfecta cuando quieres enfocarte en los conceptos que más " +
               "te cuestan o cuando estás preparando un examen y necesitas " +
               "reforzar tus puntos débiles.";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        return new RepetirIncorrectasStrategy(preguntas);
    }

    @Override
    public String getVersion() {
        return "2.0.0";
    }
} 