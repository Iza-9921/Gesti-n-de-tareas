package com.example.integradoravirna.servicio;

import com.example.integradoravirna.estructuras.Cola;
import com.example.integradoravirna.estructuras.MiListaArreglo;
import com.example.integradoravirna.estructuras.Pila;
import com.example.integradoravirna.modelo.Estado;
import com.example.integradoravirna.modelo.Historial;
import com.example.integradoravirna.modelo.Prioridad;
import com.example.integradoravirna.modelo.Tarea;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioTareas {

    private final MiListaArreglo<Tarea> repositorio = new MiListaArreglo<>();
    private final Pila<Historial> historial = new Pila<>();
    private final Cola<Tarea> colaProcesamiento = new Cola<>();

    public ServicioTareas() { }


  //Metodo agregar tarea
    public String agregarTarea(Tarea tarea) {
        repositorio.agregar(tarea);
        colaProcesamiento.encolar(tarea);

        agregarAHistorial(new Historial(
                tarea.getTitulo(),
                tarea.getDescripcion(),
                "CREADA",
                java.time.LocalDateTime.now()
        ));
        return "Tarea creada y encolada correctamente";
    }

    public boolean eliminarPorId(long id) {
        Optional<Tarea> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Tarea t = opt.get();
            boolean ok = repositorio.eliminar(t);
            colaProcesamiento.removeIf(e -> e.getId().equals(id));
            return ok;
        }
        return false;
    }

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

    // Historial
    public void agregarAHistorial(Historial h) { historial.apilar(h); }
    public List<Historial> obtenerHistorialComoLista() { return historial.comoLista(); }

    // Cola
    public void encolarTarea(Tarea tarea) { colaProcesamiento.encolar(tarea); }
    public Tarea desencolarTarea() { return colaProcesamiento.desencolar(); }
    public Tarea verFrenteCola() { return colaProcesamiento.verFrente(); }
    public List<Tarea> obtenerColaComoLista() { return colaProcesamiento.comoLista(); }
    public int tamañoCola() { return colaProcesamiento.tamaño(); }

    // Marcar completada
    public boolean marcarComoCompletada(long id) {
        Optional<Tarea> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Tarea t = opt.get();
            t.setEstado(Estado.COMPLETADA);
            colaProcesamiento.removeIf(e -> e.getId().equals(id));

            agregarAHistorial(new Historial(
                    t.getTitulo(),
                    t.getDescripcion(),
                    "COMPLETADA",
                    java.time.LocalDateTime.now()
            ));

            return true;
        }
        return false;
    }

    // Procesar frente
    public boolean procesarFrente() {
        Tarea frente = desencolarTarea();
        if (frente == null) return false;
        frente.setEstado(Estado.COMPLETADA);
        agregarAHistorial(new Historial(
                frente.getTitulo(),
                frente.getDescripcion(),
                "PROCESADA",
                java.time.LocalDateTime.now()
        ));
        return true;
    }

    // Filtra tareas por prioridad
    public List<Tarea> obtenerPorPrioridad(Prioridad prioridad) {
        List<Tarea> salida = new ArrayList<>();
        for (int i = 0; i < repositorio.tamaño(); i++) {
            Tarea t = repositorio.obtener(i);
            if (t.getPrioridad() == prioridad) salida.add(t);
        }
        return salida;
    }
}
