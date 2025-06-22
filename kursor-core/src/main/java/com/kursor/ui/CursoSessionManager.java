package com.kursor.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Gestor de sesión para el progreso del usuario en un curso.
 * 
 * <p>Esta clase se encarga de gestionar el estado de la sesión del usuario
 * durante la realización de un curso, incluyendo el seguimiento de respuestas,
 * el progreso actual y la persistencia de datos.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Seguimiento de respuestas:</strong> Registra respuestas correctas e incorrectas</li>
 *   <li><strong>Estado de progreso:</strong> Mantiene la posición actual en el curso</li>
 *   <li><strong>Persistencia:</strong> Guarda y carga el progreso del usuario</li>
 *   <li><strong>Estadísticas:</strong> Proporciona información sobre el rendimiento</li>
 * </ul>
 * 
 * <p>En futuras versiones, esta clase se integrará con JPA para persistencia
 * en base de datos. Por ahora, utiliza almacenamiento en memoria.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class CursoSessionManager {
    
    /** Logger para registrar eventos de la sesión */
    private static final Logger logger = LoggerFactory.getLogger(CursoSessionManager.class);
    
    /** Identificador del curso */
    private final String cursoId;
    
    /** Estrategia de aprendizaje seleccionada */
    private String estrategiaSeleccionada;
    
    /** Mapa de respuestas del usuario: preguntaId -> esCorrecta */
    private final Map<String, Boolean> respuestas;
    
    /** Lista de preguntas respondidas en orden */
    private final List<String> preguntasRespondidas;
    
    /** Bloque actual */
    private int bloqueActual;
    
    /** Pregunta actual dentro del bloque */
    private int preguntaActual;
    
    /** Indica si la sesión ha sido inicializada */
    private boolean inicializada;
    
    /**
     * Constructor para crear un gestor de sesión para un curso específico.
     * 
     * @param cursoId Identificador único del curso
     * @param estrategia Estrategia de aprendizaje seleccionada
     */
    public CursoSessionManager(String cursoId, String estrategia) {
        this.cursoId = cursoId;
        this.estrategiaSeleccionada = estrategia;
        this.respuestas = new HashMap<>();
        this.preguntasRespondidas = new ArrayList<>();
        this.bloqueActual = 0;
        this.preguntaActual = 0;
        this.inicializada = false;
        
        logger.info("CursoSessionManager creado para curso: " + cursoId + " con estrategia: " + estrategia);
    }
    
    /**
     * Constructor para crear un gestor de sesión para un curso específico (sin estrategia).
     * 
     * @param cursoId Identificador único del curso
     */
    public CursoSessionManager(String cursoId) {
        this(cursoId, "Secuencial"); // Estrategia por defecto
    }
    
    /**
     * Inicializa la sesión cargando el progreso guardado.
     */
    public void inicializar() {
        logger.info("Inicializando sesión para curso: " + cursoId);
        
        // Cargar progreso guardado (implementación futura con JPA)
        cargarProgreso();
        
        this.inicializada = true;
        logger.info("Sesión inicializada - Preguntas respondidas: " + respuestas.size());
    }
    
    /**
     * Guarda una respuesta del usuario.
     * 
     * @param preguntaId Identificador de la pregunta
     * @param esCorrecta true si la respuesta es correcta, false en caso contrario
     */
    public void guardarRespuesta(String preguntaId, boolean esCorrecta) {
        logger.debug("Guardando respuesta - Pregunta: " + preguntaId + ", Correcta: " + esCorrecta);
        
        respuestas.put(preguntaId, esCorrecta);
        
        if (!preguntasRespondidas.contains(preguntaId)) {
            preguntasRespondidas.add(preguntaId);
        }
        
        // Guardar progreso (implementación futura con JPA)
        guardarProgreso();
        
        logger.info("Respuesta guardada - Total respuestas: " + respuestas.size());
    }
    
    /**
     * Verifica si una pregunta ya ha sido respondida.
     * 
     * @param preguntaId Identificador de la pregunta
     * @return true si la pregunta ya fue respondida, false en caso contrario
     */
    public boolean preguntaRespondida(String preguntaId) {
        boolean respondida = respuestas.containsKey(preguntaId);
        logger.debug("Verificando si pregunta está respondida - ID: " + preguntaId + ", Respondida: " + respondida);
        return respondida;
    }
    
    /**
     * Obtiene la respuesta guardada para una pregunta.
     * 
     * @param preguntaId Identificador de la pregunta
     * @return true si la respuesta fue correcta, false si fue incorrecta, null si no fue respondida
     */
    public Boolean obtenerRespuesta(String preguntaId) {
        Boolean respuesta = respuestas.get(preguntaId);
        logger.debug("Obteniendo respuesta guardada - ID: " + preguntaId + ", Respuesta: " + respuesta);
        return respuesta;
    }
    
    /**
     * Establece la posición actual en el curso.
     * 
     * @param bloqueActual Índice del bloque actual
     * @param preguntaActual Índice de la pregunta actual dentro del bloque
     */
    public void establecerPosicion(int bloqueActual, int preguntaActual) {
        logger.debug("Estableciendo posición - Bloque: " + bloqueActual + ", Pregunta: " + preguntaActual);
        
        this.bloqueActual = bloqueActual;
        this.preguntaActual = preguntaActual;
        
        // Guardar progreso
        guardarProgreso();
    }
    
    /**
     * Obtiene la posición actual en el curso.
     * 
     * @return Array con [bloqueActual, preguntaActual]
     */
    public int[] obtenerPosicion() {
        int[] posicion = {bloqueActual, preguntaActual};
        logger.debug("Obteniendo posición actual - Bloque: " + bloqueActual + ", Pregunta: " + preguntaActual);
        return posicion;
    }
    
    /**
     * Obtiene estadísticas del progreso del curso.
     * 
     * @return Mapa con estadísticas del curso
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        int totalPreguntas = respuestas.size();
        int correctas = (int) respuestas.values().stream().filter(correcta -> correcta).count();
        int incorrectas = totalPreguntas - correctas;
        double porcentaje = totalPreguntas > 0 ? (double) correctas / totalPreguntas * 100 : 0;
        
        estadisticas.put("totalPreguntas", totalPreguntas);
        estadisticas.put("correctas", correctas);
        estadisticas.put("incorrectas", incorrectas);
        estadisticas.put("porcentaje", porcentaje);
        estadisticas.put("preguntasRespondidas", new ArrayList<>(preguntasRespondidas));
        
        logger.info("Estadísticas calculadas - Total: " + totalPreguntas + 
                   ", Correctas: " + correctas + ", Porcentaje: " + porcentaje + "%");
        
        return estadisticas;
    }
    
    /**
     * Reinicia la sesión, eliminando todo el progreso.
     */
    public void reiniciar() {
        logger.info("Reiniciando sesión para curso: " + cursoId);
        
        respuestas.clear();
        preguntasRespondidas.clear();
        bloqueActual = 0;
        preguntaActual = 0;
        
        // Guardar progreso reiniciado
        guardarProgreso();
        
        logger.info("Sesión reiniciada");
    }
    
    /**
     * Verifica si la sesión está inicializada.
     * 
     * @return true si la sesión está inicializada, false en caso contrario
     */
    public boolean estaInicializada() {
        return inicializada;
    }
    
    /**
     * Obtiene el identificador del curso.
     * 
     * @return Identificador del curso
     */
    public String getCursoId() {
        return cursoId;
    }
    
    /**
     * Obtiene el número total de preguntas respondidas.
     * 
     * @return Número de preguntas respondidas
     */
    public int getNumeroPreguntasRespondidas() {
        return respuestas.size();
    }
    
    /**
     * Obtiene la lista de preguntas respondidas en orden.
     * 
     * @return Lista de identificadores de preguntas respondidas
     */
    public List<String> getPreguntasRespondidas() {
        return new ArrayList<>(preguntasRespondidas);
    }
    
    /**
     * Obtiene la estrategia de aprendizaje seleccionada.
     * 
     * @return Nombre de la estrategia
     */
    public String getEstrategiaSeleccionada() {
        return estrategiaSeleccionada;
    }
    
    /**
     * Establece la estrategia de aprendizaje.
     * 
     * @param estrategia Nombre de la estrategia
     */
    public void setEstrategiaSeleccionada(String estrategia) {
        this.estrategiaSeleccionada = estrategia;
        logger.info("Estrategia actualizada: " + estrategia);
    }
    
    /**
     * Carga el progreso guardado del usuario.
     * 
     * <p>En la implementación actual, este método es un placeholder.
     * En futuras versiones, se implementará la carga desde base de datos
     * usando JPA.</p>
     */
    private void cargarProgreso() {
        logger.debug("Cargando progreso para curso: " + cursoId);
        
        // TODO: Implementar carga desde base de datos con JPA
        // Por ahora, no se carga ningún progreso (sesión nueva)
        
        logger.info("Progreso cargado (sesión nueva)");
    }
    
    /**
     * Guarda el progreso actual del usuario.
     * 
     * <p>En la implementación actual, este método es un placeholder.
     * En futuras versiones, se implementará el guardado en base de datos
     * usando JPA.</p>
     */
    private void guardarProgreso() {
        logger.debug("Guardando progreso para curso: " + cursoId);
        
        // TODO: Implementar guardado en base de datos con JPA
        // Por ahora, solo se registra en el log
        
        logger.info("Progreso guardado - Respuestas: " + respuestas.size() + 
                   ", Posición: Bloque " + bloqueActual + ", Pregunta " + preguntaActual);
    }
    
    /**
     * Representación en cadena del gestor de sesión.
     * 
     * @return String con información básica de la sesión
     */
    @Override
    public String toString() {
        return String.format("CursoSessionManager{cursoId='%s', respuestas=%d, inicializada=%s}", 
                           cursoId, respuestas.size(), inicializada);
    }
} 