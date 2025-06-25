package com.kursor.multiplechoice;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.multiplechoice.domain.PreguntaTest;
import com.kursor.multiplechoice.ui.MultipleChoiceUIHandler;
import com.kursor.ui.QuestionUIHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Módulo para preguntas de opción múltiple en el sistema Kursor.
 * 
 * <p>Este módulo implementa la funcionalidad para manejar preguntas de tipo
 * test que presentan varias opciones de respuesta, de las cuales solo una
 * es correcta. Es uno de los tipos de pregunta más comunes en sistemas
 * educativos.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Tipo de pregunta:</strong> "test" (opción múltiple)</li>
 *   <li><strong>Interfaz de usuario:</strong> Radio buttons para selección única</li>
 *   <li><strong>Validación:</strong> Comparación exacta de respuestas</li>
 *   <li><strong>Parsing YAML:</strong> Carga desde archivos de configuración</li>
 *   <li><strong>Flexibilidad:</strong> Soporta cualquier número de opciones</li>
 * </ul>
 * 
 * <p>Estructura YAML esperada:</p>
 * <pre>{@code
 * tipo: "test"
 * id: "pregunta1"
 * enunciado: "¿Cuál es la capital de España?"
 * opciones:
 *   - "Madrid"
 *   - "Barcelona"
 *   - "Valencia"
 *   - "Sevilla"
 * respuesta: "Madrid"
 * }</pre>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * MultipleChoiceModule module = new MultipleChoiceModule();
 * 
 * // Crear vista de pregunta
 * Node view = module.createQuestionView(preguntaTest);
 * 
 * // Validar respuesta
 * boolean correcta = module.validateAnswer(preguntaTest, "Madrid");
 * }</pre>
 * 
 * <p>El módulo se registra automáticamente en el sistema a través del
 * archivo META-INF/services/com.kursor.core.PreguntaModule.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see PreguntaTest
 * @see Pregunta
 */
public class MultipleChoiceModule implements PreguntaModule {
    
    /** Logger para registrar eventos del módulo */
    private static final Logger logger = LoggerFactory.getLogger(MultipleChoiceModule.class);
    
    /**
     * Obtiene el nombre descriptivo del módulo.
     * 
     * <p>Este nombre se utiliza para mostrar el módulo en la interfaz
     * de usuario y para propósitos de identificación.</p>
     * 
     * @return El nombre del módulo
     */
    @Override
    public String getModuleName() {
        logger.debug("Obteniendo nombre del módulo MultipleChoice");
        return "MultipleChoice Module";
    }

    /**
     * Obtiene la descripción detallada del módulo.
     * 
     * <p>Esta descripción explica la funcionalidad del módulo y puede
     * ser utilizada en la interfaz de usuario para ayudar a los usuarios
     * a entender qué tipo de preguntas maneja este módulo.</p>
     * 
     * @return La descripción del módulo
     */
    @Override
    public String getModuleDescription() {
        logger.debug("Obteniendo descripción del módulo MultipleChoice");
        return "Módulo para preguntas de opción múltiple";
    }
    
    /**
     * Obtiene el tipo de pregunta que maneja este módulo.
     * 
     * <p>Este identificador se utiliza para asociar preguntas con el
     * módulo correcto durante el proceso de carga desde archivos YAML.</p>
     * 
     * @return El tipo de pregunta ("test")
     */
    @Override
    public String getQuestionType() {
        logger.debug("Obteniendo tipo de pregunta del módulo MultipleChoice: test");
        return "test";
    }

    /**
     * Parsea datos YAML para crear una pregunta de opción múltiple.
     * 
     * <p>Este método interpreta los datos YAML y crea una instancia de
     * {@link PreguntaTest} con todos sus atributos configurados correctamente.
     * Valida que los datos contengan todos los campos necesarios.</p>
     * 
     * <p>Campos requeridos en el YAML:</p>
     * <ul>
     *   <li><strong>id:</strong> Identificador único de la pregunta</li>
     *   <li><strong>enunciado:</strong> Texto de la pregunta</li>
     *   <li><strong>opciones:</strong> Lista de opciones de respuesta</li>
     *   <li><strong>respuesta:</strong> La opción correcta</li>
     * </ul>
     * 
     * @param preguntaData Mapa con los datos YAML de la pregunta
     * @return PreguntaTest creada a partir de los datos YAML
     * @throws IllegalArgumentException si los datos YAML no son válidos
     */
    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        logger.debug("Parseando pregunta de opción múltiple desde YAML - Datos: " + preguntaData);
        
        // Validar datos requeridos
        if (preguntaData == null) {
            logger.error("Error al parsear pregunta: datos YAML no pueden ser null");
            throw new IllegalArgumentException("Datos YAML no pueden ser null");
        }
        
        String id = (String) preguntaData.get("id");
        String enunciado = (String) preguntaData.get("enunciado");
        String respuestaCorrecta = (String) preguntaData.get("respuestaCorrecta");
        
        // Validar campos obligatorios
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al parsear pregunta: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID de pregunta no puede ser null o vacío");
        }
        
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("Error al parsear pregunta: enunciado no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Enunciado de pregunta no puede ser null o vacío");
        }
        
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            logger.error("Error al parsear pregunta: respuesta correcta (respuestaCorrecta) no puede ser null o vacía - ID: " + id);
            throw new IllegalArgumentException("Respuesta correcta (respuestaCorrecta) no puede ser null o vacía");
        }
        
        // Obtener las opciones
        List<String> opciones = new ArrayList<>();
        if (preguntaData.containsKey("opciones")) {
            List<Object> opcionesData = (List<Object>) preguntaData.get("opciones");
            if (opcionesData != null && !opcionesData.isEmpty()) {
                for (Object opcion : opcionesData) {
                    if (opcion != null) {
                        opciones.add(opcion.toString().trim());
                    }
                }
            }
        }
        
        // Validar que hay opciones
        if (opciones.isEmpty()) {
            logger.error("Error al parsear pregunta: debe tener al menos una opción - ID: " + id);
            throw new IllegalArgumentException("La pregunta debe tener al menos una opción");
        }
        
        // Validar que la respuesta correcta está entre las opciones
        if (!opciones.contains(respuestaCorrecta.trim())) {
            logger.error("Error al parsear pregunta: respuesta correcta no está entre las opciones - ID: " + id + 
                        ", Respuesta: " + respuestaCorrecta + ", Opciones: " + opciones);
            throw new IllegalArgumentException("La respuesta correcta debe estar entre las opciones disponibles");
        }
        
        // Crear la pregunta de test
        PreguntaTest pregunta = new PreguntaTest(id.trim(), enunciado.trim(), opciones, respuestaCorrecta.trim());
        
        logger.info("Pregunta de opción múltiple ('test') parseada correctamente: " + pregunta.toString());
        
        return pregunta;
    }

    /**
     * Crea una pregunta de opción múltiple con un ID específico.
     * 
     * <p>En la implementación actual, este método retorna null ya que
     * la creación de preguntas se realiza principalmente a través del
     * método {@link #parsePregunta(Map)} con datos YAML.</p>
     * 
     * @param questionId Identificador de la pregunta a crear
     * @return null (no implementado en esta versión)
     */
    @Override
    public Pregunta createQuestion(String questionId) {
        logger.debug("Creando pregunta de opción múltiple con ID: " + questionId);
        logger.info("Método createQuestion no implementado para MultipleChoiceModule - ID: " + questionId);
        return null; // Implementar según necesidades específicas
    }
    
    /**
     * Crea la interfaz de usuario para una pregunta de opción múltiple.
     * 
     * <p>Este método crea y configura todos los controles JavaFX necesarios
     * para mostrar la pregunta al usuario. La interfaz incluye el enunciado
     * y radio buttons para cada opción de respuesta.</p>
     * 
     * <p>Componentes de la interfaz:</p>
     * <ul>
     *   <li><strong>Enunciado:</strong> Label con el texto de la pregunta</li>
     *   <li><strong>Opciones:</strong> Radio buttons para cada opción</li>
     *   <li><strong>Agrupación:</strong> ToggleGroup para selección única</li>
     * </ul>
     * 
     * @param pregunta La pregunta para la cual crear la vista
     * @return Nodo JavaFX que representa la interfaz de la pregunta
     * @throws IllegalArgumentException si el tipo de pregunta no es compatible
     */
    @Override
    public Node createQuestionView(Pregunta pregunta) {
        logger.debug("Creando vista de pregunta de opción múltiple - ID: " + 
                    (pregunta != null ? pregunta.getId() : "null"));
        
        if (!(pregunta instanceof PreguntaTest)) {
            String errorMsg = "Tipo de pregunta incorrecto para MultipleChoiceModule. Se esperaba PreguntaTest, se recibió: " + 
                            (pregunta != null ? pregunta.getClass().getSimpleName() : "null");
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        VBox container = new VBox(10);
        container.setStyle("-fx-padding: 15px; -fx-background-color: white; -fx-background-radius: 5px;");
        
        // Enunciado de la pregunta
        Label enunciado = new Label(preguntaTest.getEnunciado());
        enunciado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        enunciado.setWrapText(true);
        
        // Opciones de respuesta
        ToggleGroup group = new ToggleGroup();
        for (String opcion : preguntaTest.getOpciones()) {
            RadioButton rb = new RadioButton(opcion);
            rb.setToggleGroup(group);
            rb.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            rb.setWrapText(true);
            container.getChildren().add(rb);
        }
        
        container.getChildren().add(0, enunciado);
        
        logger.info("Vista de pregunta de opción múltiple creada exitosamente - ID: " + preguntaTest.getId() + 
                   ", Opciones: " + preguntaTest.getOpciones().size());
        
        return container;
    }
    
    /**
     * Valida la respuesta del usuario para una pregunta de opción múltiple.
     * 
     * <p>Este método compara la respuesta del usuario con la respuesta
     * correcta almacenada en la pregunta. La comparación es exacta y
     * case-sensitive.</p>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Verifica que la pregunta sea del tipo correcto</li>
     *   <li>Convierte la respuesta a String si es necesario</li>
     *   <li>Compara con la respuesta correcta usando el método de la pregunta</li>
     * </ul>
     * 
     * @param pregunta La pregunta a validar
     * @param answer La respuesta del usuario (puede ser String u Object)
     * @return true si la respuesta es correcta, false en caso contrario
     * @throws IllegalArgumentException si el tipo de pregunta no es compatible
     */
    @Override
    public boolean validateAnswer(Pregunta pregunta, Object answer) {
        if (!(pregunta instanceof PreguntaTest)) {
            logger.error("Pregunta no es del tipo PreguntaTest: {}", pregunta.getClass().getSimpleName());
            return false;
        }
        
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        String respuestaUsuario = answer != null ? answer.toString().trim() : "";
        String respuestaCorrecta = preguntaTest.getRespuestaCorrecta();
        
        boolean esCorrecta = respuestaUsuario.equals(respuestaCorrecta);
        
        logger.debug("Validando respuesta de pregunta 'test' - ID: {}, Respuesta usuario: {}, Respuesta correcta: {}, Es correcta: {}", 
                   pregunta.getId(), respuestaUsuario, respuestaCorrecta, esCorrecta);
        
        return esCorrecta;
    }
    
    @Override
    public QuestionUIHandler createUIHandler() {
        logger.debug("Creando manejador de UI para módulo MultipleChoice");
        return new MultipleChoiceUIHandler();
    }
    
    @Override
    public boolean supportsSpecialActions() {
        return false; // Las preguntas de opción múltiple no tienen acciones especiales
    }
    
    @Override
    public List<String> getSupportedActions() {
        return new ArrayList<>(); // Lista vacía, no hay acciones especiales
    }
} 