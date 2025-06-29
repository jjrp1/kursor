# 🔒 Configuración de Branch Protection

## 📋 Configuración Recomendada para la Rama Master

### **Configuraciones de Seguridad**

#### **1. Requerir Pull Request antes de merge**
- ✅ **Require a pull request before merging**
- ✅ **Require approvals**: 1 approval mínimo
- ✅ **Dismiss stale PR approvals when new commits are pushed**
- ✅ **Require review from code owners**

#### **2. Requerir Status Checks**
- ✅ **Require status checks to pass before merging**
- ✅ **Require branches to be up to date before merging**
- ✅ **Status checks requeridos**:
  - `Build` - CI/CD pipeline
  - `Test Coverage` - Cobertura de pruebas
  - `Security Scan` - Escaneo de seguridad

#### **3. Requerir Conversaciones Resueltas**
- ✅ **Require conversation resolution before merging**
- ✅ **Require signed commits**

#### **4. Restricciones de Push**
- ❌ **Restrict pushes that create files that are larger than 100 MB**
- ✅ **Require linear history**
- ✅ **Include administrators**

#### **5. Auto-delete de Branches**
- ✅ **Automatically delete head branches**

### **Configuración de Code Owners**

#### **Archivo `.github/CODEOWNERS`**
```
# Propietarios del código principal
* @jjrp1

# Documentación
/docs/ @jjrp1
/doc/ @jjrp1
README.md @jjrp1

# Configuración de CI/CD
.github/ @jjrp1
.github/workflows/ @jjrp1

# Configuración de Maven
pom.xml @jjrp1
kursor-*/pom.xml @jjrp1

# Código fuente principal
kursor-core/src/main/java/ @jjrp1
kursor-core/src/test/java/ @jjrp1

# Módulos
kursor-*-module/ @jjrp1
```

### **Configuración de Pull Request Templates**

#### **Archivo `.github/pull_request_template.md`**
```markdown
## 📋 Descripción
Describe brevemente los cambios realizados en este PR.

## 🎯 Tipo de Cambio
- [ ] Bug fix (cambio que corrige un problema)
- [ ] New feature (cambio que añade funcionalidad)
- [ ] Breaking change (cambio que rompe funcionalidad existente)
- [ ] Documentation update (actualización de documentación)

## 🔧 Cambios Realizados
- [Lista de cambios específicos]

## 🧪 Pruebas
- [ ] Tests unitarios añadidos/actualizados
- [ ] Tests de integración añadidos/actualizados
- [ ] Pruebas manuales realizadas

## 📚 Documentación
- [ ] README actualizado
- [ ] Documentación técnica actualizada
- [ ] Comentarios de código añadidos

## ✅ Checklist
- [ ] Código sigue las convenciones del proyecto
- [ ] Tests pasan correctamente
- [ ] No hay conflictos de merge
- [ ] Documentación actualizada
- [ ] Revisión de código solicitada

## 📸 Capturas de Pantalla (si aplica)
[Añadir capturas si hay cambios en la UI]

## 🔗 Issues Relacionados
Closes #[número de issue]
```

### **Configuración de Merge Options**

#### **Estrategia de Merge Recomendada**
- **Merge method**: `Squash and merge`
- **Squash commit title**: `{PR title} (#{PR number})`
- **Squash commit message**: `{PR body}`

#### **Razones para Squash and Merge**
1. **Historial limpio**: Un commit por feature/fix
2. **Fácil rollback**: Revertir un commit completo
3. **Mejor legibilidad**: Historial más claro
4. **Consistencia**: Formato uniforme de commits

### **Configuración de Branch Naming**

#### **Convenciones de Naming**
- **Feature branches**: `feature/nombre-descriptivo`
- **Bug fixes**: `fix/nombre-del-bug`
- **Hotfixes**: `hotfix/descripcion-urgente`
- **Documentation**: `docs/descripcion`
- **Refactoring**: `refactor/descripcion`

#### **Ejemplos**
```
feature/nuevo-tipo-pregunta
fix/memory-leak-sesiones
docs/actualizar-guia-usuario
refactor/optimizar-carga-cursos
```

### **Configuración de Status Checks**

#### **Workflows Requeridos**
1. **CI/CD Pipeline** (`ci.yml`)
   - Build
   - Test
   - Security Scan

2. **Release Pipeline** (`release.yml`)
   - Build Release
   - Create Release

3. **Pages Pipeline** (`pages.yml`)
   - Deploy Pages

### **Configuración de Dependabot**

#### **Branch Protection para Dependabot**
- ✅ **Allow Dependabot to create and update branches**
- ✅ **Allow Dependabot to merge pull requests**
- ✅ **Auto-merge for minor and patch updates**

### **Configuración de Administradores**

#### **Permisos de Administrador**
- ✅ **Include administrators in these restrictions**
- ✅ **Allow force pushes for administrators**
- ✅ **Allow deletions for administrators**

### **Configuración de Webhooks**

#### **Webhooks Recomendados**
1. **Slack/Discord notifications**
2. **Email notifications**
3. **Custom integrations**

### **Monitoreo y Alertas**

#### **Métricas a Monitorear**
- **Pull requests abiertos**
- **Tiempo promedio de review**
- **Tasa de merge**
- **Conflicts de merge**

#### **Alertas Configuradas**
- **PRs sin review por más de 2 días**
- **Build failures**
- **Security vulnerabilities**

---

## 🚀 Pasos para Implementar

### **1. Configurar Branch Protection**
1. Ir a Settings > Branches
2. Añadir rule para `master`
3. Configurar todas las opciones listadas arriba
4. Guardar configuración

### **2. Crear CODEOWNERS**
1. Crear archivo `.github/CODEOWNERS`
2. Definir propietarios por área
3. Commit y push

### **3. Configurar PR Templates**
1. Crear archivo `.github/pull_request_template.md`
2. Definir template completo
3. Commit y push

### **4. Probar Configuración**
1. Crear branch de prueba
2. Hacer cambios
3. Crear PR
4. Verificar que las reglas se aplican

---

## 📊 Beneficios de Branch Protection

### **Seguridad**
- ✅ Previene pushes directos a master
- ✅ Requiere reviews de código
- ✅ Valida calidad antes de merge

### **Calidad**
- ✅ Tests obligatorios
- ✅ Status checks requeridos
- ✅ Conversaciones resueltas

### **Colaboración**
- ✅ Proceso de review definido
- ✅ Historial de cambios claro
- ✅ Responsabilidades claras

### **Mantenibilidad**
- ✅ Historial limpio
- ✅ Rollbacks fáciles
- ✅ Debugging simplificado

---

*Configuración actualizada: 29/06/2025* 