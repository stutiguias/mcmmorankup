/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Pinfo extends CommandHandler {

    public Pinfo(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = (Player)sender;
        
        if(isInvalid(sender, args)) return true;
        
        plugin.displayNextPromo = !plugin.displayNextPromo;
        plugin.config.getConfig().set("Config.DisplayNextPromo",plugin.displayNextPromo);
        plugin.saveConfig(); 
        SendMessage("&6&lPromotional Info. Toggled &7&l%s",new Object[]{ plugin.displayNextPromo ? "ON" : "OFF" });
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
