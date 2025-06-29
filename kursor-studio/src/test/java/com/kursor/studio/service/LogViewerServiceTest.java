package com.kursor.studio.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el servicio LogViewerService.
 * 
 * <p>Esta clase contiene pruebas exhaustivas para verificar el funcionamiento
 * correcto del servicio de visualización de logs, incluyendo la carga de archivos,
 * el monitoreo en tiempo real y el manejo de errores.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see LogViewerService
 */
public class LogViewerServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(LogViewerServiceTest.class);
    
    private LogViewerService logViewerService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        logger.info("🧪 Configurando test de LogViewerService");
        logViewerService = new LogViewerService();
    }
    
    @Test
    void testGetLogDirectory() {
        logger.debug("📁 Probando obtención del directorio de logs");
        
        String logDir = logViewerService.getLogDirectory();
        
        assertNotNull(logDir);
        assertFalse(logDir.isEmpty());
        
        logger.debug("✅ Directorio de logs obtenido: {}", logDir);
    }
    
    @Test
    void testCheckLogFilesExist_WhenFilesExist() throws IOException {
        logger.debug("📄 Probando verificación de archivos de log existentes");
        
        // Crear archivos de log de prueba
        Path mainLogFile = tempDir.resolve("kursor-studio.log");
        Path errorLogFile = tempDir.resolve("error.log");
        
        Files.write(mainLogFile, "Test log content".getBytes());
        Files.write(errorLogFile, "Error log content".getBytes());
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        boolean filesExist = testService.checkLogFilesExist();
        
        assertTrue(filesExist);
        
        logger.debug("✅ Verificación de archivos exitosa");
    }
    
    @Test
    void testCheckLogFilesExist_WhenFilesDoNotExist() {
        logger.debug("📄 Probando verificación de archivos de log inexistentes");
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        boolean filesExist = testService.checkLogFilesExist();
        
        assertFalse(filesExist);
        
        logger.debug("✅ Verificación de archivos inexistentes exitosa");
    }
    
    @Test
    void testGetRecentLogs() throws IOException {
        logger.debug("📖 Probando lectura de logs recientes");
        
        // Crear archivo de log de prueba con múltiples líneas
        Path logFile = tempDir.resolve("kursor-studio.log");
        String[] logLines = {
            "[2025-01-15 10:00:00.000] [INFO ] Test message 1",
            "[2025-01-15 10:00:01.000] [DEBUG] Test message 2",
            "[2025-01-15 10:00:02.000] [WARN ] Test message 3",
            "[2025-01-15 10:00:03.000] [ERROR] Test message 4",
            "[2025-01-15 10:00:04.000] [INFO ] Test message 5"
        };
        
        Files.write(logFile, String.join("\n", logLines).getBytes());
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        List<String> recentLogs = testService.getRecentLogs(3);
        
        assertEquals(3, recentLogs.size());
        assertTrue(recentLogs.get(0).contains("Test message 3"));
        assertTrue(recentLogs.get(1).contains("Test message 4"));
        assertTrue(recentLogs.get(2).contains("Test message 5"));
        
        logger.debug("✅ Lectura de logs recientes exitosa");
    }
    
    @Test
    void testFilterLogsByLevel() throws IOException {
        logger.debug("🔍 Probando filtrado de logs por nivel");
        
        // Crear archivo de log de prueba
        Path logFile = tempDir.resolve("kursor-studio.log");
        String[] logLines = {
            "[2025-01-15 10:00:00.000] [INFO ] Info message",
            "[2025-01-15 10:00:01.000] [DEBUG] Debug message",
            "[2025-01-15 10:00:02.000] [WARN ] Warning message",
            "[2025-01-15 10:00:03.000] [ERROR] Error message",
            "[2025-01-15 10:00:04.000] [INFO ] Another info message"
        };
        
        Files.write(logFile, String.join("\n", logLines).getBytes());
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        List<String> infoLogs = testService.filterLogsByLevel("INFO", 10);
        
        assertEquals(2, infoLogs.size());
        assertTrue(infoLogs.get(0).contains("Info message"));
        assertTrue(infoLogs.get(1).contains("Another info message"));
        
        logger.debug("✅ Filtrado de logs por nivel exitoso");
    }
    
    @Test
    void testSearchLogs() throws IOException {
        logger.debug("🔍 Probando búsqueda de texto en logs");
        
        // Crear archivo de log de prueba
        Path logFile = tempDir.resolve("kursor-studio.log");
        String[] logLines = {
            "[2025-01-15 10:00:00.000] [INFO ] User login successful",
            "[2025-01-15 10:00:01.000] [DEBUG] Database connection established",
            "[2025-01-15 10:00:02.000] [WARN ] User session expired",
            "[2025-01-15 10:00:03.000] [ERROR] Database connection failed",
            "[2025-01-15 10:00:04.000] [INFO ] User logout successful"
        };
        
        Files.write(logFile, String.join("\n", logLines).getBytes());
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        List<String> userLogs = testService.searchLogs("User", 10);
        
        assertEquals(3, userLogs.size());
        assertTrue(userLogs.stream().allMatch(log -> log.contains("User")));
        
        logger.debug("✅ Búsqueda de texto en logs exitosa");
    }
    
    @Test
    void testGetLogStatistics() throws IOException {
        logger.debug("📊 Probando generación de estadísticas de logs");
        
        // Crear archivo de log de prueba
        Path logFile = tempDir.resolve("kursor-studio.log");
        String[] logLines = {
            "[2025-01-15 10:00:00.000] [INFO ] Info message 1",
            "[2025-01-15 10:00:01.000] [DEBUG] Debug message 1",
            "[2025-01-15 10:00:02.000] [WARN ] Warning message 1",
            "[2025-01-15 10:00:03.000] [ERROR] Error message 1",
            "[2025-01-15 10:00:04.000] [INFO ] Info message 2",
            "[2025-01-15 10:00:05.000] [DEBUG] Debug message 2"
        };
        
        Files.write(logFile, String.join("\n", logLines).getBytes());
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        LogViewerService.LogStatistics stats = testService.getLogStatistics();
        
        assertEquals(6, stats.totalLines);
        assertEquals(2, stats.debugCount);
        assertEquals(2, stats.infoCount);
        assertEquals(1, stats.warnCount);
        assertEquals(1, stats.errorCount);
        assertTrue(stats.mainLogSize > 0);
        
        logger.debug("✅ Generación de estadísticas exitosa: {}", stats);
    }
    
    @Test
    void testGetLogFileInfo() throws IOException {
        logger.debug("📄 Probando obtención de información del archivo de log");
        
        // Crear archivo de log de prueba
        Path logFile = tempDir.resolve("kursor-studio.log");
        String content = "Test log content for file info";
        Files.write(logFile, content.getBytes());
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        LogViewerService.LogFileInfo fileInfo = testService.getLogFileInfo();
        
        assertTrue(fileInfo.exists);
        assertEquals(logFile.toString(), fileInfo.filePath);
        assertEquals(content.length(), fileInfo.fileSize);
        assertTrue(fileInfo.canRead);
        assertTrue(fileInfo.lastModified > 0);
        
        logger.debug("✅ Información del archivo obtenida: {}", fileInfo);
    }
    
    @Test
    void testGetLogFileInfo_WhenFileDoesNotExist() {
        logger.debug("📄 Probando obtención de información de archivo inexistente");
        
        // Configurar propiedad del sistema ANTES de crear el servicio
        System.setProperty("kursor.studio.log.dir", tempDir.toString());
        LogViewerService testService = new LogViewerService();
        
        LogViewerService.LogFileInfo fileInfo = testService.getLogFileInfo();
        
        assertFalse(fileInfo.exists);
        
        logger.debug("✅ Información de archivo inexistente obtenida correctamente");
    }
    
    @Test
    void testInitialization() {
        logger.info("🧪 Probando inicialización del servicio");
        
        assertNotNull(logViewerService);
        assertNotNull(logViewerService.getCurrentLogFile());
        assertTrue(logViewerService.getCurrentLogFileName().contains("kursor-studio.log"));
        
        logger.info("✅ Inicialización correcta");
    }
    
    @Test
    void testSetCurrentLogFile() throws IOException {
        logger.info("🧪 Probando cambio de archivo de log");
        
        // Crear un archivo de log temporal
        Path testLogFile = tempDir.resolve("test.log");
        Files.write(testLogFile, List.of(
            "[2025-01-15 10:00:00] [INFO ] Test log line 1",
            "[2025-01-15 10:00:01] [DEBUG] Test log line 2",
            "[2025-01-15 10:00:02] [WARN ] Test log line 3",
            "[2025-01-15 10:00:03] [ERROR] Test log line 4"
        ));
        
        // Cambiar al archivo de test
        logViewerService.setCurrentLogFile(testLogFile);
        
        assertEquals(testLogFile, logViewerService.getCurrentLogFile());
        assertEquals("test.log", logViewerService.getCurrentLogFileName());
        assertTrue(logViewerService.isCurrentLogFileValid());
        
        logger.info("✅ Cambio de archivo correcto");
    }
    
    @Test
    void testSetCurrentLogFileFromString() throws IOException {
        logger.info("🧪 Probando cambio de archivo de log desde String");
        
        // Crear un archivo de log temporal
        Path testLogFile = tempDir.resolve("test-string.log");
        Files.write(testLogFile, List.of(
            "[2025-01-15 10:00:00] [INFO ] Test log line 1"
        ));
        
        // Cambiar al archivo de test usando String
        logViewerService.setCurrentLogFile(testLogFile.toString());
        
        assertEquals(testLogFile, logViewerService.getCurrentLogFile());
        assertEquals("test-string.log", logViewerService.getCurrentLogFileName());
        assertTrue(logViewerService.isCurrentLogFileValid());
        
        logger.info("✅ Cambio de archivo desde String correcto");
    }
    
    @Test
    void testInvalidLogFile() {
        logger.info("🧪 Probando archivo de log inválido");
        
        // Establecer un archivo que no existe
        Path invalidFile = tempDir.resolve("nonexistent.log");
        logViewerService.setCurrentLogFile(invalidFile);
        
        assertEquals(invalidFile, logViewerService.getCurrentLogFile());
        assertEquals("nonexistent.log", logViewerService.getCurrentLogFileName());
        assertFalse(logViewerService.isCurrentLogFileValid());
        
        logger.info("✅ Validación de archivo inválido correcta");
    }
    
    @Test
    void testGetRecentLogs() throws IOException {
        logger.info("🧪 Probando lectura de logs recientes");
        
        // Crear un archivo de log temporal con múltiples líneas
        Path testLogFile = tempDir.resolve("recent-test.log");
        List<String> logLines = List.of(
            "[2025-01-15 10:00:00] [INFO ] Log line 1",
            "[2025-01-15 10:00:01] [DEBUG] Log line 2",
            "[2025-01-15 10:00:02] [WARN ] Log line 3",
            "[2025-01-15 10:00:03] [ERROR] Log line 4",
            "[2025-01-15 10:00:04] [INFO ] Log line 5"
        );
        Files.write(testLogFile, logLines);
        
        logViewerService.setCurrentLogFile(testLogFile);
        
        // Leer las últimas 3 líneas
        List<String> recentLogs = logViewerService.getRecentLogs(3);
        
        assertEquals(3, recentLogs.size());
        assertTrue(recentLogs.get(0).contains("Log line 3"));
        assertTrue(recentLogs.get(1).contains("Log line 4"));
        assertTrue(recentLogs.get(2).contains("Log line 5"));
        
        logger.info("✅ Lectura de logs recientes correcta");
    }
    
    @Test
    void testFilterLogsByLevel() throws IOException {
        logger.info("🧪 Probando filtrado por nivel");
        
        // Crear un archivo de log temporal
        Path testLogFile = tempDir.resolve("filter-test.log");
        List<String> logLines = List.of(
            "[2025-01-15 10:00:00] [INFO ] Info message 1",
            "[2025-01-15 10:00:01] [DEBUG] Debug message 1",
            "[2025-01-15 10:00:02] [INFO ] Info message 2",
            "[2025-01-15 10:00:03] [ERROR] Error message 1",
            "[2025-01-15 10:00:04] [INFO ] Info message 3"
        );
        Files.write(testLogFile, logLines);
        
        logViewerService.setCurrentLogFile(testLogFile);
        
        // Filtrar por INFO
        List<String> infoLogs = logViewerService.filterLogsByLevel("INFO", 10);
        
        assertEquals(3, infoLogs.size());
        assertTrue(infoLogs.stream().allMatch(log -> log.contains("[INFO ]")));
        
        logger.info("✅ Filtrado por nivel correcto");
    }
    
    @Test
    void testSearchLogs() throws IOException {
        logger.info("🧪 Probando búsqueda en logs");
        
        // Crear un archivo de log temporal
        Path testLogFile = tempDir.resolve("search-test.log");
        List<String> logLines = List.of(
            "[2025-01-15 10:00:00] [INFO ] User login successful",
            "[2025-01-15 10:00:01] [DEBUG] Database connection established",
            "[2025-01-15 10:00:02] [INFO ] User logout successful",
            "[2025-01-15 10:00:03] [ERROR] Database connection failed"
        );
        Files.write(testLogFile, logLines);
        
        logViewerService.setCurrentLogFile(testLogFile);
        
        // Buscar "User"
        List<String> userLogs = logViewerService.searchLogs("User", 10);
        
        assertEquals(2, userLogs.size());
        assertTrue(userLogs.stream().allMatch(log -> log.toLowerCase().contains("user")));
        
        logger.info("✅ Búsqueda en logs correcta");
    }
    
    @Test
    void testGetLogStatistics() throws IOException {
        logger.info("🧪 Probando estadísticas de logs");
        
        // Crear un archivo de log temporal
        Path testLogFile = tempDir.resolve("stats-test.log");
        List<String> logLines = List.of(
            "[2025-01-15 10:00:00] [INFO ] Info message",
            "[2025-01-15 10:00:01] [DEBUG] Debug message",
            "[2025-01-15 10:00:02] [WARN ] Warning message",
            "[2025-01-15 10:00:03] [ERROR] Error message",
            "[2025-01-15 10:00:04] [INFO ] Another info message"
        );
        Files.write(testLogFile, logLines);
        
        logViewerService.setCurrentLogFile(testLogFile);
        
        LogViewerService.LogStatistics stats = logViewerService.getLogStatistics();
        
        assertEquals(5, stats.totalLines);
        assertEquals(1, stats.debugCount);
        assertEquals(2, stats.infoCount);
        assertEquals(1, stats.warnCount);
        assertEquals(1, stats.errorCount);
        assertTrue(stats.mainLogSize > 0);
        
        logger.info("✅ Estadísticas de logs correctas");
    }
    
    @Test
    void testGetLogFileInfo() throws IOException {
        logger.info("🧪 Probando información del archivo de log");
        
        // Crear un archivo de log temporal
        Path testLogFile = tempDir.resolve("info-test.log");
        List<String> logLines = List.of("[2025-01-15 10:00:00] [INFO ] Test message");
        Files.write(testLogFile, logLines);
        
        logViewerService.setCurrentLogFile(testLogFile);
        
        LogViewerService.LogFileInfo fileInfo = logViewerService.getLogFileInfo();
        
        assertTrue(fileInfo.exists);
        assertEquals(testLogFile.toString(), fileInfo.filePath);
        assertTrue(fileInfo.fileSize > 0);
        assertTrue(fileInfo.lastModified > 0);
        assertTrue(fileInfo.canRead);
        
        logger.info("✅ Información del archivo correcta");
    }
    
    @Test
    void testGetLogFileInfoForInvalidFile() {
        logger.info("🧪 Probando información de archivo inválido");
        
        Path invalidFile = tempDir.resolve("nonexistent.log");
        logViewerService.setCurrentLogFile(invalidFile);
        
        LogViewerService.LogFileInfo fileInfo = logViewerService.getLogFileInfo();
        
        assertFalse(fileInfo.exists);
        assertEquals(invalidFile.toString(), fileInfo.filePath);
        assertEquals(0, fileInfo.fileSize);
        assertEquals(0, fileInfo.lastModified);
        assertFalse(fileInfo.canRead);
        
        logger.info("✅ Información de archivo inválido correcta");
    }
} 