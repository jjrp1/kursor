# Kursor - Plataforma de Formación Interactiva

## 🚀 Inicio Rápido

### Instalación
1. **Extraer** el archivo ZIP en cualquier carpeta
2. **Ejecutar** `run.cmd` o `run.ps1`
3. **¡Listo!** La aplicación se abrirá automáticamente

### Requisitos
- ✅ Windows 10 o superior
- ✅ **No requiere Java instalado** (incluido en `jre/`)

## 📁 Estructura de Archivos

```
kursor/
├── kursor.jar                    # Aplicación principal
├── jre/                          # Java Runtime (portable)
├── lib/                          # Bibliotecas JavaFX
├── modules/                      # Tipos de preguntas
├── cursos/                       # Cursos disponibles
├── config/                       # Configuración
├── log/                          # Archivos de log
├── doc/                          # Documentación
├── run.cmd                       # Ejecutar (CMD)
└── run.ps1                       # Ejecutar (PowerShell)
```

## 🎯 Cómo Usar

### Ejecutar Aplicación
- **Normal**: `run.cmd` o `run.ps1`
- **Con debug**: `run.cmd DEBUG` o `run.ps1 DEBUG`

### Navegación
1. **Seleccionar curso** de la lista principal
2. **Hacer clic** en "Comenzar"
3. **Responder preguntas** usando los controles
4. **Navegar** con "Anterior" y "Siguiente"
5. **Verificar** respuestas antes de continuar

### Tipos de Preguntas
- **Test**: Seleccionar una respuesta correcta
- **Verdadero/Falso**: Marcar verdadero o falso
- **Completar huecos**: Rellenar espacios en blanco
- **Flashcards**: Tarjetas de estudio

## 🆘 Soporte

### Documentación Completa
- **Manual de usuario**: `doc/manual-usuario.md`
- **Guía de inicio**: `doc/guia-inicio-rapido.md`
- **FAQ**: `doc/faq.md`

### Solución de Problemas
- **Logs**: Revisar `log/kursor.log`
- **Debug**: Ejecutar con parámetro `DEBUG`
- **Configuración**: Modificar `config/logback.xml`

## ⚙️ Configuración

### Nivel de Logging
- **Normal**: Solo información importante
- **Debug**: Información detallada para problemas

Para cambiar el nivel:
1. Editar `config/logback.xml`
2. Cambiar `<property name="LOG_LEVEL" value="INFO"/>` a `DEBUG`

### Cursos Personalizados
Los cursos se almacenan en formato YAML en `cursos/`. 
Consulte la documentación técnica para crear cursos propios.

## 📞 Contacto

Para soporte técnico o reportar problemas:
- Revisar `doc/faq.md` primero
- Verificar logs en `log/kursor.log`
- Contactar al equipo de desarrollo

---

**Kursor v1.0.0** - Plataforma de Formación Interactiva 