package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "user_pymes")
public class UserPymeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_pyme_id",  columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "UUID", nullable = false)
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "pyme_id", referencedColumnName = "pyme_id", nullable = false)
    private PymeEntity pyme;

    @Column(name = "is_owner")
    private boolean isOwner = false;
}
