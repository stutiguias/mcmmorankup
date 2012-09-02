/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.mcmmorankup.task;

import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class UpdateTask implements Runnable {
    
   private final Mcmmorankup plugin;

    public UpdateTask(Mcmmorankup plugin) {
            this.plugin = plugin;
    }
    
    @Override
    public void run() {
        Player[] playerList = plugin.getServer().getOnlinePlayers();
        if(plugin.getServer().getOnlinePlayers().length > 0)
        {
            Mcmmorankup.log.log(Level.INFO,"Try to rank up online users...");
            for (Player player : playerList) {
                  plugin.PowerLevel.tryRankUp(player);
            }
        }
        
    }
    
}
