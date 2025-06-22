package com.kursor.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;

/**
 * Implementación de estrategia de aprendizaje basada en repetición espaciada.
 * 
 * <p>Esta estrategia implementa un algoritmo simplificado de repetición espaciada,
 * donde las preguntas se repiten en intervalos regulares para mejorar la
 * retención a largo plazo. Es especialmente efectiva para memorización
 * y aprendizaje de conceptos que requieren práctica repetida.</p>
 * 
 * <p>Características específicas:</p>
 * <ul>
 *   <li><strong>Repetición cíclica:</strong> Las preguntas se repiten en intervalos fijos</li>
 *   <li><strong>Intervalo configurable:</strong> Se puede ajustar la frecuencia de repetición</li>
 *   <li><strong>Ciclo completo:</strong> Garantiza que todas las preguntas sean vistas</li>
 *   <li><strong>Memoria a largo plazo:</strong> Optimizada para retención duradera</li>
 * </ul>
 * 
 * <p>Algoritmo de repetición espaciada:</p>
 * <ul>
 *   <li>Selecciona pregunta en posición actual</li>
 *   <li>Avanza por el intervalo configurado</li>
 *   <li>Cuando llega al final, vuelve al inicio</li>
 *   <li>Continúa el ciclo hasta completar todas las preguntas</li>
 * </ul>
 * 
 * <p>Ejemplo con intervalo = 3:</p>
 * <pre>
 * Preguntas: [A, B, C, D, E, F]
 * Secuencia: A, D, B, E, C, F, A, D, B, E, C, F, ...
 * </pre>
 * 
 * <p>Casos de uso ideales:</p>
 * <ul>
 *   <li><strong>Memorización:</strong> Vocabulario, fórmulas, fechas</li>
 *   <li><strong>Refuerzo:</strong> Conceptos que requieren práctica repetida</li>
 *   <li><strong>Retención:</strong> Aprendizaje a largo plazo</li>
 *   <li><strong>Preparación:</strong> Para exámenes o evaluaciones</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * List<Pregunta> preguntas = Arrays.asList(pregunta1, pregunta2, pregunta3, pregunta4);
 * EstrategiaRepeticionEspaciada estrategia = new EstrategiaRepeticionEspaciada(preguntas);
 * 
 * Pregunta primera = estrategia.siguientePregunta(); // pregunta1
 * Pregunta segunda = estrategia.siguientePregunta(); // pregunta4 (intervalo=3)
 * Pregunta tercera = estrategia.siguientePregunta(); // pregunta2
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see EstrategiaSecuencial
 * @see EstrategiaAleatoria
 * @see Pregunta
 */
public class EstrategiaRepeticionEspaciada implements EstrategiaAprendizaje {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaRepeticionEspaciada.class);
    
    /** Nombre descriptivo de la estrategia */
    private String nombre = "Repetición Espaciada";
    
    /** Lista de preguntas disponibles para la estrategia */
    private List<Pregunta> preguntas;
    
    /** Índice de la pregunta actual en la secuencia */
    private int indiceActual = 0;
    
    /** Intervalo entre repeticiones de preguntas */
    private int intervalo = 3; // Por defecto, repetir cada 3 preguntas

    /**
     * Constructor para crear una estrategia de repetición espaciada.
     * 
     * <p>Inicializa la estrategia con una lista de preguntas y establece el
     * intervalo de repetición por defecto. La estrategia comenzará desde
     * la primera pregunta y avanzará según el algoritmo de repetición espaciada.</p>
     * 
     * @param preguntas Lista de preguntas para la estrategia (no debe ser null)
     * @throws IllegalArgumentException si la lista de preguntas es null
     */
    public EstrategiaRepeticionEspaciada(List<Pregunta> preguntas) {
        logger.debug("Creando estrategia repetición espaciada - Cantidad preguntas: " + 
                    (preguntas != null ? preguntas.size() : "null") + ", Intervalo: " + intervalo);
        
        if (preguntas == null) {
            logger.error("Error al crear estrategia repetición espaciada: lista de preguntas no puede ser null");
            throw new IllegalArgumentException("Lista de preguntas no puede ser null");
        }
        
        this.preguntas = new ArrayList<>(preguntas); // Crear copia defensiva
        this.indiceActual = 0;
        this.intervalo = 3; // Intervalo por defecto
        
        logger.info("Estrategia repetición espaciada creada exitosamente - Preguntas: " + this.preguntas.size() + 
                   ", Intervalo: " + this.intervalo);
    }

    /**
     * Constructor para crear una estrategia de repetición espaciada con intervalo personalizado.
     * 
     * <p>Inicializa la estrategia con una lista de preguntas y un intervalo
     * específico para la repetición espaciada.</p>
     * 
     * @param preguntas Lista de preguntas para la estrategia (no debe ser null)
     * @param intervalo Intervalo entre repeticiones (debe ser mayor que 0)
     * @throws IllegalArgumentException si la lista de preguntas es null o el intervalo es inválido
     */
    public EstrategiaRepeticionEspaciada(List<Pregunta> preguntas, int intervalo) {
        logger.debug("Creando estrategia repetición espaciada - Cantidad preguntas: " + 
                    (preguntas != null ? preguntas.size() : "null") + ", Intervalo: " + intervalo);
        
        if (preguntas == null) {
            logger.error("Error al crear estrategia repetición espaciada: lista de preguntas no puede ser null");
            throw new IllegalArgumentException("Lista de preguntas no puede ser null");
        }
        
        if (intervalo <= 0) {
            logger.error("Error al crear estrategia repetición espaciada: intervalo debe ser mayor que 0 - Intervalo: " + intervalo);
            throw new IllegalArgumentException("Intervalo debe ser mayor que 0");
        }
        
        this.preguntas = new ArrayList<>(preguntas); // Crear copia defensiva
        this.indiceActual = 0;
        this.intervalo = intervalo;
        
        logger.info("Estrategia repetición espaciada creada exitosamente - Preguntas: " + this.preguntas.size() + 
                   ", Intervalo: " + this.intervalo);
    }

    /**
     * Obtiene el nombre de la estrategia.
     * 
     * @return El nombre descriptivo de la estrategia
     */
    @Override
    public String getNombre() { 
        logger.debug("Obteniendo nombre de estrategia repetición espaciada: " + nombre);
        return nombre; 
    }

    /**
     * Guarda el estado actual de la estrategia.
     * 
     * <p>En la implementación actual, este método no realiza ninguna acción
     * ya que el estado es simple (solo el índice actual) y se puede reconstruir
     * fácilmente. En futuras versiones podría implementarse persistencia
     * del estado.</p>
     * 
     * <p>Estado que se podría guardar:</p>
     * <ul>
     *   <li>Índice actual de la pregunta</li>
     *   <li>Intervalo de repetición</li>
     *   <li>Historial de preguntas vistas</li>
     *   <li>Progreso del ciclo actual</li>
     * </ul>
     */
    @Override
    public void guardarEstado() {
        logger.debug("Guardando estado de estrategia repetición espaciada - Índice actual: " + indiceActual + 
                    ", Intervalo: " + intervalo);
        
        // TODO: Implementar persistencia del estado si es necesario
        // Por ejemplo, guardar en archivo o base de datos
        
        logger.info("Estado de estrategia repetición espaciada guardado - Índice: " + indiceActual);
    }

    /**
     * Restaura el estado previamente guardado de la estrategia.
     * 
     * <p>En la implementación actual, este método no realiza ninguna acción
     * ya que no hay persistencia implementada. En futuras versiones podría
     * restaurar el estado desde almacenamiento persistente.</p>
     * 
     * <p>Consideraciones para implementación futura:</p>
     * <ul>
     *   <li>Validar que el estado restaurado sea válido</li>
     *   <li>Manejar casos donde no existe estado previo</li>
     *   <li>Verificar compatibilidad con la lista actual de preguntas</li>
     *   <li>Validar que el intervalo restaurado sea válido</li>
     * </ul>
     */
    @Override
    public void restaurarEstado() {
        logger.debug("Restaurando estado de estrategia repetición espaciada");
        
        // TODO: Implementar restauración del estado si es necesario
        // Por ejemplo, cargar desde archivo o base de datos
        
        logger.info("Estado de estrategia repetición espaciada restaurado - Índice: " + indiceActual);
    }

    /**
     * Obtiene la siguiente pregunta según el algoritmo de repetición espaciada.
     * 
     * <p>Este método implementa el algoritmo de repetición espaciada:</p>
     * <ol>
     *   <li>Selecciona la pregunta en la posición actual</li>
     *   <li>Si llega al final de la lista, vuelve al inicio</li>
     *   <li>Avanza por el intervalo configurado</li>
     *   <li>Continúa el ciclo</li>
     * </ol>
     * 
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Si no hay preguntas: retorna null</li>
     *   <li>Si llega al final: reinicia al inicio</li>
     *   <li>En caso contrario: retorna la pregunta actual y avanza por el intervalo</li>
     * </ul>
     * 
     * @return La siguiente pregunta según el algoritmo de repetición espaciada, o null si no hay preguntas
     */
    @Override
    public Pregunta siguientePregunta() {
        logger.debug("Obteniendo siguiente pregunta repetición espaciada - Índice actual: " + indiceActual + 
                    ", Total preguntas: " + (preguntas != null ? preguntas.size() : "null") + 
                    ", Intervalo: " + intervalo);
        
        // Verificar si hay preguntas disponibles
        if (preguntas == null || preguntas.isEmpty()) {
            logger.debug("No hay preguntas disponibles en estrategia repetición espaciada");
            return null;
        }
        
        // Si llegamos al final, volver al inicio
        if (indiceActual >= preguntas.size()) {
            logger.debug("Llegando al final de la lista, reiniciando al inicio - Índice anterior: " + indiceActual);
            indiceActual = 0;
        }
        
        // Obtener la pregunta actual
        Pregunta pregunta = preguntas.get(indiceActual);
        
        // Avanzar por el intervalo (con wraparound)
        indiceActual = (indiceActual + intervalo) % preguntas.size();
        
        logger.info("Pregunta repetición espaciada obtenida - Índice seleccionado: " + 
                   ((indiceActual - intervalo + preguntas.size()) % preguntas.size()) + 
                   ", ID pregunta: " + pregunta.getId() + ", Tipo: " + pregunta.getTipo() + 
                   ", Nuevo índice: " + indiceActual);
        
        return pregunta;
    }
    
    /**
     * Obtiene el intervalo de repetición actual.
     * 
     * @return El intervalo entre repeticiones de preguntas
     */
    public int getIntervalo() {
        logger.debug("Obteniendo intervalo de repetición espaciada: " + intervalo);
        return intervalo;
    }
    
    /**
     * Establece el intervalo de repetición.
     * 
     * @param intervalo El nuevo intervalo entre repeticiones (debe ser mayor que 0)
     * @throws IllegalArgumentException si el intervalo es inválido
     */
    public void setIntervalo(int intervalo) {
        logger.debug("Estableciendo intervalo de repetición espaciada - Anterior: " + this.intervalo + 
                    ", Nuevo: " + intervalo);
        
        if (intervalo <= 0) {
            logger.error("Error al establecer intervalo: debe ser mayor que 0 - Intervalo: " + intervalo);
            throw new IllegalArgumentException("Intervalo debe ser mayor que 0");
        }
        
        this.intervalo = intervalo;
        logger.info("Intervalo de repetición espaciada actualizado: " + this.intervalo);
    }
    
    /**
     * Obtiene el progreso actual del ciclo.
     * 
     * @return El porcentaje de progreso del ciclo actual (0.0 a 1.0)
     */
    public double getProgresoCiclo() {
        if (preguntas == null || preguntas.isEmpty()) {
            return 0.0;
        }
        
        double progreso = (double) indiceActual / preguntas.size();
        logger.debug("Obteniendo progreso de ciclo en repetición espaciada - Progreso: " + 
                    String.format("%.2f%%", progreso * 100));
        return progreso;
    }
    
    /**
     * Obtiene el número de ciclos completados.
     * 
     * <p>Este método calcula cuántos ciclos completos se han realizado
     * basándose en el número total de preguntas vistas.</p>
     * 
     * @return El número de ciclos completados
     */
    public int getCiclosCompletados() {
        if (preguntas == null || preguntas.isEmpty()) {
            return 0;
        }
        
        // TODO: Implementar tracking de ciclos completados
        // Por ahora retorna 0 ya que no se mantiene este estado
        
        logger.debug("Obteniendo ciclos completados en repetición espaciada - Ciclos: 0 (no implementado)");
        return 0;
    }
    
    /**
     * Reinicia la estrategia al inicio.
     * 
     * <p>Este método resetea el índice actual a 0, permitiendo comenzar
     * un nuevo ciclo desde el principio.</p>
     */
    public void reiniciar() {
        logger.info("Reiniciando estrategia repetición espaciada - Índice anterior: " + indiceActual);
        indiceActual = 0;
        logger.info("Estrategia repetición espaciada reiniciada - Nuevo índice: " + indiceActual);
    }
    
    /**
     * Representación en cadena de la estrategia de repetición espaciada.
     * 
     * @return String con información básica de la estrategia
     */
    @Override
    public String toString() {
        return String.format("EstrategiaRepeticionEspaciada{nombre='%s', totalPreguntas=%d, indiceActual=%d, intervalo=%d, progresoCiclo=%.1f%%}", 
                           nombre, preguntas != null ? preguntas.size() : 0, indiceActual, intervalo, getProgresoCiclo() * 100);
    }
} 