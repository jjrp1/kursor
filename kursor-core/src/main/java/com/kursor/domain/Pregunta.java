package com.kursor.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase abstracta que representa una pregunta genérica en el sistema de aprendizaje Kursor.
 * 
 * <p>Esta clase sirve como base para todos los tipos de preguntas en el sistema,
 * proporcionando una interfaz común para la verificación de respuestas y obtención
 * de enunciados. Cada tipo específico de pregunta debe implementar los métodos
 * abstractos según su lógica particular.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Identificación única:</strong> Cada pregunta tiene un ID único</li>
 *   <li><strong>Tipado:</strong> Cada pregunta tiene un tipo específico</li>
 *   <li><strong>Verificación de respuestas:</strong> Método abstracto para validar respuestas</li>
 *   <li><strong>Enunciado:</strong> Método abstracto para obtener el texto de la pregunta</li>
 * </ul>
 * 
 * <p>Tipos de preguntas soportados (implementados en módulos):</p>
 * <ul>
 *   <li><strong>Test:</strong> Preguntas de opción múltiple (módulo multiplechoice)</li>
 *   <li><strong>True/False:</strong> Preguntas de verdadero o falso (módulo truefalse)</li>
 *   <li><strong>Completar huecos:</strong> Preguntas de completar espacios (módulo fillblanks)</li>
 *   <li><strong>Flashcard:</strong> Tarjetas de memoria (módulo flashcard)</li>
 * </ul>
 * 
 * <p>Esta clase es parte del core del sistema y no debe contener implementaciones
 * específicas de tipos de pregunta. Las implementaciones específicas se encuentran
 * en sus respectivos módulos siguiendo la arquitectura modular del dominio.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see com.kursor.factory.PreguntaFactory
 * @see com.kursor.modules.PreguntaModule
 */
public abstract class Pregunta {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(Pregunta.class);
    
    /** Identificador único de la pregunta */
    private String id;
    
    /** Tipo de pregunta (test, truefalse, completar_huecos, flashcard, etc.) */
    private String tipo;

    /**
     * Constructor para crear una nueva pregunta.
     * 
     * <p>Inicializa una pregunta con un identificador único y un tipo específico.
     * El constructor registra la creación de la pregunta en el log para
     * facilitar el debugging y seguimiento.</p>
     * 
     * @param id Identificador único de la pregunta (no debe ser null)
     * @param tipo Tipo de pregunta (no debe ser null)
     * @throws IllegalArgumentException si id o tipo son null o vacíos
     */
    public Pregunta(String id, String tipo) {
        logger.debug("Creando nueva pregunta - ID: " + id + ", Tipo: " + tipo);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al crear pregunta: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID de pregunta no puede ser null o vacío");
        }
        
        if (tipo == null || tipo.trim().isEmpty()) {
            logger.error("Error al crear pregunta: Tipo no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Tipo de pregunta no puede ser null o vacío");
        }
        
        this.id = id.trim();
        this.tipo = tipo.trim();
        
        logger.info("Pregunta creada exitosamente - ID: " + this.id + ", Tipo: " + this.tipo);
    }

    /**
     * Obtiene el identificador único de la pregunta.
     * 
     * @return El ID de la pregunta
     */
    public String getId() { 
        logger.debug("Obteniendo ID de pregunta: " + id);
        return id; 
    }
    
    /**
     * Establece el identificador único de la pregunta.
     * 
     * @param id El nuevo ID de la pregunta (no debe ser null o vacío)
     * @throws IllegalArgumentException si el ID es null o vacío
     */
    public void setId(String id) { 
        logger.debug("Estableciendo ID de pregunta - Anterior: " + this.id + ", Nuevo: " + id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al establecer ID: no puede ser null o vacío");
            throw new IllegalArgumentException("ID de pregunta no puede ser null o vacío");
        }
        
        this.id = id.trim();
        logger.info("ID de pregunta actualizado: " + this.id);
    }

    /**
     * Obtiene el tipo de la pregunta.
     * 
     * @return El tipo de pregunta
     */
    public String getTipo() { 
        logger.debug("Obteniendo tipo de pregunta: " + tipo);
        return tipo; 
    }
    
    /**
     * Establece el tipo de la pregunta.
     * 
     * @param tipo El nuevo tipo de pregunta (no debe ser null o vacío)
     * @throws IllegalArgumentException si el tipo es null o vacío
     */
    public void setTipo(String tipo) { 
        logger.debug("Estableciendo tipo de pregunta - Anterior: " + this.tipo + ", Nuevo: " + tipo);
        
        if (tipo == null || tipo.trim().isEmpty()) {
            logger.error("Error al establecer tipo: no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Tipo de pregunta no puede ser null o vacío");
        }
        
        this.tipo = tipo.trim();
        logger.info("Tipo de pregunta actualizado: " + this.tipo + " - ID: " + id);
    }

    /**
     * Verifica si la respuesta proporcionada es correcta.
     * 
     * <p>Este método debe ser implementado por cada subclase específica
     * según la lógica particular del tipo de pregunta. El método registra
     * la verificación en el log para facilitar el debugging.</p>
     * 
     * @param respuesta La respuesta a verificar (puede ser null)
     * @return true si la respuesta es correcta, false en caso contrario
     * @throws UnsupportedOperationException si la implementación no está disponible
     */
    public abstract boolean esCorrecta(String respuesta);

    /**
     * Obtiene el enunciado de la pregunta.
     * 
     * <p>Este método debe ser implementado por cada subclase específica
     * para proporcionar el texto de la pregunta según su tipo particular.</p>
     * 
     * @return El enunciado de la pregunta
     * @throws UnsupportedOperationException si la implementación no está disponible
     */
    public abstract String getEnunciado();
    
    /**
     * Representación en cadena de la pregunta.
     * 
     * @return String con información básica de la pregunta
     */
    @Override
    public String toString() {
        return String.format("Pregunta{id='%s', tipo='%s'}", id, tipo);
    }
} 
