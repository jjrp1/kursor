package com.kursor.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una sesión de aprendizaje.
 * 
 * <p>Esta entidad almacena el estado de una sesión de aprendizaje,
 * incluyendo información sobre el curso, bloque, estrategia
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
    
    @Column(name = "curso_id", nullable = false)
    private String cursoId;
    
    @Column(name = "bloque_id", nullable = false)
    private String bloqueId;
    
    @Column(name = "estrategia_tipo", nullable = false)
    private String estrategiaTipo;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_ultima_revision")
    private LocalDateTime fechaUltimaRevision;
    
    @Column(name = "tiempo_total")
    private Integer tiempoTotal = 0;
    
    @Column(name = "preguntas_respondidas")
    private Integer preguntasRespondidas = 0;
    
    @Column(name = "aciertos")
    private Integer aciertos = 0;
    
    @Column(name = "tasa_aciertos")
    private Double tasaAciertos = 0.0;
    
    @Column(name = "mejor_racha_aciertos")
    private Integer mejorRachaAciertos = 0;
    
    @Column(name = "porcentaje_completitud")
    private Double porcentajeCompletitud = 0.0;
    
    @Column(name = "pregunta_actual_id")
    private String preguntaActualId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSesion estado = EstadoSesion.EN_CURSO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EstadoEstrategia estadoEstrategia;
    
    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PreguntaSesion> preguntasSesion = new ArrayList<>();
    
    /**
     * Constructor por defecto.
     */
    public Sesion() {
        this.fechaInicio = LocalDateTime.now();
        this.fechaUltimaRevision = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param cursoId ID del curso
     * @param bloqueId ID del bloque
     * @param estrategiaTipo Tipo de estrategia utilizada
     */
    public Sesion(String cursoId, String bloqueId, String estrategiaTipo) {
        this();
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
    
    public String getPreguntaActualId() {
        return preguntaActualId;
    }
    
    public void setPreguntaActualId(String preguntaActualId) {
        this.preguntaActualId = preguntaActualId;
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
    
    public List<PreguntaSesion> getPreguntasSesion() {
        return preguntasSesion;
    }
    
    public void setPreguntasSesion(List<PreguntaSesion> preguntasSesion) {
        this.preguntasSesion = preguntasSesion;
    }
    
    /**
     * Actualiza la fecha de última revisión.
     */
    public void actualizarUltimaRevision() {
        this.fechaUltimaRevision = LocalDateTime.now();
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
        this.actualizarUltimaRevision();
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
     * Calcula y actualiza la tasa de aciertos.
     */
    public void actualizarTasaAciertos() {
        if (this.preguntasRespondidas > 0) {
            this.tasaAciertos = (double) this.aciertos / this.preguntasRespondidas * 100.0;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Actualiza la mejor racha de aciertos si la racha actual es mayor.
     * 
     * @param rachaActual Racha actual de aciertos consecutivos
     */
    public void actualizarMejorRacha(int rachaActual) {
        if (rachaActual > this.mejorRachaAciertos) {
            this.mejorRachaAciertos = rachaActual;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Marca la sesión como completada.
     */
    public void completar() {
        this.estado = EstadoSesion.COMPLETADA;
        this.actualizarUltimaRevision();
    }
    
    /**
     * Marca la sesión como guardada.
     */
    public void guardar() {
        this.estado = EstadoSesion.GUARDADA;
        this.actualizarUltimaRevision();
    }
    
    /**
     * Reanuda la sesión (la marca como en curso).
     */
    public void reanudar() {
        this.estado = EstadoSesion.EN_CURSO;
        this.actualizarUltimaRevision();
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
        if (fechaUltimaRevision == null) {
            fechaUltimaRevision = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("Sesion{id=%d, cursoId='%s', bloqueId='%s', estado=%s, progreso=%.1f%%, aciertos=%.1f%%}", 
                           id, cursoId, bloqueId, estado, porcentajeCompletitud, tasaAciertos);
    }
} 
