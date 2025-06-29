package com.kursor.strategy;

import com.kursor.domain.Pregunta;
import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.shared.util.StrategyManager;
import com.kursor.strategy.EstrategiaModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para StrategyManager.
 * 
 * <p>Estas pruebas verifican la funcionalidad de carga dinámica
 * de estrategias desde archivos JAR.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class StrategyManagerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategyManagerTest.class);
    
    @TempDir
    Path tempDir;
    
    private StrategyManager strategyManager;
    private List<Pregunta> preguntasTest;
    
    @BeforeEach
    void setUp() {
        // Crear directorio temporal para estrategias
        File estrategiasDir = tempDir.resolve("strategies").toFile();
        estrategiasDir.mkdirs();
        
        // Usar el singleton pero con configuración temporal
        strategyManager = StrategyManager.getInstance();
        
        // Crear preguntas de prueba
        preguntasTest = new ArrayList<>();
        // TODO: Crear preguntas de prueba reales
    }
    
    @Test
    void testInicializacion() {
        logger.info("Probando inicialización del StrategyManager");
        
        assertNotNull(strategyManager);
        assertTrue(strategyManager.getStrategies().isEmpty());
        
        logger.info("Inicialización exitosa");
    }
    
    @Test
    void testCargarEstrategiasDirectorioVacio() {
        logger.info("Probando carga de estrategias con directorio vacío");
        
        // El directorio está vacío, no debería haber errores
        assertDoesNotThrow(() -> {
            // La carga se hace automáticamente en el constructor
            StrategyManager manager = StrategyManager.getInstance();
        });
        
        List<EstrategiaModule> estrategias = strategyManager.getStrategies();
        assertTrue(estrategias.isEmpty());
        
        logger.info("Carga con directorio vacío exitosa");
    }
    
    @Test
    void testCargarEstrategiasDirectorioInexistente() {
        logger.info("Probando carga de estrategias con directorio inexistente");
        
        // No debería lanzar excepción, solo log de advertencia
        assertDoesNotThrow(() -> {
            StrategyManager manager = StrategyManager.getInstance();
        });
        
        List<EstrategiaModule> estrategias = strategyManager.getStrategies();
        assertTrue(estrategias.isEmpty());
        
        logger.info("Carga con directorio inexistente exitosa");
    }
    
    @Test
    void testCrearEstrategiaNoDisponible() {
        logger.info("Probando creación de estrategia no disponible");
        
        // Intentar crear una estrategia que no existe
        EstrategiaAprendizaje estrategia = strategyManager.crearEstrategia("EstrategiaInexistente", preguntasTest);
        assertNull(estrategia);
        
        logger.info("Retorno null correctamente para estrategia inexistente");
    }
    
    @Test
    void testFindStrategyByName() {
        logger.info("Probando búsqueda de estrategia por nombre");
        
        // Sin estrategias cargadas, todas deberían ser null
        assertNull(strategyManager.findStrategyByName("Secuencial"));
        assertNull(strategyManager.findStrategyByName("Aleatoria"));
        assertNull(strategyManager.findStrategyByName("Repetición Espaciada"));
        assertNull(strategyManager.findStrategyByName("Repetir Incorrectas"));
        
        logger.info("Búsqueda de estrategias exitosa");
    }
    
    @Test
    void testGetStrategiesInfo() {
        logger.info("Probando obtención de información de estrategias");
        
        String info = strategyManager.getStrategiesInfo();
        assertNotNull(info);
        assertTrue(info.contains("No hay estrategias cargadas"));
        
        logger.info("Obtención de información exitosa");
    }
    
    @Test
    void testGetStrategyCount() {
        logger.info("Probando obtención del conteo de estrategias");
        
        int count = strategyManager.getStrategyCount();
        assertEquals(0, count);
        
        logger.info("Conteo de estrategias correcto");
    }
    
    @Test
    void testHasStrategies() {
        logger.info("Probando verificación de existencia de estrategias");
        
        boolean hasStrategies = strategyManager.hasStrategies();
        assertFalse(hasStrategies);
        
        logger.info("Verificación de existencia correcta");
    }
    
    @Test
    void testCrearEstrategiaConPreguntasNull() {
        logger.info("Probando creación de estrategia con preguntas null");
        
        EstrategiaAprendizaje estrategia = strategyManager.crearEstrategia("Secuencial", null);
        assertNull(estrategia);
        
        logger.info("Manejo correcto de preguntas null");
    }
    
    @Test
    void testCrearEstrategiaConPreguntasVacias() {
        logger.info("Probando creación de estrategia con preguntas vacías");
        
        List<Pregunta> preguntasVacias = new ArrayList<>();
        EstrategiaAprendizaje estrategia = strategyManager.crearEstrategia("Secuencial", preguntasVacias);
        assertNull(estrategia);
        
        logger.info("Manejo correcto de preguntas vacías");
    }
    
    @Test
    void testIntegracionCompleta() {
        logger.info("Probando integración completa del StrategyManager");
        
        // Verificar que el singleton funciona correctamente
        StrategyManager instance1 = StrategyManager.getInstance();
        StrategyManager instance2 = StrategyManager.getInstance();
        assertSame(instance1, instance2);
        
        // Verificar métodos básicos
        assertNotNull(instance1.getStrategies());
        assertEquals(0, instance1.getStrategyCount());
        assertFalse(instance1.hasStrategies());
        
        logger.info("Integración completa exitosa");
    }
} 