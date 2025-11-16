package com.example.integradoravirna.web;

import com.example.integradoravirna.modelo.Prioridad;
import com.example.integradoravirna.modelo.Tarea;
import com.example.integradoravirna.servicio.ServicioTareas;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class controladorTareas {

    private final ServicioTareas servicio;

    public controladorTareas(ServicioTareas servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String inicio(Model modelo) {
        modelo.addAttribute("tareas", servicio.obtenerTodas());
        modelo.addAttribute("prioridades", Prioridad.values());
        modelo.addAttribute("tareaNueva", new Tarea());
        modelo.addAttribute("colaFrente", servicio.verFrenteCola());
        modelo.addAttribute("ultimaCompletada", servicio.obtenerUltimaCompletada());
        return "index";
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute Tarea tarea) {
        servicio.agregarTarea(tarea);
        return "redirect:/";
    }

    @PostMapping("/encolar/{id}")
    public String encolar(@PathVariable long id) {
        servicio.buscarPorId(id).ifPresent(servicio::encolarTarea);
        return "redirect:/";
    }

    @PostMapping("/desencolar")
    public String desencolar() {
        servicio.desencolarTarea();
        return "redirect:/";
    }

    @PostMapping("/completar/{id}")
    public String completar(@PathVariable long id) {
        servicio.marcarComoCompletada(id);
        return "redirect:/";
    }
}
