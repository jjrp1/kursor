package com.kursor.truefalse.domain;

import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representa una pregunta de tipo verdadero/falso en el sistema Kursor.
 * 
 * <p>Esta clase extiende {@link Pregunta} para implementar preguntas que requieren
 * una respuesta booleana (verdadero o falso) del usuario. La pregunta incluye un
 * enunciado y la respuesta correcta como valor booleano.</p>
 * 
 * <p>Características específicas:</p>
 * <ul>
 *   <li><strong>Enunciado:</strong> Texto descriptivo de la afirmación</li>
 *   <li><strong>Respuesta booleana:</strong> true para verdadero, false para falso</li>
 *   <li><strong>Verificación flexible:</strong> Acepta strings y booleanos</li>
 *   <li><strong>Case-insensitive:</strong> No distingue mayúsculas/minúsculas</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * PreguntaTrueFalse pregunta = new PreguntaTrueFalse("p1", 
 *     "La capital de España es Madrid", true);
 * boolean esCorrecta = pregunta.esCorrecta("true"); // true
 * boolean esCorrecta2 = pregunta.esCorrecta("TRUE"); // true (case-insensitive)
 * boolean esCorrecta3 = pregunta.esCorrecta(true); // true
 * }</pre>
 * 
 * <p>Casos de uso típicos:</p>
 * <ul>
 *   <li><strong>Afirmaciones:</strong> "El agua hierve a 100°C"</li>
 *   <li><strong>Definiciones:</strong> "Un triángulo tiene 3 lados"</li>
 *   <li><strong>Hechos:</strong> "La Tierra es plana" (false)</li>
 *   <li><strong>Reglas:</strong> "Los verbos se conjugan"</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Pregunta
 */
public class PreguntaTrueFalse extends Pregunta {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(PreguntaTrueFalse.class);
    
    /** Enunciado o texto descriptivo de la afirmación */
    private String enunciado;
    
    /** Respuesta correcta como valor booleano */
    private boolean respuestaCorrecta;

    /**
     * Constructor para crear una nueva pregunta de verdadero/falso.
     * 
     * <p>Inicializa una pregunta de verdadero/falso con un enunciado y una
     * respuesta correcta booleana. El constructor valida que el enunciado
     * sea válido y registra la creación en el log.</p>
     * 
     * @param id Identificador único de la pregunta (no debe ser null)
     * @param enunciado Texto descriptivo de la afirmación (no debe ser null)
     * @param respuestaCorrecta Respuesta correcta como boolean
     * @throws IllegalArgumentException si el enunciado es null o vacío
     */
    public PreguntaTrueFalse(String id, String enunciado, boolean respuestaCorrecta) {
        super(id, "truefalse");
        
        logger.debug("Creando pregunta verdadero/falso - ID: " + id + ", Enunciado: " + enunciado + 
                    ", Respuesta correcta: " + respuestaCorrecta);
        
        // Validar enunciado
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("Error al crear pregunta verdadero/falso: enunciado no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Enunciado no puede ser null o vacío");
        }
        
        this.enunciado = enunciado.trim();
        this.respuestaCorrecta = respuestaCorrecta;
        
        logger.info("Pregunta verdadero/falso creada exitosamente - ID: " + id);
    }

    /**
     * Obtiene el enunciado de la pregunta.
     * 
     * @return El texto descriptivo de la afirmación
     */
    @Override
    public String getEnunciado() { 
        logger.debug("Obteniendo enunciado de pregunta verdadero/falso - ID: " + getId());
        return enunciado; 
    }
    
    /**
     * Establece el enunciado de la pregunta.
     * 
     * @param enunciado El nuevo texto descriptivo (no debe ser null o vacío)
     * @throws IllegalArgumentException si el enunciado es null o vacío
     */
    public void setEnunciado(String enunciado) { 
        logger.debug("Estableciendo enunciado de pregunta verdadero/falso - ID: " + getId() + 
                    ", Anterior: " + this.enunciado + ", Nuevo: " + enunciado);
        
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("Error al establecer enunciado: no puede ser null o vacío - ID: " + getId());
            throw new IllegalArgumentException("Enunciado no puede ser null o vacío");
        }
        
        this.enunciado = enunciado.trim();
        logger.info("Enunciado de pregunta verdadero/falso actualizado - ID: " + getId());
    }

    /**
     * Obtiene la respuesta correcta.
     * 
     * @return La respuesta correcta como valor booleano
     */
    public boolean getRespuestaCorrecta() { 
        logger.debug("Obteniendo respuesta correcta de pregunta verdadero/falso - ID: " + getId());
        return respuestaCorrecta; 
    }
    
    /**
     * Establece la respuesta correcta.
     * 
     * @param respuestaCorrecta Nueva respuesta correcta como boolean
     */
    public void setRespuestaCorrecta(boolean respuestaCorrecta) { 
        logger.debug("Estableciendo respuesta correcta de pregunta verdadero/falso - ID: " + getId() + 
                    ", Anterior: " + this.respuestaCorrecta + ", Nueva: " + respuestaCorrecta);
        
        this.respuestaCorrecta = respuestaCorrecta;
        logger.info("Respuesta correcta de pregunta verdadero/falso actualizada - ID: " + getId());
    }

    /**
     * Verifica si la respuesta proporcionada es correcta.
     * 
     * <p>Este método acepta tanto strings como booleanos y los convierte
     * apropiadamente para comparar con la respuesta correcta. Los strings
     * se interpretan de manera case-insensitive.</p>
     * 
     * <p>Características de la verificación:</p>
     * <ul>
     *   <li><strong>Flexible:</strong> Acepta strings y booleanos</li>
     *   <li><strong>Case-insensitive:</strong> No distingue mayúsculas/minúsculas</li>
     *   <li><strong>Null-safe:</strong> Maneja respuestas null de forma segura</li>
     *   <li><strong>Logging detallado:</strong> Registra todas las verificaciones</li>
     * </ul>
     * 
     * <p>Strings válidos para true:</p>
     * <ul>
     *   <li>"true", "TRUE", "True", "verdadero", "Verdadero", "VERDADERO"</li>
     *   <li>"yes", "YES", "Yes", "sí", "Sí", "SÍ"</li>
     *   <li>"1", "on", "ON", "On"</li>
     * </ul>
     * 
     * <p>Strings válidos para false:</p>
     * <ul>
     *   <li>"false", "FALSE", "False", "falso", "Falso", "FALSO"</li>
     *   <li>"no", "NO", "No"</li>
     *   <li>"0", "off", "OFF", "Off"</li>
     * </ul>
     * 
     * @param respuesta La respuesta del usuario a verificar (puede ser null)
     * @return true si la respuesta coincide con la correcta, false en caso contrario
     */
    @Override
    public boolean esCorrecta(String respuesta) {
        logger.debug("Verificando respuesta de pregunta verdadero/falso - ID: " + getId() + 
                    ", Respuesta usuario: " + respuesta + ", Respuesta correcta: " + respuestaCorrecta);
        
        // Verificar si la respuesta es null
        if (respuesta == null) {
            logger.debug("Respuesta null en pregunta verdadero/falso - ID: " + getId());
            return false;
        }
        
        // Convertir la respuesta a boolean
        boolean respuestaUsuario = convertirStringABoolean(respuesta);
        
        // Comparar respuestas
        boolean esCorrecta = respuestaUsuario == respuestaCorrecta;
        
        if (esCorrecta) {
            logger.info("Respuesta correcta en pregunta verdadero/falso - ID: " + getId() + 
                       ", Respuesta: " + respuesta + " (" + respuestaUsuario + ")");
        } else {
            logger.debug("Respuesta incorrecta en pregunta verdadero/falso - ID: " + getId() + 
                        ", Esperada: " + respuestaCorrecta + ", Recibida: " + respuesta + " (" + respuestaUsuario + ")");
        }
        
        return esCorrecta;
    }
    
    /**
     * Convierte un string a boolean de manera flexible.
     * 
     * <p>Este método interpreta strings de manera case-insensitive para
     * determinar si representan un valor verdadero o falso.</p>
     * 
     * @param valor String a convertir
     * @return true si el string representa un valor verdadero, false en caso contrario
     */
    private boolean convertirStringABoolean(String valor) {
        if (valor == null) {
            return false;
        }
        
        String valorNormalizado = valor.trim().toLowerCase();
        
        // Valores que representan true
        if (valorNormalizado.equals("true") || 
            valorNormalizado.equals("verdadero") ||
            valorNormalizado.equals("yes") ||
            valorNormalizado.equals("sí") ||
            valorNormalizado.equals("si") ||
            valorNormalizado.equals("1") ||
            valorNormalizado.equals("on")) {
            return true;
        }
        
        // Valores que representan false
        if (valorNormalizado.equals("false") || 
            valorNormalizado.equals("falso") ||
            valorNormalizado.equals("no") ||
            valorNormalizado.equals("0") ||
            valorNormalizado.equals("off")) {
            return false;
        }
        
        // Para cualquier otro valor, intentar parsear como boolean
        try {
            return Boolean.parseBoolean(valorNormalizado);
        } catch (Exception e) {
            logger.debug("No se pudo convertir el valor '" + valor + "' a boolean, asumiendo false");
            return false;
        }
    }
    
    /**
     * Representación en cadena de la pregunta verdadero/falso.
     * 
     * @return String con información básica de la pregunta
     */
    @Override
    public String toString() {
        return String.format("PreguntaTrueFalse{id='%s', enunciado='%s', respuestaCorrecta=%s}", 
                           getId(), enunciado, respuestaCorrecta);
    }
} 