package com.example.integradoravirna.estructuras;

public class Cola<T> {
    private MiListaArreglo<T> elementos;

    public Cola() {
        elementos = new MiListaArreglo<>();
    }

    public void encolar(T elemento) {
        elementos.agregar(elemento);
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T elemento = elementos.obtener(0);
        elementos.eliminar(elemento);
        return elemento;
    }

    public T verFrente() {
        if (estaVacia()) {
            return null;
        }
        return elementos.obtener(0);
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