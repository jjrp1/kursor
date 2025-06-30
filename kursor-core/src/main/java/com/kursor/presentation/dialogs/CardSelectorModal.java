package com.kursor.presentation.dialogs;

import com.kursor.presentation.controllers.CardSelectorController;
import com.kursor.presentation.viewmodels.CardSelectorViewModel;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Modal genérico para selección de elementos usando tarjetas visuales.
 * 
 * <p>Este modal es completamente genérico y puede trabajar con cualquier tipo
 * de elementos que implementen la interfaz SelectableItem. Proporciona una
 * interfaz visual atractiva con tarjetas animadas, efectos visuales y
 * una experiencia de usuario consistente.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Genérico:</strong> Funciona con cualquier tipo de SelectableItem</li>
 *   <li><strong>Visual:</strong> Tarjetas con iconos, colores y animaciones</li>
 *   <li><strong>Responsivo:</strong> Se adapta al número de elementos</li>
 *   <li><strong>Accesible:</strong> Tooltips y navegación por teclado</li>
 *   <li><strong>MVC:</strong> Arquitectura Model-View-Controller</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SelectableItem
 * @see CardSelectorController
 * @see CardSelectorViewModel
 */
public class CardSelectorModal {
    
    private static final Logger logger = LoggerFactory.getLogger(CardSelectorModal.class);
    
    // Componentes de la UI
    private Stage stage;
    private VBox rootContainer;
    private Label tituloLabel;
    private Label descripcionLabel;
    private ScrollPane scrollPane;
    private FlowPane tarjetasContainer;
    private Button btnSeleccionar;
    private Button btnCancelar;
    private Label mensajeErrorLabel;
    private ProgressIndicator progressIndicator;
    
    // Controlador y ViewModel
    private CardSelectorController controller;
    private CardSelectorViewModel viewModel;
    
    // Estado
    private List<VBox> tarjetasItems;
    private SelectableItem resultado;
    private CompletableFuture<SelectableItem> future;
    
    /**
     * Constructor que crea un modal con configuración inicial.
     * 
     * @param titulo El título del modal
     * @param descripcion La descripción del modal
     * @param items La lista de elementos seleccionables
     */
    public CardSelectorModal(String titulo, String descripcion, List<SelectableItem> items) {
        logger.info("🚀 INICIO - Creando CardSelectorModal para: {} con {} elementos", titulo, items.size());
        logger.debug("🔍 DEBUG - Parámetros recibidos:");
        logger.debug("   - Título: '{}'", titulo);
        logger.debug("   - Descripción: '{}'", descripcion);
        logger.debug("   - Items: {} elementos", items != null ? items.size() : "NULL");
        
        try {
            logger.debug("🔧 DEBUG - Creando controlador...");
            // Crear controlador y ViewModel
            this.controller = new CardSelectorController(titulo, descripcion, items);
            logger.debug("✅ DEBUG - Controlador creado exitosamente");
            
            logger.debug("🔧 DEBUG - Obteniendo ViewModel...");
            this.viewModel = controller.getViewModel();
            logger.debug("✅ DEBUG - ViewModel obtenido exitosamente");
            
            logger.debug("🔧 DEBUG - Inicializando componentes...");
            // Inicializar componentes
            inicializarComponentes();
            logger.debug("✅ DEBUG - Componentes inicializados exitosamente");
            
            logger.debug("🔧 DEBUG - Configurando eventos...");
            configurarEventos();
            logger.debug("✅ DEBUG - Eventos configurados exitosamente");
            
            logger.debug("🔧 DEBUG - Configurando bindings...");
            configurarBindings();
            logger.debug("✅ DEBUG - Bindings configurados exitosamente");
            
            logger.info("✅ FIN - CardSelectorModal creado correctamente");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error durante la creación del CardSelectorModal", e);
            throw e;
        }
    }
    
    /**
     * Constructor que crea un modal con controlador existente.
     * 
     * @param controller El controlador a usar
     */
    public CardSelectorModal(CardSelectorController controller) {
        logger.info("Creando CardSelectorModal con controlador existente para: {}", controller.getTitulo());
        
        this.controller = controller;
        this.viewModel = controller.getViewModel();
        
        // Inicializar componentes
        inicializarComponentes();
        configurarEventos();
        configurarBindings();
        
        logger.info("CardSelectorModal creado correctamente");
    }
    
    /**
     * Inicializa todos los componentes de la UI.
     */
    private void inicializarComponentes() {
        logger.debug("🚀 INICIO - inicializarComponentes()");
        
        try {
            logger.debug("🔧 DEBUG - Creando contenedor principal...");
            // Contenedor principal
            rootContainer = new VBox(20);
            rootContainer.setAlignment(Pos.CENTER);
            rootContainer.setPadding(new Insets(30));
            rootContainer.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
            rootContainer.setMinWidth(800);
            rootContainer.setMinHeight(600);
            rootContainer.setPrefWidth(1000);
            rootContainer.setPrefHeight(700);
            logger.debug("✅ DEBUG - Contenedor principal creado");
            
            logger.debug("🔧 DEBUG - Creando título...");
            // Título
            tituloLabel = new Label(viewModel.getTitulo());
            tituloLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
            tituloLabel.setTextFill(Color.WHITE);
            tituloLabel.setTextAlignment(TextAlignment.CENTER);
            logger.debug("✅ DEBUG - Título creado: '{}'", viewModel.getTitulo());
            
            logger.debug("🔧 DEBUG - Creando descripción...");
            // Descripción
            descripcionLabel = new Label(viewModel.getDescripcion());
            descripcionLabel.setFont(Font.font("Segoe UI", 16));
            descripcionLabel.setTextFill(Color.WHITE);
            descripcionLabel.setTextAlignment(TextAlignment.CENTER);
            descripcionLabel.setWrapText(true);
            descripcionLabel.setMaxWidth(600);
            logger.debug("✅ DEBUG - Descripción creada: '{}'", viewModel.getDescripcion());
            
            logger.debug("🔧 DEBUG - Creando contenedor de tarjetas...");
            // Contenedor de tarjetas
            tarjetasContainer = new FlowPane(15, 15);
            tarjetasContainer.setAlignment(Pos.CENTER);
            tarjetasContainer.setPadding(new Insets(20));
            logger.debug("✅ DEBUG - Contenedor de tarjetas creado");
            
            logger.debug("🔧 DEBUG - Creando ScrollPane...");
            // ScrollPane para las tarjetas
            scrollPane = new ScrollPane(tarjetasContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefViewportHeight(400);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            logger.debug("✅ DEBUG - ScrollPane creado");
            
            logger.debug("🔧 DEBUG - Creando botones...");
            // Botones
            HBox botones = crearBotones();
            logger.debug("✅ DEBUG - Botones creados");
            
            logger.debug("🔧 DEBUG - Creando mensaje de error...");
            // Mensaje de error
            mensajeErrorLabel = new Label();
            mensajeErrorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            mensajeErrorLabel.setTextFill(Color.RED);
            mensajeErrorLabel.setVisible(false);
            mensajeErrorLabel.setManaged(false);
            logger.debug("✅ DEBUG - Mensaje de error creado");
            
            logger.debug("🔧 DEBUG - Creando indicador de progreso...");
            // Indicador de progreso
            progressIndicator = new ProgressIndicator();
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
            logger.debug("✅ DEBUG - Indicador de progreso creado");
            
            logger.debug("🔧 DEBUG - Agregando componentes al contenedor principal...");
            // Agregar componentes al contenedor principal
            rootContainer.getChildren().addAll(
                tituloLabel,
                descripcionLabel,
                scrollPane,
                mensajeErrorLabel,
                progressIndicator,
                botones
            );
            logger.debug("✅ DEBUG - Componentes agregados al contenedor principal");
            
            logger.debug("🔧 DEBUG - Creando tarjetas...");
            // Crear tarjetas
            crearTarjetas();
            logger.debug("✅ DEBUG - Tarjetas creadas");
            
            logger.info("✅ FIN - inicializarComponentes() completado exitosamente");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en inicializarComponentes()", e);
            throw e;
        }
    }
    
    /**
     * Crea los botones de acción del modal.
     * 
     * @return HBox con los botones configurados
     */
    private HBox crearBotones() {
        logger.debug("🚀 INICIO - crearBotones()");
        
        try {
            logger.debug("🔧 DEBUG - Creando contenedor de botones...");
            HBox botones = new HBox(20);
            botones.setAlignment(Pos.CENTER);
            botones.setPadding(new Insets(30, 0, 20, 0));
            logger.debug("✅ DEBUG - Contenedor de botones creado");
            
            logger.debug("🔧 DEBUG - Creando botón Cancelar...");
            // Botón Cancelar
            btnCancelar = new Button("❌ Cancelar");
            btnCancelar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            btnCancelar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
            btnCancelar.setPrefWidth(150);
            btnCancelar.setPrefHeight(45);
            logger.debug("✅ DEBUG - Botón Cancelar creado");
            
            logger.debug("🔧 DEBUG - Creando botón Seleccionar...");
            // Botón Seleccionar
            btnSeleccionar = new Button("✅ Seleccionar");
            btnSeleccionar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            btnSeleccionar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
            btnSeleccionar.setPrefWidth(150);
            btnSeleccionar.setPrefHeight(45);
            logger.debug("✅ DEBUG - Botón Seleccionar creado");
            
            logger.debug("🔧 DEBUG - Agregando botones al contenedor...");
            botones.getChildren().addAll(btnCancelar, btnSeleccionar);
            logger.debug("✅ DEBUG - Botones agregados al contenedor");
            
            logger.info("✅ FIN - crearBotones() completado exitosamente");
            return botones;
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en crearBotones()", e);
            throw e;
        }
    }
    
    /**
     * Configura los eventos de los botones y teclas.
     */
    private void configurarEventos() {
        logger.debug("🚀 INICIO - configurarEventos()");
        
        try {
            logger.debug("🔧 DEBUG - Configurando evento del botón Cancelar...");
            // Evento del botón Cancelar
            btnCancelar.setOnAction(e -> {
                logger.debug("🔍 DEBUG - Usuario canceló la selección");
                controller.cancelarSeleccion();
                cerrarModal(null);
            });
            logger.debug("✅ DEBUG - Evento del botón Cancelar configurado");
            
            logger.debug("🔧 DEBUG - Configurando evento del botón Seleccionar...");
            // Evento del botón Seleccionar
            btnSeleccionar.setOnAction(e -> {
                logger.debug("🔍 DEBUG - Usuario confirmó la selección");
                SelectableItem seleccion = controller.confirmarSeleccion();
                cerrarModal(seleccion);
            });
            logger.debug("✅ DEBUG - Evento del botón Seleccionar configurado");
            
            logger.debug("🔧 DEBUG - Configurando evento de tecla ESC...");
            // Evento de tecla ESC para cancelar
            rootContainer.setOnKeyPressed(e -> {
                if (e.getCode().toString().equals("ESCAPE")) {
                    logger.debug("🔍 DEBUG - Usuario presionó ESC para cancelar");
                    controller.cancelarSeleccion();
                    cerrarModal(null);
                }
            });
            logger.debug("✅ DEBUG - Evento de tecla ESC configurado");
            
            logger.info("✅ FIN - configurarEventos() completado exitosamente");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en configurarEventos()", e);
            throw e;
        }
    }
    
    /**
     * Configura los bindings entre el ViewModel y la UI.
     */
    private void configurarBindings() {
        logger.debug("🚀 INICIO - configurarBindings()");
        
        try {
            logger.debug("🔧 DEBUG - Configurando binding del botón Seleccionar...");
            // Binding del botón Seleccionar
            btnSeleccionar.disableProperty().bind(viewModel.itemValidoProperty().not());
            logger.debug("✅ DEBUG - Binding del botón Seleccionar configurado");
            
            logger.debug("🔧 DEBUG - Configurando binding del mensaje de error...");
            // Binding del mensaje de error
            mensajeErrorLabel.textProperty().bind(viewModel.mensajeErrorProperty());
            mensajeErrorLabel.visibleProperty().bind(viewModel.mensajeErrorProperty().isNotEmpty());
            mensajeErrorLabel.managedProperty().bind(viewModel.mensajeErrorProperty().isNotEmpty());
            logger.debug("✅ DEBUG - Binding del mensaje de error configurado");
            
            logger.debug("🔧 DEBUG - Configurando binding del indicador de progreso...");
            // Binding del indicador de progreso
            progressIndicator.visibleProperty().bind(viewModel.cargandoProperty());
            progressIndicator.managedProperty().bind(viewModel.cargandoProperty());
            logger.debug("✅ DEBUG - Binding del indicador de progreso configurado");
            
            logger.info("✅ FIN - configurarBindings() completado exitosamente");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en configurarBindings()", e);
            throw e;
        }
    }
    
    /**
     * Crea las tarjetas para cada elemento seleccionable.
     */
    private void crearTarjetas() {
        logger.debug("🚀 INICIO - crearTarjetas()");
        logger.debug("🔍 DEBUG - Número de elementos a procesar: {}", viewModel.getItems().size());
        
        try {
            // Verificar si hay elementos
            if (viewModel.getItems().isEmpty()) {
                logger.warn("⚠️ WARN - No hay elementos para crear tarjetas");
                // Crear un mensaje informativo
                Label mensajeLabel = new Label("No hay elementos disponibles para seleccionar");
                mensajeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-text-alignment: center;");
                mensajeLabel.setAlignment(Pos.CENTER);
                tarjetasContainer.getChildren().add(mensajeLabel);
                return;
            }
            
            logger.debug("🔧 DEBUG - Creando lista de tarjetas...");
            tarjetasItems = viewModel.getItems().stream()
                .map(this::crearTarjeta)
                .toList();
            logger.debug("✅ DEBUG - Lista de tarjetas creada: {} tarjetas", tarjetasItems.size());
            
            logger.debug("🔧 DEBUG - Agregando tarjetas al contenedor...");
            tarjetasContainer.getChildren().addAll(tarjetasItems);
            logger.debug("✅ DEBUG - Tarjetas agregadas al contenedor");
            
            logger.info("✅ FIN - crearTarjetas() completado: {} tarjetas creadas correctamente", tarjetasItems.size());
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en crearTarjetas()", e);
            // Crear un mensaje de error
            Label errorLabel = new Label("Error al cargar elementos: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff6b6b; -fx-text-alignment: center;");
            errorLabel.setAlignment(Pos.CENTER);
            tarjetasContainer.getChildren().add(errorLabel);
        }
    }
    
    /**
     * Crea una tarjeta individual para un elemento.
     * 
     * @param item El elemento para el cual crear la tarjeta
     * @return La tarjeta creada
     */
    private VBox crearTarjeta(SelectableItem item) {
        logger.debug("🚀 INICIO - crearTarjeta() para: '{}'", item.getTitle());
        
        try {
            logger.debug("🔧 DEBUG - Creando contenedor de tarjeta...");
            VBox tarjeta = new VBox(10);
            tarjeta.setAlignment(Pos.CENTER);
            tarjeta.setPadding(new Insets(20));
            tarjeta.setPrefWidth(280);
            tarjeta.setPrefHeight(200);
            tarjeta.setCursor(Cursor.HAND);
            logger.debug("✅ DEBUG - Contenedor de tarjeta creado");
            
            logger.debug("🔧 DEBUG - Configurando estilo de tarjeta...");
            String estiloBase = String.format(
                "-fx-background-color: white; -fx-border-color: %s; -fx-border-width: 2; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 3); " +
                "-fx-cursor: hand;",
                item.getColor()
            );
            tarjeta.setStyle(estiloBase);
            logger.debug("✅ DEBUG - Estilo de tarjeta configurado con color: {}", item.getColor());
            
            logger.debug("🔧 DEBUG - Creando icono...");
            // Icono usando TextFlow para emojis coloridos
            TextFlow iconoFlow = new TextFlow();
            Text iconoText = new Text(item.getIcon());
            iconoText.setFont(Font.font("Segoe UI Emoji", 48)); // Usar fuente emoji específica
            iconoFlow.getChildren().add(iconoText);
            iconoFlow.setTextAlignment(TextAlignment.CENTER);
            logger.debug("✅ DEBUG - Icono creado: '{}'", item.getIcon());
            
            logger.debug("🔧 DEBUG - Creando título...");
            // Título
            Label tituloLabel = new Label(item.getTitle());
            tituloLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            tituloLabel.setTextAlignment(TextAlignment.CENTER);
            tituloLabel.setWrapText(true);
            tituloLabel.setMaxWidth(240);
            logger.debug("✅ DEBUG - Título creado: '{}'", item.getTitle());
            
            logger.debug("🔧 DEBUG - Creando descripción...");
            // Descripción
            Label descripcionLabel = new Label(item.getDescription());
            descripcionLabel.setFont(Font.font("Segoe UI", 12));
            descripcionLabel.setTextAlignment(TextAlignment.CENTER);
            descripcionLabel.setWrapText(true);
            descripcionLabel.setMaxWidth(240);
            descripcionLabel.setMaxHeight(80);
            logger.debug("✅ DEBUG - Descripción creada: '{}'", item.getDescription());
            
            logger.debug("🔧 DEBUG - Agregando componentes a la tarjeta...");
            // Agregar componentes a la tarjeta
            tarjeta.getChildren().addAll(iconoFlow, tituloLabel, descripcionLabel);
            logger.debug("✅ DEBUG - Componentes agregados a la tarjeta");
            
            logger.debug("🔧 DEBUG - Instalando tooltip...");
            // Tooltip
            Tooltip tooltip = new Tooltip("Haz clic para seleccionar: " + item.getTitle());
            Tooltip.install(tarjeta, tooltip);
            logger.debug("✅ DEBUG - Tooltip instalado");
            
            logger.debug("🔧 DEBUG - Configurando eventos de la tarjeta...");
            // Eventos de la tarjeta
            configurarEventosTarjeta(tarjeta, item);
            logger.debug("✅ DEBUG - Eventos de la tarjeta configurados");
            
            logger.info("✅ FIN - crearTarjeta() completado para: '{}'", item.getTitle());
            return tarjeta;
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en crearTarjeta() para: '{}'", item.getTitle(), e);
            throw e;
        }
    }
    
    /**
     * Configura los eventos para una tarjeta.
     * 
     * @param tarjeta La tarjeta a configurar
     * @param item El elemento asociado
     */
    private void configurarEventosTarjeta(VBox tarjeta, SelectableItem item) {
        logger.debug("🚀 INICIO - configurarEventosTarjeta() para: '{}'", item.getTitle());
        
        try {
            logger.debug("🔧 DEBUG - Configurando evento de clic...");
            tarjeta.setOnMouseClicked(e -> {
                logger.debug("🔍 DEBUG - Usuario seleccionó elemento: {}", item.getTitle());
                
                // Deseleccionar todas las tarjetas
                deseleccionarTodasLasTarjetas();
                
                // Seleccionar esta tarjeta
                seleccionarTarjeta(tarjeta, item);
                
                // Notificar al controlador
                controller.seleccionarItem(item);
            });
            logger.debug("✅ DEBUG - Evento de clic configurado");
            
            logger.debug("🔧 DEBUG - Configurando efectos hover...");
            // Efectos hover
            tarjeta.setOnMouseEntered(e -> {
                logger.debug("🔍 DEBUG - Mouse entró en tarjeta: {}", item.getTitle());
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tarjeta);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.play();
            });
            
            tarjeta.setOnMouseExited(e -> {
                logger.debug("🔍 DEBUG - Mouse salió de tarjeta: {}", item.getTitle());
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tarjeta);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);
                scaleTransition.play();
            });
            logger.debug("✅ DEBUG - Efectos hover configurados");
            
            logger.info("✅ FIN - configurarEventosTarjeta() completado para: '{}'", item.getTitle());
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en configurarEventosTarjeta() para: '{}'", item.getTitle(), e);
            throw e;
        }
    }
    
    /**
     * Deselecciona todas las tarjetas.
     */
    private void deseleccionarTodasLasTarjetas() {
        if (tarjetasItems == null || tarjetasItems.isEmpty()) {
            logger.debug("🔍 DEBUG - No hay tarjetas para deseleccionar");
            return;
        }
        
        for (VBox tarjeta : tarjetasItems) {
            String colorOriginal = obtenerColorOriginal(tarjeta);
            tarjeta.setStyle(String.format(
                "-fx-background-color: white; -fx-border-color: %s; -fx-border-width: 2; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 3); " +
                "-fx-cursor: hand;",
                colorOriginal
            ));
            
            // Restaurar color del texto
            for (javafx.scene.Node nodo : tarjeta.getChildren()) {
                if (nodo instanceof Label) {
                    ((Label) nodo).setTextFill(Color.BLACK);
                } else if (nodo instanceof TextFlow) {
                    // Los emojis en TextFlow mantienen su color original
                    // No necesitamos cambiar nada aquí
                }
            }
        }
    }
    
    /**
     * Selecciona una tarjeta específica.
     * 
     * @param tarjeta La tarjeta a seleccionar
     * @param item El elemento asociado
     */
    private void seleccionarTarjeta(VBox tarjeta, SelectableItem item) {
        String colorSeleccionado = item.getColor();
        tarjeta.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 3; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 5); " +
            "-fx-cursor: hand;",
            colorSeleccionado, colorSeleccionado
        ));
        
        // Cambiar color del texto a blanco
        for (javafx.scene.Node nodo : tarjeta.getChildren()) {
            if (nodo instanceof Label) {
                ((Label) nodo).setTextFill(Color.WHITE);
            }
            // Los emojis en TextFlow mantienen su color original
            // No necesitamos cambiar nada aquí
        }
    }
    
    /**
     * Obtiene el color original de una tarjeta.
     * 
     * @param tarjeta La tarjeta
     * @return El color original
     */
    private String obtenerColorOriginal(VBox tarjeta) {
        // Buscar el elemento correspondiente a esta tarjeta
        if (tarjetasItems == null || viewModel.getItems() == null) {
            return "#6c757d"; // Color por defecto
        }
        
        int index = tarjetasItems.indexOf(tarjeta);
        if (index >= 0 && index < viewModel.getItems().size()) {
            SelectableItem item = viewModel.getItems().get(index);
            if (item != null) {
                return item.getColor();
            }
        }
        return "#6c757d"; // Color por defecto
    }
    
    /**
     * Cierra el modal y completa el future.
     * 
     * @param resultado El resultado de la selección
     */
    private void cerrarModal(SelectableItem resultado) {
        logger.debug("🚀 INICIO - cerrarModal() con resultado: {}", resultado != null ? resultado.getTitle() : "NULL");
        
        try {
            logger.debug("🔧 DEBUG - Guardando resultado...");
            this.resultado = resultado;
            logger.debug("✅ DEBUG - Resultado guardado");
            
            logger.debug("🔧 DEBUG - Completando future...");
            if (future != null) {
                future.complete(resultado);
                logger.debug("✅ DEBUG - Future completado");
            } else {
                logger.warn("⚠️ WARN - Future es null, no se puede completar");
            }
            
            logger.debug("🔧 DEBUG - Cerrando stage...");
            if (stage != null) {
                // Asegurar que el cierre del stage se ejecute en el hilo de JavaFX
                if (Platform.isFxApplicationThread()) {
                    stage.close();
                    logger.debug("✅ DEBUG - Stage cerrado");
                } else {
                    Platform.runLater(() -> {
                        stage.close();
                        logger.debug("✅ DEBUG - Stage cerrado desde Platform.runLater");
                    });
                }
            } else {
                logger.warn("⚠️ WARN - Stage es null, no se puede cerrar");
            }
            
            logger.info("✅ FIN - cerrarModal() completado exitosamente");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en cerrarModal()", e);
            throw e;
        }
    }
    
    /**
     * Muestra el modal de forma simple sin animaciones complejas.
     * 
     * @return CompletableFuture que se completa con el elemento seleccionado
     */
    public CompletableFuture<SelectableItem> mostrarSimple() {
        logger.info("🚀 INICIO - mostrarSimple() para: {}", viewModel.getTitulo());
        
        try {
            logger.debug("🔧 DEBUG - Creando CompletableFuture...");
            future = new CompletableFuture<>();
            logger.debug("✅ DEBUG - CompletableFuture creado");
            
            // Siempre ejecutar en Platform.runLater para asegurar que estamos en el hilo de JavaFX
            Platform.runLater(() -> {
                try {
                    logger.debug("🔧 DEBUG - Dentro de Platform.runLater - Creando Stage...");
                    
                    // Crear la ventana
                    stage = new Stage();
                    logger.debug("✅ DEBUG - Stage creado");
                    
                    // Configurar modalidad
                    stage.initModality(Modality.APPLICATION_MODAL);
                    logger.debug("✅ DEBUG - Modalidad configurada");
                    
                    // Usar ventana normal con barra de título
                    stage.initStyle(StageStyle.DECORATED);
                    logger.debug("✅ DEBUG - Estilo configurado");
                    
                    // Configurar título
                    stage.setTitle(viewModel.getTitulo());
                    logger.debug("✅ DEBUG - Título configurado: '{}'", viewModel.getTitulo());
                    
                    // Configurar tamaño
                    stage.setMinWidth(800);
                    stage.setMinHeight(600);
                    stage.setWidth(1000);
                    stage.setHeight(700);
                    logger.debug("✅ DEBUG - Tamaño configurado");
                    
                    // Crear la escena
                    Scene scene = new Scene(rootContainer);
                    logger.debug("✅ DEBUG - Scene creado");
                    
                    // Asignar escena al stage
                    stage.setScene(scene);
                    logger.debug("✅ DEBUG - Scene asignado");
                    
                    // Centrar ventana
                    stage.centerOnScreen();
                    logger.debug("✅ DEBUG - Ventana centrada");
                    
                    // Configurar evento de cierre
                    stage.setOnCloseRequest(e -> {
                        logger.debug("🔍 DEBUG - Ventana cerrada por el usuario");
                        if (!future.isDone()) {
                            future.complete(null);
                        }
                    });
                    logger.debug("✅ DEBUG - Evento de cierre configurado");
                    
                    // Mostrar la ventana
                    stage.show();
                    logger.debug("✅ DEBUG - Ventana mostrada");
                    
                    logger.info("✅ FIN - Stage creado y mostrado exitosamente");
                    
                } catch (Exception e) {
                    logger.error("❌ ERROR - Error al crear y mostrar Stage", e);
                    if (!future.isDone()) {
                        future.completeExceptionally(e);
                    }
                }
            });
            
            logger.info("✅ FIN - mostrarSimple() completado, retornando future");
            return future;
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en mostrarSimple()", e);
            if (future != null && !future.isDone()) {
                future.completeExceptionally(e);
            }
            throw e;
        }
    }
    
    /**
     * Muestra el modal y espera la selección del usuario.
     * 
     * @return CompletableFuture que se completa con el elemento seleccionado
     */
    public CompletableFuture<SelectableItem> mostrarYEsperar() {
        logger.info("🚀 INICIO - mostrarYEsperar() para: {}", viewModel.getTitulo());
        
        try {
            logger.debug("🔧 DEBUG - Creando CompletableFuture...");
            future = new CompletableFuture<>();
            logger.debug("✅ DEBUG - CompletableFuture creado");
            
            // Verificar si ya estamos en el hilo de JavaFX
            if (Platform.isFxApplicationThread()) {
                logger.debug("🔧 DEBUG - Ya estamos en el hilo de JavaFX, creando Stage directamente...");
                crearYMostrarStage();
            } else {
                logger.debug("🔧 DEBUG - Ejecutando Platform.runLater...");
                Platform.runLater(this::crearYMostrarStage);
            }
            
            logger.info("✅ FIN - mostrarYEsperar() completado, retornando future");
            return future;
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en mostrarYEsperar()", e);
            if (future != null && !future.isDone()) {
                future.completeExceptionally(e);
            }
            throw e;
        }
    }
    
    /**
     * Muestra el modal de forma asíncrona usando callbacks.
     * 
     * @param onSeleccion Callback que se ejecuta cuando se selecciona un elemento
     * @param onCancelacion Callback que se ejecuta cuando se cancela la selección
     */
    public void mostrarAsync(Consumer<SelectableItem> onSeleccion, Runnable onCancelacion) {
        logger.info("🚀 INICIO - mostrarAsync() para: {}", viewModel.getTitulo());
        
        try {
            logger.debug("🔧 DEBUG - Creando CompletableFuture...");
            future = new CompletableFuture<>();
            logger.debug("✅ DEBUG - CompletableFuture creado");
            
            // Configurar callbacks
            future.thenAccept(resultado -> {
                if (resultado != null) {
                    logger.debug("🔍 DEBUG - Elemento seleccionado: {}", resultado.getTitle());
                    if (onSeleccion != null) {
                        onSeleccion.accept(resultado);
                    }
                } else {
                    logger.debug("🔍 DEBUG - Selección cancelada");
                    if (onCancelacion != null) {
                        onCancelacion.run();
                    }
                }
            }).exceptionally(throwable -> {
                logger.error("❌ ERROR - Error en la selección", throwable);
                if (onCancelacion != null) {
                    onCancelacion.run();
                }
                return null;
            });
            
            // Verificar si ya estamos en el hilo de JavaFX
            if (Platform.isFxApplicationThread()) {
                logger.debug("🔧 DEBUG - Ya estamos en el hilo de JavaFX, creando Stage directamente...");
                crearYMostrarStage();
            } else {
                logger.debug("🔧 DEBUG - Ejecutando Platform.runLater...");
                Platform.runLater(this::crearYMostrarStage);
            }
            
            logger.info("✅ FIN - mostrarAsync() completado");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en mostrarAsync()", e);
            if (onCancelacion != null) {
                onCancelacion.run();
            }
        }
    }
    
    /**
     * Crea y muestra el Stage del modal.
     */
    private void crearYMostrarStage() {
        try {
            logger.debug("🔧 DEBUG - Creando Stage...");
            // Crear la ventana
            stage = new Stage();
            logger.debug("✅ DEBUG - Stage creado");
            
            logger.debug("🔧 DEBUG - Configurando modalidad...");
            stage.initModality(Modality.APPLICATION_MODAL);
            logger.debug("✅ DEBUG - Modalidad configurada");
            
            logger.debug("🔧 DEBUG - Configurando estilo...");
            // Usar ventana normal con barra de título para evitar problemas de renderizado
            stage.initStyle(StageStyle.DECORATED);
            logger.debug("✅ DEBUG - Estilo configurado");
            
            logger.debug("🔧 DEBUG - Configurando título...");
            stage.setTitle(viewModel.getTitulo());
            logger.debug("✅ DEBUG - Título configurado: '{}'", viewModel.getTitulo());
            
            logger.debug("🔧 DEBUG - Configurando tamaño de ventana...");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setWidth(1000);
            stage.setHeight(700);
            logger.debug("✅ DEBUG - Tamaño de ventana configurado");
            
            logger.debug("🔧 DEBUG - Creando Scene...");
            // Crear la escena
            Scene scene = new Scene(rootContainer);
            logger.debug("✅ DEBUG - Scene creado");
            
            logger.debug("🔧 DEBUG - Asignando Scene al Stage...");
            stage.setScene(scene);
            logger.debug("✅ DEBUG - Scene asignado al Stage");
            
            logger.debug("🔧 DEBUG - Centrando ventana...");
            // Centrar la ventana
            stage.centerOnScreen();
            logger.debug("✅ DEBUG - Ventana centrada");
            
            logger.debug("🔧 DEBUG - Configurando evento de cierre...");
            // Configurar evento de cierre para completar el future
            stage.setOnCloseRequest(e -> {
                logger.debug("🔍 DEBUG - Ventana cerrada por el usuario");
                if (!future.isDone()) {
                    future.complete(null);
                }
            });
            logger.debug("✅ DEBUG - Evento de cierre configurado");
            
            logger.debug("🔧 DEBUG - Mostrando ventana...");
            // Mostrar la ventana
            stage.show();
            logger.debug("✅ DEBUG - Ventana mostrada");
            
            logger.debug("🔧 DEBUG - Configurando animación de entrada...");
            // Animación simple de entrada
            animarEntrada();
            logger.debug("✅ DEBUG - Animación de entrada iniciada");
            
            logger.info("✅ FIN - Stage creado y mostrado exitosamente");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error al crear y mostrar Stage", e);
            if (!future.isDone()) {
                future.completeExceptionally(e);
            }
        }
    }
    
    /**
     * Anima la entrada del modal.
     */
    private void animarEntrada() {
        logger.debug("🚀 INICIO - animarEntrada()");
        
        try {
            logger.debug("🔧 DEBUG - Configurando animación simple...");
            // Animación simple de fade in
            rootContainer.setOpacity(0);
            
            logger.debug("🔧 DEBUG - Creando FadeTransition...");
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), rootContainer);
            fadeTransition.setToValue(1.0);
            logger.debug("✅ DEBUG - FadeTransition creado");
            
            logger.debug("🔧 DEBUG - Iniciando animación...");
            fadeTransition.play();
            logger.debug("✅ DEBUG - Animación iniciada");
            
            logger.info("✅ FIN - animarEntrada() completado exitosamente");
        } catch (Exception e) {
            logger.error("❌ ERROR - Error en animarEntrada()", e);
            // En caso de error, simplemente hacer visible el contenedor
            rootContainer.setOpacity(1.0);
        }
    }
    
    /**
     * Cierra el modal de forma segura desde el exterior.
     */
    public void cerrar() {
        logger.debug("🚀 INICIO - cerrar() llamado desde exterior");
        cerrarModal(null);
    }
    
    /**
     * Obtiene el resultado de la selección.
     * 
     * @return El elemento seleccionado, o null si se canceló
     */
    public SelectableItem getResultado() {
        return resultado;
    }
} 