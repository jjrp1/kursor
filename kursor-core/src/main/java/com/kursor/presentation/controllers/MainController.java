package com.kursor.presentation.controllers;

import com.kursor.shared.util.CursoManager;
import com.kursor.shared.util.ModuleManager;
import com.kursor.shared.util.StrategyManager;
import com.kursor.yaml.dto.CursoDTO;
import com.kursor.presentation.dialogs.AboutDialog;
import com.kursor.presentation.dialogs.AnalyticsDialog;
import com.kursor.presentation.viewmodels.SessionViewModel;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kursor.presentation.views.SessionTableView;
import com.kursor.presentation.views.MainView;
import com.kursor.presentation.viewmodels.MainViewModel;
import com.kursor.application.services.AnalyticsService;
import com.kursor.presentation.controllers.CursoExecutionManager;

import java.util.List;
import java.util.Optional;

/**
 * Controlador principal de la aplicación Kursor.
 * Maneja la lógica de negocio de la interfaz principal.
 */
public class MainController {
    
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    private final Stage primaryStage;
    private final MainView mainView;
    private final MainViewModel viewModel;
    
    private ModuleManager moduleManager;
    private StrategyManager strategyManager;
    private CursoManager cursoManager;
    
    private List<CursoDTO> cursos;
    private CursoDTO selectedCourse;
    
    private SessionController sessionController;
    private SessionTableView sessionTableView;
    
    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.viewModel = new MainViewModel();
        this.mainView = new MainView(this);
        this.sessionController = new SessionController(primaryStage);
        this.sessionTableView = sessionController.getSessionTableView();
        
        initializeSystemComponents();
        setupEventHandlers();
    }
    
    /**
     * Inicializa los componentes del sistema
     */
    private void initializeSystemComponents() {
        logger.info("🔧 Inicializando componentes del sistema...");
        
        try {
            logger.debug("📂 Iniciando carga de cursos desde carpeta cursos/");
            cursoManager = CursoManager.getInstance();
            logger.debug("✅ CursoManager inicializado correctamente");
            
            cursos = cursoManager.cargarCursosCompletos();
            logger.info("📚 Cursos cargados: {} cursos con {} preguntas en total", 
                cursos.size(), cursos.stream().mapToInt(CursoDTO::getTotalPreguntas).sum());
            
            logger.debug("📊 Detalle de cursos cargados:");
            for (int i = 0; i < cursos.size(); i++) {
                CursoDTO curso = cursos.get(i);
                logger.debug("   {}. {} - {} bloques, {} preguntas", 
                    i + 1, curso.getTitulo(), curso.getTotalBloques(), curso.getTotalPreguntas());
            }
            
            // Actualizar el modelo de vista
            viewModel.setCursos(cursos);
            
        } catch (Exception e) {
            logger.error("❌ Error crítico al inicializar componentes del sistema", e);
            throw new RuntimeException("Error al inicializar el sistema", e);
        }
    }
    
    /**
     * Configura los manejadores de eventos
     */
    private void setupEventHandlers() {
        // Eventos de la vista principal
        mainView.setOnCourseSelected(this::handleCourseSelection);
        mainView.setOnNewSession(this::handleNewSession);
        mainView.setOnResumeSession(this::handleResumeSession);
        mainView.setOnShowStatistics(this::handleShowStatistics);
        mainView.setOnShowAbout(this::handleShowAbout);
        mainView.setOnExitApplication(this::handleTerminarSesion);
    }
    
    /**
     * Muestra la vista principal
     */
    public void showMainView() {
        mainView.createMainInterface();
        mainView.updateCourseList(cursos);
        
        // Seleccionar el primer curso por defecto
        if (!cursos.isEmpty()) {
            selectedCourse = cursos.get(0);
            viewModel.setSelectedCourse(selectedCourse);
            mainView.selectCourse(0);
            updateCourseDetails();
            updateSessionsTable();
        }
    }
    
    /**
     * Maneja la selección de un curso
     */
    private void handleCourseSelection(int index) {
        if (index >= 0 && index < cursos.size()) {
            selectedCourse = cursos.get(index);
            viewModel.setSelectedCourse(selectedCourse);
            updateCourseDetails();
            updateSessionsTable();
        }
    }
    
    /**
     * Actualiza los detalles del curso seleccionado
     */
    private void updateCourseDetails() {
        if (selectedCourse != null) {
            mainView.updateCourseDetails(selectedCourse);
        }
    }
    
    /**
     * Actualiza la tabla de sesiones
     */
    private void updateSessionsTable() {
        if (selectedCourse != null) {
            sessionController.loadSessions(selectedCourse);
            mainView.setSessionTableView(sessionTableView);
            
            // Verificar si hay sesiones disponibles y actualizar estado del botón Reanudar
            boolean haySesiones = haySesionesDisponibles();
            mainView.setResumeButtonEnabled(haySesiones);
            
            if (!haySesiones) {
                logger.debug("No hay sesiones disponibles para curso: {}, botón Reanudar deshabilitado", selectedCourse.getTitulo());
            } else {
                logger.debug("Hay sesiones disponibles para curso: {}, botón Reanudar habilitado", selectedCourse.getTitulo());
            }
        }
    }
    
    /**
     * Verifica si hay sesiones disponibles para el curso seleccionado.
     * 
     * @return true si hay sesiones disponibles, false en caso contrario
     */
    private boolean haySesionesDisponibles() {
        if (selectedCourse == null) {
            return false;
        }
        
        try {
            // Obtener las sesiones del ViewModel del SessionController
            List<SessionViewModel.SessionData> sesiones = sessionController.getViewModel().getSessions();
            
            // Verificar si hay sesiones reales (no el mensaje de "Sin datos registrados")
            return sesiones.stream()
                .anyMatch(sesion -> !sesion.getBlock().equals("(Sin datos registrados)"));
                
        } catch (Exception e) {
            logger.error("Error al verificar sesiones disponibles para curso: {}", selectedCourse.getTitulo(), e);
            return false;
        }
    }
    
    /**
     * Maneja el inicio de una nueva sesión
     */
    private void handleNewSession() {
        if (selectedCourse == null) {
            showErrorDialog("Error", "Por favor selecciona un curso primero");
            return;
        }
        
        startNewSession();
    }
    
    /**
     * Inicia una nueva sesión
     */
    private void startNewSession() {
        logger.info("Iniciando nueva sesión para curso: {}", selectedCourse.getTitulo());
        
        try {
            // Usar el nuevo CursoExecutionManager que maneja todo el flujo
            CursoExecutionManager executionManager = new CursoExecutionManager(primaryStage, selectedCourse);
            boolean success = executionManager.ejecutarCurso();
            
            if (success) {
                logger.info("Sesión iniciada correctamente");
                // Actualizar la tabla de sesiones después de completar
                updateSessionsTable();
            } else {
                logger.info("Usuario canceló o no se pudo iniciar la sesión");
                // No mostrar error, es normal que el usuario cancele
            }
            
        } catch (Exception e) {
            logger.error("Error al iniciar sesión", e);
            showErrorDialog("Error", "Error al iniciar la sesión: " + e.getMessage());
        }
    }
    
    /**
     * Maneja la reanudación de una sesión
     */
    private void handleResumeSession() {
        if (selectedCourse == null) {
            showErrorDialog("Error", "Por favor selecciona un curso primero");
            return;
        }
        
        logger.info("Reanudando sesión para curso: {}", selectedCourse.getTitulo());
        
        try {
            CursoInterfaceController controller = new CursoInterfaceController(primaryStage);
            boolean success = controller.iniciarCurso(selectedCourse);
            
            if (!success) {
                showErrorDialog("Error", "No se pudo reanudar la sesión");
            }
            
        } catch (Exception e) {
            logger.error("Error al reanudar sesión", e);
            showErrorDialog("Error", "Error al reanudar la sesión: " + e.getMessage());
        }
    }
    
    /**
     * Maneja la visualización de estadísticas
     */
    private void handleShowStatistics() {
        if (selectedCourse == null) {
            showErrorDialog("Error", "Por favor selecciona un curso primero");
            return;
        }
        
        logger.info("Mostrando estadísticas avanzadas para curso: {}", selectedCourse.getTitulo());
        
        try {
            // Crear instancia del AnalyticsService y AnalyticsController
            AnalyticsService analyticsService = new AnalyticsService();
            AnalyticsController analyticsController = new AnalyticsController(analyticsService);
            AnalyticsDialog analyticsDialog = new AnalyticsDialog(primaryStage, analyticsController);
            analyticsDialog.showAndWait();
        } catch (Exception e) {
            logger.error("Error al mostrar analytics", e);
            showErrorDialog("Error", "Error al mostrar analytics: " + e.getMessage());
        }
    }
    
    /**
     * Maneja la visualización del diálogo About
     */
    private void handleShowAbout() {
        logger.info("Mostrando diálogo About");
        
        try {
            AboutDialog.show();
        } catch (Exception e) {
            logger.error("Error al mostrar diálogo About", e);
            showErrorDialog("Error", "Error al mostrar información: " + e.getMessage());
        }
    }
    
    /**
     * Maneja la terminación de la aplicación (botón Terminar)
     */
    public void handleTerminarSesion() {
        logger.info("👋 Cerrando aplicación desde botón Terminar");
        exitApplication();
    }
    
    /**
     * Sale de la aplicación de forma ordenada
     */
    public void exitApplication() {
        try {
            logger.info("🔄 Iniciando proceso de cierre ordenado...");
            
            // Cerrar ventana principal
            if (primaryStage != null) {
                primaryStage.close();
            }
            
            // Salir de la aplicación
            Platform.exit();
            
            logger.info("✅ Aplicación cerrada correctamente");
            
        } catch (Exception e) {
            logger.error("❌ Error durante el cierre de la aplicación", e);
            System.exit(1);
        }
    }
    
    /**
     * Muestra un diálogo de error
     */
    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Muestra un diálogo de información
     */
    private void showInfoDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Obtiene la ventana principal
     */
    public Window getPrimaryWindow() {
        return primaryStage;
    }
    
    /**
     * Obtiene el curso seleccionado
     */
    public CursoDTO getSelectedCourse() {
        return selectedCourse;
    }
    
    /**
     * Obtiene la lista de cursos
     */
    public List<CursoDTO> getCursos() {
        return cursos;
    }
} 
