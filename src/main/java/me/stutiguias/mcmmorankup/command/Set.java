/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Set extends CommandHandler {

    public Set(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        player = (Player)sender;
        
        if(isInvalid(sender, args)) return true;
        
        if (args.length == 1) return ShowConfigSettting();
        if (args.length > 1)  return isSetCommand(args);
        return true;
    }

    private boolean ShowConfigSettting() {
        SendMessage("&2Usage: &c/mru set <setting> <value>&2:");
        SendMessage(ShowConfigSettings());
        return true;
    }

    private String ShowConfigSettings() {
        StringBuilder settings = new StringBuilder();
        settings.append("&2Settings: &cability <ability> <value>, autorank, autotime <#m/#h>, defskill <ability>, starttag <name>, demotions, brdemotions, brsamedemotions, globalfeed, ");
        settings.append("nextpromoinfo, rankinfoxp, rankinfotitles, onjoin, onjoindelay, playerfeed, playerxpfeed, buyranks, rankrewards, remplugingrp, ");
        settings.append("showdisabled, usealtbroadcast, usegenders, usetag, startsummary");
        settings.append("\n&2Values: '&con&2' or '&coff&2', &cM&2=minutes & &cH&2=Hours, &cAbility &2= Skill Name");
        return settings.toString();
    }
    
    private boolean isSetCommand(String[] args) {
        switch (args[1].toLowerCase()) {
            case "ability":
            case "defskill":
                return ChangeAbilityConfig(args);
            default:
                return ChangeOtherConfigs(args);
        }
    }
    
    private boolean ChangeAbilityConfig(String[] args) {
        boolean validSkill = false;
        String skill = "";
        String rankSkills = "\n";

        if (args.length >= 3) {
            skill = args[2].toUpperCase();
        }

        for (String key : plugin.isRankExist.keySet()) {
            if (plugin.isRankExist.get(key)) {
                rankSkills += "&a" + key + "  - Enabled\n";
            } else {
                rankSkills += "&8" + key + "  - Disabled\n";
            }

            if (!validSkill) {
                validSkill = skill.equalsIgnoreCase(key);
            }
        }

        if (args.length == 2) {
            SendMessage(Message.MessageSeparator);
            SendMessage("&6RankUp Files and Status: (default: &c%s&6) %s", new Object[] { plugin.DefaultSkill, rankSkills });
            SendMessage("&7  ** (ability names are not case sensitive)");
            SendMessage(Message.MessageSeparator);
        }
        
        if (args.length == 3 && !validSkill) {
            SendMessage("&4" + "Invalid ability section...");
            return true;
        }
        
        if (args[1].equalsIgnoreCase("defskill")) {
           return SetDefaultSkill(args,skill);
        }

        if (args[1].equalsIgnoreCase("ability")) {
            return SetSkillStatus(args,skill);
        }
        
        return false;
    }
        
    private boolean SetSkillStatus(String[] args,String skill) {
        if (args.length < 4) {
             SendMessage("&6Usage: &f/mru set ability &6<&7ability&6> <&7on &6or &7off&6>");
             SendMessage("&6Type: &f/mru set ability &6for Rank File Listings");
             return true;
         }
         if (!ParseToggleInput(args[3])) {
             SendMessage("&6&lInvalid ability setting. &6Values allowed: &eon,off,true,false");
             return true;
         }

         String toggle = (args[3].toUpperCase().matches("[oO]N|[tT]RUE") ? "true" : "false");
         if (UpdateSetConfig("Config.Skills." + skill + ".enabled", toggle)) {

             if (skill.equalsIgnoreCase(plugin.DefaultSkill)) {
                 SendMessage("&cYou disabled the current default skill.");
                 SendMessage("&cDefault Skill reset back to POWERLEVEL, if it");
                 SendMessage("&cis disabled, POWERLEVEL will be enabled on reload");
                 plugin.DefaultSkill = "POWERLEVEL";
                 plugin.config.getConfig().set("Config.DefaultSkill", plugin.DefaultSkill);
                 plugin.config.saveConfig();
                 plugin.config.reloadConfig();
                 Mcmmorankup.logger.log(Level.INFO, "{0} {1} just updated Default Skill: {2} to a {3} status", new Object[]{Mcmmorankup.logPrefix, sender.getName(), skill,toggle});
             }

             SendMessage("&fRanking Ability ' &3%s &f' has been set: &3%s", new Object[] { skill, args[3].toUpperCase() });
             SendMessage("&cNote: &6Ability files disabled &cARE NOT DELETED&6!!");
             Mcmmorankup.logger.log(Level.INFO, "{0} {1} just updated ability: {2} to a {3} status", new Object[]{Mcmmorankup.logPrefix, sender.getName(), skill,toggle});
         }
         SendMessage(Message.MessageSeparator);
         return true;
    }
        
    private boolean SetDefaultSkill(String[] args,String skill) {
        if (args.length < 3) {
           SendMessage("&6Usage: &f/mru set &6<&7setting&6> <&7value&6>");
           SendMessage("&6Type: &f/mru set &6For Configuration settings");
           return true;
       }

       plugin.DefaultSkill = skill;
       plugin.config.getConfig().set("Config.DefaultSkill", plugin.DefaultSkill);
       plugin.config.saveConfig();
       plugin.config.reloadConfig();
       SendMessage("&fDefaultSkill updated to: &3" + skill);
       SendMessage("&cNote: Players who were using previous default skill will be");
       SendMessage("&c       notified to change their rank-up ability to new default");
       SendMessage("&cNote: If new default skill is currently disabled, it will");
       SendMessage("&c       be enabled upon reload or server restart.");
       return true;
    }
    
    private boolean ChangeOtherConfigs(String[] args) {
        if (args.length < 3) {
            SendMessage("&6Usage: &f/mru set &6<&7setting&6> <&7value&6>");
            SendMessage("&6Type: &f/mru set &6For Configuration settings");
            return true;
        }

        String cfg = "";
        String eMsg = "&c/mru set value is invalid... \n&2expected: ";
        String value;
        boolean invalid = true;
        boolean stdParse = true;

        switch (args[1].toLowerCase()) {
            case "startsummary":
                cfg = "mruStartupSummary";
                break;
            case "usetag":
                cfg = "UseTagOnlySystem";
                break;
            case "remplugingrp":
                cfg = "RemoveOnlyPluginGroup";
                break;
            case "showdisabled":
                cfg = "DisplayDisabledRanks";
                break;
            case "demotions":
                cfg = "AllowDemotions";
                break;
            case "brdemotions":
                cfg = "AllowBuyRankDemotions";
                break;
            case "brsamedemotions":
                cfg = "AllowBuyRankSameRankLineDemotions";
                break;
            case "onjoin":
                cfg = "PromoteOnJoin";
                break;
            case "globalfeed":
                cfg = "GlobalBroadcastFeed";
                break;
            case "playerfeed":
                cfg = "PlayerBroadcastFeed";
                break;
            case "nextpromoinfo":
                cfg = "DisplayNextPromo";
                break;
            case "autorank":
                cfg = "AutoUpdate";
                break;
            case "usealtbroadcast":
                cfg = "UseAlternativeBroadCast";
                break;
            case "usegenders":
                cfg = "UseGenderClass";
                break;
            case "buyranks":
                cfg = "AllowBuyRanks";
                break;
            case "rankrewards":
                cfg = "AllowRankRewards";
                break;
            case "rankinfoxp":
                cfg = "DisplayRankInfoXp";
                break;
            case "rankinfotitles":
                cfg = "RankInfoTitles";
                break;
            case "playerxpupdate":
                cfg = "PlayerAbilityXpUpdateFeed";
                break;
            case "autotime":
                cfg = "AutoUpdateTime";
                stdParse = false;
                String tstTime = args[2] = args[2].toLowerCase();
                int upTime = Integer.parseInt(tstTime.replace("h", "").replace("m", ""));
                if (upTime >= 1 && upTime <= 24) {
                    invalid = false;
                }
                eMsg += "number<d|h> betwen &71<d|h>&2 and &724<d|h>";
                break;
            case "onjoindelay":
                cfg = "OnJoinDelay";
                stdParse = false;
                if (isNumeric(args[2])) {
                    int delay = Integer.parseInt(args[2]);
                    if (delay >= 0 && delay <= 72000) {
                        invalid = false;
                    }
                }
                eMsg += "number between &70&2 and &772000&2 (60 min)";
                break;
            case "starttag":
                cfg = "StartTagName";
                stdParse = false;
                if (isChar(args[2])) {
                    invalid = false;
                }
                eMsg += "'chars' for Starting Tag Name for new players";
                break;
        }
    
        if (cfg.isEmpty()) {
            SendMessage(Message.GeneralMessages + "Invalid configuration setting. Type /mru set for settings");
            return true;
        }
        
        if (stdParse) {
            if (ParseToggleInput(args[2])) {
                invalid = false;
                eMsg += "on, off, true, false";
            }
            value = (args[2].matches("[oO]n|[tT]rue") ? "true" : "false");
        }else{
            value = args[2];
        }

        if (invalid) {
            SendMessage(Message.MessageSeparator);
            SendMessage(Message.GeneralMessages + eMsg);
            return false;
        } 
        
        cfg = "Config." + cfg;
        if (UpdateSetConfig(cfg, value)) {
            SendMessage(Message.MessageSeparator);
            SendMessage(Message.GeneralMessages + "%s has been updated to: &7%s" , new Object[] { cfg, value });
            Mcmmorankup.logger.log(Level.INFO, "{0} {1} just updated Configuration Setting: {2} to a {3} status", new Object[]{Mcmmorankup.logPrefix, sender.getName(), args[1],value});
        }
        
        SendMessage(Message.MessageSeparator);
        return true;
    }
        
    private boolean UpdateSetConfig(String setting, String value) {
        try {
            if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                plugin.config.getConfig().set(setting,Boolean.parseBoolean(value));
            }else{
                plugin.config.getConfig().set(setting, value);
            }
            plugin.config.saveConfig();
            plugin.config.reloadConfig();
            plugin.onLoadConfig();
            return true;
        } catch (Exception e) {
            SendMessage("&4There was a problem setting that configuration setting!");
        }
        return false;
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
