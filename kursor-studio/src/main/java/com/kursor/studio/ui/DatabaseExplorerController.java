package com.kursor.studio.ui;

import com.kursor.studio.config.PersistenceConfig;
import com.kursor.studio.service.DatabaseConfigurationService;
import com.kursor.studio.service.DatabaseInspectorService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para el explorador de base de datos.
 * 
 * <p>Este controlador maneja la navegación por tablas y la visualización de datos
 * de la base de datos de Kursor.</p>
 */
public class DatabaseExplorerController {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExplorerController.class);
    
    private final DatabaseConfigurationService configurationService;
    private final DatabaseInspectorService inspectorService;
    
    // Componentes de la UI
    private TreeView<String> tableTreeView;
    private TableView<Object[]> dataTableView;
    private Label statusLabel;
    private Label infoLabel;
    private Button refreshButton;
    private Button configureButton;
    
    // Datos
    private ObservableList<Object[]> tableData = FXCollections.observableArrayList();
    private List<String> tableNames = List.of();
    
    public DatabaseExplorerController(DatabaseConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.inspectorService = new DatabaseInspectorService();
    }
    
    /**
     * Crea la interfaz principal del explorador de base de datos.
     */
    public BorderPane createExplorerInterface() {
        logger.info("🔍 Creando interfaz del Database Explorer");
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));
        
        // Panel superior con información y controles
        VBox topPanel = createTopPanel();
        mainLayout.setTop(topPanel);
        
        // Panel izquierdo con árbol de tablas
        VBox leftPanel = createTableTreePanel();
        mainLayout.setLeft(leftPanel);
        
        // Panel central con datos
        VBox centerPanel = createDataPanel();
        mainLayout.setCenter(centerPanel);
        
        // Panel inferior con estado
        HBox bottomPanel = createBottomPanel();
        mainLayout.setBottom(bottomPanel);
        
        // Cargar datos iniciales
        loadTableStructure();
        
        return mainLayout;
    }
    
    /**
     * Crea el panel superior con información y controles.
     */
    private VBox createTopPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        // Título
        Label titleLabel = new Label("🔍 Explorador de Base de Datos de Kursor");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Información de conexión
        infoLabel = new Label("Estado: Verificando conexión...");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        // Panel de controles
        HBox controlsPanel = new HBox(10);
        controlsPanel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        refreshButton = new Button("🔄 Actualizar");
        refreshButton.setOnAction(e -> refreshData());
        
        configureButton = new Button("⚙️ Configurar BD");
        configureButton.setOnAction(e -> showConfigurationDialog());
        
        controlsPanel.getChildren().addAll(refreshButton, configureButton);
        
        panel.getChildren().addAll(titleLabel, infoLabel, controlsPanel);
        
        return panel;
    }
    
    /**
     * Crea el panel izquierdo con el árbol de tablas.
     */
    private VBox createTableTreePanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(250);
        panel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        // Título del panel
        Label titleLabel = new Label("📋 Tablas de la Base de Datos");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Árbol de tablas
        tableTreeView = new TreeView<>();
        tableTreeView.setShowRoot(true);
        tableTreeView.setPrefHeight(400);
        
        // Configurar el árbol
        TreeItem<String> root = new TreeItem<>("Base de Datos");
        root.setExpanded(true);
        tableTreeView.setRoot(root);
        
        // Evento de selección
        tableTreeView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null && newValue.isLeaf() && newValue != root) {
                    loadTableData(newValue.getValue());
                }
            }
        );
        
        panel.getChildren().addAll(titleLabel, tableTreeView);
        
        return panel;
    }
    
    /**
     * Crea el panel central con la tabla de datos.
     */
    private VBox createDataPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        // Título del panel
        Label titleLabel = new Label("📊 Datos de la Tabla");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Tabla de datos
        dataTableView = new TableView<>();
        dataTableView.setPlaceholder(new Label("Selecciona una tabla para ver sus datos"));
        dataTableView.setPrefHeight(400);
        
        // ScrollPane para la tabla
        ScrollPane scrollPane = new ScrollPane(dataTableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        panel.getChildren().addAll(titleLabel, scrollPane);
        
        return panel;
    }
    
    /**
     * Crea el panel inferior con información de estado.
     */
    private HBox createBottomPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        panel.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        statusLabel = new Label("Listo");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        panel.getChildren().add(statusLabel);
        
        return panel;
    }
    
    /**
     * Carga la estructura de tablas en el árbol.
     */
    private void loadTableStructure() {
        logger.info("📋 Cargando estructura de tablas");
        
        // Verificar configuración
        if (!configurationService.hasConfiguration()) {
            updateStatus("❌ No hay configuración de base de datos", "error");
            updateInfo("Configura la base de datos de Kursor para comenzar");
            return;
        }
        
        Optional<com.kursor.studio.model.DatabaseConfiguration> config = 
            configurationService.getActiveConfiguration();
        
        if (config.isEmpty() || !config.get().isConnected()) {
            updateStatus("❌ Base de datos no conectada", "error");
            updateInfo("La base de datos configurada no está disponible");
            return;
        }
        
        updateStatus("📋 Cargando tablas...", "info");
        
        // Ejecutar en hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                // Obtener nombres de tablas desde el metamodelo JPA
                List<String> tables = getTableNamesFromJPA();
                
                Platform.runLater(() -> {
                    this.tableNames = tables;
                    populateTableTree(tables);
                    updateStatus("✅ " + tables.size() + " tablas cargadas", "success");
                    updateInfo("Base de datos: " + config.get().getKursorDatabasePath());
                });
                
            } catch (Exception e) {
                logger.error("Error al cargar estructura de tablas: {}", e.getMessage(), e);
                Platform.runLater(() -> {
                    updateStatus("❌ Error al cargar tablas: " + e.getMessage(), "error");
                    updateInfo("Verifica la conexión a la base de datos");
                });
            }
        }).start();
    }
    
    /**
     * Obtiene los nombres de las tablas desde el metamodelo JPA.
     */
    private List<String> getTableNamesFromJPA() {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorEntityManagerSafely();
            if (em == null) {
                throw new RuntimeException("No se pudo crear EntityManager para kursor");
            }
            
            Metamodel metamodel = em.getMetamodel();
            List<String> tableNames = metamodel.getEntities().stream()
                .map(EntityType::getName)
                .sorted()
                .toList();
            
            logger.info("Tablas encontradas: {}", tableNames);
            return tableNames;
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Puebla el árbol con las tablas encontradas.
     */
    private void populateTableTree(List<String> tableNames) {
        TreeItem<String> root = tableTreeView.getRoot();
        root.getChildren().clear();
        
        for (String tableName : tableNames) {
            TreeItem<String> tableItem = new TreeItem<>(tableName);
            tableItem.setExpanded(false);
            root.getChildren().add(tableItem);
        }
        
        logger.info("Árbol de tablas actualizado con {} tablas", tableNames.size());
    }
    
    /**
     * Carga los datos de una tabla específica.
     */
    private void loadTableData(String tableName) {
        logger.info("📊 Cargando datos de tabla: {}", tableName);
        
        updateStatus("📊 Cargando datos de " + tableName + "...", "info");
        
        // Ejecutar en hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                // Obtener datos de la tabla
                List<Object[]> data = getTableData(tableName);
                
                Platform.runLater(() -> {
                    displayTableData(tableName, data);
                    updateStatus("✅ " + data.size() + " registros en " + tableName, "success");
                });
                
            } catch (Exception e) {
                logger.error("Error al cargar datos de tabla {}: {}", tableName, e.getMessage(), e);
                Platform.runLater(() -> {
                    updateStatus("❌ Error al cargar datos: " + e.getMessage(), "error");
                    clearTableData();
                });
            }
        }).start();
    }
    
    /**
     * Obtiene los datos de una tabla específica.
     */
    private List<Object[]> getTableData(String tableName) {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorEntityManagerSafely();
            if (em == null) {
                throw new RuntimeException("No se pudo crear EntityManager para kursor");
            }
            
            // Mapear nombres de entidades a consultas específicas
            String jpql = getJPQLForEntity(tableName);
            jakarta.persistence.Query query = em.createQuery(jpql);
            query.setMaxResults(1000); // Limitar a 1000 registros por rendimiento
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            logger.info("Datos obtenidos de {}: {} registros", tableName, results.size());
            return results;
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Obtiene la consulta JPQL específica para una entidad.
     */
    private String getJPQLForEntity(String entityName) {
        switch (entityName) {
            case "Sesion":
                return "SELECT s.id, s.fechaInicio, s.fechaFin, s.estado, s.cursoId FROM Sesion s";
            case "EstadoEstrategia":
                return "SELECT e.id, e.estrategia, e.estado, e.fechaCreacion FROM EstadoEstrategia e";
            case "EstadisticasUsuario":
                return "SELECT eu.id, eu.usuarioId, eu.preguntasCorrectas, eu.preguntasIncorrectas, eu.fechaUltimaActividad FROM EstadisticasUsuario eu";
            case "RespuestaPregunta":
                return "SELECT rp.id, rp.preguntaId, rp.respuesta, rp.esCorrecta, rp.fechaRespuesta FROM RespuestaPregunta rp";
            default:
                // Consulta genérica como fallback
                return "SELECT e FROM " + entityName + " e";
        }
    }
    
    /**
     * Muestra los datos en la tabla.
     */
    private void displayTableData(String tableName, List<Object[]> data) {
        // Limpiar tabla anterior
        dataTableView.getColumns().clear();
        tableData.clear();
        
        if (data.isEmpty()) {
            dataTableView.setPlaceholder(new Label("No hay datos en la tabla " + tableName));
            return;
        }
        
        // Obtener nombres de columnas para la entidad
        String[] columnNames = getColumnNamesForEntity(tableName);
        
        // Crear columnas dinámicamente basadas en los datos
        Object[] firstRow = data.get(0);
        if (firstRow != null) {
            for (int i = 0; i < firstRow.length; i++) {
                final int columnIndex = i;
                String columnName = (i < columnNames.length) ? columnNames[i] : "Columna " + (i + 1);
                TableColumn<Object[], Object> column = new TableColumn<>(columnName);
                column.setCellValueFactory(param -> {
                    Object[] row = param.getValue();
                    if (row != null && columnIndex < row.length) {
                        return new javafx.beans.property.SimpleObjectProperty<>(row[columnIndex]);
                    }
                    return new javafx.beans.property.SimpleObjectProperty<>(null);
                });
                dataTableView.getColumns().add(column);
            }
        }
        
        // Agregar datos
        tableData.addAll(data);
        dataTableView.setItems(tableData);
        
        logger.info("Datos mostrados en tabla: {} columnas, {} filas", 
            dataTableView.getColumns().size(), data.size());
    }
    
    /**
     * Obtiene los nombres de columnas para una entidad específica.
     */
    private String[] getColumnNamesForEntity(String entityName) {
        switch (entityName) {
            case "Sesion":
                return new String[]{"ID", "Fecha Inicio", "Fecha Fin", "Estado", "Curso ID"};
            case "EstadoEstrategia":
                return new String[]{"ID", "Estrategia", "Estado", "Fecha Creación"};
            case "EstadisticasUsuario":
                return new String[]{"ID", "Usuario ID", "Correctas", "Incorrectas", "Última Actividad"};
            case "RespuestaPregunta":
                return new String[]{"ID", "Pregunta ID", "Respuesta", "Es Correcta", "Fecha Respuesta"};
            default:
                // Nombres genéricos como fallback
                Object[] firstRow = tableData.isEmpty() ? new Object[0] : tableData.get(0);
                String[] names = new String[firstRow != null ? firstRow.length : 0];
                for (int i = 0; i < names.length; i++) {
                    names[i] = "Columna " + (i + 1);
                }
                return names;
        }
    }
    
    /**
     * Limpia los datos de la tabla.
     */
    private void clearTableData() {
        dataTableView.getColumns().clear();
        tableData.clear();
        dataTableView.setPlaceholder(new Label("Selecciona una tabla para ver sus datos"));
    }
    
    /**
     * Actualiza el estado mostrado en la interfaz.
     */
    private void updateStatus(String message, String type) {
        statusLabel.setText(message);
        
        switch (type) {
            case "success":
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #28a745;");
                break;
            case "error":
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc3545;");
                break;
            case "info":
            default:
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #17a2b8;");
                break;
        }
    }
    
    /**
     * Actualiza la información mostrada en la interfaz.
     */
    private void updateInfo(String message) {
        infoLabel.setText(message);
    }
    
    /**
     * Refresca todos los datos.
     */
    private void refreshData() {
        logger.info("🔄 Refrescando datos del explorador");
        loadTableStructure();
    }
    
    /**
     * Muestra el diálogo de configuración de base de datos.
     */
    private void showConfigurationDialog() {
        logger.info("⚙️ Mostrando diálogo de configuración");
        
        DatabaseConfigurationDialog dialog = new DatabaseConfigurationDialog(configurationService);
        Optional<com.kursor.studio.model.DatabaseConfiguration> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get().isConnected()) {
            logger.info("✅ Configuración actualizada - refrescando datos");
            refreshData();
        }
    }
} 