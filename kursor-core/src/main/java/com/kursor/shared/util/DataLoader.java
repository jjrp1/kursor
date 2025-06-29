package com.kursor.util;

import com.kursor.persistence.config.PersistenceConfig;
import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadoSesion;
import com.kursor.persistence.entity.PreguntaSesion;
import com.kursor.persistence.entity.EstadoEstrategia;
import com.kursor.persistence.entity.EstadisticasUsuario;
import com.kursor.persistence.repository.SesionRepository;
import com.kursor.persistence.repository.PreguntaSesionRepository;
import com.kursor.persistence.repository.EstadoEstrategiaRepository;
import com.kursor.persistence.repository.EstadisticasUsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

/**
 * Utilidad para cargar datos de prueba en la base de datos.
 * 
 * <p>Esta clase proporciona métodos para inicializar la base de datos
 * con datos de ejemplo que permiten probar la funcionalidad de la aplicación.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private static final Random random = new Random(42); // Semilla fija para reproducibilidad
    
    /**
     * Carga datos de prueba en la base de datos.
     * 
     * <p>Este método crea sesiones de ejemplo para diferentes cursos
     * con estadísticas realistas que permiten probar la funcionalidad
     * de la tabla de sesiones.</p>
     * 
     * @return true si se cargaron los datos correctamente, false en caso contrario
     */
    public static boolean cargarDatosDePrueba() {
        logger.info("Iniciando carga de datos de prueba...");
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            if (!PersistenceConfig.isInitialized()) {
                logger.error("PersistenceConfig no está inicializado");
                return false;
            }
            em = PersistenceConfig.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            
            // Crear repositorios
            SesionRepository sesionRepository = new SesionRepository(em);
            PreguntaSesionRepository preguntaSesionRepository = new PreguntaSesionRepository(em);
            EstadoEstrategiaRepository estadoEstrategiaRepository = new EstadoEstrategiaRepository(em);
            EstadisticasUsuarioRepository estadisticasRepository = new EstadisticasUsuarioRepository(em);
            
            // Crear sesiones de prueba
            List<Sesion> sesionesPrueba = crearSesionesDePrueba();
            
            // Guardar cada sesión y crear datos relacionados
            for (Sesion sesion : sesionesPrueba) {
                // Guardar sesión
                sesionRepository.guardar(sesion);
                logger.debug("Sesión de prueba guardada: {} - {}", sesion.getCursoId(), sesion.getBloqueId());
                
                // Crear y guardar estado de estrategia
                EstadoEstrategia estadoEstrategia = crearEstadoEstrategia(sesion);
                estadoEstrategiaRepository.guardar(estadoEstrategia);
                logger.debug("Estado de estrategia creado para sesión: {}", sesion.getId());
                
                // Crear y guardar preguntas de la sesión
                List<PreguntaSesion> preguntasSesion = crearPreguntasSesion(sesion);
                for (PreguntaSesion preguntaSesion : preguntasSesion) {
                    preguntaSesionRepository.guardar(preguntaSesion);
                }
                logger.debug("{} preguntas creadas para sesión: {}", preguntasSesion.size(), sesion.getId());
            }
            
            // Crear estadísticas de usuario por curso
            crearEstadisticasUsuario(estadisticasRepository);
            
            tx.commit();
            logger.info("Datos de prueba cargados exitosamente: {} sesiones", sesionesPrueba.size());
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error al cargar datos de prueba", e);
            return false;
        } finally {
            if (em != null) em.close();
        }
    }
    
    /**
     * Crea una lista de sesiones de prueba con datos realistas.
     * 
     * @return Lista de sesiones de prueba
     */
    private static List<Sesion> crearSesionesDePrueba() {
        LocalDateTime ahora = LocalDateTime.now();
        
        return Arrays.asList(
            // Curso de Ciencias
            crearSesion("curso_ciencias", "Introducción a la Biología", "Aleatoria", 
                       ahora.minusDays(5), 1800, 15, 12, 80.0, 5, 75.0),
            
            crearSesion("curso_ciencias", "Química Básica", "Repetición Espaciada", 
                       ahora.minusDays(3), 2400, 20, 16, 80.0, 7, 60.0),
            
            crearSesion("curso_ciencias", "Física Fundamental", "Secuencial", 
                       ahora.minusDays(1), 1200, 10, 8, 80.0, 3, 40.0),
            
            // Curso de Historia
            crearSesion("curso_historia", "Historia Antigua", "Aleatoria", 
                       ahora.minusDays(7), 1500, 12, 10, 83.3, 4, 66.7),
            
            crearSesion("curso_historia", "Edad Media", "Repetir Incorrectas", 
                       ahora.minusDays(4), 2000, 18, 14, 77.8, 6, 55.6),
            
            crearSesion("curso_historia", "Historia Moderna", "Secuencial", 
                       ahora.minusDays(2), 1800, 16, 13, 81.3, 5, 62.5),
            
            // Curso de Inglés
            crearSesion("curso_ingles", "Vocabulario Básico", "Aleatoria", 
                       ahora.minusDays(6), 900, 8, 7, 87.5, 3, 50.0),
            
            crearSesion("curso_ingles", "Gramática Intermedia", "Repetición Espaciada", 
                       ahora.minusDays(3), 2100, 22, 18, 81.8, 8, 72.7),
            
            crearSesion("curso_ingles", "Comprensión Lectora", "Repetir Incorrectas", 
                       ahora.minusDays(1), 1500, 14, 11, 78.6, 4, 57.1),
            
            // Curso de Matemáticas
            crearSesion("curso_matematicas", "Álgebra Básica", "Secuencial", 
                       ahora.minusDays(8), 3000, 25, 20, 80.0, 9, 80.0),
            
            crearSesion("curso_matematicas", "Geometría", "Aleatoria", 
                       ahora.minusDays(5), 2400, 20, 17, 85.0, 6, 70.0),
            
            crearSesion("curso_matematicas", "Cálculo Diferencial", "Repetición Espaciada", 
                       ahora.minusDays(2), 3600, 30, 24, 80.0, 10, 80.0)
        );
    }
    
    /**
     * Crea una sesión de prueba con los parámetros especificados.
     * 
     * @param cursoId ID del curso
     * @param bloqueId ID del bloque
     * @param estrategiaTipo Tipo de estrategia utilizada
     * @param fechaInicio Fecha de inicio de la sesión
     * @param tiempoTotal Tiempo total en segundos
     * @param preguntasRespondidas Número de preguntas respondidas
     * @param aciertos Número de aciertos
     * @param tasaAciertos Tasa de aciertos como porcentaje
     * @param mejorRachaAciertos Mejor racha de aciertos consecutivos
     * @param porcentajeCompletitud Porcentaje de completitud del curso
     * @return Sesión creada
     */
    private static Sesion crearSesion(String cursoId, String bloqueId, String estrategiaTipo,
                                     LocalDateTime fechaInicio, int tiempoTotal, int preguntasRespondidas,
                                     int aciertos, double tasaAciertos, int mejorRachaAciertos,
                                     double porcentajeCompletitud) {
        
        Sesion sesion = new Sesion();
        sesion.setCursoId(cursoId);
        sesion.setBloqueId(bloqueId);
        sesion.setEstrategiaTipo(estrategiaTipo);
        sesion.setFechaInicio(fechaInicio);
        sesion.setFechaUltimaRevision(fechaInicio.plusMinutes(tiempoTotal / 60));
        sesion.setTiempoTotal(tiempoTotal);
        sesion.setPreguntasRespondidas(preguntasRespondidas);
        sesion.setAciertos(aciertos);
        sesion.setTasaAciertos(tasaAciertos);
        sesion.setMejorRachaAciertos(mejorRachaAciertos);
        sesion.setPorcentajeCompletitud(porcentajeCompletitud);
        sesion.setEstado(EstadoSesion.COMPLETADA);
        sesion.setCreatedAt(fechaInicio);
        sesion.setUpdatedAt(LocalDateTime.now());
        
        return sesion;
    }
    
    /**
     * Crea un estado de estrategia para una sesión.
     * 
     * @param sesion Sesión asociada
     * @return Estado de estrategia creado
     */
    private static EstadoEstrategia crearEstadoEstrategia(Sesion sesion) {
        String datosEstado = generarDatosEstadoEstrategia(sesion.getEstrategiaTipo(), sesion.getPreguntasRespondidas());
        double progreso = sesion.getPorcentajeCompletitud() / 100.0;
        
        EstadoEstrategia estadoEstrategia = new EstadoEstrategia();
        estadoEstrategia.setSesion(sesion);
        estadoEstrategia.setTipoEstrategia(sesion.getEstrategiaTipo());
        estadoEstrategia.setDatosEstado(datosEstado);
        estadoEstrategia.setProgreso(progreso);
        estadoEstrategia.setFechaCreacion(sesion.getFechaInicio());
        estadoEstrategia.setFechaUltimaModificacion(sesion.getFechaUltimaRevision());
        
        return estadoEstrategia;
    }
    
    /**
     * Genera datos de estado para una estrategia específica.
     * 
     * @param tipoEstrategia Tipo de estrategia
     * @param preguntasRespondidas Número de preguntas respondidas
     * @return Datos de estado en formato JSON
     */
    private static String generarDatosEstadoEstrategia(String tipoEstrategia, int preguntasRespondidas) {
        switch (tipoEstrategia) {
            case "Aleatoria":
                return String.format("{\"preguntasPendientes\": %d, \"preguntasCompletadas\": %d, \"ultimaPregunta\": \"pregunta_%d\"}", 
                                   random.nextInt(10) + 5, preguntasRespondidas, preguntasRespondidas);
            case "Secuencial":
                return String.format("{\"indiceActual\": %d, \"totalPreguntas\": %d, \"completado\": %s}", 
                                   preguntasRespondidas, preguntasRespondidas + random.nextInt(10) + 5, 
                                   preguntasRespondidas > 10 ? "true" : "false");
            case "Repetición Espaciada":
                return String.format("{\"nivel\": %d, \"repeticiones\": %d, \"siguienteRepaso\": \"%s\"}", 
                                   random.nextInt(3) + 1, preguntasRespondidas, 
                                   LocalDateTime.now().plusDays(random.nextInt(7) + 1).toString());
            case "Repetir Incorrectas":
                return String.format("{\"preguntasIncorrectas\": %d, \"intentos\": %d, \"modo\": \"repeticion\"}", 
                                   random.nextInt(5) + 1, random.nextInt(3) + 1);
            default:
                return "{\"estado\": \"completado\"}";
        }
    }
    
    /**
     * Crea preguntas de sesión para una sesión específica.
     * 
     * @param sesion Sesión asociada
     * @return Lista de preguntas de sesión
     */
    private static List<PreguntaSesion> crearPreguntasSesion(Sesion sesion) {
        List<PreguntaSesion> preguntas = new java.util.ArrayList<>();
        int totalPreguntas = sesion.getPreguntasRespondidas();
        int aciertos = sesion.getAciertos();
        
        for (int i = 1; i <= totalPreguntas; i++) {
            String preguntaId = String.format("pregunta_%s_%s_%d", sesion.getCursoId(), sesion.getBloqueId(), i);
            String resultado = i <= aciertos ? "acierto" : "fallo";
            int tiempoDedicado = random.nextInt(120) + 30; // Entre 30 y 150 segundos
            String respuesta = generarRespuesta(resultado, sesion.getCursoId());
            
            PreguntaSesion preguntaSesion = new PreguntaSesion();
            preguntaSesion.setSesion(sesion);
            preguntaSesion.setPreguntaId(preguntaId);
            preguntaSesion.setResultado(resultado);
            preguntaSesion.setTiempoDedicado(tiempoDedicado);
            preguntaSesion.setRespuesta(respuesta);
            preguntaSesion.setCreatedAt(sesion.getFechaInicio().plusMinutes(i * 2));
            preguntaSesion.setUpdatedAt(sesion.getFechaUltimaRevision());
            
            preguntas.add(preguntaSesion);
        }
        
        return preguntas;
    }
    
    /**
     * Genera una respuesta de ejemplo basada en el resultado y curso.
     * 
     * @param resultado Resultado de la pregunta
     * @param cursoId ID del curso
     * @return Respuesta generada
     */
    private static String generarRespuesta(String resultado, String cursoId) {
        String[] respuestasCiencias = {"Célula", "ADN", "Mitocondria", "Fotosíntesis", "Evolución"};
        String[] respuestasHistoria = {"Roma", "Egipto", "Grecia", "Edad Media", "Renacimiento"};
        String[] respuestasIngles = {"Hello", "World", "Learning", "English", "Vocabulary"};
        String[] respuestasMatematicas = {"42", "π", "Ecuación", "Variable", "Función"};
        
        String[] respuestas;
        switch (cursoId) {
            case "curso_ciencias": respuestas = respuestasCiencias; break;
            case "curso_historia": respuestas = respuestasHistoria; break;
            case "curso_ingles": respuestas = respuestasIngles; break;
            case "curso_matematicas": respuestas = respuestasMatematicas; break;
            default: respuestas = respuestasCiencias;
        }
        
        String respuesta = respuestas[random.nextInt(respuestas.length)];
        return resultado.equals("acierto") ? respuesta : respuesta + "_incorrecta";
    }
    
    /**
     * Crea estadísticas de usuario para cada curso.
     * 
     * @param estadisticasRepository Repositorio de estadísticas
     */
    private static void crearEstadisticasUsuario(EstadisticasUsuarioRepository estadisticasRepository) {
        String[] cursos = {"curso_ciencias", "curso_historia", "curso_ingles", "curso_matematicas"};
        LocalDateTime ahora = LocalDateTime.now();
        
        for (String cursoId : cursos) {
            EstadisticasUsuario estadisticas = new EstadisticasUsuario();
            estadisticas.setUsuarioId("usuario_default");
            estadisticas.setCursoId(cursoId);
            estadisticas.setTiempoTotal(random.nextInt(7200) + 1800); // Entre 30 min y 2 horas
            estadisticas.setSesionesCompletadas(random.nextInt(5) + 2); // Entre 2 y 6 sesiones
            estadisticas.setMejorRachaDias(random.nextInt(14) + 3); // Entre 3 y 16 días
            estadisticas.setRachaActualDias(random.nextInt(7) + 1); // Entre 1 y 7 días
            estadisticas.setFechaPrimeraSesion(ahora.minusDays(random.nextInt(30) + 10));
            estadisticas.setFechaUltimaSesion(ahora.minusDays(random.nextInt(7) + 1));
            estadisticas.setCreatedAt(estadisticas.getFechaPrimeraSesion());
            estadisticas.setUpdatedAt(ahora);
            
            estadisticasRepository.guardar(estadisticas);
            logger.debug("Estadísticas creadas para curso: {}", cursoId);
        }
    }
    
    /**
     * Limpia todos los datos de la base de datos.
     * 
     * <p><strong>¡ADVERTENCIA!</strong> Este método elimina TODOS los datos
     * de la base de datos. Úsalo solo en desarrollo.</p>
     * 
     * @return true si se limpiaron los datos correctamente, false en caso contrario
     */
    public static boolean limpiarDatos() {
        logger.warn("Limpiando todos los datos de la base de datos...");
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            if (!PersistenceConfig.isInitialized()) {
                logger.error("PersistenceConfig no está inicializado");
                return false;
            }
            em = PersistenceConfig.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            
            // Limpiar en orden correcto (respetando foreign keys)
            PreguntaSesionRepository preguntaSesionRepository = new PreguntaSesionRepository(em);
            EstadoEstrategiaRepository estadoEstrategiaRepository = new EstadoEstrategiaRepository(em);
            SesionRepository sesionRepository = new SesionRepository(em);
            EstadisticasUsuarioRepository estadisticasRepository = new EstadisticasUsuarioRepository(em);
            
            int eliminadas = 0;
            
            // Eliminar todas las preguntas de sesión
            Query queryPreguntas = em.createQuery("DELETE FROM PreguntaSesion");
            eliminadas += queryPreguntas.executeUpdate();
            
            // Eliminar todos los estados de estrategia
            Query queryEstados = em.createQuery("DELETE FROM EstadoEstrategia");
            eliminadas += queryEstados.executeUpdate();
            
            // Eliminar todas las sesiones
            eliminadas += sesionRepository.eliminarTodasLasSesiones();
            
            // Eliminar todas las estadísticas
            Query queryEstadisticas = em.createQuery("DELETE FROM EstadisticasUsuario");
            eliminadas += queryEstadisticas.executeUpdate();
            
            tx.commit();
            logger.info("Datos limpiados exitosamente: {} registros eliminados", eliminadas);
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error al limpiar datos", e);
            return false;
        } finally {
            if (em != null) em.close();
        }
    }
    
    /**
     * Muestra estadísticas de la base de datos.
     * 
     * @return true si se mostraron las estadísticas correctamente, false en caso contrario
     */
    public static boolean mostrarEstadisticas() {
        logger.info("Mostrando estadísticas de la base de datos...");
        
        try {
            if (!PersistenceConfig.isInitialized()) {
                logger.error("PersistenceConfig no está inicializado");
                return false;
            }
            
            EntityManager em = PersistenceConfig.createEntityManager();
            SesionRepository sesionRepository = new SesionRepository(em);
            
            long totalSesiones = sesionRepository.contarSesiones();
            long sesionesCompletadas = sesionRepository.contarSesionesCompletadas();
            
            // Contar preguntas de sesión
            TypedQuery<Long> queryPreguntas = em.createQuery("SELECT COUNT(ps) FROM PreguntaSesion ps", Long.class);
            long totalPreguntas = queryPreguntas.getSingleResult();
            
            // Contar estados de estrategia
            TypedQuery<Long> queryEstados = em.createQuery("SELECT COUNT(ee) FROM EstadoEstrategia ee", Long.class);
            long totalEstadosEstrategia = queryEstados.getSingleResult();
            
            // Contar estadísticas de usuario
            TypedQuery<Long> queryEstadisticas = em.createQuery("SELECT COUNT(eu) FROM EstadisticasUsuario eu", Long.class);
            long totalEstadisticas = queryEstadisticas.getSingleResult();
            
            logger.info("=== ESTADÍSTICAS DE LA BASE DE DATOS ===");
            logger.info("Total de sesiones: {}", totalSesiones);
            logger.info("Sesiones completadas: {}", sesionesCompletadas);
            logger.info("Sesiones en curso: {}", totalSesiones - sesionesCompletadas);
            logger.info("Total de preguntas de sesión: {}", totalPreguntas);
            logger.info("Total de estados de estrategia: {}", totalEstadosEstrategia);
            logger.info("Total de estadísticas de usuario: {}", totalEstadisticas);
            logger.info("========================================");
            
            em.close();
            return true;
            
        } catch (Exception e) {
            logger.error("Error al mostrar estadísticas", e);
            return false;
        }
    }
} 
