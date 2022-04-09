package org.emefz;

import org.emefz.helper.BroadcastMessageHelper;
import org.emefz.helper.MiniMapHelper;
import org.emefz.helper.ScoreboardTimer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class StartCommandExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src.hasPermission("fortcraft.server")){
            try{
                new ScoreboardTimer(300, Text.of("WorldBorder start shrink:"), ()-> {
                    ((Player)src).getWorld().getWorldBorder().setDiameter(((Player)src).getWorld().getWorldBorder().getDiameter(), 150, 150000);
                    try {
                        new ScoreboardTimer(150, Text.of("WorldBorder stop shrink:"), ()->Sponge.getServer().getBroadcastChannel().send(Text.of("WorldBorder stopped shrinking!", TextColors.GOLD, TextStyles.BOLD)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    BroadcastMessageHelper.sendBroadcastMessage(Text.of("WorldBorder started shrinking!", TextColors.GOLD, TextStyles.BOLD));
                });
                new ScoreboardTimer(120, Text.of("PlayerRadar Activation:"), ()-> {
                    BroadcastMessageHelper.sendBroadcastMessage(Text.builder("Sending some weird things... ").style(TextStyles.ITALIC).append(MiniMapHelper.enableEntityPlayerRadar()).build());
                    BroadcastMessageHelper.sendBroadcastMessage(Text.of("PlayerRadar is activated now!", TextColors.GOLD, TextStyles.BOLD));
                });
                BroadcastMessageHelper.sendBroadcastMessage(Text.builder("Sending some weird things... ").style(TextStyles.ITALIC).append(MiniMapHelper.enableMinimap()).build());
                return CommandResult.success();
            }catch(Exception e){
                e.printStackTrace();
                throw new CommandException(Text.builder("Error getting plugin instance!").color(TextColors.DARK_RED).build());
            }
        }else{
            throw new CommandException(Text.builder("You don't have sufficient permission to perform this action!").color(TextColors.DARK_RED).build());
        }
    }
}
