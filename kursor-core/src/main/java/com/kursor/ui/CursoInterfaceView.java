package com.kursor.ui;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.EstadoPregunta;
import com.kursor.yaml.dto.CursoDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vista principal para la ejecución de cursos con estrategia de aprendizaje.
 * 
 * <p>Esta vista implementa la nueva arquitectura modular donde:</p>
 * <ul>
 *   <li><strong>Cabecera:</strong> Información del curso, estrategia y progreso</li>
 *   <li><strong>Contenido:</strong> Área dinámica controlada por los módulos de preguntas</li>
 *   <li><strong>Pie:</strong> Botones configurados por los módulos + botón "Terminar"</li>
 * </ul>
 * 
 * <p>Los módulos de preguntas tienen control total sobre la configuración de la UI
 * y manejan su propia validación y presentación de resultados.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see CursoDTO
 * @see CursoInterfaceController
 */
public class CursoInterfaceView extends Stage implements PreguntaEventListener {
    
    /** Logger para registrar eventos de la vista */
    private static final Logger logger = LoggerFactory.getLogger(CursoInterfaceView.class);
    
    /** Estrategia de aprendizaje seleccionada */
    private final EstrategiaAprendizaje estrategia;
    
    /** Curso que se está ejecutando */
    private final CursoDTO curso;
    
    /** Contenedores principales */
    private VBox headerContainer;
    private VBox contentContainer;
    private VBox footerContainer;
    
    /** Controles de la cabecera */
    private Label lblTituloCurso;
    private Label lblEstrategia;
    private ProgressBar progressBar;
    private Label lblProgreso;
    
    /** Botón de terminar (siempre presente) */
    private Button btnTerminar;
    
    /** Estado actual */
    private int preguntasCompletadas = 0;
    private int totalPreguntas = 0;
    private EstadoPregunta estadoPreguntaActual = EstadoPregunta.SIN_CONTESTAR;
    
    /** Gestor de módulos */
    private final com.kursor.util.ModuleManager moduleManager;
    
    /** Pregunta actual */
    private com.kursor.domain.Pregunta preguntaActual;
    
    /** Módulo actual */
    private com.kursor.modules.PreguntaModule moduloActual;
    
    /**
     * Constructor para crear la vista de ejecución de curso.
     * 
     * @param owner Ventana padre de la vista
     * @param curso Curso a ejecutar
     * @param estrategia Estrategia de aprendizaje seleccionada
     */
    public CursoInterfaceView(Window owner, CursoDTO curso, EstrategiaAprendizaje estrategia) {
        super();
        
        this.curso = curso;
        this.estrategia = estrategia;
        
        // Inicializar gestor de módulos
        this.moduleManager = com.kursor.util.ModuleManager.getInstance();
        
        logger.info("Creando vista de curso para: " + curso.getTitulo() + " con estrategia: " + estrategia.getNombre());
        
        // Configurar la vista
        configurarVista(owner);
        
        // Crear la escena
        Scene scene = crearEscena();
        setScene(scene);
        
        // Configurar eventos
        configurarEventos();
        
        // Inicializar estado
        inicializarEstado();
        
        // Cargar primera pregunta
        cargarPrimeraPregunta();
        
        logger.info("Vista de curso creada correctamente");
    }
    
    /**
     * Configura las propiedades de la vista.
     * 
     * @param owner Ventana padre de la vista
     */
    private void configurarVista(Window owner) {
        // Configuración vista híbrida
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        
        // Propiedades de la ventana
        setResizable(true);
        setMinWidth(700);
        setMinHeight(500);
        setWidth(900);
        setHeight(700);
        setTitle("📚 " + curso.getTitulo() + " - " + estrategia.getNombre());
        
        // Centrar en la pantalla
        centerOnScreen();
    }
    
    /**
     * Crea la escena de la vista con la estructura de tres secciones.
     * 
     * @return Escena configurada
     */
    private Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");
        
        // Crear las tres secciones
        headerContainer = crearCabecera();
        contentContainer = crearContenido();
        footerContainer = crearPie();
        
        // Asignar secciones al BorderPane
        root.setTop(headerContainer);
        root.setCenter(contentContainer);
        root.setBottom(footerContainer);
        
        return new Scene(root);
    }
    
    /**
     * Crea la sección de cabecera con información del curso y progreso.
     * 
     * @return Contenedor de la cabecera
     */
    private VBox crearCabecera() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        
        // Título del curso
        lblTituloCurso = new Label(curso.getTitulo());
        lblTituloCurso.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Estrategia seleccionada
        lblEstrategia = new Label("Estrategia: " + estrategia.getNombre());
        lblEstrategia.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Barra de progreso
        HBox progressContainer = new HBox(10);
        progressContainer.setAlignment(Pos.CENTER_LEFT);
        
        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #28a745;");
        
        lblProgreso = new Label("0 / 0");
        lblProgreso.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        progressContainer.getChildren().addAll(progressBar, lblProgreso);
        
        header.getChildren().addAll(lblTituloCurso, lblEstrategia, progressContainer);
        
        return header;
    }
    
    /**
     * Crea la sección de contenido principal.
     * 
     * @return Contenedor del contenido
     */
    private VBox crearContenido() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: white;");
        
        // Mensaje inicial
        Label lblInicial = new Label("Cargando pregunta...");
        lblInicial.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");
        content.getChildren().add(lblInicial);
        
        return content;
    }
    
    /**
     * Crea la sección del pie con botones.
     * 
     * @return Contenedor del pie
     */
    private VBox crearPie() {
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(20));
        footer.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
        
        // Contenedor para botones del módulo
        HBox moduleButtonsContainer = new HBox(10);
        moduleButtonsContainer.setAlignment(Pos.CENTER);
        
        // Botón de terminar (siempre presente)
        btnTerminar = new Button("Terminar Curso");
        btnTerminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
        btnTerminar.setOnAction(e -> finalizarCurso());
        
        HBox terminarContainer = new HBox();
        terminarContainer.setAlignment(Pos.CENTER_RIGHT);
        terminarContainer.getChildren().add(btnTerminar);
        
        footer.getChildren().addAll(moduleButtonsContainer, terminarContainer);
        
        return footer;
    }
    
    /**
     * Configura los eventos de la vista.
     */
    private void configurarEventos() {
        // Eventos de ventana
        setOnCloseRequest(e -> {
            e.consume();
            finalizarCurso();
        });
    }
    
    /**
     * Inicializa el estado de la vista.
     */
    private void inicializarEstado() {
        // Calcular total de preguntas
        totalPreguntas = calcularTotalPreguntas();
        preguntasCompletadas = 0;
        
        // Actualizar progreso inicial
        actualizarProgreso();
        
        logger.info("Estado inicializado - Total preguntas: " + totalPreguntas);
    }
    
    /**
     * Calcula el total de preguntas en el curso.
     * 
     * @return Número total de preguntas
     */
    private int calcularTotalPreguntas() {
        int total = 0;
        for (var bloque : curso.getBloques()) {
            total += bloque.getPreguntas().size();
        }
        return total;
    }
    
    /**
     * Actualiza la información de progreso en la cabecera.
     */
    private void actualizarProgreso() {
        double progreso = totalPreguntas > 0 ? (double) preguntasCompletadas / totalPreguntas : 0.0;
        progressBar.setProgress(progreso);
        lblProgreso.setText(preguntasCompletadas + " / " + totalPreguntas);
        
        logger.debug("Progreso actualizado: " + preguntasCompletadas + "/" + totalPreguntas + " (" + String.format("%.1f", progreso * 100) + "%)");
    }
    
    /**
     * Carga la primera pregunta del curso.
     */
    private void cargarPrimeraPregunta() {
        logger.debug("Cargando primera pregunta");
        
        preguntaActual = estrategia.primeraPregunta();
        if (preguntaActual == null) {
            mostrarMensajeFinal("No hay preguntas disponibles en este curso.");
            return;
        }
        
        cargarModuloParaPregunta(preguntaActual);
        mostrarPregunta(preguntaActual);
    }
    
    /**
     * Carga el módulo correspondiente para una pregunta.
     * 
     * @param pregunta Pregunta para la cual cargar el módulo
     */
    private void cargarModuloParaPregunta(com.kursor.domain.Pregunta pregunta) {
        logger.debug("Cargando módulo para pregunta: " + pregunta.getId() + " (tipo: " + pregunta.getTipo() + ")");
        
        moduloActual = moduleManager.findModuleByQuestionType(pregunta.getTipo());
        if (moduloActual == null) {
            logger.error("No se encontró módulo para el tipo de pregunta: " + pregunta.getTipo());
            mostrarError("Tipo de pregunta no soportado: " + pregunta.getTipo());
            return;
        }
        
        logger.info("Módulo cargado: " + moduloActual.getModuleName());
    }
    
    /**
     * Muestra una pregunta usando el módulo correspondiente.
     * 
     * @param pregunta Pregunta a mostrar
     */
    private void mostrarPregunta(com.kursor.domain.Pregunta pregunta) {
        logger.debug("Mostrando pregunta: " + pregunta.getId());
        
        if (moduloActual == null) {
            logger.error("No hay módulo cargado para mostrar la pregunta");
            return;
        }
        
        try {
            // Limpiar contenedores
            headerContainer.getChildren().clear();
            contentContainer.getChildren().clear();
            footerContainer.getChildren().clear();
            
            // Recrear cabecera básica
            recrearCabeceraBasica();
            
            // Recrear pie básico
            recrearPieBasico();
            
            // Configurar UI completa con el módulo
            moduloActual.configureCompleteUI(pregunta, headerContainer, contentContainer, footerContainer, this);
            
            // Actualizar estado
            estadoPreguntaActual = EstadoPregunta.SIN_CONTESTAR;
            
            logger.info("Pregunta mostrada correctamente: " + pregunta.getId());
            
        } catch (Exception e) {
            logger.error("Error al mostrar pregunta: " + pregunta.getId(), e);
            mostrarError("Error al mostrar la pregunta: " + e.getMessage());
        }
    }
    
    /**
     * Recrea la cabecera básica después de limpiarla.
     */
    private void recrearCabeceraBasica() {
        // Título del curso
        lblTituloCurso = new Label(curso.getTitulo());
        lblTituloCurso.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Estrategia seleccionada
        lblEstrategia = new Label("Estrategia: " + estrategia.getNombre());
        lblEstrategia.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Barra de progreso
        HBox progressContainer = new HBox(10);
        progressContainer.setAlignment(Pos.CENTER_LEFT);
        
        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #28a745;");
        
        lblProgreso = new Label("0 / 0");
        lblProgreso.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        progressContainer.getChildren().addAll(progressBar, lblProgreso);
        
        headerContainer.getChildren().addAll(lblTituloCurso, lblEstrategia, progressContainer);
        
        // Actualizar progreso
        actualizarProgreso();
    }
    
    /**
     * Recrea el pie básico después de limpiarlo.
     */
    private void recrearPieBasico() {
        // Botón de terminar (siempre presente)
        btnTerminar = new Button("Terminar Curso");
        btnTerminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
        btnTerminar.setOnAction(e -> finalizarCurso());
        
        HBox terminarContainer = new HBox();
        terminarContainer.setAlignment(Pos.CENTER_RIGHT);
        terminarContainer.getChildren().add(btnTerminar);
        
        footerContainer.getChildren().add(terminarContainer);
    }
    
    // Implementación de PreguntaEventListener
    
    @Override
    public void onRespuestaValidada(boolean esCorrecta) {
        logger.info("Respuesta validada: " + (esCorrecta ? "CORRECTA" : "INCORRECTA"));
        
        // Actualizar estado
        estadoPreguntaActual = esCorrecta ? EstadoPregunta.CORRECTA : EstadoPregunta.INCORRECTA;
        
        // Incrementar contador si es la primera vez que se responde
        if (estadoPreguntaActual != EstadoPregunta.SIN_CONTESTAR) {
            preguntasCompletadas++;
            actualizarProgreso();
        }
        
        // TODO: Guardar progreso automáticamente
        // sessionManager.guardarProgreso(preguntaActual, esCorrecta);
        
        // Preparar transición a siguiente pregunta
        prepararSiguientePregunta();
    }
    
    @Override
    public void onProgresoActualizado(int preguntasCompletadas, int totalPreguntas) {
        logger.debug("Progreso actualizado por módulo: " + preguntasCompletadas + "/" + totalPreguntas);
        
        this.preguntasCompletadas = preguntasCompletadas;
        this.totalPreguntas = totalPreguntas;
        actualizarProgreso();
    }
    
    @Override
    public void onSolicitarSiguientePregunta() {
        logger.debug("Solicitud de siguiente pregunta desde módulo");
        cargarSiguientePregunta();
    }
    
    @Override
    public void onSolicitarTerminarCurso() {
        logger.debug("Solicitud de terminar curso desde módulo");
        finalizarCurso();
    }
    
    /**
     * Prepara la transición a la siguiente pregunta.
     */
    private void prepararSiguientePregunta() {
        // Verificar si hay más preguntas
        if (!estrategia.hayMasPreguntas()) {
            mostrarMensajeFinal("¡Felicitaciones! Has completado el curso.");
            return;
        }
        
        // Esperar un momento antes de cargar la siguiente pregunta
        // TODO: Implementar delay opcional para mostrar resultado
        cargarSiguientePregunta();
    }
    
    /**
     * Carga la siguiente pregunta del curso.
     */
    private void cargarSiguientePregunta() {
        logger.debug("Cargando siguiente pregunta");
        
        preguntaActual = estrategia.siguientePregunta();
        if (preguntaActual == null) {
            mostrarMensajeFinal("¡Felicitaciones! Has completado el curso.");
            return;
        }
        
        cargarModuloParaPregunta(preguntaActual);
        mostrarPregunta(preguntaActual);
    }
    
    /**
     * Finaliza la ejecución del curso.
     */
    private void finalizarCurso() {
        logger.info("Finalizando curso");
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Finalizar Curso");
        alert.setHeaderText("¿Estás seguro de que quieres terminar el curso?");
        alert.setContentText("Tu progreso será guardado automáticamente.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Guardar progreso final
                // sessionManager.finalizarSesion();
                
                close();
            }
        });
    }
    
    /**
     * Muestra un mensaje de finalización del curso.
     * 
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarMensajeFinal(String mensaje) {
        logger.info("Mostrando mensaje final: " + mensaje);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Curso Completado");
        alert.setHeaderText("¡Curso Finalizado!");
        alert.setContentText(mensaje);
        
        alert.showAndWait().ifPresent(response -> {
            close();
        });
    }
    
    /**
     * Muestra un mensaje de error.
     * 
     * @param mensaje Mensaje de error
     */
    private void mostrarError(String mensaje) {
        logger.error("Error en la vista: " + mensaje);
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error en el curso");
        alert.setContentText(mensaje);
        
        alert.showAndWait();
    }
    
    // Getters para acceso externo
    
    public EstrategiaAprendizaje getEstrategia() {
        return estrategia;
    }
    
    public CursoDTO getCurso() {
        return curso;
    }
    
    public int getPreguntasCompletadas() {
        return preguntasCompletadas;
    }
    
    public int getTotalPreguntas() {
        return totalPreguntas;
    }
    
    public EstadoPregunta getEstadoPreguntaActual() {
        return estadoPreguntaActual;
    }
} 