package admissionservlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ProfilePageServlet")
public class ProfilePageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String URL = "jdbc:mysql://localhost:3306/admission_management_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Archer@1234";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String userId = null;

        // Retrieve userId from cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        if (userId == null) {
            out.println("<h3>Error: User not logged in.</h3>");
            return;
        }

        // Fetch student details from the database
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT uid, first_name, last_name, email, phone FROM students WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("uid");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                // Display profile card
                out.println("<!DOCTYPE html>");
                out.println("<html lang='en'>");
                out.println("<head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                out.println("<title>Student Profile</title>");
                out.println("<style>");
                out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; display: flex; flex-direction: column; align-items: center; height: 100vh; }");
                out.println(".header { width: 100%; background-color: #343a40; padding: 10px 0; text-align: center; color: white; }");
                out.println(".header a { color: white; margin: 0 15px; text-decoration: none; font-weight: bold; }");
                out.println(".header a:hover { text-decoration: underline; }");
                out.println(".content { flex: 1; display: flex; justify-content: center; align-items: center; width: 100%; }");
                out.println(".profile-card { background: #fff; max-width: 500px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px; text-align: center; }");
                out.println(".profile-card h2 { color: #333; margin-bottom: 10px; }");
                out.println(".profile-card p { margin: 5px 0; color: #555; }");
                out.println(".profile-card .btn { margin-top: 20px; display: inline-block; padding: 10px 20px; background-color: #007bff; color: #fff; text-decoration: none; border-radius: 5px; }");
                out.println(".profile-card .btn:hover { background-color: #0056b3; }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");

                // Header Section
                out.println("<div class='header'>");
                out.println("<a href='ProfilePageServlet'>Profile Page</a>");
                out.println("<a href='login.html'>Log Out</a>");
                out.println("</div>");

                // Profile Card Section
                out.println("<div class='content'>");
                out.println("<div class='profile-card'>");
                out.println("<h2>" + name + "</h2>");
                out.println("<p><strong>ID:</strong> " + id + "</p>");
                out.println("<p><strong>Email:</strong> " + email + "</p>");
                out.println("<p><strong>Phone:</strong> " + phone + "</p>");
                out.println("<a href='admissionform.html' class='btn'>Back to Admission Form</a>");
                out.println("</div>");
                out.println("</div>");

                out.println("</body>");
                out.println("</html>");
            } else {
                out.println("<h3>No profile found for user ID: " + userId + "</h3>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}

