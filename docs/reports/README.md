# 📊 Sistema de Reportes Automáticos - Kursor

Este directorio contiene los reportes automáticos de testing y cobertura de código generados por el sistema CI/CD del proyecto Kursor.

## 🔄 Funcionamiento Automático

### **GitHub Actions Pipeline**

El sistema funciona a través de dos workflows principales:

1. **CI Workflow** (`.github/workflows/ci.yml`)
   - Ejecuta todos los tests del proyecto
   - Genera reportes de cobertura con Jacoco
   - Genera reportes de tests con Surefire
   - Sube los reportes como artifacts

2. **Pages Workflow** (`.github/workflows/pages.yml`)
   - Descarga los artifacts del CI
   - Procesa los reportes XML
   - Genera datos JSON para la web
   - Publica en GitHub Pages

### **Generación de Datos**

El script `scripts/generate-test-data.sh` procesa los reportes y genera:

```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "commit": {
    "hash": "abc123",
    "message": "Fix testing issues"
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
- **Fallback**: Datos de ejemplo si no hay reportes

### **Reportes Detallados**
- **Jacoco Coverage**: `reports/coverage/jacoco/index.html`
- **Surefire Tests**: `reports/tests/surefire-reports/`
- **Codecov**: [codecov.io/gh/jjrp1/kursor](https://codecov.io/gh/jjrp1/kursor)

## 🔧 Configuración Local

Para probar el sistema localmente:

```bash
# Ejecutar tests y generar reportes
mvn clean test jacoco:report surefire-report:report

# Generar datos JSON
./scripts/generate-test-data.sh > docs/reports/data/test-metrics.json

# Servir la documentación localmente
cd docs && python -m http.server 8000
# Visitar: http://localhost:8000/resultados-pruebas.html
```

## 📈 Métricas Monitoreadas

### **Cobertura de Código**
- **Objetivo**: >90% en módulos core
- **Mínimo**: >80% en todos los módulos
- **Herramientas**: Jacoco + Codecov

### **Tests de Calidad**
- **Tests unitarios**: Dominio y lógica de negocio
- **Tests de integración**: Persistencia y servicios
- **Tests de módulos**: Plugins de preguntas y estrategias

### **Métricas de Rendimiento**
- **Tiempo de ejecución**: <5s para todos los tests
- **Cobertura de líneas**: >90%
- **Cobertura de ramas**: >85%

## ⚠️ Solución de Problemas

### **No se muestran datos reales**
1. Verificar que el workflow de CI haya completado exitosamente
2. Revisar que los artifacts se hayan generado correctamente
3. Comprobar que el workflow de Pages haya ejecutado sin errores

### **Enlaces a reportes rotos**
- Los reportes se generan solo cuando el CI completa exitosamente
- Los enlaces apuntan a paths relativos que se crean automáticamente

### **Datos desactualizados**
- Los datos se actualizan en cada push al branch master
- La página web intenta cargar datos frescos en cada visita
- El timestamp muestra la última actualización

## 🚀 Mejoras Futuras

1. **Histórico de métricas**: Guardar tendencias temporales
2. **Alertas automáticas**: Notificaciones cuando baja la cobertura
3. **Métricas de rendimiento**: Tiempo de ejecución por test
4. **Reportes de seguridad**: Integración con análisis de vulnerabilidades

---

> **Nota**: Este sistema se actualiza automáticamente con cada cambio en el repositorio. Los datos mostrados reflejan el estado real del proyecto en tiempo de build. 