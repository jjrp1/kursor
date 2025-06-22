package com.kursor.ui;

// import com.kursor.PreguntaModule;
import com.kursor.domain.Curso;
import com.kursor.util.CursoManager;
import com.kursor.util.ModuleManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;

import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import com.kursor.yaml.dto.CursoPreviewDTO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aplicación principal de la interfaz gráfica de usuario para Kursor.
 * 
 * <p>Esta clase extiende {@link Application} de JavaFX y proporciona la interfaz
 * gráfica principal para el sistema de cursos Kursor. La aplicación presenta
 * una interfaz con pestañas que permite a los usuarios explorar cursos y módulos
 * de preguntas de manera interactiva.</p>
 * 
 * <p>Características principales de la aplicación:</p>
 * <ul>
 *   <li><strong>Vista de Cursos:</strong> Muestra todos los cursos disponibles con
 *       información detallada y opción de inspección</li>
 *   <li><strong>Vista de Módulos:</strong> Permite seleccionar y probar diferentes
 *       tipos de preguntas de los módulos cargados</li>
 *   <li><strong>Interfaz con Pestañas:</strong> Organiza la información en pestañas
 *       separadas para mejor navegación</li>
 *   <li><strong>Vistas Dinámicas:</strong> Crea pestañas adicionales para mostrar
 *       detalles de cursos y preguntas individuales</li>
 * </ul>
 * 
 * <p>La aplicación utiliza los siguientes componentes del sistema:</p>
 * <ul>
 *   <li>{@link CursoManager} para cargar y gestionar cursos</li>
 *   <li>{@link ModuleManager} para cargar y gestionar módulos de preguntas</li>
 *   <li>SLF4J Logger para registrar eventos y errores</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * // La aplicación se inicia automáticamente al ejecutar el método main
 * public static void main(String[] args) {
 *     launch(args);
 * }
 * }</pre>
 * 
 * <p>La interfaz de usuario está diseñada para ser intuitiva y responsive,
 * proporcionando una experiencia de usuario fluida para la exploración
 * y prueba de contenido educativo.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Application
 * @see CursoManager
 * @see ModuleManager
 * @see Curso
 * @see PreguntaModule
 */
public class KursorApplication extends Application {
    /** Instancia del logger para registrar eventos de la aplicación */
    private static final Logger logger = LoggerFactory.getLogger(KursorApplication.class);
        
    /** Gestor de módulos de la aplicación */
    private ModuleManager moduleManager;
    
    /** Contenedor principal de la interfaz de usuario */
    private BorderPane rootContainer;
      
    /** Referencia a la ventana principal */
    private Stage primaryStage;
    
    /** Referencia al TableView de cursos para acceder a la selección */
    private TableView<CursoPreviewDTO> tableViewCursos;
    
    /**
     * Inicializa y muestra la ventana principal de la aplicación.
     * 
     * <p>Este método es llamado automáticamente por JavaFX cuando la aplicación
     * se inicia. Configura la interfaz de usuario principal con dos pestañas:
     * "Cursos" y "Módulos", y establece las propiedades básicas de la ventana.</p>
     * 
     * <p>La configuración incluye:</p>
     * <ul>
     *   <li>Creación del panel de pestañas principal</li>
     *   <li>Configuración de la pestaña de cursos con vista de lista</li>
     *   <li>Configuración de la pestaña de módulos con selector y lista de preguntas</li>
     *   <li>Establecimiento del tamaño de ventana (1024x768)</li>
     *   <li>Configuración del título de la ventana</li>
     * </ul>
     * 
     * @param primaryStage La ventana principal de la aplicación JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        logger.info("=================================================");
        logger.info("Iniciando aplicación Kursor...");
        logger.info("=================================================");
        
        // Guardar referencia a la ventana principal
        this.primaryStage = primaryStage;
        
        // Configurar el icono de la aplicación (birrete)
        primaryStage.setTitle("🎓 Kursor - Tu Plataforma de Formación Interactiva");
        
        // Inicializar gestor de módulos
        logger.info("Inicializando gestor de módulos...");
        moduleManager = ModuleManager.getInstance();
        
        logger.info("Gestor de módulos inicializado correctamente");
        
        // Crear contenedor principal
        rootContainer = new BorderPane();
        
        // Configurar vista inicial (dashboard)
        rootContainer.setCenter(crearVistaDashboard());
        
        // Configurar barra inferior
        rootContainer.setBottom(crearBarraInferior());
        
        // Crear escena
        Scene scene = new Scene(rootContainer, 1000, 700);
        
        // Configurar ventana principal
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Centrar la ventana en la pantalla
        primaryStage.centerOnScreen();
        
        // Mostrar la ventana
        primaryStage.show();
        
        logger.info("Aplicación Kursor iniciada correctamente");
    }
    
    /**
     * Método llamado automáticamente por JavaFX cuando la aplicación se cierra.
     * 
     * <p>Este método se ejecuta cuando el usuario cierra la ventana principal
     * o cuando se llama a Platform.exit(). Es el lugar ideal para realizar
     * tareas de limpieza y registrar la finalización de la aplicación.</p>
     */
    @Override
    public void stop() {
        logger.info("================================================");
        logger.info("Finalizando aplicación Kursor...");
        logger.info("================================================");
        
        // Aquí se pueden agregar tareas de limpieza si es necesario
        // Por ejemplo: guardar configuraciones, cerrar conexiones, etc.
        
        logger.info("Aplicación Kursor finalizada correctamente");
    }

    private HBox crearBarraInferior() {
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15, 25, 15, 25));
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #34495e); -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        
        // Botón Inspeccionar con icono de lupa
        Button btnInspeccionar = crearBotonConIcono("🔍 Inspeccionar", "#3498db", "#2980b9");
        btnInspeccionar.setOnAction(e -> {
            logger.info("Botón Inspeccionar presionado desde barra inferior");
            mostrarInformacionFilaSeleccionada();
        });
        
        // Botón Comenzar con icono de play
        Button btnComenzar = crearBotonConIcono("▶️ Comenzar", "#27ae60", "#229954");
        btnComenzar.setOnAction(e ->  mostrarAlert("▶️ Comenzar"));
        
        // Botón Reanudar con icono de pausa/play
        Button btnReanudar = crearBotonConIcono("⏯️ Reanudar", "#f39c12", "#e67e22");
        btnReanudar.setOnAction(e -> mostrarAlert("⏯️ Reanudar"));
        
        // Botón Flashcards con icono de tarjetas
        Button btnFlashcards = crearBotonConIcono("🗂️ Flashcards", "#9b59b6", "#8e44ad");
        btnFlashcards.setOnAction(e -> mostrarAlert("🗂️ Flashcards"));
        
        // Botón Estadísticas con icono de gráfico
        Button btnEstadisticas = crearBotonConIcono("📊 Estadísticas", "#e74c3c", "#c0392b");
        btnEstadisticas.setOnAction(e -> EstadisticasDialog.show(this.primaryStage));
        
        // Espaciador flexible para empujar el botón About a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botón About con icono de birrete
        Button btnAbout = crearBotonConIcono("🎓 About", "#34495e", "#2c3e50");
        btnAbout.setOnAction(e -> AboutDialog.show());
        
        bottomBar.getChildren().addAll(btnInspeccionar, btnComenzar, btnReanudar, btnFlashcards, btnEstadisticas, spacer, btnAbout);
        
        return bottomBar;
    }
    
    private Button crearBotonConIcono(String texto, String colorNormal, String colorHover) {
        Button button = new Button(texto);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        button.setTextFill(Color.WHITE);
        button.setPadding(new Insets(8, 16, 8, 16));
        button.setMinHeight(35);
        
        // Estilo base con gradiente y bordes redondeados
        String estiloBase = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: rgba(255,255,255,0.2); " +
            "-fx-border-width: 1; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);",
            colorNormal, colorHover
        );
        
        // Estilo hover con efecto de elevación
        String estiloHover = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: rgba(255,255,255,0.3); " +
            "-fx-border-width: 1; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 3); " +
            "-fx-scale-x: 1.05; " +
            "-fx-scale-y: 1.05;",
            colorHover, colorNormal
        );
        
        button.setStyle(estiloBase);
        
        // Efectos de hover con transiciones suaves
        button.setOnMouseEntered(e -> {
            button.setStyle(estiloHover);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(estiloBase);
        });
        
        // Efecto de presión al hacer clic
        button.setOnMousePressed(e -> {
            button.setStyle(estiloBase + "-fx-scale-x: 0.95; -fx-scale-y: 0.95;");
        });
        
        button.setOnMouseReleased(e -> {
            button.setStyle(estiloHover);
        });
        
        return button;
    }

    private Node crearVistaDashboard() {
        VBox dashboard = new VBox(30);
        dashboard.setPadding(new Insets(40, 50, 40, 50));
        dashboard.setAlignment(Pos.TOP_CENTER);
        
        // Título de bienvenida
        Label welcomeTitle = new Label("Bienvenido a 🎓 Kursor");
        welcomeTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        
        Label welcomeSubtitle = new Label("Tu plataforma de formación interactiva");
        welcomeSubtitle.setFont(Font.font("Segoe UI", 18));
        welcomeSubtitle.setTextFill(Color.GRAY);

        dashboard.getChildren().addAll(welcomeTitle, welcomeSubtitle, crearVistaCursos()); // cursosRecientes);
        return dashboard;
    }
    

    

    
    
    private Node crearVistaCursos() {
        // Crear la tabla
        tableViewCursos = new TableView<>();
        List<CursoPreviewDTO> cursos = CursoManager.getInstance().cargarCursos();
        tableViewCursos.getItems().addAll(cursos);

        // Configurar la tabla
        tableViewCursos.setPlaceholder(new Label("No hay cursos disponibles"));
        tableViewCursos.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        
        // Columna 1: Título
        TableColumn<CursoPreviewDTO, String> tituloCol = new TableColumn<>("Título");
        tituloCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tituloCol.setCellFactory(col -> new TableCell<CursoPreviewDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 14px;");
                }
            }
        });
        tituloCol.setPrefWidth(200);
        tituloCol.setResizable(true);

        // Columna 2: Descripción (con TextArea para texto justificado y múltiples líneas)
        TableColumn<CursoPreviewDTO, String> descripcionCol = new TableColumn<>("Descripción");
        descripcionCol.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        descripcionCol.setCellFactory(col -> new TableCell<CursoPreviewDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Crear TextArea para texto justificado y múltiples líneas
                    TextArea textArea = new TextArea(item);
                    textArea.setWrapText(true);
                    textArea.setEditable(false);
                    textArea.setPrefRowCount(3); // Altura mínima de 3 líneas
                    textArea.setMaxHeight(120); // Altura máxima
                    textArea.setMinHeight(60);  // Altura mínima
                    
                    // Configurar el TextArea para que se ajuste al contenido
                    textArea.setPrefColumnCount(50);
                    textArea.setMaxWidth(Double.MAX_VALUE);
                    
                    // Ajustar altura automáticamente según el contenido
                    textArea.textProperty().addListener((obs, oldText, newText) -> {
                        if (newText != null) {
                            // Calcular altura basada en el número de líneas
                            int lineCount = newText.split("\n").length;
                            int estimatedLines = Math.max(3, Math.min(lineCount + 1, 6));
                            textArea.setPrefRowCount(estimatedLines);
                        }
                    });
                    
                    // Aplicar estilo base sin padding y que respete la selección
                    aplicarEstiloTextArea(textArea, isSelected());
                    
                    // Escuchar cambios en la selección para actualizar el estilo
                    selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        aplicarEstiloTextArea(textArea, isNowSelected);
                    });
                    
                    setGraphic(textArea);
                    setText(null); // No usar setText cuando usamos setGraphic
                }
            }
            
            private void aplicarEstiloTextArea(TextArea textArea, boolean seleccionado) {
                if (seleccionado) {
                    // Estilo para fila seleccionada
                    textArea.setStyle(
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; " +
                        "-fx-text-alignment: justify; " +
                        "-fx-padding: 0px;"
                    );
                } else {
                    // Estilo para fila no seleccionada
                    textArea.setStyle(
                        "-fx-text-fill: #7f8c8d; " +
                        "-fx-font-size: 12px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; " +
                        "-fx-text-alignment: justify; " +
                        "-fx-padding: 0px;"
                    );
                }
            }
        });
        descripcionCol.setPrefWidth(400);
        descripcionCol.setResizable(true);

        // Columna 3: ID
        TableColumn<CursoPreviewDTO, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setCellFactory(col -> new TableCell<CursoPreviewDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px; -fx-font-style: italic;");
                }
            }
        });
        idCol.setPrefWidth(150);
        idCol.setResizable(true);

        // Agregar columnas a la tabla
        tableViewCursos.getColumns().addAll(tituloCol, descripcionCol, idCol);

        // Seleccionar automáticamente el primer elemento si hay cursos disponibles
        if (!cursos.isEmpty()) {
            logger.debug("Seleccionando automáticamente el primer curso: '{}'", cursos.get(0).getTitulo());
            tableViewCursos.getSelectionModel().select(0);
        }

        // Manejo de eventos de mouse para click simple y doble-click
        /*
        tableViewCursos.setOnMouseClicked(event -> {
            CursoPreviewDTO seleccionado = tableViewCursos.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                logger.debug("Click detectado en TableView - ID: {}, Título: '{}', ClickCount: {}", 
                    seleccionado.getId(), seleccionado.getTitulo(), event.getClickCount());
                
                if (event.getClickCount() == 1) {
                    // Click simple: mostrar alert con el nombre del curso
                    logger.info("Click simple detectado en TableView - mostrando alert para curso: '{}'", seleccionado.getTitulo());     
                    InspeccionarCurso.mostrar(seleccionado, this.primaryStage);
                }
            }
        });
         */

        VBox contenedor = new VBox(15, tableViewCursos); 
        contenedor.setPadding(new Insets(20));
        return contenedor;
    }
    
    private void mostrarDetallesCurso(CursoPreviewDTO cursoPreview) {
        CursoDialog dialog = new CursoDialog(cursoPreview, this.primaryStage);
        dialog.showAndWait();
    }

    /**
     * Muestra un alert informativo genérico.
     * 
     * @param texto El texto a mostrar
     */
    private void mostrarAlert(String texto) {
        logger.debug("Mostrando alert: '{}'", texto);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Texto de Cabecera");
        alert.setContentText(texto);
        
        // Configurar el diálogo como modal
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(this.primaryStage);
        
        // Mostrar el alert y manejar la respuesta
        Optional<ButtonType> result = alert.showAndWait();
        logger.debug("Usuario cerró el alert");
    }

    /**
     * Muestra un alert informativo con título y contenido personalizados.
     * 
     * @param titulo El título del alert
     * @param contenido El contenido del alert
     */
    private void mostrarAlert(String titulo, String contenido) {
        logger.debug("Mostrando alert con título: '{}', contenido: '{}'", titulo, contenido);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        
        // Configurar el diálogo como modal
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(this.primaryStage);
        
        // Mostrar el alert y manejar la respuesta
        Optional<ButtonType> result = alert.showAndWait();
        logger.debug("Usuario cerró el alert");
    }

    /**
     * Muestra un alert informativo cuando se selecciona un curso con click simple.
     * 
     * @param curso El curso seleccionado
     */
    private void mostrarAlertSeleccion(CursoPreviewDTO curso) {
        logger.debug("Mostrando alert de selección para curso: '{}'", curso.getTitulo());
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Curso Seleccionado");
        alert.setHeaderText("Has seleccionado el siguiente curso:");
        alert.setContentText(String.format("📚 %s\n\n%s\n\nID: %s", 
            curso.getTitulo(), 
            curso.getDescripcion(), 
            curso.getId()));
        
        // Configurar el diálogo como modal
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(this.primaryStage);
        
        // Agregar botones adicionales
        ButtonType inspeccionarButton = new ButtonType("🔍 Inspeccionar");
        ButtonType cerrarButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(inspeccionarButton, cerrarButton);
        
        // Mostrar el alert y manejar la respuesta
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == inspeccionarButton) {
            logger.info("Usuario eligió inspeccionar curso desde alert: '{}'", curso.getTitulo());
            InspeccionarCurso.mostrar(curso, this.primaryStage);
        } else {
            logger.debug("Usuario cerró el alert de selección");
        }
    }


    private void mostrarInformacionFilaSeleccionada() {
        logger.info("🔍 Mostrando información de la fila seleccionada en TableView");
        
        CursoPreviewDTO seleccionado = tableViewCursos.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            logger.warn("⚠️ No hay fila seleccionada en el TableView");
            mostrarAlert("⚠️ Sin selección", "No hay ningún curso seleccionado en la tabla.\n\nPor favor, selecciona una fila haciendo clic en ella.");
            return;
        }

        InspeccionarCurso.mostrar(seleccionado, this.primaryStage);

    }

    /**
     * Punto de entrada principal para la aplicación.
     * 
     * <p>Este método lanza la aplicación JavaFX.</p>
     * 
     * @param args Argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        launch(args);
    }
} 