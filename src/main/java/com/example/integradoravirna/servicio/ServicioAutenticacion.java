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

    public ServicioAutenticacion(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = servicioUsuario.buscarPorEmail(email);

        if (usuario == null || !usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario inválido");
        }

        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())   // BCrypt hash
                .authorities("USER")
                .build();
    }
}
