package com.kursor.presentation.controllers;

import com.kursor.application.services.AnalyticsService;
import com.kursor.application.services.AnalyticsService.DashboardMetrics;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de analytics y estadísticas de aprendizaje.
 * 
 * <p>Este controlador actúa como intermediario entre la capa de presentación
 * y el servicio de analytics, proporcionando métodos para obtener y procesar
 * datos estadísticos del aprendizaje del usuario.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * Constructor del controlador de analytics.
     * 
     * @param analyticsService El servicio de analytics
     */
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    
    /**
     * Actualiza los analytics con los parámetros especificados.
     * 
     * @param usuarioId El identificador del usuario
     * @param cursoId El identificador del curso (opcional, null para todos)
     * @param bloqueId El identificador del bloque (opcional, null para todos)
     * @param periodoDias El período en días
     * @return Las métricas del dashboard
     */
    public DashboardMetrics actualizarAnalytics(Long usuarioId, Long cursoId, Long bloqueId, int periodoDias) {
        // Convertir Long a String para el servicio
        String cursoIdStr = cursoId != null ? cursoId.toString() : null;
        String bloqueIdStr = bloqueId != null ? bloqueId.toString() : null;
        
        // Determinar el período basado en los días
        String periodo;
        if (periodoDias <= 1) {
            periodo = "Última sesión";
        } else if (periodoDias <= 7) {
            periodo = "Esta semana";
        } else {
            periodo = "Este mes";
        }
        
        return analyticsService.getDashboardMetrics(cursoIdStr, bloqueIdStr, periodo);
    }
    
    /**
     * Obtiene las métricas del dashboard para un curso específico.
     * 
     * @param cursoId El identificador del curso
     * @param bloqueId El identificador del bloque (opcional, null para todos)
     * @param periodo El período de análisis
     * @return Las métricas del dashboard
     */
    public DashboardMetrics getDashboardMetrics(String cursoId, String bloqueId, String periodo) {
        return analyticsService.getDashboardMetrics(cursoId, bloqueId, periodo);
    }
    
    /**
     * Obtiene las métricas comparativas de estrategias.
     * 
     * @param cursoId El identificador del curso
     * @return Lista de métricas por estrategia
     */
    public List<Map<String, Object>> getEstrategiasMetrics(String cursoId) {
        return analyticsService.getEstrategiasMetrics(cursoId);
    }
    
    /**
     * Obtiene recomendaciones personalizadas basadas en el rendimiento.
     * 
     * @param cursoId El identificador del curso
     * @return Lista de recomendaciones
     */
    public List<Map<String, String>> getRecomendaciones(String cursoId) {
        return analyticsService.getRecomendaciones(cursoId);
    }
    
    /**
     * Calcula el progreso general del curso.
     * 
     * @param cursoId El identificador del curso
     * @return El porcentaje de progreso (0-100)
     */
    public double calcularProgresoCurso(String cursoId) {
        return analyticsService.calcularProgresoCurso(cursoId);
    }
    
    /**
     * Obtiene estadísticas de tiempo de respuesta.
     * 
     * @param cursoId El identificador del curso
     * @param bloqueId El identificador del bloque (opcional)
     * @return Estadísticas de tiempo
     */
    public Map<String, Object> getTiempoEstadisticas(String cursoId, String bloqueId) {
        return analyticsService.getTiempoEstadisticas(cursoId, bloqueId);
    }
    
    /**
     * Obtiene el servicio de analytics subyacente.
     * 
     * @return El servicio de analytics
     */
    public AnalyticsService getAnalyticsService() {
        return analyticsService;
    }
} 