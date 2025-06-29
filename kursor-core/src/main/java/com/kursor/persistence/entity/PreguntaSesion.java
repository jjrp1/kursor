package com.kursor.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una pregunta en una sesión de aprendizaje.
 * 
 * <p>Esta entidad almacena el registro de cada pregunta que se presenta
 * al usuario durante una sesión, incluyendo el resultado, tiempo dedicado
 * y la respuesta del usuario.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "preguntas_sesion")
public class PreguntaSesion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;
    
    @Column(name = "pregunta_id", nullable = false)
    private String preguntaId;
    
    @Column(name = "resultado", nullable = false)
    private String resultado = "sin contestar"; // acierto / fallo / sin contestar
    
    @Column(name = "tiempo_dedicado")
    private Integer tiempoDedicado = 0; // en segundos
    
    @Column(name = "respuesta", columnDefinition = "TEXT")
    private String respuesta; // respuesta literal del usuario
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Constructor por defecto.
     */
    public PreguntaSesion() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param sesion Sesión asociada
     * @param preguntaId ID de la pregunta
     */
    public PreguntaSesion(Sesion sesion, String preguntaId) {
        this();
        this.sesion = sesion;
        this.preguntaId = preguntaId;
    }
    
    /**
     * Constructor completo.
     * 
     * @param sesion Sesión asociada
     * @param preguntaId ID de la pregunta
     * @param resultado Resultado de la pregunta
     * @param tiempoDedicado Tiempo dedicado en segundos
     * @param respuesta Respuesta literal del usuario
     */
    public PreguntaSesion(Sesion sesion, String preguntaId, String resultado, 
                         Integer tiempoDedicado, String respuesta) {
        this(sesion, preguntaId);
        this.resultado = resultado;
        this.tiempoDedicado = tiempoDedicado;
        this.respuesta = respuesta;
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
    
    public String getResultado() {
        return resultado;
    }
    
    public void setResultado(String resultado) {
        this.resultado = resultado;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getTiempoDedicado() {
        return tiempoDedicado;
    }
    
    public void setTiempoDedicado(Integer tiempoDedicado) {
        this.tiempoDedicado = tiempoDedicado;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRespuesta() {
        return respuesta;
    }
    
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
        this.updatedAt = LocalDateTime.now();
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
     * Verifica si la pregunta fue respondida correctamente.
     * 
     * @return true si la respuesta fue correcta, false en caso contrario
     */
    public boolean esCorrecta() {
        return "acierto".equals(resultado);
    }
    
    /**
     * Verifica si la pregunta fue respondida incorrectamente.
     * 
     * @return true si la respuesta fue incorrecta, false en caso contrario
     */
    public boolean esIncorrecta() {
        return "fallo".equals(resultado);
    }
    
    /**
     * Verifica si la pregunta no ha sido respondida.
     * 
     * @return true si la pregunta no ha sido respondida, false en caso contrario
     */
    public boolean noContestada() {
        return "sin contestar".equals(resultado);
    }
    
    /**
     * Obtiene el tiempo dedicado en formato legible.
     * 
     * @return Tiempo dedicado formateado
     */
    public String getTiempoDedicadoFormateado() {
        if (tiempoDedicado == null || tiempoDedicado == 0) {
            return "0s";
        }
        
        if (tiempoDedicado < 60) {
            return tiempoDedicado + "s";
        } else {
            int minutos = tiempoDedicado / 60;
            int segundos = tiempoDedicado % 60;
            return String.format("%dm %ds", minutos, segundos);
        }
    }
    
    /**
     * Registra una respuesta del usuario.
     * 
     * @param respuesta Respuesta literal del usuario
     * @param esCorrecta Si la respuesta es correcta
     * @param tiempoDedicado Tiempo dedicado en segundos
     */
    public void registrarRespuesta(String respuesta, boolean esCorrecta, int tiempoDedicado) {
        this.respuesta = respuesta;
        this.resultado = esCorrecta ? "acierto" : "fallo";
        this.tiempoDedicado = tiempoDedicado;
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("PreguntaSesion{id=%d, preguntaId='%s', resultado='%s', tiempo=%s}", 
                           id, preguntaId, resultado, getTiempoDedicadoFormateado());
    }
} 