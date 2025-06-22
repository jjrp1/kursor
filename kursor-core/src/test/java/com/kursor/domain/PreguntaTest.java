package com.kursor.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase abstracta Pregunta.
 * 
 * <p>Esta clase de pruebas verifica el comportamiento de la clase Pregunta,
 * incluyendo la creación, validaciones y métodos comunes. Se utiliza una
 * implementación mock para probar la clase abstracta.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("Pruebas de la clase Pregunta")
class PreguntaTest {

    private PreguntaMock pregunta;

    @BeforeEach
    void setUp() {
        pregunta = new PreguntaMock("pregunta1", "test");
    }

    @Nested
    @DisplayName("Constructor y validaciones")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear una pregunta válida")
        void deberiaCrearPreguntaValida() {
            // Given & When
            PreguntaMock preguntaValida = new PreguntaMock("pregunta1", "test");

            // Then
            assertEquals("pregunta1", preguntaValida.getId());
            assertEquals("test", preguntaValida.getTipo());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new PreguntaMock(null, "test");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID está vacío")
        void deberiaLanzarExcepcionCuandoIdEstaVacio() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new PreguntaMock("", "test");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID solo tiene espacios")
        void deberiaLanzarExcepcionCuandoIdSoloTieneEspacios() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new PreguntaMock("   ", "test");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el tipo es null")
        void deberiaLanzarExcepcionCuandoTipoEsNull() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new PreguntaMock("pregunta1", null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el tipo está vacío")
        void deberiaLanzarExcepcionCuandoTipoEstaVacio() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new PreguntaMock("pregunta1", "");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el tipo solo tiene espacios")
        void deberiaLanzarExcepcionCuandoTipoSoloTieneEspacios() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new PreguntaMock("pregunta1", "   ");
            });
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco del ID y tipo")
        void deberiaRecortarEspaciosEnBlanco() {
            // Given & When
            PreguntaMock preguntaConEspacios = new PreguntaMock("  pregunta1  ", "  test  ");

            // Then
            assertEquals("pregunta1", preguntaConEspacios.getId());
            assertEquals("test", preguntaConEspacios.getTipo());
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
            pregunta.setId(nuevoId);

            // Then
            assertEquals(nuevoId, pregunta.getId());
        }

        @Test
        @DisplayName("Debería obtener y establecer tipo correctamente")
        void deberiaObtenerYEstablecerTipo() {
            // Given
            String nuevoTipo = "truefalse";

            // When
            pregunta.setTipo(nuevoTipo);

            // Then
            assertEquals(nuevoTipo, pregunta.getTipo());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer ID")
        void deberiaRecortarEspaciosAlEstablecerId() {
            // When
            pregunta.setId("  nuevo-id  ");

            // Then
            assertEquals("nuevo-id", pregunta.getId());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer tipo")
        void deberiaRecortarEspaciosAlEstablecerTipo() {
            // When
            pregunta.setTipo("  truefalse  ");

            // Then
            assertEquals("truefalse", pregunta.getTipo());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer ID null")
        void deberiaLanzarExcepcionAlEstablecerIdNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                pregunta.setId(null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer ID vacío")
        void deberiaLanzarExcepcionAlEstablecerIdVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                pregunta.setId("");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer tipo null")
        void deberiaLanzarExcepcionAlEstablecerTipoNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                pregunta.setTipo(null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer tipo vacío")
        void deberiaLanzarExcepcionAlEstablecerTipoVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                pregunta.setTipo("");
            });
        }
    }

    @Nested
    @DisplayName("Métodos abstractos")
    class MetodosAbstractosTests {

        @Test
        @DisplayName("Debería implementar esCorrecta correctamente")
        void deberiaImplementarEsCorrecta() {
            // Given
            String respuestaCorrecta = "correcta";
            String respuestaIncorrecta = "incorrecta";

            // When & Then
            assertTrue(pregunta.esCorrecta(respuestaCorrecta));
            assertFalse(pregunta.esCorrecta(respuestaIncorrecta));
        }

        @Test
        @DisplayName("Debería implementar getEnunciado correctamente")
        void deberiaImplementarGetEnunciado() {
            // When
            String enunciado = pregunta.getEnunciado();

            // Then
            assertNotNull(enunciado);
            assertTrue(enunciado.contains("pregunta1"));
            assertTrue(enunciado.contains("Pregunta mock"));
        }

        @Test
        @DisplayName("Debería manejar respuesta null en esCorrecta")
        void deberiaManejarRespuestaNull() {
            // When & Then
            assertFalse(pregunta.esCorrecta(null));
        }

        @Test
        @DisplayName("Debería manejar respuesta vacía en esCorrecta")
        void deberiaManejarRespuestaVacia() {
            // When & Then
            assertFalse(pregunta.esCorrecta(""));
        }
    }

    @Nested
    @DisplayName("Método toString")
    class ToStringTests {

        @Test
        @DisplayName("Debería retornar representación en cadena correcta")
        void deberiaRetornarToStringCorrecto() {
            // Given
            pregunta.setId("pregunta1");
            pregunta.setTipo("test");

            // When
            String resultado = pregunta.toString();

            // Then
            assertTrue(resultado.contains("pregunta1"));
            assertTrue(resultado.contains("test"));
            assertTrue(resultado.contains("Pregunta"));
        }

        @Test
        @DisplayName("Debería retornar representación en cadena con formato específico")
        void deberiaRetornarToStringConFormatoEspecifico() {
            // Given
            pregunta.setId("p1");
            pregunta.setTipo("truefalse");

            // When
            String resultado = pregunta.toString();

            // Then
            assertTrue(resultado.contains("Pregunta{"));
            assertTrue(resultado.contains("id='p1'"));
            assertTrue(resultado.contains("tipo='truefalse'"));
            assertTrue(resultado.endsWith("}"));
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTests {

        @Test
        @DisplayName("Debería manejar ID con caracteres especiales")
        void deberiaManejarIdConCaracteresEspeciales() {
            // Given & When
            PreguntaMock preguntaEspecial = new PreguntaMock("pregunta-123_456", "test");

            // Then
            assertEquals("pregunta-123_456", preguntaEspecial.getId());
        }

        @Test
        @DisplayName("Debería manejar tipo con caracteres especiales")
        void deberiaManejarTipoConCaracteresEspeciales() {
            // Given & When
            PreguntaMock preguntaEspecial = new PreguntaMock("p1", "completar_huecos");

            // Then
            assertEquals("completar_huecos", preguntaEspecial.getTipo());
        }

        @Test
        @DisplayName("Debería manejar ID muy largo")
        void deberiaManejarIdMuyLargo() {
            // Given
            String idLargo = "pregunta-con-un-identificador-muy-largo-que-puede-ser-problematico-si-no-se-maneja-correctamente";

            // When
            PreguntaMock preguntaLarga = new PreguntaMock(idLargo, "test");

            // Then
            assertEquals(idLargo, preguntaLarga.getId());
        }

        @Test
        @DisplayName("Debería manejar tipo muy largo")
        void deberiaManejarTipoMuyLargo() {
            // Given
            String tipoLargo = "tipo-de-pregunta-muy-especifico-y-detallado";

            // When
            PreguntaMock preguntaLarga = new PreguntaMock("p1", tipoLargo);

            // Then
            assertEquals(tipoLargo, preguntaLarga.getTipo());
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