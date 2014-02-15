/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.profile.AdminProfiler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Report extends CommandHandler {

    public Report(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = (Player)sender;
        
        if(isInvalid(sender, args)) return true;
        
                
        if (!sender.getName().equalsIgnoreCase("CONSOLE") && !plugin.hasPermission((Player) sender,"mru.admin.config")) {
                return false;
        }

        if (args.length < 2 || args.length > 4) {
            SendMessage("&6Usage: /mru report <a | g | c> <skill> [filename]");
            SendMessage("&2  - a [filename] - all players grouped by ability report");
            SendMessage("&2  - g [filename] - all players grouped by gender report");
            SendMessage("&2  - c <skill> [filename] - Specific ability only report");
            SendMessage("&7** FileName is Optional.");
            return true;
        }
        
        String cat = null;
        String reportFileName = null;

        switch(args[1].toLowerCase()) {
            case "c":
                if (!plugin.isRankExist.containsKey(args[2].toUpperCase())) {
                    SendMessage(Message.PlayerWarnings + "Huh! Expected a skill as the reports category and a valid skill!");
                    return false;
                }
                cat = args[2];
                
                if (args.length == 4) {
                    reportFileName = args[3];
                }
                break;
            case "g":
                cat = args[1];
            case "a":
                if (args.length == 3) {
                    reportFileName = args[2];
                }
                break;
            default:
                SendMessage(Message.PlayerWarnings + "not a valid argument for this command.");
                return true;
        }

        AdminProfiler AdminProfiler = new AdminProfiler(sender, plugin, args[1], cat);
        AdminProfiler.PrintReport(reportFileName, cat);

        return true;
        
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        if (!plugin.hasPermission(player, "mru.admin.config")) {
            SendMessage("&4You don't have permission");
            return true;
        }
        return false;
    }
    
}
