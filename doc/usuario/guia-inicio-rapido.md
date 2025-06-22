# Guía de Inicio Rápido - Kursor

## 🎉 ¡PROYECTO COMPLETADO Y FUNCIONAL!

**✅ Todos los módulos están compilados y funcionando correctamente**

## 🚀 Primera Ejecución

### Paso 1: Instalación
1. **Descargar** el archivo ZIP de Kursor
2. **Extraer** en cualquier carpeta (ej: `C:\Kursor\`)
3. **Abrir** la carpeta extraída

### Paso 2: Ejecutar Aplicación
1. **Hacer doble clic** en `run.cmd` o `run.ps1`
2. **Esperar** a que se abra la ventana principal
3. **¡Listo!** La aplicación está lista para usar

### Paso 3: Seleccionar Curso
1. **Ver** la lista de cursos disponibles
2. **Hacer clic** en el curso que desees
3. **Leer** la descripción del curso
4. **Hacer clic** en "Comenzar"

## 🛠️ Para Desarrolladores

### Compilación del Proyecto
**Nota**: Todos los módulos ya están compilados y funcionando. Si necesitas recompilar:

1. **Compilar el core:**
   ```bash
   mvn clean install -pl kursor-core -am -DskipTests
   ```

2. **Compilar módulos de preguntas:**
   ```bash
   mvn clean package -pl kursor-flashcard-module,kursor-multiplechoice-module,kursor-truefalse-module,kursor-fillblanks-module -DskipTests
   ```

3. **Compilar estrategias:**
   ```bash
   mvn clean package -pl kursor-secuencial-strategy,kursor-aleatoria-strategy,kursor-repeticion-espaciada-strategy,kursor-repetir-incorrectas-strategy -DskipTests
   ```

4. **Copiar JARs a directorios correctos:**
   ```bash
   # Copiar módulos
   Copy-Item kursor-*-module/target/*.jar modules/
   
   # Copiar estrategias
   Copy-Item kursor-*-strategy/target/*.jar strategies/
   ```

### Ejecución desde Código Fuente
```bash
mvn exec:java -pl kursor-core -Dexec.mainClass="com.kursor.ui.KursorApplication"
```

## 🎯 Navegación Básica

### Durante el Curso
- **Barra de progreso**: Muestra tu avance
- **Título**: Nombre del curso y bloque actual
- **Pregunta**: Contenido según el tipo
- **Estrategia**: Indica qué estrategia de aprendizaje estás usando

### Estrategias de Aprendizaje Disponibles
- **Secuencial**: Preguntas en orden fijo (recomendado para principiantes)
- **Aleatoria**: Preguntas en orden aleatorio (ideal para repaso)
- **Repetición Espaciada**: Repite preguntas con intervalos optimizados (para memorización)
- **Repetir Incorrectas**: Repite automáticamente las preguntas falladas (para mejorar áreas débiles)

### Botones de Control
- **Verificar**: Comprueba tu respuesta
- **Siguiente**: Avanza a la siguiente pregunta
- **Anterior**: Vuelve a la pregunta anterior
- **Terminar**: Finaliza el curso

### Indicadores Visuales
- ✅ **Verde**: Respuesta correcta
- ❌ **Rojo**: Respuesta incorrecta
- ⚠️ **Amarillo**: Respuesta pendiente de verificar

## 📝 Tipos de Preguntas

### 1. Preguntas de Test
- **Vista**: Múltiples opciones (A, B, C, D)
- **Acción**: Hacer clic en la respuesta correcta
- **Verificar**: Hacer clic en "Verificar"

### 2. Verdadero/Falso
- **Vista**: Dos opciones (Verdadero/Falso)
- **Acción**: Seleccionar una opción
- **Verificar**: Hacer clic en "Verificar"

### 3. Completar Huecos
- **Vista**: Texto con espacios en blanco
- **Acción**: Escribir en los campos vacíos
- **Verificar**: Hacer clic en "Verificar"

### 4. Flashcards
- **Vista**: Tarjeta con pregunta/respuesta
- **Acción**: Hacer clic para ver respuesta
- **Navegación**: Usar "Siguiente" para continuar

## 🔧 Modo Debug

### Cuándo Usar
- Problemas con la aplicación
- Información detallada de errores
- Desarrollo de cursos

### Cómo Activar
```cmd
# En CMD
run.cmd DEBUG

# En PowerShell
run.ps1 DEBUG
```

### Información de Debug
- **Logs detallados** en `kursor-core/log/kursor.log`
- **Información de módulos** cargados
- **Errores específicos** con contexto

## ⚠️ Solución de Problemas

### ✅ Problemas Resueltos
Los siguientes problemas ya han sido solucionados:
- ✅ **Compilación de módulos**: Todos los módulos compilan correctamente
- ✅ **Carga de estrategias**: Las estrategias se cargan desde el directorio `strategies/`
- ✅ **Carga de módulos**: Los módulos se cargan desde el directorio `modules/`
- ✅ **Ejecución de cursos**: Los cursos se pueden realizar completamente
- ✅ **Persistencia**: El sistema de base de datos funciona correctamente

### La Aplicación No Se Abre
1. **Verificar** que estás en la carpeta correcta
2. **Ejecutar** como administrador
3. **Revisar** `kursor-core/log/kursor.log` para errores
4. **Intentar** modo debug: `run.cmd DEBUG`

### Error de Java
- **No debería ocurrir** (JRE incluido)
- **Verificar** que `jre/` existe en la carpeta
- **Reinstalar** si falta el JRE

### Cursos No Aparecen
1. **Verificar** que `cursos/` existe
2. **Comprobar** archivos YAML válidos
3. **Revisar** logs en modo debug

### Módulos No Se Cargan
1. **Verificar** que los JARs estén en `modules/`
2. **Verificar** que los JARs estén en `strategies/`
3. **Recompilar** si es necesario (ver sección de compilación)

## 📊 Seguimiento del Progreso

### Durante el Curso
- **Barra de progreso** muestra avance
- **Contador** de preguntas completadas
- **Indicadores** de respuestas correctas/incorrectas

### Al Finalizar
- **Resumen** de resultados
- **Porcentaje** de aciertos
- **Tiempo** total empleado
- **Preguntas** falladas para repasar

## 🎓 Consejos de Uso

### Para Mejor Rendimiento
1. **Leer** cuidadosamente cada pregunta
2. **Verificar** antes de avanzar
3. **Revisar** respuestas incorrectas
4. **Usar** modo debug si hay problemas

### Para Desarrolladores
1. **Crear** cursos en formato YAML
2. **Probar** con modo debug
3. **Revisar** logs para errores
4. **Documentar** problemas encontrados

## 🔍 Verificación de Instalación

### Verificar que Todo Esté Correcto
1. **Módulos compilados**: Verificar que `modules/` contiene los JARs
2. **Estrategias compiladas**: Verificar que `strategies/` contiene los JARs
3. **Cursos disponibles**: Verificar que `cursos/` contiene archivos YAML
4. **Logs limpios**: Verificar que no hay errores en los logs

### Comandos de Verificación
```bash
# Verificar módulos
ls modules/

# Verificar estrategias
ls strategies/

# Verificar cursos
ls cursos/

# Verificar logs
tail -n 20 kursor-core/log/kursor.log
```

---

**¡Disfruta aprendiendo con Kursor!** 🎉 

**✅ El proyecto está completamente funcional y listo para usar.** 