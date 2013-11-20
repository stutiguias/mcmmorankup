/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.mcmmorankup.task;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.profile.Profile;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class OnJoinTask implements Runnable {

    Mcmmorankup plugin;
    Profile profile;

    public OnJoinTask(Mcmmorankup plugin, Player player) {
        this.plugin = plugin;
        this.profile = new Profile(plugin, player);
    }

    @Override
    public void run() {
        String skill = profile.GetHabilityForRank().toUpperCase();
        String gender = profile.GetGender();
        String status;
        boolean canBroadCast = false;
        
        if (plugin.isIgnored(profile.player)) {
            if(plugin.playerBroadcastFeed) {
                profile.SendFormatMessage(plugin.Message.RankCheckingIgnore.replace("%player%", profile.player.getName()).replace("%colorreset%", plugin.GeneralMessages));
            }
            return;
        }
        
        if (plugin.playerBroadcastFeed) {
            profile.SendFormatMessage(plugin.Message.RankChecking.replace("%player%", profile.player.getName()).replace("%colorreset%", plugin.GeneralMessages));
        }
        
        if (plugin.playerBroadcastFeed) {
            canBroadCast = profile.GetPlayerRankupFeed();
        }

        if (!plugin.CheckRankExist(skill))  {
            profile.SendFormatMessage(plugin.Message.NoLongerExists.replaceAll("%rankline%", skill.toUpperCase()));
            return;
        }
        
        if (!plugin.isRankAvailable(skill, profile.player)) {
            profile.SendFormatMessage(plugin.Message.NoAccess.replaceAll("%rankline%", skill));
            return;
        } 

        status = plugin.RankUp.TryRankUp(profile.player, skill, gender);

        if (!canBroadCast) return;

        switch (status.toLowerCase()) {
            case "promoted":
                profile.SendFormatMessage(plugin.Message.Sucess);
                break;
            case "demoted":
                profile.SendFormatMessage(plugin.Message.Demotion);
                break;
        }

    }
    
}
