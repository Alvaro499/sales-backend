package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.handlers.commands.ProductHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.CategoryEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.CategoryRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;

import java.math.BigDecimal;
import java.util.*;

@Component
public class ProductHandlerImpl implements ProductHandler {

    private final ProductRepository productRepository;
    private final PymeRepository pymeRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductHandlerImpl(ProductRepository productRepository, PymeRepository pymeRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.pymeRepository = pymeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Result handle(Command command) {  // Correct method signature
        // Validate fields for product creation command
        var invalidFields = validateFields(command);
        if (invalidFields != null) {
            return invalidFields;
        }

        // Check if Pyme exists for product creation
        Optional<PymeEntity> pymeOptional = pymeRepository.findById(command.pymeId());
        if (pymeOptional.isEmpty()) {
            return new Result.PymeNotFound();
        }

        PymeEntity pyme = pymeOptional.get();

        // Create Product entity
        ProductEntity product = new ProductEntity();
        product.setName(command.name());
        product.setDescription(command.description());
        product.setPrice(command.price());
        product.setPromotion(new BigDecimal(command.promotion()));
        product.setUrlImg(command.images());
        product.setAvailable(command.available());
        List<String> categoryIds = command.category();
        List<CategoryEntity> categories = new ArrayList<>();
        for (String idStr : categoryIds) {
            CategoryEntity category = new CategoryEntity();
            category.setId(Integer.valueOf(idStr));
            categories.add(category);
        }
        product.setCategories(categories);
        product.setActive(true);  // Initially active
        product.setStock(command.stock());
        product.setPyme(pyme);

        // Save product
        ProductEntity savedProduct = productRepository.save(product);
        return new Result.Success(savedProduct);
    }


    public Result listProductsByPyme(UUID pymeId) {
        Optional<PymeEntity> pymeOptional = pymeRepository.findById(pymeId);
        if (pymeOptional.isEmpty()) {
            return new Result.PymeNotFound();  // Use PymeNotFound if no Pyme is found
        }

        PymeEntity pyme = pymeOptional.get();
        List<ProductEntity> products = productRepository.findByPyme(pyme);

        if (products.isEmpty()) {
            return new Result.NotFoundProduct();  // If no products are found
        }

        // Return the list of products wrapped in the SuccessList record
        return new Result.SuccessList(products);  // Returning the list of products wrapped in SuccessList
    }



    public Result unpublishProduct(UnpublishProductCommand command) {
            Optional<ProductEntity> productOptional = productRepository.findById(command.productId());
            if (productOptional.isEmpty()) {
                return new Result.NotFoundProduct();
            }

            ProductEntity product = productOptional.get();
            product.setActive(false);  // Unpublish the product
            productRepository.save(product);

            return new Result.Success(product);
        }


    public Result applyPromotion(ApplyPromotionCommand command) {
        Optional<ProductEntity> productOptional = productRepository.findById(command.productId());
        if (productOptional.isEmpty()) {
            return new Result.NotFoundProduct();
        }

        ProductEntity product = productOptional.get();
        BigDecimal newPromo;

        try {
            newPromo = new BigDecimal(command.promotion());
        } catch (NumberFormatException e) {
            return new Result.PromotionInvalid("Promotion must be a valid number.");
        }

        if (newPromo.compareTo(BigDecimal.ZERO) <= 0) {
            return new Result.PromotionInvalid("Promotion must be greater than 0.");
        }

        if (newPromo.compareTo(product.getPrice()) >= 0) {
            return new Result.PromotionInvalid("Promotion must be less than the original price.");
        }

        product.setPromotion(newPromo);
        productRepository.save(product);

        return new Result.Success(product);
    }


    public Result changeAvailabilityAndStock(ChangeAvailabilityAndStockCommand command) {
        Optional<ProductEntity> productOptional = productRepository.findById(command.productId());
        if (productOptional.isEmpty()) {
            return new Result.NotFoundProduct();
        }
        ProductEntity product = productOptional.get();
        if(command.available() != null){
            product.setAvailable(command.available());  // Change availability
        }
        if(command.stock() != null){
            product.setStock(command.stock());  // Update stock
        }
        productRepository.save(product);

        return new Result.Success(product);
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

    @Override
    public Result searchProducts(String term, Integer categoryId, BigDecimal priceMin, BigDecimal priceMax) {
        List<ProductEntity> filteredProducts = productRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (term != null && !term.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + term.toLowerCase() + "%"));
            }

            if (categoryId != null) {
                predicates.add(cb.isMember(
                        categoryRepository.getById(categoryId),
                        root.get("categories")
                ));
            }

            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            }

            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });

        if (filteredProducts.isEmpty()) {
            return new Result.NotFoundProduct();
        }

        return new Result.SuccessList(filteredProducts);
    }


    public Result listAllProducts() {
        // Obtener todos los productos
        List<ProductEntity> products = productRepository.findAll();

        if (products.isEmpty()) {
            return new Result.NotFoundProduct();  // Si no hay productos
        }

        return new Result.SuccessList(products);  // Retorna los productos como una lista
    }




}
