package com.kursor.builder;

import com.kursor.domain.Curso;
import com.kursor.domain.Bloque;
import com.kursor.domain.Pregunta;
import com.kursor.yaml.dto.CursoDTO;
import com.kursor.yaml.dto.BloqueDTO;
import com.kursor.yaml.dto.PreguntaDTO;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el builder de cursos CursoBuilder.
 * 
 * <p>Esta clase contiene pruebas exhaustivas para verificar el funcionamiento
 * correcto del builder de cursos, incluyendo la construcción de cursos,
 * bloques y preguntas desde DTOs.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see CursoBuilder
 * @see Curso
 * @see Bloque
 * @see Pregunta
 */
@DisplayName("Pruebas de CursoBuilder")
class CursoBuilderTest {

    private CursoBuilder builder;
    private Bloque mockBloque;
    private Pregunta mockPregunta;

    @BeforeEach
    void setUp() {
        builder = new CursoBuilder();
        
        // Crear mocks
        mockBloque = mock(Bloque.class);
        when(mockBloque.getId()).thenReturn("bloque1");
        when(mockBloque.getTitulo()).thenReturn("Bloque de Prueba");
        
        mockPregunta = mock(Pregunta.class);
        when(mockPregunta.getId()).thenReturn("pregunta1");
    }

    @Nested
    @DisplayName("Configuración de metadatos")
    class ConfiguracionMetadatosTests {

        @Test
        @DisplayName("Debería establecer ID válido")
        void deberiaEstablecerIdValido() {
            CursoBuilder result = builder.conId("curso1");
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            assertEquals("curso1", builder.getCursoActual().getId(), 
                        "El ID debería estar establecido");
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco del ID")
        void deberiaRecortarEspaciosEnBlancoDelId() {
            builder.conId("  curso1  ");
            
            assertEquals("curso1", builder.getCursoActual().getId(), 
                        "El ID debería estar recortado");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                builder.conId(null);
            }, "Debería lanzar IllegalArgumentException para ID null");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID está vacío")
        void deberiaLanzarExcepcionCuandoIdEstaVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                builder.conId("");
            }, "Debería lanzar IllegalArgumentException para ID vacío");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando ID solo tiene espacios")
        void deberiaLanzarExcepcionCuandoIdSoloTieneEspacios() {
            assertThrows(IllegalArgumentException.class, () -> {
                builder.conId("   ");
            }, "Debería lanzar IllegalArgumentException para ID con solo espacios");
        }

        @Test
        @DisplayName("Debería establecer título válido")
        void deberiaEstablecerTituloValido() {
            CursoBuilder result = builder.conTitulo("Curso de Prueba");
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            assertEquals("Curso de Prueba", builder.getCursoActual().getTitulo(), 
                        "El título debería estar establecido");
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco del título")
        void deberiaRecortarEspaciosEnBlancoDelTitulo() {
            builder.conTitulo("  Curso de Prueba  ");
            
            assertEquals("Curso de Prueba", builder.getCursoActual().getTitulo(), 
                        "El título debería estar recortado");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando título es null")
        void deberiaLanzarExcepcionCuandoTituloEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                builder.conTitulo(null);
            }, "Debería lanzar IllegalArgumentException para título null");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando título está vacío")
        void deberiaLanzarExcepcionCuandoTituloEstaVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                builder.conTitulo("");
            }, "Debería lanzar IllegalArgumentException para título vacío");
        }

        @Test
        @DisplayName("Debería establecer descripción válida")
        void deberiaEstablecerDescripcionValida() {
            CursoBuilder result = builder.conDescripcion("Descripción del curso");
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            assertEquals("Descripción del curso", builder.getCursoActual().getDescripcion(), 
                        "La descripción debería estar establecida");
        }

        @Test
        @DisplayName("Debería permitir descripción null")
        void deberiaPermitirDescripcionNull() {
            CursoBuilder result = builder.conDescripcion(null);
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            assertNull(builder.getCursoActual().getDescripcion(), 
                      "La descripción debería ser null");
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco de la descripción")
        void deberiaRecortarEspaciosEnBlancoDeLaDescripcion() {
            builder.conDescripcion("  Descripción del curso  ");
            
            assertEquals("Descripción del curso", builder.getCursoActual().getDescripcion(), 
                        "La descripción debería estar recortada");
        }
    }

    @Nested
    @DisplayName("Gestión de bloques")
    class GestionBloquesTests {

        @Test
        @DisplayName("Debería agregar bloque válido")
        void deberiaAgregarBloqueValido() {
            CursoBuilder result = builder.agregarBloque(mockBloque);
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            assertEquals(1, builder.getCursoActual().getBloques().size(), 
                        "Debería tener 1 bloque");
            assertTrue(builder.getCursoActual().getBloques().contains(mockBloque), 
                      "Debería contener el bloque agregado");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando bloque es null")
        void deberiaLanzarExcepcionCuandoBloqueEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                builder.agregarBloque(null);
            }, "Debería lanzar IllegalArgumentException para bloque null");
        }

        @Test
        @DisplayName("Debería agregar múltiples bloques")
        void deberiaAgregarMultiplesBloques() {
            Bloque mockBloque2 = mock(Bloque.class);
            when(mockBloque2.getId()).thenReturn("bloque2");
            
            builder.agregarBloque(mockBloque)
                  .agregarBloque(mockBloque2);
            
            assertEquals(2, builder.getCursoActual().getBloques().size(), 
                        "Debería tener 2 bloques");
        }
    }

    @Nested
    @DisplayName("Gestión de preguntas")
    class GestionPreguntasTests {

        @BeforeEach
        void setUp() {
            builder.agregarBloque(mockBloque);
        }

        @Test
        @DisplayName("Debería agregar pregunta a bloque existente")
        void deberiaAgregarPreguntaABloqueExistente() {
            CursoBuilder result = builder.agregarPregunta("bloque1", mockPregunta);
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            verify(mockBloque, times(1)).addPregunta(mockPregunta);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando pregunta es null")
        void deberiaLanzarExcepcionCuandoPreguntaEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                builder.agregarPregunta("bloque1", null);
            }, "Debería lanzar IllegalArgumentException para pregunta null");
        }

        @Test
        @DisplayName("Debería manejar bloque inexistente silenciosamente")
        void deberiaManejarBloqueInexistenteSilenciosamente() {
            CursoBuilder result = builder.agregarPregunta("bloque_inexistente", mockPregunta);
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            verify(mockBloque, never()).addPregunta(any());
        }

        @Test
        @DisplayName("Debería agregar múltiples preguntas al mismo bloque")
        void deberiaAgregarMultiplesPreguntasAlMismoBloque() {
            Pregunta mockPregunta2 = mock(Pregunta.class);
            when(mockPregunta2.getId()).thenReturn("pregunta2");
            
            builder.agregarPregunta("bloque1", mockPregunta)
                  .agregarPregunta("bloque1", mockPregunta2);
            
            verify(mockBloque, times(1)).addPregunta(mockPregunta);
            verify(mockBloque, times(1)).addPregunta(mockPregunta2);
        }
    }

    @Nested
    @DisplayName("Construcción del curso")
    class ConstruccionCursoTests {

        @Test
        @DisplayName("Debería construir curso válido")
        void deberiaConstruirCursoValido() {
            builder.conId("curso1")
                   .conTitulo("Curso de Prueba")
                   .conDescripcion("Descripción del curso")
                   .agregarBloque(mockBloque);
            
            Curso curso = builder.build();
            
            assertNotNull(curso, "El curso no debería ser null");
            assertEquals("curso1", curso.getId());
            assertEquals("Curso de Prueba", curso.getTitulo());
            assertEquals("Descripción del curso", curso.getDescripcion());
            assertEquals(1, curso.getBloques().size());
        }

        @Test
        @DisplayName("Debería construir curso mínimo")
        void deberiaConstruirCursoMinimo() {
            Curso curso = builder.build();
            
            assertNotNull(curso, "El curso no debería ser null");
            assertNull(curso.getId());
            assertNull(curso.getTitulo());
            assertNull(curso.getDescripcion());
            assertTrue(curso.getBloques().isEmpty());
        }

        @Test
        @DisplayName("Debería construir curso con encadenamiento completo")
        void deberiaConstruirCursoConEncadenamientoCompleto() {
            Curso curso = builder
                .conId("curso1")
                .conTitulo("Curso de Prueba")
                .conDescripcion("Descripción del curso")
                .agregarBloque(mockBloque)
                .agregarPregunta("bloque1", mockPregunta)
                .build();
            
            assertNotNull(curso, "El curso no debería ser null");
            assertEquals("curso1", curso.getId());
            assertEquals("Curso de Prueba", curso.getTitulo());
            assertEquals("Descripción del curso", curso.getDescripcion());
            assertEquals(1, curso.getBloques().size());
        }
    }

    @Nested
    @DisplayName("Métodos de utilidad")
    class MetodosUtilidadTests {

        @Test
        @DisplayName("Debería obtener curso actual")
        void deberiaObtenerCursoActual() {
            Curso cursoActual = builder.getCursoActual();
            
            assertNotNull(cursoActual, "El curso actual no debería ser null");
            assertSame(cursoActual, builder.getCursoActual(), 
                      "Debería retornar la misma instancia");
        }

        @Test
        @DisplayName("Debería validar curso válido")
        void deberiaValidarCursoValido() {
            builder.conId("curso1")
                   .conTitulo("Curso de Prueba")
                   .agregarBloque(mockBloque);
            
            assertTrue(builder.esValido(), "El curso debería ser válido");
        }

        @Test
        @DisplayName("Debería validar curso inválido sin ID")
        void deberiaValidarCursoInvalidoSinId() {
            builder.conTitulo("Curso de Prueba")
                   .agregarBloque(mockBloque);
            
            assertFalse(builder.esValido(), "El curso sin ID debería ser inválido");
        }

        @Test
        @DisplayName("Debería validar curso inválido sin título")
        void deberiaValidarCursoInvalidoSinTitulo() {
            builder.conId("curso1")
                   .agregarBloque(mockBloque);
            
            assertFalse(builder.esValido(), "El curso sin título debería ser inválido");
        }

        @Test
        @DisplayName("Debería generar resumen del curso")
        void deberiaGenerarResumenDelCurso() {
            builder.conId("curso1")
                   .conTitulo("Curso de Prueba")
                   .conDescripcion("Descripción del curso")
                   .agregarBloque(mockBloque);
            
            String resumen = builder.getResumen();
            
            assertNotNull(resumen, "El resumen no debería ser null");
            assertTrue(resumen.contains("curso1"), "Debería contener el ID");
            assertTrue(resumen.contains("Curso de Prueba"), "Debería contener el título");
            assertTrue(resumen.contains("1"), "Debería contener el número de bloques");
        }

        @Test
        @DisplayName("Debería generar resumen de curso vacío")
        void deberiaGenerarResumenDeCursoVacio() {
            String resumen = builder.getResumen();
            
            assertNotNull(resumen, "El resumen no debería ser null");
            assertTrue(resumen.contains("0"), "Debería indicar 0 bloques");
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTests {

        @Test
        @DisplayName("Debería manejar múltiples llamadas a build")
        void deberiaManejarMultiplesLlamadasABuild() {
            builder.conId("curso1")
                   .conTitulo("Curso de Prueba");
            
            Curso curso1 = builder.build();
            Curso curso2 = builder.build();
            
            assertNotNull(curso1, "El primer curso no debería ser null");
            assertNotNull(curso2, "El segundo curso no debería ser null");
            assertEquals(curso1.getId(), curso2.getId());
            assertEquals(curso1.getTitulo(), curso2.getTitulo());
        }

        @Test
        @DisplayName("Debería manejar encadenamiento extenso")
        void deberiaManejarEncadenamientoExtenso() {
            Bloque mockBloque2 = mock(Bloque.class);
            when(mockBloque2.getId()).thenReturn("bloque2");
            
            Pregunta mockPregunta2 = mock(Pregunta.class);
            when(mockPregunta2.getId()).thenReturn("pregunta2");
            
            CursoBuilder result = builder
                .conId("curso1")
                .conTitulo("Curso de Prueba")
                .conDescripcion("Descripción del curso")
                .agregarBloque(mockBloque)
                .agregarBloque(mockBloque2)
                .agregarPregunta("bloque1", mockPregunta)
                .agregarPregunta("bloque2", mockPregunta2);
            
            assertSame(builder, result, "Debería retornar el mismo builder");
            assertEquals(2, builder.getCursoActual().getBloques().size());
        }
    }
} 