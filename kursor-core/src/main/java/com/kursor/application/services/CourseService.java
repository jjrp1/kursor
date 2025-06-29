package com.kursor.ui.application.services;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.ui.infrastructure.persistence.CourseRepository;
import com.kursor.util.CursoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para la gestión de cursos.
 * 
 * <p>Este servicio implementa la lógica de negocio para la gestión de cursos,
 * actuando como intermediario entre los casos de uso y el repositorio de datos.
 * Sigue los principios de Clean Architecture al no depender de detalles de
 * implementación específicos.</p>
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Orquestar operaciones de negocio relacionadas con cursos</li>
 *   <li>Validar datos de entrada y salida</li>
 *   <li>Manejar transacciones y consistencia de datos</li>
 *   <li>Proporcionar una API limpia para los casos de uso</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see CourseRepository
 * @see CursoDTO
 */
public class CourseService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    
    private final CourseRepository courseRepository;
    private final CursoManager cursoManager;
    
    /**
     * Constructor para crear el servicio de cursos.
     * 
     * @param courseRepository Repositorio para acceso a datos de cursos
     * @param cursoManager Gestor de cursos para operaciones específicas
     */
    public CourseService(CourseRepository courseRepository, CursoManager cursoManager) {
        this.courseRepository = courseRepository;
        this.cursoManager = cursoManager;
        logger.debug("CourseService creado con repositorio y gestor de cursos");
    }
    
    /**
     * Carga todos los cursos disponibles.
     * 
     * <p>Este método obtiene la lista completa de cursos desde el repositorio,
     * aplicando validaciones y transformaciones necesarias.</p>
     * 
     * @return Lista de cursos disponibles, nunca null
     * @throws ServiceException si hay un error durante la carga
     */
    public List<CursoDTO> loadCourses() throws ServiceException {
        logger.info("Cargando todos los cursos disponibles");
        
        try {
            // Intentar cargar desde el repositorio primero
            List<CursoDTO> cursos = courseRepository.findAll();
            
            if (cursos != null && !cursos.isEmpty()) {
                logger.info("Cursos cargados desde repositorio: {}", cursos.size());
                return validateAndFilterCourses(cursos);
            }
            
            // Fallback al CursoManager si el repositorio no tiene datos
            logger.info("Repositorio vacío, cargando desde CursoManager");
            cursos = cursoManager.cargarCursosCompletos();
            
            if (cursos != null && !cursos.isEmpty()) {
                logger.info("Cursos cargados desde CursoManager: {}", cursos.size());
                return validateAndFilterCourses(cursos);
            }
            
            logger.warn("No se encontraron cursos en ninguna fuente");
            return List.of();
            
        } catch (Exception e) {
            logger.error("Error al cargar cursos: {}", e.getMessage(), e);
            throw new ServiceException("Error al cargar los cursos", e);
        }
    }
    
    /**
     * Carga un curso específico por su ID.
     * 
     * @param courseId ID del curso a cargar
     * @return Curso encontrado, o null si no existe
     * @throws ServiceException si hay un error durante la carga
     */
    public CursoDTO loadCourseById(String courseId) throws ServiceException {
        logger.info("Cargando curso por ID: {}", courseId);
        
        if (courseId == null || courseId.trim().isEmpty()) {
            logger.error("ID de curso inválido: {}", courseId);
            throw new ServiceException("ID de curso no puede ser null o vacío");
        }
        
        try {
            // Intentar cargar desde el repositorio
            Optional<CursoDTO> curso = courseRepository.findById(courseId);
            
            if (curso.isPresent()) {
                logger.info("Curso encontrado en repositorio: {}", curso.get().getTitulo());
                return validateCourse(curso.get());
            }
            
            // Fallback al CursoManager
            logger.info("Curso no encontrado en repositorio, buscando en CursoManager");
            List<CursoDTO> todosLosCursos = cursoManager.cargarCursosCompletos();
            
            return todosLosCursos.stream()
                .filter(c -> courseId.equals(c.getId()))
                .findFirst()
                .map(cursoItem -> {
                    try {
                        return validateCourse(cursoItem);
                    } catch (ServiceException e) {
                        logger.error("Error al validar curso: {}", e.getMessage());
                        return null;
                    }
                })
                .orElse(null);
                
        } catch (Exception e) {
            logger.error("Error al cargar curso {}: {}", courseId, e.getMessage(), e);
            throw new ServiceException("Error al cargar el curso: " + courseId, e);
        }
    }
    
    /**
     * Guarda un curso en el repositorio.
     * 
     * @param curso Curso a guardar
     * @return Curso guardado con ID asignado
     * @throws ServiceException si hay un error durante el guardado
     */
    public CursoDTO saveCourse(CursoDTO curso) throws ServiceException {
        logger.info("Guardando curso: {}", curso.getTitulo());
        
        if (curso == null) {
            logger.error("Curso a guardar no puede ser null");
            throw new ServiceException("Curso no puede ser null");
        }
        
        try {
            // Validar el curso antes de guardar
            validateCourseForSave(curso);
            
            // Guardar en el repositorio
            CursoDTO cursoGuardado = courseRepository.save(curso);
            
            logger.info("Curso guardado exitosamente: {}", cursoGuardado.getTitulo());
            return cursoGuardado;
            
        } catch (Exception e) {
            logger.error("Error al guardar curso: {}", e.getMessage(), e);
            throw new ServiceException("Error al guardar el curso", e);
        }
    }
    
    /**
     * Elimina un curso por su ID.
     * 
     * @param courseId ID del curso a eliminar
     * @return true si se eliminó correctamente, false si no existía
     * @throws ServiceException si hay un error durante la eliminación
     */
    public boolean deleteCourse(String courseId) throws ServiceException {
        logger.info("Eliminando curso: {}", courseId);
        
        if (courseId == null || courseId.trim().isEmpty()) {
            logger.error("ID de curso inválido para eliminar: {}", courseId);
            throw new ServiceException("ID de curso no puede ser null o vacío");
        }
        
        try {
            boolean eliminado = courseRepository.deleteById(courseId);
            
            if (eliminado) {
                logger.info("Curso eliminado exitosamente: {}", courseId);
            } else {
                logger.warn("Curso no encontrado para eliminar: {}", courseId);
            }
            
            return eliminado;
            
        } catch (Exception e) {
            logger.error("Error al eliminar curso {}: {}", courseId, e.getMessage(), e);
            throw new ServiceException("Error al eliminar el curso: " + courseId, e);
        }
    }
    
    /**
     * Busca cursos que coincidan con un criterio de búsqueda.
     * 
     * @param searchTerm Término de búsqueda
     * @return Lista de cursos que coinciden con la búsqueda
     * @throws ServiceException si hay un error durante la búsqueda
     */
    public List<CursoDTO> searchCourses(String searchTerm) throws ServiceException {
        logger.info("Buscando cursos con término: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            logger.warn("Término de búsqueda vacío, retornando todos los cursos");
            return loadCourses();
        }
        
        try {
            List<CursoDTO> resultados = courseRepository.searchByTerm(searchTerm.trim());
            
            logger.info("Búsqueda completada: {} resultados encontrados", resultados.size());
            return validateAndFilterCourses(resultados);
            
        } catch (Exception e) {
            logger.error("Error en búsqueda de cursos: {}", e.getMessage(), e);
            throw new ServiceException("Error al buscar cursos", e);
        }
    }
    
    /**
     * Valida y filtra una lista de cursos.
     * 
     * @param cursos Lista de cursos a validar
     * @return Lista de cursos válidos
     */
    private List<CursoDTO> validateAndFilterCourses(List<CursoDTO> cursos) {
        if (cursos == null) {
            logger.warn("Lista de cursos es null, retornando lista vacía");
            return List.of();
        }
        
        return cursos.stream()
            .filter(this::isValidCourse)
            .toList();
    }
    
    /**
     * Valida un curso individual.
     * 
     * @param curso Curso a validar
     * @return Curso validado
     * @throws ServiceException si el curso no es válido
     */
    private CursoDTO validateCourse(CursoDTO curso) throws ServiceException {
        if (!isValidCourse(curso)) {
            logger.error("Curso inválido: {}", curso);
            throw new ServiceException("Curso no cumple con las validaciones requeridas");
        }
        
        return curso;
    }
    
    /**
     * Valida un curso antes de guardarlo.
     * 
     * @param curso Curso a validar
     * @throws ServiceException si el curso no es válido para guardar
     */
    private void validateCourseForSave(CursoDTO curso) throws ServiceException {
        if (curso.getTitulo() == null || curso.getTitulo().trim().isEmpty()) {
            throw new ServiceException("El título del curso no puede estar vacío");
        }
        
        if (curso.getDescripcion() == null || curso.getDescripcion().trim().isEmpty()) {
            throw new ServiceException("La descripción del curso no puede estar vacía");
        }
        
        // Validaciones adicionales según las reglas de negocio
        if (curso.getBloques() == null || curso.getBloques().isEmpty()) {
            throw new ServiceException("El curso debe tener al menos un bloque");
        }
        
        logger.debug("Curso validado correctamente para guardar: {}", curso.getTitulo());
    }
    
    /**
     * Verifica si un curso es válido.
     * 
     * @param curso Curso a verificar
     * @return true si el curso es válido, false en caso contrario
     */
    private boolean isValidCourse(CursoDTO curso) {
        if (curso == null) {
            return false;
        }
        
        if (curso.getId() == null || curso.getId().trim().isEmpty()) {
            logger.warn("Curso sin ID válido: {}", curso.getTitulo());
            return false;
        }
        
        if (curso.getTitulo() == null || curso.getTitulo().trim().isEmpty()) {
            logger.warn("Curso sin título válido: {}", curso.getId());
            return false;
        }
        
        return true;
    }
    
    /**
     * Excepción específica para errores en servicios.
     * 
     * <p>Esta excepción encapsula errores que ocurren durante la ejecución
     * de servicios, proporcionando información útil para el manejo
     * de errores en los casos de uso.</p>
     */
    public static class ServiceException extends Exception {
        
        /**
         * Constructor para crear una excepción de servicio.
         * 
         * @param message Mensaje descriptivo del error
         */
        public ServiceException(String message) {
            super(message);
        }
        
        /**
         * Constructor para crear una excepción de servicio con causa.
         * 
         * @param message Mensaje descriptivo del error
         * @param cause Excepción que causó este error
         */
        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 