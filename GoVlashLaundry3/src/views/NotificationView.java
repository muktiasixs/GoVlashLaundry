package views;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import controllers.NotificationController;
import models.Notification;

public class NotificationView extends JFrame {
    private int userId;
    private JList<Notification> notifList;
    private DefaultListModel<Notification> listModel;
    
    public NotificationView(int userId) {
        this.userId = userId;
        init();
    }
    
    private void init() {
        setTitle("My Notifications");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        listModel = new DefaultListModel<>();
        notifList = new JList<>(listModel);
        loadNotifications();
        
        add(new JScrollPane(notifList), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton readBtn = new JButton("Read Details");
        JButton deleteBtn = new JButton("Delete");
        
        readBtn.addActionListener(e -> {
            Notification sel = notifList.getSelectedValue();
            if(sel != null) {
                
                JOptionPane.showMessageDialog(this, sel.getMessage(), "Notification Detail", JOptionPane.INFORMATION_MESSAGE);
                NotificationController.getInstance().markAsRead(sel.getId());
                loadNotifications(); 
            }
        });
        
        deleteBtn.addActionListener(e -> {
            Notification sel = notifList.getSelectedValue();
            if(sel != null) {
                NotificationController.getInstance().deleteNotification(sel.getId());
                loadNotifications(); 
            }
        });
        
        btnPanel.add(readBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    private void loadNotifications() {
        listModel.clear();
        Vector<Notification> data = NotificationController.getInstance().getUserNotifications(userId);
        for(Notification n : data) {
            listModel.addElement(n);
        }
    }
}