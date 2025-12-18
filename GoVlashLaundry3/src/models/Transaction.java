package models;

public class Transaction {
    private int transactionId;
    private String customerName;
    private String serviceName;
    private String staffName;
    private String status;
    
    // Constructor 
    public Transaction(int transactionId, String customerName, String serviceName, String staffName, String status) {
        this.transactionId = transactionId;
        this.customerName = customerName;
        this.serviceName = serviceName;
        this.staffName = (staffName == null) ? "Unassigned" : staffName;
        this.status = status;
    }

    // Getters 
    public int getTransactionId() { return transactionId; }
    public String getCustomerName() { return customerName; }
    public String getServiceName() { return serviceName; }
    public String getStaffName() { return staffName; }
    public String getStatus() { return status; }
}