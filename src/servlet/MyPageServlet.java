package servlet;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * MyPageServlet class to present MyPage in hotelApp.
 */
public class MyPageServlet extends BaseServlet {

    private static final String NAME = "mypage";

    /**
     * doGet method to load mypage.html template and display user's MyPage.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String username = getUsername(request);

        if (username == null) {
            response.sendRedirect(response.encodeRedirectURL("/index"));
        }
        else {
            // load template
            out.println(getTemplate(request, NAME));
            navBar(request, response, NAME);
            out.println("<script> getLoginInformation(); </script>");
            out.println("<script> addResult('myReviews'); </script>");
            out.println("<script> addResult('favoriteHotels'); </script>");
            out.println("<script> addResult('likedReviews'); </script>");
            out.println("<script> addResult('expediaLinks'); </script>");
        }
    }

    /**
     * doPost method to display user's data in MyPage, five rows of data per request.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, List<String>> types = getTypesMap();
        String type = StringEscapeUtils.escapeHtml4(request.getParameter("type"));
        int finish = Integer.parseInt(request.getParameter("finish"));
        String username = getUsername(request);

        if (username != null && types.containsKey(type) && finish == 0) {
            try (ResultSet result = dbhandler.getMyData(types.get(type), username)) {
                StringBuilder sb = new StringBuilder();
                PrintWriter out = response.getWriter();
                int data = Integer.parseInt(request.getParameter("data"));
                int count = 0, display = 0;

                while (result.next()) {
                    count++;
                    if (count > data && display < 5) {
                        display++;
                        sb.append("<tr><td name=\"" + type + "Data\" onclick=\"" + getFunction(type, result) + "\">");
                        sb.append(result.getString(types.get(type).get(3)));
                        sb.append("</td></tr>");
                    }
                }

                if (count == 0) {
                    sb.append("<tr><td name=\"" + type + "Finish\">No result.</td></tr>");
                }
                else if (count == data + display) {
                    sb.append("<tr hidden><td name=\"" + type + "Finish\"></td></tr>");
                }

                out.println(sb.toString());
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Return the function of onclick operations of each user's data.
     *
     * @param type
     * @param result
     * @return String - button function
     * @throws SQLException
     */
    private String getFunction(String type, ResultSet result) throws SQLException {
        String function = "";

        if (type.equals("favoriteHotels")) {
            function = "detail('hotel', '" + result.getString("main.hotelid") + "');"
                    + "scrollDownAnimation();";
        }
        else if (type.equals("expediaLinks")) {
            function = "window.open('" + result.getString("main.link") + "');";
        }
        else if (type.endsWith("Reviews")) {
            function = "detail('review', '" + result.getString("main.reviewid") + "');"
                    + "scrollDownAnimation();";
        }

        return function;
    }
}
