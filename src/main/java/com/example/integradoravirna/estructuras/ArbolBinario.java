package com.example.integradoravirna.estructuras;

import com.example.integradoravirna.modelo.Prioridad;
import com.example.integradoravirna.modelo.Tarea;

public class ArbolBinario {

    private NodoArbol raiz;

    // Insertar
    public void insertar(Tarea tarea) {
        raiz = insertarRecursivo(raiz, tarea);
    }

    private NodoArbol insertarRecursivo(NodoArbol nodo, Tarea tarea) {
        if (nodo == null) {
            return new NodoArbol(tarea);
        }

        // Orden por prioridad
        int comparacion = compararTareas(tarea, nodo.getTarea());

        if (comparacion < 0) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), tarea));
        } else {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), tarea));
        }

        return nodo;
    }

    // Comparar prioridad y si es igual comparar ID
    private int compararTareas(Tarea t1, Tarea t2) {
        int p1 = prioridadValor(t1.getPrioridad());
        int p2 = prioridadValor(t2.getPrioridad());

        if (p1 != p2) {
            return p1 - p2;
        }
        return t1.getId().compareTo(t2.getId());
    }

    private int prioridadValor(Prioridad p) {
        return switch (p) {
            case ALTA -> 1;
            case MEDIA -> 2;
            case BAJA -> 3;
        };
    }

    // Mostrar ordenado (in-order)
    public void mostrarEnOrden() {
        mostrarEnOrdenRec(raiz);
    }

    private void mostrarEnOrdenRec(NodoArbol nodo) {
        if (nodo != null) {
            mostrarEnOrdenRec(nodo.getIzquierdo());
            System.out.println(nodo.getTarea());
            mostrarEnOrdenRec(nodo.getDerecho());
        }
    }
}
