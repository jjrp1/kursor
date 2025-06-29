package com.kursor.util;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.strategy.EstrategiaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *   <li><strong>Repetición Espaciada:</strong> Optimizada para retención a largo plazo</li>
 *   <li><strong>Repetir Incorrectas:</strong> Enfocada en preguntas falladas anteriormente</li>
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
 * // Buscar estrategia específica
 * EstrategiaModule secuencialStrategy = manager.findStrategyByName("Secuencial");
 * if (secuencialStrategy != null) {
 *     // Usar la estrategia para crear instancias
 *     EstrategiaAprendizaje estrategia = secuencialStrategy.crearEstrategia(preguntas);
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
 * @version 2.0.0
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
    
    /** Lista de estrategias cargadas exitosamente */
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
            logger.info("Intentando carga desde classpath como fallback");
            cargarEstrategiasDesdeClasspath();
            return;
        }

        logger.info("Encontrados {} archivos JAR en el directorio de estrategias", jarFiles.length);
        
        int estrategiasCargadas = 0;
        for (File jarFile : jarFiles) {
            try {
                logger.debug("Procesando archivo JAR: {}", jarFile.getName());
                
                if (validarJar(jarFile)) {
                    cargarEstrategia(jarFile);
                    estrategiasCargadas++;
                } else {
                    logger.warn("Archivo JAR no válido, saltando: {}", jarFile.getName());
                }
            } catch (Exception e) {
                logger.error("Error al procesar archivo JAR: {}", jarFile.getName(), e);
            }
        }
        
        logger.info("Carga de estrategias completada. {} estrategias cargadas exitosamente", estrategiasCargadas);
        
        // Si no se cargaron estrategias desde JARs, intentar desde classpath
        if (estrategiasCargadas == 0) {
            logger.info("No se cargaron estrategias desde JARs, intentando desde classpath");
            cargarEstrategiasDesdeClasspath();
        }
    }

    /**
     * Carga una estrategia específica desde un archivo JAR.
     * 
     * <p>Este método:</p>
     * <ol>
     *   <li>Valida que el JAR contenga el archivo de servicios correcto</li>
     *   <li>Crea un ClassLoader específico para el JAR</li>
     *   <li>Usa ServiceLoader para cargar las implementaciones</li>
     *   <li>Valida cada estrategia cargada</li>
     *   <li>Agrega las estrategias válidas a la lista</li>
     * </ol>
     * 
     * @param jarFile Archivo JAR que contiene la estrategia
     * @throws Exception Si ocurre un error durante la carga
     */
    private void cargarEstrategia(File jarFile) throws Exception {
        String jarName = jarFile.getName();
        logger.debug("Cargando estrategia desde JAR: {}", jarName);
        
        try (JarFile jar = new JarFile(jarFile)) {
            // Verificar que el JAR contenga el archivo de servicios
            JarEntry serviceEntry = jar.getJarEntry(SERVICE_FILE);
            if (serviceEntry == null) {
                logger.warn("JAR no contiene archivo de servicios: {}", jarName);
                return;
            }
            
            if (!validarArchivoServicios(jar, serviceEntry, jarName)) {
                logger.warn("Archivo de servicios no válido en JAR: {}", jarName);
                return;
            }
            
            // Crear ClassLoader para el JAR
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, getClass().getClassLoader());
            
            // Cargar estrategias usando ServiceLoader
            ServiceLoader<EstrategiaModule> serviceLoader = ServiceLoader.load(EstrategiaModule.class, classLoader);
            
            int estrategiasEnJar = 0;
            for (EstrategiaModule estrategia : serviceLoader) {
                try {
                    if (validarEstrategia(estrategia)) {
                        strategies.add(estrategia);
                        estrategiasEnJar++;
                        logger.info("Estrategia cargada desde JAR {}: {} (v{})", 
                                   jarName, estrategia.getNombre(), estrategia.getVersion());
                    } else {
                        logger.warn("Estrategia no válida en JAR {}, saltando: {}", jarName, estrategia.getNombre());
                    }
                } catch (Exception e) {
                    logger.error("Error al validar estrategia en JAR {}: {}", jarName, estrategia.getNombre(), e);
                }
            }
            
            if (estrategiasEnJar == 0) {
                logger.warn("No se cargaron estrategias válidas desde JAR: {}", jarName);
            } else {
                logger.debug("Cargadas {} estrategias desde JAR: {}", estrategiasEnJar, jarName);
            }
            
        } catch (Exception e) {
            logger.error("Error al cargar estrategia desde JAR: {}", jarName, e);
            throw e;
        }
    }

    /**
     * Carga estrategias desde el classpath como fallback.
     * 
     * <p>Este método se usa cuando no se pueden cargar estrategias desde JARs
     * o como método de respaldo para desarrollo.</p>
     */
    private void cargarEstrategiasDesdeClasspath() {
        logger.info("Cargando estrategias desde classpath");
        
        try {
            ServiceLoader<EstrategiaModule> serviceLoader = ServiceLoader.load(EstrategiaModule.class);
            
            int estrategiasCargadas = 0;
            for (EstrategiaModule estrategia : serviceLoader) {
                try {
                    if (validarEstrategia(estrategia)) {
                        strategies.add(estrategia);
                        estrategiasCargadas++;
                        logger.info("Estrategia cargada desde classpath: {} (v{})", 
                                   estrategia.getNombre(), estrategia.getVersion());
                    } else {
                        logger.warn("Estrategia no válida desde classpath, saltando: {}", estrategia.getNombre());
                    }
                } catch (Exception e) {
                    logger.error("Error al validar estrategia desde classpath: {}", estrategia.getNombre(), e);
                }
            }
            
            logger.info("Carga desde classpath completada. {} estrategias cargadas", estrategiasCargadas);
            
        } catch (Exception e) {
            logger.error("Error crítico durante la carga de estrategias desde classpath", e);
        }
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
     * Valida que un archivo JAR sea válido y contenga estrategias.
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
            logger.warn("No se pueden leer archivos JAR: {}", jarFile.getName());
            return false;
        }
        
        if (jarFile.length() == 0) {
            logger.warn("Archivo JAR vacío: {}", jarFile.getName());
            return false;
        }
        
        try (JarFile jar = new JarFile(jarFile)) {
            // Verificar que contenga el archivo de servicios
            JarEntry serviceEntry = jar.getJarEntry(SERVICE_FILE);
            if (serviceEntry == null) {
                logger.debug("JAR no contiene archivo de servicios: {}", jarFile.getName());
                return false;
            }
            
            return validarArchivoServicios(jar, serviceEntry, jarFile.getName());
            
        } catch (IOException e) {
            logger.warn("Error al abrir archivo JAR: {}", jarFile.getName(), e);
            return false;
        }
    }

    /**
     * Valida el contenido del archivo de servicios en un JAR.
     * 
     * @param jar Archivo JAR
     * @param serviceEntry Entrada del archivo de servicios
     * @param jarName Nombre del archivo JAR para logging
     * @return true si el archivo de servicios es válido, false en caso contrario
     */
    private boolean validarArchivoServicios(JarFile jar, JarEntry serviceEntry, String jarName) {
        try (InputStream inputStream = jar.getInputStream(serviceEntry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                logger.warn("Archivo de servicios vacío en JAR: {}", jarName);
                return false;
            }
            
            // Verificar que la línea contenga una clase válida
            line = line.trim();
            if (!line.contains(".")) {
                logger.warn("Clase inválida en archivo de servicios de JAR {}: {}", jarName, line);
                return false;
            }
            
            logger.debug("Archivo de servicios válido en JAR {}: {}", jarName, line);
            return true;
            
        } catch (IOException e) {
            logger.warn("Error al leer archivo de servicios en JAR: {}", jarName, e);
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

    /**
     * Obtiene la ruta del directorio de estrategias.
     * 
     * @return La ruta completa del directorio de estrategias
     */
    public String getStrategiesDir() {
        return STRATEGIES_DIR;
    }
} 