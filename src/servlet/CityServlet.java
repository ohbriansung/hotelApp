package servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CityServlet class.
 */
public class CityServlet extends BaseServlet {

    /**
     * Display city select bar for Hotels page, data base on cities in database.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        PrintWriter out = response.getWriter();

        if (getUsername(request) != null) {
            try (ResultSet result = dbhandler.getCitys()) {

                sb.append("<select id=\"city\" class=\"form-control-sm\" onchange=\"showOnChange('hotel')\">");
                sb.append("<option value=\"\">Select City</option>");
                while (result.next()) {
                    sb.append("<option value=\"" + result.getString("city") + "\">"
                            + result.getString("city") + "</option>");
                }
                sb.append("</select>");

                out.println(sb.toString());
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }
}
