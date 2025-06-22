package com.kcursor.factory;

import com.kcursor.domain.Pregunta;
import com.kcursor.modules.PreguntaModule;
import com.kcursor.util.ModuleManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase PreguntaFactory.
 * 
 * <p>Esta clase de pruebas verifica el comportamiento del factory de preguntas,
 * incluyendo:</p>
 * <ul>
 *   <li>Creación de preguntas básicas</li>
 *   <li>Creación de preguntas desde datos YAML</li>
 *   <li>Validaciones de entrada</li>
 *   <li>Manejo de errores</li>
 *   <li>Verificación de módulos</li>
 *   <li>Obtención de tipos soportados</li>
 * </ul>
 * 
 * @author Kursor Team
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de PreguntaFactory")
class PreguntaFactoryTest {

    @Mock
    private PreguntaModule mockModule;

    @Mock
    private Pregunta mockPregunta;

    @BeforeEach
    void setUp() {
        // Configurar comportamiento básico del mock
        when(mockModule.getQuestionType()).thenReturn("test");
        when(mockModule.getModuleName()).thenReturn("Test Module");
        when(mockModule.parsePregunta(anyMap())).thenReturn(mockPregunta);
        when(mockModule.createQuestion(anyString())).thenReturn(mockPregunta);
        
        // Establecer el mock como instancia de ModuleManager para tests
        ModuleManager.setInstance(new TestModuleManager(mockModule));
    }

    @AfterEach
    void tearDown() {
        // Resetear la instancia de ModuleManager después de cada test
        ModuleManager.setInstance(null);
    }

    @Nested
    @DisplayName("Crear pregunta básica")
    class CrearPreguntaBasicaTests {

        @Test
        @DisplayName("Debería crear pregunta básica exitosamente")
        void deberiaCrearPreguntaBasicaExitosamente() {
            // Given
            String id = "pregunta_1";
            String tipo = "test";

            // When
            Pregunta resultado = PreguntaFactory.crearPregunta(tipo, id);

            // Then
            assertNotNull(resultado);
            verify(mockModule).createQuestion(id);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            // Given
            String tipo = "test";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(tipo, null);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID está vacío")
        void deberiaLanzarExcepcionCuandoIdEstaVacio() {
            // Given
            String tipo = "test";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(tipo, "");
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando no se encuentra módulo")
        void deberiaLanzarExcepcionCuandoNoSeEncuentraModulo() {
            // Given
            String id = "pregunta_1";
            String tipo = "tipo_inexistente";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(tipo, id);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando tipo es null")
        void deberiaLanzarExcepcionCuandoTipoEsNull() {
            // Given
            String id = "pregunta_1";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(null, id);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando tipo está vacío")
        void deberiaLanzarExcepcionCuandoTipoEstaVacio() {
            // Given
            String id = "pregunta_1";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta("", id);
            });
        }

        @Test
        @DisplayName("Debería lanzar RuntimeException cuando módulo lanza excepción")
        void deberiaLanzarRuntimeExceptionCuandoModuloLanzaExcepcion() {
            // Given
            String id = "pregunta_1";
            String tipo = "test";
            
            when(mockModule.createQuestion(anyString()))
                .thenThrow(new RuntimeException("Error del módulo"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                PreguntaFactory.crearPregunta(tipo, id);
            });
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco de tipo e ID")
        void deberiaRecortarEspaciosEnBlancoDeTipoEId() {
            // Given
            String id = "  pregunta_1  ";
            String tipo = "  test  ";

            // When
            PreguntaFactory.crearPregunta(tipo, id);

            // Then
            verify(mockModule).createQuestion("pregunta_1");
        }

        @Test
        @DisplayName("Debería retornar null cuando módulo no puede crear pregunta")
        void deberiaRetornarNullCuandoModuloNoPuedeCrearPregunta() {
            // Given
            String id = "pregunta_1";
            String tipo = "test";
            
            when(mockModule.createQuestion(anyString()))
                .thenReturn(null);

            // When
            Pregunta resultado = PreguntaFactory.crearPregunta(tipo, id);

            // Then
            assertNull(resultado);
        }
    }

    @Nested
    @DisplayName("Crear pregunta con datos YAML")
    class CrearPreguntaConDatosYAMLTests {

        @Test
        @DisplayName("Debería crear pregunta desde datos YAML válidos")
        void deberiaCrearPreguntaDesdeDatosYAMLValidos() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_yaml_1");
            datos.put("tipo", "test");
            datos.put("pregunta", "¿Cuál es la capital de Francia?");

            // When
            Pregunta resultado = PreguntaFactory.crearPregunta(datos);

            // Then
            assertNotNull(resultado);
            verify(mockModule).parsePregunta(datos);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando datos son null")
        void deberiaLanzarExcepcionCuandoDatosSonNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta((Map<String, Object>) null);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando no se encuentra módulo")
        void deberiaLanzarExcepcionCuandoNoSeEncuentraModulo() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_1");
            datos.put("tipo", "tipo_inexistente");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando tipo es null")
        void deberiaLanzarExcepcionCuandoTipoEsNull() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_1");
            datos.put("tipo", null);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando tipo está vacío")
        void deberiaLanzarExcepcionCuandoTipoEstaVacio() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_1");
            datos.put("tipo", "");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando tipo solo tiene espacios")
        void deberiaLanzarExcepcionCuandoTipoSoloTieneEspacios() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_1");
            datos.put("tipo", "   ");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar RuntimeException cuando módulo lanza excepción")
        void deberiaLanzarRuntimeExceptionCuandoModuloLanzaExcepcion() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_1");
            datos.put("tipo", "test");
            
            when(mockModule.parsePregunta(anyMap()))
                .thenThrow(new RuntimeException("Error del módulo"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería lanzar RuntimeException cuando módulo retorna null")
        void deberiaLanzarRuntimeExceptionCuandoModuloRetornaNull() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_1");
            datos.put("tipo", "test");
            
            when(mockModule.parsePregunta(anyMap()))
                .thenReturn(null);

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                PreguntaFactory.crearPregunta(datos);
            });
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco del tipo")
        void deberiaRecortarEspaciosEnBlancoDelTipo() {
            // Given
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", "pregunta_1");
            datos.put("tipo", "  test  ");

            // When
            PreguntaFactory.crearPregunta(datos);

            // Then
            verify(mockModule).parsePregunta(datos);
        }
    }

    @Nested
    @DisplayName("Tipos soportados")
    class TiposSoportadosTests {

        @Test
        @DisplayName("Debería retornar array de tipos soportados")
        void deberiaRetornarArrayDeTiposSoportados() {
            // When
            String[] tipos = PreguntaFactory.getTiposSoportados();

            // Then
            assertNotNull(tipos);
            assertEquals(1, tipos.length);
            assertEquals("test", tipos[0]);
        }

        @Test
        @DisplayName("Debería retornar array vacío cuando no hay módulos")
        void deberiaRetornarArrayVacioCuandoNoHayModulos() {
            // Given
            ModuleManager.setInstance(new TestModuleManager(null));

            // When
            String[] tipos = PreguntaFactory.getTiposSoportados();

            // Then
            assertNotNull(tipos);
            assertEquals(0, tipos.length);
        }
    }

    @Nested
    @DisplayName("Verificación de módulos")
    class VerificacionModulosTests {

        @Test
        @DisplayName("Debería retornar true cuando existe módulo para tipo")
        void deberiaRetornarTrueCuandoExisteModuloParaTipo() {
            // When
            boolean resultado = PreguntaFactory.existeModuloParaTipo("test");

            // Then
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Debería retornar false cuando no existe módulo para tipo")
        void deberiaRetornarFalseCuandoNoExisteModuloParaTipo() {
            // When
            boolean resultado = PreguntaFactory.existeModuloParaTipo("tipo_inexistente");

            // Then
            assertFalse(resultado);
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
            // When
            boolean resultado = PreguntaFactory.existeModuloParaTipo("  test  ");

            // Then
            assertTrue(resultado);
        }
    }

    /**
     * Clase auxiliar para tests que simula ModuleManager
     */
    private static class TestModuleManager extends ModuleManager {
        private final PreguntaModule testModule;

        public TestModuleManager(PreguntaModule testModule) {
            this.testModule = testModule;
        }

        @Override
        public PreguntaModule findModuleByQuestionType(String questionType) {
            if (testModule != null && "test".equals(questionType)) {
                return testModule;
            }
            return null;
        }

        @Override
        public java.util.List<PreguntaModule> getModules() {
            if (testModule != null) {
                return java.util.List.of(testModule);
            }
            return java.util.List.of();
        }
    }
} 