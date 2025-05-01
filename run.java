
import java.util.*;
import java.time.LocalDate;

class Guest {
    String name;
    LocalDate checkIn;
    LocalDate checkOut;

    public Guest(String name, LocalDate checkIn, LocalDate checkOut) {
        this.name = name;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
}

public class run {

    public static boolean checkCapacity(int maxCapacity, List<Map<String, String>> guests) {

        List<Guest> parsedGuests = new ArrayList<>();
        PriorityQueue<LocalDate> queue = new PriorityQueue<>();

        for (Map<String, String> guestMap : guests) {
            String name = guestMap.get("name");
            LocalDate checkIn = LocalDate.parse(guestMap.get("check-in"));
            LocalDate checkOut = LocalDate.parse(guestMap.get("check-out"));

            parsedGuests.add(new Guest(name, checkIn, checkOut));
        }

        parsedGuests.sort(Comparator.comparing(g -> g.checkIn));

        for (Guest i : parsedGuests) {
            while (!queue.isEmpty() && !queue.peek().isAfter(i.checkIn)) {
                queue.poll();
            }

            if (queue.size() < maxCapacity) {
                queue.add(i.checkOut);
            } else {
                return false;
            }
        }

        return true;
    }


    // Вспомогательный метод для парсинга JSON строки в Map
    private static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        // Удаляем фигурные скобки
        json = json.substring(1, json.length() - 1);


        // Разбиваем на пары ключ-значение
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }

        return map;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        int maxCapacity = Integer.parseInt(scanner.nextLine());


        int n = Integer.parseInt(scanner.nextLine());


        List<Map<String, String>> guests = new ArrayList<>();


        for (int i = 0; i < n; i++) {
            String jsonGuest = scanner.nextLine();
            // Простой парсер JSON строки в Map
            Map<String, String> guest = parseJsonToMap(jsonGuest);
            guests.add(guest);
        }


        boolean result = checkCapacity(maxCapacity, guests);


        System.out.println(result ? "True" : "False");


        scanner.close();
    }
}
