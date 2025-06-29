package com.kursor.persistence.repository;

import com.kursor.persistence.entity.PreguntaSesion;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad PreguntaSesion.
 * 
 * <p>Este repositorio proporciona métodos para gestionar las preguntas de sesión,
 * incluyendo operaciones CRUD básicas y consultas específicas del dominio.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class PreguntaSesionRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PreguntaSesionRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Constructor por defecto.
     */
    public PreguntaSesionRepository() {
    }
    
    /**
     * Constructor con EntityManager.
     * 
     * @param entityManager EntityManager para operaciones JPA
     */
    public PreguntaSesionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Guarda una pregunta de sesión en la base de datos.
     * 
     * @param preguntaSesion Pregunta de sesión a guardar
     * @return Pregunta de sesión guardada con ID generado
     */
    public PreguntaSesion guardar(PreguntaSesion preguntaSesion) {
        logger.debug("Guardando pregunta de sesión - Sesión: {}, Pregunta: {}", 
                    preguntaSesion.getSesion().getId(), preguntaSesion.getPreguntaId());
        
        try {
            if (preguntaSesion.getId() == null) {
                entityManager.persist(preguntaSesion);
                logger.info("Pregunta de sesión creada exitosamente - ID: {}", preguntaSesion.getId());
            } else {
                preguntaSesion = entityManager.merge(preguntaSesion);
                logger.info("Pregunta de sesión actualizada exitosamente - ID: {}", preguntaSesion.getId());
            }
            return preguntaSesion;
        } catch (Exception e) {
            logger.error("Error al guardar pregunta de sesión", e);
            throw new RuntimeException("Error al guardar pregunta de sesión", e);
        }
    }
    
    /**
     * Busca una pregunta de sesión por su ID.
     * 
     * @param id ID de la pregunta de sesión
     * @return Optional con la pregunta de sesión si existe
     */
    public Optional<PreguntaSesion> buscarPorId(Long id) {
        logger.debug("Buscando pregunta de sesión por ID: {}", id);
        
        try {
            PreguntaSesion preguntaSesion = entityManager.find(PreguntaSesion.class, id);
            if (preguntaSesion != null) {
                logger.debug("Pregunta de sesión encontrada - ID: {}", id);
            } else {
                logger.debug("Pregunta de sesión no encontrada - ID: {}", id);
            }
            return Optional.ofNullable(preguntaSesion);
        } catch (Exception e) {
            logger.error("Error al buscar pregunta de sesión por ID: {}", id, e);
            throw new RuntimeException("Error al buscar pregunta de sesión por ID", e);
        }
    }
    
    /**
     * Busca preguntas de una sesión.
     * 
     * @param sesionId ID de la sesión
     * @return Lista de preguntas de la sesión ordenadas por fecha de creación
     */
    public List<PreguntaSesion> buscarPorSesion(Long sesionId) {
        logger.debug("Buscando preguntas de sesión para sesión: {}", sesionId);
        
        try {
            TypedQuery<PreguntaSesion> query = entityManager.createQuery(
                "SELECT ps FROM PreguntaSesion ps WHERE ps.sesion.id = :sesionId " +
                "ORDER BY ps.createdAt ASC",
                PreguntaSesion.class
            );
            query.setParameter("sesionId", sesionId);
            
            List<PreguntaSesion> preguntas = query.getResultList();
            logger.info("Encontradas {} preguntas para sesión: {}", preguntas.size(), sesionId);
            return preguntas;
        } catch (Exception e) {
            logger.error("Error al buscar preguntas de sesión para sesión: {}", sesionId, e);
            throw new RuntimeException("Error al buscar preguntas por sesión", e);
        }
    }
    
    /**
     * Busca una pregunta específica en una sesión.
     * 
     * @param sesionId ID de la sesión
     * @param preguntaId ID de la pregunta
     * @return Optional con la pregunta de sesión si existe
     */
    public Optional<PreguntaSesion> buscarPorSesionYPregunta(Long sesionId, String preguntaId) {
        logger.debug("Buscando pregunta de sesión - Sesión: {}, Pregunta: {}", sesionId, preguntaId);
        
        try {
            TypedQuery<PreguntaSesion> query = entityManager.createQuery(
                "SELECT ps FROM PreguntaSesion ps WHERE ps.sesion.id = :sesionId " +
                "AND ps.preguntaId = :preguntaId",
                PreguntaSesion.class
            );
            query.setParameter("sesionId", sesionId);
            query.setParameter("preguntaId", preguntaId);
            
            List<PreguntaSesion> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                logger.debug("Pregunta de sesión encontrada - Sesión: {}, Pregunta: {}", sesionId, preguntaId);
                return Optional.of(resultados.get(0));
            } else {
                logger.debug("Pregunta de sesión no encontrada - Sesión: {}, Pregunta: {}", sesionId, preguntaId);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error al buscar pregunta de sesión - Sesión: {}, Pregunta: {}", sesionId, preguntaId, e);
            throw new RuntimeException("Error al buscar pregunta por sesión y pregunta", e);
        }
    }
    
    /**
     * Busca preguntas por resultado.
     * 
     * @param sesionId ID de la sesión
     * @param resultado Resultado a buscar (acierto, fallo, sin contestar)
     * @return Lista de preguntas con el resultado especificado
     */
    public List<PreguntaSesion> buscarPorResultado(Long sesionId, String resultado) {
        logger.debug("Buscando preguntas de sesión con resultado: {} para sesión: {}", resultado, sesionId);
        
        try {
            TypedQuery<PreguntaSesion> query = entityManager.createQuery(
                "SELECT ps FROM PreguntaSesion ps WHERE ps.sesion.id = :sesionId " +
                "AND ps.resultado = :resultado ORDER BY ps.createdAt ASC",
                PreguntaSesion.class
            );
            query.setParameter("sesionId", sesionId);
            query.setParameter("resultado", resultado);
            
            List<PreguntaSesion> preguntas = query.getResultList();
            logger.info("Encontradas {} preguntas con resultado '{}' para sesión: {}", 
                      preguntas.size(), resultado, sesionId);
            return preguntas;
        } catch (Exception e) {
            logger.error("Error al buscar preguntas con resultado: {} para sesión: {}", resultado, sesionId, e);
            throw new RuntimeException("Error al buscar preguntas por resultado", e);
        }
    }
    
    /**
     * Cuenta el número de preguntas de una sesión.
     * 
     * @param sesionId ID de la sesión
     * @return Número de preguntas
     */
    public long contarPorSesion(Long sesionId) {
        logger.debug("Contando preguntas de sesión para sesión: {}", sesionId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ps) FROM PreguntaSesion ps WHERE ps.sesion.id = :sesionId",
                Long.class
            );
            query.setParameter("sesionId", sesionId);
            
            long total = query.getSingleResult();
            logger.debug("Total de preguntas para sesión {}: {}", sesionId, total);
            return total;
        } catch (Exception e) {
            logger.error("Error al contar preguntas de sesión para sesión: {}", sesionId, e);
            throw new RuntimeException("Error al contar preguntas por sesión", e);
        }
    }
    
    /**
     * Cuenta el número de preguntas con un resultado específico.
     * 
     * @param sesionId ID de la sesión
     * @param resultado Resultado a contar
     * @return Número de preguntas con el resultado especificado
     */
    public long contarPorResultado(Long sesionId, String resultado) {
        logger.debug("Contando preguntas con resultado '{}' para sesión: {}", resultado, sesionId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ps) FROM PreguntaSesion ps WHERE ps.sesion.id = :sesionId " +
                "AND ps.resultado = :resultado",
                Long.class
            );
            query.setParameter("sesionId", sesionId);
            query.setParameter("resultado", resultado);
            
            long total = query.getSingleResult();
            logger.debug("Total de preguntas con resultado '{}' para sesión {}: {}", resultado, sesionId, total);
            return total;
        } catch (Exception e) {
            logger.error("Error al contar preguntas con resultado '{}' para sesión: {}", resultado, sesionId, e);
            throw new RuntimeException("Error al contar preguntas por resultado", e);
        }
    }
    
    /**
     * Elimina una pregunta de sesión por su ID.
     * 
     * @param id ID de la pregunta de sesión a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminar(Long id) {
        logger.debug("Eliminando pregunta de sesión con ID: {}", id);
        
        try {
            PreguntaSesion preguntaSesion = entityManager.find(PreguntaSesion.class, id);
            if (preguntaSesion != null) {
                entityManager.remove(preguntaSesion);
                logger.info("Pregunta de sesión eliminada exitosamente - ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontró pregunta de sesión para eliminar - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al eliminar pregunta de sesión con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar pregunta de sesión", e);
        }
    }
    
    /**
     * Elimina todas las preguntas de una sesión.
     * 
     * @param sesionId ID de la sesión
     * @return Número de preguntas eliminadas
     */
    public int eliminarPorSesion(Long sesionId) {
        logger.debug("Eliminando todas las preguntas de la sesión: {}", sesionId);
        
        try {
            Query query = entityManager.createQuery(
                "DELETE FROM PreguntaSesion ps WHERE ps.sesion.id = :sesionId"
            );
            query.setParameter("sesionId", sesionId);
            
            int eliminadas = query.executeUpdate();
            logger.info("Eliminadas {} preguntas de la sesión: {}", eliminadas, sesionId);
            return eliminadas;
        } catch (Exception e) {
            logger.error("Error al eliminar preguntas de la sesión: {}", sesionId, e);
            throw new RuntimeException("Error al eliminar preguntas de sesión", e);
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