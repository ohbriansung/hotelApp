package data;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to store searching data as an object.
 *
 * @author BrianSung
 */
public class DataSession {
    private String name;
    private Map<String, String> elements;
    private Map<String, Object> objects;

    /**
     * Constructor.
     * Named this data session: hotel/review/attraction.
     *
     * @param name
     */
    public DataSession (String name) {
        this.name = name;
        this.elements = new HashMap<>();
        this.objects = new HashMap<>();
    }

    /**
     * Create or alter an String element.
     */
    public void setElement(String key, String value) {
        this.elements.put(key, StringEscapeUtils.escapeHtml4(value));
    }

    /**
     * Get a String element by key.
     *
     * @return String
     */
    public String getElement(String key) {
        return (this.elements.get(key) == null ? "" : this.elements.get(key));
    }

    /**
     * Create or alter an object, not just String.
     * For TouristAttractionFinder to store an Iterable Object contains with attractions data.
     *
     * @param key
     * @param value
     */
    public void setObject(String key, Object value) {
        this.objects.put(key, value);
    }

    /**
     * Get a Object by key.
     *
     * @param key
     * @return Object
     */
    public Object getObject(String key) {
        return this.objects.get(key);
    }
}
