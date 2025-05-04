package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_lines")
public class OrderLineEntity {

    @Id
    @Column(name = "order_line_id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "unit_price")
    private BigDecimal subtotal;

    @PrePersist
    public void generarId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters y Setters
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public OrderEntity getOrder() { return this.order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public ProductEntity getProduct() { return this.product; }
    public void setProduct(ProductEntity product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return this.unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() { return this.subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
