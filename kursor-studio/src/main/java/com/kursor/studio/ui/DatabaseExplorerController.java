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
import java.util.ArrayList;
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
                logger.warn("No se pudo crear EntityManager para kursor - usando lista por defecto");
                return getRealTableNames();
            }
            
            // Primero intentar obtener las tablas reales de la base de datos
            List<String> realTables = getRealTableNames();
            if (!realTables.isEmpty()) {
                logger.info("Tablas reales encontradas en BD: {}", realTables);
                return realTables;
            }
            
            // Fallback al metamodelo JPA
            Metamodel metamodel = em.getMetamodel();
            if (metamodel == null) {
                logger.warn("Metamodel es null - usando lista por defecto");
                return List.of("sesiones", "estados_estrategias", "estadisticas_usuario", "preguntas_sesion");
            }
            
            List<String> tableNames = metamodel.getEntities().stream()
                .map(EntityType::getName)
                .sorted()
                .toList();
            
            if (tableNames.isEmpty()) {
                logger.warn("No se encontraron entidades en el metamodelo - usando lista por defecto");
                return List.of("sesiones", "estados_estrategias", "estadisticas_usuario", "preguntas_sesion");
            }
            
            logger.info("Tablas encontradas desde JPA: {}", tableNames);
            return tableNames;
            
        } catch (Exception e) {
            logger.error("Error al obtener nombres de tablas desde JPA: {}", e.getMessage(), e);
            logger.info("Usando lista de tablas por defecto");
            return getRealTableNames();
        } finally {
            if (em != null) {
                try {
                    em.close();
                } catch (Exception e) {
                    logger.warn("Error al cerrar EntityManager: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * Obtiene los nombres reales de las tablas desde la base de datos.
     */
    private List<String> getRealTableNames() {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorEntityManagerSafely();
            if (em == null) {
                logger.warn("No se pudo crear EntityManager para obtener tablas reales");
                return new ArrayList<>();
            }
            
            // Consulta SQLite para obtener todas las tablas
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name";
            jakarta.persistence.Query query = em.createNativeQuery(sql);
            
            @SuppressWarnings("unchecked")
            List<Object> results = query.getResultList();
            
            List<String> tableNames = results.stream()
                .map(Object::toString)
                .sorted()
                .toList();
            
            if (tableNames.isEmpty()) {
                logger.warn("No se encontraron tablas en la base de datos");
            } else {
                logger.info("Tablas reales encontradas en BD: {}", tableNames);
                
                // Mostrar información adicional de cada tabla
                for (String tableName : tableNames) {
                    try {
                        String countSql = "SELECT COUNT(*) FROM " + tableName;
                        jakarta.persistence.Query countQuery = em.createNativeQuery(countSql);
                        Object count = countQuery.getSingleResult();
                        logger.info("  - {}: {} registros", tableName, count);
                    } catch (Exception e) {
                        logger.warn("  - {}: Error al contar registros: {}", tableName, e.getMessage());
                    }
                }
            }
            
            return tableNames;
            
        } catch (Exception e) {
            logger.error("Error al obtener tablas reales: {}", e.getMessage(), e);
            return new ArrayList<>();
        } finally {
            if (em != null) {
                try {
                    em.close();
                } catch (Exception e) {
                    logger.warn("Error al cerrar EntityManager: {}", e.getMessage());
                }
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
            
            // Verifica si la tabla existe en la base de datos
            if (!tableExists(tableName)) {
                throw new RuntimeException("Tabla no encontrada: " + tableName);
            }
            
            // Usar consulta SQL nativa simple sin LIMIT
            String sql = getSQLForTable(tableName);
            if (sql == null || sql.trim().isEmpty()) {
                throw new RuntimeException("No se pudo generar consulta SQL para la tabla: " + tableName);
            }
            
            // Agregar LIMIT directamente en la consulta SQL para evitar el problema de Hibernate
            sql += " LIMIT 1000";
            
            jakarta.persistence.Query query = em.createNativeQuery(sql);
            // NO usar setMaxResults para evitar el LIMIT duplicado
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            if (results == null) {
                logger.warn("Resultado de consulta es null para tabla: {}", tableName);
                return new ArrayList<>();
            }
            
            logger.info("Datos obtenidos de {}: {} registros", tableName, results.size());
            return results;
            
        } catch (Exception e) {
            logger.error("Error al obtener datos de tabla {}: {}", tableName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener datos de tabla " + tableName + ": " + e.getMessage(), e);
        } finally {
            if (em != null) {
                try {
                    em.close();
                } catch (Exception e) {
                    logger.warn("Error al cerrar EntityManager: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * Verifica si una tabla existe en la base de datos.
     */
    private boolean tableExists(String tableName) {
        EntityManager em = null;
        try {
            em = PersistenceConfig.createKursorEntityManagerSafely();
            if (em == null) {
                return false;
            }
            
            // Consulta SQLite para verificar si la tabla existe
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
            jakarta.persistence.Query query = em.createNativeQuery(sql);
            query.setParameter(1, tableName.toLowerCase());
            
            @SuppressWarnings("unchecked")
            List<Object> results = query.getResultList();
            
            boolean exists = !results.isEmpty();
            logger.debug("Tabla {} existe: {}", tableName, exists);
            return exists;
            
        } catch (Exception e) {
            logger.warn("Error al verificar existencia de tabla {}: {}", tableName, e.getMessage());
            return false;
        } finally {
            if (em != null) {
                try {
                    em.close();
                } catch (Exception e) {
                    logger.warn("Error al cerrar EntityManager: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * Obtiene la consulta SQL nativa para una tabla específica.
     */
    private String getSQLForTable(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            logger.warn("Nombre de tabla es null o vacío");
            return null;
        }
        
        // Mapear nombres de entidades a nombres reales de tablas
        String realTableName = getRealTableName(tableName);
        
        switch (realTableName.toLowerCase()) {
            case "sesiones":
                return "SELECT id, curso_id, bloque_id, estrategia_tipo, fecha_inicio, fecha_ultima_revision, " +
                       "tiempo_total, preguntas_respondidas, aciertos, tasa_aciertos, mejor_racha_aciertos, " +
                       "porcentaje_completitud, pregunta_actual_id, estado, created_at, updated_at " +
                       "FROM sesiones";
            case "estados_estrategias":
                return "SELECT id, sesion_id, tipo_estrategia, datos_estado, progreso, fecha_creacion, fecha_ultima_modificacion " +
                       "FROM estados_estrategias";
            case "estadisticas_usuario":
                return "SELECT id, usuario_id, curso_id, tiempo_total, sesiones_completadas, mejor_racha_dias, " +
                       "racha_actual_dias, fecha_ultima_sesion, fecha_primera_sesion, created_at, updated_at " +
                       "FROM estadisticas_usuario";
            case "preguntas_sesion":
                return "SELECT id, sesion_id, pregunta_id, resultado, tiempo_dedicado, respuesta, created_at, updated_at " +
                       "FROM preguntas_sesion";
            default:
                // Verificar si la tabla existe antes de hacer la consulta
                logger.warn("Tabla no reconocida: {}. Intentando consulta genérica", tableName);
                return "SELECT * FROM " + realTableName;
        }
    }
    
    /**
     * Mapea nombres de entidades a nombres reales de tablas.
     */
    private String getRealTableName(String entityName) {
        if (entityName == null) {
            return null;
        }
        
        switch (entityName.toLowerCase()) {
            case "sesion":
            case "sesiones":
                return "sesiones";
            case "estadoestrategia":
            case "estados_estrategias":
                return "estados_estrategias";
            case "estadisticasusuario":
            case "estadisticas_usuario":
                return "estadisticas_usuario";
            case "respuestapregunta":
            case "preguntasesion":
            case "respuestas_preguntas":
            case "preguntas_sesion":
                return "preguntas_sesion";
            default:
                // Si no es un mapeo conocido, usar el nombre tal como viene
                return entityName.toLowerCase();
        }
    }
    
    /**
     * Muestra los datos en la tabla.
     */
    private void displayTableData(String tableName, List<Object[]> data) {
        try {
            // Validar parámetros
            if (tableName == null || tableName.trim().isEmpty()) {
                logger.warn("Nombre de tabla es null o vacío");
                clearTableData();
                return;
            }
            
            if (data == null) {
                logger.warn("Datos de tabla son null para: {}", tableName);
                clearTableData();
                return;
            }
            
            // Limpiar tabla anterior
            dataTableView.getColumns().clear();
            tableData.clear();
            
            if (data.isEmpty()) {
                dataTableView.setPlaceholder(new Label("No hay datos en la tabla " + tableName));
                logger.info("Tabla {} está vacía", tableName);
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
                            Object value = row[columnIndex];
                            // Formatear valores especiales
                            if (value instanceof java.time.LocalDateTime) {
                                return new javafx.beans.property.SimpleObjectProperty<>(
                                    ((java.time.LocalDateTime) value).toString()
                                );
                            } else if (value instanceof java.time.LocalDate) {
                                return new javafx.beans.property.SimpleObjectProperty<>(
                                    ((java.time.LocalDate) value).toString()
                                );
                            } else if (value instanceof Boolean) {
                                return new javafx.beans.property.SimpleObjectProperty<>(
                                    ((Boolean) value) ? "Sí" : "No"
                                );
                            } else {
                                return new javafx.beans.property.SimpleObjectProperty<>(value);
                            }
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
                
        } catch (Exception e) {
            logger.error("Error al mostrar datos de tabla {}: {}", tableName, e.getMessage(), e);
            clearTableData();
            dataTableView.setPlaceholder(new Label("Error al mostrar datos: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene los nombres de columnas para una entidad específica.
     */
    private String[] getColumnNamesForEntity(String entityName) {
        // Mapear nombres de entidades a nombres reales de tablas
        String realTableName = getRealTableName(entityName);
        
        switch (realTableName.toLowerCase()) {
            case "sesiones":
                return new String[]{"ID", "Curso ID", "Bloque ID", "Estrategia Tipo", "Fecha Inicio", 
                                  "Fecha Última Revisión", "Tiempo Total", "Preguntas Respondidas", 
                                  "Aciertos", "Tasa Aciertos", "Mejor Racha Aciertos", 
                                  "Porcentaje Completitud", "Pregunta Actual ID", "Estado", 
                                  "Created At", "Updated At"};
            case "estados_estrategias":
                return new String[]{"ID", "Sesión ID", "Tipo Estrategia", "Datos Estado", 
                                  "Progreso", "Fecha Creación", "Fecha Última Modificación"};
            case "estadisticas_usuario":
                return new String[]{"ID", "Usuario ID", "Curso ID", "Tiempo Total", 
                                  "Sesiones Completadas", "Mejor Racha Días", "Racha Actual Días", 
                                  "Fecha Última Sesión", "Fecha Primera Sesión", 
                                  "Created At", "Updated At"};
            case "preguntas_sesion":
                return new String[]{"ID", "Sesión ID", "Pregunta ID", "Resultado", 
                                  "Tiempo Dedicado", "Respuesta", "Created At", "Updated At"};
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