package com.kursor.ui.analytics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Controlador para las funcionalidades de analytics y estadísticas avanzadas.
 * 
 * <p>Este controlador maneja la lógica de negocio para obtener y procesar
 * datos de analytics, incluyendo métricas de rendimiento, tendencias
 * temporales, comparación de estrategias y recomendaciones personalizadas.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class AnalyticsController {
    
    /**
     * Constructor del controlador de analytics.
     */
    public AnalyticsController() {
        // Constructor simplificado para la prueba
    }
    
    /**
     * Obtiene las métricas del dashboard para un usuario específico.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso (opcional, null para todos los cursos)
     * @param periodo Días hacia atrás para el análisis
     * @return Métricas del dashboard
     */
    public DashboardMetrics obtenerDashboard(Long usuarioId, Long cursoId, int periodo) {
        // Por ahora retornamos datos de ejemplo
        DashboardMetrics metrics = new DashboardMetrics();
        metrics.setPorcentajeExito(87.5);
        metrics.setVelocidadPromedio(2.3);
        metrics.setSesionesCompletadas(24);
        metrics.setPreguntasRespondidas(180);
        metrics.setProgresoCurso(68.0);
        metrics.setTendenciasTemporales(calcularTendencias(periodo));
        metrics.setRendimientoPorBloque(calcularRendimientoPorBloque());
        
        return metrics;
    }
    
    /**
     * Calcula las tendencias temporales.
     * 
     * @param periodo Días del análisis
     * @return Lista de tendencias temporales
     */
    private List<TendenciaTemporal> calcularTendencias(int periodo) {
        List<TendenciaTemporal> tendencias = new ArrayList<>();
        
        // Crear datos de ejemplo
        for (int i = periodo - 1; i >= 0; i--) {
            LocalDate fecha = LocalDate.now().minusDays(i);
            TendenciaTemporal tendencia = new TendenciaTemporal();
            tendencia.setFecha(fecha);
            tendencia.setRendimientoDiario(75.0 + (i * 2.5)); // Ejemplo: mejora gradual
            tendencia.setPreguntasRespondidas(10 + (i * 2));
            tendencia.setVelocidadPromedio(3.0 - (i * 0.1)); // Ejemplo: mejora en velocidad
            tendencias.add(tendencia);
        }
        
        return tendencias;
    }
    
    /**
     * Calcula el rendimiento por bloque de contenido.
     * 
     * @return Mapa de rendimiento por bloque
     */
    private Map<String, Double> calcularRendimientoPorBloque() {
        Map<String, Double> rendimientoPorBloque = new HashMap<>();
        
        // Datos de ejemplo
        rendimientoPorBloque.put("Gramática", 92.0);
        rendimientoPorBloque.put("Vocabulario", 72.0);
        rendimientoPorBloque.put("Pronunciación", 85.0);
        
        return rendimientoPorBloque;
    }
    
    /**
     * Obtiene las recomendaciones personalizadas para un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de recomendaciones
     */
    public List<Recomendacion> obtenerRecomendaciones(Long usuarioId) {
        List<Recomendacion> recomendaciones = new ArrayList<>();
        
        // Recomendaciones de ejemplo
        Recomendacion rec1 = new Recomendacion();
        rec1.setTipo(Recomendacion.TipoRecomendacion.ESTRATEGIA_ESTUDIO);
        rec1.setTitulo("Cambiar a Repetición Espaciada");
        rec1.setDescripcion("Tu rendimiento mejora un 15% con esta estrategia.");
        rec1.setPrioridad(1);
        rec1.setImpactoEsperado(15.0);
        recomendaciones.add(rec1);
        
        Recomendacion rec2 = new Recomendacion();
        rec2.setTipo(Recomendacion.TipoRecomendacion.HORARIO_OPTIMO);
        rec2.setTitulo("Optimizar Horario de Estudio");
        rec2.setDescripcion("Tu mejor rendimiento es entre 18:00-20:00.");
        rec2.setPrioridad(2);
        rec2.setImpactoEsperado(10.0);
        recomendaciones.add(rec2);
        
        Recomendacion rec3 = new Recomendacion();
        rec3.setTipo(Recomendacion.TipoRecomendacion.BLOQUE_ENFOQUE);
        rec3.setTitulo("Enfócate en Vocabulario");
        rec3.setDescripcion("Este bloque tiene tu menor rendimiento (72%).");
        rec3.setPrioridad(3);
        rec3.setImpactoEsperado(8.0);
        recomendaciones.add(rec3);
        
        return recomendaciones;
    }
    
    /**
     * Compara la efectividad de diferentes estrategias.
     * 
     * @param usuarioId ID del usuario
     * @param cursoId ID del curso
     * @return Comparación de estrategias
     */
    public ComparacionEstrategias compararEstrategias(Long usuarioId, Long cursoId) {
        ComparacionEstrategias comparacion = new ComparacionEstrategias();
        Map<String, ComparacionEstrategias.EstrategiaMetrics> estrategias = new HashMap<>();
        
        // Datos de ejemplo
        ComparacionEstrategias.EstrategiaMetrics esp1 = new ComparacionEstrategias.EstrategiaMetrics();
        esp1.setPorcentajeExito(85.0);
        esp1.setVelocidadPromedio(2.1);
        esp1.setSesionesUtilizadas(12);
        esp1.setRetencionMemoria(92.0);
        estrategias.put("Repetición Espaciada", esp1);
        
        ComparacionEstrategias.EstrategiaMetrics esp2 = new ComparacionEstrategias.EstrategiaMetrics();
        esp2.setPorcentajeExito(75.0);
        esp2.setVelocidadPromedio(2.8);
        esp2.setSesionesUtilizadas(8);
        esp2.setRetencionMemoria(78.0);
        estrategias.put("Secuencial", esp2);
        
        ComparacionEstrategias.EstrategiaMetrics esp3 = new ComparacionEstrategias.EstrategiaMetrics();
        esp3.setPorcentajeExito(72.0);
        esp3.setVelocidadPromedio(3.2);
        esp3.setSesionesUtilizadas(4);
        esp3.setRetencionMemoria(70.0);
        estrategias.put("Aleatoria", esp3);
        
        comparacion.setEstrategias(estrategias);
        comparacion.setEstrategiaRecomendada("Repetición Espaciada");
        comparacion.setMejoraEsperada(10.0);
        
        return comparacion;
    }
    
    /**
     * Clase para métricas del dashboard.
     */
    public static class DashboardMetrics {
        private double porcentajeExito;
        private double velocidadPromedio;
        private int sesionesCompletadas;
        private int preguntasRespondidas;
        private double progresoCurso;
        private List<TendenciaTemporal> tendenciasTemporales;
        private Map<String, Double> rendimientoPorBloque;
        
        // Getters y setters
        public double getPorcentajeExito() { return porcentajeExito; }
        public void setPorcentajeExito(double porcentajeExito) { this.porcentajeExito = porcentajeExito; }
        
        public double getVelocidadPromedio() { return velocidadPromedio; }
        public void setVelocidadPromedio(double velocidadPromedio) { this.velocidadPromedio = velocidadPromedio; }
        
        public int getSesionesCompletadas() { return sesionesCompletadas; }
        public void setSesionesCompletadas(int sesionesCompletadas) { this.sesionesCompletadas = sesionesCompletadas; }
        
        public int getPreguntasRespondidas() { return preguntasRespondidas; }
        public void setPreguntasRespondidas(int preguntasRespondidas) { this.preguntasRespondidas = preguntasRespondidas; }
        
        public double getProgresoCurso() { return progresoCurso; }
        public void setProgresoCurso(double progresoCurso) { this.progresoCurso = progresoCurso; }
        
        public List<TendenciaTemporal> getTendenciasTemporales() { return tendenciasTemporales; }
        public void setTendenciasTemporales(List<TendenciaTemporal> tendenciasTemporales) { this.tendenciasTemporales = tendenciasTemporales; }
        
        public Map<String, Double> getRendimientoPorBloque() { return rendimientoPorBloque; }
        public void setRendimientoPorBloque(Map<String, Double> rendimientoPorBloque) { this.rendimientoPorBloque = rendimientoPorBloque; }
    }
    
    /**
     * Clase para tendencias temporales.
     */
    public static class TendenciaTemporal {
        private LocalDate fecha;
        private double rendimientoDiario;
        private int tiempoTotalSesion;
        private int preguntasRespondidas;
        private double velocidadPromedio;
        private String estrategiaUtilizada;
        private double dificultadPromedio;
        
        // Getters y setters
        public LocalDate getFecha() { return fecha; }
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }
        
        public double getRendimientoDiario() { return rendimientoDiario; }
        public void setRendimientoDiario(double rendimientoDiario) { this.rendimientoDiario = rendimientoDiario; }
        
        public int getTiempoTotalSesion() { return tiempoTotalSesion; }
        public void setTiempoTotalSesion(int tiempoTotalSesion) { this.tiempoTotalSesion = tiempoTotalSesion; }
        
        public int getPreguntasRespondidas() { return preguntasRespondidas; }
        public void setPreguntasRespondidas(int preguntasRespondidas) { this.preguntasRespondidas = preguntasRespondidas; }
        
        public double getVelocidadPromedio() { return velocidadPromedio; }
        public void setVelocidadPromedio(double velocidadPromedio) { this.velocidadPromedio = velocidadPromedio; }
        
        public String getEstrategiaUtilizada() { return estrategiaUtilizada; }
        public void setEstrategiaUtilizada(String estrategiaUtilizada) { this.estrategiaUtilizada = estrategiaUtilizada; }
        
        public double getDificultadPromedio() { return dificultadPromedio; }
        public void setDificultadPromedio(double dificultadPromedio) { this.dificultadPromedio = dificultadPromedio; }
    }
    
    /**
     * Clase para recomendaciones.
     */
    public static class Recomendacion {
        private TipoRecomendacion tipo;
        private String titulo;
        private String descripcion;
        private int prioridad;
        private double impactoEsperado;
        private List<String> accionesEspecificas;
        
        public enum TipoRecomendacion {
            ESTRATEGIA_ESTUDIO,
            HORARIO_OPTIMO,
            BLOQUE_ENFOQUE,
            FRECUENCIA_SESIONES,
            TECNICA_REPASO,
            GESTION_TIEMPO
        }
        
        // Getters y setters
        public TipoRecomendacion getTipo() { return tipo; }
        public void setTipo(TipoRecomendacion tipo) { this.tipo = tipo; }
        
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        
        public int getPrioridad() { return prioridad; }
        public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
        
        public double getImpactoEsperado() { return impactoEsperado; }
        public void setImpactoEsperado(double impactoEsperado) { this.impactoEsperado = impactoEsperado; }
        
        public List<String> getAccionesEspecificas() { return accionesEspecificas; }
        public void setAccionesEspecificas(List<String> accionesEspecificas) { this.accionesEspecificas = accionesEspecificas; }
    }
    
    /**
     * Clase para comparación de estrategias.
     */
    public static class ComparacionEstrategias {
        private Map<String, EstrategiaMetrics> estrategias;
        private String estrategiaRecomendada;
        private double mejoraEsperada;
        private List<String> factoresClave;
        
        public static class EstrategiaMetrics {
            private double porcentajeExito;
            private double velocidadPromedio;
            private int sesionesUtilizadas;
            private double retencionMemoria;
            private double satisfaccionUsuario;
            
            // Getters y setters
            public double getPorcentajeExito() { return porcentajeExito; }
            public void setPorcentajeExito(double porcentajeExito) { this.porcentajeExito = porcentajeExito; }
            
            public double getVelocidadPromedio() { return velocidadPromedio; }
            public void setVelocidadPromedio(double velocidadPromedio) { this.velocidadPromedio = velocidadPromedio; }
            
            public int getSesionesUtilizadas() { return sesionesUtilizadas; }
            public void setSesionesUtilizadas(int sesionesUtilizadas) { this.sesionesUtilizadas = sesionesUtilizadas; }
            
            public double getRetencionMemoria() { return retencionMemoria; }
            public void setRetencionMemoria(double retencionMemoria) { this.retencionMemoria = retencionMemoria; }
            
            public double getSatisfaccionUsuario() { return satisfaccionUsuario; }
            public void setSatisfaccionUsuario(double satisfaccionUsuario) { this.satisfaccionUsuario = satisfaccionUsuario; }
        }
        
        // Getters y setters
        public Map<String, EstrategiaMetrics> getEstrategias() { return estrategias; }
        public void setEstrategias(Map<String, EstrategiaMetrics> estrategias) { this.estrategias = estrategias; }
        
        public String getEstrategiaRecomendada() { return estrategiaRecomendada; }
        public void setEstrategiaRecomendada(String estrategiaRecomendada) { this.estrategiaRecomendada = estrategiaRecomendada; }
        
        public double getMejoraEsperada() { return mejoraEsperada; }
        public void setMejoraEsperada(double mejoraEsperada) { this.mejoraEsperada = mejoraEsperada; }
        
        public List<String> getFactoresClave() { return factoresClave; }
        public void setFactoresClave(List<String> factoresClave) { this.factoresClave = factoresClave; }
    }
} 