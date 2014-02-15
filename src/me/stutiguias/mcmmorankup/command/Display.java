/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import java.util.ArrayList;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public class Display extends CommandHandler {

    public Display(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        
        if(args.length < 3) {
            SendMessage(Message.MessageSeparator);
            SendMessage(Message.DisplayInformAll);
            SendMessage(Message.MessageSeparator);
            return true;
        }
        
        String skill = args[1];
        String gender = args[2];
        skill = skill.toUpperCase();
        gender = WordUtils.capitalize(gender);
        
        if(!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")) {
            SendMessage(Message.MessageSeparator);
            SendMessage(Message.DisplayValidGender);
            SendMessage(Message.MessageSeparator);
            return true;
        }
        
        ArrayList<String> ranks;
        try{
            ranks = plugin.RankUpConfig.get(skill).get(gender);
        }catch(Exception ex) {
            SendMessage(Message.MessageSeparator);
            SendMessage(Message.DisplayValidSkill);
            SendMessage(Message.MessageSeparator);
            return true; 
        }
        
        SendMessage(Message.MessageSeparator);
        SendMessage(Message.DisplayTitle.replace("%skill%", skill).replace("%gender%", gender));
        
        for (String entry : ranks) {
            String[] levelRank = entry.split(",");
            SendMessage(Message.DisplayLine.replace("%point%", levelRank[1]).replace("%rank%",levelRank[0]));
        }
        SendMessage(Message.MessageSeparator);
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
