package com.kursor.ui.main;

import com.kursor.yaml.dto.CursoDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
     * Crea los botones de acción
     */
    private HBox createActionButtons() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button newSessionBtn = new Button("🆕 Nueva Sesión");
        newSessionBtn.getStyleClass().add("action-button");
        newSessionBtn.setOnAction(e -> {
            if (onNewSession != null) onNewSession.run();
        });
        
        Button resumeSessionBtn = new Button("▶️ Reanudar Sesión");
        resumeSessionBtn.getStyleClass().add("action-button");
        resumeSessionBtn.setOnAction(e -> {
            if (onResumeSession != null) onResumeSession.run();
        });
        
        Button statisticsBtn = new Button("📈 Estadísticas");
        statisticsBtn.getStyleClass().add("action-button");
        statisticsBtn.setOnAction(e -> {
            if (onShowStatistics != null) onShowStatistics.run();
        });
        
        Button aboutBtn = new Button("ℹ️ Acerca de");
        aboutBtn.getStyleClass().add("action-button");
        aboutBtn.setOnAction(e -> {
            if (onShowAbout != null) onShowAbout.run();
        });
        
        Button exitBtn = new Button("🚪 Terminar");
        exitBtn.getStyleClass().add("action-button");
        exitBtn.setOnAction(e -> {
            if (onExitApplication != null) onExitApplication.run();
        });
        
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
            VBox infoContainer = new VBox(10);
            infoContainer.setPadding(new Insets(10, 0, 0, 0));
            
            Label nameLabel = new Label("📖 " + curso.getTitulo());
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label descriptionLabel = new Label(curso.getDescripcion());
            descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d; -fx-wrap-text: true;");
            descriptionLabel.setMaxWidth(550);
            
            // Estadísticas
            HBox statsContainer = new HBox(20);
            statsContainer.setAlignment(Pos.CENTER_LEFT);
            
            VBox blocksStats = new VBox(5);
            blocksStats.setAlignment(Pos.CENTER);
            Label blocksLabel = new Label("📦 Bloques");
            blocksLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
            Label blocksValue = new Label(String.valueOf(curso.getTotalBloques()));
            blocksValue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #3498db;");
            blocksStats.getChildren().addAll(blocksValue, blocksLabel);
            
            VBox questionsStats = new VBox(5);
            questionsStats.setAlignment(Pos.CENTER);
            Label questionsLabel = new Label("❓ Preguntas");
            questionsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
            Label questionsValue = new Label(String.valueOf(curso.getTotalPreguntas()));
            questionsValue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            questionsStats.getChildren().addAll(questionsValue, questionsLabel);
            
            statsContainer.getChildren().addAll(blocksStats, questionsStats);
            
            // Tipos de preguntas
            if (curso.getBloques() != null && !curso.getBloques().isEmpty()) {
                VBox typesContainer = new VBox(10);
                typesContainer.setPadding(new Insets(10, 0, 0, 0));
                
                Label typesLabel = new Label("🎯 Tipos de Preguntas:");
                typesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                typesContainer.getChildren().add(typesLabel);
                
                HBox typesList = new HBox(10);
                typesList.setAlignment(Pos.CENTER_LEFT);
                
                // Contar tipos de preguntas
                java.util.Map<String, Long> typeCounts = curso.getBloques().stream()
                    .flatMap(bloque -> bloque.getPreguntas().stream())
                    .collect(java.util.stream.Collectors.groupingBy(
                        pregunta -> pregunta.getTipo(),
                        java.util.stream.Collectors.counting()
                    ));
                
                for (java.util.Map.Entry<String, Long> entry : typeCounts.entrySet()) {
                    VBox typeBox = new VBox(2);
                    typeBox.setAlignment(Pos.CENTER);
                    typeBox.setPadding(new Insets(5, 10, 5, 10));
                    typeBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 4; -fx-background-radius: 4;");
                    
                    Label typeIcon = new Label(getModuleIcon(entry.getKey()));
                    typeIcon.setStyle("-fx-font-size: 16px;");
                    
                    Label typeName = new Label(entry.getKey());
                    typeName.setStyle("-fx-font-size: 10px; -fx-text-fill: #6c757d;");
                    
                    Label typeCount = new Label(String.valueOf(entry.getValue()));
                    typeCount.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    
                    typeBox.getChildren().addAll(typeIcon, typeName, typeCount);
                    typesList.getChildren().add(typeBox);
                }
                
                typesContainer.getChildren().add(typesList);
                infoContainer.getChildren().add(typesContainer);
            }
            
            infoContainer.getChildren().addAll(nameLabel, descriptionLabel, statsContainer);
            courseDetailsContainer.getChildren().add(infoContainer);
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
     * Obtiene el icono para un tipo de pregunta
     */
    private String getModuleIcon(String tipoPregunta) {
        return switch (tipoPregunta.toLowerCase()) {
            case "multiplechoice" -> "📝";
            case "truefalse" -> "✅❌";
            case "flashcard" -> "💡";
            case "fillblanks" -> "🔤";
            default -> "❓";
        };
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
