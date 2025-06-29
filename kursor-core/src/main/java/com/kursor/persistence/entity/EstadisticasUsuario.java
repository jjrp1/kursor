package com.kursor.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa las estadísticas históricas de un usuario.
 * 
 * <p>Esta entidad almacena estadísticas agregadas de un usuario por curso,
 * incluyendo tiempo total, sesiones completadas y rachas de estudio.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "estadisticas_usuario")
public class EstadisticasUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;
    
    @Column(name = "curso_id", nullable = false)
    private String cursoId;
    
    @Column(name = "tiempo_total")
    private Integer tiempoTotal = 0; // en segundos
    
    @Column(name = "sesiones_completadas")
    private Integer sesionesCompletadas = 0;
    
    @Column(name = "mejor_racha_dias")
    private Integer mejorRachaDias = 0;
    
    @Column(name = "racha_actual_dias")
    private Integer rachaActualDias = 0;
    
    @Column(name = "fecha_ultima_sesion")
    private LocalDateTime fechaUltimaSesion;
    
    @Column(name = "fecha_primera_sesion")
    private LocalDateTime fechaPrimeraSesion;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Constructor por defecto.
     */
    public EstadisticasUsuario() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso
     */
    public EstadisticasUsuario(String usuarioId, String cursoId) {
        this();
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.fechaPrimeraSesion = LocalDateTime.now();
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getCursoId() {
        return cursoId;
    }
    
    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }
    
    public Integer getTiempoTotal() {
        return tiempoTotal;
    }
    
    public void setTiempoTotal(Integer tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }
    
    public Integer getSesionesCompletadas() {
        return sesionesCompletadas;
    }
    
    public void setSesionesCompletadas(Integer sesionesCompletadas) {
        this.sesionesCompletadas = sesionesCompletadas;
    }
    
    public Integer getMejorRachaDias() {
        return mejorRachaDias;
    }
    
    public void setMejorRachaDias(Integer mejorRachaDias) {
        this.mejorRachaDias = mejorRachaDias;
    }
    
    public Integer getRachaActualDias() {
        return rachaActualDias;
    }
    
    public void setRachaActualDias(Integer rachaActualDias) {
        this.rachaActualDias = rachaActualDias;
    }
    
    public LocalDateTime getFechaUltimaSesion() {
        return fechaUltimaSesion;
    }
    
    public void setFechaUltimaSesion(LocalDateTime fechaUltimaSesion) {
        this.fechaUltimaSesion = fechaUltimaSesion;
    }
    
    public LocalDateTime getFechaPrimeraSesion() {
        return fechaPrimeraSesion;
    }
    
    public void setFechaPrimeraSesion(LocalDateTime fechaPrimeraSesion) {
        this.fechaPrimeraSesion = fechaPrimeraSesion;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Agrega tiempo a las estadísticas.
     * 
     * @param tiempoAdicional Tiempo adicional en segundos
     */
    public void agregarTiempo(int tiempoAdicional) {
        this.tiempoTotal += tiempoAdicional;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Registra una sesión completada.
     */
    public void registrarSesionCompletada() {
        this.sesionesCompletadas++;
        this.fechaUltimaSesion = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Actualiza la racha de días.
     * 
     * @param nuevaRacha Nueva racha de días
     */
    public void actualizarRacha(int nuevaRacha) {
        this.rachaActualDias = nuevaRacha;
        if (nuevaRacha > this.mejorRachaDias) {
            this.mejorRachaDias = nuevaRacha;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Obtiene el tiempo total formateado.
     * 
     * @return Tiempo total en formato legible
     */
    public String getTiempoTotalFormateado() {
        if (tiempoTotal == null || tiempoTotal == 0) {
            return "0s";
        }
        
        int horas = tiempoTotal / 3600;
        int minutos = (tiempoTotal % 3600) / 60;
        int segundos = tiempoTotal % 60;
        
        if (horas > 0) {
            return String.format("%dh %dm %ds", horas, minutos, segundos);
        } else if (minutos > 0) {
            return String.format("%dm %ds", minutos, segundos);
        } else {
            return String.format("%ds", segundos);
        }
    }
    
    /**
     * Calcula los días transcurridos desde la primera sesión.
     * 
     * @return Número de días desde la primera sesión
     */
    public long getDiasDesdePrimeraSesion() {
        if (fechaPrimeraSesion == null) {
            return 0;
        }
        return java.time.Duration.between(fechaPrimeraSesion, LocalDateTime.now()).toDays();
    }
    
    /**
     * Calcula los días transcurridos desde la última sesión.
     * 
     * @return Número de días desde la última sesión
     */
    public long getDiasDesdeUltimaSesion() {
        if (fechaUltimaSesion == null) {
            return 0;
        }
        return java.time.Duration.between(fechaUltimaSesion, LocalDateTime.now()).toDays();
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (fechaPrimeraSesion == null) {
            fechaPrimeraSesion = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("EstadisticasUsuario{id=%d, usuarioId='%s', cursoId='%s', tiempoTotal=%s, sesiones=%d, mejorRacha=%d}", 
                           id, usuarioId, cursoId, getTiempoTotalFormateado(), sesionesCompletadas, mejorRachaDias);
    }
} 
