package hotelapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;

/**
 * Class HotelData - a data structure that stores information about hotels and
 * hotel reviews. Allows to quickly lookup a Hotel given the hotel id. (use TreeMap).
 * Allows to efficiently find hotel reviews for a given hotelID (use a TreeMap,
 * where for each hotelId, the value is a TreeSet). Reviews for a
 * given hotel id are sorted by date from most recent to oldest;
 * if the dates are the same, the reviews are sorted by user nickname,
 * and the user nicknames are the same, by the reviewId.
 *
 */
public class HotelData {

    private Map<String, Hotel> hotelMap;
    private Map<String, TreeSet> reviewMap;

    /**
     * Default constructor.
     */
    public HotelData() {
        hotelMap = new TreeMap<>();
        reviewMap = new TreeMap<>();
    }

    /**
     * Create a Hotel given the parameters, and add it to the appropriate data
     * structure.
     *
     * @param hotelId
     *            - the id of the hotel
     * @param hotelName
     *            - the name of the hotel
     * @param city
     *            - the city where the hotel is located
     * @param state
     *            - the state where the hotel is located.
     * @param streetAddress
     *            - the building number and the street
     * @param lat latitude
     * @param lon longitude
     */
    public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress
            , double lat, double lon) {
        Address address = new Address(city, state, streetAddress, lat, lon);
        Hotel newHotel = new Hotel(hotelId, hotelName, address);
        hotelMap.put(hotelId, newHotel);
    }

    /**
     * Add a new hotel review. Add it to the map (to the TreeSet of reviews for a given key=hotelId).
     *
     * @param hotelId
     *            - the id of the hotel reviewed
     * @param reviewId
     *            - the id of the review
     * @param rating
     *            - integer rating 1-5.
     * @param reviewTitle
     *            - the title of the review
     * @param review
     *            - text of the review
     * @param isRecom
     *            - whether the user recommends it or not
     * @param date
     *            - date of the review in the format yyyy-MM-ddThh:mm:ss, e.g. "2016-06-29T17:50:37"
     * @param username
     *            - the nickname of the user writing the review.
     * @return true if successful, false if unsuccessful because of invalid hotelId, invalid date
     *         or rating. Needs to catch and handle the following exceptions:
     *         ParseException if the date is invalid
     *         InvalidRatingException if the rating is out of range.
     */
    public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle,
                             String review, boolean isRecom, String date, String username){

        if (!hotelMap.containsKey(hotelId)) {
            System.out.println("Exception while running the addReview: Invalid hotelId.");
            return false;
        }
        else {
            try {
                Review newReview = new Review(hotelId, reviewId, rating, reviewTitle, review, isRecom, date, username);

                if (reviewMap.containsKey(hotelId)) {
                    reviewMap.get(hotelId).add(newReview);
                    return true;
                }
                else {
                    Set<Review> newReviewSet = new TreeSet<>();
                    newReviewSet.add(newReview);
                    reviewMap.put(hotelId, (TreeSet)newReviewSet);
                    return true;
                }

            } catch (java.text.ParseException e) {
                System.out.println("Exception while running the addReview: " + e);
                return false;
            } catch (InvalidRatingException e) {
                System.out.println("Exception while running the addReview: " + e);
                return false;
            }
        }
    }

    /**
     * Returns a string representing information about the hotel with the given
     * id, including all the reviews for this hotel separated by --------------------
     * Format of the string:
     * HotelName: hotelId
     * streetAddress
     * city, state
     * --------------------
     * Review by username on date
     * Rating: rating
     * ReviewTitle
     * ReviewText
     * --------------------
     * Review by username on date
     * Rating: rating
     * ReviewTitle
     * ReviewText ...
     *
     * @param hotelId
     * @return - output string.
     */
    public String toString(String hotelId) {
        StringBuilder sb = new StringBuilder();

        if (hotelMap.get(hotelId) != null) {
            sb.append(hotelMap.get(hotelId).toString());

            if (reviewMap.get(hotelId) != null) {
                Set<Review> reviews = reviewMap.get(hotelId);
                for (Review perReview : reviews) {
                    sb.append("--------------------");
                    sb.append(System.lineSeparator());
                    sb.append(perReview.toString());
                }
            }
        }

        return sb.toString();
    }

    /**
     * Return a list of hotel ids, in alphabetical order of hotelIds
     *
     * @return
     */
    public List<String> getHotels() {
        List<String> hotelIdList = new ArrayList<>();
        Set<String> hotelIdKeys = hotelMap.keySet();

        for (String key: hotelIdKeys) {
            hotelIdList.add(key);
        }

        return hotelIdList;
    }

    /**
     * Return the average rating for the given hotelId.
     *
     * @param hotelId-
     *            the id of the hotel
     * @return average rating or 0 if no ratings for the hotel
     */
    public double getRating(String hotelId) {
        double rating = 0; //return 0 when there is no key hotelId

        if (reviewMap.get(hotelId) != null) {
            int sum = 0;
            Set<Review> reviews = reviewMap.get(hotelId);

            for (Review perReview : reviews) {
                sum += perReview.getRating();
            }

            rating = (double)sum / (double)reviews.size();
        }

        return rating;
    }

    /**
     * Save the string representation of the hotel data to the file specified by
     * filename in the following format (see "expectedOutput" in the test folder):
     * an empty line
     * A line of 20 asterisks ********************
     * on the next line information for each hotel, printed
     * in the format described in the toString method of this class.
     *
     * The hotels in the file should be sorted by hotel ids
     *
     * @param filename
     *            - Path specifying where to save the output.
     */
    public void printToFile(Path filename) {
        if (hotelMap.size() > 0) {

            try (PrintWriter pw = new PrintWriter(filename.toString(), "UTF-8")) {
                StringBuilder sb = new StringBuilder();

                //get all the hotelId in hotelMap
                for (String key:getHotels()) {
                    sb.append(System.lineSeparator());	//an empty line
                    sb.append("********************");	//20 asterisks
                    sb.append(System.lineSeparator());	//\n
                    sb.append(toString(key));			//calling toString in this class
                }

                pw.println(sb.toString()); //write String into the file
                pw.flush();
            } catch (IOException e) {
                System.out.println("Exception while running the printToFile: " + e);
            }
        }
    }

    /**
     * To merge the local review maps into big review map.
     * To set the average rating of each hotel after done the review loading.
     *
     * @param data
     *            - local hotel data.
     */
    public void mergeReviewMapAndSetRating(HotelData data) {
        List<String> keys = this.getHotels();

        for (String key: keys) {
            if (data.reviewMap.get(key) != null) {
                this.reviewMap.put(key, data.reviewMap.get(key));

                //set averageRating
                this.hotelMap.get(key).setAverageRating(getRating(key));
            }
        }
    }

    /**
     * Return the details of hotel: name, address, city, state, latitude, longitude.
     * @return String[]
     */
    public String[] getHotelDetail(String hotelId) {
        return hotelMap.get(hotelId).getHotelDetail();
    }

    /**
     * Return hotel by id.
     * @return Hotel
     */
    public Hotel getHotel(String hotelId) {
        return hotelMap.get(hotelId);
    }

    /**
     * Return reviews of hotel.
     * @return Set
     */
    public Set<Review> getHotelReviews(String hotelId) {
        return reviewMap.get(hotelId);
    }
}
