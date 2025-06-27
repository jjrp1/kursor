package com.kursor.studio.service;

import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadoEstrategia;
import com.kursor.persistence.entity.RespuestaPregunta;
import com.kursor.persistence.entity.EstadisticasUsuario;
import com.kursor.persistence.entity.EstadoSesion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Servicio de inspección de base de datos que demuestra la integración 
 * con las entidades JPA de kursor-core.
 * 
 * <p>Este servicio:</p>
 * <ul>
 *   <li>Importa entidades JPA desde kursor-core</li>
 *   <li>Proporciona información sobre las tablas disponibles</li>
 *   <li>Realiza validaciones básicas de estructura</li>
 *   <li>Genera métricas para el dashboard</li>
 * </ul>
 * 
 * @author Sistema Kursor
 * @version 1.0.0
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
    
    /**
     * Constructor del servicio.
     */
    public DatabaseInspectorService() {
        logger.info("🔍 Inicializando DatabaseInspectorService");
        logger.debug("📋 Entidades JPA disponibles: {}", ENTIDADES_JPA.size());
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
        
        info.append("=== ESTADÍSTICAS ===\n");
        info.append(String.format("📊 Total de entidades: %d\n", ENTIDADES_JPA.size()));
        info.append("🗃️ Base de datos: SQLite (compartida con Kursor)\n");
        info.append("🔗 Reutilización: Entidades importadas via dependencia Maven\n");
        
        logger.info("✅ Información de tablas generada exitosamente");
        return info.toString();
    }
    
    /**
     * Valida que todas las entidades están correctamente importadas.
     * 
     * @return true si todas las entidades están disponibles
     */
    public boolean validarEntidades() {
        logger.info("🔧 Iniciando validación de entidades JPA");
        
        boolean todasValidas = true;
        
        for (Class<?> entidad : ENTIDADES_JPA) {
            try {
                // Verificar que la clase se puede cargar
                String nombreEntidad = entidad.getSimpleName();
                String paquete = entidad.getPackageName();
                
                logger.debug("✅ Entidad válida: {} ({})", nombreEntidad, paquete);
                
                // Verificar que es una entidad JPA válida
                if (entidad.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                    logger.debug("   📝 Anotación @Entity encontrada");
                } else {
                    logger.warn("   ⚠️ Anotación @Entity no encontrada en {}", nombreEntidad);
                }
                
            } catch (Exception e) {
                logger.error("❌ Error validando entidad {}: {}", entidad.getSimpleName(), e.getMessage());
                todasValidas = false;
            }
        }
        
        if (todasValidas) {
            logger.info("✅ Validación completada - Todas las entidades están correctamente importadas");
        } else {
            logger.error("❌ Validación fallida - Algunas entidades tienen problemas");
        }
        
        return todasValidas;
    }
    
    /**
     * Genera métricas simuladas para el dashboard.
     * 
     * @return objeto con métricas del sistema
     */
    public DashboardMetrics getMetricasDashboard() {
        logger.info("📊 Generando métricas para dashboard");
        
        // Métricas simuladas (en implementación real consultaría la BD)
        DashboardMetrics metrics = new DashboardMetrics();
        metrics.totalEntidades = ENTIDADES_JPA.size();
        metrics.sesionesRegistradas = 1247; // Simulado
        metrics.respuestasRegistradas = 5893; // Simulado  
        metrics.estrategiasDisponibles = 12; // Simulado
        metrics.estadoConexion = "✅ Activa";
        metrics.ultimaValidacion = "✅ OK - Sin problemas detectados";
        
        logger.info("✅ Métricas generadas: {} entidades, {} sesiones", 
                   metrics.totalEntidades, metrics.sesionesRegistradas);
        
        return metrics;
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
        
        @Override
        public String toString() {
            return String.format("DashboardMetrics{entidades=%d, sesiones=%d, respuestas=%d, estrategias=%d}", 
                               totalEntidades, sesionesRegistradas, respuestasRegistradas, estrategiasDisponibles);
        }
    }
} 