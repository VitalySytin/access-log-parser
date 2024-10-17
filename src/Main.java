import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
                    String userAgentString = entry.getUserAgent().getUserAgentString(); // Или используйте метод для получения User-Agent
                    if (userAgentString.contains("Googlebot")) {
                        googlebotCount++;
                    } else if (userAgentString.contains("YandexBot")) {
                        yandexbotCount++;
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
            }

            System.out.println("Средний объем трафика за час: " + statistics.getTrafficRate());
            System.out.println("Общее количество запросов: " + totalRequests);
            System.out.println("Количество запросов от Googlebot: " + googlebotCount);
            System.out.println("Количество запросов от YandexBot: " + yandexbotCount);
        }

    }
}

