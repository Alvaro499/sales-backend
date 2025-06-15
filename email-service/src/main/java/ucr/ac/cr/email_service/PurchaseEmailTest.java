package ucr.ac.cr.email_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.consumer.PurchaseSummaryListener;
import ucr.ac.cr.email_service.events.PurchaseSummaryMessage;

import java.util.List;

@Component
public class PurchaseEmailTest implements CommandLineRunner {

    @Autowired
    private PurchaseSummaryListener listener;

    @Override
    public void run(String... args) {

        try {
            List<PurchaseSummaryMessage.Product> products = List.of(
                    new PurchaseSummaryMessage.Product("Café Premium", 2, "3500", "7000"),
                    new PurchaseSummaryMessage.Product("Pan Casero", 1, "1500", "1500")
            );

            PurchaseSummaryMessage msg = getPurchaseSummaryMessage(products);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(msg);

            listener.consume(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PurchaseSummaryMessage getPurchaseSummaryMessage(List<PurchaseSummaryMessage.Product> products) {
        List<PurchaseSummaryMessage.PymeOrder> orders = List.of(
                new PurchaseSummaryMessage.PymeOrder("aldasi2000@hotmail.com", "Delicias Ticas", "8500", products)
        );

        return new PurchaseSummaryMessage(
                "alvarosiles499@gmail.com",
                "María",
                "Rodríguez",
                "8888-8888",
                "San José, Costa Rica",
                "Correo Rápido",
                "Sinpe Móvil",
                "8500",
                orders
        );
    }
}