package com.kursor.yaml.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO para representar los datos de una sesión de aprendizaje.
 * 
 * <p>Este DTO contiene la información esencial de una sesión que se muestra
 * en la interfaz de usuario, separando la lógica de presentación de la
 * entidad de persistencia.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class SesionDTO {
    
    private Long id;
    private String cursoId;
    private String cursoTitulo;
    private String bloqueId;
    private String bloqueTitulo;
    private String estrategiaTipo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaUltimaRevision;
    private Integer tiempoTotal;
    private Integer preguntasRespondidas;
    private Integer aciertos;
    private Double tasaAciertos;
    private Integer mejorRachaAciertos;
    private Double porcentajeCompletitud;
    private String estado;
    
    /**
     * Constructor por defecto.
     */
    public SesionDTO() {
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param id ID de la sesión
     * @param cursoId ID del curso
     * @param bloqueId ID del bloque
     * @param estrategiaTipo Tipo de estrategia
     * @param fechaInicio Fecha de inicio
     */
    public SesionDTO(Long id, String cursoId, String bloqueId, String estrategiaTipo, LocalDateTime fechaInicio) {
        this.id = id;
        this.cursoId = cursoId;
        this.bloqueId = bloqueId;
        this.estrategiaTipo = estrategiaTipo;
        this.fechaInicio = fechaInicio;
        this.fechaUltimaRevision = fechaInicio;
        this.tiempoTotal = 0;
        this.preguntasRespondidas = 0;
        this.aciertos = 0;
        this.tasaAciertos = 0.0;
        this.mejorRachaAciertos = 0;
        this.porcentajeCompletitud = 0.0;
        this.estado = "EN_CURSO";
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCursoId() {
        return cursoId;
    }
    
    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }
    
    public String getCursoTitulo() {
        return cursoTitulo;
    }
    
    public void setCursoTitulo(String cursoTitulo) {
        this.cursoTitulo = cursoTitulo;
    }
    
    public String getBloqueId() {
        return bloqueId;
    }
    
    public void setBloqueId(String bloqueId) {
        this.bloqueId = bloqueId;
    }
    
    public String getBloqueTitulo() {
        return bloqueTitulo;
    }
    
    public void setBloqueTitulo(String bloqueTitulo) {
        this.bloqueTitulo = bloqueTitulo;
    }
    
    public String getEstrategiaTipo() {
        return estrategiaTipo;
    }
    
    public void setEstrategiaTipo(String estrategiaTipo) {
        this.estrategiaTipo = estrategiaTipo;
    }
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDateTime getFechaUltimaRevision() {
        return fechaUltimaRevision;
    }
    
    public void setFechaUltimaRevision(LocalDateTime fechaUltimaRevision) {
        this.fechaUltimaRevision = fechaUltimaRevision;
    }
    
    public Integer getTiempoTotal() {
        return tiempoTotal;
    }
    
    public void setTiempoTotal(Integer tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }
    
    public Integer getPreguntasRespondidas() {
        return preguntasRespondidas;
    }
    
    public void setPreguntasRespondidas(Integer preguntasRespondidas) {
        this.preguntasRespondidas = preguntasRespondidas;
    }
    
    public Integer getAciertos() {
        return aciertos;
    }
    
    public void setAciertos(Integer aciertos) {
        this.aciertos = aciertos;
    }
    
    public Double getTasaAciertos() {
        return tasaAciertos;
    }
    
    public void setTasaAciertos(Double tasaAciertos) {
        this.tasaAciertos = tasaAciertos;
    }
    
    public Integer getMejorRachaAciertos() {
        return mejorRachaAciertos;
    }
    
    public void setMejorRachaAciertos(Integer mejorRachaAciertos) {
        this.mejorRachaAciertos = mejorRachaAciertos;
    }
    
    public Double getPorcentajeCompletitud() {
        return porcentajeCompletitud;
    }
    
    public void setPorcentajeCompletitud(Double porcentajeCompletitud) {
        this.porcentajeCompletitud = porcentajeCompletitud;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    // Métodos de utilidad para la interfaz
    
    /**
     * Obtiene la fecha de inicio formateada para mostrar en la interfaz.
     * 
     * @return Fecha formateada como string
     */
    public String getFechaInicioFormateada() {
        if (fechaInicio != null) {
            return fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return "N/A";
    }
    
    /**
     * Obtiene el tiempo total formateado en minutos y segundos.
     * 
     * @return Tiempo formateado como string
     */
    public String getTiempoTotalFormateado() {
        if (tiempoTotal != null && tiempoTotal > 0) {
            int minutos = tiempoTotal / 60;
            int segundos = tiempoTotal % 60;
            return String.format("%02d:%02d", minutos, segundos);
        }
        return "00:00";
    }
    
    /**
     * Obtiene el porcentaje de aciertos formateado.
     * 
     * @return Porcentaje formateado como string
     */
    public String getTasaAciertosFormateada() {
        if (tasaAciertos != null) {
            return String.format("%.1f%%", tasaAciertos * 100);
        }
        return "0.0%";
    }
    
    /**
     * Obtiene el porcentaje de completitud formateado.
     * 
     * @return Porcentaje formateado como string
     */
    public String getPorcentajeCompletitudFormateado() {
        if (porcentajeCompletitud != null) {
            return String.format("%.1f%%", porcentajeCompletitud * 100);
        }
        return "0.0%";
    }
    
    /**
     * Obtiene el número de preguntas pendientes (incorrectas).
     * 
     * @return Número de preguntas pendientes
     */
    public Integer getPreguntasPendientes() {
        if (preguntasRespondidas != null && aciertos != null) {
            return preguntasRespondidas - aciertos;
        }
        return 0;
    }
    
    /**
     * Verifica si la sesión está en curso.
     * 
     * @return true si está en curso, false en caso contrario
     */
    public boolean isEnCurso() {
        return "EN_CURSO".equals(estado);
    }
    
    /**
     * Verifica si la sesión está completada.
     * 
     * @return true si está completada, false en caso contrario
     */
    public boolean isCompletada() {
        return "COMPLETADA".equals(estado);
    }
    
    /**
     * Verifica si la sesión está pausada.
     * 
     * @return true si está pausada, false en caso contrario
     */
    public boolean isPausada() {
        return "PAUSADA".equals(estado);
    }
    
    @Override
    public String toString() {
        return String.format("SesionDTO{id=%d, curso='%s', bloque='%s', estrategia='%s', fecha='%s', estado='%s'}",
                id, cursoTitulo != null ? cursoTitulo : cursoId, 
                bloqueTitulo != null ? bloqueTitulo : bloqueId, 
                estrategiaTipo, getFechaInicioFormateada(), estado);
    }
} 
