package admissionservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:mysql://localhost:3306/admission_management_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Archer@1234";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String loginType = request.getParameter("loginType"); // Get login type from form
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT password, role, id FROM registers WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            	
            	Cookie userIdCookie = new Cookie("userId",""+rs.getInt("id"));
            	userIdCookie.setMaxAge(60*60*24*30);
            	userIdCookie.setPath("/");
            	response.addCookie(userIdCookie);
            	
                String storedPassword = rs.getString("password");
                String role = rs.getString("role"); 

                if (password.equals(storedPassword)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);

                    // Redirect based on role or loginType
                    if ("admin".equalsIgnoreCase(loginType) && "admin".equalsIgnoreCase(role)) {
                        response.sendRedirect("AdminDashBoard");
                    } else if ("user".equalsIgnoreCase(loginType) && "user".equalsIgnoreCase(role)) {
                        response.sendRedirect("admissionform.html"); 
                    } else {
                        sendErrorAlert(out, "Invalid login type for this user!", "login.html");
                    }
                } else {
                    sendErrorAlert(out, "Invalid Username or Password!", "login.html");
                }
            } else {
                sendErrorAlert(out, "Invalid Username or Password!", "login.html");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h2>Error: Unable to log in</h2>");
        }
    }

    private void sendErrorAlert(PrintWriter out, String message, String redirectPage) {
        out.println("<script type='text/javascript'>");
        out.println("alert('" + message + "');");
        out.println("window.location.href = '" + redirectPage + "';");
        out.println("</script>");
    }
}
