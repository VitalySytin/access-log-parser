import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = scanner.nextLine();

            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Указанный путь не существует или это папка. Попробуйте снова.");
                continue;
            }

            Statistics statistics = new Statistics();
            int totalRequests = 0; // Общее количество запросов
            int googlebotCount = 0; // Счётчик запросов от Googlebot
            int yandexbotCount = 0; // Счётчик запросов от YandexBot

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    LogEntry entry = new LogEntry(line);
                    statistics.addEntry(entry);

                    // Увеличиваем общее количество запросов
                    totalRequests++;

                    // Проверяем User-Agent
                    String userAgentString = entry.getUserAgent().getUserAgentString();
                    if (userAgentString.contains("Googlebot")) {
                        googlebotCount++;
                    } else if (userAgentString.contains("YandexBot")) {
                        yandexbotCount++;
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
            }

            System.out.println("Средний объем трафика за час: " + statistics.getTrafficRate() * -1);
            System.out.println("Общее количество запросов: " + totalRequests);
            System.out.println("Количество запросов от Googlebot: " + googlebotCount);
            System.out.println("Количество запросов от YandexBot: " + yandexbotCount);
            System.out.println("Количество используемых браузеров: " +  statistics.getBrowserStatistics());
            System.out.println("Список несуществующих страниц сайта: " + statistics.getNonExistingPages());
            System.out.println("Cреднее количество посещений за час.: " + statistics.averageVisitsPerHour() *-1);
            System.out.println("Cреднее количество ошибочных запросов за час: " + statistics.averageErrorRequestsPerHour());
           System.out.println("Cреднее количество посещений на уникального пользователя (по IP): " + statistics.averageVisitsPerUniqueUser() * -1);
            //System.out.println("Пиковая посещаемость сайта: " + statistics.calculateHoursBetween() * -1);

        }

    }
}