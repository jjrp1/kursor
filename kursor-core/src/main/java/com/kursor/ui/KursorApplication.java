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
            mostrarDialogoSeleccionCurso();
        });
        
        // Botón Comenzar con icono de play
        Button btnComenzar = crearBotonConIcono("▶️ Comenzar", "#27ae60", "#229954");
        btnComenzar.setOnAction(e -> rootContainer.setCenter(crearVistaDashboard()));
        
        // Botón Reanudar con icono de pausa/play
        Button btnReanudar = crearBotonConIcono("⏯️ Reanudar", "#f39c12", "#e67e22");
        btnReanudar.setOnAction(e -> rootContainer.setCenter(crearVistaDashboard()));
        
        // Botón Flashcards con icono de tarjetas
        Button btnFlashcards = crearBotonConIcono("🗂️ Flashcards", "#9b59b6", "#8e44ad");
        btnFlashcards.setOnAction(e -> rootContainer.setCenter(crearVistaDashboard()));
        
        // Botón Estadísticas con icono de gráfico
        Button btnEstadisticas = crearBotonConIcono("📊 Estadísticas", "#e74c3c", "#c0392b");
        btnEstadisticas.setOnAction(e -> EstadisticasDialog.show(this.primaryStage));
        
        // Espaciador flexible para empujar el botón About a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botón About con icono de birrete
        Button btnAbout = crearBotonConIcono("🎓 About", "#34495e", "#2c3e50");
        btnAbout.setOnAction(e -> mostrarDialogoAbout());
        
        bottomBar.getChildren().addAll(
            btnInspeccionar, btnComenzar, btnReanudar, 
            btnFlashcards, btnEstadisticas, spacer, btnAbout
        );
        
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
        
        // No longer needed here, moved to bottom bar
        // HBox accionesRapidas = crearAccionesRapidas();
        
        VBox cursosRecientes = crearCursosRecientes();
        
        dashboard.getChildren().addAll(welcomeTitle, welcomeSubtitle, cursosRecientes);
        return dashboard;
    }
    
    private VBox crearCursosRecientes() {
        VBox cursosContainer = new VBox(15);
        cursosContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Cursos Disponibles");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setPadding(new Insets(0, 0, 5, 0));
        
        List<CursoPreviewDTO> cursos = CursoManager.getInstance().cargarCursos();
        
        VBox cursosBox = new VBox(15);
        for (CursoPreviewDTO curso : cursos) {
            cursosBox.getChildren().add(crearTarjetaCurso(curso));
        }
        
        ScrollPane scrollPane = new ScrollPane(cursosBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        cursosContainer.getChildren().addAll(titulo, scrollPane);
        return cursosContainer;
    }
    
    private HBox crearTarjetaCurso(CursoPreviewDTO curso) {
        HBox tarjeta = new HBox(20);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4); -fx-cursor: hand;");
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        
        // Log de depuración para verificar el título
        String tituloTexto = curso.getTitulo();
        logger.debug("Creando tarjeta para curso - ID: {}, Título: '{}'", curso.getId(), tituloTexto);
        
        // Manejo de eventos de mouse para click simple y doble-click
        tarjeta.setOnMouseClicked(e -> {
            logger.debug("Click detectado en curso - ID: {}, Título: '{}', ClickCount: {}", 
                curso.getId(), curso.getTitulo(), e.getClickCount());
            
            if (e.getClickCount() == 1) {
                // Click simple: mostrar alert con el nombre del curso
                logger.info("Click simple detectado - mostrando alert para curso: '{}'", curso.getTitulo());
                mostrarAlertSeleccion(curso);
            } else if (e.getClickCount() == 2) {
                // Doble-click: inspeccionar curso
                logger.info("Doble-click detectado - inspeccionando curso: '{}'", curso.getTitulo());
                InspeccionarCurso.mostrar(curso, this.primaryStage);
            }
        });
        
        VBox infoCurso = new VBox(5);
        
        // Título del curso con color explícito y sin borde temporal
        Label tituloCurso = new Label(tituloTexto);
        tituloCurso.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tituloCurso.setTextFill(Color.web("#2c3e50")); // Color azul oscuro
        tituloCurso.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        // Descripción del curso con color explícito
        Label descCurso = new Label(curso.getDescripcion());
        descCurso.setWrapText(true);
        descCurso.setTextFill(Color.web("#7f8c8d")); // Color gris
        descCurso.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        // ID del curso con estilo distintivo
        Label idCurso = new Label("ID: " + curso.getId());
        idCurso.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px; -fx-font-style: italic;");
        
        infoCurso.getChildren().addAll(tituloCurso, descCurso, idCurso);
        
        tarjeta.getChildren().addAll(infoCurso);
        return tarjeta;
    }
    
    private Node crearVistaEstadisticas() {
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(20));
        Label label = new Label("Vista de Estadísticas (en construcción)");
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        stackPane.getChildren().add(label);
        return stackPane;
    }
    
    private Node crearVistaModulos() {
        // Contenedor principal para la vista de módulos
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        
        // Título de la sección
        Label titulo = new Label("Módulos de Preguntas Disponibles");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        
        // Selector de tipo de pregunta (ComboBox)
        ComboBox<String> moduleSelector = new ComboBox<>();
        moduleSelector.setPromptText("Selecciona un tipo de pregunta");
        
        // Lista para mostrar las preguntas del módulo seleccionado
        ListView<String> questionListView = new ListView<>();
        
        // Llenar el ComboBox con los nombres de los módulos cargados
        // moduleManager.getPreguntaModules().forEach(module -> moduleSelector.getItems().add(module.getModuleName()));
        
        // Acción al seleccionar un módulo
        /*
        moduleSelector.setOnAction(event -> {
            String selectedModule = moduleSelector.getValue();
            if (selectedModule != null) {
                // Limpiar la lista de preguntas
                questionListView.getItems().clear();
                
                // Encontrar el módulo seleccionado
                moduleManager.getPreguntaModule(selectedModule).ifPresent(module -> {
                    // Obtener la lista de preguntas de ejemplo
                    List<String> exampleQuestions = module.getExampleQuestions();
                    questionListView.getItems().addAll(exampleQuestions);
                });
            }
        });
        */
        
        mainContainer.getChildren().addAll(titulo, moduleSelector, questionListView);
        return mainContainer;
    }
    
    private Node crearVistaCursos() {
        // Crear la tabla
        TableView<CursoPreviewDTO> tableView = new TableView<>();
        List<CursoPreviewDTO> cursos = CursoManager.getInstance().cargarCursos();
        tableView.getItems().addAll(cursos);

        // Configurar la tabla
        tableView.setPlaceholder(new Label("No hay cursos disponibles"));
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        
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

        // Columna 2: Descripción con TextArea
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
                    TextArea textArea = new TextArea(item);
                    textArea.setWrapText(true);
                    textArea.setEditable(false);
                    textArea.setPrefRowCount(3);
                    textArea.setMaxHeight(80);
                    textArea.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-text-alignment: justify;");
                    
                    // Ajustar altura automáticamente
                    textArea.textProperty().addListener((obs, oldText, newText) -> {
                        textArea.setPrefRowCount(Math.min(4, newText.length() / 50 + 1));
                    });
                    
                    setGraphic(textArea);
                    setStyle("-fx-background-color: transparent;");
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
                    setText("ID: " + item);
                    setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px; -fx-font-style: italic;");
                }
            }
        });
        idCol.setPrefWidth(150);
        idCol.setResizable(true);

        // Agregar columnas a la tabla
        tableView.getColumns().addAll(tituloCol, descripcionCol, idCol);

        // Seleccionar automáticamente el primer elemento si hay cursos disponibles
        if (!cursos.isEmpty()) {
            logger.debug("Seleccionando automáticamente el primer curso: '{}'", cursos.get(0).getTitulo());
            tableView.getSelectionModel().select(0);
        }

        // Botón Inspeccionar
        Button inspectButton = crearBotonConIcono("🔍 Inspeccionar", "#3498db", "#2980b9");
        inspectButton.setDisable(cursos.isEmpty());
        inspectButton.setOnAction(e -> {
            CursoPreviewDTO seleccionado = tableView.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                logger.info("Inspeccionando curso desde botón - ID: {}, Título: '{}'", 
                    seleccionado.getId(), seleccionado.getTitulo());
                InspeccionarCurso.mostrar(seleccionado, this.primaryStage);
            }
        });

        // Habilitar el botón solo si hay selección
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            inspectButton.setDisable(newVal == null);
        });

        // Manejo de eventos de mouse para click simple y doble-click
        tableView.setOnMouseClicked(event -> {
            CursoPreviewDTO seleccionado = tableView.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                logger.debug("Click detectado en TableView - ID: {}, Título: '{}', ClickCount: {}", 
                    seleccionado.getId(), seleccionado.getTitulo(), event.getClickCount());
                
                if (event.getClickCount() == 1) {
                    // Click simple: mostrar alert con el nombre del curso
                    logger.info("Click simple detectado en TableView - mostrando alert para curso: '{}'", seleccionado.getTitulo());
                    mostrarAlertSeleccion(seleccionado);
                } else if (event.getClickCount() == 2) {
                    // Doble-click: inspeccionar curso
                    logger.info("Doble-click detectado en TableView - inspeccionando curso: '{}'", seleccionado.getTitulo());
                    InspeccionarCurso.mostrar(seleccionado, this.primaryStage);
                }
            }
        });

        // Configurar estilos de selección
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        
        // Estilo para filas seleccionadas
        tableView.setRowFactory(tv -> new TableRow<CursoPreviewDTO>() {
            @Override
            protected void updateItem(CursoPreviewDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    if (isSelected()) {
                        setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                    } else {
                        setStyle("-fx-background-color: white; -fx-text-fill: black;");
                    }
                }
            }
        });

        VBox contenedor = new VBox(15, tableView, inspectButton);
        contenedor.setPadding(new Insets(20));
        return contenedor;
    }
    
    private void mostrarDetallesCurso(CursoPreviewDTO cursoPreview) {
        CursoDialog dialog = new CursoDialog(cursoPreview, this.primaryStage);
        dialog.showAndWait();
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

    /**
     * Muestra un diálogo de selección de curso cuando se presiona el botón Inspeccionar
     * desde la barra inferior.
     */
    private void mostrarDialogoSeleccionCurso() {
        logger.debug("Mostrando diálogo de selección de curso");
        
        List<CursoPreviewDTO> cursos = CursoManager.getInstance().cargarCursos();
        
        if (cursos.isEmpty()) {
            logger.warn("No hay cursos disponibles para inspeccionar");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Cursos Disponibles");
            alert.setHeaderText("No hay cursos para inspeccionar");
            alert.setContentText("No se encontraron cursos en el sistema.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(this.primaryStage);
            alert.showAndWait();
            return;
        }
        
        // Crear ChoiceDialog con la lista de cursos
        ChoiceDialog<CursoPreviewDTO> dialog = new ChoiceDialog<>(cursos.get(0), cursos);
        dialog.setTitle("Seleccionar Curso para Inspeccionar");
        dialog.setHeaderText("Elige el curso que deseas inspeccionar:");
        dialog.setContentText("Curso:");
        
        // Configurar el diálogo como modal
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(this.primaryStage);
        
        // Mostrar el diálogo y manejar la respuesta
        Optional<CursoPreviewDTO> result = dialog.showAndWait();
        if (result.isPresent()) {
            CursoPreviewDTO cursoSeleccionado = result.get();
            logger.info("Usuario seleccionó curso para inspeccionar: '{}'", cursoSeleccionado.getTitulo());
            InspeccionarCurso.mostrar(cursoSeleccionado, this.primaryStage);
        } else {
            logger.debug("Usuario canceló la selección de curso");
        }
    }

    private void mostrarDialogoAbout() {
        AboutDialog.show();
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