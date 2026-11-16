import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

enum RoomType {
    SINGLE(80), DOUBLE(120), SUITE(250);

    final double nightlyRate;

    RoomType(double nightlyRate) {
        this.nightlyRate = nightlyRate;
    }
}

class Room {
    final int number;
    final RoomType type;

    Room(int number, RoomType type) {
        this.number = number;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Room " + number + " (" + type + ", $" + type.nightlyRate + "/night)";
    }
}

class Reservation {
    private static int nextId = 1001;

    final int id = nextId++;
    final String guestName;
    final Room room;
    final LocalDate checkIn;
    final LocalDate checkOut;
    boolean active = true;

    Reservation(String guestName, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.guestName = guestName;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    long nights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    double total() {
        return nights() * room.type.nightlyRate;
    }

    boolean overlaps(LocalDate in, LocalDate out) {
        return active && in.isBefore(checkOut) && out.isAfter(checkIn);
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s -> %s | %d nights | $%.2f | %s",
                id, guestName, room, checkIn, checkOut, nights(), total(),
                active ? "CONFIRMED" : "CANCELLED");
    }
}

class Hotel {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    Hotel() {
        for (int i = 101; i <= 105; i++) rooms.add(new Room(i, RoomType.SINGLE));
        for (int i = 201; i <= 205; i++) rooms.add(new Room(i, RoomType.DOUBLE));
        for (int i = 301; i <= 302; i++) rooms.add(new Room(i, RoomType.SUITE));
    }

    boolean isAvailable(Room room, LocalDate in, LocalDate out) {
        return reservations.stream()
                .noneMatch(r -> r.room.number == room.number && r.overlaps(in, out));
    }

    List<Room> findAvailable(RoomType type, LocalDate in, LocalDate out) {
        return rooms.stream()
                .filter(r -> type == null || r.type == type)
                .filter(r -> isAvailable(r, in, out))
                .collect(Collectors.toList());
    }

    Optional<Reservation> book(String guest, int roomNumber, LocalDate in, LocalDate out) {
        return rooms.stream()
                .filter(r -> r.number == roomNumber && isAvailable(r, in, out))
                .findFirst()
                .map(r -> {
                    Reservation res = new Reservation(guest, r, in, out);
                    reservations.add(res);
                    return res;
                });
    }

    boolean cancel(int reservationId) {
        for (Reservation r : reservations) {
            if (r.id == reservationId && r.active) {
                r.active = false;
                return true;
            }
        }
        return false;
    }

    List<Reservation> getReservations() {
        return reservations;
    }
}

public class HotelReservationSystem {
    private static final Scanner in = new Scanner(System.in);
    private static final Hotel hotel = new Hotel();

    public static void main(String[] args) {
        System.out.println("=== Hotel Reservation System ===");
        while (true) {
            System.out.println("\n1. Search available rooms");
            System.out.println("2. Make a reservation");
            System.out.println("3. Cancel a reservation");
            System.out.println("4. View all reservations");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            switch (in.nextLine().trim()) {
                case "1" -> search();
                case "2" -> book();
                case "3" -> cancel();
                case "4" -> hotel.getReservations().forEach(System.out::println);
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void search() {
        LocalDate[] dates = readDates();
        if (dates == null) return;
        List<Room> available = hotel.findAvailable(null, dates[0], dates[1]);
        if (available.isEmpty()) System.out.println("No rooms available.");
        else available.forEach(System.out::println);
    }

    private static void book() {
        System.out.print("Guest name: ");
        String name = in.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name is required.");
            return;
        }
        LocalDate[] dates = readDates();
        if (dates == null) return;

        List<Room> available = hotel.findAvailable(null, dates[0], dates[1]);
        if (available.isEmpty()) {
            System.out.println("No rooms available for those dates.");
            return;
        }
        available.forEach(System.out::println);

        System.out.print("Room number to book: ");
        try {
            int roomNo = Integer.parseInt(in.nextLine().trim());
            hotel.book(name, roomNo, dates[0], dates[1]).ifPresentOrElse(
                    r -> System.out.println("Booked! " + r),
                    () -> System.out.println("That room isn't available."));
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number.");
        }
    }

    private static void cancel() {
        System.out.print("Reservation ID: ");
        try {
            int id = Integer.parseInt(in.nextLine().trim());
            System.out.println(hotel.cancel(id) ? "Reservation cancelled." : "Reservation not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static LocalDate[] readDates() {
        try {
            System.out.print("Check-in (yyyy-MM-dd): ");
            LocalDate checkIn = LocalDate.parse(in.nextLine().trim());
            System.out.print("Check-out (yyyy-MM-dd): ");
            LocalDate checkOut = LocalDate.parse(in.nextLine().trim());
            if (checkIn.isBefore(LocalDate.now())) {
                System.out.println("Check-in can't be in the past.");
                return null;
            }
            if (!checkOut.isAfter(checkIn)) {
                System.out.println("Check-out must be after check-in.");
                return null;
            }
            return new LocalDate[]{checkIn, checkOut};
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return null;
        }
    }
}