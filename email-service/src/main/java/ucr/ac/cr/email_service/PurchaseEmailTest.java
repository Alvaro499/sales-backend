package ucr.ac.cr.email_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.consumer.PurchaseSummaryListener;
import ucr.ac.cr.email_service.events.PurchaseSummaryMessage;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PurchaseEmailTest implements CommandLineRunner {

    private final PurchaseSummaryListener listener;

    public PurchaseEmailTest(PurchaseSummaryListener listener) {
        this.listener = listener;
    }

    @Override
    public void run(String... args) {

        System.out.println("Running PurchaseEmailTest...");
        /*
        try {
            List<PurchaseSummaryMessage.Product> products = List.of(
                    new PurchaseSummaryMessage.Product(
                            "Café Premium", 2,
                            new BigDecimal("3500"),
                            new BigDecimal("7000"),
                            new BigDecimal("0.0"),  // o el porcentaje real
                            new BigDecimal("7000")
                    ),
                    new PurchaseSummaryMessage.Product(
                            "Pan Casero", 1,
                            new BigDecimal("1500"),
                            new BigDecimal("1500"),
                            new BigDecimal("0.0"),
                            new BigDecimal("1500")
                    )
            );

            PurchaseSummaryMessage msg = getPurchaseSummaryMessage(products);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(msg);

            listener.consume(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    private static PurchaseSummaryMessage getPurchaseSummaryMessage(List<PurchaseSummaryMessage.Product> products) {
        List<PurchaseSummaryMessage.PymeOrder> orders = List.of(
                new PurchaseSummaryMessage.PymeOrder(
                        "aldasi2000@hotmail.com",
                        "Delicias Ticas",
                        new BigDecimal("8500"),
                        products
                )
        );

        return new PurchaseSummaryMessage(
                "ventas.pymes5.pruebas@gmail.com",
                "María",
                "Rodríguez",
                "8888-8888",
                "San José, Costa Rica",
                "Correo Rápido",
                "Sinpe Móvil",
                new BigDecimal("8500"),
                orders
        );
    }
}