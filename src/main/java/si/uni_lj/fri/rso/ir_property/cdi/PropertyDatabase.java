package si.uni_lj.fri.rso.ir_property.cdi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.uni_lj.fri.rso.ir_property.models.Property;
import si.uni_lj.fri.rso.ir_property.models.dependencies.Review;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class PropertyDatabase {
    private Logger log = LogManager.getLogger(PropertyDatabase.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private PropertyDatabase propertyDatabase;

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Inject
    @DiscoverService("review-service")
    private String reviewBasePath;

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
    }

    private List<Review> getReviewObjects(String json) throws IOException {
        return json == null ? new ArrayList<>() : objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Review.class));
    }

    public List<Property> getProperties() {
        TypedQuery<Property> query = em.createNamedQuery("Property.getAll", Property.class);
        return query.getResultList();
    }

    public List<Property> getPropertiesFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0).build();
        return JPAUtils.queryEntities(em, Property.class, queryParameters);
    }

    public Property getProperty(String propertyId, boolean includeExtended) {
        Property property = em.find(Property.class, propertyId);
        if (property == null) {
            throw new NotFoundException();
        }
        if (includeExtended) {
            property.setReviews(propertyDatabase.getReviews(property.getId()));
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

    @CircuitBreaker(requestVolumeThreshold = 2)
    @Fallback(fallbackMethod = "getReviewsFallback")
    @Timeout
    public List<Review> getReviews(String propertyId) {
        if (reviewBasePath != null) {
            try {
                HttpGet request = new HttpGet(reviewBasePath + "/v1/reviews/filtered?where=propertyId:EQ:" + propertyId);
                HttpResponse response = httpClient.execute(request);

                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return getReviewObjects(EntityUtils.toString(entity));
                    }
                } else {
                    String msg = "Remote server '" + reviewBasePath + "' has responded with status " + status + ".";
                    throw new InternalServerErrorException(msg);
                }

            } catch (IOException e) {
                String msg = e.getClass().getName() + " occurred: " + e.getMessage();
                throw new InternalServerErrorException(msg);
            }
        } else {
            // service not available placeholder
            log.error("base path is null");
        }
        return new ArrayList<>();
    }

    public List<Review> getReviewsFallback(String userId) {
        ArrayList<Review> result = new ArrayList<>();
        Review review = new Review();
        review.setHeader("N/A");
        review.setMessage("N/A");
        review.setScore(0);
        result.add(review);
        return result;
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
