package com.kursor.studio.service;

import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadoEstrategia;
import com.kursor.persistence.entity.RespuestaPregunta;
import com.kursor.persistence.entity.EstadisticasUsuario;
import com.kursor.persistence.entity.EstadoSesion;
import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.model.DatabaseStatistics;
import com.kursor.studio.model.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Servicio de inspección de base de datos que utiliza servicios reales
 * para obtener información de la base de datos de Kursor.
 * 
 * <p>Este servicio:</p>
 * <ul>
 *   <li>Utiliza entidades JPA desde kursor-core</li>
 *   <li>Proporciona información real sobre las tablas disponibles</li>
 *   <li>Realiza validaciones de conexión y estructura</li>
 *   <li>Genera métricas reales para el dashboard</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 */
public class DatabaseInspectorService {
    
    /** Logger para registrar eventos del servicio */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInspectorService.class);
    
    /**
     * Lista de entidades JPA disponibles importadas desde kursor-core.
     */
    private static final List<Class<?>> ENTIDADES_JPA = Arrays.asList(
        Sesion.class,
        EstadoEstrategia.class, 
        RespuestaPregunta.class,
        EstadisticasUsuario.class,
        EstadoSesion.class
    );
    
    private final DatabaseConnectionService connectionService;
    private final DatabaseStatisticsService statisticsService;
    
    /**
     * Constructor del servicio.
     */
    public DatabaseInspectorService() {
        logger.info("🔍 Inicializando DatabaseInspectorService");
        logger.debug("📋 Entidades JPA disponibles: {}", ENTIDADES_JPA.size());
        
        // Inicializar servicios de base de datos
        this.connectionService = new DatabaseConnectionService();
        this.statisticsService = new DatabaseStatisticsService(connectionService);
        
        // Inicializar configuración de persistencia si no está inicializada
        if (!PersistenceConfig.isInitialized()) {
            try {
                PersistenceConfig.initialize();
                logger.info("✅ Configuración de persistencia inicializada");
            } catch (Exception e) {
                logger.error("❌ Error al inicializar configuración de persistencia", e);
            }
        }
    }
    
    /**
     * Obtiene la lista de todas las entidades JPA disponibles.
     * 
     * @return lista de clases de entidades JPA
     */
    public List<Class<?>> getEntidadesJPA() {
        logger.debug("📊 Obteniendo lista de entidades JPA");
        return ENTIDADES_JPA;
    }
    
    /**
     * Obtiene información detallada sobre las tablas de base de datos.
     * 
     * @return información de las tablas como texto
     */
    public String getInformacionTablas() {
        logger.info("📋 Generando información de tablas de base de datos");
        
        StringBuilder info = new StringBuilder();
        info.append("=== ENTIDADES JPA IMPORTADAS DESDE KURSOR-CORE ===\n\n");
        
        for (Class<?> entidad : ENTIDADES_JPA) {
            info.append(String.format("📋 %s\n", entidad.getSimpleName()));
            info.append(String.format("   Package: %s\n", entidad.getPackageName()));
            info.append(String.format("   Campos: %d\n", entidad.getDeclaredFields().length));
            info.append("\n");
            
            logger.debug("✅ Procesada entidad: {}", entidad.getSimpleName());
        }
        
        // Información real de la base de datos
        info.append("=== INFORMACIÓN REAL DE LA BASE DE DATOS ===\n");
        
        if (connectionService.isConnected()) {
            info.append("🔗 Estado de conexión: ✅ Conectado\n");
            info.append(String.format("🗃️ Ruta de BD: %s\n", connectionService.getDatabasePath()));
            info.append(String.format("📏 Tamaño de BD: %s\n", formatFileSize(connectionService.getDatabaseSize())));
            
            List<String> tableNames = connectionService.getTableNames();
            info.append(String.format("📊 Tablas encontradas: %d\n", tableNames.size()));
            
            for (String tableName : tableNames) {
                long count = connectionService.getRecordCount(tableName);
                info.append(String.format("   - %s: %d registros\n", tableName, count));
            }
        } else {
            info.append("🔗 Estado de conexión: ❌ No conectado\n");
            info.append("⚠️ No se puede obtener información de la base de datos\n");
        }
        
        info.append("\n=== ESTADÍSTICAS ===\n");
        info.append(String.format("📊 Total de entidades: %d\n", ENTIDADES_JPA.size()));
        info.append("🗃️ Base de datos: SQLite (compartida con Kursor)\n");
        info.append("🔗 Reutilización: Entidades importadas via dependencia Maven\n");
        
        logger.info("✅ Información de tablas generada exitosamente");
        return info.toString();
    }
    
    /**
     * Valida que todas las entidades están correctamente importadas y la conexión funciona.
     * 
     * @return true si todas las entidades están disponibles y la conexión funciona
     */
    public boolean validarEntidades() {
        logger.info("🔧 Iniciando validación de entidades JPA y conexión");
        
        boolean todasValidas = true;
        
        // Validar entidades JPA
        for (Class<?> entidad : ENTIDADES_JPA) {
            try {
                String nombreEntidad = entidad.getSimpleName();
                String paquete = entidad.getPackageName();
                
                logger.debug("✅ Entidad válida: {} ({})", nombreEntidad, paquete);
                
                if (entidad.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                    logger.debug("   📝 Anotación @Entity encontrada");
                } else {
                    logger.warn("   ⚠️ Anotación @Entity no encontrada en {}", nombreEntidad);
                    todasValidas = false;
                }
                
            } catch (Exception e) {
                logger.error("❌ Error validando entidad {}: {}", entidad.getSimpleName(), e.getMessage());
                todasValidas = false;
            }
        }
        
        // Validar conexión a la base de datos
        if (connectionService.isConnected()) {
            logger.info("✅ Conexión a la base de datos verificada");
        } else {
            logger.error("❌ No se puede conectar a la base de datos");
            todasValidas = false;
        }
        
        if (todasValidas) {
            logger.info("✅ Validación completada - Todas las entidades están correctamente importadas y la conexión funciona");
        } else {
            logger.error("❌ Validación fallida - Algunas entidades tienen problemas o la conexión falla");
        }
        
        return todasValidas;
    }
    
    /**
     * Obtiene estadísticas reales de la base de datos.
     * 
     * @return estadísticas reales de la base de datos
     */
    public DatabaseStatistics getRealStatistics() {
        logger.info("📊 Obteniendo estadísticas reales de la base de datos");
        
        try {
            DatabaseStatistics stats = statisticsService.getStatistics();
            logger.info("✅ Estadísticas obtenidas: {} registros totales", stats.getTotalRecords());
            return stats;
        } catch (Exception e) {
            logger.error("❌ Error al obtener estadísticas reales", e);
            return createEmptyStatistics();
        }
    }
    
    /**
     * Genera métricas reales para el dashboard.
     * 
     * @return objeto con métricas reales del sistema
     */
    public DashboardMetrics getMetricasDashboard() {
        logger.info("📊 Generando métricas reales para dashboard");
        
        DashboardMetrics metrics = new DashboardMetrics();
        
        try {
            DatabaseStatistics stats = getRealStatistics();
            
            metrics.totalEntidades = ENTIDADES_JPA.size();
            metrics.sesionesRegistradas = (int) stats.getTotalSessions();
            metrics.respuestasRegistradas = (int) stats.getTotalQuestionResponses();
            metrics.estrategiasDisponibles = (int) stats.getTotalStrategyStates();
            metrics.estadoConexion = connectionService.isConnected() ? "✅ Activa" : "❌ Inactiva";
            metrics.ultimaValidacion = validarEntidades() ? "✅ OK - Sin problemas detectados" : "❌ Problemas detectados";
            metrics.totalRegistros = (int) stats.getTotalRecords();
            metrics.tamañoBaseDatos = stats.getFormattedDatabaseSize();
            
            logger.info("✅ Métricas reales generadas: {} entidades, {} sesiones, {} registros totales", 
                       metrics.totalEntidades, metrics.sesionesRegistradas, metrics.totalRegistros);
            
        } catch (Exception e) {
            logger.error("❌ Error al generar métricas reales, usando valores por defecto", e);
            metrics.estadoConexion = "❌ Error";
            metrics.ultimaValidacion = "❌ Error al obtener datos";
        }
        
        return metrics;
    }
    
    /**
     * Obtiene estadísticas de rendimiento por estrategia.
     * 
     * @return mapa con estadísticas por estrategia
     */
    public Map<String, Long> getEstadisticasEstrategias() {
        logger.info("📊 Obteniendo estadísticas de rendimiento por estrategia");
        
        try {
            Map<String, Long> stats = statisticsService.getStrategyPerformanceStats();
            logger.info("✅ Estadísticas de estrategias obtenidas: {} tipos", stats.size());
            return stats;
        } catch (Exception e) {
            logger.error("❌ Error al obtener estadísticas de estrategias", e);
            return Map.of();
        }
    }
    
    /**
     * Verifica la conexión a la base de datos.
     * 
     * @return true si la conexión está activa
     */
    public boolean testConnection() {
        return connectionService.isConnected();
    }
    
    /**
     * Crea estadísticas vacías cuando hay errores.
     * 
     * @return estadísticas vacías
     */
    private DatabaseStatistics createEmptyStatistics() {
        return new DatabaseStatistics.Builder()
                .databasePath(connectionService.getDatabasePath())
                .databaseSize(0)
                .databaseExists(false)
                .lastUpdated(java.time.LocalDateTime.now())
                .build();
    }
    
    /**
     * Formatea el tamaño de archivo en formato legible.
     * 
     * @param size Tamaño en bytes
     * @return Tamaño formateado
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Obtiene información detallada de una tabla específica.
     * 
     * @param tableName Nombre de la tabla
     * @return Información detallada de la tabla
     */
    public TableInfo getTableInfo(String tableName) {
        try {
            // Obtener esquema de la tabla
            List<String> schema = connectionService.getTableSchema(tableName);
            
            // Obtener número de registros
            long recordCount = connectionService.getRecordCount(tableName);
            
            // Crear objeto de información de tabla
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(tableName);
            tableInfo.setSchema(schema);
            tableInfo.setRecordCount(recordCount);
            
            logger.debug("Información de tabla obtenida: {}", tableName);
            return tableInfo;
            
        } catch (Exception e) {
            logger.error("Error al obtener información de tabla {}", tableName, e);
            return null;
        }
    }
    
    /**
     * Clase para encapsular las métricas del dashboard.
     */
    public static class DashboardMetrics {
        public int totalEntidades;
        public int sesionesRegistradas;
        public int respuestasRegistradas;
        public int estrategiasDisponibles;
        public String estadoConexion;
        public String ultimaValidacion;
        public int totalRegistros;
        public String tamañoBaseDatos;
        
        @Override
        public String toString() {
            return String.format("DashboardMetrics{entidades=%d, sesiones=%d, respuestas=%d, estrategias=%d, registros=%d, tamaño=%s}", 
                               totalEntidades, sesionesRegistradas, respuestasRegistradas, estrategiasDisponibles, totalRegistros, tamañoBaseDatos);
        }
    }
} 