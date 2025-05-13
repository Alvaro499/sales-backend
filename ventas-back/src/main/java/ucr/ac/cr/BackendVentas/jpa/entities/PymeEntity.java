package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;
import java.util.ArrayList; 
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "pymes")
public class PymeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "pyme_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "phone", length = 50, nullable = false)
    private String phone;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "logo_url", length = 255,  nullable = true)
    private String logoUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "pyme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products = new ArrayList<>();
}
