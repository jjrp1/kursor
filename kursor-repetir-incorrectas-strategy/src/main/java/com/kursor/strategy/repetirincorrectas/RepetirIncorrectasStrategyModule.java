package com.kursor.strategy.repetirincorrectas;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Módulo de estrategia para la estrategia de repetir incorrectas.
 * 
 * <p>Esta estrategia se enfoca en preguntas falladas anteriormente para
 * mejorar áreas débiles y corregir errores mediante un enfoque de dos fases.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Enfoque en errores:</strong> Prioriza el aprendizaje de conceptos difíciles</li>
 *   <li><strong>Doble fase:</strong> Primero todas las preguntas, luego solo las incorrectas</li>
 *   <li><strong>Repetición inmediata:</strong> Las incorrectas se repiten inmediatamente después</li>
 *   <li><strong>Eficiencia:</strong> No repite preguntas ya dominadas</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * RepetirIncorrectasStrategyModule modulo = new RepetirIncorrectasStrategyModule();
 * EstrategiaAprendizaje estrategia = modulo.crearEstrategia(listaPreguntas);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class RepetirIncorrectasStrategyModule implements EstrategiaModule {
    
    private static final Logger logger = LoggerFactory.getLogger(RepetirIncorrectasStrategyModule.class);
    
    @Override
    public String getNombre() {
        logger.debug("[RepetirIncorrectasStrategyModule] getNombre llamado");
        return "Repetir Incorrectas";
    }

    @Override
    public String getDescripcion() {
        logger.debug("[RepetirIncorrectasStrategyModule] getDescripcion llamado");
        return "Enfocada en preguntas falladas anteriormente";
    }

    @Override
    public String getIcon() {
        logger.debug("[RepetirIncorrectasStrategyModule] getIcon llamado");
        return "🎯";
    }

    @Override
    public String getColorTema() {
        logger.debug("[RepetirIncorrectasStrategyModule] getColorTema llamado");
        return "#f39c12";
    }

    @Override
    public String getInformacionUso() {
        logger.debug("[RepetirIncorrectasStrategyModule] getInformacionUso llamado");
        return "Ideal para mejorar áreas débiles y corregir errores. " +
               "Perfecta cuando quieres enfocarte en los conceptos que más " +
               "te cuestan o cuando estás preparando un examen y necesitas " +
               "reforzar tus puntos débiles.";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        logger.debug("[RepetirIncorrectasStrategyModule] crearEstrategia llamado con {} preguntas", 
                    preguntas != null ? preguntas.size() : 0);
        
        if (preguntas == null || preguntas.isEmpty()) {
            logger.warn("[RepetirIncorrectasStrategyModule] Lista de preguntas nula o vacía");
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        
        RepetirIncorrectasStrategy estrategia = new RepetirIncorrectasStrategy(preguntas);
        logger.debug("[RepetirIncorrectasStrategyModule] Estrategia creada exitosamente");
        return estrategia;
    }

    @Override
    public String getVersion() {
        logger.debug("[RepetirIncorrectasStrategyModule] getVersion llamado");
        return "2.0.0";
    }
} 