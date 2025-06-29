package com.kursor.persistence.entity;

/**
 * Enum que representa los diferentes estados de una sesión de aprendizaje.
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public enum EstadoSesion {
    
    /**
     * Sesión en curso - el usuario está actualmente trabajando en ella.
     */
    EN_CURSO,
    
    /**
     * Sesión guardada - el usuario ha guardado la sesión para continuar más tarde.
     */
    GUARDADA,
    
    /**
     * Sesión completada - el usuario ha terminado la sesión.
     */
    COMPLETADA
} 