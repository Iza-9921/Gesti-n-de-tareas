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
        List<Tarea> tareasAlta = servicio.obtenerPorPrioridad(Prioridad.ALTA);
        List<Tarea> tareasMedia = servicio.obtenerPorPrioridad(Prioridad.MEDIA);
        List<Tarea> tareasBaja = servicio.obtenerPorPrioridad(Prioridad.BAJA);

        List<Tarea> cola = servicio.obtenerColaComoLista();
        List<Historial> historial = servicio.obtenerHistorialComoLista();

        modelo.addAttribute("tareasAlta", tareasAlta);
        modelo.addAttribute("tareasMedia", tareasMedia);
        modelo.addAttribute("tareasBaja", tareasBaja);

        modelo.addAttribute("colaTareas", (cola != null) ? cola : Collections.emptyList());
        modelo.addAttribute("pilaHistorial", (historial != null) ? historial : Collections.emptyList());
        modelo.addAttribute("totalEnCola", servicio.tamañoCola());

        Tarea frente = servicio.verFrenteCola();
        modelo.addAttribute("siguienteEnCola", (frente != null && frente.getTitulo() != null) ? frente.getTitulo() : "—");

        modelo.addAttribute("prioridades", Prioridad.values());
        modelo.addAttribute("tareaNueva", new Tarea());

        return "index";
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute Tarea tarea, RedirectAttributes redirectAttrs) {
        String mensaje = servicio.agregarTarea(tarea); // ahora devuelve String
        redirectAttrs.addFlashAttribute("mensajeConfirmacion", mensaje);
        return "redirect:/";
    }

    @PostMapping("/encolar/{id}")
    public String encolar(@PathVariable("id") long id) {
        servicio.buscarPorId(id).ifPresent(servicio::encolarTarea);
        return "redirect:/";
    }

    @PostMapping("/desencolar")
    public String desencolar() {
        servicio.desencolarTarea();
        return "redirect:/";
    }

    @PostMapping("/completar/{id}")
    public String completar(@PathVariable("id") long id) {
        servicio.marcarComoCompletada(id);
        return "redirect:/";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        servicio.buscarPorId(id).ifPresent(t -> {
            servicio.agregarAHistorial(new Historial(
                    t.getTitulo(),
                    t.getDescripcion(),
                    "ELIMINADA",
                    LocalDateTime.now()
            ));
            servicio.eliminarPorId(id);
        });
        return "redirect:/";
    }

    @PostMapping("/procesar")
    public String procesar() {
        servicio.procesarFrente();
        return "redirect:/";
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void disableFavicon() { }
}
