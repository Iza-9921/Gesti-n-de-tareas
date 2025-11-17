package com.example.integradoravirna.modelo;

import java.time.LocalDateTime;

public class Historial {
    private String titulo;
    private String descripcion;
    private String accion;
    private LocalDateTime fechaCreacion;

    public Historial(String titulo, String descripcion, String accion, LocalDateTime fechaCreacion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.accion = accion;
        this.fechaCreacion = fechaCreacion;
    }

    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getAccion() { return accion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
