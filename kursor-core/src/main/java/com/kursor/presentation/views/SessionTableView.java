package com.kursor.ui.session;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

/**
 * Vista de tabla para mostrar el historial de sesiones de aprendizaje.
 * 
 * <p>Esta clase implementa una tabla JavaFX que muestra información
 * detallada sobre las sesiones de un curso, incluyendo fechas, bloques,
 * respuestas correctas y preguntas pendientes.</p>
 * 
 * <p>Características de la vista:</p>
 * <ul>
 *   <li>Tabla responsive con 4 columnas principales</li>
 *   <li>Diseño moderno con estilos CSS personalizados</li>
 *   <li>Integración con SessionViewModel para datos</li>
 *   <li>Placeholder informativo cuando no hay datos</li>
 *   <li>Configuración automática de columnas</li>
 * </ul>
 * 
 * <p>Estructura de columnas:</p>
 * <ul>
 *   <li><strong>Fecha:</strong> Fecha de la sesión (150px)</li>
 *   <li><strong>Bloque:</strong> Nombre del bloque de contenido (200px)</li>
 *   <li><strong>Correctas:</strong> Respuestas correctas/total (100px)</li>
 *   <li><strong>Pendientes:</strong> Preguntas pendientes (100px)</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SessionViewModel
 * @see SessionController
 * @see SessionViewModel.SessionData
 */
public class SessionTableView extends VBox {
    private final TableView<SessionViewModel.SessionData> table;

    /**
     * Constructor para crear la vista de tabla de sesiones.
     * 
     * <p>Inicializa la tabla con todas las columnas necesarias y configura
     * el diseño visual. La tabla se conecta automáticamente con el modelo
     * de vista proporcionado.</p>
     * 
     * @param viewModel El modelo de vista que proporciona los datos de sesiones
     */
    public SessionTableView(SessionViewModel viewModel) {
        this.table = new TableView<>();
        this.setPadding(new Insets(15));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Configurar columnas
        TableColumn<SessionViewModel.SessionData, String> dateColumn = new TableColumn<>("Fecha");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(150);

        TableColumn<SessionViewModel.SessionData, String> blockColumn = new TableColumn<>("Bloque");
        blockColumn.setCellValueFactory(new PropertyValueFactory<>("block"));
        blockColumn.setPrefWidth(200);

        TableColumn<SessionViewModel.SessionData, String> correctColumn = new TableColumn<>("Correctas");
        correctColumn.setCellValueFactory(new PropertyValueFactory<>("correct"));
        correctColumn.setPrefWidth(100);

        TableColumn<SessionViewModel.SessionData, String> pendingColumn = new TableColumn<>("Pendientes");
        pendingColumn.setCellValueFactory(new PropertyValueFactory<>("pending"));
        pendingColumn.setPrefWidth(100);

        table.getColumns().addAll(dateColumn, blockColumn, correctColumn, pendingColumn);
        table.setPlaceholder(new Label("No hay sesiones disponibles"));
        table.setItems(viewModel.getSessions());
        table.setPrefHeight(200);

        Label titleLabel = new Label("🕐 Historial de Sesiones");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        this.getChildren().addAll(titleLabel, table);
    }

    /**
     * Obtiene la tabla de sesiones.
     * 
     * <p>Permite acceso directo a la tabla para configuraciones adicionales
     * o para agregar listeners de eventos.</p>
     * 
     * @return La tabla de sesiones configurada
     */
    public TableView<SessionViewModel.SessionData> getTable() {
        return table;
    }
} 
