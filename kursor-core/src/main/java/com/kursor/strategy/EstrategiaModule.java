package com.kursor.strategy;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import java.util.List;

/**
 * Interfaz que define un módulo de estrategia de aprendizaje.
 * 
 * <p>Un módulo de estrategia es responsable de crear instancias de una estrategia
 * específica de aprendizaje. Cada módulo debe implementar esta interfaz para
 * ser reconocido por el sistema de carga dinámica de estrategias.</p>
 * 
 * <p>Los módulos de estrategia se cargan automáticamente usando el patrón
 * Service Provider Interface (SPI) de Java.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see Pregunta
 */
public interface EstrategiaModule {
    
    /**
     * Obtiene el nombre único de la estrategia.
     * 
     * <p>Este nombre se utiliza para identificar la estrategia en el sistema
     * y debe ser único entre todas las estrategias disponibles.</p>
     * 
     * @return El nombre único de la estrategia
     */
    String getNombre();
    
    /**
     * Crea una nueva instancia de la estrategia de aprendizaje.
     * 
     * <p>Este método debe crear y retornar una nueva instancia de la estrategia
     * específica con las preguntas proporcionadas.</p>
     * 
     * @param preguntas La lista de preguntas para la estrategia
     * @return Una nueva instancia de la estrategia de aprendizaje
     */
    EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas);
    
    /**
     * Obtiene la descripción de la estrategia.
     * 
     * <p>Esta descripción se utiliza para mostrar información sobre la estrategia
     * al usuario.</p>
     * 
     * @return La descripción de la estrategia
     */
    String getDescripcion();
    
    /**
     * Obtiene la versión del módulo de estrategia.
     * 
     * <p>Esta versión se utiliza para control de versiones y compatibilidad.</p>
     * 
     * @return La versión del módulo
     */
    String getVersion();
} 