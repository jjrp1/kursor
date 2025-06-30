package com.kursor.shared.util;

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
 *   <li><strong>Carga completa:</strong> Todos los cursos en memoria</li>
 *   <li><strong>Acceso rápido:</strong> Información detallada de cursos</li>
 *   <li><strong>Cálculos automáticos:</strong> Estadísticas de preguntas y bloques</li>
 *   <li><strong>Manejo de errores:</strong> Logging detallado de operaciones</li>
 *   <li><strong>Acceso centralizado:</strong> Funcionalidad de cursos</li>
 *   <li><strong>Cache inteligente:</strong> Evita recargas innecesarias</li>
 * </ul>
 * 
 * <p>Estructura de directorios esperada:</p>
 * <pre>
 * proyecto/
 * ├── cursos/
 * │   ├── curso_ingles/
 * │   │   └── curso_ingles.yaml
 * │   ├── curso_matematicas/
 * │   │   └── curso_matematicas.yaml
 * │   └── ...
 * └── ...
 * </pre>
 * 
 * <p>Proceso de carga:</p>
 * <ol>
 *   <li>Inicialización del servicio de carga</li>
 *   <li>Detección automática de directorios de cursos</li>
 *   <li>Carga de archivos YAML de configuración</li>
 *   <li>Conversión a DTOs para la aplicación</li>
 *   <li>Almacenamiento en cache con acceso rápido</li>
 *   <li>Cálculo de estadísticas automáticas</li>
 * </ol>
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
 * @see Curso
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
     * 
     * <p>Proceso de inicialización:</p>
     * <ol>
     *   <li>Determinar la ruta base del proyecto</li>
     *   <li>Ajustar ruta si estamos en un subdirectorio</li>
     *   <li>Construir ruta del directorio de cursos</li>
     *   <li>Inicializar el servicio de carga</li>
     *   <li>Crear estructuras de datos para cache</li>
     * </ol>
     */
    private CursoManager() {
        logger.debug("🚀 Iniciando construcción del CursoManager (Singleton)");
        
        // Obtener la ruta base del proyecto
        String basePath = new File("").getAbsolutePath();
        logger.debug("📁 Ruta base detectada: {}", basePath);
        
        // Si estamos en un subdirectorio del proyecto (kursor-core, kursor-ui, etc.)
        // subir un nivel para llegar a la raíz del proyecto
        if (basePath.endsWith("kursor-core")) {
            basePath = new File(basePath).getParent();
            logger.debug("🔄 Ajustando ruta desde subdirectorio kursor-core: {}", basePath);
        }
        
        String cursosPath = basePath + File.separator + "cursos";
        logger.debug("📂 Ruta del directorio de cursos: {}", cursosPath);
        
        // Verificar que el directorio existe
        File cursosDir = new File(cursosPath);
        if (!cursosDir.exists()) {
            logger.warn("⚠️  El directorio de cursos no existe: {}", cursosPath);
        } else {
            logger.debug("✅ Directorio de cursos encontrado: {}", cursosPath);
        }
        
        this.cursoPreviewService = new CursoPreviewService(cursosPath);
        logger.debug("✅ CursoPreviewService inicializado con ruta: {}", cursosPath);
        
        // Inicializar estructuras de datos
        this.cursosEnMemoria = new ArrayList<>();
        this.cursosPorId = new HashMap<>();
        logger.debug("✅ Estructuras de datos inicializadas");
        logger.debug("   - Lista de cursos en memoria: 0 elementos");
        logger.debug("   - Mapa de acceso rápido: 0 elementos");
        logger.debug("   - Flag de carga: {}", cursosCargados);
        
        logger.info("🎉 CursoManager inicializado correctamente");
    }

    /**
     * Obtiene la instancia única del gestor de cursos.
     * 
     * <p>Implementa el patrón Singleton para asegurar que solo existe
     * una instancia del gestor de cursos en toda la aplicación.</p>
     * 
     * <p>Características del Singleton:</p>
     * <ul>
     *   <li><strong>Thread-safe:</strong> No es thread-safe en esta implementación</li>
     *   <li><strong>Lazy initialization:</strong> Se crea solo cuando se solicita</li>
     *   <li><strong>Global access:</strong> Accesible desde cualquier parte de la aplicación</li>
     * </ul>
     * 
     * @return La instancia única del CursoManager
     */
    public static CursoManager getInstance() {
        if (instance == null) {
            logger.debug("🔄 Creando nueva instancia de CursoManager (Singleton)");
            instance = new CursoManager();
        } else {
            logger.debug("✅ Retornando instancia existente de CursoManager");
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
     * <p>Proceso de carga:</p>
     * <ol>
     *   <li>Verificar si ya están cargados (cache)</li>
     *   <li>Limpiar estructuras de datos existentes</li>
     *   <li>Cargar cursos desde el servicio</li>
     *   <li>Convertir a DTOs</li>
     *   <li>Almacenar en cache</li>
     *   <li>Calcular estadísticas</li>
     *   <li>Registrar resultados</li>
     * </ol>
     * 
     * <p>Información registrada en el logger:</p>
     * <ul>
     *   <li>Número de cursos cargados exitosamente</li>
     *   <li>Estadísticas totales de preguntas y bloques</li>
     *   <li>Errores durante el proceso de carga</li>
     *   <li>Detalles de cada curso cargado</li>
     * </ul>
     * 
     * @return Lista de cursos completos cargados en memoria. Si ocurre un error,
     *         retorna una lista vacía
     * @see CursoDTO
     * @see CursoPreviewService#cargarTodosLosCursosCompletos()
     */
    public List<CursoDTO> cargarCursosCompletos() {
        if (cursosCargados) {
            logger.debug("✅ Cursos ya cargados en memoria, retornando cache");
            logger.debug("📊 Cache actual: {} cursos", cursosEnMemoria.size());
            return new ArrayList<>(cursosEnMemoria);
        }
        
        try {
            logger.info("🔄 Iniciando carga completa de cursos en memoria...");
            
            // Limpiar estructuras de datos
            logger.debug("🧹 Limpiando estructuras de datos existentes");
            cursosEnMemoria.clear();
            cursosPorId.clear();
            logger.debug("✅ Estructuras de datos limpiadas");
            
            // Cargar todos los cursos completos
            logger.debug("📚 Cargando cursos desde CursoPreviewService");
            List<Curso> cursosCompletos = cursoPreviewService.cargarTodosLosCursosCompletos();
            logger.debug("📋 Cursos cargados desde servicio: {}", cursosCompletos.size());
            
            // Convertir y almacenar en cache
            logger.debug("🔄 Convirtiendo cursos a DTOs y almacenando en cache");
            for (Curso curso : cursosCompletos) {
                logger.debug("📝 Procesando curso: {} ({})", curso.getTitulo(), curso.getId());
                
                CursoDTO cursoDTO = convertirCursoADTO(curso);
                cursosEnMemoria.add(cursoDTO);
                cursosPorId.put(cursoDTO.getId(), cursoDTO);
                
                logger.debug("✅ Curso agregado al cache: {} - {} bloques, {} preguntas", 
                           cursoDTO.getTitulo(), cursoDTO.getTotalBloques(), cursoDTO.getTotalPreguntas());
            }
            
            cursosCargados = true;
            logger.debug("✅ Flag de carga establecido: {}", cursosCargados);
            
            // Calcular estadísticas totales
            int totalCursos = cursosEnMemoria.size();
            int totalBloques = cursosEnMemoria.stream().mapToInt(CursoDTO::getTotalBloques).sum();
            int totalPreguntas = cursosEnMemoria.stream().mapToInt(CursoDTO::getTotalPreguntas).sum();
            
            logger.info("✅ Cursos cargados exitosamente en memoria:");
            logger.info("   📚 Total de cursos: {}", totalCursos);
            logger.info("   📦 Total de bloques: {}", totalBloques);
            logger.info("   ❓ Total de preguntas: {}", totalPreguntas);
            
            // Log detallado de cada curso
            logger.debug("📊 Detalle de cursos cargados:");
            for (CursoDTO curso : cursosEnMemoria) {
                logger.debug("   - {}: {} bloques, {} preguntas", 
                           curso.getTitulo(), curso.getTotalBloques(), curso.getTotalPreguntas());
            }
            
            return new ArrayList<>(cursosEnMemoria);
            
        } catch (Exception e) {
            logger.error("❌ Error al cargar cursos completos: {}", e.getMessage());
            logger.debug("🔍 Stack trace completo:", e);
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
     * <p>Proceso de búsqueda:</p>
     * <ol>
     *   <li>Verificar si los cursos están cargados</li>
     *   <li>Cargar automáticamente si es necesario</li>
     *   <li>Buscar en el mapa de acceso rápido</li>
     *   <li>Retornar el curso encontrado o null</li>
     * </ol>
     * 
     * @param id Identificador único del curso
     * @return El curso completo desde memoria, o <code>null</code> si no existe
     */
    public CursoDTO obtenerCursoPorId(String id) {
        logger.debug("🔍 Buscando curso por ID: '{}'", id);
        
        if (!cursosCargados) {
            logger.debug("📚 Cursos no cargados, iniciando carga automática");
            cargarCursosCompletos();
        }
        
        CursoDTO curso = cursosPorId.get(id);
        
        if (curso != null) {
            logger.debug("✅ Curso encontrado: {} ({} bloques, {} preguntas)", 
                       curso.getTitulo(), curso.getTotalBloques(), curso.getTotalPreguntas());
        } else {
            logger.debug("❌ Curso no encontrado con ID: '{}'", id);
            logger.debug("📋 IDs disponibles: {}", cursosPorId.keySet());
        }
        
        return curso;
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
