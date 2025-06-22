package com.kursor.ui;

import com.kursor.domain.EstrategiaAprendizaje;
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
 * Modal principal para la ejecución de cursos con estrategia de aprendizaje.
 * 
 * <p>Este modal proporciona la interfaz principal para que el usuario complete
 * un curso seleccionado. El modal tiene una estructura de tres secciones:</p>
 * <ul>
 *   <li><strong>Cabecera:</strong> Información del curso, estrategia seleccionada y progreso</li>
 *   <li><strong>Contenido:</strong> Área dinámica que muestra la pregunta actual</li>
 *   <li><strong>Pie:</strong> Botones de navegación (Verificar, Siguiente, Terminar)</li>
 * </ul>
 * 
 * <p>El modal es híbrido, comportándose como modal para la ventana padre pero
 * permitiendo redimensionamiento y manteniendo el contexto visual de la aplicación principal.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see CursoDTO
 */
public class CursoInterfaceModal extends Stage {
    
    /** Logger para registrar eventos del modal */
    private static final Logger logger = LoggerFactory.getLogger(CursoInterfaceModal.class);
    
    /** Estrategia de aprendizaje seleccionada */
    private final EstrategiaAprendizaje estrategia;
    
    /** Curso que se está ejecutando */
    private final CursoDTO curso;
    
    /** Contenedores principales */
    private VBox headerSection;
    private VBox contentSection;
    private HBox footerSection;
    
    /** Controles de la cabecera */
    private Label lblTituloCurso;
    private Label lblEstrategia;
    private Label lblBloqueActual;
    private ProgressBar progressBar;
    private Label lblContador;
    
    /** Controles del pie */
    private Button btnVerificar;
    private Button btnSiguiente;
    private Button btnTerminar;
    
    /** Estado actual */
    private int bloqueActual = 0;
    private int preguntaActual = 0;
    private int totalPreguntas = 0;
    
    /** Gestor de módulos */
    private final com.kursor.util.ModuleManager moduleManager;
    
    /** Pregunta actual */
    private com.kursor.domain.Pregunta preguntaActualObj;
    
    /** Módulo actual */
    private com.kursor.modules.PreguntaModule moduloActual;
    
    /**
     * Constructor para crear el modal de ejecución de curso.
     * 
     * @param owner Ventana padre del modal
     * @param curso Curso a ejecutar
     * @param estrategia Estrategia de aprendizaje seleccionada
     */
    public CursoInterfaceModal(Window owner, CursoDTO curso, EstrategiaAprendizaje estrategia) {
        super();
        
        this.curso = curso;
        this.estrategia = estrategia;
        
        // Inicializar gestor de módulos
        this.moduleManager = com.kursor.util.ModuleManager.getInstance();
        
        logger.info("Creando modal de curso para: " + curso.getTitulo() + " con estrategia: " + estrategia);
        
        // Configurar el modal
        configurarModal(owner);
        
        // Crear la escena
        Scene scene = crearEscena();
        setScene(scene);
        
        // Configurar eventos
        configurarEventos();
        
        // Inicializar estado
        inicializarEstado();
        
        // Cargar primera pregunta
        cargarPrimeraPregunta();
        
        logger.info("Modal de curso creado correctamente");
    }
    
    /**
     * Configura las propiedades del modal.
     * 
     * @param owner Ventana padre del modal
     */
    private void configurarModal(Window owner) {
        // Configuración modal híbrida
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL); // Modal solo para la ventana padre
        
        // Propiedades de la ventana
        setResizable(true);
        setMinWidth(600);
        setMinHeight(400);
        setWidth(800);
        setHeight(600);
        setTitle("📚 " + curso.getTitulo() + " - " + estrategia.toString());
        
        // Centrar en la pantalla
        centerOnScreen();
        
        // Mantener siempre visible
        setAlwaysOnTop(false);
    }
    
    /**
     * Crea la escena del modal con la estructura de tres secciones.
     * 
     * @return Escena configurada
     */
    private Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");
        
        // Crear las tres secciones
        headerSection = crearCabecera();
        contentSection = crearContenido();
        footerSection = crearPie();
        
        // Asignar secciones al BorderPane
        root.setTop(headerSection);
        root.setCenter(contentSection);
        root.setBottom(footerSection);
        
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
        lblEstrategia = new Label("Estrategia: " + obtenerNombreEstrategia(estrategia));
        lblEstrategia.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Información del bloque actual
        lblBloqueActual = new Label("Bloque 1 de " + curso.getBloques().size());
        lblBloqueActual.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        
        // Barra de progreso
        HBox progressContainer = new HBox(10);
        progressContainer.setAlignment(Pos.CENTER_LEFT);
        
        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #28a745;");
        
        lblContador = new Label("0 / 0");
        lblContador.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        progressContainer.getChildren().addAll(progressBar, lblContador);
        
        header.getChildren().addAll(lblTituloCurso, lblEstrategia, lblBloqueActual, progressContainer);
        
        return header;
    }
    
    /**
     * Crea la sección de contenido donde se mostrarán las preguntas.
     * 
     * @return Contenedor del contenido
     */
    private VBox crearContenido() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: white;");
        
        // Mensaje inicial
        Label lblMensajeInicial = new Label("🎯 Preparando curso...");
        lblMensajeInicial.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");
        
        content.getChildren().add(lblMensajeInicial);
        
        return content;
    }
    
    /**
     * Crea la sección del pie con botones de navegación.
     * 
     * @return Contenedor del pie
     */
    private HBox crearPie() {
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(20));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
        
        // Botón Verificar
        btnVerificar = new Button("✅ Verificar");
        btnVerificar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnVerificar.setPrefWidth(120);
        btnVerificar.setDisable(true);
        
        // Botón Siguiente
        btnSiguiente = new Button("⏭️ Siguiente");
        btnSiguiente.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnSiguiente.setPrefWidth(120);
        btnSiguiente.setDisable(true);
        
        // Botón Terminar
        btnTerminar = new Button("🏁 Terminar");
        btnTerminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnTerminar.setPrefWidth(120);
        
        footer.getChildren().addAll(btnVerificar, btnSiguiente, btnTerminar);
        
        return footer;
    }
    
    /**
     * Configura los eventos de los botones y del modal.
     */
    private void configurarEventos() {
        // Evento del botón Verificar
        btnVerificar.setOnAction(e -> {
            logger.info("Usuario presionó Verificar");
            verificarRespuesta();
        });
        
        // Evento del botón Siguiente
        btnSiguiente.setOnAction(e -> {
            logger.info("Usuario presionó Siguiente");
            siguientePregunta();
        });
        
        // Evento del botón Terminar
        btnTerminar.setOnAction(e -> {
            logger.info("Usuario presionó Terminar");
            finalizarCurso();
        });
        
        // Evento de cierre de ventana
        setOnCloseRequest(e -> {
            logger.info("Modal de curso cerrado por el usuario");
            // TODO: Implementar lógica de guardado de progreso
        });
    }
    
    /**
     * Inicializa el estado del modal.
     */
    private void inicializarEstado() {
        // Calcular total de preguntas
        totalPreguntas = curso.getBloques().stream()
            .mapToInt(bloque -> bloque.getPreguntas().size())
            .sum();
        
        // Actualizar contador
        actualizarContador();
        
        logger.info("Estado inicializado - Total preguntas: " + totalPreguntas);
    }
    
    /**
     * Actualiza el contador de progreso.
     */
    private void actualizarContador() {
        int preguntasCompletadas = 0; // TODO: Calcular desde el estado real
        lblContador.setText(preguntasCompletadas + " / " + totalPreguntas);
        
        double progreso = totalPreguntas > 0 ? (double) preguntasCompletadas / totalPreguntas : 0.0;
        progressBar.setProgress(progreso);
    }
    
    /**
     * Obtiene el nombre legible de la estrategia.
     * 
     * @param estrategia Estrategia de aprendizaje
     * @return Nombre legible de la estrategia
     */
    private String obtenerNombreEstrategia(EstrategiaAprendizaje estrategia) {
        if (estrategia == null) {
            return "Desconocida";
        }
        
        String nombre = estrategia.getNombre();
        switch (nombre) {
            case "Secuencial":
                return "Secuencial";
            case "Aleatoria":
                return "Aleatoria";
            case "RepeticionEspaciada":
                return "Repetición Espaciada";
            case "Repetir Incorrectas":
                return "Repetir Incorrectas";
            default:
                return nombre;
        }
    }
    
    /**
     * Actualiza el contenido del modal con una nueva pregunta.
     * 
     * @param contenidoPregunta Nodo JavaFX con el contenido de la pregunta
     */
    public void actualizarContenido(javafx.scene.Node contenidoPregunta) {
        contentSection.getChildren().clear();
        contentSection.getChildren().add(contenidoPregunta);
        
        logger.debug("Contenido del modal actualizado");
    }
    
    /**
     * Habilita o deshabilita el botón Verificar.
     * 
     * @param habilitado true para habilitar, false para deshabilitar
     */
    public void setVerificarHabilitado(boolean habilitado) {
        btnVerificar.setDisable(!habilitado);
    }
    
    /**
     * Habilita o deshabilita el botón Siguiente.
     * 
     * @param habilitado true para habilitar, false para deshabilitar
     */
    public void setSiguienteHabilitado(boolean habilitado) {
        btnSiguiente.setDisable(!habilitado);
    }
    
    /**
     * Actualiza la información del bloque actual.
     * 
     * @param bloqueActual Índice del bloque actual
     * @param totalBloques Total de bloques
     */
    public void actualizarBloqueActual(int bloqueActual, int totalBloques) {
        this.bloqueActual = bloqueActual;
        lblBloqueActual.setText("Bloque " + (bloqueActual + 1) + " de " + totalBloques);
    }
    
    /**
     * Obtiene la estrategia de aprendizaje.
     * 
     * @return Estrategia de aprendizaje
     */
    public EstrategiaAprendizaje getEstrategia() {
        return estrategia;
    }
    
    /**
     * Obtiene el curso que se está ejecutando.
     * 
     * @return Curso en ejecución
     */
    public CursoDTO getCurso() {
        return curso;
    }
    
    /**
     * Carga la primera pregunta del curso.
     */
    private void cargarPrimeraPregunta() {
        logger.debug("Cargando primera pregunta del curso");
        
        try {
            // Obtener la primera pregunta de la estrategia
            preguntaActualObj = estrategia.primeraPregunta();
            
            if (preguntaActualObj == null) {
                logger.warn("No hay preguntas disponibles en el curso");
                mostrarMensajeFinal("No hay preguntas disponibles en este curso.");
                return;
            }
            
            // Cargar el módulo correspondiente
            cargarModuloParaPregunta(preguntaActualObj);
            
            // Mostrar la pregunta
            mostrarPregunta(preguntaActualObj);
            
            // Actualizar estado de botones
            actualizarEstadoBotones();
            
            logger.info("Primera pregunta cargada correctamente: " + preguntaActualObj.getId());
            
        } catch (Exception e) {
            logger.error("Error al cargar primera pregunta", e);
            mostrarError("Error al cargar la primera pregunta: " + e.getMessage());
        }
    }
    
    /**
     * Carga el módulo correspondiente para una pregunta.
     * 
     * @param pregunta Pregunta para la cual cargar el módulo
     */
    private void cargarModuloParaPregunta(com.kursor.domain.Pregunta pregunta) {
        logger.debug("Cargando módulo para pregunta: " + pregunta.getId());
        
        try {
            String tipoPregunta = pregunta.getTipo();
            moduloActual = moduleManager.findModuleByQuestionType(tipoPregunta);
            
            if (moduloActual == null) {
                logger.error("No se encontró módulo para tipo: " + tipoPregunta);
                throw new RuntimeException("Módulo no encontrado para tipo: " + tipoPregunta);
            }
            
            logger.info("Módulo cargado correctamente: " + moduloActual.getModuleName());
            
        } catch (Exception e) {
            logger.error("Error al cargar módulo para pregunta", e);
            throw new RuntimeException("Error al cargar módulo: " + e.getMessage());
        }
    }
    
    /**
     * Muestra una pregunta en la interfaz.
     * 
     * @param pregunta Pregunta a mostrar
     */
    private void mostrarPregunta(com.kursor.domain.Pregunta pregunta) {
        logger.debug("Mostrando pregunta: " + pregunta.getId());
        
        try {
            // Crear la interfaz de usuario para la pregunta
            javafx.scene.Node preguntaUI = moduloActual.createQuestionUI(pregunta);
            
            // Actualizar el contenido del modal
            actualizarContenido(preguntaUI);
            
            // Actualizar información en la cabecera
            actualizarInformacionPregunta(pregunta);
            
            logger.info("Pregunta mostrada correctamente");
            
        } catch (Exception e) {
            logger.error("Error al mostrar pregunta", e);
            mostrarError("Error al mostrar la pregunta: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la información de la pregunta en la cabecera.
     * 
     * @param pregunta Pregunta actual
     */
    private void actualizarInformacionPregunta(com.kursor.domain.Pregunta pregunta) {
        // Actualizar contador de progreso
        int preguntasCompletadas = 0; // TODO: Calcular desde el estado real
        lblContador.setText(preguntasCompletadas + " / " + totalPreguntas);
        
        double progreso = totalPreguntas > 0 ? (double) preguntasCompletadas / totalPreguntas : 0.0;
        progressBar.setProgress(progreso);
        
        // Actualizar información del bloque si es necesario
        // TODO: Implementar lógica para obtener información del bloque actual
    }
    
    /**
     * Actualiza el estado de los botones según el tipo de pregunta.
     */
    private void actualizarEstadoBotones() {
        if (moduloActual == null) {
            setVerificarHabilitado(false);
            setSiguienteHabilitado(false);
            return;
        }
        
        boolean requiereValidacion = moduloActual.requiresValidation();
        
        // Para flashcards, habilitar directamente "Siguiente"
        if (!requiereValidacion) {
            setVerificarHabilitado(false);
            setSiguienteHabilitado(true);
        } else {
            // Para otros tipos, habilitar "Verificar" primero
            setVerificarHabilitado(true);
            setSiguienteHabilitado(false);
        }
    }
    
    /**
     * Valida la respuesta del usuario.
     */
    private void verificarRespuesta() {
        logger.debug("Verificando respuesta del usuario");
        
        try {
            // TODO: Obtener respuesta del usuario desde la UI
            Object respuestaUsuario = obtenerRespuestaUsuario();
            
            if (respuestaUsuario == null) {
                mostrarError("Por favor, proporciona una respuesta antes de verificar.");
                return;
            }
            
            // Validar respuesta usando el módulo
            boolean esCorrecta = moduloActual.validateAnswer(preguntaActualObj, respuestaUsuario);
            
            // Mostrar resultado
            mostrarResultadoValidacion(esCorrecta);
            
            // Habilitar botón "Siguiente"
            setSiguienteHabilitado(true);
            setVerificarHabilitado(false);
            
            logger.info("Respuesta verificada - Correcta: " + esCorrecta);
            
        } catch (Exception e) {
            logger.error("Error al verificar respuesta", e);
            mostrarError("Error al verificar la respuesta: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la respuesta del usuario desde la interfaz.
     * 
     * @return Respuesta del usuario o null si no hay respuesta
     */
    private Object obtenerRespuestaUsuario() {
        // TODO: Implementar lógica específica para obtener respuesta según el tipo de pregunta
        // Por ahora, retornamos null como placeholder
        logger.warn("Método obtenerRespuestaUsuario no implementado completamente");
        return null;
    }
    
    /**
     * Muestra el resultado de la validación al usuario.
     * 
     * @param esCorrecta true si la respuesta es correcta, false en caso contrario
     */
    private void mostrarResultadoValidacion(boolean esCorrecta) {
        String mensaje = esCorrecta ? 
            "✅ ¡Correcto! Respuesta acertada." : 
            "❌ Incorrecto. La respuesta correcta es: " + obtenerRespuestaCorrecta();
        
        // TODO: Mostrar mensaje en la interfaz de manera más elegante
        logger.info("Resultado validación: " + mensaje);
    }
    
    /**
     * Obtiene la respuesta correcta para mostrar al usuario.
     * 
     * @return Respuesta correcta como string
     */
    private String obtenerRespuestaCorrecta() {
        // TODO: Implementar lógica para obtener respuesta correcta según el tipo de pregunta
        return "Respuesta correcta";
    }
    
    /**
     * Avanza a la siguiente pregunta.
     */
    private void siguientePregunta() {
        logger.debug("Avanzando a siguiente pregunta");
        
        try {
            // Obtener siguiente pregunta de la estrategia
            com.kursor.domain.Pregunta siguientePregunta = estrategia.siguientePregunta();
            
            if (siguientePregunta == null) {
                // No hay más preguntas, finalizar curso
                logger.info("No hay más preguntas - finalizando curso");
                finalizarCurso();
                return;
            }
            
            // Actualizar pregunta actual
            preguntaActualObj = siguientePregunta;
            
            // Cargar módulo si es necesario
            if (!siguientePregunta.getTipo().equals(preguntaActualObj.getTipo())) {
                cargarModuloParaPregunta(siguientePregunta);
            }
            
            // Mostrar la nueva pregunta
            mostrarPregunta(siguientePregunta);
            
            // Actualizar estado de botones
            actualizarEstadoBotones();
            
            logger.info("Siguiente pregunta cargada: " + siguientePregunta.getId());
            
        } catch (Exception e) {
            logger.error("Error al avanzar a siguiente pregunta", e);
            mostrarError("Error al cargar la siguiente pregunta: " + e.getMessage());
        }
    }
    
    /**
     * Finaliza el curso y muestra estadísticas.
     */
    private void finalizarCurso() {
        logger.info("Finalizando curso");
        
        // TODO: Calcular estadísticas finales
        // TODO: Guardar progreso en sesión
        
        // Mostrar mensaje de finalización
        mostrarMensajeFinal("¡Curso completado! Has terminado todas las preguntas.");
        
        // Cerrar modal después de un breve delay
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(2000); // Esperar 2 segundos
                close();
            } catch (InterruptedException e) {
                close();
            }
        });
    }
    
    /**
     * Muestra un mensaje de finalización.
     * 
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarMensajeFinal(String mensaje) {
        VBox mensajeFinal = new VBox(20);
        mensajeFinal.setAlignment(javafx.geometry.Pos.CENTER);
        mensajeFinal.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 40px; -fx-background-radius: 10px;");
        
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        lblMensaje.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label lblGracias = new Label("¡Gracias por completar el curso!");
        lblGracias.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        lblGracias.setAlignment(javafx.geometry.Pos.CENTER);
        
        mensajeFinal.getChildren().addAll(lblMensaje, lblGracias);
        
        actualizarContenido(mensajeFinal);
        
        // Deshabilitar botones
        setVerificarHabilitado(false);
        setSiguienteHabilitado(false);
    }
    
    /**
     * Muestra un mensaje de error.
     * 
     * @param mensaje Mensaje de error
     */
    private void mostrarError(String mensaje) {
        VBox errorBox = new VBox(10);
        errorBox.setAlignment(javafx.geometry.Pos.CENTER);
        errorBox.setStyle("-fx-background-color: #fdf2f2; -fx-padding: 20px; -fx-background-radius: 8px; -fx-border-color: #f5c6cb; -fx-border-width: 1px;");
        
        Label lblError = new Label("❌ Error");
        lblError.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #721c24;");
        
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-font-size: 14px; -fx-text-fill: #721c24;");
        lblMensaje.setWrapText(true);
        
        errorBox.getChildren().addAll(lblError, lblMensaje);
        
        actualizarContenido(errorBox);
    }
} 