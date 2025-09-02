package bo.edu.ucb.monolito.sales.bl;

import bo.edu.ucb.monolito.sales.entity.Sale;
import bo.edu.ucb.monolito.sales.repository.SaleRepository;
import bo.edu.ucb.monolito.warehouse.dto.ProductDto;
import bo.edu.ucb.monolito.warehouse.bl.ProductStockBl;
import bo.edu.ucb.monolito.warehouse.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CompleSaleBl {

    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private ProductStockBl productStockBl;

    /**
     * Creates a new Sale based on ProductDto information
     * @param productDto The product information to create a sale for
     * @param productId The ID of the product being sold
     * @param quantity The quantity to sell
     * @return The created Sale entity
     */
    private Sale createSale(ProductDto productDto, Integer productId, Integer quantity) {
        if (productDto == null) {
            throw new IllegalArgumentException("ProductDto cannot be null");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        
        // Generate a unique sale number
        String saleNumber = "SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create the sale - assuming productId will be set separately or retrieved from a product service
        Sale sale = new Sale();
        sale.setSaleNumber(saleNumber);
        sale.setProductId(productId);
        sale.setQuantity(quantity);
        sale.setUnitPrice(productDto.getPrice());
        
        // Calculate total amount
        BigDecimal totalAmount = productDto.getPrice().multiply(BigDecimal.valueOf(quantity));
        sale.setTotalAmount(totalAmount);
        
        return sale;
    }
    
    /**
     * Creates and persists a new Sale based on ProductDto information
     * @param productDto The product information to create a sale for
     * @param productId The ID of the product being sold
     * @param quantity The quantity to sell
     * @return The persisted Sale entity with generated ID
     */
    public Sale createAndSaveSale(ProductDto productDto, Integer quantity) {
        // Validate product exists and has sufficient stock using ProductStockBl
        Product product = productStockBl.getProductById(productDto.getId());
        
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productDto.getId() + " not found");
        }
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
        }
        
        // Create the sale
        Sale sale = createSale(productDto, productDto.getId(), quantity);

        // Disminuye el stock del producto
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productStockBl.updateProductStock(product);
        
        // Persist the sale
        return saleRepository.save(sale);
    }
    

    /**
     * Finds a sale by its sale number
     * @param saleNumber The unique sale number
     * @return The Sale entity if found, null otherwise
     */
    public Sale findBySaleNumber(String saleNumber) {
        return saleRepository.findBySaleNumber(saleNumber).orElse(null);
    }
}
