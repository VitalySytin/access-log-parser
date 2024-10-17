import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> nonExistingPages; // Для хранения несуществующих страниц
    private HashMap<String, Integer> browserFrequency; // Для подсчета частоты браузеров

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.nonExistingPages = new HashSet<>();
        this.browserFrequency = new HashMap<>();
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

        // Добавляем страницу, если код ответа 404
        if (entry.getResponseCode() == 404) {
            nonExistingPages.add(entry.getRequestPath());
        }

        // Подсчитываем браузеры
        String browser = entry.getUserAgent().getBrowser();
        browserFrequency.put(browser, browserFrequency.getOrDefault(browser, 0) + 1);
    }

    public HashSet<String> getNonExistingPages() {
        return nonExistingPages;
    }

    public Map<String, Double> getBrowserStatistics() {
        HashMap<String, Double> browserStatistics = new HashMap<>();
        int totalBrowserCount = browserFrequency.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : browserFrequency.entrySet()) {
            double percentage = (double) entry.getValue() / totalBrowserCount;
            browserStatistics.put(entry.getKey(), percentage);
        }

        double sum = browserStatistics.values().stream().mapToDouble(Double::doubleValue).sum();
        for (Map.Entry<String, Double> entry : browserStatistics.entrySet()) {
            entry.setValue(entry.getValue() / sum);
        }

        return browserStatistics;
    }

    public double getTrafficRate() {
        long hoursDifference = minTime.until(maxTime, java.time.temporal.ChronoUnit.HOURS);

        if (hoursDifference <= 0) {
            return totalTraffic; // Если разница в часах 0 или отрицательная, возвращаем общий трафик
        }

        return (double) totalTraffic / hoursDifference;
    }
}