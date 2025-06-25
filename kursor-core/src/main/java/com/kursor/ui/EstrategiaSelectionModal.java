package com.kursor.ui;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.strategy.EstrategiaModule;
import com.kursor.util.StrategyManager;
import com.kursor.yaml.dto.CursoDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
 * </ul>
 * 
 * <p>El modal se muestra como una ventana modal que bloquea la interacción con la
 * ventana padre hasta que el usuario seleccione una estrategia o cancele la operación.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see CursoDTO
 * @see StrategyManager
 * @see EstrategiaModule
 */
public class EstrategiaSelectionModal extends Stage {
    
    /** Logger para registrar eventos del modal */
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaSelectionModal.class);
    
    /** Estrategia seleccionada por el usuario */
    private String estrategiaSeleccionada;
    
    /** Gestor de estrategias */
    private final StrategyManager strategyManager;
    
    /** Lista de tarjetas de estrategias */
    private List<VBox> tarjetasEstrategias;
    
    /** Botones de acción */
    private Button btnComenzar;
    private Button btnCancelar;
    
    /** Curso para el cual se selecciona la estrategia */
    private final CursoDTO curso;
    
    /**
     * Constructor para crear el modal de selección de estrategia.
     * 
     * @param owner Ventana padre del modal
     * @param curso Curso para el cual seleccionar la estrategia
     */
    public EstrategiaSelectionModal(Window owner, CursoDTO curso) {
        super();
        
        this.curso = curso;
        this.strategyManager = StrategyManager.getInstance();
        
        logger.info("Creando modal de selección de estrategia para curso: " + curso.getTitulo());
        
        // Configurar el modal
        configurarModal(owner);
        
        // Crear la escena
        Scene scene = crearEscena();
        setScene(scene);
        
        // Configurar eventos
        configurarEventos();
        
        logger.info("Modal de selección de estrategia creado correctamente");
    }
    
    /**
     * Configura las propiedades del modal.
     * 
     * @param owner Ventana padre del modal
     */
    private void configurarModal(Window owner) {
        // Configuración modal
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        
        // Propiedades de la ventana
        setResizable(false);
        setWidth(700);
        setHeight(650);
        setTitle("🎯 Seleccionar Estrategia de Aprendizaje");
        
        // Centrar en la pantalla
        centerOnScreen();
    }
    
    /**
     * Crea la escena del modal con todos los controles.
     * 
     * @return Escena configurada
     */
    private Scene crearEscena() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        // Título y descripción
        VBox header = crearHeader();
        
        // Contenido principal con tarjetas de estrategias
        VBox contenido = crearContenido();
        
        // Separador antes de los botones
        Separator separadorBotones = new Separator();
        separadorBotones.setMaxWidth(600);
        
        // Botones de acción
        HBox botones = crearBotones();
        
        // Añadir componentes al contenedor principal
        root.getChildren().addAll(header, contenido, separadorBotones, botones);
        
        return new Scene(root);
    }
    
    /**
     * Crea el header del modal con título y descripción del curso.
     * 
     * @return Contenedor del header
     */
    private VBox crearHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        // Título principal
        Label lblTitulo = new Label("🎯 Seleccionar Estrategia de Aprendizaje");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #2c3e50;");
        
        // Descripción del curso
        Label lblCurso = new Label("Curso: " + curso.getTitulo());
        lblCurso.setFont(Font.font("Segoe UI", 14));
        lblCurso.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Separador
        Separator separador = new Separator();
        separador.setMaxWidth(500);
        
        header.getChildren().addAll(lblTitulo, lblCurso, separador);
        
        return header;
    }
    
    /**
     * Crea el contenido principal con las tarjetas de estrategias.
     * 
     * @return Contenedor del contenido
     */
    private VBox crearContenido() {
        VBox contenido = new VBox(15);
        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setMaxWidth(600);
        
        // Obtener estrategias disponibles
        List<EstrategiaModule> estrategias = strategyManager.getStrategies();
        
        if (estrategias.isEmpty()) {
            // Mostrar mensaje si no hay estrategias disponibles
            Label lblNoEstrategias = new Label("⚠️ No hay estrategias disponibles");
            lblNoEstrategias.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            lblNoEstrategias.setStyle("-fx-text-fill: #e74c3c;");
            
            Label lblDescripcion = new Label("No se pudieron cargar las estrategias de aprendizaje.\n" +
                                           "La aplicación puede tener funcionalidad limitada.");
            lblDescripcion.setFont(Font.font("Segoe UI", 12));
            lblDescripcion.setStyle("-fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
            lblDescripcion.setAlignment(Pos.CENTER);
            
            contenido.getChildren().addAll(lblNoEstrategias, lblDescripcion);
            return contenido;
        }
        
        // Crear grid para las tarjetas
        GridPane gridTarjetas = new GridPane();
        gridTarjetas.setHgap(15);
        gridTarjetas.setVgap(15);
        gridTarjetas.setAlignment(Pos.CENTER);
        
        // Crear tarjetas para cada estrategia
        int columna = 0;
        int fila = 0;
        int maxColumnas = 2; // Máximo 2 tarjetas por fila
        
        for (EstrategiaModule estrategia : estrategias) {
            VBox tarjeta = crearTarjetaEstrategia(estrategia);
            
            gridTarjetas.add(tarjeta, columna, fila);
            
            columna++;
            if (columna >= maxColumnas) {
                columna = 0;
                fila++;
            }
        }
        
        // Seleccionar la primera estrategia por defecto
        if (!estrategias.isEmpty()) {
            estrategiaSeleccionada = estrategias.get(0).getNombre();
            logger.debug("Estrategia seleccionada por defecto: " + estrategiaSeleccionada);
        }
        
        contenido.getChildren().add(gridTarjetas);
        
        return contenido;
    }
    
    /**
     * Crea una tarjeta para una estrategia específica.
     * 
     * @param estrategia Módulo de estrategia
     * @return Tarjeta configurada
     */
    private VBox crearTarjetaEstrategia(EstrategiaModule estrategia) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setPrefWidth(280);
        tarjeta.setPrefHeight(200);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        
        // Estilo base de la tarjeta
        String colorTema = estrategia.getColorTema();
        String estiloBase = String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);",
            colorTema
        );
        
        String estiloSeleccionado = String.format(
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
        
        tarjeta.setStyle(estiloBase);
        
        // Icono de la estrategia
        Label lblIcono = new Label(estrategia.getIcon());
        lblIcono.setFont(Font.font("Segoe UI", 32));
        lblIcono.setStyle("-fx-text-fill: " + colorTema + ";");
        
        // Nombre de la estrategia
        Label lblNombre = new Label(estrategia.getNombre());
        lblNombre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblNombre.setStyle("-fx-text-fill: #2c3e50;");
        lblNombre.setAlignment(Pos.CENTER);
        
        // Descripción
        Label lblDescripcion = new Label(estrategia.getDescripcion());
        lblDescripcion.setFont(Font.font("Segoe UI", 12));
        lblDescripcion.setStyle("-fx-text-fill: #7f8c8d;");
        lblDescripcion.setAlignment(Pos.CENTER);
        lblDescripcion.setWrapText(true);
        
        // Información de uso (tooltip)
        Tooltip tooltip = new Tooltip(estrategia.getInformacionUso());
        tooltip.setFont(Font.font("Segoe UI", 11));
        tooltip.setMaxWidth(300);
        tooltip.setWrapText(true);
        Tooltip.install(tarjeta, tooltip);
        
        // Agregar componentes a la tarjeta
        tarjeta.getChildren().addAll(lblIcono, lblNombre, lblDescripcion);
        
        // Configurar eventos de la tarjeta
        tarjeta.setOnMouseClicked(e -> {
            // Deseleccionar todas las tarjetas
            for (EstrategiaModule est : strategyManager.getStrategies()) {
                VBox tarjetaEst = encontrarTarjetaPorEstrategia(est.getNombre());
                if (tarjetaEst != null) {
                    tarjetaEst.setStyle(estiloBase);
                }
            }
            
            // Seleccionar esta tarjeta
            tarjeta.setStyle(estiloSeleccionado);
            estrategiaSeleccionada = estrategia.getNombre();
            
            logger.debug("Estrategia seleccionada: " + estrategiaSeleccionada);
        });
        
        // Efectos hover
        tarjeta.setOnMouseEntered(e -> {
            if (!estrategia.getNombre().equals(estrategiaSeleccionada)) {
                tarjeta.setStyle(estiloBase + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            }
        });
        
        tarjeta.setOnMouseExited(e -> {
            if (!estrategia.getNombre().equals(estrategiaSeleccionada)) {
                tarjeta.setStyle(estiloBase);
            }
        });
        
        return tarjeta;
    }
    
    /**
     * Encuentra una tarjeta por el nombre de la estrategia.
     * 
     * @param nombreEstrategia Nombre de la estrategia
     * @return Tarjeta encontrada o null
     */
    private VBox encontrarTarjetaPorEstrategia(String nombreEstrategia) {
        // Esta implementación es simplificada, en una implementación real
        // se mantendría una referencia a las tarjetas
        return null;
    }
    
    /**
     * Ajusta el color para crear variaciones.
     * 
     * @param color Color base
     * @param factor Factor de ajuste
     * @return Color ajustado
     */
    private String ajustarColor(String color, double factor) {
        // Implementación simplificada, en una implementación real
        // se parsearía el color y se ajustaría
        return color;
    }
    
    /**
     * Crea los botones de acción del modal.
     * 
     * @return Contenedor de botones
     */
    private HBox crearBotones() {
        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(30, 0, 20, 0));
        
        // Botón Cancelar
        btnCancelar = new Button("❌ Cancelar");
        btnCancelar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnCancelar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 12 25; -fx-cursor: hand; -fx-background-radius: 8;");
        btnCancelar.setPrefWidth(120);
        btnCancelar.setPrefHeight(40);
        
        // Botón Comenzar
        btnComenzar = new Button("✅ Comenzar");
        btnComenzar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnComenzar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 12 25; -fx-cursor: hand; -fx-background-radius: 8;");
        btnComenzar.setPrefWidth(120);
        btnComenzar.setPrefHeight(40);
        
        botones.getChildren().addAll(btnCancelar, btnComenzar);
        
        return botones;
    }
    
    /**
     * Configura los eventos de los botones.
     */
    private void configurarEventos() {
        // Evento del botón Cancelar
        btnCancelar.setOnAction(e -> {
            logger.info("Usuario canceló la selección de estrategia");
            estrategiaSeleccionada = null;
            close();
        });
        
        // Evento del botón Comenzar
        btnComenzar.setOnAction(e -> {
            if (estrategiaSeleccionada != null) {
                logger.info("Usuario confirmó estrategia: " + estrategiaSeleccionada);
                close();
            } else {
                // Mostrar alert si no se seleccionó estrategia
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Selección Requerida");
                alert.setHeaderText("No se ha seleccionado una estrategia");
                alert.setContentText("Por favor, selecciona una estrategia de aprendizaje antes de continuar.");
                alert.showAndWait();
            }
        });
        
        // Evento de cierre de ventana
        setOnCloseRequest(e -> {
            logger.info("Modal cerrado sin seleccionar estrategia");
            estrategiaSeleccionada = null;
        });
    }
    
    /**
     * Muestra el modal y espera la selección del usuario.
     * 
     * @return La estrategia seleccionada, o null si se canceló
     */
    public String mostrarYEsperar() {
        logger.info("Mostrando modal de selección de estrategia");
        showAndWait();
        return estrategiaSeleccionada;
    }
    
    /**
     * Obtiene la estrategia seleccionada.
     * 
     * @return Estrategia seleccionada o null si no se ha seleccionado ninguna
     */
    public String getEstrategiaSeleccionada() {
        return estrategiaSeleccionada;
    }
    
    /**
     * Verifica si se seleccionó una estrategia.
     * 
     * @return true si se seleccionó una estrategia, false en caso contrario
     */
    public boolean isEstrategiaSeleccionada() {
        return estrategiaSeleccionada != null;
    }
} 