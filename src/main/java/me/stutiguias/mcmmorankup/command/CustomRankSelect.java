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
public class CustomRankSelect extends CommandHandler {

    private String rank;
    
    public CustomRankSelect(Mcmmorankup plugin,String command) {
        super(plugin);
        this.rank = command;
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        
        if(isInvalid(sender, args)) return true;
        
        Profile profile = new Profile(plugin, (Player)sender);
        profile.SetGender(rank);
        SendMessage("RANK SELECTED");
        SendMessage(Message.GeneralMessages + Message.SetGender.replace("%gender%", rank));
        SendMessage(Message.MessageSeparator);
        
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        return false;
    }
    
}
