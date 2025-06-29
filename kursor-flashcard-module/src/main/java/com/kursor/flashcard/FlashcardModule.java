package com.kursor.flashcard;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.flashcard.domain.Flashcard;
import com.kursor.ui.PreguntaEventListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;
import java.util.Map;

/**
 * Módulo para preguntas de tipo flashcard.
 * 
 * <p>Este módulo maneja tarjetas de memoria que muestran
 * una pregunta y requieren recordar la respuesta.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see Flashcard
 */
public class FlashcardModule implements PreguntaModule {
    
    @Override
    public String getModuleName() {
        return "Flashcard";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para tarjetas de memoria";
    }
    
    @Override
    public String getIcon() {
        return "🗂️";
    }

    @Override
    public String getQuestionType() {
        return "flashcard";
    }

    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        String id = (String) preguntaData.get("id");
        String pregunta = (String) preguntaData.get("pregunta");
        String respuesta = (String) preguntaData.get("respuesta");
        
        // Crear la flashcard
        Flashcard flashcard = new Flashcard(id, pregunta, respuesta);
        return flashcard;
    }

    @Override
    public Node createQuestionView(Pregunta pregunta) {
        if (!(pregunta instanceof Flashcard)) {
            throw new IllegalArgumentException("La pregunta debe ser de tipo Flashcard");
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        // Pregunta
        Label lblPregunta = new Label(flashcard.getEnunciado());
        lblPregunta.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblPregunta.setWrapText(true);
        lblPregunta.setMaxWidth(600);
        
        // Campo de respuesta
        TextField txtRespuesta = new TextField();
        txtRespuesta.setPromptText("Escribe tu respuesta");
        txtRespuesta.setPrefWidth(300);
        txtRespuesta.setStyle("-fx-font-size: 14px;");
        
        container.getChildren().addAll(lblPregunta, txtRespuesta);
        
        return container;
    }
    
    @Override
    public void configureCompleteUI(Pregunta pregunta, 
                                  VBox headerContainer, 
                                  VBox contentContainer, 
                                  VBox footerContainer,
                                  PreguntaEventListener eventListener) {
        if (!(pregunta instanceof Flashcard)) {
            throw new IllegalArgumentException("La pregunta debe ser de tipo Flashcard");
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        
        // Configurar cabecera (progreso adicional)
        configurarCabecera(headerContainer, flashcard);
        
        // Configurar contenido (pregunta)
        configurarContenido(contentContainer, flashcard);
        
        // Configurar pie (botones)
        configurarPie(footerContainer, flashcard, eventListener);
    }
    
    private void configurarCabecera(VBox headerContainer, Flashcard flashcard) {
        // Agregar información específica de la pregunta
        Label lblTipoPregunta = new Label("Tipo: Flashcard");
        lblTipoPregunta.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        // Insertar después del progreso existente
        if (headerContainer.getChildren().size() > 2) {
            headerContainer.getChildren().add(2, lblTipoPregunta);
        } else {
            headerContainer.getChildren().add(lblTipoPregunta);
        }
    }
    
    private void configurarContenido(VBox contentContainer, Flashcard flashcard) {
        // Limpiar contenido existente
        contentContainer.getChildren().clear();
        
        VBox flashcardContainer = new VBox(20);
        flashcardContainer.setPadding(new Insets(30));
        flashcardContainer.setAlignment(Pos.CENTER);
        flashcardContainer.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-border-width: 1;");
        
        // Pregunta
        Label lblPregunta = new Label(flashcard.getEnunciado());
        lblPregunta.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-text-alignment: center;");
        lblPregunta.setWrapText(true);
        lblPregunta.setMaxWidth(700);
        lblPregunta.setAlignment(Pos.CENTER);
        
        // Instrucciones
        Label lblInstrucciones = new Label("Escribe tu respuesta:");
        lblInstrucciones.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d;");
        
        flashcardContainer.getChildren().addAll(lblPregunta, lblInstrucciones);
        
        contentContainer.getChildren().add(flashcardContainer);
    }
    
    private void configurarPie(VBox footerContainer, Flashcard flashcard, PreguntaEventListener eventListener) {
        // Obtener el contenedor de botones del módulo (primer hijo)
        HBox moduleButtonsContainer = (HBox) footerContainer.getChildren().get(0);
        moduleButtonsContainer.getChildren().clear();
        
        // Campo de respuesta
        TextField txtRespuesta = new TextField();
        txtRespuesta.setPromptText("Escribe tu respuesta");
        txtRespuesta.setPrefWidth(300);
        txtRespuesta.setStyle("-fx-font-size: 14px; -fx-padding: 8 12;");
        
        // Botón de verificar
        Button btnVerificar = new Button("Verificar Respuesta");
        btnVerificar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnVerificar.setDisable(true); // Inicialmente deshabilitado
        
        // Botón de siguiente
        Button btnSiguiente = new Button("Siguiente Pregunta");
        btnSiguiente.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnSiguiente.setVisible(false); // Inicialmente oculto
        
        // Contenedor para respuesta
        HBox respuestaContainer = new HBox(10);
        respuestaContainer.setAlignment(Pos.CENTER);
        respuestaContainer.getChildren().addAll(txtRespuesta, btnVerificar);
        
        // Contenedor para botones
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        botonesContainer.getChildren().add(btnSiguiente);
        
        // Agregar al contenedor del módulo
        moduleButtonsContainer.getChildren().addAll(respuestaContainer, botonesContainer);
        
        // Configurar eventos
        configurarEventos(txtRespuesta, btnVerificar, btnSiguiente, flashcard, eventListener);
    }
    
    private void configurarEventos(TextField txtRespuesta,
                                 Button btnVerificar,
                                 Button btnSiguiente,
                                 Flashcard flashcard,
                                 PreguntaEventListener eventListener) {
        
        // Habilitar botón verificar cuando se escribe algo
        txtRespuesta.textProperty().addListener((obs, oldVal, newVal) -> {
            btnVerificar.setDisable(newVal.trim().isEmpty());
        });
        
        // Evento de verificar
        btnVerificar.setOnAction(e -> {
            String respuestaUsuario = txtRespuesta.getText().trim();
            boolean esCorrecta = validarRespuesta(flashcard, respuestaUsuario);
            
            // Mostrar resultado
            mostrarResultado(flashcard, esCorrecta, respuestaUsuario, null);
            
            // Notificar al contenedor principal
            eventListener.onRespuestaValidada(esCorrecta);
            
            // Cambiar estado de botones
            btnVerificar.setVisible(false);
            btnSiguiente.setVisible(true);
            txtRespuesta.setDisable(true);
        });
        
        // Evento de siguiente
        btnSiguiente.setOnAction(e -> {
            // El contenedor principal manejará la transición
            // Este botón es solo para UX
        });
    }
    
    @Override
    public boolean validarRespuesta(Pregunta pregunta, Object respuesta) {
        if (!(pregunta instanceof Flashcard)) {
            return false;
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        String respuestaStr = respuesta.toString();
        
        return flashcard.esCorrecta(respuestaStr);
    }
    
    @Override
    public void mostrarResultado(Pregunta pregunta, 
                                boolean esCorrecta, 
                                Object respuestaUsuario,
                                VBox contentContainer) {
        if (!(pregunta instanceof Flashcard)) {
            return;
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        
        // Crear panel de resultado
        VBox resultadoContainer = new VBox(15);
        resultadoContainer.setPadding(new Insets(20));
        resultadoContainer.setAlignment(Pos.CENTER);
        resultadoContainer.setStyle("-fx-background-color: " + (esCorrecta ? "#d4edda" : "#f8d7da") + 
                                  "; -fx-border-color: " + (esCorrecta ? "#c3e6cb" : "#f5c6cb") + 
                                  "; -fx-border-radius: 8; -fx-border-width: 1;");
        
        // Icono y mensaje
        String icono = esCorrecta ? "✅" : "❌";
        String mensaje = esCorrecta ? "¡Correcto!" : "Incorrecto";
        String color = esCorrecta ? "#155724" : "#721c24";
        
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 48px;");
        
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        // Respuesta correcta
        Label lblRespuestaCorrecta = new Label("Respuesta correcta: " + flashcard.getReverso());
        lblRespuestaCorrecta.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        
        resultadoContainer.getChildren().addAll(lblIcono, lblMensaje, lblRespuestaCorrecta);
        
        // Si hay contenedor específico, agregar ahí, sino usar el contenido principal
        if (contentContainer != null) {
            contentContainer.getChildren().add(resultadoContainer);
        }
    }
} 