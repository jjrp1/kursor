---  
title: Apuntes de pds-modelo-de-dominio 
subtitle:  
description:  
keywords:  
status: desarrollo  
created: 2025-04-15
modified: 2025-06-15  
author: "Juanjo Ruiz"  
---  

# Modelo de dominio

El modelo de dominio representa los conceptos clave, relaciones y atributos necesarios para cubrir los casos de uso principales de la aplicación: inspección y gestión de cursos, sesiones de aprendizaje con distintas estrategias, seguimiento estadístico, uso de flashcards y persistencia del progreso. El diseño está inspirado en plataformas de aprendizaje como Duolingo y Mochi, y permite la extensión a nuevos tipos de preguntas y estrategias.

---

## Diagrama de clases (PlantUML)

![Diagrama de Clases](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/jjrp1/kursor/refs/heads/master/doc/inicial/pds-clases.puml)
## Entidades principales

### **Curso**
- `id`: Identificador único
- `nombre`: Nombre del curso
- `descripcion`: Breve descripción
- `bloques`: Lista de bloques de contenido

### **Bloque**
- `id`: Identificador único
- `titulo`: Título del bloque
- `tipo`: Tipo de bloque (test, flashcards, completar huecos, etc.)
- `preguntas`: Lista de preguntas

### **Pregunta** (abstracta)
- `id`: Identificador único
- `tipo`: Tipo de pregunta (test, completar huecos, flashcard, etc.)
- `enunciado`: Texto o contenido principal
- `opciones`: Opciones de respuesta (si aplica)
- `respuestaCorrecta`: Respuesta correcta (si aplica)

#### Subtipos:
- `PreguntaTest`
- `PreguntaCompletarHuecos`
- `Flashcard`
- *(Otros tipos extensibles)*

### **Sesion**
- `id`: Identificador único
- `curso`: Referencia al curso
- `estrategia`: Estrategia de aprendizaje elegida
- `fechaComienzo`: Fecha y hora de inicio
- `fechaUltimaRevision`: Fecha y hora de la última actividad
- `tiempoDedicado`: Tiempo total acumulado
- `bloqueActual`: Referencia al bloque en curso
- `preguntaActual`: Referencia a la pregunta en curso
- `porcentajeCompletitud`: % de preguntas respondidas
- `tasaAciertos`: % de aciertos sobre preguntas respondidas
- `mejorRachaAciertos`: Mejor racha de aciertos consecutivos
- `preguntasSesion`: Lista de registros de pregunta en la sesión

### **PreguntaSesion**
- `pregunta`: Referencia a la pregunta
- `resultado`: acierto / fallo / sin contestar
- `respuesta`: respuesta facilitada por el usuario
- `tiempoDedicado`: Tiempo dedicado a responder (0 si no contestada)
- `intentos`: Número de intentos realizados (opcional)
- `pistasUsadas`: Número de pistas solicitadas (opcional)
- `fechaRespuesta`: Fecha/hora de respuesta (opcional)

### **EstrategiaAprendizaje** (abstracta/interfaz)
- `nombre`: Nombre de la estrategia
- `estado`: Estado serializable (índice, orden, etc.)
- Métodos: `siguientePregunta()`, `guardarEstado()`, `restaurarEstado()`

#### Subtipos:
- `EstrategiaSecuencial`
- `EstrategiaAleatoria`
- `EstrategiaRepeticionEspaciada`
- *(Otros tipos extensibles)*

### **EstadisticasGlobales**
- `tiempoTotalUso`: Tiempo total de uso de la aplicación
- `mejorRachaDias`: Mejor racha de días de uso consecutivos
- `sesionesPorCurso`: Historial de sesiones por curso

---

## Relaciones principales

- Un **Curso** contiene varios **Bloques**.
- Un **Bloque** contiene varias **Preguntas**.
- Una **Sesion** está asociada a un **Curso** y a una **EstrategiaAprendizaje**.
- Una **Sesion** contiene una lista de **PreguntaSesion** para registrar el detalle de cada pregunta respondida.
- Las **EstadisticasGlobales** agregan información de todas las sesiones y cursos realizados.



