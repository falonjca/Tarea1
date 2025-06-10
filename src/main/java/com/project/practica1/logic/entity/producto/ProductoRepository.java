package com.project.practica1.logic.entity.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
