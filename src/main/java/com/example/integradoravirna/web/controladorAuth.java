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

    // Constructor que inyecta el servicio de usuarios
    public controladorAuth(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    // Muestra la pagina de login
    // Puede recibir parametros para mostrar mensajes de error, logout o registro exitoso
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               @RequestParam(value = "registro", required = false) String registro,
                               Model model) {

        // Si hay error de autenticacion, muestra mensaje
        if (error != null) {
            model.addAttribute("error", "Credenciales incorrectas. Intente nuevamente.");
        }
        // Si el usuario cerro sesion, muestra mensaje
        if (logout != null) {
            model.addAttribute("mensaje", "Ha cerrado sesion exitosamente.");
        }
        // Si se registro correctamente, muestra mensaje
        if (registro != null) {
            model.addAttribute("mensaje", "Registro exitoso. Ahora puede iniciar sesion.");
        }

        // Retorna la plantilla login.html
        return "login";
    }

    // Muestra la pagina de registro de nuevo usuario
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        // Agrega un objeto Usuario vacio al modelo para el formulario
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Procesa el registro de un nuevo usuario
    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario,
                                   @RequestParam("confirmarPassword") String confirmarPassword,
                                   RedirectAttributes redirectAttributes) {

        // Mensajes de depuracion para ver lo que llega al servidor
        System.out.println("   INTENTANDO REGISTRAR USUARIO:");
        System.out.println("   Email: " + usuario.getEmail());
        System.out.println("   Contrasena recibida: " + usuario.getPassword());
        System.out.println("   Confirmar contrasena: " + confirmarPassword);

        try {
            // Validacion: las contrasenas deben coincidir
            if (!usuario.getPassword().equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contrasenas no coinciden");
                System.out.println("Error: Contrasenas no coinciden");
                return "redirect:/registro";
            }

            // Validacion: contrasena debe tener al menos 6 caracteres
            if (usuario.getPassword().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contrasena debe tener al menos 6 caracteres");
                System.out.println("Error: Contrasena muy corta");
                return "redirect:/registro";
            }

            // Registra el usuario en el sistema
            // NOTA: La contrasena esta en texto plano aqui, Spring Security deberia cifrarla
            servicioUsuario.registrarUsuario(usuario);

            System.out.println("Usuario registrado exitosamente");
            // Redirige al login con mensaje de exito
            redirectAttributes.addAttribute("registro", "true");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Captura errores de validacion del servicio
            System.out.println("Error en registro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        }
    }
}