package com.kursor.studio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para persistir la configuración de la base de datos de Kursor
 * que se quiere explorar desde Kursor Studio.
 */
@Entity
@Table(name = "database_configuration")
public class DatabaseConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "kursor_database_path", nullable = false, length = 500)
    private String kursorDatabasePath;
    
    @Column(name = "last_connection_date")
    private LocalDateTime lastConnectionDate;
    
    @Column(name = "connection_status", length = 50)
    private String connectionStatus;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    // Constructores
    public DatabaseConfiguration() {
        this.createdDate = LocalDateTime.now();
    }
    
    public DatabaseConfiguration(String kursorDatabasePath) {
        this();
        this.kursorDatabasePath = kursorDatabasePath;
        this.connectionStatus = "NOT_TESTED";
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getKursorDatabasePath() {
        return kursorDatabasePath;
    }
    
    public void setKursorDatabasePath(String kursorDatabasePath) {
        this.kursorDatabasePath = kursorDatabasePath;
        this.updatedDate = LocalDateTime.now();
    }
    
    public LocalDateTime getLastConnectionDate() {
        return lastConnectionDate;
    }
    
    public void setLastConnectionDate(LocalDateTime lastConnectionDate) {
        this.lastConnectionDate = lastConnectionDate;
        this.updatedDate = LocalDateTime.now();
    }
    
    public String getConnectionStatus() {
        return connectionStatus;
    }
    
    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
        this.updatedDate = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedDate = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    // Métodos de utilidad
    public void markConnectionSuccessful() {
        this.connectionStatus = "CONNECTED";
        this.lastConnectionDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
    
    public void markConnectionFailed() {
        this.connectionStatus = "FAILED";
        this.updatedDate = LocalDateTime.now();
    }
    
    public boolean isConfigured() {
        return kursorDatabasePath != null && !kursorDatabasePath.trim().isEmpty();
    }
    
    public boolean isConnected() {
        return "CONNECTED".equals(connectionStatus);
    }
    
    @Override
    public String toString() {
        return "DatabaseConfiguration{" +
                "id=" + id +
                ", kursorDatabasePath='" + kursorDatabasePath + '\'' +
                ", connectionStatus='" + connectionStatus + '\'' +
                ", lastConnectionDate=" + lastConnectionDate +
                '}';
    }
} 