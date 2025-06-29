package com.kursor.studio.repository;

import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.model.DatabaseConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la configuración de base de datos de Kursor.
 * 
 * <p>Este repositorio trabaja con la base de datos de kursor-studio (configuración local).</p>
 */
public class DatabaseConfigurationRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigurationRepository.class);
    
    /**
     * Guarda o actualiza una configuración de base de datos.
     */
    public DatabaseConfiguration save(DatabaseConfiguration configuration) {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorStudioEntityManager();
            em.getTransaction().begin();
            
            if (configuration.getId() == null) {
                em.persist(configuration);
                logger.info("Nueva configuración de BD guardada: {}", configuration.getKursorDatabasePath());
            } else {
                configuration = em.merge(configuration);
                logger.info("Configuración de BD actualizada: {}", configuration.getKursorDatabasePath());
            }
            
            em.getTransaction().commit();
            return configuration;
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error al guardar configuración de BD: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Busca una configuración por ID.
     */
    public Optional<DatabaseConfiguration> findById(Long id) {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorStudioEntityManager();
            DatabaseConfiguration config = em.find(DatabaseConfiguration.class, id);
            return Optional.ofNullable(config);
        } catch (Exception e) {
            logger.error("Error al buscar configuración por ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Busca la configuración activa (la primera encontrada).
     */
    public Optional<DatabaseConfiguration> findActiveConfiguration() {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorStudioEntityManager();
            TypedQuery<DatabaseConfiguration> query = em.createQuery(
                "SELECT dc FROM DatabaseConfiguration dc ORDER BY dc.createdDate DESC", 
                DatabaseConfiguration.class
            );
            query.setMaxResults(1);
            List<DatabaseConfiguration> results = query.getResultList();
            
            if (!results.isEmpty()) {
                logger.debug("Configuración activa encontrada: {}", results.get(0).getKursorDatabasePath());
                return Optional.of(results.get(0));
            } else {
                logger.debug("No se encontró configuración activa");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error al buscar configuración activa: {}", e.getMessage(), e);
            return Optional.empty();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Busca todas las configuraciones.
     */
    public List<DatabaseConfiguration> findAll() {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorStudioEntityManager();
            TypedQuery<DatabaseConfiguration> query = em.createQuery(
                "SELECT dc FROM DatabaseConfiguration dc ORDER BY dc.createdDate DESC", 
                DatabaseConfiguration.class
            );
            List<DatabaseConfiguration> results = query.getResultList();
            logger.debug("Encontradas {} configuraciones de BD", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error al buscar todas las configuraciones: {}", e.getMessage(), e);
            return List.of();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Elimina una configuración por ID.
     */
    public boolean deleteById(Long id) {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorStudioEntityManager();
            em.getTransaction().begin();
            
            DatabaseConfiguration config = em.find(DatabaseConfiguration.class, id);
            if (config != null) {
                em.remove(config);
                em.getTransaction().commit();
                logger.info("Configuración de BD eliminada: ID {}", id);
                return true;
            } else {
                em.getTransaction().rollback();
                logger.warn("No se encontró configuración con ID {} para eliminar", id);
                return false;
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error al eliminar configuración con ID {}: {}", id, e.getMessage(), e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Verifica si existe alguna configuración.
     */
    public boolean hasAnyConfiguration() {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorStudioEntityManager();
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(dc) FROM DatabaseConfiguration dc", 
                Long.class
            );
            Long count = query.getSingleResult();
            boolean hasConfig = count > 0;
            logger.debug("Verificación de configuración: {}", hasConfig);
            return hasConfig;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de configuraciones: {}", e.getMessage(), e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Busca configuraciones por estado de conexión.
     */
    public List<DatabaseConfiguration> findByConnectionStatus(String status) {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorStudioEntityManager();
            TypedQuery<DatabaseConfiguration> query = em.createQuery(
                "SELECT dc FROM DatabaseConfiguration dc WHERE dc.connectionStatus = :status ORDER BY dc.createdDate DESC", 
                DatabaseConfiguration.class
            );
            query.setParameter("status", status);
            List<DatabaseConfiguration> results = query.getResultList();
            logger.debug("Encontradas {} configuraciones con estado: {}", results.size(), status);
            return results;
        } catch (Exception e) {
            logger.error("Error al buscar configuraciones por estado {}: {}", status, e.getMessage(), e);
            return List.of();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
} 