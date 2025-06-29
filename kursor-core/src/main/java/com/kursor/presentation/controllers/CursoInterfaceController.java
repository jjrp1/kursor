package com.kursor.presentation.controllers;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.presentation.dialogs.StrategySelectorModal;
import com.kursor.shared.util.CursoManager;
import com.kursor.shared.util.StrategyManager;
import com.kursor.strategy.EstrategiaModule;
import com.kursor.yaml.dto.CursoDTO;
import com.kursor.presentation.views.CursoInterfaceView;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// Importar DTOs y dominio
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.shared.util.StrategyManager;

// Importar vistas y diálogos
import com.kursor.presentation.views.CursoInterfaceView;
import com.kursor.presentation.dialogs.StrategySelectorModal;

// Importar persistencia
import com.kursor.persistence.repository.SesionRepository;
import com.kursor.persistence.repository.PreguntaSesionRepository;
import com.kursor.persistence.repository.EstadoEstrategiaRepository;
import com.kursor.persistence.config.PersistenceConfig;

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
 * @see StrategySelectorModal
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
    
    /** Modal de selección de estrategia (versión MVC) */
    private StrategySelectorModal estrategiaModal;
    
    /** Modal de ejecución de curso */
    private CursoInterfaceView cursoView;
    
    /** Gestor de sesión del curso */
    private CursoSessionManager sessionManager;
    
    /** Curso que se está ejecutando */
    private CursoDTO cursoActual;
    
    /** Estrategia seleccionada */
    private String estrategiaSeleccionada;
    
    // Nuevos campos para persistencia
    private SesionRepository sesionRepository;
    private PreguntaSesionRepository preguntaSesionRepository;
    private EstadoEstrategiaRepository estadoEstrategiaRepository;
    
    /**
     * Constructor del controlador.
     * 
     * @param owner Ventana padre desde la cual se lanza el curso
     */
    public CursoInterfaceController(Window owner) {
        this.owner = owner;
        this.strategyManager = StrategyManager.getInstance();
        
        // Inicializar repositorios si la persistencia está disponible
        inicializarRepositorios();
        
        logger.info("CursoInterfaceController inicializado");
    }
    
    /**
     * Inicializa los repositorios de persistencia si están disponibles.
     */
    private void inicializarRepositorios() {
        try {
            if (PersistenceConfig.isInitialized()) {
                var entityManager = PersistenceConfig.createEntityManager();
                this.sesionRepository = new SesionRepository(entityManager);
                this.preguntaSesionRepository = new PreguntaSesionRepository(entityManager);
                this.estadoEstrategiaRepository = new EstadoEstrategiaRepository(entityManager);
                logger.info("Repositorios de persistencia inicializados correctamente");
            } else {
                logger.warn("PersistenceConfig no está inicializado, usando modo memoria");
            }
        } catch (Exception e) {
            logger.error("Error al inicializar repositorios de persistencia", e);
        }
    }
    
    /**
     * Inicia un curso con la estrategia y bloque ya seleccionados.
     * 
     * @param curso Curso a ejecutar
     * @param estrategiaSeleccionada Estrategia ya seleccionada
     * @param bloqueSeleccionado Bloque ya seleccionado
     * @return true si se inició correctamente, false si hubo error
     */
    public boolean iniciarCurso(CursoDTO curso, String estrategiaSeleccionada, com.kursor.yaml.dto.BloqueDTO bloqueSeleccionado) {
        logger.info("Iniciando curso: {} con estrategia: {} y bloque: {}", 
                   curso.getTitulo(), estrategiaSeleccionada, bloqueSeleccionado.getTitulo());
        
        if (curso == null) {
            logger.error("No se puede iniciar un curso null");
            return false;
        }
        
        this.cursoActual = curso;
        this.estrategiaSeleccionada = estrategiaSeleccionada;
        
        // Paso 1: Inicializar gestor de sesión con el bloque seleccionado
        inicializarSessionManager(bloqueSeleccionado.getTitulo());
        
        // Paso 2: Mostrar vista de curso
        return mostrarVistaCurso();
    }

    /**
     * Inicia un curso solicitando la estrategia al usuario.
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
        
        // Paso 1: Mostrar modal de selección de estrategia
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
        logger.debug("Mostrando modal de selección de estrategia (versión MVC)");
        
        estrategiaModal = new StrategySelectorModal(owner, cursoActual);
        return estrategiaModal.mostrarYEsperar();
    }
    
    /**
     * Inicializa el gestor de sesión para el curso actual.
     */
    private void inicializarSessionManager() {
        // Usar el método que acepta un bloque específico
        String bloqueId = obtenerBloqueSeleccionado();
        inicializarSessionManager(bloqueId);
    }
    
    /**
     * Inicializa el gestor de sesión para el curso actual con un bloque específico.
     * 
     * @param bloqueId ID del bloque seleccionado
     */
    private void inicializarSessionManager(String bloqueId) {
        logger.debug("Inicializando gestor de sesión para curso: {} con bloque: {}", 
                    cursoActual.getId(), bloqueId);
        
        try {
            if (isPersistenciaDisponible()) {
                // Usar persistencia real
                sessionManager = new CursoSessionManager(
                    cursoActual.getId(), 
                    bloqueId, 
                    estrategiaSeleccionada,
                    sesionRepository,
                    preguntaSesionRepository,
                    estadoEstrategiaRepository
                );
                logger.info("Gestor de sesión inicializado con persistencia real");
            } else {
                // Usar modo memoria (legacy)
                sessionManager = new CursoSessionManager(cursoActual.getId(), estrategiaSeleccionada);
                logger.warn("Gestor de sesión inicializado en modo memoria (sin persistencia)");
            }
            
            sessionManager.inicializar();
            logger.info("Gestor de sesión inicializado correctamente");
            
        } catch (Exception e) {
            logger.error("Error al inicializar gestor de sesión", e);
            // Fallback a modo memoria
            sessionManager = new CursoSessionManager(cursoActual.getId(), estrategiaSeleccionada);
            sessionManager.inicializar();
        }
    }
    
    /**
     * Obtiene el bloque seleccionado para el curso.
     * 
     * @return ID del bloque seleccionado
     */
    private String obtenerBloqueSeleccionado() {
        // TODO: Implementar lógica de selección de bloque
        // Por ahora usamos el primer bloque disponible
        if (cursoActual.getBloques() != null && !cursoActual.getBloques().isEmpty()) {
            // Usar el título como ID temporal ya que BloqueDTO no tiene getId()
            return cursoActual.getBloques().get(0).getTitulo();
        }
        return "default";
    }
    
    /**
     * Verifica si la persistencia está disponible.
     * 
     * @return true si la persistencia está disponible, false en caso contrario
     */
    private boolean isPersistenciaDisponible() {
        return sesionRepository != null && preguntaSesionRepository != null && estadoEstrategiaRepository != null;
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
                logger.error("No se pudo crear la estrategia: {}", estrategiaSeleccionada);
                return false;
            }
            
            // Crear y mostrar la vista de curso con el sessionManager
            if (sessionManager != null && sessionManager.isPersistenciaHabilitada()) {
                logger.info("Creando vista de curso con persistencia habilitada");
                cursoView = new CursoInterfaceView(owner, cursoActual, estrategia, sessionManager);
            } else {
                logger.warn("Creando vista de curso sin persistencia (modo memoria)");
                cursoView = new CursoInterfaceView(owner, cursoActual, estrategia);
            }
            
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
            com.kursor.domain.Curso cursoCompleto = com.kursor.shared.util.CursoManager.getInstance()
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
