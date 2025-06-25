package com.kursor.strategy.repeticionespaciada;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Tests para la estrategia de repetición espaciada.
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
@DisplayName("RepeticionEspaciadaStrategy Tests")
public class RepeticionEspaciadaStrategyTest {
    
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
    private RepeticionEspaciadaStrategy estrategia;
    
    @BeforeEach
    void setUp() {
        // Crear preguntas de prueba usando PreguntaTestSimple
        PreguntaTestSimple p1 = new PreguntaTestSimple("1", "¿Cuál es la capital de España?", "Madrid");
        PreguntaTestSimple p2 = new PreguntaTestSimple("2", "¿Cuál es la capital de Francia?", "París");
        PreguntaTestSimple p3 = new PreguntaTestSimple("3", "¿Cuál es la capital de Italia?", "Roma");
        
        this.preguntas = Arrays.asList(p1, p2, p3);
        this.estrategia = new RepeticionEspaciadaStrategy(preguntas);
    }
    
    @Test
    @DisplayName("Constructor debe inicializar correctamente")
    void testConstructor() {
        assertNotNull(estrategia);
        assertEquals("Repetición Espaciada", estrategia.getNombre());
        assertEquals(3, estrategia.getTotalPreguntas());
        assertEquals(3, estrategia.getPreguntasProgramadas());
    }
    
    @Test
    @DisplayName("Primera pregunta debe retornar una pregunta válida")
    void testPrimeraPregunta() {
        Pregunta primera = estrategia.primeraPregunta();
        assertNotNull(primera);
        assertTrue(preguntas.contains(primera));
        assertEquals(0, estrategia.getPreguntasProcesadas());
    }
    
    @Test
    @DisplayName("Hay más preguntas debe ser true inicialmente")
    void testHayMasPreguntas() {
        assertTrue(estrategia.hayMasPreguntas());
    }
    
    @Test
    @DisplayName("Progreso debe ser 0.0 al inicio")
    void testProgresoInicial() {
        assertEquals(0.0, estrategia.getProgreso());
    }
    
    @Test
    @DisplayName("Registrar respuesta correcta debe actualizar el progreso")
    void testRegistrarRespuestaCorrecta() {
        Pregunta pregunta = estrategia.primeraPregunta();
        Respuesta respuesta = new Respuesta("Madrid", true);
        
        estrategia.registrarRespuesta(respuesta);
        
        assertEquals(1, estrategia.getPreguntasProcesadas());
        assertTrue(estrategia.getProgreso() > 0.0);
    }
    
    @Test
    @DisplayName("Registrar respuesta incorrecta debe actualizar el progreso")
    void testRegistrarRespuestaIncorrecta() {
        Pregunta pregunta = estrategia.primeraPregunta();
        Respuesta respuesta = new Respuesta("Barcelona", false);
        
        estrategia.registrarRespuesta(respuesta);
        
        assertEquals(1, estrategia.getPreguntasProcesadas());
        assertTrue(estrategia.getProgreso() > 0.0);
    }
    
    @Test
    @DisplayName("Siguiente pregunta debe retornar preguntas diferentes")
    void testSiguientePregunta() {
        Pregunta primera = estrategia.primeraPregunta();
        estrategia.registrarRespuesta(new Respuesta("respuesta", true));
        
        Pregunta segunda = estrategia.siguientePregunta();
        assertNotNull(segunda);
        assertNotEquals(primera, segunda);
    }
    
    @Test
    @DisplayName("Serialización y deserialización debe mantener el estado")
    void testSerializacionDeserializacion() {
        // Ejecutar algunas preguntas
        Pregunta p1 = estrategia.primeraPregunta();
        estrategia.registrarRespuesta(new Respuesta("respuesta1", true));
        
        Pregunta p2 = estrategia.siguientePregunta();
        estrategia.registrarRespuesta(new Respuesta("respuesta2", false));
        
        // Serializar estado
        String estado = estrategia.serializarEstado();
        assertNotNull(estado);
        assertFalse(estado.isEmpty());
        
        // Crear nueva estrategia y deserializar
        RepeticionEspaciadaStrategy nuevaEstrategia = new RepeticionEspaciadaStrategy(preguntas);
        nuevaEstrategia.deserializarEstado(estado);
        
        // Verificar que el estado se mantiene
        assertEquals(estrategia.getPreguntasProcesadas(), nuevaEstrategia.getPreguntasProcesadas());
        assertEquals(estrategia.getTotalPreguntasSesion(), nuevaEstrategia.getTotalPreguntasSesion());
    }
    
    @Test
    @DisplayName("Factor de facilidad promedio debe estar en rango válido")
    void testFactorFacilidadPromedio() {
        double factorPromedio = estrategia.getFactorFacilidadPromedio();
        assertTrue(factorPromedio >= 1.3);
        assertTrue(factorPromedio <= 2.5);
    }
    
    @Test
    @DisplayName("Estado de pregunta debe ser accesible")
    void testGetEstadoPregunta() {
        String preguntaId = preguntas.get(0).getId();
        RepeticionEspaciadaStrategy.EstadoPregunta estado = estrategia.getEstadoPregunta(preguntaId);
        
        assertNotNull(estado);
        assertEquals(0, estado.repeticiones);
        assertEquals(1, estado.intervalo);
        assertEquals(2.5, estado.factorFacilidad, 0.01);
    }
    
    @Test
    @DisplayName("Constructor debe lanzar excepción con lista nula")
    void testConstructorListaNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RepeticionEspaciadaStrategy(null);
        });
    }
    
    @Test
    @DisplayName("Constructor debe lanzar excepción con lista vacía")
    void testConstructorListaVacia() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RepeticionEspaciadaStrategy(Arrays.asList());
        });
    }
    
    @Test
    @DisplayName("Ciclo completo de preguntas debe funcionar correctamente")
    void testCicloCompleto() {
        int preguntasProcesadas = 0;
        
        while (estrategia.hayMasPreguntas()) {
            Pregunta pregunta = preguntasProcesadas == 0 ? 
                estrategia.primeraPregunta() : 
                estrategia.siguientePregunta();
            
            assertNotNull(pregunta);
            
            // Simular respuesta
            Respuesta respuesta = new Respuesta("respuesta", true);
            estrategia.registrarRespuesta(respuesta);
            
            preguntasProcesadas++;
        }
        
        assertEquals(3, preguntasProcesadas);
        assertEquals(1.0, estrategia.getProgreso(), 0.01);
    }
}
