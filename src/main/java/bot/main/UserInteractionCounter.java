package bot.main;

import java.util.HashMap;
import java.util.Map;

public class UserInteractionCounter {
    private static final Map<String, Integer> userInteractions = new HashMap<>();

    public static void increment(String userId) {
        userInteractions.put(userId, userInteractions.getOrDefault(userId, 0) + 1);
    }

    public static boolean shouldSendReminder(String userId) {
        return userInteractions.getOrDefault(userId, 0) % 10 == 0;
    }
}
