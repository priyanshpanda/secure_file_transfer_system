import java.awt.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class SecureClient extends JFrame {
    private JTextField hostField, portField, keyField, fileField;
    private JTextArea statusArea;
    private File selectedFile;

    public SecureClient() {
        //  Window setup
        setTitle(" Secure File Sender");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        //  Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        //  Header
        JLabel title = new JLabel(" Secure File Transfer", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(new Color(58, 123, 213));
        title.setForeground(Color.WHITE);
        title.setPreferredSize(new Dimension(0, 60));
        mainPanel.add(title, BorderLayout.NORTH);

        //  Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Labels + fields
        JLabel hostLabel = new JLabel("Server Host:");
        hostField = new JTextField("localhost", 15);
        addField(formPanel, gbc, hostLabel, hostField);

        JLabel portLabel = new JLabel("Server Port:");
        portField = new JTextField("5000", 15);
        addField(formPanel, gbc, portLabel, portField);

        JLabel keyLabel = new JLabel("Encryption Key:");
        keyField = new JTextField(15);
        addField(formPanel, gbc, keyLabel, keyField);

        JLabel fileLabel = new JLabel("Choose File:");
        JButton browseBtn = new JButton("Browse...");
        browseBtn.setBackground(new Color(58, 123, 213));
        browseBtn.setForeground(Color.WHITE);
        browseBtn.setFocusPainted(false);
        browseBtn.addActionListener(e -> chooseFile());

        fileField = new JTextField(15);
        fileField.setEditable(false);

        gbc.gridx = 0; formPanel.add(fileLabel, gbc);
        gbc.gridx = 1; formPanel.add(fileField, gbc);
        gbc.gridx = 2; formPanel.add(browseBtn, gbc);
        gbc.gridy++;

        mainPanel.add(formPanel, BorderLayout.CENTER);

        //  Send Button
        JButton sendBtn = new JButton("Send Securely!");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendBtn.setBackground(new Color(46, 204, 113));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sendBtn.addActionListener(e -> sendFile());
        mainPanel.add(sendBtn, BorderLayout.SOUTH);

        //  Status Area
        statusArea = new JTextArea(6, 40);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        statusArea.setBackground(Color.WHITE);
        statusArea.setBorder(BorderFactory.createTitledBorder("Transfer Status"));
        mainPanel.add(new JScrollPane(statusArea), BorderLayout.EAST);

        add(mainPanel);
    }

    // Helper to add label+field in GridBag
    private void addField(JPanel panel, GridBagConstraints gbc, JLabel label, JTextField field) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; panel.add(label, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(field, gbc);
        gbc.gridy++; gbc.gridwidth = 1;
    }

    //  Choose file dialog
    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            fileField.setText(selectedFile.getName());
        }
    }

    //  Send encrypted file to server
    private void sendFile() {
        if (selectedFile == null || keyField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file and enter encryption key!");
            return;
        }

        try {
            byte[] fileData = java.nio.file.Files.readAllBytes(selectedFile.toPath());
            byte[] encryptedData = AESUtil.encrypt(fileData, keyField.getText());
            SecretDataPacket packet = new SecretDataPacket(selectedFile.getName(), encryptedData);

            String host = hostField.getText();
            int port = Integer.parseInt(portField.getText());

            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(packet);
            oos.close();
            socket.close();

            statusArea.append(" Sent: " + selectedFile.getName() + "\n");
        } catch (Exception e) {
            statusArea.append(" Error: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SecureClient().setVisible(true));
    }
}
