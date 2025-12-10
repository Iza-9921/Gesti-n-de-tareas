package com.example.integradoravirna.servicio;

import com.example.integradoravirna.estructuras.*;
import com.example.integradoravirna.modelo.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServicioTareas {

    private final Map<Long, ArbolBinarioBusqueda<Tarea>> arbolesPorUsuario = new HashMap<>();
    private final Map<Long, MiListaArreglo<Tarea>> tareasPorUsuario = new HashMap<>();
    private final Map<Long, Cola<Tarea>> colasPorUsuario = new HashMap<>();
    private final Map<Long, Pila<Historial>> historialesPorUsuario = new HashMap<>();

    private final ServicioUsuario servicioUsuario;

    public ServicioTareas(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        inicializarTareasEjemplo();
    }

    private void inicializarTareasEjemplo() {
        List<Usuario> usuarios = servicioUsuario.obtenerTodosUsuarios();
        for (Usuario usuario : usuarios) {
            Long id = usuario.getId();
            tareasPorUsuario.put(id, new MiListaArreglo<>());
            colasPorUsuario.put(id, new Cola<>());
            historialesPorUsuario.put(id, new Pila<>());
            arbolesPorUsuario.put(id, new ArbolBinarioBusqueda<>());
            crearTareasEjemploParaUsuario(id);
        }
    }

    private void crearTareasEjemploParaUsuario(Long usuarioId) {
        agregarTarea(new Tarea("Revisar emails", "Revisar correo", Prioridad.ALTA, usuarioId));
        agregarTarea(new Tarea("Preparar informe", "Informe mensual", Prioridad.MEDIA, usuarioId));
        agregarTarea(new Tarea("Organizar archivos", "Ordenar documentos", Prioridad.BAJA, usuarioId));
    }

    private MiListaArreglo<Tarea> tareas(Long id) {
        return tareasPorUsuario.computeIfAbsent(id, k -> new MiListaArreglo<>());
    }

    private Cola<Tarea> cola(Long id) {
        return colasPorUsuario.computeIfAbsent(id, k -> new Cola<>());
    }

    private Pila<Historial> historial(Long id) {
        return historialesPorUsuario.computeIfAbsent(id, k -> new Pila<>());
    }

    private ArbolBinarioBusqueda<Tarea> arbol(Long id) {
        return arbolesPorUsuario.computeIfAbsent(id, k -> new ArbolBinarioBusqueda<>());
    }

    public Tarea agregarTarea(Tarea t) {
        if (t == null) throw new IllegalArgumentException("Tarea null");
        if (t.getUsuarioId() == null) throw new IllegalArgumentException("Tarea sin usuario");
        if (t.getEstado() == null) t.setEstado(Estado.PENDIENTE);

        tareas(t.getUsuarioId()).agregar(t);
        arbol(t.getUsuarioId()).insertar(t);
        historial(t.getUsuarioId()).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "CREADA"));
        return t;
    }

    public Tarea buscarPorId(Long id, Long usuarioId) {
        if (id == null || usuarioId == null) return null;
        MiListaArreglo<Tarea> lista = tareas(usuarioId);
        for (int i = 0; i < lista.tamaño(); i++) {
            Tarea t = lista.obtener(i);
            if (t != null && id.equals(t.getId())) return t;
        }
        return null;
    }

    private void eliminarDePendientes(Tarea t) {
        if (t == null) return;
        tareas(t.getUsuarioId()).eliminar(t);
        // eliminar del árbol si existe
        arbol(t.getUsuarioId()).eliminar(t);
    }

    public boolean completarTarea(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;

        t.setEstado(Estado.COMPLETADA);
        eliminarDePendientes(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "COMPLETADA"));
        return true;
    }

    public boolean encolar(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;

        eliminarDePendientes(t);
        cola(usuarioId).encolar(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "ENCOLADA"));
        return true;
    }

    public Tarea desencolar(Long usuarioId) {
        Tarea t = cola(usuarioId).desencolar();
        if (t == null) return null;

        t.setEstado(Estado.PENDIENTE);
        tareas(usuarioId).agregar(t);
        arbol(usuarioId).insertar(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "DESENCOLADA"));
        return t;
    }

    public Tarea procesarSiguiente(Long usuarioId) {
        Tarea t = cola(usuarioId).desencolar();
        if (t == null) return null;
        t.setEstado(Estado.COMPLETADA);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "PROCESADA"));
        return t;
    }

    public boolean eliminarTarea(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;
        eliminarDePendientes(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "ELIMINADA"));
        return true;
    }

    public List<Tarea> obtenerTodas(Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> tareas = tareas(usuarioId);
        for (int i = 0; i < tareas.tamaño(); i++) {
            lista.add(tareas.obtener(i));
        }
        return lista;
    }

    public List<Tarea> obtenerColaComoLista(Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> colaLista = cola(usuarioId).comoLista();
        for (int i = 0; i < colaLista.tamaño(); i++) {
            lista.add(colaLista.obtener(i));
        }
        return lista;
    }

    public List<Historial> obtenerHistorial(Long usuarioId) {
        List<Historial> lista = new ArrayList<>();
        MiListaArreglo<Historial> pila = historial(usuarioId).comoLista();
        for (int i = 0; i < pila.tamaño(); i++) {
            lista.add(pila.obtener(i));
        }
        return lista;
    }

    public Tarea verSiguienteEnCola(Long usuarioId) {
        return cola(usuarioId).verFrente();
    }

    public int totalEnCola(Long usuarioId) {
        return cola(usuarioId).tamaño();
    }

    public List<Tarea> obtenerTareasOrdenadasAlfabeticamente(Long usuarioId) {
        MiListaArreglo<Tarea> inOrden = arbol(usuarioId).inOrden();
        List<Tarea> resultado = new ArrayList<>();
        for (int i = 0; i < inOrden.tamaño(); i++) {
            resultado.add(inOrden.obtener(i));
        }
        return resultado;
    }

    // Métodos extra usados por el controlador
    public Tarea nuevaTareaVacia(Long usuarioId) {
        Tarea t = new Tarea();
        t.setUsuarioId(usuarioId);
        return t;
    }

    public Prioridad[] obtenerPrioridades() {
        return Prioridad.values();
    }

    public List<Tarea> tareasPorPrioridad(Prioridad prioridad, Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        for (Tarea t : obtenerTodas(usuarioId)) {
            if (t != null && t.getPrioridad() == prioridad) lista.add(t);
        }
        return lista;
    }

    public List<Tarea> buscarTareas(String texto, Long usuarioId) {
        if (texto == null || texto.trim().isEmpty()) return obtenerTodas(usuarioId);
        String tbus = texto.toLowerCase();
        List<Tarea> resultados = new ArrayList<>();
        for (Tarea t : obtenerTodas(usuarioId)) {
            if (t.getTitulo() != null && t.getTitulo().toLowerCase().contains(tbus)) resultados.add(t);
            else if (t.getDescripcion() != null && t.getDescripcion().toLowerCase().contains(tbus)) resultados.add(t);
        }
        return resultados;
    }

    public Tarea actualizarTarea(Long id, Tarea nueva, Long usuarioId) {
        Tarea vieja = buscarPorId(id, usuarioId);
        if (vieja == null) return null;
        if (nueva.getTitulo() != null && !nueva.getTitulo().isBlank()) vieja.setTitulo(nueva.getTitulo());
        vieja.setDescripcion(nueva.getDescripcion());
        if (nueva.getPrioridad() != null) vieja.setPrioridad(nueva.getPrioridad());
        if (nueva.getEstado() != null) vieja.setEstado(nueva.getEstado());
        // reinsertar en árbol para mantener orden si cambió título
        arbol(usuarioId).eliminar(vieja);
        arbol(usuarioId).insertar(vieja);
        historial(usuarioId).apilar(new Historial("Tarea actualizada: " + vieja.getTitulo(),
                "Actualizada", "ACTUALIZADA"));
        return vieja;
    }

    public void limpiarHistorial(Long usuarioId) {
        historialesPorUsuario.put(usuarioId, new Pila<>());
    }

    public Map<String, Integer> obtenerEstadisticasUsuario(Long usuarioId) {
        Map<String, Integer> m = new HashMap<>();
        m.put("total", obtenerTodas(usuarioId).size());
        m.put("completadas", (int) obtenerTodas(usuarioId).stream().filter(t -> t.getEstado() == Estado.COMPLETADA).count());
        m.put("pendientes", (int) obtenerTodas(usuarioId).stream().filter(t -> t.getEstado() != Estado.COMPLETADA).count());
        m.put("alta", (int) obtenerTodas(usuarioId).stream().filter(t -> t.getPrioridad() == Prioridad.ALTA).count());
        m.put("media", (int) obtenerTodas(usuarioId).stream().filter(t -> t.getPrioridad() == Prioridad.MEDIA).count());
        m.put("baja", (int) obtenerTodas(usuarioId).stream().filter(t -> t.getPrioridad() == Prioridad.BAJA).count());
        return m;
    }

    // Aliases y adaptadores
    public List<Tarea> obtenerColaComoListaEstandar(Long usuarioId) {
        return obtenerColaComoLista(usuarioId);
    }

    public List<Historial> obtenerHistorialComoListaEstandar(Long usuarioId) {
        return obtenerHistorial(usuarioId);
    }

    public List<Tarea> obtenerTareasOrdenadasComoLista(Long usuarioId) {
        return obtenerTareasOrdenadasAlfabeticamente(usuarioId);
    }

    public String obtenerVisualizacionArbol(Long usuarioId) {
        return arbol(usuarioId).visualizarArbol();
    }
}
