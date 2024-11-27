package admissionservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/AdminDashBoard")
public class AdminDashBoard extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:mysql://localhost:3306/admission_management_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Archer@1234";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT s.uid, s.first_name, s.last_name, s.email, s.phone, s.course_type, "
                         + "s.tenth_marks, s.cet_marks, s.dob, s.photo, s.aadhar_card, "
                         + "s.tenth_marksheet, s.cet_marksheet, p.pid, p.amount "
                         + "FROM students s "
                         + "LEFT JOIN payments p ON s.uid = p.uid";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Add Bootstrap and custom CSS
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Admin Dashboard</title>");
            out.println("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css'>");
            out.println("<link rel='stylesheet' href='css/admissiondashboard.css'>");
            
            out.println("<link rel='shortcut icon' type='image/x-icon' href='img/favicon.png'>");
            out.println("<link rel='stylesheet' href='css/bootstrap.min.css'>");
            out.println("<link rel='stylesheet' href='css/owl.carousel.min.css'>");
            out.println("<link rel='stylesheet' href='css/magnific-popup.css'>");
            out.println("<link rel='stylesheet' href='css/font-awesome.min.css'>");
            out.println("<link rel='stylesheet' href='css/themify-icons.css'>");
            out.println("<link rel='stylesheet' href='css/nice-select.css'>");
            out.println("<link rel='stylesheet' href='css/flaticon.css'>");
            out.println("<link rel='stylesheet' href='css/gijgo.css'>");
            out.println("<link rel='stylesheet' href='css/animate.css'>");
            out.println("<link rel='stylesheet' href='css/slicknav.css'>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='header-area bg-dark'>");
            out.println("  <div class='header-top_area '>");
            out.println("    <div class='container-fluid'>");
            out.println("      <div class='row'>");
            out.println("        <div class='col-lg-12'>");
            out.println("          <div class='header_top_wrap d-flex justify-content-between align-items-center'>");
            out.println("            <div class='text_wrap'>");
            out.println("              <p><a href='AdminDashBoard'>Admin DashBoard</a> <a href='login.html'>Log Out</a></p>");
            out.println("            </div>");
            out.println("          </div>");
            out.println("        </div>");
            out.println("      </div>");
            out.println("    </div>");
            out.println("  </div>");
            out.println("</div>");

            // Navigation bar
//            out.println("<nav class='navbar navbar-expand-lg navbar-dark '>");
//            out.println("<div class='ml-auto'>");
//            out.println("<a href='login.html' class='btn btn-outline-light'>Log Out</a>");
//            out.println("</div>");
//            out.println("</nav>");

            // Table heading
            out.println("<div class='container my-4'>");
            out.println("<h2 class='text-center mb-4'>Student List</h2>");

            // Table structure
            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-bordered table-hover table-striped'>");
            out.println("<thead class='bg-primary'>");
            out.println("<tr>");
            out.println("<th>UId</th><th>First Name</th><th>Last Name</th><th>Email</th><th>Phone</th>");
            out.println("<th>Course Type</th><th>10th Marks</th><th>CET Marks</th><th>Date of Birth</th>");
            out.println("<th>Photo</th><th>Aadhar Card</th><th>10th Marksheet</th><th>CET Marksheet</th>");
            out.println("<th>Payment ID</th><th>Paid Amount</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            // Fetch and display data
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("uid") + "</td>");
                out.println("<td>" + rs.getString("first_name") + "</td>");
                out.println("<td>" + rs.getString("last_name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td>" + rs.getString("phone") + "</td>");
                out.println("<td>" + rs.getString("course_type") + "</td>");
                out.println("<td>" + rs.getString("tenth_marks") + "</td>");
                out.println("<td>" + rs.getString("cet_marks") + "</td>");
                out.println("<td>" + rs.getDate("dob") + "</td>");
                out.println("<td>" + rs.getString("photo") + "</td>");
                out.println("<td>" + rs.getString("aadhar_card") + "</td>");
                out.println("<td>" + rs.getString("tenth_marksheet") + "</td>");
                out.println("<td>" + rs.getString("cet_marksheet") + "</td>");

                Integer pid = rs.getInt("pid");
                if (rs.wasNull()) pid = null;
                out.println("<td>" + (pid != null ? pid : "N/A") + "</td>");
                out.println("<td>" + (rs.getBigDecimal("amount") != null ? rs.getBigDecimal("amount") : "N/A") + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</div>");

            // Bootstrap JS and optional JS
            out.println("<script src='https://code.jquery.com/jquery-3.5.1.slim.min.js'></script>");
            out.println("<script src='https://cdn.jsdelivr.net/npm/@popperjs/core@2.4.4/dist/umd/popper.min.js'></script>");
            out.println("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js'></script>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error displaying student data.");
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

