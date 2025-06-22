package com.kursor.flashcard;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.flashcard.domain.Flashcard;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Map;

/**
 * Módulo para preguntas tipo flashcard.
 * 
 * <p>Este módulo maneja flashcards que pueden mostrar solo el enunciado
 * o el enunciado con la reverso, dependiendo del estado de la flashcard.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see Flashcard
 */
public class FlashcardModule implements PreguntaModule {
    
    @Override
    public String getModuleName() {
        return "Flashcard Module";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para preguntas tipo flashcard";
    }
    
    @Override
    public String getQuestionType() {
        return "flashcard";
    }

    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        String id = (String) preguntaData.get("id");
        String anverso = (String) preguntaData.get("anverso");
        String reverso = (String) preguntaData.get("reverso");
        
        // Crear la flashcard
        Flashcard flashcard = new Flashcard(id, anverso, reverso);
        return flashcard;
    }

    @Override
    public Pregunta createQuestion(String questionId) {
        return null; // Implementar según necesidades específicas
    }
    
    @Override
    public Node createQuestionView(Pregunta pregunta) {
        if (!(pregunta instanceof Flashcard)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para FlashcardModule. Se esperaba Flashcard, se recibió: " + pregunta.getClass().getSimpleName());
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        VBox container = new VBox(15);
        
        // Título de la flashcard
        Label titulo = new Label("Flashcard");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        titulo.setTextFill(Color.DARKBLUE);
        
        // Anverso de la flashcard
        Label anverso = new Label(flashcard.getAnverso());
        anverso.setFont(Font.font("System", FontWeight.NORMAL, 14));
        anverso.setWrapText(true);
        anverso.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10px; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");
        
        // Respuesta (inicialmente oculta)
        Label respuesta = new Label(flashcard.getReverso());
        respuesta.setFont(Font.font("System", FontWeight.NORMAL, 12));
        respuesta.setWrapText(true);
        respuesta.setStyle("-fx-background-color: #e9ecef; -fx-padding: 8px; -fx-border-color: #adb5bd; -fx-border-radius: 3px;");
        respuesta.setVisible(false);
        
        // Botón para mostrar/ocultar reverso
        Button toggleButton = new Button("Voltear");
        toggleButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 12px;");
        
        toggleButton.setOnAction(e -> {
            if (respuesta.isVisible()) {
                respuesta.setVisible(false);
                anverso.setVisible(true);
            } else {
                respuesta.setVisible(true);
                anverso.setVisible(false);
            }
        });
        
        container.getChildren().addAll(titulo, anverso, respuesta, toggleButton);
        
        return container;
    }
    
    @Override
    public boolean validateAnswer(Pregunta pregunta, Object answer) {
        if (!(pregunta instanceof Flashcard)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para FlashcardModule. Se esperaba Flashcard, se recibió: " + pregunta.getClass().getSimpleName());
        }
        
        // Las flashcards no requieren validación tradicional
        // El usuario simplemente revisa el contenido
        return true;
    }
} 