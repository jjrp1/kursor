package com.kursor.ui;

/**
 * Interfaz para manejar eventos de preguntas desde los módulos hacia CursoInterfaceView.
 * 
 * <p>Esta interfaz define los contratos de comunicación que permiten a los módulos
 * de preguntas notificar eventos importantes al contenedor principal (CursoInterfaceView)
 * sin crear dependencias circulares.</p>
 * 
 * <p>Los módulos implementan su lógica específica de validación y contenido,
 * pero delegan la gestión de estado global, progreso y navegación al contenedor.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 * @see CursoInterfaceView
 * @see com.kursor.modules.PreguntaModule
 */
public interface PreguntaEventListener {
    
    /**
     * Notifica que se ha validado una respuesta.
     * 
     * <p>Este método es llamado por el módulo después de validar la respuesta
     * del usuario. El contenedor se encargará de:</p>
     * <ul>
     *   <li>Actualizar el progreso en la cabecera</li>
     *   <li>Guardar el progreso automáticamente</li>
     *   <li>Preparar la transición a la siguiente pregunta</li>
     * </ul>
     * 
     * @param esCorrecta true si la respuesta es correcta, false en caso contrario
     */
    void onRespuestaValidada(boolean esCorrecta);
    
    /**
     * Notifica que se ha actualizado el progreso de la pregunta.
     * 
     * <p>Este método permite al módulo informar sobre el progreso específico
     * de la pregunta actual (por ejemplo, en preguntas de completar huecos
     * donde se pueden completar parcialmente).</p>
     * 
     * @param preguntasCompletadas número de preguntas completadas hasta el momento
     * @param totalPreguntas número total de preguntas en el curso
     */
    void onProgresoActualizado(int preguntasCompletadas, int totalPreguntas);
    
    /**
     * Solicita avanzar a la siguiente pregunta.
     * 
     * <p>Este método es llamado por el módulo cuando considera que es momento
     * de avanzar a la siguiente pregunta. El contenedor se encargará de:</p>
     * <ul>
     *   <li>Obtener la siguiente pregunta de la estrategia</li>
     *   <li>Cargar el módulo correspondiente</li>
     *   <li>Actualizar la cabecera con la nueva información</li>
     * </ul>
     */
    void onSolicitarSiguientePregunta();
    
    /**
     * Solicita terminar el curso desde el módulo.
     * 
     * <p>Este método permite a los módulos solicitar la finalización
     * del curso (por ejemplo, cuando el usuario completa todas las
     * preguntas de un módulo específico).</p>
     */
    void onSolicitarTerminarCurso();
} 