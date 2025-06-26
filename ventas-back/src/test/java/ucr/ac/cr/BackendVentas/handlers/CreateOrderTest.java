package ucr.ac.cr.BackendVentas.handlers;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ucr.ac.cr.BackendVentas.DataInitializer;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.OrderRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.models.BaseException;
import ucr.ac.cr.BackendVentas.models.OrderProduct;
import ucr.ac.cr.BackendVentas.producers.PurchaseSummaryProducer;
import ucr.ac.cr.BackendVentas.service.SendPurchaseService;
import ucr.ac.cr.BackendVentas.utils.MonetaryUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
@Import(DataInitializer.class)
public class CreateOrderTest {

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

    /**
     * Test Case: CP-ORDER-001
     * Description: Should create an order for an existing product and return correct total.
     * Input: Product "Café Orgánico", quantity 3, shipping: ENTREGA_LOCAL, payment: EFECTIVO
     * Expected Result: Order is created successfully with correct total amount.
     */
    @Test
    public void happyPath_createSingleOrder_shouldSucceed() {
        Optional<ProductEntity> optional = productRepo.findByName("Café Orgánico");
        // El segund parametro de assertTrue es un mensaje que se mostrará si la aserción falla
        assertTrue(optional.isPresent(), "Product 'Café Orgánico' was not inserted");

        ProductEntity product = optional.get();
        int quantity = 3;
        BigDecimal expectedTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(), // usuario anónimo
                "CLIENT",
                "alvarosiles499@gmail.com",
                "Ana",
                "Pérez",
                "1234-0000",
                "San José",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(new OrderProduct(product.getId(), quantity))
        );

        CreateOrderHandler.Result result = createOrderHandler.handle(command);

        assertInstanceOf(CreateOrderHandler.Result.Success.class, result);

        List<UUID> orderIds = ((CreateOrderHandler.Result.Success) result).orderIds();
        assertEquals(1, orderIds.size(), "Solo se esperaba una orden");

        var savedOrder = orderRepo.findById(orderIds.get(0));
        assertTrue(savedOrder.isPresent(), "Orden no encontrada en la base de datos");

        BigDecimal savedTotal = savedOrder.get().getTotalAmount();
        assertEquals(0, savedTotal.compareTo(expectedTotal), "Total de la orden no coincide con el esperado");
    }

    @Test
    void createOrder_InvalidPaymentMethod_ShouldThrow() {
        Optional<ProductEntity> optional = productRepo.findByName("Café Orgánico");
        //para evitar el posible error de que el producto no exista
        assertTrue(optional.isPresent(), "Product 'Café Orgánico' was not inserted");
        ProductEntity product = optional.get();


        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(), // usuario anónimo
                "client@example.com",
                "CLIENT",
                "Ana",
                "Pérez",
                "1234-0000",
                "San José",
                "TRUEQUE", // Método de pago inválido
                "ENTREGA_LOCAL",
                List.of(new OrderProduct(product.getId(), 3))
        );

        //Capturamos la excepción de tipo BaseException lanzada por el handler desde los métodos de validación
        BaseException ex = assertThrows(
                BaseException.class,
                () -> createOrderHandler.handle(command),
                "Expected BaseException due to invalid payment method");

        assertEquals("INVALID_FORMAT", ex.getCode());
    }

    @Test
    void createOrder_InvalidShippingMethod_ShouldThrow() {
        Optional<ProductEntity> optional = productRepo.findByName("Café Orgánico");
        //para evitar el posible error de que el producto no exista
        assertTrue(optional.isPresent(), "Product 'Café Orgánico' was not inserted");
        ProductEntity product = optional.get();

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(), // usuario anónimo
                "CLIENT",
                "client@example.com",
                "Ana",
                "Pérez",
                "1234-0000",
                "San José",
                "EFECTIVO",
                "ENTREGA_DRON", // Método de envío inválido
                List.of(new OrderProduct(product.getId(), 3))
        );

        BaseException ex = assertThrows(
                BaseException.class,
                () -> createOrderHandler.handle(command),
                "Espera un BaseException de método de envío incorrecto");

        assertEquals("INVALID_FORMAT", ex.getCode());
    }

    @Test
    void createOrder_ProductDoesNotExist_ShouldThrowEntityNotFound() {

        UUID fakeProductId = UUID.randomUUID();

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(), // usuario anónimo
                "USER",
                "client@example.com",
                "Ana",
                "Pérez",
                "1234-0000",
                "San José",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(new OrderProduct(fakeProductId, 2))
        );

        BaseException ex = assertThrows(
                BaseException.class,
                () -> createOrderHandler.handle(command),
                " BaseException que espera por producto no encontrado"
        );

        assertEquals("ENTITY_NOT_FOUND", ex.getCode());
    }

    @Test
    void createOrder_MultiplePymes_ShouldCreateTwoOrders() {
        // Buscar dos productos de distintas pymes
        Optional<ProductEntity> cafe = productRepo.findByName("Café Orgánico");
        Optional<ProductEntity> audifonos = productRepo.findByName("Audífonos Bluetooth");

        assertTrue(cafe.isPresent(), "Producto 'Café Orgánico' no fue insertado");
        assertTrue(audifonos.isPresent(), "Producto 'Audífonos Bluetooth' no fue insertado");

        ProductEntity p1 = cafe.get();
        ProductEntity p2 = audifonos.get();

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(), // usuario anónimo
                "USER",
                "client@example.com",
                "Ana",
                "Pérez",
                "1234-0000",
                "San José",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(
                        new OrderProduct(p1.getId(), 1), // Producto de Delicias Ticas
                        new OrderProduct(p2.getId(), 1)  // Producto de Tech CR
                )
        );

        CreateOrderHandler.Result result = createOrderHandler.handle(command);

        assertInstanceOf(CreateOrderHandler.Result.Success.class, result);

        List<UUID> orderIds = ((CreateOrderHandler.Result.Success) result).orderIds();
        assertEquals(2, orderIds.size(), "Se esperaban 2 órdenes (una por pyme diferente)");
    }

    /**
     * Caso de Prueba: CP-ORDER-007
     * Descripción: Debe crear dos órdenes para productos de pymes diferentes y verificar los totales.
     *
     * Entrada:
     * - Producto 1: "Café Orgánico" (Pyme: Delicias Ticas), cantidad: 2 → ₡11000
     * - Producto 2: "Audífonos Bluetooth" (Pyme: Tech CR), cantidad: 2 → ₡50000
     * - Método de envío: ENTREGA_LOCAL
     * - Método de pago: EFECTIVO
     *
     * Resultado Esperado:
     * - Se crean dos órdenes separadas (una por pyme)
     * - Cada orden se almacena correctamente en base de datos
     *   - Orden 1 con total ₡11000
     *   - Orden 2 con total ₡50000
     */

    @Test
    void createOrder_MultiplePymes_ShouldCreateTwoOrdersWithCorrectTotals() {
        Optional<ProductEntity> cafe = productRepo.findByName("Café Orgánico");
        Optional<ProductEntity> audifonos = productRepo.findByName("Audífonos Bluetooth");

        assertTrue(cafe.isPresent(), "Producto 'Café Orgánico' no fue insertado");
        assertTrue(audifonos.isPresent(), "Producto 'Audífonos Bluetooth' no fue insertado");

        ProductEntity p1 = cafe.get();      // Pyme: Delicias Ticas
        ProductEntity p2 = audifonos.get(); // Pyme: Tech CR

        int cantidadCafe = 2;
        int cantidadAudifonos = 2;

        BigDecimal precioUnitarioConPromoCafe = MonetaryUtils.applyPromotion(p1.getPrice(), p1.getPromotion());
        BigDecimal precioUnitarioConPromoAudifonos = MonetaryUtils.applyPromotion(p2.getPrice(), p2.getPromotion());

        BigDecimal totalEsperadoCafe = precioUnitarioConPromoCafe.multiply(BigDecimal.valueOf(cantidadCafe));
        BigDecimal totalEsperadoAudifonos = precioUnitarioConPromoAudifonos.multiply(BigDecimal.valueOf(cantidadAudifonos));

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(),
                "USER",
                "client@example.com",
                "Ana",
                "Pérez",
                "1234-0000",
                "San José",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(
                        new OrderProduct(p1.getId(), cantidadCafe),
                        new OrderProduct(p2.getId(), cantidadAudifonos)
                )
        );

        CreateOrderHandler.Result result = createOrderHandler.handle(command);
        assertInstanceOf(CreateOrderHandler.Result.Success.class, result);

        List<UUID> orderIds = ((CreateOrderHandler.Result.Success) result).orderIds();
        assertEquals(2, orderIds.size(), "Se esperaban 2 órdenes (una por pyme diferente)");

        for (UUID orderId : orderIds) {
            Optional<OrderEntity> orderOpt = orderRepo.findById(orderId);
            assertTrue(orderOpt.isPresent(), "Orden no encontrada en la base de datos");

            OrderEntity orden = orderOpt.get();
            String nombrePyme = orden.getPyme().getName();

            if (nombrePyme.equals("Delicias Ticas")) {
                assertEquals(0, orden.getTotalAmount().compareTo(totalEsperadoCafe), "Total incorrecto para Delicias Ticas");
            } else if (nombrePyme.equals("Tech CR")) {
                assertEquals(0, orden.getTotalAmount().compareTo(totalEsperadoAudifonos), "Total incorrecto para Tech CR");
            } else {
                fail("Orden creada para una pyme inesperada: " + nombrePyme);
            }
        }
    }

    /**
     * Caso de prueba: createOrder_MultiplePymes_ShouldCalculateCorrectPromotions
     *
     * Se evalúan 3 productos con diferentes configuraciones de promoción:
     *
     * | Producto                      | Precio Original | Promoción | Cantidad | Precio Final Unitario | Total por Producto |
     * |------------------------------|------------------|-----------|----------|------------------------|---------------------|
     * | Promo90 - Smartwatch         | ₡50,000.00       | 90%       | 2        | ₡5,000.00              | ₡10,000.00          |
     * | Promo0 - Barritas Naturales  | ₡1,500.00        | 0%        | 3        | ₡1,500.00              | ₡4,500.00           |
     * | PromoNull - Cuadro Decorativo| ₡25,000.00       | null (0%) | 1        | ₡25,000.00             | ₡25,000.00          |
     *
     * Total general esperado: ₡10,000 + ₡4,500 + ₡25,000 = ₡39,500.00
     *
     * Validaciones:
     * - Se crean 3 órdenes (una por pyme distinta).
     * - El total acumulado coincide con el total esperado.
     * - Se aplican correctamente los descuentos según la lógica de negocio.
     */
    @Test
    void createOrder_MultiplePymes_ShouldCalculateCorrectPromotions() {
        Optional<ProductEntity> smartwatchOpt = productRepo.findByName("Promo90 - Smartwatch");
        Optional<ProductEntity> barritasOpt = productRepo.findByName("Promo0 - Barritas Naturales");
        Optional<ProductEntity> cuadroOpt = productRepo.findByName("PromoNull - Cuadro Decorativo");

        assertTrue(smartwatchOpt.isPresent(), "Producto 'Promo90 - Smartwatch' no encontrado");
        assertTrue(barritasOpt.isPresent(), "Producto 'Promo0 - Barritas Naturales' no encontrado");
        assertTrue(cuadroOpt.isPresent(), "Producto 'PromoNull - Cuadro Decorativo' no encontrado");

        ProductEntity smartwatch = smartwatchOpt.get(); // Pyme: PromoTech
        ProductEntity barritas = barritasOpt.get();     // Pyme: PromoFood
        ProductEntity cuadro = cuadroOpt.get();         // Pyme: PromoDeco

        int qtySmartwatch = 2;
        int qtyBarritas = 3;
        int qtyCuadro = 1;

        BigDecimal totalSmartwatch = MonetaryUtils.applyPromotion(smartwatch.getPrice(), smartwatch.getPromotion())
                .multiply(BigDecimal.valueOf(qtySmartwatch));
        BigDecimal totalBarritas = MonetaryUtils.applyPromotion(barritas.getPrice(), barritas.getPromotion())
                .multiply(BigDecimal.valueOf(qtyBarritas));
        BigDecimal totalCuadro = MonetaryUtils.applyPromotion(cuadro.getPrice(), cuadro.getPromotion())
                .multiply(BigDecimal.valueOf(qtyCuadro));

        BigDecimal totalEsperado = totalSmartwatch.add(totalBarritas).add(totalCuadro);

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(),
                "USER",
                "promo@test.com",
                "Laura", "Promo",
                "1111-2222",
                "Monteverde",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(
                        new OrderProduct(smartwatch.getId(), qtySmartwatch),
                        new OrderProduct(barritas.getId(), qtyBarritas),
                        new OrderProduct(cuadro.getId(), qtyCuadro)
                )
        );

        CreateOrderHandler.Result result = createOrderHandler.handle(command);
        assertInstanceOf(CreateOrderHandler.Result.Success.class, result);

        List<UUID> orderIds = ((CreateOrderHandler.Result.Success) result).orderIds();
        assertEquals(3, orderIds.size(), "Se esperaban 3 órdenes (una por pyme diferente)");

        BigDecimal totalAcumulado = BigDecimal.ZERO;

        for (UUID orderId : orderIds) {
            Optional<OrderEntity> orderOpt = orderRepo.findById(orderId);
            assertTrue(orderOpt.isPresent(), "Orden no encontrada");
            totalAcumulado = totalAcumulado.add(orderOpt.get().getTotalAmount());
        }

        assertEquals(0, totalAcumulado.compareTo(totalEsperado), "Total acumulado no coincide con la suma esperada");
    }


    /**
     * ??-ORDER-ROLLBACK-001
     * Descripción:
     * Verifica que si ocurre una excepción después de ejecutar el handler,
     * la transacción completa (incluyendo órdenes creadas) se revierte automáticamente.
     *
     * Justificación técnica:
     * Aunque el método `handle(...)` tiene @Transactional, Spring detecta que el test
     * ya está dentro de una transacción (por @Test + @Transactional), y lo ejecuta en la misma.
     * Por lo tanto, si lanzamos una excepción dentro del test, TODO lo que hizo el handler
     * también se revierte (gracias al @Rollback).
     */

    @Test
    void createOrder_thenFailInsideHandler_shouldRollback() {
        Optional<ProductEntity> p1 = productRepo.findByName("Teclado Mecánico");
        Optional<ProductEntity> p2 = productRepo.findByName("Caja de Chocolates");

        assertTrue(p1.isPresent());
        assertTrue(p2.isPresent());

        long countBefore = orderRepo.count();

        // Simulamos fallo al intentar crear líneas de orden
        doThrow(new RuntimeException("Fallo simulado en orderLineHandler"))
                .when(orderLineHandler)
                .createOrderLines(any(), any());

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(),
                "USER",
                "client@example.com",
                "Ana",
                "Pérez",
                "1234-0000",
                "San José",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(new OrderProduct(p1.get().getId(), 1),
                        new OrderProduct(p2.get().getId(), 1))
        );

        assertThrows(RuntimeException.class, () -> createOrderHandler.handle(command));

        long countAfter = orderRepo.count();
        assertEquals(countBefore, countAfter, "Rollback no funcionó: las órdenes no debieron guardarse");
    }

    @Test
    void createThreeOrdersWithMultipleProducts_thirdFails_shouldRollbackAll() {
        Optional<ProductEntity> p1 = productRepo.findByName("Teclado Mecánico");
        Optional<ProductEntity> p2 = productRepo.findByName("Mouse Inalámbrico");

        Optional<ProductEntity> p3 = productRepo.findByName("Caja de Chocolates");
        Optional<ProductEntity> p4 = productRepo.findByName("Mermelada de Mango");

        Optional<ProductEntity> p5 = productRepo.findByName("Florero de Cerámica");
        Optional<ProductEntity> p6 = productRepo.findByName("Velas Aromáticas");

        assertTrue(p1.isPresent());
        assertTrue(p2.isPresent());
        assertTrue(p3.isPresent());
        assertTrue(p4.isPresent());
        assertTrue(p5.isPresent());
        assertTrue(p6.isPresent());

        long countBefore = orderRepo.count();

        // Simula que en la tercera orden falla createOrderLines
        AtomicInteger counter = new AtomicInteger(0);

        doAnswer(invocation -> {
            if (counter.incrementAndGet() == 3) {
                throw new RuntimeException("Fallo simulado en la tercera orden");
            }
            return invocation.callRealMethod(); // las dos primeras órdenes sí se crean
        }).when(orderLineHandler).createOrderLines(any(), any());

        CreateOrderHandler.Command command = new CreateOrderHandler.Command(
                UUID.randomUUID(),
                "USER",
                "cliente@rollback.com",
                "Juan", "Tester",
                "9999-0000",
                "Tamarindo",
                "EFECTIVO",
                "ENTREGA_LOCAL",
                List.of(
                        // RollbackTech
                        new OrderProduct(p1.get().getId(), 1),
                        new OrderProduct(p2.get().getId(), 1),
                        // RollbackFoods
                        new OrderProduct(p3.get().getId(), 1),
                        new OrderProduct(p4.get().getId(), 1),
                        // RollbackDeco (esta debe fallar)
                        new OrderProduct(p5.get().getId(), 1),
                        new OrderProduct(p6.get().getId(), 1)
                )
        );

        assertThrows(RuntimeException.class, () -> createOrderHandler.handle(command));

        long countAfter = orderRepo.count();
        assertEquals(countBefore, countAfter, "Rollback no funcionó: alguna orden quedó guardada");
    }











    @Test
    void createOrder_InsufficientStock_ShouldThrowOutOfStock() {  }
}