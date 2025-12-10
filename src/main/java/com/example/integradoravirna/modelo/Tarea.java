package com.example.integradoravirna.modelo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Tarea implements Comparable<Tarea> {

    // Generador de IDs unicos para las tareas
    private static final AtomicLong GENERADOR_ID = new AtomicLong(1);

    // Atributos de la tarea
    private Long id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private Estado estado;
    private LocalDateTime fechaCreacion;
    private Long usuarioId;

    // Constructor vacio
    public Tarea() {
        this.id = GENERADOR_ID.getAndIncrement(); // Asigna ID autoincremental
        this.estado = Estado.PENDIENTE;           // Estado por defecto
        this.fechaCreacion = LocalDateTime.now(); // Fecha actual
    }

    // Constructor con parametros principales
    public Tarea(String titulo, String descripcion, Prioridad prioridad, Long usuarioId) {
        this(); // Llama al constructor vacio
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.usuarioId = usuarioId;
    }

    // Metodo para comparar tareas (para ordenamiento)
    // Opción 1: Modificar Tarea.compareTo()
    //tabla
    @Override
    public int compareTo(Tarea otra) {
        // Primero por prioridad (ALTA > MEDIA > BAJA)
        if (this.prioridad != otra.prioridad) {
            return otra.prioridad.compareTo(this.prioridad); // Orden descendente
        }
        // Luego alfabético
        if (this.titulo == null && otra.titulo == null) return 0;
        if (this.titulo == null) return -1;
        if (otra.titulo == null) return 1;
        return this.titulo.compareToIgnoreCase(otra.titulo);
    }


    // Compara tareas por su ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tarea tarea = (Tarea) obj;
        return Objects.equals(id, tarea.id);
    }

    // Hash basado en el ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    // Representacion en texto de la tarea
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