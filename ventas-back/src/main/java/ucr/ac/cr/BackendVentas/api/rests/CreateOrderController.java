package ucr.ac.cr.BackendVentas.api.rests;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.BackendVentas.api.types.CreateOrderRequest;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;

@RestController
@RequestMapping("/api/orders")
public class CreateOrderController {

    //Inyección por medio de constructor para facilitar
    // pruebas unitarias y seguir buenas prácticas de diseño
    private final CreateOrderHandler createOrderHandler;

    public CreateOrderController(CreateOrderHandler createOrderHandler) {
        this.createOrderHandler = createOrderHandler;
    }

    // Endpoint temporal sin autenticación
    @PostMapping("")
    public ResponseEntity<?> createOrderTemp(@RequestBody CreateOrderRequest request) {
        UUID tempUserId = request.guestUserId() != null ? request.guestUserId() : UUID.randomUUID();

        var command = new CreateOrderHandler.Command(
                request.guestUserId(),
                request.buyerType(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.shippingAddress(),
                request.paymentMethod(),
                request.shippingMethod(),
                request.products()
        );
        // Solo devuelve Success, los errores se manejan por @ControllerAdvice
        var success = createOrderHandler.handle(command);
        return ResponseEntity.ok(success);
    }

}
