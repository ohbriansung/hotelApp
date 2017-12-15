package data;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expedia for scarping data from Expedia website.
 */
public class Expedia {
    private final static String HOST = "www.expedia.com";
    private final static String FUNCTION = "Hotel-Information";
    private final static int PORT = 443;
    private DatabaseHandler dbHandler;

    /**
     * Constructor.
     */
    public Expedia() {
        this.dbHandler = DatabaseHandler.getInstance();
    }

    /**
     * Return an link to access Expedia page of particular hotel.
     * For both user accessing and Expedia scraper.
     *
     * @param hotelId
     * @return String - an Expedia link
     */
    public String getExpediaLink(String hotelId) {
        StringBuilder sb = new StringBuilder();

        // https://www.expedia.com/h11793.Hotel-Information
        sb.append("https://").append(HOST).append("/h");
        sb.append(hotelId).append(".").append(FUNCTION);
        return sb.toString();
    }

    /**
     * Return phone data and photo link scraped from Expedia.
     *
     * @param hotelId
     * @return Map - Expedia data map
     * @throws SQLException
     */
    public Map<String, String> getInfo(String hotelId) throws SQLException {
        Map<String, String> result = new HashMap<>();

        String path = getExpediaLink(hotelId).replaceAll("https://" + HOST, "");
        String html = getHtmlResponse(path);

        StringBuilder sb = new StringBuilder();
        sb.append(".*?itemprop=\"telephone\">([^<]*)<"); // phone
        sb.append(".*?<figure.*?src=\"([^\"]*)\"[^>]*Guestroom"); // photo
        Pattern p = Pattern.compile(sb.toString());
        Matcher m = p.matcher(html);

        if (m.find()) {
            result.put("phone", m.group(1));
            result.put("photo", m.group(2));
        }
        else {
            result.put("phone", "Not Available");
            result.put("photo", "https://i.imgur.com/9OueEQZ.png");
        }

        return result;
    }

    /**
     * Creates a secure socket to communicate with Expedia.
     *
     * @return String - html response from Expedia
     */
    private String getHtmlResponse(String path) {

        try {
            // connect with host -Expedia
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket socket = factory.createSocket(HOST, PORT);

            // send a request to the server through output stream
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            String requestString = getRequest(path);
            pw.println(requestString);

            // use input stream to read server's response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }

            if (sb.toString().startsWith("HTTP/1.1 301 Moved")) {
                Pattern p = Pattern.compile(".*?https://www.expedia.com([^\\s:]*):\\s");
                Matcher m = p.matcher(sb.toString());
                if (m.find()) {
                    String newPath = m.group(1).replaceAll("Server", "");
                    return getHtmlResponse(newPath);
                }
                else {
                    return "";
                }
            }
            else if (sb.toString().startsWith("HTTP/1.1 404 Not Found")) {
                return "";
            }

            // take the body part
            return sb.substring(sb.indexOf("<body"));
        }
        catch (IOException e) {
            System.out.println(e);
        }

        return "";
    }

    /**
     * A method that creates a GET request for the given host and resource.
     *
     * @param pathResourceQuery
     * @return String - HTTP GET request returned as a string
     */
    private String getRequest(String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
                // request
                + "Host: " + HOST + System.lineSeparator() // Host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator() // make sure the server closes the
                // connection after we fetch one page
                + System.lineSeparator();
        return request;
    }
}
