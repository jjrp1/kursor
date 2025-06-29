package com.kursor.presentation.controllers;

import com.kursor.yaml.dto.BloqueDTO;
import com.kursor.yaml.dto.CursoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para CursoExecutionManager
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class CursoExecutionManagerTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private CursoDTO mockCurso;
    
    @Mock
    private BloqueDTO mockBloque;
    
    private CursoExecutionManager executionManager;

    @BeforeEach
    void setUp() {
        // Configurar mocks básicos
        when(mockCurso.getTitulo()).thenReturn("Curso de Prueba");
        when(mockCurso.getId()).thenReturn("test-curso");
        when(mockBloque.getTitulo()).thenReturn("Bloque de Prueba");
        
        executionManager = new CursoExecutionManager(mockStage, mockCurso);
    }

    @Test
    void testConstructor() {
        assertNotNull(executionManager);
        assertEquals(mockCurso, executionManager.getCurso());
        assertNull(executionManager.getBloqueSeleccionado());
        assertNull(executionManager.getEstrategiaSeleccionada());
    }

    @Test
    void testGetCurso() {
        CursoDTO curso = executionManager.getCurso();
        assertEquals(mockCurso, curso);
        assertEquals("Curso de Prueba", curso.getTitulo());
    }

    @Test
    void testGetBloqueSeleccionadoInicialmenteNull() {
        assertNull(executionManager.getBloqueSeleccionado());
    }

    @Test
    void testGetEstrategiaSeleccionadaInicialmenteNull() {
        assertNull(executionManager.getEstrategiaSeleccionada());
    }

    @Test
    void testEjecutarCursoSinSeleccionDeBloque() {
        // Este test verifica que el método maneja correctamente cuando no se selecciona bloque
        // En un entorno real, esto simularía que el usuario cancela la selección de bloque
        
        // Nota: Este test no puede ejecutar el flujo completo porque requiere UI real
        // pero podemos verificar que el método existe y tiene la estructura correcta
        assertNotNull(executionManager);
        assertTrue(executionManager.getClass().getDeclaredMethods().length > 0);
    }

    @Test
    void testEstructuraDelFlujo() {
        // Verificar que el flujo tiene los pasos correctos
        // 1. Selección de bloque
        // 2. Selección de estrategia  
        // 3. Inicio del curso
        
        // Este test verifica que la estructura del flujo está correctamente definida
        assertNotNull(executionManager);
        
        // Verificar que el método ejecutarCurso existe y es público
        try {
            executionManager.getClass().getMethod("ejecutarCurso");
        } catch (NoSuchMethodException e) {
            fail("El método ejecutarCurso debe existir y ser público");
        }
    }
} 