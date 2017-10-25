package si.uni_lj.fri.rso.ir_property.cdi;

import si.uni_lj.fri.rso.ir_property.models.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyDatabase {
    private static List<Property> properties = new ArrayList<>(Arrays.asList(
            new Property("0", "location 1", "0"),
            new Property("1", "location 2", "0"),
            new Property("2", "location 3", "2")
    ));

    public static List<Property> getProperties() {
        // TODO: use the filtered hibernate thingy
        return properties;
    }

    public static Property getProperty(String propertyId) {
        for (Property property : properties) {
            if (property.getId().equals(propertyId))
                return property;
        }

        return null;
    }

    public static void addProperty(Property property) {
        properties.add(property);
    }

    public static void deleteProperty(String userId) {
        for (Property property : properties) {
            if (property.getId().equals(userId)) {
                properties.remove(property);
                break;
            }
        }
    }
}
