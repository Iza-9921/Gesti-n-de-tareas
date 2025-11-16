package com.example.integradoravirna.servicio;

import com.example.integradoravirna.estructuras.Cola;
import com.example.integradoravirna.estructuras.MiListaArreglo;
import com.example.integradoravirna.estructuras.Pila;
import com.example.integradoravirna.modelo.Estado;
import com.example.integradoravirna.modelo.Prioridad;
import com.example.integradoravirna.modelo.Tarea;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio en memoria que usa las estructuras propias.
 */
@Service
public class ServicioTareas {

    private final MiListaArreglo<Tarea> repositorio = new MiListaArreglo<>();
    private final Pila<Tarea> historial = new Pila<>();
    private final Cola<Tarea> colaProcesamiento = new Cola<>();

    public ServicioTareas() {
        // datos de ejemplo
        repositorio.agregar(new Tarea("Ejemplo 1", "Primera tarea", Prioridad.MEDIA));
        repositorio.agregar(new Tarea("Ejemplo 2", "Segunda tarea", Prioridad.ALTA));
    }

    // LISTA (repositorio)
    public void agregarTarea(Tarea tarea) { repositorio.agregar(tarea); }

    public boolean eliminarTarea(Tarea tarea) { return repositorio.eliminar(tarea); }

    public List<Tarea> obtenerTodas() {
        List<Tarea> salida = new ArrayList<>();
        for (int i = 0; i < repositorio.tamaño(); i++) salida.add(repositorio.obtener(i));
        return salida;
    }

    public Optional<Tarea> buscarPorId(long id) {
        for (int i = 0; i < repositorio.tamaño(); i++) {
            Tarea t = repositorio.obtener(i);
            if (t.getId() == id) return Optional.of(t);
        }
        return Optional.empty();
    }

    // PILA (historial)
    public void registrarHistorial(Tarea tarea) { historial.apilar(tarea); }
    public Tarea obtenerUltimaCompletada() { return historial.verTope(); }

    // COLA (procesamiento)
    public void encolarTarea(Tarea tarea) { colaProcesamiento.encolar(tarea); }
    public Tarea desencolarTarea() { return colaProcesamiento.desencolar(); }
    public Tarea verFrenteCola() { return colaProcesamiento.verFrente(); }

    // Acción: marcar completada
    public boolean marcarComoCompletada(long id) {
        Optional<Tarea> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Tarea t = opt.get();
            t.setEstado(Estado.COMPLETADA);
            registrarHistorial(t);
            return true;
        }
        return false;
    }
}
