package com.kursor.ui;

import com.kursor.domain.Curso;
import com.kursor.domain.Bloque;
import com.kursor.util.CursoManager;
import com.kursor.yaml.dto.CursoPreviewDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InspeccionarCurso {
    
    private static final Logger logger = LoggerFactory.getLogger(InspeccionarCurso.class);
    
    public static void mostrar(CursoPreviewDTO preview, Stage owner) {
        logger.info("================================================");
        logger.info("Iniciando diálogo de inspección de curso");
        logger.info("================================================");
        
        // Validar parámetros de entrada
        if (preview == null) {
            logger.warn("CursoPreviewDTO es null - mostrando diálogo sin datos específicos");
        } else {
            logger.debug("CursoPreviewDTO recibido - ID: {}, Título: '{}'", preview.getId(), preview.getTitulo());
        }
        
        if (owner == null) {
            logger.error("Stage owner es null - el diálogo no será modal correctamente");
        } else {
            logger.debug("Stage owner recibido correctamente");
        }
        
        // Cargar curso completo
        Curso curso = null;
        if (preview != null) {
            logger.info("Cargando curso completo para ID: {}", preview.getId());
            try {
                curso = CursoManager.getInstance().obtenerCursoCompleto(preview.getId());
                if (curso != null) {
                    logger.info("Curso cargado exitosamente - ID: {}, Título: '{}', Bloques: {}", 
                        curso.getId(), curso.getTitulo(), curso.getBloques().size());
                } else {
                    logger.warn("No se pudo cargar el curso completo para ID: {}", preview.getId());
                }
            } catch (Exception e) {
                logger.error("Error al cargar curso completo para ID: {} - Error: {}", preview.getId(), e.getMessage(), e);
            }
        } else {
            logger.warn("No se puede cargar curso completo - preview es null");
        }
        
        // Crear ventana modal
        logger.debug("Creando Stage para diálogo modal");
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setTitle("🔍 Inspección de Curso");
        logger.info("Stage modal configurado correctamente");

        // Crear contenido
        logger.debug("Creando contenido del diálogo");
        VBox root = new VBox(18);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Título
        Label titulo = new Label("🔍 Inspección de Curso");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setTextFill(Color.web("#2c3e50"));
        titulo.setAlignment(Pos.CENTER);
        root.getChildren().add(titulo);
        root.getChildren().add(new Separator());
        logger.debug("Título del diálogo creado");

        if (curso == null) {
            logger.warn("Mostrando mensaje de error - curso no disponible");
            Label error = new Label("No se pudo cargar el curso completo.");
            error.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 16px;");
            root.getChildren().add(error);
        } else {
            logger.info("Creando información del curso");
            VBox info = new VBox(6);
            info.getChildren().addAll(
                crearCampo("Código (ID):", curso.getId()),
                crearCampo("Fichero:", preview.getId() + ".yaml"),
                crearCampo("Título:", curso.getTitulo()),
                crearCampo("Descripción:", curso.getDescripcion()),
                crearCampo("Total de preguntas:", String.valueOf(curso.getNumeroPreguntas()))
            );
            root.getChildren().add(info);
            root.getChildren().add(new Separator());
            logger.debug("Información básica del curso agregada");
            
            // Procesar bloques
            logger.info("Procesando {} bloques del curso", curso.getBloques().size());
            for (Bloque bloque : curso.getBloques()) {
                logger.debug("Procesando bloque - ID: {}, Título: '{}', Tipo: {}, Preguntas: {}", 
                    bloque.getId(), bloque.getTitulo(), bloque.getTipo(), bloque.getPreguntas().size());
                
                VBox bloqueBox = new VBox(3);
                bloqueBox.setPadding(new Insets(8, 0, 8, 12));
                bloqueBox.setStyle("-fx-background-color: #eef2f7; -fx-background-radius: 6;");
                bloqueBox.getChildren().addAll(
                    crearCampo("Bloque:", bloque.getTitulo()),
                    crearCampo("Descripción:", bloque.getDescripcion()),
                    crearCampo("Tipo de preguntas:", bloque.getTipo()),
                    crearCampo("Total preguntas:", String.valueOf(bloque.getPreguntas().size()))
                );
                root.getChildren().add(bloqueBox);
                root.getChildren().add(new Separator());
            }
            logger.info("Todos los bloques procesados correctamente");
        }

        // Botón cerrar
        logger.debug("Creando botón de cerrar");
        Button cerrar = new Button("Cerrar");
        cerrar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-padding: 8 20;");
        cerrar.setOnAction(e -> {
            logger.info("Usuario cerró el diálogo de inspección");
            dialog.close();
        });
        
        VBox botonera = new VBox(10);
        botonera.setAlignment(Pos.CENTER);
        botonera.getChildren().add(cerrar);
        root.getChildren().add(botonera);

        // Crear ScrollPane
        logger.debug("Creando ScrollPane para el contenido");
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(500);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Crear escena y mostrar diálogo
        logger.info("Creando escena y mostrando diálogo modal");
        Scene scene = new Scene(scroll, 480, 600);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        
        logger.info("================================================");
        logger.info("Mostrando diálogo de inspección de curso");
        logger.info("================================================");
        dialog.showAndWait();
        
        logger.info("================================================");
        logger.info("Diálogo de inspección de curso cerrado");
        logger.info("================================================");
    }

    private static HBox crearCampo(String label, String valor) {
        logger.debug("Creando campo - Label: '{}', Valor: '{}'", label, valor);
        Label l = new Label(label);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#34495e"));
        Label v = new Label(valor);
        v.setFont(Font.font("Segoe UI", 12));
        v.setTextFill(Color.web("#2c3e50"));
        HBox box = new HBox(8, l, v);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
} 