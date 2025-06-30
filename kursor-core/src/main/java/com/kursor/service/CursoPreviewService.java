package com.kursor.service;

import com.kursor.domain.Curso;
import com.kursor.domain.Bloque;
import com.kursor.domain.Pregunta;
import com.kursor.factory.PreguntaFactory;
import com.kursor.yaml.dto.CursoPreviewDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Servicio para cargar información de cursos desde archivos YAML.
 * 
 * <p>Esta clase proporciona funcionalidad para cargar tanto vistas previas
 * como cursos completos desde archivos YAML ubicados en el sistema de archivos.
 * Utiliza Jackson YAML para el parsing y el ModuleManager para cargar preguntas
 * de diferentes tipos.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li><strong>Carga de vistas previas:</strong> Información básica de todos los cursos</li>
 *   <li><strong>Carga de cursos completos:</strong> Incluyendo bloques y preguntas</li>
 *   <li><strong>Soporte múltiple de formatos:</strong> .yaml y .yml</li>
 *   <li><strong>Integración con módulos:</strong> Usa ModuleManager para cargar preguntas</li>
 *   <li><strong>Manejo de errores robusto:</strong> Logging detallado de errores</li>
 * </ul>
 * 
 * <p>Estructura esperada de directorios:</p>
 * <pre>
 * cursos/
 * ├── curso_ingles/
 * │   └── curso_ingles.yaml
 * ├── curso_matematicas/
 * │   └── curso_matematicas.yml
 * └── ...
 * </pre>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * CursoPreviewService service = new CursoPreviewService("/path/to/cursos");
 * 
 * // Cargar vistas previas
 * List<CursoPreviewDTO> previews = service.cargarPreviews();
 * 
 * // Cargar curso completo
 * Curso curso = service.cargarCursoCompleto("curso_ingles");
 * }</pre>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see Curso
 * @see CursoPreviewDTO
 * @see ModuleManager
 * @see PreguntaModule
 */
public class CursoPreviewService {
    
    /** Logger para registrar eventos del servicio */
    private static final Logger logger = LoggerFactory.getLogger(CursoPreviewService.class);
    
    /** Mapper para procesar archivos YAML */
    private final ObjectMapper yamlMapper;
    
    /** Directorio donde se encuentran los cursos */
    private final String cursosDir;
    
    /** Extensiones de archivo YAML soportadas */
    private static final List<String> YAML_EXTENSIONS = Arrays.asList(".yaml", ".yml");
    
    /** Parser YAML para cargar archivos de configuración */
    private final Yaml yaml;
    
    /**
     * Constructor para crear el servicio de carga de cursos.
     * 
     * <p>Inicializa el servicio con el directorio de cursos especificado
     * y configura el mapper YAML para procesar los archivos de configuración.</p>
     * 
     * @param cursosDir Ruta al directorio que contiene los cursos (no debe ser null)
     * @throws IllegalArgumentException si el directorio es null o vacío
     */
    public CursoPreviewService(String cursosDir) {
        logger.debug("🚀 Iniciando construcción de CursoPreviewService");
        logger.debug("📁 Ruta de cursos: {}", cursosDir);
        
        if (cursosDir == null || cursosDir.trim().isEmpty()) {
            logger.error("Error al crear CursoPreviewService: directorio no puede ser null o vacío");
            throw new IllegalArgumentException("Directorio de cursos no puede ser null o vacío");
        }
        
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.cursosDir = cursosDir.trim();
        
        this.yaml = new Yaml();
        
        logger.info("🎉 CursoPreviewService creado exitosamente - Directorio: " + this.cursosDir);
    }

    /**
     * Busca un archivo YAML en un directorio específico.
     * 
     * <p>Este método busca archivos con extensiones .yaml o .yml en el directorio
     * especificado. Si encuentra múltiples archivos, usa el primero y registra
     * una advertencia.</p>
     * 
     * @param directory Directorio donde buscar el archivo
     * @param baseName Nombre base del archivo (sin extensión)
     * @return El archivo YAML encontrado, o null si no se encuentra ninguno
     */
    private File findYamlFile(File directory, String baseName) {
        logger.debug("🔍 Buscando archivo YAML en directorio: {}", directory.getPath());
        logger.debug("📋 Nombre base: {}", baseName);
        
        if (!directory.exists()) {
            logger.debug("❌ El directorio no existe: {}", directory.getPath());
            return null;
        }
        
        if (!directory.isDirectory()) {
            logger.debug("❌ La ruta no es un directorio: {}", directory.getPath());
            return null;
        }
        
        logger.debug("✅ Directorio validado correctamente");
        
        List<File> matchingFiles = new ArrayList<>();
        
        for (String extension : YAML_EXTENSIONS) {
            File file = new File(directory, baseName + extension);
            logger.debug("🔍 Verificando archivo: {}", file.getPath());
            
            if (file.exists() && file.isFile()) {
                matchingFiles.add(file);
                logger.debug("✅ Archivo encontrado: {}", file.getName());
            } else {
                logger.debug("❌ Archivo no encontrado: {}", file.getName());
            }
        }
        
        if (matchingFiles.isEmpty()) {
            logger.debug("❌ No se encontraron archivos YAML para: {}", baseName);
            return null;
        }
        
        if (matchingFiles.size() > 1) {
            String files = matchingFiles.stream()
                .map(File::getName)
                .collect(Collectors.joining(", "));
            logger.warn("⚠️  Se encontraron múltiples archivos YAML en {}: {}. Usando el primero.", 
                       directory.getPath(), files);
        }
        
        File selectedFile = matchingFiles.get(0);
        logger.debug("✅ Archivo YAML seleccionado: {}", selectedFile.getName());
        return selectedFile;
    }
    
    /**
     * Carga las vistas previas de todos los cursos disponibles.
     * 
     * <p>Este método recorre el directorio de cursos y carga la información
     * básica de cada curso (ID, título, descripción) desde sus archivos YAML.
     * Las vistas previas son útiles para mostrar listas de cursos disponibles
     * sin cargar toda la información detallada.</p>
     * 
     * <p>El método maneja automáticamente:</p>
     * <ul>
     *   <li>Directorios que no existen</li>
     *   <li>Archivos YAML corruptos o malformados</li>
     *   <li>Errores de lectura de archivos</li>
     *   <li>Múltiples extensiones de archivo (.yaml, .yml)</li>
     * </ul>
     * 
     * @return Lista de vistas previas de cursos disponibles. Si ocurre un error,
     *         retorna una lista vacía en lugar de lanzar una excepción
     */
    public List<CursoPreviewDTO> cargarPreviews() {
        logger.info("Iniciando carga de vistas previas de cursos - Directorio: " + cursosDir);
        
        List<CursoPreviewDTO> previews = new ArrayList<>();
        try {
            File dir = new File(cursosDir);
            if (!dir.exists() || !dir.isDirectory()) {
                String errorMessage = "El directorio de cursos no existe: " + cursosDir;
                logger.error(errorMessage);
                return previews;
            }
            
            File[] cursoDirs = dir.listFiles();
            if (cursoDirs == null) {
                logger.warn("No se pudieron listar archivos en el directorio: " + cursosDir);
                return previews;
            }
            
            logger.debug("Encontrados " + cursoDirs.length + " elementos en el directorio de cursos");
            
            for (File cursoDir : cursoDirs) {
                if (cursoDir.isDirectory()) {
                    String cursoId = cursoDir.getName();
                    logger.debug("Procesando directorio de curso: " + cursoId);
                    
                    File yamlFile = findYamlFile(cursoDir, cursoId);
                    
                    if (yamlFile != null) {
                        try {
                            Map<String, Object> cursoData = yamlMapper.readValue(yamlFile, Map.class);
                            CursoPreviewDTO preview = new CursoPreviewDTO(
                                cursoId,
                                (String) cursoData.get("titulo"),   
                                (String) cursoData.get("descripcion")
                            );
                            previews.add(preview);
                            logger.info("Cargado preview del curso: " + cursoId + " desde " + yamlFile.getName());
                        } catch (IOException e) {
                            logger.error("Error al leer el curso " + cursoId + ": " + e.getMessage());
                        }
                    } else {
                        logger.warn("No se encontró archivo YAML para el curso: " + cursoId);
                    }
                } else {
                    logger.debug("Ignorando archivo no-directorio: " + cursoDir.getName());
                }
            }
        } catch (Exception e) {
            logger.error("Error al cargar los cursos: " + e.getMessage());
        }
        
        logger.info("Carga de vistas previas completada - Cursos cargados: " + previews.size());
        return previews;
    }

    /**
     * Carga un curso completo por su identificador.
     * 
     * <p>Este método carga toda la información de un curso específico,
     * incluyendo bloques, preguntas y configuración detallada. Utiliza
     * el ModuleManager para cargar preguntas de diferentes tipos a través
     * de módulos especializados.</p>
     * 
     * <p>El proceso de carga incluye:</p>
     * <ol>
     *   <li>Búsqueda del archivo YAML del curso</li>
     *   <li>Parsing de metadatos del curso (título, descripción)</li>
     *   <li>Carga de bloques de contenido</li>
     *   <li>Carga de preguntas usando módulos especializados</li>
     *   <li>Validación de integridad del curso</li>
     * </ol>
     * 
     * <p>El método maneja automáticamente errores de carga y registra
     * información detallada en el logger para debugging.</p>
     * 
     * @param id Identificador único del curso a cargar
     * @return El curso completo cargado, o null si el curso no existe
     *         o ocurre un error durante la carga
     */
    public Curso cargarCursoCompleto(String id) {
        logger.info("🚀 Iniciando carga de curso completo - ID: {}", id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("❌ Error al cargar curso: ID no puede ser null o vacío");
            return null;
        }
        
        logger.debug("📁 Buscando archivo YAML para el curso: {}", id);
        File cursoDir = new File(cursosDir, id);
        File yamlFile = findYamlFile(cursoDir, id);
        
        if (yamlFile == null) {
            logger.error("❌ No se encontró el archivo YAML para el curso: {}", id);
            return null;
        }
        
        logger.debug("✅ Archivo YAML encontrado: {}", yamlFile.getAbsolutePath());

        try {
            logger.debug("📖 Leyendo contenido del archivo YAML");
            Map<String, Object> cursoData = yamlMapper.readValue(yamlFile, Map.class);
            
            if (cursoData == null) {
                logger.error("❌ El archivo YAML está vacío o es inválido: {}", yamlFile.getName());
                return null;
            }
            
            logger.debug("✅ Contenido YAML cargado correctamente");
            logger.debug("📋 Estructura de datos: {}", cursoData.keySet());
            
            // Extraer metadatos del curso
            String titulo = (String) cursoData.get("titulo");
            String descripcion = (String) cursoData.get("descripcion");
            
            logger.debug("🔍 Metadatos del curso extraídos:");
            logger.debug("   - Título: '{}'", titulo);
            logger.debug("   - Descripción: '{}'", descripcion);
            
            if (titulo == null || titulo.trim().isEmpty()) {
                logger.error("❌ Título del curso no puede ser null o vacío en archivo: {}", yamlFile.getName());
                return null;
            }
            
            // Crear curso
            logger.debug("🔄 Creando instancia de Curso");
            Curso curso = new Curso(id, titulo.trim(), descripcion != null ? descripcion.trim() : null);
            logger.debug("✅ Curso creado exitosamente: {}", curso.getTitulo());
            
            // Cargar bloques
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> bloquesData = (List<Map<String, Object>>) cursoData.get("bloques");
            
            if (bloquesData != null && !bloquesData.isEmpty()) {
                logger.debug("📦 Cargando {} bloques para el curso", bloquesData.size());
                
                for (int i = 0; i < bloquesData.size(); i++) {
                    Map<String, Object> bloqueData = bloquesData.get(i);
                    logger.debug("📝 Procesando bloque {} de {}", i + 1, bloquesData.size());
                    
                    try {
                        Bloque bloque = cargarBloque(bloqueData, curso.getId());
                        curso.addBloque(bloque);
                        logger.debug("✅ Bloque agregado al curso: {} ({} preguntas)", 
                                   bloque.getTitulo(), bloque.getPreguntas().size());
                    } catch (Exception e) {
                        logger.error("❌ Error al cargar bloque {}: {}", i + 1, e.getMessage());
                        logger.debug("🔍 Stack trace del bloque:", e);
                        // Continuar con el siguiente bloque
                    }
                }
                
                logger.debug("✅ Procesamiento de bloques completado");
            } else {
                logger.warn("⚠️  No se encontraron bloques en el curso: {}", curso.getTitulo());
            }
            
            logger.info("🎉 Curso cargado exitosamente: {} ({} bloques, {} preguntas totales)", 
                       curso.getTitulo(), curso.getBloques().size(), curso.getNumeroPreguntas());
            
            return curso;
            
        } catch (Exception e) {
            logger.error("❌ Error al cargar curso desde {}: {}", yamlFile.getName(), e.getMessage());
            logger.debug("🔍 Stack trace completo:", e);
            return null;
        }
    }
    
    /**
     * Carga un bloque completo desde datos YAML.
     * 
     * <p>Este método crea un bloque con todas sus preguntas a partir de
     * los datos YAML proporcionados. Utiliza el PreguntaFactory para
     * crear las preguntas específicas según su tipo.</p>
     * 
     * <p>Proceso de carga del bloque:</p>
     * <ol>
     *   <li>Extraer metadatos del bloque</li>
     *   <li>Validar campos obligatorios</li>
     *   <li>Crear instancia del bloque</li>
     *   <li>Cargar preguntas del bloque</li>
     *   <li>Agregar preguntas al bloque</li>
     * </ol>
     * 
     * @param bloqueData Datos YAML del bloque
     * @param cursoId ID del curso al que pertenece el bloque
     * @return Bloque completo con sus preguntas
     * @throws RuntimeException si hay un error durante la carga
     */
    private Bloque cargarBloque(Map<String, Object> bloqueData, String cursoId) {
        logger.debug("🚀 Iniciando carga de bloque desde datos YAML");
        logger.debug("📋 Datos del bloque: {}", bloqueData);
        
        // Extraer metadatos del bloque
        String bloqueId = (String) bloqueData.get("id");
        String titulo = (String) bloqueData.get("titulo");
        String descripcion = (String) bloqueData.get("descripcion");
        String tipo = (String) bloqueData.get("tipo");
        
        logger.debug("🔍 Metadatos del bloque extraídos:");
        logger.debug("   - ID: '{}'", bloqueId);
        logger.debug("   - Título: '{}'", titulo);
        logger.debug("   - Descripción: '{}'", descripcion);
        logger.debug("   - Tipo: '{}'", tipo);
        
        if (bloqueId == null || bloqueId.trim().isEmpty()) {
            logger.error("❌ ID del bloque no puede ser null o vacío");
            throw new RuntimeException("ID del bloque no puede ser null o vacío");
        }
        
        if (titulo == null || titulo.trim().isEmpty()) {
            logger.error("❌ Título del bloque no puede ser null o vacío - ID: {}", bloqueId);
            throw new RuntimeException("Título del bloque no puede ser null o vacío");
        }
        
        // Crear bloque
        logger.debug("🔄 Creando instancia de Bloque");
        String bloqueIdCompleto = cursoId + "-" + bloqueId;
        Bloque bloque = new Bloque(bloqueIdCompleto, titulo.trim(), 
                                  descripcion != null ? descripcion.trim() : null,
                                  tipo != null ? tipo.trim() : null);
        logger.debug("✅ Bloque creado exitosamente: {}", bloque.getTitulo());
        
        // Cargar preguntas del bloque
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> preguntasData = (List<Map<String, Object>>) bloqueData.get("preguntas");
        
        if (preguntasData != null) {
            logger.debug("📝 Cargando {} preguntas para el bloque: {}", preguntasData.size(), bloqueId);
            
            for (int i = 0; i < preguntasData.size(); i++) {
                Map<String, Object> preguntaData = preguntasData.get(i);
                logger.debug("🔍 Procesando pregunta {} de {} del bloque {}", i + 1, preguntasData.size(), bloqueId);
                
                String preguntaTipo = (String) preguntaData.get("tipo");
                // Si la pregunta no tiene campo 'tipo', usar el tipo del bloque
                if (preguntaTipo == null || preguntaTipo.isEmpty()) {
                    preguntaData.put("tipo", tipo);
                    preguntaTipo = tipo;
                    logger.debug("🔄 Usando tipo del bloque para pregunta: {}", preguntaTipo);
                }
                
                logger.debug("📋 Tipo de pregunta: {}", preguntaTipo);
                
                try {
                    // Usar PreguntaFactory para crear la pregunta
                    logger.debug("🔄 Delegando creación de pregunta al PreguntaFactory");
                    Pregunta pregunta = PreguntaFactory.crearPregunta(preguntaData);
                    
                    if (pregunta != null) {
                        bloque.addPregunta(pregunta);
                        logger.debug("✅ Pregunta agregada al bloque - Tipo: {}, ID: {}", 
                                   preguntaTipo, pregunta.getId());
                    } else {
                        String errorMsg = "La factory retornó null para la pregunta de tipo: " + preguntaTipo;
                        logger.error("❌ {}", errorMsg);
                        throw new RuntimeException(errorMsg);
                    }
                } catch (Exception e) {
                    String errorMsg = "Error al crear pregunta de tipo " + preguntaTipo + ": " + e.getMessage();
                    logger.error("❌ {}", errorMsg);
                    logger.debug("🔍 Stack trace de la pregunta:", e);
                    throw new RuntimeException(errorMsg, e);
                }
            }
            
            logger.debug("✅ Procesamiento de preguntas del bloque completado");
        } else {
            logger.warn("⚠️  No se encontraron preguntas en el bloque: {}", bloqueId);
        }
        
        logger.info("🎉 Bloque cargado exitosamente: {} ({} preguntas)", 
                   bloque.getTitulo(), bloque.getPreguntas().size());
        
        return bloque;
    }
    
    /**
     * Obtiene el directorio de cursos configurado.
     * 
     * <p>Este método retorna la ruta del directorio de cursos que fue
     * configurada durante la inicialización del servicio.</p>
     * 
     * @return La ruta del directorio de cursos
     */
    public String getCursosDir() {
        logger.debug("📁 Obteniendo directorio de cursos: {}", cursosDir);
        return cursosDir;
    }
    
    /**
     * Verifica si el directorio de cursos existe y es accesible.
     * 
     * <p>Este método realiza una validación completa del directorio de cursos,
     * verificando que exista, sea un directorio y sea legible.</p>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li><strong>Existencia:</strong> El directorio debe existir</li>
     *   <li><strong>Tipo:</strong> Debe ser un directorio, no un archivo</li>
     *   <li><strong>Permisos:</strong> Debe ser legible</li>
     * </ul>
     * 
     * @return true si el directorio existe y es accesible, false en caso contrario
     */
    public boolean isDirectorioValido() {
        logger.debug("🔍 Verificando validez del directorio de cursos: {}", cursosDir);
        
        File dir = new File(cursosDir);
        
        boolean existe = dir.exists();
        boolean esDirectorio = dir.isDirectory();
        boolean esLegible = dir.canRead();
        
        logger.debug("📋 Resultados de validación:");
        logger.debug("   - Existe: {}", existe);
        logger.debug("   - Es directorio: {}", esDirectorio);
        logger.debug("   - Es legible: {}", esLegible);
        
        boolean valido = existe && esDirectorio && esLegible;
        
        if (valido) {
            logger.debug("✅ Directorio de cursos válido");
        } else {
            logger.warn("⚠️  Directorio de cursos inválido - Existe: {}, Es directorio: {}, Es legible: {}", 
                       existe, esDirectorio, esLegible);
        }
        
        return valido;
    }
    
    /**
     * Carga todos los cursos completos desde el directorio de cursos.
     * 
     * <p>Este método escanea el directorio de cursos, busca archivos YAML
     * en subdirectorios y carga cada curso completo con todos sus bloques y preguntas.</p>
     * 
     * <p>Proceso de carga:</p>
     * <ol>
     *   <li>Escaneo del directorio de cursos</li>
     *   <li>Búsqueda de subdirectorios de cursos</li>
     *   <li>Carga individual de cada curso desde su subdirectorio</li>
     *   <li>Validación de integridad</li>
     *   <li>Registro de estadísticas</li>
     * </ol>
     * 
     * <p>El método maneja automáticamente los errores de carga y registra
     * información detallada sobre el proceso.</p>
     * 
     * @return Lista de cursos completos cargados
     * @throws RuntimeException si hay un error durante la carga
     */
    public List<Curso> cargarTodosLosCursosCompletos() {
        logger.info("🔄 Iniciando carga de todos los cursos completos");
        logger.debug("📁 Escaneando directorio: {}", cursosDir);
        
        List<Curso> cursos = new ArrayList<>();
        File directorioCursos = new File(cursosDir);
        
        if (!directorioCursos.exists()) {
            logger.error("❌ El directorio de cursos no existe: {}", cursosDir);
            throw new RuntimeException("Directorio de cursos no encontrado: " + cursosDir);
        }
        
        if (!directorioCursos.isDirectory()) {
            logger.error("❌ La ruta especificada no es un directorio: {}", cursosDir);
            throw new RuntimeException("La ruta no es un directorio: " + cursosDir);
        }
        
        logger.debug("✅ Directorio de cursos validado correctamente");
        
        // Buscar subdirectorios de cursos (no archivos YAML directamente)
        File[] cursoDirs = directorioCursos.listFiles(File::isDirectory);
        
        if (cursoDirs == null || cursoDirs.length == 0) {
            logger.warn("⚠️  No se encontraron subdirectorios de cursos en: {}", cursosDir);
            return cursos;
        }
        
        logger.debug("📋 Subdirectorios de cursos encontrados: {}", cursoDirs.length);
        for (File cursoDir : cursoDirs) {
            logger.debug("   - {}", cursoDir.getName());
        }
        
        int cursosCargados = 0;
        int errores = 0;
        
        for (File cursoDir : cursoDirs) {
            String cursoId = cursoDir.getName();
            logger.debug("📖 Procesando directorio de curso: {}", cursoId);
            
            try {
                logger.debug("🔍 Buscando archivo YAML en subdirectorio: {}", cursoId);
                Curso curso = cargarCursoCompleto(cursoId);
                
                if (curso != null) {
                    cursos.add(curso);
                    cursosCargados++;
                    logger.debug("✅ Curso cargado exitosamente: {} ({} bloques, {} preguntas)", 
                               curso.getTitulo(), curso.getBloques().size(), 
                               curso.getBloques().stream().mapToInt(b -> b.getPreguntas().size()).sum());
                } else {
                    errores++;
                    logger.warn("⚠️  El curso no pudo ser cargado: {}", cursoId);
                }
                
            } catch (Exception e) {
                errores++;
                logger.error("❌ Error al cargar curso {}: {}", cursoId, e.getMessage());
                logger.debug("🔍 Stack trace completo:", e);
            }
        }
        
        logger.info("✅ Carga de cursos completada:");
        logger.info("   📚 Cursos cargados exitosamente: {}", cursosCargados);
        logger.info("   ❌ Errores durante la carga: {}", errores);
        logger.info("   📊 Total de subdirectorios procesados: {}", cursoDirs.length);
        
        return cursos;
    }
} 
