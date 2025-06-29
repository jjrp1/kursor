package com.kursor.studio.model;

import java.util.List;

/**
 * Clase para almacenar información detallada de una tabla.
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class TableInfo {
    
    private String tableName;
    private List<String> schema;
    private long recordCount;
    
    /**
     * Constructor por defecto.
     */
    public TableInfo() {
    }
    
    /**
     * Constructor con parámetros.
     * 
     * @param tableName Nombre de la tabla
     * @param schema Esquema de la tabla
     * @param recordCount Número de registros
     */
    public TableInfo(String tableName, List<String> schema, long recordCount) {
        this.tableName = tableName;
        this.schema = schema;
        this.recordCount = recordCount;
    }
    
    /**
     * Obtiene el nombre de la tabla.
     * 
     * @return Nombre de la tabla
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * Establece el nombre de la tabla.
     * 
     * @param tableName Nombre de la tabla
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    /**
     * Obtiene el esquema de la tabla.
     * 
     * @return Esquema de la tabla
     */
    public List<String> getSchema() {
        return schema;
    }
    
    /**
     * Establece el esquema de la tabla.
     * 
     * @param schema Esquema de la tabla
     */
    public void setSchema(List<String> schema) {
        this.schema = schema;
    }
    
    /**
     * Obtiene el número de registros.
     * 
     * @return Número de registros
     */
    public long getRecordCount() {
        return recordCount;
    }
    
    /**
     * Establece el número de registros.
     * 
     * @param recordCount Número de registros
     */
    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }
    
    @Override
    public String toString() {
        return "TableInfo{" +
                "tableName='" + tableName + '\'' +
                ", schema=" + schema +
                ", recordCount=" + recordCount +
                '}';
    }
} 