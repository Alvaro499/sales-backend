package ucr.ac.cr.BackendVentas.handlers;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ucr.ac.cr.BackendVentas.DataInitializer;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.Impl.OrderLineHandlerImpl;
import ucr.ac.cr.BackendVentas.handlers.queries.OrderLineQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.models.BaseException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(DataInitializer.class)
public class OrderLineTest {

    @Autowired
    private ProductRepository productRepo;

    @MockitoBean
    private OrderLineQuery orderLineQuery;

    @Autowired
    private OrderLineHandler orderLineHandler;

    @Test
    void saveOrderLine_WithSufficientStock_ShouldReduceStock() {
        ProductEntity product = productRepo.findByName("Café Orgánico").orElseThrow();
        int initialStock = product.getStock();

        OrderLineEntity line = new OrderLineEntity();
        line.setProduct(product);
        line.setQuantity(5);
        line.setSubtotal(BigDecimal.valueOf(1));
        line.setUnitPrice(product.getPrice());

        when(orderLineQuery.save(any())).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        OrderLineEntity saved = invokeSave(line);

        assertEquals(initialStock - 5, saved.getProduct().getStock(), "El stock no fue reducido correctamente");
        verify(orderLineQuery, times(1)).save(any());
    }

    @Test
    void saveOrderLine_WithInsufficientStock_ShouldThrowOutOfStock() {
        ProductEntity product = productRepo.findByName("Café Orgánico").orElseThrow();

        OrderLineEntity line = new OrderLineEntity();
        line.setProduct(product);
        line.setQuantity(product.getStock() + 1);

        BaseException ex = assertThrows(BaseException.class, () -> invokeSave(line));
        assertEquals("OUT_OF_STOCK", ex.getCode());
        verify(orderLineQuery, never()).save(any());
    }

    @Test
    void saveOrderLine_SaveFails_ShouldThrowUnexpectedError() {
        ProductEntity product = productRepo.findByName("Café Orgánico").orElseThrow();

        OrderLineEntity line = new OrderLineEntity();
        line.setProduct(product);
        line.setQuantity(1);
        line.setSubtotal(BigDecimal.ONE);
        line.setUnitPrice(product.getPrice());

        when(orderLineQuery.save(any())).thenReturn(Optional.empty());

        BaseException ex = assertThrows(BaseException.class, () -> invokeSave(line));
        assertEquals("UNEXPECTED_ERROR", ex.getCode());
    }

    private OrderLineEntity invokeSave(OrderLineEntity line) {
        try {
            var impl = (OrderLineHandlerImpl) orderLineHandler;
            Method method = OrderLineHandlerImpl.class.getDeclaredMethod("saveOrderLine", OrderLineEntity.class);
            method.setAccessible(true);
            return (OrderLineEntity) method.invoke(impl, line);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof BaseException be) {
                throw be;
            }
            throw new RuntimeException("Excepción inesperada al invocar saveOrderLine", e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Error al preparar llamada a saveOrderLine", e);
        }
    }
}
