package com.kursor.modules;

import java.util.Map;
import javafx.scene.Node;
import com.kursor.domain.Pregunta;
import org.slf4j.LoggerFactory;

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
 * @version 1.0.0
 * @since 1.0.0
 * @see Pregunta
 */
public interface PreguntaModule {
    
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
     * Obtiene el tipo de pregunta que maneja este módulo.
     * 
     * @return Tipo de pregunta (ej: "test", "flashcard", "completar_huecos", "truefalse")
     */
    String getQuestionType();
    
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
     * Crea una pregunta específica del tipo que maneja este módulo.
     * 
     * @param questionId Identificador de la pregunta a crear
     * @return Pregunta creada o null si no se puede crear
     */
    default Pregunta createQuestion(String questionId) {
        LoggerFactory.getLogger(getClass()).debug("Creando pregunta con ID: " + questionId);
        return null;
    }
    
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
     * Valida la respuesta del usuario para una pregunta específica.
     * 
     * @param pregunta La pregunta a validar
     * @param answer La respuesta del usuario (puede ser de cualquier tipo)
     * @return true si la respuesta es correcta, false en caso contrario
     * @throws IllegalArgumentException si el tipo de pregunta no es compatible con este módulo
     */
    boolean validateAnswer(Pregunta pregunta, Object answer);
} 