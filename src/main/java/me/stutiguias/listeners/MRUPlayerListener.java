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
        Profile _profile = new Profile(plugin, pl);
        if(plugin.PromoteOnJoin) {
            String skill = _profile.getHabilityForRank().toUpperCase();
            String gender = _profile.getGender();
            if(plugin.RankUp.tryRankUp(pl,skill,gender)) event.getPlayer().sendMessage(plugin.MSucess);
        }
    }
}