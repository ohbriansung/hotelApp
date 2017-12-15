package hotelapp;

import data.DatabaseHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class TouristAttractionFinder implement Iterator to store data get from google's API.
 *
 * @author BrianSung
 */
public class TouristAttractionFinder implements Iterator<TouristAttraction>{

    private static final String HOST = "maps.googleapis.com";
    private static final String PLACE_API_PATH = "/maps/api/place/textsearch/json?";
    private static final String PHOTO_API_PATH = "/maps/api/place/photo?";
    private static final int PORT = 443;
    private static int index;
    private List<TouristAttraction> taList;

    /**
     * Constructor for TouristAttractionFinder
     */
    public TouristAttractionFinder() {
        this.taList = new ArrayList<>();
        this.index = 0;
    }

    /**
     * Creates a secure socket to communicate with Google Place API server that
     * provides Places API, sends a GET request (to find attractions close to
     * the hotel within a given radius), and parse the response into data structure.
     */
    public void fetchAttractions(String hotelId) throws IOException, SQLException {

        try {
            String path = getPathAndQuery("Google", "place", "place", hotelId);

            // parse into json and put into TreeSet and Hashmap data structure
            String result = getHtmlResponse(path);
            JSONParser parser = new JSONParser();
            JSONObject jObj = (JSONObject) parser.parse(result.substring(result.indexOf("{"))); // remove header
            JSONArray jArr = (JSONArray) jObj.get("results");
            for (JSONObject obj : (Iterable<JSONObject>) jArr) {
                String id = obj.get("id").toString();
                String name = obj.get("name").toString();
                double rating = (obj.get("rating") == null) ? 0.0 : Double.parseDouble(obj.get("rating").toString());
                String address = obj.get("formatted_address").toString();
                String photoUrl = getPhotoUrl((JSONArray) obj.get("photos"));
                List<String> types = getTypes((JSONArray) obj.get("types"));

                taList.add(new TouristAttraction(id, name, rating, address, photoUrl, types));
            }
        }
        catch (ParseException e) {
            System.out.println("Can not parse a given string into Json.");
        }
    }

    /**
     * Creates a secure socket to communicate with Google's API.
     *
     * @param path
     * @return String - html response from Google's API
     * @throws IOException
     */
    private static String getHtmlResponse(String path) throws IOException {
        // connect with host
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(HOST, PORT);

        // send a request to the server through output stream
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        String requestString = getRequest(HOST, path);
        pw.println(requestString);

        // use input stream to read server's response
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String str;
        while ((str = in.readLine()) != null) {
            sb.append(str).append(" ");
        }

        // take the body part
        return sb.toString();
    }

    /**
     * A method that creates a GET request for the given host and resource
     * @param host
     * @param pathResourceQuery
     * @return String - HTTP GET request returned as a string
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
                // request
                + "Host: " + host + System.lineSeparator() // Host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator() // make sure the server closes the
                // connection after we fetch one page
                + System.lineSeparator();
        return request;
    }

    /**
     * Access Google Place API to get the photo url of particular attraction.
     *
     * @param photos
     * @return String - photo url
     * @throws ParseException
     * @throws IOException
     * @throws SQLException
     */
    private String getPhotoUrl(JSONArray photos) throws ParseException, IOException, SQLException {
        String url = "https://i.imgur.com/9OueEQZ.png";

        if (photos != null) {
            JSONObject obj = (JSONObject) photos.get(0);
            Object photoReference = obj.get("photo_reference");
            Object maxHeight = obj.get("height");
            Object maxWidth = obj.get("width");

                    StringBuilder query = new StringBuilder();
            query.append("photoreference=").append(photoReference);
            if (maxHeight != null) {
                query.append("&maxheight=").append(maxHeight);
            }
            if (maxWidth != null) {
                query.append("&maxwidth=").append(maxWidth);
            }

            String path = getPathAndQuery("Google", "place2", "photo", query.toString());
            String result = getHtmlResponse(path);

            if (result.startsWith("HTTP/1.1 302 Found")) {
                Pattern p = Pattern.compile(".*?\\sLocation:\\s([^\\s]+)\\s");
                Matcher m = p.matcher(result);
                if (m.find()) {
                    url = m.group(1);
                }
            }
        }

        return url;
    }

    /**
     * Return path and query for different API usages.
     *
     * @param hostname
     * @param type
     * @param get
     * @param hotelIdOrQuery
     * @return String - path and query
     * @throws SQLException
     */
    private String getPathAndQuery(String hostname, String type, String get, String hotelIdOrQuery) throws SQLException {
        StringBuilder sb = new StringBuilder();
        DatabaseHandler dbhandler = DatabaseHandler.getInstance();
        String key = dbhandler.getApiKey(hostname, type);

        if (get.equals("place")) {
            ResultSet result = dbhandler.getHotel(hotelIdOrQuery);

            if (result.next()) {
                sb.append(PLACE_API_PATH);
                sb.append("query=tourist%20attractions+in+");
                sb.append(result.getString("city").replaceAll(" ", "%20"));
                sb.append("&location=").append(result.getString("latitude"));
                sb.append(",").append(result.getString("longitude"));
                sb.append("&language=en");
                sb.append("&radius=").append((int) (1609.344 * 3));
            }
        }
        else if (get.equals("photo")) {
            sb.append(PHOTO_API_PATH);
            sb.append(hotelIdOrQuery);
        }

        sb.append("&key=").append(key);

        return sb.toString();
    }

    /**
     * Parsing JSONArray to get the types of particular attraction.
     *
     * @param array
     * @return List - types of attraction
     */
    private List<String> getTypes(JSONArray array) {
        List<String> types = new ArrayList<>();

        if (array != null) {
            for (String type : (Iterable<String>) array) {
                types.add(type);
            }
        }

        return types;
    }

    /**
     * To check if there is a next element.
     *
     * @return boolean
     */
    @Override
    public boolean hasNext() {
        if (this.index + 1 <= this.taList.size()) {
            return true;
        }
        return false;
    }

    /**
     * To return a TouristAttraction every time we access the data.
     *
     * @return TouristAttraction
     */
    @Override
    public TouristAttraction next() {
        return taList.get(this.index++);
    }

    public void startOver() {
        this.index = 0;
    }
}
