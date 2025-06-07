package com.project.practica1.rest.producto;



import com.project.practica1.logic.entity.producto.Producto;
import com.project.practica1.logic.entity.producto.ProductoRepository;
import com.project.practica1.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/productos")


public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN_ROLE', 'USER')")
    public List<Producto> getAllProductos() {return productoRepository.findAll();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Producto updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return productoRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setNombre(producto.getNombre());
                    existingGame.setDescripcion(producto.getDescripcion());
                    existingGame.setPrecio(producto.getPrecio());
                    existingGame.setCantidad(producto.getCantidad());
                    return productoRepository.save(existingGame);
                })
                .orElseGet(() -> {
                    producto.setId(id);
                    return productoRepository.save(producto);
                });
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Producto addGame(@RequestBody Producto producto) {
        return  productoRepository.save(producto);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    @DeleteMapping("/{id}")
    public void deleteGame (@PathVariable Long id) {
        productoRepository.deleteById(id);
    }


}
