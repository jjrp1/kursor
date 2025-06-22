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
 * Modal para seleccionar la estrategia de aprendizaje antes de comenzar un curso.
 * 
 * <p>Este modal permite al usuario elegir entre las diferentes estrategias de aprendizaje
 * disponibles antes de iniciar la ejecución de un curso. Las estrategias disponibles son:</p>
 * <ul>
 *   <li><strong>Secuencial:</strong> Preguntas en orden secuencial</li>
 *   <li><strong>Aleatoria:</strong> Preguntas en orden aleatorio</li>
 *   <li><strong>Repetición Espaciada:</strong> Optimizada para retención a largo plazo</li>
 *   <li><strong>Repetir Incorrectas:</strong> Enfocada en preguntas falladas anteriormente</li>
 * </ul>
 * 
 * <p>El modal se muestra como una ventana modal que bloquea la interacción con la
 * ventana padre hasta que el usuario seleccione una estrategia o cancele la operación.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see CursoDTO
 */
public class EstrategiaSelectionModal extends Stage {
    
    /** Logger para registrar eventos del modal */
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaSelectionModal.class);
    
    /** Estrategia seleccionada por el usuario */
    private String estrategiaSeleccionada;
    
    /** Radio buttons para cada estrategia */
    private ToggleGroup toggleGroup;
    private RadioButton rbSecuencial;
    private RadioButton rbAleatoria;
    private RadioButton rbRepeticionEspaciada;
    private RadioButton rbRepetirIncorrectas;
    
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
        setWidth(550);
        setHeight(600);
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
        
        // Contenido principal con radio buttons
        VBox contenido = crearContenido();
        
        // Separador antes de los botones
        Separator separadorBotones = new Separator();
        separadorBotones.setMaxWidth(450);
        
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
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Descripción del curso
        Label lblCurso = new Label("Curso: " + curso.getTitulo());
        lblCurso.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Separador
        Separator separador = new Separator();
        separador.setMaxWidth(400);
        
        header.getChildren().addAll(lblTitulo, lblCurso, separador);
        
        return header;
    }
    
    /**
     * Crea el contenido principal con los radio buttons de estrategias.
     * 
     * @return Contenedor del contenido
     */
    private VBox crearContenido() {
        VBox contenido = new VBox(15);
        contenido.setAlignment(Pos.TOP_LEFT);
        contenido.setMaxWidth(450);
        
        // Crear toggle group
        toggleGroup = new ToggleGroup();
        
        // Crear radio buttons para cada estrategia
        rbSecuencial = crearRadioButtonEstrategia(
            "Secuencial",
            "Preguntas en orden secuencial",
            "Ideal para aprendizaje estructurado y progresivo",
            "Secuencial"
        );
        
        rbAleatoria = crearRadioButtonEstrategia(
            "Aleatoria",
            "Preguntas en orden aleatorio",
            "Ideal para repaso general y evitar memorización de orden",
            "Aleatoria"
        );
        
        rbRepeticionEspaciada = crearRadioButtonEstrategia(
            "Repetición Espaciada",
            "Optimizada para retención a largo plazo",
            "Ideal para memorización efectiva y retención",
            "RepeticionEspaciada"
        );
        
        rbRepetirIncorrectas = crearRadioButtonEstrategia(
            "Repetir Incorrectas",
            "Enfocada en preguntas falladas anteriormente",
            "Ideal para mejorar áreas débiles y corregir errores",
            "Repetir Incorrectas"
        );
        
        // Seleccionar estrategia secuencial por defecto
        rbSecuencial.setSelected(true);
        estrategiaSeleccionada = "Secuencial";
        
        contenido.getChildren().addAll(
            rbSecuencial, rbAleatoria, rbRepeticionEspaciada, rbRepetirIncorrectas
        );
        
        return contenido;
    }
    
    /**
     * Crea un radio button para una estrategia específica.
     * 
     * @param titulo Título de la estrategia
     * @param descripcion Descripción corta
     * @param uso Descripción del uso recomendado
     * @param estrategia Estrategia correspondiente
     * @return Radio button configurado
     */
    private RadioButton crearRadioButtonEstrategia(String titulo, String descripcion, String uso, String estrategia) {
        VBox contenedor = new VBox(5);
        contenedor.setPadding(new Insets(10));
        contenedor.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1; -fx-border-radius: 5;");
        
        RadioButton radioButton = new RadioButton(titulo);
        radioButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        radioButton.setToggleGroup(toggleGroup);
        
        Label lblDescripcion = new Label(descripcion);
        lblDescripcion.setStyle("-fx-font-size: 12px; -fx-text-fill: #495057;");
        
        Label lblUso = new Label(uso);
        lblUso.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        contenedor.getChildren().addAll(radioButton, lblDescripcion, lblUso);
        
        // Configurar el radio button para usar el contenedor como gráfico
        radioButton.setGraphic(contenedor);
        radioButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        
        // Configurar evento de selección
        radioButton.setOnAction(e -> {
            estrategiaSeleccionada = estrategia;
            logger.debug("Estrategia seleccionada: " + estrategia);
        });
        
        return radioButton;
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
        btnCancelar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 25; -fx-cursor: hand;");
        btnCancelar.setPrefWidth(120);
        btnCancelar.setPrefHeight(40);
        
        // Botón Comenzar
        btnComenzar = new Button("✅ Comenzar");
        btnComenzar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 25; -fx-cursor: hand;");
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
            logger.info("Usuario confirmó estrategia: " + estrategiaSeleccionada);
            close();
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