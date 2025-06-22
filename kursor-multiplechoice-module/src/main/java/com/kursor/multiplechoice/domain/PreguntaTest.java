package com.kursor.multiplechoice.domain;

import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;

/**
 * Representa una pregunta de tipo test con opciones múltiples en el sistema Kursor.
 * 
 * <p>Esta clase extiende {@link Pregunta} para implementar preguntas de opción múltiple,
 * donde el usuario debe seleccionar una respuesta correcta de entre varias opciones
 * disponibles. La pregunta incluye un enunciado, una lista de opciones y la respuesta
 * correcta.</p>
 * 
 * <p>Características específicas:</p>
 * <ul>
 *   <li><strong>Enunciado:</strong> Texto descriptivo de la pregunta</li>
 *   <li><strong>Opciones:</strong> Lista de posibles respuestas</li>
 *   <li><strong>Respuesta correcta:</strong> La opción que se considera válida</li>
 *   <li><strong>Verificación exacta:</strong> Comparación exacta de strings</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * List<String> opciones = Arrays.asList("Madrid", "Barcelona", "Valencia", "Sevilla");
 * PreguntaTest pregunta = new PreguntaTest("p1", "¿Cuál es la capital de España?", 
 *                                         opciones, "Madrid");
 * boolean esCorrecta = pregunta.esCorrecta("Madrid"); // true
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Pregunta
 */
public class PreguntaTest extends Pregunta {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(PreguntaTest.class);
    
    /** Enunciado o texto descriptivo de la pregunta */
    private String enunciado;
    
    /** Lista de opciones disponibles para la respuesta */
    private List<String> opciones;
    
    /** Respuesta correcta que debe coincidir exactamente */
    private String respuestaCorrecta;

    /**
     * Constructor para crear una nueva pregunta de tipo test.
     * 
     * <p>Inicializa una pregunta de opción múltiple con todos sus componentes.
     * El constructor valida que los parámetros sean válidos y registra la
     * creación en el log para facilitar el debugging.</p>
     * 
     * @param id Identificador único de la pregunta (no debe ser null)
     * @param enunciado Texto descriptivo de la pregunta (no debe ser null)
     * @param opciones Lista de opciones disponibles (no debe ser null o vacía)
     * @param respuestaCorrecta Respuesta correcta (debe estar en las opciones)
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public PreguntaTest(String id, String enunciado, List<String> opciones, String respuestaCorrecta) {
        super(id, "test");
        
        logger.debug("Creando pregunta 'test' - ID: " + id + ", Enunciado: " + enunciado);
        
        // Validar enunciado
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("Error al crear pregunta 'test': enunciado no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Enunciado no puede ser null o vacío");
        }
        
        // Validar opciones
        if (opciones == null || opciones.isEmpty()) {
            logger.error("Error al crear pregunta 'test': opciones no puede ser null o vacía - ID: " + id);
            throw new IllegalArgumentException("Opciones no puede ser null o vacía");
        }
        
        // Validar respuesta correcta
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            logger.error("Error al crear pregunta 'test': respuesta correcta no puede ser null o vacía - ID: " + id);
            throw new IllegalArgumentException("Respuesta correcta no puede ser null o vacía");
        }
        
        // Verificar que la respuesta correcta esté en las opciones
        if (!opciones.contains(respuestaCorrecta)) {
            logger.error("Error al crear pregunta 'test': respuesta correcta no está en las opciones - ID: " + id + 
                        ", Respuesta: " + respuestaCorrecta + ", Opciones: " + opciones);
            throw new IllegalArgumentException("Respuesta correcta debe estar en las opciones");
        }
        
        this.enunciado = enunciado.trim();
        this.opciones = new ArrayList<>(opciones); // Crear copia defensiva
        this.respuestaCorrecta = respuestaCorrecta.trim();
        
        logger.info("Pregunta 'test' creada exitosamente - ID: " + id + ", Opciones: " + opciones.size());
    }

    /**
     * Obtiene el enunciado de la pregunta.
     * 
     * @return El texto descriptivo de la pregunta
     */
    @Override
    public String getEnunciado() { 
        logger.debug("Obteniendo enunciado (" + enunciado + ") de pregunta 'test' - ID: " + getId());
        return enunciado; 
    }
    
    /**
     * Establece el enunciado de la pregunta.
     * 
     * @param enunciado El nuevo texto descriptivo (no debe ser null o vacío)
     * @throws IllegalArgumentException si el enunciado es null o vacío
     */
    public void setEnunciado(String enunciado) { 
        logger.debug("Estableciendo enunciado de pregunta 'test' - ID: " + getId() + 
                    ", Anterior: " + this.enunciado + ", Nuevo: " + enunciado);
        
        if (enunciado == null || enunciado.trim().isEmpty()) {
            logger.error("Error al establecer enunciado: no puede ser null o vacío - ID: " + getId());
            throw new IllegalArgumentException("Enunciado no puede ser null o vacío");
        }
        
        this.enunciado = enunciado.trim();
        logger.info("Enunciado de pregunta 'test' actualizado - ID: " + getId());
    }

    /**
     * Obtiene la lista de opciones disponibles.
     * 
     * @return Lista inmutable de opciones
     */
    public List<String> getOpciones() { 
        logger.debug("Obteniendo opciones de pregunta 'test' - ID: " + getId() + ", Cantidad: " + opciones.size());
        return new ArrayList<>(opciones); // Retornar copia defensiva
    }
    
    /**
     * Establece la lista de opciones disponibles.
     * 
     * @param opciones Nueva lista de opciones (no debe ser null o vacía)
     * @throws IllegalArgumentException si las opciones son inválidas
     */
    public void setOpciones(List<String> opciones) { 
        logger.debug("Estableciendo opciones de pregunta 'test' - ID: " + getId() + 
                    ", Cantidad anterior: " + this.opciones.size() + ", Nueva cantidad: " + 
                    (opciones != null ? opciones.size() : "null"));
        
        if (opciones == null || opciones.isEmpty()) {
            logger.error("Error al establecer opciones: no puede ser null o vacía - ID: " + getId());
            throw new IllegalArgumentException("Opciones no puede ser null o vacía");
        }
        
        // Verificar que la respuesta correcta esté en las nuevas opciones
        if (!opciones.contains(respuestaCorrecta)) {
            logger.error("Error al establecer opciones: respuesta correcta no está en las nuevas opciones - ID: " + 
                        getId() + ", Respuesta: " + respuestaCorrecta);
            throw new IllegalArgumentException("Respuesta correcta debe estar en las opciones");
        }
        
        this.opciones = new ArrayList<>(opciones); // Crear copia defensiva
        logger.info("Opciones de pregunta 'test' actualizadas - ID: " + getId() + ", Cantidad: " + opciones.size());
    }

    /**
     * Obtiene la respuesta correcta.
     * 
     * @return La respuesta correcta de la pregunta
     */
    public String getRespuestaCorrecta() { 
        logger.debug("Obteniendo respuesta correcta de pregunta 'test' - ID: " + getId());
        return respuestaCorrecta; 
    }
    
    /**
     * Establece la respuesta correcta.
     * 
     * @param respuestaCorrecta Nueva respuesta correcta (debe estar en las opciones)
     * @throws IllegalArgumentException si la respuesta no está en las opciones
     */
    public void setRespuestaCorrecta(String respuestaCorrecta) { 
        logger.debug("Estableciendo respuesta correcta de pregunta 'test' - ID: " + getId() + 
                    ", Anterior: " + this.respuestaCorrecta + ", Nueva: " + respuestaCorrecta);
        
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            logger.error("Error al establecer respuesta correcta: no puede ser null o vacía - ID: " + getId());
            throw new IllegalArgumentException("Respuesta correcta no puede ser null o vacía");
        }
        
        if (!opciones.contains(respuestaCorrecta)) {
            logger.error("Error al establecer respuesta correcta: no está en las opciones - ID: " + 
                        getId() + ", Respuesta: " + respuestaCorrecta + ", Opciones: " + opciones);
            throw new IllegalArgumentException("Respuesta correcta debe estar en las opciones");
        }
        
        this.respuestaCorrecta = respuestaCorrecta.trim();
        logger.info("Respuesta correcta de pregunta 'test' actualizada - ID: " + getId());
    }

    /**
     * Verifica si la respuesta proporcionada es correcta.
     * 
     * <p>Este método compara la respuesta del usuario con la respuesta correcta
     * de manera exacta. La comparación es case-sensitive y requiere coincidencia
     * exacta del string.</p>
     * 
     * <p>Características de la verificación:</p>
     * <ul>
     *   <li><strong>Case-sensitive:</strong> Distingue entre mayúsculas y minúsculas</li>
     *   <li><strong>Exacta:</strong> Requiere coincidencia exacta del string</li>
     *   <li><strong>Null-safe:</strong> Maneja respuestas null de forma segura</li>
     *   <li><strong>Logging detallado:</strong> Registra todas las verificaciones</li>
     * </ul>
     * 
     * @param respuesta La respuesta del usuario a verificar (puede ser null)
     * @return true si la respuesta coincide exactamente, false en caso contrario
     */
    @Override
    public boolean esCorrecta(String respuesta) {
        logger.debug("Verificando respuesta de pregunta 'test' - ID: " + getId() + 
                    ", Respuesta usuario: " + respuesta + ", Respuesta correcta: " + respuestaCorrecta);
        
        // Verificar si alguna respuesta es null
        if (respuesta == null || respuestaCorrecta == null) {
            logger.debug("Respuesta null en pregunta 'test' - ID: " + getId() + 
                        ", Respuesta usuario: " + (respuesta == null ? "null" : "no-null") + 
                        ", Respuesta correcta: " + (respuestaCorrecta == null ? "null" : "no-null"));
            return false;
        }
        
        // Comparación exacta (case-sensitive)
        boolean esCorrecta = respuesta.equals(respuestaCorrecta);
        
        if (esCorrecta) {
            logger.info("Respuesta correcta en pregunta 'test' - ID: " + getId() + 
                       ", Respuesta: " + respuesta);
        } else {
            logger.debug("Respuesta incorrecta en pregunta 'test' - ID: " + getId() + 
                        ", Esperada: " + respuestaCorrecta + ", Recibida: " + respuesta);
        }
        
        return esCorrecta;
    }
    
    /**
     * Representación en cadena de la pregunta test.
     * 
     * @return String con información básica de la pregunta
     */
    @Override
    public String toString() {
        return String.format("PreguntaTest{id='%s', enunciado='%s', opciones=%s, respuestaCorrecta='%s'}", 
                           getId(), enunciado, opciones, respuestaCorrecta);
    }
} 