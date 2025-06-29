package com.kursor.presentation.controllers;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.strategy.EstrategiaModule;
import com.kursor.shared.util.StrategyManager;
import com.kursor.yaml.dto.CursoDTO;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

/**
 * Controlador para el modal de selección de estrategias de aprendizaje.
 * 
 * <p>Este controlador maneja toda la lógica de negocio relacionada con la selección
 * de estrategias, separando las responsabilidades según el patrón MVC:</p>
 * <ul>
 *   <li><strong>Modelo:</strong> Gestiona los datos de estrategias y la selección</li>
 *   <li><strong>Vista:</strong> StrategySelectorModal (solo presentación)</li>
 *   <li><strong>Controlador:</strong> Esta clase (lógica de negocio)</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaModule
 * @see CursoDTO
 * @see StrategyManager
 */
public class StrategySelectorController {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategySelectorController.class);
    
    /** Gestor de estrategias */
    private final StrategyManager strategyManager;
    
    /** Curso para el cual se selecciona la estrategia */
    private final CursoDTO curso;
    
    /** Estrategia seleccionada por el usuario */
    private String estrategiaSeleccionada;
    
    /** Callback para notificar la selección */
    private Consumer<String> onEstrategiaSeleccionada;
    
    /** Callback para notificar cancelación */
    private Runnable onCancelacion;
    
    /**
     * Constructor del controlador.
     * 
     * @param curso Curso para el cual seleccionar la estrategia
     */
    public StrategySelectorController(CursoDTO curso) {
        this.curso = curso;
        this.strategyManager = StrategyManager.getInstance();
        
        logger.info("StrategySelectorController creado para curso: {}", curso.getTitulo());
    }
    
    /**
     * Carga las estrategias disponibles.
     * 
     * @return Lista de estrategias disponibles
     */
    public List<EstrategiaModule> cargarEstrategias() {
        logger.debug("Cargando estrategias disponibles");
        
        try {
            List<EstrategiaModule> estrategias = strategyManager.getStrategies();
            logger.info("Cargadas {} estrategias disponibles", estrategias.size());
            return estrategias;
        } catch (Exception e) {
            logger.error("Error al cargar estrategias: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Selecciona una estrategia.
     * 
     * @param nombreEstrategia Nombre de la estrategia a seleccionar
     */
    public void seleccionarEstrategia(String nombreEstrategia) {
        logger.debug("Seleccionando estrategia: {}", nombreEstrategia);
        
        if (nombreEstrategia == null || nombreEstrategia.trim().isEmpty()) {
            logger.warn("Intento de seleccionar estrategia con nombre vacío");
            return;
        }
        
        // Validar que la estrategia existe
        boolean estrategiaExiste = strategyManager.getStrategies().stream()
            .anyMatch(estrategia -> estrategia.getNombre().equals(nombreEstrategia));
        
        if (!estrategiaExiste) {
            logger.warn("Estrategia no encontrada: {}", nombreEstrategia);
            return;
        }
        
        this.estrategiaSeleccionada = nombreEstrategia;
        logger.info("Estrategia seleccionada: {}", estrategiaSeleccionada);
        
        // Notificar a la vista
        if (onEstrategiaSeleccionada != null) {
            onEstrategiaSeleccionada.accept(estrategiaSeleccionada);
        }
    }
    
    /**
     * Deselecciona la estrategia actual.
     */
    public void deseleccionarEstrategia() {
        logger.debug("Deseleccionando estrategia actual");
        this.estrategiaSeleccionada = null;
    }
    
    /**
     * Confirma la selección de estrategia.
     * 
     * @return true si la selección es válida, false en caso contrario
     */
    public boolean confirmarSeleccion() {
        logger.debug("Confirmando selección de estrategia");
        
        if (estrategiaSeleccionada == null) {
            logger.warn("No se puede confirmar selección: ninguna estrategia seleccionada");
            return false;
        }
        
        logger.info("Selección confirmada: {}", estrategiaSeleccionada);
        return true;
    }
    
    /**
     * Cancela la selección.
     */
    public void cancelarSeleccion() {
        logger.info("Cancelando selección de estrategia");
        this.estrategiaSeleccionada = null;
        
        if (onCancelacion != null) {
            onCancelacion.run();
        }
    }
    
    /**
     * Obtiene la estrategia seleccionada.
     * 
     * @return Nombre de la estrategia seleccionada o null si no hay selección
     */
    public String getEstrategiaSeleccionada() {
        return estrategiaSeleccionada;
    }
    
    /**
     * Verifica si hay una estrategia seleccionada.
     * 
     * @return true si hay una estrategia seleccionada, false en caso contrario
     */
    public boolean isEstrategiaSeleccionada() {
        return estrategiaSeleccionada != null;
    }
    
    /**
     * Obtiene el curso asociado.
     * 
     * @return Curso para el cual se selecciona la estrategia
     */
    public CursoDTO getCurso() {
        return curso;
    }
    
    /**
     * Configura el callback para notificar selección de estrategia.
     * 
     * @param callback Callback a ejecutar cuando se selecciona una estrategia
     */
    public void setOnEstrategiaSeleccionada(Consumer<String> callback) {
        this.onEstrategiaSeleccionada = callback;
    }
    
    /**
     * Configura el callback para notificar cancelación.
     * 
     * @param callback Callback a ejecutar cuando se cancela la selección
     */
    public void setOnCancelacion(Runnable callback) {
        this.onCancelacion = callback;
    }
    
    /**
     * Valida si una estrategia es válida para el curso.
     * 
     * @param nombreEstrategia Nombre de la estrategia a validar
     * @return true si la estrategia es válida, false en caso contrario
     */
    public boolean validarEstrategia(String nombreEstrategia) {
        if (nombreEstrategia == null || nombreEstrategia.trim().isEmpty()) {
            return false;
        }
        
        return strategyManager.getStrategies().stream()
            .anyMatch(estrategia -> estrategia.getNombre().equals(nombreEstrategia));
    }
    
    /**
     * Obtiene información detallada de una estrategia.
     * 
     * @param nombreEstrategia Nombre de la estrategia
     * @return EstrategiaModule con información detallada o null si no se encuentra
     */
    public EstrategiaModule obtenerEstrategia(String nombreEstrategia) {
        return strategyManager.getStrategies().stream()
            .filter(estrategia -> estrategia.getNombre().equals(nombreEstrategia))
            .findFirst()
            .orElse(null);
    }
} 