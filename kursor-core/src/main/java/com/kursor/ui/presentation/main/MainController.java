package com.kursor.ui.presentation.main;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.util.CursoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador básico para la vista principal.
 * 
 * <p>Esta es una implementación mínima para que compile la nueva arquitectura
 * sin interferir con el código existente.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainController {
    
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    private final CursoManager cursoManager;
    
    /**
     * Constructor básico del controlador.
     * 
     * @param cursoManager Gestor de cursos
     */
    public MainController(CursoManager cursoManager) {
        this.cursoManager = cursoManager;
        logger.debug("MainController creado");
    }
    
    /**
     * Inicia la aplicación.
     */
    public void start() {
        logger.info("Iniciando aplicación con nueva arquitectura");
        // Por ahora solo log, sin cambiar la estructura existente
    }
    
    /**
     * Maneja la recarga de cursos.
     */
    public void onReloadCourses() {
        logger.debug("Recarga de cursos solicitada");
        // Implementación básica
    }
    
    /**
     * Maneja el inicio de sesión de estudio.
     * 
     * @param curso Curso seleccionado
     */
    public void onStartStudySession(CursoDTO curso) {
        logger.debug("Inicio de sesión solicitado para: {}", 
            curso != null ? curso.getTitulo() : "null");
        // Implementación básica
    }
    
    /**
     * Maneja la salida de la aplicación.
     */
    public void onExitApplication() {
        logger.debug("Salida de aplicación solicitada");
        System.exit(0);
    }
    
    /**
     * Maneja la selección de un curso.
     * 
     * @param curso Curso seleccionado
     */
    public void onCourseSelected(CursoDTO curso) {
        logger.debug("Curso seleccionado: {}", 
            curso != null ? curso.getTitulo() : "null");
        // Implementación básica
    }
} 