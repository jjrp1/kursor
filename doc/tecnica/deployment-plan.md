# Plan de despliegue de la aplicación (deployment-plan)

El proyecto consta de los siguientes módulos (JARs):

1. **kursor-core** (ubicación: `kursor-core/target/kursor-core-1.0-SNAPSHOT.jar`)
   - Módulo base que contiene la lógica común
   - Dependencias: JavaFX, Jackson (YAML)

2. **kursor-ui** (ubicación: `kursor-ui/target/kursor-ui-1.0-SNAPSHOT.jar`)
   - Interfaz gráfica de usuario
   - Depende de kursor-core
   - Incluye todas las dependencias (shaded jar)

3. **Módulos de preguntas** (ubicación: `modules/`):
   - `kursor-fillblanks-module-1.0-SNAPSHOT.jar`
   - `kursor-flashcard-module-1.0-SNAPSHOT.jar`
   - `kursor-multiplechoice-module-1.0-SNAPSHOT.jar`
   - `kursor-truefalse-module-1.0-SNAPSHOT.jar`

Para la distribución, necesitaremos:

1. **Estructura de carpetas**:
```
kursor/
├── bin/                    # Scripts de ejecución
├── lib/                    # Bibliotecas
│   ├── kursor-ui.jar       # JAR principal con todas las dependencias
│   └── kursor-core.jar     # JAR del core
├── modules/                # Módulos de preguntas
│   ├── kursor-fillblanks-module-1.0-SNAPSHOT.jar
│   ├── kursor-flashcard-module-1.0-SNAPSHOT.jar
│   ├── kursor-multiplechoice-module-1.0-SNAPSHOT.jar
│   └── kursor-truefalse-module-1.0-SNAPSHOT.jar
├── cursos/                 # Cursos en formato YAML
├── log/                    # Directorio para logs
└── README.md               # Documentación
```

2. **Scripts de ejecución**:
   - `bin/run.bat` para Windows
   - `bin/run.sh` para Linux/Mac

¡Perfecto! Para preparar tu proyecto Java multi-módulo para su despliegue en sistemas Windows, te recomiendo los siguientes pasos, adaptados a tu estructura y entorno:

## Preparación para distribución

### 1. **Compilar y empaquetado**

Generar todos los JARs en las carpetas `target` correspondientes:  
`mvn clean install`
 
---

### 2. **Empaquetado de la Aplicación Principal**

El módulo principal de interfaz (`kursor-ui`)  
tiene configurado el plugin `jpackage-maven-plugin`  
para crear un instalador nativo de Windows.  

Esto te permite generar un instalador `.exe` o una carpeta auto-contenida con todo lo necesario para ejecutar la aplicación, incluyendo el runtime de Java si lo deseas.

#### **Comando para generar el instalador:**
```powershell
mvn -pl kursor-ui jpackage:jpackage
```
Esto generará el instalador en `kursor-ui\target\dist`.

---

### 3. **Verifica los recursos y módulos**

- Los cursos (`cursos/`) y los módulos (`modules/`) ya están configurados para copiarse al empaquetado final.
- Los logs se generarán en la carpeta `log` en la raíz del proyecto (asegúrate de que exista o que la aplicación la cree).

---

### 4. **Distribución**

- El instalador generado (`.exe`) en `kursor-ui\target\dist` es lo que debes distribuir.
- Si prefieres un ZIP portable, puedes comprimir la carpeta generada por `jpackage` (normalmente llamada `Kursor` o similar).

---

### 5. **Prueba en un entorno limpio**

Antes de distribuir, prueba el instalador o el ZIP en una máquina Windows donde no tengas Java instalado, para asegurarte de que el runtime se incluya correctamente.

---

### 6. **Resumen de archivos importantes para el despliegue**

- Instalador: `kursor-ui\target\dist\Kursor-1.0.0.exe` (o similar)
- Alternativamente, carpeta portable: `kursor-ui\target\dist\Kursor\` (o similar)

## ZIP portable

### 1. **Compilación y Empaquetado**

`mvn clean install`  
Generar los JARs en sus carpetas `target` 

### 2. **Crear el ZIP Portable**

Se construye un script en PowerShell que empaquete todo lo necesario en un ZIP:  
- Copiará el JAR principal (`kursor-ui-1.0.0.jar`) y sus dependencias.
- Copiará los módulos de ejercicios desde la carpeta `modules`.
- Copiará los cursos desde la carpeta `cursos`.
- Creará una carpeta `log` para los logs.
- Creará un archivo `run.bat` para ejecutar la aplicación fácilmente.
- Comprimirá todo en un archivo ZIP.

```bash
.\create-portable-zip.ps1
```
