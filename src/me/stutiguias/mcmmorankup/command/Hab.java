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
public class Hab extends CommandHandler {

    public Hab(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = (Player)sender;
        
        if(isInvalid(sender, args)) return true;
                
        if (args.length > 1) {
            if (plugin.isIgnored((Player) sender)) {
                SendMessage(Message.IgnoredRankLineSet);
                return true;
            } 
            return SetRankOnSkill(args[1]);
        } 
        return ListHability();
    }

    public boolean SetRankOnSkill(String Skill) {

        Profile profile = new Profile(plugin, (Player) sender);

        if (!plugin.isRankAvailable(Skill, profile.player)) {
            SendMessage(Message.HabilitySetFail.replace("%ability%", Capitalized(Skill)));
            return true;
        }
        
        if (!plugin.CheckRankExist(Skill)) {
            SendMessage(Message.MessageSeparator);
            SendMessage(Message.NotAvailable);
            SendMessage(Message.MessageSeparator);
            return false;
        }
        return profile.SetHabilityForRank(Skill);
    }
    
    public boolean ListHability() {
        SendMessage(Message.BaseRanksListing);
        String outMsg;
        String preFix="";
        Profile profile = new Profile(plugin, (Player)sender);
        String skill = profile.GetHabilityForRank();
        String fmt = "%1$-2s &6%2$-25s %3$-32s";
        boolean noPerm = false;

        for (String key : plugin.isRankExist.keySet()) {
 		
            if (!plugin.isRankAvailable(key, profile.player)) {
                noPerm = true;
            } else {
                if (plugin.BuyRankEnabled.size() == 0 && plugin.BuyRankEnabled.get(key)) {
                    preFix = Message.HabListPrefixBuy;
                }

                if (plugin.isRankExist.get(key)) {
                      outMsg = Message.AbilityEnabled;
                }else{
                      if(!plugin.displayDisabledRanks) continue;
                      outMsg = Message.AbilityDisabled;
                } 
                
                if (key.equalsIgnoreCase(skill)) {
                      outMsg = Message.HabListCurRankLine;
                } 

                String playerLevel = String.valueOf(plugin.GetSkillLevel(profile.player, key));
                SendMessage(fmt, new Object[] { preFix, key + Message.HabListLevel.replace("%level%", " " + playerLevel) , outMsg });
            }

        }

        SendMessage(("\n" + Message.DefaultSkilltoRank).replace("%ability%", plugin.DefaultSkill));
        if (!plugin.displayDisabledRanks || noPerm) {
            SendMessage("\n" + Message.NotShowInfo);
        }
        SendMessage(Message.MessageSeparator);
 	    	
        if (!plugin.isRankAvailable(skill, profile.player)) {
            SendMessage(Message.NoAccess.replaceAll("%rankline%", skill.toUpperCase()));
        }

        if (!plugin.CheckRankExist(skill.toUpperCase())) {
            SendMessage(Message.NoLongerExists.replaceAll("%rankline%", skill.toUpperCase()));
        }

        return true;
    }
    
    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        if (!plugin.hasPermission(player, "mru.hability")) {
            SendMessage("&4You don't have permission");
            return true;
        }
        return false;
    }
    
}
