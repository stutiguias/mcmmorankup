/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.apimcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.mcMMO;
import java.util.Map;
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
            int PowerLevel = getPowerLevel(player);
            boolean state = false;
            for(String PlayerToIgnore:plugin.PlayerToIgnore) {
                if(player.getName().equalsIgnoreCase(PlayerToIgnore)) {
                    return state;
                }
            }
            Integer aux = 0;
            String group = "";
            for(Map.Entry<Integer,String> entry : plugin.RankLevel.entrySet()) {
                Integer key = entry.getKey();
                String  value = entry.getValue();
                if(aux < key)
                {
                   aux = key;
                   if(aux < PowerLevel){
                        group = value; 
                   }
                }        
            }            
            for (String GroupToIgnore  : plugin.GroupToIgnore) {
                if(GroupToIgnore.equalsIgnoreCase(plugin.permission.getPrimaryGroup(player)))
                    Mcmmorankup.log.log(Level.WARNING,"Error Ignore group found for " + player.getName());
                    return false;
            }
            state = ChangeGroup(player,group);
            return state;
        }catch(Exception ex){
            Mcmmorankup.log.log(Level.WARNING,"Error try to rank up " + ex.getMessage());
            return false;
        }
    }
    
    private boolean ChangeGroup(Player player,String group)
    {
        boolean state = plugin.permission.playerAddGroup(plugin.getServer().getWorlds().get(0),player.getName(),group);
        String[] plgr = plugin.permission.getPlayerGroups(player);
        for(String gr:plgr) {
          if(!gr.equals(group))
            state = plugin.permission.playerRemoveGroup(plugin.getServer().getWorlds().get(0), player.getName(), gr);
        }
        plugin.getServer().broadcastMessage(BroadcastMessage(player, group));
        return state;
        
    }
    
    private static int getPowerLevel(Player player)
    {
        return ExperienceAPI.getPowerLevel(player);
    }
    
    private String BroadcastMessage(Player player,String group)
    {
        return plugin.MPromote.replace("%player%", player.getName()).replace("%group%", group);
    }
}
