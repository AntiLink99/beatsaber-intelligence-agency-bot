package bot.utils;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Supporters {
    public static List<String> getUserSupportTypes(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        List<String> supportTypes = new ArrayList<>();
        //TODO
        return supportTypes;
    }
}
