package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.util.ArrayList; 
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "name", unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<ProductEntity> products = new ArrayList<>();

    // Getters and setters

    public Integer getCategoryId(){ return this.id; } 

    public void setCategoryId(Integer id){ this.id = id; }

    public String getName(){ return this.name; }

    public void setName(String name){ this.name = name; }

    public String getDescription() { return this.description; }

    public void setDescription(String description) { this.description = description; }

     public List<ProductEntity> getProducts() {
        return this.products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }
}
