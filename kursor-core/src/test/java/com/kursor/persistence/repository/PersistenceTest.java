package com.kursor.persistence.repository;

import com.kursor.persistence.config.PersistenceConfig;
import com.kursor.persistence.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para la persistencia JPA.
 * 
 * <p>Esta clase prueba la funcionalidad básica de los repositorios JPA
 * y verifica que las operaciones CRUD funcionen correctamente.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersistenceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(PersistenceTest.class);
    
    private static EntityManager entityManager;
    private static SesionRepository sesionRepository;
    private static EstadoEstrategiaRepository estadoEstrategiaRepository;
    private static PreguntaSesionRepository preguntaSesionRepository;
    private static EstadisticasUsuarioRepository estadisticasUsuarioRepository;
    
    private static Long sesionId;
    private static Long estadoEstrategiaId;
    private static Long preguntaSesionId;
    private static Long estadisticasUsuarioId;
    
    @BeforeAll
    static void setUp() {
        logger.info("Inicializando pruebas de persistencia...");
        
        try {
            // Inicializar PersistenceConfig
            PersistenceConfig.initialize();
            
            // Crear EntityManager
            entityManager = PersistenceConfig.createEntityManager();
            
            // Crear repositorios
            sesionRepository = new SesionRepository(entityManager);
            estadoEstrategiaRepository = new EstadoEstrategiaRepository(entityManager);
            preguntaSesionRepository = new PreguntaSesionRepository(entityManager);
            estadisticasUsuarioRepository = new EstadisticasUsuarioRepository(entityManager);
            
            logger.info("Pruebas de persistencia inicializadas correctamente");
            
        } catch (Exception e) {
            logger.error("Error al inicializar pruebas de persistencia", e);
            fail("Error al inicializar pruebas de persistencia: " + e.getMessage());
        }
    }
    
    @AfterAll
    static void tearDown() {
        logger.info("Finalizando pruebas de persistencia...");
        
        try {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            PersistenceConfig.shutdown();
            logger.info("Pruebas de persistencia finalizadas correctamente");
        } catch (Exception e) {
            logger.error("Error al finalizar pruebas de persistencia", e);
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Crear y guardar una sesión")
    void testCrearSesion() {
        logger.info("Probando creación de sesión...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Crear sesión
            Sesion sesion = new Sesion("curso_test", "bloque_test", "SECUENCIAL");
            sesion.setEstado(EstadoSesion.EN_CURSO);
            
            // Guardar sesión
            Sesion sesionGuardada = sesionRepository.guardar(sesion);
            
            // Verificar que se guardó correctamente
            assertNotNull(sesionGuardada.getId());
            assertEquals("curso_test", sesionGuardada.getCursoId());
            assertEquals("bloque_test", sesionGuardada.getBloqueId());
            assertEquals(EstadoSesion.EN_CURSO, sesionGuardada.getEstado());
            
            sesionId = sesionGuardada.getId();
            logger.info("Sesión creada exitosamente - ID: {}", sesionId);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al crear sesión", e);
            fail("Error al crear sesión: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Buscar sesión por ID")
    void testBuscarSesion() {
        logger.info("Probando búsqueda de sesión...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Buscar sesión
            Optional<Sesion> sesionEncontrada = sesionRepository.buscarPorId(sesionId);
            
            // Verificar que se encontró
            assertTrue(sesionEncontrada.isPresent());
            assertEquals(sesionId, sesionEncontrada.get().getId());
            assertEquals("curso_test", sesionEncontrada.get().getCursoId());
            
            logger.info("Sesión encontrada exitosamente - ID: {}", sesionId);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al buscar sesión", e);
            fail("Error al buscar sesión: " + e.getMessage());
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("Crear y guardar estado de estrategia")
    void testCrearEstadoEstrategia() {
        logger.info("Probando creación de estado de estrategia...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Obtener sesión
            Sesion sesion = sesionRepository.buscarPorId(sesionId).orElseThrow();
            
            // Crear estado de estrategia
            EstadoEstrategia estadoEstrategia = new EstadoEstrategia();
            estadoEstrategia.setSesion(sesion);
            estadoEstrategia.setTipoEstrategia("SECUENCIAL");
            estadoEstrategia.setDatosEstado("{\"indiceActual\": 0, \"preguntas\": [1, 2, 3]}");
            estadoEstrategia.setProgreso(0.0);
            
            // Guardar estado de estrategia
            EstadoEstrategia estadoGuardado = estadoEstrategiaRepository.guardar(estadoEstrategia);
            
            // Verificar que se guardó correctamente
            assertNotNull(estadoGuardado.getId());
            assertEquals(sesionId, estadoGuardado.getSesion().getId());
            assertEquals("SECUENCIAL", estadoGuardado.getTipoEstrategia());
            assertEquals(0.0, estadoGuardado.getProgreso());
            
            estadoEstrategiaId = estadoGuardado.getId();
            logger.info("Estado de estrategia creado exitosamente - ID: {}", estadoEstrategiaId);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al crear estado de estrategia", e);
            fail("Error al crear estado de estrategia: " + e.getMessage());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Crear y guardar pregunta de sesión")
    void testCrearPreguntaSesion() {
        logger.info("Probando creación de pregunta de sesión...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Obtener sesión
            Sesion sesion = sesionRepository.buscarPorId(sesionId).orElseThrow();
            
            // Crear pregunta de sesión
            PreguntaSesion preguntaSesion = new PreguntaSesion();
            preguntaSesion.setSesion(sesion);
            preguntaSesion.setPreguntaId("pregunta_1");
            preguntaSesion.setResultado("acierto");
            preguntaSesion.setTiempoDedicado(30);
            preguntaSesion.setRespuesta("Respuesta correcta");
            
            // Guardar pregunta de sesión
            PreguntaSesion preguntaGuardada = preguntaSesionRepository.guardar(preguntaSesion);
            
            // Verificar que se guardó correctamente
            assertNotNull(preguntaGuardada.getId());
            assertEquals(sesionId, preguntaGuardada.getSesion().getId());
            assertEquals("pregunta_1", preguntaGuardada.getPreguntaId());
            assertTrue(preguntaGuardada.esCorrecta());
            assertEquals(30, preguntaGuardada.getTiempoDedicado());
            
            preguntaSesionId = preguntaGuardada.getId();
            logger.info("Pregunta de sesión creada exitosamente - ID: {}", preguntaSesionId);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al crear pregunta de sesión", e);
            fail("Error al crear pregunta de sesión: " + e.getMessage());
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Crear y guardar estadísticas de usuario")
    void testCrearEstadisticasUsuario() {
        logger.info("Probando creación de estadísticas de usuario...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Crear estadísticas de usuario
            EstadisticasUsuario estadisticasUsuario = new EstadisticasUsuario();
            estadisticasUsuario.setUsuarioId("usuario_test");
            estadisticasUsuario.setCursoId("curso_test");
            estadisticasUsuario.setTiempoTotal(1800); // 30 minutos
            estadisticasUsuario.setSesionesCompletadas(1);
            estadisticasUsuario.setMejorRachaDias(5);
            estadisticasUsuario.setRachaActualDias(3);
            estadisticasUsuario.setFechaPrimeraSesion(LocalDateTime.now().minusDays(10));
            estadisticasUsuario.setFechaUltimaSesion(LocalDateTime.now());
            
            // Guardar estadísticas de usuario
            EstadisticasUsuario estadisticasGuardadas = estadisticasUsuarioRepository.guardar(estadisticasUsuario);
            
            // Verificar que se guardó correctamente
            assertNotNull(estadisticasGuardadas.getId());
            assertEquals("usuario_test", estadisticasGuardadas.getUsuarioId());
            assertEquals("curso_test", estadisticasGuardadas.getCursoId());
            assertEquals(1800, estadisticasGuardadas.getTiempoTotal());
            assertEquals(1, estadisticasGuardadas.getSesionesCompletadas());
            assertEquals(5, estadisticasGuardadas.getMejorRachaDias());
            
            estadisticasUsuarioId = estadisticasGuardadas.getId();
            logger.info("Estadísticas de usuario creadas exitosamente - ID: {}", estadisticasUsuarioId);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al crear estadísticas de usuario", e);
            fail("Error al crear estadísticas de usuario: " + e.getMessage());
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Buscar todas las sesiones")
    void testBuscarTodasLasSesiones() {
        logger.info("Probando búsqueda de todas las sesiones...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Buscar todas las sesiones
            List<Sesion> sesiones = sesionRepository.buscarTodasLasSesiones();
            
            // Verificar que se encontraron
            assertFalse(sesiones.isEmpty());
            assertEquals(1, sesiones.size());
            assertEquals(sesionId, sesiones.get(0).getId());
            
            logger.info("Todas las sesiones encontradas exitosamente - Cantidad: {}", sesiones.size());
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al buscar todas las sesiones", e);
            fail("Error al buscar todas las sesiones: " + e.getMessage());
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Buscar estado de estrategia por sesión")
    void testBuscarEstadoEstrategiaPorSesion() {
        logger.info("Probando búsqueda de estado de estrategia por sesión...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Buscar estado de estrategia
            Optional<EstadoEstrategia> estadoEncontrado = estadoEstrategiaRepository.buscarPorSesion(sesionId);
            
            // Verificar que se encontró
            assertTrue(estadoEncontrado.isPresent());
            assertEquals(estadoEstrategiaId, estadoEncontrado.get().getId());
            assertEquals("SECUENCIAL", estadoEncontrado.get().getTipoEstrategia());
            
            logger.info("Estado de estrategia encontrado exitosamente - ID: {}", estadoEstrategiaId);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al buscar estado de estrategia por sesión", e);
            fail("Error al buscar estado de estrategia por sesión: " + e.getMessage());
        }
    }
    
    @Test
    @Order(8)
    @DisplayName("Buscar preguntas por sesión")
    void testBuscarPreguntasPorSesion() {
        logger.info("Probando búsqueda de preguntas por sesión...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Buscar preguntas de la sesión
            List<PreguntaSesion> preguntas = preguntaSesionRepository.buscarPorSesion(sesionId);
            
            // Verificar que se encontraron
            assertFalse(preguntas.isEmpty());
            assertEquals(1, preguntas.size());
            assertEquals(preguntaSesionId, preguntas.get(0).getId());
            assertEquals("pregunta_1", preguntas.get(0).getPreguntaId());
            
            logger.info("Preguntas de la sesión encontradas exitosamente - Cantidad: {}", preguntas.size());
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al buscar preguntas por sesión", e);
            fail("Error al buscar preguntas por sesión: " + e.getMessage());
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("Buscar estadísticas por usuario y curso")
    void testBuscarEstadisticasPorUsuarioYCurso() {
        logger.info("Probando búsqueda de estadísticas por usuario y curso...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Buscar estadísticas
            Optional<EstadisticasUsuario> estadisticasEncontradas = 
                estadisticasUsuarioRepository.buscarPorUsuarioYCurso("usuario_test", "curso_test");
            
            // Verificar que se encontraron
            assertTrue(estadisticasEncontradas.isPresent());
            assertEquals(estadisticasUsuarioId, estadisticasEncontradas.get().getId());
            assertEquals("usuario_test", estadisticasEncontradas.get().getUsuarioId());
            assertEquals("curso_test", estadisticasEncontradas.get().getCursoId());
            
            logger.info("Estadísticas encontradas exitosamente - ID: {}", estadisticasUsuarioId);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al buscar estadísticas por usuario y curso", e);
            fail("Error al buscar estadísticas por usuario y curso: " + e.getMessage());
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("Actualizar progreso de estado de estrategia")
    void testActualizarProgresoEstadoEstrategia() {
        logger.info("Probando actualización de progreso de estado de estrategia...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Actualizar progreso
            boolean actualizado = estadoEstrategiaRepository.actualizarProgreso(estadoEstrategiaId, 50.0);
            
            // Verificar que se actualizó
            assertTrue(actualizado);
            
            // Verificar el cambio
            Optional<EstadoEstrategia> estadoActualizado = estadoEstrategiaRepository.buscarPorId(estadoEstrategiaId);
            assertTrue(estadoActualizado.isPresent());
            assertEquals(50.0, estadoActualizado.get().getProgreso());
            
            logger.info("Progreso de estado de estrategia actualizado exitosamente");
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al actualizar progreso de estado de estrategia", e);
            fail("Error al actualizar progreso de estado de estrategia: " + e.getMessage());
        }
    }
    
    @Test
    @Order(11)
    @DisplayName("Actualizar resultado de pregunta")
    void testActualizarResultadoPregunta() {
        logger.info("Probando actualización de resultado de pregunta...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Obtener pregunta de sesión
            Optional<PreguntaSesion> preguntaOpt = preguntaSesionRepository.buscarPorId(preguntaSesionId);
            assertTrue(preguntaOpt.isPresent());
            
            PreguntaSesion pregunta = preguntaOpt.get();
            
            // Actualizar resultado
            pregunta.setResultado("fallo");
            pregunta.setRespuesta("Respuesta incorrecta");
            
            // Guardar cambios
            PreguntaSesion preguntaActualizada = preguntaSesionRepository.guardar(pregunta);
            
            // Verificar el cambio
            assertTrue(preguntaActualizada.esIncorrecta());
            assertEquals("Respuesta incorrecta", preguntaActualizada.getRespuesta());
            
            logger.info("Resultado de pregunta actualizado exitosamente");
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al actualizar resultado de pregunta", e);
            fail("Error al actualizar resultado de pregunta: " + e.getMessage());
        }
    }
    
    @Test
    @Order(12)
    @DisplayName("Agregar tiempo a estadísticas")
    void testAgregarTiempoEstadisticas() {
        logger.info("Probando agregar tiempo a estadísticas...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Obtener tiempo actual
            Optional<EstadisticasUsuario> estadisticasActuales = 
                estadisticasUsuarioRepository.buscarPorId(estadisticasUsuarioId);
            assertTrue(estadisticasActuales.isPresent());
            int tiempoActual = estadisticasActuales.get().getTiempoTotal();
            
            // Agregar tiempo
            boolean actualizado = estadisticasUsuarioRepository.agregarTiempo(estadisticasUsuarioId, 300); // 5 minutos
            
            // Verificar que se actualizó
            assertTrue(actualizado);
            
            // Verificar el cambio
            Optional<EstadisticasUsuario> estadisticasActualizadas = 
                estadisticasUsuarioRepository.buscarPorId(estadisticasUsuarioId);
            assertTrue(estadisticasActualizadas.isPresent());
            assertEquals(tiempoActual + 300, estadisticasActualizadas.get().getTiempoTotal());
            
            logger.info("Tiempo agregado a estadísticas exitosamente");
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al agregar tiempo a estadísticas", e);
            fail("Error al agregar tiempo a estadísticas: " + e.getMessage());
        }
    }
    
    @Test
    @Order(13)
    @DisplayName("Contar entidades")
    void testContarEntidades() {
        logger.info("Probando conteo de entidades...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Contar sesiones
            long sesionesCount = sesionRepository.contarSesiones();
            assertEquals(1, sesionesCount);
            
            // Contar estados de estrategia
            long estadosCount = estadoEstrategiaRepository.contarPorUsuario("usuario_test");
            assertEquals(1, estadosCount);
            
            // Contar preguntas de sesión
            long preguntasCount = preguntaSesionRepository.contarPorSesion(sesionId);
            assertEquals(1, preguntasCount);
            
            // Contar estadísticas
            long estadisticasCount = estadisticasUsuarioRepository.contarPorUsuario("usuario_test");
            assertEquals(1, estadisticasCount);
            
            logger.info("Conteo de entidades exitoso - Sesiones: {}, Estados: {}, Preguntas: {}, Estadísticas: {}", 
                      sesionesCount, estadosCount, preguntasCount, estadisticasCount);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al contar entidades", e);
            fail("Error al contar entidades: " + e.getMessage());
        }
    }
    
    @Test
    @Order(14)
    @DisplayName("Calcular porcentaje de aciertos")
    void testCalcularPorcentajeAciertos() {
        logger.info("Probando cálculo de porcentaje de aciertos...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Obtener preguntas de la sesión
            List<PreguntaSesion> preguntas = preguntaSesionRepository.buscarPorSesion(sesionId);
            
            // Calcular porcentaje manualmente
            long aciertos = preguntas.stream().filter(PreguntaSesion::esCorrecta).count();
            double porcentaje = preguntas.isEmpty() ? 0.0 : (double) aciertos / preguntas.size() * 100.0;
            
            // Verificar que el porcentaje es correcto (1 respuesta correcta de 1 total = 100%)
            assertEquals(100.0, porcentaje, 0.01);
            
            logger.info("Porcentaje de aciertos calculado exitosamente: {}%", porcentaje);
            
            transaction.commit();
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al calcular porcentaje de aciertos", e);
            fail("Error al calcular porcentaje de aciertos: " + e.getMessage());
        }
    }
    
    @Test
    @Order(15)
    @DisplayName("Limpiar datos de prueba")
    void testLimpiarDatos() {
        logger.info("Limpiando datos de prueba...");
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        try {
            // Eliminar en orden inverso para respetar las relaciones
            if (preguntaSesionId != null) {
                preguntaSesionRepository.eliminar(preguntaSesionId);
                logger.info("Pregunta de sesión eliminada - ID: {}", preguntaSesionId);
            }
            
            if (estadoEstrategiaId != null) {
                estadoEstrategiaRepository.eliminar(estadoEstrategiaId);
                logger.info("Estado de estrategia eliminado - ID: {}", estadoEstrategiaId);
            }
            
            if (sesionId != null) {
                sesionRepository.eliminar(sesionId);
                logger.info("Sesión eliminada - ID: {}", sesionId);
            }
            
            if (estadisticasUsuarioId != null) {
                estadisticasUsuarioRepository.eliminar(estadisticasUsuarioId);
                logger.info("Estadísticas de usuario eliminadas - ID: {}", estadisticasUsuarioId);
            }
            
            transaction.commit();
            logger.info("Datos de prueba limpiados exitosamente");
            
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error al limpiar datos de prueba", e);
            fail("Error al limpiar datos de prueba: " + e.getMessage());
        }
    }
} 