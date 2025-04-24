package ucr.ac.cr.BackendVentas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BackendVentasApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendVentasApplication.class, args);
	}

}
