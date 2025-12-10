package com.example.integradoravirna.servicio;

import com.example.integradoravirna.estructuras.*;
import com.example.integradoravirna.modelo.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServicioTareas {

    // Mapa para almacenar estructuras de datos por usuario
    // Cada usuario tiene su propio arbol, lista de tareas, cola e historial
    private final Map<Long, ArbolBinarioBusqueda<Tarea>> arbolesPorUsuario = new HashMap<>();
    private final Map<Long, MiListaArreglo<Tarea>> tareasPorUsuario = new HashMap<>();
    private final Map<Long, Cola<Tarea>> colasPorUsuario = new HashMap<>();
    private final Map<Long, Pila<Historial>> historialesPorUsuario = new HashMap<>();

    private final ServicioUsuario servicioUsuario;

    // Constructor que inyecta el servicio de usuarios e inicializa tareas de ejemplo
    public ServicioTareas(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        inicializarTareasEjemplo();
    }

    // Inicializa tareas de ejemplo para cada usuario registrado
    private void inicializarTareasEjemplo() {
        List<Usuario> usuarios = servicioUsuario.obtenerTodosUsuarios();
        for (Usuario usuario : usuarios) {
            Long id = usuario.getId();
            // Crear estructuras de datos para cada usuario
            tareasPorUsuario.put(id, new MiListaArreglo<>());
            colasPorUsuario.put(id, new Cola<>());
            historialesPorUsuario.put(id, new Pila<>());
            arbolesPorUsuario.put(id, new ArbolBinarioBusqueda<>());
            crearTareasEjemploParaUsuario(id);
        }
    }

    // Crea tres tareas de ejemplo (ALTA, MEDIA, BAJA) para un usuario
    private void crearTareasEjemploParaUsuario(Long usuarioId) {
        agregarTarea(new Tarea("Revisar emails", "Revisar correo", Prioridad.ALTA, usuarioId));
        agregarTarea(new Tarea("Preparar informe", "Informe mensual", Prioridad.MEDIA, usuarioId));
        agregarTarea(new Tarea("Organizar archivos", "Ordenar documentos", Prioridad.BAJA, usuarioId));
    }

    // Metodos auxiliares para obtener estructuras de datos de un usuario
    // Si no existen, las crea
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

    // Agrega una nueva tarea al sistema
    public Tarea agregarTarea(Tarea t) {
        if (t == null) throw new IllegalArgumentException("Tarea null");
        if (t.getUsuarioId() == null) throw new IllegalArgumentException("Tarea sin usuario");
        if (t.getEstado() == null) t.setEstado(Estado.PENDIENTE);

        // Agrega a la lista, al arbol binario y registra en historial
        tareas(t.getUsuarioId()).agregar(t);
        arbol(t.getUsuarioId()).insertar(t);
        historial(t.getUsuarioId()).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "CREADA"));
        return t;
    }

    // Busca una tarea por su ID para un usuario especifico
    public Tarea buscarPorId(Long id, Long usuarioId) {
        if (id == null || usuarioId == null) return null;
        MiListaArreglo<Tarea> lista = tareas(usuarioId);
        for (int i = 0; i < lista.tamaño(); i++) {
            Tarea t = lista.obtener(i);
            if (t != null && id.equals(t.getId())) return t;
        }
        return null;
    }

    // Elimina una tarea de la lista de pendientes y del arbol
    private void eliminarDePendientes(Tarea t) {
        if (t == null) return;
        tareas(t.getUsuarioId()).eliminar(t);
        // eliminar del arbol si existe
        arbol(t.getUsuarioId()).eliminar(t);
    }

    // Marca una tarea como completada
    public boolean completarTarea(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;

        t.setEstado(Estado.COMPLETADA);
        eliminarDePendientes(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "COMPLETADA"));
        return true;
    }

    // Encola una tarea (la mueve a la cola de procesamiento)
    public boolean encolar(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;

        eliminarDePendientes(t);
        cola(usuarioId).encolar(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "ENCOLADA"));
        return true;
    }

    // Desencola una tarea (la saca de la cola y la vuelve a pendientes)
    public Tarea desencolar(Long usuarioId) {
        Tarea t = cola(usuarioId).desencolar();
        if (t == null) return null;

        t.setEstado(Estado.PENDIENTE);
        tareas(usuarioId).agregar(t);
        arbol(usuarioId).insertar(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "DESENCOLADA"));
        return t;
    }

    // Procesa la siguiente tarea en cola (la marca como completada)
    public Tarea procesarSiguiente(Long usuarioId) {
        Tarea t = cola(usuarioId).desencolar();
        if (t == null) return null;
        t.setEstado(Estado.COMPLETADA);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "PROCESADA"));
        return t;
    }

    // Elimina una tarea completamente del sistema
    public boolean eliminarTarea(Long id, Long usuarioId) {
        Tarea t = buscarPorId(id, usuarioId);
        if (t == null) return false;
        eliminarDePendientes(t);
        historial(usuarioId).apilar(new Historial(t.getTitulo(), t.getDescripcion(), "ELIMINADA"));
        return true;
    }

    // Obtiene todas las tareas de un usuario como List estandar
    public List<Tarea> obtenerTodas(Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> tareas = tareas(usuarioId);
        for (int i = 0; i < tareas.tamaño(); i++) {
            lista.add(tareas.obtener(i));
        }
        return lista;
    }

    // Obtiene la cola de tareas como List estandar
    public List<Tarea> obtenerColaComoLista(Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        MiListaArreglo<Tarea> colaLista = cola(usuarioId).comoLista();
        for (int i = 0; i < colaLista.tamaño(); i++) {
            lista.add(colaLista.obtener(i));
        }
        return lista;
    }

    // Obtiene el historial de acciones como List estandar
    public List<Historial> obtenerHistorial(Long usuarioId) {
        List<Historial> lista = new ArrayList<>();
        MiListaArreglo<Historial> pila = historial(usuarioId).comoLista();
        for (int i = 0; i < pila.tamaño(); i++) {
            lista.add(pila.obtener(i));
        }
        return lista;
    }

    // Muestra la siguiente tarea en cola sin desencolarla
    public Tarea verSiguienteEnCola(Long usuarioId) {
        return cola(usuarioId).verFrente();
    }

    // Obtiene el numero total de tareas en cola
    public int totalEnCola(Long usuarioId) {
        return cola(usuarioId).tamaño();
    }

    // Obtiene tareas ordenadas alfabeticamente usando el arbol binario
    public List<Tarea> obtenerTareasOrdenadasAlfabeticamente(Long usuarioId) {
        MiListaArreglo<Tarea> inOrden = arbol(usuarioId).inOrden();
        List<Tarea> resultado = new ArrayList<>();
        for (int i = 0; i < inOrden.tamaño(); i++) {
            resultado.add(inOrden.obtener(i));
        }
        return resultado;
    }

    // Metodos extra usados por el controlador

    // Crea una tarea vacia para un usuario
    public Tarea nuevaTareaVacia(Long usuarioId) {
        Tarea t = new Tarea();
        t.setUsuarioId(usuarioId);
        return t;
    }

    // Obtiene todas las prioridades disponibles
    public Prioridad[] obtenerPrioridades() {
        return Prioridad.values();
    }

    // Filtra tareas por prioridad especifica
    public List<Tarea> tareasPorPrioridad(Prioridad prioridad, Long usuarioId) {
        List<Tarea> lista = new ArrayList<>();
        for (Tarea t : obtenerTodas(usuarioId)) {
            if (t != null && t.getPrioridad() == prioridad) lista.add(t);
        }
        return lista;
    }

    // Busca tareas por texto en titulo o descripcion
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

    // Actualiza una tarea existente
    public Tarea actualizarTarea(Long id, Tarea nueva, Long usuarioId) {
        Tarea vieja = buscarPorId(id, usuarioId);
        if (vieja == null) return null;
        if (nueva.getTitulo() != null && !nueva.getTitulo().isBlank()) vieja.setTitulo(nueva.getTitulo());
        vieja.setDescripcion(nueva.getDescripcion());
        if (nueva.getPrioridad() != null) vieja.setPrioridad(nueva.getPrioridad());
        if (nueva.getEstado() != null) vieja.setEstado(nueva.getEstado());
        // reinsertar en arbol para mantener orden si cambio titulo
        arbol(usuarioId).eliminar(vieja);
        arbol(usuarioId).insertar(vieja);
        historial(usuarioId).apilar(new Historial("Tarea actualizada: " + vieja.getTitulo(),
                "Actualizada", "ACTUALIZADA"));
        return vieja;
    }

    // Limpia el historial de un usuario
    public void limpiarHistorial(Long usuarioId) {
        historialesPorUsuario.put(usuarioId, new Pila<>());
    }

    // Obtiene estadisticas de tareas de un usuario
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

    // Aliases y adaptadores (metodos que llaman a otros con nombre mas descriptivo)
    public List<Tarea> obtenerColaComoListaEstandar(Long usuarioId) {
        return obtenerColaComoLista(usuarioId);
    }

    public List<Historial> obtenerHistorialComoListaEstandar(Long usuarioId) {
        return obtenerHistorial(usuarioId);
    }

    public List<Tarea> obtenerTareasOrdenadasComoLista(Long usuarioId) {
        return obtenerTareasOrdenadasAlfabeticamente(usuarioId);
    }

    // Obtiene representacion visual del arbol (tabla)
    public String obtenerVisualizacionArbol(Long usuarioId) {
        return arbol(usuarioId).visualizarArbol();
    }
}