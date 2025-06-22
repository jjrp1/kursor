package com.kursor.persistence.repository;

import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadoSesion;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Sesion.
 * 
 * <p>Este repositorio proporciona métodos para gestionar las sesiones de aprendizaje,
 * incluyendo operaciones CRUD básicas y consultas específicas del dominio.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class SesionRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(SesionRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Constructor por defecto.
     */
    public SesionRepository() {
    }
    
    /**
     * Constructor con EntityManager.
     * 
     * @param entityManager EntityManager para operaciones JPA
     */
    public SesionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Guarda una sesión en la base de datos.
     * 
     * @param sesion Sesión a guardar
     * @return Sesión guardada con ID generado
     */
    public Sesion guardar(Sesion sesion) {
        logger.debug("Guardando sesión - Usuario: {}, Curso: {}, Bloque: {}", 
                    sesion.getUsuarioId(), sesion.getCursoId(), sesion.getBloqueId());
        
        try {
            if (sesion.getId() == null) {
                entityManager.persist(sesion);
                logger.info("Sesión creada exitosamente - ID: {}", sesion.getId());
            } else {
                sesion = entityManager.merge(sesion);
                logger.info("Sesión actualizada exitosamente - ID: {}", sesion.getId());
            }
            return sesion;
        } catch (Exception e) {
            logger.error("Error al guardar sesión", e);
            throw new RuntimeException("Error al guardar sesión", e);
        }
    }
    
    /**
     * Busca una sesión por su ID.
     * 
     * @param id ID de la sesión
     * @return Optional con la sesión si existe
     */
    public Optional<Sesion> buscarPorId(Long id) {
        logger.debug("Buscando sesión por ID: {}", id);
        
        try {
            Sesion sesion = entityManager.find(Sesion.class, id);
            if (sesion != null) {
                logger.debug("Sesión encontrada - ID: {}", id);
            } else {
                logger.debug("Sesión no encontrada - ID: {}", id);
            }
            return Optional.ofNullable(sesion);
        } catch (Exception e) {
            logger.error("Error al buscar sesión por ID: {}", id, e);
            throw new RuntimeException("Error al buscar sesión por ID", e);
        }
    }
    
    /**
     * Busca la sesión activa de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Optional con la sesión activa si existe
     */
    public Optional<Sesion> buscarSesionActiva(String usuarioId) {
        logger.debug("Buscando sesión activa para usuario: {}", usuarioId);
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.usuarioId = :usuarioId AND s.estado = :estado",
                Sesion.class
            );
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("estado", EstadoSesion.ACTIVA);
            
            List<Sesion> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                logger.info("Sesión activa encontrada para usuario: {} - ID: {}", 
                          usuarioId, resultados.get(0).getId());
                return Optional.of(resultados.get(0));
            } else {
                logger.debug("No se encontró sesión activa para usuario: {}", usuarioId);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error al buscar sesión activa para usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al buscar sesión activa", e);
        }
    }
    
    /**
     * Lista todas las sesiones de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de sesiones ordenadas por fecha de inicio (más reciente primero)
     */
    public List<Sesion> buscarSesionesUsuario(String usuarioId) {
        logger.debug("Buscando sesiones para usuario: {}", usuarioId);
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.usuarioId = :usuarioId ORDER BY s.fechaInicio DESC",
                Sesion.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            List<Sesion> sesiones = query.getResultList();
            logger.info("Encontradas {} sesiones para usuario: {}", sesiones.size(), usuarioId);
            return sesiones;
        } catch (Exception e) {
            logger.error("Error al buscar sesiones para usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al buscar sesiones de usuario", e);
        }
    }
    
    /**
     * Lista las sesiones de un usuario por curso.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso
     * @return Lista de sesiones del curso ordenadas por fecha
     */
    public List<Sesion> buscarSesionesUsuarioPorCurso(String usuarioId, String cursoId) {
        logger.debug("Buscando sesiones para usuario: {} en curso: {}", usuarioId, cursoId);
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.usuarioId = :usuarioId AND s.cursoId = :cursoId " +
                "ORDER BY s.fechaInicio DESC",
                Sesion.class
            );
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("cursoId", cursoId);
            
            List<Sesion> sesiones = query.getResultList();
            logger.info("Encontradas {} sesiones para usuario: {} en curso: {}", 
                      sesiones.size(), usuarioId, cursoId);
            return sesiones;
        } catch (Exception e) {
            logger.error("Error al buscar sesiones para usuario: {} en curso: {}", usuarioId, cursoId, e);
            throw new RuntimeException("Error al buscar sesiones por curso", e);
        }
    }
    
    /**
     * Busca sesiones por estado.
     * 
     * @param estado Estado de las sesiones a buscar
     * @return Lista de sesiones con el estado especificado
     */
    public List<Sesion> buscarSesionesPorEstado(EstadoSesion estado) {
        logger.debug("Buscando sesiones con estado: {}", estado);
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.estado = :estado ORDER BY s.fechaInicio DESC",
                Sesion.class
            );
            query.setParameter("estado", estado);
            
            List<Sesion> sesiones = query.getResultList();
            logger.info("Encontradas {} sesiones con estado: {}", sesiones.size(), estado);
            return sesiones;
        } catch (Exception e) {
            logger.error("Error al buscar sesiones con estado: {}", estado, e);
            throw new RuntimeException("Error al buscar sesiones por estado", e);
        }
    }
    
    /**
     * Busca sesiones que han estado inactivas por más tiempo del especificado.
     * 
     * @param diasInactividad Número de días de inactividad
     * @return Lista de sesiones inactivas
     */
    public List<Sesion> buscarSesionesInactivas(int diasInactividad) {
        logger.debug("Buscando sesiones inactivas por más de {} días", diasInactividad);
        
        try {
            LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasInactividad);
            
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.fechaUltimaActividad < :fechaLimite " +
                "AND s.estado = :estado ORDER BY s.fechaUltimaActividad ASC",
                Sesion.class
            );
            query.setParameter("fechaLimite", fechaLimite);
            query.setParameter("estado", EstadoSesion.ACTIVA);
            
            List<Sesion> sesiones = query.getResultList();
            logger.info("Encontradas {} sesiones inactivas por más de {} días", 
                      sesiones.size(), diasInactividad);
            return sesiones;
        } catch (Exception e) {
            logger.error("Error al buscar sesiones inactivas por más de {} días", diasInactividad, e);
            throw new RuntimeException("Error al buscar sesiones inactivas", e);
        }
    }
    
    /**
     * Elimina una sesión por su ID.
     * 
     * @param id ID de la sesión a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminar(Long id) {
        logger.debug("Eliminando sesión con ID: {}", id);
        
        try {
            Sesion sesion = entityManager.find(Sesion.class, id);
            if (sesion != null) {
                entityManager.remove(sesion);
                logger.info("Sesión eliminada exitosamente - ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontró sesión para eliminar - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al eliminar sesión con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar sesión", e);
        }
    }
    
    /**
     * Elimina todas las sesiones de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de sesiones eliminadas
     */
    public int eliminarSesionesUsuario(String usuarioId) {
        logger.debug("Eliminando todas las sesiones del usuario: {}", usuarioId);
        
        try {
            Query query = entityManager.createQuery(
                "DELETE FROM Sesion s WHERE s.usuarioId = :usuarioId"
            );
            query.setParameter("usuarioId", usuarioId);
            
            int eliminadas = query.executeUpdate();
            logger.info("Eliminadas {} sesiones del usuario: {}", eliminadas, usuarioId);
            return eliminadas;
        } catch (Exception e) {
            logger.error("Error al eliminar sesiones del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al eliminar sesiones de usuario", e);
        }
    }
    
    /**
     * Cuenta el número de sesiones de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de sesiones
     */
    public long contarSesionesUsuario(String usuarioId) {
        logger.debug("Contando sesiones del usuario: {}", usuarioId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Sesion s WHERE s.usuarioId = :usuarioId",
                Long.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            long count = query.getSingleResult();
            logger.debug("Usuario {} tiene {} sesiones", usuarioId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar sesiones del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al contar sesiones de usuario", e);
        }
    }
    
    /**
     * Cuenta el número de sesiones completadas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de sesiones completadas
     */
    public long contarSesionesCompletadas(String usuarioId) {
        logger.debug("Contando sesiones completadas del usuario: {}", usuarioId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Sesion s WHERE s.usuarioId = :usuarioId AND s.estado = :estado",
                Long.class
            );
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("estado", EstadoSesion.COMPLETADA);
            
            long count = query.getSingleResult();
            logger.debug("Usuario {} tiene {} sesiones completadas", usuarioId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar sesiones completadas del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al contar sesiones completadas", e);
        }
    }
    
    /**
     * Obtiene el EntityManager.
     * 
     * @return EntityManager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    /**
     * Establece el EntityManager.
     * 
     * @param entityManager EntityManager
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
} 