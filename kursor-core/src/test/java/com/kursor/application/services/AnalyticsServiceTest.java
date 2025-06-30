package com.kursor.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de AnalyticsService")
class AnalyticsServiceTest {

    private AnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new AnalyticsService();
    }

    @Nested
    @DisplayName("Métricas del Dashboard")
    class MetricasDashboardTests {

        @Test
        @DisplayName("Debería obtener métricas básicas del dashboard")
        void deberiaObtenerMetricasBasicasDelDashboard() {
            // When
            AnalyticsService.DashboardMetrics metricas = service.getDashboardMetrics("curso1", null, "semana");
            
            // Then
            assertNotNull(metricas);
            assertTrue(metricas.getPorcentajeExito() >= 0);
            assertTrue(metricas.getVelocidadPromedio() >= 0);
            assertTrue(metricas.getTotalSesiones() >= 0);
            assertTrue(metricas.getProgresoGeneral() >= 0);
        }

        @Test
        @DisplayName("Debería obtener métricas con bloque específico")
        void deberiaObtenerMetricasConBloqueEspecifico() {
            // When
            AnalyticsService.DashboardMetrics metricas = service.getDashboardMetrics("curso1", "bloque1", "mes");
            
            // Then
            assertNotNull(metricas);
            assertNotNull(metricas.getTendencias());
            assertNotNull(metricas.getDistribucionBloques());
        }

        @Test
        @DisplayName("Debería retornar tendencias temporales")
        void deberiaRetornarTendenciasTemporales() {
            // When
            AnalyticsService.DashboardMetrics metricas = service.getDashboardMetrics("curso1", null, "semana");
            List<Map<String, Object>> tendencias = metricas.getTendencias();
            
            // Then
            assertNotNull(tendencias);
            assertFalse(tendencias.isEmpty());
            
            // Verificar estructura de tendencias
            Map<String, Object> primeraTendencia = tendencias.get(0);
            assertTrue(primeraTendencia.containsKey("fecha"));
            assertTrue(primeraTendencia.containsKey("exito"));
            assertTrue(primeraTendencia.containsKey("velocidad"));
        }

        @Test
        @DisplayName("Debería retornar distribución de bloques")
        void deberiaRetornarDistribucionDeBloques() {
            // When
            AnalyticsService.DashboardMetrics metricas = service.getDashboardMetrics("curso1", null, "mes");
            Map<String, Integer> distribucion = metricas.getDistribucionBloques();
            
            // Then
            assertNotNull(distribucion);
            assertFalse(distribucion.isEmpty());
            
            // Verificar que todos los valores son positivos
            for (Integer valor : distribucion.values()) {
                assertTrue(valor >= 0);
            }
        }
    }

    @Nested
    @DisplayName("Métricas de Estrategias")
    class MetricasEstrategiasTests {

        @Test
        @DisplayName("Debería obtener métricas de estrategias")
        void deberiaObtenerMetricasDeEstrategias() {
            // When
            List<Map<String, Object>> metricas = service.getEstrategiasMetrics("curso1");
            
            // Then
            assertNotNull(metricas);
            assertFalse(metricas.isEmpty());
            
            // Verificar estructura de métricas
            Map<String, Object> primeraEstrategia = metricas.get(0);
            assertTrue(primeraEstrategia.containsKey("nombre"));
            assertTrue(primeraEstrategia.containsKey("exito"));
            assertTrue(primeraEstrategia.containsKey("velocidad"));
            assertTrue(primeraEstrategia.containsKey("sesiones"));
            assertTrue(primeraEstrategia.containsKey("recomendacion"));
        }

        @Test
        @DisplayName("Debería incluir todas las estrategias principales")
        void deberiaIncluirTodasLasEstrategiasPrincipales() {
            // When
            List<Map<String, Object>> metricas = service.getEstrategiasMetrics("curso1");
            
            // Then
            List<String> nombresEstrategias = metricas.stream()
                .map(m -> (String) m.get("nombre"))
                .toList();
            
            assertTrue(nombresEstrategias.contains("Secuencial"));
            assertTrue(nombresEstrategias.contains("Aleatoria"));
            assertTrue(nombresEstrategias.contains("Repetición Espaciada"));
            assertTrue(nombresEstrategias.contains("Repetir Incorrectas"));
        }
    }

    @Nested
    @DisplayName("Recomendaciones")
    class RecomendacionesTests {

        @Test
        @DisplayName("Debería generar recomendaciones personalizadas")
        void deberiaGenerarRecomendacionesPersonalizadas() {
            // When
            List<Map<String, String>> recomendaciones = service.getRecomendaciones("curso1");
            
            // Then
            assertNotNull(recomendaciones);
            assertFalse(recomendaciones.isEmpty());
            
            // Verificar estructura de recomendaciones
            Map<String, String> primeraRecomendacion = recomendaciones.get(0);
            assertTrue(primeraRecomendacion.containsKey("icono"));
            assertTrue(primeraRecomendacion.containsKey("titulo"));
            assertTrue(primeraRecomendacion.containsKey("descripcion"));
        }

        @Test
        @DisplayName("Debería proporcionar recomendaciones útiles")
        void deberiaProporcionarRecomendacionesUtiles() {
            // When
            List<Map<String, String>> recomendaciones = service.getRecomendaciones("curso1");
            
            // Then
            for (Map<String, String> recomendacion : recomendaciones) {
                String titulo = recomendacion.get("titulo");
                String descripcion = recomendacion.get("descripcion");
                
                assertNotNull(titulo);
                assertNotNull(descripcion);
                assertFalse(titulo.trim().isEmpty());
                assertFalse(descripcion.trim().isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Cálculos de Progreso")
    class CalculosProgresoTests {

        @Test
        @DisplayName("Debería calcular progreso del curso")
        void deberiaCalcularProgresoCurso() {
            // When
            double progreso = service.calcularProgresoCurso("curso1");
            
            // Then
            assertTrue(progreso >= 0.0);
            assertTrue(progreso <= 100.0);
        }

        @Test
        @DisplayName("Debería mantener consistencia en cálculos")
        void deberiaMantenerConsistenciaEnCalculos() {
            // When - múltiples llamadas
            double progreso1 = service.calcularProgresoCurso("curso1");
            double progreso2 = service.calcularProgresoCurso("curso1");
            
            // Then
            assertEquals(progreso1, progreso2, 0.01);
        }
    }

    @Nested
    @DisplayName("Estadísticas de Tiempo")
    class EstadisticasTiempoTests {

        @Test
        @DisplayName("Debería obtener estadísticas de tiempo")
        void deberiaObtenerEstadisticasDeTiempo() {
            // When
            Map<String, Object> stats = service.getTiempoEstadisticas("curso1", null);
            
            // Then
            assertNotNull(stats);
            assertTrue(stats.containsKey("promedio"));
            assertTrue(stats.containsKey("minimo"));
            assertTrue(stats.containsKey("maximo"));
            assertTrue(stats.containsKey("mediana"));
        }

        @Test
        @DisplayName("Debería validar consistencia de tiempos")
        void deberiaValidarConsistenciaDeTiempos() {
            // When
            Map<String, Object> stats = service.getTiempoEstadisticas("curso1", "bloque1");
            
            // Then
            double promedio = (Double) stats.get("promedio");
            double minimo = (Double) stats.get("minimo");
            double maximo = (Double) stats.get("maximo");
            double mediana = (Double) stats.get("mediana");
            
            assertTrue(minimo >= 0);
            assertTrue(maximo >= minimo);
            assertTrue(promedio >= minimo);
            assertTrue(promedio <= maximo);
            assertTrue(mediana >= 0);
        }
    }

    @Nested
    @DisplayName("Compatibilidad")
    class CompatibilidadTests {

        @Test
        @DisplayName("Debería ser compatible con métodos legacy")
        void deberiaSerCompatibleConMetodosLegacy() {
            // When
            AnalyticsService.DashboardMetrics metricas = service.getDashboardMetrics("curso1", null, "semana");
            
            // Then - Verificar métodos de compatibilidad
            assertNotNull(metricas.getSesionesCompletadas());
            assertTrue(metricas.getSesionesCompletadas() >= 0);
            
            assertNotNull(metricas.getProgresoCurso());
            assertTrue(metricas.getProgresoCurso() >= 0);
            
            assertNotNull(metricas.getTendenciasTemporales());
            assertFalse(metricas.getTendenciasTemporales().isEmpty());
        }
    }
} 