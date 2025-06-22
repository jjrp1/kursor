package com.kursor.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;

/**
 * Implementación de estrategia de aprendizaje secuencial.
 * 
 * <p>Esta estrategia recorre las preguntas en orden secuencial, desde la primera
 * hasta la última. Es la estrategia más simple y directa, ideal para contenido
 * que debe aprenderse en un orden específico o para usuarios que prefieren
 * un enfoque estructurado.</p>
 * 
 * <p>Características específicas:</p>
 * <ul>
 *   <li><strong>Orden fijo:</strong> Las preguntas se presentan siempre en el mismo orden</li>
 *   <li><strong>Progreso lineal:</strong> Avance predecible y estructurado</li>
 *   <li><strong>Simplicidad:</strong> Fácil de entender y predecir</li>
 *   <li><strong>Completitud:</strong> Garantiza que todas las preguntas sean vistas</li>
 * </ul>
 * 
 * <p>Casos de uso ideales:</p>
 * <ul>
 *   <li><strong>Contenido secuencial:</strong> Temas que deben aprenderse en orden</li>
 *   <li><strong>Principiantes:</strong> Usuarios que prefieren estructura clara</li>
 *   <li><strong>Repaso completo:</strong> Cuando se quiere ver todo el contenido</li>
 *   <li><strong>Evaluación:</strong> Para tests que requieren orden específico</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * List<Pregunta> preguntas = Arrays.asList(pregunta1, pregunta2, pregunta3);
 * EstrategiaSecuencial estrategia = new EstrategiaSecuencial(preguntas);
 * 
 * Pregunta primera = estrategia.siguientePregunta(); // pregunta1
 * Pregunta segunda = estrategia.siguientePregunta(); // pregunta2
 * Pregunta tercera = estrategia.siguientePregunta(); // pregunta3
 * Pregunta siguiente = estrategia.siguientePregunta(); // null (no hay más)
 * }</pre>
 * 
 * <p>Flujo de la estrategia:</p>
 * <ol>
 *   <li>Inicializar con índice 0</li>
 *   <li>Retornar pregunta en posición actual</li>
 *   <li>Incrementar índice</li>
 *   <li>Repetir hasta llegar al final</li>
 *   <li>Retornar null cuando no hay más preguntas</li>
 * </ol>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see EstrategiaAprendizaje
 * @see EstrategiaAleatoria
 * @see EstrategiaRepeticionEspaciada
 * @see Pregunta
 */
public class EstrategiaSecuencial implements EstrategiaAprendizaje {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(EstrategiaSecuencial.class);
    
    /** Nombre descriptivo de la estrategia */
    private String nombre = "Secuencial";
    
    /** Lista de preguntas disponibles para la estrategia */
    private List<Pregunta> preguntas;
    
    /** Índice de la pregunta actual en la secuencia */
    private int indiceActual = 0;

    /**
     * Constructor para crear una estrategia secuencial.
     * 
     * <p>Inicializa la estrategia con una lista de preguntas y establece el
     * índice inicial en 0. La estrategia comenzará desde la primera pregunta
     * y avanzará secuencialmente.</p>
     * 
     * @param preguntas Lista de preguntas para la estrategia (no debe ser null)
     * @throws IllegalArgumentException si la lista de preguntas es null
     */
    public EstrategiaSecuencial(List<Pregunta> preguntas) {
        logger.debug("Creando estrategia secuencial - Cantidad preguntas: " + 
                    (preguntas != null ? preguntas.size() : "null"));
        
        if (preguntas == null) {
            logger.error("Error al crear estrategia secuencial: lista de preguntas no puede ser null");
            throw new IllegalArgumentException("Lista de preguntas no puede ser null");
        }
        
        this.preguntas = new ArrayList<>(preguntas); // Crear copia defensiva
        this.indiceActual = 0;
        
        logger.info("Estrategia secuencial creada exitosamente - Preguntas: " + this.preguntas.size());
    }

    /**
     * Obtiene el nombre de la estrategia.
     * 
     * @return El nombre descriptivo de la estrategia
     */
    @Override
    public String getNombre() { 
        logger.debug("Obteniendo nombre de estrategia secuencial: " + nombre);
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
     *   <li>Preguntas ya vistas</li>
     *   <li>Progreso del usuario</li>
     * </ul>
     */
    @Override
    public void guardarEstado() {
        logger.debug("Guardando estado de estrategia secuencial - Índice actual: " + indiceActual);
        
        // TODO: Implementar persistencia del estado si es necesario
        // Por ejemplo, guardar en archivo o base de datos
        
        logger.info("Estado de estrategia secuencial guardado - Índice: " + indiceActual);
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
     * </ul>
     */
    @Override
    public void restaurarEstado() {
        logger.debug("Restaurando estado de estrategia secuencial");
        
        // TODO: Implementar restauración del estado si es necesario
        // Por ejemplo, cargar desde archivo o base de datos
        
        logger.info("Estado de estrategia secuencial restaurado - Índice: " + indiceActual);
    }

    /**
     * Obtiene la siguiente pregunta en orden secuencial.
     * 
     * <p>Este método retorna la pregunta en la posición actual del índice
     * y luego incrementa el índice para la siguiente llamada. Cuando se
     * llega al final de la lista, retorna null.</p>
     * 
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Si no hay preguntas: retorna null</li>
     *   <li>Si el índice está al final: retorna null</li>
     *   <li>En caso contrario: retorna la pregunta actual e incrementa el índice</li>
     * </ul>
     * 
     * @return La siguiente pregunta en la secuencia, o null si no hay más preguntas
     */
    @Override
    public Pregunta siguientePregunta() {
        logger.debug("Obteniendo siguiente pregunta secuencial - Índice actual: " + indiceActual + 
                    ", Total preguntas: " + (preguntas != null ? preguntas.size() : "null"));
        
        // Verificar si hay preguntas disponibles
        if (preguntas == null || preguntas.isEmpty()) {
            logger.debug("No hay preguntas disponibles en estrategia secuencial");
            return null;
        }
        
        // Verificar si hemos llegado al final
        if (indiceActual >= preguntas.size()) {
            logger.info("Estrategia secuencial completada - Todas las preguntas vistas");
            return null;
        }
        
        // Obtener la pregunta actual e incrementar el índice
        Pregunta pregunta = preguntas.get(indiceActual);
        indiceActual++;
        
        logger.info("Siguiente pregunta secuencial obtenida - Índice: " + (indiceActual - 1) + 
                   ", ID pregunta: " + pregunta.getId() + ", Tipo: " + pregunta.getTipo());
        
        return pregunta;
    }
    
    /**
     * Obtiene el progreso actual de la estrategia.
     * 
     * @return El porcentaje de progreso (0.0 a 1.0)
     */
    public double getProgreso() {
        if (preguntas == null || preguntas.isEmpty()) {
            return 0.0;
        }
        
        double progreso = (double) indiceActual / preguntas.size();
        logger.debug("Obteniendo progreso de estrategia secuencial - Progreso: " + 
                    String.format("%.2f%%", progreso * 100));
        return progreso;
    }
    
    /**
     * Obtiene el número de preguntas restantes.
     * 
     * @return El número de preguntas que faltan por ver
     */
    public int getPreguntasRestantes() {
        if (preguntas == null) {
            return 0;
        }
        
        int restantes = Math.max(0, preguntas.size() - indiceActual);
        logger.debug("Obteniendo preguntas restantes en estrategia secuencial - Restantes: " + restantes);
        return restantes;
    }
    
    /**
     * Reinicia la estrategia al inicio.
     * 
     * <p>Este método resetea el índice actual a 0, permitiendo comenzar
     * la secuencia desde el principio.</p>
     */
    public void reiniciar() {
        logger.info("Reiniciando estrategia secuencial - Índice anterior: " + indiceActual);
        indiceActual = 0;
        logger.info("Estrategia secuencial reiniciada - Nuevo índice: " + indiceActual);
    }
    
    /**
     * Representación en cadena de la estrategia secuencial.
     * 
     * @return String con información básica de la estrategia
     */
    @Override
    public String toString() {
        return String.format("EstrategiaSecuencial{nombre='%s', totalPreguntas=%d, indiceActual=%d, progreso=%.1f%%}", 
                           nombre, preguntas != null ? preguntas.size() : 0, indiceActual, getProgreso() * 100);
    }
} 