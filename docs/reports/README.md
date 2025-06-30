# 📊 Sistema de Reportes Automáticos - Kursor

Este directorio contiene los reportes automáticos de testing y cobertura de código generados por el sistema CI/CD del proyecto Kursor.

## 🔄 Funcionamiento Automático

### **GitHub Actions Pipeline**

El sistema funciona a través de dos workflows principales:

1. **CI Workflow** (`.github/workflows/ci.yml`)
   - Ejecuta todos los tests del proyecto
   - Genera reportes de cobertura con Jacoco
   - Genera reportes de tests con Surefire
   - Genera archivo JSON con métricas de testing
   - Sube los reportes como artifacts

2. **Pages Workflow** (`.github/workflows/pages.yml`)
   - Descarga los artifacts del CI
   - Procesa los reportes XML
   - Genera datos JSON para la web
   - Publica en GitHub Pages

### **Generación de Datos**

El sistema puede generar datos de testing de dos formas:

#### **1. Datos Reales (CI/CD)**
Los scripts `scripts/generate-test-data.sh` y `scripts/generate-real-test-data.ps1` procesan los reportes y generan:

```json
{
  "timestamp": "2025-06-30T15:45:04Z",
  "commit": {
    "hash": "9e66a60",
    "message": "feat: Implementar sistema automático de reportes"
  },
  "tests": {
    "total": 42,
    "passed": 40,
    "failed": 2,
    "skipped": 0,
    "success_rate": 95,
    "execution_time": "2.3"
  },
  "coverage": {
    "average": 92,
    "modules_count": 9
  },
  "modules": [...],
  "status": "success"
}
```

#### **2. Datos de Ejemplo (Fallback)**
Si no hay reportes reales disponibles, se usan datos de ejemplo basados en la estructura del proyecto.

## 📁 Estructura de Directorios

```
docs/reports/
├── README.md                     # Este archivo
├── data/
│   ├── test-metrics.json        # Datos reales generados por CI
│   └── test-metrics-fallback.json  # Datos de ejemplo
├── coverage/
│   └── jacoco/                  # Reportes HTML de Jacoco
│       └── index.html
└── tests/
    └── surefire-reports/        # Reportes XML/HTML de Surefire
        └── *.xml
```

## 🎯 Acceso a los Reportes

### **Página Web Dinámica**
- **URL**: [resultados-pruebas.html](../resultados-pruebas.html)
- **Funcionalidad**: Carga automática de datos reales
- **Fallback**: Muestra datos de ejemplo si no hay datos reales

### **Reportes Detallados**
- **Jacoco**: Reportes de cobertura de código
- **Surefire**: Reportes detallados de tests
- **Codecov**: Integración con Codecov para análisis de cobertura

## 🛠️ Configuración Local

### **Generar Datos de Testing Localmente**

#### **Windows (PowerShell) - Datos Reales**
```powershell
# Generar datos de testing reales (ejecuta tests con Maven)
.\scripts\generate-real-test-data.ps1

# Actualizar y subir cambios con datos reales
.\scripts\update-real-test-data.ps1

# Probar la página localmente
.\scripts\test-page-local.ps1
```

#### **Windows (PowerShell) - Datos Ficticios**
```powershell
# Generar datos de testing ficticios (sin ejecutar tests)
.\scripts\generate-fake-test-data.ps1

# Actualizar y subir cambios con datos ficticios
.\scripts\update-fake-test-data.ps1
```

#### **Linux/macOS (Bash) - Datos Reales**
```bash
# Generar datos de testing reales
./scripts/generate-test-data.sh

# Ejecutar tests con cobertura
mvn clean test jacoco:report
```

### **Configuración de Maven**

El plugin de Jacoco está configurado en el `pom.xml` principal:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 🔧 Solución de Problemas

### **"No se pudieron cargar los datos de testing automáticos"**

Este mensaje aparece cuando:

1. **No hay datos reales**: Los workflows de CI no se han ejecutado recientemente
2. **Error en la generación**: Problemas con los scripts de generación
3. **Archivo no encontrado**: El archivo `test-metrics.json` no existe

#### **Soluciones:**

1. **Generar datos reales**:
   ```powershell
   .\scripts\generate-real-test-data.ps1
   ```

2. **Generar datos ficticios como fallback**:
   ```powershell
   .\scripts\generate-fake-test-data.ps1
   ```

3. **Verificar workflows de CI**:
   - Ir a GitHub Actions
   - Verificar que el workflow `ci.yml` se ejecute correctamente
   - Revisar logs de errores

4. **Actualizar manualmente**:
   ```powershell
   .\scripts\update-real-test-data.ps1
   ```

### **Errores de Compilación en Tests**

Si hay errores de compilación en los tests:

1. **Ejecutar solo compilación**:
   ```bash
   mvn clean compile
   ```

2. **Revisar errores específicos**:
   ```bash
   mvn test -X
   ```

3. **Corregir problemas de código** antes de ejecutar tests

### **Maven no encontrado**

Si el script de datos reales falla por Maven:

1. **Instalar Maven**: Descargar desde https://maven.apache.org/download.cgi
2. **Verificar PATH**: Asegurar que Maven esté en el PATH del sistema
3. **Usar datos ficticios**: Como alternativa temporal

## 📈 Métricas Incluidas

### **Tests**
- Total de tests ejecutados
- Tests exitosos/fallidos/omitidos
- Tasa de éxito
- Tiempo de ejecución

### **Cobertura**
- Cobertura promedio del proyecto
- Cobertura por módulo
- Líneas cubiertas vs totales

### **Información del Build**
- Hash del commit
- Mensaje del commit
- Timestamp de generación
- Estado del build

## 🚀 Contribuir

Para contribuir al sistema de reportes:

1. **Ejecutar tests localmente** antes de hacer push
2. **Verificar que los workflows de CI pasen**
3. **Actualizar datos de testing** si es necesario
4. **Documentar cambios** en este README

## 📞 Soporte

Si tienes problemas con el sistema de reportes:

1. Revisar los logs de GitHub Actions
2. Verificar la configuración de Maven
3. Ejecutar los scripts localmente para debug
4. Crear un issue en el repositorio con detalles del problema

---

> **Nota**: Este sistema se actualiza automáticamente con cada cambio en el repositorio. Los datos mostrados reflejan el estado real del proyecto en tiempo de build. 