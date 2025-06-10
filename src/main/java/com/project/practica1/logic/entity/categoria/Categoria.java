package com.project.practica1.logic.entity.categoria;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.practica1.logic.entity.producto.Producto;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name= "categorias")

public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Producto> productos;



    public Categoria() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public Categoria(Long id, String nombre, String descripcion, List<Producto> productos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", productos=" + productos +
                '}';
    }
}
