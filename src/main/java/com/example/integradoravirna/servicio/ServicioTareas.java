package com.example.integradoravirna.servicio;

import com.example.integradoravirna.estructuras.*;
import com.example.integradoravirna.modelo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioTareas {

    private final MiListaArreglo<Tarea> repositorio = new MiListaArreglo<>();
    private final Pila<Historial> historial = new Pila<>();
    private final Cola<Tarea> colaProcesamiento = new Cola<>();

    private final ArbolBinarioBusqueda<Tarea> arbolPrioridades = new ArbolBinarioBusqueda<>();

    public ServicioTareas() {}

    public void limpiarHistorial() {
        historial.vaciar();
    }

    public String agregarTarea(Tarea tarea) {
        repositorio.agregar(tarea);
        colaProcesamiento.encolar(tarea);
        arbolPrioridades.insertar(tarea);

        agregarAHistorial(new Historial(
                tarea.getTitulo(),
                tarea.getDescripcion(),
                "CREADA",
                LocalDateTime.now()
        ));

        return "Tarea creada y encolada correctamente";
    }

    public boolean eliminarPorId(long id) {
        Optional<Tarea> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Tarea t = opt.get();
            boolean ok = repositorio.eliminar(t);

            colaProcesamiento.removeIf(e -> e.getId().equals(id));
            arbolPrioridades.eliminar(t);

            return ok;
        }
        return false;
    }

    public List<Tarea> obtenerTodas() {
        List<Tarea> salida = new ArrayList<>();
        for (int i = 0; i < repositorio.tamaño(); i++)
            salida.add(repositorio.obtener(i));
        return salida;
    }

    public Optional<Tarea> buscarPorId(long id) {
        for (int i = 0; i < repositorio.tamaño(); i++) {
            Tarea t = repositorio.obtener(i);
            if (t.getId() == id) return Optional.of(t);
        }
        return Optional.empty();
    }

    public void agregarAHistorial(Historial h) { historial.apilar(h); }
    public List<Historial> obtenerHistorialComoLista() { return historial.comoLista(); }

    public void encolarTarea(Tarea tarea) { colaProcesamiento.encolar(tarea); }
    public Tarea desencolarTarea() { return colaProcesamiento.desencolar(); }
    public Tarea verFrenteCola() { return colaProcesamiento.verFrente(); }
    public List<Tarea> obtenerColaComoLista() { return colaProcesamiento.comoLista(); }
    public int tamañoCola() { return colaProcesamiento.tamaño(); }

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
                    LocalDateTime.now()
            ));

            return true;
        }
        return false;
    }

    public boolean procesarFrente() {
        Tarea frente = desencolarTarea();
        if (frente == null) return false;

        frente.setEstado(Estado.COMPLETADA);

        agregarAHistorial(new Historial(
                frente.getTitulo(),
                frente.getDescripcion(),
                "PROCESADA",
                LocalDateTime.now()
        ));

        return true;
    }

    public List<Tarea> obtenerPorPrioridad(Prioridad prioridad) {
        List<Tarea> salida = new ArrayList<>();
        for (int i = 0; i < repositorio.tamaño(); i++) {
            Tarea t = repositorio.obtener(i);
            if (t.getPrioridad() == prioridad)
                salida.add(t);
        }
        return salida;
    }

    public List<Tarea> obtenerTareasOrdenadas() {
        return arbolPrioridades.obtenerEnOrden();
    }
}
