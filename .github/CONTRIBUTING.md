# 🤝 Guía de Contribución

¡Gracias por tu interés en contribuir a Kursor! Este documento te guiará a través del proceso de contribución.

## 📋 Cómo Contribuir

### 🐛 Reportar Bugs
1. Usa la plantilla de [Bug Report](/.github/ISSUE_TEMPLATE/bug_report.md)
2. Incluye pasos detallados para reproducir el problema
3. Adjunta logs y capturas de pantalla si es posible

### 💡 Sugerir Nuevas Funcionalidades
1. Usa la plantilla de [Feature Request](/.github/ISSUE_TEMPLATE/feature_request.md)
2. Describe claramente el problema que resuelve
3. Proporciona ejemplos de uso si es posible

### 🔧 Contribuir Código

#### Configuración del Entorno
1. **Fork** el repositorio
2. **Clone** tu fork localmente:
   ```bash
   git clone https://github.com/tu-usuario/kursor.git
   cd kursor
   ```
3. **Configura** el entorno de desarrollo:
   ```bash
   mvn clean install
   ```

#### Flujo de Trabajo
1. **Crea** una rama para tu feature:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
2. **Desarrolla** tu funcionalidad
3. **Ejecuta** los tests:
   ```bash
   mvn test
   ```
4. **Commit** tus cambios:
   ```bash
   git commit -m "feat: añadir nueva funcionalidad"
   ```
5. **Push** a tu fork:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
6. **Crea** un Pull Request

### 📝 Convenciones de Código

#### Java
- Sigue las convenciones de Java
- Usa nombres descriptivos para variables y métodos
- Documenta clases y métodos públicos con JavaDoc
- Mantén métodos cortos y con una sola responsabilidad

#### Git Commits
Usa el formato [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `docs:` cambios en documentación
- `style:` cambios de formato
- `refactor:` refactorización de código
- `test:` añadir o modificar tests
- `chore:` cambios en build o herramientas

#### Pull Requests
- Título descriptivo
- Descripción detallada de los cambios
- Referencia a issues relacionados
- Screenshots si aplica

### 🧪 Tests

#### Escribir Tests
- Cada nueva funcionalidad debe incluir tests
- Mantén cobertura de código alta
- Usa nombres descriptivos para los tests

#### Ejecutar Tests
```bash
# Todos los tests
mvn test

# Tests con cobertura
mvn test jacoco:report

# Tests específicos
mvn test -Dtest=NombreTest
```

### 📚 Documentación

#### Actualizar Documentación
- Mantén la documentación actualizada
- Incluye ejemplos de uso
- Documenta APIs públicas

#### Estructura de Documentación
- `doc/usuario/` - Guías para usuarios finales
- `doc/tecnica/` - Documentación técnica
- `README.md` - Información general del proyecto

### 🔍 Revisión de Código

#### Antes de Enviar un PR
- [ ] El código compila sin errores
- [ ] Todos los tests pasan
- [ ] La documentación está actualizada
- [ ] El código sigue las convenciones
- [ ] No hay warnings de compilación

#### Durante la Revisión
- Responde a los comentarios de revisión
- Haz los cambios solicitados
- Mantén la conversación constructiva

## 🏗️ Arquitectura del Proyecto

### Estructura Modular
Kursor usa una arquitectura modular donde cada tipo de pregunta es un módulo independiente:

```
kursor-core/           # Módulo principal
kursor-*-module/       # Módulos de tipos de preguntas
```

### Crear un Nuevo Módulo
1. Crea la estructura del módulo
2. Implementa `PreguntaModule`
3. Registra el módulo en `META-INF/services/`
4. Añade tests unitarios
5. Actualiza la documentación

## 🎯 Áreas de Contribución

### Prioridades Actuales
- [ ] Mejorar la interfaz de usuario
- [ ] Añadir más tipos de preguntas
- [ ] Optimizar el rendimiento
- [ ] Mejorar la documentación

### Ideas Futuras
- [ ] Soporte para múltiples idiomas
- [ ] Integración con LMS
- [ ] Analytics avanzados
- [ ] API REST

## 📞 Contacto

- **Issues**: [GitHub Issues](https://github.com/jjrp1/kursor/issues)
- **Email**: jjrp1@um.es
- **Discussions**: [GitHub Discussions](https://github.com/jjrp1/kursor/discussions)

## 🙏 Agradecimientos

¡Gracias a todos los contribuidores que hacen de Kursor una mejor plataforma de aprendizaje!

---

**¿Tienes preguntas?** ¡No dudes en preguntar en los Issues o Discussions! 