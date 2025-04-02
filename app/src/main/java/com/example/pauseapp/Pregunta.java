package com.example.pauseapp;

import java.io.Serializable;
import java.util.List;

public class Pregunta implements Serializable {
    private String texto;
    private List<String> opciones;

    public Pregunta(String texto, List<String> opciones) {
        this.texto = texto;
        this.opciones = opciones;
    }

    public String getTexto() {
        return texto;
    }

    public List<String> getOpciones() {
        return opciones;
    }
}

