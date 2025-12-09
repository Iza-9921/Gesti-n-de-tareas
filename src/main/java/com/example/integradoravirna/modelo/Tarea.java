package com.example.integradoravirna.modelo;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Tarea implements Comparable<Tarea> {

    private static final AtomicLong GENERADOR_ID = new AtomicLong(1);

    private Long id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private Estado estado;
    private LocalDateTime fechaCreacion;
    private Long usuarioId; // ID del usuario dueño de la tarea

    public Tarea() {
        this.id = GENERADOR_ID.getAndIncrement();
        this.estado = Estado.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Tarea(String titulo, String descripcion, Prioridad prioridad, Long usuarioId) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.usuarioId = usuarioId;
    }

    @Override
    public int compareTo(Tarea otra) {
        int comp = this.prioridad.ordinal() - otra.prioridad.ordinal();
        if (comp != 0) return comp;
        return this.id.compareTo(otra.id);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                ", usuarioId=" + usuarioId +
                '}';
    }
}