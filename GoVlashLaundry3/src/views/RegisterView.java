package views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import controllers.UserController;

public class RegisterView extends JFrame implements ActionListener {

    private JTextField nameTxt, emailTxt;
    private JPasswordField passTxt, confPassTxt;
    private JRadioButton maleRb, femaleRb;
    private ButtonGroup genderGroup;
    private JSpinner ageSpinner;
    private JButton regBtn, backBtn;
    private JPanel formPanel, btnPanel, genderPanel;

    public RegisterView() {
        init();
    }

    private void init() {
        setTitle("GoVlash - Register Customer");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Register New Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Form Fields
        formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Username:"));
        nameTxt = new JTextField();
        formPanel.add(nameTxt);

        formPanel.add(new JLabel("Email (@email.com):"));
        emailTxt = new JTextField();
        formPanel.add(emailTxt);

        formPanel.add(new JLabel("Password (min 6):"));
        passTxt = new JPasswordField();
        formPanel.add(passTxt);

        formPanel.add(new JLabel("Confirm Password:"));
        confPassTxt = new JPasswordField();
        formPanel.add(confPassTxt);

        formPanel.add(new JLabel("Gender:"));
        genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maleRb = new JRadioButton("Male");
        femaleRb = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRb);
        genderGroup.add(femaleRb);
        genderPanel.add(maleRb);
        genderPanel.add(femaleRb);
        formPanel.add(genderPanel);

        formPanel.add(new JLabel("Age (min 12):"));
        ageSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 100, 1));
        formPanel.add(ageSpinner);

        add(formPanel, BorderLayout.CENTER);

        btnPanel = new JPanel();
        regBtn = new JButton("Register");
        backBtn = new JButton("Back");

        regBtn.addActionListener(this);
        backBtn.addActionListener(this);

        btnPanel.add(regBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) {
            new LoginView();
            this.dispose();
        } else if (e.getSource() == regBtn) {
            String name = nameTxt.getText();
            String email = emailTxt.getText();
            String pass = new String(passTxt.getPassword());
            String conf = new String(confPassTxt.getPassword());
            String gender = maleRb.isSelected() ? "Male" : (femaleRb.isSelected() ? "Female" : "");
            int age = (int) ageSpinner.getValue();

            String res = UserController.getInstance().register(name, email, pass, conf, gender, age);
            
            if (res.equals("Success")) {
                JOptionPane.showMessageDialog(this, "Registration Success!");
                new LoginView();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}