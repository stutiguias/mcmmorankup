package me.stutiguias.task;

import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
            Mcmmorankup.logger.log(Level.INFO," Attempting to rank up online users...");
            for (Player player : playerList) {
                try {
                    Profile _profile = new Profile(plugin, player);
                    String skill = _profile.getHabilityForRank().toUpperCase();
                    String gender = _profile.getGender();
                    
                    player.sendMessage(ChatColor.AQUA + player.getName() + ", " + ChatColor.DARK_AQUA + " checking your rank and trying to promote...");
                    
                    if(plugin.TagSystem) {
                        plugin.RankUp.tryRankUpWithoutGroup(player, skill, gender);
                    } else {
                        plugin.RankUp.tryRankUp(player, skill, gender);    	// zrocweb: added "rank" to params
                    }
                } catch(Exception ex) {
                    
                }
            }
        }
        
    }
    
}
