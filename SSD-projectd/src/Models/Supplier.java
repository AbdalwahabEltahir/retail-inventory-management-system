package Models;

public class Supplier {
    private int supplierId;
    private String supplierName;
    private String contactEmail;
    private String phone;

    public Supplier(int supplierId, String supplierName, String contactEmail, String phone) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactEmail = contactEmail;
        this.phone = phone;
    }

    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getContactEmail() { return contactEmail; }
    public String getPhone() { return phone; }
}
