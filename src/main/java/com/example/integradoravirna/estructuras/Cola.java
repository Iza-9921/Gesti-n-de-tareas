package com.example.integradoravirna.estructuras;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Cola<T> {
    private Object[] datos;
    private int inicio;
    private int fin;
    private int size;

    public Cola() {
        datos = new Object[10];
        inicio = 0;
        fin = 0;
        size = 0;
    }

    public void encolar(T elemento) {
        if (size == datos.length) expandir();
        datos[fin] = elemento;
        fin = (fin + 1) % datos.length;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T desencolar() {
        if (size == 0) return null;
        T elemento = (T) datos[inicio];
        datos[inicio] = null;
        inicio = (inicio + 1) % datos.length;
        size--;
        return elemento;
    }

    @SuppressWarnings("unchecked")
    public T verFrente() {
        if (size == 0) return null;
        return (T) datos[inicio];
    }

    public boolean estaVacia() { return size == 0; }
    public int tamaño() { return size; }

    private void expandir() {
        Object[] nuevo = new Object[datos.length * 2];
        for (int i = 0; i < size; i++) {
            nuevo[i] = datos[(inicio + i) % datos.length];
        }
        datos = nuevo;
        inicio = 0;
        fin = size;
    }

    @SuppressWarnings("unchecked")
    public List<T> comoLista() {
        List<T> lista = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            lista.add((T) datos[(inicio + i) % datos.length]);
        }
        return lista;
    }

    public void removeIf(Predicate<T> predicate) {
        List<T> lista = comoLista();
        Cola<T> nueva = new Cola<>();
        for (T e : lista) {
            if (!predicate.test(e)) {
                nueva.encolar(e);
            }
        }
        // reemplazar internals con nueva
        this.datos = nueva.datos;
        this.inicio = nueva.inicio;
        this.fin = nueva.fin;
        this.size = nueva.size;
    }
}
