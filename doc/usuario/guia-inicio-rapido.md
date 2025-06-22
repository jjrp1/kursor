# Guía de Inicio Rápido - Kursor

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

## 🎯 Navegación Básica

### Durante el Curso
- **Barra de progreso**: Muestra tu avance
- **Título**: Nombre del curso y bloque actual
- **Pregunta**: Contenido según el tipo

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
- **Logs detallados** en `log/kursor.log`
- **Información de módulos** cargados
- **Errores específicos** con contexto

## ⚠️ Solución de Problemas

### La Aplicación No Se Abre
1. **Verificar** que estás en la carpeta correcta
2. **Ejecutar** como administrador
3. **Revisar** `log/kursor.log` para errores
4. **Intentar** modo debug: `run.cmd DEBUG`

### Error de Java
- **No debería ocurrir** (JRE incluido)
- **Verificar** que `jre/` existe en la carpeta
- **Reinstalar** si falta el JRE

### Cursos No Aparecen
1. **Verificar** que `cursos/` existe
2. **Comprobar** archivos YAML válidos
3. **Revisar** logs en modo debug

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

---

**¡Disfruta aprendiendo con Kursor!** 🎉 