package models;

public class User {
    private int userId;
    private String userName;
    private String userRole;
    private String userEmail;

    public User(int userId, String userName, String userEmail, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }

    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserRole() { return userRole; }
    public String getUserEmail() { return userEmail; }
}