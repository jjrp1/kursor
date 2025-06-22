package com.kursor.util;

import com.kursor.domain.Curso;
import com.kursor.yaml.dto.CursoDTO;
import com.kursor.service.CursoPreviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase CursoManager.
 * 
 * <p>Esta clase de pruebas verifica el comportamiento del gestor de cursos,
 * incluyendo:</p>
 * <ul>
 *   <li>Patrón Singleton</li>
 *   <li>Carga de cursos disponibles</li>
 *   <li>Carga de cursos completos</li>
 *   <li>Manejo de errores</li>
 *   <li>Logging de operaciones</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CursoManager")
class CursoManagerTest {

    @Mock
    private CursoPreviewService cursoPreviewService;

    @Mock
    private CursoDTO curso1;

    @Mock
    private CursoDTO curso2;

    @Mock
    private Curso cursoCompleto;

    @BeforeEach
    void setUp() {
        // Configurar comportamiento básico de los mocks
        when(curso1.getTitulo()).thenReturn("Curso de Inglés");
        when(curso2.getTitulo()).thenReturn("Curso de Matemáticas");
        when(cursoCompleto.getTitulo()).thenReturn("Curso Completo");
    }

    @Nested
    @DisplayName("Patrón Singleton")
    class SingletonTests {

        @Test
        @DisplayName("Debería retornar la misma instancia en múltiples llamadas")
        void deberiaRetornarMismaInstancia() {
            // When
            CursoManager instance1 = CursoManager.getInstance();
            CursoManager instance2 = CursoManager.getInstance();
            CursoManager instance3 = CursoManager.getInstance();

            // Then
            assertSame(instance1, instance2);
            assertSame(instance2, instance3);
            assertSame(instance1, instance3);
        }

        @Test
        @DisplayName("Debería crear instancia única al primer acceso")
        void deberiaCrearInstanciaUnicaAlPrimerAcceso() {
            // When
            CursoManager instance = CursoManager.getInstance();

            // Then
            assertNotNull(instance);
        }
    }

    @Nested
    @DisplayName("Carga de cursos disponibles")
    class CargarCursosTests {

        @Test
        @DisplayName("Debería cargar cursos exitosamente")
        void deberiaCargarCursosExitosamente() {
            // Given
            List<Curso> cursosEsperados = List.of(cursoCompleto);
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenReturn(cursosEsperados);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenReturn(cursosEsperados);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> resultado = manager.cargarCursos();

                // Then
                assertNotNull(resultado);
                assertEquals(1, resultado.size());
            }
        }

        @Test
        @DisplayName("Debería retornar lista vacía cuando no hay cursos")
        void deberiaRetornarListaVaciaCuandoNoHayCursos() {
            // Given
            List<Curso> cursosVacios = List.of();
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenReturn(cursosVacios);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenReturn(cursosVacios);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> resultado = manager.cargarCursos();

                // Then
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
            }
        }

        @Test
        @DisplayName("Debería retornar lista vacía cuando ocurre excepción")
        void deberiaRetornarListaVaciaCuandoOcurreExcepcion() {
            // Given
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenThrow(new RuntimeException("Error de carga"));

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenThrow(new RuntimeException("Error de carga"));
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> resultado = manager.cargarCursos();

                // Then
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
            }
        }

        @Test
        @DisplayName("Debería manejar excepción de NullPointerException")
        void deberiaManejarExcepcionNullPointerException() {
            // Given
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenThrow(new NullPointerException("NPE"));

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenThrow(new NullPointerException("NPE"));
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> resultado = manager.cargarCursos();

                // Then
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
            }
        }

        @Test
        @DisplayName("Debería manejar excepción de IllegalArgumentException")
        void deberiaManejarExcepcionIllegalArgumentException() {
            // Given
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenThrow(new IllegalArgumentException("Argumento inválido"));

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenThrow(new IllegalArgumentException("Argumento inválido"));
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> resultado = manager.cargarCursos();

                // Then
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Carga de cursos completos")
    class ObtenerCursoCompletoTests {

        @Test
        @DisplayName("Debería cargar curso completo exitosamente")
        void deberiaCargarCursoCompletoExitosamente() {
            // Given
            String cursoId = "curso_ingles";
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenReturn(cursoCompleto);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(cursoId)).thenReturn(cursoCompleto);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNotNull(resultado);
                assertEquals("Curso Completo", resultado.getTitulo());
            }
        }

        @Test
        @DisplayName("Debería retornar null cuando curso no existe")
        void deberiaRetornarNullCuandoCursoNoExiste() {
            // Given
            String cursoId = "curso_inexistente";
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenReturn(null);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(cursoId)).thenReturn(null);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNull(resultado);
            }
        }

        @Test
        @DisplayName("Debería retornar null cuando ocurre excepción")
        void deberiaRetornarNullCuandoOcurreExcepcion() {
            // Given
            String cursoId = "curso_error";
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenThrow(new RuntimeException("Error de carga"));

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(cursoId)).thenThrow(new RuntimeException("Error de carga"));
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNull(resultado);
            }
        }

        @Test
        @DisplayName("Debería manejar excepción de NullPointerException")
        void deberiaManejarExcepcionNullPointerException() {
            // Given
            String cursoId = "curso_npe";
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenThrow(new NullPointerException("NPE"));

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(cursoId)).thenThrow(new NullPointerException("NPE"));
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNull(resultado);
            }
        }

        @Test
        @DisplayName("Debería manejar excepción de IllegalArgumentException")
        void deberiaManejarExcepcionIllegalArgumentException() {
            // Given
            String cursoId = "curso_invalido";
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenThrow(new IllegalArgumentException("ID inválido"));

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(cursoId)).thenThrow(new IllegalArgumentException("ID inválido"));
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNull(resultado);
            }
        }

        @Test
        @DisplayName("Debería manejar ID con espacios en blanco")
        void deberiaManejarIdConEspaciosEnBlanco() {
            // Given
            String cursoId = "  curso_espacios  ";
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenReturn(cursoCompleto);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(cursoId)).thenReturn(cursoCompleto);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNotNull(resultado);
            }
        }

        @Test
        @DisplayName("Debería manejar ID vacío")
        void deberiaManejarIdVacio() {
            // Given
            String cursoId = "";
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenReturn(null);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(cursoId)).thenReturn(null);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNull(resultado);
            }
        }

        @Test
        @DisplayName("Debería manejar ID null")
        void deberiaManejarIdNull() {
            // Given
            when(cursoPreviewService.cargarCursoCompleto(null)).thenReturn(null);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarCursoCompleto(null)).thenReturn(null);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                Curso resultado = manager.obtenerCursoCompleto(null);

                // Then
                assertNull(resultado);
            }
        }
    }

    @Nested
    @DisplayName("Casos edge y validaciones")
    class CasosEdgeTests {

        @Test
        @DisplayName("Debería manejar múltiples llamadas consecutivas")
        void deberiaManejarMultiplesLlamadasConsecutivas() {
            // Given
            List<Curso> cursos = List.of(cursoCompleto);
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenReturn(cursos);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenReturn(cursos);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> resultado1 = manager.cargarCursos();
                List<CursoDTO> resultado2 = manager.cargarCursos();
                List<CursoDTO> resultado3 = manager.cargarCursos();

                // Then
                assertNotNull(resultado1);
                assertNotNull(resultado2);
                assertNotNull(resultado3);
                assertEquals(1, resultado1.size());
                assertEquals(1, resultado2.size());
                assertEquals(1, resultado3.size());
            }
        }

        @Test
        @DisplayName("Debería manejar llamadas mixtas de carga y obtención")
        void deberiaManejarLlamadasMixtas() {
            // Given
            List<Curso> cursos = List.of(cursoCompleto);
            String cursoId = "curso_test";
            
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenReturn(cursos);
            when(cursoPreviewService.cargarCursoCompleto(cursoId)).thenReturn(cursoCompleto);

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenReturn(cursos);
                        when(mock.cargarCursoCompleto(cursoId)).thenReturn(cursoCompleto);
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> cursosList = manager.cargarCursos();
                Curso cursoCompleto = manager.obtenerCursoCompleto(cursoId);

                // Then
                assertNotNull(cursosList);
                assertNotNull(cursoCompleto);
                assertEquals(1, cursosList.size());
            }
        }

        @Test
        @DisplayName("Debería manejar excepciones en cascada")
        void deberiaManejarExcepcionesEnCascada() {
            // Given
            when(cursoPreviewService.cargarTodosLosCursosCompletos()).thenThrow(new RuntimeException("Error 1"));
            when(cursoPreviewService.cargarCursoCompleto("curso1")).thenThrow(new RuntimeException("Error 2"));

            try (MockedConstruction<CursoPreviewService> mockedConstruction = 
                    mockConstruction(CursoPreviewService.class, (mock, context) -> {
                        when(mock.cargarTodosLosCursosCompletos()).thenThrow(new RuntimeException("Error 1"));
                        when(mock.cargarCursoCompleto("curso1")).thenThrow(new RuntimeException("Error 2"));
                    })) {

                // When
                CursoManager manager = CursoManager.getInstance();
                List<CursoDTO> cursos = manager.cargarCursos();
                Curso curso = manager.obtenerCursoCompleto("curso1");

                // Then
                assertNotNull(cursos);
                assertTrue(cursos.isEmpty());
                assertNull(curso);
            }
        }
    }
} 