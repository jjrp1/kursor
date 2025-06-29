package com.kursor.util;

import com.kursor.persistence.config.PersistenceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de prueba para cargar datos de prueba en la base de datos.
 * 
 * <p>Esta clase se puede ejecutar directamente para cargar datos de ejemplo
 * que permiten probar la funcionalidad de la tabla de sesiones.</p>
 * 
 * @author Juan José Ruiz Pérez <jjrp1@um.es>
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestDataLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataLoader.class);
    
    /**
     * Método principal para ejecutar la carga de datos de prueba.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        logger.info("=== CARGADOR DE DATOS DE PRUEBA ===");
        
        try {
            // Inicializar configuración de persistencia
            logger.info("Inicializando configuración de persistencia...");
            PersistenceConfig.initialize();
            
            if (!PersistenceConfig.isInitialized()) {
                logger.error("No se pudo inicializar la configuración de persistencia");
                System.exit(1);
            }
            
            logger.info("Configuración de persistencia inicializada correctamente");
            
            // Mostrar estadísticas iniciales
            logger.info("Estadísticas iniciales:");
            DataLoader.mostrarEstadisticas();
            
            // Preguntar al usuario qué acción realizar
            if (args.length > 0) {
                String accion = args[0].toLowerCase();
                
                switch (accion) {
                    case "cargar":
                        cargarDatos();
                        break;
                    case "limpiar":
                        limpiarDatos();
                        break;
                    case "estadisticas":
                        mostrarEstadisticas();
                        break;
                    default:
                        mostrarAyuda();
                        break;
                }
            } else {
                // Sin argumentos, mostrar menú interactivo
                mostrarMenuInteractivo();
            }
            
        } catch (Exception e) {
            logger.error("Error durante la ejecución", e);
            System.exit(1);
        } finally {
            // Cerrar configuración de persistencia
            try {
                PersistenceConfig.shutdown();
                logger.info("Configuración de persistencia cerrada");
            } catch (Exception e) {
                logger.error("Error al cerrar configuración de persistencia", e);
            }
        }
        
        logger.info("=== FIN DEL CARGADOR DE DATOS ===");
    }
    
    /**
     * Carga datos de prueba en la base de datos.
     */
    private static void cargarDatos() {
        logger.info("Cargando datos de prueba...");
        
        boolean exito = DataLoader.cargarDatosDePrueba();
        
        if (exito) {
            logger.info("✅ Datos de prueba cargados exitosamente");
            logger.info("Estadísticas después de la carga:");
            DataLoader.mostrarEstadisticas();
        } else {
            logger.error("❌ Error al cargar datos de prueba");
        }
    }
    
    /**
     * Limpia todos los datos de la base de datos.
     */
    private static void limpiarDatos() {
        logger.warn("⚠️  Limpiando todos los datos de la base de datos...");
        
        boolean exito = DataLoader.limpiarDatos();
        
        if (exito) {
            logger.info("✅ Datos limpiados exitosamente");
            logger.info("Estadísticas después de la limpieza:");
            DataLoader.mostrarEstadisticas();
        } else {
            logger.error("❌ Error al limpiar datos");
        }
    }
    
    /**
     * Muestra estadísticas de la base de datos.
     */
    private static void mostrarEstadisticas() {
        logger.info("Mostrando estadísticas de la base de datos...");
        DataLoader.mostrarEstadisticas();
    }
    
    /**
     * Muestra el menú interactivo para el usuario.
     */
    private static void mostrarMenuInteractivo() {
        logger.info("Menú de opciones:");
        logger.info("1. Cargar datos de prueba");
        logger.info("2. Limpiar todos los datos");
        logger.info("3. Mostrar estadísticas");
        logger.info("4. Salir");
        logger.info("");
        logger.info("Para usar argumentos de línea de comandos:");
        logger.info("  java TestDataLoader cargar    - Cargar datos de prueba");
        logger.info("  java TestDataLoader limpiar   - Limpiar todos los datos");
        logger.info("  java TestDataLoader estadisticas - Mostrar estadísticas");
        logger.info("");
        logger.info("Ejecutando carga de datos de prueba por defecto...");
        
        // Por defecto, cargar datos
        cargarDatos();
    }
    
    /**
     * Muestra la ayuda con las opciones disponibles.
     */
    private static void mostrarAyuda() {
        logger.info("Uso: java TestDataLoader [comando]");
        logger.info("");
        logger.info("Comandos disponibles:");
        logger.info("  cargar       - Cargar datos de prueba en la base de datos");
        logger.info("  limpiar      - Limpiar todos los datos de la base de datos");
        logger.info("  estadisticas - Mostrar estadísticas de la base de datos");
        logger.info("");
        logger.info("Ejemplos:");
        logger.info("  java TestDataLoader cargar");
        logger.info("  java TestDataLoader limpiar");
        logger.info("  java TestDataLoader estadisticas");
    }
} 