package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(name = "product_id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "pyme_id", referencedColumnName = "pyme_id")
    private PymeEntity pyme;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    private BigDecimal price;

   @Column(name = "available")
    private Boolean available = true; 

    @Column(name = "promotion", nullable = true)
    private BigDecimal promotion;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "url_img", length = 512)
    private String urlImg;

    @Column(name = "is_active")
    private boolean isActive = true;

    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories = new ArrayList<>();

    @PrePersist
    public void generarId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PymeEntity getPyme() {
        return pyme;
    }

    public void setPyme(PymeEntity pyme) {
        this.pyme = pyme;
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

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }
}
