package ucr.ac.cr.BackendVentas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ucr.ac.cr.BackendVentas.jpa.repositories.CategoryRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;

import static org.assertj.core.api.Assertions.assertThat;


/*
 * Prueba de humo para verificar que el contexto de la aplicación se carga correctamente
 * con el perfil de pruebas y que la base de datos H2 contiene los datos iniciales esperados.
 */

@SpringBootTest
@ActiveProfiles("test")
@Import(DataInitializer.class)
public class SmokeTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PymeRepository pymeRepository;

    @Test
    void contextLoads_andH2Works() {
        long count = categoryRepository.count();
        assertThat(count).isGreaterThan(0);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void shouldHavePymesInDatabase() {
        long count = pymeRepository.count();
        assertThat(count).isEqualTo(6);
    }
}
