package com.kursor.studio.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el servicio DatabaseInspectorService.
 * 
 * <p>Estos tests verifican que:</p>
 * <ul>
 *   <li>Las entidades JPA se importan correctamente desde kursor-core</li>
 *   <li>El servicio puede acceder a las clases importadas</li>
 *   <li>La validación de entidades funciona</li>
 *   <li>Las métricas se generan correctamente</li>
 * </ul>
 * 
 * @author Sistema Kursor
 * @version 1.0.0
 */
public class DatabaseInspectorServiceTest {
    
    /** Logger para registrar eventos del test */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInspectorServiceTest.class);
    
    /** Servicio bajo prueba */
    private DatabaseInspectorService service;
    
    /**
     * Configuración previa a cada test.
     */
    @BeforeEach
    void setUp() {
        logger.info("🧪 Inicializando test para DatabaseInspectorService");
        service = new DatabaseInspectorService();
    }
    
    /**
     * Verifica que las entidades JPA se importan correctamente desde kursor-core.
     */
    @Test
    void testImportacionEntidadesJPA() {
        logger.info("🔍 Test: Verificar importación de entidades JPA desde kursor-core");
        
        List<Class<?>> entidades = service.getEntidadesJPA();
        
        // Verificar que se importaron entidades
        assertNotNull(entidades, "La lista de entidades no debe ser null");
        assertFalse(entidades.isEmpty(), "Debe haber al menos una entidad importada");
        
        // Verificar número esperado de entidades
        assertEquals(5, entidades.size(), "Deben importarse exactamente 5 entidades de kursor-core");
        
        logger.info("✅ Test pasado: {} entidades importadas correctamente", entidades.size());
    }
    
    /**
     * Verifica que las entidades específicas están presentes.
     */
    @Test
    void testEntidadesEspecificas() {
        logger.info("📋 Test: Verificar entidades específicas de kursor-core");
        
        List<Class<?>> entidades = service.getEntidadesJPA();
        
        // Verificar nombres de entidades esperadas
        List<String> nombresEsperados = List.of(
            "Sesion", 
            "EstadoEstrategia", 
            "RespuestaPregunta", 
            "EstadisticasUsuario", 
            "EstadoSesion"
        );
        
        for (String nombreEsperado : nombresEsperados) {
            boolean encontrada = entidades.stream()
                .anyMatch(entidad -> entidad.getSimpleName().equals(nombreEsperado));
            
            assertTrue(encontrada, "Entidad '" + nombreEsperado + "' debe estar presente");
            logger.debug("✅ Entidad encontrada: {}", nombreEsperado);
        }
        
        logger.info("✅ Test pasado: Todas las entidades específicas están presentes");
    }
    
    /**
     * Verifica que las entidades pertenecen al paquete correcto de kursor-core.
     */
    @Test 
    void testPaqueteEntidades() {
        logger.info("📦 Test: Verificar paquetes de entidades de kursor-core");
        
        List<Class<?>> entidades = service.getEntidadesJPA();
        
        for (Class<?> entidad : entidades) {
            String paquete = entidad.getPackageName();
            
            // Verificar que provienen del paquete de persistencia de kursor-core
            assertTrue(paquete.startsWith("com.kursor.persistence.entity"), 
                      "Entidad " + entidad.getSimpleName() + " debe estar en el paquete com.kursor.persistence.entity");
            
            logger.debug("✅ Paquete verificado: {} -> {}", entidad.getSimpleName(), paquete);
        }
        
        logger.info("✅ Test pasado: Todos los paquetes son correctos");
    }
    
    /**
     * Verifica que la validación de entidades funciona.
     */
    @Test
    void testValidacionEntidades() {
        logger.info("🔧 Test: Verificar validación de entidades");
        
        boolean validacionExitosa = service.validarEntidades();
        
        assertTrue(validacionExitosa, "La validación de entidades debe ser exitosa");
        
        logger.info("✅ Test pasado: Validación de entidades exitosa");
    }
    
    /**
     * Verifica que la información de tablas se genera correctamente.
     */
    @Test
    void testInformacionTablas() {
        logger.info("📋 Test: Verificar generación de información de tablas");
        
        String informacion = service.getInformacionTablas();
        
        assertNotNull(informacion, "La información de tablas no debe ser null");
        assertFalse(informacion.trim().isEmpty(), "La información debe tener contenido");
        
        // Verificar que contiene elementos esperados
        assertTrue(informacion.contains("ENTIDADES JPA IMPORTADAS DESDE KURSOR-CORE"), 
                  "Debe contener título de entidades");
        assertTrue(informacion.contains("Sesion"), "Debe mencionar entidad Sesion");
        assertTrue(informacion.contains("com.kursor.persistence.entity"), "Debe mencionar el paquete");
        assertTrue(informacion.contains("Total de entidades: 5"), "Debe mostrar el total correcto");
        
        logger.debug("📄 Información generada (primeras 200 chars): {}", 
                    informacion.length() > 200 ? informacion.substring(0, 200) + "..." : informacion);
        logger.info("✅ Test pasado: Información de tablas generada correctamente");
    }
    
    /**
     * Verifica que las métricas del dashboard se generan correctamente.
     */
    @Test
    void testMetricasDashboard() {
        logger.info("📊 Test: Verificar generación de métricas del dashboard");
        
        DatabaseInspectorService.DashboardMetrics metrics = service.getMetricasDashboard();
        
        assertNotNull(metrics, "Las métricas no deben ser null");
        
        // Verificar valores esperados
        assertEquals(5, metrics.totalEntidades, "Total de entidades debe ser 5");
        assertTrue(metrics.sesionesRegistradas > 0, "Debe haber sesiones registradas");
        assertTrue(metrics.respuestasRegistradas > 0, "Debe haber respuestas registradas"); 
        assertTrue(metrics.estrategiasDisponibles > 0, "Debe haber estrategias disponibles");
        assertNotNull(metrics.estadoConexion, "Estado de conexión no debe ser null");
        assertNotNull(metrics.ultimaValidacion, "Última validación no debe ser null");
        
        logger.info("📊 Métricas generadas: {}", metrics.toString());
        logger.info("✅ Test pasado: Métricas del dashboard generadas correctamente");
    }
    
    /**
     * Test de integración que verifica el flujo completo.
     */
    @Test
    void testIntegracionCompleta() {
        logger.info("🔄 Test de integración: Flujo completo del servicio");
        
        // 1. Obtener entidades
        List<Class<?>> entidades = service.getEntidadesJPA();
        assertNotNull(entidades);
        assertEquals(5, entidades.size());
        
        // 2. Validar entidades
        boolean validacion = service.validarEntidades();
        assertTrue(validacion);
        
        // 3. Generar información
        String informacion = service.getInformacionTablas();
        assertNotNull(informacion);
        assertFalse(informacion.trim().isEmpty());
        
        // 4. Generar métricas
        DatabaseInspectorService.DashboardMetrics metrics = service.getMetricasDashboard();
        assertNotNull(metrics);
        assertEquals(5, metrics.totalEntidades);
        
        logger.info("✅ Test de integración pasado: Flujo completo funciona correctamente");
    }
} 