package ucr.ac.cr.BackendVentas.api.rests;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import ucr.ac.cr.BackendVentas.api.types.CreateOrderRequest;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;

@RestController
@RequestMapping("/api/orders")
public class CreateOrderController {

    @Autowired
    private CreateOrderHandler createOrderHandler;

    /*
    @PostMapping("")
    public ResponseEntity<?> createOrder(
        @RequestBody CreateOrderRequest request,
        @AuthenticationPrincipal(expression = "id") UUID userId // null si es anónimo
    ) {
        // Si es autenticado, usar userId; sino usar guestUserId
        UUID finalUserId = userId != null ? userId : request.guestUserId();

        var command = new CreateOrderHandler.Command(
            finalUserId,
            request.email(),
            request.firstName(),
            request.lastName(),
            request.phone(),
            request.shippingAddress(),
            request.paymentMethod(),
            request.shippingMethod(),
            request.products()
        );

        return handleResult(createOrderHandler.handle(command));
    }
    */

    // Endpoint temporal sin autenticación
    @PostMapping("")
    public ResponseEntity<?> createOrderTemp(@RequestBody CreateOrderRequest request) {
        UUID tempUserId = request.guestUserId() != null ? request.guestUserId() : UUID.randomUUID();

        var command = new CreateOrderHandler.Command(
                tempUserId,
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.shippingAddress(),
                request.paymentMethod(),
                request.shippingMethod(),
                request.products()
        );
        return handleResult(createOrderHandler.handle(command));
    }

    private ResponseEntity<?> handleResult(CreateOrderHandler.Result result) {
        return switch (result) {
            case CreateOrderHandler.Result.Success success -> ResponseEntity.ok(success);
            case CreateOrderHandler.Result.InvalidFields invalid -> ResponseEntity.badRequest().body(invalid);
            case CreateOrderHandler.Result.OutOfStock outOfStock -> ResponseEntity.status(409).body(outOfStock);
            case CreateOrderHandler.Result.NotFound notFound -> ResponseEntity.status(404).body(notFound);
        };
    }



}
