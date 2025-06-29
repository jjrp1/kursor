package com.kursor.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Representa un bloque de contenido educativo dentro de un curso en el sistema Kursor.
 * 
 * <p>Esta clase modela una unidad de contenido que agrupa preguntas relacionadas
 * bajo un tema común. Cada bloque forma parte de un curso y contiene múltiples
 * preguntas de diferentes tipos.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Identificación única:</strong> Cada bloque tiene un ID único</li>
 *   <li><strong>Metadatos descriptivos:</strong> Título, descripción y tipo</li>
 *   <li><strong>Agrupación de preguntas:</strong> Contiene múltiples preguntas relacionadas</li>
 *   <li><strong>Tipado:</strong> Cada bloque tiene un tipo específico</li>
 *   <li><strong>Gestión de preguntas:</strong> Métodos para agregar y gestionar preguntas</li>
 * </ul>
 * 
 * <p>Tipos de bloques comunes:</p>
 * <ul>
 *   <li><strong>teoria:</strong> Contenido teórico y explicativo</li>
 *   <li><strong>practica:</strong> Ejercicios prácticos</li>
 *   <li><strong>evaluacion:</strong> Preguntas de evaluación</li>
 *   <li><strong>repaso:</strong> Contenido de repaso</li>
 *   <li><strong>introduccion:</strong> Contenido introductorio</li>
 * </ul>
 * 
 * <p>Estructura del bloque:</p>
 * <pre>
 * Bloque
 * ├── Pregunta 1 (Test)
 * ├── Pregunta 2 (True/False)
 * ├── Pregunta 3 (Completar)
 * ├── Flashcard 1
 * └── ...
 * </pre>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Bloque bloque = new Bloque("bloque1", "Números Naturales", 
 *                           "Operaciones básicas con números naturales", "teoria");
 * PreguntaTest pregunta = new PreguntaTest("p1", "¿Cuánto es 2+2?", 
 *                                         Arrays.asList("3", "4", "5"), "4");
 * bloque.addPregunta(pregunta);
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Curso
 * @see Pregunta
 * @see PreguntaTest
 * @see PreguntaTrueFalse
 * @see PreguntaCompletarHuecos
 * @see Flashcard
 */
public class Bloque {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(Bloque.class);
    
    /** Identificador único del bloque */
    private String id;
    
    /** Título descriptivo del bloque */
    private String titulo;
    
    /** Descripción detallada del contenido del bloque */
    private String descripcion;
    
    /** Tipo de contenido del bloque (teoria, practica, evaluacion, etc.) */
    private String tipo;
    
    /** Lista de preguntas que componen el bloque */
    private List<Pregunta> preguntas = new ArrayList<>();

    /**
     * Constructor por defecto para crear un bloque vacío.
     * 
     * <p>Este constructor crea un bloque sin inicializar sus propiedades.
     * Útil para frameworks de serialización o construcción gradual.</p>
     */
    public Bloque() {
        logger.debug("Creando bloque vacío");
    }

    /**
     * Constructor para crear un bloque con metadatos básicos.
     * 
     * <p>Inicializa un bloque con un identificador, título, descripción y tipo.
     * El constructor valida que los parámetros obligatorios sean válidos y
     * registra la creación en el log.</p>
     * 
     * @param id Identificador único del bloque (no debe ser null)
     * @param titulo Título descriptivo del bloque (no debe ser null)
     * @param descripcion Descripción del contenido (puede ser null)
     * @param tipo Tipo de contenido del bloque (puede ser null)
     * @throws IllegalArgumentException si id o título son null o vacíos
     */
    public Bloque(String id, String titulo, String descripcion, String tipo) {
        logger.debug("Creando bloque - ID: " + id + ", Título: " + titulo + ", Tipo: " + tipo);
        
        // Validar ID
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al crear bloque: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID del bloque no puede ser null o vacío");
        }
        
        // Validar título
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("Error al crear bloque: título no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Título del bloque no puede ser null o vacío");
        }
        
        this.id = id.trim();
        this.titulo = titulo.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        this.tipo = tipo != null ? tipo.trim() : null;
        
        logger.info("Bloque creado exitosamente - ID: " + this.id + ", Título: " + this.titulo);
    }

    /**
     * Obtiene el identificador único del bloque.
     * 
     * @return El ID del bloque
     */
    public String getId() { 
        logger.debug("Obteniendo ID de bloque: " + id);
        return id; 
    }
    
    /**
     * Establece el identificador único del bloque.
     * 
     * @param id El nuevo ID del bloque (no debe ser null o vacío)
     * @throws IllegalArgumentException si el ID es null o vacío
     */
    public void setId(String id) { 
        logger.debug("Estableciendo ID de bloque - Anterior: " + this.id + ", Nuevo: " + id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al establecer ID: no puede ser null o vacío");
            throw new IllegalArgumentException("ID del bloque no puede ser null o vacío");
        }
        
        this.id = id.trim();
        logger.info("ID de bloque actualizado: " + this.id);
    }

    /**
     * Obtiene el título del bloque.
     * 
     * @return El título descriptivo del bloque
     */
    public String getTitulo() { 
        logger.debug("Obteniendo título de bloque - ID: " + id);
        return titulo; 
    }
    
    /**
     * Establece el título del bloque.
     * 
     * @param titulo El nuevo título del bloque (no debe ser null o vacío)
     * @throws IllegalArgumentException si el título es null o vacío
     */
    public void setTitulo(String titulo) { 
        logger.debug("Estableciendo título de bloque - ID: " + id + 
                    ", Anterior: " + this.titulo + ", Nuevo: " + titulo);
        
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("Error al establecer título: no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Título del bloque no puede ser null o vacío");
        }
        
        this.titulo = titulo.trim();
        logger.info("Título de bloque actualizado - ID: " + id + ", Nuevo título: " + this.titulo);
    }

    /**
     * Obtiene la descripción del bloque.
     * 
     * @return La descripción del contenido del bloque (puede ser null)
     */
    public String getDescripcion() { 
        logger.debug("Obteniendo descripción de bloque - ID: " + id);
        return descripcion; 
    }
    
    /**
     * Establece la descripción del bloque.
     * 
     * @param descripcion La nueva descripción del bloque (puede ser null)
     */
    public void setDescripcion(String descripcion) { 
        logger.debug("Estableciendo descripción de bloque - ID: " + id + 
                    ", Anterior: " + this.descripcion + ", Nueva: " + descripcion);
        
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        logger.info("Descripción de bloque actualizada - ID: " + id);
    }

    /**
     * Obtiene el tipo del bloque.
     * 
     * @return El tipo de contenido del bloque (puede ser null)
     */
    public String getTipo() { 
        logger.debug("Obteniendo tipo de bloque - ID: " + id + ", Tipo: " + tipo);
        return tipo; 
    }
    
    /**
     * Establece el tipo del bloque.
     * 
     * @param tipo El nuevo tipo de contenido del bloque (puede ser null)
     */
    public void setTipo(String tipo) { 
        logger.debug("Estableciendo tipo de bloque - ID: " + id + 
                    ", Anterior: " + this.tipo + ", Nuevo: " + tipo);
        
        this.tipo = tipo != null ? tipo.trim() : null;
        logger.info("Tipo de bloque actualizado - ID: " + id + ", Nuevo tipo: " + this.tipo);
    }

    /**
     * Obtiene la lista de preguntas del bloque.
     * 
     * <p>Este método retorna una vista inmutable de la lista de preguntas
     * para prevenir modificaciones externas accidentales. Para agregar
     * preguntas, use el método {@link #addPregunta(Pregunta)}.</p>
     * 
     * @return Lista inmutable de preguntas del bloque
     */
    public List<Pregunta> getPreguntas() { 
        logger.debug("Obteniendo preguntas de bloque - ID: " + id + ", Cantidad: " + preguntas.size());
        return Collections.unmodifiableList(preguntas); 
    }
    
    /**
     * Establece la lista completa de preguntas del bloque.
     * 
     * <p>Este método reemplaza completamente la lista de preguntas existente.
     * Se crea una copia defensiva para evitar modificaciones externas.</p>
     * 
     * @param preguntas Nueva lista de preguntas (no debe ser null)
     * @throws IllegalArgumentException si la lista es null
     */
    public void setPreguntas(List<Pregunta> preguntas) { 
        logger.debug("Estableciendo preguntas de bloque - ID: " + id + 
                    ", Cantidad anterior: " + this.preguntas.size() + ", Nueva cantidad: " + 
                    (preguntas != null ? preguntas.size() : "null"));
        
        if (preguntas == null) {
            logger.error("Error al establecer preguntas: lista no puede ser null - ID: " + id);
            throw new IllegalArgumentException("Lista de preguntas no puede ser null");
        }
        
        this.preguntas = new ArrayList<>(preguntas); // Crear copia defensiva
        logger.info("Preguntas de bloque actualizadas - ID: " + id + ", Cantidad: " + preguntas.size());
    }

    /**
     * Agrega una pregunta al bloque.
     * 
     * <p>Este método añade una nueva pregunta al final de la lista de preguntas
     * del bloque. La pregunta se valida antes de ser agregada.</p>
     * 
     * @param pregunta La pregunta a agregar (no debe ser null)
     * @throws IllegalArgumentException si la pregunta es null
     */
    public void addPregunta(Pregunta pregunta) {
        logger.debug("Agregando pregunta a bloque - ID bloque: " + id + 
                    ", Pregunta: " + (pregunta != null ? pregunta.getId() : "null"));
        
        if (pregunta == null) {
            logger.error("Error al agregar pregunta: pregunta no puede ser null - ID bloque: " + id);
            throw new IllegalArgumentException("Pregunta no puede ser null");
        }
        
        this.preguntas.add(pregunta);
        logger.info("Pregunta agregada exitosamente - ID bloque: " + id + 
                   ", ID pregunta: " + pregunta.getId() + ", Tipo: " + pregunta.getTipo() + 
                   ", Total preguntas: " + preguntas.size());
    }
    
    /**
     * Obtiene el número total de preguntas en el bloque.
     * 
     * @return El número de preguntas del bloque
     */
    public int getNumeroPreguntas() {
        int numeroPreguntas = preguntas.size();
        logger.debug("Obteniendo número de preguntas de bloque - ID: " + id + ", Cantidad: " + numeroPreguntas);
        return numeroPreguntas;
    }
    
    /**
     * Verifica si el bloque tiene preguntas.
     * 
     * @return true si el bloque tiene al menos una pregunta, false en caso contrario
     */
    public boolean tienePreguntas() {
        boolean tienePreguntas = !preguntas.isEmpty();
        logger.debug("Verificando si bloque tiene preguntas - ID: " + id + ", Tiene preguntas: " + tienePreguntas);
        return tienePreguntas;
    }
    
    /**
     * Obtiene las preguntas de un tipo específico.
     * 
     * <p>Este método filtra las preguntas del bloque por tipo y retorna
     * una lista con todas las preguntas que coinciden con el tipo especificado.</p>
     * 
     * @param tipo Tipo de pregunta a filtrar (no debe ser null)
     * @return Lista de preguntas del tipo especificado
     * @throws IllegalArgumentException si el tipo es null
     */
    public List<Pregunta> getPreguntasPorTipo(String tipo) {
        logger.debug("Obteniendo preguntas por tipo - ID bloque: " + id + ", Tipo: " + tipo);
        
        if (tipo == null) {
            logger.error("Error al obtener preguntas por tipo: tipo no puede ser null - ID bloque: " + id);
            throw new IllegalArgumentException("Tipo no puede ser null");
        }
        
        List<Pregunta> preguntasFiltradas = preguntas.stream()
                                                    .filter(p -> tipo.equals(p.getTipo()))
                                                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        logger.info("Preguntas filtradas por tipo - ID bloque: " + id + ", Tipo: " + tipo + 
                   ", Cantidad encontrada: " + preguntasFiltradas.size());
        return preguntasFiltradas;
    }
    
    /**
     * Representación en cadena del bloque.
     * 
     * @return String con información básica del bloque
     */
    @Override
    public String toString() {
        return String.format("Bloque{id='%s', titulo='%s', tipo='%s', preguntas=%d}", 
                           id, titulo, tipo, preguntas.size());
    }
} 
