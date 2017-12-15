package servlet;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * LikeServlet class.
 */
public class LikeServlet extends BaseServlet {

    /**
     * doPost method to handle operations: like review, save hotel, check Expedia link.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> types = getTypes();
        String type = StringEscapeUtils.escapeHtml4(request.getParameter("type"));
        String id = StringEscapeUtils.escapeHtml4(request.getParameter("id"));
        String username = getUsername(request);
        PrintWriter out = response.getWriter();

        if (username != null && types.contains(type)) {
            try {
                out.println(dbhandler.setLike(type, id, username));
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }
}
