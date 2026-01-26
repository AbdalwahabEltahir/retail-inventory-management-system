import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage {

    private VBox view;
    private Stage stage;

    public LoginPage(Stage stage) {
        this.stage = stage;

        Label title = new Label("Retail Inventory System - Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Button loginBtn = new Button("Login");
        Label message = new Label();

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if (validateLogin(user, pass)) {
                message.setText("Login successful!");

                WelcomePage welcomePage = new WelcomePage(stage, user);
                stage.setScene(new Scene(welcomePage.getView(), 450, 350));

            } else {
                message.setText("Invalid username or password.");
            }
        });

        view = new VBox(10, title, usernameField, passwordField, loginBtn, message);
        view.setAlignment(Pos.CENTER);
        view.setSpacing(10);
    }

    public VBox getView() {
        return view;
    }

    private boolean validateLogin(String username, String password) {
        String sql = "SELECT password, salt FROM users WHERE username = ?";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String storedSalt = rs.getString("salt");

                String enteredHash = SecurityUtils.hashPassword(password, storedSalt);
                return storedHash.equals(enteredHash);
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return false;
    }
}
