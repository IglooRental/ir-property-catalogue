package si.uni_lj.fri.rso.ir_property.cdi;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class PropertyCatalogueHealthCheck implements HealthCheck {
    @Inject
    private Config config;

    @Override
    public HealthCheckResponse call() {
        if (config.getHealthy()) {
            return HealthCheckResponse.named(PropertyCatalogueHealthCheck.class.getSimpleName()).up().build();
        } else {
            return HealthCheckResponse.named(PropertyCatalogueHealthCheck.class.getSimpleName()).down().build();
        }
    }
}
