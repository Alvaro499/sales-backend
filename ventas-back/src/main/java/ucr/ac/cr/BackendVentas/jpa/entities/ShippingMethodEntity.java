package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_methods")
public class ShippingMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_method_id")
    private Integer shippingMethodId;

    @Column(name = "name", unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "is_active")
    private Boolean isActive;


    // Getters y Setters

    public Integer getShippingMethodId() {
        return this.shippingMethodId;
    }

    public void setShippingMethodId(Integer shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return this.cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
