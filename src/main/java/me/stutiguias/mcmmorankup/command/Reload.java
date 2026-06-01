/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public class Reload extends CommandHandler {

    public Reload(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = sender instanceof org.bukkit.entity.Player ? (org.bukkit.entity.Player)sender : null;
        
        if(isInvalid(sender, args)) return true;
        
        SendMessage(ChatColor.YELLOW + "Start reload...");
        plugin.onReload();
        SendMessage(ChatColor.YELLOW + "Reload complete...");
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        if (player == null) return false;
        if (!plugin.hasPermission(player, "mru.admin.config")) {
            SendMessage("&4You don't have permission");
            return true;
        }
        return false;
    }
    
}
