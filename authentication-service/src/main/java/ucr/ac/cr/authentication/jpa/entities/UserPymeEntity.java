package ucr.ac.cr.authentication.jpa.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "user_pymes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "pyme_id"})
)
public class UserPymeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "pyme_id", nullable = false)
    private UUID pymeId;

    public UserPymeEntity() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPymeId() {
        return pymeId;
    }

    public void setPymeId(UUID pymeId) {
        this.pymeId = pymeId;
    }
}
