package com.kursor.fillblanks.domain;

import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representa una pregunta de tipo completar huecos en el sistema Kursor.
 * 
 * <p>Esta clase extiende {@link Pregunta} para implementar preguntas donde el usuario
 * debe completar espacios en blanco o huecos en un texto. La pregunta incluye un
 * enunciado con espacios a completar y una respuesta correcta específica.</p>
 * 
 * <p>Características específicas:</p>
 * <ul>
 *   <li><strong>Enunciado:</strong> Texto con espacios a completar</li>
 *   <li><strong>Respuesta exacta:</strong> Texto que debe coincidir exactamente</li>
 *   <li><strong>Comparación case-sensitive:</strong> Distingue mayúsculas y minúsculas</li>
 *   <li><strong>Validación estricta:</strong> Requiere coincidencia exacta</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * PreguntaCompletarHuecos pregunta = new PreguntaCompletarHuecos("p1", 
 *     "La capital de España es ____", "Madrid");
 * boolean esCorrecta = pregunta.esCorrecta("Madrid"); // true
 * boolean esIncorrecta = pregunta.esCorrecta("madrid"); // false (case-sensitive)
 * }</pre>
 * 
 * <p>Casos de uso típicos:</p>
 * <ul>
 *   <li><strong>Completar frases:</strong> "La capital de Francia es ____"</li>
 *   <li><strong>Definiciones:</strong> "Un ____ es un animal que vuela"</li>
 *   <li><strong>Fórmulas:</strong> "La fórmula del agua es ____"</li>
 *   <li><strong>Secuencias:</strong> "El siguiente número es ____"</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Pregunta
 */
public class PreguntaCompletarHuecos extends Pregunta {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(PreguntaCompletarHuecos.class);
    
    /** Enunciado o texto con espacios a completar */
    private String enunciado;
    
    /** Respuesta correcta que debe coincidir exactamente */
    private String respuestaCorrecta;

    /**
     * Constructor para crear una nueva pregunta de completar huecos.
     * 
     * <p>Inicializa una pregunta de completar huecos con un enunciado y una
     * respuesta correcta. El constructor valida que ambos parámetros sean
     * válidos y registra la creación en el log.</p>
     * 
     * @param id Identificador único de la pregunta (no debe ser null)
     * @param enunciado Texto con espacios a completar (no debe ser null)
     * @param respuestaCorrecta Respuesta correcta (no debe ser null)
     * @throws IllegalArgumentException si algún parámetro es null o vacío
     */
    public PreguntaCompletarHuecos(String id, String enunciado, String respuestaCorrecta) {
        super(id, "completar_huecos");
        
        logger.debug("Creando pregunta completar huecos - ID: " + id + ", Enunciado: " + enunciado + 
                    ", Respuesta correcta: " + respuestaCorrecta);
        
        // Validar enunciado
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("Error al crear pregunta completar huecos: enunciado no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Enunciado no puede ser null o vacío");
        }
        
        // Validar respuesta correcta
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            logger.error("Error al crear pregunta completar huecos: respuesta correcta no puede ser null o vacía - ID: " + id);
            throw new IllegalArgumentException("Respuesta correcta no puede ser null o vacía");
        }
        
        this.enunciado = enunciado.trim();
        this.respuestaCorrecta = respuestaCorrecta.trim();
        
        logger.info("Pregunta completar huecos creada exitosamente - ID: " + id);
    }

    /**
     * Obtiene el enunciado de la pregunta.
     * 
     * @return El texto con espacios a completar
     */
    @Override
    public String getEnunciado() { 
        logger.debug("Obteniendo enunciado de pregunta completar huecos - ID: " + getId());
        return enunciado; 
    }
    
    /**
     * Establece el enunciado de la pregunta.
     * 
     * @param enunciado El nuevo texto con espacios a completar (no debe ser null o vacío)
     * @throws IllegalArgumentException si el enunciado es null o vacío
     */
    public void setEnunciado(String enunciado) { 
        logger.debug("Estableciendo enunciado de pregunta completar huecos - ID: " + getId() + 
                    ", Anterior: " + this.enunciado + ", Nuevo: " + enunciado);
        
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("Error al establecer enunciado: no puede ser null o vacío - ID: " + getId());
            throw new IllegalArgumentException("Enunciado no puede ser null o vacío");
        }
        
        this.enunciado = enunciado.trim();
        logger.info("Enunciado de pregunta completar huecos actualizado - ID: " + getId());
    }

    /**
     * Obtiene la respuesta correcta.
     * 
     * @return La respuesta correcta que debe coincidir exactamente
     */
    public String getRespuestaCorrecta() { 
        logger.debug("Obteniendo respuesta correcta de pregunta completar huecos - ID: " + getId());
        return respuestaCorrecta; 
    }
    
    /**
     * Establece la respuesta correcta.
     * 
     * @param respuestaCorrecta Nueva respuesta correcta (no debe ser null o vacía)
     * @throws IllegalArgumentException si la respuesta es null o vacía
     */
    public void setRespuestaCorrecta(String respuestaCorrecta) { 
        logger.debug("Estableciendo respuesta correcta de pregunta completar huecos - ID: " + getId() + 
                    ", Anterior: " + this.respuestaCorrecta + ", Nuevo: " + respuestaCorrecta);
        
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            logger.error("Error al establecer respuesta correcta: no puede ser null o vacía - ID: " + getId());
            throw new IllegalArgumentException("Respuesta correcta no puede ser null o vacía");
        }
        
        this.respuestaCorrecta = respuestaCorrecta.trim();
        logger.info("Respuesta correcta de pregunta completar huecos actualizada - ID: " + getId());
    }

    /**
     * Verifica si la respuesta proporcionada es correcta.
     * 
     * <p>Este método compara la respuesta del usuario con la respuesta correcta
     * de manera exacta (case-sensitive). La comparación es estricta y no permite
     * variaciones en mayúsculas/minúsculas o espacios adicionales.</p>
     * 
     * <p>Características de la verificación:</p>
     * <ul>
     *   <li><strong>Case-sensitive:</strong> Distingue entre mayúsculas y minúsculas</li>
     *   <li><strong>Exacta:</strong> No permite espacios adicionales</li>
     *   <li><strong>Null-safe:</strong> Maneja respuestas null de forma segura</li>
     *   <li><strong>Logging detallado:</strong> Registra todas las verificaciones</li>
     * </ul>
     * 
     * @param respuesta La respuesta del usuario a verificar (puede ser null)
     * @return true si la respuesta coincide exactamente, false en caso contrario
     */
    @Override
    public boolean esCorrecta(String respuesta) {
        logger.debug("Verificando respuesta de pregunta completar huecos - ID: " + getId() + 
                    ", Respuesta usuario: " + respuesta + ", Respuesta correcta: " + respuestaCorrecta);
        
        // Verificar si alguna respuesta es null
        if (respuesta == null || respuestaCorrecta == null) {
            logger.debug("Respuesta null en pregunta completar huecos - ID: " + getId() + 
                        ", Respuesta usuario: " + (respuesta == null ? "null" : "no-null") + 
                        ", Respuesta correcta: " + (respuestaCorrecta == null ? "null" : "no-null"));
            return false;
        }
        
        // Comparación exacta (case-sensitive)
        boolean esCorrecta = respuesta.equals(respuestaCorrecta);
        
        if (esCorrecta) {
            logger.info("Respuesta correcta en pregunta completar huecos - ID: " + getId() + 
                       ", Respuesta: " + respuesta);
        } else {
            logger.debug("Respuesta incorrecta en pregunta completar huecos - ID: " + getId() + 
                        ", Esperada: " + respuestaCorrecta + ", Recibida: " + respuesta);
        }
        
        return esCorrecta;
    }
    
    /**
     * Representación en cadena de la pregunta completar huecos.
     * 
     * @return String con información básica de la pregunta
     */
    @Override
    public String toString() {
        return String.format("PreguntaCompletarHuecos{id='%s', enunciado='%s', respuestaCorrecta='%s'}", 
                           getId(), enunciado, respuestaCorrecta);
    }
} 