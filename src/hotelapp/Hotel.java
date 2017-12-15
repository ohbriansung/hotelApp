package hotelapp;

/** A class that represents a hotel. Stores hotelId, name, address, and averageRating.
 * Implements Comparable - the hotels are compared based on the hotel names. If the names are the same, hotels
 * are compared based on the hotelId.
 */
public class Hotel implements Comparable<Hotel>{
    private String hotelId;
    private String hotelName;
    private Address hotelAddress;
    private double averageRating;

    /**
     * Constructor
     * @param hId - the id of the hotel
     * @param name - the name of the hotel
     * address should be set to null.
     */
    public Hotel(String hId, String name) {
        this.hotelId = hId;
        this.hotelName = name;
        this.hotelAddress = null;
        this.averageRating = 0;
    }

    /**
     * Constructor
     * @param hId - the id of the hotel
     * @param name - the name of the hotel
     * @param address - the address of the hotel
     */
    public Hotel(String hId, String name, Address address) {
        this.hotelId = hId;
        this.hotelName = name;
        this.hotelAddress = address;
        this.averageRating = 0;
    }

    /**
     * Set the averageRating of this hotel.
     * @param averageRating
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Return the hotel id of this hotel.
     * @return String
     */
    public String getHotelId() {
        return this.hotelId;
    }

    /**
     * Return the hotel name of this hotel.
     * @return String
     */
    public String getHotelName() {
        return this.hotelName;
    }

    /**
     * Return the address of this hotel.
     * @return Address
     */
    public Address getAddress() {
        return this.hotelAddress;
    }

    /**
     * Return the details fo hotel: name, address, city, state, latitude, longitude.
     * @return String[]
     */
    public String[] getHotelDetail() {
        String[] detail = new String[6];
        detail[0] = this.hotelName;
        detail[1] = this.hotelAddress.getStreetAddress();
        detail[2] = this.hotelAddress.getCity();
        detail[3] = this.hotelAddress.getState();
        detail[4] = "" + this.hotelAddress.getLatitude();
        detail[5] = "" + this.hotelAddress.getLongitude();
        return detail;
    }

    /** Compare hotels based on the name (alphabetically). May use compareTo method in class String.
     * If the names are the same, compare based on the hotel ids. */
    @Override
    public int compareTo(Hotel o) {
        //Order by ascending, so when compareTo returns a number greater than 0 then sort first
        if (this.getHotelName().compareTo(o.getHotelName()) > 0) {
            return 1;
        }
        else if (this.getHotelName().compareTo(o.getHotelName()) < 0) {
            return -1;
        }
        else {
            if (this.getHotelId().compareTo(o.getHotelId()) > 0) {
                return 1;
            }
            else if (this.getHotelId().compareTo(o.getHotelId()) < 0) {
                return -1;
            }
            else return 0;
        }
    }

    /**
     * Returns the string representation of the hotel in the following format:
     * hotelName: hotelID
     * streetAddress
     * city, state
     *
     * Example: Travelodge Central San Francisco: 40682
     * 1707 Market St
     * San Francisco, CA
     *
     * Does not include information about the reviews.
     * @return String
     */
    public String toString() {
        return this.hotelName + ": " + this.hotelId + System.lineSeparator() + this.hotelAddress.toString() + System.lineSeparator();
    }

}