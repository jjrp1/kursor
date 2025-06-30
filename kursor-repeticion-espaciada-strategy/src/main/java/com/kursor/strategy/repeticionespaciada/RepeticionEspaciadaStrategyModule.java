package com.kursor.strategy.repeticionespaciada;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Módulo para la estrategia de repetición espaciada.
 * 
 * <p>Esta estrategia optimiza la retención a largo plazo mediante intervalos
 * crecientes entre repeticiones de preguntas, implementando el algoritmo SuperMemo 2.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Algoritmo SuperMemo 2:</strong> Basado en investigación científica</li>
 *   <li><strong>Intervalos adaptativos:</strong> Se ajustan según el rendimiento del usuario</li>
 *   <li><strong>Factor de facilidad:</strong> Optimiza el espaciado entre repeticiones</li>
 *   <li><strong>Retención a largo plazo:</strong> Ideal para memorización efectiva</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * RepeticionEspaciadaStrategyModule modulo = new RepeticionEspaciadaStrategyModule();
 * EstrategiaAprendizaje estrategia = modulo.crearEstrategia(listaPreguntas);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class RepeticionEspaciadaStrategyModule implements EstrategiaModule {
    
    private static final Logger logger = LoggerFactory.getLogger(RepeticionEspaciadaStrategyModule.class);
    
    @Override
    public String getNombre() {
        logger.debug("[RepeticionEspaciadaStrategyModule] getNombre llamado");
        return "Repetición Espaciada";
    }

    @Override
    public String getDescripcion() {
        logger.debug("[RepeticionEspaciadaStrategyModule] getDescripcion llamado");
        return "Optimizada para retención a largo plazo";
    }

    @Override
    public String getIcon() {
        logger.debug("[RepeticionEspaciadaStrategyModule] getIcon llamado");
        return "📅";
    }

    @Override
    public String getColorTema() {
        logger.debug("[RepeticionEspaciadaStrategyModule] getColorTema llamado");
        return "#9b59b6";
    }

    @Override
    public String getInformacionUso() {
        logger.debug("[RepeticionEspaciadaStrategyModule] getInformacionUso llamado");
        return "Ideal para memorización efectiva y retención a largo plazo. " +
               "Perfecta cuando necesitas recordar información por períodos " +
               "extendidos o cuando estás preparando exámenes importantes.";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        logger.debug("[RepeticionEspaciadaStrategyModule] crearEstrategia llamado con {} preguntas", 
                    preguntas != null ? preguntas.size() : 0);
        
        if (preguntas == null || preguntas.isEmpty()) {
            logger.warn("[RepeticionEspaciadaStrategyModule] Lista de preguntas nula o vacía");
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        
        RepeticionEspaciadaStrategy estrategia = new RepeticionEspaciadaStrategy(preguntas);
        logger.debug("[RepeticionEspaciadaStrategyModule] Estrategia creada exitosamente");
        return estrategia;
    }

    @Override
    public String getVersion() {
        logger.debug("[RepeticionEspaciadaStrategyModule] getVersion llamado");
        return "2.0.0";
    }
} 