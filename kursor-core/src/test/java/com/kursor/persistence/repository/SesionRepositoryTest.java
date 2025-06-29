package com.kursor.persistence.repository;

import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadoSesion;
import com.kursor.domain.Curso;
import com.kursor.domain.EstrategiaAprendizaje;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de SesionRepository")
class SesionRepositoryTest {

    @Mock private EntityManager entityManager;
    @Mock private TypedQuery<Sesion> typedQuery;
    @Mock private Query query;
    @Mock private Curso mockCurso;
    @Mock private EstrategiaAprendizaje mockEstrategia;
    
    private SesionRepository repository;
    private Sesion sesionMock;

    @BeforeEach
    void setUp() {
        repository = new SesionRepository(entityManager);
        
        // Configurar sesión mock
        sesionMock = new Sesion();
        sesionMock.setId("sesion1");
        sesionMock.setCurso(mockCurso);
        sesionMock.setEstrategia(mockEstrategia);
        sesionMock.setFechaInicio(LocalDateTime.now());
        sesionMock.setEstado(EstadoSesion.EN_PROGRESO);
        sesionMock.setPorcentajeCompletitud(50.0);
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear repositorio con EntityManager")
        void deberiaCrearRepositorioConEntityManager() {
            SesionRepository testRepository = new SesionRepository(entityManager);
            
            assertNotNull(testRepository);
        }

        @Test
        @DisplayName("Debería crear repositorio sin EntityManager")
        void deberiaCrearRepositorioSinEntityManager() {
            SesionRepository testRepository = new SesionRepository();
            
            assertNotNull(testRepository);
        }
    }

    @Nested
    @DisplayName("Operaciones CRUD básicas")
    class OperacionesCrudTests {

        @Test
        @DisplayName("Debería guardar sesión exitosamente")
        void deberiaGuardarSesionExitosamente() {
            when(entityManager.getTransaction()).thenReturn(mock(jakarta.persistence.EntityTransaction.class));
            when(entityManager.merge(sesionMock)).thenReturn(sesionMock);
            
            Sesion resultado = repository.save(sesionMock);
            
            assertNotNull(resultado);
            assertEquals("sesion1", resultado.getId());
            verify(entityManager, times(1)).merge(sesionMock);
        }

        @Test
        @DisplayName("Debería persistir nueva sesión")
        void deberiaPersistirNuevaSesion() {
            Sesion nuevaSesion = new Sesion();
            nuevaSesion.setId("nueva");
            
            when(entityManager.getTransaction()).thenReturn(mock(jakarta.persistence.EntityTransaction.class));
            when(entityManager.merge(nuevaSesion)).thenReturn(nuevaSesion);
            
            Sesion resultado = repository.save(nuevaSesion);
            
            assertNotNull(resultado);
            assertEquals("nueva", resultado.getId());
        }

        @Test
        @DisplayName("Debería encontrar sesión por ID")
        void deberiaEncontrarSesionPorId() {
            when(entityManager.find(Sesion.class, "sesion1")).thenReturn(sesionMock);
            
            Optional<Sesion> resultado = repository.findById("sesion1");
            
            assertTrue(resultado.isPresent());
            assertEquals("sesion1", resultado.get().getId());
            verify(entityManager, times(1)).find(Sesion.class, "sesion1");
        }

        @Test
        @DisplayName("Debería retornar empty cuando sesión no existe")
        void deberiaRetornarEmptyCuandoSesionNoExiste() {
            when(entityManager.find(Sesion.class, "inexistente")).thenReturn(null);
            
            Optional<Sesion> resultado = repository.findById("inexistente");
            
            assertFalse(resultado.isPresent());
            verify(entityManager, times(1)).find(Sesion.class, "inexistente");
        }

        @Test
        @DisplayName("Debería eliminar sesión exitosamente")
        void deberiaEliminarSesionExitosamente() {
            when(entityManager.getTransaction()).thenReturn(mock(jakarta.persistence.EntityTransaction.class));
            when(entityManager.find(Sesion.class, "sesion1")).thenReturn(sesionMock);
            
            boolean resultado = repository.deleteById("sesion1");
            
            assertTrue(resultado);
            verify(entityManager, times(1)).remove(sesionMock);
        }

        @Test
        @DisplayName("Debería retornar false al eliminar sesión inexistente")
        void deberiaRetornarFalseAlEliminarSesionInexistente() {
            when(entityManager.getTransaction()).thenReturn(mock(jakarta.persistence.EntityTransaction.class));
            when(entityManager.find(Sesion.class, "inexistente")).thenReturn(null);
            
            boolean resultado = repository.deleteById("inexistente");
            
            assertFalse(resultado);
            verify(entityManager, never()).remove(any());
        }
    }

    @Nested
    @DisplayName("Consultas específicas del dominio")
    class ConsultasEspecificasTests {

        @Test
        @DisplayName("Debería encontrar sesiones por curso")
        void deberiaEncontrarSesionesPorCurso() {
            List<Sesion> sesiones = Arrays.asList(sesionMock);
            
            when(entityManager.createQuery(anyString(), eq(Sesion.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(sesiones);
            
            List<Sesion> resultado = repository.findByCursoId("curso1");
            
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("sesion1", resultado.get(0).getId());
            verify(typedQuery, times(1)).setParameter("cursoId", "curso1");
        }

        @Test
        @DisplayName("Debería encontrar sesiones por estado")
        void deberiaEncontrarSesionesPorEstado() {
            List<Sesion> sesiones = Arrays.asList(sesionMock);
            
            when(entityManager.createQuery(anyString(), eq(Sesion.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(sesiones);
            
            List<Sesion> resultado = repository.findByEstado(EstadoSesion.EN_PROGRESO);
            
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(typedQuery, times(1)).setParameter("estado", EstadoSesion.EN_PROGRESO);
        }

        @Test
        @DisplayName("Debería encontrar sesiones por rango de fechas")
        void deberiaEncontrarSesionesPorRangoDeFechas() {
            LocalDateTime inicio = LocalDateTime.now().minusDays(7);
            LocalDateTime fin = LocalDateTime.now();
            List<Sesion> sesiones = Arrays.asList(sesionMock);
            
            when(entityManager.createQuery(anyString(), eq(Sesion.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(sesiones);
            
            List<Sesion> resultado = repository.findByFechaBetween(inicio, fin);
            
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(typedQuery, times(1)).setParameter("fechaInicio", inicio);
            verify(typedQuery, times(1)).setParameter("fechaFin", fin);
        }

        @Test
        @DisplayName("Debería contar sesiones por curso")
        void deberiaContarSesionesPorCurso() {
            when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenReturn(5L);
            
            long resultado = repository.countByCursoId("curso1");
            
            assertEquals(5L, resultado);
            verify(typedQuery, times(1)).setParameter("cursoId", "curso1");
        }

        @Test
        @DisplayName("Debería encontrar sesiones completadas")
        void deberiaEncontrarSesionesCompletadas() {
            List<Sesion> sesiones = Arrays.asList(sesionMock);
            
            when(entityManager.createQuery(anyString(), eq(Sesion.class))).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(sesiones);
            
            List<Sesion> resultado = repository.findSesionesCompletadas();
            
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("Manejo de transacciones")
    class ManejoTransaccionesTests {

        @Test
        @DisplayName("Debería manejar rollback en caso de error")
        void deberiaManejarRollbackEnCasoDeError() {
            jakarta.persistence.EntityTransaction transaction = mock(jakarta.persistence.EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(sesionMock)).thenThrow(new RuntimeException("Error de BD"));
            
            assertThrows(RuntimeException.class, () -> {
                repository.save(sesionMock);
            });
            
            verify(transaction, times(1)).rollback();
        }

        @Test
        @DisplayName("Debería hacer commit en operación exitosa")
        void deberiaHacerCommitEnOperacionExitosa() {
            jakarta.persistence.EntityTransaction transaction = mock(jakarta.persistence.EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            when(entityManager.merge(sesionMock)).thenReturn(sesionMock);
            
            repository.save(sesionMock);
            
            verify(transaction, times(1)).commit();
        }
    }

    @Nested
    @DisplayName("Validaciones")
    class ValidacionesTests {

        @Test
        @DisplayName("Debería validar sesión antes de guardar")
        void deberiaValidarSesionAntesDeGuardar() {
            Sesion sesionInvalida = new Sesion();
            // Sesión sin ID ni curso
            
            when(entityManager.getTransaction()).thenReturn(mock(jakarta.persistence.EntityTransaction.class));
            
            // No debería lanzar excepción, pero debería manejar la sesión inválida
            assertDoesNotThrow(() -> {
                repository.save(sesionInvalida);
            });
        }

        @Test
        @DisplayName("Debería manejar EntityManager null")
        void deberiaManejarEntityManagerNull() {
            SesionRepository repoSinEM = new SesionRepository();
            
            // Debería manejar graciosamente la falta de EntityManager
            assertDoesNotThrow(() -> {
                repoSinEM.findById("sesion1");
            });
        }
    }

    @Nested
    @DisplayName("Métodos de utilidad")
    class MetodosUtilidadTests {

        @Test
        @DisplayName("Debería obtener todas las sesiones")
        void deberiaObtenerTodasLasSesiones() {
            List<Sesion> sesiones = Arrays.asList(sesionMock);
            
            when(entityManager.createQuery(anyString(), eq(Sesion.class))).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(sesiones);
            
            List<Sesion> resultado = repository.findAll();
            
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("sesion1", resultado.get(0).getId());
        }

        @Test
        @DisplayName("Debería contar total de sesiones")
        void deberiaContarTotalDeSesiones() {
            when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenReturn(10L);
            
            long resultado = repository.count();
            
            assertEquals(10L, resultado);
        }

        @Test
        @DisplayName("Debería verificar existencia de sesión")
        void deberiaVerificarExistenciaDeSesion() {
            when(entityManager.find(Sesion.class, "sesion1")).thenReturn(sesionMock);
            
            boolean resultado = repository.existsById("sesion1");
            
            assertTrue(resultado);
            verify(entityManager, times(1)).find(Sesion.class, "sesion1");
        }

        @Test
        @DisplayName("Debería verificar no existencia de sesión")
        void deberiaVerificarNoExistenciaDeSesion() {
            when(entityManager.find(Sesion.class, "inexistente")).thenReturn(null);
            
            boolean resultado = repository.existsById("inexistente");
            
            assertFalse(resultado);
        }
    }
} 