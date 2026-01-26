import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ChangePasswordPage {

    private VBox view;
    private Stage stage;
    private String username;

    public ChangePasswordPage(Stage stage, String username) {
        this.stage = stage;
        this.username = username;

        Label title = new Label("Change Password for: " + username);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter New Password");

        Button changePasswordBtn = new Button("Update Password");
        Label statusMsg = new Label();

        // ✅ NEW: Back to Login button
        Button backToLoginBtn = new Button("Back to Login");
        backToLoginBtn.setOnAction(e -> {
            LoginPage login = new LoginPage(stage);
            stage.setScene(new Scene(login.getView(), 400, 300));
        });

        // Change Password Logic
        changePasswordBtn.setOnAction(e -> {
            String newPw = newPasswordField.getText();

            if (newPw.isEmpty()) {
                statusMsg.setText("Password cannot be empty.");
                return;
            }

            if (updatePassword(newPw)) {
                statusMsg.setText("Password updated successfully!");

            } else {
                statusMsg.setText("Failed to update password.");
            }
        });

        view = new VBox(12, title, newPasswordField, changePasswordBtn, statusMsg, backToLoginBtn);
        view.setAlignment(Pos.CENTER);
    }

    public VBox getView() {
        return view;
    }

    private boolean updatePassword(String newPw) {
        String salt = SecurityUtils.generateSalt();
        String hashedPw = SecurityUtils.hashPassword(newPw, salt);

        String sql = "UPDATE users SET password = ?, salt = ? WHERE username = ?";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hashedPw);
            ps.setString(2, salt);
            ps.setString(3, username);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            System.out.println("Password update error: " + e.getMessage());
            return false;
        }
    }
}
