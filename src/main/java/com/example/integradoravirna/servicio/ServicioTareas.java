package com.example.integradoravirna.servicio;

import com.example.integradoravirna.estructuras.*;
import com.example.integradoravirna.modelo.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicioTareas {

    // Mapa: usuarioId -> sus tareas
    private final Map<Long, MiListaArreglo<Tarea>> tareasPorUsuario = new HashMap<>();
    private final Map<Long, Cola<Tarea>> colasPorUsuario = new HashMap<>();
    private final Map<Long, Pila<Historial>> historialesPorUsuario = new HashMap<>();

    // Tareas de ejemplo para cada usuario
    private final ServicioUsuario servicioUsuario;

    public ServicioTareas(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        inicializarTareasEjemplo();
    }

    private void inicializarTareasEjemplo() {
        // Obtener usuarios existentes y crear tareas para cada uno
        List<Usuario> usuarios = servicioUsuario.obtenerTodosUsuarios();

        for (Usuario usuario : usuarios) {
            Long usuarioId = usuario.getId();

            // Inicializar estructuras para este usuario
            tareasPorUsuario.put(usuarioId, new MiListaArreglo<>());
            colasPorUsuario.put(usuarioId, new Cola<>());
            historialesPorUsuario.put(usuarioId, new Pila<>());

            // Crear tareas de ejemplo para este usuario
            crearTareasEjemploParaUsuario(usuarioId, usuario.getNombre());
        }
    }

    private void crearTareasEjemploParaUsuario(Long usuarioId, String nombreUsuario) {
        System.out.println("Creando tareas de ejemplo para usuario: " + nombreUsuario + " (ID: " + usuarioId + ")");

        agregarTarea(new Tarea("Revisar emails", "Revisar correo electrónico del día", Prioridad.ALTA, usuarioId));
        agregarTarea(new Tarea("Preparar informe", "Preparar informe mensual", Prioridad.MEDIA, usuarioId));
        agregarTarea(new Tarea("Organizar archivos", "Organizar documentos en la nube", Prioridad.BAJA, usuarioId));
    }

    private MiListaArreglo<Tarea> obtenerTareasUsuario(Long usuarioId) {
        return tareasPorUsuario.computeIfAbsent(usuarioId, k -> new MiListaArreglo<>());
    }

    private Cola<Tarea> obtenerColaUsuario(Long usuarioId) {
        return colasPorUsuario.computeIfAbsent(usuarioId, k -> new Cola<>());
    }

    private Pila<Historial> obtenerHistorialUsuario(Long usuarioId) {
        return historialesPorUsuario.computeIfAbsent(usuarioId, k -> new Pila<>());
    }

    public Tarea agregarTarea(Tarea t) {
        if (t == null) throw new IllegalArgumentException("Tarea null");
        if (t.getUsuarioId() == null) throw new IllegalArgumentException("Tarea sin usuario");
        if (t.getEstado() == null) t.setEstado(Estado.PENDIENTE);

        Long usuarioId = t.getUsuarioId();
        obtenerTareasUsuario(usuarioId).agregar(t);
        obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "CREADA"));

        System.out.println("Tarea agregada para usuario ID: " + usuarioId + " - " + t.getTitulo());
        return t;
    }

    public boolean eliminarTarea(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;
        boolean removed = obtenerTareasUsuario(usuarioId).eliminar(t);
        if (removed) obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "ELIMINADA"));
        return removed;
    }

    public Tarea buscarPorId(Long id, Long usuarioId) {
        if (id == null || usuarioId == null) return null;
        MiListaArreglo<Tarea> tareas = obtenerTareasUsuario(usuarioId);
        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && id.equals(t.getId())) return t;
        }
        return null;
    }

    public Tarea actualizarTarea(Long id, Tarea tareaActualizada, Long usuarioId) {
        Tarea tareaExistente = buscarPorId(id, usuarioId);
        if (tareaExistente == null) return null;

        // Guardar datos antiguos para el historial
        String tituloAnterior = tareaExistente.getTitulo();
        String descripcionAnterior = tareaExistente.getDescripcion();
        Prioridad prioridadAnterior = tareaExistente.getPrioridad();

        // Actualizar solo los campos que no son null
        if (tareaActualizada.getTitulo() != null && !tareaActualizada.getTitulo().isEmpty()) {
            tareaExistente.setTitulo(tareaActualizada.getTitulo());
        }
        if (tareaActualizada.getDescripcion() != null) {
            tareaExistente.setDescripcion(tareaActualizada.getDescripcion());
        }
        if (tareaActualizada.getPrioridad() != null) {
            tareaExistente.setPrioridad(tareaActualizada.getPrioridad());
        }
        if (tareaActualizada.getEstado() != null) {
            tareaExistente.setEstado(tareaActualizada.getEstado());
        }

        obtenerHistorialUsuario(usuarioId).apilar(new Historial(
                "Tarea actualizada: " + tituloAnterior,
                "De: " + tituloAnterior + " (" + prioridadAnterior + ") a: " +
                        tareaExistente.getTitulo() + " (" + tareaExistente.getPrioridad() + ")",
                "ACTUALIZADA"
        ));

        return tareaExistente;
    }

    public List<Tarea> buscarTareas(String texto, Long usuarioId) {
        List<Tarea> resultados = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return obtenerTodasComoLista(usuarioId);
        }

        String textoBusqueda = texto.toLowerCase().trim();
        MiListaArreglo<Tarea> tareas = obtenerTareasUsuario(usuarioId);

        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null) {
                boolean coincide =
                        (t.getTitulo() != null && t.getTitulo().toLowerCase().contains(textoBusqueda)) ||
                                (t.getDescripcion() != null && t.getDescripcion().toLowerCase().contains(textoBusqueda)) ||
                                (t.getPrioridad() != null && t.getPrioridad().name().toLowerCase().contains(textoBusqueda)) ||
                                (t.getEstado() != null && t.getEstado().name().toLowerCase().contains(textoBusqueda));

                if (coincide) {
                    resultados.add(t);
                }
            }
        }

        return resultados;
    }

    public boolean encolar(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;
        obtenerColaUsuario(usuarioId).encolar(t);
        obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "ENCOLADA"));
        return true;
    }

    public Tarea desencolar(Long usuarioId) {
        Tarea t = obtenerColaUsuario(usuarioId).desencolar();
        if (t != null) obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "DESENCOLADA"));
        return t;
    }

    public Tarea procesarSiguiente(Long usuarioId) {
        Tarea t = obtenerColaUsuario(usuarioId).desencolar();
        if (t == null) return null;
        t.setEstado(Estado.COMPLETADA);
        obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "PROCESADA"));
        return t;
    }

    public Tarea verSiguienteEnCola(Long usuarioId) {
        return obtenerColaUsuario(usuarioId).verFrente();
    }

    public int totalEnCola(Long usuarioId) {
        return obtenerColaUsuario(usuarioId).tamaño();
    }

    public MiListaArreglo<Tarea> obtenerColaComoLista(Long usuarioId) {
        return obtenerColaUsuario(usuarioId).comoLista();
    }

    public List<Tarea> obtenerColaComoListaEstándar(Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> colaLista = obtenerColaUsuario(usuarioId).comoLista();
        for (int i = 0; i < colaLista.tamaño(); i++) {
            Tarea t = colaLista.obtener(i);
            if (t != null) lista.add(t);
        }
        return lista;
    }

    public MiListaArreglo<Historial> obtenerHistorialComoLista(Long usuarioId) {
        return obtenerHistorialUsuario(usuarioId).comoLista();
    }

    public List<Historial> obtenerHistorialComoListaEstándar(Long usuarioId) {
        List<Historial> lista = new ArrayList<>();
        MiListaArreglo<Historial> historialLista = obtenerHistorialUsuario(usuarioId).comoLista();
        for (int i = 0; i < historialLista.tamaño(); i++) {
            Historial h = historialLista.obtener(i);
            if (h != null) lista.add(h);
        }
        return lista;
    }

    public void limpiarHistorial(Long usuarioId) {
        obtenerHistorialUsuario(usuarioId).vaciar();
    }

    public MiListaArreglo<Tarea> obtenerTareasOrdenadasPorPrioridad(Long usuarioId) {
        MiListaArreglo<Tarea> ordenadas = new MiListaArreglo<>();
        MiListaArreglo<Tarea> tareas = obtenerTareasUsuario(usuarioId);

        // ALTA
        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && t.getPrioridad() == Prioridad.ALTA) {
                ordenadas.agregar(t);
            }
        }

        // MEDIA
        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && t.getPrioridad() == Prioridad.MEDIA) {
                ordenadas.agregar(t);
            }
        }

        // BAJA
        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && t.getPrioridad() == Prioridad.BAJA) {
                ordenadas.agregar(t);
            }
        }

        return ordenadas;
    }

    public List<Tarea> obtenerTareasOrdenadasComoLista(Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> ordenadas = obtenerTareasOrdenadasPorPrioridad(usuarioId);
        for (int i = 0; i < ordenadas.tamaño(); i++) {
            Tarea t = ordenadas.obtener(i);
            if (t != null) lista.add(t);
        }
        return lista;
    }

    public List<Tarea> tareasPorPrioridad(Prioridad p, Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> tareas = obtenerTareasUsuario(usuarioId);
        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && t.getPrioridad() == p) {
                lista.add(t);
            }
        }
        return lista;
    }

    public boolean completarTarea(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;
        t.setEstado(Estado.COMPLETADA);
        obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "COMPLETADA"));
        return true;
    }

    public Prioridad[] obtenerPrioridades() {
        return Prioridad.values();
    }

    public Tarea nuevaTareaVacia(Long usuarioId) {
        Tarea t = new Tarea();
        t.setTitulo("");
        t.setDescripcion("");
        t.setPrioridad(Prioridad.MEDIA);
        t.setEstado(Estado.PENDIENTE);
        t.setUsuarioId(usuarioId);
        return t;
    }

    public List<Tarea> obtenerTodasComoLista(Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> tareas = obtenerTareasUsuario(usuarioId);
        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null) {
                lista.add(t);
            }
        }
        return lista;
    }

    // Método para obtener estadísticas del usuario
    public Map<String, Integer> obtenerEstadisticasUsuario(Long usuarioId) {
        Map<String, Integer> stats = new HashMap<>();
        MiListaArreglo<Tarea> tareas = obtenerTareasUsuario(usuarioId);

        int total = tareas.tamaño();
        int completadas = 0;
        int pendientes = 0;
        int alta = 0, media = 0, baja = 0;

        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null) {
                if (t.getEstado() == Estado.COMPLETADA) completadas++;
                else pendientes++;

                if (t.getPrioridad() == Prioridad.ALTA) alta++;
                else if (t.getPrioridad() == Prioridad.MEDIA) media++;
                else if (t.getPrioridad() == Prioridad.BAJA) baja++;
            }
        }

        stats.put("total", total);
        stats.put("completadas", completadas);
        stats.put("pendientes", pendientes);
        stats.put("alta", alta);
        stats.put("media", media);
        stats.put("baja", baja);

        return stats;
    }
}