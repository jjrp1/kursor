package com.kursor.studio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Servicio para visualización y gestión de logs de Kursor Studio.
 * 
 * <p>Este servicio proporciona funcionalidades para:</p>
 * <ul>
 *   <li>Leer logs desde archivos</li>
 *   <li>Filtrar logs por nivel (jerárquico)</li>
 *   <li>Buscar texto en logs</li>
 *   <li>Filtrar por fecha/hora</li>
 *   <li>Obtener estadísticas de logs</li>
 *   <li>Monitorear logs en tiempo real</li>
 *   <li>Seleccionar archivos de log personalizados</li>
 * </ul>
 * 
 * @author Sistema Kursor
 * @version 2.0.0
 */
public class LogViewerService {
    
    /** Logger para registrar eventos del servicio */
    private static final Logger logger = LoggerFactory.getLogger(LogViewerService.class);
    
    /** Directorio de logs por defecto */
    private static final String DEFAULT_LOG_DIR = "log";
    
    /** Archivo de log principal */
    private static final String MAIN_LOG_FILE = "kursor-studio.log";
    
    /** Archivo de log de errores */
    private static final String ERROR_LOG_FILE = "error.log";
    
    /** Archivo de log actualmente seleccionado */
    private Path currentLogFile;
    
    /** Servicio de monitoreo de archivos */
    private WatchService watchService;
    
    /** Flag para controlar el monitoreo en tiempo real */
    private AtomicBoolean monitoringEnabled = new AtomicBoolean(false);
    
    /** Thread para monitoreo en tiempo real */
    private Thread monitoringThread;
    
    /** Callback para notificar cambios en tiempo real */
    private LogChangeListener changeListener;
    
    /** Formato de fecha para parsing de logs */
    private static final DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Constructor del servicio.
     */
    public LogViewerService() {
        logger.info("📝 Inicializando LogViewerService v2.0.0");
        // Inicializar con el archivo de log por defecto
        setCurrentLogFile(Paths.get(DEFAULT_LOG_DIR, MAIN_LOG_FILE));
        
        try {
            watchService = java.nio.file.FileSystems.getDefault().newWatchService();
            logger.debug("✅ Servicio de monitoreo de archivos inicializado");
        } catch (IOException e) {
            logger.error("❌ Error inicializando servicio de monitoreo: {}", e.getMessage());
        }
    }
    
    /**
     * Interfaz para notificar cambios en tiempo real.
     */
    public interface LogChangeListener {
        void onLogFileChanged();
    }
    
    /**
     * Establece el listener para cambios en tiempo real.
     */
    public void setChangeListener(LogChangeListener listener) {
        this.changeListener = listener;
    }
    
    /**
     * Inicia el monitoreo en tiempo real del archivo de log.
     */
    public void startRealTimeMonitoring() {
        if (monitoringEnabled.get()) {
            logger.debug("⚠️ Monitoreo en tiempo real ya está activo");
            return;
        }
        
        if (currentLogFile == null || !isCurrentLogFileValid()) {
            logger.warn("⚠️ No se puede iniciar monitoreo: archivo no válido");
            return;
        }
        
        monitoringEnabled.set(true);
        
        monitoringThread = new Thread(() -> {
            logger.info("🔄 Iniciando monitoreo en tiempo real: {}", currentLogFile);
            
            try {
                // Registrar el directorio para monitoreo
                Path directory = currentLogFile.getParent();
                if (directory != null) {
                    directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                }
                
                while (monitoringEnabled.get()) {
                    WatchKey key = watchService.take();
                    
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();
                        
                        // Verificar si el archivo modificado es el que estamos monitoreando
                        if (currentLogFile.getFileName().equals(filename)) {
                            logger.debug("📝 Archivo modificado detectado: {}", filename);
                            if (changeListener != null) {
                                changeListener.onLogFileChanged();
                            }
                        }
                    }
                    
                    if (!key.reset()) {
                        break;
                    }
                }
                
            } catch (InterruptedException e) {
                logger.info("🛑 Monitoreo en tiempo real interrumpido");
            } catch (Exception e) {
                logger.error("❌ Error en monitoreo en tiempo real: {}", e.getMessage());
            }
        });
        
        monitoringThread.setDaemon(true);
        monitoringThread.start();
        logger.info("✅ Monitoreo en tiempo real iniciado");
    }
    
    /**
     * Detiene el monitoreo en tiempo real.
     */
    public void stopRealTimeMonitoring() {
        if (!monitoringEnabled.get()) {
            logger.debug("⚠️ Monitoreo en tiempo real no está activo");
            return;
        }
        
        monitoringEnabled.set(false);
        
        if (monitoringThread != null) {
            monitoringThread.interrupt();
            try {
                monitoringThread.join(1000); // Esperar máximo 1 segundo
            } catch (InterruptedException e) {
                logger.warn("⚠️ Interrupción al detener monitoreo");
            }
        }
        
        logger.info("🛑 Monitoreo en tiempo real detenido");
    }
    
    /**
     * Verifica si el monitoreo en tiempo real está activo.
     */
    public boolean isRealTimeMonitoringActive() {
        return monitoringEnabled.get();
    }
    
    /**
     * Establece el archivo de log actual.
     * 
     * @param logFile ruta del archivo de log
     */
    public void setCurrentLogFile(Path logFile) {
        // Detener monitoreo anterior si está activo
        if (monitoringEnabled.get()) {
            stopRealTimeMonitoring();
        }
        
        this.currentLogFile = logFile;
        logger.info("📄 Archivo de log establecido: {}", logFile);
        
        // Reiniciar monitoreo si estaba activo
        if (monitoringEnabled.get()) {
            startRealTimeMonitoring();
        }
    }
    
    /**
     * Establece el archivo de log actual desde una cadena de ruta.
     * 
     * @param logFilePath ruta del archivo de log como String
     */
    public void setCurrentLogFile(String logFilePath) {
        setCurrentLogFile(Paths.get(logFilePath));
    }
    
    /**
     * Obtiene el archivo de log actual.
     * 
     * @return ruta del archivo de log actual
     */
    public Path getCurrentLogFile() {
        return currentLogFile;
    }
    
    /**
     * Obtiene el nombre del archivo de log actual.
     * 
     * @return nombre del archivo actual
     */
    public String getCurrentLogFileName() {
        return currentLogFile != null ? currentLogFile.getFileName().toString() : "Ninguno";
    }
    
    /**
     * Verifica si el archivo de log actual existe y es legible.
     * 
     * @return true si el archivo existe y es legible
     */
    public boolean isCurrentLogFileValid() {
        if (currentLogFile == null) {
            return false;
        }
        
        boolean exists = Files.exists(currentLogFile);
        boolean readable = Files.isReadable(currentLogFile);
        
        logger.debug("📄 Validación del archivo actual: {} (existe: {}, legible: {})", 
                    currentLogFile, exists, readable);
        
        return exists && readable;
    }
    
    /**
     * Obtiene la ruta del directorio de logs.
     * 
     * @return ruta del directorio de logs
     */
    public String getLogDirectory() {
        String logDir = System.getProperty("kursor.studio.log.dir", DEFAULT_LOG_DIR);
        logger.debug("📁 Directorio de logs: {}", logDir);
        return logDir;
    }
    
    /**
     * Verifica si los archivos de log existen.
     * 
     * @return true si los archivos existen
     */
    public boolean checkLogFilesExist() {
        String logDir = getLogDirectory();
        Path mainLogPath = Paths.get(logDir, MAIN_LOG_FILE);
        Path errorLogPath = Paths.get(logDir, ERROR_LOG_FILE);
        
        boolean mainExists = Files.exists(mainLogPath);
        boolean errorExists = Files.exists(errorLogPath);
        
        logger.debug("📄 Verificación de archivos de log:");
        logger.debug("   - {}: {}", MAIN_LOG_FILE, mainExists ? "✅ Existe" : "❌ No existe");
        logger.debug("   - {}: {}", ERROR_LOG_FILE, errorExists ? "✅ Existe" : "❌ No existe");
        
        return mainExists || errorExists;
    }
    
    /**
     * Lee las últimas líneas del archivo de log actual.
     * 
     * @param maxLines número máximo de líneas a leer
     * @return lista de líneas de log
     */
    public List<String> getRecentLogs(int maxLines) {
        logger.debug("📖 Leyendo últimas {} líneas del archivo: {}", maxLines, currentLogFile);
        
        List<String> lines = new ArrayList<>();
        
        if (!isCurrentLogFileValid()) {
            logger.warn("⚠️ Archivo de log no válido: {}", currentLogFile);
            return lines;
        }
        
        try {
            List<String> allLines = Files.readAllLines(currentLogFile);
            int startIndex = Math.max(0, allLines.size() - maxLines);
            
            for (int i = startIndex; i < allLines.size(); i++) {
                lines.add(allLines.get(i));
            }
            
            logger.debug("✅ Leídas {} líneas del archivo de log", lines.size());
            
        } catch (IOException e) {
            logger.error("❌ Error leyendo archivo de log: {}", e.getMessage());
        }
        
        return lines;
    }
    
    /**
     * Filtra logs por nivel específico con comportamiento jerárquico.
     * 
     * @param level nivel de log a filtrar (DEBUG, INFO, WARN, ERROR)
     * @param exclusive si es true, solo muestra el nivel especificado; si es false, incluye niveles superiores
     * @param maxLines número máximo de líneas a procesar
     * @return lista de líneas filtradas
     */
    public List<String> filterLogsByLevel(String level, boolean exclusive, int maxLines) {
        logger.debug("🔍 Filtrando logs por nivel: {} (exclusivo: {})", level, exclusive);
        
        List<String> allLogs = getRecentLogs(maxLines * 10); // Leer más para compensar filtrado
        
        List<String> filteredLogs = allLogs.stream()
            .filter(line -> {
                if (exclusive) {
                    // Modo exclusivo: solo el nivel especificado
                    return line.contains("[" + level + " ");
                } else {
                    // Modo jerárquico: nivel especificado e inferiores
                    switch (level) {
                        case "DEBUG":
                            return line.contains("[DEBUG] ") || line.contains("[INFO ] ") || 
                                   line.contains("[WARN ] ") || line.contains("[ERROR] ");
                        case "INFO":
                            return line.contains("[INFO ] ") || line.contains("[WARN ] ") || 
                                   line.contains("[ERROR] ");
                        case "WARN":
                            return line.contains("[WARN ] ") || line.contains("[ERROR] ");
                        case "ERROR":
                            return line.contains("[ERROR] ");
                        default:
                            return true; // TODOS
                    }
                }
            })
            .limit(maxLines)
            .collect(Collectors.toList());
        
        logger.debug("✅ Filtrados {} logs de nivel {} (exclusivo: {})", filteredLogs.size(), level, exclusive);
        return filteredLogs;
    }
    
    /**
     * Filtra logs por rango de fechas.
     * 
     * @param startDate fecha de inicio (formato: yyyy-MM-dd HH:mm:ss)
     * @param endDate fecha de fin (formato: yyyy-MM-dd HH:mm:ss)
     * @param maxLines número máximo de líneas a procesar
     * @return lista de líneas filtradas por fecha
     */
    public List<String> filterLogsByDateRange(String startDate, String endDate, int maxLines) {
        logger.debug("📅 Filtrando logs por rango de fechas: {} - {}", startDate, endDate);
        
        List<String> allLogs = getRecentLogs(maxLines * 5);
        List<String> filteredLogs = new ArrayList<>();
        
        try {
            LocalDateTime start = LocalDateTime.parse(startDate, LOG_DATE_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(endDate, LOG_DATE_FORMATTER);
            
            for (String line : allLogs) {
                try {
                    // Extraer fecha del log (asumiendo formato: yyyy-MM-dd HH:mm:ss.SSS)
                    String dateStr = line.substring(0, 23); // Tomar los primeros 23 caracteres
                    LocalDateTime logDate = LocalDateTime.parse(dateStr, LOG_DATE_FORMATTER);
                    
                    if (!logDate.isBefore(start) && !logDate.isAfter(end)) {
                        filteredLogs.add(line);
                        if (filteredLogs.size() >= maxLines) {
                            break;
                        }
                    }
                } catch (DateTimeParseException | StringIndexOutOfBoundsException e) {
                    // Ignorar líneas que no tienen formato de fecha válido
                    continue;
                }
            }
            
            logger.debug("✅ Filtrados {} logs por rango de fechas", filteredLogs.size());
            
        } catch (DateTimeParseException e) {
            logger.error("❌ Error parseando fechas: {}", e.getMessage());
        }
        
        return filteredLogs;
    }
    
    /**
     * Busca texto específico en los logs.
     * 
     * @param searchText texto a buscar
     * @param maxLines número máximo de líneas a procesar
     * @return lista de líneas que contienen el texto
     */
    public List<String> searchLogs(String searchText, int maxLines) {
        logger.debug("🔍 Buscando '{}' en logs", searchText);
        
        List<String> allLogs = getRecentLogs(maxLines * 5); // Leer más para compensar búsqueda
        
        List<String> matchingLogs = allLogs.stream()
            .filter(line -> line.toLowerCase().contains(searchText.toLowerCase()))
            .limit(maxLines)
            .collect(Collectors.toList());
        
        logger.debug("✅ Encontradas {} líneas que contienen '{}'", matchingLogs.size(), searchText);
        return matchingLogs;
    }
    
    /**
     * Obtiene estadísticas de los logs del archivo actual.
     * 
     * @return objeto con estadísticas de logs
     */
    public LogStatistics getLogStatistics() {
        logger.debug("📊 Generando estadísticas de logs del archivo: {}", currentLogFile);
        
        LogStatistics stats = new LogStatistics();
        
        if (!isCurrentLogFileValid()) {
            logger.warn("⚠️ No se pueden generar estadísticas: archivo no válido");
            return stats;
        }
        
        try {
            List<String> logLines = Files.readAllLines(currentLogFile);
            stats.totalLines = logLines.size();
            stats.mainLogSize = Files.size(currentLogFile);
            
            // Contar por nivel
            for (String line : logLines) {
                if (line.contains("[DEBUG] ")) stats.debugCount++;
                else if (line.contains("[INFO ] ")) stats.infoCount++;
                else if (line.contains("[WARN ] ")) stats.warnCount++;
                else if (line.contains("[ERROR] ")) stats.errorCount++;
            }
            
            logger.debug("✅ Estadísticas generadas: {} líneas totales", stats.totalLines);
            
        } catch (IOException e) {
            logger.error("❌ Error generando estadísticas: {}", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Obtiene información sobre el archivo de log actual.
     * 
     * @return información del archivo de log
     */
    public LogFileInfo getLogFileInfo() {
        logger.debug("📄 Obteniendo información del archivo de log: {}", currentLogFile);
        
        LogFileInfo info = new LogFileInfo();
        
        if (isCurrentLogFileValid()) {
            try {
                info.exists = true;
                info.filePath = currentLogFile.toString();
                info.fileSize = Files.size(currentLogFile);
                info.lastModified = Files.getLastModifiedTime(currentLogFile).toMillis();
                info.canRead = Files.isReadable(currentLogFile);
                
                logger.debug("✅ Información del archivo obtenida: {} bytes", info.fileSize);
                
            } catch (IOException e) {
                logger.error("❌ Error obteniendo información del archivo: {}", e.getMessage());
            }
        } else {
            info.exists = false;
            info.filePath = currentLogFile != null ? currentLogFile.toString() : "Ninguno";
            logger.warn("⚠️ Archivo de log no válido: {}", currentLogFile);
        }
        
        return info;
    }
    
    /**
     * Cierra recursos del servicio.
     */
    public void close() {
        stopRealTimeMonitoring();
        
        if (watchService != null) {
            try {
                watchService.close();
                logger.debug("✅ Servicio de monitoreo cerrado");
            } catch (IOException e) {
                logger.error("❌ Error cerrando servicio de monitoreo: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Clase para encapsular estadísticas de logs.
     */
    public static class LogStatistics {
        public int totalLines;
        public int debugCount;
        public int infoCount;
        public int warnCount;
        public int errorCount;
        public long mainLogSize;
        public long errorLogSize;
        public int errorLogLines;
        
        @Override
        public String toString() {
            return String.format("LogStatistics{total=%d, debug=%d, info=%d, warn=%d, error=%d}", 
                               totalLines, debugCount, infoCount, warnCount, errorCount);
        }
    }
    
    /**
     * Clase para encapsular información del archivo de log.
     */
    public static class LogFileInfo {
        public boolean exists;
        public String filePath;
        public long fileSize;
        public long lastModified;
        public boolean canRead;
        
        @Override
        public String toString() {
            return String.format("LogFileInfo{exists=%s, size=%d, modified=%d}", 
                               exists, fileSize, lastModified);
        }
    }
} 