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
 * PageServlet class to handle page lists in Hotels page and user's login information in MyPage.
 */
public class PageServlet extends BaseServlet {

    private String pageOf;

    /**
     * doGet to present user's login information in MyPage. (Last login time, IP address)
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = getUsername(request);
        StringBuilder sb = new StringBuilder();
        PrintWriter out = response.getWriter();

        if (username == null) {
            response.sendRedirect(response.encodeRedirectURL("/index"));
        }
        else {
            try (ResultSet result = dbhandler.getLogins(username)) {
                int count = 0;
                while (result.next()) {
                    count++;
                    if (count == 2) {
                        sb.append("<div class=\"col-lg-6\">");
                        sb.append("<i class=\"fa fa-clock-o\" aria-hidden=\"true\"></i> Last Login Time: ");
                        sb.append(result.getTimestamp("date"));
                        sb.append("</div>");
                        sb.append("<div class=\"col-lg-6\">");
                        sb.append("<i class=\"fa fa-compass\" aria-hidden=\"true\"></i> IP Address: ");
                        sb.append(result.getString("ipaddress"));
                        sb.append("</div>");
                    }
                }

                if (count < 2) {
                    sb.append("<div class=\"col-lg-12\">");
                    sb.append("<i class=\"fa fa-hand-spock-o\" aria-hidden=\"true\"></i> ");
                    sb.append("This is your first time to login! Huzzah!");
                    sb.append("</div>");
                }

                out.println(sb.toString());
            }
            catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * doPost method to present the page list under hotels/reviews/attraction tables in Hotels page.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.pageOf = request.getParameter("pageof");
        StringBuilder sb = new StringBuilder();
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        if (getUsername(request) != null) {
            DataSession ds = (DataSession) session.getAttribute(pageOf);
            int currentPage = Integer.parseInt(ds.getElement(pageOf + "Page"));
            int showEntries = Integer.parseInt(ds.getElement(pageOf + "ShowEntries"));
            int total;

            try {
                total = Integer.parseInt(ds.getElement(pageOf + "Count"));
                if (total > 0) {
                    int pageNum = total / showEntries + (total % showEntries == 0 ? 0 : 1);

                    // calculate the page range
                    int start, end;
                    if (currentPage <= 5) {
                        start = 1;
                        end = Math.min(9, pageNum);
                    } else if (pageNum - currentPage < 5) {
                        start = Math.max(1, pageNum - 8);
                        end = pageNum;
                    } else {
                        start = currentPage - 4;
                        end = currentPage + 4;
                    }

                    sb.append(head(currentPage));
                    for (int i = start; i <= end; i++) {
                        sb.append(mid(currentPage, i));
                    }
                    sb.append(tail(currentPage, pageNum));

                    out.println(sb.toString());
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Construct the html query of left arrow in the beginning of page list.
     *
     * @param currentPage
     * @return String - Html query of page list head
     */
    private String head(int currentPage) {
        StringBuilder sb = new StringBuilder();

        sb.append("<li class=\"page-item");
        sb.append(currentPage == 1 ? " disabled" : "\" onclick=\"gotoPage('"
                + this.pageOf + "', '" + (currentPage - 1) + "');window.location.href='#page-top';");
        sb.append("\"><a class=\"page-link\">");
        sb.append("<i class=\"fa fa-arrow-left\" aria-hidden=\"true\"></i></a>");
        sb.append("</li>");

        return sb.toString();
    }

    /**
     * Construct the html query of right arrow in the end of page list.
     *
     * @param currentPage
     * @param end
     * @return String - Html query of page list tail
     */
    private String tail(int currentPage, int end) {
        StringBuilder sb = new StringBuilder();

        sb.append("<li class=\"page-item");
        sb.append(currentPage == end ? " disabled" : "\" onclick=\"gotoPage('"
                + this.pageOf + "', '" + (currentPage + 1) + "');window.location.href='#page-top';");
        sb.append("\"><a class=\"page-link\">");
        sb.append("<i class=\"fa fa-arrow-right\" aria-hidden=\"true\"></i></a>");
        sb.append("</li>");

        return sb.toString();
    }

    /**
     * Construct the html query of page numbers in the middle of page list.
     *
     * @param currentPage
     * @param i
     * @return String - Html query of page list body
     */
    private String mid(int currentPage, int i) {
        StringBuilder sb = new StringBuilder();

        sb.append("<li class=\"page-item");
        sb.append(currentPage == i ? " active" : "\" onclick=\"gotoPage('"
                + this.pageOf + "', '" + i + "');window.location.href='#page-top';");
        sb.append("\"><a class=\"page-link\">" + i + "</a></li>");

        return sb.toString();
    }
}
