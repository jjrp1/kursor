package com.kursor.util;

import com.kursor.domain.Curso;
import com.kursor.yaml.dto.CursoDTO;
import com.kursor.yaml.dto.BloqueDTO;
import com.kursor.yaml.dto.PreguntaDTO;
import com.kursor.service.CursoPreviewService;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestor de cursos para la aplicación Kursor.
 * 
 * <p>Esta clase implementa el patrón Singleton y se encarga de gestionar
 * la carga y acceso a los cursos de la aplicación. Carga todos los cursos
 * completos en memoria al inicializarse para un acceso rápido y eficiente.</p>
 * 
 * <p>El gestor de cursos proporciona:</p>
 * <ul>
 *   <li>Carga completa de todos los cursos en memoria</li>
 *   <li>Acceso rápido a información detallada de cursos</li>
 *   <li>Cálculos automáticos de estadísticas</li>
 *   <li>Manejo de errores y logging de operaciones</li>
 *   <li>Acceso centralizado a la funcionalidad de cursos</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * CursoManager manager = CursoManager.getInstance();
 * 
 * // Cargar lista de cursos completos en memoria
 * List<CursoDTO> cursos = manager.cargarCursosCompletos();
 * for (CursoDTO curso : cursos) {
 *     System.out.println("Curso: " + curso.getTitulo() + " - " + curso.getTotalPreguntas() + " preguntas");
 * }
 * 
 * // Obtener un curso específico desde memoria
 * CursoDTO curso = manager.obtenerCursoPorId("curso_ingles");
 * if (curso != null) {
 *     System.out.println("Curso en memoria: " + curso.getTitulo());
 * }
 * }</pre>
 * 
 * <p>La clase espera que los cursos estén organizados en el directorio "cursos"
 * en la raíz del proyecto, con cada curso en su propio subdirectorio
 * conteniendo archivos YAML con la configuración del curso.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 2.0.0
 * @since 2.0.0
 * @see CursoDTO
 * @see CursoPreviewService
 */
public class CursoManager {
    /** Instancia única del gestor de cursos (patrón Singleton) */
    private static CursoManager instance;
    
    /** Servicio para cargar información de cursos desde archivos YAML */
    private final CursoPreviewService cursoPreviewService;
    
    /** Cache en memoria de todos los cursos completos */
    private List<CursoDTO> cursosEnMemoria;
    
    /** Mapa de acceso rápido por ID */
    private Map<String, CursoDTO> cursosPorId;
    
    /** Flag que indica si los cursos ya han sido cargados */
    private boolean cursosCargados = false;
    
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
        
        // Inicializar estructuras de datos
        this.cursosEnMemoria = new ArrayList<>();
        this.cursosPorId = new HashMap<>();
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
     * Carga todos los cursos completos en memoria.
     * 
     * <p>Este método carga toda la información de todos los cursos disponibles,
     * incluyendo bloques, preguntas y estadísticas calculadas. Los datos se
     * mantienen en memoria para acceso rápido posterior.</p>
     * 
     * <p>El método maneja automáticamente los errores de carga y registra
     * el resultado en el logger. Si ocurre un error durante la carga,
     * retorna una lista vacía en lugar de lanzar una excepción.</p>
     * 
     * <p>Información registrada en el logger:</p>
     * <ul>
     *   <li>Número de cursos cargados exitosamente</li>
     *   <li>Estadísticas totales de preguntas y bloques</li>
     *   <li>Errores durante el proceso de carga</li>
     * </ul>
     * 
     * @return Lista de cursos completos cargados en memoria. Si ocurre un error,
     *         retorna una lista vacía
     * @see CursoDTO
     */
    public List<CursoDTO> cargarCursosCompletos() {
        if (cursosCargados) {
            logger.debug("Cursos ya cargados en memoria, retornando cache");
            return new ArrayList<>(cursosEnMemoria);
        }
        
        try {
            logger.info("🔄 Iniciando carga completa de cursos en memoria...");
            
            // Limpiar estructuras de datos
            cursosEnMemoria.clear();
            cursosPorId.clear();
            
            // Cargar todos los cursos completos
            List<Curso> cursosCompletos = cursoPreviewService.cargarTodosLosCursosCompletos();
            
            for (Curso curso : cursosCompletos) {
                CursoDTO cursoDTO = convertirCursoADTO(curso);
                cursosEnMemoria.add(cursoDTO);
                cursosPorId.put(cursoDTO.getId(), cursoDTO);
            }
            
            cursosCargados = true;
            
            // Calcular estadísticas totales
            int totalCursos = cursosEnMemoria.size();
            int totalBloques = cursosEnMemoria.stream().mapToInt(CursoDTO::getTotalBloques).sum();
            int totalPreguntas = cursosEnMemoria.stream().mapToInt(CursoDTO::getTotalPreguntas).sum();
            
            logger.info("✅ Cursos cargados exitosamente en memoria:");
            logger.info("   📚 Total de cursos: {}", totalCursos);
            logger.info("   📦 Total de bloques: {}", totalBloques);
            logger.info("   ❓ Total de preguntas: {}", totalPreguntas);
            
            return new ArrayList<>(cursosEnMemoria);
            
        } catch (Exception e) {
            logger.error("❌ Error al cargar cursos completos: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene un curso específico por su ID desde la memoria.
     * 
     * <p>Este método accede directamente a la memoria para obtener
     * un curso específico. Si los cursos no han sido cargados aún,
     * los carga automáticamente.</p>
     * 
     * @param id Identificador único del curso
     * @return El curso completo desde memoria, o <code>null</code> si no existe
     */
    public CursoDTO obtenerCursoPorId(String id) {
        if (!cursosCargados) {
            cargarCursosCompletos();
        }
        return cursosPorId.get(id);
    }

    /**
     * Convierte un objeto Curso del dominio a CursoDTO.
     * 
     * @param curso Curso del dominio a convertir
     * @return CursoDTO con toda la información del curso
     */
    private CursoDTO convertirCursoADTO(Curso curso) {
        CursoDTO cursoDTO = new CursoDTO(
            curso.getId(),
            curso.getTitulo(),
            curso.getDescripcion(),
            curso.getId() + ".yaml"
        );
        
        // Convertir bloques
        List<BloqueDTO> bloquesDTO = curso.getBloques().stream()
            .map(this::convertirBloqueADTO)
            .collect(Collectors.toList());
        
        cursoDTO.setBloques(bloquesDTO);
        
        return cursoDTO;
    }

    /**
     * Convierte un objeto Bloque del dominio a BloqueDTO.
     * 
     * @param bloque Bloque del dominio a convertir
     * @return BloqueDTO con la información del bloque
     */
    private BloqueDTO convertirBloqueADTO(com.kursor.domain.Bloque bloque) {
        BloqueDTO bloqueDTO = new BloqueDTO(
            bloque.getTitulo(),
            bloque.getTipo()
        );
        
        // Convertir preguntas
        List<PreguntaDTO> preguntasDTO = bloque.getPreguntas().stream()
            .map(this::convertirPreguntaADTO)
            .collect(Collectors.toList());
        
        bloqueDTO.setPreguntas(preguntasDTO);
        
        return bloqueDTO;
    }

    /**
     * Convierte un objeto Pregunta del dominio a PreguntaDTO.
     * 
     * @param pregunta Pregunta del dominio a convertir
     * @return PreguntaDTO con la información básica de la pregunta
     */
    private PreguntaDTO convertirPreguntaADTO(com.kursor.domain.Pregunta pregunta) {
        return new PreguntaDTO(
            pregunta.getId(),
            pregunta.getTipo(),
            pregunta.getEnunciado()
        );
    }

    /**
     * Método de compatibilidad: carga la lista de todos los cursos disponibles.
     * 
     * @deprecated Usar {@link #cargarCursosCompletos()} en su lugar
     * @return Lista de cursos completos
     */
    @Deprecated
    public List<CursoDTO> cargarCursos() {
        return cargarCursosCompletos();
    }

    /**
     * Método de compatibilidad: carga un curso completo por su identificador.
     * 
     * @deprecated Usar {@link #obtenerCursoPorId(String)} en su lugar
     * @param id Identificador único del curso a cargar
     * @return El curso completo cargado, o <code>null</code> si no existe
     */
    @Deprecated
    public Curso obtenerCursoCompleto(String id) {
        CursoDTO cursoDTO = obtenerCursoPorId(id);
        if (cursoDTO == null) {
            return null;
        }
        
        // Convertir de vuelta a Curso del dominio si es necesario
        try {
            return cursoPreviewService.cargarCursoCompleto(id);
        } catch (Exception e) {
            logger.error("Error al cargar curso completo: " + e.getMessage());
            return null;
        }
    }
} 