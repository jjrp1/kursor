package com.kursor.fillblanks;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.fillblanks.domain.PreguntaCompletarHuecos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Módulo para preguntas de completar huecos.
 * 
 * <p>Este módulo maneja preguntas donde el usuario debe completar
 * espacios en blanco con las palabras correctas.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see PreguntaCompletarHuecos
 */
public class FillBlanksModule implements PreguntaModule {
    
    @Override
    public String getModuleName() {
        return "Fill Blanks Module";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para preguntas de completar huecos";
    }
    
    @Override
    public String getQuestionType() {
        return "completar_huecos";
    }

    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        // Los IDs ahora siempre son String en los archivos YAML
        String id = (String) preguntaData.get("id");
        
        String enunciado = (String) preguntaData.get("enunciado");
        
        // Obtener la respuesta correcta
        String respuestaCorrecta = null;
        if (preguntaData.containsKey("respuestaCorrecta")) {
            Object respuestaObj = preguntaData.get("respuestaCorrecta");
            // respuestaCorrecta = (respuestaObj instanceof Integer) ? String.valueOf(respuestaObj) : (String) respuestaObj;
            respuestaCorrecta = (String) respuestaObj; // Siempre cadena
        } else if (preguntaData.containsKey("respuesta_correcta")) {
            Object respuestaObj = preguntaData.get("respuesta_correcta");
            // respuestaCorrecta = (respuestaObj instanceof Integer) ? String.valueOf(respuestaObj) : (String) respuestaObj;
            respuestaCorrecta = (String) respuestaObj; // Siempre cadena
        }
        
        // Debug: mostrar todos los datos recibidos
        System.out.println("INFO FillBlanksModule - Datos completos: " + preguntaData);
        System.out.println("INFO FillBlanksModule - ID: " + id);
        System.out.println("INFO FillBlanksModule - Enunciado: " + enunciado);
        System.out.println("INFO FillBlanksModule - Contiene respuestaCorrecta: " + preguntaData.containsKey("respuestaCorrecta"));
        System.out.println("INFO FillBlanksModule - Valor respuestaCorrecta: " + preguntaData.get("respuestaCorrecta"));
        System.out.println("INFO FillBlanksModule - Tipo de respuestaCorrecta: " + (preguntaData.get("respuestaCorrecta") != null ? preguntaData.get("respuestaCorrecta").getClass().getSimpleName() : "null"));
        System.out.println("INFO FillBlanksModule - Respuesta correcta final: '" + respuestaCorrecta + "'");
        
        // Crear la pregunta
        PreguntaCompletarHuecos pregunta = new PreguntaCompletarHuecos(id, enunciado, respuestaCorrecta);
        return pregunta;
    }

    @Override
    public Pregunta createQuestion(String questionId) {
        return null; // Implementar según necesidades específicas
    }
    
    @Override
    public Node createQuestionView(Pregunta pregunta) {
        if (!(pregunta instanceof PreguntaCompletarHuecos)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para FillBlanksModule. Se esperaba PreguntaCompletarHuecos, se recibió: " + pregunta.getClass().getSimpleName());
        }
        
        PreguntaCompletarHuecos preguntaCH = (PreguntaCompletarHuecos) pregunta;
        VBox container = new VBox(10);
        
        // Enunciado de la pregunta
        Label enunciado = new Label(preguntaCH.getEnunciado());
        enunciado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        enunciado.setWrapText(true);
        
        // Campo de texto para completar hueco
        HBox campoBox = new HBox(5);
        Label label = new Label("Respuesta:");
        label.setStyle("-fx-font-size: 12px;");
        
        TextField campo = new TextField();
        campo.setPromptText("Escribe tu respuesta");
        campo.setPrefWidth(200);
        
        campoBox.getChildren().addAll(label, campo);
        container.getChildren().addAll(enunciado, campoBox);
        
        return container;
    }
    
    @Override
    public boolean validateAnswer(Pregunta pregunta, Object answer) {
        if (!(pregunta instanceof PreguntaCompletarHuecos)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para FillBlanksModule. Se esperaba PreguntaCompletarHuecos, se recibió: " + pregunta.getClass().getSimpleName());
        }
        
        PreguntaCompletarHuecos preguntaCH = (PreguntaCompletarHuecos) pregunta;
        
        if (answer == null) {
            return false;
        }
        
        // Convertir la respuesta a String
        String respuestaUsuario;
        if (answer instanceof String) {
            respuestaUsuario = (String) answer;
        } else {
            respuestaUsuario = answer.toString();
        }
        
        return preguntaCH.esCorrecta(respuestaUsuario);
    }
} 