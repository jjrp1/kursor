package com.kursor.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa el estado interno de una estrategia de aprendizaje.
 * 
 * <p>Esta entidad almacena el estado serializado de una estrategia de aprendizaje,
 * permitiendo restaurar el estado exacto de la estrategia cuando se reanuda una sesión.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "estados_estrategias")
public class EstadoEstrategia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;
    
    @Column(name = "tipo_estrategia", nullable = false)
    private String tipoEstrategia;
    
    @Column(name = "datos_estado", nullable = false, columnDefinition = "TEXT")
    private String datosEstado; // JSON serializado
    
    @Column(name = "progreso")
    private Double progreso = 0.0;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;
    
    /**
     * Constructor por defecto.
     */
    public EstadoEstrategia() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param sesion Sesión asociada
     * @param tipoEstrategia Tipo de estrategia
     * @param datosEstado Datos del estado serializados en JSON
     */
    public EstadoEstrategia(Sesion sesion, String tipoEstrategia, String datosEstado) {
        this();
        this.sesion = sesion;
        this.tipoEstrategia = tipoEstrategia;
        this.datosEstado = datosEstado;
    }
    
    /**
     * Constructor completo.
     * 
     * @param sesion Sesión asociada
     * @param tipoEstrategia Tipo de estrategia
     * @param datosEstado Datos del estado serializados en JSON
     * @param progreso Progreso de la estrategia (0.0 a 1.0)
     */
    public EstadoEstrategia(Sesion sesion, String tipoEstrategia, String datosEstado, Double progreso) {
        this(sesion, tipoEstrategia, datosEstado);
        this.progreso = progreso;
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Sesion getSesion() {
        return sesion;
    }
    
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }
    
    public String getTipoEstrategia() {
        return tipoEstrategia;
    }
    
    public void setTipoEstrategia(String tipoEstrategia) {
        this.tipoEstrategia = tipoEstrategia;
    }
    
    public String getDatosEstado() {
        return datosEstado;
    }
    
    public void setDatosEstado(String datosEstado) {
        this.datosEstado = datosEstado;
        this.fechaUltimaModificacion = LocalDateTime.now();
    }
    
    public Double getProgreso() {
        return progreso;
    }
    
    public void setProgreso(Double progreso) {
        this.progreso = progreso;
        this.fechaUltimaModificacion = LocalDateTime.now();
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }
    
    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }
    
    /**
     * Actualiza el estado con nuevos datos.
     * 
     * @param datosEstado Nuevos datos del estado
     * @param progreso Nuevo progreso
     */
    public void actualizarEstado(String datosEstado, Double progreso) {
        this.datosEstado = datosEstado;
        this.progreso = progreso;
        this.fechaUltimaModificacion = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaUltimaModificacion == null) {
            fechaUltimaModificacion = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaUltimaModificacion = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("EstadoEstrategia{id=%d, tipoEstrategia='%s', progreso=%.1f%%, sesionId=%d}", 
                           id, tipoEstrategia, progreso * 100, sesion != null ? sesion.getId() : null);
    }
} 
