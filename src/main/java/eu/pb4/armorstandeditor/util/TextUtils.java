package eu.pb4.armorstandeditor.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

public class TextUtils {
    public static final Text ENABLED = text("enabled").formatted(Formatting.GREEN);
    public static final Text DISABLED = text("disabled").formatted(Formatting.RED);

    public static MutableText gui(String path, Object... args) {
        return Text.translatable("gui.armor_stand_editor." + path, args);
    }


    public static MutableText text(String path, Object... args) {
        return Text.translatable("text.armor_stand_editor." + path, args);
    }

    public static MutableText command(String path, Object... args) {
        return Text.translatable("command.armor_stand_editor." + path, args);
    }

    public static MutableText direction(Direction from) {
        return Text.translatable("text.armor_stand_editor.dir." + from.asString());
    }
}
