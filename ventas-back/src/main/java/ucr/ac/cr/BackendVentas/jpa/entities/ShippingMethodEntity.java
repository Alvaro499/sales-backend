package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_methods")
public class ShippingMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_method_id", nullable = false)
    private Integer id;

    @Column(name = "name", unique = true, length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
