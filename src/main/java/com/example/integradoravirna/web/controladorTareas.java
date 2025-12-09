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
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
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

        System.out.println("=== CARGANDO PÁGINA PRINCIPAL PARA USUARIO ID: " + usuarioId + " ===");

        try {
            // 1. Tarea vacía para el formulario
            model.addAttribute("tareaNueva", servicioTareas.nuevaTareaVacia(usuarioId));
            model.addAttribute("prioridades", servicioTareas.obtenerPrioridades());

            // 2. Si hay búsqueda, mostrar resultados filtrados
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                model.addAttribute("busqueda", busqueda);
                model.addAttribute("resultadosBusqueda", servicioTareas.buscarTareas(busqueda, usuarioId));
            } else {
                // Tareas por prioridad (normales)
                model.addAttribute("tareasAlta", servicioTareas.tareasPorPrioridad(Prioridad.ALTA, usuarioId));
                model.addAttribute("tareasMedia", servicioTareas.tareasPorPrioridad(Prioridad.MEDIA, usuarioId));
                model.addAttribute("tareasBaja", servicioTareas.tareasPorPrioridad(Prioridad.BAJA, usuarioId));
            }

            // 3. Cola de procesamiento
            model.addAttribute("colaTareas", servicioTareas.obtenerColaComoListaEstándar(usuarioId));
            model.addAttribute("totalEnCola", servicioTareas.totalEnCola(usuarioId));

            // 4. Siguiente en cola
            Tarea siguiente = servicioTareas.verSiguienteEnCola(usuarioId);
            model.addAttribute("siguienteEnCola", siguiente != null ? siguiente.getTitulo() : "—");

            // 5. Historial
            model.addAttribute("pilaHistorial", servicioTareas.obtenerHistorialComoListaEstándar(usuarioId));

            // 6. Estadísticas del usuario
            model.addAttribute("estadisticas", servicioTareas.obtenerEstadisticasUsuario(usuarioId));

            // 7. Mensajes flash
            if (mensajeConfirmacion != null) {
                model.addAttribute("mensajeConfirmacion", mensajeConfirmacion);
            }
            if (error != null) {
                model.addAttribute("error", error);
            }

            System.out.println("✓ Página cargada exitosamente para usuario ID: " + usuarioId);
            return "index";

        } catch (Exception e) {
            System.err.println("✗ ERROR en index(): " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar la página: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Tarea tarea = servicioTareas.buscarPorId(id, usuarioId);
        if (tarea == null) {
            redirectAttributes.addAttribute("error", "Tarea no encontrada");
            return "redirect:/";
        }

        model.addAttribute("tarea", tarea);
        model.addAttribute("prioridades", servicioTareas.obtenerPrioridades());
        model.addAttribute("usuario", getUsuarioAutenticado());
        return "editar_tarea";
    }

    @PostMapping("/editar/{id}")
    public String actualizarTarea(@PathVariable Long id,
                                  @ModelAttribute Tarea tareaActualizada,
                                  RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            Tarea tarea = servicioTareas.actualizarTarea(id, tareaActualizada, usuarioId);
            if (tarea != null) {
                redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea actualizada exitosamente");
            } else {
                redirectAttributes.addAttribute("error", "No se pudo actualizar la tarea");
            }
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/ordenadas")
    public String ordenadas(Model model) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("tareasOrdenadas", servicioTareas.obtenerTareasOrdenadasComoLista(usuarioId));
        model.addAttribute("usuario", getUsuarioAutenticado());
        return "tareas_ordenadas";
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute("tareaNueva") Tarea tarea,
                          RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            tarea.setUsuarioId(usuarioId); // Asegurar que la tarea pertenece al usuario
            servicioTareas.agregarTarea(tarea);
            redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea agregada correctamente");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Error al agregar tarea: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/encolar/{id}")
    public String encolar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        boolean success = servicioTareas.encolar(id, usuarioId);
        if (success) {
            redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea encolada exitosamente");
        } else {
            redirectAttributes.addAttribute("error", "No se pudo encolar la tarea");
        }
        return "redirect:/";
    }

    @PostMapping("/desencolar")
    public String desencolar(RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Tarea tarea = servicioTareas.desencolar(usuarioId);
        if (tarea != null) {
            redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea desencolada: " + tarea.getTitulo());
        } else {
            redirectAttributes.addAttribute("error", "La cola está vacía");
        }
        return "redirect:/";
    }

    @PostMapping("/procesar")
    public String procesar(RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Tarea tarea = servicioTareas.procesarSiguiente(usuarioId);
        if (tarea != null) {
            redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea procesada: " + tarea.getTitulo());
        } else {
            redirectAttributes.addAttribute("error", "No hay tareas para procesar");
        }
        return "redirect:/";
    }

    @PostMapping("/completar/{id}")
    public String completar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        boolean success = servicioTareas.completarTarea(id, usuarioId);
        if (success) {
            redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea marcada como completada");
        } else {
            redirectAttributes.addAttribute("error", "No se pudo completar la tarea");
        }
        return "redirect:/";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        boolean success = servicioTareas.eliminarTarea(id, usuarioId);
        if (success) {
            redirectAttributes.addAttribute("mensajeConfirmacion", "Tarea eliminada exitosamente");
        } else {
            redirectAttributes.addAttribute("error", "No se pudo eliminar la tarea");
        }
        return "redirect:/";
    }

    @PostMapping("/limpiarHistorial")
    public String limpiarHistorial(RedirectAttributes redirectAttributes) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        servicioTareas.limpiarHistorial(usuarioId);
        redirectAttributes.addAttribute("mensajeConfirmacion", "Historial limpiado exitosamente");
        return "redirect:/";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }

        Long usuarioId = usuario.getId();
        model.addAttribute("usuario", usuario);
        model.addAttribute("estadisticas", servicioTareas.obtenerEstadisticasUsuario(usuarioId));
        return "perfil";
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "✅ APLICACIÓN FUNCIONANDO - " + java.time.LocalDateTime.now();
    }
    // En controladorTareas.java
    @GetMapping("/ordenadas-alfabetico")
    public String ordenadasAlfabetico(Model model) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("tareasOrdenadas", servicioTareas.obtenerTareasOrdenadasAlfabeticamente(usuarioId));
        model.addAttribute("usuario", getUsuarioAutenticado());
        model.addAttribute("orden", "Alfabético");
        return "tareas_ordenadas"; // Puedes reusar el mismo template
    }
    @GetMapping("/arbol")
    public String mostrarArbol(Model model) {
        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("visualizacionArbol", servicioTareas.obtenerVisualizacionArbol(usuarioId));
        model.addAttribute("usuario", getUsuarioAutenticado());
        return "arbol_visualizacion";
    }
}