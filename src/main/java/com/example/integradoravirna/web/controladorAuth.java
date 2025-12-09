package com.example.integradoravirna.web;

import com.example.integradoravirna.modelo.Usuario;
import com.example.integradoravirna.servicio.ServicioUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class controladorAuth {

    private final ServicioUsuario servicioUsuario;

    public controladorAuth(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               @RequestParam(value = "registro", required = false) String registro,
                               Model model) {

        if (error != null) {
            model.addAttribute("error", "Credenciales incorrectas. Intente nuevamente.");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Ha cerrado sesión exitosamente.");
        }
        if (registro != null) {
            model.addAttribute("mensaje", "Registro exitoso. Ahora puede iniciar sesión.");
        }

        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario,
                                   @RequestParam("confirmarPassword") String confirmarPassword,
                                   RedirectAttributes redirectAttributes) {

        System.out.println("📝 INTENTANDO REGISTRAR USUARIO:");
        System.out.println("   Email: " + usuario.getEmail());
        System.out.println("   Contraseña recibida: " + usuario.getPassword());
        System.out.println("   Confirmar contraseña: " + confirmarPassword);

        try {
            // Validaciones
            if (!usuario.getPassword().equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                System.out.println("❌ Error: Contraseñas no coinciden");
                return "redirect:/registro";
            }

            if (usuario.getPassword().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                System.out.println("❌ Error: Contraseña muy corta");
                return "redirect:/registro";
            }

            // Registrar usuario - LA CONTRASEÑA ESTÁ EN TEXTO PLANO AQUÍ
            servicioUsuario.registrarUsuario(usuario);

            System.out.println("✅ Usuario registrado exitosamente");
            redirectAttributes.addAttribute("registro", "true");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error en registro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        }
    }
}