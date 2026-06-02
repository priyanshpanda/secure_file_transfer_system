import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:transfer_logs.db";

    public DatabaseManager() {
    System.out.println("[DB] Initializing database...");
    createTable();
    File dbFile = new File("transfer_logs.db");
    System.out.println("[DB] Database path: " + dbFile.getAbsolutePath());
    if (dbFile.exists()) {
        System.out.println("[DB] Database file found");
    } else {
        System.out.println("[DB] Database file NOT found  (will be created after first insert)");
    }
}


    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS file_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "filename TEXT," +
                "sender_ip TEXT," +
                "timestamp TEXT," +
                "file_hash TEXT)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertLog(String filename, String senderIp, String fileHash) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = "INSERT INTO file_logs(filename, sender_ip, timestamp, file_hash) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, filename);
            pstmt.setString(2, senderIp);
            pstmt.setString(3, time);
            pstmt.setString(4, fileHash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}







