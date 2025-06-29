package com.kursor.presentation.dialogs;

/**
 * Interfaz para elementos que pueden ser seleccionados en el CardSelectorModal.
 * 
 * <p>Esta interfaz proporciona una abstracción común para diferentes tipos
 * de elementos seleccionables como bloques, estrategias, etc. Permite que
 * el CardSelectorModal sea genérico y reutilizable.</p>
 * 
 * <p>Los adaptadores implementan esta interfaz para convertir objetos específicos
 * del dominio (Bloque, EstrategiaModule) en elementos seleccionables.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see BloqueAdapter
 * @see EstrategiaAdapter
 * @see CardSelectorModal
 */
public interface SelectableItem {
    
    /**
     * Obtiene el identificador único del elemento.
     * 
     * @return El ID único del elemento
     */
    String getId();
    
    /**
     * Obtiene el título principal para mostrar en la tarjeta.
     * 
     * @return El título del elemento
     */
    String getTitle();
    
    /**
     * Obtiene la descripción detallada del elemento.
     * 
     * @return La descripción del elemento
     */
    String getDescription();
    
    /**
     * Obtiene el icono representativo del elemento.
     * 
     * @return El icono (emoji o símbolo Unicode)
     */
    String getIcon();
    
    /**
     * Obtiene el color temático para la tarjeta.
     * 
     * @return El color en formato CSS (hex, rgb, nombre, etc.)
     */
    String getColor();
} 