package servlet;

import data.DataSession;
import data.Expedia;
import hotelapp.TouristAttraction;
import hotelapp.TouristAttractionFinder;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * HotelDetailServlet class for hotel/review/attraction detail card.
 */
public class HotelDetailServlet extends BaseServlet {

    /**
     * doPost method to display hotel/review/attraction detail card in both Hotels page and MyPage.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> types = getTypes();
        String type = StringEscapeUtils.escapeHtml4(request.getParameter("type"));
        String username = getUsername(request);
        StringBuilder sb = new StringBuilder();
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        if (username != null && types.contains(type)) {
            checkDataSession(session, type);
            DataSession ds = (DataSession) session.getAttribute(type);
            ds.setElement(type + "id", request.getParameter("id"));

            try {
                if (type.equals(types.get(2))) {
                    sb.append(touristAttractionDetail(type, ds));
                }
                else {
                    ResultSet result = dbhandler.getDetails(type, ds);
                    if (result.next()) {
                        sb.append("<div class=\"card mb-4\">");

                        if (type.equals(types.get(0))) {
                            sb.append(head(type, result.getString("hotelname")
                                    , request.getParameter("id"), username));
                            sb.append(body(result, request.getParameter("id"), username));
                        } else if (type.equals(types.get(1))) {
                            sb.append(reviewHead(type, result));
                            sb.append(reviewBody(type, username, result));
                        }

                        sb.append("</div>");
                    }
                }

                out.println(sb.toString());
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Construct html query for head of hotel detail card in both Hotels page and MyPage.
     *
     * @param type
     * @param name
     * @param hotelId
     * @param username
     * @return String - Html query of hotel card head
     * @throws SQLException
     */
    private String head(String type, String name, String hotelId, String username) throws SQLException {
        StringBuilder sb = new StringBuilder();
        int status = dbhandler.getSaved(type, hotelId, username);
        String saved = "<span class=\"fa fa-" + (status == 1 ? "heart" : "heart-o")
                + " recommend clickable\" onclick=\"like('" + type + "', '" + hotelId + "')\"></span>";

        sb.append("<div class=\"card-header\">");
        sb.append("<span class=\"badge badge-primary\">" + type + "</span> ");
        sb.append(name).append(saved);
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Construct html query for body of hotel detail card in both Hotels page and MyPage.
     *
     * @param result
     * @param hotelId
     * @param username
     * @return String - Html query of hotel card body
     * @throws SQLException
     */
    private String body(ResultSet result, String hotelId, String username) throws SQLException {
        StringBuilder sb = new StringBuilder();
        Expedia expedia = new Expedia();
        String expediaLink = expedia.getExpediaLink(hotelId);
        ResultSet expediaInfo = dbhandler.getExpedia(hotelId);
        expediaInfo.next();
        double rating = dbhandler.getRating(hotelId);
        int expediaStatus = dbhandler.getSaved("expedia", hotelId, username);

        sb.append("<div class=\"card-body\">");
        sb.append("<img class=\"card-img\" style=\"background-image: ");
        sb.append("url('" + expediaInfo.getString("photo") + "');\" src=\"frontend/img/incognito.png\" alt=\"Image\">");
        sb.append("<p></p>");
        sb.append("<a>City: " + result.getString("city") + "</a><br/>");
        sb.append("<a onclick=\"thisGoogleMap()\" data-toggle=\"modal\" data-target=\"#addressModal\">");
        sb.append("Address: <span class=\"clickableA\">" + result.getString("address") + "</span></a><br/>");
        sb.append("<a>Phone: " + expediaInfo.getString("phone") + "</a><br/>");
        sb.append("<a>Rating: " + starts(rating) + " " + (rating == 0 ? "null" : "" + rating) + " / 5.0</a>");
        sb.append("<hr>");

        // review button
        sb.append("<button id=\"btnReviews\" class=\"button btn-success clickable\" ");
        sb.append("onClick=\"tableTitle('Reviews');window.location.href='#page-top';\">");
        sb.append("<i class=\"fa fa-files-o\" aria-hidden=\"true\"></i> Reviews</button> ");

        // attraction button
        sb.append("<button id=\"btnAttractions\" class=\"button btn-danger clickable\" ");
        sb.append("onClick=\"tableTitle('Attractions');window.location.href='#page-top';\">");
        sb.append("<i class=\"fa fa-map-marker\" aria-hidden=\"true\"></i> Attractions</button> ");

        // expedia button
        sb.append("<button id=\"btnExpedia\" class=\"button btn-info clickable\" onClick=\"window.open('" + expediaLink + "');");
        sb.append((expediaStatus == 1 ? "" : "like('expedia', '" + hotelId + "');") + "\">");
        sb.append("<i class=\"fa fa-external-link\" aria-hidden=\"true\"></i> Expedia</button> ");

        // add review button
        sb.append("<button id=\"addReview2\" class=\"button btn-primary\" ");
        sb.append("style=\"display: none;\" data-toggle=\"modal\" data-target=\"#reviewModal\" ");
        sb.append("onclick=\"reviewmodal('Add')\"><i class=\"fa fa-pencil\" aria-hidden=\"true\"></i> AddReview</button>");

        // data for google map
        sb.append("<input type=\"hidden\" id=\"gmdata\" lat=\"" + result.getString("latitude") + "\" ");
        sb.append("lon=\"" + result.getString("longitude") + "\">");
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Construct html query for head of review detail card in both Hotels page and MyPage.
     *
     * @param type
     * @param result
     * @return String - Html query of review card head
     * @throws SQLException
     */
    private String reviewHead(String type, ResultSet result) throws SQLException {
        StringBuilder sb = new StringBuilder();
        String recommend = "<span class=\"fa fa-" + (result.getInt("recommend") == 0 ? "frown-o" : "smile-o")
                + " recommend\"></span>";

        sb.append("<div class=\"card-header\">");
        sb.append("<span class=\"badge badge-success\">" + type + "</span> by ");
        sb.append(result.getString("username")).append(recommend);
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Construct html query for body of review detail card in both Hotels page and MyPage.
     *
     * @param type
     * @param username
     * @param result
     * @return String - Html query of review card body
     * @throws SQLException
     */
    private String reviewBody(String type, String username, ResultSet result) throws SQLException {
        int likes = dbhandler.getLikes(result.getString("reviewid"));

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card-body\">");
        sb.append("<h2>").append(result.getString("title")).append("</h2>");
        sb.append("<a>Hotel: " + result.getString("hotelname") + "</a><br/>");
        sb.append("<a>Rating: " + starts(result.getDouble("rating"))).append(" ");
        sb.append(result.getString("rating") + " / 5</a><br/>");
        sb.append("<a>Review: </a><br/>");
        sb.append("<div class=\"reviewContext\">" + result.getString("review") + "</div>");
        sb.append("<span class=\"small float-right text-muted\">Posted: ");
        sb.append(result.getTimestamp("date") +"</span>");
        sb.append("<hr>");

        // number of likes
        sb.append("<label class=\"clickable like\" data-toggle=\"modal\" data-target=\"#likeModal\" ");
        sb.append("onclick=\"showlike('review')\">" + likes + "</label> ");

        // like review button
        sb.append("<button class=\"button btn-primary clickable\" ");
        sb.append("onClick=\"like('" + type + "', '" + result.getString("reviewid") + "')\">");
        sb.append("<i class=\"fa fa-thumbs-o-up\" aria-hidden=\"true\"></i> Like</button>");

        if (result.getString("username").equals(username)) {
            // edit review button
            sb.append(" <button id=\"btnEdit\" class=\"button btn-dark clickable\" ");
            sb.append("data-toggle=\"modal\" data-target=\"#reviewModal\" onClick=\"reviewmodal('Edit')\">");
            sb.append("<i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i> Edit</button>");
        }

        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Return html query of attraction detail card in Hotels page.
     *
     * @param type
     * @param ds
     * @return String - Html query of attraction card
     */
    private String touristAttractionDetail(String type, DataSession ds) {
        StringBuilder sb = new StringBuilder();
        TouristAttractionFinder taFinder = (TouristAttractionFinder) ds.getObject("taFinder");

        if (taFinder != null) {
            int count = 0;
            taFinder.startOver();
            TouristAttraction ta = null;

            while (taFinder.hasNext()){
                count++;
                if (count == Integer.parseInt(ds.getElement(type + "id"))) {
                    ta = taFinder.next();
                    break;
                }
                else {
                    taFinder.next();
                }
            }

            if (ta != null) {
                sb.append("<div class=\"card my-4\">");
                sb.append(attracitonHead(type, ta));
                sb.append(attracitonBody(ta));
                sb.append("</div>");
            }
        }

        return sb.toString();
    }

    /**
     * Construct html query for head of attraction detail card in Hotels page.
     *
     * @param type
     * @param ta
     * @return String - Html query of attraction card head
     */
    private String attracitonHead(String type, TouristAttraction ta) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card-header\">");
        sb.append("<span class=\"badge badge-danger\">" + type + "</span> ");
        sb.append(ta.getName());
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Construct html query for body of attraction detail card in Hotels page.
     *
     * @param ta
     * @return String - Html query of attraction card body
     */
    private String attracitonBody(TouristAttraction ta) {
        StringBuilder sb = new StringBuilder();

        sb.append("<div class=\"card-body\">");
        sb.append("<img class=\"card-img\" style=\"background-image: ");
        sb.append("url('" + ta.getPhotoUrl() + "');\" src=\"frontend/img/incognito.png\" alt=\"Image\">");
        sb.append("<p></p>");
        sb.append("<a>Address: " + ta.getAddress() + "</a><br/>");
        sb.append("<a>Rating: " + starts(ta.getRating()) + " " + ta.getRating() + " / 5.0</a>");
        sb.append("<hr>");
        for (String type : ta.getTypes()) {
            sb.append("<span class=\"badge badge-warning\">" + type + "</span> ");
        }
        sb.append("</div>");

        return sb.toString();
    }
}