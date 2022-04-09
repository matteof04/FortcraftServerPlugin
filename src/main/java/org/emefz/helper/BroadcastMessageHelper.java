package org.emefz.helper;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public abstract class BroadcastMessageHelper {
    public static void sendBroadcastMessage(Text text){
        for(Player p : Sponge.getServer().getOnlinePlayers()){
            p.sendMessage(text);
        }
    }
    public static Text getErrorText(String text){
        return Text.builder(text).color(TextColors.DARK_RED).style(TextStyles.BOLD).build();
    }
    public static Text getNormalText(String text){
        return Text.builder(text).color(TextColors.GOLD).style(TextStyles.BOLD).build();
    }
}
