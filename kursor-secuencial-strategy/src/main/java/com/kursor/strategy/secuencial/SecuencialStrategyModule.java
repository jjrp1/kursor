package com.kursor.strategy.secuencial;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Módulo para la estrategia secuencial.
 * 
 * <p>Esta estrategia presenta las preguntas en orden secuencial, ideal para
 * aprendizaje estructurado y progresivo sin adaptación al rendimiento del usuario.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Orden fijo:</strong> Las preguntas se presentan siempre en el mismo orden</li>
 *   <li><strong>Simplicidad:</strong> Comportamiento predecible y fácil de entender</li>
 *   <li><strong>Progreso lineal:</strong> Avance secuencial sin adaptación</li>
 *   <li><strong>Ideal para principiantes:</strong> No requiere conocimiento previo del material</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * SecuencialStrategyModule modulo = new SecuencialStrategyModule();
 * EstrategiaAprendizaje estrategia = modulo.crearEstrategia(listaPreguntas);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class SecuencialStrategyModule implements EstrategiaModule {
    
    private static final Logger logger = LoggerFactory.getLogger(SecuencialStrategyModule.class);
    
    @Override
    public String getNombre() {
        logger.debug("[SecuencialStrategyModule] getNombre llamado");
        return "Secuencial";
    }

    @Override
    public String getDescripcion() {
        logger.debug("[SecuencialStrategyModule] getDescripcion llamado");
        return "Preguntas en orden secuencial";
    }

    @Override
    public String getIcon() {
        logger.debug("[SecuencialStrategyModule] getIcon llamado");
        return "🔢";
    }

    @Override
    public String getColorTema() {
        logger.debug("[SecuencialStrategyModule] getColorTema llamado");
        return "#3498db";
    }

    @Override
    public String getInformacionUso() {
        logger.debug("[SecuencialStrategyModule] getInformacionUso llamado");
        return "Ideal para aprendizaje estructurado y progresivo. " +
               "Perfecta cuando quieres seguir un orden lógico de conceptos " +
               "o cuando estás aprendiendo material nuevo por primera vez.";
    }

    @Override
    public EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas) {
        logger.debug("[SecuencialStrategyModule] crearEstrategia llamado con {} preguntas", 
                    preguntas != null ? preguntas.size() : 0);
        
        if (preguntas == null || preguntas.isEmpty()) {
            logger.warn("[SecuencialStrategyModule] Lista de preguntas nula o vacía");
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        
        SecuencialStrategy estrategia = new SecuencialStrategy(preguntas);
        logger.debug("[SecuencialStrategyModule] Estrategia creada exitosamente");
        return estrategia;
    }

    @Override
    public String getVersion() {
        logger.debug("[SecuencialStrategyModule] getVersion llamado");
        return "2.0.0";
    }
} 