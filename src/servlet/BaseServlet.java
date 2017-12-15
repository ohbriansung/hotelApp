package servlet;

import data.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Provides base functionality to all servlets in this project.
 */
@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {
    protected static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    protected static final String[] dataTableErrors =
            new String[]{"Please select one hotel from Hotels table first.", "No result."};

    /**
     * To present the date and time.
     *
     * @return String
     */
    protected String getDate() {
        String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Clear the session data when logout.
     *
     * @param request
     * @param response
     */
    protected void clearSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        session.removeAttribute("login");
        session.removeAttribute("username");
        session.removeAttribute("hotel");
        session.removeAttribute("review");
    }

    /**
     * To get the particular status message by name.
     *
     * @param errorName
     * @return String
     */
    protected String getStatusMessage(String errorName) {
        Status status = null;

        try {
            status = Status.valueOf(errorName);
        }
        catch (Exception ex) {
            System.out.println(errorName + ex);
            status = Status.ERROR;
        }

        return status.toString();
    }

    /**
     * To get the particular status message by code id.
     *
     * @param code
     * @return
     */
    protected String getStatusMessage(int code) {
        Status status = null;

        try {
            status = Status.values()[code];
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage() + ex);
            status = Status.ERROR;
        }

        return status.toString();
    }

    /**
     * To check and get the username in the session.
     *
     * @param request
     * @return String
     */
    protected String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession();

        String login = (String) session.getAttribute("login");
        String username = (String) session.getAttribute("username");

        if (login != null && login.equals("true") && username != null) {
            return username;
        }

        return null;
    }

    /**
     * Initialize the essential attributes of hotel/review data session.
     *
     * @param session
     * @param name
     */
    protected void checkDataSession(HttpSession session, String name) {
        if (session.getAttribute(name) == null) {

            // initialize parameters when first time loading
            DataSession ds = new DataSession(name);

            if (name.equals("hotel")) {
                ds.setElement("columnNum", "4");
                ds.setElement("columnName0", "#");
                ds.setElement("columnName1", "Name");
                ds.setElement("columnName2", "City");
                ds.setElement("columnName3", "Rating");
                ds.setElement("column0", "h.hotelid");
                ds.setElement("column1", "h.hotelname");
                ds.setElement("column2", "h.city");
                ds.setElement("column3", "r.rating");
                ds.setElement("city", "");
                ds.setElement("hotelname", "");
                ds.setElement("sortColumn", "column3"); // default as sorting by rating
                ds.setElement("sortType", "DESC");

            }
            else if (name.equals("review")) {
                ds.setElement("columnNum", "5");
                ds.setElement("columnName0", "#");
                ds.setElement("columnName1", "Username");
                ds.setElement("columnName2", "Title");
                ds.setElement("columnName3", "Rating");
                ds.setElement("columnName4", "Date");
                ds.setElement("column0", "r.reviewid");
                ds.setElement("column1", "r.username");
                ds.setElement("column2", "r.title");
                ds.setElement("column3", "r.rating");
                ds.setElement("column4", "r.date");
                ds.setElement("sortColumn", "column4"); // default as sorting by date
                ds.setElement("sortType", "DESC");
                ds.setElement("hotelid", "");
            }

            ds.setElement(name + "id", "");
            ds.setElement(name + "ShowEntries", "5");
            ds.setElement(name + "Page", "1");

            session.setAttribute(name, ds);
        }
    }

    /**
     * Determine index/hotels page navbar display.
     *
     * @param request
     * @param response
     * @param name
     * @throws IOException
     */
    protected void navBar(HttpServletRequest request, HttpServletResponse response, String name) throws IOException {
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        String username = getUsername(request);

        if (request.getParameter("logout") == null && username != null) {
            out.println("<script> hideAndSee('" + name + "'); </script>");

            // only greeting once.
            String greeting = (String) session.getAttribute("greeting");
            if (greeting != null && greeting.equals("false")) {
                session.setAttribute("greeting", "true");
                out.println("<script> greeting(\"" + username + "\"); </script>");
            }
        }
    }

    /**
     * Construct head html query of hotel/review table.
     *
     * @param sb
     * @param ds
     * @param name
     */
    protected void tableHead(StringBuilder sb, DataSession ds, String name) {
        sb.append("<thead>");
        sb.append("<tr>");
        for (int i = 0; i < Integer.parseInt(ds.getElement("columnNum")); i++) {
            sb.append(getColumnHtml(i, ds, name));
        }
        sb.append("</tr>");
        sb.append("</thead>");
    }

    /**
     * Construct column head html query of hotel/review table.
     *
     * @param columnNum
     * @param ds
     * @param name
     * @return String - Html query of column head
     */
    private String getColumnHtml(int columnNum, DataSession ds, String name) {
        StringBuilder sb = new StringBuilder();
        String columnName = ds.getElement("columnName" + columnNum);
        String sortType = ds.getElement("sortType").equals("DESC")
                ? "<i class=\"fa fa-arrow-down\"></i>" : "<i class=\"fa fa-arrow-up\"></i>";
        boolean checkOnThis = ds.getElement("sortColumn").equals("column" + columnNum);

        if (columnName.equals("#")) {
            sb.append("<th id=\"column" + columnNum + "\" " + setStyle(columnName) + ">").append(columnName);
        }
        else {
            sb.append("<th id=\"column" + columnNum + "\" " + setStyle(columnName)
                    + " onclick=\"sort('" + name + "', " + getSortString(columnNum, ds) + ")\">");
            sb.append(columnName);
            sb.append((checkOnThis ? " " + sortType : ""));
        }

        sb.append("</th>");

        return sb.toString();
    }

    /**
     * Return sort column and type for each column head.
     *
     * @param columnNum
     * @param ds
     * @return String - sort column and type
     */
    private String getSortString(int columnNum, DataSession ds) {
        StringBuilder sb = new StringBuilder();
        String currentColumn = ds.getElement("sortColumn");
        String currentType = ds.getElement("sortType");
        String thisColumn = "column" + columnNum;

        if (thisColumn.equals(currentColumn)) {
            if (currentType.equals("DESC")) {
                sb.append("'column" + columnNum + "', 'ASC'");
            }
            else {
                sb.append("'column" + columnNum + "', 'DESC'");
            }
        }
        else {
            sb.append("'column" + columnNum + "', 'ASC'");
        }

        return sb.toString();
    }

    /**
     * Return column style for each column.
     *
     * @param columnName
     * @return String - style query
     */
    protected String setStyle(String columnName) {
        String style = "style=\"width: auto;\"";

        if (columnName.equals("Rating")) {
            style = "style=\"width: 6.5em;\"";
        }
        else if (columnName.equals("#")) {
            style = "style=\"width: 3.5em;\"";
        }

        return style;
    }

    /**
     * Return star icons base on rating.
     *
     * @param rating
     * @return String - star icons
     */
    protected String starts(double rating) {
        StringBuilder sb = new StringBuilder();
        for (double i = 1; i <= 5; i++) {
            if (i <= rating) {
                sb.append("<i class=\"fa fa-star\"></i>");
            }
            else if (Math.abs(i - rating) <= 0.5) {
                sb.append("<i class=\"fa fa-star-half-o\"></i>");
            }
            else {
                sb.append("<i class=\"fa fa-star-o\"></i>");
            }
        }
        return sb.toString();
    }

    /**
     * Return types of applications in hotelApp to prevent database attack.
     *
     * @return List - application types
     */
    protected List<String> getTypes() {
        List<String> types = new ArrayList<>();

        types.add("hotel");
        types.add("review");
        types.add("attraction");
        types.add("expedia");

        return types;
    }

    /**
     * Return types of applications for MyPage to prevent database attack.
     *
     * @return Map - application types of MyPage
     */
    protected Map<String, List<String>> getTypesMap() {
        Map<String, List<String>> types = new HashMap<>();
        types.put("myReviews", Arrays.asList("reviews", "hotels", "hotelid", "title"));
        types.put("favoriteHotels", Arrays.asList("likehotel", "hotels", "hotelid", "hotelname"));
        types.put("likedReviews", Arrays.asList("likereview", "reviews", "reviewid", "title"));
        types.put("expediaLinks", Arrays.asList("likeexpedia", "hotels", "hotelid", "hotelname"));
        return types;
    }

    /**
     * Use Velocity Engine to get html template.
     *
     * @param request
     * @param tempName
     * @return String - template html
     */
    protected String getTemplate(HttpServletRequest request, String tempName) {
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("frontend" + File.separator + tempName + ".html");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}