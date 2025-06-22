package com.kursor.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una sesión de aprendizaje.
 * 
 * <p>Esta entidad almacena el estado de una sesión de aprendizaje,
 * incluyendo información sobre el usuario, curso, bloque, estrategia
 * y estadísticas de progreso.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "sesiones")
public class Sesion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;
    
    @Column(name = "curso_id", nullable = false)
    private String cursoId;
    
    @Column(name = "bloque_id", nullable = false)
    private String bloqueId;
    
    @Column(name = "estrategia_tipo", nullable = false)
    private String estrategiaTipo;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_ultima_actividad")
    private LocalDateTime fechaUltimaActividad;
    
    @Column(name = "tiempo_total")
    private Integer tiempoTotal = 0;
    
    @Column(name = "preguntas_respondidas")
    private Integer preguntasRespondidas = 0;
    
    @Column(name = "aciertos")
    private Integer aciertos = 0;
    
    @Column(name = "porcentaje_completitud")
    private Double porcentajeCompletitud = 0.0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSesion estado = EstadoSesion.ACTIVA;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EstadoEstrategia estadoEstrategia;
    
    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RespuestaPregunta> respuestas = new ArrayList<>();
    
    /**
     * Constructor por defecto.
     */
    public Sesion() {
        this.fechaInicio = LocalDateTime.now();
        this.fechaUltimaActividad = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso
     * @param bloqueId ID del bloque
     * @param estrategiaTipo Tipo de estrategia utilizada
     */
    public Sesion(String usuarioId, String cursoId, String bloqueId, String estrategiaTipo) {
        this();
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.bloqueId = bloqueId;
        this.estrategiaTipo = estrategiaTipo;
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
    
    public String getBloqueId() {
        return bloqueId;
    }
    
    public void setBloqueId(String bloqueId) {
        this.bloqueId = bloqueId;
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
    
    public LocalDateTime getFechaUltimaActividad() {
        return fechaUltimaActividad;
    }
    
    public void setFechaUltimaActividad(LocalDateTime fechaUltimaActividad) {
        this.fechaUltimaActividad = fechaUltimaActividad;
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
    
    public Double getPorcentajeCompletitud() {
        return porcentajeCompletitud;
    }
    
    public void setPorcentajeCompletitud(Double porcentajeCompletitud) {
        this.porcentajeCompletitud = porcentajeCompletitud;
    }
    
    public EstadoSesion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoSesion estado) {
        this.estado = estado;
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
    
    public EstadoEstrategia getEstadoEstrategia() {
        return estadoEstrategia;
    }
    
    public void setEstadoEstrategia(EstadoEstrategia estadoEstrategia) {
        this.estadoEstrategia = estadoEstrategia;
    }
    
    public List<RespuestaPregunta> getRespuestas() {
        return respuestas;
    }
    
    public void setRespuestas(List<RespuestaPregunta> respuestas) {
        this.respuestas = respuestas;
    }
    
    /**
     * Actualiza la fecha de última actividad.
     */
    public void actualizarUltimaActividad() {
        this.fechaUltimaActividad = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Incrementa el tiempo total de la sesión.
     * 
     * @param tiempoAdicional Tiempo adicional en segundos
     */
    public void agregarTiempo(int tiempoAdicional) {
        this.tiempoTotal += tiempoAdicional;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Registra una nueva respuesta.
     * 
     * @param esCorrecta Si la respuesta fue correcta
     */
    public void registrarRespuesta(boolean esCorrecta) {
        this.preguntasRespondidas++;
        if (esCorrecta) {
            this.aciertos++;
        }
        this.actualizarUltimaActividad();
    }
    
    /**
     * Calcula y actualiza el porcentaje de completitud.
     * 
     * @param totalPreguntas Total de preguntas en el bloque
     */
    public void actualizarPorcentajeCompletitud(int totalPreguntas) {
        if (totalPreguntas > 0) {
            this.porcentajeCompletitud = (double) this.preguntasRespondidas / totalPreguntas * 100.0;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marca la sesión como completada.
     */
    public void completar() {
        this.estado = EstadoSesion.COMPLETADA;
        this.actualizarUltimaActividad();
    }
    
    /**
     * Marca la sesión como pausada.
     */
    public void pausar() {
        this.estado = EstadoSesion.PAUSADA;
        this.actualizarUltimaActividad();
    }
    
    /**
     * Reanuda la sesión.
     */
    public void reanudar() {
        this.estado = EstadoSesion.ACTIVA;
        this.actualizarUltimaActividad();
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }
        if (fechaUltimaActividad == null) {
            fechaUltimaActividad = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("Sesion{id=%d, usuarioId='%s', cursoId='%s', bloqueId='%s', estado=%s, progreso=%.1f%%}", 
                           id, usuarioId, cursoId, bloqueId, estado, porcentajeCompletitud);
    }
} 