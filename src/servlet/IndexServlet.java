package servlet;

import data.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * IndexServlet class to present the index of hotelApp.
 */
@SuppressWarnings("serial")
public class IndexServlet extends BaseServlet {

    /**
     * doGet method load index.html template and display Index page.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // load template
        PrintWriter out = response.getWriter();
        out.println(getTemplate(request, "index"));

        // check error
        String error;
        int code;
        if ((error = request.getParameter("error")) != null) {
            try {
                code = Integer.parseInt(error);
            }
            catch (Exception ex) {
                code = -1;
            }

            String errorMessage = getStatusMessage(code);
            out.println("<script> errorAlert(\"" + errorMessage + "\"); </script>");
        }

        // change navBar
        navBar(request, response, "index");

        if (request.getParameter("newuser") != null) {
            out.println("<script> successAlert(\"You can now login with your new account.\"); </script>");
        }

        if (request.getParameter("logout") != null) {
            clearSession(request, response);
            out.println("<script> successAlert(\"You've been successfully logged out.\"); </script>");
        }
    }

    /**
     * doPost method to handle login and register operations.
     *
     * @param request
     * @param response
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String type = (request.getParameter("type") == null ? "" : request.getParameter("type"));
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr();

        try {
            if (type.equals("Login")) {
                // authenticate username and password with database
                Status status = dbhandler.authenticateUser(username, password);

                if (status == Status.OK) {
                    dbhandler.addLogin(username, ipAddress);
                    session.setAttribute("login", "true");
                    session.setAttribute("username", username);
                    session.setAttribute("greeting", "false");
                    response.sendRedirect(response.encodeRedirectURL("/index"));
                } else {
                    response.sendRedirect(response.encodeRedirectURL("/index?error=" + status.ordinal()));
                }
            }
            else if (type.equals("Register")) {
                // register username and password into database
                Status status = dbhandler.registerUser(username, password);

                if(status == Status.OK) {
                    response.sendRedirect(response.encodeRedirectURL("/index?newuser"));
                }
                else {
                    response.sendRedirect(response.encodeRedirectURL("/index?error=" + status.ordinal()));
                }
            }
            else {
                response.sendRedirect(response.encodeRedirectURL("/index"));
            }
        }
        catch (Exception ex) {
            System.out.println("Unable to process post request: index. " + ex);
        }
    }
}
