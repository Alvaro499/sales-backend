package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = true)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "pyme_id", referencedColumnName = "pyme_id", nullable = false)
    private PymeEntity pyme;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "status",length = 20)
    private String status;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", referencedColumnName = "payment_method_id", nullable = false)
    private PaymentMethodEntity paymentMethod;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id", referencedColumnName = "shipping_method_id", nullable = false)
    private ShippingMethodEntity shippingMethod;

    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public PymeEntity getPyme() {
        return pyme;
    }

    public void setPyme(PymeEntity pyme) {
        this.pyme = pyme;
    }

    public LocalDateTime getOrderDate() {
        return this.orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PaymentMethodEntity getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEntity paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ShippingMethodEntity getShippingMethod() {
        return this.shippingMethod;
    }

    public void setShippingMethod(ShippingMethodEntity shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
