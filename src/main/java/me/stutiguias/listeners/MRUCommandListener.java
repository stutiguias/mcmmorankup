/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.listeners;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.profile.Profile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Stutiguias
 */
public class MRUCommandListener implements CommandExecutor {
    
    public Mcmmorankup plugin;
    
    public MRUCommandListener(Mcmmorankup instance)
    {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
         // Do not use Console !
         if(!(cs instanceof Player) || args.length == 0) return false;

         // Its check ?
         if(args[0].equalsIgnoreCase("check")) return Check(cs);
         
         // Change to Rank Up on Hability
         if(args[0].equalsIgnoreCase("hab")) {
             if(args.length > 1) {
                return RankOnHability(cs,args[1].toString());
             }else{
                return ListHability(cs);
             }
         } 
         
         if(args[0].equalsIgnoreCase("male")) {
             setGender(cs,"Male");
         }
         
         if(args[0].equalsIgnoreCase("female")) {
             setGender(cs,"Female");
         }
         
         // Its Reload ?
         if(args[0].equalsIgnoreCase("reload")) {
             Player _player = (Player)cs;
             if(!plugin.permission.has(_player.getWorld(), _player.getName(),"mru.reload")) return false;
             plugin.onReload();
             cs.sendMessage("Reload Done!");
             return false;
         }
         
         if(args[0].equalsIgnoreCase("help")) return Help(cs);
         
         return false;
    }
    
    public boolean Check(CommandSender cs) {
             boolean alreadyuse = false;
             
             if(plugin.Playertime.isEmpty())
             {
                 alreadyuse = false;
             }else if(plugin.Playertime.containsKey(cs.getName()) && plugin.Playertime.get(cs.getName()) + 10000 > plugin.getCurrentMilli()) {
                 alreadyuse = true;
             }
         
             if(!alreadyuse)    
             {
                Player pl = plugin.getServer().getPlayerExact(cs.getName());
                Profile _profile = new Profile(plugin, pl);
                String skill = _profile.getHabilityForRank().toUpperCase();
                String gender = _profile.getGender();
                Boolean sucess;
                if(plugin.TagSystem) {
                  sucess = plugin.RankUp.tryRankUpWithoutGroup(pl, skill, gender);
                }else{
                  sucess =  plugin.RankUp.tryRankUp(pl,skill,gender);
                }
                if(sucess)
                {
                    cs.sendMessage("-----------------------------------------------------");
                    cs.sendMessage(plugin.parseColor(plugin.MSucess));
                    cs.sendMessage("-----------------------------------------------------");
                }else{
                    cs.sendMessage("-----------------------------------------------------");
                    cs.sendMessage(plugin.parseColor(plugin.MFail));
                    cs.sendMessage("-----------------------------------------------------");
                }
                plugin.Playertime.put(cs.getName(),plugin.getCurrentMilli());
                return true;
             }else{
                cs.sendMessage("Don't Spam");
                return false;
             }
    }
    
    public boolean RankOnHability(CommandSender cs,String Hability) {
        Player _player = (Player)cs;
        if(!plugin.permission.has(_player.getWorld(), _player.getName(),"mru.hability")) return false;
        Profile _profile = new Profile(plugin,_player);
        boolean exists = false;
        for(String key:plugin.isHabilityRankExist.keySet()) {
           if(Hability.equalsIgnoreCase(key))
               exists = plugin.isHabilityRankExist.get(key);
        }
        if(!exists) {
            cs.sendMessage("-----------------------------------------------------");
            cs.sendMessage(plugin.parseColor(plugin.NotFound));
            cs.sendMessage("-----------------------------------------------------");
            return false;
        }
        return _profile.setHabilityForRank(Hability);
    }
    
    public boolean Help(CommandSender cs) {
        Player _player = (Player)cs;
        cs.sendMessage("------------------[Mcmmorankup Help]------------------");
        cs.sendMessage(plugin.parseColor("&6/mru check &7Check your rank"));
        cs.sendMessage(plugin.parseColor("&6/mru male &7Set Gender to Male"));
        cs.sendMessage(plugin.parseColor("&6/mru female &7Set Gender to Female"));
        if(plugin.permission.has(_player.getWorld(), _player.getName(),"mru.hability")) {
            cs.sendMessage(plugin.parseColor("&6/mru hab &7See available hability"));
            cs.sendMessage(plugin.parseColor("&6/mru hab <hability> &7Change Rank Base Hability"));
        }
        if(plugin.permission.has(_player.getWorld(), _player.getName(),"mru.reload")) {
            cs.sendMessage(plugin.parseColor("&6/mru reload &7Reload the plugin"));
        }
        cs.sendMessage("-----------------------------------------------------");
        return true;
    }
    
    public boolean setGender(CommandSender cs,String gender) { 
        Player _player = (Player)cs;
        Profile _profile = new Profile(plugin, _player);
        _profile.setGender(gender);
        cs.sendMessage("-----------------------------------------------------");
        cs.sendMessage(plugin.parseColor(plugin.setGender.replace("%gender%", gender)));
        cs.sendMessage("-----------------------------------------------------");
        return true;
    }
    
    public boolean ListHability(CommandSender cs) {
        cs.sendMessage("------------------[Mcmmorankup List]------------------");
        for(String key:plugin.isHabilityRankExist.keySet()) {
           if(plugin.isHabilityRankExist.get(key)) {
               cs.sendMessage(plugin.parseColor("&6" + key + " &7 - Available"));
           }else{
               cs.sendMessage(plugin.parseColor("&6" + key + " &7 - Not Available"));
           }
        }
        cs.sendMessage("-----------------------------------------------------");
        return true;
    }
}
