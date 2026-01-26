import Models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import Models.UserSession;


public class ProductsPage {

    private Stage stage;
    private TableView<Product> table;

    public ProductsPage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        Label title = new Label("📦 Manage Products");

        table = new TableView<>();
        loadProducts();

        TableColumn<Product, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Integer> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(colId, colName, colQty, colPrice);

        TextField nameField = new TextField();
        TextField qtyField = new TextField();
        TextField priceField = new TextField();

        nameField.setPromptText("Name");
        qtyField.setPromptText("Qty");
        priceField.setPromptText("Price");

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> addProduct(nameField, qtyField, priceField));

        Button editBtn = new Button("Update");
        editBtn.setOnAction(e -> updateProduct(nameField, qtyField, priceField));

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> deleteProduct());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e ->
                stage.setScene(new Scene(new WelcomePage(stage, UserSession.username).getView(), 500, 400))
        );

        HBox actions = new HBox(10, addBtn, editBtn, delBtn, backBtn);
        actions.setAlignment(Pos.CENTER);

        VBox layout = new VBox(12, title, table, nameField, qtyField, priceField, actions);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 600, 450);
    }

    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try (Connection con = DBUtils.establishConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")));
            }

            table.setItems(products);

        } catch (Exception e) {
            System.out.println("Load products error: " + e.getMessage());
        }
    }

    private void addProduct(TextField name, TextField qty, TextField price) {
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO products(name, quantity, price) VALUES (?, ?, ?)")) {

            ps.setString(1, name.getText());
            ps.setInt(2, Integer.parseInt(qty.getText()));
            ps.setDouble(3, Double.parseDouble(price.getText()));
            ps.executeUpdate();

            loadProducts();
            name.clear();
            qty.clear();
            price.clear();

        } catch (Exception e) {
            System.out.println("Add product error: " + e.getMessage());
        }
    }

    private void updateProduct(TextField name, TextField qty, TextField price) {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE products SET name=?, quantity=?, price=? WHERE product_id=?")) {

            ps.setString(1, name.getText());
            ps.setInt(2, Integer.parseInt(qty.getText()));
            ps.setDouble(3, Double.parseDouble(price.getText()));
            ps.setInt(4, selected.getProductId());
            ps.executeUpdate();

            loadProducts();

        } catch (Exception e) {
            System.out.println("Update product error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE product_id=?")) {

            ps.setInt(1, selected.getProductId());
            ps.executeUpdate();
            loadProducts();

        } catch (Exception e) {
            System.out.println("Delete product error: " + e.getMessage());
        }
    }
}
