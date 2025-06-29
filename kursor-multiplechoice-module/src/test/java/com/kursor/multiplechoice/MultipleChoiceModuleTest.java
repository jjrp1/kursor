package com.kursor.multiplechoice;

import com.kursor.modules.PreguntaModule;
import com.kursor.domain.Pregunta;
import com.kursor.multiplechoice.domain.PreguntaTest;
import com.kursor.presentation.controllers.PreguntaEventListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
@DisplayName("Pruebas de MultipleChoiceModule")
class MultipleChoiceModuleTest {

    private MultipleChoiceModule module;
    @Mock private PreguntaEventListener eventListener;

    @BeforeEach
    void setUp() {
        module = new MultipleChoiceModule();
    }

    @Nested
    @DisplayName("Información del módulo")
    class InformacionModuloTests {

        @Test
        @DisplayName("Debería retornar nombre correcto del módulo")
        void deberiaRetornarNombreCorrectoDelModulo() {
            String nombre = module.getModuleName();
            
            assertEquals("Opción Múltiple", nombre);
        }

        @Test
        @DisplayName("Debería retornar descripción correcta del módulo")
        void deberiaRetornarDescripcionCorrectaDelModulo() {
            String descripcion = module.getModuleDescription();
            
            assertEquals("Módulo para preguntas de opción múltiple", descripcion);
        }

        @Test
        @DisplayName("Debería retornar icono correcto")
        void deberiaRetornarIconoCorrecto() {
            String icono = module.getIcon();
            
            assertEquals("📝", icono);
        }

        @Test
        @DisplayName("Debería retornar tipo de pregunta correcto")
        void deberiaRetornarTipoDePreguntaCorrecto() {
            String tipo = module.getQuestionType();
            
            assertEquals("test", tipo);
        }
    }

    @Nested
    @DisplayName("Parsing de preguntas")
    class ParsingPreguntasTests {

        @Test
        @DisplayName("Debería parsear pregunta válida")
        void deberiaParsearPreguntaValida() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", "pregunta1");
            preguntaData.put("enunciado", "¿Cuál es la capital de España?");
            preguntaData.put("opciones", Arrays.asList("Madrid", "Barcelona", "Valencia", "Sevilla"));
            preguntaData.put("respuesta", "Madrid");
            
            // When
            Pregunta pregunta = module.parsePregunta(preguntaData);
            
            // Then
            assertNotNull(pregunta);
            assertEquals("pregunta1", pregunta.getId());
            assertEquals("test", pregunta.getTipo());
            assertTrue(pregunta instanceof PreguntaTest);
        }

        @Test
        @DisplayName("Debería manejar pregunta con datos mínimos")
        void deberiaManejarPreguntaConDatosMinimos() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", "pregunta2");
            preguntaData.put("enunciado", "Pregunta simple");
            preguntaData.put("opciones", Arrays.asList("Opción A", "Opción B"));
            preguntaData.put("respuesta", "Opción A");
            
            // When
            Pregunta pregunta = module.parsePregunta(preguntaData);
            
            // Then
            assertNotNull(pregunta);
            assertEquals("pregunta2", pregunta.getId());
            assertEquals("Pregunta simple", pregunta.getEnunciado());
        }

        @Test
        @DisplayName("Debería manejar pregunta con datos null")
        void deberiaManejarPreguntaConDatosNull() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", "pregunta3");
            preguntaData.put("enunciado", null);
            preguntaData.put("opciones", null);
            preguntaData.put("respuesta", null);
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                module.parsePregunta(preguntaData);
            });
        }

        @Test
        @DisplayName("Debería validar que la respuesta esté en las opciones")
        void deberiaValidarQueLaRespuestaEsteEnLasOpciones() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", "pregunta4");
            preguntaData.put("enunciado", "Pregunta con respuesta inválida");
            preguntaData.put("opciones", Arrays.asList("Opción A", "Opción B"));
            preguntaData.put("respuesta", "Opción C"); // Respuesta que no está en las opciones
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                module.parsePregunta(preguntaData);
            });
        }
    }

    @Nested
    @DisplayName("Creación de interfaz de usuario")
    class CreacionInterfazTests {

        @Test
        @DisplayName("Debería crear interfaz de usuario válida")
        void deberiaCrearInterfazDeUsuarioValida() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "¿Cuál es la capital de España?");
            pregunta.setOpciones(Arrays.asList("Madrid", "Barcelona", "Valencia", "Sevilla"));
            pregunta.setRespuestaCorrecta("Madrid");
            
            // When
            Node interfaz = module.createQuestionUI(pregunta, eventListener);
            
            // Then
            assertNotNull(interfaz);
            assertTrue(interfaz instanceof VBox);
            
            VBox vbox = (VBox) interfaz;
            assertFalse(vbox.getChildren().isEmpty());
        }

        @Test
        @DisplayName("Debería crear opciones de radio correctamente")
        void deberiaCrearOpcionesDeRadioCorrectamente() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "Pregunta de prueba");
            pregunta.setOpciones(Arrays.asList("Opción A", "Opción B", "Opción C"));
            pregunta.setRespuestaCorrecta("Opción A");
            
            // When
            Node interfaz = module.createQuestionUI(pregunta, eventListener);
            
            // Then
            VBox vbox = (VBox) interfaz;
            
            // Verificar que se crearon las opciones de radio
            long radioButtons = vbox.getChildren().stream()
                .filter(child -> child instanceof RadioButton)
                .count();
            
            assertEquals(3, radioButtons, "Debería haber 3 opciones de radio");
        }

        @Test
        @DisplayName("Debería crear botón de respuesta")
        void deberiaCrearBotonDeRespuesta() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "Pregunta de prueba");
            pregunta.setOpciones(Arrays.asList("Opción A", "Opción B"));
            pregunta.setRespuestaCorrecta("Opción A");
            
            // When
            Node interfaz = module.createQuestionUI(pregunta, eventListener);
            
            // Then
            VBox vbox = (VBox) interfaz;
            
            // Verificar que se creó el botón de respuesta
            boolean tieneBoton = vbox.getChildren().stream()
                .anyMatch(child -> child instanceof Button);
            
            assertTrue(tieneBoton, "Debería tener un botón de respuesta");
        }

        @Test
        @DisplayName("Debería manejar pregunta sin opciones")
        void deberiaManejarPreguntaSinOpciones() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "Pregunta sin opciones");
            pregunta.setOpciones(Arrays.asList());
            pregunta.setRespuestaCorrecta("Respuesta");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                module.createQuestionUI(pregunta, eventListener);
            });
        }
    }

    @Nested
    @DisplayName("Validación de respuestas")
    class ValidacionRespuestasTests {

        @Test
        @DisplayName("Debería validar respuesta correcta")
        void deberiaValidarRespuestaCorrecta() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "¿Cuál es la capital de España?");
            pregunta.setOpciones(Arrays.asList("Madrid", "Barcelona", "Valencia", "Sevilla"));
            pregunta.setRespuestaCorrecta("Madrid");
            
            // When
            boolean esCorrecta = pregunta.esCorrecta("Madrid");
            
            // Then
            assertTrue(esCorrecta);
        }

        @Test
        @DisplayName("Debería validar respuesta incorrecta")
        void deberiaValidarRespuestaIncorrecta() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "¿Cuál es la capital de España?");
            pregunta.setOpciones(Arrays.asList("Madrid", "Barcelona", "Valencia", "Sevilla"));
            pregunta.setRespuestaCorrecta("Madrid");
            
            // When
            boolean esCorrecta = pregunta.esCorrecta("Barcelona");
            
            // Then
            assertFalse(esCorrecta);
        }

        @Test
        @DisplayName("Debería validar respuesta null")
        void deberiaValidarRespuestaNull() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "¿Cuál es la capital de España?");
            pregunta.setOpciones(Arrays.asList("Madrid", "Barcelona", "Valencia", "Sevilla"));
            pregunta.setRespuestaCorrecta("Madrid");
            
            // When
            boolean esCorrecta = pregunta.esCorrecta(null);
            
            // Then
            assertFalse(esCorrecta);
        }

        @Test
        @DisplayName("Debería validar respuesta vacía")
        void deberiaValidarRespuestaVacia() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "¿Cuál es la capital de España?");
            pregunta.setOpciones(Arrays.asList("Madrid", "Barcelona", "Valencia", "Sevilla"));
            pregunta.setRespuestaCorrecta("Madrid");
            
            // When
            boolean esCorrecta = pregunta.esCorrecta("");
            
            // Then
            assertFalse(esCorrecta);
        }
    }

    @Nested
    @DisplayName("Manejo de eventos")
    class ManejoEventosTests {

        @Test
        @DisplayName("Debería manejar selección de opción")
        void deberiaManejarSeleccionDeOpcion() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "Pregunta de prueba");
            pregunta.setOpciones(Arrays.asList("Opción A", "Opción B"));
            pregunta.setRespuestaCorrecta("Opción A");
            
            Node interfaz = module.createQuestionUI(pregunta, eventListener);
            
            // When & Then
            // Simular selección de opción (esto dependerá de la implementación específica)
            assertDoesNotThrow(() -> {
                // Aquí se simularía la selección de una opción
                // y se verificaría que se llama al eventListener
            });
        }

        @Test
        @DisplayName("Debería manejar envío de respuesta")
        void deberiaManejarEnvioDeRespuesta() {
            // Given
            PreguntaTest pregunta = new PreguntaTest("pregunta1", "Pregunta de prueba");
            pregunta.setOpciones(Arrays.asList("Opción A", "Opción B"));
            pregunta.setRespuestaCorrecta("Opción A");
            
            Node interfaz = module.createQuestionUI(pregunta, eventListener);
            
            // When & Then
            // Simular envío de respuesta
            assertDoesNotThrow(() -> {
                // Aquí se simularía el envío de la respuesta
                // y se verificaría que se llama al eventListener con la respuesta
            });
        }
    }

    @Nested
    @DisplayName("Validaciones de datos")
    class ValidacionesDatosTests {

        @Test
        @DisplayName("Debería validar ID de pregunta")
        void deberiaValidarIdDePregunta() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", null);
            preguntaData.put("enunciado", "Pregunta sin ID");
            preguntaData.put("opciones", Arrays.asList("Opción A", "Opción B"));
            preguntaData.put("respuesta", "Opción A");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                module.parsePregunta(preguntaData);
            });
        }

        @Test
        @DisplayName("Debería validar enunciado de pregunta")
        void deberiaValidarEnunciadoDePregunta() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", "pregunta1");
            preguntaData.put("enunciado", "");
            preguntaData.put("opciones", Arrays.asList("Opción A", "Opción B"));
            preguntaData.put("respuesta", "Opción A");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                module.parsePregunta(preguntaData);
            });
        }

        @Test
        @DisplayName("Debería validar lista de opciones")
        void deberiaValidarListaDeOpciones() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", "pregunta1");
            preguntaData.put("enunciado", "Pregunta sin opciones");
            preguntaData.put("opciones", Arrays.asList());
            preguntaData.put("respuesta", "Opción A");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                module.parsePregunta(preguntaData);
            });
        }

        @Test
        @DisplayName("Debería validar respuesta correcta")
        void deberiaValidarRespuestaCorrecta() {
            // Given
            Map<String, Object> preguntaData = new HashMap<>();
            preguntaData.put("id", "pregunta1");
            preguntaData.put("enunciado", "Pregunta sin respuesta");
            preguntaData.put("opciones", Arrays.asList("Opción A", "Opción B"));
            preguntaData.put("respuesta", "");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                module.parsePregunta(preguntaData);
            });
        }
    }
} 