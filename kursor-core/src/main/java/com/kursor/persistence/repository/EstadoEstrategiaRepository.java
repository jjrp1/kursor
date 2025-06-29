package com.kursor.persistence.repository;

import com.kursor.persistence.entity.EstadoEstrategia;
import com.kursor.persistence.entity.Sesion;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad EstadoEstrategia.
 * 
 * <p>Este repositorio proporciona métodos para gestionar el estado interno
 * de las estrategias de aprendizaje, incluyendo la serialización/deserialización
 * de datos JSON.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class EstadoEstrategiaRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(EstadoEstrategiaRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Constructor por defecto.
     */
    public EstadoEstrategiaRepository() {
    }
    
    /**
     * Constructor con EntityManager.
     * 
     * @param entityManager EntityManager para operaciones JPA
     */
    public EstadoEstrategiaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Guarda un estado de estrategia en la base de datos.
     * 
     * @param estadoEstrategia Estado de estrategia a guardar
     * @return Estado de estrategia guardado con ID generado
     */
    public EstadoEstrategia guardar(EstadoEstrategia estadoEstrategia) {
        logger.debug("Guardando estado de estrategia - Sesión: {}, Tipo: {}", 
                    estadoEstrategia.getSesion().getId(), estadoEstrategia.getTipoEstrategia());
        
        try {
            if (estadoEstrategia.getId() == null) {
                entityManager.persist(estadoEstrategia);
                logger.info("Estado de estrategia creado exitosamente - ID: {}", estadoEstrategia.getId());
            } else {
                estadoEstrategia = entityManager.merge(estadoEstrategia);
                logger.info("Estado de estrategia actualizado exitosamente - ID: {}", estadoEstrategia.getId());
            }
            return estadoEstrategia;
        } catch (Exception e) {
            logger.error("Error al guardar estado de estrategia", e);
            throw new RuntimeException("Error al guardar estado de estrategia", e);
        }
    }
    
    /**
     * Busca un estado de estrategia por su ID.
     * 
     * @param id ID del estado de estrategia
     * @return Optional con el estado de estrategia si existe
     */
    public Optional<EstadoEstrategia> buscarPorId(Long id) {
        logger.debug("Buscando estado de estrategia por ID: {}", id);
        
        try {
            EstadoEstrategia estadoEstrategia = entityManager.find(EstadoEstrategia.class, id);
            if (estadoEstrategia != null) {
                logger.debug("Estado de estrategia encontrado - ID: {}", id);
            } else {
                logger.debug("Estado de estrategia no encontrado - ID: {}", id);
            }
            return Optional.ofNullable(estadoEstrategia);
        } catch (Exception e) {
            logger.error("Error al buscar estado de estrategia por ID: {}", id, e);
            throw new RuntimeException("Error al buscar estado de estrategia por ID", e);
        }
    }
    
    /**
     * Busca el estado de estrategia de una sesión.
     * 
     * @param sesionId ID de la sesión
     * @return Optional con el estado de estrategia si existe
     */
    public Optional<EstadoEstrategia> buscarPorSesion(Long sesionId) {
        logger.debug("Buscando estado de estrategia para sesión: {}", sesionId);
        
        try {
            TypedQuery<EstadoEstrategia> query = entityManager.createQuery(
                "SELECT ee FROM EstadoEstrategia ee WHERE ee.sesion.id = :sesionId",
                EstadoEstrategia.class
            );
            query.setParameter("sesionId", sesionId);
            
            List<EstadoEstrategia> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                logger.info("Estado de estrategia encontrado para sesión: {} - ID: {}", 
                          sesionId, resultados.get(0).getId());
                return Optional.of(resultados.get(0));
            } else {
                logger.debug("No se encontró estado de estrategia para sesión: {}", sesionId);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error al buscar estado de estrategia para sesión: {}", sesionId, e);
            throw new RuntimeException("Error al buscar estado de estrategia por sesión", e);
        }
    }
    
    /**
     * Busca estados de estrategia por tipo.
     * 
     * @param tipoEstrategia Tipo de estrategia
     * @return Lista de estados de estrategia del tipo especificado
     */
    public List<EstadoEstrategia> buscarPorTipo(String tipoEstrategia) {
        logger.debug("Buscando estados de estrategia por tipo: {}", tipoEstrategia);
        
        try {
            TypedQuery<EstadoEstrategia> query = entityManager.createQuery(
                "SELECT ee FROM EstadoEstrategia ee WHERE ee.tipoEstrategia = :tipoEstrategia " +
                "ORDER BY ee.fechaUltimaModificacion DESC",
                EstadoEstrategia.class
            );
            query.setParameter("tipoEstrategia", tipoEstrategia);
            
            List<EstadoEstrategia> estados = query.getResultList();
            logger.info("Encontrados {} estados de estrategia de tipo: {}", estados.size(), tipoEstrategia);
            return estados;
        } catch (Exception e) {
            logger.error("Error al buscar estados de estrategia por tipo: {}", tipoEstrategia, e);
            throw new RuntimeException("Error al buscar estados de estrategia por tipo", e);
        }
    }
    
    /**
     * Busca estados de estrategia de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de estados de estrategia del usuario
     */
    public List<EstadoEstrategia> buscarPorUsuario(String usuarioId) {
        logger.debug("Buscando estados de estrategia para usuario: {}", usuarioId);
        
        try {
            TypedQuery<EstadoEstrategia> query = entityManager.createQuery(
                "SELECT ee FROM EstadoEstrategia ee JOIN ee.sesion s " +
                "WHERE s.usuarioId = :usuarioId ORDER BY ee.fechaUltimaModificacion DESC",
                EstadoEstrategia.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            List<EstadoEstrategia> estados = query.getResultList();
            logger.info("Encontrados {} estados de estrategia para usuario: {}", estados.size(), usuarioId);
            return estados;
        } catch (Exception e) {
            logger.error("Error al buscar estados de estrategia para usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al buscar estados de estrategia por usuario", e);
        }
    }
    
    /**
     * Busca estados de estrategia de un usuario por curso.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso
     * @return Lista de estados de estrategia del usuario en el curso
     */
    public List<EstadoEstrategia> buscarPorUsuarioYCurso(String usuarioId, String cursoId) {
        logger.debug("Buscando estados de estrategia para usuario: {} en curso: {}", usuarioId, cursoId);
        
        try {
            TypedQuery<EstadoEstrategia> query = entityManager.createQuery(
                "SELECT ee FROM EstadoEstrategia ee JOIN ee.sesion s " +
                "WHERE s.usuarioId = :usuarioId AND s.cursoId = :cursoId " +
                "ORDER BY ee.fechaUltimaModificacion DESC",
                EstadoEstrategia.class
            );
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("cursoId", cursoId);
            
            List<EstadoEstrategia> estados = query.getResultList();
            logger.info("Encontrados {} estados de estrategia para usuario: {} en curso: {}", 
                      estados.size(), usuarioId, cursoId);
            return estados;
        } catch (Exception e) {
            logger.error("Error al buscar estados de estrategia para usuario: {} en curso: {}", 
                        usuarioId, cursoId, e);
            throw new RuntimeException("Error al buscar estados de estrategia por usuario y curso", e);
        }
    }
    
    /**
     * Busca estados de estrategia modificados después de una fecha.
     * 
     * @param fechaLimite Fecha límite
     * @return Lista de estados de estrategia modificados después de la fecha
     */
    public List<EstadoEstrategia> buscarModificadosDespues(LocalDateTime fechaLimite) {
        logger.debug("Buscando estados de estrategia modificados después de: {}", fechaLimite);
        
        try {
            TypedQuery<EstadoEstrategia> query = entityManager.createQuery(
                "SELECT ee FROM EstadoEstrategia ee WHERE ee.fechaUltimaModificacion > :fechaLimite " +
                "ORDER BY ee.fechaUltimaModificacion DESC",
                EstadoEstrategia.class
            );
            query.setParameter("fechaLimite", fechaLimite);
            
            List<EstadoEstrategia> estados = query.getResultList();
            logger.info("Encontrados {} estados de estrategia modificados después de: {}", 
                      estados.size(), fechaLimite);
            return estados;
        } catch (Exception e) {
            logger.error("Error al buscar estados de estrategia modificados después de: {}", fechaLimite, e);
            throw new RuntimeException("Error al buscar estados de estrategia modificados", e);
        }
    }
    
    /**
     * Actualiza el progreso de un estado de estrategia.
     * 
     * @param id ID del estado de estrategia
     * @param progreso Nuevo valor de progreso
     * @return true si se actualizó correctamente, false si no existía
     */
    public boolean actualizarProgreso(Long id, Double progreso) {
        logger.debug("Actualizando progreso de estado de estrategia - ID: {}, Progreso: {}", id, progreso);
        
        try {
            Query query = entityManager.createQuery(
                "UPDATE EstadoEstrategia ee SET ee.progreso = :progreso, " +
                "ee.fechaUltimaModificacion = :fechaModificacion WHERE ee.id = :id"
            );
            query.setParameter("progreso", progreso);
            query.setParameter("fechaModificacion", LocalDateTime.now());
            query.setParameter("id", id);
            
            int actualizadas = query.executeUpdate();
            if (actualizadas > 0) {
                logger.info("Progreso actualizado exitosamente - ID: {}, Progreso: {}", id, progreso);
                return true;
            } else {
                logger.warn("No se encontró estado de estrategia para actualizar progreso - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al actualizar progreso de estado de estrategia - ID: {}", id, e);
            throw new RuntimeException("Error al actualizar progreso", e);
        }
    }
    
    /**
     * Actualiza los datos de estado de una estrategia.
     * 
     * @param id ID del estado de estrategia
     * @param datosEstado Nuevos datos de estado (JSON serializado)
     * @return true si se actualizó correctamente, false si no existía
     */
    public boolean actualizarDatosEstado(Long id, String datosEstado) {
        logger.debug("Actualizando datos de estado de estrategia - ID: {}", id);
        
        try {
            Query query = entityManager.createQuery(
                "UPDATE EstadoEstrategia ee SET ee.datosEstado = :datosEstado, " +
                "ee.fechaUltimaModificacion = :fechaModificacion WHERE ee.id = :id"
            );
            query.setParameter("datosEstado", datosEstado);
            query.setParameter("fechaModificacion", LocalDateTime.now());
            query.setParameter("id", id);
            
            int actualizadas = query.executeUpdate();
            if (actualizadas > 0) {
                logger.info("Datos de estado actualizados exitosamente - ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontró estado de estrategia para actualizar datos - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al actualizar datos de estado de estrategia - ID: {}", id, e);
            throw new RuntimeException("Error al actualizar datos de estado", e);
        }
    }
    
    /**
     * Elimina un estado de estrategia por su ID.
     * 
     * @param id ID del estado de estrategia a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminar(Long id) {
        logger.debug("Eliminando estado de estrategia con ID: {}", id);
        
        try {
            EstadoEstrategia estadoEstrategia = entityManager.find(EstadoEstrategia.class, id);
            if (estadoEstrategia != null) {
                entityManager.remove(estadoEstrategia);
                logger.info("Estado de estrategia eliminado exitosamente - ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontró estado de estrategia para eliminar - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al eliminar estado de estrategia con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar estado de estrategia", e);
        }
    }
    
    /**
     * Elimina todos los estados de estrategia de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de estados de estrategia eliminados
     */
    public int eliminarPorUsuario(String usuarioId) {
        logger.debug("Eliminando todos los estados de estrategia del usuario: {}", usuarioId);
        
        try {
            Query query = entityManager.createQuery(
                "DELETE FROM EstadoEstrategia ee WHERE ee.sesion.usuarioId = :usuarioId"
            );
            query.setParameter("usuarioId", usuarioId);
            
            int eliminados = query.executeUpdate();
            logger.info("Eliminados {} estados de estrategia del usuario: {}", eliminados, usuarioId);
            return eliminados;
        } catch (Exception e) {
            logger.error("Error al eliminar estados de estrategia del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al eliminar estados de estrategia de usuario", e);
        }
    }
    
    /**
     * Cuenta el número de estados de estrategia de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de estados de estrategia
     */
    public long contarPorUsuario(String usuarioId) {
        logger.debug("Contando estados de estrategia del usuario: {}", usuarioId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ee) FROM EstadoEstrategia ee JOIN ee.sesion s WHERE s.usuarioId = :usuarioId",
                Long.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            long count = query.getSingleResult();
            logger.debug("Usuario {} tiene {} estados de estrategia", usuarioId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar estados de estrategia del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al contar estados de estrategia de usuario", e);
        }
    }
    
    /**
     * Cuenta el número de estados de estrategia por tipo.
     * 
     * @param tipoEstrategia Tipo de estrategia
     * @return Número de estados de estrategia del tipo especificado
     */
    public long contarPorTipo(String tipoEstrategia) {
        logger.debug("Contando estados de estrategia por tipo: {}", tipoEstrategia);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ee) FROM EstadoEstrategia ee WHERE ee.tipoEstrategia = :tipoEstrategia",
                Long.class
            );
            query.setParameter("tipoEstrategia", tipoEstrategia);
            
            long count = query.getSingleResult();
            logger.debug("Tipo {} tiene {} estados de estrategia", tipoEstrategia, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar estados de estrategia por tipo: {}", tipoEstrategia, e);
            throw new RuntimeException("Error al contar estados de estrategia por tipo", e);
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
