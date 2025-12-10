/*package com.example.integradoravirna.estructuras;

public class ArbolBinario<T> {
    private NodoArbol<T> raiz;

    public ArbolBinario() {
        raiz = null;
    }

    public void insertarRaiz(T valor) {
        if (raiz == null) {
            raiz = new NodoArbol<>(valor);
        }
    }

    public void insertarIzquierdo(T padre, T hijo) {
        NodoArbol<T> nodoPadre = buscarNodo(raiz, padre);
        if (nodoPadre != null && nodoPadre.getIzquierdo() == null) {
            nodoPadre.setIzquierdo(new NodoArbol<>(hijo));
        }
    }

    public void insertarDerecho(T padre, T hijo) {
        NodoArbol<T> nodoPadre = buscarNodo(raiz, padre);
        if (nodoPadre != null && nodoPadre.getDerecho() == null) {
            nodoPadre.setDerecho(new NodoArbol<>(hijo));
        }
    }

    private NodoArbol<T> buscarNodo(NodoArbol<T> nodo, T valor) {
        if (nodo == null || nodo.getValor().equals(valor)) {
            return nodo;
        }

        NodoArbol<T> encontrado = buscarNodo(nodo.getIzquierdo(), valor);
        if (encontrado != null) {
            return encontrado;
        }

        return buscarNodo(nodo.getDerecho(), valor);
    }

    public MiListaArreglo<T> recorridoPreOrden() {
        MiListaArreglo<T> resultado = new MiListaArreglo<>();
        preOrdenRec(raiz, resultado);
        return resultado;
    }

    private void preOrdenRec(NodoArbol<T> nodo, MiListaArreglo<T> resultado) {
        if (nodo != null) {
            resultado.agregar(nodo.getValor());
            preOrdenRec(nodo.getIzquierdo(), resultado);
            preOrdenRec(nodo.getDerecho(), resultado);
        }
    }
}
*/