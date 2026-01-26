import Models.Supplier;
import Models.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class SuppliersPage {

    private Stage stage;
    private TableView<Supplier> table;

    public SuppliersPage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        Label title = new Label("🏭 Manage Suppliers");

        table = new TableView<>();
        setupTable();
        loadSuppliers();

        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        Button addBtn = new Button("Add Supplier");
        addBtn.setOnAction(e -> addSupplier(nameField, emailField, phoneField));

        Button editBtn = new Button("Edit Supplier");
        editBtn.setOnAction(e -> editSupplier(nameField, emailField, phoneField));

        Button delBtn = new Button("Delete Supplier");
        delBtn.setOnAction(e -> deleteSupplier());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            WelcomePage wp = new WelcomePage(stage, UserSession.username);
            stage.setScene(new Scene(wp.getView(), 450, 350));
        });

        VBox layout = new VBox(12, title, table,
                nameField, emailField, phoneField,
                addBtn, editBtn, delBtn, backBtn);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 600, 500);
    }

    private void setupTable() {
        TableColumn<Supplier, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("supplierId"));

        TableColumn<Supplier, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<Supplier, String> email = new TableColumn<>("Email");
        email.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));

        TableColumn<Supplier, String> phone = new TableColumn<>("Phone");
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        table.getColumns().addAll(id, name, email, phone);
    }

    private void loadSuppliers() {
        ObservableList<Supplier> list = FXCollections.observableArrayList();

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM suppliers");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_email"),
                        rs.getString("phone")
                ));
            }

            table.setItems(list);
        } catch (Exception e) {
            System.out.println("Load suppliers error: " + e.getMessage());
        }
    }

    private void addSupplier(TextField n, TextField e, TextField p) {
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO suppliers(supplier_name, contact_email, phone) VALUES (?, ?, ?)")) {

            ps.setString(1, n.getText());
            ps.setString(2, e.getText());
            ps.setString(3, p.getText());
            ps.executeUpdate();

            loadSuppliers();
        } catch (Exception ex) {
            System.out.println("Add supplier error: " + ex.getMessage());
        }
    }

    private void editSupplier(TextField n, TextField e, TextField p) {
        Supplier s = table.getSelectionModel().getSelectedItem();
        if (s == null) return;

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE suppliers SET supplier_name=?, contact_email=?, phone=? WHERE supplier_id=?")) {

            ps.setString(1, n.getText());
            ps.setString(2, e.getText());
            ps.setString(3, p.getText());
            ps.setInt(4, s.getSupplierId());
            ps.executeUpdate();

            loadSuppliers();
        } catch (Exception ex) {
            System.out.println("Edit supplier error: " + ex.getMessage());
        }
    }

    private void deleteSupplier() {
        Supplier s = table.getSelectionModel().getSelectedItem();
        if (s == null) return;

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM suppliers WHERE supplier_id=?")) {

            ps.setInt(1, s.getSupplierId());
            ps.executeUpdate();

            loadSuppliers();
        } catch (Exception ex) {
            System.out.println("Delete supplier error: " + ex.getMessage());
        }
    }
}
