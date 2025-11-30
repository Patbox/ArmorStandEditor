package eu.pb4.armorstandeditor.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextUtils {
    public static final Component ENABLED = text("enabled").withStyle(ChatFormatting.GREEN);
    public static final Component DISABLED = text("disabled").withStyle(ChatFormatting.RED);

    public static MutableComponent gui(String path, Object... args) {
        return Component.translatable("gui.armor_stand_editor." + path, args);
    }


    public static MutableComponent text(String path, Object... args) {
        return Component.translatable("text.armor_stand_editor." + path, args);
    }

    public static MutableComponent command(String path, Object... args) {
        return Component.translatable("command.armor_stand_editor." + path, args);
    }

    public static MutableComponent direction(Direction from) {
        return Component.translatable("text.armor_stand_editor.dir." + from.getSerializedName());
    }
}
