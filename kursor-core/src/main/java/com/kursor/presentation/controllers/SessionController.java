package com.kursor.presentation.controllers;

import com.kursor.yaml.dto.CursoDTO;
import com.kursor.yaml.dto.SesionDTO;
import com.kursor.persistence.repository.SesionRepository;
import com.kursor.persistence.entity.Sesion;
import com.kursor.persistence.config.PersistenceConfig;
import com.kursor.presentation.viewmodels.SessionViewModel;
import com.kursor.presentation.views.SessionTableView;
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
     * Si no hay datos disponibles, muestra una fila indicando que no hay datos registrados.</p>
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
                    logger.info("No se encontraron sesiones reales para curso: {}, mostrando mensaje de sin datos", curso.getTitulo());
                    sessions = crearMensajeSinDatos();
                }
            } else {
                logger.info("Repositorio no disponible para curso: {}, mostrando mensaje de sin datos", curso.getTitulo());
                sessions = crearMensajeSinDatos();
            }
        } catch (Exception e) {
            logger.error("Error al cargar sesiones para curso: {}", curso.getTitulo(), e);
            sessions = crearMensajeSinDatos();
        }
        
        viewModel.setSessions(sessions);
    }

    /**
     * Crea un mensaje indicando que no hay datos registrados.
     * 
     * @return Lista con una sola fila indicando que no hay datos
     */
    private List<SessionViewModel.SessionData> crearMensajeSinDatos() {
        List<SessionViewModel.SessionData> sessions = new ArrayList<>();
        sessions.add(new SessionViewModel.SessionData(
            "-", 
            "(Sin datos registrados)", 
            "-", 
            "-"
        ));
        return sessions;
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
     * Obtiene la vista de tabla de sesiones.
     * 
     * @return La tabla de sesiones configurada
     */
    public SessionTableView getSessionTableView() {
        return sessionTableView;
    }
    
    /**
     * Obtiene el ViewModel del controlador de sesiones.
     * 
     * @return SessionViewModel del controlador
     */
    public SessionViewModel getViewModel() {
        return viewModel;
    }
} 
