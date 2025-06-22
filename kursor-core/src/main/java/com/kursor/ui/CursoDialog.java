package com.kursor.ui;

import com.kursor.domain.Curso;
import com.kursor.domain.Bloque;
import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kursor.util.CursoManager;
import com.kursor.util.ModuleManager;
import com.kursor.modules.PreguntaModule;
import com.kursor.yaml.dto.CursoPreviewDTO;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Alert.AlertType;
import java.util.Map;

/**
 * Diálogo modal para la presentación y navegación de cursos.
 * 
 * <p>Esta clase implementa la interfaz gráfica principal para los cursos,
 * proporcionando una experiencia de usuario completa para la navegación
 * y validación de preguntas. El diálogo está estructurado en tres secciones:</p>
 * 
 * <ul>
 *   <li><strong>Cabecera:</strong> Información del curso, bloque actual y progreso</li>
 *   <li><strong>Contenido:</strong> Área flexible para mostrar las preguntas</li>
 *   <li><strong>Pie:</strong> Botones de navegación y control</li>
 * </ul>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Navegación secuencial entre preguntas y bloques</li>
 *   <li>Integración con módulos de preguntas existentes</li>
 *   <li>Validación automática de respuestas</li>
 *   <li>Persistencia del progreso del usuario</li>
 *   <li>Manejo especial para flashcards (sin validación)</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Curso
 * @see PreguntaModule
 * @see CursoSessionManager
 */
public class CursoDialog extends Dialog<Void> {
    
    /** Logger para registrar eventos del diálogo */
    private static final Logger logger = LoggerFactory.getLogger(CursoDialog.class);
    
    /** Curso que se está presentando */
    private final Curso curso;
    
    /** Gestor de sesión del curso */
    private final CursoSessionManager sessionManager;
    
    /** Controles de la interfaz */
    private Label tituloLabel;
    private Label bloqueLabel;
    private ProgressBar progresoBar;
    private Label progresoLabel;
    private VBox contenidoArea;
    private Button verificarBtn;
    private Button siguienteBtn;
    private Button terminarBtn;
    
    /** Estado actual */
    private int bloqueActual = 0;
    private int preguntaActual = 0;
    private Node vistaPreguntaActual;
    private Pregunta preguntaActualObj;
    
    /**
     * Constructor para crear el diálogo modal del curso.
     * 
     * @param cursoPreview Información básica del curso a cargar
     * @param ownerStage Ventana padre del diálogo
     */
    public CursoDialog(CursoPreviewDTO cursoPreview, Stage ownerStage) {
        super();
        logger.info("[CursoDialog] Creando diálogo para cursoPreview: {}", cursoPreview);
        this.curso = cargarCursoCompleto(cursoPreview.getId());
        logger.info("[CursoDialog] Curso cargado: {}", curso);
        this.sessionManager = new CursoSessionManager(cursoPreview.getId());
        logger.info("[CursoDialog] SessionManager creado para curso: {}", cursoPreview.getId());
        setTitle("Estudiar curso: " + (curso != null ? curso.getTitulo() : "(no disponible)"));
        initOwner(ownerStage);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        logger.debug("[CursoDialog] Inicializando interfaz gráfica...");
        inicializarUI();
    }
    
    /**
     * Carga el curso completo desde el gestor de cursos.
     * 
     * @param cursoId Identificador del curso
     * @return Curso completo cargado o null si hay error
     */
    private Curso cargarCursoCompleto(String cursoId) {
        logger.info("[CursoDialog] Intentando cargar curso completo con id: {}", cursoId);
        Curso curso = CursoManager.getInstance().obtenerCursoCompleto(cursoId);
        if (curso == null) {
            logger.error("[CursoDialog] No se pudo cargar el curso con id: {}", cursoId);
        } else {
            logger.info("[CursoDialog] Curso cargado correctamente: {}", curso.getTitulo());
        }
        return curso;
    }
    
    /**
     * Inicializa las propiedades básicas del diálogo.
     */
    private void inicializarUI() {
        if (curso == null) {
            logger.error("[CursoDialog] No se puede inicializar la UI porque el curso es null");
            setContentText("No se pudo cargar el curso. Por favor, revisa los datos del curso.");
            return;
        }
        logger.debug("[CursoDialog] Inicializando UI para curso: {}", curso.getTitulo());
        // Configurar propiedades del diálogo
        setHeaderText(null);
        
        // Configurar tamaño
        setWidth(800);
        setHeight(600);
        
        // Configurar botones de cierre
        ButtonType cerrarType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(cerrarType);
        
        // Configurar estilo
        getDialogPane().setStyle("-fx-background-color: #f8f9fa;");
        
        logger.info("Diálogo inicializado para curso: " + curso.getTitulo());
        
        configurarContenido();
        cargarPrimeraPregunta();
    }
    
    /**
     * Configura el contenido principal del diálogo con las tres secciones.
     */
    private void configurarContenido() {
        VBox contenidoPrincipal = new VBox(0);
        contenidoPrincipal.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");
        
        // Sección 1: Cabecera
        Node cabecera = crearCabecera();
        
        // Sección 2: Contenido
        contenidoArea = new VBox(10);
        contenidoArea.setPadding(new Insets(20));
        contenidoArea.setAlignment(Pos.TOP_CENTER);
        contenidoArea.setStyle("-fx-background-color: white;");
        contenidoArea.setPrefHeight(400);
        
        // Sección 3: Pie
        Node pie = crearPie();
        
        // Agregar secciones al contenido principal
        contenidoPrincipal.getChildren().addAll(cabecera, contenidoArea, pie);
        
        // Configurar el contenido del diálogo
        getDialogPane().setContent(contenidoPrincipal);
        
        logger.info("Contenido del diálogo configurado");
    }
    
    /**
     * Crea la sección de cabecera con información del curso y progreso.
     * 
     * @return Nodo JavaFX de la cabecera
     */
    private Node crearCabecera() {
        VBox cabecera = new VBox(10);
        cabecera.setPadding(new Insets(15, 20, 15, 20));
        cabecera.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 5 5 0 0;");
        
        // Título del curso
        tituloLabel = new Label(curso.getTitulo());
        tituloLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        tituloLabel.setTextFill(Color.WHITE);
        
        // Información del bloque actual
        bloqueLabel = new Label();
        bloqueLabel.setFont(Font.font("Segoe UI", 14));
        bloqueLabel.setTextFill(Color.WHITE);
        
        // Barra de progreso
        progresoBar = new ProgressBar(0.0);
        progresoBar.setPrefWidth(400);
        progresoBar.setStyle("-fx-accent: #3498db;");
        
        // Etiqueta de progreso
        progresoLabel = new Label();
        progresoLabel.setFont(Font.font("Segoe UI", 12));
        progresoLabel.setTextFill(Color.WHITE);
        progresoLabel.setAlignment(Pos.CENTER);
        
        cabecera.getChildren().addAll(tituloLabel, bloqueLabel, progresoBar, progresoLabel);
        
        return cabecera;
    }
    
    /**
     * Crea la sección de pie con botones de navegación.
     * 
     * @return Nodo JavaFX del pie
     */
    private Node crearPie() {
        HBox pie = new HBox(10);
        pie.setPadding(new Insets(15, 20, 15, 20));
        pie.setAlignment(Pos.CENTER_RIGHT);
        pie.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
        
        // Botón Verificar
        verificarBtn = new Button("✅ Verificar");
        verificarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        verificarBtn.setOnAction(e -> verificarRespuesta());
        
        // Botón Siguiente
        siguienteBtn = new Button("⏭️ Siguiente");
        siguienteBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
        siguienteBtn.setOnAction(e -> siguientePregunta());
        siguienteBtn.setDisable(true); // Inicialmente deshabilitado
        
        // Botón Terminar
        terminarBtn = new Button("🏁 Terminar");
        terminarBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold;");
        terminarBtn.setOnAction(e -> terminarCurso());
        terminarBtn.setVisible(false); // Solo visible en la última pregunta
        
        pie.getChildren().addAll(verificarBtn, siguienteBtn, terminarBtn);
        
        return pie;
    }
    
    /**
     * Carga la primera pregunta del curso.
     */
    private void cargarPrimeraPregunta() {
        logger.info("Cargando primera pregunta del curso");
        
        if (curso.getBloques().isEmpty() || curso.getBloques().get(0).getPreguntas().isEmpty()) {
            logger.warn("El curso no tiene preguntas.");
            mostrarError("Curso sin contenido", "Este curso no contiene preguntas para mostrar.");
            return;
        }
        
        cargarPreguntaActual();
    }
    
    /**
     * Carga y muestra la pregunta actual en la interfaz.
     */
    private void cargarPreguntaActual() {
        logger.info("Cargando pregunta actual: Bloque " + (bloqueActual + 1) + ", Pregunta " + (preguntaActual + 1));
        
        preguntaActualObj = curso.getBloques().get(bloqueActual).getPreguntas().get(preguntaActual);
        
        crearVistaPregunta();
        actualizarCabecera();
        configurarBotonesSegunTipo();
        
        siguienteBtn.setDisable(true);
        verificarBtn.setDisable(false);
    }
    
    /**
     * Crea la vista de la pregunta actual utilizando el módulo correspondiente.
     */
    private void crearVistaPregunta() {
        String tipoPregunta = preguntaActualObj.getTipo();
        
        PreguntaModule module = ModuleManager.getInstance()
                .findModuleByQuestionType(tipoPregunta);
        
        if (module == null) {
            logger.error("No se encontró módulo para el tipo de pregunta: " + tipoPregunta);
            mostrarError("Módulo no encontrado", "No se encontró un módulo para el tipo de pregunta: " + tipoPregunta);
            return;
        }
        
        vistaPreguntaActual = module.createQuestionView(preguntaActualObj);
        contenidoArea.getChildren().clear();
        contenidoArea.getChildren().add(vistaPreguntaActual);
    }
    
    /**
     * Configura la visibilidad de los botones según el tipo de pregunta.
     */
    private void configurarBotonesSegunTipo() {
        if ("flashcard".equalsIgnoreCase(preguntaActualObj.getTipo())) {
            verificarBtn.setVisible(false);
            siguienteBtn.setDisable(false); // Siguiente siempre habilitado para flashcards
        } else {
            verificarBtn.setVisible(true);
        }
        
        terminarBtn.setVisible(esUltimaPregunta());
        siguienteBtn.setVisible(!esUltimaPregunta());
    }
    
    /**
     * Verifica si la pregunta actual es la última del curso.
     * 
     * @return true si es la última pregunta, false en caso contrario
     */
    private boolean esUltimaPregunta() {
        return bloqueActual == curso.getBloques().size() - 1 &&
               preguntaActual == curso.getBloques().get(bloqueActual).getPreguntas().size() - 1;
    }
    
    /**
     * Actualiza la cabecera con la información del bloque y el progreso.
     */
    private void actualizarCabecera() {
        Bloque bloque = curso.getBloques().get(bloqueActual);
        bloqueLabel.setText("Bloque " + (bloqueActual + 1) + ": " + bloque.getTitulo());
        
        int totalPreguntas = curso.getNumeroPreguntas();
        int preguntasCompletadas = sessionManager.getNumeroPreguntasRespondidas();
        
        double progreso = (double) preguntasCompletadas / totalPreguntas;
        progresoBar.setProgress(progreso);
        progresoLabel.setText(String.format("%d / %d preguntas", preguntasCompletadas, totalPreguntas));
    }
    
    /**
     * Verifica la respuesta del usuario para la pregunta actual.
     */
    private void verificarRespuesta() {
        PreguntaModule module = ModuleManager.getInstance()
            .findModuleByQuestionType(preguntaActualObj.getTipo());
        
        if (module == null) {
            logger.error("Módulo no encontrado para el tipo de pregunta: " + preguntaActualObj.getTipo());
            return;
        }
        
        // 1. Obtener respuesta del usuario desde la UI
        Object respuestaUsuario = obtenerRespuestaUsuario();
        
        // 2. Usar el módulo para verificar la respuesta
        boolean esCorrecta = module.validateAnswer(preguntaActualObj, respuestaUsuario);
        
        // 3. Registrar el resultado en la sesión
        sessionManager.guardarRespuesta(preguntaActualObj.getId(), esCorrecta);
        
        // 4. Actualizar la interfaz
        verificarBtn.setDisable(true);
        siguienteBtn.setDisable(esUltimaPregunta());
        
        mostrarResultadoVerificacion(esCorrecta);
        
        logger.info("Respuesta verificada - Pregunta: " + preguntaActualObj.getId() + ", Correcta: " + esCorrecta);
    }
    
    /**
     * Obtiene la respuesta del usuario desde la interfaz de la pregunta.
     * 
     * @return La respuesta del usuario
     */
    private Object obtenerRespuestaUsuario() {
        // TODO: Implementar la obtención de respuestas específicas para cada tipo de pregunta
        // Por ahora, retornamos null y cada módulo debe implementar su propia lógica
        logger.warn("Método obtenerRespuestaUsuario no implementado completamente para tipo: " + preguntaActualObj.getTipo());
        return null;
    }
    
    /**
     * Muestra un indicador visual del resultado de la verificación.
     * 
     * @param esCorrecta true si la respuesta fue correcta
     */
    private void mostrarResultadoVerificacion(boolean esCorrecta) {
        String estiloBorde = esCorrecta ? "-fx-border-color: #28a745; -fx-border-width: 2px;"
                                       : "-fx-border-color: #dc3545; -fx-border-width: 2px;";
        contenidoArea.setStyle(estiloBorde);
    }
    
    /**
     * Navega a la siguiente pregunta o al siguiente bloque.
     */
    private void siguientePregunta() {
        contenidoArea.setStyle("-fx-border-color: transparent;"); // Resetear borde
        
        Bloque bloque = curso.getBloques().get(bloqueActual);
        if (preguntaActual < bloque.getPreguntas().size() - 1) {
            preguntaActual++;
        } else {
            if (bloqueActual < curso.getBloques().size() - 1) {
                bloqueActual++;
                preguntaActual = 0;
            } else {
                // Fin del curso
                terminarCurso();
                return;
            }
        }
        
        cargarPreguntaActual();
    }
    
    /**
     * Finaliza el curso y muestra el resumen.
     */
    private void terminarCurso() {
        mostrarResumenFinal();
        close();
    }
    
    /**
     * Muestra una ventana emergente con el resumen del curso.
     */
    private void mostrarResumenFinal() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("¡Curso Finalizado!");
        alert.setHeaderText("Has completado el curso: " + curso.getTitulo());
        
        int totalPreguntas = curso.getNumeroPreguntas();
        Map<String, Object> estadisticas = sessionManager.obtenerEstadisticas();
        int correctas = (Integer) estadisticas.get("correctas");
        
        double porcentaje = (double) correctas / totalPreguntas * 100;
        
        VBox contenidoResumen = new VBox(10);
        contenidoResumen.setPadding(new Insets(20));
        
        Label resumenLabel = new Label("Resumen de tu rendimiento:");
        resumenLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        Label correctasLabel = new Label("Respuestas Correctas: " + correctas + " / " + totalPreguntas);
        Label incorrectasLabel = new Label("Respuestas Incorrectas: " + (totalPreguntas - correctas));
        Label porcentajeLabel = new Label(String.format("Porcentaje de Aciertos: %.2f%%", porcentaje));
        
        // Barra de progreso final
        ProgressBar finalProgressBar = new ProgressBar(porcentaje / 100);
        finalProgressBar.setPrefWidth(300);
        finalProgressBar.setStyle("-fx-accent: " + (porcentaje >= 50 ? "#28a745" : "#dc3545") + ";");
        
        contenidoResumen.getChildren().addAll(
            resumenLabel,
            correctasLabel,
            incorrectasLabel,
            porcentajeLabel,
            finalProgressBar
        );
        
        alert.getDialogPane().setContent(contenidoResumen);
        alert.showAndWait();
        
        logger.info("Curso finalizado - Rendimiento: " + correctas + "/" + totalPreguntas);
    }
    
    /**
     * Muestra un diálogo de error genérico.
     * 
     * @param titulo Título del error
     * @param mensaje Mensaje del error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} 