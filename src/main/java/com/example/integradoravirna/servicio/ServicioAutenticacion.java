package com.example.integradoravirna.servicio;

import com.example.integradoravirna.modelo.Usuario;
import com.example.integradoravirna.servicio.ServicioUsuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicioAutenticacion implements UserDetailsService {

    private final ServicioUsuario servicioUsuario;

    // Constructor que inyecta el servicio de usuarios
    public ServicioAutenticacion(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    // Metodo requerido por Spring Security para cargar usuarios por su nombre de usuario (email)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Busca el usuario por email en nuestro servicio
        Usuario usuario = servicioUsuario.buscarPorEmail(email);

        // Valida que el usuario exista y este activo
        if (usuario == null || !usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario invalido");
        }

        // Construye un objeto UserDetails de Spring Security
        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())   // Usa el hash BCrypt almacenado
                .authorities("USER")               // Rol de usuario
                .build();
    }
}