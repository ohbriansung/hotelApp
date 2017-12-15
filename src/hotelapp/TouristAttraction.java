package hotelapp;

import java.util.List;

/** A class that represents a tourist attraction.
 * @author BrianSung
 */
public class TouristAttraction {

    private String attractionId;
    private String name;
    private double rating;
    private String address;
    private String photoUrl;
    private List<String> types;

    /** Constructor for TouristAttraction
     *
     * @param id
     * @param name
     * @param rating
     * @param address
     */
    public TouristAttraction(String id, String name, double rating, String address, String photoUrl, List<String> types) {
        this.attractionId = id;
        this.name = name;
        this.rating = rating;
        this.address = address;
        this.photoUrl = photoUrl;
        this.types = types;
    }

    /**
     * Return the id of the tourist attraction.
     *
     * @return String
     */
    public String getAttractionId() {
        return this.attractionId;
    }

    /**
     * Return the name of the tourist attraction.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the rating of the tourist attraction.
     *
     * @return double
     */
    public double getRating() {
        return this.rating;
    }

    /**
     * Return the address of the tourist attraction.
     *
     * @return String
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Return the photo url of the tourist attraction.
     *
     * @return String
     */
    public String getPhotoUrl() {
        return this.photoUrl;
    }

    /**
     * Return the types of the tourist attraction.
     *
     * @return List
     */
    public List<String> getTypes() {
        return this.types;
    }

    /** toString() method
     * @return a String representing this
     * TouristAttraction
     */
    @Override
    public String toString() {
        return this.name + "; " + this.address;
    }
}
