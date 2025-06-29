package com.kursor.presentation.dialogs;

import com.kursor.presentation.controllers.StrategySelectorController;
import com.kursor.presentation.viewmodels.StrategySelectorViewModel;
import com.kursor.strategy.EstrategiaModule;
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

import java.util.List;
import java.util.ArrayList;

/**
 * Modal refactorizado para seleccionar la estrategia de aprendizaje siguiendo MVC.
 * 
 * <p>Esta versión refactorizada separa claramente las responsabilidades:</p>
 * <ul>
 *   <li><strong>Vista (View):</strong> Solo maneja la presentación y eventos de UI</li>
 *   <li><strong>Controlador (Controller):</strong> StrategySelectorController maneja la lógica</li>
 *   <li><strong>Modelo (Model):</strong> StrategySelectorViewModel maneja el estado</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.2.0
 * @since 1.0.0
 */
public class StrategySelectorModal extends Stage {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategySelectorModal.class);
    
    /** Constantes de configuración visual */
    private static final int ANCHO_VENTANA = 800;
    private static final int ALTO_VENTANA = 555;
    private static final int TARJETAS_POR_PAGINA = 3;
    private static final int ANCHO_TARJETA = 200;
    private static final int ALTO_TARJETA = 160;
    private static final int RADIO_INDICADOR = 6;
    
    /** Componentes MVC */
    private final StrategySelectorController controller;
    private final StrategySelectorViewModel viewModel;
    
    /** Componentes de UI */
    private List<VBox> tarjetasEstrategias;
    private Button btnSeleccionar;
    private Button btnCancelar;
    private Label mensajeErrorLabel;
    private ProgressIndicator progressIndicator;
    
    /**
     * Constructor del modal refactorizado.
     * 
     * @param owner Ventana padre del modal
     * @param curso Curso para el cual seleccionar la estrategia
     */
    public StrategySelectorModal(Window owner, CursoDTO curso) {
        super();
        
        // Validación de parámetros
        if (owner == null) {
            throw new IllegalArgumentException("Owner no puede ser null");
        }
        if (curso == null) {
            throw new IllegalArgumentException("Curso no puede ser null");
        }
        
        logger.info("Creando modal refactorizado para curso: {}", curso.getTitulo());
        
        // Inicializar componentes MVC
        this.controller = new StrategySelectorController(curso);
        this.viewModel = new StrategySelectorViewModel();
        
        // Configurar el modal
        configurarModal(owner);
        
        // Crear la escena
        Scene scene = crearEscena();
        setScene(scene);
        
        // Configurar binding y eventos
        configurarBinding();
        configurarEventos();
        
        // Inicializar datos
        inicializarDatos();
        
        logger.info("Modal refactorizado creado correctamente");
    }
    
    /**
     * Configura las propiedades del modal.
     * 
     * @param owner Ventana padre del modal
     */
    private void configurarModal(Window owner) {
        logger.debug("Configurando propiedades del modal");
        
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        
        setResizable(false);
        setWidth(ANCHO_VENTANA);
        setHeight(ALTO_VENTANA);
        setTitle("🎯 Seleccionar Estrategia de Aprendizaje");
        
        centerOnScreen();
    }
    
    /**
     * Crea la escena del modal.
     * 
     * @return Escena configurada
     */
    private Scene crearEscena() {
        logger.debug("Creando escena del modal");
        
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        // Header
        VBox header = crearHeader();
        
        // Contenido principal
        VBox contenido = crearContenido();
        
        // Separador
        Separator separador = new Separator();
        separador.setMaxWidth(600);
        
        // Botones
        HBox botones = crearBotones();
        
        root.getChildren().addAll(header, contenido, separador, botones);
        
        return new Scene(root);
    }
    
    /**
     * Crea el header del modal.
     * 
     * @return Contenedor del header
     */
    private VBox crearHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("🎯 Seleccionar Estrategia de Aprendizaje");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #4a5568;");
        
        // Binding con el ViewModel para el título del curso
        Label subtitleLabel = new Label();
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        subtitleLabel.textProperty().bind(
            viewModel.cursoProperty().asString()
        );
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }
    
    /**
     * Crea el contenido principal del modal.
     * 
     * @return Contenedor del contenido
     */
    private VBox crearContenido() {
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
        
        // Contenedor de carrusel (se llenará dinámicamente)
        VBox carruselContainer = new VBox(15);
        carruselContainer.setAlignment(Pos.CENTER);
        carruselContainer.setId("carruselContainer");
        
        contenido.getChildren().addAll(progressIndicator, mensajeErrorLabel, carruselContainer);
        
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
            viewModel.estrategiaValidaProperty().not()
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
        
        // Binding de la lista de estrategias
        viewModel.estrategiasProperty().addListener((obs, oldVal, newVal) -> {
            actualizarCarrusel(newVal);
        });
        
        logger.debug("Binding configurado correctamente");
    }
    
    /**
     * Configura los eventos de la UI.
     */
    private void configurarEventos() {
        logger.debug("Configurando eventos de la UI");
        
        // Evento del botón Cancelar
        btnCancelar.setOnAction(e -> {
            logger.info("Usuario canceló la selección");
            controller.cancelarSeleccion();
            close();
        });
        
        // Evento del botón Seleccionar
        btnSeleccionar.setOnAction(e -> {
            logger.info("Usuario confirmó la selección");
            if (controller.confirmarSeleccion()) {
                close();
            } else {
                mostrarAlertaSeleccionRequerida();
            }
        });
        
        // Evento de cierre de ventana
        setOnCloseRequest(e -> {
            logger.info("Modal cerrado por el usuario");
            controller.cancelarSeleccion();
        });
        
        logger.debug("Eventos configurados correctamente");
    }
    
    /**
     * Inicializa los datos del modal.
     */
    private void inicializarDatos() {
        logger.debug("Inicializando datos del modal");
        
        // Inicializar ViewModel
        viewModel.inicializar(controller.getCurso());
        
        // Cargar estrategias desde el controlador
        List<EstrategiaModule> estrategias = controller.cargarEstrategias();
        viewModel.cargarEstrategias(estrategias);
        
        logger.info("Datos inicializados: {} estrategias cargadas", estrategias.size());
    }
    
    /**
     * Actualiza el carrusel con las estrategias.
     * 
     * @param estrategias Lista de estrategias a mostrar
     */
    private void actualizarCarrusel(List<EstrategiaModule> estrategias) {
        logger.debug("Actualizando carrusel con {} estrategias", estrategias.size());
        
        VBox carruselContainer = (VBox) getScene().lookup("#carruselContainer");
        if (carruselContainer == null) {
            logger.error("No se encontró el contenedor del carrusel");
            return;
        }
        
        carruselContainer.getChildren().clear();
        
        if (estrategias.isEmpty()) {
            carruselContainer.getChildren().add(crearMensajeNoEstrategias());
            return;
        }
        
        // Crear carrusel
        VBox carrusel = crearCarruselEstrategias(estrategias);
        carruselContainer.getChildren().add(carrusel);
        
        logger.debug("Carrusel actualizado correctamente");
    }
    
    /**
     * Crea el carrusel de estrategias.
     * 
     * @param estrategias Lista de estrategias
     * @return Contenedor del carrusel
     */
    private VBox crearCarruselEstrategias(List<EstrategiaModule> estrategias) {
        VBox carrusel = new VBox(15);
        carrusel.setAlignment(Pos.CENTER);
        
        // Contenedor principal del carrusel
        HBox contenedorCarrusel = new HBox(20);
        contenedorCarrusel.setAlignment(Pos.CENTER);
        contenedorCarrusel.setPadding(new Insets(10));
        
        // Botones de navegación
        Button btnAnterior = crearBotonNavegacion("◀", true);
        Button btnSiguiente = crearBotonNavegacion("▶", false);
        
        // Contenedor para tarjetas
        HBox contenedorTarjetas = new HBox(15);
        contenedorTarjetas.setAlignment(Pos.CENTER);
        contenedorTarjetas.setPrefWidth(ANCHO_VENTANA);
        
        // Crear tarjetas
        tarjetasEstrategias = new ArrayList<>();
        for (EstrategiaModule estrategia : estrategias) {
            VBox tarjeta = crearTarjetaEstrategia(estrategia);
            tarjetasEstrategias.add(tarjeta);
        }
        
        // Variables para control del carrusel
        final int[] paginaActual = {0};
        final int totalPaginas = (int) Math.ceil((double) estrategias.size() / TARJETAS_POR_PAGINA);
        
        // Función para mostrar página
        Runnable mostrarPagina = () -> {
            mostrarPaginaCarrusel(contenedorTarjetas, paginaActual[0], totalPaginas);
        };
        
        // Indicadores de página
        HBox indicadores = crearIndicadoresPagina(totalPaginas, paginaActual, mostrarPagina);
        
        // Configurar eventos de navegación
        configurarEventosNavegacion(btnAnterior, btnSiguiente, paginaActual, totalPaginas, mostrarPagina);
        
        contenedorCarrusel.getChildren().addAll(btnAnterior, contenedorTarjetas, btnSiguiente);
        carrusel.getChildren().addAll(contenedorCarrusel, indicadores);
        
        // Mostrar primera página
        mostrarPagina.run();
        
        return carrusel;
    }
    
    /**
     * Crea una tarjeta de estrategia.
     * 
     * @param estrategia Estrategia a mostrar
     * @return Tarjeta creada
     */
    private VBox crearTarjetaEstrategia(EstrategiaModule estrategia) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPrefSize(ANCHO_TARJETA, ALTO_TARJETA);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setCursor(javafx.scene.Cursor.HAND);
        
        // Estilos base
        String estiloBase = crearEstiloBaseTarjeta(estrategia.getColorTema());
        tarjeta.setStyle(estiloBase);
        
        // Icono
        Label icono = new Label(estrategia.getIcon());
        icono.setFont(Font.font("System", 32));
        icono.setStyle("-fx-text-fill: " + estrategia.getColorTema() + ";");
        
        // Nombre
        Label nombre = new Label(estrategia.getNombre());
        nombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        nombre.setStyle("-fx-text-fill: #2d3748; -fx-text-alignment: center;");
        nombre.setWrapText(true);
        
        // Descripción
        Label descripcion = new Label(estrategia.getDescripcion());
        descripcion.setFont(Font.font("System", 11));
        descripcion.setStyle("-fx-text-fill: #718096; -fx-text-alignment: center;");
        descripcion.setWrapText(true);
        descripcion.setMaxWidth(ANCHO_TARJETA - 30);
        
        tarjeta.getChildren().addAll(icono, nombre, descripcion);
        
        // Configurar eventos de la tarjeta
        configurarEventosTarjeta(tarjeta, estrategia);
        
        return tarjeta;
    }
    
    /**
     * Configura los eventos para una tarjeta de estrategia.
     * 
     * @param tarjeta Tarjeta a configurar
     * @param estrategia Estrategia asociada
     */
    private void configurarEventosTarjeta(VBox tarjeta, EstrategiaModule estrategia) {
        tarjeta.setOnMouseClicked(e -> {
            logger.debug("Usuario seleccionó estrategia: {}", estrategia.getNombre());
            
            // Deseleccionar todas las tarjetas
            deseleccionarTodasLasTarjetas();
            
            // Seleccionar esta tarjeta
            seleccionarTarjeta(tarjeta, estrategia);
            
            // Notificar al controlador
            controller.seleccionarEstrategia(estrategia.getNombre());
            
            // Actualizar el ViewModel para activar el botón
            viewModel.seleccionarEstrategia(estrategia.getNombre());
        });
        
        // Efectos hover
        tarjeta.setOnMouseEntered(e -> {
            if (!estrategia.getNombre().equals(controller.getEstrategiaSeleccionada())) {
                tarjeta.setStyle(crearEstiloBaseTarjeta(estrategia.getColorTema()) + 
                               "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            }
        });
        
        tarjeta.setOnMouseExited(e -> {
            if (!estrategia.getNombre().equals(controller.getEstrategiaSeleccionada())) {
                tarjeta.setStyle(crearEstiloBaseTarjeta(estrategia.getColorTema()));
            }
        });
    }
    
    /**
     * Crea el estilo base de una tarjeta.
     * 
     * @param colorTema Color tema de la estrategia
     * @return Estilo CSS
     */
    private String crearEstiloBaseTarjeta(String colorTema) {
        return String.format(
            "-fx-background-color: white; -fx-border-color: %s; -fx-border-width: 2; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(3, 3, 3, rgba(0,0,0,0.2));",
            colorTema
        );
    }
    
    /**
     * Crea el estilo de tarjeta seleccionada.
     * 
     * @param colorTema Color tema de la estrategia
     * @return Estilo CSS
     */
    private String crearEstiloSeleccionadoTarjeta(String colorTema) {
        return String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 3; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(5, 5, 5, rgba(0,0,0,0.3));",
            colorTema, colorTema
        );
    }
    
    /**
     * Deselecciona todas las tarjetas.
     */
    private void deseleccionarTodasLasTarjetas() {
        for (VBox tarjeta : tarjetasEstrategias) {
            EstrategiaModule estrategia = encontrarEstrategiaPorTarjeta(tarjeta);
            if (estrategia != null) {
                tarjeta.setStyle(crearEstiloBaseTarjeta(estrategia.getColorTema()));
            }
        }
    }
    
    /**
     * Selecciona una tarjeta específica.
     * 
     * @param tarjeta Tarjeta a seleccionar
     * @param estrategia Estrategia asociada
     */
    private void seleccionarTarjeta(VBox tarjeta, EstrategiaModule estrategia) {
        tarjeta.setStyle(crearEstiloSeleccionadoTarjeta(estrategia.getColorTema()));
        
        // Cambiar colores de texto
        for (javafx.scene.Node nodo : tarjeta.getChildren()) {
            if (nodo instanceof Label) {
                ((Label) nodo).setStyle("-fx-text-fill: white;");
            }
        }
    }
    
    /**
     * Encuentra una estrategia por su tarjeta.
     * 
     * @param tarjeta Tarjeta de la estrategia
     * @return Estrategia encontrada o null
     */
    private EstrategiaModule encontrarEstrategiaPorTarjeta(VBox tarjeta) {
        try {
            Label lblNombre = (Label) tarjeta.getChildren().get(1);
            String nombreEstrategia = lblNombre.getText();
            
            return viewModel.getEstrategias().stream()
                .filter(estrategia -> estrategia.getNombre().equals(nombreEstrategia))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar estrategia por tarjeta: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Crea un botón de navegación.
     * 
     * @param texto Texto del botón
     * @param esAnterior true si es botón anterior, false si es siguiente
     * @return Botón creado
     */
    private Button crearBotonNavegacion(String texto, boolean esAnterior) {
        Button boton = new Button(texto);
        boton.setFont(Font.font("System", FontWeight.BOLD, 18));
        boton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-cursor: hand; " +
                      "-fx-background-radius: 20; -fx-min-width: 40; -fx-min-height: 40;");
        return boton;
    }
    
    /**
     * Crea los indicadores de página.
     * 
     * @param totalPaginas Total de páginas
     * @param paginaActual Página actual
     * @param mostrarPagina Función para mostrar página
     * @return Contenedor de indicadores
     */
    private HBox crearIndicadoresPagina(int totalPaginas, int[] paginaActual, Runnable mostrarPagina) {
        HBox indicadores = new HBox(8);
        indicadores.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < totalPaginas; i++) {
            Circle indicador = new Circle(RADIO_INDICADOR);
            indicador.setFill(i == 0 ? Color.web("#667eea") : Color.web("#cbd5e0"));
            indicador.setCursor(javafx.scene.Cursor.HAND);
            
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
     * Configura los eventos de navegación.
     * 
     * @param btnAnterior Botón anterior
     * @param btnSiguiente Botón siguiente
     * @param paginaActual Página actual
     * @param totalPaginas Total de páginas
     * @param mostrarPagina Función para mostrar página
     */
    private void configurarEventosNavegacion(Button btnAnterior, Button btnSiguiente, 
                                           int[] paginaActual, int totalPaginas, Runnable mostrarPagina) {
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
    }
    
    /**
     * Muestra una página específica del carrusel.
     * 
     * @param contenedor Contenedor de tarjetas
     * @param pagina Página a mostrar
     * @param totalPaginas Total de páginas
     */
    private void mostrarPaginaCarrusel(HBox contenedor, int pagina, int totalPaginas) {
        contenedor.getChildren().clear();
        
        int inicio = pagina * TARJETAS_POR_PAGINA;
        int fin = Math.min(inicio + TARJETAS_POR_PAGINA, tarjetasEstrategias.size());
        
        for (int i = inicio; i < fin; i++) {
            contenedor.getChildren().add(tarjetasEstrategias.get(i));
        }
        
        // Actualizar indicadores
        actualizarIndicadores(contenedor, pagina, totalPaginas);
    }
    
    /**
     * Actualiza los indicadores de página.
     * 
     * @param contenedor Contenedor de tarjetas
     * @param paginaActual Página actual
     * @param totalPaginas Total de páginas
     */
    private void actualizarIndicadores(HBox contenedor, int paginaActual, int totalPaginas) {
        // Buscar el contenedor de indicadores
        VBox carrusel = (VBox) contenedor.getParent().getParent();
        HBox indicadores = (HBox) carrusel.getChildren().get(1);
        
        for (int i = 0; i < indicadores.getChildren().size(); i++) {
            Circle indicador = (Circle) indicadores.getChildren().get(i);
            indicador.setFill(i == paginaActual ? Color.web("#667eea") : Color.web("#cbd5e0"));
        }
    }
    
    /**
     * Crea un mensaje cuando no hay estrategias.
     * 
     * @return Contenedor con mensaje
     */
    private VBox crearMensajeNoEstrategias() {
        VBox mensaje = new VBox(15);
        mensaje.setAlignment(Pos.CENTER);
        mensaje.setStyle("-fx-background-color: #fed7d7; -fx-border-color: #f56565; " +
                        "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 30;");
        
        Label icono = new Label("⚠️");
        icono.setFont(Font.font("System", 48));
        
        Label titulo = new Label("No hay estrategias disponibles");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setStyle("-fx-text-fill: #c53030;");
        
        Label descripcion = new Label("No se pudieron cargar las estrategias de aprendizaje.\n" +
                                    "Verifica que los módulos estén correctamente instalados.");
        descripcion.setFont(Font.font("System", 14));
        descripcion.setStyle("-fx-text-fill: #742a2a; -fx-text-alignment: center;");
        descripcion.setWrapText(true);
        
        mensaje.getChildren().addAll(icono, titulo, descripcion);
        
        return mensaje;
    }
    
    /**
     * Muestra una alerta cuando se requiere selección.
     */
    private void mostrarAlertaSeleccionRequerida() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Selección Requerida");
        alert.setHeaderText("No se ha seleccionado una estrategia");
        alert.setContentText("Por favor, selecciona una estrategia de aprendizaje antes de continuar.");
        
        alert.initOwner(this);
        alert.initModality(Modality.WINDOW_MODAL);
        
        alert.showAndWait();
    }
    
    /**
     * Muestra el modal y espera la selección.
     * 
     * @return Estrategia seleccionada o null si se canceló
     */
    public String mostrarYEsperar() {
        logger.info("Mostrando modal refactorizado para curso: {}", controller.getCurso().getTitulo());
        
        try {
            showAndWait();
            String estrategia = controller.getEstrategiaSeleccionada();
            logger.info("Modal cerrado. Estrategia seleccionada: {}", 
                       estrategia != null ? estrategia : "Ninguna");
            return estrategia;
        } catch (Exception e) {
            logger.error("Error al mostrar el modal: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene la estrategia seleccionada.
     * 
     * @return Estrategia seleccionada o null
     */
    public String getEstrategiaSeleccionada() {
        return controller.getEstrategiaSeleccionada();
    }
    
    /**
     * Verifica si se seleccionó una estrategia.
     * 
     * @return true si se seleccionó una estrategia
     */
    public boolean isEstrategiaSeleccionada() {
        return controller.isEstrategiaSeleccionada();
    }
} 