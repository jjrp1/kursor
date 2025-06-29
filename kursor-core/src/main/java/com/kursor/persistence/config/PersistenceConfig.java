package com.kursor.persistence.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de persistencia JPA para Kursor.
 * 
 * <p>Esta clase proporciona la configuración centralizada para el EntityManagerFactory
 * y gestiona la conexión con la base de datos SQLite.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class PersistenceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(PersistenceConfig.class);
    
    private static final String PERSISTENCE_UNIT_NAME = "kursorPU";
    private static final String DATABASE_PATH = "data/kursor.db";

    private static EntityManagerFactory entityManagerFactory;
    private static boolean initialized = false;
    
    /**
     * Constructor privado para evitar instanciación.
     */
    private PersistenceConfig() {
    }
    
    /**
     * Inicializa el EntityManagerFactory.
     * 
     * <p>Este método debe ser llamado una sola vez al inicio de la aplicación.
     * Configura la base de datos SQLite y crea el EntityManagerFactory.</p>
     * 
     * @throws RuntimeException si hay un error en la inicialización
     */
    public static synchronized void initialize() {
        if (initialized) {
            logger.warn("PersistenceConfig ya está inicializado");
            return;
        }
        
        try {
            logger.info("Inicializando PersistenceConfig...");
            
            // Crear directorio de datos si no existe
            createDataDirectory();
            
            // Configurar propiedades de la base de datos
            Map<String, Object> properties = createDatabaseProperties();
            
            // Crear EntityManagerFactory
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            
            initialized = true;
            logger.info("PersistenceConfig inicializado exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al inicializar PersistenceConfig", e);
            throw new RuntimeException("Error al inicializar PersistenceConfig", e);
        }
    }
    
    /**
     * Obtiene el EntityManagerFactory.
     * 
     * @return EntityManagerFactory
     * @throws IllegalStateException si no está inicializado
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (!initialized || entityManagerFactory == null) {
            throw new IllegalStateException("PersistenceConfig no está inicializado. Llame a initialize() primero.");
        }
        return entityManagerFactory;
    }
    
    /**
     * Crea un nuevo EntityManager.
     * 
     * @return EntityManager
     * @throws IllegalStateException si no está inicializado
     */
    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Cierra el EntityManagerFactory.
     * 
     * <p>Este método debe ser llamado al finalizar la aplicación para liberar recursos.</p>
     */
    public static synchronized void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                logger.info("Cerrando EntityManagerFactory...");
                entityManagerFactory.close();
                logger.info("EntityManagerFactory cerrado exitosamente");
            } catch (Exception e) {
                logger.error("Error al cerrar EntityManagerFactory", e);
            } finally {
                entityManagerFactory = null;
                initialized = false;
            }
        }
    }
    
    /**
     * Verifica si el PersistenceConfig está inicializado.
     * 
     * @return true si está inicializado, false en caso contrario
     */
    public static boolean isInitialized() {
        return initialized && entityManagerFactory != null && entityManagerFactory.isOpen();
    }
    
    /**
     * Crea el directorio de datos si no existe.
     */
    private static void createDataDirectory() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    logger.info("Directorio de datos creado: {}", dataDir.getAbsolutePath());
                } else {
                    logger.warn("No se pudo crear el directorio de datos: {}", dataDir.getAbsolutePath());
                }
            } else {
                logger.debug("Directorio de datos ya existe: {}", dataDir.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Error al crear directorio de datos", e);
            throw new RuntimeException("Error al crear directorio de datos", e);
        }
    }
    
    /**
     * Crea las propiedades de configuración de la base de datos.
     * 
     * @return Map con las propiedades de configuración
     */
    private static Map<String, Object> createDatabaseProperties() {
        Map<String, Object> properties = new HashMap<>();
        
        // Configuración de SQLite
        properties.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
        properties.put("javax.persistence.jdbc.url", "jdbc:sqlite:" + DATABASE_PATH);
        properties.put("javax.persistence.jdbc.user", "");
        properties.put("javax.persistence.jdbc.password", "");
        
        // Configuración de Hibernate
        properties.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        
        // Configuración de conexión
        properties.put("hibernate.connection.pool_size", "1");
        properties.put("hibernate.connection.autocommit", "false");
        
        // Configuración de logging
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.jdbc.batch_size", "20");
        
        // Configuración específica para SQLite
        properties.put("hibernate.connection.foreign_keys", "true");
        
        logger.debug("Propiedades de base de datos configuradas");
        return properties;
    }
    
    /**
     * Obtiene la ruta de la base de datos.
     * 
     * @return Ruta de la base de datos
     */
    public static String getDatabasePath() {
        return DATABASE_PATH;
    }
    
    /**
     * Verifica si la base de datos existe.
     * 
     * @return true si existe, false en caso contrario
     */
    public static boolean databaseExists() {
        File dbFile = new File(DATABASE_PATH);
        return dbFile.exists() && dbFile.isFile();
    }
    
    /**
     * Obtiene el tamaño de la base de datos en bytes.
     * 
     * @return Tamaño en bytes, -1 si no existe
     */
    public static long getDatabaseSize() {
        File dbFile = new File(DATABASE_PATH);
        if (dbFile.exists() && dbFile.isFile()) {
            return dbFile.length();
        }
        return -1;
    }


    /**
     * Obtiene información de la base de datos.
     * 
     * @return String con información de la base de datos
     */
    public static String getDatabaseInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Base de datos: ").append(DATABASE_PATH).append("\n");
        info.append("Existe: ").append(databaseExists()).append("\n");
        
        if (databaseExists()) {
            long size = getDatabaseSize();
            info.append("Tamaño: ").append(size).append(" bytes\n");
            info.append("Tamaño: ").append(String.format("%.2f MB", size / (1024.0 * 1024.0))).append("\n");
        }
        
        info.append("Inicializado: ").append(isInitialized()).append("\n");
        
        return info.toString();
    }
} 
