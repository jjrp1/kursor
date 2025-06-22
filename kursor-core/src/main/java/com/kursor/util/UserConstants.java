package com.kursor.util;

/**
 * Constantes relacionadas con el modelo de usuario de la aplicación.
 * 
 * <p>Esta clase define las constantes utilizadas para el modelo monousuario
 * de Kursor, permitiendo futuras expansiones a multiusuario.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class UserConstants {
    
    /**
     * ID del usuario por defecto para la aplicación monousuario.
     * 
     * <p>Este valor se utiliza en todas las entidades que requieren un usuarioId,
     * permitiendo que la aplicación funcione como monousuario mientras mantiene
     * la arquitectura preparada para futuras expansiones a multiusuario.</p>
     */
    public static final String DEFAULT_USER_ID = "default_user";
    
    /**
     * Nombre descriptivo del usuario por defecto.
     */
    public static final String DEFAULT_USER_NAME = "Usuario Principal";
    
    /**
     * Constructor privado para evitar instanciación.
     */
    private UserConstants() {
        throw new UnsupportedOperationException("Esta clase no debe ser instanciada");
    }
    
    /**
     * Verifica si el ID de usuario proporcionado es el usuario por defecto.
     * 
     * @param usuarioId ID del usuario a verificar
     * @return true si es el usuario por defecto, false en caso contrario
     */
    public static boolean isDefaultUser(String usuarioId) {
        return DEFAULT_USER_ID.equals(usuarioId);
    }
    
    /**
     * Obtiene el ID del usuario por defecto si el proporcionado es null o vacío.
     * 
     * @param usuarioId ID del usuario a validar
     * @return El ID del usuario por defecto si el proporcionado es null o vacío
     */
    public static String getDefaultUserIdIfNull(String usuarioId) {
        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            return DEFAULT_USER_ID;
        }
        return usuarioId;
    }
} 