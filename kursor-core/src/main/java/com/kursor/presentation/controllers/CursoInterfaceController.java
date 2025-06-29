package com.kursor.ui;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.yaml.dto.CursoDTO;
import com.kursor.util.StrategyManager;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controlador principal para la ejecución de cursos con estrategia de aprendizaje.
 * 
 * <p>Este controlador coordina todo el flujo de ejecución de un curso:</p>
 * <ol>
 *   <li>Muestra el modal de selección de estrategia</li>
 *   <li>Inicializa la vista de curso con la estrategia seleccionada</li>
 *   <li>Gestiona la navegación entre preguntas</li>
 *   <li>Coordina con el CursoSessionManager para persistencia</li>
 *   <li>Integra con los módulos de preguntas</li>
 * </ol>
 * 
 * <p>El controlador actúa como intermediario entre la interfaz de usuario
 * y la lógica de negocio, proporcionando una API limpia para iniciar
 * y gestionar la ejecución de cursos.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaSelectionModal
 * @see CursoInterfaceView
 * @see CursoSessionManager
 * @see StrategyManager
 */
public class CursoInterfaceController {
    
    /** Logger para registrar eventos del controlador */
    private static final Logger logger = LoggerFactory.getLogger(CursoInterfaceController.class);
    
    /** Ventana padre para los modales */
    private final Window owner;
    
    /** Gestor de estrategias */
    private final StrategyManager strategyManager;
    
    /** Modal de selección de estrategia */
    private EstrategiaSelectionModal estrategiaModal;
    
    /** Modal de ejecución de curso */
    private CursoInterfaceView cursoView;
    
    /** Gestor de sesión del curso */
    private CursoSessionManager sessionManager;
    
    /** Curso que se está ejecutando */
    private CursoDTO cursoActual;
    
    /** Estrategia seleccionada */
    private String estrategiaSeleccionada;
    
    /**
     * Constructor para crear el controlador de curso.
     * 
     * @param owner Ventana padre para los modales
     */
    public CursoInterfaceController(Window owner) {
        this.owner = owner;
        this.strategyManager = StrategyManager.getInstance();
        
        logger.info("CursoInterfaceController creado");
    }
    
    /**
     * Inicia la ejecución de un curso.
     * 
     * <p>Este método maneja el flujo completo de inicio de un curso:</p>
     * <ol>
     *   <li>Selección de estrategia de aprendizaje (modal informativo)</li>
     *   <li>Inicialización del gestor de sesión</li>
     *   <li>Mostrar vista de curso</li>
     * </ol>
     * 
     * @param curso Curso a ejecutar
     * @return true si se inició correctamente, false si se canceló o hubo error
     */
    public boolean iniciarCurso(CursoDTO curso) {
        logger.info("Iniciando curso: " + curso.getTitulo());
        
        if (curso == null) {
            logger.error("No se puede iniciar un curso null");
            return false;
        }
        
        this.cursoActual = curso;
        
        // Paso 1: Mostrar modal de selección de estrategia (solo informativo)
        String estrategiaSeleccionadaPorUsuario = mostrarSeleccionEstrategia();
        if (estrategiaSeleccionadaPorUsuario == null) {
            logger.info("Usuario canceló la selección de estrategia");
            return false;
        }
        
        // Ignorar la selección del usuario y usar estrategia secuencial por defecto
        this.estrategiaSeleccionada = "Secuencial";
        logger.info("Usuario seleccionó: " + estrategiaSeleccionadaPorUsuario + ", pero usando estrategia secuencial por defecto");
        
        // Paso 2: Inicializar gestor de sesión
        inicializarSessionManager();
        
        // Paso 3: Mostrar vista de curso
        return mostrarVistaCurso();
    }
    
    /**
     * Muestra el modal de selección de estrategia y espera la selección del usuario.
     * 
     * @return Nombre de la estrategia seleccionada, o null si se canceló
     */
    private String mostrarSeleccionEstrategia() {
        logger.debug("Mostrando modal de selección de estrategia");
        
        estrategiaModal = new EstrategiaSelectionModal(owner, cursoActual);
        return estrategiaModal.mostrarYEsperar();
    }
    
    /**
     * Inicializa el gestor de sesión para el curso actual.
     */
    private void inicializarSessionManager() {
        logger.debug("Inicializando gestor de sesión para curso: " + cursoActual.getId() + " con estrategia: " + estrategiaSeleccionada);
        
        sessionManager = new CursoSessionManager(cursoActual.getId(), estrategiaSeleccionada);
        sessionManager.inicializar();
        
        logger.info("Gestor de sesión inicializado");
    }
    
    /**
     * Muestra la vista de ejecución del curso.
     * 
     * @return true si se mostró correctamente, false en caso contrario
     */
    private boolean mostrarVistaCurso() {
        logger.debug("Mostrando vista de curso");
        
        try {
            // Crear la estrategia de aprendizaje
            EstrategiaAprendizaje estrategia = crearEstrategia();
            if (estrategia == null) {
                logger.error("No se pudo crear la estrategia: " + estrategiaSeleccionada);
                return false;
            }
            
            // Crear y mostrar la vista de curso
            cursoView = new CursoInterfaceView(owner, cursoActual, estrategia);
            
            // Configurar eventos de la vista
            configurarEventosVista();
            
            // Mostrar la vista
            cursoView.show();
            
            logger.info("Vista de curso mostrada correctamente");
            return true;
            
        } catch (Exception e) {
            logger.error("Error al mostrar vista de curso", e);
            return false;
        }
    }
    
    /**
     * Crea la estrategia de aprendizaje seleccionada.
     * 
     * @return Estrategia de aprendizaje creada, o null si hay error
     */
    private EstrategiaAprendizaje crearEstrategia() {
        logger.debug("Creando estrategia: " + estrategiaSeleccionada);
        
        try {
            // Obtener todas las preguntas del curso
            List<com.kursor.domain.Pregunta> todasLasPreguntas = obtenerTodasLasPreguntas();
            
            // Crear la estrategia usando el StrategyManager
            EstrategiaAprendizaje estrategia = strategyManager.crearEstrategia(estrategiaSeleccionada, todasLasPreguntas);
            
            if (estrategia == null) {
                logger.error("StrategyManager no pudo crear la estrategia: " + estrategiaSeleccionada);
                return null;
            }
            
            logger.info("Estrategia creada correctamente: " + estrategia.getNombre());
            return estrategia;
            
        } catch (Exception e) {
            logger.error("Error al crear estrategia: " + estrategiaSeleccionada, e);
            return null;
        }
    }
    
    /**
     * Obtiene todas las preguntas del curso actual.
     * 
     * @return Lista de todas las preguntas del curso
     */
    private List<com.kursor.domain.Pregunta> obtenerTodasLasPreguntas() {
        logger.debug("Obteniendo todas las preguntas del curso: " + cursoActual.getId());
        
        try {
            // Cargar el curso completo usando CursoManager
            com.kursor.domain.Curso cursoCompleto = com.kursor.util.CursoManager.getInstance()
                .obtenerCursoCompleto(cursoActual.getId());
            
            if (cursoCompleto == null) {
                logger.error("No se pudo cargar el curso completo: " + cursoActual.getId());
                return new java.util.ArrayList<>();
            }
            
            // Extraer todas las preguntas de todos los bloques
            List<com.kursor.domain.Pregunta> todasLasPreguntas = new java.util.ArrayList<>();
            
            for (com.kursor.domain.Bloque bloque : cursoCompleto.getBloques()) {
                todasLasPreguntas.addAll(bloque.getPreguntas());
            }
            
            logger.info("Preguntas obtenidas correctamente - Total: " + todasLasPreguntas.size());
            return todasLasPreguntas;
            
        } catch (Exception e) {
            logger.error("Error al obtener preguntas del curso", e);
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Configura los eventos de la vista de curso.
     */
    private void configurarEventosVista() {
        // TODO: Implementar configuración de eventos
        // - Evento de verificación de respuesta
        // - Evento de navegación a siguiente pregunta
        // - Evento de finalización del curso
        logger.debug("Configurando eventos de la vista de curso");
    }
    
    /**
     * Obtiene el curso actual.
     * 
     * @return Curso que se está ejecutando
     */
    public CursoDTO getCursoActual() {
        return cursoActual;
    }
    
    /**
     * Obtiene la estrategia seleccionada.
     * 
     * @return Nombre de la estrategia seleccionada
     */
    public String getEstrategiaSeleccionada() {
        return estrategiaSeleccionada;
    }
    
    /**
     * Obtiene el gestor de sesión actual.
     * 
     * @return Gestor de sesión del curso
     */
    public CursoSessionManager getSessionManager() {
        return sessionManager;
    }
    
    /**
     * Verifica si hay un curso en ejecución.
     * 
     * @return true si hay un curso activo, false en caso contrario
     */
    public boolean isCursoActivo() {
        return cursoActual != null && cursoView != null && cursoView.isShowing();
    }
    
    /**
     * Finaliza la ejecución del curso actual.
     */
    public void finalizarCurso() {
        logger.info("Finalizando curso actual");
        
        if (cursoView != null) {
            cursoView.close();
        }
        
        // Limpiar referencias
        cursoActual = null;
        estrategiaSeleccionada = null;
        sessionManager = null;
        estrategiaModal = null;
        cursoView = null;
        
        logger.info("Curso finalizado");
    }
} 