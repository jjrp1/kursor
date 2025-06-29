package com.kursor.presentation.controllers;

import com.kursor.presentation.dialogs.SelectableItem;
import com.kursor.presentation.viewmodels.CardSelectorViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controlador para el CardSelectorModal que maneja la lógica de negocio
 * para la selección de elementos genéricos.
 * 
 * <p>Este controlador es genérico y puede trabajar con cualquier tipo de
 * elementos que implementen la interfaz SelectableItem, como bloques,
 * estrategias, etc.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SelectableItem
 * @see CardSelectorViewModel
 * @see CardSelectorModal
 */
public class CardSelectorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CardSelectorController.class);
    
    private final CardSelectorViewModel viewModel;
    private final String titulo;
    private final String descripcion;
    
    /**
     * Constructor que crea un controlador con configuración inicial.
     * 
     * @param titulo El título del modal
     * @param descripcion La descripción del modal
     * @param items La lista de elementos seleccionables
     */
    public CardSelectorController(String titulo, String descripcion, List<SelectableItem> items) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.viewModel = new CardSelectorViewModel(titulo, descripcion, items);
        
        logger.info("CardSelectorController creado para: {} con {} elementos", titulo, items.size());
    }
    
    /**
     * Constructor que crea un controlador con ViewModel existente.
     * 
     * @param viewModel El ViewModel a usar
     */
    public CardSelectorController(CardSelectorViewModel viewModel) {
        this.viewModel = viewModel;
        this.titulo = viewModel.getTitulo();
        this.descripcion = viewModel.getDescripcion();
        
        logger.info("CardSelectorController creado con ViewModel existente para: {}", titulo);
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
        viewModel.seleccionarItem(item);
    }
    
    /**
     * Deselecciona el elemento actualmente seleccionado.
     */
    public void deseleccionarItem() {
        logger.debug("Deseleccionando elemento actual");
        viewModel.deseleccionarItem();
    }
    
    /**
     * Confirma la selección actual.
     * 
     * @return El elemento seleccionado, o null si no hay selección
     */
    public SelectableItem confirmarSeleccion() {
        SelectableItem seleccion = viewModel.confirmarSeleccion();
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
        viewModel.cancelarSeleccion();
    }
    
    /**
     * Obtiene el elemento actualmente seleccionado.
     * 
     * @return El elemento seleccionado, o null si no hay selección
     */
    public SelectableItem getItemSeleccionado() {
        return viewModel.getItemSeleccionado();
    }
    
    /**
     * Verifica si hay un elemento seleccionado válido.
     * 
     * @return true si hay un elemento seleccionado, false en caso contrario
     */
    public boolean isItemValido() {
        return viewModel.isItemValido();
    }
    
    /**
     * Obtiene el ViewModel asociado a este controlador.
     * 
     * @return El ViewModel
     */
    public CardSelectorViewModel getViewModel() {
        return viewModel;
    }
    
    /**
     * Obtiene el título del modal.
     * 
     * @return El título
     */
    public String getTitulo() {
        return titulo;
    }
    
    /**
     * Obtiene la descripción del modal.
     * 
     * @return La descripción
     */
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Obtiene la lista de elementos disponibles.
     * 
     * @return La lista de elementos
     */
    public List<SelectableItem> getItems() {
        return viewModel.getItems();
    }
    
    /**
     * Establece un mensaje de error.
     * 
     * @param mensaje El mensaje de error
     */
    public void setMensajeError(String mensaje) {
        viewModel.setMensajeError(mensaje);
    }
    
    /**
     * Limpia el mensaje de error.
     */
    public void limpiarMensajeError() {
        viewModel.limpiarMensajeError();
    }
    
    /**
     * Establece el estado de carga.
     * 
     * @param cargando true si está cargando, false en caso contrario
     */
    public void setCargando(boolean cargando) {
        viewModel.setCargando(cargando);
    }
} 