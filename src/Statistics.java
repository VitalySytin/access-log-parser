import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> existingPages; // Для хранения существующих страниц
    private HashMap<String, Integer> osFrequency; // Для подсчета частоты операционных систем

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.existingPages = new HashSet<>();
        this.osFrequency = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getDataSize();


        // Обновляем минимальное и максимальное время
        if (entry.getDateTime().isBefore(minTime)) {
            minTime = entry.getDateTime();
        }

        if (entry.getDateTime().isAfter(maxTime)) {
            maxTime = entry.getDateTime();
        }

        // Добавляем страницу, если код ответа 200
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getRequestPath());
        }

        // Подсчитываем операционные системы
        String os = entry.getUserAgent().getOperatingSystem();
        osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);
    }

    public HashMap<String, Double> getOSStatistics() {
        HashMap<String, Double> osStatistics = new HashMap<>();
        int totalOSCount = osFrequency.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : osFrequency.entrySet()) {
            double percentage = (double) entry.getValue() / totalOSCount;
            osStatistics.put(entry.getKey(), percentage);
        }

        return osStatistics;
    }
    public double getTrafficRate() {
        long hoursDifference = maxTime.until(minTime, java.time.temporal.ChronoUnit.HOURS);


        if (hoursDifference == 0) {
            return totalTraffic; // Если разница в часах 0 или отрицательная, возвращаем общий трафик
        }

        return (double) totalTraffic / hoursDifference;
    }
    public int getExistingPagesCount() {
        return existingPages.size();
    }
}