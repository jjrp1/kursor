package com.kursor.presentation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

// Importar entidades y repositorios de persistencia
import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.PreguntaSesion;
import com.kursor.persistence.entity.EstadoEstrategia;
import com.kursor.persistence.entity.EstadoSesion;
import com.kursor.persistence.repository.SesionRepository;
import com.kursor.persistence.repository.PreguntaSesionRepository;
import com.kursor.persistence.repository.EstadoEstrategiaRepository;

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
 *   <li><strong>Persistencia:</strong> Guarda y carga el progreso del usuario en base de datos</li>
 *   <li><strong>Estadísticas:</strong> Proporciona información sobre el rendimiento</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class CursoSessionManager {
    
    /** Logger para registrar eventos de la sesión */
    private static final Logger logger = LoggerFactory.getLogger(CursoSessionManager.class);
    
    /** Identificador del curso */
    private final String cursoId;
    
    /** Identificador del bloque */
    private final String bloqueId;
    
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
    
    // Nuevos campos para persistencia
    private Sesion sesionActual;
    private final SesionRepository sesionRepository;
    private final PreguntaSesionRepository preguntaSesionRepository;
    private final EstadoEstrategiaRepository estadoEstrategiaRepository;
    
    /**
     * Constructor para crear un gestor de sesión para un curso específico con persistencia.
     * 
     * @param cursoId Identificador único del curso
     * @param bloqueId Identificador del bloque
     * @param estrategia Estrategia de aprendizaje seleccionada
     * @param sesionRepository Repositorio de sesiones
     * @param preguntaSesionRepository Repositorio de preguntas de sesión
     * @param estadoEstrategiaRepository Repositorio de estados de estrategia
     */
    public CursoSessionManager(String cursoId, String bloqueId, String estrategia,
                             SesionRepository sesionRepository,
                             PreguntaSesionRepository preguntaSesionRepository,
                             EstadoEstrategiaRepository estadoEstrategiaRepository) {
        this.cursoId = cursoId;
        this.bloqueId = bloqueId;
        this.estrategiaSeleccionada = estrategia;
        this.sesionRepository = sesionRepository;
        this.preguntaSesionRepository = preguntaSesionRepository;
        this.estadoEstrategiaRepository = estadoEstrategiaRepository;
        this.respuestas = new HashMap<>();
        this.preguntasRespondidas = new ArrayList<>();
        this.bloqueActual = 0;
        this.preguntaActual = 0;
        this.inicializada = false;
        
        logger.info("CursoSessionManager creado para curso: {} bloque: {} con estrategia: {}", 
                   cursoId, bloqueId, estrategia);
    }
    
    /**
     * Constructor para crear un gestor de sesión para un curso específico (sin estrategia).
     * 
     * @param cursoId Identificador único del curso
     * @param bloqueId Identificador del bloque
     * @param sesionRepository Repositorio de sesiones
     * @param preguntaSesionRepository Repositorio de preguntas de sesión
     * @param estadoEstrategiaRepository Repositorio de estados de estrategia
     */
    public CursoSessionManager(String cursoId, String bloqueId,
                             SesionRepository sesionRepository,
                             PreguntaSesionRepository preguntaSesionRepository,
                             EstadoEstrategiaRepository estadoEstrategiaRepository) {
        this(cursoId, bloqueId, "Secuencial", sesionRepository, preguntaSesionRepository, estadoEstrategiaRepository);
    }
    
    /**
     * Constructor para crear un gestor de sesión con una sesión existente.
     * 
     * @param sesion Sesión existente
     * @param sesionRepository Repositorio de sesiones
     * @param preguntaSesionRepository Repositorio de preguntas de sesión
     * @param estadoEstrategiaRepository Repositorio de estados de estrategia
     */
    public CursoSessionManager(Sesion sesion,
                             SesionRepository sesionRepository,
                             PreguntaSesionRepository preguntaSesionRepository,
                             EstadoEstrategiaRepository estadoEstrategiaRepository) {
        this.cursoId = sesion.getCursoId();
        this.bloqueId = sesion.getBloqueId();
        this.estrategiaSeleccionada = sesion.getEstrategiaTipo();
        this.sesionActual = sesion;
        this.sesionRepository = sesionRepository;
        this.preguntaSesionRepository = preguntaSesionRepository;
        this.estadoEstrategiaRepository = estadoEstrategiaRepository;
        this.respuestas = new HashMap<>();
        this.preguntasRespondidas = new ArrayList<>();
        this.bloqueActual = 0;
        this.preguntaActual = 0;
        this.inicializada = false;
        
        logger.info("CursoSessionManager creado con sesión existente: {} para curso: {} bloque: {}", 
                   sesion.getId(), cursoId, bloqueId);
    }
    
    /**
     * Constructor legacy para compatibilidad (solo memoria).
     * 
     * @param cursoId Identificador único del curso
     * @param estrategia Estrategia de aprendizaje seleccionada
     * @deprecated Usar constructor con repositorios para persistencia real
     */
    @Deprecated
    public CursoSessionManager(String cursoId, String estrategia) {
        this.cursoId = cursoId;
        this.bloqueId = "default"; // Valor por defecto
        this.estrategiaSeleccionada = estrategia;
        this.sesionRepository = null;
        this.preguntaSesionRepository = null;
        this.estadoEstrategiaRepository = null;
        this.respuestas = new HashMap<>();
        this.preguntasRespondidas = new ArrayList<>();
        this.bloqueActual = 0;
        this.preguntaActual = 0;
        this.inicializada = false;
        
        logger.warn("CursoSessionManager creado sin persistencia (modo legacy) para curso: {}", cursoId);
    }
    
    /**
     * Constructor legacy para compatibilidad (solo memoria).
     * 
     * @param cursoId Identificador único del curso
     * @deprecated Usar constructor con repositorios para persistencia real
     */
    @Deprecated
    public CursoSessionManager(String cursoId) {
        this(cursoId, "Secuencial");
    }
    
    /**
     * Inicializa la sesión cargando el progreso guardado.
     */
    public void inicializar() {
        logger.info("Inicializando sesión para curso: {} bloque: {}", cursoId, bloqueId);
        
        try {
            if (sesionActual == null && sesionRepository != null) {
                // Crear nueva sesión si no existe
                crearNuevaSesion();
            }
            
            // Cargar progreso guardado
            cargarProgreso();
            
            this.inicializada = true;
            logger.info("Sesión inicializada - Preguntas respondidas: {}", respuestas.size());
            
        } catch (Exception e) {
            logger.error("Error al inicializar sesión", e);
            // Continuar en modo memoria si hay error de persistencia
            this.inicializada = true;
        }
    }
    
    /**
     * Crea una nueva sesión en la base de datos.
     */
    private void crearNuevaSesion() {
        try {
            sesionActual = new Sesion(cursoId, bloqueId, estrategiaSeleccionada);
            sesionActual = sesionRepository.guardar(sesionActual);
            
            // Crear estado de estrategia inicial
            EstadoEstrategia estadoEstrategia = new EstadoEstrategia(sesionActual, estrategiaSeleccionada, "{}");
            estadoEstrategiaRepository.guardar(estadoEstrategia);
            
            logger.info("Nueva sesión creada: {} para curso: {} bloque: {}", 
                       sesionActual.getId(), cursoId, bloqueId);
            
        } catch (Exception e) {
            logger.error("Error al crear nueva sesión", e);
            throw new RuntimeException("Error al crear nueva sesión", e);
        }
    }
    
    /**
     * Guarda una respuesta del usuario.
     * 
     * @param preguntaId Identificador de la pregunta
     * @param esCorrecta true si la respuesta es correcta, false en caso contrario
     */
    public void guardarRespuesta(String preguntaId, boolean esCorrecta) {
        logger.debug("Guardando respuesta - Pregunta: {} Correcta: {}", preguntaId, esCorrecta);
        
        respuestas.put(preguntaId, esCorrecta);
        
        if (!preguntasRespondidas.contains(preguntaId)) {
            preguntasRespondidas.add(preguntaId);
        }
        
        // Guardar en base de datos si está disponible
        if (sesionActual != null && preguntaSesionRepository != null) {
            try {
                guardarRespuestaEnBD(preguntaId, esCorrecta);
            } catch (Exception e) {
                logger.error("Error al guardar respuesta en BD", e);
                // Continuar en modo memoria si hay error
            }
        }
        
        // Actualizar estadísticas de la sesión
        if (sesionActual != null && sesionRepository != null) {
            try {
                actualizarEstadisticasSesion();
            } catch (Exception e) {
                logger.error("Error al actualizar estadísticas de sesión", e);
            }
        }
        
        logger.info("Respuesta guardada - Total respuestas: {}", respuestas.size());
    }
    
    /**
     * Guarda una respuesta en la base de datos.
     * 
     * @param preguntaId Identificador de la pregunta
     * @param esCorrecta true si la respuesta es correcta, false en caso contrario
     */
    private void guardarRespuestaEnBD(String preguntaId, boolean esCorrecta) {
        // Verificar si ya existe una entrada para esta pregunta
        Optional<PreguntaSesion> preguntaExistente = preguntaSesionRepository
            .buscarPorSesionYPregunta(sesionActual.getId(), preguntaId);
        
        PreguntaSesion preguntaSesion;
        if (preguntaExistente.isPresent()) {
            // Actualizar respuesta existente
            preguntaSesion = preguntaExistente.get();
            preguntaSesion.registrarRespuesta("", esCorrecta, 0); // TODO: Agregar tiempo real
        } else {
            // Crear nueva entrada
            preguntaSesion = new PreguntaSesion(sesionActual, preguntaId);
            preguntaSesion.registrarRespuesta("", esCorrecta, 0); // TODO: Agregar tiempo real
        }
        
        preguntaSesionRepository.guardar(preguntaSesion);
        logger.debug("Respuesta guardada en BD para pregunta: {}", preguntaId);
    }
    
    /**
     * Actualiza las estadísticas de la sesión en la base de datos.
     */
    private void actualizarEstadisticasSesion() {
        if (sesionActual == null) return;
        
        // Calcular estadísticas
        int totalPreguntas = respuestas.size();
        int correctas = (int) respuestas.values().stream().filter(correcta -> correcta).count();
        double porcentaje = totalPreguntas > 0 ? (double) correctas / totalPreguntas * 100 : 0;
        
        // Actualizar sesión
        sesionActual.setPreguntasRespondidas(totalPreguntas);
        sesionActual.setAciertos(correctas);
        sesionActual.setTasaAciertos(porcentaje);
        sesionActual.actualizarUltimaRevision();
        
        // Guardar cambios
        sesionRepository.guardar(sesionActual);
        logger.debug("Estadísticas de sesión actualizadas - Total: {} Correctas: {} Porcentaje: {}%", 
                    totalPreguntas, correctas, porcentaje);
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
     * Carga el progreso guardado del usuario desde la base de datos.
     */
    private void cargarProgreso() {
        logger.debug("Cargando progreso para curso: {} bloque: {}", cursoId, bloqueId);
        
        if (sesionActual == null || preguntaSesionRepository == null) {
            logger.info("No hay sesión o repositorio disponible, cargando progreso vacío");
            return;
        }
        
        try {
            // Cargar respuestas guardadas
            List<PreguntaSesion> preguntasSesion = preguntaSesionRepository
                .buscarPorSesion(sesionActual.getId());
            
            respuestas.clear();
            preguntasRespondidas.clear();
            
            for (PreguntaSesion preguntaSesion : preguntasSesion) {
                String preguntaId = preguntaSesion.getPreguntaId();
                boolean esCorrecta = preguntaSesion.esCorrecta();
                
                respuestas.put(preguntaId, esCorrecta);
                preguntasRespondidas.add(preguntaId);
            }
            
            // Cargar posición actual desde la sesión
            if (sesionActual.getPreguntaActualId() != null) {
                // TODO: Implementar lógica para determinar bloque y pregunta actual
                // Por ahora usamos valores por defecto
                bloqueActual = 0;
                preguntaActual = preguntasRespondidas.size();
            }
            
            logger.info("Progreso cargado desde BD - Respuestas: {} Posición: Bloque {} Pregunta {}", 
                       respuestas.size(), bloqueActual, preguntaActual);
            
        } catch (Exception e) {
            logger.error("Error al cargar progreso desde BD", e);
            // Continuar con progreso vacío si hay error
        }
    }
    
    /**
     * Guarda el progreso actual del usuario en la base de datos.
     */
    private void guardarProgreso() {
        logger.debug("Guardando progreso para curso: {} bloque: {}", cursoId, bloqueId);
        
        if (sesionActual == null || sesionRepository == null) {
            logger.debug("No hay sesión o repositorio disponible, guardando solo en memoria");
            return;
        }
        
        try {
            // Actualizar estadísticas de la sesión
            actualizarEstadisticasSesion();
            
            // TODO: Guardar posición actual en la sesión
            // sesionActual.setPreguntaActualId(preguntaActualId);
            
            // Guardar cambios en la sesión
            sesionRepository.guardar(sesionActual);
            
            logger.info("Progreso guardado en BD - Respuestas: {} Posición: Bloque {} Pregunta {}", 
                       respuestas.size(), bloqueActual, preguntaActual);
            
        } catch (Exception e) {
            logger.error("Error al guardar progreso en BD", e);
            // Continuar en modo memoria si hay error
        }
    }
    
    /**
     * Obtiene el identificador del bloque.
     * 
     * @return Identificador del bloque
     */
    public String getBloqueId() {
        return bloqueId;
    }
    
    /**
     * Obtiene la sesión actual.
     * 
     * @return Sesión actual o null si no hay persistencia
     */
    public Sesion getSesionActual() {
        return sesionActual;
    }
    
    /**
     * Verifica si la persistencia está habilitada.
     * 
     * @return true si la persistencia está habilitada, false en caso contrario
     */
    public boolean isPersistenciaHabilitada() {
        return sesionRepository != null && preguntaSesionRepository != null && estadoEstrategiaRepository != null;
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
