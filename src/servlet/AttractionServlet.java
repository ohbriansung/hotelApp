package servlet;

import data.DataSession;
import hotelapp.TouristAttraction;
import hotelapp.TouristAttractionFinder;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * AttractionServlet class to display attractions in Hotels page.
 */
public class AttractionServlet extends BaseServlet {

    private static final String NAME = "attraction";

    /**
     * doGet method to display attractions table in Hotels page.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
        StringBuilder sb = new StringBuilder();
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        if (getUsername(request) != null) {
            checkDataSession(session);
            DataSession ds = (DataSession) session.getAttribute(NAME);
            setDataSession(request, ds);

            try {
                checkTAFinder(hotelId, ds);
                sb.append(head(ds));
                sb.append(body(ds));
                out.println(sb.toString());
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Initialize the essential attributes of attraction data session.
     *
     * @param session
     */
    private void checkDataSession(HttpSession session) {
        if (session.getAttribute(NAME) == null) {
            DataSession ds = new DataSession(NAME);
            ds.setElement("columnNum", "3");
            ds.setElement("columnName0", "#");
            ds.setElement("columnName1", "Name");
            ds.setElement("columnName2", "Rating");
            ds.setElement(NAME + "ShowEntries", "5");
            ds.setElement(NAME + "Page", "1");
            ds.setElement("hotelId", "");
            session.setAttribute(NAME, ds);
        }
    }

    /**
     * Set attributes data of attraction data session when user changes request.
     *
     * @param request
     * @param ds
     */
    private void setDataSession(HttpServletRequest request, DataSession ds) {
        String showEntries = request.getParameter(NAME + "ShowEntries");
        String page = request.getParameter(NAME + "Page");

        ds.setElement(NAME + "ShowEntries", showEntries);
        ds.setElement(NAME + "Page", page);
    }

    /**
     * Check if the attraction data session contains with attractions data of particular hotel.
     * If not, do run Google Place API to get attraction and put/replace into data session.
     *
     * @param hotelId
     * @param ds
     * @throws SQLException
     * @throws IOException
     */
    private void checkTAFinder(String hotelId, DataSession ds) throws SQLException, IOException {
        if (!ds.getElement("hotelId").equals(hotelId)) {
            TouristAttractionFinder taFinder = new TouristAttractionFinder();
            taFinder.fetchAttractions(hotelId);
            ds.setElement("hotelId", hotelId);
            ds.setObject("taFinder", taFinder);
        }
    }

    /**
     * Construct head html query of attraction table.
     *
     * @param ds
     * @return String - Html query of head
     */
    private String head(DataSession ds) {
        StringBuilder sb = new StringBuilder();
        sb.append("<thead>");
        sb.append("<tr>");
        for (int i = 0; i < Integer.parseInt(ds.getElement("columnNum")); i++) {
            String columnName = ds.getElement("columnName" + i);
            sb.append("<th id=\"column" + i + "\" " + setStyle(columnName) + ">").append(columnName).append("</th>");
        }
        sb.append("</tr>");
        sb.append("</thead>");
        return sb.toString();
    }

    /**
     * Construct body html query of attraction table.
     *
     * @param ds
     * @return String - Html query of body
     */
    private String body(DataSession ds) {
        TouristAttractionFinder taFinder = (TouristAttractionFinder) ds.getObject("taFinder");
        StringBuilder sb = new StringBuilder();

        int ShowEntries = Integer.parseInt(ds.getElement(NAME + "ShowEntries"));
        int Page = Integer.parseInt(ds.getElement(NAME + "Page"));
        int min = ShowEntries * (Page - 1) + 1, max = ShowEntries * Page;
        int count = 0;

        sb.append("<tbody>");
        if (taFinder != null) {
            taFinder.startOver();
            while (taFinder.hasNext()) {
                TouristAttraction ta = taFinder.next();
                count++;
                if (count >= min && count <= max) {
                    sb.append("<tr ");
                    sb.append("onclick=\"detail('" + NAME + "', '" + count + "');scrollDownAnimation();\">");
                    sb.append("<td>").append(count).append("</td>");
                    sb.append("<td>").append(ta.getName()).append("</td>");
                    sb.append("<td>").append(starts(ta.getRating())).append("</td>");
                    sb.append("</tr>");
                }
            }
        }
        if (count == 0) {
            sb.append("<td colspan=\"3\" align=\"center\">");
            sb.append(ds.getElement("hotelid").equals("") ? dataTableErrors[0] : dataTableErrors[1]);
            sb.append("</td>");
        }
        sb.append("</tbody>");
        ds.setElement(NAME + "Count", "" + count);

        return sb.toString();
    }
}
