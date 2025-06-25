package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp; 
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.query.Order;
import ucr.ac.cr.BackendVentas.api.types.enums.OrderStatus;


@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "UUID", nullable = true)
    private UUID user;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = true)
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "pyme_id", referencedColumnName = "pyme_id", nullable = false)
    private PymeEntity pyme;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", referencedColumnName = "payment_method_id", nullable = false)
    private PaymentMethodEntity paymentMethod;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id", referencedColumnName = "shipping_method_id", nullable = false)
    private ShippingMethodEntity shippingMethod;

    @Column(name = "shipping_address", length = 255, nullable = false)
    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderLineEntity> orderLines;

    public List<OrderLineEntity> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineEntity> orderLines) {
        this.orderLines = orderLines;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public PymeEntity getPyme() {
        return pyme;
    }

    public void setPyme(PymeEntity pyme) {
        this.pyme = pyme;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentMethodEntity getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEntity paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ShippingMethodEntity getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethodEntity shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

}
