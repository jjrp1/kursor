package com.kursor.flashcard;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.flashcard.domain.Flashcard;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
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
        // Los IDs ahora siempre son String en los archivos YAML
        String id = (String) preguntaData.get("id");
        
        // anverso y reverso siempre son String
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
        VBox container = new VBox(20);
        container.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        
        // Título de la flashcard
        Label titulo = new Label("📚 Flashcard");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.DARKBLUE);
        
        // Contenedor principal de la flashcard
        VBox flashcardContainer = new VBox(15);
        flashcardContainer.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-color: #dee2e6; -fx-border-width: 2; -fx-border-radius: 10; -fx-min-width: 400; -fx-min-height: 200;");
        flashcardContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Indicador de anverso/reverso (inicialmente anverso)
        Label indicador = new Label("ANVERSO");
        indicador.setFont(Font.font("System", FontWeight.BOLD, 10));
        indicador.setTextFill(Color.GRAY);
        indicador.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 2 8; -fx-border-color: #dee2e6; -fx-border-radius: 3;");
        
        // Contenido de la flashcard (inicialmente anverso)
        Label contenido = new Label(flashcard.getAnverso());
        contenido.setFont(Font.font("System", FontWeight.NORMAL, 16));
        contenido.setWrapText(true);
        contenido.setStyle("-fx-text-alignment: center; -fx-padding: 10px;");
        contenido.setTextAlignment(TextAlignment.CENTER);
        
        // Botones de acción
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Botón Voltear
        Button botonVoltear = new Button("🔄 Voltear");
        botonVoltear.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        
        // Botón Siguiente
        Button botonSiguiente = new Button("➡️ Siguiente");
        botonSiguiente.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        
        // Botón Terminar
        Button botonTerminar = new Button("🏁 Terminar");
        botonTerminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        
        // Variable para controlar el estado de la flashcard
        boolean[] mostrandoAnverso = {true};
        
        // Evento del botón Voltear
        botonVoltear.setOnAction(e -> {
            if (mostrandoAnverso[0]) {
                // Cambiar a reverso
                indicador.setText("REVERSO");
                contenido.setText(flashcard.getReverso());
                mostrandoAnverso[0] = false;
            } else {
                // Cambiar a anverso
                indicador.setText("ANVERSO");
                contenido.setText(flashcard.getAnverso());
                mostrandoAnverso[0] = true;
            }
        });
        
        // Evento del botón Siguiente (marca como correcta y pasa a la siguiente)
        botonSiguiente.setOnAction(e -> {
            // Marcar la pregunta como contestada y correcta
            // Esto se maneja en el CursoInterfaceModal
            System.out.println("Flashcard marcada como correcta - ID: " + flashcard.getId());
        });
        
        // Evento del botón Terminar
        botonTerminar.setOnAction(e -> {
            // Terminar el curso
            // Esto se maneja en el CursoInterfaceModal
            System.out.println("Usuario solicitó terminar el curso");
        });
        
        // Agregar botones al contenedor
        botonesContainer.getChildren().addAll(botonVoltear, botonSiguiente, botonTerminar);
        
        // Agregar elementos al contenedor de la flashcard
        flashcardContainer.getChildren().addAll(indicador, contenido);
        
        // Agregar todo al contenedor principal
        container.getChildren().addAll(titulo, flashcardContainer, botonesContainer);
        
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
    
    @Override
    public Node createQuestionUI(Pregunta pregunta) {
        if (!(pregunta instanceof Flashcard)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para FlashcardModule. Se esperaba Flashcard, se recibió: " + pregunta.getClass().getSimpleName());
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        VBox container = new VBox(20);
        container.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        
        // Título de la flashcard
        Label titulo = new Label("📚 Flashcard");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.DARKBLUE);
        
        // Contenedor principal de la flashcard
        VBox flashcardContainer = new VBox(15);
        flashcardContainer.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-color: #dee2e6; -fx-border-width: 2; -fx-border-radius: 10; -fx-min-width: 400; -fx-min-height: 200;");
        flashcardContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Indicador de anverso/reverso (inicialmente anverso)
        Label indicador = new Label("ANVERSO");
        indicador.setFont(Font.font("System", FontWeight.BOLD, 10));
        indicador.setTextFill(Color.GRAY);
        indicador.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 2 8; -fx-border-color: #dee2e6; -fx-border-radius: 3;");
        
        // Contenido de la flashcard (inicialmente anverso)
        Label contenido = new Label(flashcard.getAnverso());
        contenido.setFont(Font.font("System", FontWeight.NORMAL, 16));
        contenido.setWrapText(true);
        contenido.setStyle("-fx-text-alignment: center; -fx-padding: 10px;");
        contenido.setTextAlignment(TextAlignment.CENTER);
        
        // Botones de acción
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Botón Voltear
        Button botonVoltear = new Button("🔄 Voltear");
        botonVoltear.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        
        // Botón Siguiente
        Button botonSiguiente = new Button("➡️ Siguiente");
        botonSiguiente.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        
        // Botón Terminar
        Button botonTerminar = new Button("🏁 Terminar");
        botonTerminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        
        // Variable para controlar el estado de la flashcard
        boolean[] mostrandoAnverso = {true};
        
        // Evento del botón Voltear
        botonVoltear.setOnAction(e -> {
            if (mostrandoAnverso[0]) {
                // Cambiar a reverso
                indicador.setText("REVERSO");
                contenido.setText(flashcard.getReverso());
                mostrandoAnverso[0] = false;
            } else {
                // Cambiar a anverso
                indicador.setText("ANVERSO");
                contenido.setText(flashcard.getAnverso());
                mostrandoAnverso[0] = true;
            }
        });
        
        // Evento del botón Siguiente (marca como correcta y pasa a la siguiente)
        botonSiguiente.setOnAction(e -> {
            // Marcar la pregunta como contestada y correcta
            // Esto se maneja en el CursoInterfaceModal
            System.out.println("Flashcard marcada como correcta - ID: " + flashcard.getId());
        });
        
        // Evento del botón Terminar
        botonTerminar.setOnAction(e -> {
            // Terminar el curso
            // Esto se maneja en el CursoInterfaceModal
            System.out.println("Usuario solicitó terminar el curso");
        });
        
        // Agregar botones al contenedor
        botonesContainer.getChildren().addAll(botonVoltear, botonSiguiente, botonTerminar);
        
        // Agregar elementos al contenedor de la flashcard
        flashcardContainer.getChildren().addAll(indicador, contenido);
        
        // Agregar todo al contenedor principal
        container.getChildren().addAll(titulo, flashcardContainer, botonesContainer);
        
        return container;
    }
    
    @Override
    public boolean requiresValidation() {
        // Las flashcards no requieren validación tradicional
        // El usuario simplemente revisa el contenido
        return false;
    }
} 