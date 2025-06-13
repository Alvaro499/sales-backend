package ucr.ac.cr.BackendVentas;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ucr.ac.cr.BackendVentas.jpa.entities.*;
import ucr.ac.cr.BackendVentas.jpa.repositories.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer {

    @Autowired private PymeRepository pymeRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private PaymentMethodRepository paymentRepo;
    @Autowired private ShippingMethodRepository shippingRepo;

    @PostConstruct
    public void init() {

        if (categoryRepo.count() > 0 && pymeRepo.count() > 0 && productRepo.count() > 0 && paymentRepo.count() > 0 && shippingRepo.count() > 0) {
            return;
        }

        //Se crean categorias
        CategoryEntity tech = new CategoryEntity();
        tech.setName("Tecnología");
        tech.setDescription("Productos electrónicos y gadgets");

        CategoryEntity food = new CategoryEntity();
        food.setName("Alimentos");
        food.setDescription("Comida y snacks artesanales");

        CategoryEntity decor = new CategoryEntity();
        decor.setName("Decoración");
        decor.setDescription("Artículos para el hogar");

        categoryRepo.saveAll(List.of(tech, food, decor));

        // --- Crear Métodos de Pago ---
        paymentRepo.saveAll(List.of(
                createPayment("EFECTIVO", "Pago en el punto de entrega", true),
                createPayment("SINPE", "Pago por Sinpe Móvil", true),
                createPayment("DEBITO", "Transferencia a cuenta IBAN", true)
        ));

        // --- Crear Métodos de Envío ---
        shippingRepo.saveAll(List.of(
                createShipping("ENTREGA_LOCAL", "Retiro en el local de la pyme", BigDecimal.ZERO, true),
                createShipping("CORREOS_CR", "Entrega en 3 a 5 días hábiles", new BigDecimal("1500"), true),
                createShipping("ENVIOS_EXPRESS", "Entrega rápida en 1 día", new BigDecimal("3000"), true)
        ));

        // --- Crear Pymes ---
        PymeEntity pyme1 = createPyme("Tech CR", "tech@example.com", "1234", "San José", "8888-1111", "Tienda de electrónicos", null);
        PymeEntity pyme2 = createPyme("Delicias Ticas", "food@example.com", "1234", "Cartago", "8888-2222", "Snacks artesanales", null);
        PymeEntity pyme3 = createPyme("Casa Bonita", "deco@example.com", "1234", "Alajuela", "8888-3333", "Decoración para el hogar", null);

        pymeRepo.saveAll(List.of(pyme1, pyme2, pyme3));

        // --- Crear Productos ---
        ProductEntity prod1 = createProduct("Audífonos Bluetooth", "Inalámbricos con cancelación de ruido", new BigDecimal("25000"), 10, tech, pyme1);
        ProductEntity prod2 = createProduct("Galletas Artesanales", "Hechas con ingredientes naturales", new BigDecimal("3500"), 50, food, pyme2);
        ProductEntity prod3 = createProduct("Lámpara de Sal", "Decorativa y relajante", new BigDecimal("12000"), 20, decor, pyme3);
        ProductEntity prod4 = createProduct("Power Bank 10000mAh", "Batería externa compacta", new BigDecimal("18000"), 30, tech, pyme1);
        ProductEntity prod5 = createProduct("Café Orgánico", "Café costarricense 100% puro", new BigDecimal("5500"), 40, food, pyme2);

        productRepo.saveAll(List.of(prod1, prod2, prod3, prod4, prod5));
    }

    // Métodos auxiliares

    private PymeEntity createPyme(String name, String email, String password, String address, String phone, String desc, String logoUrl) {
        PymeEntity p = new PymeEntity();
        p.setName(name);
        p.setEmail(email);
        p.setAddress(address);
        p.setPhone(phone);
        p.setDescription(desc);
        p.setLogoUrl(logoUrl);
        p.setActive(true);
        return p;
    }

    private ProductEntity createProduct(String name, String desc, BigDecimal price, int stock, CategoryEntity category, PymeEntity pyme) {
        ProductEntity p = new ProductEntity();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStock(stock);
        p.setAvailable(true);
        p.setActive(true);
        p.setCategories(List.of(category));
        p.setPyme(pyme);
        return p;
    }

    private PaymentMethodEntity createPayment(String name, String desc, boolean isActive) {
        PaymentMethodEntity p = new PaymentMethodEntity();
        p.setName(name);
        p.setDescription(desc);
        p.setIsActive(isActive);
        return p;
    }

    private ShippingMethodEntity createShipping(String name, String desc, BigDecimal cost, boolean isActive) {
        ShippingMethodEntity s = new ShippingMethodEntity();
        s.setName(name);
        s.setDescription(desc);
        s.setCost(cost);
        s.setActive(isActive);
        return s;
    }
}
