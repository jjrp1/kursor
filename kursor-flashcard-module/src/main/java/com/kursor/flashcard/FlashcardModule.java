package com.kursor.flashcard;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.flashcard.domain.Flashcard;
import com.kursor.presentation.controllers.PreguntaEventListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * Módulo para preguntas de tipo flashcard.
 * 
 * <p>Este módulo maneja tarjetas de memoria que muestran
 * una pregunta y requieren recordar la respuesta. Es ideal
 * para memorización y repaso de conceptos.</p>
 * 
 * <p>Características del módulo:</p>
 * <ul>
 *   <li><strong>Tipo de pregunta:</strong> "flashcard"</li>
 *   <li><strong>Estructura:</strong> Pregunta + respuesta esperada</li>
 *   <li><strong>Interfaz:</strong> Campo de texto para respuesta libre</li>
 *   <li><strong>Validación:</strong> Comparación de texto (case-insensitive)</li>
 *   <li><strong>Logging:</strong> Registra todo el proceso de creación y validación</li>
 * </ul>
 * 
 * <p>Estructura YAML esperada:</p>
 * <pre>{@code
 * id: "f1"
 * tipo: "flashcard"
 * pregunta: "¿Cuál es la capital de Francia?"
 * respuesta: "París"
 * }</pre>
 * 
 * <p>Componentes de la interfaz:</p>
 * <ul>
 *   <li><strong>Pregunta:</strong> Label con el texto de la pregunta</li>
 *   <li><strong>Campo de respuesta:</strong> TextField para entrada del usuario</li>
 *   <li><strong>Botones:</strong> Verificar y Siguiente</li>
 *   <li><strong>Feedback:</strong> Indicador visual de resultado</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * FlashcardModule modulo = new FlashcardModule();
 * Pregunta flashcard = modulo.parsePregunta(datosYaml);
 * Node vista = modulo.createQuestionView(flashcard);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see Flashcard
 * @see PreguntaEventListener
 */
public class FlashcardModule implements PreguntaModule {
    
    private static final Logger logger = LoggerFactory.getLogger(FlashcardModule.class);
    
    @Override
    public String getModuleName() {
        logger.debug("[FlashcardModule] getModuleName llamado");
        return "Flashcard";
    }

    @Override
    public String getModuleDescription() {
        logger.debug("[FlashcardModule] getModuleDescription llamado");
        return "Módulo para tarjetas de memoria";
    }
    
    @Override
    public String getIcon() {
        logger.debug("[FlashcardModule] getIcon llamado");
        return "📚";
    }
    
    @Override
    public String getColor() {
        logger.debug("[FlashcardModule] getColor llamado");
        return "#28a745"; // Verde para flashcards
    }

    @Override
    public String getQuestionType() {
        logger.debug("[FlashcardModule] getQuestionType llamado");
        return "flashcard";
    }

    /**
     * Parsea datos YAML para crear una pregunta de tipo flashcard.
     * 
     * <p>Este método interpreta los datos YAML y crea una instancia de
     * {@link Flashcard} con todos sus atributos configurados. Realiza
     * validaciones exhaustivas de los datos de entrada.</p>
     * 
     * <p>Proceso de parsing:</p>
     * <ol>
     *   <li>Validar que los datos YAML no sean null</li>
     *   <li>Extraer y validar campos obligatorios (id, pregunta, respuesta)</li>
     *   <li>Crear instancia de Flashcard</li>
     *   <li>Registrar resultado en el log</li>
     * </ol>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Datos YAML no null</li>
     *   <li>ID presente y no vacío</li>
     *   <li>Pregunta presente y no vacía</li>
     *   <li>Respuesta presente y no vacía</li>
     * </ul>
     * 
     * @param preguntaData Mapa con los datos YAML de la pregunta
     * @return Flashcard creada a partir de los datos YAML, nunca null
     * @throws IllegalArgumentException si los datos YAML no son válidos
     * 
     * @see Flashcard
     */
    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        logger.debug("[FlashcardModule] parsePregunta iniciado. Datos recibidos: {}", preguntaData);
        
        // Validar datos requeridos
        if (preguntaData == null) {
            logger.error("[FlashcardModule] Error al parsear flashcard: datos YAML no pueden ser null");
            throw new IllegalArgumentException("Datos YAML no pueden ser null");
        }
        
        logger.debug("[FlashcardModule] Datos YAML validados correctamente");
        
        // Extraer campos obligatorios
        String id = (String) preguntaData.get("id");
        String anverso = (String) preguntaData.get("anverso");
        String reverso = (String) preguntaData.get("reverso");
        
        logger.debug("[FlashcardModule] Campos extraídos:");
        logger.debug("   - ID: '{}'", id);
        logger.debug("   - Anverso: '{}'", anverso);
        logger.debug("   - Reverso: '{}'", reverso);
        
        // Validar campos obligatorios
        if (id == null || id.trim().isEmpty()) {
            logger.error("[FlashcardModule] Error al parsear flashcard: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID de flashcard no puede ser null o vacío");
        }
        
        if (anverso == null || anverso.trim().isEmpty()) {
            logger.error("[FlashcardModule] Error al parsear flashcard: anverso no puede ser null o vacío - ID: {}", id);
            throw new IllegalArgumentException("Anverso de flashcard no puede ser null o vacío");
        }
        
        if (reverso == null || reverso.trim().isEmpty()) {
            logger.error("[FlashcardModule] Error al parsear flashcard: reverso no puede ser null o vacío - ID: {}", id);
            throw new IllegalArgumentException("Reverso de flashcard no puede ser null o vacío");
        }
        
        logger.debug("[FlashcardModule] Campos obligatorios validados correctamente");
        
        // Crear la flashcard
        logger.debug("[FlashcardModule] Creando instancia de Flashcard");
        Flashcard flashcard = new Flashcard(id, anverso, reverso);
        
        logger.debug("[FlashcardModule] Flashcard creada exitosamente");
        logger.debug("[FlashcardModule] Información de la flashcard creada:");
        logger.debug("   - ID: {}", flashcard.getId());
        logger.debug("   - Tipo: {}", flashcard.getTipo());
        logger.debug("   - Pregunta: {}", flashcard.getEnunciado());
        logger.debug("   - Respuesta: {}", flashcard.getReverso());
        
        logger.info("[FlashcardModule] Flashcard parseada correctamente: {}", flashcard.toString());
        
        return flashcard;
    }

    /**
     * Crea la interfaz de usuario para una pregunta de tipo flashcard.
     * 
     * <p>Este método crea y configura todos los controles JavaFX necesarios
     * para mostrar una flashcard al usuario, incluyendo la pregunta y
     * el campo de respuesta.</p>
     * 
     * <p>Componentes creados:</p>
     * <ul>
     *   <li><strong>Contenedor principal:</strong> VBox con espaciado y padding</li>
     *   <li><strong>Label de pregunta:</strong> Muestra el texto de la pregunta</li>
     *   <li><strong>Campo de respuesta:</strong> TextField para entrada del usuario</li>
     * </ul>
     * 
     * @param pregunta La pregunta de tipo Flashcard a mostrar
     * @return Node con la interfaz de usuario configurada
     * @throws IllegalArgumentException si la pregunta no es de tipo Flashcard
     */
    @Override
    public Node createQuestionView(Pregunta pregunta) {
        logger.debug("[FlashcardModule] createQuestionView llamado para pregunta: {}", pregunta != null ? pregunta.getId() : "null");
        
        if (!(pregunta instanceof Flashcard)) {
            logger.error("[FlashcardModule] Error: la pregunta debe ser de tipo Flashcard, pero es: {}", 
                        pregunta != null ? pregunta.getClass().getSimpleName() : "null");
            throw new IllegalArgumentException("La pregunta debe ser de tipo Flashcard");
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        logger.debug("[FlashcardModule] Creando vista para flashcard: {}", flashcard.getId());
        
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
        
        logger.debug("[FlashcardModule] Vista de flashcard creada exitosamente");
        logger.debug("[FlashcardModule] Componentes creados: Label de pregunta, TextField de respuesta");
        
        return container;
    }
    
    /**
     * Configura la interfaz completa para una pregunta de tipo flashcard.
     * 
     * <p>Este método configura todos los componentes de la interfaz de usuario
     * para una flashcard, incluyendo cabecera, contenido y pie de página.</p>
     * 
     * <p>Configuración realizada:</p>
     * <ul>
     *   <li><strong>Cabecera:</strong> Información del tipo de pregunta</li>
     *   <li><strong>Contenido:</strong> Pregunta y campo de respuesta</li>
     *   <li><strong>Pie:</strong> Botones de verificar y siguiente</li>
     * </ul>
     * 
     * @param pregunta La pregunta de tipo Flashcard
     * @param headerContainer Contenedor para la cabecera
     * @param contentContainer Contenedor para el contenido principal
     * @param footerContainer Contenedor para el pie de página
     * @param eventListener Listener para eventos de la pregunta
     * @throws IllegalArgumentException si la pregunta no es de tipo Flashcard
     */
    @Override
    public void configureCompleteUI(Pregunta pregunta, 
                                  VBox headerContainer, 
                                  VBox contentContainer, 
                                  VBox footerContainer,
                                  PreguntaEventListener eventListener) {
        logger.debug("[FlashcardModule] configureCompleteUI llamado para pregunta: {}", 
                    pregunta != null ? pregunta.getId() : "null");
        
        if (!(pregunta instanceof Flashcard)) {
            logger.error("[FlashcardModule] Error: la pregunta debe ser de tipo Flashcard, pero es: {}", 
                        pregunta != null ? pregunta.getClass().getSimpleName() : "null");
            throw new IllegalArgumentException("La pregunta debe ser de tipo Flashcard");
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        logger.debug("[FlashcardModule] Configurando UI completa para flashcard: {}", flashcard.getId());
        
        // Configurar cabecera (progreso adicional)
        logger.debug("[FlashcardModule] Configurando cabecera");
        configurarCabecera(headerContainer, flashcard);
        
        // Configurar contenido (pregunta)
        logger.debug("[FlashcardModule] Configurando contenido");
        configurarContenido(contentContainer, flashcard);
        
        // Configurar pie (botones)
        logger.debug("[FlashcardModule] Configurando pie de página");
        configurarPie(footerContainer, flashcard, eventListener);
        
        logger.debug("[FlashcardModule] UI completa configurada exitosamente");
    }
    
    /**
     * Configura la cabecera de la interfaz de flashcard.
     * 
     * @param headerContainer Contenedor de la cabecera
     * @param flashcard La flashcard a configurar
     */
    private void configurarCabecera(VBox headerContainer, Flashcard flashcard) {
        logger.debug("[FlashcardModule] configurarCabecera llamado para flashcard: {}", flashcard.getId());
        
        // Agregar información específica de la pregunta
        Label lblTipoPregunta = new Label("Tipo: Flashcard");
        lblTipoPregunta.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        // Insertar después del progreso existente
        if (headerContainer.getChildren().size() > 2) {
            headerContainer.getChildren().add(2, lblTipoPregunta);
            logger.debug("[FlashcardModule] Label de tipo agregado en posición 2");
        } else {
            headerContainer.getChildren().add(lblTipoPregunta);
            logger.debug("[FlashcardModule] Label de tipo agregado al final");
        }
        
        logger.debug("[FlashcardModule] Cabecera configurada exitosamente");
    }
    
    /**
     * Configura el contenido principal de la interfaz de flashcard.
     * 
     * @param contentContainer Contenedor del contenido
     * @param flashcard La flashcard a configurar
     */
    private void configurarContenido(VBox contentContainer, Flashcard flashcard) {
        logger.debug("[FlashcardModule] configurarContenido llamado para flashcard: {}", flashcard.getId());
        
        // Limpiar contenido existente
        contentContainer.getChildren().clear();
        logger.debug("[FlashcardModule] Contenido anterior limpiado");
        
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
        
        logger.debug("[FlashcardModule] Contenido configurado exitosamente");
        logger.debug("[FlashcardModule] Componentes agregados: Label de pregunta, Label de instrucciones");
    }
    
    /**
     * Configura el pie de página de la interfaz de flashcard.
     * 
     * @param footerContainer Contenedor del pie de página
     * @param flashcard La flashcard a configurar
     * @param eventListener Listener para eventos
     */
    private void configurarPie(VBox footerContainer, Flashcard flashcard, PreguntaEventListener eventListener) {
        logger.debug("[FlashcardModule] configurarPie llamado para flashcard: {}", flashcard.getId());
        
        // Obtener el contenedor de botones del módulo (primer hijo)
        HBox moduleButtonsContainer = (HBox) footerContainer.getChildren().get(0);
        moduleButtonsContainer.getChildren().clear();
        logger.debug("[FlashcardModule] Contenedor de botones limpiado");
        
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
        
        logger.debug("[FlashcardModule] Componentes del pie creados:");
        logger.debug("   - TextField de respuesta");
        logger.debug("   - Botón verificar (inicialmente deshabilitado)");
        logger.debug("   - Botón siguiente (inicialmente oculto)");
        
        // Configurar eventos
        logger.debug("[FlashcardModule] Configurando eventos");
        configurarEventos(txtRespuesta, btnVerificar, btnSiguiente, flashcard, eventListener);
        
        logger.debug("[FlashcardModule] Pie de página configurado exitosamente");
    }
    
    /**
     * Configura los eventos de la interfaz de flashcard.
     * 
     * @param txtRespuesta Campo de texto para la respuesta
     * @param btnVerificar Botón de verificar
     * @param btnSiguiente Botón de siguiente
     * @param flashcard La flashcard
     * @param eventListener Listener para eventos
     */
    private void configurarEventos(TextField txtRespuesta,
                                 Button btnVerificar,
                                 Button btnSiguiente,
                                 Flashcard flashcard,
                                 PreguntaEventListener eventListener) {
        
        logger.debug("[FlashcardModule] configurarEventos llamado para flashcard: {}", flashcard.getId());
        
        // Habilitar botón verificar cuando se escribe algo
        txtRespuesta.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean habilitado = !newVal.trim().isEmpty();
            btnVerificar.setDisable(!habilitado);
            logger.debug("[FlashcardModule] Estado del botón verificar cambiado: {}", habilitado);
        });
        
        // Evento de verificar
        btnVerificar.setOnAction(e -> {
            String respuestaUsuario = txtRespuesta.getText().trim();
            logger.debug("[FlashcardModule] Botón verificar presionado. Respuesta usuario: '{}'", respuestaUsuario);
            
            if (eventListener != null) {
                boolean esCorrecta = validarRespuesta(flashcard, respuestaUsuario);
                eventListener.onRespuestaValidada(esCorrecta);
                logger.debug("[FlashcardModule] Evento onRespuestaValidada enviado al listener: {}", esCorrecta);
            }
        });
        
        // Evento de siguiente
        btnSiguiente.setOnAction(e -> {
            logger.debug("[FlashcardModule] Botón siguiente presionado");
            if (eventListener != null) {
                eventListener.onSolicitarSiguientePregunta();
                logger.debug("[FlashcardModule] Evento onSolicitarSiguientePregunta enviado al listener");
            }
        });
        
        logger.debug("[FlashcardModule] Eventos configurados exitosamente");
    }
    
    /**
     * Valida la respuesta del usuario para una flashcard.
     * 
     * <p>La validación se realiza comparando la respuesta del usuario
     * con la respuesta correcta de manera case-insensitive.</p>
     * 
     * @param pregunta La pregunta de tipo Flashcard
     * @param respuesta La respuesta del usuario
     * @return true si la respuesta es correcta, false en caso contrario
     */
    @Override
    public boolean validarRespuesta(Pregunta pregunta, Object respuesta) {
        logger.debug("[FlashcardModule] validarRespuesta llamado para pregunta: {}", 
                    pregunta != null ? pregunta.getId() : "null");
        logger.debug("[FlashcardModule] Respuesta recibida: {}", respuesta);
        
        if (!(pregunta instanceof Flashcard)) {
            logger.error("[FlashcardModule] Error: la pregunta debe ser de tipo Flashcard");
            return false;
        }
        
        if (!(respuesta instanceof String)) {
            logger.error("[FlashcardModule] Error: la respuesta debe ser de tipo String");
            return false;
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        String respuestaUsuario = (String) respuesta;
        String respuestaCorrecta = flashcard.getReverso();
        
        boolean esCorrecta = respuestaUsuario.trim().equalsIgnoreCase(respuestaCorrecta.trim());
        
        logger.debug("[FlashcardModule] Validación completada:");
        logger.debug("   - Respuesta usuario: '{}'", respuestaUsuario);
        logger.debug("   - Respuesta correcta: '{}'", respuestaCorrecta);
        logger.debug("   - Es correcta: {}", esCorrecta);
        
        return esCorrecta;
    }
    
    /**
     * Muestra el resultado de la validación en la interfaz.
     * 
     * @param pregunta La pregunta de tipo Flashcard
     * @param esCorrecta Si la respuesta es correcta
     * @param respuestaUsuario La respuesta del usuario
     * @param contentContainer Contenedor donde mostrar el resultado
     */
    @Override
    public void mostrarResultado(Pregunta pregunta, 
                                boolean esCorrecta, 
                                Object respuestaUsuario,
                                VBox contentContainer) {
        logger.debug("[FlashcardModule] mostrarResultado llamado para pregunta: {}", 
                    pregunta != null ? pregunta.getId() : "null");
        logger.debug("[FlashcardModule] Resultado: correcta={}, respuesta='{}'", esCorrecta, respuestaUsuario);
        
        if (!(pregunta instanceof Flashcard)) {
            logger.error("[FlashcardModule] Error: la pregunta debe ser de tipo Flashcard");
            return;
        }
        
        Flashcard flashcard = (Flashcard) pregunta;
        
        // Crear label de resultado
        Label lblResultado = new Label();
        lblResultado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");
        
        if (esCorrecta) {
            lblResultado.setText("¡Correcto! 🎉");
            lblResultado.setStyle(lblResultado.getStyle() + " -fx-text-fill: #28a745;");
            logger.debug("[FlashcardModule] Mostrando resultado: Correcto");
        } else {
            lblResultado.setText("Incorrecto. La respuesta correcta era: " + flashcard.getReverso());
            lblResultado.setStyle(lblResultado.getStyle() + " -fx-text-fill: #dc3545;");
            logger.debug("[FlashcardModule] Mostrando resultado: Incorrecto");
        }
        
        // Agregar al contenedor
        contentContainer.getChildren().add(lblResultado);
        
        logger.debug("[FlashcardModule] Resultado mostrado exitosamente en la interfaz");
    }
} 