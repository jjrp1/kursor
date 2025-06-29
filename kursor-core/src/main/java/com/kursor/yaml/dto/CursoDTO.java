package com.kursor.yaml.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * DTO completo que contiene toda la información de un curso.
 * 
 * <p>Esta clase reemplaza a {@link CursoPreviewDTO} y contiene toda la información
 * necesaria para mostrar los detalles completos de un curso, incluyendo bloques,
 * preguntas y estadísticas calculadas.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 2.0.0
 */
public class CursoDTO {
    
    /** Identificador único del curso */
    private String id;
    
    /** Título del curso */
    private String titulo;
    
    /** Descripción detallada del curso */
    private String descripcion;
    
    /** Lista de bloques que componen el curso */
    private List<BloqueDTO> bloques;
    
    /** Total de preguntas en todo el curso (calculado automáticamente) */
    private int totalPreguntas;
    
    /** Nombre del archivo YAML en disco */
    private String nombreArchivo;
    
    /** Fecha y hora de carga en memoria */
    private Date fechaCarga;
    
    /** Estadísticas por tipo de pregunta */
    private Map<String, Integer> estadisticasPorTipo;
    
    /**
     * Constructor por defecto.
     */
    public CursoDTO() {
        this.fechaCarga = new Date();
        this.estadisticasPorTipo = new HashMap<>();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param id Identificador del curso
     * @param titulo Título del curso
     * @param descripcion Descripción del curso
     * @param nombreArchivo Nombre del archivo YAML
     */
    public CursoDTO(String id, String titulo, String descripcion, String nombreArchivo) {
        this();
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreArchivo = nombreArchivo;
    }
    
    /**
     * Calcula automáticamente todos los totales y estadísticas del curso.
     * 
     * <p>Este método debe llamarse después de establecer los bloques para
     * calcular correctamente los totales de preguntas y estadísticas por tipo.</p>
     */
    public void calcularTotales() {
        if (bloques == null || bloques.isEmpty()) {
            this.totalPreguntas = 0;
            this.estadisticasPorTipo.clear();
            return;
        }
        
        // Calcular total de preguntas del curso
        this.totalPreguntas = bloques.stream()
            .mapToInt(BloqueDTO::getTotalPreguntas)
            .sum();
        
        // Calcular estadísticas por tipo de pregunta
        this.estadisticasPorTipo.clear();
        bloques.stream()
            .collect(Collectors.groupingBy(
                BloqueDTO::getTipo,
                Collectors.summingInt(BloqueDTO::getTotalPreguntas)
            ))
            .forEach(estadisticasPorTipo::put);
    }
    
    /**
     * Obtiene el total de bloques en el curso.
     * 
     * @return Número total de bloques
     */
    public int getTotalBloques() {
        return bloques != null ? bloques.size() : 0;
    }
    
    /**
     * Obtiene el total de preguntas en el curso.
     * 
     * @return Número total de preguntas
     */
    public int getTotalPreguntas() {
        return totalPreguntas;
    }
    
    /**
     * Obtiene las estadísticas por tipo de pregunta.
     * 
     * @return Mapa con el tipo de pregunta como clave y el número de preguntas como valor
     */
    public Map<String, Integer> getEstadisticasPorTipo() {
        return new HashMap<>(estadisticasPorTipo);
    }
    
    /**
     * Obtiene el número de preguntas para un tipo específico.
     * 
     * @param tipo Tipo de pregunta
     * @return Número de preguntas de ese tipo, o 0 si no hay
     */
    public int getPreguntasPorTipo(String tipo) {
        return estadisticasPorTipo.getOrDefault(tipo, 0);
    }
    
    // Getters y Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<BloqueDTO> getBloques() {
        return bloques;
    }
    
    public void setBloques(List<BloqueDTO> bloques) {
        this.bloques = bloques;
        // Recalcular totales automáticamente cuando se establecen los bloques
        calcularTotales();
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public Date getFechaCarga() {
        return fechaCarga;
    }
    
    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }
    
    @Override
    public String toString() {
        return String.format("CursoDTO{id='%s', titulo='%s', bloques=%d, totalPreguntas=%d}", 
            id, titulo, getTotalBloques(), totalPreguntas);
    }
} 
