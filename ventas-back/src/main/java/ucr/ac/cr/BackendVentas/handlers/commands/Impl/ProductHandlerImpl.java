package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.handlers.commands.ProductHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ProductHandlerImpl implements ProductHandler {

    private final ProductRepository productRepository;
    private final PymeRepository pymeRepository;

    @Autowired
    public ProductHandlerImpl(ProductRepository productRepository, PymeRepository pymeRepository) {
        this.productRepository = productRepository;
        this.pymeRepository = pymeRepository;
    }

    @Override
    public Result handle(Command command) {
        // Validate fields
        var invalidFields = validateFields(command);
        if (invalidFields != null) {
            return invalidFields;
        }

        // Check if Pyme exists
        Optional<PymeEntity> pymeOptional = pymeRepository.findById(String.valueOf(command.pymeId()));
        if (pymeOptional.isEmpty()) {
            return new Result.PymeNotFound();
        }

        PymeEntity pyme = pymeOptional.get();

        // Create Product entity
        ProductEntity product = new ProductEntity();
        product.setName(command.name());
        product.setDescription(command.description());
        product.setPrice(command.price());
        product.setCategory(command.category());
        product.setImages(command.images());
        product.setAvailable(command.available());
        product.setPromotion(command.promotion());
        product.setPublished(true);
        product.setStock(command.stock());
        product.setPyme(pyme);

        // Save product
        ProductEntity savedProduct = productRepository.save(product);
        return new Result.Success(savedProduct);
    }

    private Result validateFields(Command command) {
        if (command.name() == null || command.name().isEmpty()) {
            return new Result.InvalidFields("name");
        }
        if (command.description() == null || command.description().isEmpty()) {
            return new Result.InvalidFields("description");
        }
        if (command.price() == null || command.price().compareTo(BigDecimal.ZERO) <= 0) {
            return new Result.InvalidFields("price");
        }
        return null;
    }
}
