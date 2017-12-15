package data;

/**
 * MySqlQuery to store static MySql queries.
 *
 * @author BrianSung
 */
public class MySqlQuery {

    /** Used to determine if necessary tables are provided. */
    public static final String TABLES_SQL =
            "SELECT COUNT(table_name) AS num " +
                    "FROM information_schema.tables " +
                    "WHERE table_name LIKE 'hotelapp_%';";

    /** Used to create user table for this project. */
    public static final String CREATE_USER_TABLE_SQL =
            "CREATE TABLE hotelapp_users (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL, " +
                    "status TINYINT(1) DEFAULT 1);";

    /** Used to create hotel table for this project. */
    public static final String CREATE_HOTEL_TABLE_SQL =
            "CREATE TABLE hotelapp_hotels (" +
                    "hotelid VARCHAR(32) PRIMARY KEY, " +
                    "hotelname VARCHAR(128) NOT NULL UNIQUE, " +
                    "city VARCHAR(32) NOT NULL, " +
                    "address VARCHAR(128) NOT NULL UNIQUE, " +
                    "latitude DOUBLE NOT NULL, " +
                    "longitude DOUBLE NOT NULL, " +
                    "status TINYINT(1) DEFAULT 1);";

    /** Used to create review table for this project. */
    public static final String CREATE_REVIEW_TABLE_SQL =
            "CREATE TABLE hotelapp_reviews (" +
                    "reviewid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "hotelid VARCHAR(32) NOT NULL, " +
                    "username VARCHAR(32) NOT NULL, " +
                    "rating INTEGER DEFAULT 5, " +
                    "recommend TINYINT(1) DEFAULT 1, " +
                    "title VARCHAR(128), " +
                    "review TEXT, " +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "status TINYINT(1) DEFAULT 1);";

    /** Used to create Expedia data table for this project. */
    public static final String CREATE_EXPEDIA_TABLE_SQL =
            "CREATE TABLE hotelapp_expedia ( " +
                    "hotelid VARCHAR(32) PRIMARY KEY" +
                    ", phone VARCHAR(32), photo VARCHAR(256));";

    /** Used to create liked reviews table for this project. */
    public static final String CREATE_LIKEREVIEW_TABLE_SQL =
            "CREATE TABLE hotelapp_likereview (" +
                    "reviewid INTEGER NOT NULL" +
                    ", username VARCHAR(32) NOT NULL" +
                    ", date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                    ", status TINYINT(1) DEFAULT 1 NOT NULL);";

    /** Used to create saved hotels table for this project. */
    public static final String CREATE_LIKEHOTEL_TABLE_SQL =
            "CREATE TABLE hotelapp_likehotel (" +
                    "hotelid VARCHAR(32) NOT NULL" +
                    ", username VARCHAR(32) NOT NULL" +
                    ", date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                    ", status TINYINT(1) DEFAULT 1 NOT NULL);";

    /** Used to create checked Expedia links table for this project. */
    public static final String CREATE_LIKEEXPEDIA_TABLE_SQL =
            "CREATE TABLE hotelapp_likeexpedia (" +
                    "hotelid VARCHAR(32) NOT NULL" +
                    ", username VARCHAR(32) NOT NULL" +
                    ", link VARCHAR(256)" +
                    ", date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                    ", status TINYINT(1) DEFAULT 1 NOT NULL);";

    /** Used to create API keys table for this project. */
    public static final String CREATE_APIKEYS_TABLE_SQL =
            "CREATE TABLE hotelapp_apikeys (" +
                    "hostname VARCHAR(32) NOT NULL" +
                    ", type VARCHAR(32) NOT NULL" +
                    ", token VARCHAR(64) NOT NULL);";

    /** Used to create user login data table for this project. */
    public static final String CREATE_LOGINS_TABLE_SQL =
            "CREATE TABLE hotelapp_logins (" +
                    "username VARCHAR(32) NOT NULL" +
                    ", ipaddress VARCHAR(64)" +
                    ", date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";

    /** Used to insert a new user into the database. */
    public static final String REGISTER_SQL =
            "INSERT INTO hotelapp_users (username, password, usersalt) " +
                    "VALUES (?, ?, ?);";

    /** Used to insert a new hotel into the database. */
    public static final String INSERT_HOTEL_SQL =
            "INSERT INTO hotelapp_hotels (hotelid, hotelname, city, address, latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?);";

    /** Used to insert a new review into the database. */
    public static final String INSERT_REVIEW_SQL =
            "INSERT INTO hotelapp_reviews (hotelid, username, rating, recommend, title, review, date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

    /** Used to determine if a username already exists. */
    public static final String USER_SQL =
            "SELECT username FROM hotelapp_users WHERE username = ?";

    /** Used to retrieve the salt associated with a specific user. */
    public static final String SALT_SQL =
            "SELECT usersalt FROM hotelapp_users WHERE username = ? AND status = 1";

    /** Used to authenticate a user. */
    public static final String AUTH_SQL =
            "SELECT username FROM hotelapp_users " +
                    "WHERE username = ? AND password = ? AND status = 1";
}
