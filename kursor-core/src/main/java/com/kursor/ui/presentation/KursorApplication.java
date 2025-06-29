package com.kursor.ui.presentation;

import com.kursor.ui.presentation.main.MainController;
import com.kursor.util.CursoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal de la aplicación Kursor con nueva arquitectura.
 * 
 * <p>Esta clase actúa como punto de entrada para la aplicación utilizando
 * la nueva arquitectura MVC mejorada. Se encarga de:</p>
 * <ul>
 *   <li>Inicializar las dependencias principales</li>
 *   <li>Configurar el aspecto visual de la aplicación</li>
 *   <li>Crear y configurar el controlador principal</li>
 *   <li>Manejar errores de inicialización</li>
 *   <li>Proporcionar logging detallado del proceso de inicio</li>
 * </ul>
 * 
 * <p>La nueva arquitectura sigue los principios de Clean Architecture:</p>
 * <ul>
 *   <li>Separación clara de responsabilidades</li>
 *   <li>Inversión de dependencias</li>
 *   <li>Independencia de frameworks</li>
 *   <li>Testabilidad mejorada</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see MainController
 * @see CursoManager
 */
public class KursorApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(KursorApplication.class);
    
    // Constantes de configuración
    private static final String APPLICATION_NAME = "Kursor";
    private static final String APPLICATION_VERSION = "1.0.0";
    private static final String LOOK_AND_FEEL_CLASS = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    
    /**
     * Método principal de la aplicación.
     * 
     * <p>Este método inicia la aplicación con la nueva arquitectura MVC mejorada.
     * Se ejecuta en el Event Dispatch Thread para garantizar la correcta
     * inicialización de la interfaz gráfica.</p>
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        logger.info("Iniciando aplicación Kursor v{}", APPLICATION_VERSION);
        
        try {
            // Configurar el aspecto visual de la aplicación
            setupLookAndFeel();
            
            // Inicializar la aplicación en el EDT
            SwingUtilities.invokeAndWait(() -> {
                try {
                    initializeApplication();
                } catch (Exception e) {
                    logger.error("Error durante la inicialización de la aplicación: {}", e.getMessage(), e);
                    showFatalError("Error al inicializar la aplicación", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error fatal durante el inicio de la aplicación: {}", e.getMessage(), e);
            showFatalError("Error fatal durante el inicio", e);
        }
    }
    
    /**
     * Configura el aspecto visual de la aplicación.
     * 
     * <p>Este método establece el Look and Feel de la aplicación para
     * proporcionar una interfaz moderna y consistente.</p>
     */
    private static void setupLookAndFeel() {
        logger.debug("Configurando Look and Feel de la aplicación");
        
        try {
            // Intentar usar Nimbus Look and Feel
            UIManager.setLookAndFeel(LOOK_AND_FEEL_CLASS);
            logger.info("Look and Feel configurado: {}", LOOK_AND_FEEL_CLASS);
            
        } catch (Exception e) {
            logger.warn("No se pudo configurar Nimbus Look and Feel: {}", e.getMessage());
            
            try {
                // Fallback al Look and Feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                logger.info("Usando Look and Feel del sistema");
                
            } catch (Exception fallbackException) {
                logger.warn("No se pudo configurar Look and Feel del sistema: {}", fallbackException.getMessage());
                logger.info("Usando Look and Feel por defecto");
            }
        }
        
        // Configurar propiedades globales de la UI
        setupUIGlobalProperties();
    }
    
    /**
     * Configura propiedades globales de la interfaz de usuario.
     */
    private static void setupUIGlobalProperties() {
        logger.debug("Configurando propiedades globales de la UI");
        
        try {
            // Configurar fuente por defecto
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 12);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("List.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            
            // Configurar colores por defecto
            UIManager.put("Button.background", new Color(52, 152, 219));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focus", false);
            
            logger.debug("Propiedades globales de la UI configuradas");
            
        } catch (Exception e) {
            logger.warn("Error al configurar propiedades globales de la UI: {}", e.getMessage());
        }
    }
    
    /**
     * Inicializa la aplicación principal.
     * 
     * <p>Este método crea y configura todos los componentes necesarios
     * para el funcionamiento de la aplicación.</p>
     */
    private static void initializeApplication() {
        logger.info("Inicializando componentes de la aplicación");
        
        try {
            // Inicializar CursoManager
            CursoManager cursoManager = initializeCursoManager();
            
            // Crear controlador principal
            MainController mainController = new MainController(cursoManager);
            
            // Iniciar la aplicación
            mainController.start();
            
            logger.info("Aplicación inicializada correctamente");
            
        } catch (Exception e) {
            logger.error("Error durante la inicialización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar la aplicación", e);
        }
    }
    
    /**
     * Inicializa el CursoManager.
     * 
     * @return CursoManager inicializado
     * @throws Exception si hay un error durante la inicialización
     */
    private static CursoManager initializeCursoManager() throws Exception {
        logger.debug("Inicializando CursoManager");
        
        try {
            CursoManager cursoManager = CursoManager.getInstance();
            
            // Verificar que el CursoManager se inicializó correctamente
            if (cursoManager == null) {
                throw new Exception("CursoManager no se pudo inicializar");
            }
            
            logger.info("CursoManager inicializado correctamente");
            return cursoManager;
            
        } catch (Exception e) {
            logger.error("Error al inicializar CursoManager: {}", e.getMessage(), e);
            throw new Exception("Error al inicializar CursoManager", e);
        }
    }
    
    /**
     * Muestra un error fatal al usuario.
     * 
     * <p>Este método se utiliza para mostrar errores críticos que impiden
     * el funcionamiento normal de la aplicación.</p>
     * 
     * @param message Mensaje de error a mostrar
     * @param exception Excepción que causó el error
     */
    private static void showFatalError(String message, Exception exception) {
        logger.error("Error fatal: {} - {}", message, exception.getMessage(), exception);
        
        // Mostrar diálogo de error
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                message + "\n\nDetalles: " + exception.getMessage(),
                "Error Fatal - " + APPLICATION_NAME,
                JOptionPane.ERROR_MESSAGE
            );
            
            // Salir de la aplicación
            System.exit(1);
        });
    }
    
    /**
     * Obtiene el nombre de la aplicación.
     * 
     * @return Nombre de la aplicación
     */
    public static String getApplicationName() {
        return APPLICATION_NAME;
    }
    
    /**
     * Obtiene la versión de la aplicación.
     * 
     * @return Versión de la aplicación
     */
    public static String getApplicationVersion() {
        return APPLICATION_VERSION;
    }
    
    /**
     * Verifica si la aplicación está ejecutándose en modo debug.
     * 
     * @return true si está en modo debug, false en caso contrario
     */
    public static boolean isDebugMode() {
        return logger.isDebugEnabled();
    }
    
    /**
     * Obtiene información de la aplicación.
     * 
     * @return String con información de la aplicación
     */
    public static String getApplicationInfo() {
        return String.format("%s v%s - Nueva Arquitectura MVC", APPLICATION_NAME, APPLICATION_VERSION);
    }
} 