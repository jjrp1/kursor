package com.kursor.ui.application.usecases;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.ui.application.services.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Caso de uso para cargar la lista de cursos disponibles.
 * 
 * <p>Este caso de uso encapsula la lógica de negocio para cargar cursos,
 * incluyendo la validación de datos y el manejo de errores. Sigue los
 * principios de Clean Architecture al no depender de detalles de implementación.</p>
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Validar parámetros de entrada</li>
 *   <li>Delegar la carga de datos al servicio correspondiente</li>
 *   <li>Manejar errores y excepciones</li>
 *   <li>Retornar resultados validados</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see CourseService
 * @see CursoDTO
 */
public class LoadCoursesUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadCoursesUseCase.class);
    
    private final CourseService courseService;
    
    /**
     * Constructor para crear el caso de uso de carga de cursos.
     * 
     * @param courseService Servicio de cursos para cargar los datos
     */
    public LoadCoursesUseCase(CourseService courseService) {
        this.courseService = courseService;
        logger.debug("LoadCoursesUseCase creado con CourseService");
    }
    
    /**
     * Ejecuta el caso de uso para cargar todos los cursos disponibles.
     * 
     * <p>Este método maneja el flujo completo de carga de cursos:</p>
     * <ol>
     *   <li>Validación de precondiciones</li>
     *   <li>Delegación al servicio de cursos</li>
     *   <li>Validación de resultados</li>
     *   <li>Logging de resultados</li>
     * </ol>
     * 
     * @return Lista de cursos cargados, nunca null (lista vacía si no hay cursos)
     * @throws UseCaseException si hay un error durante la carga
     */
    public List<CursoDTO> execute() throws UseCaseException {
        logger.info("Iniciando carga de cursos");
        
        try {
            // Validar precondiciones
            validatePreconditions();
            
            // Delegar al servicio
            List<CursoDTO> cursos = courseService.loadCourses();
            
            // Validar resultados
            validateResults(cursos);
            
            // Logging de resultados
            logResults(cursos);
            
            logger.info("Carga de cursos completada exitosamente: {} cursos", cursos.size());
            return cursos;
            
        } catch (Exception e) {
            logger.error("Error durante la carga de cursos: {}", e.getMessage(), e);
            throw new UseCaseException("No se pudieron cargar los cursos", e);
        }
    }
    
    /**
     * Ejecuta el caso de uso para cargar un curso específico por ID.
     * 
     * @param courseId ID del curso a cargar
     * @return Curso cargado
     * @throws UseCaseException si hay un error o el curso no existe
     */
    public CursoDTO execute(String courseId) throws UseCaseException {
        logger.info("Iniciando carga de curso específico: {}", courseId);
        
        if (courseId == null || courseId.trim().isEmpty()) {
            logger.error("ID de curso inválido: {}", courseId);
            throw new UseCaseException("ID de curso no puede ser null o vacío");
        }
        
        try {
            CursoDTO curso = courseService.loadCourseById(courseId);
            
            if (curso == null) {
                logger.warn("Curso no encontrado: {}", courseId);
                throw new UseCaseException("Curso no encontrado: " + courseId);
            }
            
            logger.info("Curso cargado exitosamente: {}", curso.getTitulo());
            return curso;
            
        } catch (Exception e) {
            logger.error("Error al cargar curso {}: {}", courseId, e.getMessage(), e);
            throw new UseCaseException("Error al cargar el curso: " + courseId, e);
        }
    }
    
    /**
     * Valida las precondiciones para la ejecución del caso de uso.
     * 
     * @throws UseCaseException si las precondiciones no se cumplen
     */
    private void validatePreconditions() throws UseCaseException {
        if (courseService == null) {
            logger.error("CourseService no está inicializado");
            throw new UseCaseException("Servicio de cursos no disponible");
        }
        
        logger.debug("Precondiciones validadas correctamente");
    }
    
    /**
     * Valida los resultados obtenidos del servicio.
     * 
     * @param cursos Lista de cursos a validar
     * @throws UseCaseException si los resultados no son válidos
     */
    private void validateResults(List<CursoDTO> cursos) throws UseCaseException {
        if (cursos == null) {
            logger.error("El servicio retornó null en lugar de lista de cursos");
            throw new UseCaseException("Error interno: lista de cursos es null");
        }
        
        // Validar que cada curso tenga datos válidos
        for (CursoDTO curso : cursos) {
            if (curso.getId() == null || curso.getId().trim().isEmpty()) {
                logger.warn("Curso encontrado sin ID válido: {}", curso.getTitulo());
            }
            if (curso.getTitulo() == null || curso.getTitulo().trim().isEmpty()) {
                logger.warn("Curso encontrado sin título válido: {}", curso.getId());
            }
        }
        
        logger.debug("Resultados validados correctamente: {} cursos", cursos.size());
    }
    
    /**
     * Registra información sobre los resultados obtenidos.
     * 
     * @param cursos Lista de cursos cargados
     */
    private void logResults(List<CursoDTO> cursos) {
        if (cursos.isEmpty()) {
            logger.warn("No se encontraron cursos disponibles");
        } else {
            logger.info("Cursos cargados:");
            for (int i = 0; i < cursos.size(); i++) {
                CursoDTO curso = cursos.get(i);
                logger.info("  {}. {} - {} bloques, {} preguntas", 
                    i + 1, curso.getTitulo(), curso.getTotalBloques(), curso.getTotalPreguntas());
            }
        }
    }
    
    /**
     * Excepción específica para errores en casos de uso.
     * 
     * <p>Esta excepción encapsula errores que ocurren durante la ejecución
     * de casos de uso, proporcionando información útil para el manejo
     * de errores en la capa de presentación.</p>
     */
    public static class UseCaseException extends Exception {
        
        /**
         * Constructor para crear una excepción de caso de uso.
         * 
         * @param message Mensaje descriptivo del error
         */
        public UseCaseException(String message) {
            super(message);
        }
        
        /**
         * Constructor para crear una excepción de caso de uso con causa.
         * 
         * @param message Mensaje descriptivo del error
         * @param cause Excepción que causó este error
         */
        public UseCaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 