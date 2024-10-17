import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class LogEntry {
    private final String ipAddress;
    private final LocalDateTime dateTime;
    private HttpMethod method;
    private final String requestPath;
    private final int responseCode;
    private final int dataSize;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String logLine) {
        String[] parts = logLine.split(" ");

        // Проверяем, достаточно ли элементов в массиве
        if (parts.length < 9) {
            throw new IllegalArgumentException("Недостаточно данных в строке лога: " + logLine);
        }

        this.ipAddress = parts[0];

        // Обрабатываем парсинг даты
        LocalDateTime parsedDateTime = null;
        try {
            parsedDateTime = LocalDateTime.parse(parts[3].substring(1), DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH));
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка парсинга даты: " + parts[1] + " " + parts[2]);
            parsedDateTime = LocalDateTime.now(); // Устанавливаем текущее время
        }
        this.dateTime = parsedDateTime;

        // Обрабатываем метод HTTP
        try {
            this.method = HttpMethod.valueOf(parts[5].substring(1));
        } catch (IllegalArgumentException e) {
            System.err.println("Неизвестный метод HTTP: " + parts[3]);
            this.method = HttpMethod.UNKNOWN; // Устанавливаем значение по умолчанию или обрабатываем ошибку
        }

        // Остальные поля
        this.requestPath = parts[6];
        this.responseCode = Integer.parseInt(parts[8]);
        this.dataSize = Integer.parseInt(parts[9]);
        this.referer = parts[10];
        this.userAgent = new UserAgent(logLine);
    }

    // Геттеры
    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getDataSize() {
        return dataSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}