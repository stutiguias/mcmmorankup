/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.mcmmorankup.task;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.Utilities;
import me.stutiguias.mcmmorankup.profile.Profile;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class OnJoinTask extends Utilities implements Runnable {

    Profile profile;

    public OnJoinTask(Mcmmorankup plugin, Player player) {
        super(plugin);
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
                SendMessage(profile.player,Message.RankCheckingIgnore.replace("%player%", profile.player.getName()).replace("%colorreset%", Message.GeneralMessages));
            }
            return;
        }
        
        if (plugin.playerBroadcastFeed) {
            SendMessage(profile.player,Message.RankChecking.replace("%player%", profile.player.getName()).replace("%colorreset%", Message.GeneralMessages));
        }
        
        if (plugin.playerBroadcastFeed) {
            canBroadCast = profile.GetPlayerRankupFeed();
        }

        if (!plugin.CheckRankExist(skill))  {
            SendMessage(profile.player,Message.NoLongerExists.replaceAll("%rankline%", skill.toUpperCase()));
            return;
        }
        
        if (!plugin.isRankAvailable(skill, profile.player)) {
            SendMessage(profile.player,Message.NoAccess.replaceAll("%rankline%", skill));
            return;
        } 

        status = plugin.RankUp.TryRankUp(profile.player, skill, gender);

        if (!canBroadCast) return;

        switch (status.toLowerCase()) {
            case "promoted":
                SendMessage(profile.player,Message.Sucess);
                break;
            case "demoted":
                SendMessage(profile.player,Message.Demotion);
                break;
        }

    }
    
}
