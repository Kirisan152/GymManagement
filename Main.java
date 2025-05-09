import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

 class GymManager1 extends JFrame implements ActionListener {
    JTextField nameField, ageField, genderField, membershipField, contactField;
    JButton insertButton, displayButton, clearButton;

    public GymManager1() {
        setTitle("Gym Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel[] labels = {
                new JLabel("Name:"), new JLabel("Age:"), new JLabel("Gender:"),
                new JLabel("Membership Type:"), new JLabel("Contact:")
        };

        JTextField[] fields = {
                nameField = new JTextField(15),
                ageField = new JTextField(15),
                genderField = new JTextField(15),
                membershipField = new JTextField(15),
                contactField = new JTextField(15)
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            add(labels[i], gbc);
            gbc.gridx = 1;
            add(fields[i], gbc);
        }

        insertButton = new JButton("Insert");
        displayButton = new JButton("Display");
        clearButton = new JButton("Clear");

        JPanel panel = new JPanel();
        panel.add(insertButton);
        panel.add(displayButton);
        panel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(panel, gbc);

        insertButton.addActionListener(this);
        displayButton.addActionListener(this);
        clearButton.addActionListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/gymdb", "root", "kirisan");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection Error");
            return null;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == insertButton) {
            insertMember();
        } else if (e.getSource() == displayButton) {
            displayMembers();
        } else {
            clearFields();
        }
    }

    private void insertMember() {
        String name = nameField.getText();
        String ageText = ageField.getText();
        String gender = genderField.getText();
        String membership = membershipField.getText();
        String contact = contactField.getText();

        if (name.isEmpty() || ageText.isEmpty() || gender.isEmpty() || membership.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = connect()) {
            int age = Integer.parseInt(ageText);
            String sql = "INSERT INTO members(name, age, gender, membership_type, contact) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, membership);
            ps.setString(5, contact);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Member inserted successfully.");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Age must be a number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inserting member.");
        }
    }

    private void displayMembers() {
        try (Connection conn = connect()) {
            String sql = "SELECT * FROM members";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            StringBuilder data = new StringBuilder();
            while (rs.next()) {
                data.append("ID: ").append(rs.getInt("memberid"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Age: ").append(rs.getInt("age"))
                        .append(", Gender: ").append(rs.getString("gender"))
                        .append(", Membership: ").append(rs.getString("membership_type"))
                        .append(", Contact: ").append(rs.getString("contact")).append("\n");
            }
            if (data.length() == 0) {
                JOptionPane.showMessageDialog(this, "No members found.");
            } else {
                JOptionPane.showMessageDialog(this, data.toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving data.");
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        genderField.setText("");
        membershipField.setText("");
        contactField.setText("");
    }

    public static void main(String[] args) {
        new GymManager();
    }
}
