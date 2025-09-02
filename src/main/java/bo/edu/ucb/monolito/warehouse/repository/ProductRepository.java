package bo.edu.ucb.monolito.warehouse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.monolito.warehouse.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Find product by name
    Optional<Product> findByName(String name);
    
    // Find product by sku
    Optional<Product> findBySku(String sku);
    
    
}
