package com.deena.product_review_system.Controller;

import com.deena.product_review_system.DTO.ProductRequest;
import com.deena.product_review_system.Entity.Product;
import com.deena.product_review_system.Services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // EXTRACT JWT FROM AUTH HEADER
    private String extractJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            return null;

        return header.substring(7); // remove "Bearer "
    }

    // CREATE PRODUCT
    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT:CREATE') or hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request,
                                           HttpServletRequest httpRequest) {
        try {
            String jwt = extractJwt(httpRequest);
            Product product = productService.createProduct(request, jwt);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // GET SINGLE PRODUCT
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProduct(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // GET ALL PRODUCTS
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PRODUCT:READ_ALL')")
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestBody ProductRequest request,
                                           HttpServletRequest httpRequest) {

        try {
            String jwt = extractJwt(httpRequest);
            Product updatedProduct = productService.updateProduct(id, request, jwt);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT:DELETE_ALL')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
