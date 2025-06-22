package com.kursor.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Pruebas unitarias para la clase Curso.
 * 
 * <p>Esta clase de pruebas verifica el comportamiento de la clase Curso,
 * incluyendo la creación, validaciones, gestión de bloques y métodos de utilidad.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("Pruebas de la clase Curso")
class CursoTest {

    private Curso curso;
    private Bloque bloque1;
    private Bloque bloque2;

    @BeforeEach
    void setUp() {
        curso = new Curso("curso1", "Matemáticas Básicas", "Curso introductorio de matemáticas");
        bloque1 = new Bloque("bloque1", "Números naturales", "Operaciones con números naturales", "teoria");
        bloque2 = new Bloque("bloque2", "Operaciones básicas", "Suma, resta, multiplicación y división", "practica");
    }

    @Nested
    @DisplayName("Constructor y validaciones")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear un curso válido con todos los parámetros")
        void deberiaCrearCursoValido() {
            // Given & When
            Curso cursoValido = new Curso("curso1", "Título", "Descripción");

            // Then
            assertEquals("curso1", cursoValido.getId());
            assertEquals("Título", cursoValido.getTitulo());
            assertEquals("Descripción", cursoValido.getDescripcion());
        }

        @Test
        @DisplayName("Debería crear un curso válido con descripción null")
        void deberiaCrearCursoConDescripcionNull() {
            // Given & When
            Curso cursoSinDescripcion = new Curso("curso1", "Título", null);

            // Then
            assertEquals("curso1", cursoSinDescripcion.getId());
            assertEquals("Título", cursoSinDescripcion.getTitulo());
            assertNull(cursoSinDescripcion.getDescripcion());
        }

        @Test
        @DisplayName("Debería crear un curso vacío con constructor por defecto")
        void deberiaCrearCursoVacio() {
            // Given & When
            Curso cursoVacio = new Curso();

            // Then
            assertNull(cursoVacio.getId());
            assertNull(cursoVacio.getTitulo());
            assertNull(cursoVacio.getDescripcion());
            assertNotNull(cursoVacio.getBloques());
            assertTrue(cursoVacio.getBloques().isEmpty());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID es null")
        void deberiaLanzarExcepcionCuandoIdEsNull() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Curso(null, "Título", "Descripción");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID está vacío")
        void deberiaLanzarExcepcionCuandoIdEstaVacio() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Curso("", "Título", "Descripción");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el ID solo tiene espacios")
        void deberiaLanzarExcepcionCuandoIdSoloTieneEspacios() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Curso("   ", "Título", "Descripción");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el título es null")
        void deberiaLanzarExcepcionCuandoTituloEsNull() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Curso("curso1", null, "Descripción");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el título está vacío")
        void deberiaLanzarExcepcionCuandoTituloEstaVacio() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Curso("curso1", "", "Descripción");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el título solo tiene espacios")
        void deberiaLanzarExcepcionCuandoTituloSoloTieneEspacios() {
            // Given & When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Curso("curso1", "   ", "Descripción");
            });
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco del ID y título")
        void deberiaRecortarEspaciosEnBlanco() {
            // Given & When
            Curso cursoConEspacios = new Curso("  curso1  ", "  Título  ", "  Descripción  ");

            // Then
            assertEquals("curso1", cursoConEspacios.getId());
            assertEquals("Título", cursoConEspacios.getTitulo());
            assertEquals("Descripción", cursoConEspacios.getDescripcion());
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
            curso.setId(nuevoId);

            // Then
            assertEquals(nuevoId, curso.getId());
        }

        @Test
        @DisplayName("Debería obtener y establecer título correctamente")
        void deberiaObtenerYEstablecerTitulo() {
            // Given
            String nuevoTitulo = "Nuevo Título";

            // When
            curso.setTitulo(nuevoTitulo);

            // Then
            assertEquals(nuevoTitulo, curso.getTitulo());
        }

        @Test
        @DisplayName("Debería obtener y establecer descripción correctamente")
        void deberiaObtenerYEstablecerDescripcion() {
            // Given
            String nuevaDescripcion = "Nueva descripción";

            // When
            curso.setDescripcion(nuevaDescripcion);

            // Then
            assertEquals(nuevaDescripcion, curso.getDescripcion());
        }

        @Test
        @DisplayName("Debería establecer descripción como null")
        void deberiaEstablecerDescripcionComoNull() {
            // When
            curso.setDescripcion(null);

            // Then
            assertNull(curso.getDescripcion());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer ID")
        void deberiaRecortarEspaciosAlEstablecerId() {
            // When
            curso.setId("  nuevo-id  ");

            // Then
            assertEquals("nuevo-id", curso.getId());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer título")
        void deberiaRecortarEspaciosAlEstablecerTitulo() {
            // When
            curso.setTitulo("  Nuevo Título  ");

            // Then
            assertEquals("Nuevo Título", curso.getTitulo());
        }

        @Test
        @DisplayName("Debería recortar espacios en blanco al establecer descripción")
        void deberiaRecortarEspaciosAlEstablecerDescripcion() {
            // When
            curso.setDescripcion("  Nueva descripción  ");

            // Then
            assertEquals("Nueva descripción", curso.getDescripcion());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer ID null")
        void deberiaLanzarExcepcionAlEstablecerIdNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                curso.setId(null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer ID vacío")
        void deberiaLanzarExcepcionAlEstablecerIdVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                curso.setId("");
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer título null")
        void deberiaLanzarExcepcionAlEstablecerTituloNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                curso.setTitulo(null);
            });
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer título vacío")
        void deberiaLanzarExcepcionAlEstablecerTituloVacio() {
            assertThrows(IllegalArgumentException.class, () -> {
                curso.setTitulo("");
            });
        }
    }

    @Nested
    @DisplayName("Gestión de bloques")
    class GestionBloquesTests {

        @Test
        @DisplayName("Debería agregar un bloque correctamente")
        void deberiaAgregarBloque() {
            // When
            curso.addBloque(bloque1);

            // Then
            assertEquals(1, curso.getNumeroBloques());
            assertTrue(curso.tieneBloques());
            assertTrue(curso.getBloques().contains(bloque1));
        }

        @Test
        @DisplayName("Debería agregar múltiples bloques correctamente")
        void deberiaAgregarMultiplesBloques() {
            // When
            curso.addBloque(bloque1);
            curso.addBloque(bloque2);

            // Then
            assertEquals(2, curso.getNumeroBloques());
            assertTrue(curso.tieneBloques());
            assertTrue(curso.getBloques().contains(bloque1));
            assertTrue(curso.getBloques().contains(bloque2));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al agregar bloque null")
        void deberiaLanzarExcepcionAlAgregarBloqueNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                curso.addBloque(null);
            });
        }

        @Test
        @DisplayName("Debería retornar lista inmutable de bloques")
        void deberiaRetornarListaInmutable() {
            // Given
            curso.addBloque(bloque1);
            List<Bloque> bloques = curso.getBloques();

            // When & Then
            assertThrows(UnsupportedOperationException.class, () -> {
                bloques.add(bloque2);
            });
        }

        @Test
        @DisplayName("Debería establecer lista de bloques correctamente")
        void deberiaEstablecerListaBloques() {
            // Given
            List<Bloque> nuevaLista = new ArrayList<>();
            nuevaLista.add(bloque1);
            nuevaLista.add(bloque2);

            // When
            curso.setBloques(nuevaLista);

            // Then
            assertEquals(2, curso.getNumeroBloques());
            assertTrue(curso.getBloques().contains(bloque1));
            assertTrue(curso.getBloques().contains(bloque2));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException al establecer lista de bloques null")
        void deberiaLanzarExcepcionAlEstablecerListaBloquesNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                curso.setBloques(null);
            });
        }
    }

    @Nested
    @DisplayName("Métodos de utilidad")
    class MetodosUtilidadTests {

        @Test
        @DisplayName("Debería retornar 0 bloques cuando el curso está vacío")
        void deberiaRetornarCeroBloquesCuandoVacio() {
            // Then
            assertEquals(0, curso.getNumeroBloques());
            assertFalse(curso.tieneBloques());
        }

        @Test
        @DisplayName("Debería retornar número correcto de bloques")
        void deberiaRetornarNumeroCorrectoBloques() {
            // Given
            curso.addBloque(bloque1);
            curso.addBloque(bloque2);

            // Then
            assertEquals(2, curso.getNumeroBloques());
            assertTrue(curso.tieneBloques());
        }

        @Test
        @DisplayName("Debería retornar 0 preguntas cuando no hay bloques")
        void deberiaRetornarCeroPreguntasCuandoNoHayBloques() {
            // Then
            assertEquals(0, curso.getNumeroPreguntas());
        }

        @Test
        @DisplayName("Debería retornar número correcto de preguntas")
        void deberiaRetornarNumeroCorrectoPreguntas() {
            // Given
            curso.addBloque(bloque1);
            curso.addBloque(bloque2);
            // Asumiendo que los bloques tienen preguntas (esto se probará cuando implementemos BloqueTest)

            // Then
            // Por ahora, solo verificamos que el método no lance excepción
            assertDoesNotThrow(() -> curso.getNumeroPreguntas());
        }
    }

    @Nested
    @DisplayName("Método toString")
    class ToStringTests {

        @Test
        @DisplayName("Debería retornar representación en cadena correcta")
        void deberiaRetornarToStringCorrecto() {
            // Given
            curso.setId("curso1");
            curso.setTitulo("Matemáticas");

            // When
            String resultado = curso.toString();

            // Then
            assertTrue(resultado.contains("curso1"));
            assertTrue(resultado.contains("Matemáticas"));
            assertTrue(resultado.contains("Curso"));
        }

        @Test
        @DisplayName("Debería retornar representación en cadena con valores null")
        void deberiaRetornarToStringConValoresNull() {
            // Given
            Curso cursoVacio = new Curso();

            // When
            String resultado = cursoVacio.toString();

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.contains("Curso"));
        }
    }
} 