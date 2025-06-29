package com.kursor.fillblanks;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.fillblanks.domain.PreguntaCompletarHuecos;
import com.kursor.presentation.controllers.PreguntaEventListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Módulo para preguntas de tipo completar huecos.
 * 
 * <p>Este módulo maneja preguntas que requieren completar espacios
 * en blanco en un texto.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see PreguntaCompletarHuecos
 */
public class FillBlanksModule implements PreguntaModule {
    
    private static final Logger logger = LoggerFactory.getLogger(FillBlanksModule.class);
       
    @Override
    public String getModuleName() {
        return "Fill Blanks Module";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para preguntas de completar huecos";
    }
    
    @Override
    public String getIcon() {
        return "🔤";
    }

    @Override
    public String getQuestionType() {
        return "completar_huecos";
    }

    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        String id = (String) preguntaData.get("id");
        String enunciado = (String) preguntaData.get("enunciado");
        String respuestaCorrecta = (String) preguntaData.get("respuesta");
        
        // Crear la pregunta de completar huecos
        PreguntaCompletarHuecos pregunta = new PreguntaCompletarHuecos(id, enunciado, respuestaCorrecta);
        return pregunta;
    }

    @Override
    public Node createQuestionView(Pregunta pregunta) {
        if (!(pregunta instanceof PreguntaCompletarHuecos)) {
            throw new IllegalArgumentException("La pregunta debe ser de tipo PreguntaCompletarHuecos");
        }
        
        PreguntaCompletarHuecos preguntaCH = (PreguntaCompletarHuecos) pregunta;
        
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        // Enunciado con hueco
        Label lblEnunciado = new Label(preguntaCH.getEnunciado());
        lblEnunciado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblEnunciado.setWrapText(true);
        lblEnunciado.setMaxWidth(600);
        
        // Campo de texto para completar
        VBox camposContainer = new VBox(10);
        camposContainer.setAlignment(Pos.CENTER_LEFT);
        
        HBox campoRow = new HBox(10);
        campoRow.setAlignment(Pos.CENTER_LEFT);
        
        Label lblNumero = new Label("1.");
        lblNumero.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        TextField txtRespuesta = new TextField();
        txtRespuesta.setPromptText("Escribe tu respuesta");
        txtRespuesta.setPrefWidth(200);
        txtRespuesta.setStyle("-fx-font-size: 14px;");
        
        campoRow.getChildren().addAll(lblNumero, txtRespuesta);
        camposContainer.getChildren().add(campoRow);
        
        container.getChildren().addAll(lblEnunciado, camposContainer);
        
        return container;
    }
    
    @Override
    public void configureCompleteUI(Pregunta pregunta, 
                                  VBox headerContainer, 
                                  VBox contentContainer, 
                                  VBox footerContainer,
                                  PreguntaEventListener eventListener) {
        if (!(pregunta instanceof PreguntaCompletarHuecos)) {
            throw new IllegalArgumentException("La pregunta debe ser de tipo PreguntaCompletarHuecos");
        }
        
        PreguntaCompletarHuecos preguntaCH = (PreguntaCompletarHuecos) pregunta;
        
        // Configurar cabecera (progreso adicional)
        configurarCabecera(headerContainer, preguntaCH);
        
        // Configurar contenido (pregunta)
        configurarContenido(contentContainer, preguntaCH);
        
        // Configurar pie (botones)
        configurarPie(footerContainer, preguntaCH, eventListener);
    }
    
    private void configurarCabecera(VBox headerContainer, PreguntaCompletarHuecos pregunta) {
        // Agregar información específica de la pregunta
        Label lblTipoPregunta = new Label("Tipo: Completar Huecos");
        lblTipoPregunta.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        // Insertar después del progreso existente
        if (headerContainer.getChildren().size() > 2) {
            headerContainer.getChildren().add(2, lblTipoPregunta);
        } else {
            headerContainer.getChildren().add(lblTipoPregunta);
        }
    }
    
    private void configurarContenido(VBox contentContainer, PreguntaCompletarHuecos pregunta) {
        // Limpiar contenido existente
        contentContainer.getChildren().clear();
        
        VBox preguntaContainer = new VBox(20);
        preguntaContainer.setPadding(new Insets(30));
        preguntaContainer.setAlignment(Pos.CENTER);
        preguntaContainer.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-border-width: 1;");
        
        // Enunciado con hueco
        Label lblEnunciado = new Label(pregunta.getEnunciado());
        lblEnunciado.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-text-alignment: center;");
        lblEnunciado.setWrapText(true);
        lblEnunciado.setMaxWidth(700);
        lblEnunciado.setAlignment(Pos.CENTER);
        
        // Instrucciones
        Label lblInstrucciones = new Label("Completa el espacio en blanco:");
        lblInstrucciones.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d;");
        
        preguntaContainer.getChildren().addAll(lblEnunciado, lblInstrucciones);
        
        contentContainer.getChildren().add(preguntaContainer);
    }
    
    private void configurarPie(VBox footerContainer, PreguntaCompletarHuecos pregunta, PreguntaEventListener eventListener) {
        // Obtener el contenedor de botones del módulo (primer hijo)
        HBox moduleButtonsContainer = (HBox) footerContainer.getChildren().get(0);
        moduleButtonsContainer.getChildren().clear();
        
        // Crear campo de texto para completar
        VBox camposContainer = new VBox(10);
        camposContainer.setAlignment(Pos.CENTER);
        
        HBox campoRow = new HBox(10);
        campoRow.setAlignment(Pos.CENTER);
        
        Label lblNumero = new Label("1.");
        lblNumero.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-min-width: 30;");
        
        TextField txtRespuesta = new TextField();
        txtRespuesta.setPromptText("Escribe tu respuesta");
        txtRespuesta.setPrefWidth(200);
        txtRespuesta.setStyle("-fx-font-size: 14px; -fx-padding: 8 12;");
        
        campoRow.getChildren().addAll(lblNumero, txtRespuesta);
        camposContainer.getChildren().add(campoRow);
        
        // Botón de verificar
        Button btnVerificar = new Button("Verificar Respuesta");
        btnVerificar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnVerificar.setDisable(true); // Inicialmente deshabilitado
        
        // Botón de siguiente
        Button btnSiguiente = new Button("Siguiente Pregunta");
        btnSiguiente.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnSiguiente.setVisible(false); // Inicialmente oculto
        
        // Contenedor para botones
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        botonesContainer.getChildren().addAll(btnVerificar, btnSiguiente);
        
        // Agregar al contenedor del módulo
        moduleButtonsContainer.getChildren().addAll(camposContainer, botonesContainer);
        
        // Configurar eventos
        configurarEventos(camposContainer, btnVerificar, btnSiguiente, pregunta, eventListener);
    }
    
    private void configurarEventos(VBox camposContainer,
                                 Button btnVerificar,
                                 Button btnSiguiente,
                                 PreguntaCompletarHuecos pregunta,
                                 PreguntaEventListener eventListener) {
        
        // Habilitar botón verificar cuando se llenan todos los campos
        for (Node node : camposContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                for (Node child : row.getChildren()) {
                    if (child instanceof TextField) {
                        TextField txt = (TextField) child;
                        txt.textProperty().addListener((obs, oldVal, newVal) -> {
                            verificarCamposCompletos(camposContainer, btnVerificar);
                        });
                    }
                }
            }
        }
        
        // Evento de verificar
        btnVerificar.setOnAction(e -> {
            String respuestaUsuario = obtenerRespuestaUsuario(camposContainer);
            boolean esCorrecta = validarRespuesta(pregunta, respuestaUsuario);
            
            // Mostrar resultado
            mostrarResultado(pregunta, esCorrecta, respuestaUsuario, null);
            
            // Notificar al contenedor principal
            eventListener.onRespuestaValidada(esCorrecta);
            
            // Cambiar estado de botones
            btnVerificar.setVisible(false);
            btnSiguiente.setVisible(true);
            
            // Deshabilitar todos los campos
            for (Node node : camposContainer.getChildren()) {
                if (node instanceof HBox) {
                    HBox row = (HBox) node;
                    for (Node child : row.getChildren()) {
                        if (child instanceof TextField) {
                            child.setDisable(true);
                        }
                    }
                }
            }
        });
        
        // Evento de siguiente
        btnSiguiente.setOnAction(e -> {
            // El contenedor principal manejará la transición
            // Este botón es solo para UX
        });
    }
    
    private void verificarCamposCompletos(VBox camposContainer, Button btnVerificar) {
        boolean todosCompletos = true;
        
        for (Node node : camposContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                for (Node child : row.getChildren()) {
                    if (child instanceof TextField) {
                        TextField txt = (TextField) child;
                        if (txt.getText().trim().isEmpty()) {
                            todosCompletos = false;
                            break;
                        }
                    }
                }
            }
        }
        
        btnVerificar.setDisable(!todosCompletos);
    }
    
    private String obtenerRespuestaUsuario(VBox camposContainer) {
        for (Node node : camposContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                for (Node child : row.getChildren()) {
                    if (child instanceof TextField) {
                        TextField txt = (TextField) child;
                        return txt.getText().trim();
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean validarRespuesta(Pregunta pregunta, Object respuesta) {
        if (!(pregunta instanceof PreguntaCompletarHuecos)) {
            return false;
        }
        
        PreguntaCompletarHuecos preguntaCH = (PreguntaCompletarHuecos) pregunta;
        
        if (respuesta instanceof String) {
            return preguntaCH.esCorrecta((String) respuesta);
        }
        
        return false;
    }
    
    @Override
    public void mostrarResultado(Pregunta pregunta, 
                                boolean esCorrecta, 
                                Object respuestaUsuario,
                                VBox contentContainer) {
        if (!(pregunta instanceof PreguntaCompletarHuecos)) {
            return;
        }
        
        PreguntaCompletarHuecos preguntaCH = (PreguntaCompletarHuecos) pregunta;
        
        // Limpiar contenido existente
        contentContainer.getChildren().clear();
        
        VBox resultadoContainer = new VBox(20);
        resultadoContainer.setPadding(new Insets(30));
        resultadoContainer.setAlignment(Pos.CENTER);
        
        // Icono de resultado
        String icono = esCorrecta ? "✅" : "❌";
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 48px;");
        
        // Mensaje de resultado
        String mensaje = esCorrecta ? "¡Correcto!" : "Incorrecto";
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + 
                           (esCorrecta ? "#28a745" : "#dc3545") + ";");
        
        // Respuesta del usuario
        Label lblRespuestaUsuario = new Label("Tu respuesta: " + respuestaUsuario);
        lblRespuestaUsuario.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");
        
        // Respuesta correcta (si es incorrecta)
        if (!esCorrecta) {
            Label lblRespuestaCorrecta = new Label("Respuesta correcta: " + preguntaCH.getRespuestaCorrecta());
            lblRespuestaCorrecta.setStyle("-fx-font-size: 16px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
            resultadoContainer.getChildren().addAll(lblIcono, lblMensaje, lblRespuestaUsuario, lblRespuestaCorrecta);
        } else {
            resultadoContainer.getChildren().addAll(lblIcono, lblMensaje, lblRespuestaUsuario);
        }
        
        contentContainer.getChildren().add(resultadoContainer);
    }
} 