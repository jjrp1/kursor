package com.kursor.studio.ui;

import com.kursor.studio.model.DatabaseConfiguration;
import com.kursor.studio.service.DatabaseConfigurationService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * Diálogo para configurar la base de datos de Kursor que se quiere explorar.
 */
public class DatabaseConfigurationDialog extends Dialog<DatabaseConfiguration> {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigurationDialog.class);
    
    private final DatabaseConfigurationService configurationService;
    private final TextField pathField;
    private final TextField descriptionField;
    private final Label statusLabel;
    private final Button testButton;
    private final Button browseButton;
    
    public DatabaseConfigurationDialog(DatabaseConfigurationService configurationService) {
        this.configurationService = configurationService;
        
        // Configurar el diálogo
        setTitle("Configuración de Base de Datos de Kursor");
        setHeaderText("Selecciona la base de datos de Kursor que quieres explorar");
        
        // Crear controles
        pathField = new TextField();
        pathField.setPromptText("Ruta a la base de datos de Kursor (.db o .sqlite)");
        pathField.setPrefWidth(400);
        
        descriptionField = new TextField();
        descriptionField.setPromptText("Descripción opcional (ej: BD de desarrollo, producción, etc.)");
        descriptionField.setPrefWidth(400);
        
        statusLabel = new Label("Estado: No configurado");
        statusLabel.setStyle("-fx-text-fill: gray;");
        
        browseButton = new Button("Examinar...");
        browseButton.setOnAction(e -> browseForDatabase());
        
        testButton = new Button("Probar Conexión");
        testButton.setOnAction(e -> testConnection());
        testButton.setDisable(true);
        
        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        grid.add(new Label("Base de Datos:"), 0, 0);
        grid.add(pathField, 1, 0);
        grid.add(browseButton, 2, 0);
        
        grid.add(new Label("Descripción:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        
        grid.add(statusLabel, 1, 2);
        grid.add(testButton, 2, 2);
        
        getDialogPane().setContent(grid);
        
        // Botones del diálogo
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Configurar resultado
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return saveConfiguration();
            }
            return null;
        });
        
        // Eventos
        pathField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean hasPath = newValue != null && !newValue.trim().isEmpty();
            testButton.setDisable(!hasPath);
            updateStatus("Ruta ingresada, prueba la conexión");
        });
        
        // Cargar configuración existente si existe
        loadExistingConfiguration();
    }
    
    /**
     * Abre el diálogo de selección de archivos para buscar la base de datos.
     */
    private void browseForDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Base de Datos de Kursor");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Bases de Datos SQLite", "*.db", "*.sqlite"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        
        // Establecer directorio inicial
        String currentPath = pathField.getText();
        if (currentPath != null && !currentPath.trim().isEmpty()) {
            File currentFile = new File(currentPath);
            if (currentFile.exists()) {
                fileChooser.setInitialDirectory(currentFile.getParentFile());
            }
        } else {
            // Directorio por defecto
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        
        File selectedFile = fileChooser.showOpenDialog(getDialogPane().getScene().getWindow());
        if (selectedFile != null) {
            pathField.setText(selectedFile.getAbsolutePath());
            updateStatus("Archivo seleccionado: " + selectedFile.getName());
        }
    }
    
    /**
     * Prueba la conexión a la base de datos seleccionada.
     */
    private void testConnection() {
        String databasePath = pathField.getText().trim();
        if (databasePath.isEmpty()) {
            showError("Error", "Por favor, selecciona una base de datos primero.");
            return;
        }
        
        updateStatus("Probando conexión...");
        testButton.setDisable(true);
        
        // Ejecutar en hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                boolean isValid = configurationService.isValidDatabaseFile(databasePath);
                if (!isValid) {
                    Platform.runLater(() -> {
                        updateStatus("Error: Archivo de base de datos no válido");
                        testButton.setDisable(false);
                        showError("Error de Validación", 
                            "El archivo seleccionado no es una base de datos SQLite válida.\n\n" +
                            "Verifica que:\n" +
                            "• El archivo existe y es legible\n" +
                            "• Tiene extensión .db o .sqlite\n" +
                            "• Tiene un tamaño mínimo de 1KB");
                    });
                    return;
                }
                
                boolean connectionSuccessful = configurationService.testConnection(databasePath);
                
                Platform.runLater(() -> {
                    if (connectionSuccessful) {
                        updateStatus("✅ Conexión exitosa");
                        testButton.setDisable(false);
                        showInfo("Conexión Exitosa", 
                            "La conexión a la base de datos se realizó correctamente.\n\n" +
                            "Puedes proceder a guardar la configuración.");
                    } else {
                        updateStatus("❌ Error de conexión");
                        testButton.setDisable(false);
                        showError("Error de Conexión", 
                            "No se pudo conectar a la base de datos.\n\n" +
                            "Verifica que:\n" +
                            "• El archivo no esté corrupto\n" +
                            "• Tengas permisos de lectura\n" +
                            "• No esté siendo usado por otra aplicación");
                    }
                });
                
            } catch (Exception e) {
                logger.error("Error al probar conexión: {}", e.getMessage(), e);
                Platform.runLater(() -> {
                    updateStatus("❌ Error inesperado");
                    testButton.setDisable(false);
                    showError("Error Inesperado", 
                        "Ocurrió un error al probar la conexión:\n\n" + e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Guarda la configuración de base de datos.
     */
    private DatabaseConfiguration saveConfiguration() {
        String databasePath = pathField.getText().trim();
        String description = descriptionField.getText().trim();
        
        if (databasePath.isEmpty()) {
            showError("Error", "Por favor, selecciona una base de datos.");
            return null;
        }
        
        try {
            // Validar archivo antes de guardar
            if (!configurationService.isValidDatabaseFile(databasePath)) {
                showError("Error de Validación", 
                    "El archivo de base de datos no es válido.\n" +
                    "Por favor, selecciona un archivo válido y prueba la conexión.");
                return null;
            }
            
            // Guardar configuración
            DatabaseConfiguration config = configurationService.saveConfiguration(databasePath, description);
            logger.info("Configuración guardada exitosamente: {}", databasePath);
            
            showInfo("Configuración Guardada", 
                "La configuración se guardó correctamente.\n\n" +
                "Base de datos: " + new File(databasePath).getName() + "\n" +
                "Estado: " + (config.isConnected() ? "Conectado" : "Error de conexión"));
            
            return config;
            
        } catch (Exception e) {
            logger.error("Error al guardar configuración: {}", e.getMessage(), e);
            showError("Error al Guardar", 
                "No se pudo guardar la configuración:\n\n" + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carga la configuración existente si existe.
     */
    private void loadExistingConfiguration() {
        try {
            Optional<DatabaseConfiguration> existingConfig = configurationService.getActiveConfiguration();
            if (existingConfig.isPresent()) {
                DatabaseConfiguration config = existingConfig.get();
                pathField.setText(config.getKursorDatabasePath());
                descriptionField.setText(config.getDescription());
                
                if (config.isConnected()) {
                    updateStatus("✅ Configuración existente - Conectado");
                } else {
                    updateStatus("⚠️ Configuración existente - Error de conexión");
                }
                
                logger.info("Configuración existente cargada: {}", config.getKursorDatabasePath());
            }
        } catch (Exception e) {
            logger.error("Error al cargar configuración existente: {}", e.getMessage(), e);
            updateStatus("Error al cargar configuración existente");
        }
    }
    
    /**
     * Actualiza el mensaje de estado.
     */
    private void updateStatus(String message) {
        statusLabel.setText("Estado: " + message);
        
        if (message.contains("✅")) {
            statusLabel.setStyle("-fx-text-fill: green;");
        } else if (message.contains("❌") || message.contains("Error")) {
            statusLabel.setStyle("-fx-text-fill: red;");
        } else if (message.contains("⚠️")) {
            statusLabel.setStyle("-fx-text-fill: orange;");
        } else {
            statusLabel.setStyle("-fx-text-fill: gray;");
        }
    }
    
    /**
     * Muestra un diálogo de error.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de información.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 