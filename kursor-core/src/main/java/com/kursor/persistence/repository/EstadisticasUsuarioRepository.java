package com.kursor.persistence.repository;

import com.kursor.persistence.entity.EstadisticasUsuario;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad EstadisticasUsuario.
 * 
 * <p>Este repositorio proporciona métodos para gestionar las estadísticas
 * históricas de los usuarios, incluyendo análisis de progreso y rendimiento.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class EstadisticasUsuarioRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(EstadisticasUsuarioRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Constructor por defecto.
     */
    public EstadisticasUsuarioRepository() {
    }
    
    /**
     * Constructor con EntityManager.
     * 
     * @param entityManager EntityManager para operaciones JPA
     */
    public EstadisticasUsuarioRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Guarda estadísticas de usuario en la base de datos.
     * 
     * @param estadisticasUsuario Estadísticas de usuario a guardar
     * @return Estadísticas de usuario guardadas con ID generado
     */
    public EstadisticasUsuario guardar(EstadisticasUsuario estadisticasUsuario) {
        logger.debug("Guardando estadísticas de usuario - Usuario: {}, Curso: {}", 
                    estadisticasUsuario.getUsuarioId(), estadisticasUsuario.getCursoId());
        
        try {
            if (estadisticasUsuario.getId() == null) {
                entityManager.persist(estadisticasUsuario);
                logger.info("Estadísticas de usuario creadas exitosamente - ID: {}", estadisticasUsuario.getId());
            } else {
                estadisticasUsuario = entityManager.merge(estadisticasUsuario);
                logger.info("Estadísticas de usuario actualizadas exitosamente - ID: {}", estadisticasUsuario.getId());
            }
            return estadisticasUsuario;
        } catch (Exception e) {
            logger.error("Error al guardar estadísticas de usuario", e);
            throw new RuntimeException("Error al guardar estadísticas de usuario", e);
        }
    }
    
    /**
     * Busca estadísticas de usuario por su ID.
     * 
     * @param id ID de las estadísticas de usuario
     * @return Optional con las estadísticas de usuario si existen
     */
    public Optional<EstadisticasUsuario> buscarPorId(Long id) {
        logger.debug("Buscando estadísticas de usuario por ID: {}", id);
        
        try {
            EstadisticasUsuario estadisticasUsuario = entityManager.find(EstadisticasUsuario.class, id);
            if (estadisticasUsuario != null) {
                logger.debug("Estadísticas de usuario encontradas - ID: {}", id);
            } else {
                logger.debug("Estadísticas de usuario no encontradas - ID: {}", id);
            }
            return Optional.ofNullable(estadisticasUsuario);
        } catch (Exception e) {
            logger.error("Error al buscar estadísticas de usuario por ID: {}", id, e);
            throw new RuntimeException("Error al buscar estadísticas de usuario por ID", e);
        }
    }
    
    /**
     * Busca estadísticas de un usuario por curso.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso
     * @return Optional con las estadísticas si existen
     */
    public Optional<EstadisticasUsuario> buscarPorUsuarioYCurso(String usuarioId, String cursoId) {
        logger.debug("Buscando estadísticas para usuario: {} en curso: {}", usuarioId, cursoId);
        
        try {
            TypedQuery<EstadisticasUsuario> query = entityManager.createQuery(
                "SELECT eu FROM EstadisticasUsuario eu WHERE eu.usuarioId = :usuarioId AND eu.cursoId = :cursoId",
                EstadisticasUsuario.class
            );
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("cursoId", cursoId);
            
            List<EstadisticasUsuario> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                logger.info("Estadísticas encontradas para usuario: {} en curso: {} - ID: {}", 
                          usuarioId, cursoId, resultados.get(0).getId());
                return Optional.of(resultados.get(0));
            } else {
                logger.debug("No se encontraron estadísticas para usuario: {} en curso: {}", usuarioId, cursoId);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error al buscar estadísticas para usuario: {} en curso: {}", usuarioId, cursoId, e);
            throw new RuntimeException("Error al buscar estadísticas por usuario y curso", e);
        }
    }
    
    /**
     * Lista todas las estadísticas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de estadísticas del usuario ordenadas por fecha de última sesión
     */
    public List<EstadisticasUsuario> buscarPorUsuario(String usuarioId) {
        logger.debug("Buscando estadísticas para usuario: {}", usuarioId);
        
        try {
            TypedQuery<EstadisticasUsuario> query = entityManager.createQuery(
                "SELECT eu FROM EstadisticasUsuario eu WHERE eu.usuarioId = :usuarioId " +
                "ORDER BY eu.fechaUltimaSesion DESC",
                EstadisticasUsuario.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            List<EstadisticasUsuario> estadisticas = query.getResultList();
            logger.info("Encontradas {} estadísticas para usuario: {}", estadisticas.size(), usuarioId);
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al buscar estadísticas para usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al buscar estadísticas por usuario", e);
        }
    }
    
    /**
     * Lista todas las estadísticas de un curso.
     * 
     * @param cursoId ID del curso
     * @return Lista de estadísticas del curso ordenadas por tiempo total
     */
    public List<EstadisticasUsuario> buscarPorCurso(String cursoId) {
        logger.debug("Buscando estadísticas para curso: {}", cursoId);
        
        try {
            TypedQuery<EstadisticasUsuario> query = entityManager.createQuery(
                "SELECT eu FROM EstadisticasUsuario eu WHERE eu.cursoId = :cursoId " +
                "ORDER BY eu.tiempoTotal DESC",
                EstadisticasUsuario.class
            );
            query.setParameter("cursoId", cursoId);
            
            List<EstadisticasUsuario> estadisticas = query.getResultList();
            logger.info("Encontradas {} estadísticas para curso: {}", estadisticas.size(), cursoId);
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al buscar estadísticas para curso: {}", cursoId, e);
            throw new RuntimeException("Error al buscar estadísticas por curso", e);
        }
    }
    
    /**
     * Busca usuarios con mejor racha de días.
     * 
     * @param limite Número máximo de usuarios a retornar
     * @return Lista de estadísticas ordenadas por mejor racha
     */
    public List<EstadisticasUsuario> buscarMejoresRachas(int limite) {
        logger.debug("Buscando {} usuarios con mejor racha", limite);
        
        try {
            TypedQuery<EstadisticasUsuario> query = entityManager.createQuery(
                "SELECT eu FROM EstadisticasUsuario eu WHERE eu.mejorRachaDias > 0 " +
                "ORDER BY eu.mejorRachaDias DESC",
                EstadisticasUsuario.class
            );
            query.setMaxResults(limite);
            
            List<EstadisticasUsuario> estadisticas = query.getResultList();
            logger.info("Encontrados {} usuarios con mejor racha", estadisticas.size());
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al buscar mejores rachas", e);
            throw new RuntimeException("Error al buscar mejores rachas", e);
        }
    }
    
    /**
     * Busca usuarios más activos por tiempo total.
     * 
     * @param limite Número máximo de usuarios a retornar
     * @return Lista de estadísticas ordenadas por tiempo total
     */
    public List<EstadisticasUsuario> buscarUsuariosMasActivos(int limite) {
        logger.debug("Buscando {} usuarios más activos", limite);
        
        try {
            TypedQuery<EstadisticasUsuario> query = entityManager.createQuery(
                "SELECT eu FROM EstadisticasUsuario eu WHERE eu.tiempoTotal > 0 " +
                "ORDER BY eu.tiempoTotal DESC",
                EstadisticasUsuario.class
            );
            query.setMaxResults(limite);
            
            List<EstadisticasUsuario> estadisticas = query.getResultList();
            logger.info("Encontrados {} usuarios más activos", estadisticas.size());
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al buscar usuarios más activos", e);
            throw new RuntimeException("Error al buscar usuarios más activos", e);
        }
    }
    
    /**
     * Busca usuarios que han estado inactivos por más tiempo.
     * 
     * @param diasInactividad Número de días de inactividad
     * @return Lista de estadísticas de usuarios inactivos
     */
    public List<EstadisticasUsuario> buscarUsuariosInactivos(int diasInactividad) {
        logger.debug("Buscando usuarios inactivos por más de {} días", diasInactividad);
        
        try {
            LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasInactividad);
            
            TypedQuery<EstadisticasUsuario> query = entityManager.createQuery(
                "SELECT eu FROM EstadisticasUsuario eu WHERE eu.fechaUltimaSesion < :fechaLimite " +
                "ORDER BY eu.fechaUltimaSesion ASC",
                EstadisticasUsuario.class
            );
            query.setParameter("fechaLimite", fechaLimite);
            
            List<EstadisticasUsuario> estadisticas = query.getResultList();
            logger.info("Encontrados {} usuarios inactivos por más de {} días", 
                      estadisticas.size(), diasInactividad);
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al buscar usuarios inactivos por más de {} días", diasInactividad, e);
            throw new RuntimeException("Error al buscar usuarios inactivos", e);
        }
    }
    
    /**
     * Actualiza el tiempo total de un usuario.
     * 
     * @param id ID de las estadísticas
     * @param tiempoAdicional Tiempo adicional a agregar (en segundos)
     * @return true si se actualizó correctamente, false si no existía
     */
    public boolean agregarTiempo(Long id, Integer tiempoAdicional) {
        logger.debug("Agregando {} segundos a estadísticas - ID: {}", tiempoAdicional, id);
        
        try {
            Query query = entityManager.createQuery(
                "UPDATE EstadisticasUsuario eu SET eu.tiempoTotal = eu.tiempoTotal + :tiempoAdicional, " +
                "eu.updatedAt = :fechaActualizacion WHERE eu.id = :id"
            );
            query.setParameter("tiempoAdicional", tiempoAdicional);
            query.setParameter("fechaActualizacion", LocalDateTime.now());
            query.setParameter("id", id);
            
            int actualizadas = query.executeUpdate();
            if (actualizadas > 0) {
                logger.info("Tiempo agregado exitosamente - ID: {}, Tiempo adicional: {}", id, tiempoAdicional);
                return true;
            } else {
                logger.warn("No se encontraron estadísticas para agregar tiempo - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al agregar tiempo a estadísticas - ID: {}", id, e);
            throw new RuntimeException("Error al agregar tiempo", e);
        }
    }
    
    /**
     * Registra una sesión completada.
     * 
     * @param id ID de las estadísticas
     * @return true si se actualizó correctamente, false si no existía
     */
    public boolean registrarSesionCompletada(Long id) {
        logger.debug("Registrando sesión completada - ID: {}", id);
        
        try {
            Query query = entityManager.createQuery(
                "UPDATE EstadisticasUsuario eu SET eu.sesionesCompletadas = eu.sesionesCompletadas + 1, " +
                "eu.updatedAt = :fechaActualizacion WHERE eu.id = :id"
            );
            query.setParameter("fechaActualizacion", LocalDateTime.now());
            query.setParameter("id", id);
            
            int actualizadas = query.executeUpdate();
            if (actualizadas > 0) {
                logger.info("Sesión completada registrada exitosamente - ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontraron estadísticas para registrar sesión completada - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al registrar sesión completada - ID: {}", id, e);
            throw new RuntimeException("Error al registrar sesión completada", e);
        }
    }
    
    /**
     * Actualiza la racha de días de un usuario.
     * 
     * @param id ID de las estadísticas
     * @param rachaActual Nueva racha actual
     * @param mejorRacha Nueva mejor racha (si aplica)
     * @return true si se actualizó correctamente, false si no existía
     */
    public boolean actualizarRacha(Long id, Integer rachaActual, Integer mejorRacha) {
        logger.debug("Actualizando racha - ID: {}, Racha actual: {}, Mejor racha: {}", id, rachaActual, mejorRacha);
        
        try {
            Query query = entityManager.createQuery(
                "UPDATE EstadisticasUsuario eu SET eu.rachaActualDias = :rachaActual, " +
                "eu.mejorRachaDias = :mejorRacha, eu.updatedAt = :fechaActualizacion WHERE eu.id = :id"
            );
            query.setParameter("rachaActual", rachaActual);
            query.setParameter("mejorRacha", mejorRacha);
            query.setParameter("fechaActualizacion", LocalDateTime.now());
            query.setParameter("id", id);
            
            int actualizadas = query.executeUpdate();
            if (actualizadas > 0) {
                logger.info("Racha actualizada exitosamente - ID: {}, Racha actual: {}, Mejor racha: {}", 
                          id, rachaActual, mejorRacha);
                return true;
            } else {
                logger.warn("No se encontraron estadísticas para actualizar racha - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al actualizar racha - ID: {}", id, e);
            throw new RuntimeException("Error al actualizar racha", e);
        }
    }
    
    /**
     * Actualiza la fecha de última sesión.
     * 
     * @param id ID de las estadísticas
     * @param fechaUltimaSesion Nueva fecha de última sesión
     * @return true si se actualizó correctamente, false si no existía
     */
    public boolean actualizarFechaUltimaSesion(Long id, LocalDateTime fechaUltimaSesion) {
        logger.debug("Actualizando fecha de última sesión - ID: {}, Fecha: {}", id, fechaUltimaSesion);
        
        try {
            Query query = entityManager.createQuery(
                "UPDATE EstadisticasUsuario eu SET eu.fechaUltimaSesion = :fechaUltimaSesion, " +
                "eu.updatedAt = :fechaActualizacion WHERE eu.id = :id"
            );
            query.setParameter("fechaUltimaSesion", fechaUltimaSesion);
            query.setParameter("fechaActualizacion", LocalDateTime.now());
            query.setParameter("id", id);
            
            int actualizadas = query.executeUpdate();
            if (actualizadas > 0) {
                logger.info("Fecha de última sesión actualizada exitosamente - ID: {}, Fecha: {}", 
                          id, fechaUltimaSesion);
                return true;
            } else {
                logger.warn("No se encontraron estadísticas para actualizar fecha de última sesión - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al actualizar fecha de última sesión - ID: {}", id, e);
            throw new RuntimeException("Error al actualizar fecha de última sesión", e);
        }
    }
    
    /**
     * Elimina estadísticas por su ID.
     * 
     * @param id ID de las estadísticas a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminar(Long id) {
        logger.debug("Eliminando estadísticas de usuario con ID: {}", id);
        
        try {
            EstadisticasUsuario estadisticasUsuario = entityManager.find(EstadisticasUsuario.class, id);
            if (estadisticasUsuario != null) {
                entityManager.remove(estadisticasUsuario);
                logger.info("Estadísticas de usuario eliminadas exitosamente - ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontraron estadísticas de usuario para eliminar - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al eliminar estadísticas de usuario con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar estadísticas de usuario", e);
        }
    }
    
    /**
     * Elimina todas las estadísticas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de estadísticas eliminadas
     */
    public int eliminarPorUsuario(String usuarioId) {
        logger.debug("Eliminando todas las estadísticas del usuario: {}", usuarioId);
        
        try {
            Query query = entityManager.createQuery(
                "DELETE FROM EstadisticasUsuario eu WHERE eu.usuarioId = :usuarioId"
            );
            query.setParameter("usuarioId", usuarioId);
            
            int eliminadas = query.executeUpdate();
            logger.info("Eliminadas {} estadísticas del usuario: {}", eliminadas, usuarioId);
            return eliminadas;
        } catch (Exception e) {
            logger.error("Error al eliminar estadísticas del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al eliminar estadísticas de usuario", e);
        }
    }
    
    /**
     * Elimina todas las estadísticas de un curso.
     * 
     * @param cursoId ID del curso
     * @return Número de estadísticas eliminadas
     */
    public int eliminarPorCurso(String cursoId) {
        logger.debug("Eliminando todas las estadísticas del curso: {}", cursoId);
        
        try {
            Query query = entityManager.createQuery(
                "DELETE FROM EstadisticasUsuario eu WHERE eu.cursoId = :cursoId"
            );
            query.setParameter("cursoId", cursoId);
            
            int eliminadas = query.executeUpdate();
            logger.info("Eliminadas {} estadísticas del curso: {}", eliminadas, cursoId);
            return eliminadas;
        } catch (Exception e) {
            logger.error("Error al eliminar estadísticas del curso: {}", cursoId, e);
            throw new RuntimeException("Error al eliminar estadísticas de curso", e);
        }
    }
    
    /**
     * Cuenta el número de estadísticas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de estadísticas
     */
    public long contarPorUsuario(String usuarioId) {
        logger.debug("Contando estadísticas del usuario: {}", usuarioId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(eu) FROM EstadisticasUsuario eu WHERE eu.usuarioId = :usuarioId",
                Long.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            long count = query.getSingleResult();
            logger.debug("Usuario {} tiene {} estadísticas", usuarioId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar estadísticas del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al contar estadísticas de usuario", e);
        }
    }
    
    /**
     * Cuenta el número de estadísticas de un curso.
     * 
     * @param cursoId ID del curso
     * @return Número de estadísticas
     */
    public long contarPorCurso(String cursoId) {
        logger.debug("Contando estadísticas del curso: {}", cursoId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(eu) FROM EstadisticasUsuario eu WHERE eu.cursoId = :cursoId",
                Long.class
            );
            query.setParameter("cursoId", cursoId);
            
            long count = query.getSingleResult();
            logger.debug("Curso {} tiene {} estadísticas", cursoId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar estadísticas del curso: {}", cursoId, e);
            throw new RuntimeException("Error al contar estadísticas de curso", e);
        }
    }
    
    /**
     * Calcula el tiempo total promedio de un curso.
     * 
     * @param cursoId ID del curso
     * @return Tiempo promedio en segundos
     */
    public double calcularTiempoPromedioCurso(String cursoId) {
        logger.debug("Calculando tiempo promedio del curso: {}", cursoId);
        
        try {
            TypedQuery<Double> query = entityManager.createQuery(
                "SELECT AVG(eu.tiempoTotal) FROM EstadisticasUsuario eu WHERE eu.cursoId = :cursoId",
                Double.class
            );
            query.setParameter("cursoId", cursoId);
            
            Double promedio = query.getSingleResult();
            if (promedio == null) {
                logger.debug("Curso {} no tiene estadísticas", cursoId);
                return 0.0;
            }
            
            logger.info("Curso {} tiene tiempo promedio de {} segundos", cursoId, promedio);
            return promedio;
        } catch (Exception e) {
            logger.error("Error al calcular tiempo promedio del curso: {}", cursoId, e);
            throw new RuntimeException("Error al calcular tiempo promedio", e);
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
