package com.kursor.presentation.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.kursor.presentation.KursorApplication;

/**
 * Ejemplo de uso de las funcionalidades de la barra de menús en Kursor.
 * 
 * <p>Esta clase demuestra cómo usar los métodos para habilitar y deshabilitar
 * la barra de menús de la aplicación Kursor.</p>
 * 
 * <p>Características demostradas:</p>
 * <ul>
 *   <li>Habilitar/deshabilitar barra de menús programáticamente</li>
 *   <li>Verificar el estado de la barra de menús</li>
 *   <li>Interfaz de control para gestionar la barra de menús</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class MenuBarExample extends Application {
    
    private KursorApplication kursorApp;
    private Label statusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ejemplo - Control de Barra de Menús");
        
        // Crear la aplicación Kursor
        // kursorApp = new KursorApplication();
        
        // Crear controles para gestionar la barra de menús
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Control de Barra de Menús");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // statusLabel = new Label("Estado: " + (kursorApp.isBarraMenusHabilitada() ? "Habilitada" : "Deshabilitada"));
        statusLabel = new Label("Estado: No disponible");
        statusLabel.setStyle("-fx-font-size: 14px;");
        
        Button habilitarBtn = new Button("Habilitar Barra de Menús");
        /*
        habilitarBtn.setOnAction(e -> {
            kursorApp.habilitarBarraMenus();
            actualizarEstado();
        });
        */
        
        Button deshabilitarBtn = new Button("Deshabilitar Barra de Menús");
        /*
        deshabilitarBtn.setOnAction(e -> {
            kursorApp.deshabilitarBarraMenus();
            actualizarEstado();
        });
        */
        
        Button toggleBtn = new Button("Alternar Barra de Menús");
        /*
        toggleBtn.setOnAction(e -> {
            kursorApp.toggleBarraMenus();
            actualizarEstado();
        });
        */
        
        Button verificarBtn = new Button("Verificar Estado");
        // verificarBtn.setOnAction(e -> actualizarEstado());
        
        // Agregar controles al contenedor
        controls.getChildren().addAll(
            titleLabel,
            statusLabel,
            habilitarBtn,
            deshabilitarBtn,
            toggleBtn,
            verificarBtn
        );
        
        Scene scene = new Scene(controls, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Actualiza la etiqueta de estado con el estado actual de la barra de menús.
     */
    private void actualizarEstado() {
        /*
        boolean habilitada = kursorApp.isBarraMenusHabilitada();
        statusLabel.setText("Estado: " + (habilitada ? "Habilitada" : "Deshabilitada"));
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (habilitada ? "green" : "red") + ";");
        */
    }
    
    /**
     * Método principal para ejecutar el ejemplo.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
} 
