package servlet;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * JettyServer class, main class for hotelApp server to handle and map all methods.
 *
 * @author BrianSung
 */
public class JettyServer {
    private static int PORT = 5000;
    private final static String DIR = System.getProperty("user.dir");

    /**
     * Server starter to initialize the server.
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server(PORT);
        ServletContextHandler servhandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        servhandler.setResourceBase(DIR);
        servhandler.setContextPath("/");
        servhandler.setAttribute("templateEngine", velocity);

        servhandler.addServlet(IndexServlet.class, "/index");
        servhandler.addServlet(HotelServlet.class, "/hotel");
        servhandler.addServlet(CategoryServlet.class, "/category");
        servhandler.addServlet(CityServlet.class, "/city");
        servhandler.addServlet(PageServlet.class, "/page");
        servhandler.addServlet(HotelDetailServlet.class, "/hotelDetail");
        servhandler.addServlet(ReviewServlet.class, "/review");
        servhandler.addServlet(LikeServlet.class, "/like");
        servhandler.addServlet(ReviewModalServlet.class, "/reviewmodal");
        servhandler.addServlet(ShowLikeServlet.class, "/showlike");
        servhandler.addServlet(AttractionServlet.class, "/attraction");
        servhandler.addServlet(MyPageServlet.class, "/mypage");
        servhandler.addServlet(ClearServlet.class, "/clear");
        server.setHandler(servhandler);

        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed","false");
        servhandler.addServlet(holderPwd,"/");

        System.out.println("Starting server on port " + PORT + "...");

        try {
            server.start();
            server.join();

            System.out.println("Exiting...");
        }
        catch (Exception ex) {
            System.out.println("Interrupted while running server. " + ex);
            System.exit(-1);
        }
    }
}