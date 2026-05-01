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
public class Male extends CommandHandler {

    public Male(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        
        if(isInvalid(sender, args)) return true;
        
        Profile profile = new Profile(plugin, (Player)sender);
        profile.SetGender("Male");
        SendMessage("GENDER SELECTED");
        SendMessage(Message.GeneralMessages + Message.SetGender.replace("%gender%", "Male"));
        SendMessage(Message.MessageSeparator);

        if(plugin.GenderFirst){
           String group = plugin.GetPlayerCurrentGroup((Player)sender);
           if(plugin.GenderOnlyGroup.equals(group)){
               plugin.permission.playerAddGroup((Player)sender, plugin.DefaultGroupAfterChoose);
               plugin.permission.playerRemoveGroup((Player)sender, group);
           }
        }
        
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        if(!plugin.hasPermission((Player)sender, "mru.setgender")){
            SendMessage("&4You don't have permission");
            return true;
        }
        if(!plugin.UseGenderClass) {
            SendMessage("&4Your server not allow gender");
            return true;
        }
        return false;
    }
    
}
