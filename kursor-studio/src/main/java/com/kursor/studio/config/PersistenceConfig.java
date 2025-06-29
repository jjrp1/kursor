package com.kursor.studio.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de persistencia para Kursor Studio.
 * 
 * <p>Esta clase maneja dos bases de datos separadas:</p>
 * <ul>
 *   <li><strong>kursor-studio</strong>: Base de datos local para configuración</li>
 *   <li><strong>kursor</strong>: Base de datos objetivo que se quiere explorar</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class PersistenceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(PersistenceConfig.class);
    
    // EntityManagerFactory para kursor-studio (configuración local)
    private static EntityManagerFactory kursorStudioEMF;
    
    // EntityManagerFactory para kursor (datos a explorar)
    private static EntityManagerFactory kursorEMF;
    
    private static boolean initialized = false;
    
    /**
     * Constructor privado para evitar instanciación.
     */
    private PersistenceConfig() {
    }
    
    /**
     * Inicializa la configuración de persistencia.
     * 
     * <p>Este método inicializa ambas bases de datos:</p>
     * <ul>
     *   <li>kursor-studio: Para configuración local (obligatoria)</li>
     *   <li>kursor: Para exploración de datos (opcional)</li>
     * </ul>
     * 
     * @throws RuntimeException si hay un error en la inicialización de kursor-studio
     */
    public static synchronized void initialize() {
        if (initialized) {
            logger.warn("PersistenceConfig ya está inicializado");
            return;
        }
        
        try {
            logger.info("Inicializando PersistenceConfig de Kursor Studio...");
            
            // Inicializar kursor-studio (configuración local) - OBLIGATORIO
            initializeKursorStudio();
            
            // Inicializar kursor (datos a explorar) - OPCIONAL
            try {
                initializeKursor("../kursor-core/data/kursor.db");
                logger.info("✅ Base de datos de kursor inicializada correctamente");
            } catch (Exception e) {
                logger.warn("⚠️ No se pudo inicializar la base de datos de kursor: {}", e.getMessage());
                logger.info("ℹ️ La aplicación continuará sin acceso a la base de datos de Kursor");
                logger.info("ℹ️ Puede configurar la base de datos más tarde desde la interfaz");
            }
            
            initialized = true;
            logger.info("✅ PersistenceConfig de Kursor Studio inicializado exitosamente");
            
        } catch (Exception e) {
            logger.error("❌ Error al inicializar PersistenceConfig de Kursor Studio", e);
            throw new RuntimeException("Error al inicializar PersistenceConfig de Kursor Studio", e);
        }
    }
    
    /**
     * Inicializa la base de datos de kursor-studio (configuración local).
     */
    private static void initializeKursorStudio() {
        try {
            logger.info("Inicializando base de datos de kursor-studio...");
            kursorStudioEMF = Persistence.createEntityManagerFactory("kursorStudioPU");
            logger.info("Base de datos de kursor-studio inicializada correctamente");
        } catch (Exception e) {
            logger.error("Error al inicializar base de datos de kursor-studio", e);
            throw new RuntimeException("Error al inicializar base de datos de kursor-studio", e);
        }
    }
    
    /**
     * Inicializa la base de datos de kursor (datos a explorar).
     * 
     * @param databasePath Ruta a la base de datos de kursor
     */
    public static synchronized void initializeKursor(String databasePath) {
        try {
            logger.info("Inicializando base de datos de kursor: {}", databasePath);
            
            // Cerrar conexión anterior si existe
            if (kursorEMF != null) {
                kursorEMF.close();
                logger.debug("Conexión anterior a kursor cerrada");
            }
            
            // Crear nueva conexión con la BD de kursor
            Map<String, String> properties = new HashMap<>();
            properties.put("javax.persistence.jdbc.url", "jdbc:sqlite:" + databasePath);
            
            kursorEMF = Persistence.createEntityManagerFactory("kursorPU", properties);
            logger.info("Base de datos de kursor inicializada correctamente: {}", databasePath);
            
        } catch (Exception e) {
            logger.error("Error al inicializar base de datos de kursor: {}", databasePath, e);
            throw new RuntimeException("Error al inicializar base de datos de kursor: " + databasePath, e);
        }
    }
    
    /**
     * Obtiene el EntityManagerFactory de kursor-studio.
     * 
     * @return EntityManagerFactory de kursor-studio
     * @throws IllegalStateException si no está inicializado
     */
    public static EntityManagerFactory getKursorStudioEntityManagerFactory() {
        if (!initialized || kursorStudioEMF == null) {
            throw new IllegalStateException("PersistenceConfig de kursor-studio no está inicializado. Llame a initialize() primero.");
        }
        return kursorStudioEMF;
    }
    
    /**
     * Obtiene el EntityManagerFactory de kursor.
     * 
     * @return EntityManagerFactory de kursor
     * @throws IllegalStateException si no está inicializado
     */
    public static EntityManagerFactory getKursorEntityManagerFactory() {
        if (!initialized || kursorEMF == null) {
            throw new IllegalStateException("PersistenceConfig de kursor no está inicializado. Llame a initialize() primero.");
        }
        return kursorEMF;
    }
    
    /**
     * Crea un nuevo EntityManager para kursor-studio.
     * 
     * @return EntityManager de kursor-studio
     * @throws IllegalStateException si no está inicializado
     */
    public static EntityManager createKursorStudioEntityManager() {
        return getKursorStudioEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Crea un nuevo EntityManager para kursor.
     * 
     * @return EntityManager de kursor
     * @throws IllegalStateException si no está inicializado
     */
    public static EntityManager createKursorEntityManager() {
        return getKursorEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Cierra la configuración de persistencia.
     * 
     * <p>Este método debe ser llamado al finalizar la aplicación para liberar recursos.</p>
     */
    public static synchronized void shutdown() {
        if (initialized) {
            try {
                logger.info("Cerrando PersistenceConfig de Kursor Studio...");
                
                // Cerrar kursor-studio
                if (kursorStudioEMF != null) {
                    kursorStudioEMF.close();
                    logger.info("Base de datos de kursor-studio cerrada correctamente");
                }
                
                // Cerrar kursor
                if (kursorEMF != null) {
                    kursorEMF.close();
                    logger.info("Base de datos de kursor cerrada correctamente");
                }
                
                logger.info("PersistenceConfig de Kursor Studio cerrado exitosamente");
            } catch (Exception e) {
                logger.error("Error al cerrar PersistenceConfig de Kursor Studio", e);
            } finally {
                kursorStudioEMF = null;
                kursorEMF = null;
                initialized = false;
            }
        }
    }
    
    /**
     * Verifica si el PersistenceConfig está inicializado.
     * 
     * @return true si está inicializado (al menos kursor-studio), false en caso contrario
     */
    public static boolean isInitialized() {
        return initialized && kursorStudioEMF != null; // Solo requiere kursor-studio
    }
    
    /**
     * Verifica si la base de datos de kursor-studio está disponible.
     * 
     * @return true si está disponible, false en caso contrario
     */
    public static boolean isKursorStudioAvailable() {
        return initialized && kursorStudioEMF != null;
    }
    
    /**
     * Verifica si la base de datos de kursor está disponible.
     * 
     * @return true si está disponible, false en caso contrario
     */
    public static boolean isKursorAvailable() {
        return initialized && kursorEMF != null;
    }
    
    /**
     * Crea un nuevo EntityManager para kursor (versión segura).
     * 
     * @return EntityManager de kursor o null si no está disponible
     */
    public static EntityManager createKursorEntityManagerSafely() {
        if (isKursorAvailable()) {
            return getKursorEntityManagerFactory().createEntityManager();
        }
        return null;
    }
    
    /**
     * Obtiene información de las bases de datos.
     * 
     * @return Información de las bases de datos
     */
    public static String getDatabaseInfo() {
        StringBuilder info = new StringBuilder();
        info.append("PersistenceConfig Status:\n");
        info.append("- Inicializado: ").append(initialized).append("\n");
        info.append("- kursor-studio disponible: ").append(isKursorStudioAvailable()).append("\n");
        info.append("- kursor disponible: ").append(isKursorAvailable()).append("\n");
        
        if (isKursorStudioAvailable()) {
            info.append("✅ La aplicación puede funcionar normalmente\n");
        } else {
            info.append("❌ Error crítico: kursor-studio no disponible\n");
        }
        
        if (!isKursorAvailable()) {
            info.append("ℹ️ La base de datos de Kursor no está disponible\n");
            info.append("ℹ️ Puede configurarla más tarde desde la interfaz\n");
        }
        
        return info.toString();
    }
} 