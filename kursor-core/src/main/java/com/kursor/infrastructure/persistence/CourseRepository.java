package com.kursor.ui.infrastructure.persistence;

import com.kursor.yaml.dto.CursoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para la gestión de datos de cursos.
 * 
 * <p>Esta interfaz define el contrato para el acceso a datos de cursos,
 * siguiendo los principios de Clean Architecture al no depender de
 * detalles de implementación específicos de la base de datos.</p>
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Definir operaciones CRUD para cursos</li>
 *   <li>Proporcionar métodos de búsqueda</li>
 *   <li>Mantener independencia de la tecnología de persistencia</li>
 *   <li>Facilitar testing mediante mocks</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see CursoDTO
 */
public interface CourseRepository {
    
    /**
     * Obtiene todos los cursos disponibles.
     * 
     * @return Lista de todos los cursos, nunca null
     * @throws RepositoryException si hay un error durante la consulta
     */
    List<CursoDTO> findAll() throws RepositoryException;
    
    /**
     * Busca un curso por su ID.
     * 
     * @param id ID del curso a buscar
     * @return Optional con el curso encontrado, o empty si no existe
     * @throws RepositoryException si hay un error durante la búsqueda
     */
    Optional<CursoDTO> findById(String id) throws RepositoryException;
    
    /**
     * Guarda un curso en el repositorio.
     * 
     * @param curso Curso a guardar
     * @return Curso guardado con ID asignado
     * @throws RepositoryException si hay un error durante el guardado
     */
    CursoDTO save(CursoDTO curso) throws RepositoryException;
    
    /**
     * Actualiza un curso existente.
     * 
     * @param curso Curso a actualizar
     * @return Curso actualizado
     * @throws RepositoryException si hay un error durante la actualización
     */
    CursoDTO update(CursoDTO curso) throws RepositoryException;
    
    /**
     * Elimina un curso por su ID.
     * 
     * @param id ID del curso a eliminar
     * @return true si se eliminó correctamente, false si no existía
     * @throws RepositoryException si hay un error durante la eliminación
     */
    boolean deleteById(String id) throws RepositoryException;
    
    /**
     * Busca cursos que coincidan con un término de búsqueda.
     * 
     * @param searchTerm Término de búsqueda
     * @return Lista de cursos que coinciden con la búsqueda
     * @throws RepositoryException si hay un error durante la búsqueda
     */
    List<CursoDTO> searchByTerm(String searchTerm) throws RepositoryException;
    
    /**
     * Verifica si existe un curso con el ID especificado.
     * 
     * @param id ID del curso a verificar
     * @return true si existe, false en caso contrario
     * @throws RepositoryException si hay un error durante la verificación
     */
    boolean existsById(String id) throws RepositoryException;
    
    /**
     * Cuenta el número total de cursos.
     * 
     * @return Número total de cursos
     * @throws RepositoryException si hay un error durante el conteo
     */
    long count() throws RepositoryException;
    
    /**
     * Excepción específica para errores en repositorios.
     * 
     * <p>Esta excepción encapsula errores que ocurren durante las operaciones
     * de persistencia, proporcionando información útil para el manejo
     * de errores en los servicios.</p>
     */
    class RepositoryException extends Exception {
        
        /**
         * Constructor para crear una excepción de repositorio.
         * 
         * @param message Mensaje descriptivo del error
         */
        public RepositoryException(String message) {
            super(message);
        }
        
        /**
         * Constructor para crear una excepción de repositorio con causa.
         * 
         * @param message Mensaje descriptivo del error
         * @param cause Excepción que causó este error
         */
        public RepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 
