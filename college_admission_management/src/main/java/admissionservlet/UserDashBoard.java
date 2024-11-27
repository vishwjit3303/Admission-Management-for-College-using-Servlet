package admissionservlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


@WebServlet("/UserDashBoard")
public class UserDashBoard extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:mysql://localhost:3306/admission_management_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Archer@1234";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // File upload directory
        String uploadPath = "C:/Users/vishw/eclipse-workspace/college_admission_management/src/main/webapp/uploads"; 
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // Variables to store file paths
        String photoPath = null;
        String aadharCardPath = null;
        String tenthMarksheetPath = null;
        String cetMarksheetPath = null;

        // File upload handling
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Variables for form data
        String firstName = null;
        String lastName = null;
        String email = null;
        String phone = null;
        String courseType = null;
        String dob = null;
        String tenthMarks = null;
        String cetMarks = null;

        try {
            List<FileItem> items = upload.parseRequest(request);

            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Handle form fields
                    switch (item.getFieldName()) {
                        case "first_name":
                            firstName = item.getString();
                            break;
                        case "last_name":
                            lastName = item.getString();
                            break;
                        case "email":
                            email = item.getString();
                            break;
                        case "phone":
                            phone = item.getString();
                            break;
                        case "course_type":
                            courseType = item.getString();
                            break;
                        case "dob":
                            dob = item.getString();
                            break;
                        case "tenth_marks":
                            tenthMarks = item.getString();
                            break;
                        case "cet_marks":
                            cetMarks = item.getString();
                            break;
                    }
                } else {
                    // Handle file uploads
                    String fileName = item.getName();
                    if (fileName != null && !fileName.isEmpty()) {
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        item.write(storeFile);

                        // Determine which file was uploaded
                        switch (item.getFieldName()) {
                            case "photo":
                                photoPath = "uploads/" + fileName;
                                break;
                            case "aadhar_card":
                                aadharCardPath = "uploads/" + fileName;
                                break;
                            case "tenth_marksheet":
                                tenthMarksheetPath = "uploads/" + fileName;
                                break;
                            case "cet_marksheet":
                                cetMarksheetPath = "uploads/" + fileName;
                                break;
                        }
                    }
                }
            }

            // Database operation
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                Cookie[] cookies = request.getCookies();
                String userId = null;
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("userId".equals(cookie.getName())) {
                            userId = cookie.getValue();
                            break;
                        }
                    }
                }

                if (userId == null) {
                    out.println("<h1>Error: User ID not found in cookies.</h1>");
                    return;
                }
                
               

                // SQL query to insert data
                String sql = "INSERT INTO students (first_name, last_name, email, phone, course_type, tenth_marks, cet_marks, dob, photo, aadhar_card, tenth_marksheet, cet_marksheet, id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, phone);
                pstmt.setString(5, courseType);

                // Handle marks based on course type
                if ("Diploma".equals(courseType)) {
                    pstmt.setBigDecimal(6, tenthMarks != null && !tenthMarks.isEmpty() ? new BigDecimal(tenthMarks) : null);
                    pstmt.setNull(7, java.sql.Types.DECIMAL); // CET marks not applicable
                } else if ("Degree".equals(courseType)) {
                    pstmt.setNull(6, java.sql.Types.DECIMAL); // 10th marks not applicable
                    pstmt.setBigDecimal(7, cetMarks != null && !cetMarks.isEmpty() ? new BigDecimal(cetMarks) : null);
                }

                pstmt.setDate(8, java.sql.Date.valueOf(dob));
                pstmt.setString(9, photoPath);
                pstmt.setString(10, aadharCardPath);
                pstmt.setString(11, tenthMarksheetPath);
                pstmt.setString(12, cetMarksheetPath);
                pstmt.setInt(13, Integer.parseInt(userId));

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    response.sendRedirect("payment.html");
                } else {
                    out.println("<h1>Error: Failed to insert the student record.</h1>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
