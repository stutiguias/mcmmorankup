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
public class Feeds extends CommandHandler {

    public Feeds(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = (Player)sender;
        
        if(isInvalid(sender, args)) return true;

        Profile profile = new Profile(plugin, (Player) sender);

        boolean rankupFeed  = profile.GetPlayerRankupFeed();
        boolean xpFeed      = profile.GetPlayerXpUpdateFeed();
        boolean globalFeed  = profile.GetPlayerGlobalFeed();

        if (args.length < 3) {
            SendMessage(Message.MessageSeparator);
            SendMessage("&3Manage Your mcmmoRank Feeds &a(&f&lcurrent status&a)");
            SendMessage(Message.PlayerWarnings + "Usage: /mru feeds <feedname> <on | off>");
            SendMessage(Message.MessageSeparator);
            SendMessage("&aRankup &6(&f %s& 6)&a - Show your Promotion when you rank up",new Object[] { rankupFeed ? "On" : "Off" });
            SendMessage("&aXp &6(&f %s& &6)&a - Excluding Powerlevel, show your skills XP Ups?",new Object[] { xpFeed ? "On" : "Off" });
            SendMessage("&aGlobal &6(&f %s& &6)&a - Share your Promotions/Demotions with others??\n",new Object[] { globalFeed ? "On" : "Off" });
            SendMessage(Message.MessageSeparator);
            return true;
        }

        String msg = "";
        boolean toggle = ParseToggleInput(args[2]);

        if (!toggle) {
            SendMessage(Message.GeneralMessages + "Invalid setting: Expected On, Off, True or False");
            return false;
        }
        
        toggle = (args[2].toUpperCase().matches("[oO]N|[tT]RUE"));
        
        switch(args[1].toLowerCase()) {
            case "rankup": 
                if (toggle != rankupFeed) {
                    profile.SetPlayerRankupFeed(toggle);
                    msg = "&3Rank Up";
                }
                break;
            case "global":
                if (toggle != globalFeed) {
                    profile.SetPlayerGlobalFeed(toggle);
                    msg = "&3Your Global";
                }
                break;
            case "xp":
                if (toggle != xpFeed) {
                    profile.SetPlayerXpUpdateFeed(toggle);
                    msg = "&3Ability Xp Updates";
                }
                break;
        }

        if (!msg.isEmpty()) {
            msg += " Feed has been set: &f" + (toggle ? "On" : "Off");
        } else {
            msg = Message.PlayerWarnings + "Was already " + (toggle ? "On" : "Off") + " or not a valid feed!";
        }
        
        SendMessage(msg);
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        if (!plugin.playerBroadcastFeed) {
            SendMessage(Message.PlayerFeedsDisabled);
            return true;
        }

        if (!plugin.hasPermission(player, "mru.playerfeeds")) {
            SendMessage(Message.NoPermPlayerFeeds);
            return true;
        } 
        return false;
    }
    
}
