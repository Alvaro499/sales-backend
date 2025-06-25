package ucr.ac.cr.BackendVentas.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.BackendVentas.api.mappers.ProductMapper;
import ucr.ac.cr.BackendVentas.handlers.commands.ProductHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.models.BaseException;
import ucr.ac.cr.BackendVentas.models.ErrorCode;
import ucr.ac.cr.BackendVentas.api.types.ProductRequest;
import ucr.ac.cr.BackendVentas.api.types.Response;
import ucr.ac.cr.BackendVentas.api.types.ProductDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductHandler productHandler;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductHandler productHandler, ProductRepository productRepository) {
        this.productHandler = productHandler;
        this.productRepository = productRepository;
    }

    @PostMapping
    public Response createProduct(@RequestBody ProductRequest request) {
        ProductHandler.Command command = new ProductHandler.Command(
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.images(),
                request.available(),
                request.promotion(),
                request.stock(),
                request.pymeId()
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

    @GetMapping("/info/{productID}")
    public Response listProductsInfo(@PathVariable UUID productID) {
        // Buscar el producto en la base de datos usando el productID
        ProductEntity product = productRepository.findById(productID)
                .orElseThrow(() -> BaseException.exceptionBuilder()
                        .code(ErrorCode.PRODUCT_NOT_FOUND)
                        .message("Product not found")
                        .build());

        // Mapear el producto a un DTO (opcional, si estás usando DTOs)
        ProductDTO productDTO = ProductMapper.toDTO(product);

        // Retornar la respuesta con el producto
        return new Response("Product retrieved", productDTO);
    }


    @GetMapping("/by-pyme/{pymeId}")
    public Response listProductsByPyme(@PathVariable UUID pymeId) {

        var result = productHandler.listProductsByPyme(pymeId);

        if (result instanceof ProductHandler.Result.SuccessList successList) {
            return new Response("Products retrieved", successList.products());
        } else if (result instanceof ProductHandler.Result.PymeNotFound) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.PYME_NOT_FOUND)
                    .message("Pyme not found")
                    .build();
        } else if (result instanceof ProductHandler.Result.NotFoundProduct) {
            return new Response("No products found", null);
        } else {
            throw new IllegalStateException("Unexpected result: " + result);
        }
    }


    @PutMapping("/unpublish/{productId}")
    public Response unpublishProduct(@PathVariable UUID productId) {
        var result = productHandler.unpublishProduct(new ProductHandler.UnpublishProductCommand(productId));

        // Directly handling result without switch statement
        if (result instanceof ProductHandler.Result.Success success) {
            return new Response("Product unpublished successfully", success.product());
        } else if (result instanceof ProductHandler.Result.NotFoundProduct) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.PRODUCT_NOT_FOUND)
                    .message("Product not found")
                    .build();
        } else {
            throw new IllegalStateException("Unexpected result: " + result);
        }
    }



    @PutMapping("/update-stock/{productId}")
    public Response updateStockAndAvailability(@PathVariable UUID productId, @RequestBody Map<String, Object> updates) {
        Boolean available = null;
        Integer stock = null;

        if (updates.containsKey("available")) {
            available = (Boolean) updates.get("available");
        }
        if (updates.containsKey("stock")) {
            Object stockObj = updates.get("stock");
            if (stockObj instanceof Number) {
                stock = ((Number) stockObj).intValue();
            }
        }

        var command = new ProductHandler.ChangeAvailabilityAndStockCommand(productId, available, stock);
        var result = productHandler.changeAvailabilityAndStock(command);

        // Directly handling result without switch statement
        if (result instanceof ProductHandler.Result.Success success) {
            return new Response("Product stock/availability updated successfully", success.product());
        } else if (result instanceof ProductHandler.Result.NotFoundProduct) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.PRODUCT_NOT_FOUND)
                    .message("Product not found")
                    .build();
        } else {
            throw new IllegalStateException("Unexpected result: " + result);
        }
    }

  
    @GetMapping("/search")
    public Response searchProducts(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax
    ) {
        ProductHandler.Result result;

        if (term == null && categoryId == null && priceMin == null && priceMax == null) {
            result = productHandler.listAllProducts();
        } else {
            result = productHandler.searchProducts(term, categoryId, priceMin, priceMax);
        }

        if (result instanceof ProductHandler.Result.SuccessList(
                List<ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity> products
        )) {
            List<ProductDTO> dtoList = ProductMapper.toDTOList(products);

            return new Response("Products retrieved", dtoList);
        } else {
            return new Response("No products found", List.of());
        }

    }


    @PutMapping("/promotion/{productId}")
    public Response applyPromotion(@PathVariable UUID productId, @RequestBody Map<String, String> request) {
        String promotion = request.get("promotion");

        var command = new ProductHandler.ApplyPromotionCommand(productId, promotion);
        var result = productHandler.applyPromotion(command);

        if (result instanceof ProductHandler.Result.Success success) {
            return new Response("Promotion applied successfully", success.product());
        } else if (result instanceof ProductHandler.Result.NotFoundProduct) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.PRODUCT_NOT_FOUND)
                    .message("Product not found")
                    .build();
        } else {
            throw new IllegalStateException("Unexpected result: " + result);
        }
    }

    @GetMapping("/count/active")
    public Response countActiveProducts() {
        long count = productRepository.countByIsActiveTrue();
        return new Response("Active products count", count);
    }

    @GetMapping("/count")
    public Response countProducts() {
        long count = productRepository.count();
        return new Response("Total products count", count);
    }



}
