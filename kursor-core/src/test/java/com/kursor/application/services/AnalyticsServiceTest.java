package com.kursor.application.services;

import com.kursor.persistence.repository.SesionRepository;
import com.kursor.persistence.repository.EstadisticasUsuarioRepository;
import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.entity.EstadisticasUsuario;
import com.kursor.persistence.entity.EstadoSesion;
import com.kursor.domain.Curso;
import com.kursor.domain.EstrategiaAprendizaje;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de AnalyticsService")
class AnalyticsServiceTest {

    @Mock private SesionRepository sesionRepository;
    @Mock private EstadisticasUsuarioRepository estadisticasRepository;
    @Mock private Curso mockCurso;
    @Mock private EstrategiaAprendizaje mockEstrategia;
    
    private AnalyticsService service;
    private List<Sesion> sesionesMock;

    @BeforeEach
    void setUp() {
        service = new AnalyticsService(sesionRepository, estadisticasRepository);
        
        // Configurar sesiones mock
        sesionesMock = Arrays.asList(
            createMockSesion("sesion1", "curso1", EstadoSesion.COMPLETADA, 100.0, LocalDateTime.now().minusDays(1)),
            createMockSesion("sesion2", "curso1", EstadoSesion.EN_PROGRESO, 50.0, LocalDateTime.now()),
            createMockSesion("sesion3", "curso2", EstadoSesion.COMPLETADA, 100.0, LocalDateTime.now().minusDays(2))
        );
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear servicio con repositorios válidos")
        void deberiaCrearServicioConRepositoriosValidos() {
            AnalyticsService testService = new AnalyticsService(sesionRepository, estadisticasRepository);
            
            assertNotNull(testService);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando repositorio de sesiones es null")
        void deberiaLanzarExcepcionCuandoRepositorioSesionesEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new AnalyticsService(null, estadisticasRepository);
            });
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando repositorio de estadísticas es null")
        void deberiaLanzarExcepcionCuandoRepositorioEstadisticasEsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new AnalyticsService(sesionRepository, null);
            });
        }
    }

    @Nested
    @DisplayName("Métricas de dashboard")
    class MetricasDashboardTests {

        @Test
        @DisplayName("Debería generar métricas de dashboard")
        void deberiaGenerarMetricasDeDashboard() {
            when(sesionRepository.findAll()).thenReturn(sesionesMock);
            when(sesionRepository.count()).thenReturn(3L);
            
            AnalyticsService.DashboardMetrics metricas = service.generateDashboardMetrics("usuario1", 30);
            
            assertNotNull(metricas);
            assertEquals(3, metricas.getTotalSesiones());
            assertEquals(2, metricas.getSesionesCompletadas());
            assertEquals(1, metricas.getSesionesEnProgreso());
            assertTrue(metricas.getPorcentajeCompletitud() > 0);
        }

        @Test
        @DisplayName("Debería manejar usuario sin sesiones")
        void deberiaManejarUsuarioSinSesiones() {
            when(sesionRepository.findAll()).thenReturn(List.of());
            when(sesionRepository.count()).thenReturn(0L);
            
            AnalyticsService.DashboardMetrics metricas = service.generateDashboardMetrics("usuario_nuevo", 30);
            
            assertNotNull(metricas);
            assertEquals(0, metricas.getTotalSesiones());
            assertEquals(0, metricas.getSesionesCompletadas());
            assertEquals(0, metricas.getSesionesEnProgreso());
            assertEquals(0.0, metricas.getPorcentajeCompletitud());
        }

        @Test
        @DisplayName("Debería calcular porcentaje de completitud correctamente")
        void deberiaCalcularPorcentajeDeCompletitudCorrectamente() {
            when(sesionRepository.findAll()).thenReturn(sesionesMock);
            when(sesionRepository.count()).thenReturn(3L);
            
            AnalyticsService.DashboardMetrics metricas = service.generateDashboardMetrics("usuario1", 30);
            
            // 2 sesiones completadas de 3 totales = 66.67%
            double porcentajeEsperado = (2.0 / 3.0) * 100;
            assertEquals(porcentajeEsperado, metricas.getPorcentajeCompletitud(), 0.01);
        }
    }

    @Nested
    @DisplayName("Análisis por curso")
    class AnalisisPorCursoTests {

        @Test
        @DisplayName("Debería generar análisis por curso")
        void deberiaGenerarAnalisisPorCurso() {
            when(sesionRepository.findByCursoId("curso1")).thenReturn(
                Arrays.asList(sesionesMock.get(0), sesionesMock.get(1))
            );
            
            Map<String, Object> analisis = service.generateCourseAnalytics("usuario1", "curso1", 30);
            
            assertNotNull(analisis);
            assertTrue(analisis.containsKey("totalSesiones"));
            assertTrue(analisis.containsKey("sesionesCompletadas"));
            assertTrue(analisis.containsKey("promedioCompletitud"));
        }

        @Test
        @DisplayName("Debería manejar curso sin sesiones")
        void deberiaManejarCursoSinSesiones() {
            when(sesionRepository.findByCursoId("curso_inexistente")).thenReturn(List.of());
            
            Map<String, Object> analisis = service.generateCourseAnalytics("usuario1", "curso_inexistente", 30);
            
            assertNotNull(analisis);
            assertEquals(0, analisis.get("totalSesiones"));
            assertEquals(0, analisis.get("sesionesCompletadas"));
            assertEquals(0.0, analisis.get("promedioCompletitud"));
        }

        @Test
        @DisplayName("Debería calcular promedio de completitud por curso")
        void deberiaCalcularPromedioDeCompletitudPorCurso() {
            when(sesionRepository.findByCursoId("curso1")).thenReturn(
                Arrays.asList(sesionesMock.get(0), sesionesMock.get(1))
            );
            
            Map<String, Object> analisis = service.generateCourseAnalytics("usuario1", "curso1", 30);
            
            // (100.0 + 50.0) / 2 = 75.0
            assertEquals(75.0, analisis.get("promedioCompletitud"));
        }
    }

    @Nested
    @DisplayName("Análisis por período")
    class AnalisisPorPeriodoTests {

        @Test
        @DisplayName("Debería generar análisis por período")
        void deberiaGenerarAnalisisPorPeriodo() {
            LocalDateTime inicio = LocalDateTime.now().minusDays(7);
            LocalDateTime fin = LocalDateTime.now();
            
            when(sesionRepository.findByFechaBetween(inicio, fin)).thenReturn(sesionesMock);
            
            Map<String, Object> analisis = service.generatePeriodAnalytics("usuario1", inicio, fin);
            
            assertNotNull(analisis);
            assertTrue(analisis.containsKey("sesionesEnPeriodo"));
            assertTrue(analisis.containsKey("promedioCompletitud"));
            assertTrue(analisis.containsKey("tendencia"));
        }

        @Test
        @DisplayName("Debería manejar período sin sesiones")
        void deberiaManejarPeriodoSinSesiones() {
            LocalDateTime inicio = LocalDateTime.now().minusDays(30);
            LocalDateTime fin = LocalDateTime.now().minusDays(25);
            
            when(sesionRepository.findByFechaBetween(inicio, fin)).thenReturn(List.of());
            
            Map<String, Object> analisis = service.generatePeriodAnalytics("usuario1", inicio, fin);
            
            assertNotNull(analisis);
            assertEquals(0, analisis.get("sesionesEnPeriodo"));
            assertEquals(0.0, analisis.get("promedioCompletitud"));
        }

        @Test
        @DisplayName("Debería calcular tendencia de aprendizaje")
        void deberiaCalcularTendenciaDeAprendizaje() {
            LocalDateTime inicio = LocalDateTime.now().minusDays(7);
            LocalDateTime fin = LocalDateTime.now();
            
            when(sesionRepository.findByFechaBetween(inicio, fin)).thenReturn(sesionesMock);
            
            Map<String, Object> analisis = service.generatePeriodAnalytics("usuario1", inicio, fin);
            
            assertNotNull(analisis.get("tendencia"));
            // La tendencia debería ser un valor numérico que indique si el progreso está mejorando
        }
    }

    @Nested
    @DisplayName("Estadísticas de usuario")
    class EstadisticasUsuarioTests {

        @Test
        @DisplayName("Debería generar estadísticas de usuario")
        void deberiaGenerarEstadisticasDeUsuario() {
            when(sesionRepository.findAll()).thenReturn(sesionesMock);
            when(estadisticasRepository.findByUsuarioId("usuario1")).thenReturn(
                Arrays.asList(createMockEstadisticas("usuario1", "curso1", 85.0))
            );
            
            Map<String, Object> estadisticas = service.generateUserStatistics("usuario1");
            
            assertNotNull(estadisticas);
            assertTrue(estadisticas.containsKey("totalSesiones"));
            assertTrue(estadisticas.containsKey("promedioCompletitud"));
            assertTrue(estadisticas.containsKey("cursosCompletados"));
        }

        @Test
        @DisplayName("Debería manejar usuario sin estadísticas")
        void deberiaManejarUsuarioSinEstadisticas() {
            when(sesionRepository.findAll()).thenReturn(List.of());
            when(estadisticasRepository.findByUsuarioId("usuario_nuevo")).thenReturn(List.of());
            
            Map<String, Object> estadisticas = service.generateUserStatistics("usuario_nuevo");
            
            assertNotNull(estadisticas);
            assertEquals(0, estadisticas.get("totalSesiones"));
            assertEquals(0.0, estadisticas.get("promedioCompletitud"));
            assertEquals(0, estadisticas.get("cursosCompletados"));
        }

        @Test
        @DisplayName("Debería calcular promedio de rendimiento")
        void deberiaCalcularPromedioDeRendimiento() {
            when(sesionRepository.findAll()).thenReturn(sesionesMock);
            when(estadisticasRepository.findByUsuarioId("usuario1")).thenReturn(
                Arrays.asList(
                    createMockEstadisticas("usuario1", "curso1", 85.0),
                    createMockEstadisticas("usuario1", "curso2", 90.0)
                )
            );
            
            Map<String, Object> estadisticas = service.generateUserStatistics("usuario1");
            
            // (85.0 + 90.0) / 2 = 87.5
            assertEquals(87.5, estadisticas.get("promedioCompletitud"));
        }
    }

    @Nested
    @DisplayName("Reportes")
    class ReportesTests {

        @Test
        @DisplayName("Debería generar reporte de progreso")
        void deberiaGenerarReporteDeProgreso() {
            when(sesionRepository.findAll()).thenReturn(sesionesMock);
            
            String reporte = service.generateProgressReport("usuario1", 30);
            
            assertNotNull(reporte);
            assertFalse(reporte.isEmpty());
            assertTrue(reporte.contains("usuario1"));
        }

        @Test
        @DisplayName("Debería generar reporte de rendimiento")
        void deberiaGenerarReporteDeRendimiento() {
            when(sesionRepository.findAll()).thenReturn(sesionesMock);
            when(estadisticasRepository.findByUsuarioId("usuario1")).thenReturn(
                Arrays.asList(createMockEstadisticas("usuario1", "curso1", 85.0))
            );
            
            String reporte = service.generatePerformanceReport("usuario1");
            
            assertNotNull(reporte);
            assertFalse(reporte.isEmpty());
            assertTrue(reporte.contains("Rendimiento"));
        }

        @Test
        @DisplayName("Debería generar reporte de tendencias")
        void deberiaGenerarReporteDeTendencias() {
            LocalDateTime inicio = LocalDateTime.now().minusDays(30);
            LocalDateTime fin = LocalDateTime.now();
            
            when(sesionRepository.findByFechaBetween(inicio, fin)).thenReturn(sesionesMock);
            
            String reporte = service.generateTrendReport("usuario1", 30);
            
            assertNotNull(reporte);
            assertFalse(reporte.isEmpty());
            assertTrue(reporte.contains("Tendencias"));
        }
    }

    @Nested
    @DisplayName("Manejo de errores")
    class ManejoErroresTests {

        @Test
        @DisplayName("Debería manejar error en repositorio de sesiones")
        void deberiaManejarErrorEnRepositorioSesiones() {
            when(sesionRepository.findAll()).thenThrow(new RuntimeException("Error de BD"));
            
            assertThrows(RuntimeException.class, () -> {
                service.generateDashboardMetrics("usuario1", 30);
            });
        }

        @Test
        @DisplayName("Debería manejar error en repositorio de estadísticas")
        void deberiaManejarErrorEnRepositorioEstadisticas() {
            when(estadisticasRepository.findByUsuarioId("usuario1")).thenThrow(new RuntimeException("Error de BD"));
            
            assertThrows(RuntimeException.class, () -> {
                service.generateUserStatistics("usuario1");
            });
        }

        @Test
        @DisplayName("Debería manejar parámetros inválidos")
        void deberiaManejarParametrosInvalidos() {
            assertThrows(IllegalArgumentException.class, () -> {
                service.generateDashboardMetrics(null, 30);
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                service.generateDashboardMetrics("usuario1", -1);
            });
        }
    }

    /**
     * Crea una sesión mock para testing.
     */
    private Sesion createMockSesion(String id, String cursoId, EstadoSesion estado, double completitud, LocalDateTime fecha) {
        Sesion sesion = new Sesion();
        // Configurar propiedades básicas según la estructura real de Sesion
        return sesion;
    }

    /**
     * Crea estadísticas mock para testing.
     */
    private EstadisticasUsuario createMockEstadisticas(String usuarioId, String cursoId, double rendimiento) {
        EstadisticasUsuario estadisticas = new EstadisticasUsuario();
        // Configurar propiedades básicas según la estructura real de EstadisticasUsuario
        return estadisticas;
    }
} 