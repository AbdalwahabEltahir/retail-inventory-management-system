import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBUtils {

    private static final String URL = "jdbc:mysql://localhost:3306/test";
    private static final String APP_USERNAME = "root";
    private static final String APP_PASSWORD = ""; // use your DB password here

    // -----------------------------------------------
    // Establish Database Connection
    // -----------------------------------------------
    public static Connection establishConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, APP_USERNAME, APP_PASSWORD);
            System.out.println("✅ Database Connected");
            return con;
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Error: " + e.getMessage());
            return null;
        }
    }

    // -----------------------------------------------
    // Close Connection + Statement
    // -----------------------------------------------
    public static void closeConnection(Connection con, Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
                System.out.println("🔻 Statement closed");
            } catch (SQLException e) {
                System.err.println("❌ Error closing Statement: " + e.getMessage());
            }
        }

        if (con != null) {
            try {
                con.close();
                System.out.println("🔻 Connection closed");
            } catch (SQLException e) {
                System.err.println("❌ Error closing Connection: " + e.getMessage());
            }
        }
    }

    // -----------------------------------------------
    // Close Connection + PreparedStatement
    // -----------------------------------------------
    public static void closeConnection(Connection con, PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
                System.out.println("🔻 PreparedStatement closed");
            } catch (SQLException e) {
                System.err.println("❌ Error closing PreparedStatement: " + e.getMessage());
            }
        }
        closeConnection(con, (Statement) null);
    }

    // -----------------------------------------------
    // Overloaded closeConnection (only connection)
    // -----------------------------------------------
    public static void closeConnection(Connection con) {
        closeConnection(con, (Statement) null);
    }

    // -----------------------------------------------
    // REQUIRED METHOD: Get user role (manager/employee)
    // Used by WelcomePage to restrict access
    // -----------------------------------------------
    public static String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";

        try (Connection con = establishConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); // manager or employee
            }

        } catch (SQLException e) {
            System.err.println("❌ Role Fetch Error: " + e.getMessage());
        }

        return null; // user not found OR error
    }
}
