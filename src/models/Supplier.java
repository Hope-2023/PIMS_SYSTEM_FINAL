package models;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;

    public Supplier(int supplierId, String name, String contactPerson, String phone, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getContactPerson() { return contactPerson; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
}