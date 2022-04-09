package org.emefz.helper;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.sponge.SPlayerBoard;
import org.emefz.FortcraftServerPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ScoreboardTimer {
    private final Runnable runnable;
    private Integer time;
    private final Text boardName = Text.builder("FORTCRAFT").color(TextColors.GOLD).style(TextStyles.BOLD).build();
    private final Text name;
    public ScoreboardTimer(Integer time, Text name, Runnable runnable) throws Exception {
        if(!time.equals(0)){
            this.time = Math.abs(time);
        }else{
            this.time = 100;
        }
        this.runnable = runnable;
        this.name = name;
        if(FortcraftServerPlugin.getInstance().isPresent()) {
            Task.builder().execute(new TimerTask()).interval(1, TimeUnit.SECONDS).submit(FortcraftServerPlugin.getInstance().get());
        }else{
            throw new Exception("Error getting plugin instance!");
        }
    }
    private class TimerTask implements Consumer<Task>{
        @Override
        public void accept(Task task) {
            if(time > 0){
                time--;
                updateTimer(time);
            }else{
                for(Player p : Sponge.getServer().getOnlinePlayers()){
                    getOrCreateScoreboard(p).remove(time);
                }
                runnable.run();
                task.cancel();
            }
        }
        private void updateTimer(int time){
            for(Player p : Sponge.getServer().getOnlinePlayers()){
                getOrCreateScoreboard(p).set(name, time);
            }
        }
    }
    private SPlayerBoard getOrCreateScoreboard(Player p){
        if(Netherboard.instance().hasBoard(p)){
            return Netherboard.instance().getBoard(p);
        }else{
            return Netherboard.instance().createBoard(p, this.boardName);
        }
    }
}
