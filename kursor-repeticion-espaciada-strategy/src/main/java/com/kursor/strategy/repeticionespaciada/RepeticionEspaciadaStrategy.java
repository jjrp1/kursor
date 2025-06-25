package com.kursor.strategy.repeticionespaciada;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Estrategia de aprendizaje con repetición espaciada basada en el algoritmo SuperMemo 2.
 * 
 * <p>Esta estrategia implementa el algoritmo de repetición espaciada que optimiza
 * el intervalo entre repeticiones basándose en la dificultad percibida de cada pregunta
 * y el historial de respuestas del usuario.</p>
 * 
 * <p>Características del algoritmo:</p>
 * <ul>
 *   <li><strong>Factor de Facilidad (EF):</strong> Se ajusta según la calidad de las respuestas</li>
 *   <li><strong>Intervalos Crecientes:</strong> Las preguntas fáciles se repiten con mayor espaciado</li>
 *   <li><strong>Repetición Adaptativa:</strong> Las preguntas difíciles se repiten más frecuentemente</li>
 *   <li><strong>Calidad de Respuesta:</strong> 0-5 donde 5 es "perfecto" y 0 es "completamente olvidado"</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class RepeticionEspaciadaStrategy implements EstrategiaAprendizaje {
    
    /** Lista original de preguntas */
    private final List<Pregunta> preguntas;
    
    /** Cola de prioridad para preguntas programadas */
    private final PriorityQueue<PreguntaProgramada> colaPreguntas;
    
    /** Mapa de estado de cada pregunta */
    private final Map<String, EstadoPregunta> estadosPreguntas;
    
    /** Pregunta actual en la sesión */
    private Pregunta preguntaActual;
    
    /** Contador de preguntas procesadas en esta sesión */
    private int preguntasProcesadas;
    
    /** Total de preguntas en la sesión actual */
    private int totalPreguntasSesion;
    
    /**
     * Clase interna que representa una pregunta programada para repetición.
     */
    private static class PreguntaProgramada {
        final Pregunta pregunta;
        final long tiempoProgramado;
        final int prioridad;
        
        PreguntaProgramada(Pregunta pregunta, long tiempoProgramado, int prioridad) {
            this.pregunta = pregunta;
            this.tiempoProgramado = tiempoProgramado;
            this.prioridad = prioridad;
        }
    }
    
    /**
     * Clase interna que mantiene el estado de repetición espaciada de una pregunta.
     */
    public static class EstadoPregunta {
        int repeticiones = 0;           // Número de repeticiones
        int intervalo = 1;              // Intervalo actual en días
        double factorFacilidad = 2.5;   // Factor de facilidad (EF)
        long ultimaRepeticion = 0;      // Timestamp de la última repetición
        int calidadUltimaRespuesta = -1; // Calidad de la última respuesta (0-5)
        
        EstadoPregunta() {}
        
        /**
         * Actualiza el estado basándose en la calidad de la respuesta.
         * 
         * @param calidad Calidad de la respuesta (0-5)
         */
        void actualizarEstado(int calidad) {
            this.calidadUltimaRespuesta = calidad;
            this.ultimaRepeticion = System.currentTimeMillis();
            
            if (calidad >= 3) {
                // Respuesta correcta - aumentar intervalo
                this.repeticiones++;
                if (this.repeticiones == 1) {
                    this.intervalo = 1; // Primera repetición: 1 día
                } else if (this.repeticiones == 2) {
                    this.intervalo = 6; // Segunda repetición: 6 días
                } else {
                    // Repeticiones posteriores: intervalo * factor de facilidad
                    this.intervalo = (int) Math.round(this.intervalo * this.factorFacilidad);
                }
                
                // Actualizar factor de facilidad
                actualizarFactorFacilidad(calidad);
            } else {
                // Respuesta incorrecta - resetear a intervalo corto
                this.repeticiones = 0;
                this.intervalo = 1;
                this.factorFacilidad = Math.max(1.3, this.factorFacilidad - 0.2);
            }
        }
        
        /**
         * Actualiza el factor de facilidad según la calidad de la respuesta.
         * 
         * @param calidad Calidad de la respuesta (0-5)
         */
        private void actualizarFactorFacilidad(int calidad) {
            // Fórmula del algoritmo SuperMemo 2
            double q = calidad;
            double newEF = this.factorFacilidad + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));
            
            // El factor de facilidad no puede ser menor que 1.3
            this.factorFacilidad = Math.max(1.3, newEF);
        }
        
        /**
         * Calcula el tiempo de la próxima repetición.
         * 
         * @return Timestamp de la próxima repetición
         */
        long calcularProximaRepeticion() {
            // Convertir días a milisegundos (aproximadamente)
            long milisegundosPorDia = 24 * 60 * 60 * 1000L;
            return this.ultimaRepeticion + (this.intervalo * milisegundosPorDia);
        }
    }
    
    /**
     * Constructor de la estrategia de repetición espaciada.
     * 
     * @param preguntas Lista de preguntas para la estrategia
     * @throws IllegalArgumentException Si la lista es nula o vacía
     */
    public RepeticionEspaciadaStrategy(List<Pregunta> preguntas) {
        if (preguntas == null || preguntas.isEmpty()) {
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        
        this.preguntas = new ArrayList<>(preguntas);
        this.estadosPreguntas = new HashMap<>();
        this.preguntasProcesadas = 0;
        
        // Inicializar estados de preguntas
        for (Pregunta pregunta : this.preguntas) {
            this.estadosPreguntas.put(pregunta.getId(), new EstadoPregunta());
        }
        
        // Crear cola de prioridad ordenada por tiempo de programación
        this.colaPreguntas = new PriorityQueue<>(
            Comparator.comparingLong(p -> p.tiempoProgramado)
        );
        
        // Programar todas las preguntas para la primera vez
        programarPreguntasIniciales();
    }
    
    /**
     * Programa todas las preguntas para su primera repetición.
     */
    private void programarPreguntasIniciales() {
        long tiempoActual = System.currentTimeMillis();
        for (Pregunta pregunta : this.preguntas) {
            EstadoPregunta estado = this.estadosPreguntas.get(pregunta.getId());
            long tiempoProgramado = tiempoActual + (estado.intervalo * 24 * 60 * 60 * 1000L);
            this.colaPreguntas.offer(new PreguntaProgramada(pregunta, tiempoProgramado, 0));
        }
        this.totalPreguntasSesion = this.preguntas.size();
    }
    
    @Override
    public String getNombre() {
        return "Repetición Espaciada";
    }
    
    @Override
    public Pregunta primeraPregunta() {
        this.preguntasProcesadas = 0;
        return siguientePregunta();
    }
    
    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        if (this.preguntaActual == null) {
            return;
        }
        
        EstadoPregunta estado = this.estadosPreguntas.get(this.preguntaActual.getId());
        if (estado == null) {
            return;
        }
        
        // Convertir respuesta a calidad (0-5)
        int calidad = convertirRespuestaACalidad(respuesta);
        
        // Actualizar estado de la pregunta
        estado.actualizarEstado(calidad);
        
        // Reprogramar la pregunta para su próxima repetición
        long proximaRepeticion = estado.calcularProximaRepeticion();
        int prioridad = calcularPrioridad(estado, calidad);
        
        this.colaPreguntas.offer(new PreguntaProgramada(
            this.preguntaActual, 
            proximaRepeticion, 
            prioridad
        ));
        
        this.preguntasProcesadas++;
    }
    
    /**
     * Convierte una respuesta a una calidad numérica (0-5).
     * 
     * @param respuesta La respuesta a evaluar
     * @return Calidad de la respuesta (0-5)
     */
    private int convertirRespuestaACalidad(Respuesta respuesta) {
        if (respuesta == null) {
            return 0;
        }
        
        if (respuesta.esCorrecta()) {
            // Respuesta correcta: calidad 3-5 basada en tiempo de respuesta
            long tiempoRespuesta = respuesta.getTimestamp();
            if (tiempoRespuesta < 2000) { // Menos de 2 segundos
                return 5; // Perfecto
            } else if (tiempoRespuesta < 5000) { // Menos de 5 segundos
                return 4; // Correcto con dudas
            } else {
                return 3; // Correcto con dificultad
            }
        } else {
            // Respuesta incorrecta: calidad 0-2
            if (respuesta.getTimestamp() < 1000) {
                return 0; // Completamente olvidado
            } else {
                return 1; // Incorrecto pero recordado
            }
        }
    }
    
    /**
     * Calcula la prioridad de una pregunta basándose en su estado y calidad.
     * 
     * @param estado Estado de la pregunta
     * @param calidad Calidad de la última respuesta
     * @return Prioridad de la pregunta (mayor número = mayor prioridad)
     */
    private int calcularPrioridad(EstadoPregunta estado, int calidad) {
        if (calidad < 3) {
            // Preguntas incorrectas tienen alta prioridad
            return 100 - estado.repeticiones;
        } else {
            // Preguntas correctas tienen prioridad basada en intervalo
            return Math.max(1, 50 - estado.intervalo);
        }
    }
    
    @Override
    public boolean hayMasPreguntas() {
        return !this.colaPreguntas.isEmpty();
    }
    
    @Override
    public Pregunta siguientePregunta() {
        if (this.colaPreguntas.isEmpty()) {
            this.preguntaActual = null;
            return null;
        }
        
        // Obtener la siguiente pregunta programada
        PreguntaProgramada programada = this.colaPreguntas.poll();
        this.preguntaActual = programada.pregunta;
        
        return this.preguntaActual;
    }
    
    @Override
    public double getProgreso() {
        if (this.totalPreguntasSesion == 0) {
            return 0.0;
        }
        return (double) this.preguntasProcesadas / this.totalPreguntasSesion;
    }
    
    @Override
    public String serializarEstado() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.preguntasProcesadas).append(";");
        sb.append(this.totalPreguntasSesion).append(";");
        
        // Serializar estados de preguntas
        for (Map.Entry<String, EstadoPregunta> entry : this.estadosPreguntas.entrySet()) {
            EstadoPregunta estado = entry.getValue();
            sb.append(entry.getKey()).append(",")
              .append(estado.repeticiones).append(",")
              .append(estado.intervalo).append(",")
              .append(estado.factorFacilidad).append(",")
              .append(estado.ultimaRepeticion).append(",")
              .append(estado.calidadUltimaRespuesta).append(";");
        }
        
        return sb.toString();
    }
    
    @Override
    public void deserializarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return;
        }
        
        try {
            String[] partes = estado.split(";");
            if (partes.length < 2) {
                return;
            }
            
            this.preguntasProcesadas = Integer.parseInt(partes[0]);
            this.totalPreguntasSesion = Integer.parseInt(partes[1]);
            
            // Deserializar estados de preguntas
            for (int i = 2; i < partes.length; i++) {
                String[] estadoPregunta = partes[i].split(",");
                if (estadoPregunta.length >= 6) {
                    String preguntaId = estadoPregunta[0];
                    EstadoPregunta estadoObj = new EstadoPregunta();
                    estadoObj.repeticiones = Integer.parseInt(estadoPregunta[1]);
                    estadoObj.intervalo = Integer.parseInt(estadoPregunta[2]);
                    estadoObj.factorFacilidad = Double.parseDouble(estadoPregunta[3]);
                    estadoObj.ultimaRepeticion = Long.parseLong(estadoPregunta[4]);
                    estadoObj.calidadUltimaRespuesta = Integer.parseInt(estadoPregunta[5]);
                    
                    this.estadosPreguntas.put(preguntaId, estadoObj);
                }
            }
            
            // Reconstruir cola de preguntas
            this.colaPreguntas.clear();
            for (Pregunta pregunta : this.preguntas) {
                EstadoPregunta estadoPregunta = this.estadosPreguntas.get(pregunta.getId());
                if (estadoPregunta != null) {
                    long proximaRepeticion = estadoPregunta.calcularProximaRepeticion();
                    int prioridad = calcularPrioridad(estadoPregunta, estadoPregunta.calidadUltimaRespuesta);
                    this.colaPreguntas.offer(new PreguntaProgramada(pregunta, proximaRepeticion, prioridad));
                }
            }
            
        } catch (Exception e) {
            // En caso de error, reinicializar
            this.preguntasProcesadas = 0;
            this.totalPreguntasSesion = this.preguntas.size();
        }
    }
    
    // Métodos auxiliares para pruebas y persistencia
    
    /**
     * Obtiene el número de preguntas procesadas en la sesión actual.
     * 
     * @return Número de preguntas procesadas
     */
    public int getPreguntasProcesadas() {
        return this.preguntasProcesadas;
    }
    
    /**
     * Obtiene el total de preguntas en la sesión actual.
     * 
     * @return Total de preguntas en la sesión
     */
    public int getTotalPreguntasSesion() {
        return this.totalPreguntasSesion;
    }
    
    /**
     * Obtiene el número total de preguntas en la estrategia.
     * 
     * @return Total de preguntas
     */
    public int getTotalPreguntas() {
        return this.preguntas.size();
    }
    
    /**
     * Obtiene el número de preguntas programadas para repetición.
     * 
     * @return Número de preguntas en la cola
     */
    public int getPreguntasProgramadas() {
        return this.colaPreguntas.size();
    }
    
    /**
     * Obtiene el estado de una pregunta específica.
     * 
     * @param preguntaId ID de la pregunta
     * @return Estado de la pregunta, o null si no existe
     */
    public EstadoPregunta getEstadoPregunta(String preguntaId) {
        return this.estadosPreguntas.get(preguntaId);
    }
    
    /**
     * Obtiene el factor de facilidad promedio de todas las preguntas.
     * 
     * @return Factor de facilidad promedio
     */
    public double getFactorFacilidadPromedio() {
        if (this.estadosPreguntas.isEmpty()) {
            return 2.5;
        }
        
        double suma = 0.0;
        for (EstadoPregunta estado : this.estadosPreguntas.values()) {
            suma += estado.factorFacilidad;
        }
        
        return suma / this.estadosPreguntas.size();
    }
}
