package org.emefz;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Plugin(
        id = FortcraftServerPlugin.ID, name = FortcraftServerPlugin.NAME, version = FortcraftServerPlugin.VERSION,
        description = "FortCraft Server dedicated plugin",
        authors = {"EmEfZ"}
)
public class FortcraftServerPlugin {
    @Inject
    private Logger logger;
    public static final String ID = "fortcraftserverplugin";
    public static final String NAME = "FortCraft Server Plugin";
    public static final String VERSION = "1.0.0";
    public static Optional<PluginContainer> getInstance() {
        if(Sponge.getPluginManager().getPlugin(FortcraftServerPlugin.ID).isPresent()){
            return Optional.of(Sponge.getPluginManager().getPlugin(FortcraftServerPlugin.ID).get());
        }
        return Optional.empty();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandSpec startCommand = CommandSpec.builder().description(Text.of("Start FortCraft match!")).permission("fortcraft.start").executor(new StartCommandExecutor()).build();
        CommandSpec restartCommand = CommandSpec.builder().description(Text.of("Restore world to start a new FortCraft match!")).permission("fortcraft.restart").arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("world")))).executor(new RestartCommandExecutor()).build();
        Sponge.getCommandManager().register(this, startCommand, "start", "fcstart");
        Sponge.getCommandManager().register(this, restartCommand, "restart", "fcrestart");
    }
    public Logger getLogger(){
        return logger;
    }
    public static Optional<Logger> getStaticLogger(){
        return getInstance().isPresent()? Optional.of(getInstance().get().getLogger()) : Optional.empty();
    }
}
