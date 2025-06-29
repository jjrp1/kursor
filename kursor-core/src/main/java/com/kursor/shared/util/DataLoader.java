package com.kursor.util;

import com.kursor.persistence.config.PersistenceConfig;
import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadoSesion;
import com.kursor.persistence.repository.SesionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
        
        try {
            // Verificar que la configuración de persistencia esté inicializada
            if (!PersistenceConfig.isInitialized()) {
                logger.error("PersistenceConfig no está inicializado");
                return false;
            }
            
            // Crear repositorio
            SesionRepository sesionRepository = new SesionRepository(PersistenceConfig.createEntityManager());
            
            // Crear sesiones de prueba
            List<Sesion> sesionesPrueba = crearSesionesDePrueba();
            
            // Guardar cada sesión
            for (Sesion sesion : sesionesPrueba) {
                sesionRepository.guardar(sesion);
                logger.debug("Sesión de prueba guardada: {} - {}", sesion.getCursoId(), sesion.getBloqueId());
            }
            
            logger.info("Datos de prueba cargados exitosamente: {} sesiones", sesionesPrueba.size());
            return true;
            
        } catch (Exception e) {
            logger.error("Error al cargar datos de prueba", e);
            return false;
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
     * Limpia todos los datos de la base de datos.
     * 
     * <p><strong>¡ADVERTENCIA!</strong> Este método elimina TODOS los datos
     * de la base de datos. Úsalo solo en desarrollo.</p>
     * 
     * @return true si se limpiaron los datos correctamente, false en caso contrario
     */
    public static boolean limpiarDatos() {
        logger.warn("Limpiando todos los datos de la base de datos...");
        
        try {
            if (!PersistenceConfig.isInitialized()) {
                logger.error("PersistenceConfig no está inicializado");
                return false;
            }
            
            SesionRepository sesionRepository = new SesionRepository(PersistenceConfig.createEntityManager());
            int eliminadas = sesionRepository.eliminarTodasLasSesiones();
            
            logger.info("Datos limpiados exitosamente: {} sesiones eliminadas", eliminadas);
            return true;
            
        } catch (Exception e) {
            logger.error("Error al limpiar datos", e);
            return false;
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
            
            SesionRepository sesionRepository = new SesionRepository(PersistenceConfig.createEntityManager());
            
            long totalSesiones = sesionRepository.contarSesiones();
            long sesionesCompletadas = sesionRepository.contarSesionesCompletadas();
            
            logger.info("=== ESTADÍSTICAS DE LA BASE DE DATOS ===");
            logger.info("Total de sesiones: {}", totalSesiones);
            logger.info("Sesiones completadas: {}", sesionesCompletadas);
            logger.info("Sesiones en curso: {}", totalSesiones - sesionesCompletadas);
            logger.info("========================================");
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error al mostrar estadísticas", e);
            return false;
        }
    }
} 
