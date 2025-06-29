package com.kursor.factory;

import com.kursor.domain.Pregunta;
import com.kursor.modules.PreguntaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kursor.util.ModuleManager;

import java.util.Map;

/**
 * Factory para crear instancias de preguntas de diferentes tipos.
 * 
 * <p>Esta clase implementa el patrón Factory para instanciar dinámicamente
 * diferentes tipos de preguntas basándose en el tipo especificado. Utiliza
 * el ModuleManager para encontrar el módulo correspondiente y delegar la
 * creación de la pregunta específica.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Carga dinámica:</strong> Utiliza ServiceLoader para detectar módulos</li>
 *   <li><strong>Desacoplamiento:</strong> El core no conoce implementaciones específicas</li>
 *   <li><strong>Extensibilidad:</strong> Nuevos tipos se pueden agregar sin modificar el core</li>
 *   <li><strong>Validación:</strong> Verifica que el tipo sea válido antes de crear</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * // Crear pregunta desde datos YAML
 * Map<String, Object> datos = Map.of(
 *     "id", "p1",
 *     "tipo", "test",
 *     "enunciado", "¿Cuál es la capital de España?",
 *     "opciones", List.of("Madrid", "Barcelona", "Valencia"),
 *     "respuestaCorrecta", "Madrid"
 * );
 * 
 * Pregunta pregunta = PreguntaFactory.crearPregunta(datos);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see ModuleManager
 * @see Pregunta
 */
public class PreguntaFactory {
    
    /** Logger para registrar eventos de la factory */
    private static final Logger logger = LoggerFactory.getLogger(PreguntaFactory.class);
    
    /**
     * Crea una pregunta a partir de datos YAML.
     * 
     * <p>Este método analiza los datos YAML para determinar el tipo de pregunta
     * y utiliza el módulo correspondiente para crear la instancia específica.
     * El método valida que los datos contengan la información necesaria y que
     * exista un módulo para el tipo especificado.</p>
     * 
     * <p>Proceso de creación:</p>
     * <ol>
     *   <li>Extraer el tipo de pregunta de los datos</li>
     *   <li>Buscar el módulo correspondiente</li>
     *   <li>Delegar la creación al módulo</li>
     *   <li>Validar que la pregunta se creó correctamente</li>
     * </ol>
     * 
     * @param datos Mapa con los datos YAML de la pregunta
     * @return Pregunta creada según el tipo especificado
     * @throws IllegalArgumentException si los datos son inválidos o no se encuentra el módulo
     * @throws RuntimeException si hay un error durante la creación
     */
    public static Pregunta crearPregunta(Map<String, Object> datos) {
        logger.debug("Creando pregunta desde datos YAML: " + datos);
        
        // Validar datos de entrada
        if (datos == null) {
            logger.error("Error al crear pregunta: datos no pueden ser null");
            throw new IllegalArgumentException("Datos de pregunta no pueden ser null");
        }
        
        // Extraer tipo de pregunta
        String tipo = (String) datos.get("tipo");
        if (tipo == null || tipo.trim().isEmpty()) {
            logger.error("Error al crear pregunta: tipo no puede ser null o vacío");
            throw new IllegalArgumentException("Tipo de pregunta no puede ser null o vacío");
        }
        
        // Buscar módulo correspondiente
        PreguntaModule modulo = ModuleManager.getInstance().findModuleByQuestionType(tipo.trim());
        if (modulo == null) {
            logger.error("No se encontró módulo para el tipo de pregunta: " + tipo);
            throw new IllegalArgumentException("No se encontró módulo para el tipo: " + tipo);
        }
        
        logger.debug("Módulo encontrado para tipo '" + tipo + "': " + modulo.getModuleName());
        
        try {
            // Delegar creación al módulo
            Pregunta pregunta = modulo.parsePregunta(datos);
            
            if (pregunta == null) {
                logger.error("El módulo " + modulo.getModuleName() + " retornó null para tipo: " + tipo);
                throw new RuntimeException("El módulo no pudo crear la pregunta");
            }
            
            logger.info("Pregunta creada exitosamente - Tipo: " + tipo + ", ID: " + pregunta.getId());
            return pregunta;
            
        } catch (Exception e) {
            logger.error("Error al crear pregunta de tipo '" + tipo + "': " + e.getMessage(), e);
            throw new RuntimeException("Error al crear pregunta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si existe un módulo para un tipo de pregunta específico.
     * 
     * @param tipo Tipo de pregunta a verificar
     * @return true si existe un módulo para el tipo, false en caso contrario
     */
    public static boolean existeModuloParaTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return false;
        }
        
        PreguntaModule modulo = ModuleManager.getInstance().findModuleByQuestionType(tipo.trim());
        return modulo != null;
    }
    
    /**
     * Obtiene la lista de tipos de preguntas soportados.
     * 
     * @return Array con los tipos de preguntas disponibles
     */
    public static String[] getTiposSoportados() {
        return ModuleManager.getInstance().getModules().stream()
                .map(PreguntaModule::getQuestionType)
                .toArray(String[]::new);
    }
} 