package com.kursor.presentation.dialogs;

import com.kursor.strategy.EstrategiaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptador que convierte objetos EstrategiaModule en SelectableItem para su uso
 * en el CardSelectorModal.
 * 
 * <p>Este adaptador encapsula la lógica de conversión de una EstrategiaModule
 * a un elemento seleccionable, utilizando directamente los métodos de la estrategia
 * para obtener icono, color y descripción.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SelectableItem
 * @see EstrategiaModule
 * @see CardSelectorModal
 */
public class EstrategiaAdapter implements SelectableItem {
    
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaAdapter.class);
    
    private final EstrategiaModule estrategia;
    
    /**
     * Constructor que crea un adaptador para una estrategia específica.
     * 
     * @param estrategia La estrategia a adaptar
     */
    public EstrategiaAdapter(EstrategiaModule estrategia) {
        this.estrategia = estrategia;
        logger.debug("EstrategiaAdapter creado para estrategia: {}", estrategia.getNombre());
    }
    
    @Override
    public String getId() {
        return estrategia.getNombre();
    }
    
    @Override
    public String getTitle() {
        return estrategia.getNombre();
    }
    
    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        
        // Descripción de la estrategia
        desc.append(estrategia.getDescripcion());
        desc.append("\n\n");
        
        // Información de uso
        if (estrategia.getInformacionUso() != null && !estrategia.getInformacionUso().trim().isEmpty()) {
            desc.append("ℹ️ ").append(estrategia.getInformacionUso());
            desc.append("\n");
        }
        
        // Versión
        desc.append("📦 v").append(estrategia.getVersion());
        
        return desc.toString();
    }
    
    @Override
    public String getIcon() {
        return estrategia.getIcon();
    }
    
    @Override
    public String getColor() {
        return estrategia.getColorTema();
    }
    
    /**
     * Obtiene la estrategia original.
     * 
     * @return La estrategia adaptada
     */
    public EstrategiaModule getEstrategia() {
        return estrategia;
    }
} 