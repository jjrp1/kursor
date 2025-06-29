package com.kursor.domain;

import java.time.LocalDateTime;

/**
 * Registro de una pregunta en una sesión de aprendizaje.
 */
public class PreguntaSesion {
    private Pregunta pregunta;
    private String resultado; // acierto / fallo / sin contestar
    private String respuesta;
    private int tiempoDedicado;


    public PreguntaSesion() {}

    public PreguntaSesion(Pregunta pregunta) {
        this.pregunta = pregunta;
        this.resultado = "sin contestar";
    }

    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public int getTiempoDedicado() { return tiempoDedicado; }
    public void setTiempoDedicado(int tiempoDedicado) { this.tiempoDedicado = tiempoDedicado; }

} 
