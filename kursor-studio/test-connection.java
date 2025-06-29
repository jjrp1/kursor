import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.service.DatabaseConfigurationService;
import com.kursor.studio.model.DatabaseConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Optional;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("🔍 Probando conexión a la base de datos de Kursor...");
        
        try {
            // Inicializar configuración
            PersistenceConfig.initialize();
            System.out.println("✅ PersistenceConfig inicializado");
            
            // Verificar estado de las bases de datos
            System.out.println("📊 Estado de las bases de datos:");
            System.out.println("  - kursor-studio disponible: " + PersistenceConfig.isKursorStudioAvailable());
            System.out.println("  - kursor disponible: " + PersistenceConfig.isKursorAvailable());
            
            if (PersistenceConfig.isKursorAvailable()) {
                // Probar conexión a kursor
                EntityManager em = PersistenceConfig.createKursorEntityManagerSafely();
                if (em != null) {
                    Metamodel metamodel = em.getMetamodel();
                    List<String> tableNames = metamodel.getEntities().stream()
                        .map(EntityType::getName)
                        .sorted()
                        .toList();
                    
                    System.out.println("📋 Tablas encontradas: " + tableNames);
                    
                    // Probar consulta simple
                    for (String tableName : tableNames) {
                        try {
                            jakarta.persistence.Query query = em.createQuery("SELECT COUNT(e) FROM " + tableName + " e");
                            Long count = (Long) query.getSingleResult();
                            System.out.println("  - " + tableName + ": " + count + " registros");
                        } catch (Exception e) {
                            System.out.println("  - " + tableName + ": Error - " + e.getMessage());
                        }
                    }
                    
                    em.close();
                }
            }
            
            // Probar servicio de configuración
            DatabaseConfigurationService configService = new DatabaseConfigurationService();
            System.out.println("⚙️ Servicio de configuración:");
            System.out.println("  - Tiene configuración: " + configService.hasConfiguration());
            
            Optional<DatabaseConfiguration> config = configService.getActiveConfiguration();
            if (config.isPresent()) {
                DatabaseConfiguration dbConfig = config.get();
                System.out.println("  - Configuración activa: " + dbConfig.getKursorDatabasePath());
                System.out.println("  - Estado: " + dbConfig.getConnectionStatus());
                System.out.println("  - Conectado: " + dbConfig.isConnected());
            }
            
            System.out.println("✅ Prueba completada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        } finally {
            PersistenceConfig.shutdown();
        }
    }
} 