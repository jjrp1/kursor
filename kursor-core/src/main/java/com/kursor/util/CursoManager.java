package com.kursor.util;

import com.kursor.domain.Curso;
import com.kursor.yaml.dto.CursoPreviewDTO;
import com.kursor.service.CursoPreviewService;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestor de cursos para la aplicación Kursor.
 * 
 * <p>Esta clase implementa el patrón Singleton y se encarga de gestionar
 * la carga y acceso a los cursos de la aplicación. Utiliza el servicio
 * {@link CursoPreviewService} para cargar información de cursos desde
 * archivos YAML ubicados en el directorio "cursos".</p>
 * 
 * <p>El gestor de cursos proporciona:</p>
 * <ul>
 *   <li>Carga de vistas previas de todos los cursos disponibles</li>
 *   <li>Carga de cursos completos por identificador</li>
 *   <li>Manejo de errores y logging de operaciones</li>
 *   <li>Acceso centralizado a la funcionalidad de cursos</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * CursoManager manager = CursoManager.getInstance();
 * 
 * // Cargar lista de cursos disponibles
 * List<CursoPreviewDTO> cursos = manager.cargarCursos();
 * for (CursoPreviewDTO curso : cursos) {
 *     System.out.println("Curso: " + curso.getTitulo());
 * }
 * 
 * // Cargar un curso completo
 * Curso cursoCompleto = manager.obtenerCursoCompleto("curso_ingles");
 * if (cursoCompleto != null) {
 *     System.out.println("Curso cargado: " + cursoCompleto.getTitulo());
 * }
 * }</pre>
 * 
 * <p>La clase espera que los cursos estén organizados en el directorio "cursos"
 * en la raíz del proyecto, con cada curso en su propio subdirectorio
 * conteniendo archivos YAML con la configuración del curso.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Curso
 * @see CursoPreviewDTO
 * @see CursoPreviewService
 */
public class CursoManager {
    /** Instancia única del gestor de cursos (patrón Singleton) */
    private static CursoManager instance;
    
    /** Servicio para cargar información de cursos desde archivos YAML */
    private final CursoPreviewService cursoPreviewService;
    
    /** Instancia del logger para registrar eventos */
    private static final Logger logger = LoggerFactory.getLogger(CursoManager.class);

    /**
     * Constructor privado que inicializa el gestor de cursos.
     * 
     * <p>Este constructor es privado para implementar el patrón Singleton.
     * Inicializa el servicio de carga de cursos y el logger, configurando
     * la ruta del directorio "cursos" en la raíz del proyecto.</p>
     * 
     * <p>La ruta de los cursos se construye como:
     * <code>directorio_raiz_proyecto + File.separator + "cursos"</code></p>
     */
    private CursoManager() {
        // Obtener la ruta base del proyecto
        String basePath = new File("").getAbsolutePath();
        
        // Si estamos en un subdirectorio del proyecto (kursor-core, kursor-ui, etc.)
        // subir un nivel para llegar a la raíz del proyecto
        if (basePath.endsWith("kursor-core")) {
            basePath = new File(basePath).getParent();
        }
        
        String cursosPath = basePath + File.separator + "cursos";
        this.cursoPreviewService = new CursoPreviewService(cursosPath);
    }

    /**
     * Obtiene la instancia única del gestor de cursos.
     * 
     * <p>Implementa el patrón Singleton para asegurar que solo existe
     * una instancia del gestor de cursos en toda la aplicación.</p>
     * 
     * @return La instancia única del CursoManager
     */
    public static CursoManager getInstance() {
        if (instance == null) {
            instance = new CursoManager();
        }
        return instance;
    }

    /**
     * Carga la lista de todos los cursos disponibles.
     * 
     * <p>Este método utiliza el {@link CursoPreviewService} para cargar
     * las vistas previas de todos los cursos encontrados en el directorio
     * "cursos". Las vistas previas contienen información básica como título,
     * descripción e identificador del curso.</p>
     * 
     * <p>El método maneja automáticamente los errores de carga y registra
     * el resultado en el logger. Si ocurre un error durante la carga,
     * retorna una lista vacía en lugar de lanzar una excepción.</p>
     * 
     * <p>Información registrada en el logger:</p>
     * <ul>
     *   <li>Número de cursos cargados exitosamente</li>
     *   <li>Errores durante el proceso de carga</li>
     * </ul>
     * 
     * @return Lista de vistas previas de cursos disponibles. Si ocurre un error,
     *         retorna una lista vacía
     * @see CursoPreviewDTO
     * @see CursoPreviewService#cargarPreviews()
     */
    public List<CursoPreviewDTO> cargarCursos() {
        try {
            List<CursoPreviewDTO> cursos = cursoPreviewService.cargarPreviews();
            logger.info("Cursos cargados exitosamente: " + cursos.size());
            return cursos;
        } catch (Exception e) {
            logger.error("Error al cargar cursos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Carga un curso completo por su identificador.
     * 
     * <p>Este método carga toda la información de un curso específico,
     * incluyendo bloques, preguntas y configuración detallada. Utiliza
     * el {@link CursoPreviewService} para realizar la carga desde los
     * archivos YAML del curso.</p>
     * 
     * <p>El método maneja automáticamente los errores de carga y registra
     * cualquier problema en el logger. Si el curso no existe o ocurre
     * un error durante la carga, retorna <code>null</code>.</p>
     * 
     * <p>Información registrada en el logger:</p>
     * <ul>
     *   <li>Errores durante el proceso de carga del curso</li>
     *   <li>Mensajes de error específicos para debugging</li>
     * </ul>
     * 
     * @param id Identificador único del curso a cargar. Debe corresponder
     *           al nombre del directorio del curso en la carpeta "cursos"
     * @return El curso completo cargado, o <code>null</code> si el curso
     *         no existe o ocurre un error durante la carga
     * @see Curso
     * @see CursoPreviewService#cargarCursoCompleto(String)
     */
    public Curso obtenerCursoCompleto(String id) {
        try {
            return cursoPreviewService.cargarCursoCompleto(id);
        } catch (Exception e) {
            logger.error("Error al cargar curso completo: " + e.getMessage());
            return null;
        }
    }
} 