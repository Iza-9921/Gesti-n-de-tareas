package com.example.integradoravirna.estructuras;

/**
 * Pila simple (LIFO).
 */
public class Pila<T> {
    private Object[] datos;
    private int tope;

    public Pila() {
        datos = new Object[10];
        tope = 0;
    }

    public void apilar(T elemento) {
        if (tope == datos.length) {
            Object[] nuevo = new Object[datos.length * 2];
            System.arraycopy(datos, 0, nuevo, 0, datos.length);
            datos = nuevo;
        }
        datos[tope++] = elemento;
    }

    @SuppressWarnings("unchecked")
    public T desapilar() {
        if (tope == 0) return null;
        T valor = (T) datos[--tope];
        datos[tope] = null;
        return valor;
    }

    @SuppressWarnings("unchecked")
    public T verTope() {
        if (tope == 0) return null;
        return (T) datos[tope - 1];
    }

    public boolean estaVacia() { return tope == 0; }
    public int tamaño() { return tope; }
}
