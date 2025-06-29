package com.kursor.ui.analytics;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Cuadro de diálogo para mostrar estadísticas avanzadas de aprendizaje.
 * 
 * <p>Este diálogo proporciona una interfaz completa para visualizar analytics
 * del usuario, incluyendo métricas de rendimiento, tendencias temporales,
 * comparación de estrategias y recomendaciones personalizadas.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Filtros jerárquicos por curso, bloque y período</li>
 *   <li>Dashboard con métricas principales</li>
 *   <li>Gráficos interactivos de tendencias y bloques</li>
 *   <li>Recomendaciones personalizadas</li>
 *   <li>Comparación detallada de estrategias</li>
 *   <li>Diseño responsive y moderno</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class AnalyticsDialog extends Stage {
    
    private final AnalyticsController controller;
    private final VBox mainContainer;
    
    // Componentes de filtros
    private ComboBox<String> cursoFilter;
    private ComboBox<String> bloqueFilter;
    private ComboBox<String> periodoFilter;
    
    // Componentes de métricas
    private Label porcentajeExitoLabel;
    private Label velocidadLabel;
    private Label sesionesLabel;
    private Label progresoLabel;
    
    // Componentes de gráficos
    private LineChart<String, Number> tendenciasChart;
    private PieChart bloquesChart;
    
    // Componentes de recomendaciones
    private VBox recomendacionesContainer;
    
    // Componentes de estrategias
    private TableView<EstrategiaData> estrategiasTable;
    
    /**
     * Constructor del diálogo de analytics.
     * 
     * @param parentStage La ventana padre desde la cual se lanza el diálogo
     */
    public AnalyticsDialog(Stage parentStage) {
        this.controller = new AnalyticsController();
        this.mainContainer = new VBox(20);
        
        initDialog(parentStage);
        createHeader();
        createFilters();
        createMetricsCards();
        createCharts();
        createRecommendations();
        createStrategiesTable();
        
        loadInitialData();
    }
    
    /**
     * Inicializa la configuración básica del diálogo.
     * 
     * @param parentStage La ventana padre
     */
    private void initDialog(Stage parentStage) {
        setTitle("📊 Analytics Avanzados - Kursor");
        setWidth(1200);
        setHeight(800);
        setResizable(true);
        
        initModality(Modality.APPLICATION_MODAL);
        initOwner(parentStage);
        
        Scene scene = new Scene(mainContainer);
        scene.getStylesheets().add(getClass().getResource("/styles/analytics.css").toExternalForm());
        setScene(scene);
        
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);
    }
    
    /**
     * Crea el encabezado del diálogo.
     */
    private void createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("📊 Analytics Avanzados - Kursor");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #4a5568;");
        
        Label subtitleLabel = new Label("Análisis detallado de tu progreso de aprendizaje");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        mainContainer.getChildren().add(header);
    }
    
    /**
     * Crea la sección de filtros.
     */
    private void createFilters() {
        VBox filtersContainer = new VBox(15);
        filtersContainer.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 10; -fx-padding: 20;");
        
        Label filtersTitle = new Label("Filtros de Análisis");
        filtersTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        filtersTitle.setStyle("-fx-text-fill: #4a5568;");
        
        HBox filtersRow = new HBox(15);
        filtersRow.setAlignment(Pos.CENTER_LEFT);
        
        // Filtro de curso
        VBox cursoBox = new VBox(5);
        Label cursoLabel = new Label("Curso");
        cursoLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        cursoFilter = new ComboBox<>();
        cursoFilter.getItems().addAll("Todos los cursos", "Inglés B2", "Matemáticas", "Historia");
        cursoFilter.setValue("Inglés B2");
        cursoFilter.setPrefWidth(150);
        cursoBox.getChildren().addAll(cursoLabel, cursoFilter);
        
        // Filtro de bloque
        VBox bloqueBox = new VBox(5);
        Label bloqueLabel = new Label("Bloque");
        bloqueLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        bloqueFilter = new ComboBox<>();
        bloqueFilter.getItems().addAll("Todos los bloques", "Vocabulario", "Gramática", "Pronunciación");
        bloqueFilter.setValue("Gramática");
        bloqueFilter.setPrefWidth(150);
        bloqueBox.getChildren().addAll(bloqueLabel, bloqueFilter);
        
        // Filtro de período
        VBox periodoBox = new VBox(5);
        Label periodoLabel = new Label("Período");
        periodoLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        periodoFilter = new ComboBox<>();
        periodoFilter.getItems().addAll("Este mes", "Esta semana", "Última sesión");
        periodoFilter.setValue("Este mes");
        periodoFilter.setPrefWidth(150);
        periodoBox.getChildren().addAll(periodoLabel, periodoFilter);
        
        // Botón de actualizar
        Button updateButton = new Button("🔄 Actualizar");
        updateButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        updateButton.setOnAction(e -> updateAnalytics());
        
        filtersRow.getChildren().addAll(cursoBox, bloqueBox, periodoBox, updateButton);
        filtersContainer.getChildren().addAll(filtersTitle, filtersRow);
        
        mainContainer.getChildren().add(filtersContainer);
    }
    
    /**
     * Crea las tarjetas de métricas principales.
     */
    private void createMetricsCards() {
        HBox metricsGrid = new HBox(20);
        metricsGrid.setAlignment(Pos.CENTER);
        
        // Tarjeta de porcentaje de éxito
        VBox exitoCard = createMetricCard("Porcentaje de Éxito", "87.5%", "↗ +5.2%", true);
        
        // Tarjeta de velocidad
        VBox velocidadCard = createMetricCard("Velocidad Promedio", "2.3s", "↗ -0.4s", true);
        
        // Tarjeta de sesiones
        VBox sesionesCard = createMetricCard("Sesiones", "24", "↗ +3", true);
        
        // Tarjeta de progreso
        VBox progresoCard = createMetricCard("Progreso", "68%", "↗ +8%", true);
        
        metricsGrid.getChildren().addAll(exitoCard, velocidadCard, sesionesCard, progresoCard);
        mainContainer.getChildren().add(metricsGrid);
    }
    
    /**
     * Crea una tarjeta de métrica individual.
     * 
     * @param title El título de la métrica
     * @param value El valor principal
     * @param change El cambio respecto al período anterior
     * @param isPositive Si el cambio es positivo
     * @return La tarjeta creada
     */
    private VBox createMetricCard(String title, String value, String change, boolean isPositive) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        titleLabel.setStyle("-fx-text-fill: #718096;");
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: #4a5568;");
        
        Label changeLabel = new Label(change);
        changeLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
        changeLabel.setStyle(isPositive ? "-fx-text-fill: #48bb78;" : "-fx-text-fill: #f56565;");
        
        card.getChildren().addAll(titleLabel, valueLabel, changeLabel);
        return card;
    }
    
    /**
     * Crea los gráficos de tendencias y bloques.
     */
    private void createCharts() {
        HBox chartsGrid = new HBox(20);
        chartsGrid.setAlignment(Pos.CENTER);
        
        // Gráfico de tendencias
        VBox tendenciasContainer = createChartContainer("📈 Tendencias de Rendimiento");
        tendenciasChart = createTendenciasChart();
        tendenciasContainer.getChildren().add(tendenciasChart);
        
        // Gráfico de bloques
        VBox bloquesContainer = createChartContainer("🎯 Rendimiento por Bloque");
        bloquesChart = createBloquesChart();
        bloquesContainer.getChildren().add(bloquesChart);
        
        chartsGrid.getChildren().addAll(tendenciasContainer, bloquesContainer);
        mainContainer.getChildren().add(chartsGrid);
    }
    
    /**
     * Crea un contenedor para gráficos.
     * 
     * @param title El título del gráfico
     * @return El contenedor creado
     */
    private VBox createChartContainer(String title) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setPrefWidth(500);
        container.setPrefHeight(350);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #4a5568;");
        titleLabel.setAlignment(Pos.CENTER);
        
        container.getChildren().add(titleLabel);
        return container;
    }
    
    /**
     * Crea el gráfico de tendencias.
     * 
     * @return El gráfico de líneas creado
     */
    private LineChart<String, Number> createTendenciasChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 20);
        yAxis.setLabel("Porcentaje de Éxito (%)");
        
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("");
        chart.setLegendVisible(true);
        chart.setPrefHeight(280);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Porcentaje de Éxito");
        series.getData().add(new XYChart.Data<>("1 Jun", 75));
        series.getData().add(new XYChart.Data<>("8 Jun", 78));
        series.getData().add(new XYChart.Data<>("15 Jun", 82));
        series.getData().add(new XYChart.Data<>("22 Jun", 85));
        series.getData().add(new XYChart.Data<>("29 Jun", 87.5));
        
        chart.getData().add(series);
        return chart;
    }
    
    /**
     * Crea el gráfico de bloques.
     * 
     * @return El gráfico circular creado
     */
    private PieChart createBloquesChart() {
        PieChart chart = new PieChart();
        chart.setTitle("");
        chart.setPrefHeight(280);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Gramática", 92),
            new PieChart.Data("Vocabulario", 72),
            new PieChart.Data("Pronunciación", 85)
        );
        
        chart.setData(pieChartData);
        return chart;
    }
    
    /**
     * Crea la sección de recomendaciones.
     */
    private void createRecommendations() {
        VBox recommendationsContainer = new VBox(15);
        recommendationsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label titleLabel = new Label("💡 Recomendaciones Personalizadas");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #4a5568;");
        titleLabel.setAlignment(Pos.CENTER);
        
        recomendacionesContainer = new VBox(10);
        
        // Agregar recomendaciones de ejemplo
        addRecommendation("🎯", "Cambiar a Repetición Espaciada", "Tu rendimiento mejora un 15% con esta estrategia.");
        addRecommendation("⏰", "Optimizar Horario de Estudio", "Tu mejor rendimiento es entre 18:00-20:00.");
        addRecommendation("📚", "Enfócate en Vocabulario", "Este bloque tiene tu menor rendimiento (72%).");
        
        recommendationsContainer.getChildren().addAll(titleLabel, recomendacionesContainer);
        mainContainer.getChildren().add(recommendationsContainer);
    }
    
    /**
     * Agrega una recomendación a la lista.
     * 
     * @param icon El icono de la recomendación
     * @param title El título de la recomendación
     * @param description La descripción de la recomendación
     */
    private void addRecommendation(String icon, String title, String description) {
        HBox recommendationItem = new HBox(15);
        recommendationItem.setAlignment(Pos.CENTER_LEFT);
        recommendationItem.setPadding(new Insets(15));
        recommendationItem.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-border-color: #667eea; -fx-border-width: 0 0 0 4;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 20));
        
        VBox contentBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setStyle("-fx-text-fill: #4a5568;");
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", 12));
        descLabel.setStyle("-fx-text-fill: #718096;");
        
        contentBox.getChildren().addAll(titleLabel, descLabel);
        recommendationItem.getChildren().addAll(iconLabel, contentBox);
        
        recomendacionesContainer.getChildren().add(recommendationItem);
    }
    
    /**
     * Crea la tabla de comparación de estrategias.
     */
    private void createStrategiesTable() {
        VBox tableContainer = new VBox(15);
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label titleLabel = new Label("🔄 Comparación de Estrategias");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #4a5568;");
        titleLabel.setAlignment(Pos.CENTER);
        
        estrategiasTable = new TableView<>();
        estrategiasTable.setPrefHeight(200);
        
        // Crear columnas
        TableColumn<EstrategiaData, String> estrategiaCol = new TableColumn<>("Estrategia");
        estrategiaCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        estrategiaCol.setPrefWidth(150);
        
        TableColumn<EstrategiaData, String> exitoCol = new TableColumn<>("Éxito");
        exitoCol.setCellValueFactory(new PropertyValueFactory<>("exito"));
        exitoCol.setPrefWidth(80);
        
        TableColumn<EstrategiaData, String> velocidadCol = new TableColumn<>("Velocidad");
        velocidadCol.setCellValueFactory(new PropertyValueFactory<>("velocidad"));
        velocidadCol.setPrefWidth(80);
        
        TableColumn<EstrategiaData, String> sesionesCol = new TableColumn<>("Sesiones");
        sesionesCol.setCellValueFactory(new PropertyValueFactory<>("sesiones"));
        sesionesCol.setPrefWidth(80);
        
        TableColumn<EstrategiaData, String> recomendacionCol = new TableColumn<>("Recomendación");
        recomendacionCol.setCellValueFactory(new PropertyValueFactory<>("recomendacion"));
        recomendacionCol.setPrefWidth(120);
        
        estrategiasTable.getColumns().addAll(estrategiaCol, exitoCol, velocidadCol, sesionesCol, recomendacionCol);
        
        // Agregar datos de ejemplo
        ObservableList<EstrategiaData> data = FXCollections.observableArrayList(
            new EstrategiaData("Repetición Espaciada", "85%", "2.1s", "12", "Recomendada"),
            new EstrategiaData("Secuencial", "75%", "2.8s", "8", "Regular"),
            new EstrategiaData("Aleatoria", "72%", "3.2s", "4", "Evitar")
        );
        
        estrategiasTable.setItems(data);
        
        tableContainer.getChildren().addAll(titleLabel, estrategiasTable);
        mainContainer.getChildren().add(tableContainer);
    }
    
    /**
     * Carga los datos iniciales del diálogo.
     */
    private void loadInitialData() {
        // Aquí se cargarían los datos reales desde el controlador
        // Por ahora usamos datos de ejemplo
        updateAnalytics();
    }
    
    /**
     * Actualiza los analytics con los filtros seleccionados.
     */
    private void updateAnalytics() {
        String curso = cursoFilter.getValue();
        String bloque = bloqueFilter.getValue();
        String periodo = periodoFilter.getValue();
        
        // Aquí se llamaría al controlador para obtener datos reales
        // Por ahora solo mostramos un mensaje
        System.out.println("Actualizando analytics con filtros: " + curso + ", " + bloque + ", " + periodo);
        
        // TODO: Implementar llamada real al controlador
        // controller.updateAnalytics(curso, bloque, periodo);
    }
    
    /**
     * Clase de datos para la tabla de estrategias.
     */
    public static class EstrategiaData {
        private final String nombre;
        private final String exito;
        private final String velocidad;
        private final String sesiones;
        private final String recomendacion;
        
        public EstrategiaData(String nombre, String exito, String velocidad, String sesiones, String recomendacion) {
            this.nombre = nombre;
            this.exito = exito;
            this.velocidad = velocidad;
            this.sesiones = sesiones;
            this.recomendacion = recomendacion;
        }
        
        public String getNombre() { return nombre; }
        public String getExito() { return exito; }
        public String getVelocidad() { return velocidad; }
        public String getSesiones() { return sesiones; }
        public String getRecomendacion() { return recomendacion; }
    }
} 