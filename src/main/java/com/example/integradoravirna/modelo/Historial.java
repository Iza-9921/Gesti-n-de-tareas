package com.example.integradoravirna.modelo;

import java.time.LocalDateTime;

public class Historial {
    private String titulo;
    private String descripcion;
    private String accion;
    private LocalDateTime fecha;

    public Historial(String titulo, String descripcion, String accion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.accion = accion;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters (¡IMPORTANTE: deben ser públicos!)
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    // ¡ESTE GETTER ES EL QUE FALTA!
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return "Historial{" +
                "titulo='" + titulo + '\'' +
                ", accion='" + accion + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}