package si.uni_lj.fri.rso.ir_property.models;

import org.eclipse.persistence.annotations.UuidGenerator;
import si.uni_lj.fri.rso.ir_property.models.dependencies.Review;

import javax.persistence.*;
import java.util.List;

@Entity(name = "properties")
@NamedQueries(value = {
        @NamedQuery(name = "Property.getAll", query = "SELECT p FROM properties p")
})
@UuidGenerator(name = "idGenerator")
public class Property {
    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String location;

    @Column(name = "owner_id")
    private String ownerId;

    @Transient
    private List<Review> reviews;

    public Property(String id, String location, String ownerId) {
        this.id = id;
        this.location = location;
        this.ownerId = ownerId;
    }

    public Property() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
