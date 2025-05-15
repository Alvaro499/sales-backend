package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp; 
import org.hibernate.annotations.GenericGenerator;


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
    @JoinColumn(name = "pyme_id", referencedColumnName = "pyme_id", nullable = false)
    private PymeEntity pyme;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "status",length = 20, nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", referencedColumnName = "payment_method_id", nullable = false)
    private PaymentMethodEntity paymentMethod;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id", referencedColumnName = "shipping_method_id", nullable = false)
    private ShippingMethodEntity shippingMethod;

    @Column(name = "shipping_address", length = 255, nullable = false)
    private String shippingAddress;
}
