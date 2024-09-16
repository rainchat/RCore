package ru.rainchat.rlib.messages.placeholder.replacers;

import java.util.Arrays;
import java.util.List;

import ru.rainchat.rlib.messages.placeholder.base.CustomPlaceholder;
import org.bukkit.entity.Player;

public class ArgsReplacements extends CustomPlaceholder<Player> {
    public List<String> args;

    public ArgsReplacements(String... strings) {
        super("args_");
        this.args = Arrays.asList(strings);
    }

    public static boolean isNumeric(String strNum) {

        if (strNum == null) {
            return false;
        } else {
            try {
                Integer.parseInt(strNum);
                return true;
            } catch (NumberFormatException var3) {
                return false;
            }
        }
    }

    public Class<Player> forClass() {
        return Player.class;
    }

    public String getReplacement(String base, String fullKey) {

        if (isNumeric(base)) {
            int number = Integer.parseInt(base);
            if (number <= this.args.size()) {
                return this.args.get(number);
            }
        }

        return "";
    }
}
