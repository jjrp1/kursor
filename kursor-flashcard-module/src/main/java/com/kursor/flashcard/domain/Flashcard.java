package com.kursor.flashcard.domain;

import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representa una flashcard (tarjeta de memoria) en el sistema Kursor.
 * 
 * <p>Esta clase extiende {@link Pregunta} para implementar tarjetas de memoria
 * utilizadas para el estudio y memorización. Las flashcards tienen un anverso
 * (pregunta o concepto) y un reverso (respuesta o definición).</p>
 * 
 * <p>Características específicas:</p>
 * <ul>
 *   <li><strong>Anverso:</strong> Contenido frontal de la tarjeta (pregunta/concepto)</li>
 *   <li><strong>Reverso:</strong> Contenido trasero de la tarjeta (respuesta/definición)</li>
 *   <li><strong>Sin validación:</strong> No requiere verificación de respuestas</li>
 *   <li><strong>Estudio flexible:</strong> Permite diferentes estrategias de aprendizaje</li>
 * </ul>
 * 
 * <p>Casos de uso típicos:</p>
 * <ul>
 *   <li><strong>Vocabulario:</strong> Palabra en anverso, definición en reverso</li>
 *   <li><strong>Conceptos:</strong> Concepto en anverso, explicación en reverso</li>
 *   <li><strong>Fórmulas:</strong> Problema en anverso, solución en reverso</li>
 *   <li><strong>Fechas:</strong> Evento en anverso, fecha en reverso</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Flashcard flashcard = new Flashcard("f1", "¿Cuál es la capital de Francia?", "París");
 * String pregunta = flashcard.getEnunciado(); // "¿Cuál es la capital de Francia?"
 * String respuesta = flashcard.getReverso();   // "París"
 * boolean esCorrecta = flashcard.esCorrecta("cualquier cosa"); // true (no valida)
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Pregunta
 */
public class Flashcard extends Pregunta {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(Flashcard.class);
    
    /** Contenido frontal de la tarjeta (pregunta o concepto) */
    private String anverso;
    
    /** Contenido trasero de la tarjeta (respuesta o definición) */
    private String reverso;

    /**
     * Constructor para crear una nueva flashcard.
     * 
     * <p>Inicializa una flashcard con un anverso y un reverso. El constructor
     * valida que ambos parámetros sean válidos y registra la creación en el log.
     * Las flashcards pueden tener reverso null para casos de estudio unidireccional.</p>
     * 
     * @param id Identificador único de la flashcard (no debe ser null)
     * @param anverso Contenido frontal de la tarjeta (no debe ser null)
     * @param reverso Contenido trasero de la tarjeta (puede ser null)
     * @throws IllegalArgumentException si el anverso es null o vacío
     */
    public Flashcard(String id, String anverso, String reverso) {
        super(id, "flashcard");
        
        logger.debug("Creando flashcard - ID: " + id + ", Anverso: " + anverso + 
                    ", Reverso: " + reverso);
        
        // Validar anverso
        if (anverso == null || anverso.trim().isEmpty()) {
            logger.error("Error al crear flashcard: anverso no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Anverso no puede ser null o vacío");
        }
        
        this.anverso = anverso.trim();
        this.reverso = reverso != null ? reverso.trim() : null;
        
        logger.info("Flashcard creada exitosamente - ID: " + id + 
                   ", Tiene reverso: " + (this.reverso != null));
    }

    /**
     * Obtiene el contenido del anverso de la flashcard.
     * 
     * @return El contenido frontal de la tarjeta
     */
    public String getAnverso() { 
        logger.debug("Obteniendo anverso de flashcard - ID: " + getId());
        return anverso; 
    }
    
    /**
     * Establece el contenido del anverso de la flashcard.
     * 
     * @param anverso El nuevo contenido frontal (no debe ser null o vacío)
     * @throws IllegalArgumentException si el anverso es null o vacío
     */
    public void setAnverso(String anverso) { 
        logger.debug("Estableciendo anverso de flashcard - ID: " + getId() + 
                    ", Anterior: " + this.anverso + ", Nuevo: " + anverso);
        
        if (anverso == null || anverso.trim().isEmpty()) {
            logger.error("Error al establecer anverso: no puede ser null o vacío - ID: " + getId());
            throw new IllegalArgumentException("Anverso no puede ser null o vacío");
        }
        
        this.anverso = anverso.trim();
        logger.info("Anverso de flashcard actualizado - ID: " + getId());
    }
    
    /**
     * Obtiene el contenido del reverso de la flashcard.
     * 
     * @return El contenido trasero de la tarjeta (puede ser null)
     */
    public String getReverso() { 
        logger.debug("Obteniendo reverso de flashcard - ID: " + getId() + 
                    ", Tiene reverso: " + (reverso != null));
        return reverso; 
    }
    
    /**
     * Establece el contenido del reverso de la flashcard.
     * 
     * @param reverso El nuevo contenido trasero (puede ser null)
     */
    public void setReverso(String reverso) { 
        logger.debug("Estableciendo reverso de flashcard - ID: " + getId() + 
                    ", Anterior: " + this.reverso + ", Nuevo: " + reverso);
        
        this.reverso = reverso != null ? reverso.trim() : null;
        logger.info("Reverso de flashcard actualizado - ID: " + getId() + 
                   ", Tiene reverso: " + (this.reverso != null));
    }

    /**
     * Verifica si la respuesta proporcionada es correcta.
     * 
     * <p>Las flashcards no requieren validación de respuestas ya que están
     * diseñadas para el estudio y memorización. Este método siempre retorna
     * true para permitir flexibilidad en las estrategias de aprendizaje.</p>
     * 
     * <p>Razones para no validar:</p>
     * <ul>
     *   <li><strong>Estudio flexible:</strong> Permite diferentes enfoques de aprendizaje</li>
     *   <li><strong>Auto-evaluación:</strong> El usuario evalúa su propio progreso</li>
     *   <li><strong>Múltiples respuestas:</strong> Puede haber varias formas correctas</li>
     *   <li><strong>Proceso de aprendizaje:</strong> El error es parte del aprendizaje</li>
     * </ul>
     * 
     * @param respuesta La respuesta del usuario (no se valida)
     * @return Siempre true (las flashcards no requieren validación)
     */
    @Override
    public boolean esCorrecta(String respuesta) {
        logger.debug("Verificando respuesta de flashcard - ID: " + getId() + 
                    ", Respuesta: " + respuesta + " (no se valida)");
        
        // Las flashcards no requieren validación
        logger.info("Flashcard no requiere validación - ID: " + getId() + 
                   ", Respuesta aceptada automáticamente");
        return true;
    }

    /**
     * Obtiene el enunciado de la flashcard.
     * 
     * <p>Para las flashcards, el enunciado es el contenido del anverso,
     * que representa la pregunta o concepto a estudiar.</p>
     * 
     * @return El contenido del anverso como enunciado
     */
    @Override
    public String getEnunciado() { 
        logger.debug("Obteniendo enunciado de flashcard (anverso) - ID: " + getId());
        return anverso; 
    }
    
    /**
     * Verifica si la flashcard tiene reverso.
     * 
     * @return true si tiene reverso, false en caso contrario
     */
    public boolean tieneReverso() {
        boolean tieneReverso = reverso != null && !reverso.trim().isEmpty();
        logger.debug("Verificando si flashcard tiene reverso - ID: " + getId() + 
                    ", Tiene reverso: " + tieneReverso);
        return tieneReverso;
    }
    
    /**
     * Representación en cadena de la flashcard.
     * 
     * @return String con información básica de la flashcard
     */
    @Override
    public String toString() {
        return String.format("Flashcard{id='%s', anverso='%s', reverso='%s'}", 
                           getId(), anverso, reverso);
    }
} 