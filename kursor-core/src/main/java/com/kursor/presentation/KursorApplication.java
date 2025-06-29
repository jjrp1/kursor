package com.kursor.ui;

import com.kursor.persistence.config.PersistenceConfig;
import com.kursor.ui.main.MainController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;

public class KursorApplication extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(KursorApplication.class);
    private static final String APP_TITLE = "🎓 Kursor - Plataforma de Aprendizaje Interactivo";
    private static final String APP_VERSION = "1.0.0";
    
    private MainController mainController;
    
    public static void main(String[] args) {
        logger.info("================================================");
        logger.info("🚀 Iniciando Kursor v{}", APP_VERSION);
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
                
                // Mostrar estado de la base de datos
                if (PersistenceConfig.isInitialized()) {
                    logger.info("✅ kursor.db: Base de datos inicializada");
                } else {
                    logger.info("ℹ️ kursor.db: Base de datos de NO inicializada");
                    logger.info("ℹ️ Puede configurar la base de datos más tarde desde la interfaz");
                    throw new RuntimeException("Base de datos no inicializada");
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
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Crear el controlador principal
            mainController = new MainController(primaryStage);
            
            // Mostrar la vista principal
            mainController.showMainView();
            
            // Mostrar la ventana
            primaryStage.show();
            
            logger.info("✅ Kursor iniciado correctamente");
            
            // Capturar evento de cierre de ventana para salida ordenada
            primaryStage.setOnCloseRequest(e -> {
                logger.info("👋 Cerrando aplicación desde botón de ventana");
                e.consume(); // Prevenir cierre automático
                exitApplication();
            });
            
        } catch (Exception e) {
            logger.error("❌ Error al iniciar Kursor", e);
            showErrorDialog("Error de Inicio", "No se pudo iniciar la aplicación: " + e.getMessage());
            Platform.exit();
        }
    }
    
    /**
     * Sale de la aplicación de forma ordenada
     */
    private void exitApplication() {
        try {
            logger.info("🔄 Iniciando proceso de cierre ordenado...");
            
            // Cerrar ventana principal
            if (mainController != null) {
                mainController.exitApplication();
            }
            
            logger.info("✅ Aplicación cerrada correctamente");
            
        } catch (Exception e) {
            logger.error("❌ Error durante el cierre de la aplicación", e);
            System.exit(1);
        }
    }
    
    /**
     * Muestra un diálogo de error
     */
    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    @Override
    public void stop() {
        logger.info("🛑 Deteniendo aplicación Kursor...");
        shutdown();
    }
    
    /**
     * Realiza el cierre ordenado de la aplicación
     */
    private void shutdown() {
        try {
            logger.info("🧹 Limpiando recursos...");
            
            // Aquí se pueden agregar tareas de limpieza adicionales
            // como cerrar conexiones de base de datos, guardar configuraciones, etc.
            
            logger.info("✅ Recursos liberados correctamente");
            
        } catch (Exception e) {
            logger.error("❌ Error durante la limpieza de recursos", e);
        } finally {
            logger.info("👋 Kursor finalizado");
        }
    }
} 
