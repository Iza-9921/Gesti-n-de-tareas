package com.example.integradoravirna.modelo;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Usuario {
    private static final AtomicLong GENERADOR_ID = new AtomicLong(1);

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String password;
    private LocalDateTime fechaRegistro;
    private boolean activo;

    public Usuario() {
        this.id = GENERADOR_ID.getAndIncrement();
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    public Usuario(String nombre, String email, String telefono, String password) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}