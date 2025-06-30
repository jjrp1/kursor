package com.kursor.presentation.dialogs;

import com.kursor.domain.Bloque;
import com.kursor.domain.Pregunta;
import com.kursor.modules.PreguntaModule;
import com.kursor.shared.util.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptador que convierte objetos Bloque en SelectableItem para su uso
 * en el CardSelectorModal.
 * 
 * <p>Este adaptador encapsula la lógica de conversión de un Bloque del dominio
 * a un elemento seleccionable, incluyendo la determinación del icono basado
 * en el tipo de pregunta predominante y el color basado en el tipo de bloque.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SelectableItem
 * @see Bloque
 * @see CardSelectorModal
 */
public class BloqueAdapter implements SelectableItem {
    
    private static final Logger logger = LoggerFactory.getLogger(BloqueAdapter.class);
    
    private final Bloque bloque;
    
    /**
     * Constructor que crea un adaptador para un bloque específico.
     * 
     * @param bloque El bloque a adaptar
     */
    public BloqueAdapter(Bloque bloque) {
        this.bloque = bloque;
        logger.debug("BloqueAdapter creado para bloque: {}", bloque.getTitulo());
    }
    
    @Override
    public String getId() {
        return bloque.getId();
    }
    
    @Override
    public String getTitle() {
        return bloque.getTitulo();
    }
    
    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        
        // Descripción del bloque
        if (bloque.getDescripcion() != null && !bloque.getDescripcion().trim().isEmpty()) {
            desc.append(bloque.getDescripcion());
        } else {
            desc.append("Bloque de contenido educativo");
        }
        
        desc.append("\n\n");
        
        // Información de preguntas
        desc.append("📊 ").append(bloque.getNumeroPreguntas()).append(" preguntas");
        
        // Tipos de preguntas
        if (bloque.tienePreguntas()) {
            Map<String, Long> tipos = bloque.getPreguntas().stream()
                .collect(Collectors.groupingBy(Pregunta::getTipo, Collectors.counting()));
            
            if (!tipos.isEmpty()) {
                desc.append("\n🎯 Tipos: ");
                desc.append(tipos.entrySet().stream()
                    .map(e -> e.getKey() + " (" + e.getValue() + ")")
                    .collect(Collectors.joining(", ")));
            }
        }
        
        // Tipo de bloque
        if (bloque.getTipo() != null && !bloque.getTipo().trim().isEmpty()) {
            desc.append("\n📝 Tipo: ").append(bloque.getTipo());
        }
        
        return desc.toString();
    }
    
    @Override
    public String getIcon() {
        PreguntaModule module = ModuleManager.getInstance().findModuleByQuestionType(bloque.getTipo());
        return module != null ? module.getIcon() : "📚";
    }
    
    @Override
    public String getColor() {
        PreguntaModule module = ModuleManager.getInstance().findModuleByQuestionType(bloque.getTipo());
        return module != null ? module.getColor() : "#6c757d";
    }
    
    /**
     * Obtiene el bloque original.
     * 
     * @return El bloque adaptado
     */
    public Bloque getBloque() {
        return bloque;
    }
    

    

} 