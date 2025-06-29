package com.kursor.ui.presentation.main;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.ui.application.usecases.LoadCoursesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel para la vista principal de la aplicación.
 * 
 * <p>Este ViewModel implementa el patrón MVVM mejorado con las siguientes características:</p>
 * <ul>
 *   <li>Binding automático mediante PropertyChangeSupport</li>
 *   <li>Operaciones asíncronas para no bloquear la UI</li>
 *   <li>Manejo de estados de carga y error</li>
 *   <li>Separación clara de responsabilidades</li>
 *   <li>Logging detallado de operaciones</li>
 * </ul>
 * 
 * <p>Propiedades observables:</p>
 * <ul>
 *   <li>cursos - Lista de cursos disponibles</li>
 *   <li>loading - Estado de carga</li>
 *   <li>error - Mensaje de error</li>
 *   <li>selectedCourse - Curso seleccionado</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see LoadCoursesUseCase
 * @see CursoDTO
 */
public class MainViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(MainViewModel.class);
    
    // Constantes para nombres de propiedades
    public static final String PROPERTY_CURSOS = "cursos";
    public static final String PROPERTY_LOADING = "loading";
    public static final String PROPERTY_ERROR = "error";
    public static final String PROPERTY_SELECTED_COURSE = "selectedCourse";
    
    // Propiedades observables
    private List<CursoDTO> cursos;
    private boolean loading;
    private String error;
    private CursoDTO selectedCourse;
    
    // Soporte para binding automático
    private final PropertyChangeSupport propertyChangeSupport;
    
    // Casos de uso
    private final LoadCoursesUseCase loadCoursesUseCase;
    
    // Executor para operaciones asíncronas
    private final ExecutorService executorService;
    
    /**
     * Constructor para crear el ViewModel principal.
     * 
     * @param loadCoursesUseCase Caso de uso para cargar cursos
     */
    public MainViewModel(LoadCoursesUseCase loadCoursesUseCase) {
        this.loadCoursesUseCase = loadCoursesUseCase;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.executorService = Executors.newCachedThreadPool();
        
        // Inicializar propiedades
        this.cursos = List.of();
        this.loading = false;
        this.error = null;
        this.selectedCourse = null;
        
        logger.debug("MainViewModel creado exitosamente");
    }
    
    /**
     * Carga los cursos de forma asíncrona.
     * 
     * <p>Este método inicia la carga de cursos en un hilo separado para no
     * bloquear la interfaz de usuario. Los resultados se notifican a través
     * de las propiedades observables.</p>
     */
    public void loadCoursesAsync() {
        logger.info("Iniciando carga asíncrona de cursos");
        
        // Actualizar estado de carga
        setLoading(true);
        setError(null);
        
        CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Ejecutando caso de uso de carga de cursos");
                return loadCoursesUseCase.execute();
            } catch (LoadCoursesUseCase.UseCaseException e) {
                logger.error("Error en caso de uso: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, executorService)
        .thenAccept(cursosCargados -> {
            logger.info("Cursos cargados exitosamente: {}", cursosCargados.size());
            setCursos(cursosCargados);
            setLoading(false);
        })
        .exceptionally(throwable -> {
            logger.error("Error durante la carga de cursos: {}", throwable.getMessage(), throwable);
            setError("Error al cargar los cursos: " + throwable.getMessage());
            setLoading(false);
            return null;
        });
    }
    
    /**
     * Selecciona un curso específico.
     * 
     * @param curso Curso a seleccionar
     */
    public void selectCourse(CursoDTO curso) {
        logger.debug("Seleccionando curso: {}", curso != null ? curso.getTitulo() : "null");
        setSelectedCourse(curso);
    }
    
    /**
     * Limpia la selección actual.
     */
    public void clearSelection() {
        logger.debug("Limpiando selección de curso");
        setSelectedCourse(null);
    }
    
    /**
     * Limpia el mensaje de error.
     */
    public void clearError() {
        logger.debug("Limpiando mensaje de error");
        setError(null);
    }
    
    /**
     * Obtiene la lista de cursos.
     * 
     * @return Lista de cursos disponibles
     */
    public List<CursoDTO> getCursos() {
        return cursos;
    }
    
    /**
     * Establece la lista de cursos y notifica a los observadores.
     * 
     * @param cursos Nueva lista de cursos
     */
    public void setCursos(List<CursoDTO> cursos) {
        List<CursoDTO> oldCursos = this.cursos;
        this.cursos = cursos != null ? cursos : List.of();
        
        logger.debug("Cursos actualizados: {} cursos", this.cursos.size());
        propertyChangeSupport.firePropertyChange(PROPERTY_CURSOS, oldCursos, this.cursos);
    }
    
    /**
     * Verifica si está en estado de carga.
     * 
     * @return true si está cargando, false en caso contrario
     */
    public boolean isLoading() {
        return loading;
    }
    
    /**
     * Establece el estado de carga y notifica a los observadores.
     * 
     * @param loading Nuevo estado de carga
     */
    public void setLoading(boolean loading) {
        boolean oldLoading = this.loading;
        this.loading = loading;
        
        logger.debug("Estado de carga actualizado: {}", loading);
        propertyChangeSupport.firePropertyChange(PROPERTY_LOADING, oldLoading, this.loading);
    }
    
    /**
     * Obtiene el mensaje de error actual.
     * 
     * @return Mensaje de error, o null si no hay error
     */
    public String getError() {
        return error;
    }
    
    /**
     * Establece el mensaje de error y notifica a los observadores.
     * 
     * @param error Nuevo mensaje de error
     */
    public void setError(String error) {
        String oldError = this.error;
        this.error = error;
        
        if (error != null) {
            logger.warn("Error establecido: {}", error);
        } else {
            logger.debug("Error limpiado");
        }
        
        propertyChangeSupport.firePropertyChange(PROPERTY_ERROR, oldError, this.error);
    }
    
    /**
     * Obtiene el curso seleccionado actualmente.
     * 
     * @return Curso seleccionado, o null si no hay selección
     */
    public CursoDTO getSelectedCourse() {
        return selectedCourse;
    }
    
    /**
     * Establece el curso seleccionado y notifica a los observadores.
     * 
     * @param selectedCourse Nuevo curso seleccionado
     */
    public void setSelectedCourse(CursoDTO selectedCourse) {
        CursoDTO oldSelectedCourse = this.selectedCourse;
        this.selectedCourse = selectedCourse;
        
        logger.debug("Curso seleccionado actualizado: {}", 
            selectedCourse != null ? selectedCourse.getTitulo() : "null");
        propertyChangeSupport.firePropertyChange(PROPERTY_SELECTED_COURSE, oldSelectedCourse, this.selectedCourse);
    }
    
    /**
     * Agrega un listener para cambios en las propiedades.
     * 
     * @param listener Listener a agregar
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        logger.debug("PropertyChangeListener agregado: {}", listener.getClass().getSimpleName());
    }
    
    /**
     * Agrega un listener para una propiedad específica.
     * 
     * @param propertyName Nombre de la propiedad a observar
     * @param listener Listener a agregar
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
        logger.debug("PropertyChangeListener agregado para {}: {}", 
            propertyName, listener.getClass().getSimpleName());
    }
    
    /**
     * Remueve un listener de cambios en las propiedades.
     * 
     * @param listener Listener a remover
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
        logger.debug("PropertyChangeListener removido: {}", listener.getClass().getSimpleName());
    }
    
    /**
     * Remueve un listener de una propiedad específica.
     * 
     * @param propertyName Nombre de la propiedad
     * @param listener Listener a remover
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
        logger.debug("PropertyChangeListener removido de {}: {}", 
            propertyName, listener.getClass().getSimpleName());
    }
    
    /**
     * Libera los recursos utilizados por el ViewModel.
     * 
     * <p>Este método debe ser llamado cuando el ViewModel ya no se necesite
     * para evitar fugas de memoria y liberar recursos del executor.</p>
     */
    public void dispose() {
        logger.info("Disponiendo MainViewModel");
        
        try {
            executorService.shutdown();
            logger.debug("ExecutorService cerrado");
        } catch (Exception e) {
            logger.error("Error al cerrar ExecutorService: {}", e.getMessage(), e);
        }
        
        // Limpiar listeners
        PropertyChangeListener[] listeners = propertyChangeSupport.getPropertyChangeListeners();
        for (PropertyChangeListener listener : listeners) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
        
        logger.debug("MainViewModel dispuesto correctamente");
    }
    
    /**
     * Verifica si el ViewModel está en un estado válido.
     * 
     * @return true si el estado es válido, false en caso contrario
     */
    public boolean isValid() {
        boolean valid = loadCoursesUseCase != null && 
                       propertyChangeSupport != null && 
                       !executorService.isShutdown();
        
        logger.debug("Estado de validez del ViewModel: {}", valid);
        return valid;
    }
} 