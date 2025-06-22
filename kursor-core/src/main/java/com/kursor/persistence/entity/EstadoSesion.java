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
     * Sesión activa - el usuario está actualmente trabajando en ella.
     */
    ACTIVA,
    
    /**
     * Sesión pausada - el usuario ha pausado temporalmente la sesión.
     */
    PAUSADA,
    
    /**
     * Sesión completada - el usuario ha terminado la sesión.
     */
    COMPLETADA,
    
    /**
     * Sesión cancelada - el usuario ha cancelado la sesión.
     */
    CANCELADA
} 