package com.example.integradoravirna.servicio;

import com.example.integradoravirna.modelo.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioUsuario {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ServicioUsuario() {
        // Crear usuarios de prueba CON CONTRASEÑAS ENCRIPTADAS
        registrarUsuario("Juan Pérez", "juan@ejemplo.com", "5551234567", "password123");
        registrarUsuario("María García", "maria@ejemplo.com", "5557654321", "password123");

        System.out.println("=== USUARIOS DE PRUEBA CREADOS ===");
        System.out.println("1. juan@ejemplo.com / password123");
        System.out.println("2. maria@ejemplo.com / password123");
        System.out.println("Total usuarios: " + usuarios.size());
    }

    public Usuario registrarUsuario(String nombre, String email, String telefono, String password) {
        // Verificar si el email ya existe
        if (buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear usuario y ENCRIPTAR la contraseña
        Usuario usuario = new Usuario(nombre, email, telefono, password);
        String passwordEncriptada = passwordEncoder.encode(password);
        usuario.setPassword(passwordEncriptada); // ¡IMPORTANTE!
        usuarios.add(usuario);

        System.out.println("✅ Nuevo usuario registrado: " + email + " (ID: " + usuario.getId() + ")");
        System.out.println("   Contraseña original: " + password);
        System.out.println("   Contraseña encriptada: " + passwordEncriptada);
        return usuario;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de registrar
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        return registrarUsuario(usuario.getNombre(), usuario.getEmail(),
                usuario.getTelefono(), usuario.getPassword());
    }

    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean autenticar(String email, String password) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null || !usuario.isActivo()) {
            System.out.println("❌ Usuario no encontrado o inactivo: " + email);
            return false;
        }

        boolean coincide = passwordEncoder.matches(password, usuario.getPassword());
        System.out.println("🔐 Autenticando: " + email +
                " | Contraseña coincide: " + coincide);
        return coincide;
    }

    public List<Usuario> obtenerTodosUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public boolean cambiarEstadoUsuario(Long id, boolean activo) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null) {
            usuario.setActivo(activo);
            return true;
        }
        return false;
    }

    public Usuario getUsuarioAutenticado(String email) {
        return buscarPorEmail(email);
    }
}