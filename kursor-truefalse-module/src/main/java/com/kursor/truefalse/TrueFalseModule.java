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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * Módulo para preguntas de tipo verdadero/falso.
 * 
 * <p>Este módulo maneja preguntas que requieren una respuesta booleana
 * (verdadero o falso) del usuario. Es ideal para evaluar comprensión
 * de conceptos y afirmaciones.</p>
 * 
 * <p>Características del módulo:</p>
 * <ul>
 *   <li><strong>Tipo de pregunta:</strong> "truefalse"</li>
 *   <li><strong>Estructura:</strong> Enunciado + respuesta booleana</li>
 *   <li><strong>Interfaz:</strong> Radio buttons para selección única</li>
 *   <li><strong>Validación:</strong> Comparación directa de valores booleanos</li>
 *   <li><strong>Logging:</strong> Registra todo el proceso de creación y validación</li>
 * </ul>
 * 
 * <p>Estructura YAML esperada:</p>
 * <pre>{@code
 * id: "tf1"
 * tipo: "truefalse"
 * enunciado: "La capital de España es Madrid"
 * respuesta: "true"
 * }</pre>
 * 
 * <p>Componentes de la interfaz:</p>
 * <ul>
 *   <li><strong>Enunciado:</strong> Label con la afirmación a evaluar</li>
 *   <li><strong>Opciones:</strong> Radio buttons para Verdadero/Falso</li>
 *   <li><strong>Botones:</strong> Verificar y Siguiente</li>
 *   <li><strong>Feedback:</strong> Indicador visual de resultado</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * TrueFalseModule modulo = new TrueFalseModule();
 * Pregunta pregunta = modulo.parsePregunta(datosYaml);
 * Node vista = modulo.createQuestionView(pregunta);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see PreguntaTrueFalse
 * @see PreguntaEventListener
 */
public class TrueFalseModule implements PreguntaModule {
    
    private static final Logger logger = LoggerFactory.getLogger(TrueFalseModule.class);
    
    @Override
    public String getModuleName() {
        logger.debug("[TrueFalseModule] getModuleName llamado");
        return "Verdadero o Falso";
    }

    @Override
    public String getModuleDescription() {
        logger.debug("[TrueFalseModule] getModuleDescription llamado");
        return "Módulo para preguntas de tipo verdadero o falso";
    }
    
    @Override
    public String getIcon() {
        logger.debug("[TrueFalseModule] getIcon llamado");
        return "✅❌";
    }
    
    @Override
    public String getColor() {
        logger.debug("[TrueFalseModule] getColor llamado");
        return "#dc3545"; // Rojo para verdadero/falso
    }

    @Override
    public String getQuestionType() {
        logger.debug("[TrueFalseModule] getQuestionType llamado");
        return "truefalse";
    }

    /**
     * Parsea datos YAML para crear una pregunta de tipo verdadero/falso.
     * 
     * <p>Este método interpreta los datos YAML y crea una instancia de
     * {@link PreguntaTrueFalse} con todos sus atributos configurados. Realiza
     * validaciones exhaustivas de los datos de entrada.</p>
     * 
     * <p>Proceso de parsing:</p>
     * <ol>
     *   <li>Validar que los datos YAML no sean null</li>
     *   <li>Extraer y validar campos obligatorios (id, enunciado, respuesta)</li>
     *   <li>Convertir respuesta a boolean</li>
     *   <li>Crear instancia de PreguntaTrueFalse</li>
     *   <li>Registrar resultado en el log</li>
     * </ol>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Datos YAML no null</li>
     *   <li>ID presente y no vacío</li>
     *   <li>Enunciado presente y no vacío</li>
     *   <li>Respuesta presente y convertible a boolean</li>
     * </ul>
     * 
     * @param preguntaData Mapa con los datos YAML de la pregunta
     * @return PreguntaTrueFalse creada a partir de los datos YAML, nunca null
     * @throws IllegalArgumentException si los datos YAML no son válidos
     * 
     * @see PreguntaTrueFalse
     */
    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        logger.debug("[TrueFalseModule] parsePregunta iniciado. Datos recibidos: {}", preguntaData);
        
        // Validar datos requeridos
        if (preguntaData == null) {
            logger.error("[TrueFalseModule] Error al parsear pregunta: datos YAML no pueden ser null");
            throw new IllegalArgumentException("Datos YAML no pueden ser null");
        }
        
        logger.debug("[TrueFalseModule] Datos YAML validados correctamente");
        
        // Extraer campos obligatorios
        String id = (String) preguntaData.get("id");
        String enunciado = (String) preguntaData.get("enunciado");
        String respuestaCorrecta = (String) preguntaData.get("respuesta");
        
        logger.debug("[TrueFalseModule] Campos extraídos:");
        logger.debug("   - ID: '{}'", id);
        logger.debug("   - Enunciado: '{}'", enunciado);
        logger.debug("   - Respuesta correcta: '{}'", respuestaCorrecta);
        
        // Validar campos obligatorios
        if (id == null || id.trim().isEmpty()) {
            logger.error("[TrueFalseModule] Error al parsear pregunta: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID de pregunta no puede ser null o vacío");
        }
        
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("[TrueFalseModule] Error al parsear pregunta: enunciado no puede ser null o vacío - ID: {}", id);
            throw new IllegalArgumentException("Enunciado de pregunta no puede ser null o vacío");
        }
        
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            logger.error("[TrueFalseModule] Error al parsear pregunta: respuesta correcta no puede ser null o vacía - ID: {}", id);
            throw new IllegalArgumentException("Respuesta correcta no puede ser null o vacía");
        }
        
        logger.debug("[TrueFalseModule] Campos obligatorios validados correctamente");
        
        // Convertir la respuesta a boolean
        boolean respuesta;
        try {
            respuesta = Boolean.parseBoolean(respuestaCorrecta);
            logger.debug("[TrueFalseModule] Respuesta convertida a boolean: {}", respuesta);
        } catch (Exception e) {
            logger.error("[TrueFalseModule] Error al convertir respuesta a boolean: '{}' - ID: {}", respuestaCorrecta, id);
            throw new IllegalArgumentException("Respuesta debe ser 'true' o 'false'");
        }
        
        // Crear la pregunta de verdadero/falso
        logger.debug("[TrueFalseModule] Creando instancia de PreguntaTrueFalse");
        PreguntaTrueFalse pregunta = new PreguntaTrueFalse(id, enunciado, respuesta);
        
        logger.debug("[TrueFalseModule] PreguntaTrueFalse creada exitosamente");
        logger.debug("[TrueFalseModule] Información de la pregunta creada:");
        logger.debug("   - ID: {}", pregunta.getId());
        logger.debug("   - Tipo: {}", pregunta.getTipo());
        logger.debug("   - Enunciado: {}", pregunta.getEnunciado());
        logger.debug("   - Respuesta correcta: {}", pregunta.getRespuestaCorrecta());
        
        logger.info("[TrueFalseModule] Pregunta verdadero/falso parseada correctamente: {}", pregunta.toString());
        
        return pregunta;
    }

    /**
     * Crea la interfaz de usuario para una pregunta de tipo verdadero/falso.
     * 
     * <p>Este método crea y configura todos los controles JavaFX necesarios
     * para mostrar una pregunta verdadero/falso al usuario, incluyendo el
     * enunciado y las opciones de respuesta.</p>
     * 
     * <p>Componentes creados:</p>
     * <ul>
     *   <li><strong>Contenedor principal:</strong> VBox con espaciado y padding</li>
     *   <li><strong>Label de enunciado:</strong> Muestra la afirmación a evaluar</li>
     *   <li><strong>Radio buttons:</strong> Opciones Verdadero y Falso</li>
     *   <li><strong>ToggleGroup:</strong> Agrupación para selección única</li>
     * </ul>
     * 
     * @param pregunta La pregunta de tipo PreguntaTrueFalse a mostrar
     * @return Node con la interfaz de usuario configurada
     * @throws IllegalArgumentException si la pregunta no es de tipo PreguntaTrueFalse
     */
    @Override
    public Node createQuestionView(Pregunta pregunta) {
        logger.debug("[TrueFalseModule] createQuestionView llamado para pregunta: {}", 
                    pregunta != null ? pregunta.getId() : "null");
        
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            logger.error("[TrueFalseModule] Error: la pregunta debe ser de tipo PreguntaTrueFalse, pero es: {}", 
                        pregunta != null ? pregunta.getClass().getSimpleName() : "null");
            throw new IllegalArgumentException("La pregunta debe ser de tipo PreguntaTrueFalse");
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        logger.debug("[TrueFalseModule] Creando vista para pregunta: {}", preguntaTF.getId());
        
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
        
        logger.debug("[TrueFalseModule] Vista de pregunta verdadero/falso creada exitosamente");
        logger.debug("[TrueFalseModule] Componentes creados: Label de enunciado, Radio buttons Verdadero/Falso");
        
        return container;
    }
    
    /**
     * Configura la interfaz completa para una pregunta de tipo verdadero/falso.
     * 
     * <p>Este método configura todos los componentes de la interfaz de usuario
     * para una pregunta verdadero/falso, incluyendo cabecera, contenido y pie de página.</p>
     * 
     * <p>Configuración realizada:</p>
     * <ul>
     *   <li><strong>Cabecera:</strong> Información del tipo de pregunta</li>
     *   <li><strong>Contenido:</strong> Enunciado y opciones de respuesta</li>
     *   <li><strong>Pie:</strong> Botones de verificar y siguiente</li>
     * </ul>
     * 
     * @param pregunta La pregunta de tipo PreguntaTrueFalse
     * @param headerContainer Contenedor para la cabecera
     * @param contentContainer Contenedor para el contenido principal
     * @param footerContainer Contenedor para el pie de página
     * @param eventListener Listener para eventos de la pregunta
     * @throws IllegalArgumentException si la pregunta no es de tipo PreguntaTrueFalse
     */
    @Override
    public void configureCompleteUI(Pregunta pregunta, 
                                  VBox headerContainer, 
                                  VBox contentContainer, 
                                  VBox footerContainer,
                                  PreguntaEventListener eventListener) {
        logger.debug("[TrueFalseModule] configureCompleteUI llamado para pregunta: {}", 
                    pregunta != null ? pregunta.getId() : "null");
        
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            logger.error("[TrueFalseModule] Error: la pregunta debe ser de tipo PreguntaTrueFalse, pero es: {}", 
                        pregunta != null ? pregunta.getClass().getSimpleName() : "null");
            throw new IllegalArgumentException("La pregunta debe ser de tipo PreguntaTrueFalse");
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        logger.debug("[TrueFalseModule] Configurando UI completa para pregunta: {}", preguntaTF.getId());
        
        // Configurar cabecera (progreso adicional)
        logger.debug("[TrueFalseModule] Configurando cabecera");
        configurarCabecera(headerContainer, preguntaTF);
        
        // Configurar contenido (pregunta)
        logger.debug("[TrueFalseModule] Configurando contenido");
        configurarContenido(contentContainer, preguntaTF);
        
        // Configurar pie (botones)
        logger.debug("[TrueFalseModule] Configurando pie de página");
        configurarPie(footerContainer, preguntaTF, eventListener);
        
        logger.debug("[TrueFalseModule] UI completa configurada exitosamente");
    }
    
    /**
     * Configura la cabecera de la interfaz de pregunta verdadero/falso.
     * 
     * @param headerContainer Contenedor de la cabecera
     * @param pregunta La pregunta a configurar
     */
    private void configurarCabecera(VBox headerContainer, PreguntaTrueFalse pregunta) {
        logger.debug("[TrueFalseModule] configurarCabecera llamado para pregunta: {}", pregunta.getId());
        
        // Agregar información específica de la pregunta
        Label lblTipoPregunta = new Label("Tipo: Verdadero/Falso");
        lblTipoPregunta.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        // Insertar después del progreso existente
        if (headerContainer.getChildren().size() > 2) {
            headerContainer.getChildren().add(2, lblTipoPregunta);
            logger.debug("[TrueFalseModule] Label de tipo agregado en posición 2");
        } else {
            headerContainer.getChildren().add(lblTipoPregunta);
            logger.debug("[TrueFalseModule] Label de tipo agregado al final");
        }
        
        logger.debug("[TrueFalseModule] Cabecera configurada exitosamente");
    }
    
    /**
     * Configura el contenido principal de la interfaz de pregunta verdadero/falso.
     * 
     * @param contentContainer Contenedor del contenido
     * @param pregunta La pregunta a configurar
     */
    private void configurarContenido(VBox contentContainer, PreguntaTrueFalse pregunta) {
        logger.debug("[TrueFalseModule] configurarContenido llamado para pregunta: {}", pregunta.getId());
        
        // Limpiar contenido existente
        contentContainer.getChildren().clear();
        logger.debug("[TrueFalseModule] Contenido anterior limpiado");
        
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
        
        logger.debug("[TrueFalseModule] Contenido configurado exitosamente");
        logger.debug("[TrueFalseModule] Componentes agregados: Label de enunciado, Label de instrucciones");
    }
    
    /**
     * Configura el pie de página de la interfaz de pregunta verdadero/falso.
     * 
     * @param footerContainer Contenedor del pie de página
     * @param pregunta La pregunta a configurar
     * @param eventListener Listener para eventos
     */
    private void configurarPie(VBox footerContainer, PreguntaTrueFalse pregunta, PreguntaEventListener eventListener) {
        logger.debug("[TrueFalseModule] configurarPie llamado para pregunta: {}", pregunta.getId());
        
        // Obtener el contenedor de botones del módulo (primer hijo)
        HBox moduleButtonsContainer = (HBox) footerContainer.getChildren().get(0);
        moduleButtonsContainer.getChildren().clear();
        logger.debug("[TrueFalseModule] Contenedor de botones limpiado");
        
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
        
        logger.debug("[TrueFalseModule] Componentes del pie creados:");
        logger.debug("   - Radio buttons Verdadero/Falso");
        logger.debug("   - Botón verificar (inicialmente deshabilitado)");
        logger.debug("   - Botón siguiente (inicialmente oculto)");
        
        // Configurar eventos
        logger.debug("[TrueFalseModule] Configurando eventos");
        configurarEventos(rbVerdadero, rbFalso, btnVerificar, btnSiguiente, pregunta, eventListener);
        
        logger.debug("[TrueFalseModule] Pie de página configurado exitosamente");
    }
    
    /**
     * Configura los eventos de la interfaz de pregunta verdadero/falso.
     * 
     * @param rbVerdadero Radio button para verdadero
     * @param rbFalso Radio button para falso
     * @param btnVerificar Botón de verificar
     * @param btnSiguiente Botón de siguiente
     * @param pregunta La pregunta
     * @param eventListener Listener para eventos
     */
    private void configurarEventos(RadioButton rbVerdadero, 
                                 RadioButton rbFalso, 
                                 Button btnVerificar, 
                                 Button btnSiguiente,
                                 PreguntaTrueFalse pregunta,
                                 PreguntaEventListener eventListener) {
        
        logger.debug("[TrueFalseModule] configurarEventos llamado para pregunta: {}", pregunta.getId());
        
        // Habilitar botón verificar cuando se selecciona una opción
        rbVerdadero.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean habilitado = newVal || rbFalso.isSelected();
            btnVerificar.setDisable(!habilitado);
            logger.debug("[TrueFalseModule] Estado del botón verificar cambiado: {}", habilitado);
        });
        
        rbFalso.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean habilitado = newVal || rbVerdadero.isSelected();
            btnVerificar.setDisable(!habilitado);
            logger.debug("[TrueFalseModule] Estado del botón verificar cambiado: {}", habilitado);
        });
        
        // Evento de verificar
        btnVerificar.setOnAction(e -> {
            boolean respuestaUsuario = rbVerdadero.isSelected();
            logger.debug("[TrueFalseModule] Botón verificar presionado. Respuesta usuario: {}", respuestaUsuario);
            
            if (eventListener != null) {
                boolean esCorrecta = validarRespuesta(pregunta, respuestaUsuario);
                eventListener.onRespuestaValidada(esCorrecta);
                logger.debug("[TrueFalseModule] Evento onRespuestaValidada enviado al listener: {}", esCorrecta);
            }
        });
        
        // Evento de siguiente
        btnSiguiente.setOnAction(e -> {
            logger.debug("[TrueFalseModule] Botón siguiente presionado");
            if (eventListener != null) {
                eventListener.onSolicitarSiguientePregunta();
                logger.debug("[TrueFalseModule] Evento onSolicitarSiguientePregunta enviado al listener");
            }
        });
        
        logger.debug("[TrueFalseModule] Eventos configurados exitosamente");
    }
    
    /**
     * Valida la respuesta del usuario para una pregunta verdadero/falso.
     * 
     * <p>La validación se realiza comparando directamente los valores booleanos
     * de la respuesta del usuario y la respuesta correcta.</p>
     * 
     * @param pregunta La pregunta de tipo PreguntaTrueFalse
     * @param respuesta La respuesta del usuario
     * @return true si la respuesta es correcta, false en caso contrario
     */
    @Override
    public boolean validarRespuesta(Pregunta pregunta, Object respuesta) {
        logger.debug("[TrueFalseModule] validarRespuesta llamado para pregunta: {}", 
                    pregunta != null ? pregunta.getId() : "null");
        logger.debug("[TrueFalseModule] Respuesta recibida: {}", respuesta);
        
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            logger.error("[TrueFalseModule] Error: la pregunta debe ser de tipo PreguntaTrueFalse");
            return false;
        }
        
        if (!(respuesta instanceof Boolean)) {
            logger.error("[TrueFalseModule] Error: la respuesta debe ser de tipo Boolean");
            return false;
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        boolean respuestaUsuario = (Boolean) respuesta;
        boolean respuestaCorrecta = preguntaTF.getRespuestaCorrecta();
        
        boolean esCorrecta = respuestaUsuario == respuestaCorrecta;
        
        logger.debug("[TrueFalseModule] Validación completada:");
        logger.debug("   - Respuesta usuario: {}", respuestaUsuario);
        logger.debug("   - Respuesta correcta: {}", respuestaCorrecta);
        logger.debug("   - Es correcta: {}", esCorrecta);
        
        return esCorrecta;
    }
    
    /**
     * Muestra el resultado de la validación en la interfaz.
     * 
     * @param pregunta La pregunta de tipo PreguntaTrueFalse
     * @param esCorrecta Si la respuesta es correcta
     * @param respuestaUsuario La respuesta del usuario
     * @param contentContainer Contenedor donde mostrar el resultado
     */
    @Override
    public void mostrarResultado(Pregunta pregunta, 
                                boolean esCorrecta, 
                                Object respuestaUsuario,
                                VBox contentContainer) {
        logger.debug("[TrueFalseModule] mostrarResultado llamado para pregunta: {}", 
                    pregunta != null ? pregunta.getId() : "null");
        logger.debug("[TrueFalseModule] Resultado: correcta={}, respuesta={}", esCorrecta, respuestaUsuario);
        
        if (!(pregunta instanceof PreguntaTrueFalse)) {
            logger.error("[TrueFalseModule] Error: la pregunta debe ser de tipo PreguntaTrueFalse");
            return;
        }
        
        PreguntaTrueFalse preguntaTF = (PreguntaTrueFalse) pregunta;
        
        // Crear label de resultado
        Label lblResultado = new Label();
        lblResultado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");
        
        if (esCorrecta) {
            lblResultado.setText("¡Correcto! 🎉");
            lblResultado.setStyle(lblResultado.getStyle() + " -fx-text-fill: #28a745;");
            logger.debug("[TrueFalseModule] Mostrando resultado: Correcto");
        } else {
            String respuestaCorrecta = preguntaTF.getRespuestaCorrecta() ? "Verdadero" : "Falso";
            lblResultado.setText("Incorrecto. La respuesta correcta era: " + respuestaCorrecta);
            lblResultado.setStyle(lblResultado.getStyle() + " -fx-text-fill: #dc3545;");
            logger.debug("[TrueFalseModule] Mostrando resultado: Incorrecto");
        }
        
        // Agregar al contenedor
        contentContainer.getChildren().add(lblResultado);
        
        logger.debug("[TrueFalseModule] Resultado mostrado exitosamente en la interfaz");
    }
} 