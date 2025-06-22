package com.kursor.domain;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas de Sesion")
class SesionTest {

    private Curso mockCurso;
    private EstrategiaAprendizaje mockEstrategia;
    private Bloque mockBloque;
    private Pregunta mockPregunta;
    private PreguntaSesion mockPreguntaSesion;

    @BeforeEach
    void setUp() {
        mockCurso = mock(Curso.class);
        when(mockCurso.getId()).thenReturn("curso1");
        mockEstrategia = mock(EstrategiaAprendizaje.class);
        when(mockEstrategia.getNombre()).thenReturn("EstrategiaTest");
        mockBloque = mock(Bloque.class);
        when(mockBloque.getId()).thenReturn("bloque1");
        mockPregunta = mock(Pregunta.class);
        when(mockPregunta.getId()).thenReturn("pregunta1");
        mockPreguntaSesion = mock(PreguntaSesion.class);
    }

    @Nested
    @DisplayName("Constructores y validaciones")
    class ConstructoresTests {
        @Test
        @DisplayName("Debería crear sesión válida con parámetros correctos")
        void deberiaCrearSesionValida() {
            Sesion sesion = new Sesion("sesion1", mockCurso, mockEstrategia);
            assertEquals("sesion1", sesion.getId());
            assertEquals(mockCurso, sesion.getCurso());
            assertEquals(mockEstrategia, sesion.getEstrategia());
            assertNotNull(sesion.getFechaInicio());
        }

        @Test
        @DisplayName("Debería lanzar excepción si ID es null")
        void deberiaLanzarExcepcionSiIdEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Sesion(null, mockCurso, mockEstrategia);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción si curso es null")
        void deberiaLanzarExcepcionSiCursoEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Sesion("sesion1", null, mockEstrategia);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción si estrategia es null")
        void deberiaLanzarExcepcionSiEstrategiaEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Sesion("sesion1", mockCurso, null);
            });
        }
    }

    @Nested
    @DisplayName("Getters y setters")
    class GettersSettersTests {
        Sesion sesion;
        @BeforeEach
        void initSesion() {
            sesion = new Sesion("sesion1", mockCurso, mockEstrategia);
        }

        @Test
        void testSetIdValido() {
            sesion.setId("nuevaSesion");
            assertEquals("nuevaSesion", sesion.getId());
        }

        @Test
        void testSetIdNull() {
            assertThrows(IllegalArgumentException.class, () -> sesion.setId(null));
        }

        @Test
        void testSetCurso() {
            Curso otroCurso = mock(Curso.class);
            sesion.setCurso(otroCurso);
            assertEquals(otroCurso, sesion.getCurso());
        }

        @Test
        void testSetEstrategia() {
            EstrategiaAprendizaje otraEstrategia = mock(EstrategiaAprendizaje.class);
            sesion.setEstrategia(otraEstrategia);
            assertEquals(otraEstrategia, sesion.getEstrategia());
        }

        @Test
        void testSetFechaInicioYFin() {
            LocalDateTime ahora = LocalDateTime.now();
            sesion.setFechaInicio(ahora);
            sesion.setFechaFin(ahora.plusHours(1));
            assertEquals(ahora, sesion.getFechaInicio());
            assertEquals(ahora.plusHours(1), sesion.getFechaFin());
        }

        @Test
        void testSetBloqueActual() {
            sesion.setBloqueActual(mockBloque);
            assertEquals(mockBloque, sesion.getBloqueActual());
        }

        @Test
        void testSetPreguntaActual() {
            sesion.setPreguntaActual(mockPregunta);
            assertEquals(mockPregunta, sesion.getPreguntaActual());
        }

        @Test
        void testSetPorcentajeCompletitud() {
            sesion.setPorcentajeCompletitud(80);
            assertEquals(80, sesion.getPorcentajeCompletitud());
        }

        @Test
        void testSetTasaAciertos() {
            sesion.setTasaAciertos(90);
            assertEquals(90, sesion.getTasaAciertos());
        }

        @Test
        void testSetMejorRachaAciertos() {
            sesion.setMejorRachaAciertos(5);
            assertEquals(5, sesion.getMejorRachaAciertos());
        }

        @Test
        void testSetPreguntasSesion() {
            List<PreguntaSesion> lista = new ArrayList<>();
            lista.add(mockPreguntaSesion);
            sesion.setPreguntasSesion(lista);
            assertEquals(lista, sesion.getPreguntasSesion());
        }

        @Test
        void testSetPreguntasRespondidas() {
            List<Pregunta> lista = new ArrayList<>();
            lista.add(mockPregunta);
            sesion.setPreguntasRespondidas(lista);
            assertEquals(lista, sesion.getPreguntasRespondidas());
        }

        @Test
        void testSetPuntuacionTotal() {
            sesion.setPuntuacionTotal(100);
            assertEquals(100, sesion.getPuntuacionTotal());
        }
    }

    @Nested
    @DisplayName("Ciclo de vida y métodos de utilidad")
    class CicloVidaTests {
        Sesion sesion;
        @BeforeEach
        void initSesion() {
            sesion = new Sesion("sesion1", mockCurso, mockEstrategia);
        }

        @Test
        void testEstaActiva() {
            assertTrue(sesion.estaActiva());
            sesion.setFechaFin(LocalDateTime.now());
            assertFalse(sesion.estaActiva());
        }

        @Test
        void testGetDuracionSegundos() {
            LocalDateTime inicio = LocalDateTime.now();
            sesion.setFechaInicio(inicio);
            sesion.setFechaFin(inicio.plusSeconds(120));
            assertEquals(120, sesion.getDuracionSegundos());
        }

        @Test
        void testToStringNoNulo() {
            assertNotNull(sesion.toString());
        }
    }

    // NOTA: Los métodos guardarSesion, restaurarSesion, responderPregunta, actualizarEstadisticas, finalizarSesion
    // pueden requerir integración o mocks avanzados según su implementación interna.
} 