# 📋 **PROPUESTA COMPLETA: Unificación y Reorganización del Sistema de Preguntas**

## 🎯 **Objetivo General**
Unificar toda la nomenclatura en español y reorganizar la arquitectura para que los módulos (plugins) sean responsables de manejar las peculiaridades de cada tipo de pregunta, incluyendo la UI.

## 🏗️ **Arquitectura Final Propuesta**

### **1. Mapeo Completo de Tipos**
```
Dominio                    Módulo (Plugin)
─────────────────────────────────────────
PreguntaTest           →   MultipleChoiceModule
PreguntaCompletarHuecos →   FillBlanksModule  
Flashcard              →   FlashcardModule
PreguntaTrueFalse      →   TrueFalseModule (NUEVO)
```

### **2. Estructura de Paquetes**
```
com.kursor.core/
├── domain/              # Entidades de dominio (PURE)
│   ├── Pregunta.java    # Clase abstracta base
│   ├── PreguntaTest.java
│   ├── PreguntaCompletarHuecos.java
│   ├── PreguntaTrueFalse.java (NUEVO)
│   └── Flashcard.java
└── PreguntaModule.java  # Interfaz principal (RENOMBRADA)

com.kursor.ui/
├── KursorApplication.java
├── CursoManager.java
└── ModuleManager.java

Módulos (plugins):
├── kursor-multiplechoice-module/
├── kursor-fillblanks-module/
├── kursor-flashcard-module/
└── kursor-truefalse-module/
```

## 🔧 **Cambios Específicos**

### **1. Eliminar Question Interface**
- **Eliminar**: `kursor-core/src/main/java/com/kursor/core/Question.java`
- **Limpiar**: Todos los imports y referencias

### **2. Renombrar QuestionModule → PreguntaModule**
```java
// Nueva interfaz PreguntaModule
public interface PreguntaModule {
    String getModuleName();
    String getModuleDescription();
    
    // Métodos de dominio
    List<Pregunta> getQuestions();
    Pregunta createQuestion(String questionId);
    
    // Métodos de UI específicos del módulo
    Node createQuestionView(Pregunta pregunta);
    boolean validateAnswer(Pregunta pregunta, Object answer);
    String getQuestionType(); // "test", "flashcard", "completar_huecos", "truefalse"
}
```

### **3. Crear PreguntaTrueFalse en Dominio**
```java
public class PreguntaTrueFalse extends Pregunta {
    private boolean respuestaCorrecta;

    public PreguntaTrueFalse(String id, String enunciado, boolean respuestaCorrecta) {
        super(id, "truefalse", enunciado, 1);
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public boolean getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(boolean respuestaCorrecta) { 
        this.respuestaCorrecta = respuestaCorrecta; 
    }

    @Override
    public boolean esCorrecta(String respuesta) {
        return Boolean.parseBoolean(respuesta) == respuestaCorrecta;
    }
}
```

### **4. Actualizar Todos los Módulos**
```java
// Ejemplo: MultipleChoiceModule
public class MultipleChoiceModule implements PreguntaModule {
    @Override
    public String getModuleName() {
        return "Multiple Choice Module";
    }

    @Override
    public String getModuleDescription() {
        return "Módulo para preguntas de opción múltiple";
    }

    @Override
    public String getQuestionType() {
        return "test";
    }

    @Override
    public List<Pregunta> getQuestions() {
        return new ArrayList<>();
    }

    @Override
    public Pregunta createQuestion(String questionId) {
        return null; // Implementar según necesidades
    }

    @Override
    public Node createQuestionView(Pregunta pregunta) {
        if (!(pregunta instanceof PreguntaTest)) {
            throw new IllegalArgumentException("Tipo de pregunta incorrecto para este módulo");
        }
        
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        VBox container = new VBox(10);
        Label enunciado = new Label(preguntaTest.getEnunciado());
        
        ToggleGroup group = new ToggleGroup();
        for (String opcion : preguntaTest.getOpciones()) {
            RadioButton rb = new RadioButton(opcion);
            rb.setToggleGroup(group);
            container.getChildren().add(rb);
        }
        
        container.getChildren().add(0, enunciado);
        return container;
    }

    @Override
    public boolean validateAnswer(Pregunta pregunta, Object answer) {
        if (!(pregunta instanceof PreguntaTest)) return false;
        PreguntaTest preguntaTest = (PreguntaTest) pregunta;
        return preguntaTest.esCorrecta((String) answer);
    }
}
```

### **5. Actualizar UI Principal**
```java
// En KursorApplication
private Node crearVistaModulos() {
    VBox modulosBox = new VBox(10);
    modulosBox.setPadding(new Insets(10));
    
    ComboBox<PreguntaModule> moduleSelector = new ComboBox<>();
    moduleSelector.getItems().addAll(ModuleManager.getInstance().getModules());
    moduleSelector.setConverter(new StringConverter<PreguntaModule>() {
        @Override
        public String toString(PreguntaModule module) {
            return module != null ? module.getModuleName() : "";
        }

        @Override
        public PreguntaModule fromString(String string) {
            return null;
        }
    });

    ListView<Pregunta> preguntaList = new ListView<>();
    preguntaList.setCellFactory(lv -> new ListCell<Pregunta>() {
        @Override
        protected void updateItem(Pregunta item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : item.getEnunciado());
        }
    });

    moduleSelector.setOnAction(e -> {
        PreguntaModule selectedModule = moduleSelector.getValue();
        if (selectedModule != null) {
            logger.info("Módulo seleccionado: " + selectedModule.getModuleName());
            preguntaList.getItems().clear();
            preguntaList.getItems().addAll(selectedModule.getQuestions());
        }
    });

    preguntaList.setOnMouseClicked(e -> {
        Pregunta selectedPregunta = preguntaList.getSelectionModel().getSelectedItem();
        if (selectedPregunta != null) {
            mostrarPregunta(selectedPregunta);
        }
    });

    modulosBox.getChildren().addAll(moduleSelector, preguntaList);
    return modulosBox;
}

private void mostrarPregunta(Pregunta pregunta) {
    // Encontrar el módulo correcto para esta pregunta
    PreguntaModule module = ModuleManager.getInstance()
        .findModuleByQuestionType(pregunta.getTipo());
    
    if (module == null) {
        logger.error("No se encontró módulo para tipo: " + pregunta.getTipo());
        return;
    }

    VBox preguntaBox = new VBox(10);
    preguntaBox.setPadding(new Insets(10));
    preguntaBox.setStyle("-fx-background-color: #f0f0f0;");
    
    Label titleLabel = new Label(pregunta.getEnunciado());
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    
    Node preguntaView = module.createQuestionView(pregunta);
    
    Button validateButton = new Button("Validar Respuesta");
    validateButton.setOnAction(e -> {
        // Implementar lógica de validación usando el módulo
        boolean isCorrect = module.validateAnswer(pregunta, null); // Obtener respuesta del UI
        Alert alert = new Alert(isCorrect ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Resultado");
        alert.setHeaderText(null);
        alert.setContentText(isCorrect ? "¡Respuesta correcta!" : "Respuesta incorrecta");
        alert.showAndWait();
    });

    preguntaBox.getChildren().addAll(titleLabel, preguntaView, validateButton);
    
    Tab preguntaTab = new Tab(pregunta.getEnunciado());
    preguntaTab.setContent(preguntaBox);
    preguntaTab.setClosable(true);
    
    tabPane.getTabs().add(preguntaTab);
    tabPane.getSelectionModel().select(preguntaTab);
}
```

## 📋 **Plan de Implementación**

### **Fase 1: Crear PreguntaTrueFalse**
1. **Crear** `PreguntaTrueFalse.java` en dominio
2. **Implementar** lógica de validación
3. **Añadir** a factory/builder si es necesario

### **Fase 2: Renombrar QuestionModule → PreguntaModule**
1. **Renombrar** archivo `QuestionModule.java` → `PreguntaModule.java`
2. **Actualizar** interfaz con nuevos métodos
3. **Actualizar** todos los imports

### **Fase 3: Eliminar Question**
1. **Eliminar** `Question.java`
2. **Limpiar** imports en todos los archivos
3. **Actualizar** referencias

### **Fase 4: Actualizar Módulos**
1. **Implementar** `PreguntaModule` en todos los módulos
2. **Añadir** métodos de UI específicos
3. **Actualizar** archivos META-INF y module-info.java

### **Fase 5: Actualizar UI Principal**
1. **Modificar** `KursorApplication` para usar `PreguntaModule`
2. **Implementar** búsqueda de módulo por tipo
3. **Delegar** creación de UI a módulos

### **Fase 6: Testing y Validación**
1. **Compilar** todo el proyecto
2. **Probar** cada módulo individualmente
3. **Verificar** que UI funciona correctamente

## �� **Archivos a Crear/Modificar**

### **Nuevos archivos**:
- `kursor-core/src/main/java/com/kursor/core/domain/PreguntaTrueFalse.java`

### **Renombrar**:
- `kursor-core/src/main/java/com/kursor/core/QuestionModule.java` → `PreguntaModule.java`

### **Eliminar**:
- `kursor-core/src/main/java/com/kursor/core/Question.java`

### **Modificar**:
- `kursor-truefalse-module/src/main/java/com/kursor/truefalse/TrueFalseModule.java`
- `kursor-multiplechoice-module/src/main/java/com/kursor/multiplechoice/MultipleChoiceModule.java`
- `kursor-fillblanks-module/src/main/java/com/kursor/fillblanks/FillBlanksModule.java`
- `kursor-flashcard-module/src/main/java/com/kursor/flashcard/FlashcardModule.java`
- `kursor-ui/src/main/java/com/kursor/ui/KursorApplication.java`
- `kursor-ui/src/main/java/com/kursor/ui/ModuleManager.java`
- Todos los `module-info.java` de los módulos
- Todos los archivos `META-INF/services/` de los módulos

## 🎯 **Beneficios Finales**

### ✅ **Unificación Completa**
- **Nomenclatura 100% en español**
- **Concepto único** para representar preguntas
- **Sin duplicación** de entidades

### ✅ **Arquitectura de Plugins Correcta**
- **Módulos responsables** de su UI específica
- **Dominio puro** sin dependencias de UI
- **Separación clara** de responsabilidades

### ✅ **Extensibilidad**
- **Nuevos tipos** = Nuevo módulo + nueva entidad
- **Módulos independientes** = Fácil de añadir/quitar
- **Arquitectura escalable**

### ✅ **Mantenibilidad**
- **Código más limpio** y organizado
- **Testing independiente** de cada capa
- **Menos confusión** para desarrolladores

## ❓ **¿Procedemos con esta propuesta completa?**

Esta es la reorganización completa que unifica nomenclatura, elimina duplicación y establece la arquitectura correcta de módulos. ¿Empezamos por la Fase 1?