package com.kursor.studio.service;

import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.model.DatabaseConfiguration;
import com.kursor.studio.repository.DatabaseConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la configuración de la base de datos de Kursor.
 * 
 * <p>Este servicio trabaja con la base de datos de kursor-studio (configuración local)
 * y gestiona la conexión dinámica a la base de datos de kursor.</p>
 */
public class DatabaseConfigurationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigurationService.class);
    
    private final DatabaseConfigurationRepository repository;
    
    public DatabaseConfigurationService() {
        this.repository = new DatabaseConfigurationRepository();
    }
    
    /**
     * Obtiene la configuración activa de la base de datos de Kursor.
     */
    public Optional<DatabaseConfiguration> getActiveConfiguration() {
        logger.debug("Obteniendo configuración activa de BD de Kursor");
        return repository.findActiveConfiguration();
    }
    
    /**
     * Verifica si existe alguna configuración de base de datos.
     */
    public boolean hasConfiguration() {
        logger.debug("Verificando existencia de configuración de BD");
        return repository.hasAnyConfiguration();
    }
    
    /**
     * Guarda una nueva configuración de base de datos.
     */
    public DatabaseConfiguration saveConfiguration(String databasePath, String description) {
        logger.info("Guardando nueva configuración de BD: {}", databasePath);
        
        // Validar que el archivo existe
        if (!isValidDatabaseFile(databasePath)) {
            throw new IllegalArgumentException("El archivo de base de datos no es válido: " + databasePath);
        }
        
        // Crear nueva configuración
        DatabaseConfiguration config = new DatabaseConfiguration(databasePath);
        config.setDescription(description);
        
        // Probar la conexión
        if (testConnection(databasePath)) {
            config.markConnectionSuccessful();
            logger.info("Conexión exitosa a BD: {}", databasePath);
        } else {
            config.markConnectionFailed();
            logger.warn("Fallo en conexión a BD: {}", databasePath);
        }
        
        // Guardar configuración
        DatabaseConfiguration savedConfig = repository.save(config);
        
        // Si la conexión fue exitosa, inicializar la BD de kursor
        if (savedConfig.isConnected()) {
            try {
                PersistenceConfig.initializeKursor(databasePath);
                logger.info("Base de datos de kursor inicializada: {}", databasePath);
            } catch (Exception e) {
                logger.error("Error al inicializar BD de kursor: {}", e.getMessage(), e);
                // Marcar como fallida si no se puede inicializar
                savedConfig.markConnectionFailed();
                repository.save(savedConfig);
            }
        }
        
        return savedConfig;
    }
    
    /**
     * Actualiza una configuración existente.
     */
    public DatabaseConfiguration updateConfiguration(Long id, String databasePath, String description) {
        logger.info("Actualizando configuración de BD ID {}: {}", id, databasePath);
        
        Optional<DatabaseConfiguration> existingConfig = repository.findById(id);
        if (existingConfig.isEmpty()) {
            throw new IllegalArgumentException("No se encontró configuración con ID: " + id);
        }
        
        DatabaseConfiguration config = existingConfig.get();
        
        // Validar que el archivo existe
        if (!isValidDatabaseFile(databasePath)) {
            throw new IllegalArgumentException("El archivo de base de datos no es válido: " + databasePath);
        }
        
        // Actualizar campos
        config.setKursorDatabasePath(databasePath);
        config.setDescription(description);
        
        // Probar la conexión
        if (testConnection(databasePath)) {
            config.markConnectionSuccessful();
            logger.info("Conexión exitosa a BD actualizada: {}", databasePath);
        } else {
            config.markConnectionFailed();
            logger.warn("Fallo en conexión a BD actualizada: {}", databasePath);
        }
        
        // Guardar configuración
        DatabaseConfiguration savedConfig = repository.save(config);
        
        // Si la conexión fue exitosa, reinicializar la BD de kursor
        if (savedConfig.isConnected()) {
            try {
                PersistenceConfig.initializeKursor(databasePath);
                logger.info("Base de datos de kursor reinicializada: {}", databasePath);
            } catch (Exception e) {
                logger.error("Error al reinicializar BD de kursor: {}", e.getMessage(), e);
                // Marcar como fallida si no se puede inicializar
                savedConfig.markConnectionFailed();
                repository.save(savedConfig);
            }
        }
        
        return savedConfig;
    }
    
    /**
     * Prueba la conexión a una base de datos SQLite.
     */
    public boolean testConnection(String databasePath) {
        logger.debug("Probando conexión a BD: {}", databasePath);
        
        if (!isValidDatabaseFile(databasePath)) {
            logger.warn("Archivo de BD no válido: {}", databasePath);
            return false;
        }
        
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            boolean isValid = connection.isValid(5); // 5 segundos de timeout
            logger.debug("Resultado de prueba de conexión: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Error al probar conexión a BD {}: {}", databasePath, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Valida que un archivo sea una base de datos SQLite válida.
     */
    public boolean isValidDatabaseFile(String databasePath) {
        if (databasePath == null || databasePath.trim().isEmpty()) {
            logger.warn("Ruta de BD vacía o nula");
            return false;
        }
        
        try {
            Path path = Paths.get(databasePath);
            
            // Verificar que el archivo existe
            if (!Files.exists(path)) {
                logger.warn("Archivo de BD no existe: {}", databasePath);
                return false;
            }
            
            // Verificar que es un archivo (no directorio)
            if (!Files.isRegularFile(path)) {
                logger.warn("La ruta no es un archivo: {}", databasePath);
                return false;
            }
            
            // Verificar que es legible
            if (!Files.isReadable(path)) {
                logger.warn("Archivo de BD no es legible: {}", databasePath);
                return false;
            }
            
            // Verificar extensión .db o .sqlite
            String fileName = path.getFileName().toString().toLowerCase();
            if (!fileName.endsWith(".db") && !fileName.endsWith(".sqlite")) {
                logger.warn("Archivo no tiene extensión de BD válida: {}", databasePath);
                return false;
            }
            
            // Verificar tamaño mínimo (al menos 1KB para ser una BD válida)
            long fileSize = Files.size(path);
            if (fileSize < 1024) {
                logger.warn("Archivo de BD demasiado pequeño ({} bytes): {}", fileSize, databasePath);
                return false;
            }
            
            logger.debug("Archivo de BD válido: {} ({} bytes)", databasePath, fileSize);
            return true;
            
        } catch (Exception e) {
            logger.error("Error al validar archivo de BD {}: {}", databasePath, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene todas las configuraciones.
     */
    public List<DatabaseConfiguration> getAllConfigurations() {
        logger.debug("Obteniendo todas las configuraciones de BD");
        return repository.findAll();
    }
    
    /**
     * Elimina una configuración por ID.
     */
    public boolean deleteConfiguration(Long id) {
        logger.info("Eliminando configuración de BD ID: {}", id);
        return repository.deleteById(id);
    }
    
    /**
     * Obtiene configuraciones por estado de conexión.
     */
    public List<DatabaseConfiguration> getConfigurationsByStatus(String status) {
        logger.debug("Obteniendo configuraciones con estado: {}", status);
        return repository.findByConnectionStatus(status);
    }
    
    /**
     * Obtiene configuraciones conectadas exitosamente.
     */
    public List<DatabaseConfiguration> getConnectedConfigurations() {
        return getConfigurationsByStatus("CONNECTED");
    }
    
    /**
     * Obtiene configuraciones con fallo de conexión.
     */
    public List<DatabaseConfiguration> getFailedConfigurations() {
        return getConfigurationsByStatus("FAILED");
    }
    
    /**
     * Obtiene configuraciones no probadas.
     */
    public List<DatabaseConfiguration> getUntestedConfigurations() {
        return getConfigurationsByStatus("NOT_TESTED");
    }
    
    /**
     * Reinicia el estado de conexión de una configuración.
     */
    public DatabaseConfiguration resetConnectionStatus(Long id) {
        logger.info("Reiniciando estado de conexión para configuración ID: {}", id);
        
        Optional<DatabaseConfiguration> configOpt = repository.findById(id);
        if (configOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró configuración con ID: " + id);
        }
        
        DatabaseConfiguration config = configOpt.get();
        config.setConnectionStatus("NOT_TESTED");
        config.setLastConnectionDate(null);
        
        return repository.save(config);
    }
    
    /**
     * Prueba la conexión de la configuración activa.
     */
    public boolean testActiveConfiguration() {
        logger.info("Probando conexión de configuración activa");
        
        Optional<DatabaseConfiguration> activeConfig = getActiveConfiguration();
        if (activeConfig.isEmpty()) {
            logger.warn("No hay configuración activa para probar");
            return false;
        }
        
        DatabaseConfiguration config = activeConfig.get();
        boolean connectionSuccessful = testConnection(config.getKursorDatabasePath());
        
        if (connectionSuccessful) {
            config.markConnectionSuccessful();
        } else {
            config.markConnectionFailed();
        }
        
        repository.save(config);
        return connectionSuccessful;
    }
    
    /**
     * Inicializa la configuración de persistencia con la configuración activa.
     */
    public void initializeWithActiveConfiguration() {
        logger.info("Inicializando con configuración activa");
        
        Optional<DatabaseConfiguration> activeConfig = getActiveConfiguration();
        if (activeConfig.isPresent()) {
            DatabaseConfiguration config = activeConfig.get();
            if (config.isConnected()) {
                try {
                    PersistenceConfig.initializeKursor(config.getKursorDatabasePath());
                    logger.info("Inicializada BD de kursor con configuración activa: {}", config.getKursorDatabasePath());
                } catch (Exception e) {
                    logger.error("Error al inicializar BD de kursor con configuración activa: {}", e.getMessage(), e);
                }
            } else {
                logger.warn("Configuración activa no está conectada: {}", config.getKursorDatabasePath());
            }
        } else {
            logger.info("No hay configuración activa, usando configuración por defecto");
        }
    }
} 