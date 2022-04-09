package org.emefz;

import org.emefz.helper.BroadcastMessageHelper;
import org.emefz.helper.MiniMapHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class RestartCommandExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<WorldProperties> original = Sponge.getServer().getWorldProperties((String)args.requireOne("world"));
        Optional<WorldProperties> world = Sponge.getServer().getDefaultWorld();
        Optional<WorldProperties> tmp = Sponge.getServer().getWorldProperties("tmp");
        if(world.isPresent()){
            if(original.isPresent()){
                BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getNormalText("Transfer to lobby..."));
                Optional<World> world_world = getWorldFromProperties(world.get());
                if(world_world.isPresent()){
                    for(Player p : Sponge.getServer().getOnlinePlayers()){
                        p.transferToWorld(world_world.get());
                        p.gameMode().set(GameModes.SURVIVAL);
                    }
                }else{
                    throw new CommandException(BroadcastMessageHelper.getErrorText("World not found!"));
                }
                BroadcastMessageHelper.sendBroadcastMessage(Text.builder("Sending some weird things... ").style(TextStyles.ITALIC).append(MiniMapHelper.disableMinimap()).build());
                if(tmp.isPresent()){
                    if(!isWorldLoaded(tmp.get())){
                        Sponge.getServer().loadWorld(tmp.get());
                    }
                    Optional<World> tmp_world = getWorldFromProperties(tmp.get());
                    if(tmp_world.isPresent()) {
                        Sponge.getServer().unloadWorld(tmp_world.get());
                        BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getNormalText("Deleting temporary world..."));
                        Sponge.getServer().deleteWorld(tmp.get()).handle((result, ex)->{
                            if(result != null) {
                                if (result) {
                                    BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getNormalText("Temporary world successfully deleted!"));
                                    BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getNormalText("Start creating the new world..."));
                                    if(FortcraftServerPlugin.getInstance().isPresent()){
                                        Sponge.getServer().copyWorld(original.get(), "tmp").handle(this::handleCopyWorld);
                                    }else{
                                        FortcraftServerPlugin.getStaticLogger().get().info("PluginInstance not available!");
                                    }
                                } else {
                                    BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getErrorText("Cannot delete world!"));
                                }
                                return result;
                            }
                            if (ex != null) {
                                ex.printStackTrace();
                            }
                            return null;
                        });
                    }else{
                        throw new CommandException(BroadcastMessageHelper.getErrorText("Temporary world not found!"));
                    }
                }else{
                    BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getNormalText("Start creating the new world..."));
                    if(FortcraftServerPlugin.getInstance().isPresent()) {
                        Sponge.getServer().copyWorld(original.get(), "tmp").handle(this::handleCopyWorld);
                    }else{
                        FortcraftServerPlugin.getStaticLogger().get().info("PluginInstance not available!");
                    }
                }
                return CommandResult.success();
            }else{
                throw new CommandException(BroadcastMessageHelper.getErrorText("World not found!"));
            }
        }else{
            throw new CommandException(BroadcastMessageHelper.getErrorText("Cannot get default world!"));
        }
    }
    private Optional<World> getWorldFromProperties(WorldProperties p){
        return Sponge.getServer().getWorld(p.getWorldName());
    }
    private boolean isWorldLoaded(WorldProperties worldProperties){
        for(WorldProperties wp : Sponge.getServer().getUnloadedWorlds()){
            if(wp.equals(worldProperties)){
                return false;
            }
        }
        return true;
    }
    private Optional<WorldProperties> handleCopyWorld(Optional<WorldProperties> result, Throwable ex){
        if(result != null) {
            if (result.isPresent()) {
                BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getNormalText("Temporary world created!"));
                WorldProperties wp = result.get();
                wp.setEnabled(true);
                Task.builder().execute(()->{
                    Sponge.getServer().loadWorld(wp);
                    Optional<World> wp_world = getWorldFromProperties(wp);
                    if (wp_world.isPresent()) {
                        for (Player p : Sponge.getServer().getOnlinePlayers()) {
                            p.transferToWorld(wp_world.get());
                            p.sendMessage(BroadcastMessageHelper.getNormalText("Transfer to map..."));
                            p.gameMode().set(GameModes.SURVIVAL);
                        }
                        BroadcastMessageHelper.sendBroadcastMessage(Text.builder("Sending some weird things... ").style(TextStyles.ITALIC).append(MiniMapHelper.enableMinimap()).build());
                    } else {
                        BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getErrorText("Temporary world not found!"));
                    }
                }).submit(FortcraftServerPlugin.getInstance().get());
            } else {
                BroadcastMessageHelper.sendBroadcastMessage(BroadcastMessageHelper.getErrorText("Temporary world not found!"));
            }
            return result;
        }
        if(ex != null){
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
