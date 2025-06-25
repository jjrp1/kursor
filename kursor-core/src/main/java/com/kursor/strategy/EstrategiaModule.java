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
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Información de Presentación:</strong> Cada módulo define su propio
 *       icono, color temático e información de uso para la interfaz de usuario</li>
 *   <li><strong>Autocontenido:</strong> Cada módulo proporciona toda la información
 *       necesaria para su representación visual</li>
 *   <li><strong>Extensible:</strong> Fácil agregar nuevas estrategias sin modificar
 *       el código principal</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
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
     * Obtiene la descripción corta de la estrategia.
     * 
     * <p>Esta descripción se utiliza para mostrar información básica sobre la estrategia
     * al usuario en la interfaz de selección.</p>
     * 
     * @return La descripción corta de la estrategia
     */
    String getDescripcion();
    
    /**
     * Obtiene el icono de la estrategia.
     * 
     * <p>El icono debe ser un emoji o símbolo Unicode que represente visualmente
     * la estrategia. Se utiliza en la interfaz de usuario para identificar
     * rápidamente cada estrategia.</p>
     * 
     * @return El icono de la estrategia (emoji o símbolo Unicode)
     */
    String getIcon();
    
    /**
     * Obtiene el color temático de la estrategia.
     * 
     * <p>Este color se utiliza para personalizar la apariencia visual de la
     * tarjeta de la estrategia en la interfaz de usuario. Debe ser un color
     * CSS válido (hex, rgb, nombre, etc.).</p>
     * 
     * @return El color temático de la estrategia en formato CSS
     */
    String getColorTema();
    
    /**
     * Obtiene la información detallada de uso de la estrategia.
     * 
     * <p>Esta información describe cuándo y cómo usar la estrategia de manera
     * efectiva, incluyendo casos de uso específicos y recomendaciones.</p>
     * 
     * @return La información detallada de uso de la estrategia
     */
    String getInformacionUso();
    
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
     * Obtiene la versión del módulo de estrategia.
     * 
     * <p>Esta versión se utiliza para control de versiones y compatibilidad.</p>
     * 
     * @return La versión del módulo
     */
    String getVersion();
} 