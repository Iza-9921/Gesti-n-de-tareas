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

    // ✔ Constructor vacío necesario para Spring y para inicializar campos
    public Tarea() {
        this.id = GENERADOR_ID.getAndIncrement();
        this.estado = Estado.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    // ✔ Constructor con parámetros
    public Tarea(String titulo, String descripcion, Prioridad prioridad) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    // ✔ Implementación de Comparable (prioridad → id)
    @Override
    public int compareTo(Tarea otra) {
        // primero compara por prioridad
        int comp = this.prioridad.ordinal() - otra.prioridad.ordinal();

        if (comp != 0) return comp;

        // si tienen misma prioridad, ordena por ID
        return this.id.compareTo(otra.id);
    }

    // --- Getters y Setters ----

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
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                '}';
    }
}
