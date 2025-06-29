package com.kursor.builder;

import com.kursor.domain.Curso;
import com.kursor.domain.Bloque;
import com.kursor.domain.Pregunta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kursor.yaml.dto.CursoPreviewDTO;

/**
 * Builder para la construcción de cursos a partir de archivos YAML.
 * 
 * <p>Esta clase implementa el patrón Builder para facilitar la construcción
 * de objetos {@link Curso} de manera fluida y legible. Es especialmente útil
 * durante el proceso de importación de cursos desde archivos YAML y para
 * la creación programática de cursos.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>API fluida:</strong> Permite encadenar métodos para construir el curso</li>
 *   <li><strong>Construcción gradual:</strong> Permite agregar elementos paso a paso</li>
 *   <li><strong>Validación:</strong> Valida la integridad del curso antes de construirlo</li>
 *   <li><strong>Logging:</strong> Registra el proceso de construcción</li>
 *   <li><strong>Flexibilidad:</strong> Permite construir cursos complejos fácilmente</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * CursoBuilder builder = new CursoBuilder();
 * Curso curso = builder
 *     .conId("curso1")
 *     .conTitulo("Matemáticas Básicas")
 *     .conDescripcion("Curso introductorio de matemáticas")
 *     .agregarBloque(new Bloque("bloque1", "Números", "Teoría de números", "teoria"))
 *     .agregarPregunta("bloque1", new PreguntaTest("p1", "¿Cuánto es 2+2?", 
 *                                                  Arrays.asList("3", "4", "5"), "4"))
 *     .build();
 * }</pre>
 * 
 * <p>Flujo típico de construcción:</p>
 * <ol>
 *   <li>Crear instancia del builder</li>
 *   <li>Configurar metadatos básicos (ID, título, descripción)</li>
 *   <li>Agregar bloques de contenido</li>
 *   <li>Agregar preguntas a los bloques</li>
 *   <li>Llamar build() para obtener el curso final</li>
 * </ol>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Curso
 * @see Bloque
 * @see Pregunta
 * @see CursoPreviewDTO
 */
public class CursoBuilder {
    
    /** Logger para registrar eventos del builder */
    private static final Logger logger = LoggerFactory.getLogger(CursoBuilder.class);
    
    /** Curso que se está construyendo */
    private Curso curso;

    /**
     * Constructor para crear un nuevo builder de cursos.
     * 
     * <p>Inicializa un builder con un curso vacío listo para ser configurado.
     * El builder comienza con un curso sin metadatos ni contenido.</p>
     */
    public CursoBuilder() {
        logger.debug("Creando nuevo CursoBuilder");
        this.curso = new Curso();
        logger.debug("CursoBuilder inicializado con curso vacío");
    }

    /**
     * Establece el identificador único del curso.
     * 
     * <p>Este método configura el ID del curso que se está construyendo.
     * El ID debe ser único dentro del sistema y se utiliza para identificar
     * el curso en operaciones posteriores.</p>
     * 
     * @param id Identificador único del curso (no debe ser null o vacío)
     * @return El builder para permitir encadenamiento de métodos
     * @throws IllegalArgumentException si el ID es null o vacío
     */
    public CursoBuilder conId(String id) {
        logger.debug("Estableciendo ID de curso en builder - ID: " + id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al establecer ID en builder: no puede ser null o vacío");
            throw new IllegalArgumentException("ID del curso no puede ser null o vacío");
        }
        
        curso.setId(id.trim());
        logger.info("ID de curso establecido en builder: " + curso.getId());
        return this;
    }

    /**
     * Establece el título del curso.
     * 
     * <p>Este método configura el título descriptivo del curso que se está
     * construyendo. El título se muestra al usuario y debe ser descriptivo
     * del contenido del curso.</p>
     * 
     * @param titulo Título descriptivo del curso (no debe ser null o vacío)
     * @return El builder para permitir encadenamiento de métodos
     * @throws IllegalArgumentException si el título es null o vacío
     */
    public CursoBuilder conTitulo(String titulo) {
        logger.debug("Estableciendo título de curso en builder - Título: " + titulo);
        
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("Error al establecer título en builder: no puede ser null o vacío");
            throw new IllegalArgumentException("Título del curso no puede ser null o vacío");
        }
        
        curso.setTitulo(titulo.trim());
        logger.info("Título de curso establecido en builder: " + curso.getTitulo());
        return this;
    }

    /**
     * Establece la descripción del curso.
     * 
     * <p>Este método configura la descripción detallada del curso que se está
     * construyendo. La descripción proporciona información adicional sobre
     * el contenido y objetivos del curso.</p>
     * 
     * @param descripcion Descripción del curso (puede ser null)
     * @return El builder para permitir encadenamiento de métodos
     */
    public CursoBuilder conDescripcion(String descripcion) {
        logger.debug("Estableciendo descripción de curso en builder - Descripción: " + descripcion);
        
        curso.setDescripcion(descripcion != null ? descripcion.trim() : null);
        logger.info("Descripción de curso establecida en builder");
        return this;
    }

    /**
     * Agrega un bloque al curso.
     * 
     * <p>Este método añade un bloque de contenido al curso que se está
     * construyendo. Los bloques organizan el contenido del curso en
     * unidades temáticas coherentes.</p>
     * 
     * @param bloque El bloque a agregar (no debe ser null)
     * @return El builder para permitir encadenamiento de métodos
     * @throws IllegalArgumentException si el bloque es null
     */
    public CursoBuilder agregarBloque(Bloque bloque) {
        logger.debug("Agregando bloque al curso en builder - Bloque: " + 
                    (bloque != null ? bloque.getId() : "null"));
        
        if (bloque == null) {
            logger.error("Error al agregar bloque en builder: bloque no puede ser null");
            throw new IllegalArgumentException("Bloque no puede ser null");
        }
        
        curso.addBloque(bloque);
        logger.info("Bloque agregado al curso en builder - ID bloque: " + bloque.getId() + 
                   ", Total bloques: " + curso.getBloques().size());
        return this;
    }

    /**
     * Agrega una pregunta a un bloque específico del curso.
     * 
     * <p>Este método busca un bloque por su ID y le agrega una pregunta.
     * Si el bloque no existe, la pregunta no se agrega y se registra
     * un warning en el log.</p>
     * 
     * @param bloqueId Identificador del bloque donde agregar la pregunta
     * @param pregunta La pregunta a agregar (no debe ser null)
     * @return El builder para permitir encadenamiento de métodos
     * @throws IllegalArgumentException si la pregunta es null
     */
    public CursoBuilder agregarPregunta(String bloqueId, Pregunta pregunta) {
        logger.debug("Agregando pregunta a bloque en builder - Bloque ID: " + bloqueId + 
                    ", Pregunta: " + (pregunta != null ? pregunta.getId() : "null"));
        
        if (pregunta == null) {
            logger.error("Error al agregar pregunta en builder: pregunta no puede ser null");
            throw new IllegalArgumentException("Pregunta no puede ser null");
        }
        
        boolean preguntaAgregada = false;
        for (Bloque bloque : curso.getBloques()) {
            if (bloque.getId().equals(bloqueId)) {
                bloque.addPregunta(pregunta);
                preguntaAgregada = true;
                logger.info("Pregunta agregada al bloque en builder - Bloque: " + bloqueId + 
                           ", Pregunta: " + pregunta.getId());
                break;
            }
        }
        
        if (!preguntaAgregada) {
            logger.warn("No se pudo agregar pregunta: bloque no encontrado - Bloque ID: " + bloqueId + 
                          ", Pregunta: " + pregunta.getId());
        }
        
        return this;
    }

    /**
     * Construye y retorna el curso final.
     * 
     * <p>Este método finaliza la construcción del curso y retorna la instancia
     * completa. Antes de retornar, valida que el curso tenga los elementos
     * mínimos necesarios y registra información sobre el curso construido.</p>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>El curso debe tener un ID válido</li>
     *   <li>El curso debe tener un título válido</li>
     *   <li>Se recomienda que tenga al menos un bloque</li>
     * </ul>
     * 
     * @return El curso construido
     * @throws IllegalStateException si el curso no tiene los elementos mínimos necesarios
     */
    public Curso build() {
        logger.info("Construyendo curso final - ID: " + curso.getId() + 
                   ", Título: " + curso.getTitulo() + 
                   ", Bloques: " + curso.getBloques().size());
        
        // Validar que el curso tenga elementos mínimos
        if (curso.getId() == null || curso.getId().trim().isEmpty()) {
            logger.error("Error al construir curso: falta ID");
            throw new IllegalStateException("El curso debe tener un ID válido");
        }
        
        if (curso.getTitulo() == null || curso.getTitulo().trim().isEmpty()) {
            logger.error("Error al construir curso: falta título");
            throw new IllegalStateException("El curso debe tener un título válido");
        }
        
        if (curso.getBloques().isEmpty()) {
            logger.warn("Curso construido sin bloques - ID: " + curso.getId());
        }
        
        logger.info("Curso construido exitosamente - ID: " + curso.getId() + 
                   ", Título: " + curso.getTitulo() + 
                   ", Bloques: " + curso.getBloques().size() + 
                   ", Preguntas totales: " + curso.getNumeroPreguntas());
        
        return curso;
    }
    
    /**
     * Obtiene el curso actual en construcción.
     * 
     * <p>Este método permite acceder al curso que se está construyendo
     * sin finalizar el proceso de construcción. Útil para inspeccionar
     * el estado actual del curso durante la construcción.</p>
     * 
     * @return El curso actual en construcción
     */
    public Curso getCursoActual() {
        logger.debug("Obteniendo curso actual en construcción - ID: " + 
                    (curso != null ? curso.getId() : "null"));
        return curso;
    }
    
    /**
     * Verifica si el curso actual tiene los elementos mínimos necesarios.
     * 
     * @return true si el curso tiene ID y título válidos, false en caso contrario
     */
    public boolean esValido() {
        boolean valido = curso != null && 
                        curso.getId() != null && !curso.getId().trim().isEmpty() &&
                        curso.getTitulo() != null && !curso.getTitulo().trim().isEmpty();
        
        logger.debug("Verificando validez del curso en construcción - Válido: " + valido);
        return valido;
    }
    
    /**
     * Obtiene información de resumen del curso en construcción.
     * 
     * @return String con información básica del curso
     */
    public String getResumen() {
        if (curso == null) {
            return "CursoBuilder: Sin curso inicializado";
        }
        
        String resumen = String.format("CursoBuilder{id='%s', titulo='%s', bloques=%d, preguntas=%d}", 
                                     curso.getId(), curso.getTitulo(), 
                                     curso.getBloques().size(), curso.getNumeroPreguntas());
        
        logger.debug("Obteniendo resumen del curso en construcción: " + resumen);
        return resumen;
    }
} 
