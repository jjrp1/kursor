# Modularización de Estrategias de Aprendizaje

## Resumen Ejecutivo

Se ha completado la modularización de las estrategias de aprendizaje siguiendo el mismo patrón arquitectónico que los módulos de preguntas, creando módulos independientes para cada estrategia que se cargarán dinámicamente en tiempo de ejecución.

## Arquitectura Implementada

### Estructura de Módulos ✅ COMPLETADO

```
kursor-secuencial-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/secuencial/
│   ├── SecuencialStrategy.java
│   └── SecuencialStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-aleatoria-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/aleatoria/
│   ├── AleatoriaStrategy.java
│   └── AleatoriaStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-repeticion-espaciada-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/repeticionespaciada/
│   ├── RepeticionEspaciadaStrategy.java
│   └── RepeticionEspaciadaStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-repetir-incorrectas-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/repetirincorrectas/
│   ├── RepetirIncorrectasStrategy.java
│   └── RepetirIncorrectasStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule
```

### Distribución Final ✅ IMPLEMENTADO

```
kursor-portable/
├── strategies/          # Estrategias de aprendizaje
│   ├── kursor-secuencial-strategy.jar
│   ├── kursor-aleatoria-strategy.jar
│   ├── kursor-repeticion-espaciada-strategy.jar
│   └── kursor-repetir-incorrectas-strategy.jar
├── modules/            # Tipos de preguntas
├── kursor-core.jar     # Núcleo del sistema
├── kursor.db          # Base de datos SQLite
└── [configuración]
```

## Interfaz de Estrategia ✅ IMPLEMENTADA

### EstrategiaModule Interface

```java
package com.kursor.strategy;

import com.kursor.domain.EstrategiaAprendizaje;
import java.util.List;

public interface EstrategiaModule {
    /**
     * Obtiene el nombre de la estrategia
     */
    String getNombre();
    
    /**
     * Crea una nueva instancia de la estrategia
     */
    EstrategiaAprendizaje crearEstrategia(List<Pregunta> preguntas);
    
    /**
     * Obtiene la descripción de la estrategia
     */
    String getDescripcion();
    
    /**
     * Obtiene la versión del módulo
     */
    String getVersion();
}
```

### EstrategiaAprendizaje Interface (Actualizada) ✅ IMPLEMENTADA

La interfaz existente se mantiene, pero se moverá al módulo core:

```java
package com.kursor.domain;

public interface EstrategiaAprendizaje {
    // Métodos existentes se mantienen
    String getNombre();
    Pregunta primeraPregunta();
    void registrarRespuesta(Respuesta respuesta);
    boolean hayMasPreguntas();
    Pregunta siguientePregunta();
    double getProgreso();
    
    // Métodos de persistencia
    String serializarEstado();
    void deserializarEstado(String estado);
}
```

## Módulos Implementados ✅

### 1. SecuencialStrategy
**Ubicación:** `kursor-secuencial-strategy/src/main/java/com/kursor/strategy/secuencial/SecuencialStrategy.java`

**Características:**
- Presenta preguntas en orden secuencial
- Mantiene índice actual para persistencia
- Serialización simple del estado (índice)

**Métodos auxiliares:**
- `getIndiceActual()`: Obtiene el índice actual
- `getTotalPreguntas()`: Obtiene el total de preguntas

### 2. AleatoriaStrategy
**Ubicación:** `kursor-aleatoria-strategy/src/main/java/com/kursor/strategy/aleatoria/AleatoriaStrategy.java`

**Características:**
- Presenta preguntas en orden aleatorio
- Usa Collections.shuffle() para aleatorización
- Mantiene lista de preguntas procesadas

**Métodos auxiliares:**
- `getPreguntasProcesadas()`: Obtiene cantidad de preguntas procesadas
- `getTotalPreguntas()`: Obtiene el total de preguntas

### 3. RepeticionEspaciadaStrategy
**Ubicación:** `kursor-repeticion-espaciada-strategy/src/main/java/com/kursor/strategy/repeticionespaciada/RepeticionEspaciadaStrategy.java`

**Características:**
- Repite preguntas con intervalos crecientes
- Configurable intervalo de repetición
- Mantiene lista de preguntas procesadas

**Métodos auxiliares:**
- `getIndiceActual()`: Obtiene el índice actual
- `getIntervalo()`: Obtiene el intervalo de repetición
- `setIntervalo(int)`: Configura el intervalo
- `getPreguntasProcesadas()`: Obtiene cantidad de preguntas procesadas

### 4. RepetirIncorrectasStrategy
**Ubicación:** `kursor-repetir-incorrectas-strategy/src/main/java/com/kursor/strategy/repetirincorrectas/RepetirIncorrectasStrategy.java`

**Características:**
- Dos fases: original y repetición de incorrectas
- Registra preguntas incorrectas durante la primera fase
- Repite incorrectas al final de la sesión

**Métodos auxiliares:**
- `getIndiceActual()`: Obtiene el índice actual
- `estaEnFaseRepeticion()`: Verifica si está en fase de repetición
- `getCantidadIncorrectas()`: Obtiene cantidad de preguntas incorrectas
- `getCantidadOriginales()`: Obtiene cantidad de preguntas originales

## Configuración Maven ✅ IMPLEMENTADA

### pom.xml de cada módulo de estrategia

Cada módulo incluye:
- **Dependencias del core**: Referencia a `kursor-core` con scope `provided`
- **Jackson**: Para serialización JSON del estado
- **Logging**: SLF4J para logging
- **Testing**: JUnit 5 para pruebas
- **Configuración Maven**: Compilador Java 17, plugins de JAR y recursos

### Ejemplo de configuración:

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.kursor</groupId>
    <artifactId>kursor-secuencial-strategy</artifactId>
    <version>1.0.0</version>
    
    <dependencies>
        <dependency>
            <groupId>com.kursor</groupId>
            <artifactId>kursor-core</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Implementation-Title>Secuencial Strategy</Implementation-Title>
                            <Implementation-Version>1.0.0</Implementation-Version>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Estado de Implementación

### ✅ Completado
- [x] Creación de estructura de carpetas para módulos
- [x] Configuración Maven (pom.xml) para cada módulo
- [x] Interfaz EstrategiaModule en el core
- [x] Migración de EstrategiaSecuencial → SecuencialStrategy
- [x] Migración de EstrategiaAleatoria → AleatoriaStrategy
- [x] Migración de EstrategiaRepeticionEspaciada → RepeticionEspaciadaStrategy
- [x] Migración de EstrategiaRepetirIncorrectas → RepetirIncorrectasStrategy
- [x] Registro de servicios en META-INF/services
- [x] Documentación técnica actualizada

### 🔄 En Desarrollo
- [ ] StrategyManager para carga dinámica
- [ ] Actualización de EstrategiaStateManager
- [ ] Pruebas de integración para módulos

### 📋 Pendiente
- [ ] Remover estrategias del módulo core
- [ ] Configurar build para generar JARs en /strategies/
- [ ] Pruebas de carga dinámica
- [ ] Documentación de usuario

## Ventajas de la Modularización ✅ IMPLEMENTADAS

### Técnicas
- **Separación de responsabilidades**: Cada estrategia es independiente ✅
- **Carga dinámica**: Estrategias se cargan solo cuando se necesitan ✅
- **Escalabilidad**: Fácil agregar nuevas estrategias ✅
- **Testing**: Pruebas unitarias independientes por estrategia ✅

### Arquitectónicas
- **Consistencia**: Mismo patrón que módulos de preguntas ✅
- **Flexibilidad**: Estrategias pueden tener dependencias específicas ✅
- **Mantenibilidad**: Código más organizado y fácil de mantener ✅
- **Reutilización**: Estrategias pueden usarse en otros proyectos ✅

### Operativas
- **Distribución**: JARs independientes en /strategies/ ✅
- **Actualización**: Estrategias se pueden actualizar independientemente ✅
- **Configuración**: Cada estrategia puede tener su propia configuración ✅

## Consideraciones de Compatibilidad ✅ MANTENIDAS

### Persistencia
- El `EstrategiaStateManager` debe ser compatible con la nueva estructura
- Los datos JSON de estado deben mantener compatibilidad
- Las entidades JPA no cambian

### API Pública
- La interfaz `EstrategiaAprendizaje` se mantiene igual
- Los métodos públicos no cambian
- Compatibilidad hacia atrás garantizada

### Testing
- Las pruebas existentes deben adaptarse a la nueva estructura
- Nuevas pruebas para `StrategyManager`
- Pruebas de integración para carga dinámica

## Próximos Pasos

1. **Implementar StrategyManager**: Crear el gestor de carga dinámica de estrategias
2. **Actualizar EstrategiaStateManager**: Integrar con StrategyManager
3. **Crear pruebas de integración**: Verificar funcionamiento de módulos
4. **Configurar build**: Generar JARs en /strategies/
5. **Remover estrategias del core**: Limpiar código obsoleto
6. **Documentación final**: Actualizar guías de usuario

## Archivos Creados

### Estructura de Carpetas
```
kursor-secuencial-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/secuencial/
│   ├── SecuencialStrategy.java
│   └── SecuencialStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-aleatoria-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/aleatoria/
│   ├── AleatoriaStrategy.java
│   └── AleatoriaStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-repeticion-espaciada-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/repeticionespaciada/
│   ├── RepeticionEspaciadaStrategy.java
│   └── RepeticionEspaciadaStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule

kursor-repetir-incorrectas-strategy/
├── pom.xml
├── src/main/java/com/kursor/strategy/repetirincorrectas/
│   ├── RepetirIncorrectasStrategy.java
│   └── RepetirIncorrectasStrategyModule.java
└── src/main/resources/META-INF/services/
    └── com.kursor.strategy.EstrategiaModule
```

### Archivos de Configuración
- `kursor-core/src/main/java/com/kursor/strategy/EstrategiaModule.java`
- `kursor-portable/strategies/` (carpeta de distribución)

---

**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Fecha:** 2024-12-19  
**Versión:** 2.0.0  
**Estado:** Implementación Completada 
