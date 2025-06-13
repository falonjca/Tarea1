package com.project.practica1.rest.producto;



import com.project.practica1.logic.entity.categoria.Categoria;
import com.project.practica1.logic.entity.categoria.CategoriaRepository;
import com.project.practica1.logic.entity.http.GlobalResponseHandler;
import com.project.practica1.logic.entity.http.Meta;
import com.project.practica1.logic.entity.producto.Producto;
import com.project.practica1.logic.entity.producto.ProductoRepository;
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
        Page<Producto> productosPage = productoRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productosPage.getTotalPages());
        meta.setTotalElements(productosPage.getTotalElements());
        meta.setPageNumber(productosPage.getNumber() + 1);
        meta.setPageSize(productosPage.getSize());

        return new GlobalResponseHandler().handleResponse("Producto devuelto exitosamente",
                productosPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductoById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(id);
        if (foundProducto.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Producto devuelto exitosamente",
                    foundProducto.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Producto con id " + id + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> updateProductoById(@PathVariable Long id, @RequestBody Producto producto, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(id);
        if(foundProducto.isPresent()) {
            Producto existingProducto = foundProducto.get();

            existingProducto.setNombre(producto.getNombre());
            existingProducto.setDescripcion(producto.getDescripcion());
            existingProducto.setPrecio(producto.getPrecio());
            existingProducto.setCantidad(producto.getCantidad());
            existingProducto.setCategoria(producto.getCategoria());

            productoRepository.save(existingProducto);
            return new GlobalResponseHandler().handleResponse("Producto actualizado correctamente",
                    existingProducto, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Producto id " + id + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> createProducto(@RequestBody Producto producto, HttpServletRequest request) {
        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            Optional<Categoria> foundCategoria = categoriaRepository.findById(producto.getCategoria().getId());
            if (foundCategoria.isPresent()) {
                producto.setCategoria(foundCategoria.get());
            } else {
                return new GlobalResponseHandler().handleResponse(
                        "La categoría con id " + producto.getCategoria().getId() + " no existe.",
                        HttpStatus.BAD_REQUEST, request);
            }
        } else {
            producto.setCategoria(null);
        }

        Producto savedProducto = productoRepository.save(producto);
        return new GlobalResponseHandler().handleResponse(
                "Producto registrado correctamente.",
                savedProducto, HttpStatus.CREATED, request);
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
            return new GlobalResponseHandler().handleResponse("Producto id " + id + " no encontrado"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }


}
