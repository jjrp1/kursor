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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        logger.info("Creando CardSelectorModal para: {} con {} elementos", titulo, items.size());
        
        // Crear controlador y ViewModel
        this.controller = new CardSelectorController(titulo, descripcion, items);
        this.viewModel = controller.getViewModel();
        
        // Inicializar componentes
        inicializarComponentes();
        configurarEventos();
        configurarBindings();
        
        logger.info("CardSelectorModal creado correctamente");
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
        // Contenedor principal
        rootContainer = new VBox(20);
        rootContainer.setAlignment(Pos.CENTER);
        rootContainer.setPadding(new Insets(30));
        rootContainer.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        
        // Título
        tituloLabel = new Label(viewModel.getTitulo());
        tituloLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        tituloLabel.setTextFill(Color.WHITE);
        tituloLabel.setTextAlignment(TextAlignment.CENTER);
        
        // Descripción
        descripcionLabel = new Label(viewModel.getDescripcion());
        descripcionLabel.setFont(Font.font("Segoe UI", 16));
        descripcionLabel.setTextFill(Color.WHITE);
        descripcionLabel.setTextAlignment(TextAlignment.CENTER);
        descripcionLabel.setWrapText(true);
        descripcionLabel.setMaxWidth(600);
        
        // Contenedor de tarjetas
        tarjetasContainer = new FlowPane(15, 15);
        tarjetasContainer.setAlignment(Pos.CENTER);
        tarjetasContainer.setPadding(new Insets(20));
        
        // ScrollPane para las tarjetas
        scrollPane = new ScrollPane(tarjetasContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(400);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Botones
        HBox botones = crearBotones();
        
        // Mensaje de error
        mensajeErrorLabel = new Label();
        mensajeErrorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        mensajeErrorLabel.setTextFill(Color.RED);
        mensajeErrorLabel.setVisible(false);
        mensajeErrorLabel.setManaged(false);
        
        // Indicador de progreso
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        
        // Agregar componentes al contenedor principal
        rootContainer.getChildren().addAll(
            tituloLabel,
            descripcionLabel,
            scrollPane,
            mensajeErrorLabel,
            progressIndicator,
            botones
        );
        
        // Crear tarjetas
        crearTarjetas();
    }
    
    /**
     * Crea los botones de acción del modal.
     * 
     * @return HBox con los botones configurados
     */
    private HBox crearBotones() {
        logger.debug("Creando botones de acción del modal");
        
        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(30, 0, 20, 0));
        
        // Botón Cancelar
        btnCancelar = new Button("❌ Cancelar");
        btnCancelar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnCancelar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 12 25; " +
                           "-fx-cursor: hand; -fx-background-radius: 8;");
        btnCancelar.setPrefWidth(200);
        btnCancelar.setPrefHeight(40);
        
        // Botón Seleccionar (inicialmente deshabilitado)
        btnSeleccionar = new Button("✅ Seleccionar");
        btnSeleccionar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnSeleccionar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 12 25; " +
                              "-fx-cursor: hand; -fx-background-radius: 8;");
        btnSeleccionar.setPrefWidth(200);
        btnSeleccionar.setPrefHeight(40);
        btnSeleccionar.setDisable(true);
        
        botones.getChildren().addAll(btnCancelar, btnSeleccionar);
        
        return botones;
    }
    
    /**
     * Configura los eventos de los componentes.
     */
    private void configurarEventos() {
        // Evento del botón Cancelar
        btnCancelar.setOnAction(e -> {
            logger.debug("Usuario canceló la selección");
            controller.cancelarSeleccion();
            cerrarModal(null);
        });
        
        // Evento del botón Seleccionar
        btnSeleccionar.setOnAction(e -> {
            logger.debug("Usuario confirmó la selección");
            SelectableItem seleccion = controller.confirmarSeleccion();
            cerrarModal(seleccion);
        });
        
        // Evento de tecla ESC para cancelar
        rootContainer.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ESCAPE")) {
                logger.debug("Usuario presionó ESC para cancelar");
                controller.cancelarSeleccion();
                cerrarModal(null);
            }
        });
    }
    
    /**
     * Configura los bindings entre el ViewModel y la UI.
     */
    private void configurarBindings() {
        // Binding del botón Seleccionar
        btnSeleccionar.disableProperty().bind(viewModel.itemValidoProperty().not());
        
        // Binding del mensaje de error
        mensajeErrorLabel.textProperty().bind(viewModel.mensajeErrorProperty());
        mensajeErrorLabel.visibleProperty().bind(viewModel.mensajeErrorProperty().isNotEmpty());
        mensajeErrorLabel.managedProperty().bind(viewModel.mensajeErrorProperty().isNotEmpty());
        
        // Binding del indicador de progreso
        progressIndicator.visibleProperty().bind(viewModel.cargandoProperty());
        progressIndicator.managedProperty().bind(viewModel.cargandoProperty());
    }
    
    /**
     * Crea las tarjetas para cada elemento seleccionable.
     */
    private void crearTarjetas() {
        logger.debug("Creando tarjetas para {} elementos", viewModel.getItems().size());
        
        tarjetasItems = viewModel.getItems().stream()
            .map(this::crearTarjeta)
            .toList();
        
        tarjetasContainer.getChildren().addAll(tarjetasItems);
        
        logger.info("Tarjetas creadas correctamente: {}", tarjetasItems.size());
    }
    
    /**
     * Crea una tarjeta individual para un elemento.
     * 
     * @param item El elemento para el cual crear la tarjeta
     * @return La tarjeta creada
     */
    private VBox crearTarjeta(SelectableItem item) {
        VBox tarjeta = new VBox(15);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(25));
        tarjeta.setPrefWidth(280);
        tarjeta.setPrefHeight(200);
        tarjeta.setMaxWidth(280);
        tarjeta.setMaxHeight(200);
        
        // Estilo base de la tarjeta
        String estiloBase = String.format(
            "-fx-background-color: white; -fx-border-color: %s; -fx-border-width: 2; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 3); " +
            "-fx-cursor: hand;",
            item.getColor()
        );
        tarjeta.setStyle(estiloBase);
        
        // Icono
        Label iconoLabel = new Label(item.getIcon());
        iconoLabel.setFont(Font.font("Segoe UI", 48));
        
        // Título
        Label tituloLabel = new Label(item.getTitle());
        tituloLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        tituloLabel.setTextAlignment(TextAlignment.CENTER);
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(240);
        
        // Descripción
        Label descripcionLabel = new Label(item.getDescription());
        descripcionLabel.setFont(Font.font("Segoe UI", 12));
        descripcionLabel.setTextAlignment(TextAlignment.CENTER);
        descripcionLabel.setWrapText(true);
        descripcionLabel.setMaxWidth(240);
        descripcionLabel.setMaxHeight(80);
        
        // Agregar componentes a la tarjeta
        tarjeta.getChildren().addAll(iconoLabel, tituloLabel, descripcionLabel);
        
        // Tooltip
        Tooltip tooltip = new Tooltip("Haz clic para seleccionar: " + item.getTitle());
        Tooltip.install(tarjeta, tooltip);
        
        // Eventos de la tarjeta
        configurarEventosTarjeta(tarjeta, item);
        
        return tarjeta;
    }
    
    /**
     * Configura los eventos para una tarjeta.
     * 
     * @param tarjeta La tarjeta a configurar
     * @param item El elemento asociado
     */
    private void configurarEventosTarjeta(VBox tarjeta, SelectableItem item) {
        tarjeta.setOnMouseClicked(e -> {
            logger.debug("Usuario seleccionó elemento: {}", item.getTitle());
            
            // Deseleccionar todas las tarjetas
            deseleccionarTodasLasTarjetas();
            
            // Seleccionar esta tarjeta
            seleccionarTarjeta(tarjeta, item);
            
            // Notificar al controlador
            controller.seleccionarItem(item);
        });
        
        // Efectos hover
        tarjeta.setOnMouseEntered(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tarjeta);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });
        
        tarjeta.setOnMouseExited(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tarjeta);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }
    
    /**
     * Deselecciona todas las tarjetas.
     */
    private void deseleccionarTodasLasTarjetas() {
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
        int index = tarjetasItems.indexOf(tarjeta);
        if (index >= 0 && index < viewModel.getItems().size()) {
            return viewModel.getItems().get(index).getColor();
        }
        return "#6c757d"; // Color por defecto
    }
    
    /**
     * Cierra el modal y completa el future.
     * 
     * @param resultado El resultado de la selección
     */
    private void cerrarModal(SelectableItem resultado) {
        this.resultado = resultado;
        if (future != null) {
            future.complete(resultado);
        }
        if (stage != null) {
            stage.close();
        }
    }
    
    /**
     * Muestra el modal y espera la selección del usuario.
     * 
     * @return CompletableFuture que se completa con el elemento seleccionado
     */
    public CompletableFuture<SelectableItem> mostrarYEsperar() {
        logger.info("Mostrando CardSelectorModal para: {}", viewModel.getTitulo());
        
        future = new CompletableFuture<>();
        
        Platform.runLater(() -> {
            // Crear la ventana
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(viewModel.getTitulo());
            
            // Crear la escena
            Scene scene = new Scene(rootContainer);
            stage.setScene(scene);
            
            // Centrar la ventana
            stage.centerOnScreen();
            
            // Mostrar la ventana
            stage.show();
            
            // Animación de entrada
            animarEntrada();
        });
        
        return future;
    }
    
    /**
     * Anima la entrada del modal.
     */
    private void animarEntrada() {
        rootContainer.setScaleX(0.8);
        rootContainer.setScaleY(0.8);
        rootContainer.setOpacity(0);
        
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), rootContainer);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), rootContainer);
        fadeTransition.setToValue(1.0);
        
        scaleTransition.play();
        fadeTransition.play();
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