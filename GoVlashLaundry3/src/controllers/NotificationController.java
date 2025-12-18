package controllers;

import java.sql.ResultSet;
import java.util.Vector;
import models.Notification;
import utils.Connect;

class NotificationModel {
    int id; String message; boolean isRead;
    public NotificationModel(int id, String message, boolean isRead) {
        this.id = id; this.message = message; this.isRead = isRead;
    }
    public String toString() { return (isRead ? "[Read] " : "[New] ") + message; }
    public int getId() { return id; }
    public String getMessage() { return message; }
}

public class NotificationController {
    private static NotificationController instance;
    private Connect con = Connect.getInstance();

    public static NotificationController getInstance() {
        if(instance == null) instance = new NotificationController();
        return instance;
    }

    public void sendNotification(int userId) {
        String msg = "Your order is finished and ready for pickup. Thank you for choosing our service!";
        String query = String.format("INSERT INTO notifications (user_id, message, created_at, is_read) VALUES (%d, '%s', CURDATE(), FALSE)", userId, msg);
        con.execUpdate(query);
    }
    

    public Vector<Notification> getUserNotifications(int userId) {
        Vector<Notification> list = new Vector<>();
        String query = "SELECT * FROM notifications WHERE user_id = " + userId + " ORDER BY notification_id DESC";
        ResultSet rs = con.execQuery(query);
        try {
            while(rs.next()) {
                list.add(new Notification(
                    rs.getInt("notification_id"),
                    rs.getString("message"),
                    rs.getBoolean("is_read")
                ));
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }

    public void markAsRead(int notifId) {
        con.execUpdate("UPDATE notifications SET is_read = TRUE WHERE notification_id = " + notifId);
    }

    public void deleteNotification(int notifId) {
        con.execUpdate("DELETE FROM notifications WHERE notification_id = " + notifId);
    }
}