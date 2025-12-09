package com.example.integradoravirna.servicio;

import com.example.integradoravirna.estructuras.*;
import com.example.integradoravirna.modelo.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    // PROBLEMA: El árbol no se inicializa en el método inicializarTareasEjemplo()
    private void inicializarTareasEjemplo() {
        // Obtener usuarios existentes
        java.util.List<Usuario> usuarios = servicioUsuario.obtenerTodosUsuarios();

        for (Usuario usuario : usuarios) {
            Long usuarioId = usuario.getId();

            // Inicializar estructuras para este usuario
            // FALTA: Inicializar el árbol aquí
            tareasPorUsuario.put(usuarioId, new MiListaArreglo<>());
            colasPorUsuario.put(usuarioId, new Cola<>());
            historialesPorUsuario.put(usuarioId, new Pila<>());

            // AGREGAR ESTA LÍNEA: Inicializar el árbol para cada usuario
            arbolesPorUsuario.put(usuarioId, new ArbolBinarioBusqueda<>());

            // Crear tareas de ejemplo
            crearTareasEjemploParaUsuario(usuarioId, usuario.getNombre());
        }
    }

    private void crearTareasEjemploParaUsuario(Long usuarioId, String nombreUsuario) {
        Tarea t1 = new Tarea("Revisar emails", "Revisar correo electrónico del día", Prioridad.ALTA, usuarioId);
        Tarea t2 = new Tarea("Preparar informe", "Preparar informe mensual", Prioridad.MEDIA, usuarioId);
        Tarea t3 = new Tarea("Organizar archivos", "Organizar documentos en la nube", Prioridad.BAJA, usuarioId);

        // Agregar a estructuras
        agregarTarea(t1);
        agregarTarea(t2);
        agregarTarea(t3);
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

    // Método para obtener el árbol de un usuario
    private ArbolBinarioBusqueda<Tarea> obtenerArbolUsuario(Long usuarioId) {
        return arbolesPorUsuario.computeIfAbsent(usuarioId, k -> new ArbolBinarioBusqueda<>());
    }

    public Tarea agregarTarea(Tarea t) {
        if (t == null) throw new IllegalArgumentException("Tarea null");
        if (t.getUsuarioId() == null) throw new IllegalArgumentException("Tarea sin usuario");
        if (t.getEstado() == null) t.setEstado(Estado.PENDIENTE);

        Long usuarioId = t.getUsuarioId();

        // 1. Agregar a la lista principal
        obtenerTareasUsuario(usuarioId).agregar(t);

        // 2. Agregar al árbol de búsqueda
        obtenerArbolUsuario(usuarioId).insertar(t);

        // 3. Registrar en historial
        obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "CREADA"));

        return t;
    }

    // PROBLEMA: Eliminar tarea no la elimina del árbol
    public boolean eliminarTarea(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;

        // 1. Eliminar de la lista principal
        boolean removed = obtenerTareasUsuario(usuarioId).eliminar(t);

        // NOTA: Para eliminar del árbol necesitarías implementar un método de eliminación
        // en ArbolBinarioBusqueda. Por ahora solo se elimina de la lista.
        // System.out.println("Nota: La tarea debería eliminarse también del árbol BST");

        if (removed) {
            obtenerHistorialUsuario(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "ELIMINADA"));
        }
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

        String tituloAnterior = tareaExistente.getTitulo();
        String descripcionAnterior = tareaExistente.getDescripcion();
        Prioridad prioridadAnterior = tareaExistente.getPrioridad();

        // Actualizar campos
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

        // NOTA: Al actualizar el título, el árbol BST ya no estará correctamente ordenado
        // porque no se actualiza la posición en el árbol. Se necesitaría eliminar y reinsertar.

        obtenerHistorialUsuario(usuarioId).apilar(new Historial(
                "Tarea actualizada: " + tituloAnterior,
                "De: " + tituloAnterior + " (" + prioridadAnterior + ") a: " +
                        tareaExistente.getTitulo() + " (" + tareaExistente.getPrioridad() + ")",
                "ACTUALIZADA"
        ));

        return tareaExistente;
    }

    public java.util.List<Tarea> buscarTareas(String texto, Long usuarioId) {
        java.util.List<Tarea> resultados = new java.util.ArrayList<>();
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

    public java.util.List<Tarea> obtenerColaComoListaEstándar(Long usuarioId) {
        java.util.List<Tarea> lista = new java.util.ArrayList<>();
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

    public java.util.List<Historial> obtenerHistorialComoListaEstándar(Long usuarioId) {
        java.util.List<Historial> lista = new java.util.ArrayList<>();
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

        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && t.getPrioridad() == Prioridad.ALTA) {
                ordenadas.agregar(t);
            }
        }

        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && t.getPrioridad() == Prioridad.MEDIA) {
                ordenadas.agregar(t);
            }
        }

        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null && t.getPrioridad() == Prioridad.BAJA) {
                ordenadas.agregar(t);
            }
        }

        return ordenadas;
    }

    public java.util.List<Tarea> obtenerTareasOrdenadasComoLista(Long usuarioId) {
        java.util.List<Tarea> lista = new java.util.ArrayList<>();
        MiListaArreglo<Tarea> ordenadas = obtenerTareasOrdenadasPorPrioridad(usuarioId);
        for (int i = 0; i < ordenadas.tamaño(); i++) {
            Tarea t = ordenadas.obtener(i);
            if (t != null) lista.add(t);
        }
        return lista;
    }

    public java.util.List<Tarea> tareasPorPrioridad(Prioridad p, Long usuarioId) {
        java.util.List<Tarea> lista = new java.util.ArrayList<>();
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

    public java.util.List<Tarea> obtenerTodasComoLista(Long usuarioId) {
        java.util.List<Tarea> lista = new java.util.ArrayList<>();
        MiListaArreglo<Tarea> tareas = obtenerTareasUsuario(usuarioId);
        for (int i = 0; i < tareas.tamaño(); i++) {
            Tarea t = tareas.obtener(i);
            if (t != null) {
                lista.add(t);
            }
        }
        return lista;
    }

    public java.util.Map<String, Integer> obtenerEstadisticasUsuario(Long usuarioId) {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
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

    // Buscar tareas usando el árbol (aunque no es más eficiente así)
    public java.util.List<Tarea> buscarTareasConArbol(String texto, Long usuarioId) {
        java.util.List<Tarea> resultados = new java.util.ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return obtenerTodasComoLista(usuarioId);
        }

        String textoBusqueda = texto.toLowerCase().trim();
        MiListaArreglo<Tarea> todasTareas = obtenerTareasUsuario(usuarioId);

        // NOTA: Esto no usa realmente el árbol para búsqueda eficiente
        // Para usar el árbol eficientemente necesitarías implementar búsqueda por rango
        for (int i = 0; i < todasTareas.tamaño(); i++) {
            Tarea t = todasTareas.obtener(i);
            if (t != null) {
                boolean coincide =
                        (t.getTitulo() != null && t.getTitulo().toLowerCase().contains(textoBusqueda)) ||
                                (t.getDescripcion() != null && t.getDescripcion().toLowerCase().contains(textoBusqueda));

                if (coincide) {
                    resultados.add(t);
                }
            }
        }

        return resultados;
    }

    // Obtener tareas ordenadas alfabéticamente (usando árbol - ESTO SÍ FUNCIONA)
    public java.util.List<Tarea> obtenerTareasOrdenadasAlfabeticamente(Long usuarioId) {
        java.util.List<Tarea> lista = new java.util.ArrayList<>();
        MiListaArreglo<Tarea> ordenadas = obtenerArbolUsuario(usuarioId).inOrden();

        for (int i = 0; i < ordenadas.tamaño(); i++) {
            Tarea t = ordenadas.obtener(i);
            if (t != null) lista.add(t);
        }

        return lista;
    }

    // Buscar si existe una tarea con título específico (usando árbol)
    public boolean existeTareaConTitulo(String titulo, Long usuarioId) {
        // Crear una tarea temporal para la búsqueda
        Tarea tareaBusqueda = new Tarea();
        tareaBusqueda.setTitulo(titulo);

        // ESTO SÍ usa el árbol para búsqueda eficiente O(log n)
        return obtenerArbolUsuario(usuarioId).buscar(tareaBusqueda);
    }

    public String obtenerVisualizacionArbol(Long usuarioId) {
        return obtenerArbolUsuario(usuarioId).visualizarArbol();
    }

    // Método adicional para obtener estadísticas del árbol
    public java.util.Map<String, Object> obtenerEstadisticasArbol(Long usuarioId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // Verificar si el árbol está vacío (necesitas agregar método estaVacio() en ArbolBinarioBusqueda)
        // Por ahora asumimos que si no hay tareas, el árbol está vacío
        java.util.List<Tarea> tareasOrdenadas = obtenerTareasOrdenadasAlfabeticamente(usuarioId);

        stats.put("totalNodos", tareasOrdenadas.size());
        stats.put("tareasOrdenadas", tareasOrdenadas);

        if (!tareasOrdenadas.isEmpty()) {
            stats.put("primeraAlfabeticamente", tareasOrdenadas.get(0).getTitulo());
            stats.put("ultimaAlfabeticamente", tareasOrdenadas.get(tareasOrdenadas.size() - 1).getTitulo());
        }

        return stats;
    }
}