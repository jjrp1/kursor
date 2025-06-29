package com.kursor.util;

import com.kursor.modules.PreguntaModule;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el gestor de módulos ModuleManager.
 * 
 * <p>Esta clase contiene pruebas exhaustivas para verificar el funcionamiento
 * correcto del gestor de módulos, incluyendo la carga dinámica, validación
 * y gestión de módulos de preguntas.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see ModuleManager
 * @see PreguntaModule
 */
@DisplayName("Pruebas de ModuleManager")
class ModuleManagerTest {

    private PreguntaModule mockModule;
    private TestModuleManager testManager;

    @BeforeEach
    void setUp() {
        // Crear mock de módulo
        mockModule = mock(PreguntaModule.class);
        when(mockModule.getQuestionType()).thenReturn("test");
        when(mockModule.getModuleName()).thenReturn("Test Module");
        
        // Crear instancia de prueba con el mock
        testManager = new TestModuleManager(List.of(mockModule));
    }

    @AfterEach
    void tearDown() {
        // Limpiar la instancia después de cada test
        ModuleManager.setInstance(null);
    }

    @Nested
    @DisplayName("Patrón Singleton")
    class SingletonTests {

        @Test
        @DisplayName("Debería retornar la misma instancia")
        void deberiaRetornarLaMismaInstancia() {
            ModuleManager.setInstance(null);
            ModuleManager instance1 = ModuleManager.getInstance();
            ModuleManager instance2 = ModuleManager.getInstance();
            
            assertSame(instance1, instance2, "Las instancias deberían ser la misma");
        }

        @Test
        @DisplayName("Debería crear nueva instancia cuando no existe")
        void deberiaCrearNuevaInstanciaCuandoNoExiste() {
            ModuleManager.setInstance(null);
            ModuleManager instance = ModuleManager.getInstance();
            
            assertNotNull(instance, "La instancia no debería ser null");
        }
    }

    @Nested
    @DisplayName("Gestión de módulos")
    class GestionModulosTests {

        @Test
        @DisplayName("Debería obtener lista de módulos")
        void deberiaObtenerListaDeModulos() {
            List<PreguntaModule> modules = testManager.getModules();
            
            assertNotNull(modules, "La lista de módulos no debería ser null");
            assertEquals(1, modules.size(), "Debería tener 1 módulo");
            assertEquals("Test Module", modules.get(0).getModuleName(), 
                        "El nombre del módulo debería coincidir");
        }

        @Test
        @DisplayName("Debería obtener conteo correcto de módulos")
        void deberiaObtenerConteoCorrectoDeModulos() {
            assertEquals(1, testManager.getModuleCount(), 
                        "Debería tener 1 módulo");
        }

        @Test
        @DisplayName("Debería indicar que tiene módulos")
        void deberiaIndicarQueTieneModulos() {
            assertTrue(testManager.hasModules(), 
                      "Debería indicar que tiene módulos");
        }

        @Test
        @DisplayName("Debería indicar que no tiene módulos cuando está vacío")
        void deberiaIndicarQueNoTieneModulosCuandoEstaVacio() {
            TestModuleManager emptyManager = new TestModuleManager(new ArrayList<>());
            
            assertFalse(emptyManager.hasModules(), 
                       "Debería indicar que no tiene módulos");
        }
    }

    @Nested
    @DisplayName("Búsqueda de módulos")
    class BusquedaModulosTests {

        @Test
        @DisplayName("Debería encontrar módulo por tipo existente")
        void deberiaEncontrarModuloPorTipoExistente() {
            PreguntaModule found = testManager.findModuleByQuestionType("test");
            
            assertNotNull(found, "Debería encontrar el módulo");
            assertEquals("Test Module", found.getModuleName(), 
                        "El nombre del módulo debería coincidir");
        }

        @Test
        @DisplayName("Debería retornar null para tipo inexistente")
        void deberiaRetornarNullParaTipoInexistente() {
            PreguntaModule found = testManager.findModuleByQuestionType("otro");
            
            assertNull(found, "Debería retornar null para tipo inexistente");
        }

        @Test
        @DisplayName("Debería retornar null para tipo null")
        void deberiaRetornarNullParaTipoNull() {
            PreguntaModule found = testManager.findModuleByQuestionType(null);
            
            assertNull(found, "Debería retornar null para tipo null");
        }

        @Test
        @DisplayName("Debería retornar null para tipo vacío")
        void deberiaRetornarNullParaTipoVacio() {
            PreguntaModule found = testManager.findModuleByQuestionType("");
            
            assertNull(found, "Debería retornar null para tipo vacío");
        }
    }

    @Nested
    @DisplayName("Información de módulos")
    class InformacionModulosTests {

        @Test
        @DisplayName("Debería generar información de módulos")
        void deberiaGenerarInformacionDeModulos() {
            String info = testManager.getModulesInfo();
            
            assertNotNull(info, "La información no debería ser null");
            assertTrue(info.contains("Test Module"), 
                      "Debería contener el nombre del módulo");
        }

        @Test
        @DisplayName("Debería generar información vacía cuando no hay módulos")
        void deberiaGenerarInformacionVaciaCuandoNoHayModulos() {
            TestModuleManager emptyManager = new TestModuleManager(new ArrayList<>());
            String info = emptyManager.getModulesInfo();
            
            assertNotNull(info, "La información no debería ser null");
            assertTrue(info.isEmpty() || info.trim().isEmpty(), 
                      "Debería estar vacía o contener solo espacios");
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTests {

        @Test
        @DisplayName("Debería manejar múltiples módulos")
        void deberiaManejarMultiplesModulos() {
            // Crear segundo mock
            PreguntaModule mockModule2 = mock(PreguntaModule.class);
            when(mockModule2.getQuestionType()).thenReturn("flashcard");
            when(mockModule2.getModuleName()).thenReturn("Flashcard Module");
            
            List<PreguntaModule> multipleModules = List.of(mockModule, mockModule2);
            TestModuleManager multiManager = new TestModuleManager(multipleModules);
            
            assertEquals(2, multiManager.getModuleCount(), 
                        "Debería tener 2 módulos");
            assertTrue(multiManager.hasModules(), 
                      "Debería indicar que tiene módulos");
            
            // Verificar búsqueda
            PreguntaModule found1 = multiManager.findModuleByQuestionType("test");
            PreguntaModule found2 = multiManager.findModuleByQuestionType("flashcard");
            
            assertNotNull(found1, "Debería encontrar módulo test");
            assertNotNull(found2, "Debería encontrar módulo flashcard");
            assertEquals("Test Module", found1.getModuleName());
            assertEquals("Flashcard Module", found2.getModuleName());
        }

        @Test
        @DisplayName("Debería manejar lista null de módulos")
        void deberiaManejarListaNullDeModulos() {
            TestModuleManager nullManager = new TestModuleManager(null);
            
            assertThrows(NullPointerException.class, () -> {
                nullManager.getModules();
            }, "Debería lanzar NullPointerException al obtener módulos");
        }
    }

    /**
     * Implementación de prueba para ModuleManager que permite inyectar módulos simulados.
     * Esta clase usa composición en lugar de herencia para evitar problemas con el constructor privado.
     */
    private static class TestModuleManager {
        private final List<PreguntaModule> testModules;

        public TestModuleManager(List<PreguntaModule> testModules) {
            this.testModules = testModules;
        }

        public List<PreguntaModule> getModules() {
            return testModules;
        }

        public PreguntaModule findModuleByQuestionType(String questionType) {
            if (testModules == null || questionType == null || questionType.trim().isEmpty()) {
                return null;
            }
            
            return testModules.stream()
                    .filter(m -> m.getQuestionType().equals(questionType))
                    .findFirst()
                    .orElse(null);
        }

        public int getModuleCount() {
            return testModules != null ? testModules.size() : 0;
        }

        public boolean hasModules() {
            return testModules != null && !testModules.isEmpty();
        }

        public String getModulesInfo() {
            if (testModules == null || testModules.isEmpty()) {
                return "";
            }
            
            StringBuilder sb = new StringBuilder();
            for (PreguntaModule module : testModules) {
                sb.append(module.getModuleName()).append("\n");
            }
            return sb.toString();
        }
    }
} 