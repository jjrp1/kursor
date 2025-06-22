package com.kursor.ui;

import com.kursor.domain.Curso;
import com.kursor.domain.Bloque;
import com.kursor.util.CursoManager;
import com.kursor.yaml.dto.CursoDTO;
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
    
    public static void mostrar(CursoDTO cursoDTO, Stage owner) {
        logger.info("================================================");
        logger.info("🚀 INICIANDO: Diálogo de inspección de curso");
        logger.info("================================================");
        
        // Validar parámetros de entrada
        logger.debug("🔍 Validando parámetros de entrada...");
        if (cursoDTO == null) {
            logger.warn("⚠️  CursoDTO es null - mostrando diálogo sin datos específicos");
        } else {
            logger.debug("✅ CursoDTO recibido - ID: '{}', Título: '{}', Descripción: '{}'", 
                cursoDTO.getId(), cursoDTO.getTitulo(), cursoDTO.getDescripcion());
        }
        
        if (owner == null) {
            logger.error("❌ Stage owner es null - el diálogo no será modal correctamente");
            logger.warn("⚠️  Continuando sin modalidad - esto puede causar problemas de UX");
        } else {
            logger.debug("✅ Stage owner recibido correctamente - Título: '{}'", owner.getTitle());
        }
        
        // Cargar curso completo
        Curso curso = null;
        if (cursoDTO != null) {
            logger.info("📚 Cargando curso completo para ID: '{}'", cursoDTO.getId());
            try {
                logger.debug("🔄 Llamando a CursoManager.getInstance().obtenerCursoCompleto('{}')", cursoDTO.getId());
                curso = CursoManager.getInstance().obtenerCursoCompleto(cursoDTO.getId());
                
                if (curso != null) {
                    logger.info("✅ Curso cargado exitosamente");
                    logger.debug("📊 Detalles del curso - ID: '{}', Título: '{}', Bloques: {}, Total Preguntas: {}", 
                        curso.getId(), curso.getTitulo(), curso.getBloques().size(), curso.getNumeroPreguntas());
                    
                    // Log detallado de bloques
                    logger.debug("📋 Información detallada de bloques:");
                    for (int i = 0; i < curso.getBloques().size(); i++) {
                        Bloque bloque = curso.getBloques().get(i);
                        logger.debug("   Bloque {}: ID='{}', Título='{}', Tipo='{}', Preguntas={}", 
                            i + 1, bloque.getId(), bloque.getTitulo(), bloque.getTipo(), bloque.getPreguntas().size());
                    }
                } else {
                    logger.warn("⚠️  No se pudo cargar el curso completo para ID: '{}' - CursoManager retornó null", cursoDTO.getId());
                }
            } catch (Exception e) {
                logger.error("❌ Error al cargar curso completo para ID: '{}'", cursoDTO.getId(), e);
                logger.error("🔍 Detalles del error: {}", e.getMessage());
                logger.debug("📚 Stack trace completo:", e);
            }
        } else {
            logger.warn("⚠️  No se puede cargar curso completo - cursoDTO es null");
        }
        
        // Crear ventana modal
        logger.debug("🖼️  Creando Stage para diálogo modal...");
        Stage dialog = new Stage();
        
        try {
            if (owner != null) {
                dialog.initOwner(owner);
                logger.debug("✅ Owner configurado correctamente");
            } else {
                logger.warn("⚠️  No se pudo configurar owner - owner es null");
            }
            
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);
            dialog.setTitle("🔍 Inspección de Curso");
            logger.info("✅ Stage modal configurado correctamente - Título: '{}'", dialog.getTitle());
        } catch (Exception e) {
            logger.error("❌ Error al configurar Stage modal", e);
        }

        // Crear contenido
        logger.debug("🎨 Creando contenido del diálogo...");
        VBox root = new VBox(18);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f8f9fa;");
        logger.debug("✅ Contenedor principal creado con padding: 24px, espaciado: 18px");

        // Título
        logger.debug("📝 Creando título del diálogo...");
        Label titulo = new Label("🔍 Inspección de Curso");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setTextFill(Color.web("#2c3e50"));
        titulo.setAlignment(Pos.CENTER);
        root.getChildren().add(titulo);
        root.getChildren().add(new Separator());
        logger.debug("✅ Título del diálogo creado y agregado");

        if (curso == null) {
            logger.warn("⚠️  Mostrando mensaje de error - curso no disponible");
            Label error = new Label("No se pudo cargar el curso completo.");
            error.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 16px;");
            root.getChildren().add(error);
            logger.debug("✅ Mensaje de error agregado al contenido");
        } else {
            logger.info("📋 Creando información del curso...");
            VBox info = new VBox(6);
            
            // Crear campos de información
            logger.debug("🔧 Creando campos de información básica...");
            info.getChildren().addAll(
                crearCampo("Código (ID):", curso.getId()),
                crearCampo("Fichero:", cursoDTO.getId() + ".yaml"),
                crearCampo("Título:", curso.getTitulo()),
                crearCampo("Descripción:", curso.getDescripcion()),
                crearCampo("Total de preguntas:", String.valueOf(curso.getNumeroPreguntas()))
            );
            root.getChildren().add(info);
            root.getChildren().add(new Separator());
            logger.debug("✅ Información básica del curso agregada - {} campos creados", info.getChildren().size());
            
            // Procesar bloques
            logger.info("📚 Procesando {} bloques del curso", curso.getBloques().size());
            int bloquesProcesados = 0;
            for (Bloque bloque : curso.getBloques()) {
                logger.debug("🔄 Procesando bloque {}/{} - ID: '{}', Título: '{}', Tipo: '{}', Preguntas: {}", 
                    bloquesProcesados + 1, curso.getBloques().size(), 
                    bloque.getId(), bloque.getTitulo(), bloque.getTipo(), bloque.getPreguntas().size());
                
                try {
                    VBox bloqueBox = new VBox(3);
                    bloqueBox.setPadding(new Insets(8, 0, 8, 12));
                    bloqueBox.setStyle("-fx-background-color: #eef2f7; -fx-background-radius: 6;");
                    
                    // Crear campos del bloque
                    logger.debug("🔧 Creando campos para bloque '{}'", bloque.getTitulo());
                    bloqueBox.getChildren().addAll(
                        crearCampo("Bloque:", bloque.getTitulo()),
                        crearCampo("Descripción:", bloque.getDescripcion()),
                        crearCampo("Tipo de preguntas:", bloque.getTipo()),
                        crearCampo("Total preguntas:", String.valueOf(bloque.getPreguntas().size()))
                    );
                    
                    root.getChildren().add(bloqueBox);
                    root.getChildren().add(new Separator());
                    bloquesProcesados++;
                    logger.debug("✅ Bloque '{}' procesado correctamente", bloque.getTitulo());
                } catch (Exception e) {
                    logger.error("❌ Error al procesar bloque '{}'", bloque.getTitulo(), e);
                    logger.warn("⚠️  Continuando con el siguiente bloque...");
                }
            }
            logger.info("✅ Todos los bloques procesados correctamente - {} de {} bloques", bloquesProcesados, curso.getBloques().size());
        }

        // Botón cerrar
        logger.debug("🔘 Creando botón de cerrar...");
        Button cerrar = new Button("Cerrar");
        cerrar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-padding: 8 20;");
        cerrar.setOnAction(e -> {
            logger.info("👤 Usuario cerró el diálogo de inspección");
            logger.debug("🔄 Cerrando Stage modal...");
            dialog.close();
            logger.debug("✅ Stage modal cerrado correctamente");
        });
        
        VBox botonera = new VBox(10);
        botonera.setAlignment(Pos.CENTER);
        botonera.getChildren().add(cerrar);
        root.getChildren().add(botonera);
        logger.debug("✅ Botón de cerrar creado y agregado");

        // Crear ScrollPane
        logger.debug("📜 Creando ScrollPane para el contenido...");
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(500);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        logger.debug("✅ ScrollPane configurado - Altura: 500px, FitToWidth: true");

        // Crear escena y mostrar diálogo
        logger.info("🎭 Creando escena y mostrando diálogo modal...");
        try {
            Scene scene = new Scene(scroll, 480, 600);
            dialog.setScene(scene);
            dialog.centerOnScreen();
            logger.debug("✅ Escena creada - Dimensiones: 480x600, Centrada en pantalla");
            
            logger.info("================================================");
            logger.info("🎬 MOSTRANDO: Diálogo de inspección de curso");
            logger.info("================================================");
            dialog.showAndWait();
            
            logger.info("================================================");
            logger.info("✅ FINALIZADO: Diálogo de inspección de curso cerrado");
            logger.info("================================================");
        } catch (Exception e) {
            logger.error("❌ Error al mostrar el diálogo modal", e);
            logger.error("🔍 Detalles del error: {}", e.getMessage());
        }
    }

    private static HBox crearCampo(String label, String valor) {
        logger.debug("🔧 Creando campo - Label: '{}', Valor: '{}'", label, valor);
        
        try {
            Label l = new Label(label);
            l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            l.setTextFill(Color.web("#34495e"));
            
            Label v = new Label(valor != null ? valor : "");
            v.setFont(Font.font("Segoe UI", 12));
            v.setTextFill(Color.web("#2c3e50"));
            
            HBox box = new HBox(8, l, v);
            box.setAlignment(Pos.CENTER_LEFT);
            
            logger.debug("✅ Campo creado exitosamente - Label: '{}'", label);
            return box;
        } catch (Exception e) {
            logger.error("❌ Error al crear campo - Label: '{}', Valor: '{}'", label, valor, e);
            logger.warn("⚠️  Retornando campo vacío debido al error");
            
            // Retornar un campo vacío en caso de error
            Label errorLabel = new Label("Error al crear campo");
            errorLabel.setStyle("-fx-text-fill: #c0392b;");
            HBox errorBox = new HBox(8, errorLabel);
            errorBox.setAlignment(Pos.CENTER_LEFT);
            return errorBox;
        }
    }
} 