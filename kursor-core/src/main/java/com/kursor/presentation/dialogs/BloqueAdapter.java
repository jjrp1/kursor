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
    private final PreguntaModule moduloPredominante;
    
    /**
     * Constructor que crea un adaptador para un bloque específico.
     * 
     * @param bloque El bloque a adaptar
     * @param moduloPredominante El módulo de pregunta predominante en el bloque
     */
    public BloqueAdapter(Bloque bloque, PreguntaModule moduloPredominante) {
        this.bloque = bloque;
        this.moduloPredominante = moduloPredominante;
        logger.debug("BloqueAdapter creado para bloque: {}", bloque.getTitulo());
    }
    
    /**
     * Constructor que determina automáticamente el módulo predominante.
     * 
     * @param bloque El bloque a adaptar
     */
    public BloqueAdapter(Bloque bloque) {
        this.bloque = bloque;
        this.moduloPredominante = determinarModuloPredominante(bloque);
        logger.debug("BloqueAdapter creado para bloque: {} con módulo predominante: {}", 
                    bloque.getTitulo(), 
                    moduloPredominante != null ? moduloPredominante.getModuleName() : "null");
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
        if (moduloPredominante != null) {
            return moduloPredominante.getIcon();
        }
        return "📚"; // Icono por defecto para bloques
    }
    
    @Override
    public String getColor() {
        return obtenerColorPorTipo(bloque.getTipo());
    }
    
    /**
     * Obtiene el bloque original.
     * 
     * @return El bloque adaptado
     */
    public Bloque getBloque() {
        return bloque;
    }
    
    /**
     * Determina el módulo de pregunta predominante en un bloque.
     * 
     * @param bloque El bloque a analizar
     * @return El módulo predominante, o null si no hay preguntas
     */
    private PreguntaModule determinarModuloPredominante(Bloque bloque) {
        if (!bloque.tienePreguntas()) {
            return null;
        }
        
        // Contar tipos de preguntas
        Map<String, Long> conteoTipos = bloque.getPreguntas().stream()
            .collect(Collectors.groupingBy(
                Pregunta::getTipo, 
                Collectors.counting()
            ));
        
        // Obtener el tipo más frecuente
        String tipoPredominante = conteoTipos.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("test");
        
        logger.debug("Tipo predominante en bloque {}: {}", bloque.getTitulo(), tipoPredominante);
        
        // Obtener el módulo correspondiente
        try {
            return ModuleManager.getInstance().findModuleByQuestionType(tipoPredominante);
        } catch (Exception e) {
            logger.warn("No se pudo obtener el módulo para tipo: {}", tipoPredominante, e);
            return null;
        }
    }
    
    /**
     * Obtiene el color CSS basado en el tipo de bloque.
     * 
     * @param tipo El tipo de bloque
     * @return El color CSS correspondiente
     */
    private String obtenerColorPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return "#6c757d"; // Gris por defecto
        }
        
        return switch (tipo.toLowerCase().trim()) {
            case "teoria" -> "#007bff";      // Azul
            case "practica" -> "#28a745";    // Verde
            case "evaluacion" -> "#dc3545";  // Rojo
            case "repaso" -> "#ffc107";      // Amarillo
            case "introduccion" -> "#17a2b8"; // Cian
            case "ejercicios" -> "#fd7e14";  // Naranja
            case "resumen" -> "#6f42c1";     // Púrpura
            default -> "#6c757d";            // Gris
        };
    }
} 