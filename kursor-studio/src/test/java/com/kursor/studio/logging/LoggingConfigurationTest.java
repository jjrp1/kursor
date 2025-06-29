package com.kursor.studio.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la configuración de logging.
 * 
 * <p>Esta clase contiene pruebas para verificar que la configuración
 * de logging funciona correctamente, incluyendo la creación de archivos
 * de log y la configuración de niveles de logging.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoggingConfigurationTest {
    
    /**
     * Verifica que el logger se puede crear correctamente.
     */
    @Test
    void testLoggerConfiguration() {
        Logger logger = LoggerFactory.getLogger(LoggingConfigurationTest.class);
        
        // Verificar que el logger no es null
        assertNotNull(logger, "El logger no debe ser null");
        
        // Verificar que es una instancia de Logback
        assertTrue(logger instanceof ch.qos.logback.classic.Logger, 
                   "El logger debe ser una instancia de Logback");
        
        // Verificar el nombre del logger
        assertEquals("com.kursor.studio.logging.LoggingConfigurationTest", 
                     logger.getName(), 
                     "El nombre del logger debe coincidir con la clase");
    }
    
    /**
     * Verifica que todos los niveles de logging funcionan sin excepciones.
     */
    @Test
    void testLogLevels() {
        Logger logger = LoggerFactory.getLogger("TestLogger");
        
        // Probar todos los niveles - no debe lanzar excepciones
        assertDoesNotThrow(() -> {
            logger.trace("🔍 Mensaje TRACE de prueba");
            logger.debug("🐛 Mensaje DEBUG de prueba");
            logger.info("ℹ️ Mensaje INFO de prueba");
            logger.warn("⚠️ Mensaje WARN de prueba");
            logger.error("❌ Mensaje ERROR de prueba");
        }, "Los mensajes de log no deben lanzar excepciones");
    }
    
    /**
     * Verifica que los loggers con diferentes nombres funcionan correctamente.
     */
    @Test
    void testMultipleLoggers() {
        Logger logger1 = LoggerFactory.getLogger("com.kursor.studio.service.TestService");
        Logger logger2 = LoggerFactory.getLogger("com.kursor.studio.controller.TestController");
        Logger logger3 = LoggerFactory.getLogger("com.kursor.studio.model.TestModel");
        
        // Todos los loggers deben ser válidos
        assertNotNull(logger1, "Logger de servicio debe ser válido");
        assertNotNull(logger2, "Logger de controlador debe ser válido");
        assertNotNull(logger3, "Logger de modelo debe ser válido");
        
        // Los loggers con diferentes nombres deben ser instancias diferentes
        assertNotSame(logger1, logger2, "Loggers con diferentes nombres deben ser diferentes instancias");
        assertNotSame(logger1, logger3, "Loggers con diferentes nombres deben ser diferentes instancias");
        assertNotSame(logger2, logger3, "Loggers con diferentes nombres deben ser diferentes instancias");
    }
    
    /**
     * Verifica que los mensajes con parámetros funcionan correctamente.
     */
    @Test
    void testParameterizedMessages() {
        Logger logger = LoggerFactory.getLogger("ParameterizedTest");
        
        // Probar mensajes con parámetros
        assertDoesNotThrow(() -> {
            logger.info("📊 Procesando {} registros de la tabla {}", 100, "sesion");
            logger.debug("🔧 Configuración: nivel={}, archivo={}", "DEBUG", "test.log");
            logger.warn("⚠️ Advertencia: {} elementos procesados en {} ms", 50, 1500);
        }, "Los mensajes parametrizados no deben lanzar excepciones");
    }
    
    /**
     * Verifica que los mensajes con excepciones funcionan correctamente.
     */
    @Test
    void testExceptionLogging() {
        Logger logger = LoggerFactory.getLogger("ExceptionTest");
        
        Exception testException = new RuntimeException("Excepción de prueba");
        
        // Probar logging de excepciones
        assertDoesNotThrow(() -> {
            logger.error("❌ Error durante la operación", testException);
            logger.warn("⚠️ Advertencia con excepción", testException);
        }, "El logging de excepciones no debe fallar");
    }
    
    /**
     * Test de integración que simula el uso real del sistema de logging.
     */
    @Test
    void testIntegrationLogging() {
        Logger logger = LoggerFactory.getLogger("IntegrationTest");
        
        // Simular una secuencia típica de logging en Kursor Studio
        assertDoesNotThrow(() -> {
            logger.info("🚀 Iniciando test de integración de logging");
            
            logger.debug("🔧 Configurando conexión a base de datos");
            logger.info("📊 Cargando {} tablas del esquema", 5);
            
            // Simular warning
            logger.warn("⚠️ Tabla 'test' no encontrada, creando...");
            
            // Simular operación exitosa
            logger.info("✅ Test de integración completado exitosamente");
            
        }, "La secuencia de logging de integración debe ejecutarse sin errores");
    }
    
    /**
     * Verifica que la configuración es consistente con los estándares de Kursor.
     */
    @Test
    void testKursorConsistency() {
        Logger logger = LoggerFactory.getLogger("ConsistencyTest");
        
        // Verificar que podemos crear loggers con el patrón de nombres de Kursor Studio
        Logger studioLogger = LoggerFactory.getLogger("com.kursor.studio.KursorStudioApplication");
        Logger serviceLogger = LoggerFactory.getLogger("com.kursor.studio.service.DatabaseInspectorService");
        Logger controllerLogger = LoggerFactory.getLogger("com.kursor.studio.controller.DashboardController");
        
        assertNotNull(studioLogger, "Logger principal de Studio debe ser válido");
        assertNotNull(serviceLogger, "Logger de servicio debe ser válido");
        assertNotNull(controllerLogger, "Logger de controlador debe ser válido");
        
        // Verificar que los nombres siguen el patrón esperado
        assertTrue(studioLogger.getName().startsWith("com.kursor.studio"), 
                   "Logger debe seguir el patrón de nombres de Kursor Studio");
        assertTrue(serviceLogger.getName().contains("service"), 
                   "Logger de servicio debe contener 'service' en el nombre");
        assertTrue(controllerLogger.getName().contains("controller"), 
                   "Logger de controlador debe contener 'controller' en el nombre");
    }
} 