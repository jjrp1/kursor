package com.kursor.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Representa un curso educativo en el sistema Kursor.
 * 
 * <p>Esta clase modela un curso completo compuesto por múltiples bloques de contenido.
 * Cada curso tiene una estructura jerárquica que organiza el contenido educativo
 * de manera lógica y secuencial.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Identificación única:</strong> Cada curso tiene un ID único</li>
 *   <li><strong>Metadatos:</strong> Título y descripción descriptivos</li>
 *   <li><strong>Estructura modular:</strong> Organizado en bloques de contenido</li>
 *   <li><strong>Gestión de bloques:</strong> Métodos para agregar y gestionar bloques</li>
 *   <li><strong>Inmutabilidad defensiva:</strong> Protección contra modificaciones externas</li>
 * </ul>
 * 
 * <p>Estructura del curso:</p>
 * <pre>
 * Curso
 * ├── Bloque 1
 * │   ├── Pregunta 1.1
 * │   ├── Pregunta 1.2
 * │   └── ...
 * ├── Bloque 2
 * │   ├── Pregunta 2.1
 * │   └── ...
 * └── ...
 * </pre>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Curso curso = new Curso("curso1", "Matemáticas Básicas", "Curso introductorio de matemáticas");
 * Bloque bloque1 = new Bloque("bloque1", "Números naturales");
 * curso.addBloque(bloque1);
 * List<Bloque> bloques = curso.getBloques();
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Bloque
 * @see Pregunta
 * @see CursoBuilder
 */
public class Curso {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(Curso.class);
    
    /** Identificador único del curso */
    private String id;
    
    /** Título descriptivo del curso */
    private String titulo;
    
    /** Descripción detallada del contenido del curso */
    private String descripcion;
    
    /** Lista de bloques que componen el curso */
    private List<Bloque> bloques = new ArrayList<>();

    /**
     * Constructor por defecto para crear un curso vacío.
     * 
     * <p>Este constructor crea un curso sin inicializar sus propiedades.
     * Útil para frameworks de serialización o construcción gradual.</p>
     */
    public Curso() {
        logger.debug("Creando curso vacío");
    }

    /**
     * Constructor para crear un curso con metadatos básicos.
     * 
     * <p>Inicializa un curso con un identificador, título y descripción.
     * El constructor valida que los parámetros sean válidos y registra
     * la creación en el log.</p>
     * 
     * @param id Identificador único del curso (no debe ser null)
     * @param titulo Título descriptivo del curso (no debe ser null)
     * @param descripcion Descripción del contenido (puede ser null)
     * @throws IllegalArgumentException si id o título son null o vacíos
     */
    public Curso(String id, String titulo, String descripcion) {
        logger.debug("Creando curso - ID: " + id + ", Título: " + titulo);
        
        // Validar ID
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al crear curso: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID del curso no puede ser null o vacío");
        }
        
        // Validar título
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("Error al crear curso: título no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Título del curso no puede ser null o vacío");
        }
        
        this.id = id.trim();
        this.titulo = titulo.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        
        logger.info("Curso creado exitosamente - ID: " + this.id + ", Título: " + this.titulo);
    }

    /**
     * Obtiene el identificador único del curso.
     * 
     * @return El ID del curso
     */
    public String getId() { 
        logger.debug("Obteniendo ID de curso: " + id);
        return id; 
    }
    
    /**
     * Establece el identificador único del curso.
     * 
     * @param id El nuevo ID del curso (no debe ser null o vacío)
     * @throws IllegalArgumentException si el ID es null o vacío
     */
    public void setId(String id) { 
        logger.debug("Estableciendo ID de curso - Anterior: " + this.id + ", Nuevo: " + id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al establecer ID: no puede ser null o vacío");
            throw new IllegalArgumentException("ID del curso no puede ser null o vacío");
        }
        
        this.id = id.trim();
        logger.info("ID de curso actualizado: " + this.id);
    }

    /**
     * Obtiene el título del curso.
     * 
     * @return El título descriptivo del curso
     */
    public String getTitulo() { 
        logger.debug("Obteniendo título de curso - ID: " + id);
        return titulo; 
    }
    
    /**
     * Establece el título del curso.
     * 
     * @param titulo El nuevo título del curso (no debe ser null o vacío)
     * @throws IllegalArgumentException si el título es null o vacío
     */
    public void setTitulo(String titulo) { 
        logger.debug("Estableciendo título de curso - ID: " + id + 
                    ", Anterior: " + this.titulo + ", Nuevo: " + titulo);
        
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("Error al establecer título: no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Título del curso no puede ser null o vacío");
        }
        
        this.titulo = titulo.trim();
        logger.info("Título de curso actualizado - ID: " + id + ", Nuevo título: " + this.titulo);
    }

    /**
     * Obtiene la descripción del curso.
     * 
     * @return La descripción del contenido del curso (puede ser null)
     */
    public String getDescripcion() { 
        logger.debug("Obteniendo descripción de curso - ID: " + id);
        return descripcion; 
    }
    
    /**
     * Establece la descripción del curso.
     * 
     * @param descripcion La nueva descripción del curso (puede ser null)
     */
    public void setDescripcion(String descripcion) { 
        logger.debug("Estableciendo descripción de curso - ID: " + id + 
                    ", Anterior: " + this.descripcion + ", Nueva: " + descripcion);
        
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        logger.info("Descripción de curso actualizada - ID: " + id);
    }

    /**
     * Obtiene la lista de bloques del curso.
     * 
     * <p>Este método retorna una vista inmutable de la lista de bloques
     * para prevenir modificaciones externas accidentales. Para agregar
     * bloques, use el método {@link #addBloque(Bloque)}.</p>
     * 
     * @return Lista inmutable de bloques del curso
     */
    public List<Bloque> getBloques() { 
        logger.debug("Obteniendo bloques de curso - ID: " + id + ", Cantidad: " + bloques.size());
        return Collections.unmodifiableList(bloques); 
    }
    
    /**
     * Establece la lista completa de bloques del curso.
     * 
     * <p>Este método reemplaza completamente la lista de bloques existente.
     * Se crea una copia defensiva para evitar modificaciones externas.</p>
     * 
     * @param bloques Nueva lista de bloques (no debe ser null)
     * @throws IllegalArgumentException si la lista es null
     */
    public void setBloques(List<Bloque> bloques) { 
        logger.debug("Estableciendo bloques de curso - ID: " + id + 
                    ", Cantidad anterior: " + this.bloques.size() + ", Nueva cantidad: " + 
                    (bloques != null ? bloques.size() : "null"));
        
        if (bloques == null) {
            logger.error("Error al establecer bloques: lista no puede ser null - ID: " + id);
            throw new IllegalArgumentException("Lista de bloques no puede ser null");
        }
        
        this.bloques = new ArrayList<>(bloques); // Crear copia defensiva
        logger.info("Bloques de curso actualizados - ID: " + id + ", Cantidad: " + bloques.size());
    }

    /**
     * Agrega un bloque al curso.
     * 
     * <p>Este método añade un nuevo bloque al final de la lista de bloques
     * del curso. El bloque se valida antes de ser agregado.</p>
     * 
     * @param bloque El bloque a agregar (no debe ser null)
     * @throws IllegalArgumentException si el bloque es null
     */
    public void addBloque(Bloque bloque) {
        logger.debug("Agregando bloque a curso - ID curso: " + id + 
                    ", Bloque: " + (bloque != null ? bloque.getId() : "null"));
        
        if (bloque == null) {
            logger.error("Error al agregar bloque: bloque no puede ser null - ID curso: " + id);
            throw new IllegalArgumentException("Bloque no puede ser null");
        }
        
        this.bloques.add(bloque);
        logger.info("Bloque agregado exitosamente - ID curso: " + id + 
                   ", ID bloque: " + bloque.getId() + ", Total bloques: " + bloques.size());
    }
    
    /**
     * Obtiene el número total de bloques en el curso.
     * 
     * @return El número de bloques del curso
     */
    public int getNumeroBloques() {
        int numeroBloques = bloques.size();
        logger.debug("Obteniendo número de bloques de curso - ID: " + id + ", Cantidad: " + numeroBloques);
        return numeroBloques;
    }
    
    /**
     * Verifica si el curso tiene bloques.
     * 
     * @return true si el curso tiene al menos un bloque, false en caso contrario
     */
    public boolean tieneBloques() {
        boolean tieneBloques = !bloques.isEmpty();
        logger.debug("Verificando si curso tiene bloques - ID: " + id + ", Tiene bloques: " + tieneBloques);
        return tieneBloques;
    }
    
    /**
     * Obtiene el número total de preguntas en el curso.
     * 
     * <p>Este método recorre todos los bloques del curso y suma el número
     * total de preguntas contenidas en ellos.</p>
     * 
     * @return El número total de preguntas del curso
     */
    public int getNumeroPreguntas() {
        int totalPreguntas = bloques.stream()
                                   .mapToInt(bloque -> bloque.getPreguntas().size())
                                   .sum();
        
        logger.debug("Obteniendo número total de preguntas de curso - ID: " + id + 
                    ", Total preguntas: " + totalPreguntas);
        return totalPreguntas;
    }
    
    /**
     * Representación en cadena del curso.
     * 
     * @return String con información básica del curso
     */
    @Override
    public String toString() {
        return String.format("Curso{id='%s', titulo='%s', bloques=%d, preguntas=%d}", 
                           id, titulo, bloques.size(), getNumeroPreguntas());
    }
} 
