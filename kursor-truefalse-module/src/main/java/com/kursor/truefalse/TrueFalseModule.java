package com.kursor.truefalse;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.truefalse.domain.PreguntaTrueFalse;
import com.kursor.presentation.controllers.PreguntaEventListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Map;

/**
 * Módulo para preguntas de tipo verdadero/falso.
 * 
 * <p>Este módulo maneja preguntas que requieren una respuesta booleana
 * (verdadero o falso) del usuario.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see PreguntaTrueFalse
 */
public class TrueFalseModule implements PreguntaModule {
    
    @Override
    public String getModuleName() {
        return "Verdaro o Falso";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para preguntas de tipo verdadero o falso";
    }
    
    @Override
    public String getIcon() {
        return "✅❌";
    }

    @Override
    public String getQuestionType() {
        return "truefalse";
    }

    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        String id = (String) preguntaData.get("id");
        String enunciado = (String) preguntaData.get("enunciado");
        String respuestaCorrecta = (String) preguntaData.get("respuesta");
        
        // Convertir la respuesta a boolean
        boolean respuesta = Boolean.parseBoolean(respuestaCorrecta);
        
        // Crear la pregunta de verdadero/falso
        PreguntaTrueFalse pregunta = new PreguntaTrueFalse(id, enunciado, respuesta);
        return pregunta;
    }

    @Override
    public Node createQuestionView(Pregunta pregunta) {
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            throw new IllegalArgumentException("La pregunta debe ser de tipo PreguntaTrueFalse");
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        // Enunciado
        Label lblEnunciado = new Label(preguntaTF.getEnunciado());
        lblEnunciado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblEnunciado.setWrapText(true);
        lblEnunciado.setMaxWidth(600);
        
        // Opciones
        ToggleGroup toggleGroup = new ToggleGroup();
        
        RadioButton rbVerdadero = new RadioButton("Verdadero");
        rbVerdadero.setToggleGroup(toggleGroup);
        rbVerdadero.setStyle("-fx-font-size: 14px;");
        
        RadioButton rbFalso = new RadioButton("Falso");
        rbFalso.setToggleGroup(toggleGroup);
        rbFalso.setStyle("-fx-font-size: 14px;");
        
        VBox opcionesContainer = new VBox(10);
        opcionesContainer.setAlignment(Pos.CENTER_LEFT);
        opcionesContainer.getChildren().addAll(rbVerdadero, rbFalso);
        
        container.getChildren().addAll(lblEnunciado, opcionesContainer);
        
        return container;
    }
    
    @Override
    public void configureCompleteUI(Pregunta pregunta, 
                                  VBox headerContainer, 
                                  VBox contentContainer, 
                                  VBox footerContainer,
                                  PreguntaEventListener eventListener) {
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            throw new IllegalArgumentException("La pregunta debe ser de tipo PreguntaTrueFalse");
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        
        // Configurar cabecera (progreso adicional)
        configurarCabecera(headerContainer, preguntaTF);
        
        // Configurar contenido (pregunta)
        configurarContenido(contentContainer, preguntaTF);
        
        // Configurar pie (botones)
        configurarPie(footerContainer, preguntaTF, eventListener);
    }
    
    private void configurarCabecera(VBox headerContainer, PreguntaTrueFalse pregunta) {
        // Agregar información específica de la pregunta
        Label lblTipoPregunta = new Label("Tipo: Verdadero/Falso");
        lblTipoPregunta.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        // Insertar después del progreso existente
        if (headerContainer.getChildren().size() > 2) {
            headerContainer.getChildren().add(2, lblTipoPregunta);
        } else {
            headerContainer.getChildren().add(lblTipoPregunta);
        }
    }
    
    private void configurarContenido(VBox contentContainer, PreguntaTrueFalse pregunta) {
        // Limpiar contenido existente
        contentContainer.getChildren().clear();
        
        VBox preguntaContainer = new VBox(20);
        preguntaContainer.setPadding(new Insets(30));
        preguntaContainer.setAlignment(Pos.CENTER);
        preguntaContainer.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-border-width: 1;");
        
        // Enunciado
        Label lblEnunciado = new Label(pregunta.getEnunciado());
        lblEnunciado.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-text-alignment: center;");
        lblEnunciado.setWrapText(true);
        lblEnunciado.setMaxWidth(700);
        lblEnunciado.setAlignment(Pos.CENTER);
        
        // Instrucciones
        Label lblInstrucciones = new Label("Selecciona si la afirmación es verdadera o falsa:");
        lblInstrucciones.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d;");
        
        preguntaContainer.getChildren().addAll(lblEnunciado, lblInstrucciones);
        
        contentContainer.getChildren().add(preguntaContainer);
    }
    
    private void configurarPie(VBox footerContainer, PreguntaTrueFalse pregunta, PreguntaEventListener eventListener) {
        // Obtener el contenedor de botones del módulo (primer hijo)
        HBox moduleButtonsContainer = (HBox) footerContainer.getChildren().get(0);
        moduleButtonsContainer.getChildren().clear();
        
        // Crear controles de la pregunta
        ToggleGroup toggleGroup = new ToggleGroup();
        
        RadioButton rbVerdadero = new RadioButton("Verdadero");
        rbVerdadero.setToggleGroup(toggleGroup);
        rbVerdadero.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        
        RadioButton rbFalso = new RadioButton("Falso");
        rbFalso.setToggleGroup(toggleGroup);
        rbFalso.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        
        // Botón de verificar
        Button btnVerificar = new Button("Verificar Respuesta");
        btnVerificar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnVerificar.setDisable(true); // Inicialmente deshabilitado
        
        // Botón de siguiente
        Button btnSiguiente = new Button("Siguiente Pregunta");
        btnSiguiente.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnSiguiente.setVisible(false); // Inicialmente oculto
        
        // Contenedor para opciones
        HBox opcionesContainer = new HBox(20);
        opcionesContainer.setAlignment(Pos.CENTER);
        opcionesContainer.getChildren().addAll(rbVerdadero, rbFalso);
        
        // Contenedor para botones
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        botonesContainer.getChildren().addAll(btnVerificar, btnSiguiente);
        
        // Agregar al contenedor del módulo
        moduleButtonsContainer.getChildren().addAll(opcionesContainer, botonesContainer);
        
        // Configurar eventos
        configurarEventos(rbVerdadero, rbFalso, btnVerificar, btnSiguiente, pregunta, eventListener);
    }
    
    private void configurarEventos(RadioButton rbVerdadero, 
                                 RadioButton rbFalso, 
                                 Button btnVerificar, 
                                 Button btnSiguiente,
                                 PreguntaTrueFalse pregunta,
                                 PreguntaEventListener eventListener) {
        
        // Habilitar botón verificar cuando se selecciona una opción
        rbVerdadero.selectedProperty().addListener((obs, oldVal, newVal) -> {
            btnVerificar.setDisable(!newVal && !rbFalso.isSelected());
        });
        
        rbFalso.selectedProperty().addListener((obs, oldVal, newVal) -> {
            btnVerificar.setDisable(!newVal && !rbVerdadero.isSelected());
        });
        
        // Evento de verificar
        btnVerificar.setOnAction(e -> {
            String respuestaUsuario = rbVerdadero.isSelected() ? "true" : "false";
            boolean esCorrecta = validarRespuesta(pregunta, respuestaUsuario);
            
            // Mostrar resultado
            mostrarResultado(pregunta, esCorrecta, respuestaUsuario, null);
            
            // Notificar al contenedor principal
            eventListener.onRespuestaValidada(esCorrecta);
            
            // Cambiar estado de botones
            btnVerificar.setVisible(false);
            btnSiguiente.setVisible(true);
            rbVerdadero.setDisable(true);
            rbFalso.setDisable(true);
        });
        
        // Evento de siguiente
        btnSiguiente.setOnAction(e -> {
            // El contenedor principal manejará la transición
            // Este botón es solo para UX
        });
    }
    
    @Override
    public boolean validarRespuesta(Pregunta pregunta, Object respuesta) {
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            return false;
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        String respuestaStr = respuesta.toString();
        
        boolean respuestaUsuario = Boolean.parseBoolean(respuestaStr);
        return preguntaTF.esCorrecta(respuestaStr);
    }
    
    @Override
    public void mostrarResultado(Pregunta pregunta, 
                                boolean esCorrecta, 
                                Object respuestaUsuario,
                                VBox contentContainer) {
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            return;
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        
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
        Label lblRespuestaCorrecta = new Label("Respuesta correcta: " + 
            (preguntaTF.getRespuestaCorrecta() ? "Verdadero" : "Falso"));
        lblRespuestaCorrecta.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        
        resultadoContainer.getChildren().addAll(lblIcono, lblMensaje, lblRespuestaCorrecta);
        
        // Si hay contenedor específico, agregar ahí, sino usar el contenido principal
        if (contentContainer != null) {
            contentContainer.getChildren().add(resultadoContainer);
        }
    }
} 