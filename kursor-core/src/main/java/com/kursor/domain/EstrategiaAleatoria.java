package com.kursor.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementación de estrategia de aprendizaje aleatoria.
 * 
 * <p>Esta estrategia selecciona preguntas de forma completamente aleatoria,
 * sin considerar el orden o el historial previo. Es útil para evitar el
 * sesgo del orden y para hacer el aprendizaje más dinámico e impredecible.</p>
 * 
 * <p>Características específicas:</p>
 * <ul>
 *   <li><strong>Selección aleatoria:</strong> Cada pregunta se selecciona al azar</li>
 *   <li><strong>Sin memoria:</strong> No recuerda preguntas ya vistas</li>
 *   <li><strong>Impredecible:</strong> Cada selección es independiente</li>
 *   <li><strong>Variedad:</strong> Evita patrones repetitivos</li>
 * </ul>
 * 
 * <p>Casos de uso ideales:</p>
 * <ul>
 *   <li><strong>Repaso general:</strong> Cuando el orden no importa</li>
 *   <li><strong>Evaluación:</strong> Para evitar memorización por posición</li>
 *   <li><strong>Práctica:</strong> Para hacer el estudio más dinámico</li>
 *   <li><strong>Variedad:</strong> Para evitar aburrimiento</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * List<Pregunta> preguntas = Arrays.asList(pregunta1, pregunta2, pregunta3);
 * EstrategiaAleatoria estrategia = new EstrategiaAleatoria(preguntas);
 * 
 * Pregunta primera = estrategia.siguientePregunta(); // Cualquiera de las 3
 * Pregunta segunda = estrategia.siguientePregunta(); // Cualquiera de las 3
 * Pregunta tercera = estrategia.siguientePregunta(); // Cualquiera de las 3
 * }</pre>
 * 
 * <p>Consideraciones importantes:</p>
 * <ul>
 *   <li><strong>Repetición posible:</strong> La misma pregunta puede aparecer múltiples veces</li>
 *   <li><strong>Sin progreso:</strong> No hay garantía de ver todas las preguntas</li>
 *   <li><strong>Aleatoriedad verdadera:</strong> Usa java.util.Random para generación</li>
 *   <li><strong>Sin estado:</strong> No mantiene información de preguntas vistas</li>
 * </ul>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see EstrategiaSecuencial
 * @see EstrategiaRepeticionEspaciada
 * @see Pregunta
 * @see Random
 */
public class EstrategiaAleatoria implements EstrategiaAprendizaje {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaAleatoria.class);
    
    /** Nombre descriptivo de la estrategia */
    private String nombre = "Aleatoria";
    
    /** Lista de preguntas disponibles para la estrategia */
    private List<Pregunta> preguntas;
    
    /** Generador de números aleatorios para selección */
    private Random random = new Random();

    /**
     * Constructor para crear una estrategia aleatoria.
     * 
     * <p>Inicializa la estrategia con una lista de preguntas y crea un
     * generador de números aleatorios. La estrategia seleccionará preguntas
     * de forma completamente aleatoria en cada llamada.</p>
     * 
     * @param preguntas Lista de preguntas para la estrategia (no debe ser null)
     * @throws IllegalArgumentException si la lista de preguntas es null
     */
    public EstrategiaAleatoria(List<Pregunta> preguntas) {
        logger.debug("Creando estrategia aleatoria - Cantidad preguntas: " + 
                    (preguntas != null ? preguntas.size() : "null"));
        
        if (preguntas == null) {
            logger.error("Error al crear estrategia aleatoria: lista de preguntas no puede ser null");
            throw new IllegalArgumentException("Lista de preguntas no puede ser null");
        }
        
        this.preguntas = new ArrayList<>(preguntas); // Crear copia defensiva
        this.random = new Random();
        
        logger.info("Estrategia aleatoria creada exitosamente - Preguntas: " + this.preguntas.size());
    }

    /**
     * Obtiene el nombre de la estrategia.
     * 
     * @return El nombre descriptivo de la estrategia
     */
    @Override
    public String getNombre() { 
        logger.debug("Obteniendo nombre de estrategia aleatoria: " + nombre);
        return nombre; 
    }

    /**
     * Guarda el estado actual de la estrategia.
     * 
     * <p>En la implementación actual, este método no realiza ninguna acción
     * ya que la estrategia aleatoria no mantiene estado significativo que
     * necesite ser persistido. Cada selección es independiente.</p>
     * 
     * <p>Nota:</p>
     * <ul>
     *   <li>La estrategia aleatoria no mantiene estado de progreso</li>
     *   <li>Cada llamada a siguientePregunta() es independiente</li>
     *   <li>No hay necesidad de persistir estado</li>
     * </ul>
     */
    @Override
    public void guardarEstado() {
        logger.debug("Guardando estado de estrategia aleatoria (no hay estado que guardar)");
        
        // La estrategia aleatoria no mantiene estado significativo
        // Cada selección es independiente y no requiere persistencia
        
        logger.info("Estado de estrategia aleatoria guardado (sin estado)");
    }

    /**
     * Restaura el estado previamente guardado de la estrategia.
     * 
     * <p>En la implementación actual, este método no realiza ninguna acción
     * ya que la estrategia aleatoria no mantiene estado que necesite ser
     * restaurado. La estrategia funciona igual independientemente del estado.</p>
     * 
     * <p>Nota:</p>
     * <ul>
     *   <li>No hay estado previo que restaurar</li>
     *   <li>La estrategia funciona igual desde cualquier punto</li>
     *   <li>No hay progreso o memoria que mantener</li>
     * </ul>
     */
    @Override
    public void restaurarEstado() {
        logger.debug("Restaurando estado de estrategia aleatoria (no hay estado que restaurar)");
        
        // La estrategia aleatoria no mantiene estado significativo
        // No hay necesidad de restaurar nada
        
        logger.info("Estado de estrategia aleatoria restaurado (sin estado)");
    }

    /**
     * Obtiene una pregunta seleccionada aleatoriamente.
     * 
     * <p>Este método selecciona una pregunta de forma completamente aleatoria
     * de la lista disponible. Cada llamada es independiente y puede retornar
     * la misma pregunta múltiples veces.</p>
     * 
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Si no hay preguntas: retorna null</li>
     *   <li>En caso contrario: retorna una pregunta seleccionada aleatoriamente</li>
     *   <li>Puede retornar la misma pregunta en llamadas consecutivas</li>
     *   <li>No hay garantía de ver todas las preguntas</li>
     * </ul>
     * 
     * <p>Algoritmo de selección:</p>
     * <ul>
     *   <li>Genera un índice aleatorio entre 0 y (tamaño - 1)</li>
     *   <li>Retorna la pregunta en esa posición</li>
     *   <li>Usa java.util.Random para generación de números aleatorios</li>
     * </ul>
     * 
     * @return Una pregunta seleccionada aleatoriamente, o null si no hay preguntas
     */
    @Override
    public Pregunta siguientePregunta() {
        logger.debug("Obteniendo siguiente pregunta aleatoria - Total preguntas: " + 
                    (preguntas != null ? preguntas.size() : "null"));
        
        // Verificar si hay preguntas disponibles
        if (preguntas == null || preguntas.isEmpty()) {
            logger.debug("No hay preguntas disponibles en estrategia aleatoria");
            return null;
        }
        
        // Seleccionar índice aleatorio
        int indiceAleatorio = random.nextInt(preguntas.size());
        Pregunta pregunta = preguntas.get(indiceAleatorio);
        
        logger.info("Pregunta aleatoria seleccionada - Índice: " + indiceAleatorio + 
                   ", ID pregunta: " + pregunta.getId() + ", Tipo: " + pregunta.getTipo());
        
        return pregunta;
    }
    
    /**
     * Obtiene el número total de preguntas disponibles.
     * 
     * @return El número de preguntas en la estrategia
     */
    public int getNumeroPreguntas() {
        int numeroPreguntas = preguntas != null ? preguntas.size() : 0;
        logger.debug("Obteniendo número de preguntas en estrategia aleatoria - Cantidad: " + numeroPreguntas);
        return numeroPreguntas;
    }
    
    /**
     * Verifica si la estrategia tiene preguntas disponibles.
     * 
     * @return true si hay preguntas disponibles, false en caso contrario
     */
    public boolean tienePreguntas() {
        boolean tienePreguntas = preguntas != null && !preguntas.isEmpty();
        logger.debug("Verificando si estrategia aleatoria tiene preguntas - Tiene preguntas: " + tienePreguntas);
        return tienePreguntas;
    }
    
    /**
     * Obtiene una lista con todas las preguntas disponibles.
     * 
     * <p>Este método retorna una copia de la lista de preguntas para evitar
     * modificaciones externas accidentales.</p>
     * 
     * @return Lista inmutable con todas las preguntas disponibles
     */
    public List<Pregunta> getTodasLasPreguntas() {
        logger.debug("Obteniendo todas las preguntas de estrategia aleatoria - Cantidad: " + 
                    (preguntas != null ? preguntas.size() : 0));
        return new ArrayList<>(preguntas);
    }
    
    /**
     * Establece una semilla para el generador de números aleatorios.
     * 
     * <p>Este método permite establecer una semilla específica para el
     * generador de números aleatorios, lo que hace que la secuencia
     * de selecciones sea reproducible para propósitos de testing.</p>
     * 
     * @param semilla La semilla para el generador de números aleatorios
     */
    public void setSemilla(long semilla) {
        logger.info("Estableciendo semilla para estrategia aleatoria - Semilla: " + semilla);
        this.random = new Random(semilla);
        logger.info("Semilla establecida para estrategia aleatoria");
    }
    
    /**
     * Representación en cadena de la estrategia aleatoria.
     * 
     * @return String con información básica de la estrategia
     */
    @Override
    public String toString() {
        return String.format("EstrategiaAleatoria{nombre='%s', totalPreguntas=%d, tienePreguntas=%s}", 
                           nombre, preguntas != null ? preguntas.size() : 0, tienePreguntas());
    }
} 