package com.kursor.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una respuesta del usuario a una pregunta.
 * 
 * <p>Esta entidad almacena el historial detallado de respuestas del usuario,
 * incluyendo información sobre la pregunta, la respuesta, el tiempo y los intentos.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "respuestas_preguntas")
public class RespuestaPregunta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;
    
    @Column(name = "pregunta_id", nullable = false)
    private String preguntaId;
    
    @Column(name = "respuesta_correcta", nullable = false)
    private Boolean respuestaCorrecta;
    
    @Column(name = "tiempo_respuesta")
    private Integer tiempoRespuesta; // en segundos
    
    @Column(name = "intentos")
    private Integer intentos = 1;
    
    @Column(name = "texto_respuesta")
    private String textoRespuesta;
    
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
    
    /**
     * Constructor por defecto.
     */
    public RespuestaPregunta() {
        this.fechaRespuesta = LocalDateTime.now();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param sesion Sesión asociada
     * @param preguntaId ID de la pregunta
     * @param respuestaCorrecta Si la respuesta fue correcta
     */
    public RespuestaPregunta(Sesion sesion, String preguntaId, Boolean respuestaCorrecta) {
        this();
        this.sesion = sesion;
        this.preguntaId = preguntaId;
        this.respuestaCorrecta = respuestaCorrecta;
    }
    
    /**
     * Constructor completo.
     * 
     * @param sesion Sesión asociada
     * @param preguntaId ID de la pregunta
     * @param respuestaCorrecta Si la respuesta fue correcta
     * @param tiempoRespuesta Tiempo de respuesta en segundos
     * @param intentos Número de intentos
     * @param textoRespuesta Texto de la respuesta del usuario
     */
    public RespuestaPregunta(Sesion sesion, String preguntaId, Boolean respuestaCorrecta, 
                           Integer tiempoRespuesta, Integer intentos, String textoRespuesta) {
        this(sesion, preguntaId, respuestaCorrecta);
        this.tiempoRespuesta = tiempoRespuesta;
        this.intentos = intentos;
        this.textoRespuesta = textoRespuesta;
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
    
    public String getPreguntaId() {
        return preguntaId;
    }
    
    public void setPreguntaId(String preguntaId) {
        this.preguntaId = preguntaId;
    }
    
    public Boolean getRespuestaCorrecta() {
        return respuestaCorrecta;
    }
    
    public void setRespuestaCorrecta(Boolean respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }
    
    public Integer getTiempoRespuesta() {
        return tiempoRespuesta;
    }
    
    public void setTiempoRespuesta(Integer tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }
    
    public Integer getIntentos() {
        return intentos;
    }
    
    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }
    
    public String getTextoRespuesta() {
        return textoRespuesta;
    }
    
    public void setTextoRespuesta(String textoRespuesta) {
        this.textoRespuesta = textoRespuesta;
    }
    
    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }
    
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
    
    /**
     * Incrementa el número de intentos.
     */
    public void incrementarIntentos() {
        this.intentos++;
    }
    
    /**
     * Verifica si la respuesta fue correcta.
     * 
     * @return true si la respuesta fue correcta, false en caso contrario
     */
    public boolean esCorrecta() {
        return respuestaCorrecta != null && respuestaCorrecta;
    }
    
    /**
     * Obtiene el tiempo de respuesta en formato legible.
     * 
     * @return Tiempo de respuesta formateado
     */
    public String getTiempoRespuestaFormateado() {
        if (tiempoRespuesta == null) {
            return "N/A";
        }
        
        if (tiempoRespuesta < 60) {
            return tiempoRespuesta + "s";
        } else {
            int minutos = tiempoRespuesta / 60;
            int segundos = tiempoRespuesta % 60;
            return String.format("%dm %ds", minutos, segundos);
        }
    }
    
    @PrePersist
    protected void onCreate() {
        if (fechaRespuesta == null) {
            fechaRespuesta = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return String.format("RespuestaPregunta{id=%d, preguntaId='%s', correcta=%s, intentos=%d, tiempo=%s}", 
                           id, preguntaId, respuestaCorrecta, intentos, getTiempoRespuestaFormateado());
    }
} 