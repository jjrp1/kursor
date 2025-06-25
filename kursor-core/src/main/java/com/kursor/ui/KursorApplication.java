package com.kursor.ui;

// import com.kursor.PreguntaModule;
import com.kursor.domain.Bloque;
import com.kursor.domain.Curso;
import com.kursor.util.CursoManager;
import com.kursor.util.ModuleManager;
import com.kursor.util.StrategyManager;
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
import com.kursor.yaml.dto.CursoDTO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kursor.ui.CursoInterfaceController;
import javafx.scene.control.Alert;
import javafx.application.Platform;

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
 *   <li>{@link StrategyManager} para gestionar estrategias de aprendizaje</li>
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
    
    /** Gestor de estrategias de la aplicación */
    private StrategyManager strategyManager;
    
    /** Contenedor principal de la interfaz de usuario */
    private BorderPane rootContainer;
      
    /** Referencia a la ventana principal */
    private Stage primaryStage;
    
    /** Referencia al ListView de cursos para acceder a la selección */
    private ListView<CursoDTO> listViewCursos;
    
    /** Controlador para la ejecución de cursos */
    private CursoInterfaceController cursoController;
    
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
        strategyManager = StrategyManager.getInstance();
        
        // Mostrar información de estrategias cargadas
        if (strategyManager.hasStrategies()) {
            logger.info("📊 Estrategias cargadas: {} estrategias disponibles", strategyManager.getStrategyCount());
            logger.debug("📋 Información detallada de estrategias:\n{}", strategyManager.getStrategiesInfo());
        } else {
            logger.warn("⚠️ No se cargaron estrategias de aprendizaje. La funcionalidad de estrategias estará limitada.");
        }
        
        // Validación crítica: verificar que hay al menos módulos o estrategias disponibles
        if (!moduleManager.hasModules() && !strategyManager.hasStrategies()) {
            String errorMsg = "❌ ERROR CRÍTICO: No se pudieron cargar módulos de preguntas ni estrategias de aprendizaje.\n" +
                             "La aplicación no puede funcionar sin estos componentes esenciales.\n\n" +
                             "Verifica que:\n" +
                             "• Existen archivos JAR en el directorio 'modules/' con implementaciones de PreguntaModule\n" +
                             "• Existen archivos JAR en el directorio 'strategies/' con implementaciones de EstrategiaModule\n" +
                             "• Los archivos JAR contienen los archivos de servicios correctos\n" +
                             "• Los archivos JAR no están corruptos\n\n" +
                             "Rutas de búsqueda:\n" +
                             "• Módulos: " + moduleManager.getModulosDir() + "\n" +
                             "• Estrategias: " + strategyManager.getStrategiesDir();
            
            logger.error("=================================================");
            logger.error("ERROR CRÍTICO - APLICACIÓN NO FUNCIONAL");
            logger.error("=================================================");
            logger.error(errorMsg);
            logger.error("=================================================");
            
            // Mostrar alert crítico al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("❌ Error Crítico - Aplicación No Funcional");
            alert.setHeaderText("No se pudieron cargar componentes esenciales");
            alert.setContentText(errorMsg);
            alert.setResizable(true);
            alert.getDialogPane().setPrefWidth(600);
            alert.getDialogPane().setPrefHeight(400);
            
            // Configurar el alert como modal y centrado
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(primaryStage);
            
            // Mostrar el alert y terminar la aplicación
            alert.showAndWait();
            
            // Terminar la aplicación con código de error
            logger.error("Terminando aplicación debido a error crítico");
            Platform.exit();
            System.exit(1);
        }
        
        logger.info("Gestor de módulos inicializado correctamente");
        
        // Inicializar controlador de cursos
        logger.info("Inicializando controlador de cursos...");
        cursoController = new CursoInterfaceController(primaryStage);
        
        logger.info("Controlador de cursos inicializado correctamente");
        
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
        // Button btnInspeccionar = crearBotonConIcono("🔍 Inspeccionar", "#3498db", "#2980b9");
        // btnInspeccionar.setOnAction(e -> {
        //     logger.info("Botón Inspeccionar presionado desde barra inferior");
        //     mostrarInformacionFilaSeleccionada();
        // });
        
        // Botón Comenzar con icono de play
        Button btnComenzar = crearBotonConIcono("▶️ Comenzar", "#27ae60", "#229954");
        btnComenzar.setOnAction(e -> {
            logger.info("Botón Comenzar presionado desde barra inferior");
            iniciarCursoSeleccionado();
        });
        
        // Botón Reanudar con icono de pausa/play
        Button btnReanudar = crearBotonConIcono("⏯️ Reanudar", "#f39c12", "#e67e22");
        btnReanudar.setOnAction(e -> mostrarAlert("⏯️ Reanudar"));
        
        // Botón Flashcards con icono de tarjetas
        // Button btnFlashcards = crearBotonConIcono("🗂️ Flashcards", "#9b59b6", "#8e44ad");
        // btnFlashcards.setOnAction(e -> mostrarAlert("🗂️ Flashcards"));
        
        // Botón Estadísticas con icono de gráfico
        Button btnEstadisticas = crearBotonConIcono("📊 Estadísticas", "#e74c3c", "#c0392b");
        btnEstadisticas.setOnAction(e -> EstadisticasDialog.show(this.primaryStage));
        
        // Espaciador flexible para empujar el botón About a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botón About con icono de birrete
        Button btnAbout = crearBotonConIcono("🎓 About", "#34495e", "#2c3e50");
        btnAbout.setOnAction(e -> AboutDialog.show());
        
        bottomBar.getChildren().addAll(btnComenzar, btnReanudar, btnEstadisticas, spacer, btnAbout);
        
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
        List<CursoDTO> cursos = CursoManager.getInstance().cargarCursosCompletos();
        
        // Panel izquierdo: ListView con títulos de cursos
        listViewCursos = new ListView<>();
        listViewCursos.getItems().addAll(cursos);
        listViewCursos.setPrefWidth(300);
        listViewCursos.setMaxWidth(300);
        
        // Configurar el ListView para mostrar solo el título
        listViewCursos.setCellFactory(param -> new ListCell<CursoDTO>() {
            @Override
            protected void updateItem(CursoDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item.getTitulo());
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px;");
                }
            }
        });
        
        // Panel derecho: Detalles del curso seleccionado
        VBox panelDetalles = new VBox(15);
        panelDetalles.setPadding(new Insets(20));
        panelDetalles.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        
        // Contenido inicial del panel de detalles
        Label labelSeleccion = new Label("Selecciona un curso para ver sus detalles");
        labelSeleccion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        labelSeleccion.setTextFill(Color.GRAY);
        labelSeleccion.setAlignment(Pos.CENTER);
        
        panelDetalles.getChildren().add(labelSeleccion);
        
        // Hacer que el panel de detalles sea scrollable
        ScrollPane scrollPaneDetalles = new ScrollPane(panelDetalles);
        scrollPaneDetalles.setFitToWidth(true);
        scrollPaneDetalles.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneDetalles.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        // Contenedor principal con los dos paneles
        HBox contenedorPrincipal = new HBox(0);
        contenedorPrincipal.getChildren().addAll(listViewCursos, scrollPaneDetalles);
        HBox.setHgrow(scrollPaneDetalles, Priority.ALWAYS);
        
        // Seleccionar automáticamente el primer elemento si hay cursos disponibles
        if (!cursos.isEmpty()) {
            logger.debug("Seleccionando automáticamente el primer curso: '{}'", cursos.get(0).getTitulo());
            listViewCursos.getSelectionModel().select(0);
            mostrarDetallesCurso(cursos.get(0), panelDetalles);
        }
        
        // Listener para cuando se selecciona un curso
        listViewCursos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                logger.info("Curso seleccionado: '{}'", newVal.getTitulo());
                mostrarDetallesCurso(newVal, panelDetalles);
            }
        });
        
        return contenedorPrincipal;
    }
    
    private void mostrarDetallesCurso(CursoDTO cursoDTO, VBox panelDetalles) {
        logger.info("📋 Mostrando detalles del curso: '{}'", cursoDTO.getTitulo());
        
        // Limpiar el panel de detalles
        panelDetalles.getChildren().clear();
        
        try {
            // Crear contenido detallado directamente desde CursoDTO
            VBox contenidoDetallado = crearContenidoDetallado(cursoDTO);
            panelDetalles.getChildren().add(contenidoDetallado);
            
        } catch (Exception e) {
            logger.error("❌ Error al mostrar detalles del curso: {}", e.getMessage());
            mostrarErrorCargaCurso(panelDetalles, cursoDTO);
        }
    }
    
    private VBox crearContenidoDetallado(CursoDTO cursoDTO) {
        VBox contenido = new VBox(20);
        
        // Sección 1: Información básica del curso
        VBox infoBasica = crearSeccionInfoBasica(cursoDTO);
        
        // Sección 2: Estadísticas generales
        VBox estadisticas = crearSeccionEstadisticas(cursoDTO);
        
        // Sección 3: Lista de bloques
        VBox listaBloques = crearSeccionBloques(cursoDTO);
        
        contenido.getChildren().addAll(infoBasica, estadisticas, listaBloques);
        return contenido;
    }
    
    private VBox crearSeccionInfoBasica(CursoDTO cursoDTO) {
        VBox seccion = new VBox(10);
        seccion.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15px; -fx-background-radius: 8px;");
        
        // Título de la sección
        Label tituloSeccion = new Label("📚 INFORMACIÓN BÁSICA");
        tituloSeccion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tituloSeccion.setTextFill(Color.web("#2c3e50"));
        
        // Título del curso
        Label labelTitulo = new Label("🎯 Título: " + cursoDTO.getTitulo());
        labelTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        labelTitulo.setTextFill(Color.web("#2c3e50"));
        
        // Descripción
        Label labelDescripcion = new Label("📝 Descripción:");
        labelDescripcion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        labelDescripcion.setTextFill(Color.web("#7f8c8d"));
        
        TextArea textAreaDescripcion = new TextArea(cursoDTO.getDescripcion());
        textAreaDescripcion.setWrapText(true);
        textAreaDescripcion.setEditable(false);
        textAreaDescripcion.setPrefRowCount(3);
        textAreaDescripcion.setMaxHeight(80);
        textAreaDescripcion.setStyle(
            "-fx-text-fill: #7f8c8d; " +
            "-fx-font-size: 12px; " +
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-text-alignment: justify; " +
            "-fx-padding: 8px;"
        );
        
        // ID del curso
        Label labelId = new Label("🆔 ID: " + cursoDTO.getId());
        labelId.setFont(Font.font("Segoe UI", 12));
        labelId.setTextFill(Color.web("#27ae60"));
        
        // Nombre del archivo
        Label labelArchivo = new Label("📁 Archivo: " + cursoDTO.getNombreArchivo());
        labelArchivo.setFont(Font.font("Segoe UI", 12));
        labelArchivo.setTextFill(Color.web("#7f8c8d"));
        
        seccion.getChildren().addAll(tituloSeccion, labelTitulo, labelDescripcion, textAreaDescripcion, labelId, labelArchivo);
        return seccion;
    }
    
    private VBox crearSeccionEstadisticas(CursoDTO cursoDTO) {
        VBox seccion = new VBox(10);
        seccion.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 15px; -fx-background-radius: 8px;");
        
        // Título de la sección
        Label tituloSeccion = new Label("📊 ESTADÍSTICAS GENERALES");
        tituloSeccion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tituloSeccion.setTextFill(Color.web("#2c3e50"));
        
        // Total de bloques
        int totalBloques = cursoDTO.getTotalBloques();
        Label labelBloques = new Label("📦 Total de bloques: " + totalBloques);
        labelBloques.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        labelBloques.setTextFill(Color.web("#27ae60"));
        
        // Total de preguntas
        int totalPreguntas = cursoDTO.getTotalPreguntas();
        Label labelPreguntas = new Label("❓ Total de preguntas: " + totalPreguntas);
        labelPreguntas.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        labelPreguntas.setTextFill(Color.web("#27ae60"));
        
        seccion.getChildren().addAll(tituloSeccion, labelBloques, labelPreguntas);
        return seccion;
    }
    
    private VBox crearSeccionBloques(CursoDTO cursoDTO) {
        VBox seccion = new VBox(10);
        seccion.setStyle("-fx-background-color: #fff3cd; -fx-padding: 15px; -fx-background-radius: 8px;");
        
        // Título de la sección
        Label tituloSeccion = new Label("📋 BLOQUES DE CONTENIDO");
        tituloSeccion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tituloSeccion.setTextFill(Color.web("#2c3e50"));
        
        VBox listaBloques = new VBox(8);
        
        for (int i = 0; i < cursoDTO.getBloques().size(); i++) {
            com.kursor.yaml.dto.BloqueDTO bloqueDTO = cursoDTO.getBloques().get(i);
            VBox bloqueItem = crearItemBloque(bloqueDTO, i + 1);
            listaBloques.getChildren().add(bloqueItem);
        }
        
        seccion.getChildren().addAll(tituloSeccion, listaBloques);
        return seccion;
    }
    
    private VBox crearItemBloque(com.kursor.yaml.dto.BloqueDTO bloqueDTO, int numero) {
        VBox item = new VBox(5);
        item.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-background-radius: 5px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");
        
        // Título del bloque
        Label labelTitulo = new Label("🎯 Bloque " + numero + ": " + bloqueDTO.getTitulo());
        labelTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        labelTitulo.setTextFill(Color.web("#2c3e50"));
        
        // Tipo de preguntas
        Label labelTipo = new Label("   📝 Tipo: " + bloqueDTO.getTipo() + " (" + bloqueDTO.getTotalPreguntas() + " preguntas)");
        labelTipo.setFont(Font.font("Segoe UI", 11));
        labelTipo.setTextFill(Color.web("#7f8c8d"));
        
        item.getChildren().addAll(labelTitulo, labelTipo);
        return item;
    }
    
    private void mostrarErrorCargaCurso(VBox panelDetalles, CursoDTO cursoDTO) {
        panelDetalles.getChildren().clear();
        
        Label errorLabel = new Label("❌ Error al cargar los detalles del curso");
        errorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        errorLabel.setTextFill(Color.RED);
        errorLabel.setAlignment(Pos.CENTER);
        
        Label infoLabel = new Label("Solo se muestra información básica del curso");
        infoLabel.setFont(Font.font("Segoe UI", 12));
        infoLabel.setTextFill(Color.GRAY);
        infoLabel.setAlignment(Pos.CENTER);
        
        // Mostrar información básica disponible
        VBox infoBasica = new VBox(10);
        infoBasica.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15px; -fx-background-radius: 8px;");
        
        Label titulo = new Label("🎯 Título: " + cursoDTO.getTitulo());
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        Label descripcion = new Label("📝 Descripción: " + cursoDTO.getDescripcion());
        descripcion.setFont(Font.font("Segoe UI", 12));
        descripcion.setWrapText(true);
        
        Label id = new Label("🆔 ID: " + cursoDTO.getId());
        id.setFont(Font.font("Segoe UI", 12));
        
        infoBasica.getChildren().addAll(titulo, descripcion, id);
        
        panelDetalles.getChildren().addAll(errorLabel, infoLabel, infoBasica);
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

    private void mostrarInformacionFilaSeleccionada() {
        logger.info("🔍 Mostrando información de la fila seleccionada en ListView");
        
        if (listViewCursos == null) {
            logger.warn("⚠️ ListView no está disponible");
            mostrarAlert("⚠️ Error", "No hay lista de cursos disponible");
            return;
        }
        
        CursoDTO seleccionado = listViewCursos.getSelectionModel().getSelectedItem();
        int indiceSeleccionado = listViewCursos.getSelectionModel().getSelectedIndex();
        
        if (seleccionado == null) {
            logger.warn("⚠️ No hay fila seleccionada en el ListView");
            mostrarAlert("⚠️ Sin selección", "No hay ningún curso seleccionado en la lista.\n\nPor favor, selecciona un curso haciendo clic en él.");
            return;
        }

        // Crear mensaje con información de la fila seleccionada
        String mensaje = String.format(
            "📋 INFORMACIÓN DE LA FILA SELECCIONADA\n\n" +
            "📍 Número de fila: %d\n\n" +
            "📚 Título: %s\n" +
            "📝 Descripción: %s\n" +
            "🆔 ID: %s",
            indiceSeleccionado + 1,
            seleccionado.getTitulo(),
            seleccionado.getDescripcion(),
            seleccionado.getId()
        );
        
        logger.info("✅ Mostrando información de fila %d: '%s'", indiceSeleccionado + 1, seleccionado.getTitulo());
        mostrarAlert("🔍 Información de Fila Seleccionada", mensaje);
    }

    /**
     * Inicia la ejecución del curso seleccionado.
     * 
     * <p>Este método verifica si hay un curso seleccionado y, si es así,
     * inicia el flujo de ejecución usando el CursoInterfaceController.</p>
     */
    private void iniciarCursoSeleccionado() {
        CursoDTO cursoSeleccionado = listViewCursos.getSelectionModel().getSelectedItem();
        
        if (cursoSeleccionado == null) {
            logger.warn("No hay curso seleccionado para comenzar");
            mostrarAlert("Selección requerida", "Por favor, selecciona un curso para comenzar.");
            return;
        }
        
        logger.info("Iniciando curso seleccionado: " + cursoSeleccionado.getTitulo());
        
        try {
            // Usar el controlador para iniciar el curso
            boolean iniciado = cursoController.iniciarCurso(cursoSeleccionado);
            
            if (iniciado) {
                logger.info("Curso iniciado correctamente: " + cursoSeleccionado.getTitulo());
            } else {
                logger.info("Usuario canceló la ejecución del curso: " + cursoSeleccionado.getTitulo());
            }
            
        } catch (Exception e) {
            logger.error("Error al iniciar curso: " + cursoSeleccionado.getTitulo(), e);
            mostrarAlert("Error", "No se pudo iniciar el curso: " + e.getMessage());
        }
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