# Kursor

## Sobre las sesiones

El modelo de dominio representa una clase Sesión

El objetivo es registrar (mediante persistencia JPA Hibernate + sqlite) la sesiones de aprendizaje del usuario.

A priori (según el modelo de dominio) necesitamos:

- `id`: Identificador único
- `curso`: Referencia al curso
- `bloque`: Referencia al bloque del curso
- `estrategia`: Estrategia de aprendizaje elegida
- `fechaComienzo`: Fecha y hora de inicio
- `fechaUltimaRevision`: Fecha y hora de la última actividad
- `tiempoDedicado`: Tiempo total acumulado
- `preguntaActual`: Referencia a la pregunta en curso
- `porcentajeCompletitud`: % de preguntas respondidas
- `tasaAciertos`: % de aciertos sobre preguntas respondidas
- `mejorRachaAciertos`: Mejor racha de aciertos consecutivos
- `preguntasSesion`: Lista de registros de pregunta en la sesión

Por otro lado, para cada `preguntaSesion` tenemos, tenemos una clase con estos atributos:

- `pregunta`: Referencia a la pregunta (ID_curso + ID_bloque + ID_pregunta)
- `respuesta`: (no figura en el modelo de dominio)
- `resultado`: acierto / fallo / sin contestar
- `tiempoDedicado`: Tiempo dedicado a responder (0 si no contestada)

(hay más, pero los obviaremos por ahora y los entenderemos opcionales)

Al comenzar una nueva sesión de aprendizaje, el usuario:
- elije curso
- elije bloque
- elije estrategia
- comienza a realizar el "curso" (en realidad un bloque de aprendizaje)

el sistema por su parte:
- registra una nueva sesión, con:
  - un ID único
  - el curso, bloque y estrategia elegidos
  - fechaComienzo y fechaUltimaRevisión (iguales en el primer caso)


Progresivamente, según corresponda el sistema registrará 
- al comenzar una nueva pregunta
  - registra la pregunta
  - "sin respuesta"
  - respuesta:=null
  - registra tiempoDedicado:=0
- al contestar una pregunta
  - registra respuesta
  - Verifica corrección y registra `resultado`: acierto / fallo
  - registra tiempoDedicado:= n (lo que corresponda, el tiempo transcurrido entre presentar y contestar)
  - registrará también tiempoDedicado (acumulado) de la sesión

- mejorRachaAciertos:
  - comenzará en 0
  - El dialogo del curso tendrá un contador propio "rachaAcumulada" que también comenzará en 0
    - con una pregunta correcta:
      rachaAcumulada++
      if rachaAcumulada > mejorRachaAciertos  entonces
         mejorRachaAciertos := rachaAcumulada 
    - con una pregunta incorrecta:
      rachaAcumulada := 0
    

Te contesto entre las líneas de tu texto:
---
Lo que está bien implementado:
1. Estructura básica: Tienes tanto el modelo de dominio (Sesion.java) como la entidad JPA (persistence.entity.Sesion.java)
> BIEN, pero están alineadas? requieren alguna modificación?

2. Persistencia: Ya tienes RespuestaPregunta como entidad separada (que es mejor que tu propuesta de PreguntaSesion)
> ¿SI? ¿Porqué? PreguntaSesion está declarada en el modelo de dominio, ¿la corregimos?

3. Estados: Tienes EstadoSesion (ACTIVA, PAUSADA, COMPLETADA)
> AQUI tengo SERIAS dudas, no recuerdo tal cosas, recuerdo EN_CURSO, GUARDADA, COMPLETADA (o algo así)  

4. Timestamps: Tienes createdAt, updatedAt, fechaUltimaActividad
> ¿eh? ¿dónde?  

---

Lo que falta o está incompleto:
Mis preguntas principales:
1. Gestión de rachas de aciertos
En tu documento mencionas:
> "El dialogo del curso tendrá un contador propio 'rachaAcumulada'"
Pregunta: ¿Dónde se almacena la mejorRachaAciertos? Veo que está en el modelo de dominio pero no en la entidad JPA. ¿Debería persistirse?
>Claro, por supuesto! 

2. Pregunta actual vs. Estado de la estrategia
Tu documento menciona preguntaActual, pero en la implementación actual veo que hay EstadoEstrategia que probablemente maneja esto.
Pregunta: ¿La preguntaActual debería ser parte de la sesión o del estado de la estrategia? ¿Cómo se relaciona con EstadoEstrategia?
> Esta no sé contestarla, necesito más contexto  

3. Tiempo de respuesta individual
En RespuestaPregunta tienes tiempoRespuesta, pero en tu documento mencionas que se registra cuando se presenta y cuando se contesta.
Pregunta: ¿Necesitas registrar también el momento de presentación de la pregunta, o solo el tiempo de respuesta es suficiente?
> Yo diría que conocer el tiempo dedicado a responder una pregunta es suficiente para propósitos estadísticos, no necesitamos marcas de tiempo exactas.

4. Usuario
Tu documento no menciona el usuario, pero la entidad JPA tiene usuarioId.
Pregunta: ¿Es un sistema multi-usuario o single-user? ¿El usuarioId es necesario?
> Esta aplicación/proyecto contempla un sistema mono-usuario. Si JPA "requiere" un usuario SI o SI, habrá que utiliza un usuario por defecto (o algo así).  

5. Bloque vs. Curso
Tu documento menciona que el usuario elige curso, bloque y estrategia, pero la sesión parece estar a nivel de bloque.
Pregunta: ¿Una sesión corresponde a un bloque específico o puede abarcar múltiples bloques de un curso?
> Pues, buena pregunta, pero creo que si limitamos cada sesión a un bloque, será más sencillo de implementar y manejar. Si queremos averiguar el tiempo total dedicaddo a un curso entiendo que basta con sumar todos los registros de cierto curso con independencia de los bloques a los que corresponden. Creo que así es más granular.  ¿qué opinas de esto?  

6. Respuesta vs. Resultado
En tu documento mencionas: "respuesta: (no figura en el modelo de dominio)"
Pregunta: ¿Necesitas almacenar el texto literal de la respuesta del usuario (como en RespuestaPregunta.textoRespuesta) o solo si fue correcta/incorrecta?
> Creo que es conveniente guardar lo que el usuario contestó (además de si es correcto o no), por si el usuario quiere revisar sus respuestas.  

7. Puntuación
El modelo de dominio tiene puntuacionTotal pero la entidad JPA no.
Pregunta: ¿La puntuación es necesaria? ¿Cómo se calcula?
> Desconozco donde habla el modelo de dominio de "puntuaciones" pero, a priori, en esta primera versión, no vamos a implementar mecanismo alguno de puntuación.  

8. Fecha de última revisión
Tu documento menciona fechaUltimaRevision pero la entidad JPA tiene fechaUltimaActividad.
Pregunta: ¿Son lo mismo o tienen propósitos diferentes?
> Yo diría que son lo mismo y que `fechaUltimaRevision` resulta más intuitivo/significativo.

---
Cosas que probablemente olvidaste:
1. Manejo de errores: ¿Qué pasa si se cierra la aplicación abruptamente?
2. Sesiones concurrentes: ¿Puede un usuario tener múltiples sesiones activas?
3. Migración de datos: ¿Cómo se migran las sesiones existentes?
4. Limpieza: ¿Cuánto tiempo se mantienen las sesiones antiguas?
5. Backup/restore: ¿Cómo se maneja la recuperación de sesiones?
¿Qué opinas de estas observaciones? ¿Hay alguna que quieras que profundicemos primero?

1. En principio, espero que la sesión quede marcada "EN_CURSO", con una pregunta "sin_contestar", de forma que el usuario, llegado el caso, cuando vuelva a ejecutar la aplicación, podrá revisar las sesiones de determinado curso y observará que cierta sesión NO está guardada (EN_CURSO) y estaba contestando cierta pregunta "sin_contestar". No sé si tal como lo he planteado es suficiente, o necesitamos capturar más información.

2. NO, en principio NO, desconozco si sqlite lo admite (juraría que no, que es monousuario, sin control de concurrencia), pero en todo caso, este extremo debería controlarse por otro agente externo a nuestro proyecto.

3. Cómo se migran? a dónde?

4. Las sesiones no se borran NUNCA, en todo caso, se pueden pasar a un histórico más adelante a petición del usuario y en una fase más avanzada del proyecto, esto no nos preocupa ahora

5. A ver, las copias de seguridad de los datos de la aplicación se manejan de forma externa a la aplicación, es decir, no son nuestra responsabilidad. Sin embargo, en cuanto a "restore" visto como una forma de recuperación.... en la GUI principal del proyecto, las sesiones asociadas a un curso se recuperan al seleccionar un curso, bien para una nueva ronda o para reanudar una sesión incompleta (volutaria o accidentalmente).



---
Preguntas que necesito que me contestes:
1. PreguntaSesion vs RespuestaPregunta
> Cual es tu recomendación, yo creo que el mejor usar PreguntaSesion también como entidad JPA, revisa que dicen las mejores prácticas de desarrollo de software con respecto a esto

2. EstadoEstrategia
¿Qué información contiene exactamente EstadoEstrategia?
¿Se relaciona con la pregunta actual?
¿Necesitamos mantener esta entidad o podemos simplificar?
> ¿Puedes aportarme más contexto? ¿Es la implementación del iterador o un clase auxiliar de este?

3. Pregunta actual
¿Dónde se almacena la referencia a la pregunta que está siendo respondida actualmente?
¿En la sesión o en el estado de la estrategia?
> Yo diría que en ambas ¿no? ¿supone una dificultad? ¿oscurece el código? ¿qué recomiendas?. Quiero decir, probablemente, el iterador de cierta estrategia, que si no recuerdo mal tambien debemos guardar su estado junto a la sesión; necesita saber cual es la pregunta en curso....¿no?

4. Siguiente paso
> seguir debatiendo

---


1. PreguntaSesion vs RespuestaPregunta - Mi recomendación
Convertir PreguntaSesion en entidad JPA y eliminar RespuestaPregunta.
> OK! pero además creo que deberíamos marcar en algún sitio que ciertos atributos de `PreguntaSesion` tales como `pistasUsadas` deberían marcarse como opcionales o implementables a futuro, desconozco cómo hacer esto de la mejor manera, quizás lo más sencillo sea elimnarlos del modelo de dominio.

2. EstadoEstrategia - Necesito más contexto
Ahora entiendo mejor. EstadoEstrategia es el estado serializado del iterador/estrategia. Contiene:
datosEstado: JSON con el estado interno (índice actual, preguntas pendientes, etc.)
tipoEstrategia: Qué estrategia se está usando
progreso: Progreso general
Mi análisis: Es una implementación del patrón `Memento` para persistir el estado del iterador.
> Vale, pero dónde la guardamos, ¿serializada en la entidad sesion JPA? ¿como afecta esto al dominio? ¿o no afecta por ser parte de la implementación?


3. Pregunta actual - Mi recomendación
Tienes razón, debería estar en ambas:
En Sesion: Para consultas rápidas y estadísticas
En EstadoEstrategia: Como parte del estado del iterador
No oscurece el código si se maneja bien:
Sesion tiene preguntaActualId (referencia simple)
EstadoEstrategia tiene el estado completo del iterador
> ¿Esto ha quedado claro? Explícamelo mejor, que estoy perdido.

4. Mi propuesta para continuar el debate:
> Convertir PreguntaSesion a entidad JPA y eliminar RespuestaPregunta ... OK!
> Y sigamos debatiendo

---
1. PreguntaSesion como entidad JPA - Atributos opcionales
>Eliminar `pistasUsadas` del modelo de dominio (más simple)

2. EstadoEstrategia - Dónde guardarlo
>Mantener EstadoEstrategia como entidad separada (como está ahora).
>Separación de responsabilidades: Sesion maneja estadísticas, EstadoEstrategia maneja el estado del iterador

3. Pregunta actual - Te explico mejor
> Sigo sin verlo claro, pero confio esto a la aplicación de tu buen juicio, criterio, experiencia y buenas prácticas.

4. Próximo paso
> Otra ronda de repaso

---

1. PreguntaSesion como entidad JPA
✅ Convertir PreguntaSesion a entidad JPA
✅ Eliminar RespuestaPregunta
✅ Eliminar pistasUsadas, intentos y fechaRespuesta del modelo de dominio
✅ Mantener: pregunta, resultado, tiempoDedicado
2. EstadoEstrategia
✅ Mantener como entidad separada
✅ Responsabilidad: estado serializado del iterador/estrategia
✅ No afecta al dominio (es implementación)
3. Pregunta actual
✅ Mi criterio: implementar en ambas partes
✅ Sesion.preguntaActualId para consultas rápidas
✅ EstadoEstrategia.datosEstado para estado completo del iterador

Nueva ronda de repaso - Preguntas pendientes:
A. Campos adicionales en Sesion
¿Necesitamos agregar estos campos a la entidad Sesion?
preguntaActualId (para consultas rápidas)
> Diría que SI
rachaActual (contador temporal durante la sesión)
> Diría que NO y lo gestiona el formulario/diálogo

B. Relación PreguntaSesion-Sesion
¿Cómo relacionamos PreguntaSesion con Sesion?
@ManyToOne desde PreguntaSesion hacia Sesion
@OneToMany desde Sesion hacia PreguntaSesion
> No veo diferencia; cada sesión tiene muchas preguntas
> ¿puedes declarar las dos? creo que lo normal es recuperar sesión y a continuación sus preguntas, pero puede ser que desde una pregunta, necesitemos acceder a la sesión correspondiente, por ejemplo: para actualizar el tiempoAcumulado (por sesión)

C. Campos en PreguntaSesion
¿Qué campos exactos tendrá la entidad PreguntaSesion?
> preguntaId (String) 
> resultado (String) 
> respuesta ¿lo necesitamos? SI!

D. Esquema de base de datos
¿Quieres que diseñe el esquema completo de las tablas?
> Haz tu propuesta, recuerda que ya hay algo hecho/escrito

Sigamos revisando

# Revisión global del código del proyecto a nivel de documentación
  - Revisa que todo el código fuente esté correctamente documentado siguiendo/utilizando javadoc. 
  - Revisa la coherencia de comentarios en el código fuente con la documentación
  - Revisa las carpetas de documentación:
    - doc/tecnica
    - doc/usuario
  - Revisa las github pages
    - docs/
  - Revisa la coherencia y alineación de la documentación general (doc/) con las github pages (docs/)
  - Elabora un informe (revision-general-aaaammdd.md) (siendo aaaa el año, mm el mes y dd el día), con los cambios realizados



# Mejores prácticas: 
- El modelo de dominio debe reflejarse en la capa de persistencia.

¿?
- EstadoEstrategia -> implementación del patrón `Memento` para persistir el estado del iterador.