package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hotelapp.*;

/**
 * Handles all database-related actions. Uses singleton design pattern.
 *
 * @author BrianSung
 */
public class DatabaseHandler {
    private static HotelDataBuilder builder;
    private static ThreadSafeHotelData hdata;
    public static final int THREAD = 4;

    /** Makes sure only one database handler is instantiated. */
    private static DatabaseHandler singleton = new DatabaseHandler();

    /** Used to configure connection to database. */
    private DatabaseConnector db;

    /** Used to generate password hash salt for user. */
    private Random random;

    /**
     * Initializes a database handler for the Login example. Private constructor
     * forces all other classes to use singleton.
     */
    private DatabaseHandler() {
        Status status = Status.OK;
        random = new Random(System.currentTimeMillis());

        try {
            db = new DatabaseConnector();
            status = db.testConnection() ? setupTables() : Status.CONNECTION_FAILED;
        }
        catch (FileNotFoundException e) {
            status = Status.MISSING_CONFIG;
        }
        catch (IOException e) {
            status = Status.MISSING_VALUES;
        }

        if (status != Status.OK) {
            System.out.println(status.message());
        }
    }

    /**
     * Gets the single instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return singleton;
    }

    /**
     * Checks to see if a String is null or empty.
     * @param text - String to check
     * @return true if non-null and non-empty
     */
    public static boolean isBlank(String text) {
        return (text == null) || text.trim().isEmpty();
    }

    /**
     * Checks if necessary table exists in database, and if not tries to create it.
     *
     * @return Status
     */
    private Status setupTables() {
        Status status = Status.ERROR;

        try (Connection connection = db.getConnection();Statement statement = connection.createStatement()) {
            ResultSet res = statement.executeQuery(MySqlQuery.TABLES_SQL);
            if (!res.next() || res.getInt("num") < 9) {
                // Table missing, must create
                System.out.println("Creating tables...");
                statement.executeUpdate(MySqlQuery.CREATE_USER_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_HOTEL_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_REVIEW_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_EXPEDIA_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_LIKEREVIEW_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_LIKEHOTEL_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_LIKEEXPEDIA_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_APIKEYS_TABLE_SQL);
                statement.executeUpdate(MySqlQuery.CREATE_LOGINS_TABLE_SQL);

                // loading data
                System.out.println("Loading data...");
                loadData(connection);

                // Check if create was successful
                if (!statement.executeQuery(MySqlQuery.TABLES_SQL).next()) {
                    status = Status.CREATE_FAILED;
                }
                else {
                    status = Status.OK;
                    db.testConnection();
                }
            }
            else {
                System.out.println("Tables found.");
                status = Status.OK;
            }
        }
        catch (Exception ex) {
            status = Status.CREATE_FAILED;
            System.out.println(status.toString() + ex);
        }

        return status;
    }

    /**
     * Tests if a user already exists in the database. Requires an active
     * database connection.
     *
     * @param connection - active database connection
     * @param user - username to check
     * @return Status.OK if user does not exist in database
     */
    private Status duplicateUser(Connection connection, String user) {

        assert connection != null;
        assert user != null;

        Status status = Status.ERROR;

        try (PreparedStatement statement = connection.prepareStatement(MySqlQuery.USER_SQL)) {
            statement.setString(1, user);

            ResultSet results = statement.executeQuery();
            status = results.next() ? Status.DUPLICATE_USER : Status.OK;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage() + e);
            status = Status.SQL_EXCEPTION;
        }

        return status;
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return String
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt - salt associated with user
     * @return String
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            System.out.println("Unable to properly hash password." + ex);
        }

        return hashed;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database if the username does not already exist.
     *
     * @param newUsername - username of new user
     * @param newPassword - password of new user
     * @return Status
     */
    private Status registerUser(Connection connection, String newUsername, String newPassword) {
        Status status = Status.ERROR;

        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32);
        String passhash = getHash(newPassword, usersalt);

        try (PreparedStatement statement = connection.prepareStatement(MySqlQuery.REGISTER_SQL)) {
            statement.setString(1, newUsername);
            statement.setString(2, passhash);
            statement.setString(3, usersalt);
            statement.executeUpdate();

            status = Status.OK;
        }
        catch (SQLException ex) {
            status = Status.SQL_EXCEPTION;
            System.out.println(ex.getMessage() + ex);
        }

        return status;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database if the username does not already exist.
     *
     * @param newUsername - username of new user
     * @param newPassword - password of new user
     * @return Status
     */
    public Status registerUser(String newUsername, String newPassword) {
        Status status = Status.ERROR;
        System.out.println("Registering " + newUsername + ".");

        // make sure we have non-null and non-empty values for login
        if (isBlank(newUsername) || isBlank(newPassword)) {
            status = Status.INVALID_LOGIN;
            return status;
        }
        else {
            status = checkUsername(newUsername);
            if (status == Status.USERNAME_NOT_GOOD) {
                return status;
            }

            status = checkPassword(newPassword);
            if (status == Status.PASSWORD_NOT_GOOD) {
                return status;
            }
        }

        // try to connect to database and test for duplicate user
        System.out.println(db);

        try (Connection connection = db.getConnection()) {
            status = duplicateUser(connection, newUsername);

            // if okay so far, try to insert new user
            if (status == Status.OK) {
                status = registerUser(connection, newUsername, newPassword);
            }
        }
        catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            System.out.println(status.toString() + ex);
        }

        return status;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param user - which user to retrieve salt for
     * @return String
     * @throws SQLException
     */
    private String getSalt(Connection connection, String user) throws SQLException {
        assert connection != null;
        assert user != null;

        String salt = null;

        try (PreparedStatement statement = connection.prepareStatement(MySqlQuery.SALT_SQL)) {
            statement.setString(1, user);


            ResultSet results = statement.executeQuery();

            if (results.next()) {
                salt = results.getString("usersalt");
            }
        }

        return salt;
    }

    /**
     * Checks if the provided username and password match what is stored
     * in the database. Requires an active database connection.
     *
     * @param username - username to authenticate
     * @param password - password to authenticate
     * @return Status
     * @throws SQLException
     */
    private Status authenticateUser(Connection connection, String username, String password) throws SQLException {
        Status status = Status.ERROR;

        try (PreparedStatement statement = connection.prepareStatement(MySqlQuery.AUTH_SQL)) {
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);

            ResultSet results = statement.executeQuery();
            status = results.next() ? Status.OK : Status.INVALID_LOGIN;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage() + e);
            status = Status.SQL_EXCEPTION;
        }

        return status;
    }

    /**
     * Checks if the provided username and password match what is stored
     * in the database. Must retrieve the salt and hash the password to
     * do the comparison.
     *
     * @param username - username to authenticate
     * @param password - password to authenticate
     * @return Status
     */
    public Status authenticateUser(String username, String password) {
        Status status = Status.ERROR;

        System.out.println("Authenticating user " + username + ".");

        try (Connection connection = db.getConnection()) {
            status = authenticateUser(connection, username, password);
        }
        catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            System.out.println(status.toString() + ex);
        }

        return status;
    }

    /**
     * When user login to hotelApp we record their login time and ip address.
     *
     * @param username
     * @param ipAddress
     * @return int - update result
     * @throws SQLException
     */
    public int addLogin(String username, String ipAddress) throws SQLException {
        String query = "INSERT INTO hotelapp_logins (username, ipaddress) VALUES (?, ?);";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, ipAddress);

        return statement.executeUpdate();
    }

    /**
     * Return the user login information to display on MyPage.
     *
     * @param username
     * @return ResultSet - user login data
     * @throws SQLException
     */
    public ResultSet getLogins(String username) throws SQLException {
        String query = "SELECT * FROM hotelapp_logins " +
                "WHERE username = ? ORDER BY date DESC LIMIT 2;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        return statement.executeQuery();
    }

    /**
     * Get hotel data from database for displaying hotel table.
     *
     * @param ds
     * @return ResultSet - hotel data and average rating
     * @throws SQLException
     */
    public ResultSet getHotels(DataSession ds) throws SQLException {
        String sortColumn = ds.getElement(ds.getElement("sortColumn"));
        String sortType = ds.getElement("sortType");
        String query = "SELECT h.hotelid, h.hotelname, h.city, h.latitude, h.longitude, r.rating " +
                "FROM hotelapp_hotels AS h LEFT JOIN ( " +
                "SELECT hotelid, ROUND(AVG(rating), 2) AS rating " +
                "FROM hotelapp_reviews WHERE status = 1 GROUP BY hotelid) AS r " +
                "ON r.hotelid = h.hotelid " +
                "WHERE h.city LIKE ? AND h.hotelname LIKE ? AND h.status = 1 " +
                "ORDER BY " + sortColumn + " " + sortType + ", h.hotelid ASC;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, ds.getElement("city") + "%");
        statement.setString(2, "%" + ds.getElement("hotelname") + "%");

        return statement.executeQuery();
    }

    /**
     * Return all cities of hotels in database.
     *
     * @return ResultSet - city names
     * @throws SQLException
     */
    public ResultSet getCitys() throws SQLException {
        String query = "SELECT city FROM hotelapp_hotels GROUP BY city ORDER BY city ASC;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);

        return statement.executeQuery();
    }

    /**
     * Return hotel/review detail base on id.
     * The data is used for card displaying.
     *
     * @param type
     * @param ds
     * @return ResultSet - hotel/review detail data
     * @throws SQLException
     */
    public ResultSet getDetails(String type, DataSession ds) throws SQLException {
        String query = "SELECT * FROM hotelapp_" + type + "s WHERE " + type + "id = ?;";

        if (type.equals("review")) {
            query = "SELECT r.*, h.hotelname FROM hotelapp_" + type + "s AS r " +
                    "INNER JOIN hotelapp_hotels AS h ON h.hotelid = r.hotelid " +
                    "WHERE " + type + "id = ?;";
        }

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, ds.getElement(type + "id"));

        return statement.executeQuery();
    }

    /**
     * Return the average rating of particular hotel.
     *
     * @param id
     * @return double - rating
     * @throws SQLException
     */
    public double getRating(String id) throws SQLException {
        String query = "SELECT ROUND(AVG(rating), 2) AS rating FROM hotelapp_reviews WHERE hotelid = ? AND status = 1;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);

        ResultSet result = statement.executeQuery();
        result.next();

        return result.getDouble("rating");
    }

    /**
     * Return hotel data.
     * Used for Expedia scraper and editing review.
     *
     * @param id
     * @return ResultSet - hotel data with Expedia data
     * @throws SQLException
     */
    public ResultSet getHotel(String id) throws SQLException {
        String query = "SELECT h.*, e.phone, e.photo " +
                "FROM hotelapp_hotels AS h " +
                "LEFT JOIN hotelapp_expedia AS e " +
                "ON e.hotelid = h.hotelid " +
                "WHERE h.hotelid = ?;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);

        return statement.executeQuery();
    }

    /**
     * Return Expedia data of a particular hotel.
     * If there is no result in database, we do the scraping on Expedia to insert data into database
     * , and recursively call this function again to get Expedia data from database.
     *
     * @param id
     * @return ResultSet - Expedia data
     * @throws SQLException
     */
    public ResultSet getExpedia(String id) throws SQLException {
        String query = "SELECT * FROM hotelapp_expedia WHERE hotelid = ?;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);
        ResultSet result = statement.executeQuery();

        if (!result.next()) {
            if (setExpedia(id) == 0) {
                return null;
            }
            else {
                return getExpedia(id);
            }
        }

        return statement.executeQuery();
    }

    /**
     * Call Expedia class to do scraping and insert data we get from Expedia into database.
     *
     * @param id
     * @return int - update result
     * @throws SQLException
     */
    private int setExpedia(String id) throws SQLException {
        String query = "INSERT INTO hotelapp_expedia (hotelid, phone, photo) VALUES (?, ?, ?);";

        Expedia expedia = new Expedia();
        Map<String, String> info = expedia.getInfo(id);

        if (info.size() == 0) {
            return 0;
        }

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);
        statement.setString(2, info.get("phone"));
        statement.setString(3, info.get("photo"));

        return statement.executeUpdate();
    }

    /**
     * Get review data from database for displaying review table.
     *
     * @param ds
     * @return ResultSet - review data
     * @throws SQLException
     */
    public ResultSet getReviews(DataSession ds) throws SQLException {
        String sortColumn = ds.getElement(ds.getElement("sortColumn"));
        String sortType = ds.getElement("sortType");
        String query = "SELECT r.reviewid, r.username, r.title, r.rating, r.date " +
                "FROM hotelapp_reviews AS r WHERE r.hotelid = ? AND r.status = 1 " +
                "ORDER BY " + sortColumn + " " + sortType + ", r.reviewid ASC;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, ds.getElement("hotelid"));

        return statement.executeQuery();
    }

    /**
     * Return number of likes of particular review.
     *
     * @param id
     * @return int - number of likes
     * @throws SQLException
     */
    public int getLikes(String id) throws SQLException {
        String query = "SELECT COUNT(*) AS likes FROM hotelapp_likereview WHERE reviewid = ? AND status = 1;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);

        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return result.getInt("likes");
        }

        return 0;
    }

    /**
     * Record liking reviews, saving hotels, checking Expedia links by user.
     * Maintaining status in 0 or 1 for data available.
     *
     * @param type
     * @param id
     * @param username
     * @return int - update result
     * @throws SQLException
     */
    public int setLike(String type, String id, String username) throws SQLException {
        String query = "INSERT INTO hotelapp_like" + type + " (" + type + "id, username) VALUES (?, ?)";

        if (type.equals("expedia")) {
            query = "INSERT INTO hotelapp_like" + type + " (hotelid, username, link) VALUES (?, ?, ?)";
        }

        if (checkLike(type, id, username) != 0) {
            query = "UPDATE hotelapp_like" + type
                    + " SET status = ABS(status - 1), date = CURRENT_TIMESTAMP"
                    + " WHERE " + type + "id = ? AND username = ?;";

            if (type.equals("expedia")) {
                query = "UPDATE hotelapp_like" + type
                        + " SET status = ABS(status - 1), date = CURRENT_TIMESTAMP"
                        + " WHERE hotelid = ? AND username = ?;";
            }
        }

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);
        statement.setString(2, username);

        if (type.equals("expedia") && query.startsWith("INSERT")) {
            Expedia expedia = new Expedia();
            statement.setString(3, expedia.getExpediaLink(id));
        }

        return statement.executeUpdate();
    }

    /**
     * Support setLike function, check if a user has already liked a review/hotel/expedia.
     * If yes, we do update in setLike function.
     *
     * @param type
     * @param id
     * @param username
     * @return int - data count
     * @throws SQLException
     */
    private int checkLike(String type, String id, String username) throws SQLException {
        String query = "SELECT COUNT(*) AS likes FROM hotelapp_like" + type
                + " WHERE " + type + "id = ? AND username = ?;";

        if (type.equals("expedia")) {
            query = "SELECT COUNT(*) AS likes FROM hotelapp_like" + type
                    + " WHERE hotelid = ? AND username = ?;";
        }

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);
        statement.setString(2, username);

        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return result.getInt("likes");
        }

        return 0;
    }

    /**
     * Return review data and hotel name for editing review modal.
     *
     * @param reviewId
     * @return ResultSet - review data and hotel name
     * @throws SQLException
     */
    public ResultSet getReview(String reviewId) throws SQLException {
        String query = "SELECT h.hotelname, r.title, r.rating, r.recommend, r.review " +
                "FROM hotelapp_reviews AS r INNER JOIN hotelapp_hotels AS h " +
                "ON h.hotelid = r.hotelid WHERE r.reviewid = ? AND r.status = 1;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, reviewId);

        return statement.executeQuery();
    }

    /**
     * Adding a new review to a particular hotel, or editing a particular review in database.
     *
     * @param inputs
     * @return int - update result
     * @throws SQLException
     */
    public int putReview(DataSession inputs) throws SQLException {
        String query;

        if (inputs.getElement("type").equals("add")) {
            query = "INSERT INTO hotelapp_reviews (" +
                "title, rating, recommend, review, hotelid, username" +
                ") VALUES (?, ?, ?, ?, ?, ?);";
        }
        else if (inputs.getElement("type").equals("edit")) {
            query = "UPDATE hotelapp_reviews SET date = CURRENT_TIMESTAMP " +
                    ", title = ?, rating = ?, recommend = ?, review = ? " +
                    "WHERE reviewid = ? AND username = ?;";
        }
        else if (inputs.getElement("type").equals("delete")) {
            return deleteReview(inputs);
        }
        else {
            return 0;
        }

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, inputs.getElement("title"));
        statement.setInt(2, Integer.parseInt(inputs.getElement("rating")));
        statement.setInt(3, Integer.parseInt(inputs.getElement("recommend")));
        statement.setString(4, inputs.getElement("review"));
        statement.setString(5, inputs.getElement("id"));
        statement.setString(6, inputs.getElement("username"));

        return statement.executeUpdate();
    }

    /**
     * Deleting a review by setting status into 0 to disable data.
     * If a review has been deleted, we clear all likes data fo this review.
     * Concept: We do not allow a user to remove any data from database, only a DBA can do that.
     *
     * @param inputs
     * @return int - update result
     * @throws SQLException
     */
    private int deleteReview(DataSession inputs) throws SQLException {
        String query = "UPDATE hotelapp_reviews SET status = 0, date = CURRENT_TIMESTAMP " +
                "WHERE reviewid = ? AND username = ?;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, inputs.getElement("id"));
        statement.setString(2, inputs.getElement("username"));

        clearLikes(inputs.getElement("id"));

        return statement.executeUpdate();
    }

    /**
     * Displaying "who liked this review".
     *
     * @param reviewId
     * @return ResultSet - review likes data
     * @throws SQLException
     */
    public ResultSet showReviewLikes(String reviewId) throws SQLException {
        String query = "SELECT * FROM hotelapp_likereview " +
                "WHERE reviewid = ? AND status = 1 ORDER BY date DESC;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, reviewId);

        return statement.executeQuery();
    }

    /**
     * Return an API key for particular type of usage. (ex: google place api)
     * It is important to store API key in database for an open source app.
     *
     * @param hostname
     * @param type
     * @return String - API key
     * @throws SQLException
     */
    public String getApiKey(String hostname, String type) throws SQLException {
        String query = "SELECT token FROM hotelapp_apikeys WHERE hostname = ? AND type = ?;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, hostname);
        statement.setString(2, type);

        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return result.getString("token");
        }

        return "";
    }

    /**
     * Return a status of saving hotels, checking Expedia links for button displaying.
     *
     * @param type
     * @param hotelId
     * @param username
     * @return int - liked data of hotel/expedia
     * @throws SQLException
     */
    public int getSaved(String type, String hotelId, String username) throws SQLException {
        String query = "SELECT * FROM hotelapp_like" + type + " WHERE hotelid = ? AND username = ?;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, hotelId);
        statement.setString(2, username);

        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return result.getInt("status");
        }

        return 0;
    }

    /**
     * Return reviewed data, saved hotels, liked reviews, checked Expedia links of user for MyPage.
     *
     * @param type
     * @param username
     * @return ResultSet - data of user
     * @throws SQLException
     */
    public ResultSet getMyData(List<String> type, String username) throws SQLException {
        String query = "SELECT * FROM hotelapp_" + type.get(0) + " AS main " +
                "LEFT JOIN hotelapp_" + type.get(1) + " AS sub " +
                "ON sub." + type.get(2) + " = main." + type.get(2) +
                " WHERE main.username = ? AND main.status = 1 ORDER BY main.date DESC;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        return statement.executeQuery();
    }

    /**
     * Clear particular field of user data.
     * If clearing MyReviews, we clear all likes data of these reviews.
     *
     * @param type
     * @param username
     * @return int - update result
     * @throws SQLException
     */
    public int clearMyData(List<String> type, String username) throws SQLException {
        if (type.get(0).equals("reviews")) {
            clearLikesByMe(username);
        }

        String query = "UPDATE hotelapp_" + type.get(0) +
                " SET status = 0 WHERE username = ? AND status = 1;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        return statement.executeUpdate();
    }

    /**
     * Clear all likes of particular review.
     *
     * @param reviewId
     * @return int - update result
     * @throws SQLException
     */
    private int clearLikes(String reviewId) throws SQLException {
        String query = "UPDATE hotelapp_likereview " +
                "SET status = 0 WHERE reviewid = ? AND status = 1;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, reviewId);

        return statement.executeUpdate();
    }

    /**
     * Clear all likes of a user's reviews.
     *
     * @param username
     * @return int - update result
     * @throws SQLException
     */
    private int clearLikesByMe(String username) throws SQLException {
        String clearLikes = "UPDATE hotelapp_likereview " +
                "SET status = 0 " +
                "WHERE reviewid IN (" +
                "SELECT reviewid FROM hotelapp_reviews " +
                "WHERE username = ? AND status = 1" +
                ") AND status = 1;";

        Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(clearLikes);
        statement.setString(1, username);

        return statement.executeUpdate();
    }

    /**
     * Return the available status of the username.
     *
     * @param username
     * @return Status - username available
     */
    private Status checkUsername(String username) {
        Status status = Status.USERNAME_OK;

        Pattern p = Pattern.compile("[^a-z\\d@_\\-]+", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(username);

        if (m.find()) {
            status = Status.USERNAME_NOT_GOOD;
        }

        return status;
    }

    /**
     * To check if the password is at least 8 characters and contains at least 1 special character.
     *
     * @param password
     * @return Status
     */
    private Status checkPassword(String password) {
        Status status = Status.PASSWORD_OK;

        Pattern pUpper = Pattern.compile("[A-Z]+");
        Matcher mUpper = pUpper.matcher(password);

        Pattern pLower = Pattern.compile("[a-z]+");
        Matcher mLower = pLower.matcher(password);

        Pattern pDigit = Pattern.compile("[\\d]+");
        Matcher mDigit = pDigit.matcher(password);

        Pattern pSpecial = Pattern.compile("[!@#$%&*_\\-]+");
        Matcher mSpecial = pSpecial.matcher(password);

        Pattern pNo = Pattern.compile("[^a-z\\d!@#$%&*_\\-]+", Pattern.CASE_INSENSITIVE);
        Matcher mNo = pNo.matcher(password);

        if (password.length() < 8 || !mUpper.find() || !mLower.find()
                || !mDigit.find() || !mSpecial.find() || mNo.find()) {
            status = Status.PASSWORD_NOT_GOOD;
        }

        return status;
    }

    /**
     * To load json files into database, only do when the first time server starts.
     *
     * @param connection
     * @throws SQLException
     */
    private void loadData(Connection connection) throws SQLException {
        // load all hotel info and reviews into data structure
        hdata = new ThreadSafeHotelData();
        builder = new HotelDataBuilder(hdata, THREAD);
        String inputHotelFile = "input" + File.separator + "hotels.json";
        builder.loadHotelInfo(inputHotelFile);
        builder.loadReviews(Paths.get("input" + File.separator + "reviews"));
        System.out.println("Completed loading from json file to data structure.");

        // load data into database
        Set<String> usernames = new TreeSet<>();
        for (String id : hdata.getHotels()) {
            Hotel hotel = hdata.getHotel(id);

            try (PreparedStatement statement = connection.prepareStatement(MySqlQuery.INSERT_HOTEL_SQL)) {
                // hotelid, hotelname, city, address, latitude, longitude
                statement.setString(1, id);
                statement.setString(2, hotel.getHotelName());
                statement.setString(3, hotel.getAddress().getCity());
                statement.setString(4, hotel.getAddress().toString());
                statement.setDouble(5, hotel.getAddress().getLatitude());
                statement.setDouble(6, hotel.getAddress().getLongitude());
                statement.executeUpdate();
            }

            Set<Review> reviews;
            if ((reviews = hdata.getHotelReviews(id)) != null) {
                for (Review review : reviews) {
                    String username;
                    if ((username = review.getUsername()) != null && !username.equals("")) {
                        try (PreparedStatement statement = connection.prepareStatement(MySqlQuery.INSERT_REVIEW_SQL)) {
                            // hotelid, username, rating, recommend, title, review, date
                            statement.setString(1, id);
                            statement.setString(2, username);
                            statement.setInt(3, review.getRating());
                            statement.setInt(4, (review.getIsRecom()) ? 1 : 0);
                            statement.setString(5, review.getReviewTitle());
                            statement.setString(6, review.getReview());
                            statement.setTimestamp(7, new Timestamp(review.getDate().getTime()));
                            statement.executeUpdate();
                        }

                        usernames.add(username);
                    }
                }
            }
        }

        // load usernames into database
        for (String username : usernames) {
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);
            String randomPassword = encodeHex(saltBytes, 32);
            registerUser(username, "@" + randomPassword);
        }

        System.out.println("Completed loading from data structure to database.");
    }
}
