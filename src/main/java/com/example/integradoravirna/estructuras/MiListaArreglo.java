package com.example.integradoravirna.estructuras;

import java.util.Arrays;

public class MiListaArreglo<T> {
    private Object[] datos;
    private int size;

    public MiListaArreglo() {
        datos = new Object[10];
        size = 0;
    }

    public void agregar(T elemento) {
        asegurarCapacidad();
        datos[size++] = elemento;
    }

    public boolean eliminar(T elemento) {
        if (elemento == null) return false;
        for (int i = 0; i < size; i++) {
            if (datos[i].equals(elemento)) {
                int mov = size - i - 1;
                if (mov > 0) System.arraycopy(datos, i + 1, datos, i, mov);
                datos[--size] = null;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        if (indice < 0 || indice >= size) throw new IndexOutOfBoundsException();
        return (T) datos[indice];
    }

    public int tamaño() { return size; }
    public boolean estaVacia() { return size == 0; }

    @SuppressWarnings("unchecked")
    public boolean contiene(T elemento) {
        if (elemento == null) return false;
        for (int i = 0; i < size; i++) if (datos[i].equals(elemento)) return true;
        return false;
    }

    private void asegurarCapacidad() {
        if (size == datos.length) datos = Arrays.copyOf(datos, datos.length * 2);
    }
}
