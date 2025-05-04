package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "user_id", length = 36)
    private String id;

     @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    
     @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "is_active")
    private boolean isActive = true;


    @PrePersist
    public void generarId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters y Setters
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return this.passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return this.firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return this.lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return this.address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return this.phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return this.role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return this.isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
