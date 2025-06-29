package com.kursor.presentation.controllers;

import com.kursor.presentation.viewmodels.BloqueSelectorViewModel;
import com.kursor.yaml.dto.BloqueDTO;
import com.kursor.yaml.dto.CursoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para el selector de bloques siguiendo el patrón MVC.
 * 
 * <p>Este controlador maneja la lógica de negocio para la selección de bloques,
 * separando la lógica de la vista y el modelo.</p>
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Gestionar la lógica de selección de bloques</li>
 *   <li>Validar la selección del usuario</li>
 *   <li>Coordinar entre la vista y el ViewModel</li>
 *   <li>Manejar errores y excepciones</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see BloqueSelectorViewModel
 * @see BloqueDTO
 * @see CursoDTO
 */
public class BloqueSelectorController {
    
    private static final Logger logger = LoggerFactory.getLogger(BloqueSelectorController.class);
    
    /** ViewModel asociado */
    private final BloqueSelectorViewModel viewModel;
    
    /**
     * Constructor del controlador.
     * 
     * @param curso Curso para el cual seleccionar bloques
     */
    public BloqueSelectorController(CursoDTO curso) {
        logger.debug("Creando BloqueSelectorController para curso: {}", curso.getTitulo());
        
        this.viewModel = new BloqueSelectorViewModel();
        inicializarConCurso(curso);
    }
    
    /**
     * Inicializa el controlador con un curso.
     * 
     * @param curso Curso para el cual seleccionar bloques
     */
    public void inicializarConCurso(CursoDTO curso) {
        logger.debug("Inicializando controlador con curso: {}", curso.getTitulo());
        
        if (curso == null) {
            logger.error("Error: curso no puede ser null");
            throw new IllegalArgumentException("Curso no puede ser null");
        }
        
        viewModel.inicializarConCurso(curso);
    }
    
    /**
     * Selecciona un bloque.
     * 
     * @param bloque Bloque a seleccionar
     */
    public void seleccionarBloque(BloqueDTO bloque) {
        logger.debug("Seleccionando bloque: {}", bloque != null ? bloque.getTitulo() : "null");
        
        if (bloque == null) {
            logger.warn("Intento de seleccionar bloque null");
            return;
        }
        
        // Validar que el bloque pertenece al curso
        if (!viewModel.getBloquesDisponibles().contains(bloque)) {
            logger.warn("Bloque {} no pertenece al curso actual", bloque.getTitulo());
            return;
        }
        
        viewModel.seleccionarBloque(bloque);
        logger.info("Bloque seleccionado correctamente: {}", bloque.getTitulo());
    }
    
    /**
     * Deselecciona el bloque actual.
     */
    public void deseleccionarBloque() {
        logger.debug("Deseleccionando bloque actual");
        viewModel.limpiarSeleccion();
    }
    
    /**
     * Valida la selección actual.
     * 
     * @return true si la selección es válida, false en caso contrario
     */
    public boolean validarSeleccion() {
        boolean esValido = viewModel.validarSeleccion();
        logger.debug("Validación de selección: {}", esValido);
        return esValido;
    }
    
    /**
     * Confirma la selección actual.
     * 
     * @return Bloque seleccionado, o null si la selección no es válida
     */
    public BloqueDTO confirmarSeleccion() {
        logger.debug("Confirmando selección de bloque");
        
        if (!validarSeleccion()) {
            logger.warn("No se puede confirmar selección: selección no válida");
            return null;
        }
        
        BloqueDTO bloqueSeleccionado = viewModel.getBloqueSeleccionado();
        logger.info("Selección confirmada: {}", bloqueSeleccionado.getTitulo());
        return bloqueSeleccionado;
    }
    
    /**
     * Cancela la selección actual.
     */
    public void cancelarSeleccion() {
        logger.debug("Cancelando selección de bloque");
        viewModel.limpiarSeleccion();
    }
    
    /**
     * Obtiene el bloque seleccionado.
     * 
     * @return Bloque seleccionado, o null si no hay selección
     */
    public BloqueDTO getBloqueSeleccionado() {
        return viewModel.getBloqueSeleccionado();
    }
    
    /**
     * Verifica si hay un bloque seleccionado.
     * 
     * @return true si hay un bloque seleccionado, false en caso contrario
     */
    public boolean isBloqueSeleccionado() {
        return viewModel.isBloqueSeleccionado();
    }
    
    /**
     * Obtiene el ViewModel asociado.
     * 
     * @return ViewModel del selector de bloques
     */
    public BloqueSelectorViewModel getViewModel() {
        return viewModel;
    }
    
    /**
     * Obtiene el curso actual.
     * 
     * @return Curso actual
     */
    public CursoDTO getCurso() {
        return viewModel.getCurso();
    }
    
    /**
     * Verifica si hay bloques disponibles.
     * 
     * @return true si hay bloques disponibles, false en caso contrario
     */
    public boolean tieneBloquesDisponibles() {
        return viewModel.tieneBloquesDisponibles();
    }
    
    /**
     * Obtiene el número de bloques disponibles.
     * 
     * @return Número de bloques disponibles
     */
    public int getNumeroBloques() {
        return viewModel.getNumeroBloques();
    }
    
    /**
     * Verifica si está cargando.
     * 
     * @return true si está cargando, false en caso contrario
     */
    public boolean isCargando() {
        return viewModel.isCargando();
    }
    
    /**
     * Obtiene el mensaje de error actual.
     * 
     * @return Mensaje de error, o cadena vacía si no hay error
     */
    public String getMensajeError() {
        return viewModel.getMensajeError();
    }
    
    /**
     * Verifica si la selección es válida.
     * 
     * @return true si la selección es válida, false en caso contrario
     */
    public boolean isSeleccionValida() {
        return viewModel.isSeleccionValida();
    }
} 