package views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder; 

import controllers.UserController;
import models.User;

public class LoginView extends JFrame implements ActionListener {

    private JTextField userTxt;
    private JPasswordField passTxt;
    private JButton loginBtn, registerBtn;

    public LoginView() {
        init();
    }

    private void init() {
        setTitle("GoVlash Laundry - Login");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        JLabel titleLbl = new JLabel("GoVlash Login", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 20));
        titleLbl.setBorder(new EmptyBorder(20, 10, 10, 10)); 
        add(titleLbl, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 5, 5)); 
        centerPanel.setBorder(new EmptyBorder(10, 50, 10, 50)); 

        JLabel userLbl = new JLabel("Username:");
        userTxt = new JTextField();
        
        JLabel passLbl = new JLabel("Password:");
        passTxt = new JPasswordField();

        centerPanel.add(userLbl);
        centerPanel.add(userTxt);
        centerPanel.add(passLbl);
        centerPanel.add(passTxt);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 50, 30, 50)); 

        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register (Guest)");

        loginBtn.addActionListener(this);
        registerBtn.addActionListener(this);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String username = userTxt.getText();
            String password = new String(passTxt.getPassword());

            String result = UserController.getInstance().login(username, password);
            
            if (result.equals("Success")) {
                User user = UserController.getInstance().getCurrentUser();
                JOptionPane.showMessageDialog(this, "Welcome " + user.getUserRole() + ": " + user.getUserName());
                new MainView(user); 
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, result, "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == registerBtn) {
            new RegisterView(); 
            this.dispose();
        }
    }
}