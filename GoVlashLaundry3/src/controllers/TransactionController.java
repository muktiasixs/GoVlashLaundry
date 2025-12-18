package controllers;

import java.sql.ResultSet;
import java.util.Vector;
import models.Transaction;
import utils.Connect;

public class TransactionController {
    private static TransactionController instance;
    private Connect con = Connect.getInstance();

    public static TransactionController getInstance() {
        if (instance == null) instance = new TransactionController();
        return instance;
    }

    public Vector<Transaction> getTransactions() {
        Vector<Transaction> list = new Vector<>();
        String role = UserController.getInstance().getCurrentUser().getUserRole(); 
        int uid = UserController.getInstance().getCurrentUser().getUserId();
        

        String query = "SELECT t.transaction_id, u.user_name AS custName, s.service_name, st.user_name AS staffName, t.transaction_status " +
                       "FROM transactions t " +
                       "JOIN users u ON t.user_id = u.user_id " +
                       "JOIN services s ON t.service_id = s.service_id " +
                       "LEFT JOIN users st ON t.staff_id = st.user_id ";


        if (role.equals("Customer")) {
            query += " WHERE t.user_id = " + uid;
        } else if (role.equals("Laundry Staff")) { 
            query += " WHERE t.staff_id = " + uid;
        } else if (role.equals("Receptionist")) {
        }
        
        query += " ORDER BY t.transaction_id DESC"; 

        ResultSet rs = con.execQuery(query);
        try {
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("transaction_id"), 
                    rs.getString("custName"), 
                    rs.getString("service_name"),
                    rs.getString("staffName"), 
                    rs.getString("transaction_status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void assignStaff(int transactionId, int staffId) {
        int recepId = UserController.getInstance().getCurrentUser().getUserId();

        String q = String.format("UPDATE transactions SET staff_id = %d, receptionist_id = %d, transaction_status = 'In Progress' WHERE transaction_id = %d", 
                staffId, recepId, transactionId);
        con.execUpdate(q);
    }
    

    public void completeTransaction(int transactionId) {

        con.execUpdate("UPDATE transactions SET transaction_status = 'Finished' WHERE transaction_id = " + transactionId);

        sendNotification(transactionId);
    }
    
    private void sendNotification(int transId) {
        try {
            ResultSet rs = con.execQuery("SELECT user_id FROM transactions WHERE transaction_id=" + transId);
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String msg = "Your order is finished and ready for pickup. Thank you!";
                con.execUpdate(String.format("INSERT INTO notifications (user_id, message, created_at) VALUES (%d, '%s', CURDATE())", userId, msg));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    

    public void createTransaction(int serviceId) {
        int uid = UserController.getInstance().getCurrentUser().getUserId();
        String q = String.format("INSERT INTO transactions (user_id, service_id, transaction_date, transaction_status) VALUES (%d, %d, CURDATE(), 'Pending')", uid, serviceId);
        con.execUpdate(q);
    }
}