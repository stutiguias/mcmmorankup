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
         if(args[0].equalsIgnoreCase("hab")) return RankOnHability(cs,args[1].toString()); 
         
         // Its Reload ?
         if(args[0].equalsIgnoreCase("reload")) {
             plugin.onReload();
             cs.sendMessage("Reload Done!");
             return false;
         }
         
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
                boolean sucess = plugin.PowerLevel.tryRankUp(pl);
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
        Profile _profile = new Profile(plugin,_player);
        return _profile.setHabilityForRank(Hability);
    }
}
