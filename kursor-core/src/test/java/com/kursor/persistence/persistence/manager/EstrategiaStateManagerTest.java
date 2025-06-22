package com.kursor.persistence.persistence.manager;

import com.kursor.domain.Pregunta;
import com.kursor.persistence.persistence.entity.EstadoEstrategia;
import com.kursor.persistence.persistence.entity.Sesion;
import com.kursor.persistence.persistence.repository.EstadoEstrategiaRepository;
import com.kursor.strategy.StrategyManager;
import com.kursor.domain.EstrategiaAprendizaje;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para EstrategiaStateManager.
 * 
 * <p>Estas pruebas verifican la funcionalidad de persistencia
 * de estado de estrategias con integración del StrategyManager.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class EstrategiaStateManagerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaStateManagerTest.class);
    
    @TempDir
    Path tempDir;
    
    private EstrategiaStateManager stateManager;
    private StrategyManager strategyManager;
    private EntityManager entityManager;
    private EstadoEstrategiaRepository estadoRepository;
    private Sesion sesionTest;
    private List<Pregunta> preguntasTest;
    
    @BeforeEach
    void setUp() {
        logger.info("Configurando pruebas de EstrategiaStateManager");
        
        // Crear directorio temporal para estrategias
        File estrategiasDir = tempDir.resolve("strategies").toFile();
        estrategiasDir.mkdirs();
        
        // Mock del EntityManager
        entityManager = mock(EntityManager.class);
        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);
        
        // Mock del StrategyManager
        strategyManager = mock(StrategyManager.class);
        
        // Mock del repositorio
        estadoRepository = mock(EstadoEstrategiaRepository.class);
        
        // Crear sesión de prueba
        sesionTest = new Sesion();
        sesionTest.setId(1L);
        sesionTest.setBloqueId("bloque_test");
        
        // Crear preguntas de prueba
        preguntasTest = new ArrayList<>();
        // TODO: Crear preguntas de prueba reales
        
        // Crear el state manager real
        stateManager = new EstrategiaStateManager(entityManager, strategyManager);
        
        logger.info("Configuración completada");
    }
    
    @Test
    void testInicializacion() {
        logger.info("Probando inicialización del EstrategiaStateManager");
        
        assertNotNull(stateManager);
        assertNotNull(stateManager.getStrategyManager());
        
        logger.info("Inicialización exitosa");
    }
    
    @Test
    void testGuardarEstadoEstrategia() {
        logger.info("Probando guardado de estado de estrategia");
        
        // Mock de estrategia
        EstrategiaAprendizaje estrategia = mock(EstrategiaAprendizaje.class);
        when(estrategia.getNombre()).thenReturn("Secuencial");
        when(estrategia.getProgreso()).thenReturn(0.5);
        
        // Mock del repositorio para simular que no existe estado previo
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.empty());
        
        // Mock de guardado exitoso
        EstadoEstrategia estadoGuardado = new EstadoEstrategia();
        estadoGuardado.setId(1L);
        when(estadoRepository.guardar(any(EstadoEstrategia.class)))
            .thenReturn(estadoGuardado);
        
        // Ejecutar método
        assertDoesNotThrow(() -> stateManager.guardarEstadoEstrategia(sesionTest, estrategia));
        
        // Verificar que se llamó al repositorio
        verify(estadoRepository).buscarPorSesion(sesionTest.getId());
        verify(estadoRepository).guardar(any(EstadoEstrategia.class));
        
        logger.info("Guardado de estado exitoso");
    }
    
    @Test
    void testGuardarEstadoEstrategiaConEstadoExistente() {
        logger.info("Probando guardado de estado con estado existente");
        
        // Mock de estrategia
        EstrategiaAprendizaje estrategia = mock(EstrategiaAprendizaje.class);
        when(estrategia.getNombre()).thenReturn("Aleatoria");
        when(estrategia.getProgreso()).thenReturn(0.75);
        
        // Mock de estado existente
        EstadoEstrategia estadoExistente = new EstadoEstrategia();
        estadoExistente.setId(1L);
        estadoExistente.setSesion(sesionTest);
        estadoExistente.setTipoEstrategia("Aleatoria");
        
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.of(estadoExistente));
        
        // Mock de guardado exitoso
        when(estadoRepository.guardar(any(EstadoEstrategia.class)))
            .thenReturn(estadoExistente);
        
        // Ejecutar método
        assertDoesNotThrow(() -> stateManager.guardarEstadoEstrategia(sesionTest, estrategia));
        
        // Verificar que se actualizó el estado existente
        verify(estadoRepository).guardar(estadoExistente);
        
        logger.info("Actualización de estado existente exitosa");
    }
    
    @Test
    void testRestaurarEstadoEstrategiaSinEstadoPrevio() {
        logger.info("Probando restauración sin estado previo");
        
        // Mock del repositorio para simular que no existe estado
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.empty());
        
        // Ejecutar método
        EstrategiaAprendizaje estrategiaRestaurada = stateManager.restaurarEstadoEstrategia(sesionTest, "Secuencial");
        
        // Debería retornar null
        assertNull(estrategiaRestaurada);
        
        logger.info("Restauración sin estado previo exitosa");
    }
    
    @Test
    void testRestaurarEstadoEstrategiaConEstadoExistente() {
        logger.info("Probando restauración con estado existente");
        
        // Mock de estado existente
        EstadoEstrategia estadoExistente = new EstadoEstrategia();
        estadoExistente.setId(1L);
        estadoExistente.setSesion(sesionTest);
        estadoExistente.setTipoEstrategia("Secuencial");
        estadoExistente.setDatosEstado("{\"tipo\":\"Secuencial\",\"progreso\":0.5,\"indiceActual\":3}");
        estadoExistente.setProgreso(0.5);
        
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.of(estadoExistente));
        
        // Mock de estrategia creada por StrategyManager
        EstrategiaAprendizaje estrategiaMock = mock(EstrategiaAprendizaje.class);
        when(estrategiaMock.getNombre()).thenReturn("Secuencial");
        when(strategyManager.crearEstrategia("Secuencial", preguntasTest))
            .thenReturn(estrategiaMock);
        
        // Ejecutar método
        EstrategiaAprendizaje estrategiaRestaurada = stateManager.restaurarEstadoEstrategia(sesionTest, "Secuencial");
        
        // Debería retornar la estrategia
        assertNotNull(estrategiaRestaurada);
        assertEquals("Secuencial", estrategiaRestaurada.getNombre());
        
        // Verificar que se usó el StrategyManager
        verify(strategyManager).crearEstrategia("Secuencial", preguntasTest);
        
        logger.info("Restauración con estado existente exitosa");
    }
    
    @Test
    void testEliminarEstadoEstrategia() {
        logger.info("Probando eliminación de estado de estrategia");
        
        // Mock de estado existente
        EstadoEstrategia estadoExistente = new EstadoEstrategia();
        estadoExistente.setId(1L);
        estadoExistente.setSesion(sesionTest);
        
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.of(estadoExistente));
        
        // Mock de eliminación exitosa
        when(estadoRepository.eliminar(estadoExistente))
            .thenReturn(true);
        
        // Ejecutar método
        boolean resultado = stateManager.eliminarEstadoEstrategia(sesionTest);
        
        // Debería retornar true
        assertTrue(resultado);
        
        // Verificar que se llamó al repositorio
        verify(estadoRepository).eliminar(estadoExistente);
        
        logger.info("Eliminación de estado exitosa");
    }
    
    @Test
    void testEliminarEstadoEstrategiaInexistente() {
        logger.info("Probando eliminación de estado inexistente");
        
        // Mock del repositorio para simular que no existe estado
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.empty());
        
        // Ejecutar método
        boolean resultado = stateManager.eliminarEstadoEstrategia(sesionTest);
        
        // Debería retornar false
        assertFalse(resultado);
        
        // Verificar que no se llamó al repositorio de eliminación
        verify(estadoRepository, never()).eliminar(any());
        
        logger.info("Eliminación de estado inexistente exitosa");
    }
    
    @Test
    void testExisteEstadoGuardado() {
        logger.info("Probando verificación de existencia de estado guardado");
        
        // Mock de estado existente
        EstadoEstrategia estadoExistente = new EstadoEstrategia();
        estadoExistente.setId(1L);
        estadoExistente.setSesion(sesionTest);
        
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.of(estadoExistente));
        
        // Ejecutar método
        boolean existe = stateManager.existeEstadoGuardado(sesionTest);
        
        // Debería retornar true
        assertTrue(existe);
        
        logger.info("Verificación de existencia exitosa");
    }
    
    @Test
    void testExisteEstadoGuardadoInexistente() {
        logger.info("Probando verificación de existencia de estado inexistente");
        
        // Mock del repositorio para simular que no existe estado
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.empty());
        
        // Ejecutar método
        boolean existe = stateManager.existeEstadoGuardado(sesionTest);
        
        // Debería retornar false
        assertFalse(existe);
        
        logger.info("Verificación de inexistencia exitosa");
    }
    
    @Test
    void testGetInformacionEstado() {
        logger.info("Probando obtención de información de estado");
        
        // Mock de estado existente
        EstadoEstrategia estadoExistente = new EstadoEstrategia();
        estadoExistente.setId(1L);
        estadoExistente.setSesion(sesionTest);
        estadoExistente.setTipoEstrategia("Secuencial");
        estadoExistente.setProgreso(0.5);
        estadoExistente.setDatosEstado("{\"indiceActual\":3}");
        
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.of(estadoExistente));
        
        // Ejecutar método
        String informacion = stateManager.getInformacionEstado(sesionTest);
        
        // Debería contener información del estado
        assertNotNull(informacion);
        assertTrue(informacion.contains("Secuencial"));
        assertTrue(informacion.contains("0.5"));
        
        logger.info("Obtención de información exitosa");
    }
    
    @Test
    void testGetInformacionEstadoInexistente() {
        logger.info("Probando obtención de información de estado inexistente");
        
        // Mock del repositorio para simular que no existe estado
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.empty());
        
        // Ejecutar método
        String informacion = stateManager.getInformacionEstado(sesionTest);
        
        // Debería retornar mensaje de no encontrado
        assertNotNull(informacion);
        assertTrue(informacion.contains("No se encontró estado guardado"));
        
        logger.info("Obtención de información de estado inexistente exitosa");
    }
    
    @Test
    void testGuardarEstadoEstrategiaConError() {
        logger.info("Probando guardado de estado con error");
        
        // Mock de estrategia
        EstrategiaAprendizaje estrategia = mock(EstrategiaAprendizaje.class);
        when(estrategia.getNombre()).thenReturn("Secuencial");
        when(estrategia.getProgreso()).thenReturn(0.5);
        
        // Mock del repositorio para simular error
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenThrow(new RuntimeException("Error de base de datos"));
        
        // Ejecutar método - debería manejar el error
        assertDoesNotThrow(() -> stateManager.guardarEstadoEstrategia(sesionTest, estrategia));
        
        logger.info("Manejo de error en guardado exitoso");
    }
    
    @Test
    void testRestaurarEstadoEstrategiaConError() {
        logger.info("Probando restauración de estado con error");
        
        // Mock del repositorio para simular error
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenThrow(new RuntimeException("Error de base de datos"));
        
        // Ejecutar método - debería manejar el error
        EstrategiaAprendizaje estrategiaRestaurada = stateManager.restaurarEstadoEstrategia(sesionTest, "Secuencial");
        
        // Debería retornar null en caso de error
        assertNull(estrategiaRestaurada);
        
        logger.info("Manejo de error en restauración exitoso");
    }
    
    @Test
    void testIntegracionCompleta() {
        logger.info("Probando integración completa del EstrategiaStateManager");
        
        // Mock de estrategia
        EstrategiaAprendizaje estrategia = mock(EstrategiaAprendizaje.class);
        when(estrategia.getNombre()).thenReturn("Secuencial");
        when(estrategia.getProgreso()).thenReturn(0.5);
        
        // Mock del repositorio
        when(estadoRepository.buscarPorSesion(sesionTest.getId()))
            .thenReturn(Optional.empty());
        
        EstadoEstrategia estadoGuardado = new EstadoEstrategia();
        estadoGuardado.setId(1L);
        when(estadoRepository.guardar(any(EstadoEstrategia.class)))
            .thenReturn(estadoGuardado);
        
        // Prueba completa del flujo
        assertDoesNotThrow(() -> stateManager.guardarEstadoEstrategia(sesionTest, estrategia));
        
        boolean existe = stateManager.existeEstadoGuardado(sesionTest);
        assertTrue(existe);
        
        String informacion = stateManager.getInformacionEstado(sesionTest);
        assertNotNull(informacion);
        
        boolean eliminado = stateManager.eliminarEstadoEstrategia(sesionTest);
        assertTrue(eliminado);
        
        logger.info("Integración completa exitosa");
    }
} 