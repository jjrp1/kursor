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
        logger.debug("Creando CursoPreviewService - Directorio: " + cursosDir);
        
        if (cursosDir == null || cursosDir.trim().isEmpty()) {
            logger.error("Error al crear CursoPreviewService: directorio no puede ser null o vacío");
            throw new IllegalArgumentException("Directorio de cursos no puede ser null o vacío");
        }
        
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.cursosDir = cursosDir.trim();
        
        logger.info("CursoPreviewService creado exitosamente - Directorio: " + this.cursosDir);
    }

    /**
     * Busca un archivo YAML en un directorio dado.
     * 
     * <p>Este método busca archivos con extensiones .yaml o .yml que coincidan
     * con el nombre base especificado. Si encuentra múltiples archivos,
     * usa el primero y registra un warning.</p>
     * 
     * @param directory Directorio donde buscar el archivo
     * @param baseName Nombre base del archivo (sin extensión)
     * @return El archivo YAML encontrado, o null si no se encuentra ninguno
     */
    private File findYamlFile(File directory, String baseName) {
        logger.debug("Buscando archivo YAML - Directorio: " + directory.getPath() + 
                    ", Nombre base: " + baseName);
        
        List<File> matchingFiles = YAML_EXTENSIONS.stream()
            .map(ext -> new File(directory, baseName + ext))
            .filter(File::exists)
            .collect(Collectors.toList());

        if (matchingFiles.isEmpty()) {
            logger.error("No se encontró el archivo YAML para el curso: " + baseName);
            return null;
        }

        if (matchingFiles.size() > 1) {
            String files = matchingFiles.stream()
                .map(File::getName)
                .collect(Collectors.joining(", "));
            logger.warn("Se encontraron múltiples archivos YAML para " + baseName + ": " + files + ". Usando el primero.");
        }

        File selectedFile = matchingFiles.get(0);
        logger.debug("Archivo YAML encontrado: " + selectedFile.getName());
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
        logger.info("Iniciando carga de curso completo - ID: " + id);
        
        if (id == null || id.trim().isEmpty()) {
            logger.error("Error al cargar curso: ID no puede ser null o vacío");
            return null;
        }
        
        File cursoDir = new File(cursosDir, id);
        File yamlFile = findYamlFile(cursoDir, id);
        
        if (yamlFile == null) {
            logger.error("No se encontró el archivo YAML para el curso: " + id);
            return null;
        }

        try {
            logger.debug("Leyendo archivo YAML: " + yamlFile.getAbsolutePath());
            Map<String, Object> cursoData = yamlMapper.readValue(yamlFile, Map.class);
            
            Curso curso = new Curso(
                id,
                (String) cursoData.get("titulo"),
                (String) cursoData.get("descripcion")
            );
            
            logger.info("Curso creado - ID: " + id + ", Título: " + curso.getTitulo());
            
            // Cargar bloques
            List<Map<String, Object>> bloquesData = (List<Map<String, Object>>) cursoData.get("bloques");
            if (bloquesData != null) {
                logger.debug("Cargando " + bloquesData.size() + " bloques para el curso: " + id);
                
                for (Map<String, Object> bloqueData : bloquesData) {
                    String bloqueId = (String) bloqueData.get("id");
                    String bloqueTitulo = (String) bloqueData.get("titulo");
                    String bloqueDescripcion = (String) bloqueData.get("descripcion");
                    String bloqueTipo = (String) bloqueData.get("tipo");
                    
                    logger.debug("Procesando bloque - ID: " + bloqueId + ", Título: " + bloqueTitulo);
                    
                    Bloque bloque = new Bloque(
                        curso.getId() + "-" + bloqueId,
                        bloqueTitulo,
                        bloqueDescripcion,
                        bloqueTipo
                    );
                    
                    // Cargar preguntas del bloque
                    List<Map<String, Object>> preguntasData = (List<Map<String, Object>>) bloqueData.get("preguntas");
                    if (preguntasData != null) {
                        logger.debug("Cargando " + preguntasData.size() + " preguntas para el bloque: " + bloqueId);
                        
                        for (Map<String, Object> preguntaData : preguntasData) {
                            String tipo = (String) preguntaData.get("tipo");
                            logger.debug("Procesando pregunta de tipo: " + tipo);
                            
                            try {
                                // Usar PreguntaFactory para crear la pregunta
                                Pregunta pregunta = PreguntaFactory.crearPregunta(preguntaData);
                                if (pregunta != null) {
                                    bloque.addPregunta(pregunta);
                                    logger.debug("Pregunta creada exitosamente - Tipo: " + tipo + 
                                               ", ID: " + pregunta.getId());
                                } else {
                                    String errorMsg = "La factory retornó null para la pregunta de tipo: " + tipo;
                                    logger.error(errorMsg);
                                    throw new RuntimeException(errorMsg);
                                }
                            } catch (Exception e) {
                                String errorMsg = "Error al crear pregunta de tipo " + tipo + ": " + e.getMessage();
                                logger.error(errorMsg);
                                throw new RuntimeException(errorMsg, e);
                            }
                        }
                    } else {
                        logger.debug("Bloque sin preguntas: " + bloqueId);
                    }

                    curso.addBloque(bloque);
                    logger.debug("Bloque agregado al curso - ID: " + bloque.getId() + 
                               ", Preguntas: " + bloque.getPreguntas().size());
                }
            } else {
                logger.warn("Curso sin bloques: " + id);
            }

            logger.info("Curso cargado exitosamente desde: " + yamlFile.getName() + 
                       " - Bloques: " + curso.getBloques().size() + 
                       ", Preguntas totales: " + curso.getNumeroPreguntas());
            return curso;

        } catch (Exception e) {
            logger.error("Error al cargar curso completo " + id + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene el directorio de cursos configurado.
     * 
     * @return La ruta del directorio de cursos
     */
    public String getCursosDir() {
        logger.debug("Obteniendo directorio de cursos: " + cursosDir);
        return cursosDir;
    }
    
    /**
     * Verifica si el directorio de cursos existe y es accesible.
     * 
     * @return true si el directorio existe y es accesible, false en caso contrario
     */
    public boolean isDirectorioValido() {
        File dir = new File(cursosDir);
        boolean valido = dir.exists() && dir.isDirectory() && dir.canRead();
        logger.debug("Verificando validez del directorio de cursos - Válido: " + valido);
        return valido;
    }
} 