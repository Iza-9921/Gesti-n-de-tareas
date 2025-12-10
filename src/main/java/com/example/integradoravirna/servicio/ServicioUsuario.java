package com.example.integradoravirna.servicio;

import com.example.integradoravirna.modelo.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioUsuario {

    // Lista en memoria para almacenar usuarios (no usa base de datos)
    private final List<Usuario> usuarios = new ArrayList<>();

    // Codificador para encriptar contrasenas con BCrypt
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Constructor que crea usuarios de prueba al iniciar la aplicacion
    public ServicioUsuario() {
        // Crear usuarios de prueba CON CONTRASENAS ENCRIPTADAS
        registrarUsuario("Juan Perez", "juan@ejemplo.com", "5551234567", "password123");
        registrarUsuario("Maria Garcia", "maria@ejemplo.com", "5557654321", "password123");

        // Mensajes de consola para depuracion
        System.out.println("=== USUARIOS DE PRUEBA CREADOS ===");
        System.out.println("1. juan@ejemplo.com / password123");
        System.out.println("2. maria@ejemplo.com / password123");
        System.out.println("Total usuarios: " + usuarios.size());
    }

    // Metodo para registrar usuario recibiendo datos individuales
    public Usuario registrarUsuario(String nombre, String email, String telefono, String password) {
        // Verificar si el email ya existe
        if (buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        // Crear usuario y ENCRIPTAR la contrasena
        Usuario usuario = new Usuario(nombre, email, telefono, password);
        String passwordEncriptada = passwordEncoder.encode(password);
        usuario.setPassword(passwordEncriptada); // Contrasena encriptada
        usuarios.add(usuario);

        // Mensajes de depuracion
        System.out.println("Nuevo usuario registrado: " + email + " (ID: " + usuario.getId() + ")");
        System.out.println("Contrasena original: " + password);
        System.out.println("Contrasena encriptada: " + passwordEncriptada);
        return usuario;
    }

    // Metodo para registrar usuario recibiendo objeto Usuario completo
    public Usuario registrarUsuario(Usuario usuario) {
        // Verificar si el email ya existe
        if (buscarPorEmail(usuario.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        // Encriptar la contrasena antes de guardar
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        // Agregar usuario a la lista
        usuarios.add(usuario);

        // Mensajes de depuracion
        System.out.println(" Usuario nuevo guardado: " + usuario.getEmail());
        System.out.println(" Hash: " + passwordEncriptada);

        return usuario;
    }

    // Buscar usuario por su email
    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Obtener todos los usuarios registrados
    public List<Usuario> obtenerTodosUsuarios() {
        return new ArrayList<>(usuarios);
    }

    // Obtener usuario autenticado por email (alias de buscarPorEmail)
    public Usuario getUsuarioAutenticado(String email) {
        return buscarPorEmail(email);
    }
}