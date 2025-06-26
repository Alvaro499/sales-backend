package ucr.ac.cr.BackendVentas;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ucr.ac.cr.BackendVentas.jpa.entities.*;
import ucr.ac.cr.BackendVentas.jpa.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Profile("!test")
@Component
public class DataInitializer {

    @Autowired private PymeRepository pymeRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private PaymentMethodRepository paymentRepo;
    @Autowired private ShippingMethodRepository shippingRepo;
    @Autowired private ClientRepository clientRepo;


    @PostConstruct
    public void testInit() {
        System.out.println("DataInitializer de PRODUCCIÓN ejecutado (esto NO debería verse en tests)");
    }

    @PostConstruct
    public void init() {
        System.out.println("[PRODUCCIÓN] DataInitializer ejecutado.");
        /*
        if (categoryRepo.count() > 0 && pymeRepo.count() > 0 && productRepo.count() > 0 && paymentRepo.count() > 0 && shippingRepo.count() > 0 && clientRepo.count() > 0) {
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
        PymeEntity pyme1 = createPyme("Tech CR", "pymetesting25@yopmail.com", "San José", "8888-1111", "Tienda de electrónicos", null);
        PymeEntity pyme2 = createPyme("Delicias Ticas", "pymetesting225@yopmail.com", "Cartago", "8888-2222", "Snacks artesanales", null);
        PymeEntity pyme3 = createPyme("Casa Bonita", "pymetesting325@yopmail.com", "Alajuela", "8888-3333", "Decoración para el hogar", null);

        pymeRepo.saveAll(List.of(pyme1, pyme2, pyme3));

        // --- Crear Clientes Anónimos ---
        ClientEntity anonClient1 = new ClientEntity();
        anonClient1.setExpiresAt(LocalDateTime.now().plusDays(30));

        ClientEntity anonClient2 = new ClientEntity();
        anonClient2.setExpiresAt(LocalDateTime.now().plusDays(30));

        clientRepo.saveAll(List.of(anonClient1, anonClient2));


        // --- Crear Productos ---
        ProductEntity prod1 = createProduct("Audífonos Bluetooth", "Inalámbricos con cancelación de ruido", new BigDecimal("25000"), 10, tech, pyme1, new BigDecimal("0.90"));
        ProductEntity prod2 = createProduct("Power Bank 10000mAh", "Batería externa compacta", new BigDecimal("18000"), 30, tech, pyme1, BigDecimal.ZERO);
        ProductEntity prod3 = createProduct("Mouse Inalámbrico", "Ergonómico y silencioso", new BigDecimal("10000"), 15, tech, pyme1, BigDecimal.ZERO);

        ProductEntity prod4 = createProduct("Galletas Artesanales", "Hechas con ingredientes naturales", new BigDecimal("3500"), 50, food, pyme2, new BigDecimal("0.50"));
        ProductEntity prod5 = createProduct("Café Orgánico", "Café costarricense 100% puro", new BigDecimal("5500"), 40, food, pyme2, BigDecimal.ZERO);
        ProductEntity prod6 = createProduct("Mermelada de Guayaba", "Hecha en casa", new BigDecimal("4000"), 25, food, pyme2, BigDecimal.ZERO);

        ProductEntity prod7 = createProduct("Lámpara de Sal", "Decorativa y relajante", new BigDecimal("12000"), 20, decor, pyme3, new BigDecimal("0.20"));
        ProductEntity prod8 = createProduct("Cojín Bohemio", "Con relleno hipoalergénico", new BigDecimal("8000"), 30, decor, pyme3, BigDecimal.ZERO);
        ProductEntity prod9 = createProduct("Portarretrato de Madera", "Hecho a mano", new BigDecimal("6000"), 10, decor, pyme3, BigDecimal.ZERO);

        productRepo.saveAll(List.of(prod1, prod2, prod3, prod4, prod5, prod6, prod7, prod8, prod9));

         */
    }

    // Métodos auxiliares

    private PymeEntity createPyme(String name, String email, String address, String phone, String desc, String logoUrl) {
        PymeEntity p = new PymeEntity();
        p.setName(name);
        p.setEmail(email);
        p.setAddress(address);
        p.setPhone(phone);
        p.setDescription(desc);
        p.setLogoUrl(logoUrl);
        p.setActive(true);
        p.setUserId(UUID.randomUUID());
        return p;
    }

    private ProductEntity createProduct(String name, String desc, BigDecimal price, int stock, CategoryEntity category, PymeEntity pyme, BigDecimal promotion) {
        ProductEntity p = new ProductEntity();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStock(stock);
        p.setAvailable(true);
        p.setActive(true);
        p.setCategories(List.of(category));
        p.setPyme(pyme);
        p.setPromotion(promotion);
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
