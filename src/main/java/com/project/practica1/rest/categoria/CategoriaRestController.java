package com.project.practica1.rest.categoria;

import com.project.practica1.logic.entity.categoria.Categoria;
import com.project.practica1.logic.entity.categoria.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias")

public class CategoriaRestController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE', 'USER')")
    public List<Categoria> getAllCategorias() {return categoriaRepository.findAll();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Categoria updateCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        return categoriaRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setNombre(categoria.getNombre());
                    existingGame.setDescripcion(categoria.getDescripcion());
                    return categoriaRepository.save(existingGame);
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

    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    @DeleteMapping("/{id}")
    public void deleteCategoria (@PathVariable Long id) {
        categoriaRepository.deleteById(id);
    }


}
