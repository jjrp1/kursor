package com.kursor.factory;

import com.kursor.domain.Pregunta;
import com.kursor.modules.PreguntaModule;
import com.kursor.util.ModuleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase PreguntaFactory.
 * 
 * <p>Esta clase de pruebas verifica el comportamiento del patrón Factory
 * para la creación de preguntas de diferentes tipos, incluyendo:</p>
 * <ul>
 *   <li>Creación de preguntas desde datos YAML</li>
 *   <li>Creación de preguntas básicas con tipo e ID</li>
 *   <li>Validación de parámetros de entrada</li>
 *   <li>Manejo de errores y excepciones</li>
 *   <li>Verificación de existencia de módulos</li>
 *   <li>Obtención de tipos soportados</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de PreguntaFactory")
class PreguntaFactoryTest {

    @Mock
    private ModuleManager moduleManager;
    
    @Mock
    private PreguntaModule preguntaModule;
    
    @Mock
    private Pregunta pregunta;

    @BeforeEach
    void setUp() {
        // Configurar comportamiento básico del mock
        when(preguntaModule.getModuleName()).thenReturn("TestModule");
        when(preguntaModule.getQuestionType()).thenReturn("test");
    }

    @Nested
    @DisplayName("Creación de preguntas desde datos YAML")
    class CrearPreguntaConDatosYAMLTests {

        @Test
        @DisplayName("Debería crear pregunta exitosamente desde datos YAML válidos")
        void deberiaCrearPreguntaDesdeDatosYAMLValidos() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            datos.put("tipo", "test");
            datos.put("enunciado", "¿Cuál es la capital de España?");
            datos.put("opciones", List.of("Madrid", "Barcelona", "Valencia"));
            datos.put("respuestaCorrecta", "Madrid");

            when(pregunta.getId()).thenReturn("p1");
            when(preguntaModule.parsePregunta(datos)).thenReturn(pregunta);

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When
                Pregunta resultado = PreguntaFactory.crearPregunta(datos);

                // Then
                assertNotNull(resultado);
                assertEquals("p1", resultado.getId());
                verify(preguntaModule).parsePregunta(datos);
            }
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando datos son null")
        void deberiaLanzarExcepcionCuandoDatosSonNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta((Map<String, Object>) null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando tipo es null")
        void deberiaLanzarExcepcionCuandoTipoEsNull() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            // tipo es null

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando tipo está vacío")
        void deberiaLanzarExcepcionCuandoTipoEstaVacio() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            datos.put("tipo", "");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando tipo solo tiene espacios")
        void deberiaLanzarExcepcionCuandoTipoSoloTieneEspacios() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            datos.put("tipo", "   ");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando no se encuentra módulo")
        void deberiaLanzarExcepcionCuandoNoSeEncuentraModulo() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            datos.put("tipo", "tipo_inexistente");

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("tipo_inexistente")).thenReturn(null);

                // When & Then
                assertThrows(IllegalArgumentException.class, () -> {
                    PreguntaFactory.crearPregunta(datos);
                });
            }
        }

        @Test
        @DisplayName("Debería lanzar RuntimeException cuando módulo retorna null")
        void deberiaLanzarRuntimeExceptionCuandoModuloRetornaNull() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            datos.put("tipo", "test");

            when(preguntaModule.parsePregunta(datos)).thenReturn(null);

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When & Then
                assertThrows(RuntimeException.class, () -> {
                    PreguntaFactory.crearPregunta(datos);
                });
            }
        }

        @Test
        @DisplayName("Debería lanzar RuntimeException cuando módulo lanza excepción")
        void deberiaLanzarRuntimeExceptionCuandoModuloLanzaExcepcion() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            datos.put("tipo", "test");

            when(preguntaModule.parsePregunta(datos)).thenThrow(new RuntimeException("Error del módulo"));

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When & Then
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                    PreguntaFactory.crearPregunta(datos);
                });
                assertTrue(exception.getMessage().contains("Error del módulo"));
            }
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco del tipo")
        void deberiaRecortarEspaciosEnBlancoDelTipo() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "p1");
            datos.put("tipo", "  test  ");

            when(pregunta.getId()).thenReturn("p1");
            when(preguntaModule.parsePregunta(datos)).thenReturn(pregunta);

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When
                PreguntaFactory.crearPregunta(datos);

                // Then
                verify(moduleManager).findModuleByQuestionType("test");
            }
        }
    }

    @Nested
    @DisplayName("Creación de preguntas básicas")
    class CrearPreguntaBasicaTests {

        @Test
        @DisplayName("Debería crear pregunta básica exitosamente")
        void deberiaCrearPreguntaBasicaExitosamente() {
            // Given
            when(pregunta.getId()).thenReturn("p1");
            when(preguntaModule.createQuestion("p1")).thenReturn(pregunta);

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When
                Pregunta resultado = PreguntaFactory.crearPregunta("test", "p1");

                // Then
                assertNotNull(resultado);
                assertEquals("p1", resultado.getId());
                verify(preguntaModule).createQuestion("p1");
            }
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando tipo es null")
        void deberiaLanzarExcepcionCuandoTipoEsNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(null, "p1");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando tipo está vacío")
        void deberiaLanzarExcepcionCuandoTipoEstaVacio() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta("", "p1");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta("test", null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando ID está vacío")
        void deberiaLanzarExcepcionCuandoIdEstaVacio() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta("test", "");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando no se encuentra módulo")
        void deberiaLanzarExcepcionCuandoNoSeEncuentraModulo() {
            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("tipo_inexistente")).thenReturn(null);

                // When & Then
                assertThrows(IllegalArgumentException.class, () -> {
                    PreguntaFactory.crearPregunta("tipo_inexistente", "p1");
                });
            }
        }

        @Test
        @DisplayName("Debería retornar null cuando módulo no puede crear pregunta")
        void deberiaRetornarNullCuandoModuloNoPuedeCrearPregunta() {
            // Given
            when(preguntaModule.createQuestion("p1")).thenReturn(null);

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When
                Pregunta resultado = PreguntaFactory.crearPregunta("test", "p1");

                // Then
                assertNull(resultado);
            }
        }

        @Test
        @DisplayName("Debería lanzar RuntimeException cuando módulo lanza excepción")
        void deberiaLanzarRuntimeExceptionCuandoModuloLanzaExcepcion() {
            // Given
            when(preguntaModule.createQuestion("p1")).thenThrow(new RuntimeException("Error del módulo"));

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When & Then
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                    PreguntaFactory.crearPregunta("test", "p1");
                });
                assertTrue(exception.getMessage().contains("Error del módulo"));
            }
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco de tipo e ID")
        void deberiaRecortarEspaciosEnBlancoDeTipoEId() {
            // Given
            when(pregunta.getId()).thenReturn("p1");
            when(preguntaModule.createQuestion("p1")).thenReturn(pregunta);

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When
                PreguntaFactory.crearPregunta("  test  ", "  p1  ");

                // Then
                verify(moduleManager).findModuleByQuestionType("test");
                verify(preguntaModule).createQuestion("p1");
            }
        }
    }

    @Nested
    @DisplayName("Verificación de existencia de módulos")
    class VerificacionModulosTests {

        @Test
        @DisplayName("Debería retornar true cuando existe módulo para tipo")
        void deberiaRetornarTrueCuandoExisteModuloParaTipo() {
            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When
                boolean resultado = PreguntaFactory.existeModuloParaTipo("test");

                // Then
                assertTrue(resultado);
            }
        }

        @Test
        @DisplayName("Debería retornar false cuando no existe módulo para tipo")
        void deberiaRetornarFalseCuandoNoExisteModuloParaTipo() {
            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("tipo_inexistente")).thenReturn(null);

                // When
                boolean resultado = PreguntaFactory.existeModuloParaTipo("tipo_inexistente");

                // Then
                assertFalse(resultado);
            }
        }

        @Test
        @DisplayName("Debería retornar false cuando tipo es null")
        void deberiaRetornarFalseCuandoTipoEsNull() {
            // When
            boolean resultado = PreguntaFactory.existeModuloParaTipo(null);

            // Then
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Debería retornar false cuando tipo está vacío")
        void deberiaRetornarFalseCuandoTipoEstaVacio() {
            // When
            boolean resultado = PreguntaFactory.existeModuloParaTipo("");

            // Then
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Debería retornar false cuando tipo solo tiene espacios")
        void deberiaRetornarFalseCuandoTipoSoloTieneEspacios() {
            // When
            boolean resultado = PreguntaFactory.existeModuloParaTipo("   ");

            // Then
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco del tipo")
        void deberiaRecortarEspaciosEnBlancoDelTipo() {
            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.findModuleByQuestionType("test")).thenReturn(preguntaModule);

                // When
                PreguntaFactory.existeModuloParaTipo("  test  ");

                // Then
                verify(moduleManager).findModuleByQuestionType("test");
            }
        }
    }

    @Nested
    @DisplayName("Obtención de tipos soportados")
    class TiposSoportadosTests {

        @Test
        @DisplayName("Debería retornar array de tipos soportados")
        void deberiaRetornarArrayDeTiposSoportados() {
            // Given
            PreguntaModule module1 = mock(PreguntaModule.class);
            PreguntaModule module2 = mock(PreguntaModule.class);
            
            when(module1.getQuestionType()).thenReturn("test");
            when(module2.getQuestionType()).thenReturn("flashcard");

            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.getModules()).thenReturn(List.of(module1, module2));

                // When
                String[] tipos = PreguntaFactory.getTiposSoportados();

                // Then
                assertNotNull(tipos);
                assertEquals(2, tipos.length);
                assertArrayEquals(new String[]{"test", "flashcard"}, tipos);
            }
        }

        @Test
        @DisplayName("Debería retornar array vacío cuando no hay módulos")
        void deberiaRetornarArrayVacioCuandoNoHayModulos() {
            try (MockedStatic<ModuleManager> mockedModuleManager = mockStatic(ModuleManager.class)) {
                mockedModuleManager.when(ModuleManager::getInstance).thenReturn(moduleManager);
                when(moduleManager.getModules()).thenReturn(List.of());

                // When
                String[] tipos = PreguntaFactory.getTiposSoportados();

                // Then
                assertNotNull(tipos);
                assertEquals(0, tipos.length);
            }
        }
    }
} 