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

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String URL = "jdbc:mysql://localhost:3306/admission_management_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Archer@1234";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        Cookie[] cookies = request.getCookies();
        String user_id="";
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                	user_id = cookie.getValue();
                    break;
                }
            }
        }

        // Retrieve form data
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String amount = request.getParameter("amount");
        String cardNumber = request.getParameter("cardNumber");
        String expiryDate = request.getParameter("expiryDate");
        String cvv = request.getParameter("cvv");

        // Simulate payment success and save to database
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        	String sqlquery = "SELECT uid FROM students WHERE id=?";
        	PreparedStatement pstmt = conn.prepareStatement(sqlquery);
        	pstmt.setInt(1, Integer.parseInt(user_id));
        	ResultSet rst = pstmt.executeQuery();
        	int studentId = 0;
        	
        	if(rst.next()) {
        		studentId = rst.getInt("uid");
        	}
            String sql = "INSERT INTO payments (full_name, email, amount, card_number, expiry_date, cvv, uid) VALUES (?, ?, ?, ?, ?, ?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setBigDecimal(3, new java.math.BigDecimal(amount));
            pstmt.setString(4, cardNumber);
            pstmt.setString(5, expiryDate);
            pstmt.setString(6, cvv);
            pstmt.setInt(7, studentId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                out.println("<h1>Payment Successful!</h1>");
                out.println("<p>Thank you, " + fullName + ", for completing your payment.</p>");
                response.sendRedirect("ProfilePageServlet");
                out.println("<a href='admissionform.html'>Go Back</a>");
            } else {
                out.println("<h1>Error: Payment Failed.</h1>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }
}
