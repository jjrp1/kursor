package com.kursor.util;

import com.kursor.modules.PreguntaModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestor de módulos de preguntas para la aplicación Kursor.
 * 
 * <p>Esta clase se encarga de cargar dinámicamente todos los módulos de preguntas
 * desde archivos JAR ubicados en el directorio "modules". Utiliza el patrón
 * ServiceLoader para descubrir e instanciar implementaciones de {@link PreguntaModule}.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Carga automática de módulos desde el directorio modules/</li>
 *   <li>Validación exhaustiva de JARs antes de cargarlos</li>
 *   <li>Manejo robusto de errores de carga de módulos</li>
 *   <li>Lista inmutable de módulos cargados</li>
 *   <li>Búsqueda eficiente de módulos por tipo de pregunta</li>
 *   <li>Logging comprehensivo para debugging y monitoreo</li>
 * </ul>
 * 
 * <p><strong>Tipos de pregunta soportados:</strong></p>
 * <ul>
 *   <li>{@code test} - Preguntas de opción múltiple</li>
 *   <li>{@code flashcard} - Preguntas de flashcards</li>
 *   <li>{@code completar_huecos} - Preguntas de completar espacios</li>
 *   <li>{@code truefalse} - Preguntas verdadero/falso</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * ModuleManager manager = ModuleManager.getInstance();
 * List<PreguntaModule> modules = manager.getModules();
 * for (PreguntaModule module : modules) {
 *     System.out.println("Módulo cargado: " + module.getModuleName());
 * }
 * 
 * // Buscar módulo específico
 * PreguntaModule testModule = manager.findModuleByQuestionType("test");
 * if (testModule != null) {
 *     // Usar el módulo para crear preguntas
 * }
 * }</pre>
 * 
 * <p><strong>Thread Safety:</strong> Esta clase es thread-safe después de la inicialización.
 * El patrón Singleton garantiza que solo existe una instancia, y la lista de módulos
 * se inicializa una sola vez durante la construcción.</p>
 * 
 * <p><strong>Manejo de Errores:</strong> Los errores de carga de módulos individuales
 * no interrumpen la carga de otros módulos. Todos los errores se registran apropiadamente.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see PreguntaModule
 * @see ServiceLoader
 */
public class ModuleManager {
    
    /** Instancia única del gestor de módulos (Singleton) */
    private static ModuleManager instance;
    
    /** Logger para registrar eventos del gestor de módulos */
    private static final Logger logger = LoggerFactory.getLogger(ModuleManager.class);
    
    /** Lista de módulos de preguntas cargados exitosamente */
    private final List<PreguntaModule> modules;
    
    /** Directorio donde se buscan los módulos JAR */
    private static final String MODULES_DIR;
    
    /** Nombre del archivo de servicios esperado en los JARs */
    private static final String SERVICE_FILE = "META-INF/services/com.kursor.modules.PreguntaModule";
    
    // Inicializar la ruta de módulos dinámicamente
    static {
        logger.trace("Inicializando configuración estática de ModuleManager");
        String basePath = new File("").getAbsolutePath();
        logger.debug("Ruta base detectada: {}", basePath);
        
        // Si estamos en un subdirectorio del proyecto (kursor-core, kursor-ui, etc.)
        // subir un nivel para llegar a la raíz del proyecto
        if (basePath.endsWith("kursor-core")) {
            basePath = new File(basePath).getParent();
            logger.debug("Ajustando ruta base desde kursor-core a: {}", basePath);
        }
        
        MODULES_DIR = basePath + File.separator + "modules";
        logger.info("Directorio de módulos configurado: {}", MODULES_DIR);
    }

    /**
     * Constructor privado para implementar el patrón Singleton.
     * 
     * <p>Inicializa la lista de módulos e inmediatamente comienza
     * el proceso de carga de todos los módulos disponibles.</p>
     * 
     * @throws RuntimeException Si ocurre un error crítico durante la inicialización
     */
    private ModuleManager() {
        logger.debug("Creando nueva instancia de ModuleManager");
        this.modules = new ArrayList<>();
        logger.trace("Lista de módulos inicializada");
        
        try {
            cargarModulos();
            logger.info("ModuleManager inicializado exitosamente con {} módulos", modules.size());
        } catch (Exception e) {
            logger.error("Error crítico durante la inicialización de ModuleManager", e);
            throw new RuntimeException("No se pudo inicializar ModuleManager", e);
        }
    }

    /**
     * Obtiene la instancia única del gestor de módulos.
     * 
     * <p>Si la instancia no existe, se crea automáticamente y se cargan
     * todos los módulos disponibles. Este método es thread-safe.</p>
     * 
     * @return Instancia única del ModuleManager, nunca {@code null}
     * @throws RuntimeException Si hay errores durante la inicialización
     */
    public static synchronized ModuleManager getInstance() {
        logger.trace("Solicitando instancia de ModuleManager");
        
        if (instance == null) {
            logger.debug("Instancia de ModuleManager no existe, creando nueva");
            instance = new ModuleManager();
        } else {
            logger.trace("Retornando instancia existente de ModuleManager");
        }
        
        return instance;
    }

    /**
     * Carga todos los módulos JAR disponibles en el directorio modules/.
     * 
     * <p>Este método realiza las siguientes operaciones:</p>
     * <ol>
     *   <li>Verifica que el directorio modules/ existe</li>
     *   <li>Busca archivos JAR en el directorio</li>
     *   <li>Valida que cada JAR contenga implementaciones de PreguntaModule</li>
     *   <li>Carga dinámicamente los módulos usando ServiceLoader</li>
     *   <li>Registra errores de carga sin interrumpir la aplicación</li>
     * </ol>
     * 
     * <p><strong>Comportamiento ante errores:</strong> Si un módulo individual falla
     * al cargar, se registra el error y se continúa con los demás módulos.</p>
     * 
     * <p>Los módulos se cargan en el orden en que se encuentran los archivos JAR.</p>
     * 
     * @throws IllegalStateException Si el directorio modules no es accesible
     */
    private void cargarModulos() {
        logger.info("Iniciando carga de módulos desde directorio: {}", MODULES_DIR);
        
        File modulesDir = new File(MODULES_DIR);
        
        // Verificar existencia del directorio
        if (!modulesDir.exists()) {
            logger.warn("El directorio de módulos no existe: {}", MODULES_DIR);
            logger.debug("Intentando crear directorio de módulos");
            
            if (modulesDir.mkdirs()) {
                logger.info("Directorio de módulos creado: {}", MODULES_DIR);
            } else {
                logger.error("No se pudo crear el directorio de módulos: {}", MODULES_DIR);
            }
            return;
        }
        
        if (!modulesDir.isDirectory()) {
            logger.error("La ruta de módulos no es un directorio: {}", MODULES_DIR);
            throw new IllegalStateException("La ruta de módulos no es un directorio: " + MODULES_DIR);
        }
        
        if (!modulesDir.canRead()) {
            logger.error("No se tienen permisos de lectura en el directorio: {}", MODULES_DIR);
            throw new IllegalStateException("Sin permisos de lectura en: " + MODULES_DIR);
        }

        logger.debug("Buscando archivos JAR en directorio: {}", MODULES_DIR);
        File[] jarFiles = modulesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        
        if (jarFiles == null) {
            logger.error("Error al listar archivos en el directorio: {}", MODULES_DIR);
            return;
        }
        
        if (jarFiles.length == 0) {
            logger.warn("No se encontraron archivos JAR en el directorio: {}", MODULES_DIR);
            return;
        }

        logger.info("Encontrados {} archivos JAR para procesar", jarFiles.length);
        
        int modulosExitosos = 0;
        int modulosFallidos = 0;
        
        for (File jarFile : jarFiles) {
            logger.debug("Procesando archivo JAR: {}", jarFile.getName());
            
            try {
                int modulosAnteriores = modules.size();
                cargarModulo(jarFile);
                int modulosNuevos = modules.size() - modulosAnteriores;
                
                if (modulosNuevos > 0) {
                    modulosExitosos += modulosNuevos;
                    logger.info("Carga exitosa de {} módulos desde: {}", modulosNuevos, jarFile.getName());
                } else {
                    logger.warn("No se cargaron módulos desde: {}", jarFile.getName());
                }
                
            } catch (Exception e) {
                modulosFallidos++;
                logger.error("Error al cargar el módulo {}: {}", jarFile.getName(), e.getMessage(), e);
            }
        }

        logger.info("Proceso de carga completado. Total módulos cargados: {}, Exitosos: {}, Fallidos: {}", 
                   modules.size(), modulosExitosos, modulosFallidos);
        
        if (modules.isEmpty()) {
            logger.warn("ATENCIÓN: No se cargó ningún módulo. La aplicación puede tener funcionalidad limitada");
        }
    }

    /**
     * Carga un módulo específico desde un archivo JAR.
     * 
     * <p>Este método realiza una validación exhaustiva del JAR antes de intentar
     * cargar los módulos. Utiliza un ClassLoader dedicado para aislar las clases
     * del módulo y evitar conflictos.</p>
     * 
     * <p><strong>Proceso de carga:</strong></p>
     * <ol>
     *   <li>Valida que el JAR contenga implementaciones de PreguntaModule</li>
     *   <li>Crea un URLClassLoader específico para el JAR</li>
     *   <li>Utiliza ServiceLoader para descubrir implementaciones</li>
     *   <li>Instancia y registra cada módulo encontrado</li>
     * </ol>
     * 
     * @param jarFile Archivo JAR del módulo a cargar, no debe ser {@code null}
     * @throws Exception Si hay errores durante la carga del módulo
     * @throws IllegalArgumentException Si el archivo JAR es {@code null} o no existe
     */
    private void cargarModulo(File jarFile) throws Exception {
        if (jarFile == null) {
            throw new IllegalArgumentException("El archivo JAR no puede ser null");
        }
        
        if (!jarFile.exists()) {
            throw new IllegalArgumentException("El archivo JAR no existe: " + jarFile.getAbsolutePath());
        }
        
        logger.debug("Iniciando carga de módulo desde: {} (tamaño: {} bytes)", 
                    jarFile.getName(), jarFile.length());

        // Verificar que el JAR contiene implementaciones de PreguntaModule
        if (!validarJar(jarFile)) {
            logger.warn("El JAR no contiene implementaciones válidas de PreguntaModule: {}", jarFile.getName());
            return;
        }

        logger.debug("JAR validado exitosamente, creando ClassLoader para: {}", jarFile.getName());
        
        // Crear class loader para el JAR
        URLClassLoader classLoader = null;
        try {
            classLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL()},
                getClass().getClassLoader()
            );
            
            logger.trace("ClassLoader creado para: {}", jarFile.getName());

            // Cargar módulos usando ServiceLoader
            logger.debug("Iniciando ServiceLoader para: {}", jarFile.getName());
            ServiceLoader<PreguntaModule> serviceLoader = ServiceLoader.load(PreguntaModule.class, classLoader);
            
            int modulosCargados = 0;
            for (PreguntaModule module : serviceLoader) {
                try {
                    // Validar el módulo antes de agregarlo
                    if (validarModulo(module)) {
                        modules.add(module);
                        modulosCargados++;
                        logger.debug("Módulo cargado exitosamente: {} (tipo: {})", 
                                   module.getModuleName(), module.getQuestionType());
                    } else {
                        logger.warn("Módulo inválido descartado: {}", module.getClass().getName());
                    }
                } catch (Exception e) {
                    logger.error("Error al validar módulo {}: {}", module.getClass().getName(), e.getMessage(), e);
                }
            }

            if (modulosCargados == 0) {
                logger.warn("No se encontraron implementaciones válidas de PreguntaModule en: {}", jarFile.getName());
            } else {
                logger.info("Cargados {} módulos desde: {}", modulosCargados, jarFile.getName());
            }
            
        } finally {
            // Cerrar el ClassLoader para liberar recursos
            if (classLoader != null) {
                try {
                    classLoader.close();
                    logger.trace("ClassLoader cerrado para: {}", jarFile.getName());
                } catch (IOException e) {
                    logger.warn("Error al cerrar ClassLoader para {}: {}", jarFile.getName(), e.getMessage());
                }
            }
        }
    }

    /**
     * Valida que un módulo cargado es funcional y seguro.
     * 
     * <p>Realiza verificaciones básicas para asegurar que el módulo puede
     * funcionar correctamente en la aplicación.</p>
     * 
     * @param module Módulo a validar
     * @return {@code true} si el módulo es válido, {@code false} en caso contrario
     */
    private boolean validarModulo(PreguntaModule module) {
        if (module == null) {
            logger.debug("Módulo es null");
            return false;
        }
        
        try {
            String moduleName = module.getModuleName();
            String questionType = module.getQuestionType();
            
            if (moduleName == null || moduleName.trim().isEmpty()) {
                logger.warn("Módulo tiene nombre inválido: {}", moduleName);
                return false;
            }
            
            if (questionType == null || questionType.trim().isEmpty()) {
                logger.warn("Módulo {} tiene tipo de pregunta inválido: {}", moduleName, questionType);
                return false;
            }
            
            // Verificar si ya existe un módulo con el mismo tipo
            for (PreguntaModule existingModule : modules) {
                if (questionType.equals(existingModule.getQuestionType())) {
                    logger.warn("Ya existe un módulo para el tipo '{}': {}. Módulo {} será descartado",
                              questionType, existingModule.getModuleName(), moduleName);
                    return false;
                }
            }
            
            logger.trace("Módulo validado exitosamente: {} (tipo: {})", moduleName, questionType);
            return true;
            
        } catch (Exception e) {
            logger.error("Error durante validación de módulo {}: {}", module.getClass().getName(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Valida que un archivo JAR contenga implementaciones de PreguntaModule.
     * 
     * <p>Realiza las siguientes verificaciones:</p>
     * <ul>
     *   <li>Existe el archivo de servicios META-INF/services/com.kursor.modules.PreguntaModule</li>
     *   <li>El archivo de servicios contiene referencias a clases válidas</li>
     *   <li>El JAR contiene archivos .class (no está vacío)</li>
     *   <li>El archivo de servicios está correctamente codificado</li>
     * </ul>
     * 
     * @param jarFile Archivo JAR a validar, no debe ser {@code null}
     * @return {@code true} si el JAR es válido, {@code false} en caso contrario
     * @throws IllegalArgumentException Si jarFile es {@code null}
     */
    private boolean validarJar(File jarFile) {
        if (jarFile == null) {
            throw new IllegalArgumentException("El archivo JAR no puede ser null");
        }
        
        logger.debug("Iniciando validación de JAR: {} (tamaño: {} bytes)", 
                    jarFile.getName(), jarFile.length());
        
        try (JarFile jar = new JarFile(jarFile)) {
            logger.trace("JAR abierto exitosamente: {}", jarFile.getName());
            
            // Verificar archivo de servicios
            logger.debug("Buscando archivo de servicios: {}", SERVICE_FILE);
            JarEntry serviceEntry = jar.getJarEntry(SERVICE_FILE);
            
            if (serviceEntry == null) {
                logger.warn("No se encontró el archivo de servicio {} en {}", SERVICE_FILE, jarFile.getName());
                return false;
            }
            
            logger.trace("Archivo de servicios encontrado: {} (tamaño: {} bytes)", 
                        SERVICE_FILE, serviceEntry.getSize());

            // Validar contenido del archivo de servicios
            if (!validarArchivoServicios(jar, serviceEntry, jarFile.getName())) {
                return false;
            }

            // Verificar si hay clases que implementen PreguntaModule
            logger.debug("Verificando presencia de clases de implementación");
            int classCount = 0;
            for (JarEntry entry : Collections.list(jar.entries())) {
                String entryName = entry.getName();
                if (entryName.endsWith(".class") && !entryName.contains("$")) {
                    classCount++;
                    logger.trace("Clase encontrada: {}", entryName);
                }
            }

            if (classCount == 0) {
                logger.error("El JAR {} no contiene clases de implementación", jarFile.getName());
                return false;
            }
            
            logger.debug("JAR {} validado exitosamente: {} clases encontradas", jarFile.getName(), classCount);
            return true;
            
        } catch (IOException e) {
            logger.error("Error de E/O al validar el JAR {}: {}", jarFile.getName(), e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al validar el JAR {}: {}", jarFile.getName(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Valida el contenido del archivo de servicios dentro del JAR.
     * 
     * @param jar Archivo JAR abierto
     * @param serviceEntry Entrada del archivo de servicios
     * @param jarName Nombre del JAR (para logging)
     * @return {@code true} si el archivo es válido, {@code false} en caso contrario
     */
    private boolean validarArchivoServicios(JarFile jar, JarEntry serviceEntry, String jarName) {
        try (InputStream is = jar.getInputStream(serviceEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            logger.trace("Leyendo contenido del archivo de servicios en {}", jarName);
            
            String line;
            int lineCount = 0;
            boolean hasValidEntries = false;
            
            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();
                
                // Ignorar líneas vacías y comentarios
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Verificar que la línea parece un nombre de clase válido
                if (line.matches("[a-zA-Z_$][a-zA-Z\\d_$]*(?:\\.[a-zA-Z_$][a-zA-Z\\d_$]*)*")) {
                    hasValidEntries = true;
                    logger.trace("Entrada válida encontrada en línea {}: {}", lineCount, line);
                } else {
                    logger.warn("Entrada inválida en línea {} del archivo de servicios en {}: {}", 
                              lineCount, jarName, line);
                }
            }
            
            if (!hasValidEntries) {
                logger.warn("No se encontraron entradas válidas en el archivo de servicios de {}", jarName);
                return false;
            }
            
            logger.debug("Archivo de servicios validado: {} líneas procesadas, entradas válidas encontradas", lineCount);
            return true;
            
        } catch (IOException e) {
            logger.error("Error al leer archivo de servicios en {}: {}", jarName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Obtiene la lista de todos los módulos cargados.
     * 
     * <p>La lista devuelta es inmutable para evitar modificaciones externas
     * que podrían causar inconsistencias en el estado del gestor.</p>
     * 
     * <p><strong>Nota:</strong> Esta operación es thread-safe y puede ser llamada
     * concurrentemente desde múltiples hilos.</p>
     * 
     * @return Lista inmutable de todos los módulos de preguntas cargados.
     *         Nunca retorna {@code null}, pero puede retornar una lista vacía
     *         si no se cargaron módulos exitosamente.
     */
    public List<PreguntaModule> getModules() {
        logger.trace("Solicitando lista de módulos cargados (total: {})", modules.size());
        return Collections.unmodifiableList(modules);
    }
    
    /**
     * Encuentra un módulo por su tipo de pregunta.
     * 
     * <p>Busca entre todos los módulos cargados aquel que maneje
     * el tipo de pregunta especificado. La búsqueda es case-sensitive.</p>
     * 
     * <p><strong>Tipos comunes soportados:</strong></p>
     * <ul>
     *   <li>{@code "test"} - Preguntas de opción múltiple</li>
     *   <li>{@code "flashcard"} - Preguntas de flashcards</li>
     *   <li>{@code "completar_huecos"} - Preguntas de completar espacios</li>
     *   <li>{@code "truefalse"} - Preguntas verdadero/falso</li>
     * </ul>
     * 
     * @param questionType Tipo de pregunta a buscar. No debe ser {@code null} ni vacío.
     * @return El módulo que maneja el tipo especificado, o {@code null} si no se encuentra
     * @throws IllegalArgumentException Si questionType es {@code null} o vacío
     */
    public PreguntaModule findModuleByQuestionType(String questionType) {
        if (questionType == null) {
            logger.warn("Búsqueda de módulo con tipo null");
            throw new IllegalArgumentException("El tipo de pregunta no puede ser null");
        }
        
        if (questionType.trim().isEmpty()) {
            logger.warn("Búsqueda de módulo con tipo vacío");
            throw new IllegalArgumentException("El tipo de pregunta no puede estar vacío");
        }
        
        logger.debug("Buscando módulo para tipo de pregunta: '{}'", questionType);
        
        for (PreguntaModule module : modules) {
            try {
                String moduleType = module.getQuestionType();
                if (questionType.equals(moduleType)) {
                    logger.debug("Módulo encontrado para tipo '{}': {}", questionType, module.getModuleName());
                    return module;
                }
            } catch (Exception e) {
                logger.warn("Error al obtener tipo de pregunta del módulo {}: {}", 
                          module.getClass().getName(), e.getMessage());
            }
        }
        
        logger.warn("No se encontró módulo para el tipo de pregunta: '{}'", questionType);
        logger.debug("Módulos disponibles: {}", 
                    modules.stream()
                           .map(m -> {
                               try {
                                   return m.getQuestionType();
                               } catch (Exception e) {
                                   return "ERROR";
                               }
                           })
                           .toList());
        
        return null;
    }
    
    /**
     * Obtiene el número total de módulos cargados exitosamente.
     * 
     * <p>Este método es útil para verificar si el sistema tiene módulos
     * disponibles para procesar preguntas.</p>
     * 
     * @return Número de módulos cargados. Puede ser 0 si no se cargaron módulos exitosamente.
     */
    public int getModuleCount() {
        logger.trace("Solicitando conteo de módulos: {}", modules.size());
        return modules.size();
    }
    
    /**
     * Verifica si hay módulos cargados en el sistema.
     * 
     * <p>Método de conveniencia equivalente a {@code getModuleCount() > 0}.</p>
     * 
     * @return {@code true} si hay al menos un módulo cargado, {@code false} en caso contrario
     */
    public boolean hasModules() {
        boolean hasModules = !modules.isEmpty();
        logger.trace("Verificando existencia de módulos: {}", hasModules);
        return hasModules;
    }

    /**
     * Obtiene información detallada sobre todos los módulos cargados.
     * 
     * <p>Útil para debugging y diagnóstico del sistema.</p>
     * 
     * @return String con información detallada de todos los módulos
     */
    public String getModulesInfo() {
        logger.debug("Generando información detallada de módulos");
        
        if (modules.isEmpty()) {
            return "No hay módulos cargados";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Módulos cargados (").append(modules.size()).append("):\n");
        
        for (int i = 0; i < modules.size(); i++) {
            PreguntaModule module = modules.get(i);
            try {
                info.append(String.format("%d. %s (tipo: %s) - %s\n", 
                                        i + 1, 
                                        module.getModuleName(), 
                                        module.getQuestionType(),
                                        module.getClass().getName()));
            } catch (Exception e) {
                info.append(String.format("%d. ERROR al obtener info: %s\n", 
                                        i + 1, e.getMessage()));
            }
        }
        
        return info.toString();
    }
} 