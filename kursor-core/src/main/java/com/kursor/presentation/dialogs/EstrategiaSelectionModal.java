package com.kursor.presentation.dialogs;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.strategy.EstrategiaModule;
import com.kursor.shared.util.StrategyManager;
import com.kursor.yaml.dto.CursoDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
 * Modal para seleccionar la estrategia de aprendizaje antes de comenzar un curso.
 * 
 * <p>Este modal permite al usuario elegir entre las diferentes estrategias de aprendizaje
 * disponibles antes de iniciar la ejecución de un curso. Las estrategias se cargan
 * dinámicamente desde el StrategyManager y se presentan como tarjetas interactivas.</p>
 * 
 * <p>Características de la interfaz:</p>
 * <ul>
 *   <li><strong>Tarjetas Dinámicas:</strong> Cada estrategia se muestra como una tarjeta
 *       con icono, color temático e información detallada</li>
 *   <li><strong>Selección Visual:</strong> Las tarjetas cambian de apariencia al ser
 *       seleccionadas con efectos visuales</li>
 *   <li><strong>Información Completa:</strong> Cada tarjeta muestra nombre, descripción
 *       e información de uso de la estrategia</li>
 *   <li><strong>Carga Dinámica:</strong> Las estrategias se cargan automáticamente
 *       desde los módulos disponibles</li>
 *   <li><strong>Carrusel Interactivo:</strong> Navegación por páginas con indicadores visuales</li>
 *   <li><strong>Animaciones Suaves:</strong> Transiciones de fade para mejorar la experiencia de usuario</li>
 * </ul>
 * 
 * <p>El modal se muestra como una ventana modal que bloquea la interacción con la
 * ventana padre hasta que el usuario seleccione una estrategia o cancele la operación.</p>
 * 
 * <p><strong>Flujo de trabajo:</strong></p>
 * <ol>
 *   <li>El usuario inicia una nueva sesión de curso</li>
 *   <li>Se muestra este modal con las estrategias disponibles</li>
 *   <li>El usuario navega por las páginas del carrusel con animaciones suaves</li>
 *   <li>El usuario selecciona una estrategia haciendo clic en una tarjeta</li>
 *   <li>Se confirma la selección con el botón "Comenzar"</li>
 *   <li>Se retorna la estrategia seleccionada para iniciar el curso</li>
 * </ol>
 * 
 * <p><strong>Características técnicas:</strong></p>
 * <ul>
 *   <li><strong>Animaciones:</strong> Transiciones de fade in/out de 150-200ms para cambios de página</li>
 *   <li><strong>Navegación:</strong> Botones anterior/siguiente + indicadores circulares</li>
 *   <li><strong>Responsive:</strong> Adaptación automática al número de estrategias</li>
 *   <li><strong>Accesibilidad:</strong> Tooltips informativos y navegación por teclado</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.1.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see CursoDTO
 * @see StrategyManager
 * @see EstrategiaModule
 */
public class EstrategiaSelectionModal extends Stage {
    
    /** Logger para registrar eventos del modal */
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaSelectionModal.class);
    
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
    
    /** Estrategia seleccionada por el usuario */
    private String estrategiaSeleccionada;
    
    /** Gestor de estrategias para cargar las estrategias disponibles */
    private final StrategyManager strategyManager;
    
    /** Lista de tarjetas de estrategias para gestión de selección */
    private List<VBox> tarjetasEstrategias;
    
    /** Botones de acción del modal */
    private Button btnComenzar;
    private Button btnCancelar;
    
    /** Curso para el cual se selecciona la estrategia */
    private final CursoDTO curso;
    
    /**
     * Constructor para crear el modal de selección de estrategia.
     * 
     * <p>Inicializa el modal con la configuración necesaria y crea todos los componentes
     * de la interfaz de usuario. El modal se configura como una ventana modal que bloquea
     * la interacción con la ventana padre.</p>
     * 
     * @param owner Ventana padre del modal (no puede ser null)
     * @param curso Curso para el cual seleccionar la estrategia (no puede ser null)
     * @throws IllegalArgumentException si owner o curso son null
     */
    public EstrategiaSelectionModal(Window owner, CursoDTO curso) {
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
        
        this.curso = curso;
        this.strategyManager = StrategyManager.getInstance();
        
        logger.info("Creando modal de selección de estrategia para curso: {}", curso.getTitulo());
        
        try {
            // Configurar el modal
            configurarModal(owner);
            
            // Crear la escena
            Scene scene = crearEscena();
            setScene(scene);
            
            // Configurar eventos
            configurarEventos();
            
            logger.info("Modal de selección de estrategia creado correctamente para curso: {}", curso.getTitulo());
        } catch (Exception e) {
            logger.error("Error al crear el modal de selección de estrategia: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo crear el modal de selección de estrategia", e);
        }
    }
    
    /**
     * Configura las propiedades del modal.
     * 
     * <p>Establece las propiedades básicas de la ventana modal, incluyendo tamaño,
     * modalidad, título y posicionamiento en pantalla.</p>
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
        setTitle("🎯 Seleccionar Estrategia de Aprendizaje");
        
        // Centrar en la pantalla para mejor experiencia de usuario
        centerOnScreen();
        
        logger.debug("Modal configurado con dimensiones {}x{}", ANCHO_VENTANA, ALTO_VENTANA);
    }
    
    /**
     * Crea la escena del modal con todos los controles.
     * 
     * <p>Construye la estructura completa de la interfaz de usuario, incluyendo
     * header, contenido principal con carrusel de estrategias, y botones de acción.</p>
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
        
        // Título y descripción del curso
        VBox header = crearHeader();
        
        // Contenido principal con tarjetas de estrategias en carrusel
        VBox contenido = crearContenido();
        
        // Separador visual antes de los botones
        Separator separadorBotones = new Separator();
        separadorBotones.setMaxWidth(600);
        
        // Botones de acción (Comenzar/Cancelar)
        HBox botones = crearBotones();
        
        // Añadir componentes al contenedor principal en orden
        root.getChildren().addAll(header, contenido, separadorBotones, botones);
        
        logger.debug("Escena del modal creada con {} componentes principales", root.getChildren().size());
        return new Scene(root);
    }
    
    /**
     * Crea el header del modal con título y descripción del curso.
     * 
     * <p>Genera el encabezado visual que incluye el título principal del modal
     * y la información del curso para el cual se selecciona la estrategia.</p>
     * 
     * @return Contenedor del header con título y descripción
     */
    private VBox crearHeader() {
        logger.debug("Creando header del modal");
        
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        // Título principal con emoji y estilo destacado
        Label lblTitulo = new Label("🎯 Seleccionar Estrategia de Aprendizaje");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #2c3e50;");
        
        // Descripción del curso seleccionado
        Label lblCurso = new Label("Curso: " + curso.getTitulo());
        lblCurso.setFont(Font.font("Segoe UI", 14));
        lblCurso.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Separador visual
        Separator separador = new Separator();
        separador.setMaxWidth(500);
        
        header.getChildren().addAll(lblTitulo, lblCurso, separador);
        
        logger.debug("Header creado para curso: {}", curso.getTitulo());
        return header;
    }
    
    /**
     * Crea el contenido principal con las tarjetas de estrategias en un carrusel.
     * 
     * <p>Genera el área principal del modal que contiene el carrusel de estrategias.
     * Si no hay estrategias disponibles, muestra un mensaje de error informativo.</p>
     * 
     * @return Contenedor del contenido principal
     */
    private VBox crearContenido() {
        logger.debug("Creando contenido principal del modal");
        
        VBox contenido = new VBox(15);
        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setMaxWidth(600);
        
        // Obtener estrategias disponibles desde el gestor
        List<EstrategiaModule> estrategias = strategyManager.getStrategies();
        logger.info("Cargadas {} estrategias disponibles", estrategias.size());
        
        if (estrategias.isEmpty()) {
            logger.warn("No se encontraron estrategias disponibles - mostrando mensaje de error");
            return crearMensajeNoEstrategias();
        }
        
        // Crear carrusel de estrategias con navegación
        VBox carrusel = crearCarruselEstrategias(estrategias);
        contenido.getChildren().add(carrusel);
        
        logger.debug("Contenido principal creado con carrusel de {} estrategias", estrategias.size());
        return contenido;
    }
    
    /**
     * Crea un mensaje de error cuando no hay estrategias disponibles.
     * 
     * <p>Muestra una interfaz informativa cuando no se pueden cargar las estrategias,
     * explicando al usuario que la funcionalidad puede estar limitada.</p>
     * 
     * @return Contenedor con mensaje de error
     */
    private VBox crearMensajeNoEstrategias() {
        logger.warn("Creando mensaje de error por falta de estrategias");
        
        VBox contenido = new VBox(15);
        contenido.setAlignment(Pos.CENTER);
        
        // Icono y título de advertencia
        Label lblNoEstrategias = new Label("⚠️ No hay estrategias disponibles");
        lblNoEstrategias.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblNoEstrategias.setStyle("-fx-text-fill: #e74c3c;");
        
        // Descripción detallada del problema
        Label lblDescripcion = new Label("No se pudieron cargar las estrategias de aprendizaje.\n" +
                                       "La aplicación puede tener funcionalidad limitada.");
        lblDescripcion.setFont(Font.font("Segoe UI", 12));
        lblDescripcion.setStyle("-fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        lblDescripcion.setAlignment(Pos.CENTER);
        
        contenido.getChildren().addAll(lblNoEstrategias, lblDescripcion);
        
        logger.warn("Mensaje de error mostrado al usuario");
        return contenido;
    }
    
    /**
     * Crea el carrusel de estrategias con botones de navegación y animaciones.
     * 
     * <p>Genera un carrusel interactivo que permite navegar entre las estrategias
     * disponibles. Incluye botones de navegación, indicadores de página, gestión
     * de la visualización de tarjetas por página y animaciones suaves de transición.</p>
     * 
     * <p><strong>Características del carrusel:</strong></p>
     * <ul>
     *   <li><strong>Navegación por páginas:</strong> Botones anterior/siguiente para navegar</li>
     *   <li><strong>Indicadores visuales:</strong> Puntos circulares que muestran la página actual</li>
     *   <li><strong>Animaciones suaves:</strong> Transiciones de fade in/out al cambiar páginas</li>
     *   <li><strong>Navegación directa:</strong> Clic en indicadores para ir directamente a una página</li>
     * </ul>
     * 
     * @param estrategias Lista de estrategias disponibles para mostrar
     * @return Contenedor del carrusel con navegación completa y animaciones
     */
    private VBox crearCarruselEstrategias(List<EstrategiaModule> estrategias) {
        logger.debug("Creando carrusel para {} estrategias", estrategias.size());
        
        VBox carrusel = new VBox(15);
        carrusel.setAlignment(Pos.CENTER);
        
        // Contenedor principal del carrusel con botones de navegación
        HBox contenedorCarrusel = new HBox(20);
        contenedorCarrusel.setAlignment(Pos.CENTER);
        contenedorCarrusel.setPadding(new Insets(10));
        
        // Botones de navegación con estilo circular
        Button btnAnterior = crearBotonNavegacion("◀", true);
        Button btnSiguiente = crearBotonNavegacion("▶", false);
        
        // Contenedor para las tarjetas visibles en la página actual
        HBox contenedorTarjetas = new HBox(15);
        contenedorTarjetas.setAlignment(Pos.CENTER);
        contenedorTarjetas.setPrefWidth(ANCHO_VENTANA);
        
        // Inicializar la lista de tarjetas
        tarjetasEstrategias = new ArrayList<>();
        
        // Crear todas las tarjetas de estrategias
        for (EstrategiaModule estrategia : estrategias) {
            VBox tarjeta = crearTarjetaEstrategia(estrategia);
            tarjetasEstrategias.add(tarjeta);
            logger.debug("Tarjeta creada para estrategia: {}", estrategia.getNombre());
        }
        
        // Variables para el control del carrusel
        final int[] paginaActual = {0};
        final int totalPaginas = (int) Math.ceil((double) estrategias.size() / TARJETAS_POR_PAGINA);
        
        logger.debug("Carrusel configurado con {} páginas para mostrar {} estrategias", 
                    totalPaginas, estrategias.size());
        
        // Crear indicadores de página (puntos circulares) - debe crearse antes de mostrarPagina
        final HBox indicadores = crearIndicadoresPagina(totalPaginas, paginaActual, null);
        
        // Función lambda para mostrar la página actual con transición suave
        Runnable mostrarPagina = () -> {
            logger.debug("Iniciando transición de página con animación fade");
            
            // Crear transición de fade out para las tarjetas actuales
            // Esto crea una animación suave que desvanece el contenido actual
            FadeTransition fadeOut = new FadeTransition(Duration.millis(DURACION_FADE_OUT), contenedorTarjetas);
            fadeOut.setFromValue(1.0);  // Opacidad completa
            fadeOut.setToValue(0.0);    // Transparente
            
            fadeOut.setOnFinished(e -> {
                logger.debug("Fade out completado, actualizando contenido de la página");
                
                // Limpiar y añadir nuevas tarjetas
                contenedorTarjetas.getChildren().clear();
                
                // Calcular índices de inicio y fin para la página actual
                int inicio = paginaActual[0] * TARJETAS_POR_PAGINA;
                int fin = Math.min(inicio + TARJETAS_POR_PAGINA, tarjetasEstrategias.size());
                
                // Añadir tarjetas de la página actual
                for (int i = inicio; i < fin; i++) {
                    contenedorTarjetas.getChildren().add(tarjetasEstrategias.get(i));
                }
                
                // Crear transición de fade in para las nuevas tarjetas
                // Esto crea una animación suave que revela el nuevo contenido
                FadeTransition fadeIn = new FadeTransition(Duration.millis(DURACION_FADE_IN), contenedorTarjetas);
                fadeIn.setFromValue(0.0);  // Transparente
                fadeIn.setToValue(1.0);    // Opacidad completa
                fadeIn.play();
                
                // Actualizar estado de botones de navegación
                btnAnterior.setDisable(paginaActual[0] == 0);
                btnSiguiente.setDisable(paginaActual[0] >= totalPaginas - 1);
                
                // Actualizar indicadores de página para reflejar la página actual
                actualizarIndicadores(indicadores, paginaActual[0], totalPaginas);
                
                logger.debug("Mostrando página {} de {} (tarjetas {} a {}) con fade in completado", 
                            paginaActual[0] + 1, totalPaginas, inicio + 1, fin);
            });
            
            // Iniciar la animación de fade out
            fadeOut.play();
        };
        
        // Configurar los eventos de los indicadores después de definir mostrarPagina
        configurarEventosIndicadores(indicadores, paginaActual, mostrarPagina);
        
        // Configurar eventos de navegación con botones
        btnAnterior.setOnAction(e -> {
            if (paginaActual[0] > 0) {
                paginaActual[0]--;
                mostrarPagina.run();
                logger.debug("Navegación a página anterior: {}", paginaActual[0] + 1);
            }
        });
        
        btnSiguiente.setOnAction(e -> {
            if (paginaActual[0] < totalPaginas - 1) {
                paginaActual[0]++;
                mostrarPagina.run();
                logger.debug("Navegación a página siguiente: {}", paginaActual[0] + 1);
            }
        });
        
        // Mostrar la primera página inicialmente
        mostrarPagina.run();
        
        // Agregar componentes al carrusel
        contenedorCarrusel.getChildren().addAll(btnAnterior, contenedorTarjetas, btnSiguiente);
        carrusel.getChildren().addAll(contenedorCarrusel, indicadores);
        
        logger.debug("Carrusel creado completamente con navegación, indicadores y animaciones");
        return carrusel;
    }
    
    /**
     * Crea un botón de navegación para el carrusel.
     * 
     * @param texto Texto del botón (◀ o ▶)
     * @param esAnterior true si es el botón anterior, false si es el siguiente
     * @return Botón configurado
     */
    private Button crearBotonNavegacion(String texto, boolean esAnterior) {
        Button boton = new Button(texto);
        boton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        boton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8 12; " +
                      "-fx-cursor: hand; -fx-background-radius: 20;");
        boton.setPrefSize(40, 40);
        
        logger.debug("Botón de navegación creado: {}", texto);
        return boton;
    }
    
    /**
     * Crea los indicadores de página para el carrusel.
     * 
     * <p>Genera puntos circulares que representan cada página del carrusel.
     * Los indicadores permiten navegación directa a cualquier página y
     * se actualizan visualmente para mostrar la página actual.</p>
     * 
     * @param totalPaginas Número total de páginas en el carrusel
     * @param paginaActual Array con la página actual (para modificación en lambda)
     * @param mostrarPagina Función para mostrar la página actual (puede ser null)
     * @return Contenedor con indicadores circulares
     */
    private HBox crearIndicadoresPagina(int totalPaginas, final int[] paginaActual, Runnable mostrarPagina) {
        logger.debug("Creando {} indicadores de página", totalPaginas);
        
        HBox indicadores = new HBox(8);
        indicadores.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < totalPaginas; i++) {
            Circle indicador = new Circle(RADIO_INDICADOR);
            final int pagina = i;
            
            // Estilo inicial: activo para la primera página
            if (i == 0) {
                indicador.setStyle("-fx-fill: #007bff;");
            } else {
                indicador.setStyle("-fx-fill: #dee2e6;");
            }
            
            // Evento de clic para navegar directamente a una página
            indicador.setOnMouseClicked(e -> {
                paginaActual[0] = pagina;
                if (mostrarPagina != null) {
                    mostrarPagina.run();
                }
                logger.debug("Clic en indicador de página: {}", pagina + 1);
            });
            
            indicadores.getChildren().add(indicador);
        }
        
        logger.debug("Indicadores de página creados: {} indicadores", totalPaginas);
        return indicadores;
    }
    
    /**
     * Actualiza los indicadores de página para reflejar la página actual.
     * 
     * <p>Cambia visualmente el estado de los indicadores circulares para mostrar
     * cuál es la página actualmente activa. El indicador de la página actual
     * se muestra en azul, mientras que los demás se muestran en gris.</p>
     * 
     * @param indicadores Contenedor con los indicadores circulares
     * @param paginaActual Página actual (0-based)
     * @param totalPaginas Número total de páginas
     */
    private void actualizarIndicadores(HBox indicadores, int paginaActual, int totalPaginas) {
        logger.debug("Actualizando indicadores para página {} de {}", paginaActual + 1, totalPaginas);
        
        for (int i = 0; i < indicadores.getChildren().size(); i++) {
            Circle indicador = (Circle) indicadores.getChildren().get(i);
            if (i == paginaActual) {
                indicador.setStyle("-fx-fill: #007bff;"); // Azul para página activa
            } else {
                indicador.setStyle("-fx-fill: #dee2e6;"); // Gris para páginas inactivas
            }
        }
        
        logger.debug("Indicadores actualizados correctamente");
    }
    
    /**
     * Crea una tarjeta para una estrategia específica.
     * 
     * <p>Genera una tarjeta interactiva que representa una estrategia de aprendizaje.
     * La tarjeta incluye icono, nombre, descripción y efectos visuales para la selección
     * y hover. Cada tarjeta es completamente funcional y responde a eventos del usuario.</p>
     * 
     * @param estrategia Módulo de estrategia para crear la tarjeta
     * @return Tarjeta configurada con todos los elementos visuales y eventos
     */
    private VBox crearTarjetaEstrategia(EstrategiaModule estrategia) {
        logger.debug("Creando tarjeta para estrategia: {}", estrategia.getNombre());
        
        // Contenedor principal de la tarjeta
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setMinWidth(ANCHO_TARJETA);
        tarjeta.setMaxWidth(ANCHO_TARJETA);
        tarjeta.setPrefHeight(ALTO_TARJETA);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        
        // Obtener color temático de la estrategia
        String colorTema = estrategia.getColorTema();
        
        // Definir estilos para diferentes estados de la tarjeta
        String estiloBase = crearEstiloBaseTarjeta(colorTema);
        String estiloSeleccionado = crearEstiloSeleccionadoTarjeta(colorTema);
        
        // Aplicar estilo base inicialmente
        tarjeta.setStyle(estiloBase);
        
        // Crear componentes de la tarjeta
        Label lblIcono = crearIconoEstrategia(estrategia, colorTema);
        Label lblNombre = crearNombreEstrategia(estrategia);
        VBox contenedorDescripcion = crearDescripcionEstrategia(estrategia);
        
        // Crear tooltip con información detallada
        Tooltip tooltip = crearTooltipEstrategia(estrategia);
        Tooltip.install(tarjeta, tooltip);
        
        // Agregar componentes a la tarjeta
        tarjeta.getChildren().addAll(lblIcono, lblNombre, contenedorDescripcion);
        
        // Configurar eventos de interacción
        configurarEventosTarjeta(tarjeta, estrategia, estiloBase, estiloSeleccionado);
        
        logger.debug("Tarjeta creada exitosamente para estrategia: {}", estrategia.getNombre());
        return tarjeta;
    }
    
    /**
     * Crea el estilo base para una tarjeta de estrategia.
     * 
     * @param colorTema Color temático de la estrategia
     * @return String con el estilo CSS
     */
    private String crearEstiloBaseTarjeta(String colorTema) {
        return String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);",
            colorTema
        );
    }
    
    /**
     * Crea el estilo para una tarjeta seleccionada.
     * 
     * @param colorTema Color temático de la estrategia
     * @return String con el estilo CSS
     */
    private String crearEstiloSeleccionadoTarjeta(String colorTema) {
        return String.format(
            "-fx-background-color: linear-gradient(to bottom right, %s, %s); " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 4); " +
            "-fx-scale-x: 1.02; " +
            "-fx-scale-y: 1.02;",
            colorTema, ajustarColor(colorTema, 0.8), colorTema
        );
    }
    
    /**
     * Crea el icono de la estrategia.
     * 
     * @param estrategia Estrategia para obtener el icono
     * @param colorTema Color temático
     * @return Label con el icono
     */
    private Label crearIconoEstrategia(EstrategiaModule estrategia, String colorTema) {
        Label lblIcono = new Label(estrategia.getIcon());
        lblIcono.setFont(Font.font("Segoe UI", 32));
        lblIcono.setStyle("-fx-text-fill: " + colorTema + ";");
        return lblIcono;
    }
    
    /**
     * Crea el nombre de la estrategia.
     * 
     * @param estrategia Estrategia para obtener el nombre
     * @return Label con el nombre
     */
    private Label crearNombreEstrategia(EstrategiaModule estrategia) {
        Label lblNombre = new Label(estrategia.getNombre());
        lblNombre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblNombre.setStyle("-fx-text-fill: #2c3e50;");
        lblNombre.setAlignment(Pos.CENTER);
        return lblNombre;
    }
    
    /**
     * Crea la descripción de la estrategia.
     * 
     * @param estrategia Estrategia para obtener la descripción
     * @return Contenedor con la descripción
     */
    private VBox crearDescripcionEstrategia(EstrategiaModule estrategia) {
        Label lblDescripcion = new Label(estrategia.getDescripcion());
        lblDescripcion.setFont(Font.font("Segoe UI", 12));
        lblDescripcion.setStyle("-fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        lblDescripcion.setWrapText(true);
        lblDescripcion.setPrefWidth(ANCHO_TARJETA);
        lblDescripcion.setAlignment(Pos.CENTER);
        
        // Usar StackPane para centrar el Label
        StackPane contenedor = new StackPane(lblDescripcion);
        contenedor.setPrefWidth(ANCHO_TARJETA);
        
        return new VBox(contenedor);
    }
    
    /**
     * Crea el tooltip con información detallada de la estrategia.
     * 
     * @param estrategia Estrategia para obtener la información
     * @return Tooltip configurado
     */
    private Tooltip crearTooltipEstrategia(EstrategiaModule estrategia) {
        Tooltip tooltip = new Tooltip(estrategia.getInformacionUso());
        tooltip.setFont(Font.font("Segoe UI", 11));
        tooltip.setStyle("-fx-text-alignment: justify;");
        tooltip.setMaxWidth(300);
        tooltip.setWrapText(true);
        return tooltip;
    }
    
    /**
     * Configura los eventos de interacción para una tarjeta.
     * 
     * @param tarjeta Tarjeta a configurar
     * @param estrategia Estrategia asociada
     * @param estiloBase Estilo base de la tarjeta
     * @param estiloSeleccionado Estilo de tarjeta seleccionada
     */
    private void configurarEventosTarjeta(VBox tarjeta, EstrategiaModule estrategia, 
                                        String estiloBase, String estiloSeleccionado) {
        
        // Evento de clic para seleccionar la estrategia
        tarjeta.setOnMouseClicked(e -> {
            logger.debug("Usuario hizo clic en tarjeta de estrategia: {}", estrategia.getNombre());
            
            // Deseleccionar todas las tarjetas primero
            deseleccionarTodasLasTarjetas();
            
            // Seleccionar esta tarjeta específica
            seleccionarTarjeta(tarjeta, estrategia, estiloSeleccionado);
            
            // Actualizar la estrategia seleccionada
            estrategiaSeleccionada = estrategia.getNombre();
            
            // Habilitar el botón Comenzar
            btnComenzar.setDisable(false);
            
            logger.info("Estrategia seleccionada: {}", estrategiaSeleccionada);
        });
        
        // Efectos hover para mejorar la experiencia de usuario
        tarjeta.setOnMouseEntered(e -> {
            if (!estrategia.getNombre().equals(estrategiaSeleccionada)) {
                tarjeta.setStyle(estiloBase + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
                logger.debug("Efecto hover aplicado a tarjeta: {}", estrategia.getNombre());
            }
        });
        
        tarjeta.setOnMouseExited(e -> {
            if (!estrategia.getNombre().equals(estrategiaSeleccionada)) {
                tarjeta.setStyle(estiloBase);
                logger.debug("Efecto hover removido de tarjeta: {}", estrategia.getNombre());
            }
        });
    }
    
    /**
     * Deselecciona todas las tarjetas de estrategias.
     * 
     * <p>Restaura el estilo base de todas las tarjetas y sus componentes,
     * eliminando cualquier efecto de selección previo.</p>
     */
    private void deseleccionarTodasLasTarjetas() {
        logger.debug("Deseleccionando todas las tarjetas de estrategias");
        
        for (VBox tarjetaEst : tarjetasEstrategias) {
            EstrategiaModule est = encontrarEstrategiaPorTarjeta(tarjetaEst);
            
            if (est != null) {
                // Restaurar estilo base de la tarjeta
                String estiloBaseTarjeta = crearEstiloBaseTarjeta(est.getColorTema());
                tarjetaEst.setStyle(estiloBaseTarjeta);
                
                // Restaurar colores de texto para tarjetas no seleccionadas
                restaurarColoresTexto(tarjetaEst, est);
            }
        }
    }
    
    /**
     * Restaura los colores de texto de una tarjeta al estado no seleccionado.
     * 
     * @param tarjeta Tarjeta a restaurar
     * @param estrategia Estrategia asociada
     */
    private void restaurarColoresTexto(VBox tarjeta, EstrategiaModule estrategia) {
        try {
            // Obtener componentes de la tarjeta
            Label iconoEst = (Label) tarjeta.getChildren().get(0);
            Label nombreEst = (Label) tarjeta.getChildren().get(1);
            VBox contenedorEst = (VBox) tarjeta.getChildren().get(2);
            StackPane stackPane = (StackPane) contenedorEst.getChildren().get(0);
            Label descripcionEst = (Label) stackPane.getChildren().get(0);
            
            // Restaurar colores originales
            iconoEst.setStyle("-fx-text-fill: " + estrategia.getColorTema() + ";");
            nombreEst.setStyle("-fx-text-fill: #2c3e50;");
            descripcionEst.setStyle("-fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
            
        } catch (Exception e) {
            logger.warn("Error al restaurar colores de texto de tarjeta: {}", e.getMessage());
        }
    }
    
    /**
     * Selecciona una tarjeta específica aplicando el estilo de selección.
     * 
     * @param tarjeta Tarjeta a seleccionar
     * @param estrategia Estrategia asociada
     * @param estiloSeleccionado Estilo de selección a aplicar
     */
    private void seleccionarTarjeta(VBox tarjeta, EstrategiaModule estrategia, String estiloSeleccionado) {
        logger.debug("Seleccionando tarjeta de estrategia: {}", estrategia.getNombre());
        
        // Aplicar estilo de selección
        tarjeta.setStyle(estiloSeleccionado);
        
        // Ajustar colores de texto para la tarjeta seleccionada
        try {
            Label lblIcono = (Label) tarjeta.getChildren().get(0);
            Label lblNombre = (Label) tarjeta.getChildren().get(1);
            VBox contenedor = (VBox) tarjeta.getChildren().get(2);
            StackPane stackPane = (StackPane) contenedor.getChildren().get(0);
            Label lblDescripcion = (Label) stackPane.getChildren().get(0);
            
            // Aplicar colores de texto para selección
            lblIcono.setStyle("-fx-text-fill: white;");
            lblNombre.setStyle("-fx-text-fill: white;");
            lblDescripcion.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-text-alignment: center;");
            
        } catch (Exception e) {
            logger.warn("Error al ajustar colores de texto de tarjeta seleccionada: {}", e.getMessage());
        }
    }
    
    /**
     * Encuentra una estrategia por una tarjeta específica.
     * 
     * <p>Busca en la lista de estrategias disponibles para encontrar la que
     * corresponde a una tarjeta específica, comparando por nombre.</p>
     * 
     * @param tarjeta Tarjeta de estrategia a buscar
     * @return Estrategia encontrada o null si no se encuentra
     */
    private EstrategiaModule encontrarEstrategiaPorTarjeta(VBox tarjeta) {
        try {
            // Obtener el nombre de la estrategia desde la tarjeta
            Label lblNombre = (Label) tarjeta.getChildren().get(1);
            String nombreEstrategia = lblNombre.getText();
            
            // Buscar la estrategia correspondiente
            for (EstrategiaModule estrategia : strategyManager.getStrategies()) {
                if (estrategia.getNombre().equals(nombreEstrategia)) {
                    return estrategia;
                }
            }
            
            logger.warn("No se encontró estrategia para tarjeta con nombre: {}", nombreEstrategia);
            return null;
            
        } catch (Exception e) {
            logger.error("Error al buscar estrategia por tarjeta: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Ajusta el color para crear variaciones.
     * 
     * <p>Método utilitario para crear variaciones de color. En la implementación
     * actual es simplificada, pero en una versión completa se parsearía el color
     * y se ajustaría según el factor proporcionado.</p>
     * 
     * @param color Color base en formato CSS
     * @param factor Factor de ajuste (0.0 a 1.0)
     * @return Color ajustado (actualmente retorna el color original)
     */
    private String ajustarColor(String color, double factor) {
        // TODO: Implementar parsing y ajuste real del color
        // Por ahora retorna el color original
        logger.debug("Ajustando color {} con factor {}", color, factor);
        return color;
    }
    
    /**
     * Crea los botones de acción del modal.
     * 
     * <p>Genera los botones "Comenzar" y "Cancelar" con estilos apropiados
     * y configuración de tamaño consistente.</p>
     * 
     * @return Contenedor de botones configurados
     */
    private HBox crearBotones() {
        logger.debug("Creando botones de acción del modal");
        
        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(30, 0, 20, 0));
        
        // Botón Cancelar con estilo rojo
        btnCancelar = new Button("❌ Cancelar");
        btnCancelar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnCancelar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 12 25; " +
                           "-fx-cursor: hand; -fx-background-radius: 8;");
        btnCancelar.setPrefWidth(200);
        btnCancelar.setPrefHeight(40);
        
        // Botón Comenzar con estilo verde (inicialmente deshabilitado)
        btnComenzar = new Button("✅ Comenzar");
        btnComenzar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnComenzar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 12 25; " +
                           "-fx-cursor: hand; -fx-background-radius: 8;");
        btnComenzar.setPrefWidth(200);
        btnComenzar.setPrefHeight(40);
        btnComenzar.setDisable(true); // Deshabilitado hasta que se seleccione una estrategia
        
        botones.getChildren().addAll(btnCancelar, btnComenzar);
        
        logger.debug("Botones de acción creados: Cancelar y Comenzar");
        return botones;
    }
    
    /**
     * Configura los eventos de los botones.
     * 
     * <p>Establece los manejadores de eventos para los botones de acción,
     * incluyendo validaciones y logging apropiado para cada acción.</p>
     */
    private void configurarEventos() {
        logger.debug("Configurando eventos de los botones");
        
        // Evento del botón Cancelar
        btnCancelar.setOnAction(e -> {
            logger.info("Usuario canceló la selección de estrategia para curso: {}", curso.getTitulo());
            estrategiaSeleccionada = null;
            close();
        });
        
        // Evento del botón Comenzar
        btnComenzar.setOnAction(e -> {
            if (estrategiaSeleccionada != null) {
                logger.info("Usuario confirmó estrategia '{}' para curso: {}", 
                           estrategiaSeleccionada, curso.getTitulo());
                close();
            } else {
                logger.warn("Usuario intentó comenzar sin seleccionar estrategia");
                mostrarAlertaSeleccionRequerida();
            }
        });
        
        // Evento de cierre de ventana (X en la esquina)
        setOnCloseRequest(e -> {
            logger.info("Modal cerrado por el usuario sin seleccionar estrategia para curso: {}", 
                       curso.getTitulo());
            estrategiaSeleccionada = null;
        });
        
        logger.debug("Eventos de botones configurados correctamente");
    }
    
    /**
     * Muestra una alerta cuando el usuario intenta comenzar sin seleccionar estrategia.
     * 
     * <p>Presenta un diálogo de advertencia informando al usuario que debe
     * seleccionar una estrategia antes de continuar.</p>
     */
    private void mostrarAlertaSeleccionRequerida() {
        logger.debug("Mostrando alerta de selección requerida");
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Selección Requerida");
        alert.setHeaderText("No se ha seleccionado una estrategia");
        alert.setContentText("Por favor, selecciona una estrategia de aprendizaje antes de continuar.");
        
        // Configurar el alert para que sea modal respecto al modal principal
        alert.initOwner(this);
        alert.initModality(Modality.WINDOW_MODAL);
        
        alert.showAndWait();
        logger.debug("Alerta de selección requerida mostrada al usuario");
    }
    
    /**
     * Muestra el modal y espera la selección del usuario.
     * 
     * <p>Este método bloquea la ejecución hasta que el usuario seleccione una estrategia
     * o cancele la operación. Es el punto de entrada principal para usar este modal.</p>
     * 
     * @return La estrategia seleccionada, o null si se canceló la operación
     */
    public String mostrarYEsperar() {
        logger.info("Mostrando modal de selección de estrategia para curso: {}", curso.getTitulo());
        
        try {
            showAndWait(); // Bloquea hasta que se cierre el modal
            logger.info("Modal cerrado. Estrategia seleccionada: {}", 
                       estrategiaSeleccionada != null ? estrategiaSeleccionada : "Ninguna");
            return estrategiaSeleccionada;
        } catch (Exception e) {
            logger.error("Error al mostrar el modal de selección de estrategia: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene la estrategia seleccionada.
     * 
     * <p>Retorna el nombre de la estrategia que fue seleccionada por el usuario,
     * o null si no se ha seleccionado ninguna.</p>
     * 
     * @return Estrategia seleccionada o null si no se ha seleccionado ninguna
     */
    public String getEstrategiaSeleccionada() {
        return estrategiaSeleccionada;
    }
    
    /**
     * Verifica si se seleccionó una estrategia.
     * 
     * <p>Método de conveniencia para verificar si el usuario ha realizado
     * una selección válida.</p>
     * 
     * @return true si se seleccionó una estrategia, false en caso contrario
     */
    public boolean isEstrategiaSeleccionada() {
        return estrategiaSeleccionada != null;
    }
    
    /**
     * Configura los eventos de los indicadores de página.
     * 
     * <p>Establece los manejadores de eventos para cada indicador circular,
     * permitiendo la navegación directa a cualquier página del carrusel
     * cuando el usuario hace clic en un indicador específico.</p>
     * 
     * @param indicadores Contenedor con los indicadores circulares
     * @param paginaActual Array con la página actual (para modificación en lambda)
     * @param mostrarPagina Función para mostrar la página seleccionada
     */
    private void configurarEventosIndicadores(HBox indicadores, final int[] paginaActual, Runnable mostrarPagina) {
        logger.debug("Configurando eventos para {} indicadores de página", indicadores.getChildren().size());
        
        // Iterar sobre todos los indicadores y configurar eventos de clic
        for (int i = 0; i < indicadores.getChildren().size(); i++) {
            Circle indicador = (Circle) indicadores.getChildren().get(i);
            final int pagina = i; // Variable final para usar en lambda
            
            // Configurar evento de clic para navegación directa
            indicador.setOnMouseClicked(e -> {
                logger.debug("Usuario hizo clic en indicador de página: {}", pagina + 1);
                
                // Actualizar la página actual
                paginaActual[0] = pagina;
                
                // Ejecutar la función de mostrar página con animación
                mostrarPagina.run();
                
                logger.debug("Navegación directa a página: {}", pagina + 1);
            });
        }
        
        logger.debug("Eventos de indicadores configurados correctamente");
    }
}

/*
 * ===== HISTORIAL DE CAMBIOS =====
 * 
 * Versión 2.1.0 (Actual)
 * - Añadidas animaciones suaves de transición (fade in/out) para cambios de página
 * - Mejorada la experiencia de usuario con transiciones de 150-200ms
 * - Refactorizado el sistema de indicadores de página para mejor separación de responsabilidades
 * - Añadido logging detallado para debugging de animaciones
 * - Mejorada la documentación JavaDoc con características técnicas
 * - Añadidas constantes de configuración para duración de animaciones
 * 
 * Versión 2.0.0
 * - Implementación completa del carrusel de estrategias
 * - Sistema de navegación por páginas con indicadores visuales
 * - Tarjetas interactivas con efectos hover y selección
 * - Logging completo y manejo de errores robusto
 * 
 * Versión 1.0.0
 * - Implementación inicial del modal de selección de estrategias
 * - Funcionalidad básica de selección y confirmación
 */ 
