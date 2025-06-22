package com.kursor.domain;

import java.time.LocalDateTime;
import com.kursor.domain.Pregunta;

/**
 * Registro de una pregunta en una sesión de aprendizaje.
 */
public class PreguntaSesion {
    private Pregunta pregunta;
    private String resultado; // acierto / fallo / sin contestar
    private int tiempoDedicado;
    private int intentos;
    private int pistasUsadas;
    private LocalDateTime fechaRespuesta;

    public PreguntaSesion() {}

    public PreguntaSesion(Pregunta pregunta) {
        this.pregunta = pregunta;
        this.resultado = "sin contestar";
    }

    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public int getTiempoDedicado() { return tiempoDedicado; }
    public void setTiempoDedicado(int tiempoDedicado) { this.tiempoDedicado = tiempoDedicado; }

    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }

    public int getPistasUsadas() { return pistasUsadas; }
    public void setPistasUsadas(int pistasUsadas) { this.pistasUsadas = pistasUsadas; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
} 