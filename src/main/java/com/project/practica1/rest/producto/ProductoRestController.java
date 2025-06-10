package com.project.practica1.rest.producto;



import com.project.practica1.logic.entity.categoria.Categoria;
import com.project.practica1.logic.entity.categoria.CategoriaRepository;
import com.project.practica1.logic.entity.http.GlobalResponseHandler;
import com.project.practica1.logic.entity.http.Meta;
import com.project.practica1.logic.entity.producto.Producto;
import com.project.practica1.logic.entity.producto.ProductoRepository;
import com.project.practica1.logic.entity.user.User;
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
@RequestMapping("/productos")


public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Producto> ordersPage = productoRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Products retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductoById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(id);
        if (foundProducto.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                    foundProducto.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product with id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public Producto updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return productoRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setNombre(producto.getNombre());
                    existingProduct.setDescripcion(producto.getDescripcion());
                    existingProduct.setPrecio(producto.getPrecio());
                    existingProduct.setCantidad(producto.getCantidad());
                    return productoRepository.save(existingProduct);
                })
                .orElseGet(() -> {
                    producto.setId(id);
                    return productoRepository.save(producto);
                });
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Producto addProducto(@RequestBody Producto producto) {
        return  productoRepository.save(producto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(id);
        if(foundProducto.isPresent()) {
            Producto producto = foundProducto.get();
            Categoria categoria = producto.getCategoria();
            if (categoria != null) {
                categoria.getProductos().remove(producto);
                producto.setCategoria(null);
                categoriaRepository.save(categoria);
            }
            return new GlobalResponseHandler().handleResponse("Producto borrado de forma correcta",
                    foundProducto.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Producto id " + id + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }


}
