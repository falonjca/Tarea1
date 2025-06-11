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
        Page<Categoria> categoriasPage = categoriaRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriasPage.getTotalPages());
        meta.setTotalElements(categoriasPage.getTotalElements());
        meta.setPageNumber(categoriasPage.getNumber() + 1);
        meta.setPageSize(categoriasPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categorias devueltas exitosamente",
                categoriasPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoriaById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(id);
        if (foundCategoria.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Categoria devuelta exitosamente",
                    foundCategoria.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria con id " + id + " no encontrada",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> updateCategoriaById(@PathVariable Long id, @RequestBody Categoria categoria, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(id);
        if(foundCategoria.isPresent()) {
            Categoria existingCategoria = foundCategoria.get();

            existingCategoria.setNombre(categoria.getNombre());
            existingCategoria.setDescripcion(categoria.getDescripcion());

            categoriaRepository.save(existingCategoria);
            return new GlobalResponseHandler().handleResponse("Categoria actualizada correctamente",
                    existingCategoria, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria id " + id + " no encontrada",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Categoria addCategoria(@RequestBody Categoria categoria) {
        return  categoriaRepository.save(categoria);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN_ROLE')")
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
