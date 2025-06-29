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
        logger.debug("Guardando sesión - Curso: {}, Bloque: {}", 
                    sesion.getCursoId(), sesion.getBloqueId());
        
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
     * Busca la sesión en curso.
     * 
     * @return Optional con la sesión en curso si existe
     */
    public Optional<Sesion> buscarSesionEnCurso() {
        logger.debug("Buscando sesión en curso");
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.estado = :estado",
                Sesion.class
            );
            query.setParameter("estado", EstadoSesion.EN_CURSO);
            
            List<Sesion> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                logger.info("Sesión en curso encontrada - ID: {}", resultados.get(0).getId());
                return Optional.of(resultados.get(0));
            } else {
                logger.debug("No se encontró sesión en curso");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error al buscar sesión en curso", e);
            throw new RuntimeException("Error al buscar sesión en curso", e);
        }
    }
    
    /**
     * Lista todas las sesiones.
     * 
     * @return Lista de sesiones ordenadas por fecha de inicio (más reciente primero)
     */
    public List<Sesion> buscarTodasLasSesiones() {
        logger.debug("Buscando todas las sesiones");
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s ORDER BY s.fechaInicio DESC",
                Sesion.class
            );
            
            List<Sesion> sesiones = query.getResultList();
            logger.info("Encontradas {} sesiones", sesiones.size());
            return sesiones;
        } catch (Exception e) {
            logger.error("Error al buscar todas las sesiones", e);
            throw new RuntimeException("Error al buscar todas las sesiones", e);
        }
    }
    
    /**
     * Lista las sesiones por curso.
     * 
     * @param cursoId ID del curso
     * @return Lista de sesiones del curso ordenadas por fecha
     */
    public List<Sesion> buscarSesionesPorCurso(String cursoId) {
        logger.debug("Buscando sesiones para curso: {}", cursoId);
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.cursoId = :cursoId " +
                "ORDER BY s.fechaInicio DESC",
                Sesion.class
            );
            query.setParameter("cursoId", cursoId);
            
            List<Sesion> sesiones = query.getResultList();
            logger.info("Encontradas {} sesiones para curso: {}", sesiones.size(), cursoId);
            return sesiones;
        } catch (Exception e) {
            logger.error("Error al buscar sesiones para curso: {}", cursoId, e);
            throw new RuntimeException("Error al buscar sesiones por curso", e);
        }
    }
    
    /**
     * Lista las sesiones por bloque.
     * 
     * @param cursoId ID del curso
     * @param bloqueId ID del bloque
     * @return Lista de sesiones del bloque ordenadas por fecha
     */
    public List<Sesion> buscarSesionesPorBloque(String cursoId, String bloqueId) {
        logger.debug("Buscando sesiones para curso: {} bloque: {}", cursoId, bloqueId);
        
        try {
            TypedQuery<Sesion> query = entityManager.createQuery(
                "SELECT s FROM Sesion s WHERE s.cursoId = :cursoId AND s.bloqueId = :bloqueId " +
                "ORDER BY s.fechaInicio DESC",
                Sesion.class
            );
            query.setParameter("cursoId", cursoId);
            query.setParameter("bloqueId", bloqueId);
            
            List<Sesion> sesiones = query.getResultList();
            logger.info("Encontradas {} sesiones para curso: {} bloque: {}", 
                      sesiones.size(), cursoId, bloqueId);
            return sesiones;
        } catch (Exception e) {
            logger.error("Error al buscar sesiones para curso: {} bloque: {}", cursoId, bloqueId, e);
            throw new RuntimeException("Error al buscar sesiones por bloque", e);
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
                "SELECT s FROM Sesion s WHERE s.fechaUltimaRevision < :fechaLimite " +
                "AND s.estado = :estado ORDER BY s.fechaUltimaRevision ASC",
                Sesion.class
            );
            query.setParameter("fechaLimite", fechaLimite);
            query.setParameter("estado", EstadoSesion.EN_CURSO);
            
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
     * Elimina todas las sesiones.
     * 
     * @return Número de sesiones eliminadas
     */
    public int eliminarTodasLasSesiones() {
        logger.debug("Eliminando todas las sesiones");
        
        try {
            Query query = entityManager.createQuery("DELETE FROM Sesion s");
            
            int eliminadas = query.executeUpdate();
            logger.info("Eliminadas {} sesiones", eliminadas);
            return eliminadas;
        } catch (Exception e) {
            logger.error("Error al eliminar todas las sesiones", e);
            throw new RuntimeException("Error al eliminar todas las sesiones", e);
        }
    }
    
    /**
     * Cuenta el número total de sesiones.
     * 
     * @return Número de sesiones
     */
    public long contarSesiones() {
        logger.debug("Contando total de sesiones");
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Sesion s", Long.class
            );
            
            long total = query.getSingleResult();
            logger.debug("Total de sesiones: {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Error al contar sesiones", e);
            throw new RuntimeException("Error al contar sesiones", e);
        }
    }
    
    /**
     * Cuenta el número de sesiones completadas.
     * 
     * @return Número de sesiones completadas
     */
    public long contarSesionesCompletadas() {
        logger.debug("Contando sesiones completadas");
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Sesion s WHERE s.estado = :estado", Long.class
            );
            query.setParameter("estado", EstadoSesion.COMPLETADA);
            
            long total = query.getSingleResult();
            logger.debug("Total de sesiones completadas: {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Error al contar sesiones completadas", e);
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
