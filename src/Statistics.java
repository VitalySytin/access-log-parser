import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> nonExistingPages; // Для хранения несуществующих страниц
    private HashMap<String, Integer> browserFrequency; // Для подсчета частоты браузеров
    private Map<String, Integer> visitsByIp;
    private long startTime; // Время начала в миллисекундах
    private long endTime;
    private int errorRequests;
    private Map<Integer, Integer> visitsPerSecond; // Хранит количество посещений за каждую секунду
    private HashSet<String> referers; // Хранит уникальные домены рефереров
    private final String BOT_IDENTIFIER = "bot"; // Идентификатор для определения ботов

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.nonExistingPages = new HashSet<>();
        this.browserFrequency = new HashMap<>();
        this.errorRequests = 0;
        this.visitsByIp = new HashMap<>();
        this.startTime = Long.MAX_VALUE; // Инициализируем максимальным значением
        this.endTime = Long.MIN_VALUE;    // Инициализируем минимальным значением
        this.visitsPerSecond = new HashMap<>(); // Инициализируем для хранения посещений по секундам
        this.referers = new HashSet<>(); // Инициализируем для хранения рефереров
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getDataSize();
        String userAgentString = entry.getUserAgent().getUserAgentString();
        String ipAddress = entry.getIpAddress();
        String responseCode = String.valueOf(entry.getResponseCode());
        long timestamp = entry.getDateTime().toEpochSecond(ZoneOffset.UTC); // Получаем временную метку

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

        if (!userAgentString.toLowerCase().contains(BOT_IDENTIFIER)) {
            visitsByIp.put(ipAddress, visitsByIp.getOrDefault(ipAddress, 0) + 1);

            // Обновляем время начала и конца
            if (timestamp < startTime) {
                startTime = timestamp;
            }
            if (timestamp > endTime) {
                endTime = timestamp;
            }
            // Увеличиваем количество посещений в текущую секунду
            visitsPerSecond.put((int) (timestamp), visitsPerSecond.getOrDefault((int) (timestamp), 0) + 1);

            // Обрабатываем рефереры
            if (entry.getReferer() != null && !entry.getReferer().isEmpty()) {
                String domain = getDomainFromReferer(entry.getReferer());
                if (domain != null) {
                    referers.add(domain);
                }
            }
        }

        // Проверяем на ошибочные коды ответа
        if (responseCode.startsWith("4") || responseCode.startsWith("5")) {
            errorRequests++;
        }

        // Подсчитываем браузеры
        String browser = entry.getUserAgent().getBrowser();
        browserFrequency.put(browser, browserFrequency.getOrDefault(browser, 0) + 1);
    }
    public int getPeakVisitsPerSecond() {
        return visitsPerSecond.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    // Метод для получения списка рефереров
    public HashSet<String> getReferers() {
        return referers;
    }

    // Метод для расчёта максимальной посещаемости одним пользователем
    public int getMaxVisitsPerUser () {
        return visitsByIp.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    public HashSet<String> getNonExistingPages() {
        return nonExistingPages;
    }

    public Map<String, Double> getBrowserStatistics() {
        HashMap<String, Double> browserStatistics = new HashMap<>();
        int totalBrowserCount = browserFrequency.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : browserFrequency.entrySet()) {
            double percentage = (double) entry.getValue() / totalBrowserCount * 100; // Процент

            browserStatistics.put(entry.getKey(), percentage);
        }

        return browserStatistics;
    }

    public double getTrafficRate() {
        long hoursDifference = minTime.until(maxTime, java.time.temporal.ChronoUnit.HOURS);

        if (hoursDifference <= 0) {
            return totalTraffic; // Если разница в часах 0 или отрицательная,возвращаем общий трафик
        }

        return (double) totalTraffic / hoursDifference;
    }


    public double averageErrorRequestsPerHour() {
        long hours = calculateHours();
        return hours > 0 ? (double) errorRequests / hours : 0;
    }

    private long calculateHours() {
        if (minTime.equals(LocalDateTime.MAX) || maxTime.equals(LocalDateTime.MIN)) {
            return 0;
        }

        return minTime.until(maxTime, java.time.temporal.ChronoUnit.HOURS);
    }
    public double averageVisitsPerHour() {
        long hours = calculateHours();
        int realUserVisits = totalTraffic - errorRequests; // Учитываем только реальные посещения
        return hours > 0 ? (double) realUserVisits / hours : 0; // Среднее количество посещений в час
    }

    public double averageVisitsPerUniqueUser () {
        int realUserVisits = visitsByIp.size(); // Получаем количество уникальных пользователей
        return realUserVisits > 0 ? (double) totalTraffic / realUserVisits : 0; // Возвращаем среднее количество посещений на уникального пользователя
    }

    private String getDomainFromReferer(String referer) {
        try {
            // Извлекаем домен из реферера
            String[] parts = referer.split("/");
            if (parts.length > 2) {
                return parts[2]; // Возвращаем домен
            }
        } catch (Exception e) {
            // Игнорируем исключения
        }
        return null; // Если домен не найден
    }



    public int getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public int getErrorRequests() {
        return errorRequests;
    }

    public Map<Integer, Integer> getVisitsPerSecond() {
        return visitsPerSecond;
    }

    public Map<String, Integer> getVisitsByIp() {
        return visitsByIp;
    }
}