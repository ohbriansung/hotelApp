package servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * CategoryServlet class.
 */
public class CategoryServlet extends BaseServlet {

    /**
     * doPost method to display categories of Hotels page.
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
            String type = request.getParameter("type");
            String[] titles = new String[] {"Hotels", "Reviews", "Attractions"};

            for (int i = 0; i < titles.length; i++) {
                if (type.equals(titles[i])) {
                    sb.append("<label class=\"list-group-item list-group-item-action active\">");
                    sb.append(titles[i]).append("</label>");
                }
                else {
                    sb.append("<label class=\"list-group-item list-group-item-action\" ");
                    sb.append("onclick=\"tableTitle('" + titles[i] + "')\">");
                    sb.append(titles[i]).append("</label>");
                }
            }

            out.println(sb.toString());
        }
    }
}
