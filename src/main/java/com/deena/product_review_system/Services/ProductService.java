package com.deena.product_review_system.Services;

import com.deena.product_review_system.DTO.ProductRequest;
import com.deena.product_review_system.Entity.Product;
import com.deena.product_review_system.Repository.ProductRepository;
import com.deena.product_review_system.auth.Repository.UserRepository;
import com.deena.product_review_system.auth.model.User;
import com.deena.product_review_system.auth.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // CREATE PRODUCT
    public Product createProduct(ProductRequest request, String jwtToken) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name cannot be empty");
        }

        String email = jwtService.extractUsername(jwtToken);

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCreatedAt(LocalDate.now());
        product.setOwner(owner); // important

        return productRepository.save(product);
    }

    // GET ONE PRODUCT
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id)
                );
    }

    // GET ALL PRODUCTS
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // UPDATE PRODUCT
    public Product updateProduct(Long id, ProductRequest request, String jwtToken) {

        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id)
                );

        String email = jwtService.extractUsername(jwtToken);

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // update fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOwner(owner);

        return productRepository.save(product);
    }

    // DELETE PRODUCT
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
