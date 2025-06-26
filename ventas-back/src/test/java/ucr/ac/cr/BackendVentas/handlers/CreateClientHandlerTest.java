package ucr.ac.cr.BackendVentas.handlers;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ucr.ac.cr.BackendVentas.DataInitializer;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateClientHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.ClientQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.ClientEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ClientRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.OrderRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.models.OrderProduct;
import ucr.ac.cr.BackendVentas.producers.PurchaseSummaryProducer;
import ucr.ac.cr.BackendVentas.service.SendPurchaseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(DataInitializer.class)
public class CreateClientHandlerTest {

    @Autowired
    private CreateClientHandler createClientHandler;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private  ClientQuery clientQuery;


    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CreateOrderHandler createOrderHandler;

    @MockitoBean(answers = Answers.CALLS_REAL_METHODS)
    private OrderLineHandler orderLineHandler;

    @MockitoBean
    private PurchaseSummaryProducer purchaseSummaryProducer;

    @MockitoBean
    private SendPurchaseService sendPurchaseService;

    @Test
    void createClient_withNullId_shouldGenerateNewId() {
        UUID id = createClientHandler.handle(new CreateClientHandler.Command(null));

        assertNotNull(id, "El ID generado no debe ser null");

        assertTrue(clientRepo.findById(id).isPresent(), "El cliente no fue guardado correctamente");
    }

    @Test
    void createClient_withExistingId_shouldReuseThatClient() {
        // Creamos un cliente manualmente
        ClientEntity existing = new ClientEntity();
        existing.setExpiresAt(LocalDateTime.now().plusDays(15));
        UUID existingId = clientRepo.save(existing).getId();

        UUID reused = createClientHandler.handle(new CreateClientHandler.Command(existingId));

        assertEquals(existingId, reused, "Se esperaba que el ID existente fuera reutilizado");
    }

    @Test
    void createClient_withNonExistentId_shouldIgnoreAndGenerateNewId() {
        UUID nonexistent = UUID.randomUUID();

        UUID result = createClientHandler.handle(new CreateClientHandler.Command(nonexistent));

        assertNotNull(result, "El ID no debe ser null");
        assertNotEquals(nonexistent, result, "Se esperaba que ignorara el ID inexistente");
        assertTrue(clientRepo.findById(result).isPresent(), "El cliente nuevo no fue creado correctamente");
    }


    @Test
    void createOrder_buyerTypeUser_shouldAssignUserId() {
        Optional<ProductEntity> optional = productRepo.findByName("Café Orgánico");
        assertTrue(optional.isPresent(), "Producto requerido no insertado");

        ProductEntity product = optional.get();

        UUID userId = UUID.randomUUID(); // Simula un usuario registrado

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                userId,
                "USER", // Tipo USER → se usa el ID directo
                "usuario@registrado.com",
                "María",
                "Registrada",
                "7777-0000",
                "Cartago",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(new OrderProduct(product.getId(), 1))
        );

        CreateOrderHandler.Result result = createOrderHandler.handle(command);

        assertInstanceOf(CreateOrderHandler.Result.Success.class, result);

        List<UUID> orderIds = ((CreateOrderHandler.Result.Success) result).orderIds();
        assertEquals(1, orderIds.size(), "Se esperaba una orden");

        OrderEntity orden = orderRepo.findById(orderIds.get(0)).orElseThrow();
        assertEquals(userId, orden.getUser(), "Se esperaba que el ID del usuario registrado se asignara");
        assertNull(orden.getClient(), "No se debe asociar cliente anónimo en este caso");
    }


    @Test
    void createOrder_buyerTypeClient_shouldCreateClientEntity() {
        Optional<ProductEntity> optional = productRepo.findByName("Café Orgánico");
        assertTrue(optional.isPresent(), "Producto requerido no insertado");

        ProductEntity product = optional.get();

        // No enviamos ID para que se genere uno automáticamente
        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                null, // ID nulo
                "CLIENT", // Tipo CLIENT → se crea cliente anónimo
                "anon@example.com",
                "Cliente",
                "Anónimo",
                "8888-0000",
                "Alajuela",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(new OrderProduct(product.getId(), 1))
        );

        CreateOrderHandler.Result result = createOrderHandler.handle(command);

        assertInstanceOf(CreateOrderHandler.Result.Success.class, result);

        List<UUID> orderIds = ((CreateOrderHandler.Result.Success) result).orderIds();
        assertEquals(1, orderIds.size(), "Se esperaba una orden");

        OrderEntity orden = orderRepo.findById(orderIds.get(0)).orElseThrow();
        assertNotNull(orden.getClient(), "Se esperaba un cliente anónimo asociado");
        assertNull(orden.getUser(), "No se debe asociar un usuario registrado");
    }

    @Test
    void createOrder_buyerTypeClient_withInvalidId_shouldGenerateNewClient() {
        Optional<ProductEntity> optional = productRepo.findByName("Café Orgánico");
        assertTrue(optional.isPresent(), "Producto requerido no insertado");

        ProductEntity product = optional.get();

        UUID nonExistentId = UUID.randomUUID(); // ID inválido que no está en DB

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                nonExistentId, // ID que no existe
                "CLIENT",
                "cliente@falso.com",
                "Falso",
                "Cliente",
                "8888-1234",
                "Heredia",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(new OrderProduct(product.getId(), 1))
        );

        CreateOrderHandler.Result result = createOrderHandler.handle(command);

        assertInstanceOf(CreateOrderHandler.Result.Success.class, result);

        List<UUID> orderIds = ((CreateOrderHandler.Result.Success) result).orderIds();
        assertEquals(1, orderIds.size(), "Se esperaba una orden");

        OrderEntity orden = orderRepo.findById(orderIds.get(0)).orElseThrow();
        assertNotNull(orden.getClient(), "Se esperaba un cliente anónimo asociado");
        assertNull(orden.getUser(), "No se debe asociar un usuario registrado");
        assertNotEquals(nonExistentId, orden.getClient().getId(), "No se debe reutilizar el ID inexistente");
    }



}
