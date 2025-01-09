package database;

import model.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class TaskDatabase {
    // URL koneksi ke database MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo_list";
    private static final String DB_USER = "root"; // Default user di Laragon
    private static final String DB_PASSWORD = ""; // Default password di Laragon (kosong jika belum diubah)

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Buat tabel tasks jika belum ada
            String createTableSQL = "CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "taskName VARCHAR(255) NOT NULL," +
                    "description TEXT NOT NULL," +
                    "deadline DATE NOT NULL," +
                    "status VARCHAR(50) NOT NULL)";
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk menambahkan task ke database
    public static void insertTask(String taskName, String description, LocalDate deadline, String status) {
        String sql = "INSERT INTO tasks (taskName, description, deadline, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, taskName);
            pstmt.setString(2, description);
            pstmt.setDate(3, Date.valueOf(deadline));
            pstmt.setString(4, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk memperbarui task di database
    public static void updateTask(int id, String taskName, String description, LocalDate deadline, String status) {
        String sql = "UPDATE tasks SET taskName = ?, description = ?, deadline = ?, status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, taskName);
            pstmt.setString(2, description);
            pstmt.setDate(3, Date.valueOf(deadline));
            pstmt.setString(4, status);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk menghapus task dari database
    public static void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk mendapatkan semua task dari database
    public static ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String taskName = rs.getString("taskName");
                String description = rs.getString("description");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();
                String status = rs.getString("status");

                tasks.add(new Task(id, taskName, description, deadline, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }
}
