package com.example.integradoravirna.web;

import com.example.integradoravirna.modelo.*;
import com.example.integradoravirna.servicio.ServicioTareas;
import com.example.integradoravirna.servicio.ServicioUsuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class controladorTareas {

    private final ServicioTareas servicioTareas;
    private final ServicioUsuario servicioUsuario;

    public controladorTareas(ServicioTareas servicioTareas, ServicioUsuario servicioUsuario) {
        this.servicioTareas = servicioTareas;
        this.servicioUsuario = servicioUsuario;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return servicioUsuario.getUsuarioAutenticado(auth.getName());
        }
        return null;
    }

    private Long getUsuarioIdAutenticado() {
        Usuario usuario = getUsuarioAutenticado();
        return usuario != null ? usuario.getId() : null;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "mensajeConfirmacion", required = false) String mensajeConfirmacion,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "busqueda", required = false) String busqueda) {

        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Usuario usuario = getUsuarioAutenticado();
        model.addAttribute("usuario", usuario);

        try {
            model.addAttribute("tareaNueva", servicioTareas.nuevaTareaVacia(usuarioId));
            model.addAttribute("prioridades", servicioTareas.obtenerPrioridades());

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                model.addAttribute("busqueda", busqueda);
                model.addAttribute("resultadosBusqueda", servicioTareas.buscarTareas(busqueda, usuarioId));
            } else {
                model.addAttribute("tareasAlta", servicioTareas.tareasPorPrioridad(Prioridad.ALTA, usuarioId));
                model.addAttribute("tareasMedia", servicioTareas.tareasPorPrioridad(Prioridad.MEDIA, usuarioId));
                model.addAttribute("tareasBaja", servicioTareas.tareasPorPrioridad(Prioridad.BAJA, usuarioId));
            }

            model.addAttribute("colaTareas", servicioTareas.obtenerColaComoListaEstandar(usuarioId));
            model.addAttribute("totalEnCola", servicioTareas.totalEnCola(usuarioId));

            Tarea siguiente = servicioTareas.verSiguienteEnCola(usuarioId);
            model.addAttribute("siguienteEnCola", siguiente != null ? siguiente.getTitulo() : "—");

            model.addAttribute("pilaHistorial", servicioTareas.obtenerHistorialComoListaEstandar(usuarioId));
            model.addAttribute("estadisticas", servicioTareas.obtenerEstadisticasUsuario(usuarioId));

            if (mensajeConfirmacion != null) model.addAttribute("mensajeConfirmacion", mensajeConfirmacion);
            if (error != null) model.addAttribute("error", error);

            return "index";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la página: " + e.getMessage());
            return "index";
        }
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute("tareaNueva") Tarea tarea, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";

        tarea.setUsuarioId(usuarioId);
        servicioTareas.agregarTarea(tarea);
        redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea agregada correctamente");
        return "redirect:/";
    }

    @PostMapping("/encolar/{id}")
    public String encolar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";

        boolean success = servicioTareas.encolar(id, usuarioId);
        if (success) redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea encolada exitosamente");
        else redirectAttributes.addAttribute("error", "No se pudo encolar la tarea");
        return "redirect:/";
    }

    @PostMapping("/desencolar")
    public String desencolar(RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";

        Tarea tarea = servicioTareas.desencolar(usuarioId);
        if (tarea != null) redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea desencolada: " + tarea.getTitulo());
        else redirectAttributes.addAttribute("error", "La cola está vacía");
        return "redirect:/";
    }

    @PostMapping("/procesar")
    public String procesar(RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";

        Tarea tarea = servicioTareas.procesarSiguiente(usuarioId);
        if (tarea != null) redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea procesada: " + tarea.getTitulo());
        else redirectAttributes.addAttribute("error", "No hay tareas para procesar");
        return "redirect:/";
    }

    @PostMapping("/completar/{id}")
    public String completar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";

        boolean success = servicioTareas.completarTarea(id, usuarioId);
        if (success) redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea marcada como completada");
        else redirectAttributes.addAttribute("error", "No se pudo completar la tarea");
        return "redirect:/";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";

        boolean success = servicioTareas.eliminarTarea(id, usuarioId);
        if (success) redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea eliminada exitosamente");
        else redirectAttributes.addAttribute("error", "No se pudo eliminar la tarea");
        return "redirect:/";
    }

    @PostMapping("/limpiarHistorial")
    public String limpiarHistorial(RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";
        servicioTareas.limpiarHistorial(usuarioId);
        redirectAttributes.addAttribute("mensajeConfirmacion", "Historial limpiado exitosamente");
        return "redirect:/";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";
        Long usuarioId = usuario.getId();
        model.addAttribute("usuario", usuario);
        model.addAttribute("estadisticas", servicioTareas.obtenerEstadisticasUsuario(usuarioId));
        return "perfil";
    }

    @GetMapping("/panel-data")
    @ResponseBody
    public Map<String, Object> panelData() {
        Map<String, Object> data = new HashMap<>();
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            data.put("totalEnCola", 0);
            data.put("siguienteEnCola", "—");
            data.put("cola", Collections.emptyList());
            data.put("historial", Collections.emptyList());
            return data;
        }

        data.put("totalEnCola", servicioTareas.totalEnCola(usuarioId));
        Tarea s = servicioTareas.verSiguienteEnCola(usuarioId);
        data.put("siguienteEnCola", s != null ? s.getTitulo() : "—");
        data.put("cola", servicioTareas.obtenerColaComoLista(usuarioId));

        List<Map<String, String>> hist = new ArrayList<>();
        for (Historial h : servicioTareas.obtenerHistorial(usuarioId)) {
            Map<String, String> m = new HashMap<>();
            m.put("hora", h.getFecha() != null ? h.getFecha().toLocalTime().toString().substring(0,5) : "--:--");
            m.put("titulo", h.getTitulo());
            m.put("accion", h.getAccion());
            m.put("descripcion", h.getDescripcion());
            hist.add(m);
        }
        data.put("historial", hist);

        return data;
    }

    @GetMapping("/ordenadas-alfabetico")
    public String ordenadasAlfabetico(Model model) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";
        model.addAttribute("tareasOrdenadas", servicioTareas.obtenerTareasOrdenadasComoLista(usuarioId));
        model.addAttribute("usuario", getUsuarioAutenticado());
        model.addAttribute("orden", "Alfabético");
        return "tareas_ordenadas";
    }

    @GetMapping("/arbol")
    public String mostrarArbol(Model model) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) return "redirect:/login";
        model.addAttribute("visualizacionArbol", servicioTareas.obtenerVisualizacionArbol(usuarioId));
        model.addAttribute("usuario", getUsuarioAutenticado());
        return "arbol_visualizacion";
    }
}
