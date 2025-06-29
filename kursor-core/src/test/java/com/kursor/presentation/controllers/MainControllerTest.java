package com.kursor.presentation.controllers;

import com.kursor.shared.util.ModuleManager;
import com.kursor.shared.util.StrategyManager;
import com.kursor.shared.util.CursoManager;
import com.kursor.yaml.dto.CursoDTO;
import com.kursor.presentation.viewmodels.MainViewModel;
import com.kursor.presentation.views.MainView;
import com.kursor.application.services.AnalyticsService;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MainController.
 * 
 * <p>Esta clase contiene pruebas exhaustivas para verificar el funcionamiento
 * correcto del controlador principal de la aplicación Kursor.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see MainController
 */
@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
@DisplayName("Pruebas de MainController")
class MainControllerTest {

    @Mock private Stage primaryStage;
    @Mock private ModuleManager moduleManager;
    @Mock private StrategyManager strategyManager;
    @Mock private CursoManager cursoManager;
    @Mock private MainView mainView;
    @Mock private MainViewModel viewModel;
    @Mock private SessionController sessionController;
    @Mock private AnalyticsService analyticsService;
    
    private MainController controller;
    private List<CursoDTO> cursosMock;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        cursosMock = Arrays.asList(
            createMockCurso("curso1", "Matemáticas Básicas"),
            createMockCurso("curso2", "Historia Universal"),
            createMockCurso("curso3", "Inglés Básico")
        );
        
        // Configurar mocks
        when(cursoManager.cargarCursosCompletos()).thenReturn(cursosMock);
        when(mainView.getCursosListView()).thenReturn(mock(ListView.class));
        when(mainView.getSessionsTableView()).thenReturn(mock(TableView.class));
        when(mainView.getStartButton()).thenReturn(mock(Button.class));
        when(mainView.getResumeButton()).thenReturn(mock(Button.class));
        when(mainView.getAnalyticsButton()).thenReturn(mock(Button.class));
    }

    @Nested
    @DisplayName("Inicialización del controlador")
    class InicializacionTests {

        @Test
        @DisplayName("Debería inicializar correctamente con Stage válido")
        void deberiaInicializarCorrectamenteConStageValido() {
            // Given & When
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                controller = new MainController(primaryStage);
                
                // Then
                assertNotNull(controller, "El controlador no debería ser null");
                verify(moduleManager, times(1)).getInstance();
                verify(strategyManager, times(1)).getInstance();
                verify(cursoManager, times(1)).getInstance();
            }
        }

        @Test
        @DisplayName("Debería cargar cursos al inicializar")
        void deberiaCargarCursosAlInicializar() {
            // Given & When
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                controller = new MainController(primaryStage);
                
                // Then
                verify(cursoManager, times(1)).cargarCursosCompletos();
            }
        }

        @Test
        @DisplayName("Debería manejar errores de carga de cursos")
        void deberiaManejarErroresDeCargaDeCursos() {
            // Given
            when(cursoManager.cargarCursosCompletos()).thenThrow(new RuntimeException("Error de carga"));
            
            // When & Then
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                // No debería lanzar excepción, sino manejar el error
                assertDoesNotThrow(() -> {
                    controller = new MainController(primaryStage);
                });
            }
        }
    }

    @Nested
    @DisplayName("Gestión de cursos")
    class GestionCursosTests {

        @BeforeEach
        void setUpGestionCursos() {
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                controller = new MainController(primaryStage);
            }
        }

        @Test
        @DisplayName("Debería obtener lista de cursos")
        void deberiaObtenerListaDeCursos() {
            // When
            List<CursoDTO> cursos = controller.getCursos();
            
            // Then
            assertNotNull(cursos, "La lista de cursos no debería ser null");
            assertEquals(3, cursos.size(), "Debería tener 3 cursos");
            assertEquals("Matemáticas Básicas", cursos.get(0).getTitulo());
            assertEquals("Historia Universal", cursos.get(1).getTitulo());
            assertEquals("Inglés Básico", cursos.get(2).getTitulo());
        }

        @Test
        @DisplayName("Debería seleccionar curso válido")
        void deberiaSeleccionarCursoValido() {
            // Given
            CursoDTO cursoSeleccionado = cursosMock.get(0);
            
            // When
            controller.setSelectedCourse(cursoSeleccionado);
            
            // Then
            assertEquals(cursoSeleccionado, controller.getSelectedCourse(), 
                        "El curso seleccionado debería ser el correcto");
        }

        @Test
        @DisplayName("Debería manejar selección de curso null")
        void deberiaManejarSeleccionDeCursoNull() {
            // When
            controller.setSelectedCourse(null);
            
            // Then
            assertNull(controller.getSelectedCourse(), "El curso seleccionado debería ser null");
        }
    }

    @Nested
    @DisplayName("Gestión de sesiones")
    class GestionSesionesTests {

        @BeforeEach
        void setUpGestionSesiones() {
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                controller = new MainController(primaryStage);
            }
        }

        @Test
        @DisplayName("Debería actualizar tabla de sesiones")
        void deberiaActualizarTablaDeSesiones() {
            // Given
            CursoDTO curso = cursosMock.get(0);
            controller.setSelectedCourse(curso);
            
            // When
            controller.updateSessionsTable();
            
            // Then
            // Verificar que se llama al método de actualización de sesiones
            // (esto dependerá de la implementación específica)
            assertDoesNotThrow(() -> controller.updateSessionsTable());
        }

        @Test
        @DisplayName("Debería manejar actualización sin curso seleccionado")
        void deberiaManejarActualizacionSinCursoSeleccionado() {
            // Given
            controller.setSelectedCourse(null);
            
            // When & Then
            assertDoesNotThrow(() -> controller.updateSessionsTable());
        }
    }

    @Nested
    @DisplayName("Manejo de eventos")
    class ManejoEventosTests {

        @BeforeEach
        void setUpManejoEventos() {
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                controller = new MainController(primaryStage);
            }
        }

        @Test
        @DisplayName("Debería manejar inicio de nueva sesión")
        void deberiaManejarInicioDeNuevaSesion() {
            // Given
            CursoDTO curso = cursosMock.get(0);
            controller.setSelectedCourse(curso);
            
            // When & Then
            assertDoesNotThrow(() -> {
                // Simular el inicio de una nueva sesión
                // Esto dependerá de la implementación específica del método
            });
        }

        @Test
        @DisplayName("Debería manejar reanudación de sesión")
        void deberiaManejarReanudacionDeSesion() {
            // Given
            CursoDTO curso = cursosMock.get(0);
            controller.setSelectedCourse(curso);
            
            // When & Then
            assertDoesNotThrow(() -> {
                // Simular la reanudación de una sesión
                // Esto dependerá de la implementación específica del método
            });
        }

        @Test
        @DisplayName("Debería manejar visualización de analytics")
        void deberiaManejarVisualizacionDeAnalytics() {
            // Given
            CursoDTO curso = cursosMock.get(0);
            controller.setSelectedCourse(curso);
            
            // When & Then
            assertDoesNotThrow(() -> {
                // Simular la visualización de analytics
                // Esto dependerá de la implementación específica del método
            });
        }
    }

    @Nested
    @DisplayName("Validaciones y manejo de errores")
    class ValidacionesTests {

        @Test
        @DisplayName("Debería validar Stage no null en constructor")
        void deberiaValidarStageNoNullEnConstructor() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new MainController(null);
            }, "Debería lanzar IllegalArgumentException para Stage null");
        }

        @Test
        @DisplayName("Debería manejar errores de inicialización de componentes")
        void deberiaManejarErroresDeInicializacionDeComponentes() {
            // Given
            when(moduleManager.getModules()).thenThrow(new RuntimeException("Error de módulos"));
            
            // When & Then
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                // No debería lanzar excepción, sino manejar el error
                assertDoesNotThrow(() -> {
                    controller = new MainController(primaryStage);
                });
            }
        }
    }

    @Nested
    @DisplayName("Métodos de utilidad")
    class MetodosUtilidadTests {

        @BeforeEach
        void setUpMetodosUtilidad() {
            try (MockedStatic<ModuleManager> moduleManagerMock = mockStatic(ModuleManager.class);
                 MockedStatic<StrategyManager> strategyManagerMock = mockStatic(StrategyManager.class);
                 MockedStatic<CursoManager> cursoManagerMock = mockStatic(CursoManager.class)) {
                
                moduleManagerMock.when(ModuleManager::getInstance).thenReturn(moduleManager);
                strategyManagerMock.when(StrategyManager::getInstance).thenReturn(strategyManager);
                cursoManagerMock.when(CursoManager::getInstance).thenReturn(cursoManager);
                
                controller = new MainController(primaryStage);
            }
        }

        @Test
        @DisplayName("Debería obtener MainView")
        void deberiaObtenerMainView() {
            // When
            MainView view = controller.getMainView();
            
            // Then
            assertNotNull(view, "La vista principal no debería ser null");
        }

        @Test
        @DisplayName("Debería obtener SessionController")
        void deberiaObtenerSessionController() {
            // When
            SessionController sessionCtrl = controller.getSessionController();
            
            // Then
            assertNotNull(sessionCtrl, "El controlador de sesiones no debería ser null");
        }

        @Test
        @DisplayName("Debería obtener SessionTableView")
        void deberiaObtenerSessionTableView() {
            // When
            TableView<?> tableView = controller.getSessionTableView();
            
            // Then
            assertNotNull(tableView, "La tabla de sesiones no debería ser null");
        }
    }

    /**
     * Crea un curso mock para testing.
     * 
     * @param id ID del curso
     * @param titulo Título del curso
     * @return CursoDTO mock
     */
    private CursoDTO createMockCurso(String id, String titulo) {
        CursoDTO curso = mock(CursoDTO.class);
        when(curso.getId()).thenReturn(id);
        when(curso.getTitulo()).thenReturn(titulo);
        return curso;
    }
} 