/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.XpCalc;
import me.stutiguias.mcmmorankup.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Stats extends CommandHandler {

    public Stats(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = (Player)sender;
        
        if(isInvalid(sender, args)) return true;
        
        
        Profile profile = new Profile(plugin, (Player)sender);

        boolean lastSet = false;
        
        if (args.length == 2 && !args[1].isEmpty()) {
            OfflinePlayer ckPlayer = Bukkit.getOfflinePlayer(args[1].toString());
            if (!ckPlayer.hasPlayedBefore()) {
                SendMessage(Message.PlayerWarnings + "Not a player of the server...");
                return true;
            } 
            if (!ckPlayer.isOnline()) {
                SendMessage("&2&l" + ckPlayer.getName() + Message.PlayerWarnings + " is not online!");
                SendMessage(Message.PlayerWarnings + "\nStats are restricted to online players!");
                return true;
            } 			
            profile = new Profile(plugin,(Player)ckPlayer.getPlayer());
        }
        
        String lastRank        = profile.GetQuitRank();
        String lastSkill       = profile.GetQuitSkill();
        int lastSkillLevel     = profile.GetQuitLevel();
        int lastXp             = (int) profile.GetQuitXp();
        int lastXpL            = profile.GetQuitXpLevel();
        double lastBalance     = profile.GetQuitBalance();
    
        String rank            = plugin.TagSystem ? profile.GetTag() : plugin.permission.getPrimaryGroup(profile.player.getWorld(), profile.player.getName());
        String skill           = profile.GetHabilityForRank();
        Integer skillLevel     = plugin.GetSkillLevel(profile.player, skill);
        int Xp                 = XpCalc.GetTotalExperience(profile.player);
        int XpL                = XpCalc.GetPlayerXpl(profile.player);
        double balance         = plugin.GetPlayerCurrency(profile.player);

        if (lastSkill.isEmpty() || lastSkill.equalsIgnoreCase("N/A")) {
            SendMessage(Message.LastQuitStatsFail + "\n\n");
            profile.SetQuitStats();
            lastSet = true;
        }	
        
        StringBuilder outPut = new StringBuilder();
        outPut.append("\n").append(profile.player.getName());
        outPut.append("\n").append(String.format("%1$45s", "&a&oLAST STATS:"));
        if (!lastSet) {
            outPut.append("\n&3").append(plugin.BuyRankCurrencyName).append(" &6").append(String.format("$%.2f", lastBalance));
            outPut.append("\n").append(Message.BuyPurchaseXp.replace("%xp%", String.valueOf(lastXp)).replace("%level%", String.valueOf(lastXpL)));
            outPut.append("\n&3 Rank: &6").append(Message.BuyProfile.replace("%rankline%", lastSkill.toLowerCase()).replace("%group%", Capitalized(lastRank)).replace("%level%", String.valueOf(lastSkillLevel)));
        } else {
            outPut.append("\n").append(Message.PlayerWarnings).append("No last stats available - Profile just migrated...");
        }
        outPut.append("\n").append(Message.MessageSeparator);
        outPut.append(String.format("%1$45s", "&a&oCURRENT STATS:"));
        outPut.append("\n&3").append(plugin.BuyRankCurrencyName).append(" &6").append(String.format("$%.2f", balance));
        outPut.append("\n").append(Message.BuyPurchaseXp.replace("%xp%", String.valueOf(Xp)).replace("%level%", String.valueOf(XpL)));
        outPut.append("\n&3 Rank: &6").append(Message.BuyProfile.replace("%rankline%", skill.toLowerCase()).replace("%group%", Capitalized(rank)).replace("%level%", String.valueOf(skillLevel)));
        outPut.append("\n").append(Message.MessageSeparator).append("\n");

        SendMessage(outPut.toString());
        
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        
        if(args.length == 2 && !plugin.hasPermission(player, "mru.stats.others")) {
            SendMessage("&4You don't have permission");
            return true;
        }
        
        if (plugin.hasPermission(player, "mru.stats") || plugin.hasPermission(player, "mru.admin.config")) {
            return false;
        }
        SendMessage("&4You don't have permission");
        return true;
    }

}
