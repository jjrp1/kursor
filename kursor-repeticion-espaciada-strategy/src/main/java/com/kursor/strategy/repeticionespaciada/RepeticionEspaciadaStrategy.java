package com.kursor.strategy.repeticionespaciada;

import com.kursor.domain.EstrategiaAprendizaje;
import com.kursor.domain.Pregunta;
import com.kursor.domain.Respuesta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * RepeticionEspaciadaStrategy estrategia = new RepeticionEspaciadaStrategy(listaPreguntas);
 * Pregunta primera = estrategia.primeraPregunta();
 * while (estrategia.hayMasPreguntas()) {
 *     Respuesta respuesta = ...;
 *     estrategia.registrarRespuesta(respuesta);
 *     Pregunta siguiente = estrategia.siguientePregunta();
 * }
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 1.0.0
 */
public class RepeticionEspaciadaStrategy implements EstrategiaAprendizaje {
    
    private static final Logger logger = LoggerFactory.getLogger(RepeticionEspaciadaStrategy.class);
    
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
     * 
     * <p>Contiene la pregunta, el tiempo en que debe ser presentada y su prioridad
     * para determinar el orden de presentación.</p>
     */
    private static class PreguntaProgramada {
        final Pregunta pregunta;
        final long tiempoProgramado;
        final int prioridad;
        
        /**
         * Constructor de pregunta programada.
         * 
         * @param pregunta La pregunta a programar
         * @param tiempoProgramado Timestamp cuando debe ser presentada
         * @param prioridad Prioridad de la pregunta (mayor = más urgente)
         */
        PreguntaProgramada(Pregunta pregunta, long tiempoProgramado, int prioridad) {
            this.pregunta = pregunta;
            this.tiempoProgramado = tiempoProgramado;
            this.prioridad = prioridad;
        }
    }
    
    /**
     * Clase interna que mantiene el estado de repetición espaciada de una pregunta.
     * 
     * <p>Implementa el algoritmo SuperMemo 2 para calcular intervalos y factores de facilidad
     * basándose en el historial de respuestas del usuario.</p>
     */
    public static class EstadoPregunta {
        int repeticiones = 0;           // Número de repeticiones
        int intervalo = 1;              // Intervalo actual en días
        double factorFacilidad = 2.5;   // Factor de facilidad (EF)
        long ultimaRepeticion = 0;      // Timestamp de la última repetición
        int calidadUltimaRespuesta = -1; // Calidad de la última respuesta (0-5)
        
        /**
         * Constructor por defecto.
         */
        EstadoPregunta() {
            logger.debug("[EstadoPregunta] Creando nuevo estado de pregunta");
        }
        
        /**
         * Actualiza el estado basándose en la calidad de la respuesta.
         * 
         * <p>Implementa la lógica del algoritmo SuperMemo 2 para ajustar intervalos
         * y factores de facilidad según la calidad de la respuesta del usuario.</p>
         * 
         * @param calidad Calidad de la respuesta (0-5)
         */
        void actualizarEstado(int calidad) {
            logger.debug("[EstadoPregunta] Actualizando estado con calidad: {}", calidad);
            this.calidadUltimaRespuesta = calidad;
            this.ultimaRepeticion = System.currentTimeMillis();
            
            if (calidad >= 3) {
                // Respuesta correcta - aumentar intervalo
                logger.debug("[EstadoPregunta] Respuesta correcta (calidad >= 3). Repeticiones actuales: {}", repeticiones);
                this.repeticiones++;
                if (this.repeticiones == 1) {
                    this.intervalo = 1; // Primera repetición: 1 día
                    logger.debug("[EstadoPregunta] Primera repetición - intervalo establecido a 1 día");
                } else if (this.repeticiones == 2) {
                    this.intervalo = 6; // Segunda repetición: 6 días
                    logger.debug("[EstadoPregunta] Segunda repetición - intervalo establecido a 6 días");
                } else {
                    // Repeticiones posteriores: intervalo * factor de facilidad
                    int intervaloAnterior = this.intervalo;
                    this.intervalo = (int) Math.round(this.intervalo * this.factorFacilidad);
                    logger.debug("[EstadoPregunta] Repetición {} - intervalo actualizado de {} a {} días", 
                                repeticiones, intervaloAnterior, intervalo);
                }
                
                // Actualizar factor de facilidad
                actualizarFactorFacilidad(calidad);
            } else {
                // Respuesta incorrecta - resetear a intervalo corto
                logger.debug("[EstadoPregunta] Respuesta incorrecta (calidad < 3). Reseteando estado");
                this.repeticiones = 0;
                this.intervalo = 1;
                double factorAnterior = this.factorFacilidad;
                this.factorFacilidad = Math.max(1.3, this.factorFacilidad - 0.2);
                logger.debug("[EstadoPregunta] Factor de facilidad reducido de {} a {}", factorAnterior, factorFacilidad);
            }
        }
        
        /**
         * Actualiza el factor de facilidad según la calidad de la respuesta.
         * 
         * <p>Implementa la fórmula del algoritmo SuperMemo 2 para calcular el nuevo
         * factor de facilidad basándose en la calidad de la respuesta actual.</p>
         * 
         * @param calidad Calidad de la respuesta (0-5)
         */
        private void actualizarFactorFacilidad(int calidad) {
            double factorAnterior = this.factorFacilidad;
            // Fórmula del algoritmo SuperMemo 2
            double q = calidad;
            double newEF = this.factorFacilidad + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));
            
            // El factor de facilidad no puede ser menor que 1.3
            this.factorFacilidad = Math.max(1.3, newEF);
            logger.debug("[EstadoPregunta] Factor de facilidad actualizado de {} a {} (calidad: {})", 
                        factorAnterior, factorFacilidad, calidad);
        }
        
        /**
         * Calcula el tiempo de la próxima repetición.
         * 
         * @return Timestamp de la próxima repetición
         */
        long calcularProximaRepeticion() {
            // Convertir días a milisegundos (aproximadamente)
            long milisegundosPorDia = 24 * 60 * 60 * 1000L;
            long proximaRepeticion = this.ultimaRepeticion + (this.intervalo * milisegundosPorDia);
            logger.debug("[EstadoPregunta] Próxima repetición calculada: {} (intervalo: {} días)", 
                        proximaRepeticion, intervalo);
            return proximaRepeticion;
        }
    }
    
    /**
     * Constructor de la estrategia de repetición espaciada.
     * 
     * <p>Inicializa la estrategia con la lista de preguntas proporcionada,
     * creando estados iniciales para cada pregunta y programándolas para
     * su primera repetición.</p>
     * 
     * @param preguntas Lista de preguntas para la estrategia
     * @throws IllegalArgumentException Si la lista es nula o vacía
     */
    public RepeticionEspaciadaStrategy(List<Pregunta> preguntas) {
        logger.debug("[RepeticionEspaciadaStrategy] Inicializando con {} preguntas", 
                    preguntas != null ? preguntas.size() : 0);
        
        if (preguntas == null || preguntas.isEmpty()) {
            logger.error("[RepeticionEspaciadaStrategy] La lista de preguntas no puede ser nula ni vacía");
            throw new IllegalArgumentException("La lista de preguntas no puede ser nula ni vacía");
        }
        
        this.preguntas = new ArrayList<>(preguntas);
        this.estadosPreguntas = new HashMap<>();
        this.preguntasProcesadas = 0;
        
        // Inicializar estados de preguntas
        logger.debug("[RepeticionEspaciadaStrategy] Inicializando estados para {} preguntas", this.preguntas.size());
        for (Pregunta pregunta : this.preguntas) {
            this.estadosPreguntas.put(pregunta.getId(), new EstadoPregunta());
        }
        
        // Crear cola de prioridad ordenada por tiempo de programación
        this.colaPreguntas = new PriorityQueue<>(
            Comparator.comparingLong(p -> p.tiempoProgramado)
        );
        
        // Programar todas las preguntas para la primera vez
        programarPreguntasIniciales();
        logger.debug("[RepeticionEspaciadaStrategy] Estrategia inicializada correctamente");
    }
    
    /**
     * Programa todas las preguntas para su primera repetición.
     * 
     * <p>Establece el tiempo de programación inicial para todas las preguntas
     * basándose en sus intervalos iniciales.</p>
     */
    private void programarPreguntasIniciales() {
        logger.debug("[RepeticionEspaciadaStrategy] Programando preguntas iniciales");
        long tiempoActual = System.currentTimeMillis();
        for (Pregunta pregunta : this.preguntas) {
            EstadoPregunta estado = this.estadosPreguntas.get(pregunta.getId());
            long tiempoProgramado = tiempoActual + (estado.intervalo * 24 * 60 * 60 * 1000L);
            this.colaPreguntas.offer(new PreguntaProgramada(pregunta, tiempoProgramado, 0));
            logger.debug("[RepeticionEspaciadaStrategy] Pregunta {} programada para: {}", 
                        pregunta.getId(), tiempoProgramado);
        }
        this.totalPreguntasSesion = this.preguntas.size();
        logger.debug("[RepeticionEspaciadaStrategy] {} preguntas programadas inicialmente", totalPreguntasSesion);
    }
    
    @Override
    public String getNombre() {
        logger.debug("[RepeticionEspaciadaStrategy] getNombre llamado");
        return "Repetición Espaciada";
    }
    
    @Override
    public Pregunta primeraPregunta() {
        logger.debug("[RepeticionEspaciadaStrategy] primeraPregunta llamado. Reiniciando contador de preguntas");
        this.preguntasProcesadas = 0;
        return siguientePregunta();
    }
    
    @Override
    public void registrarRespuesta(Respuesta respuesta) {
        logger.debug("[RepeticionEspaciadaStrategy] registrarRespuesta llamado. Respuesta: {}", respuesta);
        
        if (this.preguntaActual == null) {
            logger.warn("[RepeticionEspaciadaStrategy] No hay pregunta actual para registrar respuesta");
            return;
        }
        
        EstadoPregunta estado = this.estadosPreguntas.get(this.preguntaActual.getId());
        if (estado == null) {
            logger.error("[RepeticionEspaciadaStrategy] No se encontró estado para pregunta: {}", preguntaActual.getId());
            return;
        }
        
        // Convertir respuesta a calidad (0-5)
        int calidad = convertirRespuestaACalidad(respuesta);
        logger.debug("[RepeticionEspaciadaStrategy] Calidad calculada: {} para pregunta: {}", 
                    calidad, preguntaActual.getId());
        
        // Actualizar estado de la pregunta
        estado.actualizarEstado(calidad);
        
        // Reprogramar la pregunta para la próxima repetición
        long proximaRepeticion = estado.calcularProximaRepeticion();
        int prioridad = calcularPrioridad(estado, calidad);
        
        PreguntaProgramada nuevaProgramacion = new PreguntaProgramada(
            this.preguntaActual, proximaRepeticion, prioridad
        );
        this.colaPreguntas.offer(nuevaProgramacion);
        
        logger.debug("[RepeticionEspaciadaStrategy] Pregunta {} reprogramada para: {} con prioridad: {}", 
                    preguntaActual.getId(), proximaRepeticion, prioridad);
        
        this.preguntasProcesadas++;
    }
    
    /**
     * Convierte una respuesta a un valor de calidad (0-5).
     * 
     * <p>Mapea las respuestas del usuario a valores de calidad según el algoritmo SuperMemo 2:</p>
     * <ul>
     *   <li>5: Perfecto - respuesta inmediata y correcta</li>
     *   <li>4: Correcto con dudas - respuesta correcta pero con alguna duda</li>
     *   <li>3: Correcto con dificultad - respuesta correcta pero con mucha dificultad</li>
     *   <li>2: Incorrecto pero recordado - respuesta incorrecta pero el usuario recuerda la correcta</li>
     *   <li>1: Incorrecto pero casi recordado - respuesta incorrecta pero casi correcta</li>
     *   <li>0: Completamente olvidado - no se recuerda nada</li>
     * </ul>
     * 
     * @param respuesta La respuesta del usuario
     * @return Valor de calidad entre 0 y 5
     */
    private int convertirRespuestaACalidad(Respuesta respuesta) {
        logger.debug("[RepeticionEspaciadaStrategy] Convirtiendo respuesta a calidad: {}", respuesta);
        
        if (respuesta == null) {
            logger.warn("[RepeticionEspaciadaStrategy] Respuesta nula, asignando calidad 0");
            return 0;
        }
        
        // Por ahora, simplificamos: correcta = 4, incorrecta = 1
        // En una implementación completa, esto dependería de la interfaz de usuario
        int calidad = respuesta.esCorrecta() ? 4 : 1;
        logger.debug("[RepeticionEspaciadaStrategy] Calidad calculada: {} (correcta: {})", 
                    calidad, respuesta.esCorrecta());
        return calidad;
    }
    
    /**
     * Calcula la prioridad de una pregunta basándose en su estado y la calidad de la respuesta.
     * 
     * <p>Las preguntas con mayor prioridad se presentan antes en la cola de prioridad.</p>
     * 
     * @param estado Estado actual de la pregunta
     * @param calidad Calidad de la respuesta (0-5)
     * @return Valor de prioridad (mayor = más urgente)
     */
    private int calcularPrioridad(EstadoPregunta estado, int calidad) {
        // Prioridad inversa a la calidad: menor calidad = mayor prioridad
        int prioridad = 5 - calidad;
        logger.debug("[RepeticionEspaciadaStrategy] Prioridad calculada: {} (calidad: {})", prioridad, calidad);
        return prioridad;
    }
    
    @Override
    public boolean hayMasPreguntas() {
        boolean hayMas = !this.colaPreguntas.isEmpty();
        logger.debug("[RepeticionEspaciadaStrategy] hayMasPreguntas llamado. Hay más: {} (cola: {} elementos)", 
                    hayMas, colaPreguntas.size());
        return hayMas;
    }
    
    @Override
    public Pregunta siguientePregunta() {
        logger.debug("[RepeticionEspaciadaStrategy] siguientePregunta llamado");
        
        if (this.colaPreguntas.isEmpty()) {
            logger.debug("[RepeticionEspaciadaStrategy] No hay más preguntas en la cola");
            this.preguntaActual = null;
            return null;
        }
        
        // Obtener la siguiente pregunta de la cola
        PreguntaProgramada programada = this.colaPreguntas.poll();
        this.preguntaActual = programada.pregunta;
        
        logger.debug("[RepeticionEspaciadaStrategy] Siguiente pregunta: {} (programada para: {}, prioridad: {})", 
                    preguntaActual.getId(), programada.tiempoProgramado, programada.prioridad);
        
        return this.preguntaActual;
    }
    
    @Override
    public double getProgreso() {
        double progreso = this.preguntasProcesadas / (double) this.totalPreguntasSesion;
        logger.debug("[RepeticionEspaciadaStrategy] getProgreso llamado. Progreso: {} ({}/{})", 
                    progreso, preguntasProcesadas, totalPreguntasSesion);
        return progreso;
    }
    
    @Override
    public String serializarEstado() {
        logger.debug("[RepeticionEspaciadaStrategy] serializarEstado llamado");
        
        StringBuilder sb = new StringBuilder();
        sb.append(this.preguntasProcesadas).append(";");
        sb.append(this.totalPreguntasSesion).append(";");
        
        // Serializar estados de preguntas
        for (Map.Entry<String, EstadoPregunta> entry : this.estadosPreguntas.entrySet()) {
            String preguntaId = entry.getKey();
            EstadoPregunta estado = entry.getValue();
            
            sb.append(preguntaId).append(",")
              .append(estado.repeticiones).append(",")
              .append(estado.intervalo).append(",")
              .append(estado.factorFacilidad).append(",")
              .append(estado.ultimaRepeticion).append(",")
              .append(estado.calidadUltimaRespuesta).append(";");
        }
        
        String estadoSerializado = sb.toString();
        logger.debug("[RepeticionEspaciadaStrategy] Estado serializado: {} caracteres", estadoSerializado.length());
        return estadoSerializado;
    }
    
    @Override
    public void deserializarEstado(String estado) {
        logger.debug("[RepeticionEspaciadaStrategy] deserializarEstado llamado. Estado: {} caracteres", 
                    estado != null ? estado.length() : 0);
        
        if (estado == null || estado.isEmpty()) {
            logger.warn("[RepeticionEspaciadaStrategy] Estado nulo o vacío, usando estado inicial");
            return;
        }
        
        try {
            String[] partes = estado.split(";");
            if (partes.length < 2) {
                logger.error("[RepeticionEspaciadaStrategy] Formato de estado inválido");
                return;
            }
            
            this.preguntasProcesadas = Integer.parseInt(partes[0]);
            this.totalPreguntasSesion = Integer.parseInt(partes[1]);
            
            logger.debug("[RepeticionEspaciadaStrategy] Contadores restaurados: procesadas={}, total={}", 
                        preguntasProcesadas, totalPreguntasSesion);
            
            // Deserializar estados de preguntas
            for (int i = 2; i < partes.length; i++) {
                String[] datosPregunta = partes[i].split(",");
                if (datosPregunta.length >= 6) {
                    String preguntaId = datosPregunta[0];
                    EstadoPregunta estadoPregunta = new EstadoPregunta();
                    estadoPregunta.repeticiones = Integer.parseInt(datosPregunta[1]);
                    estadoPregunta.intervalo = Integer.parseInt(datosPregunta[2]);
                    estadoPregunta.factorFacilidad = Double.parseDouble(datosPregunta[3]);
                    estadoPregunta.ultimaRepeticion = Long.parseLong(datosPregunta[4]);
                    estadoPregunta.calidadUltimaRespuesta = Integer.parseInt(datosPregunta[5]);
                    
                    this.estadosPreguntas.put(preguntaId, estadoPregunta);
                    logger.debug("[RepeticionEspaciadaStrategy] Estado restaurado para pregunta: {}", preguntaId);
                }
            }
            
            // Reconstruir cola de preguntas
            this.colaPreguntas.clear();
            for (Pregunta pregunta : this.preguntas) {
                EstadoPregunta estadoPregunta = this.estadosPreguntas.get(pregunta.getId());
                if (estadoPregunta != null) {
                    long proximaRepeticion = estadoPregunta.calcularProximaRepeticion();
                    this.colaPreguntas.offer(new PreguntaProgramada(pregunta, proximaRepeticion, 0));
                }
            }
            
            logger.debug("[RepeticionEspaciadaStrategy] Estado deserializado correctamente. Cola reconstruida con {} elementos", 
                        colaPreguntas.size());
            
        } catch (Exception e) {
            logger.error("[RepeticionEspaciadaStrategy] Error al deserializar estado: {}", e.getMessage());
        }
    }
    
    // Métodos auxiliares para pruebas y persistencia
    
    /**
     * Obtiene el número de preguntas procesadas en la sesión actual.
     * 
     * @return Número de preguntas procesadas
     */
    public int getPreguntasProcesadas() {
        logger.debug("[RepeticionEspaciadaStrategy] getPreguntasProcesadas llamado: {}", preguntasProcesadas);
        return this.preguntasProcesadas;
    }
    
    /**
     * Obtiene el total de preguntas en la sesión actual.
     * 
     * @return Total de preguntas en la sesión
     */
    public int getTotalPreguntasSesion() {
        logger.debug("[RepeticionEspaciadaStrategy] getTotalPreguntasSesion llamado: {}", totalPreguntasSesion);
        return this.totalPreguntasSesion;
    }
    
    /**
     * Obtiene el total de preguntas en la estrategia.
     * 
     * @return Total de preguntas
     */
    public int getTotalPreguntas() {
        logger.debug("[RepeticionEspaciadaStrategy] getTotalPreguntas llamado: {}", preguntas.size());
        return this.preguntas.size();
    }
    
    /**
     * Obtiene el número de preguntas programadas en la cola.
     * 
     * @return Número de preguntas programadas
     */
    public int getPreguntasProgramadas() {
        logger.debug("[RepeticionEspaciadaStrategy] getPreguntasProgramadas llamado: {}", colaPreguntas.size());
        return this.colaPreguntas.size();
    }
    
    /**
     * Obtiene el estado de una pregunta específica.
     * 
     * @param preguntaId ID de la pregunta
     * @return Estado de la pregunta, o null si no existe
     */
    public EstadoPregunta getEstadoPregunta(String preguntaId) {
        logger.debug("[RepeticionEspaciadaStrategy] getEstadoPregunta llamado para: {}", preguntaId);
        return this.estadosPreguntas.get(preguntaId);
    }
    
    /**
     * Calcula el factor de facilidad promedio de todas las preguntas.
     * 
     * @return Factor de facilidad promedio
     */
    public double getFactorFacilidadPromedio() {
        if (this.estadosPreguntas.isEmpty()) {
            logger.debug("[RepeticionEspaciadaStrategy] getFactorFacilidadPromedio: no hay estados");
            return 2.5;
        }
        
        double suma = this.estadosPreguntas.values().stream()
            .mapToDouble(estado -> estado.factorFacilidad)
            .sum();
        double promedio = suma / this.estadosPreguntas.size();
        
        logger.debug("[RepeticionEspaciadaStrategy] getFactorFacilidadPromedio: {}", promedio);
        return promedio;
    }
}
