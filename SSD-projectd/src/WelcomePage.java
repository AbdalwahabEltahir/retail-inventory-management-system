import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Models.UserSession;

public class WelcomePage {

    private VBox view;
    private Stage stage;
    private String username;

    public WelcomePage(Stage stage, String username) {
        this.stage = stage;
        this.username = username;

        Label title = new Label("Welcome, " + username + "!");

        // Save logged in user for other pages
        UserSession.username = username;

        // Get role from DB
        String role = DBUtils.getUserRole(username);
        boolean isManager = "manager".equalsIgnoreCase(role);

        // =====================================================
        // COMMON BUTTONS (Manager + Employee)
        // =====================================================
        Button manageProductsBtn = new Button("📦 Manage Products");
        manageProductsBtn.setOnAction(e -> {
            ProductsPage productsPage = new ProductsPage(stage);
            stage.setScene(productsPage.getScene());
        });

        Button manageSalesBtn = new Button("🛒 Manage Sales");
        manageSalesBtn.setOnAction(e -> {
            SalesPage salesPage = new SalesPage(stage);
            stage.setScene(salesPage.getScene());
        });

        // =====================================================
        // MANAGER-ONLY BUTTONS
        // =====================================================
        Button suppliersBtn = new Button("🏭 Manage Suppliers");
        suppliersBtn.setOnAction(e -> {
            SuppliersPage sp = new SuppliersPage(stage);
            stage.setScene(sp.getScene());
        });

        Button createStaffBtn = new Button("➕ Create Account");
        createStaffBtn.setOnAction(e -> {
            SignupPage sp = new SignupPage(stage);
            stage.setScene(new Scene(sp.getView(), 400, 500));
        });

        // =====================================================
        // ACCOUNT OPTIONS
        // =====================================================
        Button changePassBtn = new Button("Change Password");
        changePassBtn.setOnAction(e -> {
            ChangePasswordPage cp = new ChangePasswordPage(stage, username);
            stage.setScene(new Scene(cp.getView(), 400, 250));
        });

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            LoginPage login = new LoginPage(stage);
            stage.setScene(new Scene(login.getView(), 400, 300));
        });

        // =====================================================
        // BUILD UI
        // =====================================================
        view = new VBox(12);
        view.setAlignment(Pos.CENTER);

        view.getChildren().add(title);
        view.getChildren().add(manageProductsBtn);
        view.getChildren().add(manageSalesBtn);

        // Add manager-only buttons if role is manager
        if (isManager) {
            view.getChildren().add(suppliersBtn);
            view.getChildren().add(createStaffBtn);
        }

        view.getChildren().add(changePassBtn);
        view.getChildren().add(logoutBtn);
    }

    public VBox getView() {
        return view;
    }
}
