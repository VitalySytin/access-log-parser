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
        String os = entry.getUserAgent().getOperatingSystem(); // Предполагаем, что метод getOperatingSystem() существует в классе UserAgent
        osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);
    }

    public double getTrafficRate() {
        long hoursDifference = minTime.until(maxTime, java.time.temporal.ChronoUnit.HOURS);

        if (hoursDifference <= 0) {
            return totalTraffic; // Если разница в часах 0 или отрицательная, возвращаем общий трафик
        }

        return (double) totalTraffic / hoursDifference;
    }

    // Метод для получения списка всех существующих страниц
    public Set<String> getExistingPages() {
        return existingPages; // Возвращаем HashSet как Set
    }

    // Метод для получения статистики операционных систем
    public HashMap<String, Double> getOSStatistics() {
        HashMap<String, Double> osStatistics = new HashMap<>();
        int totalOSCount = osFrequency.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : osFrequency.entrySet()) {
            double percentage = (double) entry.getValue() / totalOSCount * 100; // Процентное соотношение
            osStatistics.put(entry.getKey(), percentage);
        }

        return osStatistics;
    }
}