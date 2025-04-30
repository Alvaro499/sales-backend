package ucr.ac.cr.BackendVentas.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.BackendVentas.handlers.commands.ProductHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.models.BaseException;
import ucr.ac.cr.BackendVentas.models.ErrorCode;
import ucr.ac.cr.BackendVentas.api.types.Response;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductHandler productHandler;

    @Autowired
    public ProductController(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    @PostMapping
    public Response createProduct(@RequestBody ProductRequest request) {
        ProductHandler.Command command = new ProductHandler.Command(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory(),
                request.getImages(),
                request.getAvailable(),
                request.getPromotion(),
                request.getStock(),
                request.getPymeId()
        );

        var result = productHandler.handle(command);

        return switch (result) {
            case ProductHandler.Result.Success success ->
                    new Response("Product created successfully", success.product());  // Returning the entity directly

            case ProductHandler.Result.InvalidFields invalidFields ->
                    throw BaseException.exceptionBuilder()
                            .code(ErrorCode.REQUIRED_FIELDS)
                            .message("Invalid Fields")
                            .params(List.of(invalidFields.fields()))
                            .build();

            case ProductHandler.Result.PymeNotFound pymeNotFound ->
                    throw BaseException.exceptionBuilder()
                            .code(ErrorCode.PYME_NOT_FOUND)
                            .message("Pyme not found")
                            .build();

            default -> throw new IllegalStateException("Unexpected result: " + result);
        };
    }

    // DTO class for the incoming product request
    public static class ProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
        private List<String> images;
        private Boolean available;
        private String promotion;
        private Integer stock;
        private Long pymeId;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }

        public Boolean getAvailable() { return available; }
        public void setAvailable(Boolean available) { this.available = available; }

        public String getPromotion() { return promotion; }
        public void setPromotion(String promotion) { this.promotion = promotion; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }

        public Long getPymeId() { return pymeId; }
        public void setPymeId(Long pymeId) { this.pymeId = pymeId; }
    }
}
