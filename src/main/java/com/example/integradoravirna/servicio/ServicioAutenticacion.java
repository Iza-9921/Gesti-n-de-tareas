package com.example.integradoravirna.servicio;

import com.example.integradoravirna.modelo.Usuario;
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
        System.out.println("🔍 Buscando usuario: " + email);

        Usuario usuario = servicioUsuario.buscarPorEmail(email);

        if (usuario == null) {
            System.out.println("❌ Usuario no encontrado: " + email);
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        if (!usuario.isActivo()) {
            System.out.println("❌ Usuario desactivado: " + email);
            throw new UsernameNotFoundException("Usuario desactivado: " + email);
        }

        System.out.println("✅ Usuario encontrado: " + email);
        System.out.println("   Contraseña en BD: " + usuario.getPassword());

        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities("USER")
                .build();
    }
}