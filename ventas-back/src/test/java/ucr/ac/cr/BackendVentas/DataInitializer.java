package ucr.ac.cr.BackendVentas;


import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import ucr.ac.cr.BackendVentas.jpa.entities.*;
import ucr.ac.cr.BackendVentas.jpa.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@TestConfiguration
public class DataInitializer {

    @Autowired private PymeRepository pymeRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private PaymentMethodRepository paymentRepo;
    @Autowired private ShippingMethodRepository shippingRepo;
    @Autowired private ClientRepository clientRepo;


    @PostConstruct
    public void init() {

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

        // Métodos de pago y envío
        paymentRepo.saveAll(List.of(
                createPayment("EFECTIVO", "Pago en el punto de entrega", true),
                createPayment("SINPE", "Pago por Sinpe Móvil", true),
                createPayment("DEBITO", "Transferencia a cuenta IBAN", true)
        ));

        shippingRepo.saveAll(List.of(
                createShipping("ENTREGA_LOCAL", "Retiro en el local de la pyme", BigDecimal.ZERO, true),
                createShipping("CORREOS_CR", "Entrega en 3 a 5 días hábiles", new BigDecimal("1500"), true),
                createShipping("ENVIOS_EXPRESS", "Entrega rápida en 1 día", new BigDecimal("3000"), true)
        ));

        // Crear cliente de prueba
        ClientEntity testClient = new ClientEntity();
        testClient.setExpiresAt(LocalDateTime.now().plusDays(60));
        clientRepo.save(testClient);

        createDataForBasic(tech, food, decor);
        createDataForRollback(tech, food, decor);
        createDataForPromotion(tech, food, decor);
    }

    private void createDataForBasic(CategoryEntity tech, CategoryEntity food, CategoryEntity decor) {
        PymeEntity pyme1 = createPyme("Tech CR", "tech@example.com", "San José", "8888-1111", "Tienda de electrónicos", null);
        PymeEntity pyme2 = createPyme("Delicias Ticas", "food@example.com", "Cartago", "8888-2222", "Snacks artesanales", null);
        PymeEntity pyme3 = createPyme("Casa Bonita", "deco@example.com", "Alajuela", "8888-3333", "Decoración para el hogar", null);

        pymeRepo.saveAll(List.of(pyme1, pyme2, pyme3));

        productRepo.saveAll(List.of(
                createProduct("Audífonos Bluetooth", "Inalámbricos con cancelación de ruido", new BigDecimal("25000"), 10, tech, pyme1),
                createProduct("Galletas Artesanales", "Hechas con ingredientes naturales", new BigDecimal("3500"), 50, food, pyme2),
                createProduct("Lámpara de Sal", "Decorativa y relajante", new BigDecimal("12000"), 20, decor, pyme3),
                createProduct("Power Bank 10000mAh", "Batería externa compacta", new BigDecimal("18000"), 30, tech, pyme1),
                createProduct("Café Orgánico", "Café costarricense 100% puro", new BigDecimal("5500"), 40, food, pyme2)
        ));
    }

    private void createDataForRollback(CategoryEntity tech, CategoryEntity food, CategoryEntity decor) {
        PymeEntity pymeRollback1 = createPyme("RollbackTech", "rollback-tech@example.com", "Heredia", "8888-4444", "Electrónica para pruebas", null);
        PymeEntity pymeRollback2 = createPyme("RollbackFoods", "rollback-food@example.com", "Limón", "8888-5555", "Snacks para pruebas", null);
        PymeEntity pymeRollback3 = createPyme("RollbackDeco", "rollback-deco@example.com","Puntarenas", "8888-6666", "Decoración de prueba", null);

        pymeRepo.saveAll(List.of(pymeRollback1, pymeRollback2, pymeRollback3));

        productRepo.saveAll(List.of(
                createProduct("Teclado Mecánico", "RGB con switches azules", new BigDecimal("35000"), 100, tech, pymeRollback1),
                createProduct("Mouse Inalámbrico", "Con batería recargable", new BigDecimal("15000"), 80, tech, pymeRollback1),
                createProduct("Caja de Chocolates", "Chocolates artesanales surtidos", new BigDecimal("8000"), 100, food, pymeRollback2),
                createProduct("Mermelada de Mango", "Mermelada casera natural", new BigDecimal("4500"), 120, food, pymeRollback2),
                createProduct("Florero de Cerámica", "Florero decorativo hecho a mano", new BigDecimal("11000"), 60, decor, pymeRollback3),
                createProduct("Velas Aromáticas", "Set de velas con esencias", new BigDecimal("9500"), 70, decor, pymeRollback3)
        ));
    }

    private void createDataForPromotion(CategoryEntity tech, CategoryEntity food, CategoryEntity decor) {
        PymeEntity promoTech = createPyme("PromoTech", "promo-tech@example.com", "Guanacaste", "7777-0000", "Tecnología con promos", null);
        PymeEntity promoFood = createPyme("PromoFood", "promo-food@example.com", "San Carlos", "7777-1111", "Comida con promos", null);
        PymeEntity promoDeco = createPyme("PromoDeco", "promo-deco@example.com", "Liberia", "7777-2222", "Decoración con promos", null);

        pymeRepo.saveAll(List.of(promoTech, promoFood, promoDeco));

        ProductEntity p90 = createProduct("Promo90 - Smartwatch", "Reloj inteligente con 90% descuento", new BigDecimal("50000"), 10, tech, promoTech);
        p90.setPromotion(new BigDecimal("0.90"));

        ProductEntity p0 = createProduct("Promo0 - Barritas Naturales", "Snacks saludables sin descuento", new BigDecimal("1500"), 30, food, promoFood);
        p0.setPromotion(BigDecimal.ZERO);

        ProductEntity pNull = createProduct("PromoNull - Cuadro Decorativo", "Decoración sin promo definida", new BigDecimal("25000"), 5, decor, promoDeco);
        pNull.setPromotion(null); // explícitamente nulo

        productRepo.saveAll(List.of(p90, p0, pNull));
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