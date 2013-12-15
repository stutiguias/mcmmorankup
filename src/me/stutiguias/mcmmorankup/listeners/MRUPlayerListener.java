package me.stutiguias.mcmmorankup.listeners;

import me.stutiguias.mcmmorankup.apimcmmo.McMMOApi;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Utilities;
import me.stutiguias.mcmmorankup.profile.Profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.stutiguias.mcmmorankup.task.OnJoinTask;

public class MRUPlayerListener implements Listener {

    private final Mcmmorankup plugin;
    private Profile profile;
    
    public MRUPlayerListener(Mcmmorankup plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        Player player = event.getPlayer();
        
        if(plugin.UpdaterNotify && plugin.hasPermission(player,"mru.update") && Mcmmorankup.update)
        {
          player.sendMessage(Utilities.parseColor("&6An update is available: " + Mcmmorankup.name + ", a " + Mcmmorankup.type + " for " + Mcmmorankup.version + " available at " + Mcmmorankup.link));
          player.sendMessage(Utilities.parseColor("&6Type /mru update if you would like to automatically update."));
        }
        
        if (plugin.PromoteOnJoin && plugin.hasPermission(player, "mru.rankup")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,new OnJoinTask(plugin, player), plugin.onJoinDelay);
        }
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLeave(PlayerQuitEvent event) {
        profile = new Profile(plugin, event.getPlayer() );
        profile.SetQuitStats();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMcMMOPlayerXpGain(McMMOPlayerXpGainEvent event) {
        if (!plugin.playerAbilityXpUpdateFeed) return;
        SkillType skillType = event.getSkill();

        profile = new Profile(plugin, event.getPlayer());
        String skill = profile.GetHabilityForRank();

        if (!profile.GetPlayerXpUpdateFeed() || !skillType.toString().equalsIgnoreCase(skill) || skill.equalsIgnoreCase("POWERLEVEL")) return; 
        
        String xp = String.valueOf((int)event.getRawXpGained() + McMMOApi.getXp(profile.player, skill));
        String toNextLevel = String.valueOf(McMMOApi.getXpToNextLevel(profile.player, skill));
        
        profile.SendFormatMessage(plugin.Message.McmmoXpGain.replaceAll("%cXp%",xp).replaceAll("%rXp%",toNextLevel)); 
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.TagSystem) return;
        if (plugin.isIgnored(event.getPlayer())) return;
        
        profile = new Profile(plugin, event.getPlayer());
        String Tag = profile.GetTag();
        if (Tag == null) Tag = "";
        String format = event.getFormat();
        if(format.contains("-mru")) {
            event.setFormat(format.replace("-mru",Utilities.parseColor(Tag)));
        }else{
            event.setFormat(Utilities.parseColor(Tag) + " " + format);
        }
    }
    
}
