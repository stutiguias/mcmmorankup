package me.stutiguias.mcmmorankup.task;

import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Utilities;
import me.stutiguias.mcmmorankup.profile.Profile;

import org.bukkit.entity.Player;

public class UpdateTask extends Utilities implements Runnable {

    public UpdateTask(Mcmmorankup plugin) {
    	super(plugin);       
    }
    
    @Override
    public void run() {
        Player[] playerList = plugin.getServer().getOnlinePlayers();
        
        if (plugin.getServer().getOnlinePlayers().length == 0) return;
            
        if(plugin.globalBroadcastFeed) {
            Mcmmorankup.logger.log(Level.INFO,"{0} Attempting to rank up online players...", new Object[]{Mcmmorankup.logPrefix});
        }

        for (Player player : playerList) {
            try {
                    if (!plugin.hasPermission(player, "mru.rankup") || plugin.isIgnored(player)) continue;

                    Profile _profile = new Profile(plugin, player);
                    String skill = _profile.GetHabilityForRank().toUpperCase();
                    String gender = _profile.GetGender();

                    if (plugin.playerBroadcastFeed) {
                        SendMessage(_profile.player,plugin.Message.RankChecking.replace("%player%", player.getName()).replace("%colorreset%", plugin.GeneralMessages));						
                    }

                    if (!plugin.isRankAvailable(skill, player)) {
                            SendMessage(_profile.player,plugin.Message.NoAccess.replaceAll("%rankline%", skill.toUpperCase()));
                    } else if(!plugin.CheckRankExist(skill.toUpperCase())) {
                            SendMessage(_profile.player,plugin.Message.NoLongerExists.replaceAll("%rankline%", skill.toUpperCase()));
                    } else {
                            plugin.RankUp.TryRankUp(player, skill, gender);
                    }

            } catch(Exception ex) {

            }
        }
            
    }  
    
}
