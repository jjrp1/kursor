---  
title: Apuntes de pds-casos-de-uso 
subtitle:  
description:  
keywords:  
status: desarrollo  
created: 2025-04-15
modified: 2025-06-15  
author: "Juanjo Ruiz"  
---  
 
# Casos de uso  

## Actores 

- **Usuario**: Persona que utiliza la aplicación (monopuesto y monousuario).
- **Sistema**: La propia aplicación que gestiona cursos y sesiones (progreso y estadísticas).

## Casos de uso principales

- **Inspeccionar**:  
  Visualiza información detallada sobre el contenido, bloques y tipos de preguntas de un curso.

- **Comenzar**:  
  Inicia una nueva sesión de un curso, eligiendo una *estrategia de aprendizaje* y comenzando desde el principio.  
  *Alternativa*: Si existe alguna sesión sin terminar, se avisa al usuario y se solicita confirmar el inicio de una nueva sesión. 

- **Reanudar**:  
  Continuar una sesión de curso previamente iniciada, manteniendo la estrategia seleccionada.  
  *Pre-condición*: el curso debe tener alguna sesión sin terminar. 

- ***Flashcards***:   
  Inicia o reanuda una *sesión de aprendizaje* mediante ***flashcards***.  
  *Pre-condición*: el curso debe tener algún bloque de tipo ***flashcards***.  
  **Ir a**: *Documento detalle de [flashcards](pds-estadisticas.md)*.

- **Estadísticas**:  
  Muestra estadísticas detalladas de uso, progreso, rachas y rendimiento, incluyendo el historial de sesiones para cada curso.  
  **Ir a**: *Documento detalle de [Estadísticas de Sesión](pds-estadisticas.md)*.
  

- **Guardar estado**:  
  El sistema guarda automáticamente el estado y progreso de la sesión en curso, periódicamente y sin intervención del usuario.  
  *Alternativa*: Al terminar un curso, una sesión, o al cerrar la aplicación de forma manual.


## Casos de uso secundarios 

Estos *casos de uso* se entienden dentro del alcance de la aplicación, pero fuera de su implementación.

- **Crear curso**:  
  El usuario diseña un curso, añade bloques y preguntas de diferentes tipos, y lo guarda en formato YAML; para esto podrá utilizar cualquier editor de texto siguiendo las indicaciones de un manual de usuario que especificará el formato de los cursos, bloques y los tipos de preguntas disponibles y sus campos.

- **Importar curso**:  
  El usuario puede cargar nuevos cursos en la aplicación, para ello bastará con copiar los contenidos del curso (fichero YAML + imágenes y audios, si precisa) en la carpeta "cursos" de la aplicación. Cada curso se guardará en su propia carpeta, con el mismo nombre que el fichero YAML correspondiente.


Aquí tienes los **casos de uso principales reescritos con identificador (código) y siguiendo la plantilla de Larman**. Son definitivos y alineados tanto con el enunciado oficial[1] como con las buenas prácticas de documentación[2].

---

## Casos de uso principales desarrollados (*Larman*)

Desarrollo de los casos de uso principales según la plantilla de *Larman*:

| Código                                     | Nombre         | Descripción breve                         |
|:------------------------------------------:|----------------|-------------------------------------------|
| [CU01](#cu01--inspeccionar-curso)          | Inspeccionar   | Visualizar información de un curso        |
| [CU02](#cu02--comenzar-curso)              | Comenzar       | Iniciar nueva sesión de un curso          |
| [CU03](#cu03--reanudar-sesión)             | Reanudar       | Continuar sesión previamente iniciada     |
| [CU04](#cu04--flashcards)                  | Flashcards     | Sesión de aprendizaje con flashcards      |
| [CU05](#cu05--consultar-estadísticas)      | Estadísticas   | Consultar estadísticas e historial        |
| [CU06](#cu06--guardar-estado-de-la-sesión) | Guardar estado | Guardado automático de la sesión          |

---

### CU01 – Inspeccionar curso

**Actor principal:** Usuario

**Breve descripción:**  
El usuario visualiza la información detallada de un curso disponible, incluyendo sus bloques y los tipos de preguntas que contiene.

**Precondiciones:**  
- El usuario ha iniciado la aplicación.
- Existen cursos cargados en la aplicación.

**Postcondiciones:**  
- El usuario ha accedido a la información del curso seleccionado.

**Flujo principal:**
1. El usuario selecciona la opción de inspeccionar cursos.
2. El sistema muestra la lista de cursos disponibles.
3. El usuario selecciona un curso.
4. El sistema presenta la información detallada del curso: bloques, tipos de preguntas y resumen de contenidos.

**Flujos alternativos y excepciones:**
- 2a. Si no existen cursos cargados, el sistema informa al usuario y termina la aplicación.

---

### CU02 – Comenzar curso

**Actor principal:** Usuario

**Breve descripción:**  
El usuario inicia una nueva sesión de un curso, eligiendo la estrategia de aprendizaje y comenzando desde el principio.

**Precondiciones:**  
- El usuario ha seleccionado un curso.
- El curso tiene al menos un bloque y preguntas.

**Postcondiciones:**  
- Se crea una nueva sesión asociada al usuario, curso y estrategia elegida.
- El usuario comienza la sesión desde la primera pregunta según la estrategia.

**Flujo principal:**
1. El usuario selecciona la opción de comenzar un curso.
2. El sistema verifica si existe alguna sesión sin terminar para ese curso.
3. El sistema solicita al usuario elegir la estrategia de aprendizaje (secuencial, aleatoria, repetición espaciada, etc.).
4. El usuario selecciona la estrategia.
5. Se crea una nueva sesión y se inicia el curso desde el principio.
6. El usuario comienza a interactuar con la primera pregunta.

**Flujos alternativos y excepciones:**
- 2a. El sistema informa al usuario de la existencia de alguna sesión sin terminar y solicita confirmación para iniciar una nueva sesión.
  - 2a1. Si el usuario confirma, continúa el flujo principal (3).
  - 2a2. Si el usuario cancela, vuelve al menú anterior.

---

## CU03 – Reanudar sesión

**Actor principal:** Usuario

**Breve descripción:**  
El usuario continúa una sesión de un curso previamente iniciada, manteniendo la estrategia seleccionada.

**Precondiciones:**  
- Existe al menos una sesión sin terminar para el curso seleccionado.

**Postcondiciones:**  
- El usuario retoma la sesión en el punto exacto donde la dejó.

**Flujo principal:**
1. El usuario selecciona la opción de reanudar sesión.
2. El sistema muestra las sesiones sin terminar del curso.
3. El usuario selecciona la sesión a reanudar.
4. El sistema restaura el estado de la sesión (incluyendo la estrategia y el iterador) y presenta la pregunta en curso.

---

## CU04 – Flashcards

**Actor principal:** Usuario

**Breve descripción:**  
El usuario inicia o reanuda una sesión de aprendizaje centrada en bloques de tipo flashcards.

**Precondiciones:**  
- El curso seleccionado tiene al menos un bloque de tipo flashcards.

**Postcondiciones:**  
- El usuario accede a una sesión de flashcards, nueva o reanudada.

**Flujo principal:**
1. El usuario selecciona la opción de flashcards.
2. El sistema muestra los bloques de tipo flashcards disponibles.
3. El usuario selecciona un bloque.
4. El sistema verifica si existe una sesión de flashcards sin terminar para ese bloque.
5. Si no existe, se crea una nueva sesión de flashcards y se inicia desde la primera tarjeta.
6. El usuario avanza por las tarjetas según la estrategia.

**Flujos alternativos y excepciones:**
- 4a. Si existe alguna sesión de flashcards sin terminar, el sistema pregunta si desea reanudarla.
  - 4a1. Si confirma, se reanuda la sesión.
  - 4a2. Si cancela, puede iniciar una nueva.

---

## CU05 – Consultar estadísticas

**Actor principal:** Usuario

**Breve descripción:**  
El usuario consulta estadísticas detalladas de uso, progreso, rachas y rendimiento, incluyendo el historial de sesiones para cada curso.

**Precondiciones:**  
- El usuario ha realizado al menos una sesión en algún curso.

**Postcondiciones:**  
- El usuario visualiza las estadísticas y el historial de sesiones.

**Flujo principal:**
1. El usuario selecciona la opción de estadísticas.
2. El sistema muestra las métricas generales y detalladas del curso/sesión: tiempo, rachas, porcentaje de completitud, tasa de aciertos, etc.
3. El usuario puede consultar el historial de sesiones y comparar su rendimiento.

**Flujos alternativos y excepciones:**
- 2a. Si no existen datos de sesiones previas, el sistema informa al usuario.

---

## CU06 – Guardar estado de la sesión

**Actor principal:** Sistema

**Breve descripción:**  
El sistema guarda automáticamente el estado y progreso de la sesión en curso, periódicamente y sin intervención del usuario.

**Precondiciones:**  
- El usuario está realizando una sesión activa.

**Postcondiciones:**  
- El estado de la sesión (incluyendo el iterador de la estrategia) queda persistido y listo para ser reanudado.

**Flujo principal:**
1. Durante la sesión, el sistema detecta un evento de guardado (por tiempo, cambio de pregunta, etc.).
2. El sistema serializa y guarda el estado completo de la sesión en la base de datos.

**Flujos alternativos y excepciones:**
- 2a. Si ocurre un error de persistencia, el sistema informa al usuario y sugiere reintentar o guardar manualmente.

