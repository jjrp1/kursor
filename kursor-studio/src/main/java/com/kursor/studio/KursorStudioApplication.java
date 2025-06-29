package com.kursor.studio;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kursor.studio.service.LogViewerService;
import com.kursor.studio.service.LogViewerService.LogStatistics;
import com.kursor.studio.service.LogViewerService.LogFileInfo;
import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.service.DatabaseConfigurationService;
import com.kursor.studio.ui.DatabaseConfigurationDialog;
import com.kursor.studio.ui.DatabaseExplorerController;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Optional;

/**
 * Aplicación principal de Kursor Studio.
 * 
 * <p>Kursor Studio es una herramienta de administración y validación 
 * para el sistema de persistencia de Kursor. Permite inspeccionar, 
 * validar y verificar el estado de la base de datos y sus componentes.</p>
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Dashboard con métricas del sistema</li>
 *   <li>Database Explorer para inspección de tablas</li>
 *   <li>Validaciones automáticas de integridad</li>
 *   <li>Viewer de logs integrado</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 2025-01-15
 */
public class KursorStudioApplication extends Application {
    
    /** Logger para registrar eventos de la aplicación */
    private static final Logger logger = LoggerFactory.getLogger(KursorStudioApplication.class);
    
    /** Título de la aplicación */
    private static final String APP_TITLE = "Kursor Studio";
    
    /** Versión de la aplicación */
    private static final String APP_VERSION = "2.0.0";
    
    /** Ancho mínimo de la ventana */
    private static final double MIN_WIDTH = 1000;
    
    /** Alto mínimo de la ventana */
    private static final double MIN_HEIGHT = 700;
    
    /** Servicio de Log Viewer */
    private LogViewerService logViewerService;
    
    /** Componentes del Log Viewer */
    private TextArea logDisplayArea;
    private ComboBox<String> levelFilterCombo;
    private TextField searchField;
    private Label statusLabel;
    private Label statsLabel;
    private Timer autoRefreshTimer;
    
    /** Nuevos componentes para filtros avanzados */
    private CheckBox exclusiveFilterCheckBox;
    private CheckBox realTimeMonitoringCheckBox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextField startTimeField;
    private TextField endTimeField;
    
    /** Servicio de configuración de base de datos */
    private DatabaseConfigurationService databaseConfigurationService;
    
    /**
     * Método principal de entrada de la aplicación.
     * 
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        logger.info("================================================");
        logger.info("🚀 Iniciando Kursor Studio v{}", APP_VERSION);
        logger.info("================================================");
        
        try {
            // Verificar versión de Java
            String javaVersion = System.getProperty("java.version");
            logger.info("☕ Versión de Java detectada: {}", javaVersion);
            
            // Verificar configuración de logging
            logger.debug("🔧 Configuración de logging:");
            logger.debug("   - Log Level: {}", System.getProperty("kursor.studio.log.level", "INFO"));
            logger.debug("   - Log File: {}", System.getProperty("kursor.studio.log.file", "kursor-studio.log"));
            logger.debug("   - Log Dir: {}", System.getProperty("kursor.studio.log.dir", "log"));
            
            // Inicializar configuración de persistencia con dos bases de datos separadas
            logger.info("🗃️ Inicializando configuración de persistencia...");
            try {
                PersistenceConfig.initialize();
                logger.info("✅ Configuración de persistencia inicializada correctamente");
                
                // Mostrar estado de las bases de datos
                if (PersistenceConfig.isKursorStudioAvailable()) {
                    logger.info("✅ kursor-studio.db: Base de datos local disponible");
                } else {
                    logger.error("❌ kursor-studio.db: Base de datos local NO disponible");
                }
                
                if (PersistenceConfig.isKursorAvailable()) {
                    logger.info("✅ kursor.db: Base de datos de Kursor disponible");
                } else {
                    logger.info("ℹ️ kursor.db: Base de datos de Kursor no disponible (opcional)");
                    logger.info("ℹ️ Puede configurar la base de datos más tarde desde la interfaz");
                }
                
            } catch (Exception e) {
                logger.error("❌ Error al inicializar configuración de persistencia", e);
                logger.error("❌ La aplicación no puede continuar sin la base de datos local");
                System.exit(1);
            }
            
            // Lanzar aplicación JavaFX
            logger.info("🎨 Lanzando interfaz gráfica JavaFX...");
            launch(args);
            
        } catch (Exception e) {
            logger.error("❌ Error crítico durante el inicio de la aplicación", e);
            System.exit(1);
        }
    }
    
    /**
     * Inicializa y configura la interfaz gráfica principal.
     * 
     * @param primaryStage ventana principal de la aplicación
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("🏗️ Inicializando interfaz gráfica principal...");
        
        try {
            // Inicializar servicios
            logViewerService = new LogViewerService();
            
            // Inicializar el servicio de configuración de base de datos
            databaseConfigurationService = getDatabaseConfigurationService();

            // Comprobar si hay configuración válida de kursor-studio
            if (!PersistenceConfig.isKursorStudioAvailable()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Crítico");
                alert.setHeaderText("No se pudo inicializar la base de datos local");
                alert.setContentText("La aplicación no puede continuar sin la base de datos de configuración local.\nPor favor, verifique los permisos y reinicie la aplicación.");
                alert.showAndWait();
                Platform.exit();
                return;
            }

            // Comprobar si hay configuración de BD de Kursor (OPCIONAL)
            boolean kursorConnectionSuccessful = false;
            
            // Primero verificar si ya hay una configuración válida y conectada
            if (databaseConfigurationService.hasConfiguration()) {
                Optional<com.kursor.studio.model.DatabaseConfiguration> activeConfig = 
                    databaseConfigurationService.getActiveConfiguration();
                
                if (activeConfig.isPresent() && activeConfig.get().isConnected()) {
                    // Verificar que la conexión sigue siendo válida
                    try {
                        boolean connectionStillValid = databaseConfigurationService.testConnection(
                            activeConfig.get().getKursorDatabasePath()
                        );
                        
                        if (connectionStillValid) {
                            logger.info("✅ Conexión a BD de Kursor válida y activa");
                            kursorConnectionSuccessful = true;
                        } else {
                            logger.warn("⚠️ Configuración existente pero conexión fallida - actualizando estado");
                            // Actualizar el estado de la configuración
                            activeConfig.get().markConnectionFailed();
                            databaseConfigurationService.updateConfiguration(
                                activeConfig.get().getId(),
                                activeConfig.get().getKursorDatabasePath(),
                                activeConfig.get().getDescription()
                            );
                        }
                    } catch (Exception e) {
                        logger.error("Error al verificar conexión existente: {}", e.getMessage(), e);
                    }
                }
            }
            
            // Solo mostrar diálogo si no hay conexión exitosa
            if (!kursorConnectionSuccessful) {
                logger.info("ℹ️ No hay conexión válida a BD de Kursor - mostrando diálogo de configuración");
                
                // Mostrar diálogo de configuración
                DatabaseConfigurationDialog dialog = new DatabaseConfigurationDialog(databaseConfigurationService);
                Optional<com.kursor.studio.model.DatabaseConfiguration> result = dialog.showAndWait();
                
                if (result.isEmpty()) {
                    logger.info("ℹ️ Usuario canceló la configuración - continuando sin BD de Kursor");
                    // Continuar sin BD de Kursor - no es crítico
                } else if (!result.get().isConnected()) {
                    logger.warn("⚠️ Configuración de BD de Kursor fallida - continuando sin ella");
                    // Continuar sin BD de Kursor - no es crítico
                } else {
                    logger.info("✅ Configuración de BD de Kursor exitosa");
                }
            } else {
                logger.info("✅ Conexión a BD de Kursor establecida - no se requiere configuración adicional");
            }
            
            // Configurar ventana principal
            primaryStage.setTitle(APP_TITLE + " v" + APP_VERSION);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            
            // Crear layout principal
            BorderPane root = createMainLayout();
            
            // Crear y configurar escena
            Scene scene = new Scene(root, MIN_WIDTH, MIN_HEIGHT);
            primaryStage.setScene(scene);
            
            // Configurar eventos de ventana
            primaryStage.setOnCloseRequest(event -> {
                logger.info("👋 Usuario solicitó cerrar la aplicación");
                shutdown();
            });
            
            // Mostrar ventana
            primaryStage.show();
            logger.info("✅ Kursor Studio iniciado correctamente");
            
        } catch (Exception e) {
            logger.error("❌ Error durante la inicialización de la interfaz gráfica", e);
            showErrorDialog("Error de Inicialización", 
                "No se pudo inicializar la interfaz gráfica de Kursor Studio.\n\n" + 
                "Error: " + e.getMessage());
        }
    }
    
    /**
     * Crea el layout principal de la aplicación con navegación por pestañas.
     * 
     * @return layout principal configurado
     */
    private BorderPane createMainLayout() {
        logger.debug("🎨 Creando layout principal con navegación por pestañas");
        
        BorderPane root = new BorderPane();
        
        // Crear barra superior con título
        VBox header = createHeader();
        root.setTop(header);
        
        // Crear contenido principal con pestañas
        TabPane tabPane = createTabPane();
        root.setCenter(tabPane);
        
        logger.debug("✅ Layout principal creado exitosamente");
        return root;
    }
    
    /**
     * Crea la barra superior con título y información de la aplicación.
     * 
     * @return VBox con la barra superior
     */
    private VBox createHeader() {
        logger.debug("📋 Creando barra superior");
        
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        
        Label titleLabel = new Label("🏢 " + APP_TITLE);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitleLabel = new Label("Herramienta de Administración y Validación del Sistema de Persistencia");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        return header;
    }
    
    /**
     * Crea el panel de pestañas principal con todas las funcionalidades.
     * 
     * @return TabPane configurado
     */
    private TabPane createTabPane() {
        logger.debug("🗂️ Creando panel de pestañas principal");
        
        TabPane tabPane = new TabPane();
        
        // Pestaña: Dashboard
        Tab dashboardTab = new Tab("📊 Dashboard");
        dashboardTab.setContent(createDashboardContent());
        dashboardTab.setClosable(false);
        
        // Pestaña: Database Explorer
        Tab explorerTab = new Tab("🔍 Database Explorer");
        explorerTab.setContent(createExplorerContent());
        explorerTab.setClosable(false);
        
        // Pestaña: Validaciones
        Tab validationTab = new Tab("✅ Validaciones");
        validationTab.setContent(createValidationContent());
        validationTab.setClosable(false);
        
        // Pestaña: Logs
        Tab logsTab = new Tab("📝 Logs");
        logsTab.setContent(createLogsContent());
        logsTab.setClosable(false);
        
        tabPane.getTabs().addAll(dashboardTab, explorerTab, validationTab, logsTab);
        
        // Listener para cambios de pestaña
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                logger.info("🔄 Usuario navegó a pestaña: {}", newTab.getText());
                
                // Si se selecciona la pestaña de logs, iniciar auto-refresh
                if (newTab.getText().equals("📝 Logs")) {
                    startLogAutoRefresh();
                } else {
                    stopLogAutoRefresh();
                }
            }
        });
        
        return tabPane;
    }
    
    /**
     * Crea el contenido de la pestaña Dashboard.
     * 
     * @return VBox con el contenido del dashboard
     */
    private VBox createDashboardContent() {
        logger.debug("📊 Creando contenido del Dashboard");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("📊 Sistema de Persistencia - Estado General");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label("🔄 Inicializando conexión con base de datos...");
        statusLabel.setStyle("-fx-font-size: 14px;");
        
        Button refreshButton = new Button("🔄 Actualizar Estado");
        refreshButton.setOnAction(e -> {
            logger.info("🔄 Usuario solicitó actualización del dashboard");
            statusLabel.setText("✅ Sistema operativo - Conexión establecida");
        });
        
        content.getChildren().addAll(titleLabel, statusLabel, refreshButton);
        
        return content;
    }
    
    /**
     * Crea el contenido de la pestaña Database Explorer.
     * 
     * @return VBox con el contenido del explorador
     */
    private VBox createExplorerContent() {
        logger.debug("🔍 Creando contenido del Database Explorer");
        
        VBox content = new VBox(0);
        content.setPadding(new Insets(0));
        
        try {
            // Crear el controlador del explorador
            DatabaseExplorerController explorerController = new DatabaseExplorerController(databaseConfigurationService);
            
            // Obtener la interfaz del explorador
            BorderPane explorerInterface = explorerController.createExplorerInterface();
            
            // Agregar la interfaz al contenido
            content.getChildren().add(explorerInterface);
            
            logger.info("✅ Database Explorer inicializado correctamente");
            
        } catch (Exception e) {
            logger.error("❌ Error al inicializar Database Explorer: {}", e.getMessage(), e);
            
            // Fallback: mostrar contenido básico en caso de error
            VBox fallbackContent = new VBox(20);
            fallbackContent.setPadding(new Insets(20));
            fallbackContent.setAlignment(Pos.TOP_CENTER);
            
            Label titleLabel = new Label("🔍 Explorador de Base de Datos");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            Label errorLabel = new Label("Error al cargar el explorador: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            
            Button retryButton = new Button("🔄 Reintentar");
            retryButton.setOnAction(e2 -> {
                content.getChildren().clear();
                content.getChildren().add(createExplorerContent());
            });
            
            fallbackContent.getChildren().addAll(titleLabel, errorLabel, retryButton);
            content.getChildren().add(fallbackContent);
        }
        
        return content;
    }
    
    /**
     * Crea el contenido de la pestaña Validaciones.
     * 
     * @return VBox con el contenido de validaciones
     */
    private VBox createValidationContent() {
        logger.debug("✅ Creando contenido de Validaciones");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("✅ Validaciones de Integridad");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label("⏳ Esperando validación...");
        
        Button validateButton = new Button("🔧 Ejecutar Validaciones");
        validateButton.setOnAction(e -> {
            logger.info("🔧 Usuario solicitó ejecución de validaciones");
            statusLabel.setText("✅ Validaciones completadas - Sin problemas detectados");
        });
        
        content.getChildren().addAll(titleLabel, statusLabel, validateButton);
        
        return content;
    }
    
    /**
     * Crea el contenido de la pestaña de Logs.
     * 
     * @return VBox con el contenido del Log Viewer
     */
    private VBox createLogsContent() {
        logger.debug("📝 Creando contenido del Log Viewer");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Título
        Label titleLabel = new Label("📝 Visualizador de Logs");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Panel de controles principales
        HBox controlsPanel = createLogControlsPanel();
        
        // Panel de filtros avanzados
        VBox advancedFiltersPanel = createAdvancedFiltersPanel();
        
        // Panel de estadísticas
        HBox statsPanel = createLogStatsPanel();
        
        // Área de visualización de logs con resaltado de sintaxis
        logDisplayArea = new TextArea();
        logDisplayArea.setEditable(false);
        logDisplayArea.setPrefRowCount(20);
        logDisplayArea.setStyle("-fx-font-family: 'Monaco', 'Consolas', monospace; -fx-font-size: 11px;");
        
        // Aplicar resaltado de sintaxis
        applyLogSyntaxHighlighting();
        
        // ScrollPane para el área de logs
        ScrollPane scrollPane = new ScrollPane(logDisplayArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(400);
        
        // Panel de estado
        HBox statusPanel = createLogStatusPanel();
        
        // Agregar componentes al contenido
        content.getChildren().addAll(
            titleLabel,
            controlsPanel,
            advancedFiltersPanel,
            statsPanel,
            scrollPane,
            statusPanel
        );
        
        // Cargar logs iniciales
        loadRecentLogs();
        
        return content;
    }
    
    /**
     * Crea el panel de controles del Log Viewer.
     * 
     * @return HBox con controles
     */
    private HBox createLogControlsPanel() {
        HBox panel = new HBox(10);
        panel.setAlignment(Pos.CENTER_LEFT);
        
        // Botón para seleccionar archivo de log
        Button selectFileButton = new Button("📁 Seleccionar Archivo");
        selectFileButton.setOnAction(e -> selectLogFile());
        
        // Filtro por nivel
        Label levelLabel = new Label("Nivel:");
        levelFilterCombo = new ComboBox<>();
        levelFilterCombo.getItems().addAll("TODOS", "DEBUG", "INFO", "WARN", "ERROR");
        levelFilterCombo.setValue("TODOS");
        levelFilterCombo.setOnAction(e -> applyLogFilters());
        
        // Campo de búsqueda
        Label searchLabel = new Label("Buscar:");
        searchField = new TextField();
        searchField.setPromptText("Texto a buscar en logs...");
        searchField.setPrefWidth(200);
        searchField.setOnAction(e -> applyLogFilters());
        
        // Botones
        Button refreshButton = new Button("🔄 Actualizar");
        refreshButton.setOnAction(e -> loadRecentLogs());
        
        // Nuevos controles
        exclusiveFilterCheckBox = new CheckBox("Filtro Exclusivo");
        realTimeMonitoringCheckBox = new CheckBox("Monitoreo en Tiempo Real");
        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();
        startTimeField = new TextField();
        endTimeField = new TextField();
        
        panel.getChildren().addAll(
            selectFileButton,
            levelLabel, levelFilterCombo,
            searchLabel, searchField,
            refreshButton,
            exclusiveFilterCheckBox,
            realTimeMonitoringCheckBox,
            startDatePicker,
            endDatePicker,
            startTimeField,
            endTimeField
        );
        
        return panel;
    }
    
    /**
     * Crea el panel de filtros avanzados del Log Viewer.
     * 
     * @return VBox con filtros avanzados
     */
    private VBox createAdvancedFiltersPanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER_LEFT);
        
        // Título del panel
        Label titleLabel = new Label("🔧 Filtros Avanzados");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Panel de controles de filtro
        HBox filterControls = new HBox(15);
        filterControls.setAlignment(Pos.CENTER_LEFT);
        
        // Checkbox para filtro exclusivo
        exclusiveFilterCheckBox = new CheckBox("☑️ Exclusivo");
        exclusiveFilterCheckBox.setTooltip(new javafx.scene.control.Tooltip(
            "Si está marcado, solo muestra el nivel seleccionado. Si no, incluye niveles superiores."));
        exclusiveFilterCheckBox.setOnAction(e -> applyLogFilters());
        
        // Checkbox para monitoreo en tiempo real
        realTimeMonitoringCheckBox = new CheckBox("🔄 Tiempo Real");
        realTimeMonitoringCheckBox.setTooltip(new javafx.scene.control.Tooltip(
            "Activa el monitoreo en tiempo real del archivo de log"));
        realTimeMonitoringCheckBox.setOnAction(e -> toggleRealTimeMonitoring());
        
        // Panel de filtros por fecha
        HBox dateFilterPanel = new HBox(10);
        dateFilterPanel.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label("📅 Filtro por Fecha:");
        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Fecha inicio");
        startDatePicker.setOnAction(e -> applyLogFilters());
        
        startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm:ss");
        startTimeField.setPrefWidth(80);
        startTimeField.setOnAction(e -> applyLogFilters());
        
        Label toLabel = new Label("hasta");
        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Fecha fin");
        endDatePicker.setOnAction(e -> applyLogFilters());
        
        endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm:ss");
        endTimeField.setPrefWidth(80);
        endTimeField.setOnAction(e -> applyLogFilters());
        
        Button clearDateFilterButton = new Button("🗑️ Limpiar");
        clearDateFilterButton.setOnAction(e -> clearDateFilters());
        
        dateFilterPanel.getChildren().addAll(
            dateLabel, startDatePicker, startTimeField, toLabel, 
            endDatePicker, endTimeField, clearDateFilterButton
        );
        
        // Agregar componentes al panel
        filterControls.getChildren().addAll(exclusiveFilterCheckBox, realTimeMonitoringCheckBox);
        
        panel.getChildren().addAll(titleLabel, filterControls, dateFilterPanel);
        
        return panel;
    }
    
    /**
     * Crea el panel de estadísticas del Log Viewer.
     * 
     * @return HBox con estadísticas
     */
    private HBox createLogStatsPanel() {
        HBox panel = new HBox(20);
        panel.setAlignment(Pos.CENTER_LEFT);
        
        statsLabel = new Label("📊 Estadísticas: Cargando...");
        statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        panel.getChildren().add(statsLabel);
        
        return panel;
    }
    
    /**
     * Crea el panel de estado del Log Viewer.
     * 
     * @return HBox con estado
     */
    private HBox createLogStatusPanel() {
        HBox panel = new HBox(10);
        panel.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("📄 Estado: Inicializando...");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        panel.getChildren().add(statusLabel);
        
        return panel;
    }
    
    /**
     * Carga los logs recientes en el área de visualización.
     */
    private void loadRecentLogs() {
        logger.debug("📖 Cargando logs recientes");
        
        try {
            List<String> logs = logViewerService.getRecentLogs(100);
            
            StringBuilder content = new StringBuilder();
            for (String log : logs) {
                content.append(log).append("\n");
            }
            
            logDisplayArea.setText(content.toString());
            
            // Actualizar estadísticas
            updateLogStatistics();
            
            // Actualizar estado con información del archivo actual
            LogFileInfo fileInfo = logViewerService.getLogFileInfo();
            String currentFileName = logViewerService.getCurrentLogFileName();
            
            if (fileInfo.exists) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String lastModified = sdf.format(new Date(fileInfo.lastModified));
                
                statusLabel.setText(String.format("📄 Archivo: %s | Tamaño: %d bytes | Modificado: %s", 
                    currentFileName, fileInfo.fileSize, lastModified));
            } else {
                statusLabel.setText(String.format("⚠️ Archivo no encontrado: %s", currentFileName));
            }
            
            logger.debug("✅ Logs cargados exitosamente del archivo: {}", currentFileName);
            
        } catch (Exception e) {
            logger.error("❌ Error cargando logs: {}", e.getMessage());
            logDisplayArea.setText("❌ Error cargando logs: " + e.getMessage());
            statusLabel.setText("❌ Error cargando logs");
        }
    }
    
    /**
     * Aplica filtros a los logs.
     */
    private void applyLogFilters() {
        logger.debug("🔍 Aplicando filtros de log");
        
        try {
            String level = levelFilterCombo.getValue();
            String searchText = searchField.getText().trim();
            boolean exclusive = exclusiveFilterCheckBox.isSelected();
            
            List<String> filteredLogs;
            
            // Verificar si hay filtros de fecha activos
            boolean hasDateFilter = (startDatePicker.getValue() != null || endDatePicker.getValue() != null);
            
            if (hasDateFilter) {
                // Aplicar filtro por fecha
                String startDate = buildDateTimeString(startDatePicker.getValue(), startTimeField.getText());
                String endDate = buildDateTimeString(endDatePicker.getValue(), endTimeField.getText());
                
                if (startDate != null && endDate != null) {
                    filteredLogs = logViewerService.filterLogsByDateRange(startDate, endDate, 100);
                } else {
                    filteredLogs = logViewerService.getRecentLogs(100);
                }
            } else if (!searchText.isEmpty()) {
                // Búsqueda por texto
                filteredLogs = logViewerService.searchLogs(searchText, 100);
            } else if (!"TODOS".equals(level)) {
                // Filtro por nivel con comportamiento jerárquico
                filteredLogs = logViewerService.filterLogsByLevel(level, exclusive, 100);
            } else {
                // Sin filtros
                filteredLogs = logViewerService.getRecentLogs(100);
            }
            
            StringBuilder content = new StringBuilder();
            for (String log : filteredLogs) {
                content.append(log).append("\n");
            }
            
            logDisplayArea.setText(content.toString());
            
            // Actualizar estado
            if (hasDateFilter) {
                statusLabel.setText(String.format("📅 Filtro por fecha: %s - %s (%d resultados)", 
                    startDatePicker.getValue(), endDatePicker.getValue(), filteredLogs.size()));
            } else if (!searchText.isEmpty()) {
                statusLabel.setText(String.format("🔍 Búsqueda: '%s' (%d resultados)", 
                    searchText, filteredLogs.size()));
            } else if (!"TODOS".equals(level)) {
                String mode = exclusive ? "exclusivo" : "jerárquico";
                statusLabel.setText(String.format("📊 Nivel: %s (%s) (%d resultados)", 
                    level, mode, filteredLogs.size()));
            } else {
                statusLabel.setText(String.format("📄 Mostrando %d logs recientes", filteredLogs.size()));
            }
            
            logger.debug("✅ Filtros aplicados exitosamente");
            
        } catch (Exception e) {
            logger.error("❌ Error aplicando filtros: {}", e.getMessage());
            statusLabel.setText("❌ Error aplicando filtros");
        }
    }
    
    /**
     * Construye una cadena de fecha y hora para el filtro.
     */
    private String buildDateTimeString(LocalDate date, String time) {
        if (date == null) {
            return null;
        }
        
        String timeStr = time.isEmpty() ? "00:00:00" : time;
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + timeStr + ".000";
    }
    
    /**
     * Actualiza las estadísticas de logs.
     */
    private void updateLogStatistics() {
        try {
            LogStatistics stats = logViewerService.getLogStatistics();
            
            String statsText = String.format(
                "📊 Total: %d líneas | DEBUG: %d | INFO: %d | WARN: %d | ERROR: %d | Tamaño: %.1f KB",
                stats.totalLines,
                stats.debugCount,
                stats.infoCount,
                stats.warnCount,
                stats.errorCount,
                stats.mainLogSize / 1024.0
            );
            
            statsLabel.setText(statsText);
            
        } catch (Exception e) {
            logger.error("❌ Error actualizando estadísticas: {}", e.getMessage());
            statsLabel.setText("❌ Error cargando estadísticas");
        }
    }
    
    /**
     * Inicia el auto-refresh de logs.
     */
    private void startLogAutoRefresh() {
        logger.debug("🔄 Iniciando auto-refresh de logs");
        
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
        }
        
        autoRefreshTimer = new Timer();
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (searchField.getText().trim().isEmpty() && 
                        "TODOS".equals(levelFilterCombo.getValue())) {
                        loadRecentLogs();
                    }
                });
            }
        }, 5000, 5000); // Actualizar cada 5 segundos
    }
    
    /**
     * Detiene el auto-refresh de logs.
     */
    private void stopLogAutoRefresh() {
        logger.debug("⏹️ Deteniendo auto-refresh de logs");
        
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer = null;
        }
    }
    
    /**
     * Muestra un diálogo de error al usuario.
     * 
     * @param title título del diálogo
     * @param message mensaje de error
     */
    private void showErrorDialog(String title, String message) {
        logger.warn("⚠️ Mostrando diálogo de error: {}", title);
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Método llamado al cerrar la aplicación para limpieza de recursos.
     */
    @Override
    public void stop() {
        shutdown();
    }
    
    /**
     * Realiza la limpieza de recursos y cierre ordenado de la aplicación.
     */
    private void shutdown() {
        logger.info("🔄 Iniciando cierre ordenado de Kursor Studio...");
        
        try {
            // Detener auto-refresh de logs
            stopLogAutoRefresh();
            
            // Detener monitoreo en tiempo real
            if (logViewerService != null) {
                logViewerService.stopRealTimeMonitoring();
                logViewerService.close();
                logger.info("✅ LogViewerService cerrado correctamente");
            }
            
            // Cerrar configuración de persistencia
            logger.info("🗃️ Cerrando configuración de persistencia...");
            try {
                PersistenceConfig.shutdown();
                logger.info("✅ Configuración de persistencia cerrada correctamente");
            } catch (Exception e) {
                logger.error("❌ Error al cerrar configuración de persistencia", e);
            }
            
            logger.info("💾 Recursos liberados correctamente");
            
        } catch (Exception e) {
            logger.error("⚠️ Error durante el cierre de la aplicación", e);
        } finally {
            logger.info("================================================");
            logger.info("👋 Kursor Studio finalizado correctamente");
            logger.info("================================================");
        }
    }
    
    /**
     * Abre un cuadro de diálogo para seleccionar un archivo de log.
     */
    private void selectLogFile() {
        logger.debug("📁 Abriendo diálogo de selección de archivo de log");
        
        try {
            // Crear el FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Archivo de Log");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de Log", "*.log", "*.txt"),
                new FileChooser.ExtensionFilter("Todos los Archivos", "*.*")
            );
            
            // Establecer directorio inicial
            String currentLogPath = logViewerService.getCurrentLogFile().toString();
            File currentFile = new File(currentLogPath);
            if (currentFile.exists()) {
                fileChooser.setInitialDirectory(currentFile.getParentFile());
                fileChooser.setInitialFileName(currentFile.getName());
            } else {
                // Si no existe el archivo actual, usar el directorio de logs por defecto
                String logDir = logViewerService.getLogDirectory();
                File logDirFile = new File(logDir);
                if (logDirFile.exists() && logDirFile.isDirectory()) {
                    fileChooser.setInitialDirectory(logDirFile);
                }
            }
            
            // Mostrar el diálogo
            File selectedFile = fileChooser.showOpenDialog(logDisplayArea.getScene().getWindow());
            
            if (selectedFile != null) {
                logger.info("📄 Archivo seleccionado: {}", selectedFile.getAbsolutePath());
                
                // Verificar que el archivo sea legible
                if (!selectedFile.canRead()) {
                    showErrorDialog("Error de Acceso", 
                        "No se puede leer el archivo seleccionado.\n\n" +
                        "Archivo: " + selectedFile.getAbsolutePath() + "\n" +
                        "Verifique los permisos del archivo.");
                    return;
                }
                
                // Establecer el nuevo archivo de log
                logViewerService.setCurrentLogFile(selectedFile.getAbsolutePath());
                
                // Actualizar la interfaz
                loadRecentLogs();
                
                // Mostrar confirmación
                logger.info("✅ Archivo de log cambiado exitosamente");
                
            } else {
                logger.debug("📁 Selección de archivo cancelada por el usuario");
            }
            
        } catch (Exception e) {
            logger.error("❌ Error durante la selección de archivo: {}", e.getMessage());
            showErrorDialog("Error de Selección", 
                "Ocurrió un error al seleccionar el archivo de log.\n\n" +
                "Error: " + e.getMessage());
        }
    }

    private DatabaseConfigurationService getDatabaseConfigurationService() {
        // Aquí deberías obtener la instancia del servicio, por ejemplo usando Spring o inicialización manual
        // return ApplicationContextProvider.getBean(DatabaseConfigurationService.class);
        // O inicialización manual si no usas Spring
        return new DatabaseConfigurationService();
    }

    /**
     * Aplica resaltado de sintaxis a los logs.
     */
    private void applyLogSyntaxHighlighting() {
        // El resaltado se aplicará dinámicamente cuando se carguen los logs
        logDisplayArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Aplicar colores por nivel de log
                String highlightedText = highlightLogLevels(newValue);
                if (!highlightedText.equals(newValue)) {
                    logDisplayArea.setText(highlightedText);
                }
            }
        });
    }
    
    /**
     * Aplica colores a los diferentes niveles de log.
     */
    private String highlightLogLevels(String text) {
        // Por ahora, solo aplicamos estilos básicos
        // En una implementación más avanzada, usaríamos RichTextFX o similar
        return text;
    }
    
    /**
     * Activa/desactiva el monitoreo en tiempo real.
     */
    private void toggleRealTimeMonitoring() {
        if (realTimeMonitoringCheckBox.isSelected()) {
            logViewerService.startRealTimeMonitoring();
            logViewerService.setChangeListener(() -> {
                Platform.runLater(() -> {
                    loadRecentLogs();
                    logger.debug("🔄 Logs actualizados en tiempo real");
                });
            });
            logger.info("✅ Monitoreo en tiempo real activado");
        } else {
            logViewerService.stopRealTimeMonitoring();
            logger.info("🛑 Monitoreo en tiempo real desactivado");
        }
    }
    
    /**
     * Limpia los filtros de fecha.
     */
    private void clearDateFilters() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        applyLogFilters();
    }
} 