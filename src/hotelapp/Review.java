package hotelapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** The class stores information about one hotel review.
 *  Stores the id of the review, the id of the corresponding hotel, the rating,
 *  the title of the review, the text of the review, the date when the review was posted in
 *  the following format: yyyy-MM-ddThh:mm:ss
 *  Also stores the nickname of the user who submitted this review,
 *  and whether the user recommends the hotel to others or not.
 *  Implements Comparable - reviews should be compared based on the date
 *  (more recent review is considered "less" that the older one).
 *  If the dates are the same, compares reviews based on the user nicknames alphabetically.
 *  If the user nicknames are the same, compares based on the review id.
 *
 */
public class Review implements Comparable<Review> {

    public DateFormat format; // the date format
    public static final double MINREVIEW = 1;
    public static final double MAXREVIEW = 5;

    private String reviewId;
    private String hotelId;
    private int rating;
    private String reviewTitle;
    private String review;
    private boolean isRecom;
    private Date date;
    private String username;

    /**
     * Default constructor.
     */
    public Review() {
        this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.hotelId = null;
        this.reviewId = null;
        this.rating = 0;
        this.reviewTitle = null;
        this.review = null;
        this.isRecom = true;
        this.date = null;
        this.username = null;
    }

    /**
     * Constructor
     *
     * @param hotelId
     *            - the id of the hotel that is being reviewed
     * @param reviewId
     *            = the id of the review
     * By default, the hotel is recommended.
     */
    public Review(String hotelId, String reviewId) {
        this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.rating = 0;
        this.reviewTitle = null;
        this.review = null;
        this.isRecom = true;
        this.date = null;
        this.username = null;
    }

    /**
     * Constructor
     *
     * @param hotelId
     *            - id of the hotel that is being reviewed
     * @param reviewId
     *            - id of the review
     * @param rating
     *            - integer rating from 1 to 5
     * @param reviewTitle
     *            - the title of the review
     * @param review
     *            - text of the review.
     * @param isRecom
     *            - boolean, whether the user recommends it or not
     * @param date
     *            - date of the review in the format yyyy-MM-ddThh:mm:ss
     * @param username
     *            - the nickname of the user writing the review. If empty, save it as  "Anonymous"
     * @throws ParseException
     *             - If date is not valid.
     * @throws InvalidRatingException
     * 			   - If the rating is out of the correct range from MINREVIEW TO MAXREVIEW
     */
    public Review(String hotelId, String reviewId, int rating, String reviewTitle, String review, boolean isRecom,
                  String date, String username) throws ParseException, InvalidRatingException {
        //if the rating is out of range then throw exception
        if (rating < MINREVIEW || rating > MAXREVIEW) {
            throw new InvalidRatingException("The value of the hotel rating is out of range");
        }

        this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.rating = rating;
        this.reviewTitle = reviewTitle;
        this.review = review;
        this.isRecom = isRecom;
        try {
            this.date = format.parse(date); //check if the format of incoming date is correct
        }
        catch (ParseException e) {
            this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            this.date = format.parse(date); // from database date format
        }
        this.username = username;
    }

    /**
     * Return the hotel id of this review.
     * @return String
     */
    public String getHotelId() {
        return this.hotelId;
    }

    /**
     * Return the review id of this review.
     * @return String
     */
    public String getReviewId() {
        return this.reviewId;
    }

    /**
     * Return the rating of this review.
     * @return int
     */
    public int getRating() {
        return this.rating;
    }

    /**
     * Return the title of this review.
     * @return String
     */
    public String getReviewTitle() {
        return this.reviewTitle;
    }

    /**
     * Return the review content of this review.
     * @return String
     */
    public String getReview() {
        return this.review;
    }

    /**
     * Return the date of this review.
     * @return Date
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Return the username of this review.
     * @return String
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Return the recommendation status of this review.
     * @return boolean
     */
    public boolean getIsRecom() {
        return this.isRecom;
    }

    /** Compares this review with the review passed as a parameter based on
     *  the dates (more recent date is "less" than older date).
     *  If the dates are equal, it compares reviews based on the user nicknames, alphabetically.
     *  If user nicknames are the same, it compares based on the review ids.
     *  Note that we only care about comparing reviews for the same hotel id.
     *  @param other review to compare this one with
     *  @return
     *  	-1 if this review is "less than" the argument,
     *       0 if equal
     *  	 1 if this review is "greater" than the other one
     */
    @Override
    public int compareTo(Review other) {
        //date should be order descending so when compareTo returns a number smaller then 0 then sort first
        if(this.date.compareTo(other.getDate()) < 0) {
            return 1;
        }
        else if (this.date.compareTo(other.getDate()) > 0) {
            return -1;
        }
        else {
            //name and id should be order ascending
            if (this.username.compareTo(other.getUsername()) > 0) {
                return 1;
            }
            else if (this.username.compareTo(other.getUsername()) < 0) {
                return -1;
            }
            else {
                if (this.reviewId.compareTo(other.getReviewId()) > 0) {
                    return 1;
                }
                else if (this.reviewId.compareTo(other.getReviewId()) < 0) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        }
    }

    /** Return a string representation of this review. Use StringBuilder for efficiency.
     * @return A string in the following format:
                    Review by username on date
                    Rating: rating
                    reviewTitle
                    textOfReview
     * Example:
                    Review by Ben on Tue Aug 16 18:38:29 PDT 2016
                    Rating: 2
                    Very bad experience
                    Awaken by noises from top floor at 5AM. Lots of mosquitos too.

     * If the username is null or empty, print "Anonymous" instead of the username
     */
    public String toString() {

        //when username is empty or null then print out "Anonymous"
        String name = ((this.username.equals("") || this.username.equals(null)) ? "Anonymous" : this.username);
        StringBuilder sb= new StringBuilder();

        sb.append("Review by " + name + " on " + this.date.toString() + System.lineSeparator());
        sb.append("Rating: " + this.rating + System.lineSeparator());
        sb.append(this.reviewTitle + System.lineSeparator());
        sb.append(this.review + System.lineSeparator());

        return sb.toString();
    }
}