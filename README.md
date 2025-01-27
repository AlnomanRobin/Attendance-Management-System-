# Attendance-Management-System-

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Model class to represent a Student
class Student {
    String name;
    String batch;
    String roll;
    boolean isPresent;
    String date; // To store the date when marked present or absent

    // Constructor
    public Student(String name, String batch, String roll, String date) {
        this.name = name;
        this.batch = batch;
        this.roll = roll;
        this.isPresent = false; // Default status
        this.date = date; // Default date assigned when added
    }
}

class Attendance extends JFrame {
    private final ArrayList<Student> studentList = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JTable studentTable;

    public Attendance() {
        // Set up the JFrame
        setTitle("Attendance Management System");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create components
        JLabel heading = new JLabel("Attendance Management System", JLabel.CENTER);
        heading.setFont(new Font("Courier New", Font.BOLD, 20));

        JPanel inputPanel = new JPanel(new BorderLayout());

        JPanel addPanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Student Name: ");
        JTextField nameField = new JTextField(15);
        JLabel batchLabel = new JLabel("Batch: ");
        JTextField batchField = new JTextField(10);
        JLabel rollLabel = new JLabel("Roll: ");
        JTextField rollField = new JTextField(10);
        JButton addButton = new JButton("Add Student");

        addPanel.add(nameLabel);
        addPanel.add(nameField);
        addPanel.add(batchLabel);
        addPanel.add(batchField);
        addPanel.add(rollLabel);
        addPanel.add(rollField);
        addPanel.add(addButton);

        // Table to display students
        String[] columnNames = {"Serial No.", "Name", "Batch", "Roll", "Status", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        inputPanel.add(addPanel, BorderLayout.NORTH);
        inputPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton markPresentButton = new JButton("Mark Present");
        JButton viewAttendanceButton = new JButton("View Attendance");

        controlPanel.add(markPresentButton);
        controlPanel.add(viewAttendanceButton);

        // Add components to the JFrame
        add(heading, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Event Listeners
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String batch = batchField.getText().trim();
            String roll = rollField.getText().trim();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            if (!name.isEmpty() && !batch.isEmpty() && !roll.isEmpty()) {
                Student newStudent = new Student(name, batch, roll, currentDate);
                studentList.add(newStudent);

                int serialNo = studentList.size();
                tableModel.addRow(new Object[]{
                        serialNo,
                        newStudent.name,
                        newStudent.batch,
                        newStudent.roll,
                        "❌ Absent",
                        currentDate
                });

                // Clear input fields
                nameField.setText("");
                batchField.setText("");
                rollField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Please fill all fields!");
            }
        });

        markPresentButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter the student's name to mark present:");
            if (name != null) {
                boolean found = false;
                for (int i = 0; i < studentList.size(); i++) {
                    Student student = studentList.get(i);
                    if (student.name.equalsIgnoreCase(name)) {
                        student.isPresent = true;
                        student.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // Update to current date
                        tableModel.setValueAt("✅ Present", i, 4);
                        tableModel.setValueAt(student.date, i, 5);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null, "Student not found!");
                }
            }
        });

        viewAttendanceButton.addActionListener(e -> {
            JFrame attendanceFrame = new JFrame("Attendance List");
            attendanceFrame.setSize(700, 400);

            String[] columnNames1 = {"Serial No.", "Name", "Batch", "Roll", "Status", "Date"};
            DefaultTableModel attendanceModel = new DefaultTableModel(columnNames1, 0);

            for (int i = 0; i < studentList.size(); i++) {
                Student student = studentList.get(i);
                String status = student.isPresent ? "✅ Present" : "❌ Absent";
                attendanceModel.addRow(new Object[]{
                        i + 1,
                        student.name,
                        student.batch,
                        student.roll,
                        status,
                        student.date
                });
            }

            JTable attendanceTable = new JTable(attendanceModel);
            JScrollPane scrollPane = new JScrollPane(attendanceTable);

            attendanceFrame.add(scrollPane);
            attendanceFrame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Attendance frame = new Attendance();
            frame.setVisible(true);
        });
    }
}
