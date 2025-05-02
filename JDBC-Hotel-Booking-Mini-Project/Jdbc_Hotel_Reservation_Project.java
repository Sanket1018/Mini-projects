import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Scanner;


public class Jdbc_Hotel_Reservation_Project {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "manager";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Registering the driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Establish the connection
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("6. Exit");
                System.out.println("Choose an option");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner);
                        break;
                    case 4:
                        updateReservations(connection,scanner);
                        break;
                    case 5:
                        deleteReservations(connection,scanner);
                        break;
                    case 6:
                        exit();
                        scanner.close();
                }


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter the guest name");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter the room number");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter the contact number");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name,room_number,contact_number)" + "VALUES('" + guestName + "', " + roomNumber + ",'" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedrows = statement.executeUpdate(sql);

                if (affectedrows > 0) {
                    System.out.println("Reservation successful");
                } else {
                    System.out.println("Reservation failed");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private static void viewReservation(Connection connection) throws SQLException
    {
        String sql = "SELECT reservation_id,guest_name,room_number,contact_number,reservation_date FROM reservations";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql))
        {
            System.out.println("Current Reservations : ");
            System.out.println("*----------------+-------+-------------+----------------+-------------------------------------------");
            System.out.println("| Reservation ID | Guest | Room Number | Contact Number | Resrvation Date |");
            System.out.println("*----------------+-------+-------------+----------------+-------------------------------------------");
            while(resultSet.next())
            {
                int reservationid = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate= resultSet.getString("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %14d  | %-15s | %-13d  | %-20s | %19s  |\n",reservationid,guestName,roomNumber,contactNumber,reservationDate);
            }

            System.out.println("*----------------+-------+-------------+----------------+-------------------------------------------");
        }
    }

    private static void getRoomNumber(Connection connection,Scanner scanner) {
        try {
            System.out.println("Enter reservation ID : ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " + "WHERE reservation_id = " + reservationId + " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number of Reservation id " + reservationId + " and Guest " + guestName + "is: " + roomNumber);
                }

            }

        }catch(SQLException e)
        {
            e.printStackTrace();
        }

    }

    private static void updateReservations(Connection connection,Scanner scanner) {
        try {
            System.out.println("Enter reservation ID to update: ");
            int reservationID = scanner.nextInt();
            scanner.nextLine(); // Consume the nextline character

            if (!reservationExists(connection, reservationID)) {
                System.out.println("Reservation not found for the given id");
                return;
            }

            System.out.println("Enter the new guest name : ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " + "room_number = " + newRoomNumber + "," + "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationID;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully");
                } else {
                    System.out.println("Reservation updated failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservations(Connection connection,Scanner scanner)
    {
        try{
            System.out.println("Enter reservation ID to delete");
                int reservationid = scanner.nextInt();

                if(!reservationExists(connection,reservationid))
                {
                    System.out.println("Reservation not found for the given ID.");
                    return;
                }
                String sql = "DELETE FROM reservations WHERE reservation_id = "+reservationid;

                try(Statement statement = connection.createStatement())
                {
                    int affectedRows = statement.executeUpdate(sql);

                    if(affectedRows > 0)
                    {
                        System.out.println("Reservation deleted successfully");
                    }
                    else {
                        System.out.println("Reservation deletion failed");
                    }

                }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection,int reservationId)
    {
        try{
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;


            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                    return resultSet.next(); // If there's result, the reservation exists

            }
        }catch (SQLException e)
        {
            e.printStackTrace();
            return false; // Handles database error as needed
        }
    }

    public static void exit() throws InterruptedException
    {
        System.out.print("Existing System");
        int i = 5;
        while(i!=0)
        {
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank you Using Reservation system !!!!");
    }
}










