package ucr.ac.cr.BackendVentas.api.types;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductDTO {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean available;
    private BigDecimal promotion;
    private Integer stock;
    private List<String> urlImg;
    private LocalDateTime createdAt;
    private List<String> categories; // Aquí solo nombres o IDs

    public ProductDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public BigDecimal getPromotion() {
        return promotion;
    }

    public void setPromotion(BigDecimal promotion) {
        this.promotion = promotion;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<String> getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(List<String> urlImg) {
        this.urlImg = urlImg;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
