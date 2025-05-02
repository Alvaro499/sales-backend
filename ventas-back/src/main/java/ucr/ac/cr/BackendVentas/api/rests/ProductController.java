package ucr.ac.cr.BackendVentas.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.BackendVentas.handlers.commands.ProductHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.models.BaseException;
import ucr.ac.cr.BackendVentas.models.ErrorCode;
import ucr.ac.cr.BackendVentas.api.types.ProductRequest;
import ucr.ac.cr.BackendVentas.api.types.Response;

import java.util.List;
import java.util.UUID;

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
}
