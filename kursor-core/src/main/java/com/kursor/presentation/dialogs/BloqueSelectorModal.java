package com.kursor.presentation.dialogs;

import com.kursor.presentation.controllers.BloqueSelectorController;
import com.kursor.presentation.viewmodels.BloqueSelectorViewModel;
import com.kursor.yaml.dto.BloqueDTO;
import com.kursor.yaml.dto.CursoDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.List;
import java.util.ArrayList;

/**
 * Modal refactorizado para seleccionar un bloque del curso siguiendo MVC.
 * 
 * <p>Esta versión refactorizada separa claramente las responsabilidades:</p>
 * <ul>
 *   <li><strong>Vista (View):</strong> Solo maneja la presentación y eventos de UI</li>
 *   <li><strong>Controlador (Controller):</strong> BloqueSelectorController maneja la lógica</li>
 *   <li><strong>Modelo (Model):</strong> BloqueSelectorViewModel maneja el estado</li>
 * </ul>
 * 
 * <p>Características de la interfaz:</p>
 * <ul>
 *   <li><strong>Tarjetas Dinámicas:</strong> Cada bloque se muestra como una tarjeta
 *       con icono, color temático e información detallada</li>
 *   <li><strong>Selección Visual:</strong> Las tarjetas cambian de apariencia al ser
 *       seleccionadas con efectos visuales</li>
 *   <li><strong>Información Completa:</strong> Cada tarjeta muestra título, número de preguntas
 *       y tipo del bloque</li>
 *   <li><strong>Carrusel Interactivo:</strong> Navegación por páginas con indicadores visuales</li>
 *   <li><strong>Animaciones Suaves:</strong> Transiciones de fade para mejorar la experiencia de usuario</li>
 *   <li><strong>Binding Reactivo:</strong> La interfaz se actualiza automáticamente con cambios en el ViewModel</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see BloqueSelectorController
 * @see BloqueSelectorViewModel
 * @see BloqueDTO
 * @see CursoDTO
 */
public class BloqueSelectorModal extends Stage {
    
    /** Logger para registrar eventos del modal */
    private static final Logger logger = LoggerFactory.getLogger(BloqueSelectorModal.class);
    
    /** Constantes de configuración visual */
    private static final int ANCHO_VENTANA = 800;
    private static final int ALTO_VENTANA = 555;
    private static final int TARJETAS_POR_PAGINA = 3;
    private static final int ANCHO_TARJETA = 200;
    private static final int ALTO_TARJETA = 160;
    private static final int RADIO_INDICADOR = 6;
    
    /** Constantes de animación para transiciones suaves */
    private static final int DURACION_FADE_OUT = 150; // milisegundos
    private static final int DURACION_FADE_IN = 200;  // milisegundos
    
    /** Componentes MVC */
    private final BloqueSelectorController controller;
    private final BloqueSelectorViewModel viewModel;
    
    /** Componentes de UI */
    private List<VBox> tarjetasBloques;
    private Button btnSeleccionar;
    private Button btnCancelar;
    private Label mensajeErrorLabel;
    private ProgressIndicator progressIndicator;
    private VBox carruselContainer;
    
    /**
     * Constructor del modal refactorizado.
     * 
     * @param owner Ventana padre del modal
     * @param curso Curso para el cual seleccionar el bloque
     * @throws IllegalArgumentException si owner o curso son null
     */
    public BloqueSelectorModal(Window owner, CursoDTO curso) {
        super();
        
        // Validación de parámetros
        if (owner == null) {
            logger.error("Error: owner no puede ser null");
            throw new IllegalArgumentException("Owner no puede ser null");
        }
        if (curso == null) {
            logger.error("Error: curso no puede ser null");
            throw new IllegalArgumentException("Curso no puede ser null");
        }
        
        logger.info("Creando modal refactorizado para curso: {} con {} bloques", 
                   curso.getTitulo(), curso.getBloques() != null ? curso.getBloques().size() : 0);
        
        // Inicializar componentes MVC
        this.controller = new BloqueSelectorController(curso);
        this.viewModel = controller.getViewModel();
        
        try {
            // Configurar el modal
            configurarModal(owner);
            
            // Crear la escena
            Scene scene = crearEscena();
            setScene(scene);
            
            // Configurar binding y eventos
            configurarBinding();
            configurarEventos();
            
            logger.info("Modal refactorizado creado correctamente para curso: {}", curso.getTitulo());
        } catch (Exception e) {
            logger.error("Error al crear el modal refactorizado: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo crear el modal refactorizado", e);
        }
    }
    
    /**
     * Configura las propiedades del modal.
     * 
     * @param owner Ventana padre del modal
     */
    private void configurarModal(Window owner) {
        logger.debug("Configurando propiedades del modal");
        
        // Configuración modal - bloquea la interacción con la ventana padre
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        
        // Propiedades de la ventana
        setResizable(false); // Tamaño fijo para mantener la consistencia visual
        setWidth(ANCHO_VENTANA);
        setHeight(ALTO_VENTANA);
        setTitle("📚 Seleccionar Bloque de Aprendizaje");
        
        // Centrar en la pantalla para mejor experiencia de usuario
        centerOnScreen();
        
        logger.debug("Modal configurado con dimensiones {}x{}", ANCHO_VENTANA, ALTO_VENTANA);
    }
    
    /**
     * Crea la escena del modal con todos los controles.
     * 
     * @return Escena configurada con todos los componentes
     */
    private Scene crearEscena() {
        logger.debug("Creando escena del modal");
        
        // Contenedor principal con espaciado y estilo
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        // Agregar componentes principales
        root.getChildren().addAll(
            crearHeader(),
            crearContenido(),
            crearBotones()
        );
        
        logger.debug("Escena del modal creada correctamente");
        return new Scene(root);
    }
    
    /**
     * Crea el header del modal con información del curso.
     * 
     * @return VBox con el header del modal
     */
    private VBox crearHeader() {
        logger.debug("Creando header del modal");
        
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        // Título principal
        Label titulo = new Label("Selecciona un Bloque");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setStyle("-fx-text-fill: #2c3e50;");
        
        // Subtítulo con información del curso (binding con ViewModel)
        Label subtitulo = new Label();
        subtitulo.setFont(Font.font("System", 14));
        subtitulo.setStyle("-fx-text-fill: #7f8c8d;");
        subtitulo.textProperty().bind(
            viewModel.cursoProperty().asString()
        );
        
        // Separador visual
        Separator separador = new Separator();
        separador.setMaxWidth(500);
        
        header.getChildren().addAll(titulo, subtitulo, separador);
        
        logger.debug("Header creado correctamente");
        return header;
    }
    
    /**
     * Crea el contenido principal del modal.
     * 
     * @return VBox con el contenido principal
     */
    private VBox crearContenido() {
        logger.debug("Creando contenido principal del modal");
        
        VBox contenido = new VBox(15);
        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setMaxWidth(600);
        
        // Indicador de carga
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(50, 50);
        
        // Mensaje de error
        mensajeErrorLabel = new Label();
        mensajeErrorLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-weight: bold;");
        mensajeErrorLabel.setVisible(false);
        
        // Contenedor del carrusel
        carruselContainer = new VBox(15);
        carruselContainer.setAlignment(Pos.CENTER);
        carruselContainer.setId("carruselContainer");
        
        contenido.getChildren().addAll(progressIndicator, mensajeErrorLabel, carruselContainer);
        
        logger.debug("Contenido principal creado correctamente");
        return contenido;
    }
    
    /**
     * Crea los botones de acción del modal.
     * 
     * @return HBox con los botones configurados
     */
    private HBox crearBotones() {
        logger.debug("Creando botones de acción del modal");
        
        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(30, 0, 20, 0));
        
        // Botón Cancelar
        btnCancelar = new Button("❌ Cancelar");
        btnCancelar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnCancelar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 12 25; " +
                           "-fx-cursor: hand; -fx-background-radius: 8;");
        btnCancelar.setPrefWidth(200);
        btnCancelar.setPrefHeight(40);
        
        // Botón Seleccionar (inicialmente deshabilitado)
        btnSeleccionar = new Button("✅ Seleccionar");
        btnSeleccionar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnSeleccionar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 12 25; " +
                           "-fx-cursor: hand; -fx-background-radius: 8;");
        btnSeleccionar.setPrefWidth(200);
        btnSeleccionar.setPrefHeight(40);
        btnSeleccionar.setDisable(true);
        
        botones.getChildren().addAll(btnCancelar, btnSeleccionar);
        
        logger.debug("Botones de acción creados correctamente");
        return botones;
    }
    
    /**
     * Configura el binding entre el ViewModel y la UI.
     */
    private void configurarBinding() {
        logger.debug("Configurando binding entre ViewModel y UI");
        
        // Binding del botón Seleccionar con la validación
        btnSeleccionar.disableProperty().bind(
            viewModel.bloqueValidoProperty().not()
        );
        
        // Binding del indicador de carga
        progressIndicator.visibleProperty().bind(
            viewModel.cargandoProperty()
        );
        
        // Binding del mensaje de error
        mensajeErrorLabel.textProperty().bind(
            viewModel.mensajeErrorProperty()
        );
        mensajeErrorLabel.visibleProperty().bind(
            viewModel.mensajeErrorProperty().isNotEmpty()
        );
        
        // Inicializar el carrusel con los datos actuales
        actualizarCarrusel();
        
        logger.debug("Binding configurado correctamente");
    }
    
    /**
     * Configura los eventos de la UI.
     */
    private void configurarEventos() {
        logger.debug("Configurando eventos de la UI");
        
        // Evento del botón Cancelar
        btnCancelar.setOnAction(e -> {
            logger.info("Usuario canceló la selección de bloque");
            controller.cancelarSeleccion();
            close();
        });
        
        // Evento del botón Seleccionar
        btnSeleccionar.setOnAction(e -> {
            logger.info("Usuario confirmó la selección de bloque");
            if (controller.confirmarSeleccion() != null) {
                close();
            } else {
                mostrarAlertaSeleccionRequerida();
            }
        });
        
        // Evento de cierre de ventana
        setOnCloseRequest(e -> {
            logger.info("Modal cerrado por el usuario sin seleccionar bloque");
            controller.cancelarSeleccion();
        });
        
        logger.debug("Eventos configurados correctamente");
    }
    
    /**
     * Actualiza el carrusel con los bloques del ViewModel.
     */
    private void actualizarCarrusel() {
        logger.debug("Actualizando carrusel con bloques del ViewModel");
        
        carruselContainer.getChildren().clear();
        
        List<BloqueDTO> bloques = viewModel.getBloquesDisponibles();
        if (bloques.isEmpty()) {
            carruselContainer.getChildren().add(crearMensajeNoBloques());
            return;
        }
        
        // Crear carrusel
        VBox carrusel = crearCarruselBloques();
        carruselContainer.getChildren().add(carrusel);
        
        logger.debug("Carrusel actualizado con {} bloques", bloques.size());
    }
    
    /**
     * Crea un mensaje cuando no hay bloques disponibles.
     * 
     * @return VBox con el mensaje de error
     */
    private VBox crearMensajeNoBloques() {
        logger.debug("Creando mensaje de no bloques disponibles");
        
        VBox mensaje = new VBox(15);
        mensaje.setAlignment(Pos.CENTER);
        mensaje.setStyle("-fx-background-color: #fed7d7; -fx-border-color: #f56565; " +
                        "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 30;");
        
        Label icono = new Label("⚠️");
        icono.setFont(Font.font("System", 48));
        
        Label titulo = new Label("No hay bloques disponibles");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setStyle("-fx-text-fill: #c53030;");
        
        Label descripcion = new Label("Este curso no tiene bloques de aprendizaje configurados.\n" +
                                    "Contacta al administrador del curso.");
        descripcion.setFont(Font.font("System", 14));
        descripcion.setStyle("-fx-text-fill: #742a2a; -fx-text-alignment: center;");
        descripcion.setWrapText(true);
        
        mensaje.getChildren().addAll(icono, titulo, descripcion);
        
        logger.debug("Mensaje de no bloques creado correctamente");
        return mensaje;
    }
    
    /**
     * Crea el carrusel de bloques con navegación.
     * 
     * @return VBox con el carrusel configurado
     */
    private VBox crearCarruselBloques() {
        logger.debug("Creando carrusel de bloques");
        
        VBox carrusel = new VBox(20);
        carrusel.setAlignment(Pos.CENTER);
        
        List<BloqueDTO> bloques = viewModel.getBloquesDisponibles();
        int totalPaginas = (int) Math.ceil((double) bloques.size() / TARJETAS_POR_PAGINA);
        final int[] paginaActual = {0};
        
        // Contenedor de tarjetas
        VBox contenedorTarjetas = new VBox(15);
        contenedorTarjetas.setAlignment(Pos.CENTER);
        contenedorTarjetas.setId("contenedorTarjetas");
        
        // Botones de navegación
        HBox navegacion = new HBox(20);
        navegacion.setAlignment(Pos.CENTER);
        
        Button btnAnterior = crearBotonNavegacion("◀", true);
        Button btnSiguiente = crearBotonNavegacion("▶", false);
        
        // Indicadores de página
        HBox indicadores = crearIndicadoresPagina(totalPaginas, paginaActual, () -> mostrarPagina(paginaActual[0], contenedorTarjetas, bloques));
        
        navegacion.getChildren().addAll(btnAnterior, indicadores, btnSiguiente);
        
        // Función para mostrar página
        Runnable mostrarPagina = () -> {
            mostrarPagina(paginaActual[0], contenedorTarjetas, bloques);
            actualizarBotonesNavegacion(btnAnterior, btnSiguiente, paginaActual[0], totalPaginas);
            actualizarIndicadores(indicadores, paginaActual[0], totalPaginas);
        };
        
        // Eventos de navegación
        btnAnterior.setOnAction(e -> {
            if (paginaActual[0] > 0) {
                paginaActual[0]--;
                mostrarPagina.run();
            }
        });
        
        btnSiguiente.setOnAction(e -> {
            if (paginaActual[0] < totalPaginas - 1) {
                paginaActual[0]++;
                mostrarPagina.run();
            }
        });
        
        // Mostrar primera página
        mostrarPagina.run();
        
        carrusel.getChildren().addAll(contenedorTarjetas, navegacion);
        
        logger.debug("Carrusel creado con {} páginas", totalPaginas);
        return carrusel;
    }
    
    /**
     * Crea un botón de navegación.
     * 
     * @param texto Texto del botón
     * @param esAnterior true si es botón anterior, false si es siguiente
     * @return Button configurado
     */
    private Button crearBotonNavegacion(String texto, boolean esAnterior) {
        Button boton = new Button(texto);
        boton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        boton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8 15; " +
                      "-fx-cursor: hand; -fx-background-radius: 6;");
        boton.setPrefWidth(120);
        boton.setPrefHeight(35);
        
        return boton;
    }
    
    /**
     * Actualiza el estado de los botones de navegación.
     * 
     * @param btnAnterior Botón anterior
     * @param btnSiguiente Botón siguiente
     * @param paginaActual Página actual
     * @param totalPaginas Total de páginas
     */
    private void actualizarBotonesNavegacion(Button btnAnterior, Button btnSiguiente, int paginaActual, int totalPaginas) {
        btnAnterior.setDisable(paginaActual == 0);
        btnSiguiente.setDisable(paginaActual == totalPaginas - 1);
    }
    
    /**
     * Crea los indicadores de página.
     * 
     * @param totalPaginas Total de páginas
     * @param paginaActual Array con la página actual
     * @param mostrarPagina Función para mostrar página
     * @return HBox con los indicadores
     */
    private HBox crearIndicadoresPagina(int totalPaginas, final int[] paginaActual, Runnable mostrarPagina) {
        HBox indicadores = new HBox(8);
        indicadores.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < totalPaginas; i++) {
            Circle indicador = new Circle(RADIO_INDICADOR);
            final int pagina = i;
            
            indicador.setOnMouseClicked(e -> {
                paginaActual[0] = pagina;
                mostrarPagina.run();
            });
            
            indicadores.getChildren().add(indicador);
        }
        
        return indicadores;
    }
    
    /**
     * Actualiza el estado visual de los indicadores.
     * 
     * @param contenedorIndicadores Contenedor de indicadores
     * @param paginaActual Página actual
     * @param totalPaginas Total de páginas
     */
    private void actualizarIndicadores(HBox contenedorIndicadores, int paginaActual, int totalPaginas) {
        for (int i = 0; i < contenedorIndicadores.getChildren().size(); i++) {
            Circle indicador = (Circle) contenedorIndicadores.getChildren().get(i);
            if (i == paginaActual) {
                indicador.setFill(Color.web("#007bff"));
                indicador.setStroke(Color.web("#007bff"));
            } else {
                indicador.setFill(Color.web("#dee2e6"));
                indicador.setStroke(Color.web("#6c757d"));
            }
        }
    }
    
    /**
     * Muestra una página específica del carrusel.
     * 
     * @param pagina Página a mostrar
     * @param contenedor Contenedor de tarjetas
     * @param bloques Lista de bloques
     */
    private void mostrarPagina(int pagina, VBox contenedor, List<BloqueDTO> bloques) {
        logger.debug("Mostrando página {} del carrusel", pagina);
        
        contenedor.getChildren().clear();
        tarjetasBloques = new ArrayList<>();
        
        int inicio = pagina * TARJETAS_POR_PAGINA;
        int fin = Math.min(inicio + TARJETAS_POR_PAGINA, bloques.size());
        
        HBox filaTarjetas = new HBox(20);
        filaTarjetas.setAlignment(Pos.CENTER);
        
        for (int i = inicio; i < fin; i++) {
            BloqueDTO bloque = bloques.get(i);
            VBox tarjeta = crearTarjetaBloque(bloque);
            tarjetasBloques.add(tarjeta);
            filaTarjetas.getChildren().add(tarjeta);
        }
        
        contenedor.getChildren().add(filaTarjetas);
        
        logger.debug("Página {} mostrada con {} bloques", pagina, fin - inicio);
    }
    
    /**
     * Crea una tarjeta para un bloque.
     * 
     * @param bloque Bloque para crear la tarjeta
     * @return VBox con la tarjeta configurada
     */
    private VBox crearTarjetaBloque(BloqueDTO bloque) {
        logger.debug("Creando tarjeta para bloque: {}", bloque.getTitulo());
        
        VBox tarjeta = new VBox(10);
        tarjeta.setPrefWidth(ANCHO_TARJETA);
        tarjeta.setPrefHeight(ALTO_TARJETA);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setCursor(javafx.scene.Cursor.HAND);
        
        // Color temático del bloque (usar color por defecto ya que BloqueDTO no tiene colorTema)
        String colorTema = "#6c757d"; // Color por defecto
        String estiloBase = crearEstiloBaseTarjeta(colorTema);
        String estiloSeleccionado = crearEstiloSeleccionadoTarjeta(colorTema);
        
        tarjeta.setStyle(estiloBase);
        
        // Icono del bloque
        Label icono = crearIconoBloque(bloque, colorTema);
        
        // Título del bloque
        Label titulo = crearTituloBloque(bloque);
        
        // Información del bloque
        VBox info = crearInfoBloque(bloque);
        
        tarjeta.getChildren().addAll(icono, titulo, info);
        
        // Tooltip informativo
        Tooltip tooltip = crearTooltipBloque(bloque);
        Tooltip.install(tarjeta, tooltip);
        
        // Configurar eventos
        configurarEventosTarjeta(tarjeta, bloque, estiloBase, estiloSeleccionado);
        
        logger.debug("Tarjeta creada correctamente para bloque: {}", bloque.getTitulo());
        return tarjeta;
    }
    
    /**
     * Crea el estilo base para una tarjeta.
     * 
     * @param colorTema Color temático del bloque
     * @return String con el estilo CSS
     */
    private String crearEstiloBaseTarjeta(String colorTema) {
        return String.format(
            "-fx-background-color: white; -fx-border-color: %s; -fx-border-width: 2; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(3, 3, 3, rgba(0,0,0,0.2));",
            colorTema
        );
    }
    
    /**
     * Crea el estilo para una tarjeta seleccionada.
     * 
     * @param colorTema Color temático del bloque
     * @return String con el estilo CSS
     */
    private String crearEstiloSeleccionadoTarjeta(String colorTema) {
        return String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 3; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(5, 5, 5, rgba(0,0,0,0.3));",
            colorTema, colorTema
        );
    }
    
    /**
     * Crea el icono para un bloque.
     * 
     * @param bloque Bloque para crear el icono
     * @param colorTema Color temático
     * @return Label con el icono
     */
    private Label crearIconoBloque(BloqueDTO bloque, String colorTema) {
        String icono = "📚"; // Icono por defecto
        if (bloque.getTipo() != null) {
            switch (bloque.getTipo().toLowerCase()) {
                case "test": icono = "📝"; break;
                case "flashcard": icono = "🗂️"; break;
                case "completar": icono = "✏️"; break;
                case "verdadero_falso": icono = "✅"; break;
                default: icono = "📚"; break;
            }
        }
        
        Label labelIcono = new Label(icono);
        labelIcono.setFont(Font.font("System", 32));
        labelIcono.setStyle("-fx-text-fill: " + colorTema + ";");
        
        return labelIcono;
    }
    
    /**
     * Crea el título para un bloque.
     * 
     * @param bloque Bloque para crear el título
     * @return Label con el título
     */
    private Label crearTituloBloque(BloqueDTO bloque) {
        Label titulo = new Label(bloque.getTitulo());
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titulo.setStyle("-fx-text-fill: #2c3e50; -fx-text-alignment: center;");
        titulo.setWrapText(true);
        titulo.setMaxWidth(ANCHO_TARJETA - 30);
        
        return titulo;
    }
    
    /**
     * Crea la información para un bloque.
     * 
     * @param bloque Bloque para crear la información
     * @return VBox con la información
     */
    private VBox crearInfoBloque(BloqueDTO bloque) {
        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER);
        
        // Número de preguntas
        int numPreguntas = bloque.getPreguntas() != null ? bloque.getPreguntas().size() : 0;
        Label lblPreguntas = new Label(numPreguntas + " preguntas");
        lblPreguntas.setFont(Font.font("Segoe UI", 12));
        lblPreguntas.setStyle("-fx-text-fill: #6c757d;");
        
        // Tipo de bloque
        String tipo = bloque.getTipo() != null ? bloque.getTipo() : "Sin tipo";
        Label lblTipo = new Label("Tipo: " + tipo);
        lblTipo.setFont(Font.font("Segoe UI", 10));
        lblTipo.setStyle("-fx-text-fill: #6c757d;");
        
        info.getChildren().addAll(lblPreguntas, lblTipo);
        
        return info;
    }
    
    /**
     * Crea un tooltip para un bloque.
     * 
     * @param bloque Bloque para crear el tooltip
     * @return Tooltip configurado
     */
    private Tooltip crearTooltipBloque(BloqueDTO bloque) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("Título: ").append(bloque.getTitulo()).append("\n");
        contenido.append("Tipo: ").append(bloque.getTipo() != null ? bloque.getTipo() : "Sin especificar").append("\n");
        contenido.append("Preguntas: ").append(bloque.getPreguntas() != null ? bloque.getPreguntas().size() : 0);
        
        Tooltip tooltip = new Tooltip(contenido.toString());
        tooltip.setFont(Font.font("Segoe UI", 12));
        tooltip.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-border-color: #34495e;");
        
        return tooltip;
    }
    
    /**
     * Configura los eventos para una tarjeta.
     * 
     * @param tarjeta Tarjeta a configurar
     * @param bloque Bloque asociado
     * @param estiloBase Estilo base de la tarjeta
     * @param estiloSeleccionado Estilo de selección
     */
    private void configurarEventosTarjeta(VBox tarjeta, BloqueDTO bloque, String estiloBase, String estiloSeleccionado) {
        tarjeta.setOnMouseClicked(e -> {
            logger.debug("Usuario seleccionó bloque: {}", bloque.getTitulo());
            
            // Deseleccionar todas las tarjetas
            deseleccionarTodasLasTarjetas();
            
            // Seleccionar la tarjeta actual
            seleccionarTarjeta(tarjeta, bloque, estiloSeleccionado);
            
            // Notificar al controlador
            controller.seleccionarBloque(bloque);
        });
        
        // Efectos hover
        tarjeta.setOnMouseEntered(e -> {
            if (!controller.isBloqueSeleccionado()) {
                tarjeta.setStyle(ajustarColor(estiloBase, 0.95));
            }
        });
        
        tarjeta.setOnMouseExited(e -> {
            if (!controller.isBloqueSeleccionado()) {
                tarjeta.setStyle(estiloBase);
            }
        });
    }
    
    /**
     * Deselecciona todas las tarjetas.
     */
    private void deseleccionarTodasLasTarjetas() {
        for (VBox tarjeta : tarjetasBloques) {
            BloqueDTO bloque = encontrarBloquePorTarjeta(tarjeta);
            if (bloque != null) {
                String colorTema = "#6c757d"; // Color por defecto
                tarjeta.setStyle(crearEstiloBaseTarjeta(colorTema));
                restaurarColoresTexto(tarjeta, bloque);
            }
        }
    }
    
    /**
     * Restaura los colores de texto de una tarjeta.
     * 
     * @param tarjeta Tarjeta a restaurar
     * @param bloque Bloque asociado
     */
    private void restaurarColoresTexto(VBox tarjeta, BloqueDTO bloque) {
        for (javafx.scene.Node nodo : tarjeta.getChildren()) {
            if (nodo instanceof Label) {
                Label label = (Label) nodo;
                if (label.getText().equals(bloque.getTitulo())) {
                    label.setStyle("-fx-text-fill: #2c3e50; -fx-text-alignment: center;");
                } else if (label.getText().contains("preguntas") || label.getText().contains("Tipo:")) {
                    label.setStyle("-fx-text-fill: #6c757d;");
                }
            }
        }
    }
    
    /**
     * Selecciona una tarjeta específica.
     * 
     * @param tarjeta Tarjeta a seleccionar
     * @param bloque Bloque asociado
     * @param estiloSeleccionado Estilo de selección
     */
    private void seleccionarTarjeta(VBox tarjeta, BloqueDTO bloque, String estiloSeleccionado) {
        tarjeta.setStyle(estiloSeleccionado);
        
        // Cambiar colores de texto a blanco
        for (javafx.scene.Node nodo : tarjeta.getChildren()) {
            if (nodo instanceof Label) {
                ((Label) nodo).setStyle("-fx-text-fill: white;");
            }
        }
    }
    
    /**
     * Encuentra un bloque por su tarjeta.
     * 
     * @param tarjeta Tarjeta del bloque
     * @return BloqueDTO encontrado o null
     */
    private BloqueDTO encontrarBloquePorTarjeta(VBox tarjeta) {
        try {
            // Obtener el título del bloque desde la tarjeta
            Label lblTitulo = (Label) tarjeta.getChildren().get(1);
            String tituloBloque = lblTitulo.getText();
            
            // Buscar el bloque correspondiente
            for (BloqueDTO bloque : viewModel.getBloquesDisponibles()) {
                if (bloque.getTitulo().equals(tituloBloque)) {
                    return bloque;
                }
            }
            
            logger.warn("No se encontró bloque para tarjeta con título: {}", tituloBloque);
            return null;
            
        } catch (Exception e) {
            logger.error("Error al buscar bloque por tarjeta: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Ajusta el color para crear variaciones.
     * 
     * @param estilo Estilo CSS base
     * @param factor Factor de ajuste
     * @return Estilo ajustado
     */
    private String ajustarColor(String estilo, double factor) {
        // TODO: Implementar ajuste real del color
        logger.debug("Ajustando estilo con factor {}", factor);
        return estilo;
    }
    
    /**
     * Muestra una alerta cuando se requiere selección.
     */
    private void mostrarAlertaSeleccionRequerida() {
        logger.debug("Mostrando alerta de selección requerida");
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Selección Requerida");
        alert.setHeaderText("No se ha seleccionado un bloque");
        alert.setContentText("Por favor, selecciona un bloque de aprendizaje antes de continuar.");
        
        alert.initOwner(this);
        alert.initModality(Modality.WINDOW_MODAL);
        
        alert.showAndWait();
        
        logger.debug("Alerta de selección requerida mostrada al usuario");
    }
    
    /**
     * Muestra el modal y espera la selección del usuario.
     * 
     * @return BloqueDTO seleccionado, o null si se canceló la operación
     */
    public BloqueDTO mostrarYEsperar() {
        logger.info("Mostrando modal de selección de bloque para curso: {}", controller.getCurso().getTitulo());
        
        try {
            showAndWait(); // Bloquea hasta que se cierre el modal
            BloqueDTO bloqueSeleccionado = controller.getBloqueSeleccionado();
            logger.info("Modal cerrado. Bloque seleccionado: {}", 
                       bloqueSeleccionado != null ? bloqueSeleccionado.getTitulo() : "Ninguno");
            return bloqueSeleccionado;
        } catch (Exception e) {
            logger.error("Error al mostrar el modal de selección de bloque: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene el bloque seleccionado.
     * 
     * @return BloqueDTO seleccionado o null si no se ha seleccionado ninguno
     */
    public BloqueDTO getBloqueSeleccionado() {
        return controller.getBloqueSeleccionado();
    }
    
    /**
     * Verifica si se seleccionó un bloque.
     * 
     * @return true si se seleccionó un bloque, false en caso contrario
     */
    public boolean isBloqueSeleccionado() {
        return controller.isBloqueSeleccionado();
    }
} 