package com.kursor.presentation.viewmodels;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.yaml.dto.CursoDTO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * ViewModel para el modal de selección de estrategias de aprendizaje.
 * 
 * <p>Este ViewModel maneja el estado de la vista y proporciona binding
 * automático para la interfaz de usuario, siguiendo el patrón MVVM:</p>
 * <ul>
 *   <li><strong>Estado:</strong> Gestiona las propiedades observables</li>
 *   <li><strong>Binding:</strong> Proporciona propiedades para binding automático</li>
 *   <li><strong>Validación:</strong> Maneja la validación de estado</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaModule
 * @see CursoDTO
 */
public class StrategySelectorViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategySelectorViewModel.class);
    
    /** Soporte para notificaciones de cambio de propiedades */
    private final PropertyChangeSupport propertyChangeSupport;
    
    /** Propiedades observables para binding */
    private final StringProperty estrategiaSeleccionada;
    private final BooleanProperty estrategiaValida;
    private final BooleanProperty cargando;
    private final StringProperty mensajeError;
    private final ObjectProperty<CursoDTO> curso;
    private final ListProperty<EstrategiaModule> estrategias;
    
    /** Estado interno */
    private boolean inicializado;
    
    /**
     * Constructor del ViewModel.
     */
    public StrategySelectorViewModel() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        
        // Inicializar propiedades observables
        this.estrategiaSeleccionada = new SimpleStringProperty(this, "estrategiaSeleccionada", null);
        this.estrategiaValida = new SimpleBooleanProperty(this, "estrategiaValida", false);
        this.cargando = new SimpleBooleanProperty(this, "cargando", false);
        this.mensajeError = new SimpleStringProperty(this, "mensajeError", null);
        this.curso = new SimpleObjectProperty<>(this, "curso", null);
        this.estrategias = new SimpleListProperty<>(this, "estrategias", FXCollections.observableArrayList());
        
        // Configurar listeners para validación automática
        configurarValidacionAutomatica();
        
        logger.debug("StrategySelectorViewModel creado");
    }
    
    /**
     * Configura la validación automática basada en cambios de propiedades.
     */
    private void configurarValidacionAutomatica() {
        // Validar cuando cambie la estrategia seleccionada
        estrategiaSeleccionada.addListener((obs, oldVal, newVal) -> {
            validarEstrategiaSeleccionada();
        });
        
        // Validar cuando cambien las estrategias disponibles
        estrategias.addListener((obs, oldVal, newVal) -> {
            validarEstrategiaSeleccionada();
        });
    }
    
    /**
     * Valida la estrategia seleccionada contra las estrategias disponibles.
     */
    private void validarEstrategiaSeleccionada() {
        String estrategia = estrategiaSeleccionada.get();
        List<EstrategiaModule> disponibles = estrategias.get();
        
        boolean esValida = estrategia != null && 
                          !estrategia.trim().isEmpty() &&
                          disponibles.stream()
                              .anyMatch(e -> e.getNombre().equals(estrategia));
        
        estrategiaValida.set(esValida);
        
        logger.debug("Validación de estrategia: {} -> {}", estrategia, esValida);
    }
    
    /**
     * Inicializa el ViewModel con un curso.
     * 
     * @param curso Curso para el cual seleccionar la estrategia
     */
    public void inicializar(CursoDTO curso) {
        logger.debug("Inicializando ViewModel con curso: {}", curso.getTitulo());
        
        this.curso.set(curso);
        this.inicializado = true;
        
        // Limpiar estado previo
        limpiarEstado();
        
        logger.info("ViewModel inicializado para curso: {}", curso.getTitulo());
    }
    
    /**
     * Carga las estrategias en el ViewModel.
     * 
     * @param estrategias Lista de estrategias disponibles
     */
    public void cargarEstrategias(List<EstrategiaModule> estrategias) {
        logger.debug("Cargando {} estrategias en el ViewModel", estrategias.size());
        
        cargando.set(true);
        mensajeError.set(null);
        
        try {
            this.estrategias.setAll(estrategias);
            logger.info("Estrategias cargadas correctamente: {}", estrategias.size());
        } catch (Exception e) {
            logger.error("Error al cargar estrategias: {}", e.getMessage(), e);
            mensajeError.set("Error al cargar estrategias: " + e.getMessage());
        } finally {
            cargando.set(false);
        }
    }
    
    /**
     * Selecciona una estrategia.
     * 
     * @param nombreEstrategia Nombre de la estrategia a seleccionar
     */
    public void seleccionarEstrategia(String nombreEstrategia) {
        logger.debug("Seleccionando estrategia en ViewModel: {}", nombreEstrategia);
        
        if (!inicializado) {
            logger.warn("ViewModel no inicializado");
            return;
        }
        
        estrategiaSeleccionada.set(nombreEstrategia);
        
        // Notificar cambio
        propertyChangeSupport.firePropertyChange("estrategiaSeleccionada", null, nombreEstrategia);
    }
    
    /**
     * Deselecciona la estrategia actual.
     */
    public void deseleccionarEstrategia() {
        logger.debug("Deseleccionando estrategia en ViewModel");
        
        estrategiaSeleccionada.set(null);
        
        // Notificar cambio
        propertyChangeSupport.firePropertyChange("estrategiaSeleccionada", null, null);
    }
    
    /**
     * Confirma la selección actual.
     * 
     * @return true si la selección es válida, false en caso contrario
     */
    public boolean confirmarSeleccion() {
        logger.debug("Confirmando selección en ViewModel");
        
        if (!estrategiaValida.get()) {
            logger.warn("No se puede confirmar: estrategia no válida");
            return false;
        }
        
        logger.info("Selección confirmada: {}", estrategiaSeleccionada.get());
        return true;
    }
    
    /**
     * Cancela la selección.
     */
    public void cancelarSeleccion() {
        logger.debug("Cancelando selección en ViewModel");
        
        deseleccionarEstrategia();
        
        // Notificar cancelación
        propertyChangeSupport.firePropertyChange("seleccionCancelada", false, true);
    }
    
    /**
     * Limpia el estado del ViewModel.
     */
    public void limpiarEstado() {
        logger.debug("Limpiando estado del ViewModel");
        
        estrategiaSeleccionada.set(null);
        estrategiaValida.set(false);
        cargando.set(false);
        mensajeError.set(null);
        estrategias.clear();
    }
    
    /**
     * Establece un mensaje de error.
     * 
     * @param mensaje Mensaje de error
     */
    public void setError(String mensaje) {
        logger.debug("Estableciendo error en ViewModel: {}", mensaje);
        mensajeError.set(mensaje);
    }
    
    /**
     * Limpia el mensaje de error.
     */
    public void limpiarError() {
        mensajeError.set(null);
    }
    
    // ===== PROPIEDADES PARA BINDING =====
    
    /**
     * Obtiene la propiedad de estrategia seleccionada.
     * 
     * @return StringProperty para binding
     */
    public StringProperty estrategiaSeleccionadaProperty() {
        return estrategiaSeleccionada;
    }
    
    /**
     * Obtiene la propiedad de validación de estrategia.
     * 
     * @return BooleanProperty para binding
     */
    public BooleanProperty estrategiaValidaProperty() {
        return estrategiaValida;
    }
    
    /**
     * Obtiene la propiedad de estado de carga.
     * 
     * @return BooleanProperty para binding
     */
    public BooleanProperty cargandoProperty() {
        return cargando;
    }
    
    /**
     * Obtiene la propiedad de mensaje de error.
     * 
     * @return StringProperty para binding
     */
    public StringProperty mensajeErrorProperty() {
        return mensajeError;
    }
    
    /**
     * Obtiene la propiedad del curso.
     * 
     * @return ObjectProperty para binding
     */
    public ObjectProperty<CursoDTO> cursoProperty() {
        return curso;
    }
    
    /**
     * Obtiene la propiedad de lista de estrategias.
     * 
     * @return ListProperty para binding
     */
    public ListProperty<EstrategiaModule> estrategiasProperty() {
        return estrategias;
    }
    
    // ===== GETTERS Y SETTERS =====
    
    public String getEstrategiaSeleccionada() {
        return estrategiaSeleccionada.get();
    }
    
    public void setEstrategiaSeleccionada(String estrategiaSeleccionada) {
        this.estrategiaSeleccionada.set(estrategiaSeleccionada);
    }
    
    public boolean isEstrategiaValida() {
        return estrategiaValida.get();
    }
    
    public void setEstrategiaValida(boolean estrategiaValida) {
        this.estrategiaValida.set(estrategiaValida);
    }
    
    public boolean isCargando() {
        return cargando.get();
    }
    
    public void setCargando(boolean cargando) {
        this.cargando.set(cargando);
    }
    
    public String getMensajeError() {
        return mensajeError.get();
    }
    
    public void setMensajeError(String mensajeError) {
        this.mensajeError.set(mensajeError);
    }
    
    public CursoDTO getCurso() {
        return curso.get();
    }
    
    public void setCurso(CursoDTO curso) {
        this.curso.set(curso);
    }
    
    public ObservableList<EstrategiaModule> getEstrategias() {
        return estrategias.get();
    }
    
    public void setEstrategias(ObservableList<EstrategiaModule> estrategias) {
        this.estrategias.set(estrategias);
    }
    
    // ===== PROPERTY CHANGE SUPPORT =====
    
    /**
     * Añade un listener para cambios de propiedades.
     * 
     * @param listener Listener a añadir
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Añade un listener para una propiedad específica.
     * 
     * @param propertyName Nombre de la propiedad
     * @param listener Listener a añadir
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    /**
     * Remueve un listener de cambios de propiedades.
     * 
     * @param listener Listener a remover
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Remueve un listener de una propiedad específica.
     * 
     * @param propertyName Nombre de la propiedad
     * @param listener Listener a remover
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }
} 