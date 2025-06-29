package com.kursor.domain;

import java.io.Serializable;

/**
 * Representa una respuesta del usuario a una pregunta en el sistema Kursor.
 * 
 * <p>Esta clase encapsula la respuesta proporcionada por el usuario,
 * incluyendo el contenido de la respuesta y si es correcta o no.
 * Es utilizada por las estrategias de aprendizaje para ajustar su
 * comportamiento y por el sistema para mantener estadísticas.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Contenido de respuesta:</strong> El texto o valor de la respuesta</li>
 *   <li><strong>Estado de corrección:</strong> Indica si la respuesta es correcta</li>
 *   <li><strong>Inmutabilidad:</strong> Una vez creada, no se puede modificar</li>
 *   <li><strong>Serializable:</strong> Puede ser persistida y transmitida</li>
 * </ul>
 * 
 * <p>Uso típico:</p>
 * <ul>
 *   <li>Capturar respuestas del usuario en la interfaz</li>
 *   <li>Validar respuestas contra las respuestas correctas</li>
 *   <li>Proporcionar feedback a las estrategias de aprendizaje</li>
 *   <li>Mantener estadísticas de rendimiento</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Respuesta respuesta = new Respuesta("París", true);
 * if (respuesta.esCorrecta()) {
 *     System.out.println("¡Correcto!");
 * } else {
 *     System.out.println("Incorrecto. La respuesta correcta era: " + respuestaCorrecta);
 * }
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Pregunta
 * @see EstrategiaAprendizaje
 * @see Sesion
 */
public class Respuesta implements Serializable {
    
    /** Versión de serialización */
    private static final long serialVersionUID = 1L;
    
    /** Contenido de la respuesta del usuario */
    private final String contenido;
    
    /** Indica si la respuesta es correcta */
    private final boolean correcta;
    
    /** Timestamp de cuando se proporcionó la respuesta */
    private final long timestamp;

    /**
     * Constructor para crear una nueva respuesta.
     * 
     * <p>Inicializa una respuesta con el contenido proporcionado por el usuario
     * y el estado de corrección. El timestamp se establece automáticamente
     * al momento de la creación.</p>
     * 
     * @param contenido El contenido de la respuesta (puede ser null)
     * @param correcta true si la respuesta es correcta, false en caso contrario
     */
    public Respuesta(String contenido, boolean correcta) {
        this.contenido = contenido;
        this.correcta = correcta;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructor para crear una respuesta con timestamp personalizado.
     * 
     * <p>Útil para casos donde se necesita especificar un timestamp específico,
     * como cuando se restaura desde persistencia.</p>
     * 
     * @param contenido El contenido de la respuesta (puede ser null)
     * @param correcta true si la respuesta es correcta, false en caso contrario
     * @param timestamp El timestamp en milisegundos
     */
    public Respuesta(String contenido, boolean correcta, long timestamp) {
        this.contenido = contenido;
        this.correcta = correcta;
        this.timestamp = timestamp;
    }

    /**
     * Obtiene el contenido de la respuesta.
     * 
     * @return El contenido de la respuesta, o null si no hay contenido
     */
    public String getContenido() {
        return contenido;
    }
    
    /**
     * Verifica si la respuesta es correcta.
     * 
     * @return true si la respuesta es correcta, false en caso contrario
     */
    public boolean esCorrecta() {
        return correcta;
    }
    
    /**
     * Obtiene el timestamp de cuando se proporcionó la respuesta.
     * 
     * @return El timestamp en milisegundos desde la época Unix
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Verifica si la respuesta tiene contenido.
     * 
     * @return true si la respuesta tiene contenido no vacío, false en caso contrario
     */
    public boolean tieneContenido() {
        return contenido != null && !contenido.trim().isEmpty();
    }
    
    /**
     * Obtiene el contenido de la respuesta como string, manejando casos null.
     * 
     * @return El contenido de la respuesta, o una cadena vacía si es null
     */
    public String getContenidoComoString() {
        return contenido != null ? contenido : "";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Respuesta respuesta = (Respuesta) obj;
        
        if (correcta != respuesta.correcta) return false;
        if (timestamp != respuesta.timestamp) return false;
        return contenido != null ? contenido.equals(respuesta.contenido) : respuesta.contenido == null;
    }
    
    @Override
    public int hashCode() {
        int result = contenido != null ? contenido.hashCode() : 0;
        result = 31 * result + (correcta ? 1 : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("Respuesta{contenido='%s', correcta=%s, timestamp=%d}", 
                           contenido, correcta, timestamp);
    }
} 
