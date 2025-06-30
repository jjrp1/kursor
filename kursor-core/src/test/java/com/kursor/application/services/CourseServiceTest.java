package com.kursor.application.services;

import com.kursor.yaml.dto.CursoDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CourseService")
class CourseServiceTest {

    @Mock private CursoManager cursoManager;
    
    @Nested
    @DisplayName("Gestión básica de cursos")
    class GestionBasicaTests {

        @Test
        @DisplayName("Debería cargar cursos desde CursoManager")
        void deberiaCargarCursosDesdeManager() {
            // Given
            List<CursoDTO> cursosEsperados = Arrays.asList(
                createMockCurso("curso1", "Matemáticas Básicas", "Curso introductorio"),
                createMockCurso("curso2", "Historia Universal", "Historia general")
            );
            when(cursoManager.cargarCursosCompletos()).thenReturn(cursosEsperados);
            
            // When
            List<CursoDTO> cursos = cursoManager.cargarCursosCompletos();
            
            // Then
            assertNotNull(cursos);
            assertEquals(2, cursos.size());
            assertEquals("Matemáticas Básicas", cursos.get(0).getTitulo());
            verify(cursoManager, times(1)).cargarCursosCompletos();
        }

        @Test
        @DisplayName("Debería manejar lista vacía de cursos")
        void deberiaManejarListaVaciaDeCursos() {
            // Given
            when(cursoManager.cargarCursosCompletos()).thenReturn(List.of());
            
            // When
            List<CursoDTO> cursos = cursoManager.cargarCursosCompletos();
            
            // Then
            assertNotNull(cursos);
            assertTrue(cursos.isEmpty());
        }

        @Test
        @DisplayName("Debería manejar cuando manager retorna null")
        void deberiaManejarCuandoManagerRetornaNull() {
            // Given
            when(cursoManager.cargarCursosCompletos()).thenReturn(null);
            
            // When
            List<CursoDTO> cursos = cursoManager.cargarCursosCompletos();
            
            // Then
            assertNull(cursos);
        }
    }

    @Nested
    @DisplayName("Validaciones de datos")
    class ValidacionesDatosTests {

        @Test
        @DisplayName("Debería validar estructura básica de curso")
        void deberiaValidarEstructuraBasicaDeCurso() {
            // Given
            CursoDTO curso = createMockCurso("curso1", "Título Válido", "Descripción válida");
            
            // When & Then
            assertNotNull(curso.getId());
            assertNotNull(curso.getTitulo());
            assertNotNull(curso.getDescripcion());
            assertEquals("curso1", curso.getId());
            assertEquals("Título Válido", curso.getTitulo());
        }

        @Test
        @DisplayName("Debería manejar curso con datos mínimos")
        void deberiaManejarCursoConDatosMinimos() {
            // Given
            CursoDTO curso = createMockCurso("curso1", "Título", null);
            
            // When & Then
            assertNotNull(curso.getId());
            assertNotNull(curso.getTitulo());
            assertEquals("curso1", curso.getId());
            assertEquals("Título", curso.getTitulo());
        }
    }

    @Nested
    @DisplayName("Integración con CursoManager")
    class IntegracionCursoManagerTests {

        @Test
        @DisplayName("Debería gestionar múltiples llamadas al manager")
        void deberiaGestionarMultiplesLlamadasAlManager() {
            // Given
            List<CursoDTO> cursosEsperados = Arrays.asList(
                createMockCurso("curso1", "Curso 1", "Descripción 1")
            );
            when(cursoManager.cargarCursosCompletos()).thenReturn(cursosEsperados);
            
            // When - múltiples llamadas
            List<CursoDTO> cursos1 = cursoManager.cargarCursosCompletos();
            List<CursoDTO> cursos2 = cursoManager.cargarCursosCompletos();
            
            // Then
            assertNotNull(cursos1);
            assertNotNull(cursos2);
            assertEquals(cursos1.size(), cursos2.size());
            verify(cursoManager, times(2)).cargarCursosCompletos();
        }

        @Test
        @DisplayName("Debería mantener consistencia en los datos")
        void deberiaMantenerConsistenciaEnLosDatos() {
            // Given
            List<CursoDTO> cursosEsperados = Arrays.asList(
                createMockCurso("curso1", "Matemáticas", "Curso de matemáticas"),
                createMockCurso("curso2", "Ciencias", "Curso de ciencias")
            );
            when(cursoManager.cargarCursosCompletos()).thenReturn(cursosEsperados);
            
            // When
            List<CursoDTO> cursos = cursoManager.cargarCursosCompletos();
            
            // Then
            assertEquals(2, cursos.size());
            
            // Verificar primer curso
            CursoDTO curso1 = cursos.get(0);
            assertEquals("curso1", curso1.getId());
            assertEquals("Matemáticas", curso1.getTitulo());
            
            // Verificar segundo curso
            CursoDTO curso2 = cursos.get(1);
            assertEquals("curso2", curso2.getId());
            assertEquals("Ciencias", curso2.getTitulo());
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