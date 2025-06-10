package com.project.practica1.rest.categoria;

import com.project.practica1.logic.entity.categoria.Categoria;
import com.project.practica1.logic.entity.categoria.CategoriaRepository;
import com.project.practica1.logic.entity.http.GlobalResponseHandler;
import com.project.practica1.logic.entity.http.Meta;
import com.project.practica1.logic.entity.producto.Producto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categorias")

public class CategoriaRestController {

    @Autowired
    private CategoriaRepository categoriaRepository;


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Categoria> ordersPage = categoriaRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categories retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Categoria updateCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        return categoriaRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setNombre(categoria.getNombre());
                    existingCategory.setDescripcion(categoria.getDescripcion());
                    return categoriaRepository.save(existingCategory);
                })
                .orElseGet(() -> {
                    categoria.setId(id);
                    return categoriaRepository.save(categoria);
                });
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Categoria addCategoria(@RequestBody Categoria categoria) {
        return  categoriaRepository.save(categoria);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long id, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(id);

        if (foundCategoria.isPresent()) {
            Categoria categoria = foundCategoria.get();

            if (!categoria.getProductos().isEmpty()) {
                return new GlobalResponseHandler().handleResponse(
                        "No se puede eliminar la categoría porque tiene productos asociados.",
                        HttpStatus.BAD_REQUEST, request);
            }

            categoriaRepository.deleteById(id);

            return new GlobalResponseHandler().handleResponse(
                    "Categoría eliminada correctamente.",
                    categoria, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Categoría con id " + id + " no encontrada.",
                    HttpStatus.NOT_FOUND, request);
        }
    }



}
