package com.kursor.service;

import com.kursor.yaml.dto.CursoPreviewDTO;
import com.kursor.factory.PreguntaFactory;
import com.kursor.domain.Curso;
import com.kursor.domain.Bloque;
import com.kursor.domain.Pregunta;

import org.junit.jupiter.api.*;
import java.util.List;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de CursoPreviewService")
class CursoPreviewServiceTest {

    private CursoPreviewService service;
    private static final String TEST_CURSOS_DIR = "test-cursos";

    @BeforeEach
    void setUp() {
        service = new CursoPreviewService(TEST_CURSOS_DIR);
    }

    @AfterEach
    void tearDown() {
        cleanupTestFiles();
    }

    @Test
    @DisplayName("Debería crear servicio con directorio válido")
    void deberiaCrearServicioConDirectorioValido() {
        CursoPreviewService testService = new CursoPreviewService("directorio-valido");
        
        assertNotNull(testService);
        assertEquals("directorio-valido", testService.getCursosDir());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando directorio es null")
    void deberiaLanzarExcepcionCuandoDirectorioEsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CursoPreviewService(null);
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando directorio está vacío")
    void deberiaLanzarExcepcionCuandoDirectorioEstaVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CursoPreviewService("");
        });
    }

    @Test
    @DisplayName("Debería validar directorio existente")
    void deberiaValidarDirectorioExistente() {
        File tempDir = new File(TEST_CURSOS_DIR);
        tempDir.mkdirs();
        
        try {
            assertTrue(service.isDirectorioValido());
        } finally {
            tempDir.delete();
        }
    }

    @Test
    @DisplayName("Debería invalidar directorio inexistente")
    void deberiaInvalidarDirectorioInexistente() {
        CursoPreviewService invalidService = new CursoPreviewService("directorio-inexistente");
        assertFalse(invalidService.isDirectorioValido());
    }

    @Test
    @DisplayName("Debería retornar lista vacía para directorio inexistente")
    void deberiaRetornarListaVaciaParaDirectorioInexistente() {
        CursoPreviewService invalidService = new CursoPreviewService("directorio-inexistente");
        List<CursoPreviewDTO> previews = invalidService.cargarPreviews();
        
        assertNotNull(previews);
        assertTrue(previews.isEmpty());
    }

    @Test
    @DisplayName("Debería retornar null para curso inexistente")
    void deberiaRetornarNullParaCursoInexistente() {
        Curso curso = service.cargarCursoCompleto("curso-inexistente");
        assertNull(curso);
    }

    @Test
    @DisplayName("Debería retornar null para ID null")
    void deberiaRetornarNullParaIdNull() {
        Curso curso = service.cargarCursoCompleto(null);
        assertNull(curso);
    }

    @Test
    @DisplayName("Debería obtener directorio de cursos")
    void deberiaObtenerDirectorioDeCursos() {
        assertEquals(TEST_CURSOS_DIR, service.getCursosDir());
    }

    private void cleanupTestFiles() {
        File testDir = new File(TEST_CURSOS_DIR);
        if (testDir.exists()) {
            deleteDirectory(testDir);
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
} 