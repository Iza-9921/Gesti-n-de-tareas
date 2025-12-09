package com.example.integradoravirna.estructuras;

public class Pila<T> {
    private MiListaArreglo<T> elementos;

    public Pila() {
        elementos = new MiListaArreglo<>();
    }

    public void apilar(T elemento) {
        elementos.agregar(elemento);
    }

    public T desapilar() {
        if (estaVacia()) {
            return null;
        }
        T elemento = elementos.obtener(elementos.tamaño() - 1);
        elementos.eliminar(elemento);
        return elemento;
    }

    public T verTope() {
        if (estaVacia()) {
            return null;
        }
        return elementos.obtener(elementos.tamaño() - 1);
    }

    public int tamaño() {
        return elementos.tamaño();
    }

    public boolean estaVacia() {
        return elementos.estaVacia();
    }

    public MiListaArreglo<T> comoLista() {
        return elementos;
    }

    public void vaciar() {
        elementos.limpiar(); // ¡Aquí se usa limpiar()!
    }

    @Override
    public String toString() {
        return elementos.toString();
    }
}