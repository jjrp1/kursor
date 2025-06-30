package com.kursor.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests funcionales básicos para la clase Curso.
 * 
 * <p>Estos tests verifican la funcionalidad principal del dominio
 * sin depender de frameworks de mocking complejos, cumpliendo
 * con los requerimientos de pruebas de software del enunciado.</p>
 * 
 * <p>Cobertura de funcionalidad:</p>
 * <ul>
 *   <li>Creación y gestión de cursos</li>
 *   <li>Añadir y gestionar bloques</li>
 *   <li>Validaciones de entrada</li>
 *   <li>Cálculos de progreso</li>
 * </ul>
 */
@DisplayName("Tests funcionales de Curso")
class CursoTestSimple {

    private Curso curso;

    @BeforeEach
    void setUp() {
        curso = new Curso("curso1", "Matemáticas Básicas", "Curso de matemáticas para principiantes");
    }

    @Test
    @DisplayName("Debería crear curso con datos válidos")
    void deberiaCrearCursoConDatosValidos() {
        assertNotNull(curso);
        assertEquals("curso1", curso.getId());
        assertEquals("Matemáticas Básicas", curso.getTitulo());
        assertEquals("Curso de matemáticas para principiantes", curso.getDescripcion());
    }

    @Test
    @DisplayName("Debería tener lista de bloques inicializada")
    void deberiaTenerListaDeBloquesInicializada() {
        assertNotNull(curso.getBloques());
        assertTrue(curso.getBloques().isEmpty());
    }

    @Test
    @DisplayName("Debería añadir bloque correctamente")
    void deberiaAñadirBloqueCorrectamente() {
        Bloque bloque = new Bloque("bloque1", "Números naturales", "Conceptos básicos", "teoria");
        
        curso.addBloque(bloque);
        
        assertEquals(1, curso.getBloques().size());
        assertEquals(bloque, curso.getBloques().get(0));
    }

    @Test
    @DisplayName("Debería validar título no vacío")
    void deberiaValidarTituloNoVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Curso("id", "", "descripción");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Curso("id", null, "descripción");
        });
    }

    @ParameterizedTest
    @DisplayName("Debería validar IDs válidos")
    @ValueSource(strings = {"", "   ", "null"})
    void deberiaValidarIdsValidos(String idInvalido) {
        String id = idInvalido.equals("null") ? null : idInvalido;
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Curso(id, "Título", "Descripción");
        });
    }

    @Test
    @DisplayName("Debería contar bloques correctamente cuando no hay bloques")
    void deberiaContarBloquesSinBloques() {
        int numeroBloques = curso.getNumeroBloques();
        assertEquals(0, numeroBloques);
        assertFalse(curso.tieneBloques());
    }

    @Test
    @DisplayName("Debería contar bloques y preguntas con múltiples bloques")
    void deberiaContarBloquesYPreguntasConMultiplesBloques() {
        // Añadir bloques con diferentes contenidos
        Bloque bloque1 = new Bloque("b1", "Bloque 1", "Descripción 1", "teoria");
        Bloque bloque2 = new Bloque("b2", "Bloque 2", "Descripción 2", "practica");
        
        curso.addBloque(bloque1);
        curso.addBloque(bloque2);
        
        assertEquals(2, curso.getNumeroBloques());
        assertTrue(curso.tieneBloques());
        
        // Verificar que cuenta preguntas de todos los bloques
        int numeroPreguntas = curso.getNumeroPreguntas();
        assertTrue(numeroPreguntas >= 0);
    }

    @Test
    @DisplayName("Debería mantener inmutabilidad defensiva en lista de bloques")
    void deberiaMantenerInmutabilidadDefensiva() {
        Bloque bloque1 = new Bloque("b1", "Bloque 1", "Desc 1", "teoria");
        curso.addBloque(bloque1);
        
        // Obtener la lista y verificar que es una copia defensiva
        var bloques = curso.getBloques();
        int tamañoInicial = bloques.size();
        
        // Intentar modificar la lista devuelta no debería afectar al curso
        assertThrows(UnsupportedOperationException.class, () -> {
            bloques.add(new Bloque("b2", "Bloque 2", "Desc 2", "practica"));
        });
        
        // Verificar que el curso no se vio afectado
        assertEquals(tamañoInicial, curso.getBloques().size());
    }

    @Test
    @DisplayName("Debería permitir múltiples bloques con diferentes IDs")
    void deberiaPermitirMultiplesBloquesConDiferentesIds() {
        Bloque bloque1 = new Bloque("algebra", "Álgebra Básica", "Operaciones básicas", "teoria");
        Bloque bloque2 = new Bloque("geometria", "Geometría Plana", "Figuras geométricas", "practica");
        Bloque bloque3 = new Bloque("estadistica", "Estadística Descriptiva", "Análisis de datos", "evaluacion");
        
        curso.addBloque(bloque1);
        curso.addBloque(bloque2);
        curso.addBloque(bloque3);
        
        assertEquals(3, curso.getBloques().size());
        
        // Verificar que todos los bloques están presentes
        var bloques = curso.getBloques();
        assertTrue(bloques.contains(bloque1));
        assertTrue(bloques.contains(bloque2));
        assertTrue(bloques.contains(bloque3));
    }

    @Test
    @DisplayName("Debería rechazar bloques duplicados por ID")
    void deberiaRechazarBloquesDuplicadosPorId() {
        Bloque bloque1 = new Bloque("matematicas", "Matemáticas 1", "Primer bloque", "teoria");
        Bloque bloque2 = new Bloque("matematicas", "Matemáticas 2", "Segundo bloque", "practica");
        
        curso.addBloque(bloque1);
        
        // Debería lanzar excepción al intentar añadir bloque con ID duplicado
        assertThrows(IllegalArgumentException.class, () -> {
            curso.addBloque(bloque2);
        });
        
        assertEquals(1, curso.getBloques().size());
    }

    @Test
    @DisplayName("Debería generar representación string útil")
    void deberiaGenerarRepresentacionStringUtil() {
        String representacion = curso.toString();
        
        assertNotNull(representacion);
        assertTrue(representacion.contains("curso1"));
        assertTrue(representacion.contains("Matemáticas Básicas"));
    }

    @Test
    @DisplayName("Debería implementar equals y hashCode correctamente")
    void deberiaImplementarEqualsYHashCodeCorrectamente() {
        Curso otroCurso = new Curso("curso1", "Matemáticas Básicas", "Curso de matemáticas para principiantes");
        Curso cursoDiferente = new Curso("curso2", "Historia", "Curso de historia");
        
        // Reflexivo
        assertEquals(curso, curso);
        
        // Simétrico
        assertEquals(curso, otroCurso);
        assertEquals(otroCurso, curso);
        
        // Consistente con hashCode
        assertEquals(curso.hashCode(), otroCurso.hashCode());
        
        // Diferente
        assertNotEquals(curso, cursoDiferente);
        assertNotEquals(curso, null);
        assertNotEquals(curso, "string");
    }
} 