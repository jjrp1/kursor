package com.kursor.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Pruebas unitarias para la clase Bloque.
 * 
 * <p>Esta clase de pruebas verifica el comportamiento de la clase Bloque,
 * incluyendo la creación, validaciones, gestión de preguntas y métodos de utilidad.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("Pruebas de la clase Bloque")
class BloqueTest {

    private Bloque bloque;
    private PreguntaMock pregunta1;
    private PreguntaMock pregunta2;

    @BeforeEach
    void setUp() {
        bloque = new Bloque("bloque1", "Números naturales", "Operaciones con números naturales", "teoria");
        pregunta1 = new PreguntaMock("p1", "test");
        pregunta2 = new PreguntaMock("p2", "truefalse");
    }

    @Nested
    @DisplayName("Constructor y validaciones")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear un bloque válido con todos los parámetros")
        void deberiaCrearBloqueValido() {
            // Given & When
            Bloque bloqueValido = new Bloque("bloque1", "Título", "Descripción", "teoria");

            // Then
            assertEquals("bloque1", bloqueValido.getId());
            assertEquals("Título", bloqueValido.getTitulo());
            assertEquals("Descripción", bloqueValido.getDescripcion());
            assertEquals("teoria", bloqueValido.getTipo());
        }

        @Test
        @DisplayName("Debería crear un bloque válido con descripción y tipo null")
        void deberiaCrearBloqueConDescripcionYTipoNull() {
            // Given & When
            Bloque bloqueSinDescripcion = new Bloque("bloque1", "Título", null, null);

            // Then
            assertEquals("bloque1", bloqueSinDescripcion.getId());
            assertEquals("Título", bloqueSinDescripcion.getTitulo());
            assertNull(bloqueSinDescripcion.getDescripcion());
            assertNull(bloqueSinDescripcion.getTipo());
        }

        @Test
        @DisplayName("Debería crear un bloque vacío con constructor por defecto")
        void deberiaCrearBloqueVacio() {
            // Given & When
            Bloque bloqueVacio = new Bloque();

            // Then
            assertNull(bloqueVacio.getId());
            assertNull(bloqueVacio.getTitulo());
            assertNull(bloqueVacio.getDescripcion());
            assertNull(bloqueVacio.getTipo());
            assertNotNull(bloqueVacio.getPreguntas());
            assertTrue(bloqueVacio.getPreguntas().isEmpty());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Bloque(null, "Título", "Descripción", "teoria");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID está vacío")
        void deberiaLanzarExcepcionCuandoIdEstaVacio() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Bloque("", "Título", "Descripción", "teoria");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID solo tiene espacios")
        void deberiaLanzarExcepcionCuandoIdSoloTieneEspacios() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Bloque("   ", "Título", "Descripción", "teoria");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el título es null")
        void deberiaLanzarExcepcionCuandoTituloEsNull() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Bloque("bloque1", null, "Descripción", "teoria");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el título está vacío")
        void deberiaLanzarExcepcionCuandoTituloEstaVacio() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Bloque("bloque1", "", "Descripción", "teoria");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el título solo tiene espacios")
        void deberiaLanzarExcepcionCuandoTituloSoloTieneEspacios() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Bloque("bloque1", "   ", "Descripción", "teoria");
            });
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco de todos los parámetros")
        void deberiaRecortarEspaciosEnBlanco() {
            // Given & When
            Bloque bloqueConEspacios = new Bloque("  bloque1  ", "  Título  ", "  Descripción  ", "  teoria  ");

            // Then
            assertEquals("bloque1", bloqueConEspacios.getId());
            assertEquals("Título", bloqueConEspacios.getTitulo());
            assertEquals("Descripción", bloqueConEspacios.getDescripcion());
            assertEquals("teoria", bloqueConEspacios.getTipo());
        }
    }

    @Nested
    @DisplayName("Getters y Setters")
    class GettersSettersTests {

        @Test
        @DisplayName("Debería obtener y establecer ID correctamente")
        void deberiaObtenerYEstablecerId() {
            // Given
            String nuevoId = "nuevo-id";

            // When
            bloque.setId(nuevoId);

            // Then
            assertEquals(nuevoId, bloque.getId());
        }

        @Test
        @DisplayName("Debería obtener y establecer título correctamente")
        void deberiaObtenerYEstablecerTitulo() {
            // Given
            String nuevoTitulo = "Nuevo Título";

            // When
            bloque.setTitulo(nuevoTitulo);

            // Then
            assertEquals(nuevoTitulo, bloque.getTitulo());
        }

        @Test
        @DisplayName("Debería obtener y establecer descripción correctamente")
        void deberiaObtenerYEstablecerDescripcion() {
            // Given
            String nuevaDescripcion = "Nueva descripción";

            // When
            bloque.setDescripcion(nuevaDescripcion);

            // Then
            assertEquals(nuevaDescripcion, bloque.getDescripcion());
        }

        @Test
        @DisplayName("Debería obtener y establecer tipo correctamente")
        void deberiaObtenerYEstablecerTipo() {
            // Given
            String nuevoTipo = "practica";

            // When
            bloque.setTipo(nuevoTipo);

            // Then
            assertEquals(nuevoTipo, bloque.getTipo());
        }

        @Test
        @DisplayName("Debería establecer descripción como null")
        void deberiaEstablecerDescripcionComoNull() {
            // When
            bloque.setDescripcion(null);

            // Then
            assertNull(bloque.getDescripcion());
        }

        @Test
        @DisplayName("Debería establecer tipo como null")
        void deberiaEstablecerTipoComoNull() {
            // When
            bloque.setTipo(null);

            // Then
            assertNull(bloque.getTipo());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer ID")
        void deberiaRecortarEspaciosAlEstablecerId() {
            // When
            bloque.setId("  nuevo-id  ");

            // Then
            assertEquals("nuevo-id", bloque.getId());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer título")
        void deberiaRecortarEspaciosAlEstablecerTitulo() {
            // When
            bloque.setTitulo("  Nuevo Título  ");

            // Then
            assertEquals("Nuevo Título", bloque.getTitulo());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer descripción")
        void deberiaRecortarEspaciosAlEstablecerDescripcion() {
            // When
            bloque.setDescripcion("  Nueva descripción  ");

            // Then
            assertEquals("Nueva descripción", bloque.getDescripcion());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer tipo")
        void deberiaRecortarEspaciosAlEstablecerTipo() {
            // When
            bloque.setTipo("  practica  ");

            // Then
            assertEquals("practica", bloque.getTipo());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer ID null")
        void deberiaLanzarExcepcionAlEstablecerIdNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                bloque.setId(null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer ID vacío")
        void deberiaLanzarExcepcionAlEstablecerIdVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                bloque.setId("");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer título null")
        void deberiaLanzarExcepcionAlEstablecerTituloNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                bloque.setTitulo(null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer título vacío")
        void deberiaLanzarExcepcionAlEstablecerTituloVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                bloque.setTitulo("");
            });
        }
    }

    @Nested
    @DisplayName("Gestión de preguntas")
    class GestionPreguntasTests {

        @Test
        @DisplayName("Debería agregar una pregunta correctamente")
        void deberiaAgregarPregunta() {
            // When
            bloque.addPregunta(pregunta1);

            // Then
            assertEquals(1, bloque.getNumeroPreguntas());
            assertTrue(bloque.tienePreguntas());
            assertTrue(bloque.getPreguntas().contains(pregunta1));
        }

        @Test
        @DisplayName("Debería agregar múltiples preguntas correctamente")
        void deberiaAgregarMultiplesPreguntas() {
            // When
            bloque.addPregunta(pregunta1);
            bloque.addPregunta(pregunta2);

            // Then
            assertEquals(2, bloque.getNumeroPreguntas());
            assertTrue(bloque.tienePreguntas());
            assertTrue(bloque.getPreguntas().contains(pregunta1));
            assertTrue(bloque.getPreguntas().contains(pregunta2));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al agregar pregunta null")
        void deberiaLanzarExcepcionAlAgregarPreguntaNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                bloque.addPregunta(null);
            });
        }

        @Test
        @DisplayName("Debería retornar lista inmutable de preguntas")
        void deberiaRetornarListaInmutable() {
            // Given
            bloque.addPregunta(pregunta1);
            List<Pregunta> preguntas = bloque.getPreguntas();

            // When & Then
            assertThrows(UnsupportedOperationException.class, () -> {
                preguntas.add(pregunta2);
            });
        }

        @Test
        @DisplayName("Debería establecer lista de preguntas correctamente")
        void deberiaEstablecerListaPreguntas() {
            // Given
            List<Pregunta> nuevaLista = new ArrayList<>();
            nuevaLista.add(pregunta1);
            nuevaLista.add(pregunta2);

            // When
            bloque.setPreguntas(nuevaLista);

            // Then
            assertEquals(2, bloque.getNumeroPreguntas());
            assertTrue(bloque.getPreguntas().contains(pregunta1));
            assertTrue(bloque.getPreguntas().contains(pregunta2));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer lista de preguntas null")
        void deberiaLanzarExcepcionAlEstablecerListaPreguntasNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                bloque.setPreguntas(null);
            });
        }
    }

    @Nested
    @DisplayName("Métodos de utilidad")
    class MetodosUtilidadTests {

        @Test
        @DisplayName("Debería retornar 0 preguntas cuando el bloque está vacío")
        void deberiaRetornarCeroPreguntasCuandoVacio() {
            // Then
            assertEquals(0, bloque.getNumeroPreguntas());
            assertFalse(bloque.tienePreguntas());
        }

        @Test
        @DisplayName("Debería retornar número correcto de preguntas")
        void deberiaRetornarNumeroCorrectoPreguntas() {
            // Given
            bloque.addPregunta(pregunta1);
            bloque.addPregunta(pregunta2);

            // Then
            assertEquals(2, bloque.getNumeroPreguntas());
            assertTrue(bloque.tienePreguntas());
        }

        @Test
        @DisplayName("Debería filtrar preguntas por tipo correctamente")
        void deberiaFiltrarPreguntasPorTipo() {
            // Given
            bloque.addPregunta(pregunta1); // tipo "test"
            bloque.addPregunta(pregunta2); // tipo "truefalse"

            // When
            List<Pregunta> preguntasTest = bloque.getPreguntasPorTipo("test");

            // Then
            assertEquals(1, preguntasTest.size());
            assertTrue(preguntasTest.contains(pregunta1));
            assertFalse(preguntasTest.contains(pregunta2));
        }

        @Test
        @DisplayName("Debería retornar lista vacía para tipo inexistente")
        void deberiaRetornarListaVaciaParaTipoInexistente() {
            // Given
            bloque.addPregunta(pregunta1);
            bloque.addPregunta(pregunta2);

            // When
            List<Pregunta> preguntasInexistentes = bloque.getPreguntasPorTipo("inexistente");

            // Then
            assertTrue(preguntasInexistentes.isEmpty());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException para tipo null")
        void deberiaLanzarExcepcionParaTipoNull() {
            // Given
            bloque.addPregunta(pregunta1);
            bloque.addPregunta(pregunta2);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                bloque.getPreguntasPorTipo(null);
            });
        }
    }

    @Nested
    @DisplayName("Método toString")
    class ToStringTests {

        @Test
        @DisplayName("Debería retornar representación en cadena correcta")
        void deberiaRetornarToStringCorrecto() {
            // Given
            bloque.setId("bloque1");
            bloque.setTitulo("Matemáticas");

            // When
            String resultado = bloque.toString();

            // Then
            assertTrue(resultado.contains("bloque1"));
            assertTrue(resultado.contains("Matemáticas"));
            assertTrue(resultado.contains("Bloque"));
        }

        @Test
        @DisplayName("Debería retornar representación en cadena con valores null")
        void deberiaRetornarToStringConValoresNull() {
            // Given
            Bloque bloqueVacio = new Bloque();

            // When
            String resultado = bloqueVacio.toString();

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.contains("Bloque"));
        }
    }

    /**
     * Clase mock para probar la clase abstracta Pregunta.
     */
    private static class PreguntaMock extends Pregunta {
        
        public PreguntaMock(String id, String tipo) {
            super(id, tipo);
        }

        @Override
        public boolean esCorrecta(String respuesta) {
            return "correcta".equals(respuesta);
        }

        @Override
        public String getEnunciado() {
            return "Pregunta mock " + getId();
        }
    }
} 