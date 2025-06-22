package com.kursor.util;

import com.kursor.strategy.EstrategiaModule;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
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
 * Gestor de estrategias de aprendizaje para la aplicación Kursor.
 * 
 * <p>Esta clase se encarga de cargar dinámicamente todas las estrategias de aprendizaje
 * desde archivos JAR ubicados en el directorio "strategies". Utiliza el patrón
 * ServiceLoader para descubrir e instanciar implementaciones de {@link EstrategiaModule}.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Carga automática de estrategias desde el directorio strategies/</li>
 *   <li>Validación exhaustiva de JARs antes de cargarlos</li>
 *   <li>Manejo robusto de errores de carga de estrategias</li>
 *   <li>Lista inmutable de estrategias cargadas</li>
 *   <li>Búsqueda eficiente de estrategias por nombre</li>
 *   <li>Logging comprehensivo para debugging y monitoreo</li>
 * </ul>
 * 
 * <p><strong>Estrategias soportadas:</strong></p>
 * <ul>
 *   <li><strong>Secuencial:</strong> Preguntas en orden secuencial</li>
 *   <li><strong>Aleatoria:</strong> Preguntas en orden aleatorio</li>
 *   <li><strong>Repetición Espaciada:</strong> Algoritmos de memoria optimizados</li>
 *   <li><strong>Repetir Incorrectas:</strong> Repite preguntas falladas al final</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * StrategyManager manager = StrategyManager.getInstance();
 * List<EstrategiaModule> strategies = manager.getStrategies();
 * for (EstrategiaModule strategy : strategies) {
 *     System.out.println("Estrategia cargada: " + strategy.getNombre());
 * }
 * 
 * // Crear estrategia específica
 * EstrategiaAprendizaje estrategia = manager.crearEstrategia("Secuencial", preguntas);
 * if (estrategia != null) {
 *     // Usar la estrategia para el aprendizaje
 * }
 * }</pre>
 * 
 * <p><strong>Thread Safety:</strong> Esta clase es thread-safe después de la inicialización.
 * El patrón Singleton garantiza que solo existe una instancia, y la lista de estrategias
 * se inicializa una sola vez durante la construcción.</p>
 * 
 * <p><strong>Manejo de Errores:</strong> Los errores de carga de estrategias individuales
 * no interrumpen la carga de otras estrategias. Todos los errores se registran apropiadamente.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaModule
 * @see EstrategiaAprendizaje
 * @see ServiceLoader
 */
public class StrategyManager {
    
    /** Instancia única del gestor de estrategias (Singleton) */
    private static StrategyManager instance;
    
    /** Logger para registrar eventos del gestor de estrategias */
    private static final Logger logger = LoggerFactory.getLogger(StrategyManager.class);
    
    /** Lista de estrategias de aprendizaje cargadas exitosamente */
    private final List<EstrategiaModule> strategies;
    
    /** Directorio donde se buscan las estrategias JAR */
    private static final String STRATEGIES_DIR;
    
    /** Nombre del archivo de servicios esperado en los JARs */
    private static final String SERVICE_FILE = "META-INF/services/com.kursor.strategy.EstrategiaModule";
    
    // Inicializar la ruta de estrategias dinámicamente
    static {
        logger.trace("Inicializando configuración estática de StrategyManager");
        String basePath = new File("").getAbsolutePath();
        logger.debug("Ruta base detectada: {}", basePath);
        
        // Si estamos en un subdirectorio del proyecto (kursor-core, kursor-ui, etc.)
        // subir un nivel para llegar a la raíz del proyecto
        if (basePath.endsWith("kursor-core")) {
            basePath = new File(basePath).getParent();
            logger.debug("Ajustando ruta base desde kursor-core a: {}", basePath);
        }
        
        STRATEGIES_DIR = basePath + File.separator + "strategies";
        logger.info("Directorio de estrategias configurado: {}", STRATEGIES_DIR);
    }

    /**
     * Constructor privado para implementar el patrón Singleton.
     * 
     * <p>Inicializa la lista de estrategias e inmediatamente comienza
     * el proceso de carga de todas las estrategias disponibles.</p>
     * 
     * @throws RuntimeException Si ocurre un error crítico durante la inicialización
     */
    private StrategyManager() {
        logger.debug("Creando nueva instancia de StrategyManager");
        this.strategies = new ArrayList<>();
        logger.trace("Lista de estrategias inicializada");
        
        try {
            cargarEstrategias();
            logger.info("StrategyManager inicializado exitosamente con {} estrategias", strategies.size());
        } catch (Exception e) {
            logger.error("Error crítico durante la inicialización de StrategyManager", e);
            throw new RuntimeException("No se pudo inicializar StrategyManager", e);
        }
    }

    /**
     * Obtiene la instancia única del gestor de estrategias.
     * 
     * <p>Si la instancia no existe, se crea automáticamente y se cargan
     * todas las estrategias disponibles. Este método es thread-safe.</p>
     * 
     * @return Instancia única del StrategyManager, nunca {@code null}
     * @throws RuntimeException Si hay errores durante la inicialización
     */
    public static synchronized StrategyManager getInstance() {
        logger.trace("Solicitando instancia de StrategyManager");
        
        if (instance == null) {
            logger.debug("Instancia de StrategyManager no existe, creando nueva");
            instance = new StrategyManager();
        } else {
            logger.trace("Retornando instancia existente de StrategyManager");
        }
        
        return instance;
    }

    /**
     * Carga todas las estrategias JAR disponibles en el directorio strategies/.
     * 
     * <p>Este método realiza las siguientes operaciones:</p>
     * <ol>
     *   <li>Verifica que el directorio strategies/ existe</li>
     *   <li>Busca archivos JAR en el directorio</li>
     *   <li>Valida que cada JAR contenga implementaciones de EstrategiaModule</li>
     *   <li>Carga dinámicamente las estrategias usando ServiceLoader</li>
     *   <li>Registra errores de carga sin interrumpir la aplicación</li>
     * </ol>
     * 
     * <p><strong>Comportamiento ante errores:</strong> Si una estrategia individual falla
     * al cargar, se registra el error y se continúa con las demás estrategias.</p>
     * 
     * <p>Las estrategias se cargan en el orden en que se encuentran los archivos JAR.</p>
     * 
     * @throws IllegalStateException Si el directorio strategies no es accesible
     */
    private void cargarEstrategias() {
        logger.info("Iniciando carga de estrategias desde directorio: {}", STRATEGIES_DIR);
        
        File strategiesDir = new File(STRATEGIES_DIR);
        
        // Verificar existencia del directorio
        if (!strategiesDir.exists()) {
            logger.warn("El directorio de estrategias no existe: {}", STRATEGIES_DIR);
            logger.debug("Intentando crear directorio de estrategias");
            
            if (strategiesDir.mkdirs()) {
                logger.info("Directorio de estrategias creado: {}", STRATEGIES_DIR);
            } else {
                logger.error("No se pudo crear el directorio de estrategias: {}", STRATEGIES_DIR);
            }
            return;
        }
        
        if (!strategiesDir.isDirectory()) {
            logger.error("La ruta de estrategias no es un directorio: {}", STRATEGIES_DIR);
            throw new IllegalStateException("La ruta de estrategias no es un directorio: " + STRATEGIES_DIR);
        }
        
        if (!strategiesDir.canRead()) {
            logger.error("No se tienen permisos de lectura en el directorio: {}", STRATEGIES_DIR);
            throw new IllegalStateException("Sin permisos de lectura en: " + STRATEGIES_DIR);
        }

        logger.debug("Buscando archivos JAR en directorio: {}", STRATEGIES_DIR);
        File[] jarFiles = strategiesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        
        if (jarFiles == null || jarFiles.length == 0) {
            logger.warn("No se encontraron archivos JAR en el directorio: {}", STRATEGIES_DIR);
            return;
        }
        
        logger.info("Encontrados {} archivos JAR para procesar", jarFiles.length);
        
        // Procesar cada archivo JAR
        for (File jarFile : jarFiles) {
            try {
                logger.debug("Procesando archivo JAR: {}", jarFile.getName());
                cargarEstrategia(jarFile);
            } catch (Exception e) {
                logger.error("Error al cargar estrategia desde JAR: {}", jarFile.getName(), e);
                // Continuar con el siguiente JAR
            }
        }
        
        logger.info("Carga de estrategias completada. {} estrategias cargadas exitosamente", strategies.size());
    }

    /**
     * Carga una estrategia específica desde un archivo JAR.
     * 
     * <p>Este método valida el JAR y carga las estrategias usando ServiceLoader.
     * Si el JAR no es válido o no contiene estrategias, se registra el error
     * pero no se interrumpe el proceso.</p>
     * 
     * @param jarFile Archivo JAR a procesar
     * @throws Exception Si hay errores durante la carga
     */
    private void cargarEstrategia(File jarFile) throws Exception {
        logger.debug("Cargando estrategia desde: {}", jarFile.getName());
        
        // Validar el JAR antes de cargarlo
        if (!validarJar(jarFile)) {
            logger.warn("JAR no válido, saltando: {}", jarFile.getName());
            return;
        }
        
        // Crear class loader para el JAR
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, getClass().getClassLoader());
        
        logger.debug("ClassLoader creado para: {}", jarFile.getName());
        
        // Cargar estrategias usando ServiceLoader
        ServiceLoader<EstrategiaModule> serviceLoader = ServiceLoader.load(EstrategiaModule.class, classLoader);
        
        int estrategiasCargadas = 0;
        for (EstrategiaModule estrategia : serviceLoader) {
            try {
                if (validarEstrategia(estrategia)) {
                    strategies.add(estrategia);
                    estrategiasCargadas++;
                    logger.info("Estrategia cargada exitosamente: {} (v{})", 
                               estrategia.getNombre(), estrategia.getVersion());
                } else {
                    logger.warn("Estrategia no válida, saltando: {}", estrategia.getNombre());
                }
            } catch (Exception e) {
                logger.error("Error al validar estrategia: {}", estrategia.getNombre(), e);
            }
        }
        
        if (estrategiasCargadas == 0) {
            logger.warn("No se cargaron estrategias válidas desde: {}", jarFile.getName());
        } else {
            logger.debug("{} estrategias cargadas desde: {}", estrategiasCargadas, jarFile.getName());
        }
        
        classLoader.close();
    }

    /**
     * Valida que una estrategia sea correcta y funcional.
     * 
     * <p>Realiza validaciones básicas para asegurar que la estrategia
     * puede ser utilizada de forma segura.</p>
     * 
     * @param estrategia La estrategia a validar
     * @return true si la estrategia es válida, false en caso contrario
     */
    private boolean validarEstrategia(EstrategiaModule estrategia) {
        if (estrategia == null) {
            logger.warn("Estrategia es null");
            return false;
        }
        
        try {
            // Validar nombre
            String nombre = estrategia.getNombre();
            if (nombre == null || nombre.trim().isEmpty()) {
                logger.warn("Nombre de estrategia inválido: {}", nombre);
                return false;
            }
            
            // Validar descripción
            String descripcion = estrategia.getDescripcion();
            if (descripcion == null || descripcion.trim().isEmpty()) {
                logger.warn("Descripción de estrategia inválida para: {}", nombre);
                return false;
            }
            
            // Validar versión
            String version = estrategia.getVersion();
            if (version == null || version.trim().isEmpty()) {
                logger.warn("Versión de estrategia inválida para: {}", nombre);
                return false;
            }
            
            // Verificar que no haya duplicados
            for (EstrategiaModule existing : strategies) {
                if (existing.getNombre().equals(nombre)) {
                    logger.warn("Estrategia duplicada encontrada: {}", nombre);
                    return false;
                }
            }
            
            logger.debug("Estrategia válida: {} (v{})", nombre, version);
            return true;
            
        } catch (Exception e) {
            logger.error("Error al validar estrategia", e);
            return false;
        }
    }

    /**
     * Valida que un archivo JAR contenga estrategias válidas.
     * 
     * <p>Verifica que el JAR sea legible y contenga el archivo de servicios
     * necesario para cargar las estrategias.</p>
     * 
     * @param jarFile Archivo JAR a validar
     * @return true si el JAR es válido, false en caso contrario
     */
    private boolean validarJar(File jarFile) {
        if (!jarFile.exists()) {
            logger.warn("Archivo JAR no existe: {}", jarFile.getName());
            return false;
        }
        
        if (!jarFile.canRead()) {
            logger.warn("No se puede leer el archivo JAR: {}", jarFile.getName());
            return false;
        }
        
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry serviceEntry = jar.getJarEntry(SERVICE_FILE);
            if (serviceEntry == null) {
                logger.warn("Archivo de servicios no encontrado en JAR: {}", jarFile.getName());
                return false;
            }
            
            return validarArchivoServicios(jar, serviceEntry, jarFile.getName());
            
        } catch (IOException e) {
            logger.error("Error al leer archivo JAR: {}", jarFile.getName(), e);
            return false;
        }
    }

    /**
     * Valida el contenido del archivo de servicios en el JAR.
     * 
     * @param jar Archivo JAR abierto
     * @param serviceEntry Entrada del archivo de servicios
     * @param jarName Nombre del archivo JAR para logging
     * @return true si el archivo de servicios es válido
     */
    private boolean validarArchivoServicios(JarFile jar, JarEntry serviceEntry, String jarName) {
        try (InputStream is = jar.getInputStream(serviceEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            String line;
            boolean tieneImplementaciones = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    tieneImplementaciones = true;
                    logger.debug("Implementación encontrada en {}: {}", jarName, line);
                }
            }
            
            if (!tieneImplementaciones) {
                logger.warn("Archivo de servicios vacío en JAR: {}", jarName);
                return false;
            }
            
            return true;
            
        } catch (IOException e) {
            logger.error("Error al leer archivo de servicios en JAR: {}", jarName, e);
            return false;
        }
    }

    /**
     * Obtiene una lista inmutable de todas las estrategias cargadas.
     * 
     * @return Lista inmutable de estrategias, nunca {@code null}
     */
    public List<EstrategiaModule> getStrategies() {
        return Collections.unmodifiableList(strategies);
    }

    /**
     * Busca una estrategia por su nombre.
     * 
     * @param nombre Nombre de la estrategia a buscar
     * @return La estrategia encontrada, o {@code null} si no existe
     */
    public EstrategiaModule findStrategyByName(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            logger.warn("Nombre de estrategia inválido para búsqueda: {}", nombre);
            return null;
        }
        
        for (EstrategiaModule estrategia : strategies) {
            if (estrategia.getNombre().equals(nombre)) {
                logger.debug("Estrategia encontrada: {}", nombre);
                return estrategia;
            }
        }
        
        logger.debug("Estrategia no encontrada: {}", nombre);
        return null;
    }

    /**
     * Crea una instancia de estrategia de aprendizaje por nombre.
     * 
     * @param nombre Nombre de la estrategia a crear
     * @param preguntas Lista de preguntas para la estrategia
     * @return Instancia de la estrategia, o {@code null} si no se encuentra
     */
    public EstrategiaAprendizaje crearEstrategia(String nombre, List<Pregunta> preguntas) {
        EstrategiaModule estrategiaModule = findStrategyByName(nombre);
        if (estrategiaModule == null) {
            logger.warn("No se pudo encontrar estrategia: {}", nombre);
            return null;
        }
        
        try {
            EstrategiaAprendizaje estrategia = estrategiaModule.crearEstrategia(preguntas);
            logger.debug("Estrategia creada exitosamente: {} con {} preguntas", nombre, preguntas.size());
            return estrategia;
        } catch (Exception e) {
            logger.error("Error al crear estrategia: {}", nombre, e);
            return null;
        }
    }

    /**
     * Obtiene el número total de estrategias cargadas.
     * 
     * @return Número de estrategias cargadas
     */
    public int getStrategyCount() {
        return strategies.size();
    }

    /**
     * Verifica si hay estrategias cargadas.
     * 
     * @return true si hay al menos una estrategia cargada, false en caso contrario
     */
    public boolean hasStrategies() {
        return !strategies.isEmpty();
    }

    /**
     * Obtiene información detallada de todas las estrategias cargadas.
     * 
     * @return String con información de todas las estrategias
     */
    public String getStrategiesInfo() {
        if (strategies.isEmpty()) {
            return "No hay estrategias cargadas";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Estrategias cargadas (").append(strategies.size()).append("):\n");
        
        for (EstrategiaModule estrategia : strategies) {
            info.append("  - ").append(estrategia.getNombre())
                .append(" (v").append(estrategia.getVersion()).append(")")
                .append(": ").append(estrategia.getDescripcion())
                .append("\n");
        }
        
        return info.toString();
    }

    /**
     * Método de utilidad para testing que permite establecer una instancia específica.
     * 
     * @param testInstance Instancia de prueba
     */
    public static void setInstance(StrategyManager testInstance) {
        instance = testInstance;
    }
} 