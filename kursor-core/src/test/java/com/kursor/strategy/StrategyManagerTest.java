package com.kursor.strategy;

import com.kursor.domain.Pregunta;
import com.kursor.domain.EstrategiaAprendizaje;

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
        
        strategyManager = new StrategyManager(estrategiasDir.getAbsolutePath());
        
        // Crear preguntas de prueba
        preguntasTest = new ArrayList<>();
        // TODO: Crear preguntas de prueba reales
    }
    
    @Test
    void testInicializacion() {
        logger.info("Probando inicialización del StrategyManager");
        
        assertNotNull(strategyManager);
        assertTrue(strategyManager.getEstrategiasDisponibles().isEmpty());
        
        logger.info("Inicialización exitosa");
    }
    
    @Test
    void testCargarEstrategiasDirectorioVacio() {
        logger.info("Probando carga de estrategias con directorio vacío");
        
        // El directorio está vacío, no debería haber errores
        assertDoesNotThrow(() -> strategyManager.cargarEstrategias());
        
        List<String> estrategias = strategyManager.getEstrategiasDisponibles();
        assertTrue(estrategias.isEmpty());
        
        logger.info("Carga con directorio vacío exitosa");
    }
    
    @Test
    void testCargarEstrategiasDirectorioInexistente() {
        logger.info("Probando carga de estrategias con directorio inexistente");
        
        StrategyManager manager = new StrategyManager("/ruta/inexistente");
        
        // No debería lanzar excepción, solo log de advertencia
        assertDoesNotThrow(() -> manager.cargarEstrategias());
        
        List<String> estrategias = manager.getEstrategiasDisponibles();
        assertTrue(estrategias.isEmpty());
        
        logger.info("Carga con directorio inexistente exitosa");
    }
    
    @Test
    void testCargarEstrategiasDirectorioNoEsDirectorio() {
        logger.info("Probando carga de estrategias cuando la ruta no es un directorio");
        
        // Crear un archivo en lugar de directorio
        File archivo = tempDir.resolve("archivo.txt").toFile();
        try {
            archivo.createNewFile();
        } catch (Exception e) {
            fail("No se pudo crear archivo de prueba");
        }
        
        StrategyManager manager = new StrategyManager(archivo.getAbsolutePath());
        
        // Debería lanzar excepción
        assertThrows(RuntimeException.class, () -> manager.cargarEstrategias());
        
        logger.info("Excepción lanzada correctamente para ruta que no es directorio");
    }
    
    @Test
    void testCrearEstrategiaNoDisponible() {
        logger.info("Probando creación de estrategia no disponible");
        
        strategyManager.cargarEstrategias();
        
        // Intentar crear una estrategia que no existe
        assertThrows(IllegalArgumentException.class, () -> {
            strategyManager.crearEstrategia("EstrategiaInexistente", preguntasTest);
        });
        
        logger.info("Excepción lanzada correctamente para estrategia inexistente");
    }
    
    @Test
    void testEsEstrategiaDisponible() {
        logger.info("Probando verificación de disponibilidad de estrategias");
        
        strategyManager.cargarEstrategias();
        
        // Sin estrategias cargadas, todas deberían ser false
        assertFalse(strategyManager.esEstrategiaDisponible("Secuencial"));
        assertFalse(strategyManager.esEstrategiaDisponible("Aleatoria"));
        assertFalse(strategyManager.esEstrategiaDisponible("Repetición Espaciada"));
        assertFalse(strategyManager.esEstrategiaDisponible("Repetir Incorrectas"));
        
        logger.info("Verificación de disponibilidad exitosa");
    }
    
    @Test
    void testGetInformacionEstrategia() {
        logger.info("Probando obtención de información de estrategias");
        
        strategyManager.cargarEstrategias();
        
        // Sin estrategias cargadas, debería retornar null
        assertNull(strategyManager.getInformacionEstrategia("Secuencial"));
        assertNull(strategyManager.getInformacionEstrategia("EstrategiaInexistente"));
        
        logger.info("Obtención de información exitosa");
    }
    
    @Test
    void testGetModuloEstrategia() {
        logger.info("Probando obtención de módulos de estrategias");
        
        strategyManager.cargarEstrategias();
        
        // Sin estrategias cargadas, debería retornar null
        assertNull(strategyManager.getModuloEstrategia("Secuencial"));
        assertNull(strategyManager.getModuloEstrategia("EstrategiaInexistente"));
        
        logger.info("Obtención de módulos exitosa");
    }
    
    @Test
    void testRecargarEstrategias() {
        logger.info("Probando recarga de estrategias");
        
        strategyManager.cargarEstrategias();
        
        // Recargar no debería lanzar excepción
        assertDoesNotThrow(() -> strategyManager.recargarEstrategias());
        
        List<String> estrategias = strategyManager.getEstrategiasDisponibles();
        assertTrue(estrategias.isEmpty());
        
        logger.info("Recarga de estrategias exitosa");
    }
    
    @Test
    void testGetEstadisticasCarga() {
        logger.info("Probando obtención de estadísticas de carga");
        
        strategyManager.cargarEstrategias();
        
        String estadisticas = strategyManager.getEstadisticasCarga();
        assertNotNull(estadisticas);
        assertTrue(estadisticas.contains("Estadísticas de Carga de Estrategias"));
        assertTrue(estadisticas.contains("Estrategias cargadas: 0"));
        
        logger.info("Estadísticas de carga obtenidas correctamente");
    }
    
    @Test
    void testCerrar() {
        logger.info("Probando cierre del StrategyManager");
        
        strategyManager.cargarEstrategias();
        
        // Cerrar no debería lanzar excepción
        assertDoesNotThrow(() -> strategyManager.cerrar());
        
        // Después de cerrar, las estrategias deberían estar vacías
        List<String> estrategias = strategyManager.getEstrategiasDisponibles();
        assertTrue(estrategias.isEmpty());
        
        logger.info("Cierre del StrategyManager exitoso");
    }
    
    @Test
    void testCargarEstrategiasMultipleVeces() {
        logger.info("Probando carga múltiple de estrategias");
        
        // Cargar múltiples veces no debería causar problemas
        strategyManager.cargarEstrategias();
        strategyManager.cargarEstrategias();
        strategyManager.cargarEstrategias();
        
        List<String> estrategias = strategyManager.getEstrategiasDisponibles();
        assertTrue(estrategias.isEmpty());
        
        logger.info("Carga múltiple exitosa");
    }
    
    @Test
    void testCrearEstrategiaConPreguntasNull() {
        logger.info("Probando creación de estrategia con preguntas null");
        
        strategyManager.cargarEstrategias();
        
        // Intentar crear estrategia con preguntas null debería lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            strategyManager.crearEstrategia("Secuencial", null);
        });
        
        logger.info("Excepción lanzada correctamente para preguntas null");
    }
    
    @Test
    void testCrearEstrategiaConPreguntasVacias() {
        logger.info("Probando creación de estrategia con preguntas vacías");
        
        strategyManager.cargarEstrategias();
        
        // Intentar crear estrategia con preguntas vacías debería lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            strategyManager.crearEstrategia("Secuencial", new ArrayList<>());
        });
        
        logger.info("Excepción lanzada correctamente para preguntas vacías");
    }
    
    @Test
    void testIntegracionCompleta() {
        logger.info("Probando integración completa del StrategyManager");
        
        // 1. Inicialización
        assertNotNull(strategyManager);
        
        // 2. Carga de estrategias
        strategyManager.cargarEstrategias();
        
        // 3. Verificación de estado
        List<String> estrategias = strategyManager.getEstrategiasDisponibles();
        assertTrue(estrategias.isEmpty());
        
        // 4. Verificación de disponibilidad
        assertFalse(strategyManager.esEstrategiaDisponible("Secuencial"));
        
        // 5. Obtención de información
        assertNull(strategyManager.getInformacionEstrategia("Secuencial"));
        
        // 6. Obtención de estadísticas
        String estadisticas = strategyManager.getEstadisticasCarga();
        assertNotNull(estadisticas);
        
        // 7. Recarga
        strategyManager.recargarEstrategias();
        
        // 8. Cierre
        strategyManager.cerrar();
        
        logger.info("Integración completa exitosa");
    }
} 