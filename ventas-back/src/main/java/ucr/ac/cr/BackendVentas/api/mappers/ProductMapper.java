package ucr.ac.cr.BackendVentas.api.mappers;

import ucr.ac.cr.BackendVentas.api.types.ProductDTO;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductDTO toDTO(ProductEntity product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setAvailable(product.getAvailable());
        dto.setPromotion(product.getPromotion());
        dto.setStock(product.getStock());
        dto.setUrlImg(product.getUrlImg());
        dto.setCreatedAt(product.getCreatedAt());

        // Mapeo de categorías, si existe
        if (product.getCategories() != null) {
            dto.setCategories(product.getCategories().stream()
                    .map(c -> c.getName()) // O c.getId().toString() si prefieres IDs
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static List<ProductDTO> toDTOList(List<ProductEntity> products) {
        return products.stream()
                .map(ProductMapper::toDTO)  // Mapea cada ProductEntity a ProductDTO
                .collect(Collectors.toList());
    }
}
