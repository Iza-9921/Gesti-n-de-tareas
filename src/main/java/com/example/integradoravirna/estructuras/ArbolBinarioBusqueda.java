package com.example.integradoravirna.estructuras;

public class ArbolBinarioBusqueda<T extends Comparable<T>> {
    private NodoArbol<T> raiz;

    public ArbolBinarioBusqueda() {
        raiz = null;
    }

    public void insertar(T valor) {
        raiz = insertarRec(raiz, valor);
    }

    private NodoArbol<T> insertarRec(NodoArbol<T> nodo, T valor) {
        if (nodo == null) {
            return new NodoArbol<>(valor);
        }

        if (valor.compareTo(nodo.getValor()) < 0) {
            nodo.setIzquierdo(insertarRec(nodo.getIzquierdo(), valor));
        } else if (valor.compareTo(nodo.getValor()) > 0) {
            nodo.setDerecho(insertarRec(nodo.getDerecho(), valor));
        }

        return nodo;
    }

    public boolean buscar(T valor) {
        return buscarRec(raiz, valor);
    }

    private boolean buscarRec(NodoArbol<T> nodo, T valor) {
        if (nodo == null) {
            return false;
        }

        if (valor.equals(nodo.getValor())) {
            return true;
        }

        return valor.compareTo(nodo.getValor()) < 0 ?
                buscarRec(nodo.getIzquierdo(), valor) :
                buscarRec(nodo.getDerecho(), valor);
    }

    public MiListaArreglo<T> inOrden() {
        MiListaArreglo<T> resultado = new MiListaArreglo<>();
        inOrdenRec(raiz, resultado);
        return resultado;
    }

    private void inOrdenRec(NodoArbol<T> nodo, MiListaArreglo<T> resultado) {
        if (nodo != null) {
            inOrdenRec(nodo.getIzquierdo(), resultado);
            resultado.agregar(nodo.getValor());
            inOrdenRec(nodo.getDerecho(), resultado);
        }
    }
}