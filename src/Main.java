import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int totalRequests = 0; // Общее количество запросов
        int googlebotCount = 0; // Счётчик запросов от Googlebot
        int yandexbotCount = 0; // Счётчик запросов от YandexBot

        while (true) {
            System.out.println(" Введите путь к файлу:");
            String path = scanner.nextLine();

            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Указанный путь не существует или это папка. Попробуйте снова.");
                continue;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    totalRequests++;



                    // Разделяем строку по кавычкам
                    String[] parts = line.split(";");

                    // Проверяем, что строка содержит User-Agent
                    if (parts.length >= 2) {
                        String userAgent = parts[1];

                        // Подсчет запросов от ботов
                        if (userAgent.toLowerCase().contains("googlebot")) {
                            googlebotCount++;
                        } else if (userAgent.toLowerCase().contains("yandexbot")) {
                            yandexbotCount++;
                        }
                    }
                }

                // Вычисление долей
                double googlebotShare = (double) googlebotCount / totalRequests * 100;
                double yandexbotShare = (double) yandexbotCount / totalRequests * 100;

                System.out.printf("Доля запросов от Googlebot: %.2f%%n", googlebotShare);
                System.out.printf("Доля запросов от YandexBot: %.2f%%n", yandexbotShare);

            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}