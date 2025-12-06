package com.example.integradoravirna.estructuras;

import com.example.integradoravirna.modelo.Tarea;

public class NodoArbol {

    private Tarea tarea;
    private NodoArbol izquierdo;
    private NodoArbol derecho;

    public NodoArbol(Tarea tarea) {
        this.tarea = tarea;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public NodoArbol getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoArbol izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoArbol getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoArbol derecho) {
        this.derecho = derecho;
    }
}
