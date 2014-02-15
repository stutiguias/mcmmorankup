/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.profile.Profile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class RankUp extends CommandHandler {

    
    
    public RankUp(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = (Player) sender;
        
        if(isInvalid(sender, args)) return true;

        if (plugin.Playertime.containsKey(sender.getName()) && plugin.Playertime.get(sender.getName()) + 5000 > plugin.GetCurrentMilli()) {
            SendMessage(Message.MessageSeparator);
            SendMessage(Message.CommandAttempt);
            SendMessage(Message.MessageSeparator);
            return true;
        }

        Profile profile = new Profile(plugin,(Player)sender);
        String skill = profile.GetHabilityForRank().toUpperCase();
        String gender = profile.GetGender();

        if (!plugin.CheckRankExist(skill)) {
            SendMessage(Message.NoLongerExists.replace("%rankline%", skill.toUpperCase()));
            return false;
        }

        if (!plugin.isRankAvailable(skill,profile.player)) {
            SendMessage(Message.NoAccess.replace("%rankline%", skill.toUpperCase()));
            return true;
        }

        String ruStatus = plugin.RankUp.TryRankUp(profile.player, skill, gender);
        plugin.Playertime.put(sender.getName(), plugin.GetCurrentMilli());

        if (plugin.playerBroadcastFeed && !profile.GetPlayerRankupFeed()) {
            return true;
        }

        String bCastMsg;
        switch(ruStatus.toLowerCase()){
            case "promoted":
                bCastMsg = Message.Sucess;
                break;
            case "demoted":
                bCastMsg = Message.Demotion;
                break;
            case "ignore":
                 bCastMsg = Message.PromosIgnored;
                 break;
            default:
                 bCastMsg = null;
                 break;
        }

        if (bCastMsg != null) {
            SendMessage(Message.MessageSeparator);
            SendMessage(bCastMsg);
            SendMessage(Message.MessageSeparator);
        }
        
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        if (!plugin.hasPermission(player, "mru.rankup")) {
            SendMessage("&4You don't have permission");
            return true;
        }
        return false;
    }
    
}
