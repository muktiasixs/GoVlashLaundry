package controllers;

import java.sql.ResultSet;
import java.util.Vector;
import models.User;
import utils.Connect;

public class UserController {
    private static UserController instance;
    private Connect con = Connect.getInstance();
    private User currentUser;

    public static UserController getInstance() {
        if(instance == null) instance = new UserController();
        return instance;
    }

    public User getCurrentUser() { return currentUser; }
    public void logout() { currentUser = null; }

    public String login(String email, String password) {
        if(email.isEmpty() || password.isEmpty()) return "All fields must be filled";
        
        String query = String.format("SELECT * FROM users WHERE user_email = '%s' AND user_password = '%s'", email, password);
        ResultSet rs = con.execQuery(query);
        try {
            if(rs.next()) {
                currentUser = new User(
                    rs.getInt("user_id"), rs.getString("user_name"), 
                    rs.getString("user_email"), rs.getString("user_role")
                );
                return "Success";
            }
        } catch(Exception e) { e.printStackTrace(); }
        return "Invalid Credentials";
    }

    public String register(String name, String email, String pass, String confPass, String gender, int age) {
        if(name.isEmpty() || email.isEmpty() || pass.isEmpty()) return "All fields must be filled";
        if(!email.endsWith("@email.com")) return "Email must end with @email.com"; // Validasi tanpa Regex
        if(pass.length() < 6) return "Password must be at least 6 chars";
        if(!pass.equals(confPass)) return "Confirm Password must match";
        if(!gender.equals("Male") && !gender.equals("Female")) return "Gender must be Male or Female";
        if(age < 12) return "Must be at least 12 years old";

        String query = String.format("INSERT INTO users (user_name, user_password, user_email, user_role, user_gender, user_dob) VALUES ('%s', '%s', '%s', 'Customer', '%s', DATE_SUB(CURDATE(), INTERVAL %d YEAR))", name, pass, email, gender, age);
        con.execUpdate(query);
        return "Success";
    }
    
    public String registerEmployee(String name, String email, String pass, String role) {
        if(name.isEmpty() || pass.isEmpty()) return "Fields cannot be empty";
        if(pass.length() < 6) return "Password min 6 chars";
        if(!email.endsWith("@govlash.com")) return "Email must end with @govlash.com";
        
        String query = String.format("INSERT INTO users (user_name, user_email, user_password, user_role) VALUES ('%s', '%s', '%s', '%s')", 
                name, email, pass, role);
        con.execUpdate(query);
        return "Employee Added Successfully";
    }

    public Vector<User> getStaffs() {
        Vector<User> list = new Vector<>();
        try {
            ResultSet rs = con.execQuery("SELECT * FROM users WHERE user_role = 'Laundry Staff'");
            while(rs.next()) list.add(new User(rs.getInt("user_id"), rs.getString("user_name"), rs.getString("user_email"), "Laundry Staff"));
        } catch(Exception e){}
        return list;
    }
}