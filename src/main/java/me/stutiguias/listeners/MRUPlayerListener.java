/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.listeners;

import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Daniel
 */
public class MRUPlayerListener implements Listener {
    
    private final Mcmmorankup plugin;

    public MRUPlayerListener(Mcmmorankup plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority= EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        if(plugin.PromoteOnJoin) {
            boolean sucess = plugin._RankUp.tryRankUp(pl,"POWERLEVEL");
            if(sucess)
             event.getPlayer().sendMessage(plugin.MSucess);
        }
        
        Profile profile = new Profile(plugin, pl);
    }
}