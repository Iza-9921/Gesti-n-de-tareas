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

    public String visualizarArbol() {
        if (raiz == null) {
            return "🌳 Árbol vacío - No hay tareas";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🌳 ÁRBOL BINARIO DE BÚSQUEDA 🌳\n");
        sb.append("═".repeat(40)).append("\n\n");
        visualizarRec(raiz, "", true, sb);
        return sb.toString();
    }

    private void visualizarRec(NodoArbol<T> nodo, String prefijo, boolean esUltimo, StringBuilder sb) {
        if (nodo != null) {
            sb.append(prefijo);
            sb.append(esUltimo ? "└── " : "├── ");
            sb.append(nodo.getValor().toString()).append("\n");

            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");

            // Primero mostrar el hijo derecho (para mejor visualización)
            if (nodo.getDerecho() != null || nodo.getIzquierdo() != null) {
                if (nodo.getDerecho() != null) {
                    visualizarRec(nodo.getDerecho(), nuevoPrefijo, false, sb);
                } else {
                    // Mostrar null si no hay hijo derecho
                    sb.append(nuevoPrefijo).append("├── [NULL]\n");
                }

                if (nodo.getIzquierdo() != null) {
                    visualizarRec(nodo.getIzquierdo(), nuevoPrefijo, true, sb);
                } else {
                    // Mostrar null si no hay hijo izquierdo
                    sb.append(nuevoPrefijo).append("└── [NULL]\n");
                }
            }
        }
    }

    // Método adicional para ver si el árbol está vacío
    public boolean estaVacio() {
        return raiz == null;
    }
}