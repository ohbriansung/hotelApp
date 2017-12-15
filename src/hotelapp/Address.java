package hotelapp;

/** The class that represents the address of a hotel in USA. Stores the following data about the address:
 * city, state, street address, latitude and longitude.
 */
public class Address {
    private String streetAddress;
    private String city;
    private String state;
    private double latitude;
    private double longitude;

    /**
     * Constructor that takes city, state, streetAddress, latitude and longitude
     */
    public Address(String city, String state, String streetAddress, double lat, double lon) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.latitude = lat;
        this.longitude = lon;
    }

    /**
     * Return the street address of this address.
     * @return String
     */
    public String getStreetAddress() {
        return this.streetAddress;
    }

    /**
     * Return the city of this address.
     * @return String
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Return the state of this address.
     * @return String
     */
    public String getState() {
        return this.state;
    }

    /**
     * Return the latitude of this address.
     * @return double
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Return the longitude of this address.
     * @return double
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Return the string representing the address in the following format:
     * street address on the first line,
     * city, state on the second line. Example:
     * 17 Green st., San Francisco, CA
     *
     * @return string representing the address of the hotel
     */
    public String toString() {
        return this.getStreetAddress() + ", " + this.getCity() + ", " + getState();
    }
}