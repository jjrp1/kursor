package com.kursor.studio.service;

import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.model.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Optional;

/**
 * Servicio para gestionar la conexión con la base de datos.
 * 
 * <p>Este servicio proporciona métodos para verificar la conexión,
 * obtener información de las tablas y realizar consultas básicas.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatabaseConnectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionService.class);
    
    /**
     * Verifica si la conexión a la base de datos está activa.
     * 
     * @return true si está conectado, false en caso contrario
     */
    public boolean isConnected() {
        try {
            if (!PersistenceConfig.isInitialized()) {
                logger.warn("PersistenceConfig no está inicializado");
                return false;
            }
            
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                // Intentar una consulta simple para verificar la conexión
                Query query = em.createNativeQuery("SELECT 1");
                query.getSingleResult();
                logger.debug("Conexión a la base de datos verificada exitosamente");
                return true;
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al verificar la conexión a la base de datos", e);
            return false;
        }
    }
    
    /**
     * Obtiene los nombres de todas las tablas en la base de datos.
     * 
     * @return Lista de nombres de tablas
     */
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                // Consulta específica para SQLite
                Query query = em.createNativeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'"
                );
                
                @SuppressWarnings("unchecked")
                List<Object> results = query.getResultList();
                
                for (Object result : results) {
                    if (result instanceof String) {
                        tableNames.add((String) result);
                    }
                }
                
                logger.debug("Tablas encontradas: {}", tableNames);
                
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener nombres de tablas", e);
        }
        
        return tableNames;
    }
    
    /**
     * Obtiene el número de registros en una tabla específica.
     * 
     * @param tableName Nombre de la tabla
     * @return Número de registros, -1 si hay error
     */
    public long getRecordCount(String tableName) {
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                Query query = em.createNativeQuery("SELECT COUNT(*) FROM " + tableName);
                Object result = query.getSingleResult();
                
                if (result instanceof Number) {
                    long count = ((Number) result).longValue();
                    logger.debug("Tabla {} tiene {} registros", tableName, count);
                    return count;
                } else {
                    logger.warn("Resultado inesperado al contar registros en tabla {}", tableName);
                    return -1;
                }
                
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al contar registros en tabla {}", tableName, e);
            return -1;
        }
    }
    
    /**
     * Obtiene información del esquema de una tabla.
     * 
     * @param tableName Nombre de la tabla
     * @return Información del esquema como lista de strings
     */
    public List<String> getTableSchema(String tableName) {
        List<String> schemaInfo = new ArrayList<>();
        
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                Query query = em.createNativeQuery("PRAGMA table_info(" + tableName + ")");
                
                @SuppressWarnings("unchecked")
                List<Object[]> results = query.getResultList();
                
                for (Object[] row : results) {
                    if (row.length >= 6) {
                        String columnName = String.valueOf(row[1]);
                        String dataType = String.valueOf(row[2]);
                        String notNull = String.valueOf(row[3]);
                        String defaultValue = String.valueOf(row[4]);
                        String primaryKey = String.valueOf(row[5]);
                        
                        String columnInfo = String.format("%s (%s) %s %s %s",
                                columnName, dataType,
                                "1".equals(notNull) ? "NOT NULL" : "",
                                "1".equals(primaryKey) ? "PRIMARY KEY" : "",
                                !"null".equals(defaultValue) ? "DEFAULT " + defaultValue : ""
                        ).trim();
                        
                        schemaInfo.add(columnInfo);
                    }
                }
                
                logger.debug("Esquema de tabla {}: {}", tableName, schemaInfo);
                
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al obtener esquema de tabla {}", tableName, e);
        }
        
        return schemaInfo;
    }
    
    /**
     * Ejecuta una consulta SQL personalizada.
     * 
     * @param sql Consulta SQL a ejecutar
     * @return Lista de resultados
     */
    public List<Object[]> executeQuery(String sql) {
        List<Object[]> results = new ArrayList<>();
        
        try {
            EntityManager em = PersistenceConfig.createKursorEntityManager();
            try {
                Query query = em.createNativeQuery(sql);
                
                @SuppressWarnings("unchecked")
                List<Object> queryResults = query.getResultList();
                
                for (Object result : queryResults) {
                    if (result instanceof Object[]) {
                        results.add((Object[]) result);
                    } else {
                        // Si es un resultado simple, crear un array con un elemento
                        results.add(new Object[]{result});
                    }
                }
                
                logger.debug("Consulta ejecutada exitosamente: {}", sql);
                
            } finally {
                em.close();
            }
        } catch (Exception e) {
            logger.error("Error al ejecutar consulta: {}", sql, e);
        }
        
        return results;
    }
    
    /**
     * Obtiene información de la base de datos.
     * 
     * @return Información de la base de datos
     */
    public String getDatabaseInfo() {
        return PersistenceConfig.getDatabaseInfo();
    }
    
    /**
     * Obtiene la ruta de la base de datos.
     * 
     * @return Ruta de la base de datos
     */
    public String getDatabasePath() {
        // Retornar la ruta de la BD de kursor desde la configuración activa
        try {
            DatabaseConfigurationService configService = new DatabaseConfigurationService();
            Optional<DatabaseConfiguration> activeConfig = configService.getActiveConfiguration();
            if (activeConfig.isPresent()) {
                return activeConfig.get().getKursorDatabasePath();
            }
        } catch (Exception e) {
            logger.error("Error al obtener ruta de BD: {}", e.getMessage(), e);
        }
        return "data/kursor.db"; // Ruta por defecto
    }
    
    /**
     * Verifica si la base de datos existe.
     * 
     * @return true si existe, false en caso contrario
     */
    public boolean databaseExists() {
        try {
            String dbPath = getDatabasePath();
            return new File(dbPath).exists();
        } catch (Exception e) {
            logger.error("Error al verificar existencia de BD: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene el tamaño de la base de datos en bytes.
     * 
     * @return Tamaño de la base de datos en bytes
     */
    public long getDatabaseSize() {
        try {
            String dbPath = getDatabasePath();
            File dbFile = new File(dbPath);
            return dbFile.exists() ? dbFile.length() : 0;
        } catch (Exception e) {
            logger.error("Error al obtener tamaño de BD: {}", e.getMessage(), e);
            return 0;
        }
    }
} 