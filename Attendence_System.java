package project2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

class Student {
    String name;
    String batch;
    String roll;
    boolean isPresent;
    String date;

    public Student(String name, String batch, String roll, String date, boolean isPresent) {
        this.name = name;
        this.batch = batch;
        this.roll = roll;
        this.date = date;
        this.isPresent = isPresent;
    }

    public String toFileString() {
        return name + "," + batch + "," + roll + "," + (isPresent ? "Present" : "Absent") + "," + date;
    }

    public static Student fromFileString(String line) {
        String[] parts = line.split(",");
        return new Student(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[4].trim(),
                parts[3].trim().equals("Present")
        );
    }
}

class Attendance extends JFrame {
    private final ArrayList<Student> studentList = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JTable studentTable;
    private final File dataFile;

    public Attendance() {
        File directory = new File("D:\\Attendance");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        dataFile = new File(directory, "project2.txt");

        setTitle("Attendance Management System");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Attendance Management System", JLabel.CENTER);
        heading.setFont(new Font("Courier New", Font.BOLD, 20));

        JPanel inputPanel = new JPanel(new BorderLayout());

        JPanel addPanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Student Name: ");
        JTextField nameField = new JTextField(15);
        JLabel batchLabel = new JLabel("Batch: ");
        JTextField batchField = new JTextField(15);
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

        String[] columnNames = {"Serial No.", "Name", "Batch", "Roll", "Status", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        inputPanel.add(addPanel, BorderLayout.NORTH);
        inputPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton markPresentButton = new JButton("Mark Present");
        JButton saveButton = new JButton("Save to File");
        JButton deleteButton = new JButton("Delete Student");

        controlPanel.add(markPresentButton);
        controlPanel.add(saveButton);
        controlPanel.add(deleteButton);

        add(heading, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // 👉 *Add Student (Validation Added)*
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String batch = batchField.getText().trim();
            String roll = rollField.getText().trim();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Name Validation (Only Letters Allowed)
            if (!name.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(null, "Wrong Name! Name should contain only letters.");
                return;
            }

            // Roll Validation (Only Numbers Allowed)
            if (!roll.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "Wrong Roll! Roll should contain only numbers.");
                return;
            }

            if (!name.isEmpty() && !batch.isEmpty() && !roll.isEmpty()) {
                Student newStudent = new Student(name, batch, roll, currentDate, false);
                studentList.add(newStudent);

                int serialNo = studentList.size();
                tableModel.addRow(new Object[]{
                        serialNo,
                        newStudent.name,
                        newStudent.batch,
                        newStudent.roll,
                        "Absent",
                        currentDate
                });

                nameField.setText("");
                batchField.setText("");
                rollField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Please fill all fields!");
            }
        });

        // 👉 *Mark Present*
        markPresentButton.addActionListener(e -> {
            String rollNumber = JOptionPane.showInputDialog("Enter the student's roll number to mark present:");
            if (rollNumber != null) {
                boolean found = false;
                for (int i = 0; i < studentList.size(); i++) {
                    Student student = studentList.get(i);
                    if (student.roll.equalsIgnoreCase(rollNumber)) {
                        student.isPresent = true;
                        student.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        tableModel.setValueAt("Present", i, 4);
                        tableModel.setValueAt(student.date, i, 5);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null, "Student with Roll Number " + rollNumber + " not found!");
                }
            }
        });

        saveButton.addActionListener(e -> saveToFile());

        // 👉 *Delete Student*
        deleteButton.addActionListener(e -> {
            String rollNumber = JOptionPane.showInputDialog("Enter the roll number of the student to delete:");
            if (rollNumber != null) {
                Iterator<Student> iterator = studentList.iterator();
                boolean found = false;
                int indexToRemove = -1;

                while (iterator.hasNext()) {
                    Student student = iterator.next();
                    if (student.roll.equalsIgnoreCase(rollNumber)) {
                        indexToRemove = studentList.indexOf(student);
                        iterator.remove();
                        found = true;
                        break;
                    }
                }

                if (found && indexToRemove != -1) {
                    tableModel.removeRow(indexToRemove);
                    saveToFile();
                    JOptionPane.showMessageDialog(null, "Student deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Student with Roll Number " + rollNumber + " not found!");
                }
            }
        });

        loadFromFile();
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Student student : studentList) {
                writer.write(student.toFileString());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Data saved successfully at:\n" + dataFile.getAbsolutePath());

            Desktop.getDesktop().open(new File("D:\\Attendance"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving to file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        if (!dataFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Student student = Student.fromFileString(line);
                studentList.add(student);

                int serialNo = studentList.size();
                tableModel.addRow(new Object[]{
                        serialNo,
                        student.name,
                        student.batch,
                        student.roll,
                        student.isPresent ? "Present" : "Absent",
                        student.date
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Attendance frame = new Attendance();
            frame.setVisible(true);
        });
    }
}