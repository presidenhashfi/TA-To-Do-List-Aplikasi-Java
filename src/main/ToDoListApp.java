package main;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import database.TaskDatabase;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToDoListApp {

    private final JFrame frame;
    private final JTable taskTable;
    private final DefaultTableModel tableModel;
    private final JTextField taskField;
    private final JTextField descriptionField;
    private final DatePicker datePicker;
    private final JComboBox<String> statusComboBox;

    private static final String[] STATUSES = {"Pending", "In Progress", "Completed"};
    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private static final Logger LOGGER = Logger.getLogger(ToDoListApp.class.getName());

    public ToDoListApp() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to set LookAndFeel", e);
        }

        frame = new JFrame("Aplikasi To-Do List dengan Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // Table setup
        String[] columnNames = {"ID", "Task", "Description", "Deadline", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel);
        taskTable.setFillsViewportHeight(true);

        // Sembunyikan kolom ID
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());

        JLabel taskLabel = new JLabel("Task:");
        taskField = new JTextField(20);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField(20);

        JLabel deadlineLabel = new JLabel("Deadline:");
        DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setFormatForDatesCommonEra(DATE_FORMAT);
        datePicker = new DatePicker(datePickerSettings);

        JLabel statusLabel = new JLabel("Status:");
        statusComboBox = new JComboBox<>(STATUSES);

        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update Task");
        JButton deleteButton = new JButton("Delete Task");

        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        updateButton.setBackground(new Color(33, 150, 243));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);

        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(taskLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(taskField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(deadlineLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(datePicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(statusComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(addButton, gbc);

        gbc.gridy = 5;
        inputPanel.add(updateButton, gbc);

        gbc.gridy = 6;
        inputPanel.add(deleteButton, gbc);

        // Listener untuk klik baris tabel
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && taskTable.getSelectedRow() != -1) {
                int selectedRow = taskTable.getSelectedRow();

                // Ambil nilai dari baris yang dipilih
                String task = (String) tableModel.getValueAt(selectedRow, 1);
                String description = (String) tableModel.getValueAt(selectedRow, 2);
                String deadline = (String) tableModel.getValueAt(selectedRow, 3);
                String status = (String) tableModel.getValueAt(selectedRow, 4);

                // Tampilkan data ke input form
                taskField.setText(task);
                descriptionField.setText(description);
                datePicker.setDate(LocalDate.parse(deadline, DateTimeFormatter.ofPattern(DATE_FORMAT)));
                statusComboBox.setSelectedItem(status);
            }
        });

        // Add Task Action
        addButton.addActionListener(e -> {
            String task = taskField.getText();
            String description = descriptionField.getText();
            LocalDate selectedDate = datePicker.getDate();
            String status = (String) statusComboBox.getSelectedItem();

            if (task.isEmpty() || description.isEmpty() || selectedDate == null) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save to database
            TaskDatabase.insertTask(task, description, selectedDate, status);
            loadTasks();
            resetFields();
        });

        // Update Task Action
        updateButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a task to update!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ambil data dari form
            String task = taskField.getText();
            String description = descriptionField.getText();
            LocalDate selectedDate = datePicker.getDate();
            String status = (String) statusComboBox.getSelectedItem();

            if (task.isEmpty() || description.isEmpty() || selectedDate == null) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ambil ID dari baris yang dipilih
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);

            // Perbarui data di database
            TaskDatabase.updateTask(taskId, task, description, selectedDate, status);
            loadTasks();
            resetFields();
        });

        // Delete Task Action
        deleteButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a task to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int taskId = (int) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                TaskDatabase.deleteTask(taskId);
                loadTasks();
                resetFields();
            }
        });

        // Layout setup
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Load tasks from database
        loadTasks();
    }

    private void resetFields() {
        taskField.setText("");
        descriptionField.setText("");
        datePicker.clear();
    }

    private void loadTasks() {
        tableModel.setRowCount(0); // Clear table
        ArrayList<Task> tasks = TaskDatabase.getAllTasks();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{task.id, task.taskName, task.description, formatter.format(task.deadline), task.status});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
