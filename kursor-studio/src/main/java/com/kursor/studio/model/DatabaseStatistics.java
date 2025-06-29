package com.kursor.studio.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Modelo para las estadísticas de la base de datos.
 * 
 * <p>Esta clase encapsula toda la información estadística relevante
 * sobre la base de datos de Kursor.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatabaseStatistics {
    
    private final String databasePath;
    private final long databaseSize;
    private final boolean databaseExists;
    private final LocalDateTime lastUpdated;
    private final Map<String, Long> tableCounts;
    private final long totalSessions;
    private final long totalUserStats;
    private final long totalStrategyStates;
    private final long totalQuestionResponses;
    private final String databaseInfo;
    
    /**
     * Constructor privado. Usar el builder para crear instancias.
     */
    private DatabaseStatistics(Builder builder) {
        this.databasePath = builder.databasePath;
        this.databaseSize = builder.databaseSize;
        this.databaseExists = builder.databaseExists;
        this.lastUpdated = builder.lastUpdated;
        this.tableCounts = new HashMap<>(builder.tableCounts);
        this.totalSessions = builder.totalSessions;
        this.totalUserStats = builder.totalUserStats;
        this.totalStrategyStates = builder.totalStrategyStates;
        this.totalQuestionResponses = builder.totalQuestionResponses;
        this.databaseInfo = builder.databaseInfo;
    }
    
    // Getters
    public String getDatabasePath() { return databasePath; }
    public long getDatabaseSize() { return databaseSize; }
    public boolean isDatabaseExists() { return databaseExists; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public Map<String, Long> getTableCounts() { return new HashMap<>(tableCounts); }
    public long getTotalSessions() { return totalSessions; }
    public long getTotalUserStats() { return totalUserStats; }
    public long getTotalStrategyStates() { return totalStrategyStates; }
    public long getTotalQuestionResponses() { return totalQuestionResponses; }
    public String getDatabaseInfo() { return databaseInfo; }
    
    /**
     * Obtiene el tamaño de la base de datos en formato legible.
     * 
     * @return Tamaño formateado (KB, MB, GB)
     */
    public String getFormattedDatabaseSize() {
        if (databaseSize < 1024) {
            return databaseSize + " B";
        } else if (databaseSize < 1024 * 1024) {
            return String.format("%.1f KB", databaseSize / 1024.0);
        } else if (databaseSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", databaseSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", databaseSize / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Obtiene el total de registros en todas las tablas.
     * 
     * @return Total de registros
     */
    public long getTotalRecords() {
        return tableCounts.values().stream().mapToLong(Long::longValue).sum();
    }
    
    /**
     * Verifica si la base de datos está vacía.
     * 
     * @return true si está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return getTotalRecords() == 0;
    }
    
    /**
     * Obtiene el conteo de una tabla específica.
     * 
     * @param tableName Nombre de la tabla
     * @return Conteo de registros, 0 si la tabla no existe
     */
    public long getTableCount(String tableName) {
        return tableCounts.getOrDefault(tableName, 0L);
    }
    
    @Override
    public String toString() {
        return String.format("DatabaseStatistics{path='%s', size=%s, exists=%s, records=%d, lastUpdated=%s}",
                databasePath, getFormattedDatabaseSize(), databaseExists, getTotalRecords(), lastUpdated);
    }
    
    /**
     * Builder para DatabaseStatistics.
     */
    public static class Builder {
        private String databasePath = "";
        private long databaseSize = 0;
        private boolean databaseExists = false;
        private LocalDateTime lastUpdated = LocalDateTime.now();
        private Map<String, Long> tableCounts = new HashMap<>();
        private long totalSessions = 0;
        private long totalUserStats = 0;
        private long totalStrategyStates = 0;
        private long totalQuestionResponses = 0;
        private String databaseInfo = "";
        
        public Builder databasePath(String databasePath) {
            this.databasePath = databasePath;
            return this;
        }
        
        public Builder databaseSize(long databaseSize) {
            this.databaseSize = databaseSize;
            return this;
        }
        
        public Builder databaseExists(boolean databaseExists) {
            this.databaseExists = databaseExists;
            return this;
        }
        
        public Builder lastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
        
        public Builder tableCounts(Map<String, Long> tableCounts) {
            this.tableCounts = new HashMap<>(tableCounts);
            return this;
        }
        
        public Builder addTableCount(String tableName, long count) {
            this.tableCounts.put(tableName, count);
            return this;
        }
        
        public Builder totalSessions(long totalSessions) {
            this.totalSessions = totalSessions;
            return this;
        }
        
        public Builder totalUserStats(long totalUserStats) {
            this.totalUserStats = totalUserStats;
            return this;
        }
        
        public Builder totalStrategyStates(long totalStrategyStates) {
            this.totalStrategyStates = totalStrategyStates;
            return this;
        }
        
        public Builder totalQuestionResponses(long totalQuestionResponses) {
            this.totalQuestionResponses = totalQuestionResponses;
            return this;
        }
        
        public Builder databaseInfo(String databaseInfo) {
            this.databaseInfo = databaseInfo;
            return this;
        }
        
        public DatabaseStatistics build() {
            return new DatabaseStatistics(this);
        }
    }
} 