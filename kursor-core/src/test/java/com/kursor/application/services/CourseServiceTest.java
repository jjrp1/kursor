package com.kursor.application.services;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.infrastructure.persistence.CourseRepository;
import com.kursor.shared.util.CursoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CourseService")
class CourseServiceTest {

    @Mock private CourseRepository courseRepository;
    @Mock private CursoManager cursoManager;
    
    private CourseService service;
    private List<CursoDTO> cursosMock;

    @BeforeEach
    void setUp() {
        service = new CourseService(courseRepository, cursoManager);
        
        cursosMock = Arrays.asList(
            createMockCurso("curso1", "Matemáticas Básicas", "Curso introductorio"),
            createMockCurso("curso2", "Historia Universal", "Historia general"),
            createMockCurso("curso3", "Inglés Básico", "Inglés para principiantes")
        );
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear servicio con repositorio y gestor válidos")
        void deberiaCrearServicioConRepositorioYGestorValidos() {
            CourseService testService = new CourseService(courseRepository, cursoManager);
            
            assertNotNull(testService);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando repositorio es null")
        void deberiaLanzarExcepcionCuandoRepositorioEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new CourseService(null, cursoManager);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando gestor es null")
        void deberiaLanzarExcepcionCuandoGestorEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new CourseService(courseRepository, null);
            });
        }
    }

    @Nested
    @DisplayName("Carga de cursos")
    class CargaCursosTests {

        @Test
        @DisplayName("Debería cargar todos los cursos exitosamente")
        void deberiaCargarTodosLosCursosExitosamente() throws Exception {
            when(courseRepository.findAll()).thenReturn(cursosMock);
            
            List<CursoDTO> cursos = service.loadAllCourses();
            
            assertNotNull(cursos);
            assertEquals(3, cursos.size());
            assertEquals("Matemáticas Básicas", cursos.get(0).getTitulo());
            verify(courseRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debería manejar error del repositorio")
        void deberiaManejarErrorDelRepositorio() {
            when(courseRepository.findAll()).thenThrow(new CourseRepository.RepositoryException("Error de BD"));
            
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.loadAllCourses();
            });
            
            assertTrue(exception.getMessage().contains("Error al cargar los cursos"));
        }

        @Test
        @DisplayName("Debería retornar lista vacía cuando no hay cursos")
        void deberiaRetornarListaVaciaCuandoNoHayCursos() throws Exception {
            when(courseRepository.findAll()).thenReturn(List.of());
            
            List<CursoDTO> cursos = service.loadAllCourses();
            
            assertNotNull(cursos);
            assertTrue(cursos.isEmpty());
        }
    }

    @Nested
    @DisplayName("Búsqueda de cursos")
    class BusquedaCursosTests {

        @Test
        @DisplayName("Debería cargar curso por ID exitosamente")
        void deberiaCargarCursoPorIdExitosamente() throws Exception {
            CursoDTO curso = cursosMock.get(0);
            when(courseRepository.findById("curso1")).thenReturn(Optional.of(curso));
            
            CursoDTO resultado = service.loadCourseById("curso1");
            
            assertNotNull(resultado);
            assertEquals("curso1", resultado.getId());
            assertEquals("Matemáticas Básicas", resultado.getTitulo());
            verify(courseRepository, times(1)).findById("curso1");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.loadCourseById(null);
            });
            
            assertTrue(exception.getMessage().contains("ID de curso no puede ser null"));
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID está vacío")
        void deberiaLanzarExcepcionCuandoIdEstaVacio() {
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.loadCourseById("");
            });
            
            assertTrue(exception.getMessage().contains("ID de curso no puede ser null"));
        }

        @Test
        @DisplayName("Debería retornar null cuando curso no existe en repositorio")
        void deberiaRetornarNullCuandoCursoNoExisteEnRepositorio() throws Exception {
            when(courseRepository.findById("curso_inexistente")).thenReturn(Optional.empty());
            when(cursoManager.cargarCursosCompletos()).thenReturn(cursosMock);
            
            CursoDTO resultado = service.loadCourseById("curso_inexistente");
            
            assertNull(resultado);
            verify(courseRepository, times(1)).findById("curso_inexistente");
            verify(cursoManager, times(1)).cargarCursosCompletos();
        }

        @Test
        @DisplayName("Debería buscar curso en CursoManager como fallback")
        void deberiaBuscarCursoEnCursoManagerComoFallback() throws Exception {
            when(courseRepository.findById("curso1")).thenReturn(Optional.empty());
            when(cursoManager.cargarCursosCompletos()).thenReturn(cursosMock);
            
            CursoDTO resultado = service.loadCourseById("curso1");
            
            assertNotNull(resultado);
            assertEquals("curso1", resultado.getId());
            verify(cursoManager, times(1)).cargarCursosCompletos();
        }
    }

    @Nested
    @DisplayName("Guardado de cursos")
    class GuardadoCursosTests {

        @Test
        @DisplayName("Debería guardar curso exitosamente")
        void deberiaGuardarCursoExitosamente() throws Exception {
            CursoDTO curso = createMockCurso("nuevo", "Nuevo Curso", "Descripción");
            when(courseRepository.save(curso)).thenReturn(curso);
            
            CursoDTO resultado = service.saveCourse(curso);
            
            assertNotNull(resultado);
            assertEquals("nuevo", resultado.getId());
            verify(courseRepository, times(1)).save(curso);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando curso es null")
        void deberiaLanzarExcepcionCuandoCursoEsNull() {
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.saveCourse(null);
            });
            
            assertTrue(exception.getMessage().contains("Curso no puede ser null"));
        }

        @Test
        @DisplayName("Debería manejar error del repositorio al guardar")
        void deberiaManejarErrorDelRepositorioAlGuardar() {
            CursoDTO curso = createMockCurso("nuevo", "Nuevo Curso", "Descripción");
            when(courseRepository.save(curso)).thenThrow(new CourseRepository.RepositoryException("Error de BD"));
            
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.saveCourse(curso);
            });
            
            assertTrue(exception.getMessage().contains("Error al guardar el curso"));
        }
    }

    @Nested
    @DisplayName("Eliminación de cursos")
    class EliminacionCursosTests {

        @Test
        @DisplayName("Debería eliminar curso exitosamente")
        void deberiaEliminarCursoExitosamente() throws Exception {
            when(courseRepository.deleteById("curso1")).thenReturn(true);
            
            boolean resultado = service.deleteCourse("curso1");
            
            assertTrue(resultado);
            verify(courseRepository, times(1)).deleteById("curso1");
        }

        @Test
        @DisplayName("Debería retornar false cuando curso no existe")
        void deberiaRetornarFalseCuandoCursoNoExiste() throws Exception {
            when(courseRepository.deleteById("curso_inexistente")).thenReturn(false);
            
            boolean resultado = service.deleteCourse("curso_inexistente");
            
            assertFalse(resultado);
            verify(courseRepository, times(1)).deleteById("curso_inexistente");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.deleteCourse(null);
            });
            
            assertTrue(exception.getMessage().contains("ID de curso no puede ser null"));
        }

        @Test
        @DisplayName("Debería manejar error del repositorio al eliminar")
        void deberiaManejarErrorDelRepositorioAlEliminar() {
            when(courseRepository.deleteById("curso1")).thenThrow(new CourseRepository.RepositoryException("Error de BD"));
            
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.deleteCourse("curso1");
            });
            
            assertTrue(exception.getMessage().contains("Error al eliminar el curso"));
        }
    }

    @Nested
    @DisplayName("Búsqueda por término")
    class BusquedaPorTerminoTests {

        @Test
        @DisplayName("Debería buscar cursos por término exitosamente")
        void deberiaBuscarCursosPorTerminoExitosamente() throws Exception {
            List<CursoDTO> resultados = Arrays.asList(cursosMock.get(0));
            when(courseRepository.searchByTerm("matemáticas")).thenReturn(resultados);
            
            List<CursoDTO> cursos = service.searchCourses("matemáticas");
            
            assertNotNull(cursos);
            assertEquals(1, cursos.size());
            assertEquals("Matemáticas Básicas", cursos.get(0).getTitulo());
            verify(courseRepository, times(1)).searchByTerm("matemáticas");
        }

        @Test
        @DisplayName("Debería manejar término de búsqueda null")
        void deberiaManejarTerminoDeBusquedaNull() throws Exception {
            when(courseRepository.searchByTerm(null)).thenReturn(List.of());
            
            List<CursoDTO> cursos = service.searchCourses(null);
            
            assertNotNull(cursos);
            assertTrue(cursos.isEmpty());
            verify(courseRepository, times(1)).searchByTerm(null);
        }

        @Test
        @DisplayName("Debería manejar error del repositorio en búsqueda")
        void deberiaManejarErrorDelRepositorioEnBusqueda() {
            when(courseRepository.searchByTerm("término")).thenThrow(new CourseRepository.RepositoryException("Error de BD"));
            
            CourseService.ServiceException exception = assertThrows(CourseService.ServiceException.class, () -> {
                service.searchCourses("término");
            });
            
            assertTrue(exception.getMessage().contains("Error al buscar cursos"));
        }
    }

    @Nested
    @DisplayName("Validaciones")
    class ValidacionesTests {

        @Test
        @DisplayName("Debería validar curso válido")
        void deberiaValidarCursoValido() {
            CursoDTO curso = createMockCurso("curso1", "Título", "Descripción");
            
            boolean resultado = service.isValidCourse(curso);
            
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Debería validar curso null como inválido")
        void deberiaValidarCursoNullComoInvalido() {
            boolean resultado = service.isValidCourse(null);
            
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Debería validar curso sin ID como inválido")
        void deberiaValidarCursoSinIdComoInvalido() {
            CursoDTO curso = createMockCurso(null, "Título", "Descripción");
            
            boolean resultado = service.isValidCourse(curso);
            
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Debería validar curso sin título como inválido")
        void deberiaValidarCursoSinTituloComoInvalido() {
            CursoDTO curso = createMockCurso("curso1", null, "Descripción");
            
            boolean resultado = service.isValidCourse(curso);
            
            assertFalse(resultado);
        }
    }

    /**
     * Crea un curso mock para testing.
     */
    private CursoDTO createMockCurso(String id, String titulo, String descripcion) {
        CursoDTO curso = mock(CursoDTO.class);
        when(curso.getId()).thenReturn(id);
        when(curso.getTitulo()).thenReturn(titulo);
        when(curso.getDescripcion()).thenReturn(descripcion);
        return curso;
    }
} 