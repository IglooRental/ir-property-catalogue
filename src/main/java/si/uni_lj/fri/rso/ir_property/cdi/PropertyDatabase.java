package si.uni_lj.fri.rso.ir_property.cdi;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.uni_lj.fri.rso.ir_property.models.Property;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@RequestScoped
public class PropertyDatabase {
    @Inject
    private EntityManager em;

    public List<Property> getProperties() {
        TypedQuery<Property> query = em.createNamedQuery("Property.getAll", Property.class);
        return query.getResultList();
    }

    public List<Property> getPropertiesFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0).build();
        return JPAUtils.queryEntities(em, Property.class, queryParameters);
    }

    public Property getProperty(String propertyId) {
        Property property = em.find(Property.class, propertyId);
        if (property == null) {
            throw new NotFoundException();
        }
        return property;
    }

    public Property createProperty(Property property) {
        try {
            beginTx();
            em.persist(property);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return property;
    }

    public Property putProperty(String propertyId, Property property) {
        Property p = em.find(Property.class, propertyId);
        if (p == null) {
            return null;
        }
        try {
            beginTx();
            property.setId(p.getId());
            property = em.merge(property);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }
        return property;
    }

    public boolean deleteProperty(String propertyId) {
        Property p = em.find(Property.class, propertyId);
        if (p != null) {
            try {
                beginTx();
                em.remove(p);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }
        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
