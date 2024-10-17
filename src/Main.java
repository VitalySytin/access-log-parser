import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int validFileCount = 0; // Счётчик корректных файлов

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = scanner.nextLine();

            File file = new File(path);
            boolean fileExists = file.exists(); // Проверка существования файла
            boolean isDirectory = file.isDirectory(); // Проверка, является ли это папкой

            // Проверка входных данных
            if (!fileExists || isDirectory) {
                System.out.println("Указанный путь не существует или это папка. Попробуйте снова.");
                continue;
            }

            // Если файл существует и это файл
            validFileCount++;
            System.out.println("Путь указан верно. Это файл номер " + validFileCount);

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineCount = 0;
                int maxLength = 0;
                int minLength = Integer.MAX_VALUE;

                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    int length = line.length();

                    if (length > 1024) {
                        throw new LineTooLongException("Строка длиннее 1024 символов: " + length);
                    }

                    if (length > maxLength) {
                        maxLength = length;
                    }
                    if (length < minLength) {
                        minLength = length;
                    }
                }

                System.out.println("Общее количество строк в файле: " + lineCount);
                System.out.println("Длина самой длинной строки в файле: " + maxLength);
                System.out.println("Длина самой короткой строки в файле: " + (minLength == Integer.MAX_VALUE ? 0 : minLength));

            } catch (LineTooLongException e) {
                System.err.println("Ошибка: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}