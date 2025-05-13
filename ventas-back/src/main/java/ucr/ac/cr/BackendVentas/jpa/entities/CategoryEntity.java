package ucr.ac.cr.BackendVentas.jpa.entities;

import jakarta.persistence.*;

import java.util.ArrayList; 
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer id;

    @Column(name = "name", unique = true, length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<ProductEntity> products = new ArrayList<>();
}
