package com.kursor.persistence.repository;

import com.kursor.persistence.entity.RespuestaPregunta;
import com.kursor.persistence.entity.Sesion;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad RespuestaPregunta.
 * 
 * <p>Este repositorio proporciona métodos para gestionar el historial de respuestas
 * de los usuarios, incluyendo estadísticas y análisis de rendimiento.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class RespuestaPreguntaRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(RespuestaPreguntaRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Constructor por defecto.
     */
    public RespuestaPreguntaRepository() {
    }
    
    /**
     * Constructor con EntityManager.
     * 
     * @param entityManager EntityManager para operaciones JPA
     */
    public RespuestaPreguntaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Guarda una respuesta de pregunta en la base de datos.
     * 
     * @param respuestaPregunta Respuesta de pregunta a guardar
     * @return Respuesta de pregunta guardada con ID generado
     */
    public RespuestaPregunta guardar(RespuestaPregunta respuestaPregunta) {
        logger.debug("Guardando respuesta de pregunta - Sesión: {}, Pregunta: {}", 
                    respuestaPregunta.getSesion().getId(), respuestaPregunta.getPreguntaId());
        
        try {
            if (respuestaPregunta.getId() == null) {
                entityManager.persist(respuestaPregunta);
                logger.info("Respuesta de pregunta creada exitosamente - ID: {}", respuestaPregunta.getId());
            } else {
                respuestaPregunta = entityManager.merge(respuestaPregunta);
                logger.info("Respuesta de pregunta actualizada exitosamente - ID: {}", respuestaPregunta.getId());
            }
            return respuestaPregunta;
        } catch (Exception e) {
            logger.error("Error al guardar respuesta de pregunta", e);
            throw new RuntimeException("Error al guardar respuesta de pregunta", e);
        }
    }
    
    /**
     * Busca una respuesta de pregunta por su ID.
     * 
     * @param id ID de la respuesta de pregunta
     * @return Optional con la respuesta de pregunta si existe
     */
    public Optional<RespuestaPregunta> buscarPorId(Long id) {
        logger.debug("Buscando respuesta de pregunta por ID: {}", id);
        
        try {
            RespuestaPregunta respuestaPregunta = entityManager.find(RespuestaPregunta.class, id);
            if (respuestaPregunta != null) {
                logger.debug("Respuesta de pregunta encontrada - ID: {}", id);
            } else {
                logger.debug("Respuesta de pregunta no encontrada - ID: {}", id);
            }
            return Optional.ofNullable(respuestaPregunta);
        } catch (Exception e) {
            logger.error("Error al buscar respuesta de pregunta por ID: {}", id, e);
            throw new RuntimeException("Error al buscar respuesta de pregunta por ID", e);
        }
    }
    
    /**
     * Busca respuestas de una sesión.
     * 
     * @param sesionId ID de la sesión
     * @return Lista de respuestas de la sesión ordenadas por fecha
     */
    public List<RespuestaPregunta> buscarPorSesion(Long sesionId) {
        logger.debug("Buscando respuestas de pregunta para sesión: {}", sesionId);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp WHERE rp.sesion.id = :sesionId " +
                "ORDER BY rp.fechaRespuesta ASC",
                RespuestaPregunta.class
            );
            query.setParameter("sesionId", sesionId);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas para sesión: {}", respuestas.size(), sesionId);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas de pregunta para sesión: {}", sesionId, e);
            throw new RuntimeException("Error al buscar respuestas por sesión", e);
        }
    }
    
    /**
     * Busca respuestas de una pregunta específica.
     * 
     * @param preguntaId ID de la pregunta
     * @return Lista de respuestas de la pregunta ordenadas por fecha
     */
    public List<RespuestaPregunta> buscarPorPregunta(String preguntaId) {
        logger.debug("Buscando respuestas de pregunta: {}", preguntaId);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp WHERE rp.preguntaId = :preguntaId " +
                "ORDER BY rp.fechaRespuesta DESC",
                RespuestaPregunta.class
            );
            query.setParameter("preguntaId", preguntaId);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas para pregunta: {}", respuestas.size(), preguntaId);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas de pregunta: {}", preguntaId, e);
            throw new RuntimeException("Error al buscar respuestas por pregunta", e);
        }
    }
    
    /**
     * Busca respuestas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de respuestas del usuario ordenadas por fecha
     */
    public List<RespuestaPregunta> buscarPorUsuario(String usuarioId) {
        logger.debug("Buscando respuestas de pregunta para usuario: {}", usuarioId);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp JOIN rp.sesion s " +
                "WHERE s.usuarioId = :usuarioId ORDER BY rp.fechaRespuesta DESC",
                RespuestaPregunta.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas para usuario: {}", respuestas.size(), usuarioId);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas de pregunta para usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al buscar respuestas por usuario", e);
        }
    }
    
    /**
     * Busca respuestas de un usuario por curso.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso
     * @return Lista de respuestas del usuario en el curso
     */
    public List<RespuestaPregunta> buscarPorUsuarioYCurso(String usuarioId, String cursoId) {
        logger.debug("Buscando respuestas de pregunta para usuario: {} en curso: {}", usuarioId, cursoId);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp JOIN rp.sesion s " +
                "WHERE s.usuarioId = :usuarioId AND s.cursoId = :cursoId " +
                "ORDER BY rp.fechaRespuesta DESC",
                RespuestaPregunta.class
            );
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("cursoId", cursoId);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas para usuario: {} en curso: {}", 
                      respuestas.size(), usuarioId, cursoId);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas de pregunta para usuario: {} en curso: {}", 
                        usuarioId, cursoId, e);
            throw new RuntimeException("Error al buscar respuestas por usuario y curso", e);
        }
    }
    
    /**
     * Busca respuestas correctas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de respuestas correctas del usuario
     */
    public List<RespuestaPregunta> buscarRespuestasCorrectas(String usuarioId) {
        logger.debug("Buscando respuestas correctas para usuario: {}", usuarioId);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp JOIN rp.sesion s " +
                "WHERE s.usuarioId = :usuarioId AND rp.respuestaCorrecta = true " +
                "ORDER BY rp.fechaRespuesta DESC",
                RespuestaPregunta.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas correctas para usuario: {}", respuestas.size(), usuarioId);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas correctas para usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al buscar respuestas correctas", e);
        }
    }
    
    /**
     * Busca respuestas incorrectas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de respuestas incorrectas del usuario
     */
    public List<RespuestaPregunta> buscarRespuestasIncorrectas(String usuarioId) {
        logger.debug("Buscando respuestas incorrectas para usuario: {}", usuarioId);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp JOIN rp.sesion s " +
                "WHERE s.usuarioId = :usuarioId AND rp.respuestaCorrecta = false " +
                "ORDER BY rp.fechaRespuesta DESC",
                RespuestaPregunta.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas incorrectas para usuario: {}", respuestas.size(), usuarioId);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas incorrectas para usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al buscar respuestas incorrectas", e);
        }
    }
    
    /**
     * Busca respuestas en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de respuestas en el rango de fechas
     */
    public List<RespuestaPregunta> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        logger.debug("Buscando respuestas entre {} y {}", fechaInicio, fechaFin);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp WHERE rp.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin " +
                "ORDER BY rp.fechaRespuesta DESC",
                RespuestaPregunta.class
            );
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas entre {} y {}", respuestas.size(), fechaInicio, fechaFin);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas entre {} y {}", fechaInicio, fechaFin, e);
            throw new RuntimeException("Error al buscar respuestas por rango de fechas", e);
        }
    }
    
    /**
     * Busca respuestas con múltiples intentos.
     * 
     * @param intentosMinimos Número mínimo de intentos
     * @return Lista de respuestas con múltiples intentos
     */
    public List<RespuestaPregunta> buscarConMultiplesIntentos(int intentosMinimos) {
        logger.debug("Buscando respuestas con {} o más intentos", intentosMinimos);
        
        try {
            TypedQuery<RespuestaPregunta> query = entityManager.createQuery(
                "SELECT rp FROM RespuestaPregunta rp WHERE rp.intentos >= :intentosMinimos " +
                "ORDER BY rp.intentos DESC, rp.fechaRespuesta DESC",
                RespuestaPregunta.class
            );
            query.setParameter("intentosMinimos", intentosMinimos);
            
            List<RespuestaPregunta> respuestas = query.getResultList();
            logger.info("Encontradas {} respuestas con {} o más intentos", respuestas.size(), intentosMinimos);
            return respuestas;
        } catch (Exception e) {
            logger.error("Error al buscar respuestas con {} o más intentos", intentosMinimos, e);
            throw new RuntimeException("Error al buscar respuestas con múltiples intentos", e);
        }
    }
    
    /**
     * Actualiza los intentos de una respuesta.
     * 
     * @param id ID de la respuesta
     * @param intentos Nuevo número de intentos
     * @return true si se actualizó correctamente, false si no existía
     */
    public boolean actualizarIntentos(Long id, Integer intentos) {
        logger.debug("Actualizando intentos de respuesta - ID: {}, Intentos: {}", id, intentos);
        
        try {
            Query query = entityManager.createQuery(
                "UPDATE RespuestaPregunta rp SET rp.intentos = :intentos WHERE rp.id = :id"
            );
            query.setParameter("intentos", intentos);
            query.setParameter("id", id);
            
            int actualizadas = query.executeUpdate();
            if (actualizadas > 0) {
                logger.info("Intentos actualizados exitosamente - ID: {}, Intentos: {}", id, intentos);
                return true;
            } else {
                logger.warn("No se encontró respuesta para actualizar intentos - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al actualizar intentos de respuesta - ID: {}", id, e);
            throw new RuntimeException("Error al actualizar intentos", e);
        }
    }
    
    /**
     * Elimina una respuesta por su ID.
     * 
     * @param id ID de la respuesta a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminar(Long id) {
        logger.debug("Eliminando respuesta de pregunta con ID: {}", id);
        
        try {
            RespuestaPregunta respuestaPregunta = entityManager.find(RespuestaPregunta.class, id);
            if (respuestaPregunta != null) {
                entityManager.remove(respuestaPregunta);
                logger.info("Respuesta de pregunta eliminada exitosamente - ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontró respuesta de pregunta para eliminar - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al eliminar respuesta de pregunta con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar respuesta de pregunta", e);
        }
    }
    
    /**
     * Elimina todas las respuestas de una sesión.
     * 
     * @param sesionId ID de la sesión
     * @return Número de respuestas eliminadas
     */
    public int eliminarPorSesion(Long sesionId) {
        logger.debug("Eliminando todas las respuestas de la sesión: {}", sesionId);
        
        try {
            Query query = entityManager.createQuery(
                "DELETE FROM RespuestaPregunta rp WHERE rp.sesion.id = :sesionId"
            );
            query.setParameter("sesionId", sesionId);
            
            int eliminadas = query.executeUpdate();
            logger.info("Eliminadas {} respuestas de la sesión: {}", eliminadas, sesionId);
            return eliminadas;
        } catch (Exception e) {
            logger.error("Error al eliminar respuestas de la sesión: {}", sesionId, e);
            throw new RuntimeException("Error al eliminar respuestas de sesión", e);
        }
    }
    
    /**
     * Elimina todas las respuestas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de respuestas eliminadas
     */
    public int eliminarPorUsuario(String usuarioId) {
        logger.debug("Eliminando todas las respuestas del usuario: {}", usuarioId);
        
        try {
            Query query = entityManager.createQuery(
                "DELETE FROM RespuestaPregunta rp WHERE rp.sesion.usuarioId = :usuarioId"
            );
            query.setParameter("usuarioId", usuarioId);
            
            int eliminadas = query.executeUpdate();
            logger.info("Eliminadas {} respuestas del usuario: {}", eliminadas, usuarioId);
            return eliminadas;
        } catch (Exception e) {
            logger.error("Error al eliminar respuestas del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al eliminar respuestas de usuario", e);
        }
    }
    
    /**
     * Cuenta el número de respuestas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de respuestas
     */
    public long contarPorUsuario(String usuarioId) {
        logger.debug("Contando respuestas del usuario: {}", usuarioId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(rp) FROM RespuestaPregunta rp JOIN rp.sesion s WHERE s.usuarioId = :usuarioId",
                Long.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            long count = query.getSingleResult();
            logger.debug("Usuario {} tiene {} respuestas", usuarioId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar respuestas del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al contar respuestas de usuario", e);
        }
    }
    
    /**
     * Cuenta el número de respuestas correctas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de respuestas correctas
     */
    public long contarRespuestasCorrectas(String usuarioId) {
        logger.debug("Contando respuestas correctas del usuario: {}", usuarioId);
        
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(rp) FROM RespuestaPregunta rp JOIN rp.sesion s " +
                "WHERE s.usuarioId = :usuarioId AND rp.respuestaCorrecta = true",
                Long.class
            );
            query.setParameter("usuarioId", usuarioId);
            
            long count = query.getSingleResult();
            logger.debug("Usuario {} tiene {} respuestas correctas", usuarioId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar respuestas correctas del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al contar respuestas correctas", e);
        }
    }
    
    /**
     * Calcula el porcentaje de aciertos de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Porcentaje de aciertos (0.0 a 100.0)
     */
    public double calcularPorcentajeAciertos(String usuarioId) {
        logger.debug("Calculando porcentaje de aciertos del usuario: {}", usuarioId);
        
        try {
            long total = contarPorUsuario(usuarioId);
            if (total == 0) {
                logger.debug("Usuario {} no tiene respuestas", usuarioId);
                return 0.0;
            }
            
            long correctas = contarRespuestasCorrectas(usuarioId);
            double porcentaje = (double) correctas / total * 100.0;
            
            logger.info("Usuario {} tiene {}% de aciertos ({} de {})", 
                      usuarioId, String.format("%.2f", porcentaje), correctas, total);
            return porcentaje;
        } catch (Exception e) {
            logger.error("Error al calcular porcentaje de aciertos del usuario: {}", usuarioId, e);
            throw new RuntimeException("Error al calcular porcentaje de aciertos", e);
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