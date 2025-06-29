package com.kursor.presentation.viewmodels;

import com.kursor.yaml.dto.BloqueDTO;
import com.kursor.yaml.dto.CursoDTO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ViewModel para el selector de bloques siguiendo el patrón MVC.
 * 
 * <p>Este ViewModel maneja el estado y la lógica de presentación para el
 * modal de selección de bloques, separando la lógica de negocio de la vista.</p>
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Gestionar el estado de los bloques disponibles</li>
 *   <li>Mantener la referencia al bloque seleccionado</li>
 *   <li>Proporcionar propiedades observables para binding con la vista</li>
 *   <li>Validar la selección del usuario</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see BloqueDTO
 * @see CursoDTO
 */
public class BloqueSelectorViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(BloqueSelectorViewModel.class);
    
    /** Propiedades observables para binding con la vista */
    private final ObjectProperty<CursoDTO> curso = new SimpleObjectProperty<>();
    private final ObjectProperty<BloqueDTO> bloqueSeleccionado = new SimpleObjectProperty<>();
    private final ObservableList<BloqueDTO> bloquesDisponibles = FXCollections.observableArrayList();
    private final BooleanProperty bloqueValido = new SimpleBooleanProperty(false);
    private final StringProperty mensajeError = new SimpleStringProperty("");
    private final BooleanProperty cargando = new SimpleBooleanProperty(false);
    
    /**
     * Constructor por defecto.
     */
    public BloqueSelectorViewModel() {
        logger.debug("Inicializando BloqueSelectorViewModel");
        
        // Configurar listeners para validación automática
        bloqueSeleccionado.addListener((observable, oldValue, newValue) -> {
            validarSeleccion();
        });
    }
    
    /**
     * Inicializa el ViewModel con un curso.
     * 
     * @param curso Curso para el cual seleccionar bloques
     */
    public void inicializarConCurso(CursoDTO curso) {
        logger.debug("Inicializando ViewModel con curso: {}", curso.getTitulo());
        
        this.curso.set(curso);
        this.bloqueSeleccionado.set(null);
        this.mensajeError.set("");
        this.cargando.set(true);
        
        try {
            // Cargar bloques del curso
            if (curso.getBloques() != null && !curso.getBloques().isEmpty()) {
                bloquesDisponibles.setAll(curso.getBloques());
                logger.info("Cargados {} bloques para curso: {}", curso.getBloques().size(), curso.getTitulo());
            } else {
                bloquesDisponibles.clear();
                logger.warn("El curso {} no tiene bloques disponibles", curso.getTitulo());
                this.mensajeError.set("Este curso no tiene bloques configurados.");
            }
        } catch (Exception e) {
            logger.error("Error al cargar bloques del curso: {}", curso.getTitulo(), e);
            this.mensajeError.set("Error al cargar los bloques del curso.");
            bloquesDisponibles.clear();
        } finally {
            this.cargando.set(false);
        }
    }
    
    /**
     * Selecciona un bloque.
     * 
     * @param bloque Bloque a seleccionar
     */
    public void seleccionarBloque(BloqueDTO bloque) {
        logger.debug("Seleccionando bloque: {}", bloque != null ? bloque.getTitulo() : "null");
        this.bloqueSeleccionado.set(bloque);
    }
    
    /**
     * Valida la selección actual del usuario.
     * 
     * @return true si la selección es válida, false en caso contrario
     */
    public boolean validarSeleccion() {
        boolean esValido = bloqueSeleccionado.get() != null;
        bloqueValido.set(esValido);
        
        if (!esValido) {
            mensajeError.set("Por favor, selecciona un bloque antes de continuar.");
        } else {
            mensajeError.set("");
        }
        
        logger.debug("Validación de selección: {}", esValido);
        return esValido;
    }
    
    /**
     * Limpia la selección actual.
     */
    public void limpiarSeleccion() {
        logger.debug("Limpiando selección de bloque");
        this.bloqueSeleccionado.set(null);
        this.mensajeError.set("");
    }
    
    /**
     * Obtiene el bloque seleccionado.
     * 
     * @return Bloque seleccionado, o null si no hay selección
     */
    public BloqueDTO getBloqueSeleccionado() {
        return bloqueSeleccionado.get();
    }
    
    /**
     * Verifica si hay un bloque seleccionado.
     * 
     * @return true si hay un bloque seleccionado, false en caso contrario
     */
    public boolean isBloqueSeleccionado() {
        return bloqueSeleccionado.get() != null;
    }
    
    /**
     * Obtiene la lista de bloques disponibles.
     * 
     * @return Lista observable de bloques disponibles
     */
    public ObservableList<BloqueDTO> getBloquesDisponibles() {
        return bloquesDisponibles;
    }
    
    /**
     * Obtiene el número de bloques disponibles.
     * 
     * @return Número de bloques disponibles
     */
    public int getNumeroBloques() {
        return bloquesDisponibles.size();
    }
    
    /**
     * Verifica si hay bloques disponibles.
     * 
     * @return true si hay bloques disponibles, false en caso contrario
     */
    public boolean tieneBloquesDisponibles() {
        return !bloquesDisponibles.isEmpty();
    }
    
    /**
     * Obtiene el curso actual.
     * 
     * @return Curso actual
     */
    public CursoDTO getCurso() {
        return curso.get();
    }
    
    /**
     * Verifica si está cargando.
     * 
     * @return true si está cargando, false en caso contrario
     */
    public boolean isCargando() {
        return cargando.get();
    }
    
    /**
     * Obtiene el mensaje de error actual.
     * 
     * @return Mensaje de error, o cadena vacía si no hay error
     */
    public String getMensajeError() {
        return mensajeError.get();
    }
    
    /**
     * Verifica si la selección es válida.
     * 
     * @return true si la selección es válida, false en caso contrario
     */
    public boolean isSeleccionValida() {
        return bloqueValido.get();
    }
    
    // Propiedades para binding con la vista
    
    /**
     * Propiedad del curso para binding.
     * 
     * @return Propiedad observable del curso
     */
    public ObjectProperty<CursoDTO> cursoProperty() {
        return curso;
    }
    
    /**
     * Propiedad del bloque seleccionado para binding.
     * 
     * @return Propiedad observable del bloque seleccionado
     */
    public ObjectProperty<BloqueDTO> bloqueSeleccionadoProperty() {
        return bloqueSeleccionado;
    }
    
    /**
     * Propiedad de bloques disponibles para binding.
     * 
     * @return Lista observable de bloques disponibles
     */
    public ObservableList<BloqueDTO> bloquesDisponiblesProperty() {
        return bloquesDisponibles;
    }
    
    /**
     * Propiedad de validación para binding.
     * 
     * @return Propiedad observable de validación
     */
    public BooleanProperty bloqueValidoProperty() {
        return bloqueValido;
    }
    
    /**
     * Propiedad de mensaje de error para binding.
     * 
     * @return Propiedad observable del mensaje de error
     */
    public StringProperty mensajeErrorProperty() {
        return mensajeError;
    }
    
    /**
     * Propiedad de carga para binding.
     * 
     * @return Propiedad observable de carga
     */
    public BooleanProperty cargandoProperty() {
        return cargando;
    }
} 