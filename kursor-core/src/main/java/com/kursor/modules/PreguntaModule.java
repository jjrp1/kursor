package com.kursor.modules;

import java.util.Map;
import javafx.scene.Node;
import com.kursor.domain.Pregunta;
import org.slf4j.LoggerFactory;
import com.kursor.ui.PreguntaEventListener;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Interfaz para módulos de preguntas que manejan tipos específicos de preguntas.
 * 
 * <p>Cada módulo es responsable de:</p>
 * <ul>
 *   <li>Gestionar preguntas de un tipo específico</li>
 *   <li>Crear la interfaz de usuario para sus preguntas</li>
 *   <li>Validar respuestas según su lógica específica</li>
 *   <li>Parsear datos YAML para crear preguntas</li>
 * </ul>
 * 
 * <p>Los módulos se cargan dinámicamente como plugins y deben registrarse
 * en el archivo META-INF/services/com.kursor.modules.PreguntaModule</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see Pregunta
 */
public interface PreguntaModule {
    
    /**
     * Obtiene el tipo de pregunta que maneja este módulo.
     * 
     * @return Tipo de pregunta (ej: "test", "flashcard", "completar_huecos", "truefalse")
     */
    String getQuestionType();
        
    /**
     * Obtiene el nombre del módulo para mostrar en la interfaz de usuario.
     * 
     * @return Nombre descriptivo del módulo
     */
    String getModuleName();
    
    /**
     * Obtiene la descripción del módulo.
     * 
     * @return Descripción detallada de la funcionalidad del módulo
     */
    String getModuleDescription();

    /**
     * Obtiene el icono representativo del módulo.
     * 
     * @return Emoji o símbolo que representa visualmente el tipo de pregunta
     */
    String getIcon();

    /**
     * Parsea datos YAML para crear una pregunta específica del tipo que maneja este módulo.
     * 
     * <p>Este método debe interpretar los datos YAML y crear una instancia de la clase
     * de pregunta correspondiente con todos sus atributos configurados.</p>
     * 
     * @param preguntaData Mapa con los datos YAML de la pregunta
     * @return Pregunta creada a partir de los datos YAML
     * @throws IllegalArgumentException si los datos YAML no son válidos para este tipo de pregunta
     */
    Pregunta parsePregunta(Map<String, Object> preguntaData);
    
    /**
     * Crea la interfaz de usuario para una pregunta específica.
     * 
     * <p>Este método debe crear y configurar todos los controles JavaFX
     * necesarios para mostrar la pregunta al usuario.</p>
     * 
     * @param pregunta La pregunta para la cual crear la vista
     * @return Nodo JavaFX que representa la interfaz de la pregunta
     * @throws IllegalArgumentException si el tipo de pregunta no es compatible con este módulo
     */
    Node createQuestionView(Pregunta pregunta);
    
    /**
     * Configura la interfaz de usuario completa para una pregunta.
     * 
     * <p>Este método permite al módulo configurar todas las secciones de la UI:
     * cabecera (progreso), contenido (pregunta) y pie (botones). El módulo tiene
     * control total sobre la presentación y comportamiento de su tipo de pregunta.</p>
     * 
     * @param pregunta La pregunta para la cual configurar la UI
     * @param headerContainer Contenedor para la cabecera (progreso, título)
     * @param contentContainer Contenedor para el contenido principal (pregunta)
     * @param footerContainer Contenedor para el pie (botones, antes del botón "Terminar")
     * @param eventListener Listener para notificar eventos al contenedor principal
     */
    void configureCompleteUI(Pregunta pregunta, 
                           VBox headerContainer, 
                           VBox contentContainer, 
                           VBox footerContainer,
                           PreguntaEventListener eventListener);
    
    /**
     * Valida la respuesta del usuario para una pregunta específica.
     * 
     * <p>Este método debe implementar la lógica de validación específica
     * para el tipo de pregunta que maneja el módulo.</p>
     * 
     * @param pregunta La pregunta a validar
     * @param respuesta La respuesta del usuario
     * @return true si la respuesta es correcta, false en caso contrario
     */
    boolean validarRespuesta(Pregunta pregunta, Object respuesta);
    
    /**
     * Muestra el resultado de la validación en la UI.
     * 
     * <p>Este método debe actualizar la interfaz para mostrar si la respuesta
     * fue correcta o incorrecta, incluyendo la respuesta correcta si es necesario.</p>
     * 
     * @param pregunta La pregunta validada
     * @param esCorrecta Si la respuesta fue correcta
     * @param respuestaUsuario La respuesta que dio el usuario
     * @param contentContainer Contenedor donde mostrar el resultado
     */
    void mostrarResultado(Pregunta pregunta, 
                         boolean esCorrecta, 
                         Object respuestaUsuario,
                         VBox contentContainer);
    
    /**
     * Crea la interfaz de usuario para una pregunta específica en el contexto de curso.
     * 
     * <p>Este método crea una interfaz optimizada para la ejecución de cursos,
     * diferente de la vista de prueba que se usa en la pestaña de módulos.</p>
     * 
     * @param pregunta La pregunta para la cual crear la vista
     * @return Nodo JavaFX que representa la interfaz de la pregunta para curso
     * @throws IllegalArgumentException si el tipo de pregunta no es compatible con este módulo
     */
    default Node createQuestionUI(Pregunta pregunta) {
        // Por defecto, usar la vista estándar
        return createQuestionView(pregunta);
    }
    
    /**
     * Verifica si este tipo de pregunta requiere validación antes de continuar.
     * 
     * <p>Algunos tipos de preguntas como flashcards no requieren validación
     * y pueden avanzar directamente al siguiente.</p>
     * 
     * @return true si requiere validación, false si puede avanzar directamente
     */
    default boolean requiresValidation() {
        // Por defecto, todas las preguntas requieren validación
        return true;
    }
} 