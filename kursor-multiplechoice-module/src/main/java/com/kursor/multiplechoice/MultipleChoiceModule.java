package com.kursor.multiplechoice;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.multiplechoice.domain.PreguntaTest;
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
 * Módulo para preguntas de tipo opción múltiple (test).
 * 
 * <p>Este módulo maneja preguntas que requieren seleccionar una respuesta
 * de entre varias opciones disponibles. Es uno de los tipos de pregunta
 * más comunes en sistemas de aprendizaje.</p>
 * 
 * <p>Características del módulo:</p>
 * <ul>
 *   <li><strong>Tipo de pregunta:</strong> "test"</li>
 *   <li><strong>Estructura:</strong> Enunciado + múltiples opciones + respuesta correcta</li>
 *   <li><strong>Interfaz:</strong> Radio buttons para selección única</li>
 *   <li><strong>Validación:</strong> Verifica que la respuesta seleccionada sea correcta</li>
 *   <li><strong>Logging:</strong> Registra todo el proceso de creación y validación</li>
 * </ul>
 * 
 * <p>Estructura YAML esperada:</p>
 * <pre>{@code
 * id: "p1"
 * tipo: "test"
 * enunciado: "¿Cuál es la capital de España?"
 * opciones:
 *   - "Madrid"
 *   - "Barcelona"
 *   - "Valencia"
 *   - "Sevilla"
 * respuesta: "Madrid"
 * }</pre>
 * 
 * <p>Componentes de la interfaz:</p>
 * <ul>
 *   <li><strong>Enunciado:</strong> Label con el texto de la pregunta</li>
 *   <li><strong>Opciones:</strong> Radio buttons para cada opción</li>
 *   <li><strong>Agrupación:</strong> ToggleGroup para selección única</li>
 *   <li><strong>Botones:</strong> Verificar y Siguiente</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see PreguntaTest
 * @see PreguntaEventListener
 */
public class MultipleChoiceModule implements PreguntaModule {
    
    /** Logger para registrar eventos del módulo */
    private static final Logger logger = LoggerFactory.getLogger(MultipleChoiceModule.class);
    
    @Override
    public String getModuleName() {
        return "Opción Múltiple";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para preguntas de opción múltiple";
    }
    
    @Override
    public String getIcon() {
        return "📝";
    }
    
    @Override
    public String getColor() {
        return "#007bff"; // Azul para opción múltiple
    }
    
    @Override
    public String getQuestionType() {
        return "test";
    }

    /**
     * Parsea datos YAML para crear una pregunta de opción múltiple.
     * 
     * <p>Este método interpreta los datos YAML y crea una instancia de
     * {@link PreguntaTest} con todos sus atributos configurados. Realiza
     * validaciones exhaustivas de los datos de entrada.</p>
     * 
     * <p>Proceso de parsing:</p>
     * <ol>
     *   <li>Validar que los datos YAML no sean null</li>
     *   <li>Extraer y validar campos obligatorios (id, enunciado, respuesta)</li>
     *   <li>Extraer y validar lista de opciones</li>
     *   <li>Crear instancia de PreguntaTest</li>
     *   <li>Registrar resultado en el log</li>
     * </ol>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Datos YAML no null</li>
     *   <li>ID presente y no vacío</li>
     *   <li>Enunciado presente y no vacío</li>
     *   <li>Respuesta correcta presente y no vacía</li>
     *   <li>Lista de opciones no null y no vacía</li>
     * </ul>
     * 
     * <p>Estructura YAML esperada:</p>
     * <ul>
     *   <li><strong>id:</strong> Identificador único de la pregunta</li>
     *   <li><strong>enunciado:</strong> Texto de la pregunta</li>
     *   <li><strong>opciones:</strong> Lista de opciones de respuesta</li>
     *   <li><strong>respuesta:</strong> La opción correcta</li>
     * </ul>
     * 
     * @param preguntaData Mapa con los datos YAML de la pregunta
     * @return PreguntaTest creada a partir de los datos YAML, nunca null
     * @throws IllegalArgumentException si los datos YAML no son válidos
     * 
     * @see PreguntaTest
     */
    @Override
    public Pregunta parsePregunta(Map<String, Object> preguntaData) {
        logger.debug("🚀 Iniciando parsing de pregunta de opción múltiple");
        logger.debug("📋 Datos YAML recibidos: {}", preguntaData);
        
        // Validar datos requeridos
        if (preguntaData == null) {
            logger.error("❌ Error al parsear pregunta: datos YAML no pueden ser null");
            throw new IllegalArgumentException("Datos YAML no pueden ser null");
        }
        
        logger.debug("✅ Datos YAML validados correctamente");
        
        // Extraer campos obligatorios
        String id = (String) preguntaData.get("id");
        String enunciado = (String) preguntaData.get("enunciado");
        String respuestaCorrecta = (String) preguntaData.get("respuesta");
        
        logger.debug("🔍 Campos extraídos:");
        logger.debug("   - ID: '{}'", id);
        logger.debug("   - Enunciado: '{}'", enunciado);
        logger.debug("   - Respuesta correcta: '{}'", respuestaCorrecta);
        
        // Validar campos obligatorios
        if (id == null || id.trim().isEmpty()) {
            logger.error("❌ Error al parsear pregunta: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID de pregunta no puede ser null o vacío");
        }
        
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("❌ Error al parsear pregunta: enunciado no puede ser null o vacío - ID: {}", id);
            throw new IllegalArgumentException("Enunciado de pregunta no puede ser null o vacío");
        }
        
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            logger.error("❌ Error al parsear pregunta: respuesta correcta (respuesta) no puede ser null o vacía - ID: {}", id);
            throw new IllegalArgumentException("Respuesta correcta (respuesta) no puede ser null o vacía");
        }
        
        logger.debug("✅ Campos obligatorios validados correctamente");
        
        // Obtener las opciones
        @SuppressWarnings("unchecked")
        List<String> opciones = (List<String>) preguntaData.get("opciones");
        
        logger.debug("🔍 Opciones extraídas: {}", opciones);
        
        // Validar que hay opciones
        if (opciones == null || opciones.isEmpty()) {
            logger.error("❌ Error al parsear pregunta: debe tener al menos una opción - ID: {}", id);
            throw new IllegalArgumentException("La pregunta debe tener al menos una opción");
        }
        
        logger.debug("✅ Opciones validadas correctamente - Cantidad: {}", opciones.size());
        
        // Crear la pregunta de opción múltiple
        logger.debug("🔄 Creando instancia de PreguntaTest");
        PreguntaTest pregunta = new PreguntaTest(id, enunciado, opciones, respuestaCorrecta);
        
        logger.debug("✅ PreguntaTest creada exitosamente");
        logger.debug("📊 Información de la pregunta creada:");
        logger.debug("   - ID: {}", pregunta.getId());
        logger.debug("   - Tipo: {}", pregunta.getTipo());
        logger.debug("   - Enunciado: {}", pregunta.getEnunciado());
        logger.debug("   - Opciones: {}", pregunta.getOpciones().size());
        logger.debug("   - Respuesta correcta: {}", pregunta.getRespuestaCorrecta());
        
        logger.info("🎉 Pregunta de opción múltiple ('test') parseada correctamente: {}", pregunta.toString());
        
        return pregunta;
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
     * <p>Estilos aplicados:</p>
     * <ul>
     *   <li><strong>Enunciado:</strong> Fuente 16px, negrita, color #2c3e50</li>
     *   <li><strong>Opciones:</strong> Fuente 14px, alineación izquierda</li>
     *   <li><strong>Contenedor:</strong> Espaciado 20px, centrado</li>
     * </ul>
     * 
     * @param pregunta La pregunta para la cual crear la vista
     * @return Nodo JavaFX que representa la interfaz de la pregunta
     * @throws IllegalArgumentException si el tipo de pregunta no es compatible
     * 
     * @see PreguntaTest
     * @see javafx.scene.control.RadioButton
     * @see javafx.scene.control.ToggleGroup
     */
    @Override
    public Node createQuestionView(Pregunta pregunta) {
        logger.debug("🚀 Iniciando creación de vista para pregunta de opción múltiple");
        logger.debug("📋 Pregunta recibida: {}", pregunta != null ? pregunta.getId() : "null");
        
        if (!(pregunta instanceof PreguntaTest)) {
            String errorMsg = "Tipo de pregunta incorrecto para MultipleChoiceModule. Se esperaba PreguntaTest, se recibió: " + 
                            (pregunta != null ? pregunta.getClass().getSimpleName() : "null");
            logger.error("❌ {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        logger.debug("✅ Tipo de pregunta validado correctamente");
        logger.debug("📊 Información de la pregunta:");
        logger.debug("   - ID: {}", preguntaTest.getId());
        logger.debug("   - Enunciado: {}", preguntaTest.getEnunciado());
        logger.debug("   - Opciones: {}", preguntaTest.getOpciones().size());
        
        // Crear contenedor principal
        logger.debug("🔄 Creando contenedor principal (VBox)");
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        // Crear enunciado
        logger.debug("🔄 Creando label de enunciado");
        Label lblEnunciado = new Label(preguntaTest.getEnunciado());
        lblEnunciado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblEnunciado.setWrapText(true);
        lblEnunciado.setMaxWidth(600);
        
        logger.debug("✅ Enunciado configurado correctamente");
        
        // Crear opciones
        logger.debug("🔄 Creando opciones de respuesta");
        ToggleGroup toggleGroup = new ToggleGroup();
        VBox opcionesContainer = new VBox(10);
        opcionesContainer.setAlignment(Pos.CENTER_LEFT);
        
        for (int i = 0; i < preguntaTest.getOpciones().size(); i++) {
            String opcion = preguntaTest.getOpciones().get(i);
            RadioButton rbOpcion = new RadioButton(opcion);
            rbOpcion.setToggleGroup(toggleGroup);
            rbOpcion.setStyle("-fx-font-size: 14px;");
            opcionesContainer.getChildren().add(rbOpcion);
            
            logger.debug("   - Opción {}: '{}'", i + 1, opcion);
        }
        
        logger.debug("✅ Opciones creadas correctamente - Cantidad: {}", preguntaTest.getOpciones().size());
        
        // Ensamblar interfaz
        logger.debug("🔄 Ensamblando componentes de la interfaz");
        container.getChildren().addAll(lblEnunciado, opcionesContainer);
        
        logger.info("🎉 Vista de pregunta de opción múltiple creada exitosamente - ID: {}, Opciones: {}", 
                   preguntaTest.getId(), preguntaTest.getOpciones().size());
        
        return container;
    }
    
    /**
     * Configura la interfaz completa para una pregunta de opción múltiple.
     * 
     * <p>Este método configura la interfaz completa para una pregunta de opción múltiple,
     * incluyendo la cabecera, el contenido y el pie de la interfaz.</p>
     * 
     * @param pregunta La pregunta para la cual configurar la interfaz
     * @param headerContainer Contenedor de la cabecera
     * @param contentContainer Contenedor del contenido
     * @param footerContainer Contenedor del pie
     * @param eventListener Listener para eventos de la pregunta
     * @throws IllegalArgumentException si el tipo de pregunta no es compatible
     */
    @Override
    public void configureCompleteUI(Pregunta pregunta, 
                                  VBox headerContainer, 
                                  VBox contentContainer, 
                                  VBox footerContainer,
                                  PreguntaEventListener eventListener) {
        if (!(pregunta instanceof PreguntaTest)) {
            throw new IllegalArgumentException("La pregunta debe ser de tipo PreguntaTest");
        }
        
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        
        // Configurar cabecera (progreso adicional)
        configurarCabecera(headerContainer, preguntaTest);
        
        // Configurar contenido (pregunta)
        configurarContenido(contentContainer, preguntaTest);
        
        // Configurar pie (botones)
        configurarPie(footerContainer, preguntaTest, eventListener);
    }
    
    private void configurarCabecera(VBox headerContainer, PreguntaTest pregunta) {
        // Agregar información específica de la pregunta
        Label lblTipoPregunta = new Label("Tipo: Opción Múltiple");
        lblTipoPregunta.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        // Insertar después del progreso existente
        if (headerContainer.getChildren().size() > 2) {
            headerContainer.getChildren().add(2, lblTipoPregunta);
        } else {
            headerContainer.getChildren().add(lblTipoPregunta);
        }
    }
    
    private void configurarContenido(VBox contentContainer, PreguntaTest pregunta) {
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
        Label lblInstrucciones = new Label("Selecciona la respuesta correcta:");
        lblInstrucciones.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d;");
        
        preguntaContainer.getChildren().addAll(lblEnunciado, lblInstrucciones);
        
        contentContainer.getChildren().add(preguntaContainer);
    }
    
    private void configurarPie(VBox footerContainer, PreguntaTest pregunta, PreguntaEventListener eventListener) {
        // Obtener el contenedor de botones del módulo (primer hijo)
        HBox moduleButtonsContainer = (HBox) footerContainer.getChildren().get(0);
        moduleButtonsContainer.getChildren().clear();
        
        // Crear controles de la pregunta
        ToggleGroup toggleGroup = new ToggleGroup();
        VBox opcionesContainer = new VBox(10);
        opcionesContainer.setAlignment(Pos.CENTER);
        
        // Crear radio buttons para cada opción
        for (String opcion : pregunta.getOpciones()) {
            RadioButton rbOpcion = new RadioButton(opcion);
            rbOpcion.setToggleGroup(toggleGroup);
            rbOpcion.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
            opcionesContainer.getChildren().add(rbOpcion);
        }
        
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
        moduleButtonsContainer.getChildren().addAll(opcionesContainer, botonesContainer);
        
        // Configurar eventos
        configurarEventos(toggleGroup, btnVerificar, btnSiguiente, pregunta, eventListener, footerContainer);
    }
    
    private void configurarEventos(ToggleGroup toggleGroup,
                                 Button btnVerificar,
                                 Button btnSiguiente,
                                 PreguntaTest pregunta,
                                 PreguntaEventListener eventListener,
                                 VBox footerContainer) {
        
        // Habilitar botón verificar cuando se selecciona una opción
        toggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            btnVerificar.setDisable(newVal == null);
        });
        
        // Evento de verificar
        btnVerificar.setOnAction(e -> {
            RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
            if (selected == null) return;
            
            String respuestaUsuario = selected.getText();
            boolean esCorrecta = validarRespuesta(pregunta, respuestaUsuario);
            
            // Mostrar resultado
            mostrarResultado(pregunta, esCorrecta, respuestaUsuario, null);
            
            // Notificar al contenedor principal
            eventListener.onRespuestaValidada(esCorrecta);
            
            // Cambiar estado de botones
            btnVerificar.setVisible(false);
            btnSiguiente.setVisible(true);
            
            // Deshabilitar todas las opciones
            for (Node node : ((VBox) ((HBox) footerContainer.getChildren().get(0)).getChildren().get(0)).getChildren()) {
                if (node instanceof RadioButton) {
                    node.setDisable(true);
                }
            }
        });
        
        // Evento de siguiente
        btnSiguiente.setOnAction(e -> {
            // El contenedor principal manejará la transición
            // Este botón es solo para UX
        });
    }
    
    @Override
    public boolean validarRespuesta(Pregunta pregunta, Object respuesta) {
        if (!(pregunta instanceof PreguntaTest)) {
            return false;
        }
        
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        String respuestaStr = respuesta.toString();
        
        return preguntaTest.esCorrecta(respuestaStr);
    }
    
    @Override
    public void mostrarResultado(Pregunta pregunta, 
                                boolean esCorrecta, 
                                Object respuestaUsuario,
                                VBox contentContainer) {
        if (!(pregunta instanceof PreguntaTest)) {
            return;
        }
        
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        
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
        Label lblRespuestaCorrecta = new Label("Respuesta correcta: " + preguntaTest.getRespuestaCorrecta());
        lblRespuestaCorrecta.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        
        resultadoContainer.getChildren().addAll(lblIcono, lblMensaje, lblRespuestaCorrecta);
        
        // Si hay contenedor específico, agregar ahí, sino usar el contenido principal
        if (contentContainer != null) {
            contentContainer.getChildren().add(resultadoContainer);
        }
    }
} 