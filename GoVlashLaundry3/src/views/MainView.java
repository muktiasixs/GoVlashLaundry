package views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controllers.NotificationController;
import controllers.ServiceController;
import controllers.TransactionController;
import controllers.UserController;
import models.Service;
import models.Transaction;
import models.User;

public class MainView extends JFrame implements ActionListener {

    private User currentUser;
    

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton logoutBtn;
    

    private JPanel bottomPanel, actionPanel;
    private JComboBox<String> serviceCombo; 
    private JComboBox<String> staffCombo;   
    

    private JButton actionBtn; 
    private JButton notifBtn;
    

    private JButton addServiceBtn, editServiceBtn, deleteServiceBtn, addEmployeeBtn, sendNotifBtn;

    public MainView(User user) {
        this.currentUser = user;
        init();
    }

    private void init() {
        setTitle("GoVlash Dashboard - " + currentUser.getUserRole());
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLbl = new JLabel("Welcome, " + currentUser.getUserName() + " (" + currentUser.getUserRole() + ")");
        welcomeLbl.setFont(new Font("Arial", Font.BOLD, 16));
        
        logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(this);
        
        headerPanel.add(welcomeLbl, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Customer", "Service", "Staff", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        loadTableData();
        add(new JScrollPane(table), BorderLayout.CENTER);

        bottomPanel = new JPanel(new BorderLayout());
        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        setupRoleBasedActions(); 
        
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setupRoleBasedActions() {
        String role = currentUser.getUserRole();
        
        if (role.equals("Customer")) {
            actionPanel.add(new JLabel("Choose Service: "));
            serviceCombo = new JComboBox<>();
            refreshServiceCombo();
            
            actionBtn = new JButton("Place Order");
            notifBtn = new JButton("My Notifications");
            
            actionBtn.addActionListener(this);
            notifBtn.addActionListener(e -> new NotificationView(currentUser.getUserId()));
            
            actionPanel.add(serviceCombo);
            actionPanel.add(actionBtn);
            actionPanel.add(Box.createHorizontalStrut(20));
            actionPanel.add(notifBtn);
            
        } else if (role.equals("Receptionist")) {
            actionPanel.add(new JLabel("Assign to Staff: "));
            staffCombo = new JComboBox<>();
            
            Vector<User> staffs = UserController.getInstance().getStaffs();
            for (User s : staffs) {
                staffCombo.addItem(s.getUserId() + " - " + s.getUserName());
            }
            
            actionBtn = new JButton("Assign Selected Order");
            actionBtn.addActionListener(this);
            
            actionPanel.add(staffCombo);
            actionPanel.add(actionBtn);
            
        } else if (role.equals("Laundry Staff")) {
            actionBtn = new JButton("Mark Selected as Finished");
            actionBtn.addActionListener(this);
            actionPanel.add(actionBtn);
            
        } else if (role.equals("Admin")) {
            addServiceBtn = new JButton("Add Service");
            editServiceBtn = new JButton("Edit Service");   
            deleteServiceBtn = new JButton("Delete Service"); 
            addEmployeeBtn = new JButton("Add Employee");
            sendNotifBtn = new JButton("Send Notification");
            
            addServiceBtn.addActionListener(this);
            editServiceBtn.addActionListener(this);
            deleteServiceBtn.addActionListener(this);
            addEmployeeBtn.addActionListener(this);
            sendNotifBtn.addActionListener(this);
            
            actionPanel.add(addServiceBtn);
            actionPanel.add(editServiceBtn);
            actionPanel.add(deleteServiceBtn);
            actionPanel.add(new JSeparator(SwingConstants.VERTICAL));
            actionPanel.add(addEmployeeBtn);
            actionPanel.add(new JSeparator(SwingConstants.VERTICAL));
            actionPanel.add(sendNotifBtn);
        }
    }
    
    private void loadTableData() {
        tableModel.setRowCount(0); 
        Vector<Transaction> transactions = TransactionController.getInstance().getTransactions();
        
        for (Transaction t : transactions) {
            Object[] row = {
                t.getTransactionId(), 
                t.getCustomerName(),
                t.getServiceName(),
                t.getStaffName(),
                t.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void refreshServiceCombo() {
        if(serviceCombo == null) return;
        serviceCombo.removeAllItems();
        Vector<Service> services = ServiceController.getInstance().getAllServices();
        for (Service s : services) {
            serviceCombo.addItem(s.getServiceId() + " - " + s.getServiceName() + " (" + s.getServicePrice() + ")");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == logoutBtn) {
            UserController.getInstance().logout();
            new LoginView();
            this.dispose();
            return;
        }
        
        String role = currentUser.getUserRole();

        if (role.equals("Admin")) {
            if (e.getSource() == addServiceBtn) {
                showAddServiceDialog();
                refreshServiceCombo();
            } 
            else if (e.getSource() == editServiceBtn) {
                handleEditService(); 
            } 
            else if (e.getSource() == deleteServiceBtn) {
                handleDeleteService(); 
            } 
            else if (e.getSource() == addEmployeeBtn) {
                showAddEmployeeDialog();
            } 
            else if (e.getSource() == sendNotifBtn) {
                handleSendNotification();
            }
            return;
        }

        if (e.getSource() == actionBtn) {
            if (role.equals("Customer")) {
                String selectedService = (String) serviceCombo.getSelectedItem();
                if (selectedService != null) {
                    int serviceId = Integer.parseInt(selectedService.split(" - ")[0]);
                    TransactionController.getInstance().createTransaction(serviceId);
                    JOptionPane.showMessageDialog(this, "Order Created Successfully!");
                    loadTableData(); 
                }
                
            } else if (role.equals("Receptionist")) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a transaction first!");
                    return;
                }
                
                int transId = (int) tableModel.getValueAt(selectedRow, 0);
                String status = (String) tableModel.getValueAt(selectedRow, 4);
                
                if (!status.equals("Pending")) {
                    JOptionPane.showMessageDialog(this, "Only 'Pending' transactions can be assigned.");
                    return;
                }

                String selectedStaff = (String) staffCombo.getSelectedItem();
                if (selectedStaff != null) {
                    int staffId = Integer.parseInt(selectedStaff.split(" - ")[0]);
                    TransactionController.getInstance().assignStaff(transId, staffId);
                    JOptionPane.showMessageDialog(this, "Staff Assigned!");
                    loadTableData();
                }
                
            } else if (role.equals("Laundry Staff")) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a transaction!");
                    return;
                }
                
                int transId = (int) tableModel.getValueAt(selectedRow, 0);
                TransactionController.getInstance().completeTransaction(transId);
                JOptionPane.showMessageDialog(this, "Task Completed!");
                loadTableData();
            }
        }
    }

    private void handleEditService() {
        // 1. Ambil semua service
        Vector<Service> services = ServiceController.getInstance().getAllServices();
        
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No services available to edit.");
            return;
        }

        JComboBox<String> editCombo = new JComboBox<>();
        for (Service s : services) {
            editCombo.addItem(s.getServiceId() + " - " + s.getServiceName() + " [Rp " + s.getServicePrice() + "]");
        }

        Object[] message = { "Select Service to Edit:", editCombo };
        int option = JOptionPane.showConfirmDialog(this, message, "Edit Service", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {

            int selectedIndex = editCombo.getSelectedIndex();
            Service selectedService = services.get(selectedIndex);

            showEditForm(selectedService);
        }
    }
    
    private void showEditForm(Service s) {
        JTextField nameField = new JTextField(s.getServiceName());
        JTextField priceField = new JTextField(String.valueOf(s.getServicePrice()));
        JTextField durField = new JTextField(String.valueOf(s.getServiceDuration()));
        
        Object[] message = {
            "Edit Name:", nameField,
            "Edit Price:", priceField,
            "Edit Duration (Days):", durField
        };
        
        int opt = JOptionPane.showConfirmDialog(this, message, "Editing: " + s.getServiceName(), JOptionPane.OK_CANCEL_OPTION);
        
        if (opt == JOptionPane.OK_OPTION) {
            try {
                ServiceController.getInstance().updateService(
                    s.getServiceId(), 
                    nameField.getText(), 
                    Integer.parseInt(priceField.getText()), 
                    Integer.parseInt(durField.getText())
                );
                JOptionPane.showMessageDialog(this, "Service Updated Successfully!");
                refreshServiceCombo(); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Input: " + e.getMessage());
            }
        }
    }

    private void handleDeleteService() {
        Vector<Service> services = ServiceController.getInstance().getAllServices();
        
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No services available to delete.");
            return;
        }

        JComboBox<String> delCombo = new JComboBox<>();
        for (Service s : services) {
            delCombo.addItem(s.getServiceId() + " - " + s.getServiceName());
        }

        Object[] message = { "Select Service to Delete:", delCombo };
        int option = JOptionPane.showConfirmDialog(this, message, "Delete Service", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            int selectedIndex = delCombo.getSelectedIndex();
            Service selectedService = services.get(selectedIndex);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '" + selectedService.getServiceName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                ServiceController.getInstance().deleteService(selectedService.getServiceId());
                JOptionPane.showMessageDialog(this, "Service Deleted!");
                refreshServiceCombo();
            }
        }
    }
    
    private void showAddServiceDialog() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField durField = new JTextField();
        
        Object[] message = { "Name:", nameField, "Price:", priceField, "Duration (Days):", durField };
        int opt = JOptionPane.showConfirmDialog(this, message, "Add Service", JOptionPane.OK_CANCEL_OPTION);
        
        if (opt == JOptionPane.OK_OPTION) {
            try {
                ServiceController.getInstance().addService(nameField.getText(), Integer.parseInt(priceField.getText()), Integer.parseInt(durField.getText()));
                JOptionPane.showMessageDialog(this, "Service Added!");
            } catch(Exception e) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        }
    }
    
    private void showAddEmployeeDialog() {
        JTextField userField = new JTextField();
        JTextField passField = new JTextField();
        String[] roles = {"Laundry Staff", "Receptionist", "Admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        
        Object[] message = { "Username:", userField, "Password:", passField, "Role:", roleBox };
        int opt = JOptionPane.showConfirmDialog(this, message, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        
        if(opt == JOptionPane.OK_OPTION) {
            String u = userField.getText();
            String email = u.toLowerCase().replaceAll("\\s+", "") + "@govlash.com";
            String res = UserController.getInstance().registerEmployee(u, email, passField.getText(), (String)roleBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, res);
        }
    }
    
    private void handleSendNotification() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a Finished transaction first!");
            return;
        }
        
        String status = (String) tableModel.getValueAt(selectedRow, 4);
        if(!status.equals("Finished")) {
            JOptionPane.showMessageDialog(this, "Transaction is not Finished yet.");
            return;
        }
        
        int transId = (int) tableModel.getValueAt(selectedRow, 0);
        int userId = getUserIdFromTransaction(transId);
        
        if(userId != -1) {
            NotificationController.getInstance().sendNotification(userId);
            JOptionPane.showMessageDialog(this, "Notification Sent!");
        } else {
            JOptionPane.showMessageDialog(this, "Error finding user.");
        }
    }
    
    private int getUserIdFromTransaction(int transId) {
        try {
            java.sql.ResultSet rs = utils.Connect.getInstance().execQuery("SELECT user_id FROM transactions WHERE transaction_id=" + transId);
            if(rs.next()) return rs.getInt("user_id");
        } catch(Exception e) { e.printStackTrace(); }
        return -1;
    }
}