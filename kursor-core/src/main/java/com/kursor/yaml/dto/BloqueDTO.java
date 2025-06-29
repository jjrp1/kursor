package com.kursor.yaml.dto;

import java.util.List;

/**
 * DTO que contiene la información de un bloque de preguntas.
 * 
 * <p>Un bloque representa una sección temática dentro de un curso que contiene
 * un conjunto de preguntas del mismo tipo.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 2.0.0
 */
public class BloqueDTO {
    
    /** Título del bloque */
    private String titulo;
    
    /** Identificador único del bloque */
    private String id;
    
    /** Descripción del bloque */
    private String descripcion;
    
    /** Tipo de preguntas que contiene el bloque */
    private String tipo;
    
    /** Lista de preguntas del bloque */
    private List<PreguntaDTO> preguntas;
    
    /** Total de preguntas en el bloque (calculado automáticamente) */
    private int totalPreguntas;
    
    /**
     * Constructor por defecto.
     */
    public BloqueDTO() {
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param titulo Título del bloque
     * @param tipo Tipo de preguntas
     */
    public BloqueDTO(String titulo, String tipo) {
        this.titulo = titulo;
        this.tipo = tipo;
    }
    
    /**
     * Calcula automáticamente el total de preguntas en el bloque.
     * 
     * <p>Este método debe llamarse después de establecer las preguntas para
     * calcular correctamente el total.</p>
     */
    public void calcularTotalPreguntas() {
        this.totalPreguntas = preguntas != null ? preguntas.size() : 0;
    }
    
    /**
     * Obtiene el total de preguntas en el bloque.
     * 
     * @return Número total de preguntas
     */
    public int getTotalPreguntas() {
        return totalPreguntas;
    }
    
    // Getters y Setters
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public List<PreguntaDTO> getPreguntas() {
        return preguntas;
    }
    
    public void setPreguntas(List<PreguntaDTO> preguntas) {
        this.preguntas = preguntas;
        // Recalcular total automáticamente cuando se establecen las preguntas
        calcularTotalPreguntas();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return String.format("BloqueDTO{titulo='%s', tipo='%s', totalPreguntas=%d}", 
            titulo, tipo, totalPreguntas);
    }
} 
