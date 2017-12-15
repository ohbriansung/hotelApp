package servlet;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * ClearServlet class.
 */
public class ClearServlet extends BaseServlet {

    /**
     * doPost method to clear particular field of MyPage.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, List<String>> types = getTypesMap();
        String type = StringEscapeUtils.escapeHtml4(request.getParameter("type"));
        String username = getUsername(request);
        PrintWriter out = response.getWriter();

        if (username != null && types.containsKey(type)) {
            try {
                out.println(dbhandler.clearMyData(types.get(type), username));
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }
}
