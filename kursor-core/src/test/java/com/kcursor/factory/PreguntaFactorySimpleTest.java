package com.kcursor.factory;

import com.kcursor.domain.Pregunta;
import com.kcursor.modules.PreguntaModule;
import com.kcursor.util.ModuleManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias simplificadas para la clase PreguntaFactory.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas simples de PreguntaFactory")
class PreguntaFactorySimpleTest {

    @Mock
    private PreguntaModule mockModule;

    @Mock
    private Pregunta mockPregunta;

    @BeforeEach
    void setUp() {
        when(mockModule.getQuestionType()).thenReturn("test");
        when(mockModule.getModuleName()).thenReturn("Test Module");
        when(mockModule.parsePregunta(anyMap())).thenReturn(mockPregunta);
        when(mockModule.createQuestion(anyString())).thenReturn(mockPregunta);
    }

    @AfterEach
    void tearDown() {
        // Resetear la instancia de ModuleManager
        ModuleManager.setInstance(null);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando ID es null")
    void deberiaLanzarExcepcionCuandoIdEsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            PreguntaFactory.crearPregunta("test", null);
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando tipo es null")
    void deberiaLanzarExcepcionCuandoTipoEsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            PreguntaFactory.crearPregunta(null, "test");
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando datos son null")
    void deberiaLanzarExcepcionCuandoDatosSonNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            PreguntaFactory.crearPregunta((Map<String, Object>) null);
        });
    }

    @Test
    @DisplayName("Debería retornar false cuando tipo es null")
    void deberiaRetornarFalseCuandoTipoEsNull() {
        boolean resultado = PreguntaFactory.existeModuloParaTipo(null);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería retornar false cuando tipo está vacío")
    void deberiaRetornarFalseCuandoTipoEstaVacio() {
        boolean resultado = PreguntaFactory.existeModuloParaTipo("");
        assertFalse(resultado);
    }
} 