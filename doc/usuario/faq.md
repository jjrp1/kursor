# Preguntas Frecuentes (FAQ) - Kursor

## 🚀 Instalación y Ejecución

### ¿Por qué no se abre la aplicación?
**Posibles causas:**
1. **Carpeta incorrecta**: Asegúrate de ejecutar desde la carpeta donde está `kursor.jar`
2. **Permisos**: Intenta ejecutar como administrador
3. **Antivirus**: Algunos antivirus bloquean aplicaciones Java
4. **JRE corrupto**: Verifica que `jre/` existe y está completo

**Solución:**
```cmd
# Ejecutar como administrador
run.cmd DEBUG
```

### ¿Necesito instalar Java?
**No.** Kursor incluye un JRE portable en la carpeta `jre/`. No necesitas instalar Java en tu sistema.

### ¿Por qué aparece "JavaFX runtime components are missing"?
**Causa:** Los módulos de JavaFX no se encuentran.

**Solución:**
1. Verificar que `lib/` contiene los archivos JavaFX
2. Ejecutar desde la carpeta correcta
3. Usar modo debug para más información

### ¿La aplicación modifica mi sistema?
**No.** Kursor es completamente portable:
- ✅ No instala nada en el sistema
- ✅ No modifica el registro de Windows
- ✅ No requiere permisos de administrador
- ✅ Todo se ejecuta desde la carpeta

## 🔧 Configuración y Debug

### ¿Cómo activar el modo debug?
```cmd
# En CMD
run.cmd DEBUG

# En PowerShell
run.ps1 DEBUG
```

### ¿Dónde están los logs?
- **Ubicación**: `log/kursor.log`
- **Formato**: Texto plano, legible
- **Contenido**: Información de ejecución y errores

### ¿Cómo cambiar el nivel de logging?
1. Abrir `config/logback.xml`
2. Cambiar `<property name="LOG_LEVEL" value="INFO"/>` a `DEBUG`
3. Guardar y reiniciar la aplicación

### ¿Por qué no veo información de debug?
1. Verificar que ejecutaste con `DEBUG`
2. Comprobar que `config/logback.xml` existe
3. Revisar `log/kursor.log` para mensajes

## 📚 Cursos y Contenido

### ¿Por qué no aparecen cursos?
**Posibles causas:**
1. **Carpeta vacía**: `cursos/` no contiene archivos YAML
2. **Formato incorrecto**: Los archivos YAML tienen errores
3. **Permisos**: No se puede leer la carpeta

**Solución:**
```cmd
# Verificar contenido
dir cursos

# Ejecutar con debug
run.cmd DEBUG
```

### ¿Puedo agregar mis propios cursos?
**Sí.** Los cursos se crean en formato YAML:
1. Crear archivo `.yaml` en `cursos/`
2. Seguir el formato de ejemplo
3. Reiniciar la aplicación

### ¿Qué formatos de curso soporta?
- **Test**: Preguntas de opción múltiple
- **Verdadero/Falso**: Preguntas booleanas
- **Completar huecos**: Texto con espacios
- **Flashcards**: Tarjetas de estudio

### ¿Puedo compartir mis cursos?
**Sí.** Los archivos YAML son portables:
1. Copiar archivo `.yaml` de `cursos/`
2. Compartir con otros usuarios
3. Colocar en su carpeta `cursos/`

## 🎯 Uso de la Aplicación

### ¿Cómo navegar entre preguntas?
- **Siguiente**: Avanza a la siguiente pregunta
- **Anterior**: Vuelve a la pregunta anterior
- **Verificar**: Comprueba la respuesta actual
- **Terminar**: Finaliza el curso

### ¿Puedo volver a preguntas anteriores?
**Sí.** Usa el botón "Anterior" para navegar hacia atrás.

### ¿Cómo sé si mi respuesta es correcta?
1. Hacer clic en "Verificar"
2. **Verde**: Respuesta correcta
3. **Rojo**: Respuesta incorrecta
4. **Amarillo**: Pendiente de verificar

### ¿Se guarda mi progreso?
**Actualmente no.** El progreso se reinicia al cerrar la aplicación.

## ⚠️ Problemas Comunes

### Error: "Could not find or load main class"
**Causa:** Problema con el classpath o JAR.

**Solución:**
1. Verificar que `kursor.jar` existe
2. Ejecutar desde la carpeta correcta
3. Usar modo debug para más información

### Error: "Access is denied"
**Causa:** Permisos insuficientes.

**Solución:**
1. Ejecutar como administrador
2. Verificar permisos de la carpeta
3. Desactivar temporalmente el antivirus

### La aplicación se cierra inesperadamente
**Solución:**
1. Ejecutar con modo debug: `run.cmd DEBUG`
2. Revisar `log/kursor.log` para errores
3. Verificar que todos los archivos están presentes

### Caracteres extraños en los logs
**Causa:** Problema de codificación UTF-8.

**Solución:**
1. Verificar configuración en `config/logback.xml`
2. Asegurar que `-Dfile.encoding=UTF-8` está configurado
3. Usar PowerShell en lugar de CMD

## 🔄 Actualizaciones

### ¿Cómo actualizar Kursor?
1. Descargar nueva versión
2. Reemplazar archivos (mantener `cursos/` personalizados)
3. Ejecutar nueva versión

### ¿Se pierden mis cursos al actualizar?
**No.** Los cursos en `cursos/` se mantienen si no los sobrescribes.

## 📞 Soporte Técnico

### ¿Dónde reportar errores?
1. Revisar esta FAQ primero
2. Ejecutar con modo debug
3. Incluir contenido de `log/kursor.log`
4. Contactar al equipo de desarrollo

### ¿Qué información incluir al reportar un error?
- **Versión**: Kursor v1.0.0
- **Sistema**: Windows 10/11
- **Logs**: Contenido de `log/kursor.log`
- **Pasos**: Cómo reproducir el error
- **Configuración**: Modificaciones realizadas

## Preguntas Frecuentes

### ¿Cuántas estrategias de aprendizaje están disponibles?
Kursor ofrece **4 estrategias de aprendizaje** diferentes:
- **Secuencial**: Preguntas en orden fijo
- **Aleatoria**: Preguntas en orden aleatorio  
- **Repetición Espaciada**: Repite preguntas con intervalos optimizados
- **Repetir Incorrectas**: Repite automáticamente las preguntas falladas

### ¿Cuál es la mejor estrategia para principiantes?
Para principiantes se recomienda la **Estrategia Secuencial** porque:
- Proporciona estructura y orden
- Es fácil de seguir y entender
- Ideal para aprender conceptos nuevos
- Progreso predecible y lineal

### ¿Cuál es la mejor estrategia para repaso?
Para repaso se recomienda la **Estrategia Aleatoria** porque:
- Evita la memorización por orden
- Mejora la retención real
- Ideal para preparar exámenes
- Desafía la memoria de manera impredecible

### ¿Cuál es la mejor estrategia para memorización?
Para memorización se recomienda la **Estrategia de Repetición Espaciada** porque:
- Optimiza el momento de repaso
- Basada en principios científicos de la curva del olvido
- Mejora la retención a largo plazo
- Intervalo configurable según necesidades

### ¿Cuál es la mejor estrategia para mejorar áreas débiles?
Para mejorar áreas débiles se recomienda la **Estrategia de Repetir Incorrectas** porque:
- Enfoque específico en conceptos difíciles
- Mejora progresiva en áreas problemáticas
- Sesiones más intensivas pero efectivas
- Dos fases: originales + repetición de incorrectas

---

**¿No encuentras tu pregunta?** Ejecuta `run.cmd DEBUG` y revisa los logs para más información. 