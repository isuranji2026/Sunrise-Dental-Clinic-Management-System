import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Staff login window. Shown first when the application starts.
 * On successful authentication it opens MainFrame and closes itself.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private LoginManager loginManager;

    public LoginFrame() {
        loginManager = new LoginManager();

        setTitle("Sunrise Dental Clinic - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Sunrise Dental Clinic", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitleLabel = new JLabel("Appointment & Patient Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(java.awt.Color.RED);

        JLabel hintLabel = new JLabel(
            "<html><center>Default staff account: admin / admin123</center></html>",
            SwingConstants.CENTER);
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));

        JPanel buttonWrapper = new JPanel();
        buttonWrapper.add(loginButton);

        bottomPanel.add(messageLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonWrapper, BorderLayout.CENTER);
        bottomPanel.add(hintLabel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        };
        loginButton.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
        usernameField.addActionListener(loginAction);

        getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }

        if (loginManager.authenticate(username, password)) {
            dispose();
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        } else {
            messageLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}
