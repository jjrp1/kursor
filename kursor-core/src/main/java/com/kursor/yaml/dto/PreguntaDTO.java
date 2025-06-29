package com.kursor.yaml.dto;

/**
 * DTO que contiene la información básica de una pregunta.
 * 
 * <p>Esta clase representa la información mínima necesaria para mostrar
 * una pregunta en las listas y estadísticas.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 2.0.0
 */
public class PreguntaDTO {
    
    /** Identificador único de la pregunta */
    private String id;
    
    /** Tipo de pregunta */
    private String tipo;
    
    /** Enunciado de la pregunta */
    private String enunciado;
    
    /**
     * Constructor por defecto.
     */
    public PreguntaDTO() {
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param id Identificador de la pregunta
     * @param tipo Tipo de pregunta
     * @param enunciado Enunciado de la pregunta
     */
    public PreguntaDTO(String id, String tipo, String enunciado) {
        this.id = id;
        this.tipo = tipo;
        this.enunciado = enunciado;
    }
    
    // Getters y Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getEnunciado() {
        return enunciado;
    }
    
    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }
    
    @Override
    public String toString() {
        return String.format("PreguntaDTO{id='%s', tipo='%s', enunciado='%s'}", 
            id, tipo, enunciado != null ? enunciado.substring(0, Math.min(enunciado.length(), 50)) + "..." : "null");
    }
} 
