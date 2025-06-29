package com.kursor.presentation.controllers;

import com.kursor.domain.Bloque;
import com.kursor.presentation.dialogs.BloqueAdapter;
import com.kursor.presentation.dialogs.CardSelectorModal;
import com.kursor.presentation.dialogs.EstrategiaAdapter;
import com.kursor.presentation.dialogs.SelectableItem;
import com.kursor.strategy.EstrategiaModule;
import com.kursor.shared.util.StrategyManager;
import com.kursor.yaml.dto.BloqueDTO;
import com.kursor.yaml.dto.CursoDTO;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Gestor de ejecución de cursos que coordina todo el flujo de realización.
 * 
 * <p>Este gestor maneja la secuencia completa de pasos necesarios para iniciar
 * un curso, incluyendo la selección de bloque y estrategia como pasos previos
 * antes de mostrar el diálogo de realización del curso.</p>
 * 
 * <p>Flujo de trabajo:</p>
 * <ol>
 *   <li>Selección de bloque (CardSelectorModal con BloqueAdapter)</li>
 *   <li>Selección de estrategia (CardSelectorModal con EstrategiaAdapter)</li>
 *   <li>Inicio del curso (CursoInterfaceController)</li>
 * </ol>
 * 
 * <p>Si en cualquier paso el usuario cancela, se notifica y se vuelve a la
 * ventana principal sin iniciar el curso.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 * @see CardSelectorModal
 * @see BloqueAdapter
 * @see EstrategiaAdapter
 * @see CursoInterfaceController
 */
public class CursoExecutionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(CursoExecutionManager.class);
    
    /** Ventana padre para los modales */
    private final Window owner;
    
    /** Curso a ejecutar */
    private final CursoDTO curso;
    
    /** Resultados de los pasos previos */
    private BloqueDTO bloqueSeleccionado;
    private String estrategiaSeleccionada;
    
    /**
     * Constructor del gestor de ejecución.
     * 
     * @param owner Ventana padre para los modales
     * @param curso Curso a ejecutar
     */
    public CursoExecutionManager(Window owner, CursoDTO curso) {
        this.owner = owner;
        this.curso = curso;
        
        logger.info("CursoExecutionManager creado para curso: {}", curso.getTitulo());
    }
    
    /**
     * Ejecuta el flujo completo de realización del curso.
     * 
     * <p>Este método coordina todos los pasos necesarios para iniciar un curso.
     * Si en cualquier paso el usuario cancela, se retorna false y se debe
     * volver a la ventana principal.</p>
     * 
     * @return true si el curso se inició correctamente, false si se canceló en algún paso
     */
    public boolean ejecutarCurso() {
        logger.info("Iniciando flujo de ejecución para curso: {}", curso.getTitulo());
        
        try {
            // Paso 1: Selección de bloque
            if (!seleccionarBloque()) {
                logger.info("Usuario canceló en la selección de bloque");
                return false;
            }
            
            // Paso 2: Selección de estrategia
            if (!seleccionarEstrategia()) {
                logger.info("Usuario canceló en la selección de estrategia");
                return false;
            }
            
            // Paso 3: Inicio del curso
            if (!iniciarCurso()) {
                logger.info("No se pudo iniciar el curso");
                return false;
            }
            
            logger.info("Flujo de ejecución completado exitosamente para curso: {}", curso.getTitulo());
            return true;
            
        } catch (Exception e) {
            logger.error("Error durante el flujo de ejecución del curso: {}", curso.getTitulo(), e);
            return false;
        }
    }
    
    /**
     * Paso 1: Selección de bloque usando CardSelectorModal.
     * 
     * @return true si se seleccionó un bloque, false si se canceló
     */
    private boolean seleccionarBloque() {
        logger.debug("Paso 1: Mostrando selector de bloques con CardSelectorModal");
        
        try {
            // Convertir bloques a SelectableItem usando adaptadores
            List<SelectableItem> items = curso.getBloques().stream()
                .map(bloque -> new BloqueAdapter(convertirABloque(bloque)))
                .collect(Collectors.toList());
            
            // Crear y mostrar el modal genérico
            CardSelectorModal modal = new CardSelectorModal(
                "Seleccionar Bloque",
                "Elige el bloque de contenido que quieres estudiar:",
                items
            );
            
            CompletableFuture<SelectableItem> future = modal.mostrarYEsperar();
            SelectableItem itemSeleccionado = future.get(); // Esperar resultado
            
            if (itemSeleccionado != null && itemSeleccionado instanceof BloqueAdapter) {
                Bloque bloque = ((BloqueAdapter) itemSeleccionado).getBloque();
                bloqueSeleccionado = convertirABloqueDTO(bloque);
                logger.info("Bloque seleccionado: {}", bloqueSeleccionado.getTitulo());
                return true;
            } else {
                logger.info("Usuario canceló la selección de bloque");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error al mostrar selector de bloques", e);
            return false;
        }
    }
    
    /**
     * Paso 2: Selección de estrategia usando CardSelectorModal.
     * 
     * @return true si se seleccionó una estrategia, false si se canceló
     */
    private boolean seleccionarEstrategia() {
        logger.debug("Paso 2: Mostrando selector de estrategias con CardSelectorModal");
        
        try {
            // Obtener estrategias disponibles
            List<EstrategiaModule> estrategias = StrategyManager.getInstance().getStrategies();
            
            // Convertir estrategias a SelectableItem usando adaptadores
            List<SelectableItem> items = estrategias.stream()
                .map(EstrategiaAdapter::new)
                .collect(Collectors.toList());
            
            // Crear y mostrar el modal genérico
            CardSelectorModal modal = new CardSelectorModal(
                "Seleccionar Estrategia",
                "Elige la estrategia de aprendizaje que prefieres:",
                items
            );
            
            CompletableFuture<SelectableItem> future = modal.mostrarYEsperar();
            SelectableItem itemSeleccionado = future.get(); // Esperar resultado
            
            if (itemSeleccionado != null && itemSeleccionado instanceof EstrategiaAdapter) {
                EstrategiaModule estrategia = ((EstrategiaAdapter) itemSeleccionado).getEstrategia();
                estrategiaSeleccionada = estrategia.getNombre();
                logger.info("Estrategia seleccionada: {}", estrategiaSeleccionada);
                return true;
            } else {
                logger.info("Usuario canceló la selección de estrategia");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error al mostrar selector de estrategias", e);
            return false;
        }
    }
    
    /**
     * Paso 3: Inicio del curso.
     * 
     * @return true si el curso se inició correctamente, false en caso contrario
     */
    private boolean iniciarCurso() {
        logger.debug("Paso 3: Iniciando curso con bloque '{}' y estrategia '{}'", 
                    bloqueSeleccionado.getTitulo(), estrategiaSeleccionada);
        
        try {
            CursoInterfaceController controller = new CursoInterfaceController(owner);
            boolean success = controller.iniciarCurso(curso, estrategiaSeleccionada, bloqueSeleccionado);
            
            if (success) {
                logger.info("Curso iniciado correctamente");
                return true;
            } else {
                logger.warn("No se pudo iniciar el curso");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error al iniciar el curso", e);
            return false;
        }
    }
    
    /**
     * Convierte un BloqueDTO a Bloque del dominio.
     * 
     * @param bloqueDTO El BloqueDTO a convertir
     * @return El Bloque del dominio
     */
    private Bloque convertirABloque(BloqueDTO bloqueDTO) {
        Bloque bloque = new Bloque();
        
        // Generar ID automáticamente si no existe
        String id = bloqueDTO.getId();
        if (id == null || id.trim().isEmpty()) {
            id = "bloque_" + System.currentTimeMillis() + "_" + bloqueDTO.getTitulo().hashCode();
        }
        bloque.setId(id);
        
        bloque.setTitulo(bloqueDTO.getTitulo());
        
        // Manejar descripción null
        String descripcion = bloqueDTO.getDescripcion();
        if (descripcion == null) {
            descripcion = "Bloque de " + bloqueDTO.getTitulo();
        }
        bloque.setDescripcion(descripcion);
        
        bloque.setTipo(bloqueDTO.getTipo());
        // Las preguntas se cargarán cuando sea necesario
        return bloque;
    }
    
    /**
     * Convierte un Bloque del dominio a BloqueDTO.
     * 
     * @param bloque El Bloque del dominio a convertir
     * @return El BloqueDTO
     */
    private BloqueDTO convertirABloqueDTO(Bloque bloque) {
        BloqueDTO bloqueDTO = new BloqueDTO();
        bloqueDTO.setId(bloque.getId());
        bloqueDTO.setTitulo(bloque.getTitulo());
        bloqueDTO.setDescripcion(bloque.getDescripcion());
        bloqueDTO.setTipo(bloque.getTipo());
        return bloqueDTO;
    }
    
    /**
     * Obtiene el bloque seleccionado.
     * 
     * @return Bloque seleccionado, o null si no se ha seleccionado ninguno
     */
    public BloqueDTO getBloqueSeleccionado() {
        return bloqueSeleccionado;
    }
    
    /**
     * Obtiene la estrategia seleccionada.
     * 
     * @return Estrategia seleccionada, o null si no se ha seleccionado ninguna
     */
    public String getEstrategiaSeleccionada() {
        return estrategiaSeleccionada;
    }
    
    /**
     * Obtiene el curso asociado.
     * 
     * @return El curso
     */
    public CursoDTO getCurso() {
        return curso;
    }
} 