package com.kursor.ui.session;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.yaml.dto.SesionDTO;
import com.kursor.persistence.repository.SesionRepository;
import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.config.PersistenceConfig;
import javafx.stage.Stage;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la gestión de sesiones de aprendizaje.
 * 
 * <p>Esta clase maneja la lógica de negocio para cargar, mostrar y gestionar
 * el historial de sesiones de un curso específico. Proporciona una capa de
 * abstracción entre la vista y el repositorio de datos.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Carga de sesiones desde base de datos o datos ficticios</li>
 *   <li>Conversión entre entidades y DTOs</li>
 *   <li>Manejo de errores robusto</li>
 *   <li>Integración con el sistema de persistencia</li>
 *   <li>Fallback a datos ficticios cuando la base de datos no está disponible</li>
 * </ul>
 * 
 * <p>Flujo de trabajo típico:</p>
 * <ol>
 *   <li>Inicialización del repositorio de sesiones</li>
 *   <li>Carga de sesiones para un curso específico</li>
 *   <li>Conversión de entidades a DTOs</li>
 *   <li>Transformación de datos para la vista</li>
 *   <li>Actualización del modelo de vista</li>
 * </ol>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see SessionViewModel
 * @see SessionTableView
 * @see SesionRepository
 * @see CursoDTO
 * @see SesionDTO
 */
public class SessionController {
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
    private final SessionViewModel viewModel;
    private final SessionTableView sessionTableView;
    private SesionRepository sesionRepository;

    /**
     * Constructor para crear un controlador de sesiones.
     * 
     * <p>Inicializa el controlador con un modelo de vista y una tabla de sesiones.
     * También intenta inicializar el repositorio de sesiones para acceder a la
     * base de datos.</p>
     * 
     * @param owner La ventana propietaria para el contexto de la aplicación
     */
    public SessionController(Stage owner) {
        this.viewModel = new SessionViewModel();
        this.sessionTableView = new SessionTableView(viewModel);
        initializeRepository();
    }

    /**
     * Inicializa el repositorio de sesiones.
     * 
     * <p>Intenta crear una conexión con la base de datos a través del
     * PersistenceConfig. Si la configuración no está disponible o hay errores,
     * el repositorio se establece como null y se usarán datos ficticios.</p>
     */
    private void initializeRepository() {
        try {
            if (PersistenceConfig.isInitialized()) {
                this.sesionRepository = new SesionRepository(PersistenceConfig.createEntityManager());
                logger.info("SesionRepository inicializado correctamente");
            } else {
                logger.warn("PersistenceConfig no está inicializado, usando datos ficticios");
                this.sesionRepository = null;
            }
        } catch (Exception e) {
            logger.error("Error al inicializar SesionRepository", e);
            this.sesionRepository = null;
        }
    }

    /**
     * Carga las sesiones para un curso específico.
     * 
     * <p>Este método intenta cargar sesiones reales desde la base de datos.
     * Si no hay datos disponibles o hay errores, genera datos ficticios
     * basados en la estructura del curso.</p>
     * 
     * @param curso El curso para el cual cargar las sesiones
     */
    public void loadSessions(CursoDTO curso) {
        List<SessionViewModel.SessionData> sessions = new ArrayList<>();
        
        try {
            if (sesionRepository != null) {
                // Intentar cargar sesiones reales desde la base de datos
                List<Sesion> sesionesReales = sesionRepository.buscarSesionesPorCurso(curso.getId());
                
                if (!sesionesReales.isEmpty()) {
                    logger.info("Cargadas {} sesiones reales para curso: {}", sesionesReales.size(), curso.getTitulo());
                    
                    // Convertir entidades a DTOs
                    List<SesionDTO> sesionesDTO = convertirA_DTOs(sesionesReales, curso);
                    
                    // Convertir DTOs a datos de vista
                    sessions = convertirDTOsAVista(sesionesDTO);
                } else {
                    logger.info("No se encontraron sesiones reales para curso: {}, usando datos ficticios", curso.getTitulo());
                    sessions = createFictitiousData(curso);
                }
            } else {
                logger.info("Usando datos ficticios para curso: {}", curso.getTitulo());
                sessions = createFictitiousData(curso);
            }
        } catch (Exception e) {
            logger.error("Error al cargar sesiones para curso: {}", curso.getTitulo(), e);
            sessions = createFictitiousData(curso);
        }
        
        viewModel.setSessions(sessions);
    }

    /**
     * Convierte entidades Sesion a SesionDTO.
     * 
     * @param sesiones Lista de entidades Sesion
     * @param curso Curso para obtener información adicional
     * @return Lista de SesionDTO
     */
    private List<SesionDTO> convertirA_DTOs(List<Sesion> sesiones, CursoDTO curso) {
        return sesiones.stream().map(sesion -> {
            SesionDTO dto = new SesionDTO(
                sesion.getId(),
                sesion.getCursoId(),
                sesion.getBloqueId(),
                sesion.getEstrategiaTipo(),
                sesion.getFechaInicio()
            );
            
            // Copiar datos adicionales
            dto.setCursoTitulo(curso.getTitulo());
            dto.setFechaUltimaRevision(sesion.getFechaUltimaRevision());
            dto.setTiempoTotal(sesion.getTiempoTotal());
            dto.setPreguntasRespondidas(sesion.getPreguntasRespondidas());
            dto.setAciertos(sesion.getAciertos());
            dto.setTasaAciertos(sesion.getTasaAciertos());
            dto.setMejorRachaAciertos(sesion.getMejorRachaAciertos());
            dto.setPorcentajeCompletitud(sesion.getPorcentajeCompletitud());
            dto.setEstado(sesion.getEstado().toString());
            
            // Buscar título del bloque
            if (curso.getBloques() != null) {
                curso.getBloques().stream()
                    .filter(bloque -> bloque.getTitulo().equals(sesion.getBloqueId()))
                    .findFirst()
                    .ifPresent(bloque -> dto.setBloqueTitulo(bloque.getTitulo()));
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Convierte SesionDTO a SessionData para la vista.
     * 
     * @param sesionesDTO Lista de SesionDTO
     * @return Lista de SessionData
     */
    private List<SessionViewModel.SessionData> convertirDTOsAVista(List<SesionDTO> sesionesDTO) {
        return sesionesDTO.stream().map(dto -> {
            String fecha = dto.getFechaInicioFormateada();
            String bloque = dto.getBloqueTitulo() != null ? dto.getBloqueTitulo() : dto.getBloqueId();
            String correctas = dto.getAciertos() + "/" + dto.getPreguntasRespondidas();
            String pendientes = String.valueOf(dto.getPreguntasPendientes());
            
            return new SessionViewModel.SessionData(fecha, bloque, correctas, pendientes);
        }).collect(Collectors.toList());
    }

    /**
     * Crea datos ficticios para mostrar cuando no hay sesiones reales.
     * 
     * <p>Genera datos de ejemplo basados en la estructura del curso,
     * simulando sesiones previas con estadísticas realistas.</p>
     * 
     * @param curso El curso para el cual generar datos ficticios
     * @return Lista de datos de sesión ficticios
     */
    private List<SessionViewModel.SessionData> createFictitiousData(CursoDTO curso) {
        List<SessionViewModel.SessionData> sessions = new ArrayList<>();
        
        // Crear datos ficticios basados en el curso
        if (curso.getBloques() != null && !curso.getBloques().isEmpty()) {
            for (int i = 0; i < Math.min(3, curso.getBloques().size()); i++) {
                String bloqueTitulo = curso.getBloques().get(i).getTitulo();
                int preguntasBloque = curso.getBloques().get(i).getPreguntas().size();
                int aciertos = (int) (preguntasBloque * 0.8); // 80% de aciertos
                int pendientes = preguntasBloque - aciertos;
                
                sessions.add(new SessionViewModel.SessionData(
                    "2024-01-" + String.format("%02d", 15 - i), // Fechas ficticias
                    bloqueTitulo,
                    aciertos + "/" + preguntasBloque,
                    String.valueOf(pendientes)
                ));
            }
        } else {
            // Datos genéricos si no hay bloques
            sessions.add(new SessionViewModel.SessionData("2024-01-15", "Bloque 1", "8/10", "2"));
            sessions.add(new SessionViewModel.SessionData("2024-01-10", "Bloque 2", "6/8", "2"));
        }
        
        return sessions;
    }

    /**
     * Obtiene la vista de tabla de sesiones.
     * 
     * @return La tabla de sesiones configurada
     */
    public SessionTableView getSessionTableView() {
        return sessionTableView;
    }
} 
