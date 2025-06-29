package com.kursor.studio.service;

import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadisticasUsuario;
import com.kursor.persistence.entity.EstadoEstrategia;
import com.kursor.persistence.entity.RespuestaPregunta;
import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.model.DatabaseStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para obtener estadísticas reales de la base de datos.
 * 
 * <p>Este servicio utiliza las entidades JPA del módulo kursor-core
 * para obtener estadísticas reales de la base de datos.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatabaseStatisticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseStatisticsService.class);
    
    private final DatabaseConnectionService connectionService;
    
    /**
     * Constructor.
     * 
     * @param connectionService Servicio de conexión a la base de datos
     */
    public DatabaseStatisticsService(DatabaseConnectionService connectionService) {
        this.connectionService = connectionService;
    }
    
    /**
     * Obtiene estadísticas completas de la base de datos.
     * 
     * @return Estadísticas de la base de datos
     */
    public DatabaseStatistics getStatistics() {
        logger.debug("Obteniendo estadísticas de la base de datos...");
        
        DatabaseStatistics.Builder builder = new DatabaseStatistics.Builder()
                .databasePath(connectionService.getDatabasePath())
                .databaseSize(connectionService.getDatabaseSize())
                .databaseExists(connectionService.databaseExists())
                .lastUpdated(LocalDateTime.now())
                .databaseInfo(connectionService.getDatabaseInfo());
        
        // Obtener conteos de tablas
        Map<String, Long> tableCounts = getTableCounts();
        builder.tableCounts(tableCounts);
        
        // Obtener estadísticas específicas de entidades
        long totalSessions = getTotalSessions();
        long totalUserStats = getTotalUserStats();
        long totalStrategyStates = getTotalStrategyStates();
        long totalQuestionResponses = getTotalQuestionResponses();
        
        builder.totalSessions(totalSessions)
               .totalUserStats(totalUserStats)
               .totalStrategyStates(totalStrategyStates)
               .totalQuestionResponses(totalQuestionResponses);
        
        DatabaseStatistics statistics = builder.build();
        logger.debug("Estadísticas obtenidas: {}", statistics);
        
        return statistics;
    }
    
    /**
     * Obtiene el conteo de registros por tabla.
     * 
     * @return Mapa con el conteo de registros por tabla
     */
    public Map<String, Long> getTableCounts() {
        Map<String, Long> tableCounts = new HashMap<>();
        
        try {
            List<String> tableNames = connectionService.getTableNames();
            
            for (String tableName : tableNames) {
                long count = connectionService.getRecordCount(tableName);
                if (count >= 0) {
                    tableCounts.put(tableName, count);
                }
            }
            
            logger.debug("Conteos de tablas obtenidos: {}", tableCounts);
            
        } catch (Exception e) {
            logger.error("Error al obtener conteos de tablas", e);
        }
        
        return tableCounts;
    }
    
    /**
     * Obtiene el total de sesiones.
     * 
     * @return Total de sesiones
     */
    public long getTotalSessions() {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(s) FROM Sesion s", Long.class
                );
                Long count = query.getSingleResult();
                logger.debug("Total de sesiones: {}", count);
                return count != null ? count : 0;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener total de sesiones", e);
            return 0;
        }
    }
    
    /**
     * Obtiene el total de estadísticas de usuario.
     * 
     * @return Total de estadísticas de usuario
     */
    public long getTotalUserStats() {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM EstadisticasUsuario e", Long.class
                );
                Long count = query.getSingleResult();
                logger.debug("Total de estadísticas de usuario: {}", count);
                return count != null ? count : 0;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener total de estadísticas de usuario", e);
            return 0;
        }
    }
    
    /**
     * Obtiene el total de estados de estrategia.
     * 
     * @return Total de estados de estrategia
     */
    public long getTotalStrategyStates() {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM EstadoEstrategia e", Long.class
                );
                Long count = query.getSingleResult();
                logger.debug("Total de estados de estrategia: {}", count);
                return count != null ? count : 0;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener total de estados de estrategia", e);
            return 0;
        }
    }
    
    /**
     * Obtiene el total de respuestas de preguntas.
     * 
     * @return Total de respuestas de preguntas
     */
    public long getTotalQuestionResponses() {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM RespuestaPregunta r", Long.class
                );
                Long count = query.getSingleResult();
                logger.debug("Total de respuestas de preguntas: {}", count);
                return count != null ? count : 0;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener total de respuestas de preguntas", e);
            return 0;
        }
    }
    
    /**
     * Obtiene las sesiones recientes.
     * 
     * @param limit Número máximo de sesiones a obtener
     * @return Lista de sesiones recientes
     */
    public List<Sesion> getRecentSessions(int limit) {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                TypedQuery<Sesion> query = em.createQuery(
                    "SELECT s FROM Sesion s ORDER BY s.fechaInicio DESC", Sesion.class
                );
                query.setMaxResults(limit);
                
                List<Sesion> sessions = query.getResultList();
                logger.debug("Sesiones recientes obtenidas: {}", sessions.size());
                return sessions;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener sesiones recientes", e);
            return List.of();
        }
    }
    
    /**
     * Obtiene estadísticas de usuario recientes.
     * 
     * @param limit Número máximo de estadísticas a obtener
     * @return Lista de estadísticas de usuario recientes
     */
    public List<EstadisticasUsuario> getRecentUserStats(int limit) {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                TypedQuery<EstadisticasUsuario> query = em.createQuery(
                    "SELECT e FROM EstadisticasUsuario e ORDER BY e.fechaUltimaActividad DESC", 
                    EstadisticasUsuario.class
                );
                query.setMaxResults(limit);
                
                List<EstadisticasUsuario> stats = query.getResultList();
                logger.debug("Estadísticas de usuario recientes obtenidas: {}", stats.size());
                return stats;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de usuario recientes", e);
            return List.of();
        }
    }
    
    /**
     * Obtiene respuestas de preguntas recientes.
     * 
     * @param limit Número máximo de respuestas a obtener
     * @return Lista de respuestas recientes
     */
    public List<RespuestaPregunta> getRecentQuestionResponses(int limit) {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                TypedQuery<RespuestaPregunta> query = em.createQuery(
                    "SELECT r FROM RespuestaPregunta r ORDER BY r.fechaRespuesta DESC", 
                    RespuestaPregunta.class
                );
                query.setMaxResults(limit);
                
                List<RespuestaPregunta> responses = query.getResultList();
                logger.debug("Respuestas de preguntas recientes obtenidas: {}", responses.size());
                return responses;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener respuestas de preguntas recientes", e);
            return List.of();
        }
    }
    
    /**
     * Obtiene estadísticas de rendimiento por estrategia.
     * 
     * @return Mapa con estadísticas por estrategia
     */
    public Map<String, Long> getStrategyPerformanceStats() {
        Map<String, Long> stats = new HashMap<>();
        
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                // Obtener conteo por tipo de estrategia
                TypedQuery<Object[]> query = em.createQuery(
                    "SELECT e.tipoEstrategia, COUNT(e) FROM EstadoEstrategia e GROUP BY e.tipoEstrategia", 
                    Object[].class
                );
                
                List<Object[]> results = query.getResultList();
                
                for (Object[] result : results) {
                    if (result.length >= 2 && result[0] instanceof String && result[1] instanceof Number) {
                        String strategyType = (String) result[0];
                        Long count = ((Number) result[1]).longValue();
                        stats.put(strategyType, count);
                    }
                }
                
                logger.debug("Estadísticas de estrategia obtenidas: {}", stats);
                
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de estrategia", e);
        }
        
        return stats;
    }
} 