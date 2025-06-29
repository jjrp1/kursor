package com.kursor.ui.session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * Modelo de vista para la gestión de datos de sesiones.
 * 
 * <p>Esta clase actúa como intermediario entre el controlador y la vista,
 * proporcionando una lista observable de datos de sesiones que se actualiza
 * automáticamente cuando cambian los datos subyacentes.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Lista observable para actualizaciones automáticas de UI</li>
 *   <li>Clase interna SessionData para encapsular datos</li>
 *   <li>Métodos de acceso thread-safe</li>
 *   <li>Integración con JavaFX ObservableList</li>
 *   <li>Gestión de datos inmutables</li>
 * </ul>
 * 
 * <p>Patrón de diseño implementado:</p>
 * <ul>
 *   <li><strong>MVVM (Model-View-ViewModel):</strong> Separa la lógica de presentación</li>
 *   <li><strong>Observer Pattern:</strong> Notifica cambios automáticamente</li>
 *   <li><strong>Data Transfer Object:</strong> SessionData encapsula información</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SessionController
 * @see SessionTableView
 * @see SessionData
 */
public class SessionViewModel {
    private final ObservableList<SessionData> sessions = FXCollections.observableArrayList();

    /**
     * Establece la lista de sesiones en el modelo.
     * 
     * <p>Reemplaza completamente la lista actual con los nuevos datos.
     * La vista se actualizará automáticamente gracias a la lista observable.</p>
     * 
     * @param sessions La nueva lista de datos de sesiones
     */
    public void setSessions(List<SessionData> sessions) {
        this.sessions.setAll(sessions);
    }

    /**
     * Obtiene la lista observable de sesiones.
     * 
     * <p>Retorna la lista observable que puede ser utilizada directamente
     * por componentes JavaFX como TableView para mostrar los datos.</p>
     * 
     * @return La lista observable de datos de sesiones
     */
    public ObservableList<SessionData> getSessions() {
        return sessions;
    }

    /**
     * Clase de datos para representar información de una sesión.
     * 
     * <p>Esta clase encapsula los datos básicos de una sesión de aprendizaje
     * que se muestran en la tabla de historial. Los datos son inmutables
     * para garantizar consistencia y thread-safety.</p>
     * 
     * <p>Campos de datos:</p>
     * <ul>
     *   <li><strong>date:</strong> Fecha de la sesión (formato: YYYY-MM-DD)</li>
     *   <li><strong>block:</strong> Nombre del bloque de contenido</li>
     *   <li><strong>correct:</strong> Respuestas correctas en formato "X/Y"</li>
     *   <li><strong>pending:</strong> Número de preguntas pendientes</li>
     * </ul>
     * 
     * <p>Características de diseño:</p>
     * <ul>
     *   <li><strong>Inmutabilidad:</strong> Todos los campos son final</li>
     *   <li><strong>Encapsulación:</strong> Acceso controlado a través de getters</li>
     *   <li><strong>Simplicidad:</strong> Solo contiene datos, sin lógica de negocio</li>
     * </ul>
     * 
     * @author Juan José Ruiz Pérez <jjrp1@um.es>
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class SessionData {
        private final String date;
        private final String block;
        private final String correct;
        private final String pending;

        /**
         * Constructor para crear un objeto de datos de sesión.
         * 
         * <p>Inicializa todos los campos con los valores proporcionados.
         * Los datos se almacenan como strings para facilitar la presentación
         * en la interfaz de usuario.</p>
         * 
         * @param date Fecha de la sesión (formato: YYYY-MM-DD)
         * @param block Nombre del bloque de contenido
         * @param correct Respuestas correctas (formato: "X/Y")
         * @param pending Número de preguntas pendientes
         */
        public SessionData(String date, String block, String correct, String pending) {
            this.date = date;
            this.block = block;
            this.correct = correct;
            this.pending = pending;
        }

        /**
         * Obtiene la fecha de la sesión.
         * 
         * @return La fecha en formato YYYY-MM-DD
         */
        public String getDate() { return date; }
        
        /**
         * Obtiene el nombre del bloque.
         * 
         * @return El nombre del bloque de contenido
         */
        public String getBlock() { return block; }
        
        /**
         * Obtiene las respuestas correctas.
         * 
         * @return Las respuestas correctas en formato "X/Y"
         */
        public String getCorrect() { return correct; }
        
        /**
         * Obtiene las preguntas pendientes.
         * 
         * @return El número de preguntas pendientes como string
         */
        public String getPending() { return pending; }
    }
} 