package com.kursor.factory;

import com.kursor.domain.Pregunta;
import com.kursor.modules.PreguntaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kursor.shared.util.ModuleManager;

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
 *   <li><strong>Logging detallado:</strong> Registra todo el proceso de creación para debugging</li>
 * </ul>
 * 
 * <p>Proceso de creación de preguntas:</p>
 * <ol>
 *   <li>Validación de datos de entrada</li>
 *   <li>Extracción del tipo de pregunta</li>
 *   <li>Búsqueda del módulo correspondiente</li>
 *   <li>Delegación de la creación al módulo</li>
 *   <li>Validación del resultado</li>
 * </ol>
 * 
 * <p>Tipos de preguntas soportados:</p>
 * <ul>
 *   <li><strong>test:</strong> Preguntas de opción múltiple</li>
 *   <li><strong>truefalse:</strong> Preguntas de verdadero/falso</li>
 *   <li><strong>completar_huecos:</strong> Preguntas de completar espacios</li>
 *   <li><strong>flashcard:</strong> Tarjetas de memoria</li>
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
     *   <li>Validar que los datos no sean null</li>
     *   <li>Extraer y validar el tipo de pregunta</li>
     *   <li>Buscar el módulo correspondiente en el ModuleManager</li>
     *   <li>Delegar la creación al módulo específico</li>
     *   <li>Validar que la pregunta se creó correctamente</li>
     *   <li>Registrar el resultado en el log</li>
     * </ol>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Datos de entrada no null</li>
     *   <li>Tipo de pregunta presente y no vacío</li>
     *   <li>Módulo disponible para el tipo</li>
     *   <li>Pregunta creada exitosamente</li>
     * </ul>
     * 
     * @param datos Mapa con los datos YAML de la pregunta. Debe contener al menos
     *              los campos "id" y "tipo". Los demás campos dependen del tipo de pregunta.
     * @return Pregunta creada según el tipo especificado, nunca null
     * @throws IllegalArgumentException si los datos son inválidos o no se encuentra el módulo
     * @throws RuntimeException si hay un error durante la creación de la pregunta
     * 
     * @see ModuleManager#findModuleByQuestionType(String)
     * @see PreguntaModule#parsePregunta(Map)
     */
    public static Pregunta crearPregunta(Map<String, Object> datos) {
        logger.debug("🚀 Iniciando creación de pregunta desde datos YAML");
        logger.debug("📋 Datos recibidos: {}", datos);
        
        // Validar datos de entrada
        if (datos == null) {
            logger.error("❌ Error al crear pregunta: datos no pueden ser null");
            throw new IllegalArgumentException("Datos de pregunta no pueden ser null");
        }
        
        logger.debug("✅ Datos de entrada validados correctamente");
        
        // Extraer tipo de pregunta
        String tipo = (String) datos.get("tipo");
        logger.debug("🔍 Extrayendo tipo de pregunta: '{}'", tipo);
        
        if (tipo == null || tipo.trim().isEmpty()) {
            logger.error("❌ Error al crear pregunta: tipo no puede ser null o vacío");
            throw new IllegalArgumentException("Tipo de pregunta no puede ser null o vacío");
        }
        
        String tipoNormalizado = tipo.trim();
        logger.debug("✅ Tipo de pregunta normalizado: '{}'", tipoNormalizado);
        
        // Buscar módulo correspondiente
        logger.debug("🔍 Buscando módulo para tipo: '{}'", tipoNormalizado);
        PreguntaModule modulo = ModuleManager.getInstance().findModuleByQuestionType(tipoNormalizado);
        
        if (modulo == null) {
            logger.error("❌ No se encontró módulo para el tipo de pregunta: '{}'", tipoNormalizado);
            logger.debug("📋 Tipos disponibles: {}", ModuleManager.getInstance().getModules().stream()
                    .map(PreguntaModule::getQuestionType)
                    .toList());
            throw new IllegalArgumentException("No se encontró módulo para el tipo: " + tipoNormalizado);
        }
        
        logger.debug("✅ Módulo encontrado para tipo '{}': {}", tipoNormalizado, modulo.getModuleName());
        logger.debug("📝 Descripción del módulo: {}", modulo.getModuleDescription());
        
        try {
            // Delegar creación al módulo
            logger.debug("🔄 Delegando creación de pregunta al módulo: {}", modulo.getModuleName());
            Pregunta pregunta = modulo.parsePregunta(datos);
            
            if (pregunta == null) {
                logger.error("❌ El módulo {} retornó null para tipo: {}", modulo.getModuleName(), tipoNormalizado);
                throw new RuntimeException("El módulo no pudo crear la pregunta");
            }
            
            logger.debug("✅ Pregunta creada exitosamente por el módulo");
            logger.debug("📊 Información de la pregunta creada:");
            logger.debug("   - ID: {}", pregunta.getId());
            logger.debug("   - Tipo: {}", pregunta.getTipo());
            logger.debug("   - Clase: {}", pregunta.getClass().getSimpleName());
            
            logger.info("🎉 Pregunta creada exitosamente - Tipo: {}, ID: {}", tipoNormalizado, pregunta.getId());
            return pregunta;
            
        } catch (Exception e) {
            logger.error("❌ Error al crear pregunta de tipo '{}': {}", tipoNormalizado, e.getMessage());
            logger.debug("🔍 Stack trace completo:", e);
            throw new RuntimeException("Error al crear pregunta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si existe un módulo para un tipo de pregunta específico.
     * 
     * <p>Este método consulta el ModuleManager para verificar si existe
     * un módulo registrado que pueda manejar el tipo de pregunta especificado.</p>
     * 
     * @param tipo Tipo de pregunta a verificar (puede ser null)
     * @return true si existe un módulo para el tipo, false en caso contrario
     * 
     * @see ModuleManager#findModuleByQuestionType(String)
     */
    public static boolean existeModuloParaTipo(String tipo) {
        logger.debug("🔍 Verificando existencia de módulo para tipo: '{}'", tipo);
        
        if (tipo == null || tipo.trim().isEmpty()) {
            logger.debug("❌ Tipo inválido o vacío, no existe módulo");
            return false;
        }
        
        String tipoNormalizado = tipo.trim();
        PreguntaModule modulo = ModuleManager.getInstance().findModuleByQuestionType(tipoNormalizado);
        boolean existe = modulo != null;
        
        logger.debug("✅ Resultado de verificación: {} para tipo '{}'", existe, tipoNormalizado);
        if (existe) {
            logger.debug("📝 Módulo encontrado: {}", modulo.getModuleName());
        }
        
        return existe;
    }
    
    /**
     * Obtiene la lista de tipos de preguntas soportados.
     * 
     * <p>Este método consulta todos los módulos registrados y retorna
     * un array con los tipos de preguntas que pueden ser manejados.</p>
     * 
     * @return Array con los tipos de preguntas disponibles, nunca null
     * 
     * @see ModuleManager#getModules()
     * @see PreguntaModule#getQuestionType()
     */
    public static String[] getTiposSoportados() {
        logger.debug("🔍 Obteniendo lista de tipos de preguntas soportados");
        
        String[] tipos = ModuleManager.getInstance().getModules().stream()
                .map(PreguntaModule::getQuestionType)
                .toArray(String[]::new);
        
        logger.debug("✅ Tipos soportados encontrados: {}", tipos.length);
        for (String tipo : tipos) {
            logger.debug("   - {}", tipo);
        }
        
        return tipos;
    }
} 
