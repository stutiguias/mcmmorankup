/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Help extends CommandHandler {

    public Help(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
               
        player = (Player) sender;
        SendMessage(Message.MessageSeparator);
        SendMessage(" &7RANKING HELP ");

        if (plugin.hasPermission(player, "mru.hability")) {
            SendMessage("&6/mru view <on|off> &7" + Message.HelpView);
            SendMessage("&6/mru hab &7" + Message.HelpHab);
            SendMessage("&6/mru hab <ability> " + SetHelpForHab());
            SendMessage("&6/mru display <ability> <gender> &7" + Message.HelpDisplayHab);
        }
        
        if (plugin.hasPermission(player, "mru.rankup")) {
            SendMessage("&6/mru rank &7" + Message.HelpRank);
        }

        if (plugin.UseGenderClass && plugin.hasPermission(player, "mru.setgender")) {
            SendMessage("&6/mru <male|female> &7" + Message.HelpMaleFemale);
        }
        
        if (plugin.hasPermission(player, "mru.playerfeeds") && plugin.playerBroadcastFeed) {
            SendMessage("&6/mru feeds &7" + Message.HelpFeeds);
        }

        if (plugin.hasPermission(player, "mru.buyrankxp") || plugin.hasPermission(player, "mru.buyrankbuks")) {
            SendMessage("&6/mru buy <x | b> &7" + Message.BuyMenu.replace("%currencyname%", plugin.BuyRankCurrencyName));
        }

        if (plugin.hasPermission(player, "mru.stats") || plugin.hasPermission(player, "mru.stats.others")) {
            SendMessage("&6/mru stats [player] &7Stats Skill Check. [player] Optional");
        }
        
        if (plugin.hasPermission(player, "mru.admin.config")) {
            SendMessage(Message.MessageSeparator);
            SendMessage(" &7ADMIN HELP ");
            SendMessage("&6/mru ver &7Show mcmmoRankup version information");
            SendMessage("&6/mru report &7Admin Ranking Report Options");
            SendMessage("&6/mru set <setting> <value> &7Set Config. Settings");
            SendMessage("&6/mru pinfo &7Toggle Next Promotion Info. &e" + (plugin.displayNextPromo ? "OFF" : "ON"));
        }
        
        if (plugin.hasPermission(player, "mru.admin.reload")) {
            SendMessage("&6/mru reload &7Reload the all configs...");
        }

        SendMessage(Message.MessageSeparator);

        return true;
    }

    private String SetHelpForHab(){
        String outMsg;
        if (!plugin.isIgnored(player)) {
            outMsg = "&7" + Message.HelpSethab;
        } else {
            outMsg = "&c" + Message.HelpSethabIgnore;
        }
        return outMsg;
    }
    
    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
