package IK.files;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        var validNumbers = defineValidPhoneNumbersFromFile("./src/main/java/IK/files/numbers.txt");
        validNumbers.forEach(System.out::println);

        writeUsersToJsonFile("./src/main/java/IK/files/users.txt", "./src/main/java/IK/files/users.json");

        countWordFrequency("./src/main/java/IK/files/words.txt");
    }

    public static void countWordFrequency(String fileName) {
        Map<String, Integer> wordsMapCount = new HashMap<>();

        try (BufferedReader r = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    wordsMapCount.put(word, wordsMapCount.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        wordsMapCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getKey() + " : " + entry.getValue()));
    }

    public static void writeUsersToJsonFile(String inputFileName, String outputFileName) {
        List<User> users = new ArrayList<>();

        try (BufferedReader r = new BufferedReader(new FileReader(inputFileName))) {
            String line = r.readLine();

            int counter = 1;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    long id = Long.parseLong(parts[0]);
                    String name = parts[1];
                    int age = Integer.parseInt(parts[2]);

                    users.add(new User(id, name, age));
                    counter++;
                } else {
                    System.out.println("Error parsing line value #" + counter);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing numeric value", e);
        }

        // Writing data in JSON file
        ObjectMapper mapper = new ObjectMapper();
        try (FileWriter writer = new FileWriter(outputFileName)) {
            mapper.writeValue(writer, users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> defineValidPhoneNumbersFromFile(String fileName) {
        List<String> list = new ArrayList<>();

        String regex1 = "\\(\\d{3}\\) \\d{3}-\\d{4}";
        String regex2 = "\\d{3}-\\d{3}-\\d{4}";

        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher1 = pattern1.matcher(line);
                Matcher matcher2 = pattern2.matcher(line);

                if (matcher1.matches() || matcher2.matches()) {
                    list.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

}
