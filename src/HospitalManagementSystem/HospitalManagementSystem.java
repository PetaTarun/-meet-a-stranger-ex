package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;
public class HospitalManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Admin@123";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL JDBC driver
            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                // Your existing code for managing the hospital system
                // ...
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }




   
    private static void displayMenu() {
        System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
        System.out.println("1. Add Patient");
        System.out.println("2. View Patients");
        System.out.println("3. View Doctors");
        System.out.println("4. Book Appointment");
        System.out.println("5. Exit");
        System.out.println("Enter your choice: ");
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        try {
            if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
                if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                    String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery)) {
                        preparedStatement.setInt(1, patientId);
                        preparedStatement.setInt(2, doctorId);
                        preparedStatement.setString(3, appointmentDate);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Appointment Booked!");
                        } else {
                            System.out.println("Failed to Book Appointment!");
                        }
                    }
                } else {
                    System.out.println("Doctor not available on this date!!");
                }
            } else {
                System.out.println("Either doctor or patient doesn't exist!!!");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return false;
    }
}
