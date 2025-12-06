package com.example.integradoravirna.estructuras;

import java.util.ArrayList;
import java.util.List;

public class ArbolBinarioBusqueda<T extends Comparable<T>> {

    private Nodo<T> raiz;

    private static class Nodo<T> {
        T valor;
        Nodo<T> izquierda;
        Nodo<T> derecha;

        public Nodo(T valor) {
            this.valor = valor;
        }
    }

    // Insertar
    public void insertar(T valor) {
        raiz = insertarRec(raiz, valor);
    }

    private Nodo<T> insertarRec(Nodo<T> actual, T valor) {
        if (actual == null) return new Nodo<>(valor);

        if (valor.compareTo(actual.valor) < 0)
            actual.izquierda = insertarRec(actual.izquierda, valor);
        else
            actual.derecha = insertarRec(actual.derecha, valor);

        return actual;
    }

    // Recorrido inorder
    public List<T> obtenerEnOrden() {
        List<T> lista = new ArrayList<>();
        inorderRec(raiz, lista);
        return lista;
    }

    private void inorderRec(Nodo<T> actual, List<T> lista) {
        if (actual != null) {
            inorderRec(actual.izquierda, lista);
            lista.add(actual.valor);
            inorderRec(actual.derecha, lista);
        }
    }

    // Eliminar nodo
    public void eliminar(T valor) {
        raiz = eliminarRec(raiz, valor);
    }

    private Nodo<T> eliminarRec(Nodo<T> actual, T valor) {
        if (actual == null) return null;

        int cmp = valor.compareTo(actual.valor);

        if (cmp < 0) {
            actual.izquierda = eliminarRec(actual.izquierda, valor);
        } else if (cmp > 0) {
            actual.derecha = eliminarRec(actual.derecha, valor);
        } else {
            if (actual.izquierda == null) return actual.derecha;
            if (actual.derecha == null) return actual.izquierda;

            Nodo<T> sucesor = minimo(actual.derecha);
            actual.valor = sucesor.valor;
            actual.derecha = eliminarRec(actual.derecha, sucesor.valor);
        }
        return actual;
    }

    private Nodo<T> minimo(Nodo<T> nodo) {
        while (nodo.izquierda != null) nodo = nodo.izquierda;
        return nodo;
    }
}
