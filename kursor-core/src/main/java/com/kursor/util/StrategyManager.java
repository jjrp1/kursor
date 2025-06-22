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

/**
 * Gestor de estrategias de aprendizaje para el sistema Kursor.
 * 
 * <p>Esta clase implementa el patrón Singleton y es responsable de:</p>
 * <ul>
 *   <li>Cargar dinámicamente todas las estrategias disponibles</li>
 *   <li>Gestionar el ciclo de vida de las estrategias</li>
 *   <li>Proporcionar acceso a las estrategias por nombre</li>
 *   <li>Crear instancias de estrategias con preguntas específicas</li>
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
 * <p><strong>Uso:</strong></p>
 * <pre>{@code
 * StrategyManager manager = StrategyManager.getInstance();
 * EstrategiaAprendizaje estrategia = manager.crearEstrategia("Secuencial", preguntas);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaModule
 * @see EstrategiaAprendizaje
 */
public class StrategyManager {
    
    /** Instancia única del gestor de estrategias */
    private static StrategyManager instance;
    
    /** Logger para esta clase */
    private static final Logger logger = LoggerFactory.getLogger(StrategyManager.class);
    
    /** Lista de estrategias cargadas */
    private final List<EstrategiaModule> strategies;

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
     * Carga todas las estrategias disponibles desde el classpath.
     * 
     * <p>Este método utiliza ServiceLoader para cargar dinámicamente
     * todas las implementaciones de EstrategiaModule disponibles
     * en el classpath.</p>
     * 
     * <p><strong>Comportamiento ante errores:</strong> Si una estrategia individual falla
     * al cargar, se registra el error y se continúa con las demás estrategias.</p>
     */
    private void cargarEstrategias() {
        logger.info("Iniciando carga de estrategias desde el classpath");
        
        try {
            // Cargar estrategias usando ServiceLoader desde el classpath
            ServiceLoader<EstrategiaModule> serviceLoader = ServiceLoader.load(EstrategiaModule.class);
            
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
            
            logger.info("Carga de estrategias completada. {} estrategias cargadas exitosamente", estrategiasCargadas);
            
        } catch (Exception e) {
            logger.error("Error crítico durante la carga de estrategias", e);
            throw new RuntimeException("No se pudieron cargar las estrategias", e);
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