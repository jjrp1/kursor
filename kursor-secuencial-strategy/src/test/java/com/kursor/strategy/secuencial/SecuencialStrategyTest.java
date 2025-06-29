package com.kursor.strategy.secuencial;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Tests para la estrategia de aprendizaje secuencial.
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("SecuencialStrategy Tests")
public class SecuencialStrategyTest {
    
    /**
     * Implementación simple de Pregunta para testing.
     */
    private static class PreguntaTestSimple extends Pregunta {
        private final String enunciado;
        private final String respuestaCorrecta;
        
        public PreguntaTestSimple(String id, String enunciado, String respuestaCorrecta) {
            super(id, "test");
            this.enunciado = enunciado;
            this.respuestaCorrecta = respuestaCorrecta;
        }
        
        @Override
        public boolean esCorrecta(String respuesta) {
            return respuestaCorrecta.equals(respuesta);
        }
        
        @Override
        public String getEnunciado() {
            return enunciado;
        }
    }
    
    private List<Pregunta> preguntas;
    private SecuencialStrategy estrategia;
    
    @BeforeEach
    void setUp() {
        // Crear preguntas de prueba
        PreguntaTestSimple p1 = new PreguntaTestSimple("1", "¿Cuál es la capital de España?", "Madrid");
        PreguntaTestSimple p2 = new PreguntaTestSimple("2", "¿Cuál es la capital de Francia?", "París");
        PreguntaTestSimple p3 = new PreguntaTestSimple("3", "¿Cuál es la capital de Italia?", "Roma");
        
        this.preguntas = Arrays.asList(p1, p2, p3);
        this.estrategia = new SecuencialStrategy(preguntas);
    }
    
    @Nested
    @DisplayName("Constructor e inicialización")
    class ConstructorTests {
        
        @Test
        @DisplayName("Constructor debe inicializar correctamente")
        void testConstructor() {
            assertNotNull(estrategia);
            assertEquals("Secuencial", estrategia.getNombre());
            assertEquals(3, estrategia.getTotalPreguntas());
            assertEquals(3, estrategia.getPreguntasProgramadas());
        }
        
        @Test
        @DisplayName("Constructor debe lanzar excepción con lista vacía")
        void testConstructorConListaVacia() {
            assertThrows(IllegalArgumentException.class, () -> {
                new SecuencialStrategy(List.of());
            });
        }
        
        @Test
        @DisplayName("Constructor debe lanzar excepción con lista null")
        void testConstructorConListaNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new SecuencialStrategy(null);
            });
        }
    }
    
    @Nested
    @DisplayName("Navegación entre preguntas")
    class NavegacionTests {
        
        @Test
        @DisplayName("Primera pregunta debe retornar la primera de la lista")
        void testPrimeraPregunta() {
            Pregunta primera = estrategia.primeraPregunta();
            
            assertNotNull(primera);
            assertEquals("1", primera.getId());
            assertEquals("¿Cuál es la capital de España?", primera.getEnunciado());
            assertEquals(0, estrategia.getPreguntasProcesadas());
        }
        
        @Test
        @DisplayName("Siguiente pregunta debe avanzar secuencialmente")
        void testSiguientePregunta() {
            estrategia.primeraPregunta(); // Ir a la primera
            
            Pregunta segunda = estrategia.siguientePregunta();
            assertNotNull(segunda);
            assertEquals("2", segunda.getId());
            assertEquals("¿Cuál es la capital de Francia?", segunda.getEnunciado());
            assertEquals(1, estrategia.getPreguntasProcesadas());
            
            Pregunta tercera = estrategia.siguientePregunta();
            assertNotNull(tercera);
            assertEquals("3", tercera.getId());
            assertEquals("¿Cuál es la capital de Italia?", tercera.getEnunciado());
            assertEquals(2, estrategia.getPreguntasProcesadas());
        }
        
        @Test
        @DisplayName("Siguiente pregunta debe retornar null al final")
        void testSiguientePreguntaAlFinal() {
            estrategia.primeraPregunta(); // Primera
            estrategia.siguientePregunta(); // Segunda
            estrategia.siguientePregunta(); // Tercera
            
            Pregunta siguiente = estrategia.siguientePregunta();
            assertNull(siguiente);
            assertEquals(3, estrategia.getPreguntasProcesadas());
        }
        
        @Test
        @DisplayName("Hay más preguntas debe ser true inicialmente")
        void testHayMasPreguntas() {
            assertTrue(estrategia.hayMasPreguntas());
            
            estrategia.primeraPregunta();
            assertTrue(estrategia.hayMasPreguntas());
            
            estrategia.siguientePregunta();
            assertTrue(estrategia.hayMasPreguntas());
            
            estrategia.siguientePregunta();
            assertFalse(estrategia.hayMasPreguntas());
        }
    }
    
    @Nested
    @DisplayName("Cálculo de progreso")
    class ProgresoTests {
        
        @Test
        @DisplayName("Progreso debe ser 0.0 al inicio")
        void testProgresoInicial() {
            assertEquals(0.0, estrategia.getProgreso());
        }
        
        @Test
        @DisplayName("Progreso debe ser 0.33 después de la primera pregunta")
        void testProgresoPrimeraPregunta() {
            estrategia.primeraPregunta();
            
            double progreso = estrategia.getProgreso();
            assertEquals(1.0 / 3.0, progreso, 0.01);
        }
        
        @Test
        @DisplayName("Progreso debe ser 0.67 después de la segunda pregunta")
        void testProgresoSegundaPregunta() {
            estrategia.primeraPregunta();
            estrategia.siguientePregunta();
            
            double progreso = estrategia.getProgreso();
            assertEquals(2.0 / 3.0, progreso, 0.01);
        }
        
        @Test
        @DisplayName("Progreso debe ser 1.0 al completar todas las preguntas")
        void testProgresoCompleto() {
            estrategia.primeraPregunta();
            estrategia.siguientePregunta();
            estrategia.siguientePregunta();
            
            double progreso = estrategia.getProgreso();
            assertEquals(1.0, progreso, 0.01);
        }
    }
    
    @Nested
    @DisplayName("Registro de respuestas")
    class RegistroRespuestasTests {
        
        @Test
        @DisplayName("Registrar respuesta no debe afectar el orden")
        void testRegistrarRespuesta() {
            estrategia.primeraPregunta();
            
            // Registrar respuesta correcta
            Respuesta respuesta = new Respuesta("1", "Madrid", true);
            estrategia.registrarRespuesta(respuesta);
            
            // La siguiente pregunta debe seguir siendo la segunda
            Pregunta siguiente = estrategia.siguientePregunta();
            assertEquals("2", siguiente.getId());
        }
        
        @Test
        @DisplayName("Registrar múltiples respuestas debe mantener el orden")
        void testRegistrarMultiplesRespuestas() {
            estrategia.primeraPregunta();
            
            // Registrar respuesta correcta
            Respuesta respuesta1 = new Respuesta("1", "Madrid", true);
            estrategia.registrarRespuesta(respuesta1);
            
            estrategia.siguientePregunta();
            
            // Registrar respuesta incorrecta
            Respuesta respuesta2 = new Respuesta("2", "Londres", false);
            estrategia.registrarRespuesta(respuesta2);
            
            // La siguiente pregunta debe seguir siendo la tercera
            Pregunta siguiente = estrategia.siguientePregunta();
            assertEquals("3", siguiente.getId());
        }
    }
    
    @Nested
    @DisplayName("Serialización de estado")
    class SerializacionTests {
        
        @Test
        @DisplayName("Serializar estado debe retornar índice actual")
        void testSerializarEstado() {
            String estado = estrategia.serializarEstado();
            assertEquals("0", estado);
            
            estrategia.primeraPregunta();
            estado = estrategia.serializarEstado();
            assertEquals("0", estado);
            
            estrategia.siguientePregunta();
            estado = estrategia.serializarEstado();
            assertEquals("1", estado);
        }
        
        @Test
        @DisplayName("Deserializar estado debe restaurar posición")
        void testDeserializarEstado() {
            // Avanzar a la segunda pregunta
            estrategia.primeraPregunta();
            estrategia.siguientePregunta();
            
            // Serializar estado
            String estado = estrategia.serializarEstado();
            
            // Crear nueva instancia y deserializar
            SecuencialStrategy nuevaEstrategia = new SecuencialStrategy(preguntas);
            nuevaEstrategia.deserializarEstado(estado);
            
            // Verificar que está en la posición correcta
            assertEquals(1, nuevaEstrategia.getPreguntasProcesadas());
            
            // La siguiente pregunta debe ser la tercera
            Pregunta siguiente = nuevaEstrategia.siguientePregunta();
            assertEquals("3", siguiente.getId());
        }
        
        @Test
        @DisplayName("Deserializar estado inválido debe usar valor por defecto")
        void testDeserializarEstadoInvalido() {
            SecuencialStrategy nuevaEstrategia = new SecuencialStrategy(preguntas);
            
            // Deserializar estado inválido
            nuevaEstrategia.deserializarEstado("invalido");
            
            // Debería estar en la posición inicial
            assertEquals(0, nuevaEstrategia.getPreguntasProcesadas());
        }
    }
    
    @Nested
    @DisplayName("Métodos de utilidad")
    class MetodosUtilidadTests {
        
        @Test
        @DisplayName("GetTotalPreguntas debe retornar el número correcto")
        void testGetTotalPreguntas() {
            assertEquals(3, estrategia.getTotalPreguntas());
        }
        
        @Test
        @DisplayName("GetPreguntasProgramadas debe retornar el número correcto")
        void testGetPreguntasProgramadas() {
            assertEquals(3, estrategia.getPreguntasProgramadas());
        }
        
        @Test
        @DisplayName("GetPreguntasProcesadas debe retornar el número correcto")
        void testGetPreguntasProcesadas() {
            assertEquals(0, estrategia.getPreguntasProcesadas());
            
            estrategia.primeraPregunta();
            assertEquals(0, estrategia.getPreguntasProcesadas());
            
            estrategia.siguientePregunta();
            assertEquals(1, estrategia.getPreguntasProcesadas());
        }
        
        @Test
        @DisplayName("GetNombre debe retornar el nombre correcto")
        void testGetNombre() {
            assertEquals("Secuencial", estrategia.getNombre());
        }
    }
    
    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTests {
        
        @Test
        @DisplayName("Estrategia con una sola pregunta")
        void testEstrategiaConUnaSolaPregunta() {
            PreguntaTestSimple p1 = new PreguntaTestSimple("1", "Pregunta única", "Respuesta");
            SecuencialStrategy estrategiaUnica = new SecuencialStrategy(Arrays.asList(p1));
            
            assertNotNull(estrategiaUnica.primeraPregunta());
            assertFalse(estrategiaUnica.hayMasPreguntas());
            assertNull(estrategiaUnica.siguientePregunta());
            assertEquals(1.0, estrategiaUnica.getProgreso(), 0.01);
        }
        
        @Test
        @DisplayName("Estrategia con muchas preguntas")
        void testEstrategiaConMuchasPreguntas() {
            List<Pregunta> muchasPreguntas = Arrays.asList(
                new PreguntaTestSimple("1", "Pregunta 1", "Respuesta 1"),
                new PreguntaTestSimple("2", "Pregunta 2", "Respuesta 2"),
                new PreguntaTestSimple("3", "Pregunta 3", "Respuesta 3"),
                new PreguntaTestSimple("4", "Pregunta 4", "Respuesta 4"),
                new PreguntaTestSimple("5", "Pregunta 5", "Respuesta 5")
            );
            
            SecuencialStrategy estrategiaGrande = new SecuencialStrategy(muchasPreguntas);
            
            assertEquals(5, estrategiaGrande.getTotalPreguntas());
            assertEquals(5, estrategiaGrande.getPreguntasProgramadas());
            
            // Navegar por todas las preguntas
            estrategiaGrande.primeraPregunta();
            for (int i = 0; i < 4; i++) {
                assertTrue(estrategiaGrande.hayMasPreguntas());
                assertNotNull(estrategiaGrande.siguientePregunta());
            }
            
            assertFalse(estrategiaGrande.hayMasPreguntas());
            assertNull(estrategiaGrande.siguientePregunta());
        }
        
        @Test
        @DisplayName("Deserializar estado con índice mayor al total")
        void testDeserializarEstadoConIndiceMayor() {
            estrategia.deserializarEstado("10"); // Índice mayor al número de preguntas
            
            // Debería estar en la posición inicial
            assertEquals(0, estrategia.getPreguntasProcesadas());
        }
        
        @Test
        @DisplayName("Deserializar estado con índice negativo")
        void testDeserializarEstadoConIndiceNegativo() {
            estrategia.deserializarEstado("-1");
            
            // Debería estar en la posición inicial
            assertEquals(0, estrategia.getPreguntasProcesadas());
        }
    }
} 