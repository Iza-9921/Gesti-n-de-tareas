package com.example.integradoravirna.modelo;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Modelo que representa una tarea.
 */
public class Tarea {
    private static final AtomicLong GENERADOR_ID = new AtomicLong(1);

    private Long id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private Estado estado;
    private LocalDateTime fechaCreacion;

    public Tarea() {
        this.id = GENERADOR_ID.getAndIncrement();
        this.estado = Estado.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Tarea(String titulo, String descripcion, Prioridad prioridad) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    @Override
    public String toString() {
        return "Tarea{" + "id=" + id + ", titulo='" + titulo + '\'' + ", prioridad=" + prioridad + ", estado=" + estado + '}';
    }
}
