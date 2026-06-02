import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

/*
  SecureServer:
  - Receives SecretDataPacket objects
  - Asks for the decryption key on console (must match client key)
  - Decrypts data using AESUtil.decrypt(byte[], String)
  - Saves file as received_<originalname>
  - Logs filename, sender IP and SHA-256 hash into SQLite via DatabaseManager
*/

public class SecureServer {
    public static void main(String[] args) {
        int port = 5000;
        DatabaseManager db = new DatabaseManager();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("   Secure Server Started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                String clientIP = socket.getInetAddress().getHostAddress();
                System.out.println(" Connected from: " + clientIP);

                // handle in a new thread so server can accept multiple clients
                new Thread(() -> handleClient(socket, clientIP, db)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, String clientIP, DatabaseManager db) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            // Read the packet object
            SecretDataPacket packet = (SecretDataPacket) ois.readObject();
            System.out.println("Receiving file: " + packet.getFileName());

            // Ask server operator for the password to decrypt
            System.out.print("Enter decryption key for '" + packet.getFileName() + "': ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String key = br.readLine();

            // Decrypt using AESUtil (matches single-folder AESUtil signature)
            byte[] decryptedData = AESUtil.decrypt(packet.getEncryptedData(), key);

            // Save to disk
            String outName = "received_" + packet.getFileName();
            Files.write(Paths.get(outName), decryptedData);
            System.out.println(" Saved file as: " + outName);

            // Compute SHA-256 hash of saved file (integrity)
            String fileHash = sha256Hex(decryptedData);

            // Log into DB
            db.insertLog(packet.getFileName(), clientIP, fileHash);
            System.out.println(" Logged in DB: filename=" + packet.getFileName() + " hash=" + fileHash);

        } catch (Exception e) {
            System.err.println("Error handling client " + clientIP + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "N/A";
        }
    }
}
