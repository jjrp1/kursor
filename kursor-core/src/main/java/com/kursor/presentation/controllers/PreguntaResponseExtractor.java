package com.kursor.ui;

import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.ArrayList;

/**
 * Extractor de respuestas del usuario desde las vistas de preguntas.
 * 
 * <p>Esta clase se encarga de extraer las respuestas del usuario desde
 * las vistas de preguntas creadas por los módulos. Analiza la estructura
 * de la vista y extrae la respuesta según el tipo de pregunta.</p>
 * 
 * <p>Tipos de preguntas soportados:</p>
 * <ul>
 *   <li><strong>Opción múltiple:</strong> Extrae la opción seleccionada del ToggleGroup</li>
 *   <li><strong>Verdadero/Falso:</strong> Extrae la opción seleccionada del ToggleGroup</li>
 *   <li><strong>Completar huecos:</strong> Extrae el texto del TextField</li>
 *   <li><strong>Flashcard:</strong> No requiere extracción (siempre correcta)</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Pregunta
 */
public class PreguntaResponseExtractor {
    
    /** Logger para registrar eventos del extractor */
    private static final Logger logger = LoggerFactory.getLogger(PreguntaResponseExtractor.class);
    
    /**
     * Extrae la respuesta del usuario desde la vista de la pregunta.
     * 
     * @param vistaPregunta Nodo JavaFX que representa la vista de la pregunta
     * @param pregunta La pregunta para la cual extraer la respuesta
     * @return La respuesta del usuario o null si no se puede extraer
     */
    public static Object extraerRespuesta(Node vistaPregunta, Pregunta pregunta) {
        logger.debug("Extrayendo respuesta para pregunta: " + pregunta.getId() + 
                    ", Tipo: " + pregunta.getTipo());
        
        if (vistaPregunta == null) {
            logger.warn("Vista de pregunta es null");
            return null;
        }
        
        try {
            switch (pregunta.getTipo()) {
                case "test":
                    return extraerRespuestaOpcionMultiple(vistaPregunta);
                case "truefalse":
                    return extraerRespuestaTrueFalse(vistaPregunta);
                case "completar_huecos":
                    return extraerRespuestaCompletarHuecos(vistaPregunta);
                case "flashcard":
                    return extraerRespuestaFlashcard();
                default:
                    logger.warn("Tipo de pregunta no soportado: " + pregunta.getTipo());
                    return null;
            }
        } catch (Exception e) {
            logger.error("Error al extraer respuesta: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Extrae la respuesta de una pregunta de opción múltiple.
     * 
     * @param vistaPregunta Vista de la pregunta
     * @return La opción seleccionada o null si no hay selección
     */
    private static String extraerRespuestaOpcionMultiple(Node vistaPregunta) {
        logger.debug("Extrayendo respuesta de opción múltiple");
        
        if (vistaPregunta instanceof VBox) {
            VBox vbox = (VBox) vistaPregunta;
            
            // Buscar RadioButtons en la vista
            for (Node node : vbox.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton rb = (RadioButton) node;
                    if (rb.isSelected()) {
                        String respuesta = rb.getText();
                        logger.info("Respuesta de opción múltiple extraída: " + respuesta);
                        return respuesta;
                    }
                }
            }
        }
        
        logger.warn("No se encontró respuesta seleccionada en opción múltiple");
        return null;
    }
    
    /**
     * Extrae la respuesta de una pregunta de verdadero/falso.
     * 
     * @param vistaPregunta Vista de la pregunta
     * @return "true" o "false" según la selección, o null si no hay selección
     */
    private static String extraerRespuestaTrueFalse(Node vistaPregunta) {
        logger.debug("Extrayendo respuesta de verdadero/falso");
        
        if (vistaPregunta instanceof VBox) {
            VBox vbox = (VBox) vistaPregunta;
            
            // Buscar RadioButtons en la vista
            for (Node node : vbox.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton rb = (RadioButton) node;
                    if (rb.isSelected()) {
                        String respuesta = rb.getText().toLowerCase();
                        // Convertir "verdadero"/"true" a "true", "falso"/"false" a "false"
                        if (respuesta.contains("verdadero") || respuesta.contains("true")) {
                            logger.info("Respuesta verdadero/falso extraída: true");
                            return "true";
                        } else if (respuesta.contains("falso") || respuesta.contains("false")) {
                            logger.info("Respuesta verdadero/falso extraída: false");
                            return "false";
                        }
                    }
                }
            }
        }
        
        logger.warn("No se encontró respuesta seleccionada en verdadero/falso");
        return null;
    }
    
    /**
     * Extrae la respuesta de una pregunta de completar huecos.
     * 
     * @param vistaPregunta Vista de la pregunta
     * @return El texto ingresado en el TextField o null si está vacío
     */
    private static String extraerRespuestaCompletarHuecos(Node vistaPregunta) {
        logger.debug("Extrayendo respuesta de completar huecos");
        
        if (vistaPregunta instanceof VBox) {
            VBox vbox = (VBox) vistaPregunta;
            
            // Buscar TextField en la vista
            for (Node node : vbox.getChildren()) {
                if (node instanceof TextField) {
                    TextField tf = (TextField) node;
                    String respuesta = tf.getText().trim();
                    if (!respuesta.isEmpty()) {
                        logger.info("Respuesta de completar huecos extraída: " + respuesta);
                        return respuesta;
                    }
                } else if (node instanceof HBox) {
                    // Buscar en HBox anidados
                    HBox hbox = (HBox) node;
                    for (Node childNode : hbox.getChildren()) {
                        if (childNode instanceof TextField) {
                            TextField tf = (TextField) childNode;
                            String respuesta = tf.getText().trim();
                            if (!respuesta.isEmpty()) {
                                logger.info("Respuesta de completar huecos extraída: " + respuesta);
                                return respuesta;
                            }
                        }
                    }
                }
            }
        }
        
        logger.warn("No se encontró respuesta en completar huecos");
        return null;
    }
    
    /**
     * Extrae la respuesta de una flashcard.
     * 
     * <p>Las flashcards no requieren validación, por lo que siempre
     * se consideran correctas.</p>
     * 
     * @return Siempre retorna "correct" para flashcards
     */
    private static String extraerRespuestaFlashcard() {
        logger.debug("Extrayendo respuesta de flashcard");
        logger.info("Flashcard - respuesta siempre correcta");
        return "correct";
    }
    
    /**
     * Verifica si la vista de pregunta tiene una respuesta válida.
     * 
     * @param vistaPregunta Vista de la pregunta
     * @param pregunta La pregunta a verificar
     * @return true si hay una respuesta válida, false en caso contrario
     */
    public static boolean tieneRespuestaValida(Node vistaPregunta, Pregunta pregunta) {
        Object respuesta = extraerRespuesta(vistaPregunta, pregunta);
        boolean tieneRespuesta = respuesta != null && !respuesta.toString().trim().isEmpty();
        
        logger.debug("Verificando respuesta válida - Tiene respuesta: " + tieneRespuesta);
        return tieneRespuesta;
    }
    
    /**
     * Obtiene todas las respuestas posibles de una vista de pregunta.
     * 
     * @param vistaPregunta Vista de la pregunta
     * @param pregunta La pregunta
     * @return Lista de todas las opciones disponibles
     */
    public static List<String> obtenerOpcionesDisponibles(Node vistaPregunta, Pregunta pregunta) {
        List<String> opciones = new ArrayList<>();
        
        if (vistaPregunta instanceof VBox) {
            VBox vbox = (VBox) vistaPregunta;
            
            for (Node node : vbox.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton rb = (RadioButton) node;
                    opciones.add(rb.getText());
                }
            }
        }
        
        logger.debug("Opciones disponibles extraídas: " + opciones.size());
        return opciones;
    }
} 