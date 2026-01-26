import Models.UserSession;
import Models.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class SalesPage {

    private Stage stage;
    private TableView<Sale> table;

    public SalesPage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        Label title = new Label("🛒 Manage Sales");

        table = new TableView<>();
        setupTable();
        loadSales();

        TextField productIdField = new TextField();
        productIdField.setPromptText("Product ID");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity Sold");

        Button addBtn = new Button("Add Sale");
        addBtn.setOnAction(e -> addSale(productIdField, qtyField));

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            WelcomePage wp = new WelcomePage(stage, UserSession.username);
            stage.setScene(new Scene(wp.getView(), 450, 350));
        });

        VBox layout = new VBox(12, title, table, productIdField, qtyField, addBtn, backBtn);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 600, 450);
    }

    private void setupTable() {
        TableColumn<Sale, Integer> idCol = new TableColumn<>("Sale ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));

        TableColumn<Sale, Integer> pidCol = new TableColumn<>("Product ID");
        pidCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Sale, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));

        TableColumn<Sale, Double> priceCol = new TableColumn<>("Total Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        table.getColumns().addAll(idCol, pidCol, qtyCol, priceCol);
    }

    private void loadSales() {
        ObservableList<Sale> sales = FXCollections.observableArrayList();

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM sales");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sales.add(new Sale(
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity_sold"),
                        rs.getDouble("total_price")
                ));
            }

            table.setItems(sales);

        } catch (Exception e) {
            System.out.println("Load sales error: " + e.getMessage());
        }
    }

    private void addSale(TextField productId, TextField qty) {
        int pid = Integer.parseInt(productId.getText());
        int quantity = Integer.parseInt(qty.getText());
        double pricePerUnit = 0;

        try (Connection con = DBUtils.establishConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT price FROM products WHERE product_id=?");
            ps.setInt(1, pid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) pricePerUnit = rs.getDouble("price");

            double totalPrice = pricePerUnit * quantity;

            PreparedStatement insertSale = con.prepareStatement(
                    "INSERT INTO sales(product_id, quantity_sold, total_price) VALUES (?, ?, ?)");

            insertSale.setInt(1, pid);
            insertSale.setInt(2, quantity);
            insertSale.setDouble(3, totalPrice);
            insertSale.executeUpdate();

            PreparedStatement reduce = con.prepareStatement(
                    "UPDATE products SET quantity = quantity - ? WHERE product_id=?");

            reduce.setInt(1, quantity);
            reduce.setInt(2, pid);
            reduce.executeUpdate();

        } catch (Exception e) {
            System.out.println("Add sale error: " + e.getMessage());
        }

        loadSales();
    }
}
