package com.kursor.ui.infrastructure.persistence;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.shared.util.CursoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de cursos utilizando CursoManager.
 * 
 * <p>Esta implementación actúa como adaptador entre la interfaz del repositorio
 * y el CursoManager existente, manteniendo la compatibilidad con el código
 * actual mientras proporciona una interfaz limpia para la nueva arquitectura.</p>
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Adaptar las operaciones del repositorio al CursoManager</li>
 *   <li>Manejar conversiones de datos cuando sea necesario</li>
 *   <li>Proporcionar logging detallado de operaciones</li>
 *   <li>Manejar errores y excepciones apropiadamente</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see CourseRepository
 * @see CursoManager
 */
public class CourseRepositoryImpl implements CourseRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseRepositoryImpl.class);
    
    private final CursoManager cursoManager;
    
    /**
     * Constructor para crear la implementación del repositorio.
     * 
     * @param cursoManager Gestor de cursos para operaciones de datos
     */
    public CourseRepositoryImpl(CursoManager cursoManager) {
        this.cursoManager = cursoManager;
        logger.debug("CourseRepositoryImpl creado con CursoManager");
    }
    
    @Override
    public List<CursoDTO> findAll() throws RepositoryException {
        logger.debug("Obteniendo todos los cursos");
        
        try {
            List<CursoDTO> cursos = cursoManager.cargarCursosCompletos();
            
            if (cursos == null) {
                logger.warn("CursoManager retornó null, retornando lista vacía");
                return List.of();
            }
            
            logger.debug("Cursos obtenidos: {}", cursos.size());
            return cursos;
            
        } catch (Exception e) {
            logger.error("Error al obtener todos los cursos: {}", e.getMessage(), e);
            throw new RepositoryException("Error al cargar los cursos", e);
        }
    }
    
    @Override
    public Optional<CursoDTO> findById(String id) throws RepositoryException {
        logger.debug("Buscando curso por ID: {}", id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.warn("ID de curso inválido: {}", id);
            return Optional.empty();
        }
        
        try {
            List<CursoDTO> todosLosCursos = cursoManager.cargarCursosCompletos();
            
            if (todosLosCursos == null) {
                logger.warn("CursoManager retornó null, no se puede buscar curso");
                return Optional.empty();
            }
            
            Optional<CursoDTO> curso = todosLosCursos.stream()
                .filter(c -> id.equals(c.getId()))
                .findFirst();
            
            if (curso.isPresent()) {
                logger.debug("Curso encontrado: {}", curso.get().getTitulo());
            } else {
                logger.debug("Curso no encontrado: {}", id);
            }
            
            return curso;
            
        } catch (Exception e) {
            logger.error("Error al buscar curso por ID {}: {}", id, e.getMessage(), e);
            throw new RepositoryException("Error al buscar el curso: " + id, e);
        }
    }
    
    @Override
    public CursoDTO save(CursoDTO curso) throws RepositoryException {
        logger.info("Guardando curso: {}", curso.getTitulo());
        
        if (curso == null) {
            logger.error("Curso a guardar no puede ser null");
            throw new RepositoryException("Curso no puede ser null");
        }
        
        try {
            // Por ahora, el CursoManager no tiene método de guardado
            // Esta es una implementación temporal que simula el guardado
            logger.warn("Operación de guardado no implementada en CursoManager");
            
            // Simular guardado exitoso
            if (curso.getId() == null || curso.getId().trim().isEmpty()) {
                // Generar ID temporal si no existe
                curso.setId("temp_" + System.currentTimeMillis());
                logger.info("ID temporal generado: {}", curso.getId());
            }
            
            logger.info("Curso guardado (simulado): {}", curso.getTitulo());
            return curso;
            
        } catch (Exception e) {
            logger.error("Error al guardar curso: {}", e.getMessage(), e);
            throw new RepositoryException("Error al guardar el curso", e);
        }
    }
    
    @Override
    public CursoDTO update(CursoDTO curso) throws RepositoryException {
        logger.info("Actualizando curso: {}", curso.getTitulo());
        
        if (curso == null) {
            logger.error("Curso a actualizar no puede ser null");
            throw new RepositoryException("Curso no puede ser null");
        }
        
        if (curso.getId() == null || curso.getId().trim().isEmpty()) {
            logger.error("Curso a actualizar debe tener ID válido");
            throw new RepositoryException("Curso debe tener ID válido para actualizar");
        }
        
        try {
            // Verificar que el curso existe
            Optional<CursoDTO> cursoExistente = findById(curso.getId());
            if (cursoExistente.isEmpty()) {
                logger.warn("Curso no encontrado para actualizar: {}", curso.getId());
                throw new RepositoryException("Curso no encontrado: " + curso.getId());
            }
            
            // Por ahora, el CursoManager no tiene método de actualización
            // Esta es una implementación temporal
            logger.warn("Operación de actualización no implementada en CursoManager");
            
            logger.info("Curso actualizado (simulado): {}", curso.getTitulo());
            return curso;
            
        } catch (RepositoryException e) {
            throw e; // Re-lanzar excepciones de repositorio
        } catch (Exception e) {
            logger.error("Error al actualizar curso: {}", e.getMessage(), e);
            throw new RepositoryException("Error al actualizar el curso", e);
        }
    }
    
    @Override
    public boolean deleteById(String id) throws RepositoryException {
        logger.info("Eliminando curso: {}", id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("ID de curso inválido para eliminar: {}", id);
            throw new RepositoryException("ID de curso no puede ser null o vacío");
        }
        
        try {
            // Verificar que el curso existe
            Optional<CursoDTO> curso = findById(id);
            if (curso.isEmpty()) {
                logger.warn("Curso no encontrado para eliminar: {}", id);
                return false;
            }
            
            // Por ahora, el CursoManager no tiene método de eliminación
            // Esta es una implementación temporal
            logger.warn("Operación de eliminación no implementada en CursoManager");
            
            logger.info("Curso eliminado (simulado): {}", curso.get().getTitulo());
            return true;
            
        } catch (RepositoryException e) {
            throw e; // Re-lanzar excepciones de repositorio
        } catch (Exception e) {
            logger.error("Error al eliminar curso {}: {}", id, e.getMessage(), e);
            throw new RepositoryException("Error al eliminar el curso: " + id, e);
        }
    }
    
    @Override
    public List<CursoDTO> searchByTerm(String searchTerm) throws RepositoryException {
        logger.debug("Buscando cursos con término: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            logger.debug("Término de búsqueda vacío, retornando todos los cursos");
            return findAll();
        }
        
        try {
            List<CursoDTO> todosLosCursos = cursoManager.cargarCursosCompletos();
            
            if (todosLosCursos == null) {
                logger.warn("CursoManager retornó null, no se puede realizar búsqueda");
                return List.of();
            }
            
            String terminoBusqueda = searchTerm.toLowerCase().trim();
            
            List<CursoDTO> resultados = todosLosCursos.stream()
                .filter(curso -> 
                    (curso.getTitulo() != null && 
                     curso.getTitulo().toLowerCase().contains(terminoBusqueda)) ||
                    (curso.getDescripcion() != null && 
                     curso.getDescripcion().toLowerCase().contains(terminoBusqueda)) ||
                    (curso.getId() != null && 
                     curso.getId().toLowerCase().contains(terminoBusqueda))
                )
                .collect(Collectors.toList());
            
            logger.debug("Búsqueda completada: {} resultados encontrados", resultados.size());
            return resultados;
            
        } catch (Exception e) {
            logger.error("Error en búsqueda de cursos: {}", e.getMessage(), e);
            throw new RepositoryException("Error al buscar cursos", e);
        }
    }
    
    @Override
    public boolean existsById(String id) throws RepositoryException {
        logger.debug("Verificando existencia de curso: {}", id);
        
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        try {
            Optional<CursoDTO> curso = findById(id);
            boolean existe = curso.isPresent();
            
            logger.debug("Curso {} existe: {}", id, existe);
            return existe;
            
        } catch (RepositoryException e) {
            throw e; // Re-lanzar excepciones de repositorio
        } catch (Exception e) {
            logger.error("Error al verificar existencia de curso {}: {}", id, e.getMessage(), e);
            throw new RepositoryException("Error al verificar existencia del curso: " + id, e);
        }
    }
    
    @Override
    public long count() throws RepositoryException {
        logger.debug("Contando total de cursos");
        
        try {
            List<CursoDTO> cursos = cursoManager.cargarCursosCompletos();
            
            if (cursos == null) {
                logger.warn("CursoManager retornó null, contando 0");
                return 0;
            }
            
            long total = cursos.size();
            logger.debug("Total de cursos: {}", total);
            return total;
            
        } catch (Exception e) {
            logger.error("Error al contar cursos: {}", e.getMessage(), e);
            throw new RepositoryException("Error al contar los cursos", e);
        }
    }
} 
