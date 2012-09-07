/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.apimcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import java.util.Iterator;
import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Stutiguias
 */
public class PowerLevel {
    
    private static Mcmmorankup plugin;
    
    public PowerLevel(Mcmmorankup instance)
    {
        plugin = instance;
        Plugin pl = plugin.getServer().getPluginManager().getPlugin("mcMMO");
        if(pl != null) {
            Mcmmorankup.log.log(Level.INFO, plugin.logPrefix + " mcMMO found !!!");
        }
    }
    
    public boolean tryRankUp(Player player) {
        try {
            Integer PowerLevel = getPowerLevel(player);
            boolean state = false;
            for(String PlayerToIgnore:plugin.PlayerToIgnore) {
                if(player.getName().equalsIgnoreCase(PlayerToIgnore)) {
                    return state;
                }
            }
            String group = "";
            for (Iterator<String> it = plugin.RankLevel.iterator(); it.hasNext();) {
                String entry = it.next();
                String[] values = entry.split(",");
                if(Integer.parseInt(values[0]) < PowerLevel){
                    group = values[1]; 
                }
            }
            for (String GroupToIgnore  : plugin.GroupToIgnore) {
                if(GroupToIgnore.equalsIgnoreCase(plugin.permission.getPrimaryGroup(player))) {
                    return false;
                }
            }
            if(!group.equalsIgnoreCase("")) 
                state = ChangeGroup(player,group);

            return state;
        }catch(Exception ex){
            Mcmmorankup.log.log(Level.WARNING,"Error try to rank up " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    private boolean ChangeGroup(Player player,String group)
    {
        String groupnow = plugin.permission.getPrimaryGroup(player);
        boolean state = plugin.permission.playerAddGroup(player.getWorld(),player.getName(),group);
        String[] plgr = plugin.permission.getPlayerGroups(player);
        for(String gr:plgr) {
          if(!gr.equals(group))
            state = plugin.permission.playerRemoveGroup(player.getWorld(), player.getName(), gr);
        }
        if(!groupnow.equalsIgnoreCase(group))  {
            plugin.getServer().broadcastMessage("-----------------McMMORANKUP-------------------------");
            plugin.getServer().broadcastMessage(plugin.parseColor(BroadcastMessage(player, group)));
            plugin.getServer().broadcastMessage("-----------------------------------------------------");
            return state;
        }else{
            return false;
        }
            
    }
    
    private static int getPowerLevel(Player player)
    {
        return ExperienceAPI.getPowerLevel(player);
    }
    
    private String BroadcastMessage(Player player,String group)
    {
        if(plugin.UseAlternativeBroadcast) {
            try {
               String bc = plugin.RealBroadCast.get(group);
               return plugin.MPromote.replace("%player%", player.getName()).replace("%group%", bc);
            }catch(Exception ex) {
                Mcmmorankup.log.log(Level.WARNING,"Error try to broadcast Alternative " + ex.getMessage());
                ex.printStackTrace();
                return "Error try to broadcast Alternative";
            }
        }else {
            return plugin.MPromote.replace("%player%", player.getName()).replace("%group%", group);
        }
    }
    
}
