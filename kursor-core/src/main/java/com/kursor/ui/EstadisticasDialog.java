package com.kursor.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representa el cuadro de diálogo "Estadísticas" de la aplicación Kursor.
 * <p>
 * Esta clase encapsula la creación y visualización de una ventana modal
 * que muestra información sobre las estadísticas de la aplicación.
 * Sigue un diseño limpio y modular al separar la lógica del diálogo de la ventana principal.
 *
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class EstadisticasDialog {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticasDialog.class);

    /**
     * Muestra la ventana modal "Estadísticas".
     * <p>
     * Este método estático construye y muestra un {@link Stage} modal con un diseño
     * personalizado que incluye un mensaje de "en construcción".
     * 
     * @param owner La ventana principal que será el propietario del diálogo
     */
    public static void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setTitle("📊 Estadísticas");

        // --- Custom Content ---
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Mensaje principal
        Label mensaje = new Label("EN CONSTRUCCIÓN");
        mensaje.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        mensaje.setTextFill(Color.web("#2c3e50"));
        mensaje.setTextAlignment(TextAlignment.CENTER);

        // Botón cerrar
        Button cerrar = new Button("Cerrar");
        cerrar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-padding: 8 20;");
        cerrar.setOnAction(e -> dialog.close());

        root.getChildren().addAll(mensaje, cerrar);

        Scene scene = new Scene(root, 400, 250);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }
} 