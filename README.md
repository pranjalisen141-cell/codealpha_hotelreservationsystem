# Hotel Reservation System (Java)

A console-based hotel reservation system built in Java as part of the CodeAlpha internship.

## Features

- Search available rooms for a date range
- Make a reservation (guest name, dates, room number)
- Automatic total cost calculation
- Cancel a reservation by ID
- View all reservations
- Prevents double booking of the same room
- Input validation for dates and numbers

## Room Types

| Type   | Rooms     | Price per night |
|--------|-----------|-----------------|
| Single | 101-105   | $80             |
| Double | 201-205   | $120            |
| Suite  | 301-302   | $250            |

## Requirements

- Java 17 or newer

## How to Run

```
javac HotelReservationSystem.java
java HotelReservationSystem
```

## Usage

1. Choose an option from the menu (1-4, or 0 to exit).
2. Enter dates in `yyyy-MM-dd` format, for example `2026-11-20`.
3. Check-in must be today or later, and check-out must be after check-in.

## Concepts Used

- Object-oriented programming (classes, enums, encapsulation)
- Java Collections and Streams
- `java.time` (LocalDate)
- Exception handling

## Author

Pranjali Sen
