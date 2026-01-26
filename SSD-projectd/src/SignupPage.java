import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignupPage {

    private VBox view;
    private Stage stage;

    public SignupPage(Stage stage) {
        this.stage = stage;

        Label title = new Label("Register New Staff Account");

        TextField firstName = new TextField();
        firstName.setPromptText("First Name");

        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");

        TextField username = new TextField();
        username.setPromptText("Choose Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Create Password");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("manager", "employee");
        roleCombo.setValue("employee");

        Button createBtn = new Button("Create Account");
        Button backBtn = new Button("Back to Dashboard");

        Label message = new Label();

        createBtn.setOnAction(e -> {

            if (username.getText().isEmpty() || password.getText().isEmpty() ||
                    firstName.getText().isEmpty() || lastName.getText().isEmpty()) {

                message.setText("Please fill all fields.");
                return;
            }

            if (createUser(firstName.getText(), lastName.getText(),
                    username.getText(), password.getText(), roleCombo.getValue())) {

                message.setText("Staff account created successfully!");
            } else {
                message.setText("Error creating account.");
            }
        });

        backBtn.setOnAction(e -> {
            // Return to WelcomePage
            LoginPage login = new LoginPage(stage);
            stage.setScene(new Scene(login.getView(), 400, 300));
        });

        view = new VBox(10, title, firstName, lastName, username, password,
                roleCombo, createBtn, backBtn, message);

        view.setAlignment(Pos.CENTER);
    }

    public VBox getView() {
        return view;
    }

    private boolean createUser(String fn, String ln, String un, String pw, String role) {

        String salt = SecurityUtils.generateSalt();
        String hashed = SecurityUtils.hashPassword(pw, salt);

        String sql = "INSERT INTO users(firstname, lastname, username, password, salt, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fn);
            ps.setString(2, ln);
            ps.setString(3, un);
            ps.setString(4, hashed);
            ps.setString(5, salt);
            ps.setString(6, role);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Signup error: " + e.getMessage());
            return false;
        }
    }
}
