package admissionservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:mysql://localhost:3306/admission_management_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Archer@1234";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");
        String role = request.getParameter("role"); // Get the role from the form

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Registration</title>");
        out.println("<link rel='stylesheet' type='text/css' href='css/styles.css'>");
        out.println("</head>");
        out.println("<body>");

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role.isEmpty()) {
            out.println("<h2>All fields are required!</h2>");
            out.println("<a href='registration.html'>Go Back</a>");
        } else if (password.equals(confirmPassword)) {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "INSERT INTO registers (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role); // Add role to the database

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                	response.sendRedirect("login.html");
                } else {
                    out.println("<script type='text/javascript'>");
                    out.println("alert('There was an issue with registration. Please try again.');");
                    out.println("window.location.href = 'registration.html';");
                    out.println("</script>");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                out.println("<h2>Error: Unable to add user</h2>");
                out.println("<p>" + e.getMessage() + "</p>");
            }
        } else {
            out.println("<h2>Passwords do not match!</h2>");
            out.println("<a href='registration.html'>Go Back</a>");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
