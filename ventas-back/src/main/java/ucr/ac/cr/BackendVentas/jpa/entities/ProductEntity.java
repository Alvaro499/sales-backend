package ucr.ac.cr.BackendVentas.jpa.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp; 
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "product_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pyme_id", referencedColumnName = "pyme_id", nullable = false)
    private PymeEntity pyme;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = true)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "available", nullable = false)
    private Boolean available = true; 

    @Column(name = "promotion", nullable = true)
    private BigDecimal promotion;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "url_img", length = 512, nullable = true)
    private List<String> urlImg;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
