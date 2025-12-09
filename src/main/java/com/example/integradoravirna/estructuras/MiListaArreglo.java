package com.example.integradoravirna.estructuras;

public class MiListaArreglo<T> {
    private Object[] elementos;
    private int tamaño;
    private static final int CAPACIDAD_INICIAL = 10;

    public MiListaArreglo() {
        elementos = new Object[CAPACIDAD_INICIAL];
        tamaño = 0;
    }

    public void agregar(T elemento) {
        if (tamaño == elementos.length) {
            expandir();
        }
        elementos[tamaño] = elemento;
        tamaño++;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return (T) elementos[indice];
    }

    public boolean eliminar(T elemento) {
        for (int i = 0; i < tamaño; i++) {
            if (elementos[i].equals(elemento)) {
                for (int j = i; j < tamaño - 1; j++) {
                    elementos[j] = elementos[j + 1];
                }
                elementos[tamaño - 1] = null;
                tamaño--;
                return true;
            }
        }
        return false;
    }

    public int tamaño() {
        return tamaño;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    private void expandir() {
        Object[] nuevoArreglo = new Object[elementos.length * 2];
        System.arraycopy(elementos, 0, nuevoArreglo, 0, elementos.length);
        elementos = nuevoArreglo;
    }

    public void limpiar() {
        for (int i = 0; i < tamaño; i++) {
            elementos[i] = null;
        }
        tamaño = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tamaño; i++) {
            sb.append(elementos[i]);
            if (i < tamaño - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}