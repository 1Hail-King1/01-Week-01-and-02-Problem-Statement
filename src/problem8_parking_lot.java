import java.util.*;

public class problem8_parking_lot {

    enum SpotStatus { EMPTY, OCCUPIED, DELETED }

    static class ParkingSpot {
        SpotStatus status = SpotStatus.EMPTY;
        String licensePlate = null;
        long entryTimeMs = 0;
    }

    private final ParkingSpot[] spots;
    private final int totalSpots;
    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int totalParkings = 0;
    private static final double RATE_PER_HOUR = 5.0;

    public problem8_parking_lot(int size) {
        this.totalSpots = size;
        spots = new ParkingSpot[size];
        for (int i = 0; i < size; i++) spots[i] = new ParkingSpot();
    }

    private int hash(String plate) {
        int h = 0;
        for (char c : plate.toCharArray()) h = (h * 31 + c) % totalSpots;
        return Math.abs(h);
    }

    // Returns assigned spot number, or -1 if full
    public int parkVehicle(String plate) {
        int preferred = hash(plate);
        int probes = 0, idx = preferred;

        while (spots[idx].status == SpotStatus.OCCUPIED) {
            idx = (idx + 1) % totalSpots;
            if (++probes >= totalSpots) return -1; // lot full
        }

        spots[idx].status = SpotStatus.OCCUPIED;
        spots[idx].licensePlate = plate;
        spots[idx].entryTimeMs = System.currentTimeMillis();
        occupiedCount++;
        totalProbes += probes;
        totalParkings++;
        return idx;
    }

    // Returns fee charged, or -1 if plate not found
    public double exitVehicle(String plate) {
        for (int i = 0; i < totalSpots; i++) {
            if (spots[i].status == SpotStatus.OCCUPIED
                    && plate.equals(spots[i].licensePlate)) {
                long durationMs = System.currentTimeMillis() - spots[i].entryTimeMs;
                double hours = durationMs / 3_600_000.0;
                double fee = Math.max(2.50, hours * RATE_PER_HOUR);
                spots[i].status = SpotStatus.DELETED;
                spots[i].licensePlate = null;
                occupiedCount--;
                return fee;
            }
        }
        return -1;
    }

    public boolean isOccupied(int spotNumber)    { return spots[spotNumber].status == SpotStatus.OCCUPIED; }
    public int getOccupiedCount()                { return occupiedCount; }
    public double getOccupancyRate()             { return (occupiedCount * 100.0) / totalSpots; }
    public double getAverageProbes()             { return totalParkings == 0 ? 0 : (double) totalProbes / totalParkings; }
    public int getTotalSpots()                   { return totalSpots; }
}