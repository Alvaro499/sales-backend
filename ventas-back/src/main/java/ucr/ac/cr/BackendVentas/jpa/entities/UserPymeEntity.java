package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_pymes")
public class UserPymeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pymes")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "pyme_id", referencedColumnName = "pyme_id", nullable = false)
    private PymeEntity pyme;

    @Column(name = "is_owner")
    private boolean isOwner = false;

    // Getters y Setters
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }

    public UserEntity getUser() { return this.user; }
    public void setUser(UserEntity user) { this.user = user; }

    public PymeEntity getPyme() { return this.pyme; }
    public void setPyme(PymeEntity pyme) { this.pyme = pyme; }

    public boolean isOwner() { return this.isOwner; }
    public void setOwner(boolean owner) { this.isOwner = owner; }
}
