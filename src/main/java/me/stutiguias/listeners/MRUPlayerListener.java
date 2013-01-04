package me.stutiguias.listeners;

//import java.util.logging.Level;
import me.stutiguias.mcmmorankup.ChatTools;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.profile.Profile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MRUPlayerListener implements Listener {
    
    private final Mcmmorankup plugin;

    public MRUPlayerListener(Mcmmorankup plugin){
        this.plugin = plugin;
    }
    
    /* zrocweb: Updated to include SyncDelayedTask as player logons if PromoteOnJoin is enabled.  
     *          This allows for other server and player plugins to respond either before or after to avoid earlier or later
     *          messaging (promotion) to the player(s) as well as spam to the broadcaster.  Some plugins that have spam detection
     *          could detect and intercept this as immediate spam and it would be discarded.
     ******************************************************************************************************************************* */  
    @EventHandler(priority= EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	final Player pl = event.getPlayer();
    	
        if(plugin.PromoteOnJoin) {
        	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {                
                Profile _profile = new Profile(plugin, pl);
        		
                @Override
                public void run() {
					String skill = _profile.getHabilityForRank().toUpperCase();
					String gender = _profile.getGender();
					String promoted = "";
					Boolean sucess=false;
					
					pl.sendMessage(ChatColor.AQUA + pl.getName() + ", " + ChatColor.DARK_AQUA + " checking your rank and trying to promote...");
					if(plugin.TagSystem) {
					    sucess = plugin.RankUp.tryRankUpWithoutGroup(pl, skill, gender);
					} else {
					    promoted =  plugin.RankUp.tryRankUp(pl, skill, gender);
					} 
					
					if (sucess || "promoted".equals(promoted)) {
						pl.getPlayer().sendMessage(ChatTools.getAltColor(plugin.generalMessages) + plugin.MSucess);
					}
                }
           }, plugin.onJoinDelay);
        }
    }    
    
    /* Original
    @EventHandler(priority= EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        Profile _profile = new Profile(plugin, pl);
        if(plugin.PromoteOnJoin) {
            String skill = _profile.getHabilityForRank().toUpperCase();
            String gender = _profile.getGender();
            Boolean sucess;
            if(plugin.TagSystem) {
                sucess = plugin.RankUp.tryRankUpWithoutGroup(pl, skill, gender);
            }else{
                sucess =  plugin.RankUp.tryRankUp(pl,skill,gender);
            }
            if(sucess) event.getPlayer().sendMessage(plugin.MSucess);
        }
    }
    ********************************************************************************* */
    
    @EventHandler(priority= EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;
        if(!plugin.TagSystem) return;
        Player _player = event.getPlayer();
        Profile _profile = new Profile(plugin, _player);
        String Tag = _profile.getTag();
        if(Tag == null) Tag = "";
        String format = event.getFormat();
        event.setFormat(plugin.parseColor(Tag) + " " + format);
        
    }
}
