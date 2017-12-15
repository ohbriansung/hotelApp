package servlet;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ShowLikeServlet class.
 */
public class ShowLikeServlet extends BaseServlet {

    /**
     * doPost method to display "who liked this review" modal data.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = StringEscapeUtils.escapeHtml4(request.getParameter("type"));
        String id = StringEscapeUtils.escapeHtml4(request.getParameter("id"));
        PrintWriter out = response.getWriter();

        if (getUsername(request) != null) {

            try {
                if (type.equals("review")) {
                    out.println(showReviewLikes(id));
                }
            }
            catch (SQLException e){
                System.out.println(e);
            }
        }
    }

    /**
     * Return users who liked a particular review.
     *
     * @param reviewId
     * @return String - users
     * @throws SQLException
     */
    private String showReviewLikes(String reviewId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSet result = dbhandler.showReviewLikes(reviewId);

        while (result.next()) {
            sb.append("<span class=\"badge badge-warning\" ");
            sb.append("title=\"").append(result.getTimestamp("date")).append("\">");
            sb.append(result.getString("username")).append("</span> ");
        }

        if (sb.length() == 0) {
            sb.append("<div align=\"float-center\">No result.</div>");
        }

        return sb.toString();
    }
}
