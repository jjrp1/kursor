package com.kursor.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Representa el cuadro de diálogo "Acerca de" de la aplicación Kursor.
 * <p>
 * Esta clase encapsula la creación y visualización de un diálogo de información
 * que muestra detalles sobre la aplicación, su autor y otra información relevante.
 * Sigue un diseño limpio y modular al separar la lógica del diálogo de la ventana principal.
 *
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class AboutDialog {

    private static final Logger logger = LoggerFactory.getLogger(AboutDialog.class);

    /**
     * Muestra el cuadro de diálogo "Acerca de".
     * <p>
     * Este método estático construye y muestra un {@link Alert} modal con un diseño
     * personalizado que incluye un encabezado, información de la aplicación y datos
     * de contacto del autor.
     */
    public static void show() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de Kursor");
        alert.setHeaderText(null);

        // --- Custom Header ---
        Label title = new Label("🎓 Kursor");
        title.setTextFill(Color.BLUE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        Label subtitle = new Label("Tu Plataforma de Formación Interactiva");
        subtitle.setTextFill(Color.BLUE);

        VBox headerVBox = new VBox(2, title, subtitle);
        headerVBox.setAlignment(Pos.CENTER_LEFT);
        
        // --- Main Content ---
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label appInfo = new Label("Kursor es una aplicación de escritorio para crear y realizar cursos interactivos. Permite la gestión de cursos, la realización de ejercicios de diversos tipos y el seguimiento del progreso del estudiante.");
        appInfo.setWrapText(true);
        appInfo.setTextAlignment(TextAlignment.JUSTIFY);

        Label authorLabel = new Label("Autor: Juan José Ruiz Pérez");
        Hyperlink emailLink = new Hyperlink("jjrp1@um.es");

        VBox footerVBox = new VBox(2, authorLabel, emailLink);
        footerVBox.setAlignment(Pos.CENTER_LEFT);

        emailLink.setOnAction(e -> {
            try {
                String subject = URLEncoder.encode("Sobre su apliación Kursor, quiero comentarle...", StandardCharsets.UTF_8.toString()).replace("+", "%20");
                String mailto = "mailto:jjrp1@um.es?subject=" + subject;
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                    Desktop.getDesktop().mail(new URI(mailto));
                } else {
                    logger.warn("La API de Desktop no es compatible. No se puede abrir el cliente de correo.");
                }
            } catch (IOException | java.net.URISyntaxException ex) {
                logger.error("Error al intentar abrir el cliente de correo", ex);
            }
        });

        vbox.getChildren().addAll(headerVBox, new Separator(), appInfo, new Separator(), footerVBox);

        alert.getDialogPane().setContent(vbox);
        alert.getDialogPane().setPrefWidth(360);
        alert.showAndWait();
    }
} 