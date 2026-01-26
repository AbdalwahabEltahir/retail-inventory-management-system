import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Retail Inventory Management System");

        // Load Login Page
        LoginPage loginPage = new LoginPage(stage);
        Scene scene = new Scene(loginPage.getView(), 400, 300);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);  // Start JavaFX
    }
}
