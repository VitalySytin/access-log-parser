import java.time.LocalDateTime;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getDataSize();

        if (entry.getDateTime().isBefore(minTime)) {
            minTime = entry.getDateTime();
        }

        if (entry.getDateTime().isAfter(maxTime)) {
            maxTime = entry.getDateTime();
        }
    }

    public double getTrafficRate() {
        long hoursDifference = maxTime.until(minTime, java.time.temporal.ChronoUnit.HOURS);

        if (hoursDifference == 0) {
            return totalTraffic; // Если разница в часах 0, возвращаем общий трафик
        }

        return (double) totalTraffic / hoursDifference;
    }
}