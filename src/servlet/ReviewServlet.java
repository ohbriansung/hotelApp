package servlet;

import data.DataSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReviewServlet class to display reviews in Hotels page.
 */
public class ReviewServlet extends BaseServlet {

    private static final String NAME = "review";

    /**
     * doPost method to display reviews table in Hotels page.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        if (getUsername(request) != null) {
            checkDataSession(session, NAME);
            DataSession ds = (DataSession) session.getAttribute(NAME);
            setDataSession(request, ds);

            // displaying the result from database
            try (ResultSet result = dbhandler.getReviews(ds)) {
                int count = 0;
                int ShowEntries = Integer.parseInt(ds.getElement(NAME + "ShowEntries"));
                int Page = Integer.parseInt(ds.getElement(NAME + "Page"));
                int min = ShowEntries * (Page - 1) + 1, max = ShowEntries * Page;

                tableHead(sb, ds, NAME);

                sb.append("<tbody>");
                while (result.next()) {
                    count++;
                    if (count >= min && count <= max) {
                        sb.append("<tr ");
                        sb.append("onclick=\"detail('" + NAME + "', '" + result.getString(ds.getElement("column0")) + "');");
                        sb.append("scrollDownAnimation();\">");
                        sb.append("<td>").append(count).append("</td>");
                        sb.append("<td>").append(result.getString(ds.getElement("column1"))).append("</td>");
                        sb.append("<td>").append(result.getString(ds.getElement("column2"))).append("</td>");
                        sb.append("<td>").append(starts(result.getDouble(ds.getElement("column3")))).append("</td>");
                        sb.append("<td>").append(result.getTimestamp(ds.getElement("column4"))).append("</td>");
                        sb.append("</tr>");
                    }
                }
                if (count == 0) {
                    sb.append("<td colspan=\"5\" align=\"center\">");
                    sb.append(ds.getElement("hotelid").equals("") ? dataTableErrors[0] : dataTableErrors[1]);
                    sb.append("</td>");
                }
                sb.append("</tbody>");
                ds.setElement(NAME + "Count", "" + count);

                out.println(sb.toString());
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Set attributes data of attraction data session when user changes request.
     *
     * @param request
     * @param ds
     */
    private void setDataSession(HttpServletRequest request, DataSession ds) {
        String sortColumn = request.getParameter("sortColumn");
        String sortType = request.getParameter("sortType");
        String showEntries = request.getParameter(NAME + "ShowEntries");
        String page = request.getParameter(NAME + "Page");
        String hotelId = request.getParameter("hotelId");

        ds.setElement("hotelid", hotelId);
        ds.setElement("sortColumn", sortColumn);
        ds.setElement("sortType", sortType);
        ds.setElement(NAME + "ShowEntries", showEntries);
        ds.setElement(NAME + "Page", page);
    }
}
