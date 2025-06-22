package com.kursor.yaml.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DTO (Data Transfer Object) que contiene solo la información básica de un curso
 * para su visualización en listas y vistas previas.
 * 
 * <p>Esta clase se utiliza para transferir información básica de cursos sin
 * cargar toda la estructura completa (bloques, preguntas, etc.). Es especialmente
 * útil para mostrar listas de cursos disponibles o información resumida.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Información mínima:</strong> Solo contiene datos esenciales</li>
 *   <li><strong>Transferencia eficiente:</strong> Reduce el overhead de datos</li>
 *   <li><strong>Serialización simple:</strong> Fácil de convertir a JSON/YAML</li>
 *   <li><strong>Inmutabilidad opcional:</strong> Permite modificación si es necesario</li>
 * </ul>
 * 
 * <p>Casos de uso típicos:</p>
 * <ul>
 *   <li><strong>Listas de cursos:</strong> Mostrar cursos disponibles</li>
 *   <li><strong>Vistas previas:</strong> Información básica antes de cargar completo</li>
 *   <li><strong>APIs REST:</strong> Respuestas ligeras para endpoints de listado</li>
 *   <li><strong>UI/UX:</strong> Datos para interfaces de usuario</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * CursoPreviewDTO preview = new CursoPreviewDTO(
 *     "curso_ingles",
 *     "Inglés Básico",
 *     "Curso introductorio de inglés para principiantes"
 * );
 * 
 * System.out.println("Curso: " + preview.getTitulo());
 * System.out.println("Descripción: " + preview.getDescripcion());
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see com.kursor.domain.Curso
 * @see com.kursor.service.CursoPreviewService
 */
public class CursoPreviewDTO {
    
    /** Logger para registrar eventos de la clase */
    private static final Logger logger = LoggerFactory.getLogger(CursoPreviewDTO.class);
    
    /** Identificador único del curso */
    private String id;
    
    /** Título descriptivo del curso */
    private String titulo;
    
    /** Descripción del contenido del curso */
    private String descripcion;
    
    /**
     * Constructor por defecto para crear un DTO vacío.
     * 
     * <p>Este constructor crea un DTO sin inicializar sus propiedades.
     * Útil para frameworks de serialización o construcción gradual.</p>
     */
    public CursoPreviewDTO() {
        logger.debug("Creando CursoPreviewDTO vacío");
    }
    
    /**
     * Constructor para crear un DTO con información básica del curso.
     * 
     * <p>Inicializa el DTO con el identificador, título y descripción del curso.
     * El constructor valida que los parámetros obligatorios sean válidos y
     * registra la creación en el log.</p>
     * 
     * @param id Identificador único del curso (no debe ser null)
     * @param titulo Título descriptivo del curso (no debe ser null)
     * @param descripcion Descripción del curso (puede ser null)
     * @throws IllegalArgumentException si id o título son null
     */
    public CursoPreviewDTO(String id, String titulo, String descripcion) {
        logger.debug("Creando CursoPreviewDTO - ID: " + id + ", Título: " + titulo);
        
        // Validar parámetros obligatorios
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al crear CursoPreviewDTO: ID no puede ser null o vacío");
            throw new IllegalArgumentException("ID del curso no puede ser null o vacío");
        }
        
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("Error al crear CursoPreviewDTO: título no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Título del curso no puede ser null o vacío");
        }
        
        this.id = id.trim();
        this.titulo = titulo.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        
        logger.info("CursoPreviewDTO creado exitosamente - ID: " + this.id + ", Título: " + this.titulo);
    }
    
    /**
     * Obtiene el identificador único del curso.
     * 
     * @return El ID del curso
     */
    public String getId() { 
        logger.debug("Obteniendo ID de CursoPreviewDTO: " + id);
        return id; 
    }
    
    /**
     * Establece el identificador único del curso.
     * 
     * @param id El nuevo ID del curso (no debe ser null o vacío)
     * @throws IllegalArgumentException si el ID es null o vacío
     */
    public void setId(String id) { 
        logger.debug("Estableciendo ID de CursoPreviewDTO - Anterior: " + this.id + ", Nuevo: " + id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al establecer ID: no puede ser null o vacío");
            throw new IllegalArgumentException("ID del curso no puede ser null o vacío");
        }
        
        this.id = id.trim();
        logger.info("ID de CursoPreviewDTO actualizado: " + this.id);
    }
    
    /**
     * Obtiene el título del curso.
     * 
     * @return El título descriptivo del curso
     */
    public String getTitulo() { 
        logger.debug("Obteniendo título de CursoPreviewDTO - ID: " + id);
        return titulo; 
    }
    
    /**
     * Establece el título del curso.
     * 
     * @param titulo El nuevo título del curso (no debe ser null o vacío)
     * @throws IllegalArgumentException si el título es null o vacío
     */
    public void setTitulo(String titulo) { 
        logger.debug("Estableciendo título de CursoPreviewDTO - ID: " + id + 
                    ", Anterior: " + this.titulo + ", Nuevo: " + titulo);
        
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("Error al establecer título: no puede ser null o vacío - ID: " + id);
            throw new IllegalArgumentException("Título del curso no puede ser null o vacío");
        }
        
        this.titulo = titulo.trim();
        logger.info("Título de CursoPreviewDTO actualizado - ID: " + id + ", Nuevo título: " + this.titulo);
    }
    
    /**
     * Obtiene la descripción del curso.
     * 
     * @return La descripción del contenido del curso (puede ser null)
     */
    public String getDescripcion() { 
        logger.debug("Obteniendo descripción de CursoPreviewDTO - ID: " + id);
        return descripcion; 
    }
    
    /**
     * Establece la descripción del curso.
     * 
     * @param descripcion La nueva descripción del curso (puede ser null)
     */
    public void setDescripcion(String descripcion) { 
        logger.debug("Estableciendo descripción de CursoPreviewDTO - ID: " + id + 
                    ", Anterior: " + this.descripcion + ", Nueva: " + descripcion);
        
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        logger.info("Descripción de CursoPreviewDTO actualizada - ID: " + id);
    }
    
    /**
     * Verifica si el DTO tiene una descripción.
     * 
     * @return true si tiene descripción, false en caso contrario
     */
    public boolean tieneDescripcion() {
        boolean tieneDescripcion = descripcion != null && !descripcion.trim().isEmpty();
        logger.debug("Verificando si CursoPreviewDTO tiene descripción - ID: " + id + 
                    ", Tiene descripción: " + tieneDescripcion);
        return tieneDescripcion;
    }
    
    /**
     * Obtiene una descripción truncada para mostrar en listas.
     * 
     * <p>Este método retorna una versión truncada de la descripción
     * si es muy larga, útil para mostrar en interfaces de usuario
     * con espacio limitado.</p>
     * 
     * @param maxLength Longitud máxima de la descripción truncada
     * @return La descripción truncada, o la descripción completa si es más corta
     */
    public String getDescripcionTruncada(int maxLength) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return "";
        }
        
        String descripcionLimpia = descripcion.trim();
        if (descripcionLimpia.length() <= maxLength) {
            return descripcionLimpia;
        }
        
        String descripcionTruncada = descripcionLimpia.substring(0, maxLength - 3) + "...";
        logger.debug("Obteniendo descripción truncada de CursoPreviewDTO - ID: " + id + 
                    ", Longitud original: " + descripcionLimpia.length() + 
                    ", Longitud truncada: " + descripcionTruncada.length());
        return descripcionTruncada;
    }
    
    /**
     * Verifica si el DTO es válido.
     * 
     * <p>Un DTO es válido si tiene un ID y título no vacíos.</p>
     * 
     * @return true si el DTO es válido, false en caso contrario
     */
    public boolean esValido() {
        boolean valido = id != null && !id.trim().isEmpty() &&
                        titulo != null && !titulo.trim().isEmpty();
        
        logger.debug("Verificando validez de CursoPreviewDTO - ID: " + id + ", Válido: " + valido);
        return valido;
    }
    
    /**
     * Representación en cadena del DTO.
     * 
     * @return String con información básica del DTO
     */
    @Override
    public String toString() {
        return String.format("CursoPreviewDTO{id='%s', titulo='%s', tieneDescripcion=%s}", 
                           id, titulo, tieneDescripcion());
    }
    
    /**
     * Compara este DTO con otro objeto.
     * 
     * <p>Dos DTOs son iguales si tienen el mismo ID.</p>
     * 
     * @param obj El objeto a comparar
     * @return true si los objetos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CursoPreviewDTO that = (CursoPreviewDTO) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    /**
     * Calcula el hash code del DTO.
     * 
     * @return El hash code basado en el ID
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 