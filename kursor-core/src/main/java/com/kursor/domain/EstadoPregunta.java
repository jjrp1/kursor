package com.kursor.domain;

/**
 * Enum que representa los diferentes estados de una pregunta en el sistema.
 * 
 * <p>Este enum define los estados posibles que puede tener una pregunta
 * durante su ciclo de vida en el sistema de aprendizaje.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public enum EstadoPregunta {
    
    /**
     * Pregunta no ha sido respondida por el usuario.
     * Estado inicial cuando se presenta la pregunta.
     */
    SIN_CONTESTAR,
    
    /**
     * Respuesta del usuario es correcta.
     * Estado después de validar una respuesta correcta.
     */
    CORRECTA,
    
    /**
     * Respuesta del usuario es incorrecta.
     * Estado después de validar una respuesta incorrecta.
     */
    INCORRECTA
} 
