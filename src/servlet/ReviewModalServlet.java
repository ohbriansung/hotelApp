package servlet;

import data.DataSession;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReviewModalServlet class to handle adding/editing/deleting reviews in both Hotels page and MyPage.
 */
public class ReviewModalServlet extends BaseServlet {

    /**
     * doGet method to display the body and footer of review modal.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = StringEscapeUtils.escapeHtml4(request.getParameter("type"));
        String id = StringEscapeUtils.escapeHtml4(request.getParameter("id"));
        StringBuilder sb = new StringBuilder();
        PrintWriter out = response.getWriter();

        if (getUsername(request) != null) {
            try {
                sb.append(body(id, type));
                sb.append(footer(type));
                out.println(sb.toString());
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Construct the html query for the body of review modal.
     *
     * @param id
     * @param type
     * @return String - Html query of the body
     * @throws SQLException
     */
    private String body(String id, String type) throws SQLException {
        StringBuilder sb = new StringBuilder();
        String hotelName = "";
        String reviewTitle = "";
        int reviewRating = 5;
        String reviewRecommend1 = "checked";
        String reviewRecommend0 = "";
        String reviewContent = "";
        ResultSet result = getResult(id, type);

        if (result != null && result.next()) {
            hotelName = result.getString("hotelname");

            if (type.equals("Edit")) {
                reviewTitle = result.getString("title");
                reviewRating = result.getInt("rating");
                if (result.getInt("recommend") == 0){
                    reviewRecommend1 = "";
                    reviewRecommend0 = "checked";
                }
                reviewContent = result.getString("review");
            }
        }

        sb.append("<div class=\"modal-body\">");

        // table head and hotel name
        sb.append("<table style=\"width: 100%;\"><tr><th width=\"30%\"></th>");
        sb.append("<th width=\"50%\"></th><th width=\"20%\"></th></tr>");
        sb.append("<tr><td colspan=\"3\"><h4>" + hotelName + "</h4></td></tr>");

        // title
        sb.append("<tr><td colspan=\"3\"><p><input type=\"text\" id=\"reviewTitle\" maxlength=\"50\" class=\"form-control\" ");
        sb.append("placeholder=\"Title\" value=\"" + reviewTitle + "\" onkeyup=\"contentChange('Title')\">");
        sb.append("<span id=\"countTitle\" class=\"small\" style=\"position: absolute; right: 25px; bottom: 230px;\"");
        sb.append(">50</span></p></td></tr>");

        // rating
        sb.append("<tr><td><p>Rating: <select id=\"reviewRating\">");
        for (int i = 5; i >= 1; i--){
            if (i == reviewRating) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            }
            else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }
        sb.append("</select></p></td>");

        // recommend
        sb.append("<td colspan=\"2\"><p>Recommend: <input type=\"radio\" id=\"recommend1\" name=\"recommend\" ");
        sb.append("" + reviewRecommend1 + "> YES <input type=\"radio\" id=\"recommend0\" ");
        sb.append("name=\"recommend\" " + reviewRecommend0 + "> NO</p></td></tr>");

        // review
        sb.append("<tr><td colspan=\"3\"><p><textarea id=\"reviewContent\" class=\"form-control\" rows=\"5\" ");
        sb.append("cols=\"ml-auto\" maxlength=\"250\" placeholder=\"Review\" onkeyup=\"contentChange('Content')\">");
        sb.append(reviewContent + "</textarea><span id=\"countContent\" class=\"small\" ");
        sb.append("style=\"position: absolute; right: 25px; bottom: 35px;\">250</span></p></td></tr>");

        sb.append("</table>");
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Get data depends on which method: add or edit.
     *
     * @param id
     * @param type
     * @return ResultSet - review data
     * @throws SQLException
     */
    private ResultSet getResult(String id, String type) throws SQLException {
        ResultSet result = null;

        if (type.equals("Add")) {
            result = dbhandler.getHotel(id);
        }
        else if (type.equals("Edit")) {
            result = dbhandler.getReview(id);
        }

        return result;
    }

    /**
     * Construct the html query for the footer of review modal.
     *
     * @param type
     * @return String - Html query of the footer
     * @throws SQLException
     */
    private String footer(String type) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"modal-footer\">");
        sb.append("<button class=\"button btn-secondary\" data-dismiss=\"modal\">");
        sb.append("<i class=\"fa fa-ban\" aria-hidden=\"true\"></i> Close</button>");

        if (type.equals("Add")) {
            sb.append("<button class=\"button btn-primary\" ");
            sb.append("onclick=\"putreview('add')\" data-dismiss=\"modal\">");
            sb.append("<i class=\"fa fa-plus\" aria-hidden=\"true\"></i> Add</button> ");
        }
        else if (type.equals("Edit")) {
            sb.append("<button class=\"button btn-primary\" ");
            sb.append("onclick=\"putreview('edit')\" data-dismiss=\"modal\">");
            sb.append("<i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i> Edit</button> ");
            sb.append("<button class=\"button btn-danger\" ");
            sb.append("onclick=\"deleteConfirm()\" data-dismiss=\"modal\">");
            sb.append("<i class=\"fa fa-trash\" aria-hidden=\"true\"></i> Delete</button>");
        }


        sb.append("</div>");
        return sb.toString();
    }

    /**
     * doPost method to handle adding/editing/deleting operations.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DataSession inputs = getInputs(request);
        PrintWriter out = response.getWriter();

        try {
            out.println(dbhandler.putReview(inputs));
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Create new data session to store the data inputted by user .
     *
     * @param request
     * @return
     */
    private DataSession getInputs(HttpServletRequest request) {
        DataSession ds = new DataSession("inputs");
        ds.setElement("type", request.getParameter("type"));
        ds.setElement("id", request.getParameter("id"));
        ds.setElement("title", request.getParameter("title"));
        ds.setElement("rating", request.getParameter("rating"));
        ds.setElement("recommend", request.getParameter("recommend"));
        ds.setElement("review", request.getParameter("review"));
        ds.setElement("username", getUsername(request));
        return ds;
    }
}
