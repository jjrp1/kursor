package com.kursor.presentation.views;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.presentation.controllers.MainController;
import com.kursor.shared.util.ModuleManager;
import com.kursor.modules.PreguntaModule;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Orientation;
import java.util.List;
import java.util.function.Consumer;

/**
 * Vista principal de la aplicación Kursor.
 * Maneja toda la interfaz de usuario de la pantalla principal.
 */
public class MainView {
    
    private static final String APP_TITLE = "🎓 Kursor - Plataforma de Aprendizaje Interactivo";
    private static final String APP_VERSION = "1.0.0";
    private static final double MIN_WIDTH = 1400;
    private static final double MIN_HEIGHT = 900;
    
    private final MainController controller;
    private final Stage primaryStage;
    
    // Contenedores principales
    private VBox mainContainer;
    private ListView<String> courseListView;
    private VBox courseDetailsContainer;
    private TableView<SessionData> sessionsTable;
    private VBox bottomSection;
    private VBox sessionsContainer;
    
    // Callbacks para eventos
    private Consumer<Integer> onCourseSelected;
    private Runnable onNewSession;
    private Runnable onResumeSession;
    private Runnable onShowStatistics;
    private Runnable onShowAbout;
    private Runnable onExitApplication;
    
    // Referencia al botón de reanudar para controlar su estado
    private Button resumeSessionBtn;
    
    public MainView(MainController controller) {
        this.controller = controller;
        this.primaryStage = (Stage) controller.getPrimaryWindow();
    }
    
    /**
     * Crea la interfaz principal
     */
    public void createMainInterface() {
        mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f5f6fa;");
        
        // Header
        Node header = createHeader();
        
        // Contenedor principal dividido en superior e inferior
        VBox mainContent = new VBox(20);
        
        // Parte superior: Cursos (izquierda) y detalles (derecha)
        HBox topSection = createTopSection();
        
        // Parte inferior: Sesiones y botones
        bottomSection = createBottomSection();
        
        mainContent.getChildren().addAll(topSection, bottomSection);
        mainContainer.getChildren().addAll(header, mainContent);
        
        // Configurar la escena
        Scene scene = new Scene(mainContainer);
        scene.getStylesheets().add(getClass().getResource("/styles/kursor.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setWidth(MIN_WIDTH);
        primaryStage.setHeight(MIN_HEIGHT);
    }
    
    /**
     * Crea el header de la aplicación
     */
    private Node createHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 10, 0));

        VBox titleBox = new VBox(0);
        titleBox.setAlignment(Pos.CENTER);
        Label titleLabel = new Label(APP_TITLE);
        titleLabel.getStyleClass().add("header-title");
        Label subtitleLabel = new Label("Plataforma de Aprendizaje Interactivo v" + APP_VERSION);
        subtitleLabel.getStyleClass().add("header-subtitle");
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    /**
     * Crea la sección superior con lista de cursos y detalles
     */
    private HBox createTopSection() {
        HBox topSection = new HBox(20);
        topSection.setAlignment(Pos.TOP_LEFT);
        
        // Lista de cursos (izquierda)
        VBox courseListContainer = createCourseListContainer();
        
        // Detalles del curso (derecha)
        courseDetailsContainer = createCourseDetailsContainer();
        
        topSection.getChildren().addAll(courseListContainer, courseDetailsContainer);
        
        return topSection;
    }
    
    /**
     * Crea el contenedor de la lista de cursos
     */
    private VBox createCourseListContainer() {
        VBox container = new VBox(10);
        container.setPrefWidth(400);
        container.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
        container.setPadding(new Insets(15));
        
        Label titleLabel = new Label("📚 Cursos Disponibles");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        courseListView = new ListView<>();
        courseListView.setPrefHeight(300);
        courseListView.getStyleClass().add("course-list");
        
        // Configurar evento de selección
        courseListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0 && onCourseSelected != null) {
                onCourseSelected.accept(newVal.intValue());
            }
        });
        
        container.getChildren().addAll(titleLabel, courseListView);
        
        return container;
    }
    
    /**
     * Crea el contenedor de detalles del curso
     */
    private VBox createCourseDetailsContainer() {
        VBox container = new VBox(15);
        container.setPrefWidth(600);
        container.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
        container.setPadding(new Insets(15));
        
        Label titleLabel = new Label("📋 Detalles del Curso");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        container.getChildren().add(titleLabel);
        
        return container;
    }
    
    /**
     * Crea la sección inferior con sesiones y botones
     */
    private VBox createBottomSection() {
        VBox bottomSection = new VBox(20);
        
        // Contenedor de sesiones
        sessionsContainer = createSessionsContainer();
        
        // Botones de acción
        HBox actionButtons = createActionButtons();
        
        bottomSection.getChildren().addAll(sessionsContainer, actionButtons);
        
        return bottomSection;
    }
    
    /**
     * Crea el contenedor de sesiones
     */
    private VBox createSessionsContainer() {
        VBox container = new VBox(10);
        container.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
        container.setPadding(new Insets(15));
        
        Label titleLabel = new Label("📊 Historial de Sesiones");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Tabla de sesiones
        sessionsTable = new TableView<>();
        sessionsTable.setPrefHeight(200);
        sessionsTable.setPlaceholder(new Label("No hay sesiones disponibles"));
        
        // Configurar columnas
        TableColumn<SessionData, String> dateColumn = new TableColumn<>("Fecha");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(150);
        
        TableColumn<SessionData, String> blockColumn = new TableColumn<>("Bloque");
        blockColumn.setCellValueFactory(new PropertyValueFactory<>("block"));
        blockColumn.setPrefWidth(200);
        
        TableColumn<SessionData, String> correctColumn = new TableColumn<>("Correctas");
        correctColumn.setCellValueFactory(new PropertyValueFactory<>("correct"));
        correctColumn.setPrefWidth(100);
        
        TableColumn<SessionData, String> pendingColumn = new TableColumn<>("Pendientes");
        pendingColumn.setCellValueFactory(new PropertyValueFactory<>("pending"));
        pendingColumn.setPrefWidth(100);
        
        sessionsTable.getColumns().addAll(dateColumn, blockColumn, correctColumn, pendingColumn);
        
        container.getChildren().addAll(titleLabel, sessionsTable);
        
        return container;
    }
    
    /**
     * Crea un botón con emoji colorido usando VBox para separar emoji y texto
     * 
     * @param emoji El emoji a mostrar
     * @param text El texto del botón
     * @param action La acción a ejecutar
     * @return El botón creado
     */
    private Button createColoredButton(String emoji, String text, Runnable action) {
        Button button = new Button();
        button.getStyleClass().add("action-button");
        button.setOnAction(e -> action.run());
        
        // Crear VBox para emoji arriba y texto abajo
        VBox contentBox = new VBox(2);
        contentBox.setAlignment(Pos.CENTER);
        
        // Emoji con fuente colorida
        Text emojiText = new Text(emoji);
        emojiText.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        emojiText.setStyle("-fx-fill: inherit;"); // Hereda el color del botón
        
        // Texto del botón
        Text labelText = new Text(text);
        labelText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        labelText.setStyle("-fx-fill: inherit;"); // Hereda el color del botón
        
        contentBox.getChildren().addAll(emojiText, labelText);
        button.setGraphic(contentBox);
        
        return button;
    }
    
    /**
     * Crea los botones de acción
     */
    private HBox createActionButtons() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button newSessionBtn = createColoredButton("⚡", "Nueva Sesión", () -> {
            if (onNewSession != null) onNewSession.run();
        });
        newSessionBtn.getStyleClass().add("btn-new-session");
        
        resumeSessionBtn = createColoredButton("🎬", "Reanudar Sesión", () -> {
            if (onResumeSession != null) onResumeSession.run();
        });
        resumeSessionBtn.getStyleClass().add("btn-resume");
        resumeSessionBtn.setDisable(true); // Deshabilitado por defecto
        
        Button statisticsBtn = createColoredButton("📈", "Estadísticas", () -> {
            if (onShowStatistics != null) onShowStatistics.run();
        });
        statisticsBtn.getStyleClass().add("btn-statistics");
        
        Button aboutBtn = createColoredButton("🌟", "Acerca de", () -> {
            if (onShowAbout != null) onShowAbout.run();
        });
        aboutBtn.getStyleClass().add("btn-kursor");
        
        Button exitBtn = createColoredButton("🎯", "Terminar", () -> {
            if (onExitApplication != null) onExitApplication.run();
        });
        exitBtn.getStyleClass().add("btn-exit");
        
        buttonContainer.getChildren().addAll(newSessionBtn, resumeSessionBtn, statisticsBtn, aboutBtn, exitBtn);
        
        return buttonContainer;
    }
    
    /**
     * Actualiza la lista de cursos
     */
    public void updateCourseList(List<CursoDTO> cursos) {
        ObservableList<String> courseNames = FXCollections.observableArrayList();
        for (CursoDTO curso : cursos) {
            courseNames.add(curso.getTitulo());
        }
        courseListView.setItems(courseNames);
    }
    
    /**
     * Selecciona un curso en la lista
     */
    public void selectCourse(int index) {
        if (index >= 0 && index < courseListView.getItems().size()) {
            courseListView.getSelectionModel().select(index);
        }
    }
    
    /**
     * Actualiza los detalles del curso
     */
    public void updateCourseDetails(CursoDTO curso) {
        courseDetailsContainer.getChildren().clear();
        
        // Título del curso
        Label titleLabel = new Label("📋 Detalles del Curso");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        courseDetailsContainer.getChildren().add(titleLabel);
        
        if (curso != null) {
            // Información del curso
            VBox infoContainer = new VBox(15);
            infoContainer.setPadding(new Insets(10, 0, 0, 0));
            
            // Título del curso
            Label nameLabel = new Label("📖 " + curso.getTitulo());
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            // Descripción del curso (manejar null y vacío)
            String descripcion = curso.getDescripcion();
            if (descripcion != null && !descripcion.trim().isEmpty()) {
                Label descriptionLabel = new Label(descripcion.trim());
                descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d; -fx-wrap-text: true;");
                descriptionLabel.setMaxWidth(550);
                infoContainer.getChildren().add(descriptionLabel);
            }
            
            // Total de preguntas y nombre del archivo
            HBox courseInfoContainer = new HBox(10);
            courseInfoContainer.setAlignment(Pos.CENTER_LEFT);
            
            Label totalQuestionsLabel = new Label("🎯 Total de preguntas: " + curso.getTotalPreguntas());
            totalQuestionsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            
            // Obtener el nombre del archivo del curso
            String fileName = getCourseFileName(curso);
            Label fileNameLabel = new Label("(" + fileName + ")");
            fileNameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");
            
            courseInfoContainer.getChildren().addAll(totalQuestionsLabel, fileNameLabel);
            
            infoContainer.getChildren().addAll(nameLabel, courseInfoContainer);
            
            // Lista de bloques
            if (curso.getBloques() != null && !curso.getBloques().isEmpty()) {
                VBox blocksContainer = new VBox(10);
                blocksContainer.setPadding(new Insets(10, 0, 0, 0));
                
                Label blocksTitleLabel = new Label("🧩 Bloques de Preguntas");
                blocksTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                blocksContainer.getChildren().add(blocksTitleLabel);
                
                // ListView para los bloques
                ListView<HBox> blocksListView = new ListView<>();
                blocksListView.setPrefHeight(300);
                blocksListView.setStyle("-fx-background-color: transparent; -fx-border-color: #dee2e6; -fx-border-radius: 4; -fx-background-radius: 4;");
                
                ObservableList<HBox> blocksItems = FXCollections.observableArrayList();
                
                for (com.kursor.yaml.dto.BloqueDTO bloque : curso.getBloques()) {
                    HBox blockItem = createBlockItem(bloque);
                    blocksItems.add(blockItem);
                }
                
                blocksListView.setItems(blocksItems);
                blocksContainer.getChildren().add(blocksListView);
                infoContainer.getChildren().add(blocksContainer);
            }
            
            courseDetailsContainer.getChildren().add(infoContainer);
        }
    }
    
    /**
     * Crea un elemento de bloque para mostrar en la lista
     */
    private HBox createBlockItem(com.kursor.yaml.dto.BloqueDTO bloque) {
        HBox blockItem = new HBox(10);
        blockItem.setAlignment(Pos.CENTER_LEFT);
        blockItem.setPadding(new Insets(10));
        blockItem.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 4; -fx-background-radius: 4; -fx-border-width: 1;");
        
        // Icono del tipo de pregunta (usar el tipo del bloque o el primer tipo encontrado en las preguntas)
        String questionType = bloque.getTipo();
        if ((questionType == null || questionType.trim().isEmpty()) && 
            bloque.getPreguntas() != null && !bloque.getPreguntas().isEmpty()) {
            questionType = bloque.getPreguntas().get(0).getTipo();
        }
        
        Label typeIcon = new Label(getModuleIcon(questionType));
        typeIcon.setStyle("-fx-font-size: 20px;");
        typeIcon.setPrefWidth(30);
        typeIcon.setAlignment(Pos.CENTER);
        
        // Contenedor de información del bloque
        VBox blockInfo = new VBox(5);
        blockInfo.setAlignment(Pos.CENTER_LEFT);
        
        // Título del bloque
        Label blockTitle = new Label(bloque.getTitulo());
        blockTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Total de preguntas del bloque
        int totalQuestions = bloque.getPreguntas() != null ? bloque.getPreguntas().size() : 0;
        Label questionsCount = new Label("📝 " + totalQuestions + " preguntas");
        questionsCount.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        // Agregar título y conteo de preguntas
        blockInfo.getChildren().addAll(blockTitle, questionsCount);
        
        // Descripción del bloque (manejar null y vacío)
        String descripcion = bloque.getDescripcion();
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            Label blockDescription = new Label(descripcion.trim());
            blockDescription.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-wrap-text: true;");
            blockDescription.setMaxWidth(400);
            blockInfo.getChildren().add(blockDescription);
        }
        
        blockItem.getChildren().addAll(typeIcon, blockInfo);
        
        return blockItem;
    }
    
    /**
     * Obtiene el nombre del archivo del curso
     */
    private String getCourseFileName(CursoDTO curso) {
        // Usar el campo nombreArchivo del CursoDTO si está disponible
        String nombreArchivo = curso.getNombreArchivo();
        if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
            return nombreArchivo.trim();
        }
        
        // Fallback: generar nombre basado en el ID del curso
        try {
            return curso.getId() + ".yaml";
        } catch (Exception e) {
            return "curso.yaml";
        }
    }
    
    /**
     * Actualiza la tabla de sesiones
     */
    public void updateSessionsTable(List<SessionData> sessions) {
        ObservableList<SessionData> sessionsData = FXCollections.observableArrayList(sessions);
        sessionsTable.setItems(sessionsData);
    }
    
    /**
     * Obtiene el icono para un tipo de pregunta desde el módulo correspondiente
     */
    private String getModuleIcon(String tipoPregunta) {
        try {
            PreguntaModule module = ModuleManager.getInstance().findModuleByQuestionType(tipoPregunta);
            if (module != null) {
                return module.getIcon();
            }
        } catch (Exception e) {
            // Log del error pero no fallar la aplicación
            System.err.println("Error al obtener icono para tipo de pregunta '" + tipoPregunta + "': " + e.getMessage());
        }
        return "❓"; // Icono por defecto si no se encuentra el módulo
    }
    
    // Setters para los callbacks de eventos
    public void setOnCourseSelected(Consumer<Integer> callback) {
        this.onCourseSelected = callback;
    }
    
    public void setOnNewSession(Runnable callback) {
        this.onNewSession = callback;
    }
    
    public void setOnResumeSession(Runnable callback) {
        this.onResumeSession = callback;
    }
    
    public void setOnShowStatistics(Runnable callback) {
        this.onShowStatistics = callback;
    }
    
    public void setOnShowAbout(Runnable callback) {
        this.onShowAbout = callback;
    }
    
    public void setOnExitApplication(Runnable callback) {
        this.onExitApplication = callback;
    }
    
    public void setSessionTableView(javafx.scene.Node sessionTableView) {
        if (bottomSection != null && sessionsContainer != null) {
            int idx = bottomSection.getChildren().indexOf(sessionsContainer);
            if (idx != -1) {
                bottomSection.getChildren().set(idx, sessionTableView);
                sessionsContainer = null;
            }
        }
    }
    
    /**
     * Habilita o deshabilita el botón de reanudar sesión.
     * 
     * @param enabled true para habilitar, false para deshabilitar
     */
    public void setResumeButtonEnabled(boolean enabled) {
        if (resumeSessionBtn != null) {
            resumeSessionBtn.setDisable(!enabled);
            
            // Cambiar el estilo visual para indicar el estado
            if (enabled) {
                resumeSessionBtn.setStyle("-fx-opacity: 1.0;");
                resumeSessionBtn.setTooltip(null);
            } else {
                resumeSessionBtn.setStyle("-fx-opacity: 0.5;");
                resumeSessionBtn.setTooltip(new Tooltip("No hay sesiones disponibles para reanudar"));
            }
        }
    }
    
    /**
     * Clase para representar los datos de sesión
     */
    public static class SessionData {
        private final String date;
        private final String block;
        private final String correct;
        private final String pending;
        
        public SessionData(String date, String block, String correct, String pending) {
            this.date = date;
            this.block = block;
            this.correct = correct;
            this.pending = pending;
        }
        
        public String getDate() { return date; }
        public String getBlock() { return block; }
        public String getCorrect() { return correct; }
        public String getPending() { return pending; }
    }
} 
