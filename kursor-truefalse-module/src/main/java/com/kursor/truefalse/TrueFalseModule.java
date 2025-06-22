package com.kursor.truefalse;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.truefalse.domain.PreguntaTrueFalse;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Map;

/**
 * Módulo para preguntas de tipo verdadero/falso.
 * 
 * <p>Este módulo maneja preguntas que requieren una respuesta booleana
 * (verdadero o falso) del usuario.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see PreguntaTrueFalse
 */
public class TrueFalseModule implements PreguntaModule {
    
    @Override
    public String getModuleName() {
        return "True/False Module";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para preguntas de verdadero/falso";
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
    public Pregunta createQuestion(String questionId) {
        return null; // Implementar según necesidades específicas
    }
    
    @Override
    public Node createQuestionView(Pregunta pregunta) {
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para TrueFalseModule. Se esperaba PreguntaTrueFalse, se recibió: " + pregunta.getClass().getSimpleName());
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        VBox container = new VBox(10);
        
        // Enunciado de la pregunta
        Label enunciado = new Label(preguntaTF.getEnunciado());
        enunciado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        enunciado.setWrapText(true);
        
        // Opciones verdadero/falso
        ToggleGroup group = new ToggleGroup();
        RadioButton rbTrue = new RadioButton("Verdadero");
        RadioButton rbFalse = new RadioButton("Falso");
        
        rbTrue.setToggleGroup(group);
        rbFalse.setToggleGroup(group);
        
        // Configurar estilos
        rbTrue.setStyle("-fx-font-size: 12px;");
        rbFalse.setStyle("-fx-font-size: 12px;");
        
        container.getChildren().addAll(enunciado, rbTrue, rbFalse);
        
        return container;
    }
    
    @Override
    public boolean validateAnswer(Pregunta pregunta, Object answer) {
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para TrueFalseModule. Se esperaba PreguntaTrueFalse, se recibió: " + pregunta.getClass().getSimpleName());
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        
        if (answer == null) {
            return false;
        }
        
        // Convertir la respuesta a boolean
        boolean respuestaUsuario;
        if (answer instanceof Boolean) {
            respuestaUsuario = (Boolean) answer;
        } else if (answer instanceof String) {
            respuestaUsuario = Boolean.parseBoolean((String) answer);
        } else {
            return false;
        }
        
        return preguntaTF.esCorrecta(String.valueOf(respuestaUsuario));
    }
} 