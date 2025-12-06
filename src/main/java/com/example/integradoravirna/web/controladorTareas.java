package com.example.integradoravirna.web;

import com.example.integradoravirna.modelo.Prioridad;
import com.example.integradoravirna.modelo.Tarea;
import com.example.integradoravirna.modelo.Historial;
import com.example.integradoravirna.servicio.ServicioTareas;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
public class controladorTareas {

    private final ServicioTareas servicio;

    public controladorTareas(ServicioTareas servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String inicio(Model modelo) {
        modelo.addAttribute("tareasAlta", servicio.obtenerPorPrioridad(Prioridad.ALTA));
        modelo.addAttribute("tareasMedia", servicio.obtenerPorPrioridad(Prioridad.MEDIA));
        modelo.addAttribute("tareasBaja", servicio.obtenerPorPrioridad(Prioridad.BAJA));

        modelo.addAttribute("colaTareas", servicio.obtenerColaComoLista());
        modelo.addAttribute("pilaHistorial", servicio.obtenerHistorialComoLista());
        modelo.addAttribute("totalEnCola", servicio.tamañoCola());

        Tarea frente = servicio.verFrenteCola();
        modelo.addAttribute("siguienteEnCola", (frente != null) ? frente.getTitulo() : "—");

        modelo.addAttribute("prioridades", Prioridad.values());
        modelo.addAttribute("tareaNueva", new Tarea());

        return "index";
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute Tarea tarea, RedirectAttributes redirectAttrs) {
        String mensaje = servicio.agregarTarea(tarea);
        redirectAttrs.addFlashAttribute("mensajeConfirmacion", mensaje);
        return "redirect:/";
    }

    @PostMapping("/encolar/{id}")
    public String encolar(@PathVariable long id, RedirectAttributes redirectAttrs) {
        servicio.buscarPorId(id).ifPresent(servicio::encolarTarea);
        redirectAttrs.addFlashAttribute("mensajeConfirmacion", "Tarea encolada");
        return "redirect:/";
    }

    @PostMapping("/desencolar")
    public String desencolar(RedirectAttributes redirectAttrs) {
        servicio.desencolarTarea();
        redirectAttrs.addFlashAttribute("mensajeConfirmacion", "Tarea quitada de la cola");
        return "redirect:/";
    }

    @PostMapping("/completar/{id}")
    public String completar(@PathVariable long id, RedirectAttributes redirectAttrs) {
        boolean ok = servicio.marcarComoCompletada(id);
        redirectAttrs.addFlashAttribute(ok ? "mensajeConfirmacion" : "error",
                ok ? "Tarea completada" : "No se encontró la tarea");
        return "redirect:/";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable long id, RedirectAttributes redirectAttrs) {
        servicio.buscarPorId(id).ifPresent(t -> {
            servicio.agregarAHistorial(new Historial(
                    t.getTitulo(),
                    t.getDescripcion(),
                    "ELIMINADA",
                    LocalDateTime.now()
            ));
            servicio.eliminarPorId(id);
        });
        redirectAttrs.addFlashAttribute("mensajeConfirmacion", "Tarea eliminada");
        return "redirect:/";
    }

    @PostMapping("/procesar")
    public String procesar(RedirectAttributes redirectAttrs) {
        boolean ok = servicio.procesarFrente();
        redirectAttrs.addFlashAttribute(ok ? "mensajeConfirmacion" : "error",
                ok ? "Tarea procesada" : "No hay tareas");
        return "redirect:/";
    }

    @GetMapping("/ordenadas")
    public String mostrarOrdenadas(Model model) {
        model.addAttribute("tareasOrdenadas", servicio.obtenerTareasOrdenadas());
        return "tareas_ordenadas";
    }

    @PostMapping("/limpiarHistorial")
    public String limpiarHistorial(RedirectAttributes redirectAttrs) {
        servicio.limpiarHistorial();
        redirectAttrs.addFlashAttribute("mensajeConfirmacion", "Historial limpiado correctamente");
        return "redirect:/";
    }
}
