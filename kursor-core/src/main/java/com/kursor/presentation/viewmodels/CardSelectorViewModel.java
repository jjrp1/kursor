package com.kursor.presentation.viewmodels;

import com.kursor.presentation.dialogs.SelectableItem;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ViewModel para el CardSelectorModal que maneja el estado y la lógica
 * de presentación para elementos seleccionables genéricos.
 * 
 * <p>Este ViewModel es genérico y puede trabajar con cualquier tipo de
 * elementos que implementen la interfaz SelectableItem, como bloques,
 * estrategias, etc.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SelectableItem
 * @see CardSelectorModal
 */
public class CardSelectorViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(CardSelectorViewModel.class);
    
    // Propiedades observables
    private final StringProperty tituloProperty = new SimpleStringProperty();
    private final StringProperty descripcionProperty = new SimpleStringProperty();
    private final ObservableList<SelectableItem> itemsProperty = FXCollections.observableArrayList();
    private final ObjectProperty<SelectableItem> itemSeleccionadoProperty = new SimpleObjectProperty<>();
    private final BooleanProperty itemValidoProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty cargandoProperty = new SimpleBooleanProperty(false);
    private final StringProperty mensajeErrorProperty = new SimpleStringProperty();
    
    /**
     * Constructor por defecto.
     */
    public CardSelectorViewModel() {
        logger.debug("CardSelectorViewModel creado");
        inicializarPropiedades();
    }
    
    /**
     * Constructor con configuración inicial.
     * 
     * @param titulo El título del modal
     * @param descripcion La descripción del modal
     * @param items La lista de elementos seleccionables
     */
    public CardSelectorViewModel(String titulo, String descripcion, List<SelectableItem> items) {
        logger.debug("CardSelectorViewModel creado con título: {}", titulo);
        inicializarPropiedades();
        configurar(titulo, descripcion, items);
    }
    
    /**
     * Inicializa las propiedades y sus listeners.
     */
    private void inicializarPropiedades() {
        // El botón se habilita solo cuando hay un elemento seleccionado
        itemValidoProperty.bind(itemSeleccionadoProperty.isNotNull());
        
        // Limpiar mensaje de error cuando se selecciona un elemento
        itemSeleccionadoProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mensajeErrorProperty.set("");
            }
        });
    }
    
    /**
     * Configura el ViewModel con los datos iniciales.
     * 
     * @param titulo El título del modal
     * @param descripcion La descripción del modal
     * @param items La lista de elementos seleccionables
     */
    public void configurar(String titulo, String descripcion, List<SelectableItem> items) {
        logger.debug("Configurando ViewModel - Título: {}, Items: {}", titulo, items.size());
        
        tituloProperty.set(titulo);
        descripcionProperty.set(descripcion);
        
        itemsProperty.clear();
        if (items != null) {
            itemsProperty.addAll(items);
        }
        
        // Limpiar selección previa
        itemSeleccionadoProperty.set(null);
        mensajeErrorProperty.set("");
        
        logger.info("ViewModel configurado con {} elementos", itemsProperty.size());
    }
    
    /**
     * Selecciona un elemento específico.
     * 
     * @param item El elemento a seleccionar
     */
    public void seleccionarItem(SelectableItem item) {
        if (item == null) {
            logger.warn("Intento de seleccionar un elemento null");
            return;
        }
        
        logger.debug("Seleccionando elemento: {}", item.getTitle());
        itemSeleccionadoProperty.set(item);
    }
    
    /**
     * Deselecciona el elemento actualmente seleccionado.
     */
    public void deseleccionarItem() {
        logger.debug("Deseleccionando elemento actual");
        itemSeleccionadoProperty.set(null);
    }
    
    /**
     * Confirma la selección actual.
     * 
     * @return El elemento seleccionado, o null si no hay selección
     */
    public SelectableItem confirmarSeleccion() {
        SelectableItem seleccion = itemSeleccionadoProperty.get();
        if (seleccion != null) {
            logger.info("Selección confirmada: {}", seleccion.getTitle());
        } else {
            logger.warn("Intento de confirmar selección sin elemento seleccionado");
        }
        return seleccion;
    }
    
    /**
     * Cancela la selección y limpia el estado.
     */
    public void cancelarSeleccion() {
        logger.debug("Cancelando selección");
        itemSeleccionadoProperty.set(null);
        mensajeErrorProperty.set("");
    }
    
    /**
     * Establece un mensaje de error.
     * 
     * @param mensaje El mensaje de error
     */
    public void setMensajeError(String mensaje) {
        logger.warn("Mensaje de error establecido: {}", mensaje);
        mensajeErrorProperty.set(mensaje);
    }
    
    /**
     * Limpia el mensaje de error.
     */
    public void limpiarMensajeError() {
        mensajeErrorProperty.set("");
    }
    
    /**
     * Establece el estado de carga.
     * 
     * @param cargando true si está cargando, false en caso contrario
     */
    public void setCargando(boolean cargando) {
        cargandoProperty.set(cargando);
    }
    
    // Getters para las propiedades observables
    
    public StringProperty tituloProperty() {
        return tituloProperty;
    }
    
    public StringProperty descripcionProperty() {
        return descripcionProperty;
    }
    
    public ObservableList<SelectableItem> itemsProperty() {
        return itemsProperty;
    }
    
    public ObjectProperty<SelectableItem> itemSeleccionadoProperty() {
        return itemSeleccionadoProperty;
    }
    
    public BooleanProperty itemValidoProperty() {
        return itemValidoProperty;
    }
    
    public BooleanProperty cargandoProperty() {
        return cargandoProperty;
    }
    
    public StringProperty mensajeErrorProperty() {
        return mensajeErrorProperty;
    }
    
    // Getters para valores simples
    
    public String getTitulo() {
        return tituloProperty.get();
    }
    
    public String getDescripcion() {
        return descripcionProperty.get();
    }
    
    public List<SelectableItem> getItems() {
        return itemsProperty;
    }
    
    public SelectableItem getItemSeleccionado() {
        return itemSeleccionadoProperty.get();
    }
    
    public boolean isItemValido() {
        return itemValidoProperty.get();
    }
    
    public boolean isCargando() {
        return cargandoProperty.get();
    }
    
    public String getMensajeError() {
        return mensajeErrorProperty.get();
    }
} 