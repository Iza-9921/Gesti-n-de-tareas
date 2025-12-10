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
            return "Árbol vacío - No hay tareas";
        }

        StringBuilder sb = new StringBuilder();
        visualizarRec(raiz, "", true, sb);
        return sb.toString();
    }

    private void visualizarRec(NodoArbol<T> nodo, String prefijo, boolean esUltimo, StringBuilder sb) {
        if (nodo != null) {
            sb.append(prefijo);
            sb.append(esUltimo ? "└── " : "├── ");
            sb.append(nodo.getValor().toString()).append("\n");

            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");

            if (nodo.getDerecho() != null || nodo.getIzquierdo() != null) {
                if (nodo.getDerecho() != null) {
                    visualizarRec(nodo.getDerecho(), nuevoPrefijo, false, sb);
                } else {
                    sb.append(nuevoPrefijo).append("├── [NULL]\n");
                }

                if (nodo.getIzquierdo() != null) {
                    visualizarRec(nodo.getIzquierdo(), nuevoPrefijo, true, sb);
                } else {
                    sb.append(nuevoPrefijo).append("└── [NULL]\n");
                }
            }
        }
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    // Eliminación de un valor en el BST
    public void eliminar(T valor) {
        raiz = eliminarRec(raiz, valor);
    }

    private NodoArbol<T> eliminarRec(NodoArbol<T> nodo, T valor) {
        if (nodo == null) return null;

        if (valor.compareTo(nodo.getValor()) < 0) {
            nodo.setIzquierdo(eliminarRec(nodo.getIzquierdo(), valor));
        } else if (valor.compareTo(nodo.getValor()) > 0) {
            nodo.setDerecho(eliminarRec(nodo.getDerecho(), valor));
        } else {
            // encontrado
            if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
                return null;
            }
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            NodoArbol<T> sucesor = obtenerMinimo(nodo.getDerecho());
            nodo.setValor(sucesor.getValor());
            nodo.setDerecho(eliminarRec(nodo.getDerecho(), sucesor.getValor()));
        }
        return nodo;
    }

    private NodoArbol<T> obtenerMinimo(NodoArbol<T> nodo) {
        NodoArbol<T> current = nodo;
        while (current.getIzquierdo() != null) {
            current = current.getIzquierdo();
        }
        return current;
    }

    // Alias para compatibilidad
    public String visualizar() {
        return visualizarArbol();
    }
}
