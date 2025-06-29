package com.kursor.studio;

import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.service.DatabaseConfigurationService;
import com.kursor.studio.model.DatabaseConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de prueba para verificar la conexión a las bases de datos de Kursor Studio.
 * 
 * <p>Esta clase realiza pruebas de conectividad y funcionalidad básica
 * de las bases de datos kursor-studio y kursor en el entorno de testing.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConnectionTest {
    
    /** Logger para registrar eventos de la prueba de conexión */
    private static final Logger logger = LoggerFactory.getLogger(ConnectionTest.class);
    
    public static void main(String[] args) {
        logger.info("🔍 Iniciando prueba de conexión a la base de datos de Kursor...");
        
        try {
            // Inicializar configuración
            logger.debug("Inicializando PersistenceConfig...");
            PersistenceConfig.initialize();
            logger.info("✅ PersistenceConfig inicializado exitosamente");
            
            // Verificar estado de las bases de datos
            logger.info("📊 Estado de las bases de datos:");
            logger.info("  - kursor-studio disponible: {}", PersistenceConfig.isKursorStudioAvailable());
            logger.info("  - kursor disponible: {}", PersistenceConfig.isKursorAvailable());
            
            if (PersistenceConfig.isKursorAvailable()) {
                logger.debug("Procediendo con pruebas de conexión a kursor");
                
                // Probar conexión a kursor
                EntityManager em = PersistenceConfig.createKursorEntityManagerSafely();
                if (em != null) {
                    logger.debug("EntityManager creado exitosamente");
                    
                    Metamodel metamodel = em.getMetamodel();
                    List<String> tableNames = metamodel.getEntities().stream()
                        .map(EntityType::getName)
                        .sorted()
                        .toList();
                    
                    logger.info("📋 Tablas encontradas: {}", tableNames);
                    
                    // Probar consulta simple
                    for (String tableName : tableNames) {
                        try {
                            logger.debug("Probando consulta COUNT en tabla: {}", tableName);
                            jakarta.persistence.Query query = em.createQuery("SELECT COUNT(e) FROM " + tableName + " e");
                            Long count = (Long) query.getSingleResult();
                            logger.info("  - {}: {} registros", tableName, count);
                        } catch (Exception e) {
                            logger.warn("  - {}: Error - {}", tableName, e.getMessage());
                        }
                    }
                    
                    em.close();
                    logger.debug("EntityManager cerrado");
                } else {
                    logger.warn("No se pudo crear EntityManager para kursor");
                }
            } else {
                logger.warn("Base de datos kursor no disponible");
            }
            
            // Probar servicio de configuración
            logger.debug("Probando servicio de configuración...");
            DatabaseConfigurationService configService = new DatabaseConfigurationService();
            logger.info("⚙️ Servicio de configuración:");
            logger.info("  - Tiene configuración: {}", configService.hasConfiguration());
            
            Optional<DatabaseConfiguration> config = configService.getActiveConfiguration();
            if (config.isPresent()) {
                DatabaseConfiguration dbConfig = config.get();
                logger.info("  - Configuración activa: {}", dbConfig.getKursorDatabasePath());
                logger.info("  - Estado: {}", dbConfig.getConnectionStatus());
                logger.info("  - Conectado: {}", dbConfig.isConnected());
            } else {
                logger.warn("No hay configuración activa disponible");
            }
            
            logger.info("✅ Prueba completada exitosamente");
            
        } catch (Exception e) {
            logger.error("❌ Error durante la prueba: {}", e.getMessage(), e);
        } finally {
            logger.debug("Cerrando PersistenceConfig...");
            PersistenceConfig.shutdown();
            logger.info("PersistenceConfig cerrado");
        }
    }
} 