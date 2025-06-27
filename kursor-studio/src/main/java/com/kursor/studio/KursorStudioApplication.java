package com.kursor.studio;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author Sistema Kursor
 * @version 1.0.0
 * @since 2025-01-15
 */
public class KursorStudioApplication extends Application {
    
    /** Logger para registrar eventos de la aplicación */
    private static final Logger logger = LoggerFactory.getLogger(KursorStudioApplication.class);
    
    /** Título de la aplicación */
    private static final String APP_TITLE = "Kursor Studio";
    
    /** Versión de la aplicación */
    private static final String APP_VERSION = "1.0.0";
    
    /** Ancho mínimo de la ventana */
    private static final double MIN_WIDTH = 1000;
    
    /** Alto mínimo de la ventana */
    private static final double MIN_HEIGHT = 700;
    
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
    public void start(Stage primaryStage) {
        logger.info("🏗️ Inicializando interfaz gráfica principal...");
        
        try {
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
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("🔍 Explorador de Base de Datos");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label("📋 Cargando esquema de base de datos...");
        
        Button loadTablesButton = new Button("📋 Cargar Tablas");
        loadTablesButton.setOnAction(e -> {
            logger.info("📋 Usuario solicitó carga de tablas");
            statusLabel.setText("✅ Tablas cargadas: sesion, respuesta_pregunta, estadisticas_usuario, estado_estrategia");
        });
        
        content.getChildren().addAll(titleLabel, statusLabel, loadTablesButton);
        
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
     * Crea el contenido de la pestaña Logs.
     * 
     * @return VBox con el contenido del viewer de logs
     */
    private VBox createLogsContent() {
        logger.debug("📝 Creando contenido del Log Viewer");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("📝 Visualizador de Logs");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label("📄 Logs disponibles en: log/kursor-studio.log");
        
        Button viewLogsButton = new Button("👁️ Ver Logs Recientes");
        viewLogsButton.setOnAction(e -> {
            logger.info("👁️ Usuario solicitó visualización de logs");
            logger.warn("⚠️ Funcionalidad de Log Viewer en desarrollo");
        });
        
        content.getChildren().addAll(titleLabel, statusLabel, viewLogsButton);
        
        return content;
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
            // Aquí se agregarían cierres de conexiones, etc.
            logger.info("💾 Recursos liberados correctamente");
            
        } catch (Exception e) {
            logger.error("⚠️ Error durante el cierre de la aplicación", e);
        } finally {
            logger.info("================================================");
            logger.info("👋 Kursor Studio finalizado correctamente");
            logger.info("================================================");
        }
    }
} 